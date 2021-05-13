package main;

import main.endpoints.Endpoint;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Prototype for a socket endpoint
 *      Delivers abstraction for receiving, processing, handling, and broadcasting commands to connection clients
 *      Maintains state on connected/pending clients and collections of clients (rooms)
 *      Handles standardized synthetic handshake
 * @author Joel Seidel
 */
public class Socket extends WebSocketServer {

    //WebSocket endpoint handlers map
    private final Map<String, Endpoint> endpoints;

    //Name of the socket implementation for debug/logging
    private final String socketName;
    //Port this server is running on for debug/logging
    private final int port;

    /**
     * Constructor for a socket based on the socket prototype
     * @param port port to run the socket from
     * @param socketName name of the socket
     * @param endpoints map of endpoint names to endpoint objects for this socket
     */
    public Socket(int port, String socketName, Map<String, Endpoint> endpoints) {
        //Create the WebSocket server instance at the specified port
        super(new InetSocketAddress(port));
        //Set debug/log values
        this.port = port;
        this.socketName = socketName;
        //Set socket endpoints
        this.endpoints = endpoints;
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
        // Really don't need to do anything here
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
        //Get the endpoint this command is pointed at
        Endpoint cmdEndpoint = endpoints.get(cmdType);
        //Is this really a valid endpoint
        if(cmdEndpoint == null) {
            //This has been sent to an invalid endpoint
            //Instant return as a DOS trap
            return;
        }
        //Handle the request at the specified endpoint
        JSONObject response = cmdEndpoint.handle(cmd);
        //Was the endpoint able to produce a response?
        if(response == null) {
            //Apparently not, this signifies a problem with the sender, not this server
            System.out.println("Dropped a command: " + cmd.toString());
            return;
        }
        //Send the processed command to each of the connections
        this.sendToAll(response);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //TODO: handle an error
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    /**
     * Send this message to all of the connected clients in all rooms.
     * @param msg JSON object to send to all clients
     */
    protected void sendToAll(JSONObject msg){
        // This a generally needless abstraction of the method already provided by the super class
        // But alas, isn't needless abstraction the name of the game in Java?
        super.broadcast(msg.toString());
    }
}
