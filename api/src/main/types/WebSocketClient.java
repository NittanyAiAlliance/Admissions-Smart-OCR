package main.types;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.util.Random;

public class WebSocketClient {
    private String username;
    private String colorCode;
    private WebSocket connection;
    private String uid;
    private String connectionId;
    private boolean alive;

    /**
     * Constructor for a connecting WebSocket client
     * @param username client username
     * @param connection WebSocket connection object
     * @param uid client user id (this relates to the data base)
     * @param connectionId unique identifier of the connection
     */
    public WebSocketClient(String username, WebSocket connection, String uid, String connectionId) {
        this.username = username;
        this.connection = connection;
        this.uid = uid;
        this.connectionId = connectionId;
        this.colorCode = generateRandomColorCode();
        this.alive = true;
    }

    /**
     * Generate a random color to assign to this client
     * @return color hex code
     */
    private String generateRandomColorCode(){
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }

    /**
     * Getter for WebSocket connection object
     * @return WebSocket connection object
     */
    public WebSocket getConnection(){
        return this.connection;
    }

    /**
     * Getter for client username
     * @return client username
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Getter for user identifier
     * @return user identifier (uid)
     */
    public String getUid() { return this.uid; }

    /**
     * Getter for is this socket alive
     * @return is socket alive?
     */
    public boolean isAlive() {
        return this.alive;
    }

    /**
     * Set whether a client is alive or not
     */
    public void setAlive(boolean isAlive) {
        this.alive = isAlive;
    }

    /**
     * Convert this connected client to a JSON object representation
     * @return JSON representation of this client
     */
    public JSONObject toJson() {
        return new JSONObject(){{
            put("username", username);
            put("uid", uid);
            put("initials", username.substring(0, 2));
            put("color", colorCode);
        }};
    }
}