package main.endpoints;

import main.listeners.InteractionLogListener;
import main.types.InteractionLog;
import main.types.WebSocketClient;
import org.json.JSONObject;

import java.util.Date;

public class QueueEndpoint extends Endpoint {

    @Override
    public JSONObject fulfillRequest(JSONObject cmd) {
        String type = cmd.getString("type");
        switch (type) {
            case "checkOut":
                this.handleCheckOut(cmd);
                break;
            case "checkIn":
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
            InteractionLogListener.enqueue(new JSONObject(){{
                put("checkedout", true);
                put("did", cmd.getString("did"));
            }});
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
            InteractionLogListener.enqueue(new JSONObject(){{
                put("checkedout", false);
                put("did", cmd.getString("did"));
            }});
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        }
    }
}