package main.endpoints;

import main.types.WebSocketClient;
import org.json.JSONObject;

public abstract class Endpoint {
    protected String[] requiredKeys;
    protected String endpointName;

    public JSONObject handle(JSONObject cmd, WebSocketClient conn){
        return fulfillRequest(cmd, conn);
    }

    public abstract JSONObject fulfillRequest(JSONObject cmd, WebSocketClient conn);
}