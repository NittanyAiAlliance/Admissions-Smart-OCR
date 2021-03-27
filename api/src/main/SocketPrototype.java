package main;

import main.endpoints.Endpoint;
import main.managers.RoomManager;
import main.types.Room;
import main.types.WebSocketClient;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Prototype for a socket endpoint
 *      Delivers abstraction for receiving, processing, handling, and broadcasting commands to connection clients
 *      Maintains state on connected/pending clients and collections of clients (rooms)
 *      Handles standardized synthetic handshake
 * @author Joel Seidel
 */
public class SocketPrototype extends WebSocketServer {

    //WebSocket endpoint handlers map
    private Map<String, Endpoint> endpoints;

    /*
     *  Map of user identifiers to connected client objects ~ these are the clients who completed the synthetic
     *      handshake and have identified themselves to the server
     */
    private Map<String, WebSocketClient> connectedClients;

    /*
     *  Map of connection identifier to connection ~ these are not yet connected clients and have yet to complete the
     *      synthetic handshake to be considered connected clients
     */
    private Map<String, WebSocket> pendingClients;

    //Room manager instance ~ rooms can be for collaboration or for projects
    private RoomManager roomManager;

    //Name of the socket implementation for debug/logging
    private String socketName;
    //Port this server is running on for debug/logging
    private int port;

