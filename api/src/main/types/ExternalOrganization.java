package main.types;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExternalOrganization {
    private int atpCode;
    private String name, addressLine1, addressLine2, city, state, zip, country;

    /**
     * Default constructor
     */
    public ExternalOrganization(int atpCode, String name, String[] addressLines, String city, String state, String zip, String country){
        this.atpCode = atpCode;
        this.name = name;
        this.addressLine1 = addressLines[0];
        this.addressLine2 = addressLines[1];
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }

    public int getAtpCode() {
        return atpCode;
    }

    public String getName() {
        return name;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    /**
     * Convert this external organization object into its JSON representation
     * @return JSONObject representation of this external organization object
     */
    public JSONObject convertToJson(){
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("ceeb", this.getAtpCode());
        jsonObj.put("name", this.getName());
        JSONArray addressLinesArr = new JSONArray();
        addressLinesArr.put(this.getAddressLine1());
        addressLinesArr.put(this.getAddressLine2());
        jsonObj.put("addressLines", addressLinesArr);
        jsonObj.put("city", this.city);
        jsonObj.put("state", this.state);
        jsonObj.put("zip", this.zip);
        jsonObj.put("country", this.country);
        return jsonObj;
    }
}
