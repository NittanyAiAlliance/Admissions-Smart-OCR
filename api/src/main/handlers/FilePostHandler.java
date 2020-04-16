package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.types.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilePostHandler extends HandlerPrototype implements HttpHandler {
    public FilePostHandler(){
        super.handlerName = "File Post Handler";
        super.requiredKeys = new String[] { "file", "token" };
    }

    private final String internalUrl = "http://localhost:5000";

    @Override
    public void fulfillRequest(JSONObject requestParams) {
        //Get image file string from request parameters
        String imgStr = requestParams.getString("file").split(",")[1];
        //Create OCR API request
        String ocrResponse = "";
        try{
            JSONObject reqArgObj = new JSONObject();
            reqArgObj.put("file", imgStr);
            ocrResponse = getOCRRequest(reqArgObj);
        } catch (Exception ioEx){
            ioEx.printStackTrace();
        }
        JSONObject ocrResponseObj = formatOCRResponse(ocrResponse);
        returnActionSuccess(ocrResponseObj);
    }

    /**
     * Request OCR services from the backend API and get the results
     * @param reqArgObj JSONObject containing the image and string to be OCR'd
     * @return string containing the tagged fields from the OCR
     * @throws IOException something went wrong with the internal request
     */
    private String getOCRRequest(JSONObject reqArgObj) throws IOException{
        //Create connection to local OCR api
        HttpURLConnection httpClient = (HttpURLConnection) new URL(internalUrl).openConnection();
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setDoOutput(true);
        //Write the request arguments
        try(OutputStream os = httpClient.getOutputStream()){
            os.write(reqArgObj.toString().getBytes());
            os.flush();
            os.close();
        }
        int responseCode = httpClient.getResponseCode();
        if(responseCode != 200){
            //Something went wrong
            System.out.println("Could not connect to internal API");
        }
        //Read request response ~ expecting OCR string
        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))){
            StringBuilder response = new StringBuilder();
            String line;
            while((line = in.readLine()) != null){
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * Convert the stringified list of lists into the JSON object the front-end is expecting
     * @param ocrStr string response from the ocr api
     * @return properly formatted JSON object
     */
    private JSONObject formatOCRResponse(String ocrStr){
        JSONObject ocrObj = new JSONObject(ocrStr);
        JSONArray courseArray = new JSONArray();
        Iterator<String> lineKeys = ocrObj.keys();
        while(lineKeys.hasNext()){
            String lineKey = lineKeys.next();
            if(ocrObj.get(lineKey) instanceof JSONObject){
                //This is a line key ~ can be cast into a json object
                JSONObject thisLineObj = ocrObj.getJSONObject(lineKey);
                if(thisLineObj.has("COURSE")){
                    //This object has a course! Cool! That means we can do something with it.
                    if(!thisLineObj.getString("COURSE").equals("Table:")) {
                        JSONObject thisCourseObj = new JSONObject();
                        thisCourseObj.put("name", thisLineObj.getString("COURSE"));
                        if (thisLineObj.has("GRADE")) {
                            thisCourseObj.put("grade", thisLineObj.getString("GRADE"));
                        }
                        if (thisLineObj.has("CREDIT")) {
                            thisCourseObj.put("credits", thisLineObj.getString("CREDIT"));
                        }
                        courseArray.put(thisCourseObj);
                    }
                }
            }
        }
        JSONObject formattedResponseObj = new JSONObject();
        JSONObject studentObj = new JSONObject();
        JSONObject highSchoolObj = new JSONObject();
        studentObj.put("name", "");
        studentObj.put("address", "");
        studentObj.put("gpa", "");
        studentObj.put("sat", "");
        highSchoolObj.put("name", "");
        highSchoolObj.put("address", "");
        highSchoolObj.put("ceeb", "");
        formattedResponseObj.put("student", studentObj);
        formattedResponseObj.put("highSchool", highSchoolObj);
        JSONObject tempYearObject = new JSONObject();
        tempYearObject.put("name", "");
        tempYearObject.put("classes", courseArray);
        formattedResponseObj.put("classYears", new JSONArray().put(tempYearObject));
        return formattedResponseObj;
    }

    @Override
    public Log toLog(){
        return new Log();
    }
}
