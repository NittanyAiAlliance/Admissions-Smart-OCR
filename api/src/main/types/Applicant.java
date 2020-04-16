package main.types;

/**
 * Object to abstract data for the data on an applicant
 */
public class Applicant {
    private String firstName, lastName, highSchoolName, ceebCode;

    public Applicant(String firstName, String lastName, String highSchoolName, String ceebCode){
        this.firstName = firstName;
        this.lastName = lastName;
        this.highSchoolName = highSchoolName;
        this.ceebCode = ceebCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getHighSchoolName() {
        return highSchoolName;
    }

    public String getCeebCode() {
        return ceebCode;
    }
}
