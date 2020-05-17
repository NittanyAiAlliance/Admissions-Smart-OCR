package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.managers.SubmissionManager;
import main.types.Field;
import main.types.Submission;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GetSubmissionHandler extends HandlerPrototype implements HttpHandler {
    public GetSubmissionHandler(){
        super.requiredKeys = new String[] { };
        super.handlerName = "Get Submission Handler";
    }

    @Override
    public void fulfillRequest(JSONObject requestParams) {
        SubmissionManager submissionManager = new SubmissionManager();
        List<Submission> submissions = submissionManager.getSubmissions();
        JSONArray submissionArr = formatSubmissions(submissions);
        returnActionSuccess(new JSONObject().put("submissions", submissionArr));
    }

    /**
     * Convert the list of Submission objects into a returnable json array
     * @param submissions submission object array
     * @return json array representation of the submission object array
     */
    private JSONArray formatSubmissions(List<Submission> submissions){
        JSONArray submissionsArr = new JSONArray();
        for (Submission thisSubmission : submissions) {
            //Get the JSON object representation of the object
            JSONObject subObj = thisSubmission.convertToJson();
            //Add additional fields validity data properties
            int validFieldCount = SubmissionManager.getValidFieldCount(thisSubmission);
            subObj.put("count", thisSubmission.getFields().size());
            subObj.put("validcount", validFieldCount);
            submissionsArr.put(subObj);
        }
        return submissionsArr;
    }
}
