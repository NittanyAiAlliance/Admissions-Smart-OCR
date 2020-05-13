package main.types;

import java.util.List;

public class Submission {
    private String uid, firstname, lastname, highschool, ceeb, timeStamp;
    private List<Field> fields;
    public Submission(String uid, String firstname, String lastname, String highschool, String ceeb, String timeStamp, List<Field> fields) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.highschool = highschool;
        this.ceeb = ceeb;
        this.timeStamp = timeStamp;
        this.fields = fields;
    }

    public String getUid() {
        return uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getHighschool() {
        return highschool;
    }

    public String getCeeb() {
        return ceeb;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public List<Field> getFields() {
        return fields;
    }
}
