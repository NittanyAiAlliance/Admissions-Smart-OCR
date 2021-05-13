package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.listeners.TranscriptQueueListener;
import main.managers.TranscriptManager;
import main.types.Transcript;
import org.json.JSONObject;

import java.sql.SQLException;

public class TranscriptPostHandler extends HandlerPrototype implements HttpHandler {
    public TranscriptPostHandler(){
        super.handlerName = "Transcript Post Handler";
        //TODO: Determine the format in which this request will be sent ~ get the required keys
        super.requiredKeys = new String[] { };
    }
    @Override
    public void fulfillRequest(JSONObject requestParams) {
        //Get the meta data JSON object from the request object
        JSONObject metadata = requestParams.getJSONObject("meta");
        //Get the file string data from the request object
        String file = requestParams.getString("file");
        TranscriptManager transcriptManager = new TranscriptManager();
        try {
            //Attempt to create a new transcript object and record
            Transcript newTranscript = transcriptManager.createNew(metadata, file);
            //Let the server know to start parsing these transcripts
            //It worked, no args to return
            returnActionSuccess();
        } catch(SQLException sqlEx) {
            //Something didn't go right, return action failure
            sqlEx.printStackTrace();
            returnActionFailure();
        }
    }
}
