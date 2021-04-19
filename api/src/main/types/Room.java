package main.types;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    private final Map<String, WebSocketClient> subscribers;
    private final String entityId;

    /**
     * Default room constructor
     * @param entityId entity that is being collaborated on in this room
     */
    public Room(String entityId){
        this.entityId = entityId;
        this.subscribers = new HashMap<>();
    }

    /**
     * Getter for room subscribers ~ that is ~ the clients connected to this room
     * @return list of connections for the subscribers to this room
     */
    public List<WebSocket> getSubscriberConnections(){
        List<WebSocket> connList = new ArrayList<>();
        for(WebSocketClient webSocketClient : this.subscribers.values()){
            connList.add(webSocketClient.getConnection());
        }
        return connList;
    }

    /**
     * Getter for the entity id for this room
     * @return identifier for this room
     */
    public String getIdentifier() {
        return this.entityId;
    }

    /**
     * Add a connection to the list of subscribers
     * @param conn connection to add to the list of subscribers
     */
    public void addSubscriber(WebSocketClient conn) {
        this.subscribers.put(conn.getUid(), conn);
    }

    /**
     * Look for a specific user within this room
     * @param uid user identifier to look for
     * @return is the user in this room?
     */
    public boolean hasUser(String uid) {
        //If the user is in this room, the result of the get process will not be null
        return subscribers.get(uid) != null;
    }
}
