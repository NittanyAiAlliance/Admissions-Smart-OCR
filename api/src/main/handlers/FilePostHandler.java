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

import java.io.IOException;
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
        super.requiredKeys = new String[] { "file", "metadata", "token" };
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
        JSONObject ocrResponseObj = ocrApiManager.formatResponse(ocrResponse);
        //Everything looks good ~ send it back
        returnActionSuccess(ocrResponseObj);
    }
    @Override
    public Log toLog(){
        return new Log();
    }
}
