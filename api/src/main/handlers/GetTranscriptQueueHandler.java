package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.managers.TranscriptManager;
import org.json.JSONObject;

import java.sql.SQLException;

public class GetTranscriptQueueHandler extends HandlerPrototype implements HttpHandler {
    public GetTranscriptQueueHandler(){
        super.requiredKeys = new String[] {};
        super.handlerName = "Get Transcript Queue Handler";
    }
    @Override
    public void fulfillRequest(JSONObject requestParams) {
        try{
            returnActionSuccess(new JSONObject(){{
                put("queue", new TranscriptManager().fetchQueue());
            }});
        } catch (SQLException sqlEx){
            sqlEx.printStackTrace();
        }
    }
}
