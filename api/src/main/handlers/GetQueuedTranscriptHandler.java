package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.managers.TranscriptManager;
import org.json.JSONObject;

import java.sql.SQLException;

public class GetQueuedTranscriptHandler extends HandlerPrototype implements HttpHandler {
    public GetQueuedTranscriptHandler(){
        super.requiredKeys = new String[] { "id" };
        super.handlerName = "";
    }
    @Override
    public void fulfillRequest(JSONObject requestParams) {
        String docId = requestParams.getString("id");
        try{
            returnActionSuccess(new TranscriptManager().fetchResults(docId));
        } catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
}
