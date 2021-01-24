package main.types;

import org.json.JSONObject;

public class Transcript {
    private String id;
    private String psuId;
    private JSONObject metadata;
    private String file;

    public Transcript(String id, JSONObject metadata, String file) {
        this.id = id;
        this.metadata = metadata;
        this.file = file;
    }

    public String getId() { return this.id; }

    public String getPsuId() {
        return this.psuId;
    }

    public JSONObject getMetadata() {
        return this.metadata;
    }

    public String getFile() {
        return this.file;
    }
}
