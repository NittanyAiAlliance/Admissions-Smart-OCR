package main.types;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FieldVersion {
    private boolean isCurrent;
    private String value;

    /**
     * Constructor to convert a version object to an in-memory object
     * @param versionObj version JSON object
     */
    public FieldVersion(JSONObject versionObj) {
        this.isCurrent = versionObj.getBoolean("isCurrent");
        this.value = versionObj.getString("value");
    }

    /**
     * Get if this is the current version of the field - that is, the actual
     * @return if this is the current version of the field or not
     */
    public boolean getIsCurrent(){
        return this.isCurrent;
    }

    /**
     * The value of this version of the field
     * @return value of this version of the field
     */
    public String getValue(){
        return this.value;
    }
}
