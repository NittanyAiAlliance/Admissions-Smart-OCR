package main.types;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Submission {
    private String uid, firstname, lastname, highschool, ceeb, timeStamp;
    private List<Field> fields;

    /**
     * Constructor with full arguments
     * @param uid unique id of this submission, also known as the submission id
     * @param firstname First name of the submitting person
     * @param lastname Last name of the submitting person
     * @param highschool High school name of the submitting person
     * @param ceeb CEEB code of the submitting person
     * @param timeStamp Time stamp of the submission
     * @param fields List of fields within this submission
     */
    public Submission(String uid, String firstname, String lastname, String highschool, String ceeb, String timeStamp, List<Field> fields) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.highschool = highschool;
        this.ceeb = ceeb;
        this.timeStamp = timeStamp;
        this.fields = fields;
    }

    /**
     * Constructor for creating submission object without fields
     * @param uid unique id of this submission, also known as the submission id
     * @param firstname First name of the submitting person
     * @param lastname Last name of the submitting person
     * @param highschool High school name of the submitting person
     * @param ceeb CEEB code of the submitting person
     * @param timeStamp Time stamp of the submission
     */
    public Submission(String uid, String firstname, String lastname, String highschool, String ceeb, String timeStamp){
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.highschool = highschool;
        this.ceeb = ceeb;
        this.timeStamp = timeStamp;
        this.fields = new ArrayList<>();
    }

    /**
     * Getter for submission id property
     * @return submission id
     */
    public String getUid() {
        return uid;
    }

    /**
     * Getter for the first name property
     * @return first name property
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Getter for the last name property
     * @return last name property
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Getter for the high school property
     * @return high school property
     */
    public String getHighschool() {
        return highschool;
    }

    /**
     * Getter for the CEEB code property
     * @return CEEB code property
     */
    public String getCeeb() {
        return ceeb;
    }

    /**
     * Getter for the time stamp property
     * @return time stamp property
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Getter for the fields list
     * @return list of fields in this submission
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Add a field to the list of fields for this submission
     * @param field field object to add to the submission
     */
    public void addField(Field field){
        this.fields.add(field);
    }

    /**
     * Convert this object into a JSON representation
     * @return JSONObject version of this object
     */
    public JSONObject convertToJson(){
        //Create JSON object for the submission from this object
        JSONObject thisJson = new JSONObject();
        thisJson.put("subId", this.getUid());
        thisJson.put("firstname", this.getFirstname());
        thisJson.put("lastname", this.getLastname());
        thisJson.put("highschool", this.getHighschool());
        thisJson.put("ceeb", this.getCeeb());
        thisJson.put("timestamp", this.getTimeStamp());
        //Create array of corresponding fields within this submission
        JSONArray fieldArr = new JSONArray();
        for (Field field : this.getFields()) {
            JSONObject fieldObj = new JSONObject();
            fieldObj.put("expected", field.getExpected());
            fieldObj.put("actual", field.getActual());
            fieldObj.put("isValid", field.getIsValid());
            fieldArr.put(fieldObj);
        }
        thisJson.put("fields", fieldArr);
        return thisJson;
    }
}
