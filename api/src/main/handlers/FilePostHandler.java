package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.managers.ExternalOrganizationManager;
import main.managers.GenericCourseManager;
import main.managers.LogManager;
import main.managers.OcrApiManager;
import main.types.ErrorLog;
import main.types.ExternalOrganization;
import main.types.GenericSubjectArea;
import main.types.Log;
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
 * Handler for posting a file action
 * @author Joel Seidel
 */
public class FilePostHandler extends HandlerPrototype implements HttpHandler {
    /**
     * Required constructor (HandlerProtoype)
     */
    public FilePostHandler(){
        super.handlerName = "File Post Handler";
        super.requiredKeys = new String[] { "file", "token" };
    }

    /**
     * Fulfill file post request
     * @param requestParams parameters associated with the request
     */
    @Override
    public void fulfillRequest(JSONObject requestParams) {
        //Get image file string from request parameters
        String imgStr = requestParams.getString("file").split(",")[1];
        //Create OCR API request
        String ocrResponse = "";
        OcrApiManager ocrApiManager = new OcrApiManager();
        try {
            //Perform the request to the OCR API
            ocrResponse = ocrApiManager.doOcr(imgStr);
        } catch (IOException ioEx){
            ioEx.printStackTrace();
        }
        //Convert the OCR API response string to a JSON object
        JSONObject ocrResponseObj = formatOCRResponse(ocrResponse);
        //Everything looks good ~ send it back
        returnActionSuccess(ocrResponseObj);
    }

    /**
     * Convert the stringified list of lists into the JSON object the front-end is expecting
     * @param ocrStr string response from the ocr api
     * @return properly formatted JSON object
     */
    private JSONObject formatOCRResponse(String ocrStr){
        JSONObject ocrObj = new JSONObject(ocrStr);
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

    @Override
    public Log toLog(){
        return new Log();
    }
}
