package main.managers;

import main.types.ExternalOrganization;
import main.types.GenericSubjectArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Manager for interactions between the server and OCR API
 * @author Joel Seidel
 */
public class OcrApiManager {

    //Constant URL for OCR API
    private final String internalUrl = "http://localhost:5000";

    public OcrApiManager() { }

    /**
     * Make a connection to and perform OCR request with a file on the OCR API
     * @param fileStr string representation of the file to be posted to the OCR API
     * @return String response from the OCR API ~ that is ~ the result of the OCR operation
     * @throws IOException thrown if there is a critical error in communication between this server and the OCR API
     */
    public String doOcr(String fileStr) throws IOException {
        //Create the request object and send to the OCR API
        HttpURLConnection ocrConn = sendOcrRequest(fileStr);
        //Does the request code tell us everything is good?
        int responseCode = ocrConn.getResponseCode();
        if(responseCode != 200){
            //There was an error with making the OCR request
            throw new IOException();
        }
        //Read the response to the OCR request and return the result
        return readOcrResponse(ocrConn);
    }

    /**
     * Create the response object that the front end and the rest of the application can use
     * @param ocrResponse OCR API response string
     * @return translated/formatted response object from OCR API response string
     */
    public JSONObject formatResponse(String ocrResponse) {
        JSONObject ocrObj = new JSONObject(ocrResponse);
        JSONArray courseArray = parseCourses(ocrObj);
        JSONObject formattedResponseObj = new JSONObject();
        JSONObject highSchoolObj = parseHighSchool(ocrObj);
        formattedResponseObj.put("highSchool", highSchoolObj);
        JSONObject tempYearObject = new JSONObject();
        tempYearObject.put("name", "");
        tempYearObject.put("classes", courseArray);
        formattedResponseObj.put("classYears", new JSONArray().put(tempYearObject));
        JSONObject courseOptions = formatCourseOptions();
        formattedResponseObj.put("genericCourses", courseOptions);
        return formattedResponseObj;
    }

    /**
     * Get high school data from CEEB code ~ perform look up
     * @param ocrObj string response from the ocr api
     * @return properly formatted high school sub-object
     */
    private JSONObject parseHighSchool(JSONObject ocrObj){
        //Did they find a CEEB code here?
        if(ocrObj.has("CEEB")){
            //There is a CEEB code ~ we can perform a lookup
            ExternalOrganizationManager externalOrganizationManager = new ExternalOrganizationManager();
            ExternalOrganization thisHighSchool = externalOrganizationManager.getByCeeb(ocrObj.getInt("CEEB"));
            return thisHighSchool.convertToJson();
        } else {
            return new JSONObject();
        }
    }

    /**
     * Get the course data from the OCR response
     * @param ocrObj parse the courses from the OCR response string
     * @return json array of the courses within the ocr response string
     */
    private JSONArray parseCourses(JSONObject ocrObj){
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
                        if (thisLineObj.has("COURSE_NAME")){
                            //Every line will have this, but it only matters if the course has a long name too
                            thisCourseObj.put("generic_name", thisLineObj.getString("COURSE_NAME"));
                        }
                        courseArray.put(thisCourseObj);
                    }
                }
            }
        }
        return courseArray;
    }

    /**
     * Get the generic course options from the database
     * These will match up with the tagged courses
     * @return JSON object containing the course options
     */
    private JSONObject formatCourseOptions(){
        GenericCourseManager genericCourseManager = new GenericCourseManager();
        List<GenericSubjectArea> genericSubjectAreas = genericCourseManager.getAllGenericCourseOptions();
        return genericCourseManager.convertCourseOptionsToJson(genericSubjectAreas);
    }

    /**
     * Create the HTTP Connection object between this server and the OCR API
     * @return HTTPConnection object pointed at the OCR server
     * @throws IOException If there is an issue with the connection
     */
    private HttpURLConnection createOCR_APIConnection() throws IOException {
        //Create the HTTP Connection object
        HttpURLConnection httpClient = (HttpURLConnection)new URL(internalUrl).openConnection();
        //Set required parameters of the request object
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setDoOutput(true);
        return httpClient;
    }

    /**
     * Write the parameters to the OCR API ~ that is ~ write the output stream of parameters
     * @param fileStr string representation of the file being sent to the OCR API
     * @return Updated HttpURLConnection awaiting the results of the OCR process
     * @throws IOException thrown if there is a critical error in communication between this server and the OCR API
     */
    private HttpURLConnection sendOcrRequest(String fileStr) throws IOException {
        //Create connection to the OCR API
        HttpURLConnection ocrConn = createOCR_APIConnection();
        //Attempt to write the parameters of the request to the connection output stream
        try (OutputStream os = ocrConn.getOutputStream()){
            //Create argument wrapper JSON object and add the file parameter
            JSONObject ocrRequestArgs = new JSONObject().put("file", fileStr);
            //Write to the connection output stream
            os.write(ocrRequestArgs.toString().getBytes());
            os.flush();
        }
        return ocrConn;
    }

    /**
     * Read the result of the OCR request ~ that is ~ the result of the OCR process
     * @param ocrConn connection with the OCR API
     * @return Response string from the OCR API
     * @throws IOException thrown if there is a critical error in reading the input stream from the connection to the OCR API
     */
    private String readOcrResponse(HttpURLConnection ocrConn) throws IOException {
        //Attempt to read the result of the OCR request from the connection input stream
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ocrConn.getInputStream()))){
            //Build the string response from the lines of the OCR response
            StringBuilder ocrResponse = new StringBuilder();
            String ocrResponseLine;
            while((ocrResponseLine = in.readLine()) != null) {
                ocrResponse.append(ocrResponseLine);
            }
            return ocrResponse.toString();
        }
    }
}
