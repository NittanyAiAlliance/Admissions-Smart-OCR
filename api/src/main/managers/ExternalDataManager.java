package main.managers;

import main.data.DatabaseInteraction;
import main.listeners.TranscriptQueueListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExternalDataManager {
    private DatabaseInteraction database;

    /**
     * Default constructor ~ create new database interaction instance for the manager
     */
    public ExternalDataManager(){
        this.database = new DatabaseInteraction();
    }

    /**
     * Make the auth token request
     */
    public String requestAuthToken() {
        // Create sample auth parameters
        JSONObject authParams = new JSONObject(){{
            put("uid", "sample");
            put("password", "password");
        }};
        HttpURLConnection conn = getExternalHttpConnection("http://localhost:5001/auth");
        sendRequest(conn, authParams);
        JSONObject reqResponse = getRequestResponse(conn);
        return reqResponse.getString("auth");
    }

    /**
     * Make the available transcript request
     */
    public void requestAvailableTranscripts() {
        JSONObject availableTranscriptRequest = new JSONObject() {{
            put("auth", requestAuthToken());
        }};
        HttpURLConnection conn = getExternalHttpConnection("http://localhost:5001/new_transcripts");
        sendRequest(conn, availableTranscriptRequest);
        JSONObject reqResponse = getRequestResponse(conn);
        JSONArray availableTranscriptObjects = reqResponse.getJSONArray("transcripts");
        for(int i = 0; i < availableTranscriptObjects.length(); i++){
            JSONObject availableTranscript = availableTranscriptObjects.getJSONObject(i);
            TranscriptQueueListener.enqueue(availableTranscript);
        }
    }

    /**
     * Request image of transcript by document ID
     * @param documentId document ID of the requested transcript
     * @return Base64 image string representation of transcript
     */
    public String requestTranscriptImage(String documentId) {
        //Create transcript request object
        JSONObject transcriptImageRequest = new JSONObject() {{
            put("auth", requestAuthToken());
            put("DOCUMENT_ID", documentId);
        }};
        //Perform API request
        HttpURLConnection conn = getExternalHttpConnection("http://localhost:5001/image");
        sendRequest(conn, transcriptImageRequest);
        JSONObject reqResponse = getRequestResponse(conn);
        return reqResponse.getString("imageStr");
    }

    private HttpURLConnection getExternalHttpConnection(String urlString) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return conn;
    }

    private void sendRequest(HttpURLConnection conn, JSONObject req) {
        try {
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(req.toString());
            wr.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private JSONObject getRequestResponse(HttpURLConnection conn) {
        StringBuilder response = new StringBuilder();
        try{
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new JSONObject(response.toString());
    }
}
