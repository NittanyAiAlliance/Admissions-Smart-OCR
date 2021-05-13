package main.types;

import org.json.JSONObject;

public class Transcript {
    private String docId;
    private JSONObject metadata;
    private String transcriptImage;

    public Transcript(String docId, JSONObject metadata, String transcriptImage) {
        this.docId = docId;
        this.metadata = metadata;
        this.transcriptImage = transcriptImage;
    }

    public String getDocId(){
        return this.docId;
    }

    public byte[] getMetaDataBlob() {
        return this.metadata.toString().getBytes();
    }

    public byte[] getImageBlob() {
        return this.transcriptImage.getBytes();
    }

    public String getImageString() {
        return this.transcriptImage;
    }

    public String getMetaDataProp(String propName) {
        return this.metadata.getString(propName);
    }
}
