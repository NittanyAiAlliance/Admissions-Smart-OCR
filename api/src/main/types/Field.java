package main.types;

/**
 * Class to encapsulate field data
 */
public class Field {
    private String expected, actual;
    private boolean isValid;

    /**
     * Default constructor
     * @param expected expected value ~ what did the OCR think the field was?
     * @param actual actual value ~ what did the user submit as the actual value of that field?
     */
    public Field(String expected, String actual, boolean isValid){
        this.expected = expected;
        this.actual = actual;
        this.isValid = isValid;
    }

    /**
     * Getter for validity of this field ~ that is ~ did the actual field value match the expected
     * @return does the actual field value match expected field value?
     */
    public boolean getValuesMatch(){
        return isValid;
    }

    /**
     * Getter for expected property
     * @return expected property
     */
    public String getExpected(){
        return this.expected;
    }

    /**
     * Getter for actual property
     * @return actual property
     */
    public String getActual(){
        return this.actual;
    }
}
