package main.types;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to encapsulate field data
 */
public class Field {
    private String name, value;
    private boolean isEdited;
    private List<FieldVersion> versions;

    /**
     * Constructor to create a default field
     * @param fieldRecord JSON representation of the field
     */
    public Field(JSONObject fieldRecord){
        this.name = fieldRecord.getString("name");
        //Determine if this field has been edited or not
        if(fieldRecord.has("versions")){
            //There are several versions of this field as it has been edited
            this.isEdited = true;
            this.versions = getFieldVersions(fieldRecord);
        } else {
            //There is but one version of this field, it is current
            this.isEdited = false;
            this.versions = new ArrayList<>();
            //Add the only version that exists to the version list
            this.versions.add(new FieldVersion(fieldRecord));
        }
    }

    /**
     * Get the versions of an edited field
     * @param fieldRecord field record object containing separate versions
     * @return list of the versions objects within this field
     */
    private List<FieldVersion> getFieldVersions(JSONObject fieldRecord){
        JSONArray fieldVersionArr = fieldRecord.getJSONArray("versions");
        List<FieldVersion> versions = new ArrayList<>();
        for(int i = 0; i < fieldVersionArr.length(); i ++) {
            versions.add(new FieldVersion(fieldVersionArr.getJSONObject(i)));
        }
        return versions;
    }

    /**
     * Get if this field has been edited or not
     * @return if this field has been edited or not
     */
    public boolean getIsEdited(){
        return this.isEdited;
    }

    /**
     * Get the current version of the field
     * @return current field version
     */
    public FieldVersion getCurrentVersion(){
        //Find the current field from the fields list
        for(FieldVersion thisVersion : this.versions){
            if(thisVersion.getIsCurrent()){
                return thisVersion;
            }
        }
        return null;
    }

    /**
     * Get the expected version of the field - that is, the version that the parser sent to the front end
     * @return expected field version
     */
    public FieldVersion getExpectedVersion(){
        //Find the non current field from the fields list
        for(FieldVersion thisVersion : this.versions){
            if(!thisVersion.getIsCurrent()){
                return thisVersion;
            }
        }
        return null;
    }
}