    /**
     * Constructor for a socket based on the socket prototype
     * @param port port to run the socket from
     * @param socketName name of the socket
     * @param endpoints map of endpoint names to endpoint objects for this socket
     */
    public SocketPrototype(int port, String socketName, Map<String, Endpoint> endpoints) {
        //Create the WebSocket server instance at the specified port
        super(new InetSocketAddress(port));
        //Initialize the client state collections
        this.connectedClients = new HashMap<>();
        this.pendingClients = new HashMap<>();
        //Initialize the room manager
        this.roomManager = new RoomManager();
        //Set debug/log values
        this.port = port;
        this.socketName = socketName;
        //Set socket endpoints
        this.endpoints = endpoints;
        //Start looking for dead connections
        ScheduledExecutorService deadClientWorker = Executors.newScheduledThreadPool(1);
        //Runnable for removing closed connections from the connected client map
        Runnable removeClosedClients = () -> {
            for (Map.Entry<String, WebSocketClient> clientEntry : this.connectedClients.entrySet()) {
                //Find the clients who are already dead
                WebSocketClient client = clientEntry.getValue();
                //Has this connected already closed or is it in the process of closing?
                if (!client.isAlive() || client.getConnection().isClosed()) {
                    JSONObject clientLeftMessage = new JSONObject() {{
                        put("type", "goodbye");
                        put("from", client.getUid());
                    }};
                    //Find which room the user is in
                    Room deadClientRoom = roomManager.getUserRoom(client.getUid());
                    this.sendToRoom(deadClientRoom, clientLeftMessage);
                    //It is closing or has already closed, it is no longer a connected client
                    connectedClients.remove(clientEntry.getKey());
                    System.out.println("Removed dead client ~ " + client.getUsername());
                } else {
                    //The client is considered alive anymore
                    client.setAlive(false);
                    //Construct a ping message
                    JSONObject pingMessage = new JSONObject() {{
                        put("type", "ping");
                        put("from", "server");
                    }};
                    //Send this ping to a specific client
                    sendToClient(client.getConnection(), pingMessage);
                }
            }
        };
        deadClientWorker.scheduleAtFixedRate(removeClosedClients, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Handle socket server start event
     */
    @Override
    public void onStart() {
        System.out.println(this.socketName + " socket opened on " + this.port);
        //Set the connection timeout for low latency action
        setConnectionLostTimeout(100);
    }

    /**
     * Handles the on open event of a WebSocket connection with the front end ~ Starts the synthetic handshake process
     * @param conn WebSocket connection
     * @param handshake Completed client TCP handshake object
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        //As per RFC6455 WebSocket Protocol, this is a sufficient identifier for a client
        String connectedUid = handshake.getFieldValue("Sec-WebSocket-Key");
        //This is a pending connection until it completes the synthetic handshake
        pendingClients.put(connectedUid, conn);
        //Create the synthetic handshake object
        JSONObject welcomeResponse = createWelcomeObject(connectedUid);
        //Send the synthetic handshake and wait for the client to respond complete the handshake
        this.sendToClient(conn, welcomeResponse);
    }

    /**
     * Handle a new message from the client
     * @param conn WebSocket connection
     * @param cmdString JSON string representing the command passed from the client
     */
    @Override
    public void onMessage(WebSocket conn, String cmdString) {
        //Get the command object so this command can be routed to the proper endpoint
        JSONObject cmd = new JSONObject(cmdString);
        String cmdType = cmd.getString("endpoint");
        //Is the client trying to complete a synthetic handshake?
        if(cmdType.equals("broadcast")) {
            //Handle the client completion of the synthetic handshake
            handleCompleteHandshake(cmd);
            //There is nothing left to do, we do not need to route this command/do anything else with it
            return;
        } else if(cmdType.equals("pong")){
            handlePongMessage(cmd);
        }
        //This is assumed to be a connected client ~ get it from the map of connected clients
        WebSocketClient client = connectedClients.get(cmd.getString("from"));
        //Is this really a connected client?
        if(client == null) {
            //It is not a connected client
            //Instant return as a DOS trap
            return;
        }
        //Get the endpoint this command is pointed at
        Endpoint cmdEndpoint = endpoints.get(cmdType);
        //Is this really a valid endpoint
        if(cmdEndpoint == null) {
            //This has been sent to an invalid endpoint
            //Instant return as a DOS trap
            return;
        }
        //Handle the request at the specified endpoint
        JSONObject response = cmdEndpoint.handle(cmd, client);
        //Was the endpoint able to produce a response?
        if(response == null) {
            //Apparently not, this signifies a problem with the sender, not this server
            System.out.println("Dropped a command: " + cmd.toString());
            return;
        }
        //Get this destination room for this command to be sent to
        String roomId = cmd.getString("room");
        //Get the destination room object from the room manager
        Room room = roomManager.getRoom(roomId);
        //Send the processed command to each of the connections within the specified room
        this.sendToRoom(room, response);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //TODO: handle an error
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    /*
     * -----------------------------------------------------------------------------------------------------------------
     *                                              SYNTHETIC HANDSHAKE
     *                      as described by with a "new student in a young adult movie" metaphor
     * -----------------------------------------------------------------------------------------------------------------
     *      OnOpen - User has now completed the administrative paper work to be a student at this school (TCP handshake)
     *          And is now in the classroom. The teacher will now deliver a welcome speech before the student sits down.
     *
     *      createWelcomeMessage - The teacher (server) is delivering a welcome speech to the student. The speech starts
     *          by telling him this speech will be of the "welcome" variety. Then the teacher introduces everyone in the
     *          class. Then the teacher hands the new student a name tag to identify themselves by.
     *
     *      The new student then remembers all of this information so they can communicate with their new classmates,
     *          and remember who they are. When they finally walk to their own desk and sit down, they, the teacher, and
     *          everyone else knows they are student in this class because they verify the room number they are in.
     *          This is now a valid student in this classroom.
     */

    /**
     * Create the welcome message component of the synthetic handshake ~ this initiates that process
     * @param identifier identifier of the WebSocket connection from the TCP handshake
     * @return welcome message of the synthetic handshake in JSON format
     */
    private JSONObject createWelcomeObject(String identifier){
        JSONObject welcomeResponse = new JSONObject();
        //Designate this command to be of "welcome" type - the client will know this means synthetic handshake
        welcomeResponse.put("type", "welcome");
        //Create an array of the identifiers of the connected clients
        JSONArray friendsHereArray = new JSONArray();
        for(WebSocketClient client : connectedClients.values()){
            friendsHereArray.put(client.toJson());
        }
        welcomeResponse.put("friendsHere", friendsHereArray);
        //Let the client know what the server is identifying them as so they are able to ID themselves on commands
        welcomeResponse.put("me", identifier);
        return welcomeResponse;
    }

    /**
     * Handler for a command send to the broadcast endpoint ~ that is ~ the end of the synthetic handshake
     * @param cmd JSON command object
     */
    private void handleCompleteHandshake(JSONObject cmd) {
        String connectionIdentifier = cmd.getString("from");
        //Retrieve the pending connection identified by the connection id
        WebSocket conn = pendingClients.get(connectionIdentifier);
        //Retrieve the self-identified data from the client - username, uid, and entity identifier (cid)
        String entityId = cmd.getString("entityId");
        String username = cmd.getString("username");
        String uid = cmd.getString("uid");
        //Build the connected client object from the retrieved data components
        WebSocketClient webSocketClient = createConnectedClient(username, uid, connectionIdentifier, conn);
        //Assign the, now connected, client to the proper room - as per what entity they declared to be in the room of
        Room newClientRoom = roomManager.assignRoom(entityId, webSocketClient);
        //Add the room assignment to the returning command
        cmd.put("room", newClientRoom.getIdentifier());
        //Send the client json object representation
        cmd.put("client", webSocketClient.toJson());
        //Notify the room of this new connected client
        this.sendToRoom(newClientRoom, cmd);
    }

    /**
     * Handle a connection telling us that they are still here
     * @param cmd pong command
     */
    public void handlePongMessage(JSONObject cmd){
        WebSocketClient respondingClient = connectedClients.get(cmd.getString("from"));
        respondingClient.setAlive(true);
        System.out.println(respondingClient.getUsername() + " is still here");
    }

    /**
     * Complete the synthetic handshake and create a connected client object
     * @param username username of the new client
     * @param uid user identifier of the new client
     * @param connId connection identifier of the new client
     * @param connection WebSocket connection of the new client
     * @return WebSocket client object representing the newly connected client
     */
    private WebSocketClient createConnectedClient(String username, String uid, String connId, WebSocket connection){
        //Create the WebSocket client object from the specified data
        WebSocketClient newClient = new WebSocketClient(username, connection, uid, connId);
        //Register the client as being officially recognized by the server, and the State of Pennsylvania, as connected
        registerConnectedClient(connId, uid, newClient);
        return newClient;
    }

    /**
     * Swap the specified client from the pending clients collection to the connected clients collection
     * @param connectionIdentifier identifier of the WebSocket connection ~ temp id via TCP
     * @param userIdentifier identifier of the user of this connection ~ relates to user id in data
     * @param client WebSocket client object created on the completion of the synthetic handshake
     */
    private void registerConnectedClient(String connectionIdentifier, String userIdentifier, WebSocketClient client){
        //Remove the client from the pending collection
        this.pendingClients.remove(connectionIdentifier);
        //Add the client to the connected collection - now identified by their user identifier instead of connection id
        this.connectedClients.put(userIdentifier, client);
    }

    /*
     *  Send methods. Abstractions of the broadcast method in the websocket super dependency
     *      NOTE: odds are you don't want to use the send to all method
     */

    /**
     * Send this message to a specified room
     * @param room room object to send the message to
     * @param msg JSON object to send this room
     */
    protected void sendToRoom(Room room, JSONObject msg){
        //Create the collection will all of the connections to the subscribers of this room
        Collection<WebSocket> roomConnections = room.getSubscriberConnections();
        //Broadcast this message to all of the subscribers from the room
        super.broadcast(msg.toString(), roomConnections);
    }

    /**
     * Send this message a single client, specified by a client connection
     * @param conn client connection to send the message to
     * @param msg JSON object to send to this client
     */
    protected void sendToClient(WebSocket conn, JSONObject msg){
        //Create the collection with only a single websocket connection it ~ sending it just to this client
        Collection<WebSocket> clientConnection = new ArrayList<WebSocket>() {{
            add(conn);
        }};
        //Broadcast this message just to the one specified client
        super.broadcast(msg.toString(), clientConnection);
    }

    /**
     * Send this message to all of the connected clients in all rooms.
     *      This probably isn't the method you want to use
     * @param msg JSON object to send to all clients
     */
    protected void sendToAll(JSONObject msg){
        //This a generally needless abstraction of the method already provided by the super class
        super.broadcast(msg.toString());
    }

}
