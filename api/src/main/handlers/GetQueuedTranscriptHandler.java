package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.managers.TranscriptManager;
import org.json.JSONObject;

public class GetQueuedTranscriptHandler extends HandlerPrototype implements HttpHandler {
    public GetQueuedTranscriptHandler(){
        super.requiredKeys = new String[] {};
        super.handlerName = "";
    }
    @Override
    public void fulfillRequest(JSONObject requestParams) {
        returnActionSuccess(new TranscriptManager().fetchQueuedTranscripts());
    }
}
