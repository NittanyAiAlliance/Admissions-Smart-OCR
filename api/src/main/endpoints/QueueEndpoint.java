package main.endpoints;

import main.listeners.InteractionLogListener;
import main.types.InteractionLog;
import main.types.WebSocketClient;
import org.json.JSONObject;

import java.util.Date;

public class QueueEndpoint extends Endpoint {

    @Override
    public JSONObject fulfillRequest(JSONObject cmd, WebSocketClient conn) {
        String type = cmd.getString("type");
        switch (type) {
            case "check-out":
                this.handleCheckOut(cmd);
                break;
            case "check-in":
                this.handleCheckIn(cmd);
                break;
            default:
        }
        return cmd;
    }

    private void handleCheckOut(JSONObject cmd) {
        try {
            InteractionLog interactionLog = new InteractionLog(
                    "Check Out",
                    cmd.getString("did"),
                    cmd.getString("uid"),
                    new Date()
            );
            InteractionLogListener.enqueue(interactionLog);
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        }
    }

    private void handleCheckIn(JSONObject cmd) {
        try {
            InteractionLog interactionLog = new InteractionLog(
                    "Check In",
                    cmd.getString("did"),
                    cmd.getString("uid"),
                    new Date()
            );
            InteractionLogListener.enqueue(interactionLog);
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        }
    }
}