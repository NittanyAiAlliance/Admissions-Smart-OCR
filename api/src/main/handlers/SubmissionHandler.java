package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.managers.SubmissionManager;
import main.types.Applicant;
import main.types.Field;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Handler for final submission the transcript
 */
public class SubmissionHandler extends HandlerPrototype implements HttpHandler {
    public SubmissionHandler(){
        super.requiredKeys = new String[] { "user", "fields", "file", "token" };
        super.handlerName = "Submission";
    }

    @Override
    public void fulfillRequest(JSONObject requestParams) {
        JSONObject user = requestParams.getJSONObject("user");
        Applicant submittingUser = parseUserJson(user);
        JSONArray fields = requestParams.getJSONArray("fields");
        List<Field> fieldList = parseFieldJsonArray(fields);
        String file = requestParams.getString("file");
        SubmissionManager submissionManager = new SubmissionManager();
        if(submissionManager.createNewSubmission(submittingUser, file, fieldList)){
            returnActionSuccess();
        } else {
            returnActionFailure("Could not complete submission");
        }
    }

    /**
     * Convert a json array of fields into a list of field objects
     * @param fields json array containing the fields from OCR/user/both
     * @return list of field objects
     */
    private List<Field> parseFieldJsonArray(JSONArray fields){
        //Beautiful, perfect, incredible lambda one liner to convert json array to list of field objects
        return IntStream.range(0, fields.length()).mapToObj(fields::getJSONObject).map(field -> new Field(field.getJSONObject("expected").toString(), field.getJSONObject("actual").toString(), field.getBoolean("isValid"))).collect(Collectors.toList());
    }

    private Applicant parseUserJson(JSONObject userJson){
        String firstName = userJson.getString("firstname");
        String lastName = userJson.getString("lastname");
        String highSchoolName = userJson.getString("hs_name");
        String ceebCode = userJson.getString("ceeb_code");
        return new Applicant(firstName, lastName, highSchoolName, ceebCode);
    }
}
