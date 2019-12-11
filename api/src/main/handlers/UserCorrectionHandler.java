package main.handlers;

import main.managers.ParseErrorManager;
import main.types.Field;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserCorrectionHandler extends HandlerPrototype {
    public UserCorrectionHandler(){
        super.requiredKeys = new String[] {"fields", "token"};
        super.handlerName = "User Correction Handler";
    }

    @Override
    public void fulfillRequest(JSONObject requestParams) {
        List<JSONObject> recordsList = getRecords(requestParams);
        List<Field> allFieldsList = new ArrayList<>();
        //Create the list of all available records
        for(JSONObject thisRecord : recordsList) {
            List<Field> thisRecordFields = getFields(thisRecord);
            allFieldsList.addAll(thisRecordFields);
        }
        //Record the errors that the user corrected
        ParseErrorManager parseErrorManager = new ParseErrorManager();
        for(Field thisField : allFieldsList){
            if(thisField.getIsEdited()){
                //This field has been changed
                String actual = thisField.getCurrentVersion().getValue();
                String expected = thisField.getExpectedVersion().getValue();
                //Record the change in the database
                parseErrorManager.addParseError(actual, expected);
            }
        }

    }

    /**
     * Get the fields from a specific class level
     * @param thisRecord the record to extract the fields from
     * @return list of fields contained in the record
     */
    private List<Field> getFields(JSONObject thisRecord){
        List<Field> fieldList = new ArrayList<>();
        JSONArray fieldObjArr = thisRecord.getJSONArray("fields");
        for(int i = 0; i < fieldObjArr.length(); i++) {
            fieldList.add(new Field(fieldObjArr.getJSONObject(i)));
        }
        return fieldList;
    }

    /**
     * Get the records from a transcript table
     * @param transcriptObj JSON version of the
     * @return list of the JSON versioned records
     */
    private List<JSONObject> getRecords(JSONObject transcriptObj){
        List<JSONObject> recordsList = new ArrayList<>();
        JSONArray recordObjArr = transcriptObj.getJSONArray("records");
        for(int i = 0; i < recordObjArr.length(); i++) {
            recordsList.add(recordObjArr.getJSONObject(i));
        }
        return recordsList;
    }
}