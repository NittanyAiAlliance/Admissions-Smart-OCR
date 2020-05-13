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
            JSONObject subObj = new JSONObject();
            subObj.put("subId", thisSubmission.getUid());
            subObj.put("firstname", thisSubmission.getFirstname());
            subObj.put("lastname", thisSubmission.getLastname());
            subObj.put("highschool", thisSubmission.getHighschool());
            subObj.put("ceeb", thisSubmission.getCeeb());
            subObj.put("timestamp", thisSubmission.getTimeStamp());
            int validCounter = 0;
            int totalCounter = 0;
            for (int j = 0; j < thisSubmission.getFields().size(); j++) {
                Field thisField = thisSubmission.getFields().get(j);
                if (thisField.getValuesMatch()) {
                    validCounter++;
                }
                totalCounter++;
            }
            subObj.put("count", totalCounter);
            subObj.put("validcount", validCounter);
            submissionsArr.put(subObj);
        }
        return submissionsArr;
    }
}
