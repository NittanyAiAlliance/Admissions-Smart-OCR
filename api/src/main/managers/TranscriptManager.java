package main.managers;

import main.data.DatabaseInteraction;
import main.types.Transcript;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TranscriptManager {

    private DatabaseInteraction database;

    public TranscriptManager(){
        this.database = new DatabaseInteraction();
    }

    /**
     * Create a new transcript data record
     * @param metadata meta data object from the request object
     * @param fileString file string from the request object
     */
    public Transcript createNew(JSONObject metadata, String fileString) throws SQLException{
        String insertTranscriptSql = "INSERT INTO TRANSCRIPTS (TRANSCRIPT_ID, METADATA, TRANSCRIPT_FILE) VALUES (?, ?, ?)";
        PreparedStatement insertTranscriptStmt = database.prepareStatement(insertTranscriptSql);
        String TEMP_ID = Integer.toString(new Random().nextInt());
        //Set the PSU ID identifier
        insertTranscriptStmt.setString(1, TEMP_ID);
        //Set the meta data BLOB
        Blob metaDataBlob = database.getBlob();
        metaDataBlob.setBytes(1, metadata.toString().getBytes());
        insertTranscriptStmt.setBlob(2, metaDataBlob);
        //Set the file string data BLOB
        Blob transcriptDataBlob = database.getBlob();
        transcriptDataBlob.setBytes(1, fileString.getBytes());
        insertTranscriptStmt.setBlob(3, transcriptDataBlob);
        database.nonQuery(insertTranscriptStmt);
        //Create transcript data structure
        return new Transcript(TEMP_ID, metadata, fileString);
    }

    /**
     * Record the results of the OCR scan and parse of this transcript
     * @param ocrResponse formatted response object from the OCR API
     */
    public void putResults(String ocrResponse, Transcript transcript) throws SQLException {
        String insertTranscriptResultsSql = "INSERT INTO TRANSCRIPT_QUEUE (RESULTS, PSU_ID, HS_EXT_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, CAMPUS, CITIZENSHIP, DOCUMENT_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement insertTranscriptResultsStmt = database.prepareStatement(insertTranscriptResultsSql);
        //Set the OCR response object BLOB
        Blob resultsBlob = database.getBlob();
        resultsBlob.setBytes(1, ocrResponse.getBytes());
        insertTranscriptResultsStmt.setBlob(1, resultsBlob);
        //Set the other metadata properties relevant to the transcript queue
        insertTranscriptResultsStmt.setString(2, transcript.getMetaDataProp("PSU_ID"));
        insertTranscriptResultsStmt.setString(3, transcript.getMetaDataProp("HS_EXT_ID"));
        insertTranscriptResultsStmt.setString(4, transcript.getMetaDataProp("FIRST_NAME"));
        insertTranscriptResultsStmt.setString(5, transcript.getMetaDataProp("MIDDLE_NAME"));
        insertTranscriptResultsStmt.setString(6, transcript.getMetaDataProp("LAST_NAME"));
        insertTranscriptResultsStmt.setString(7, transcript.getMetaDataProp("CAMPUS"));
        insertTranscriptResultsStmt.setString(8, transcript.getMetaDataProp("CITIZENSHIP"));
        insertTranscriptResultsStmt.setString(9, transcript.getMetaDataProp("DOCUMENT_ID"));
        //Write the data record to the database
        database.nonQuery(insertTranscriptResultsStmt);
    }

    public Transcript fetchById(String transcriptId) throws SQLException {
        String fetchByIdSql = "SELECT TRANSCRIPT_FILE FROM TRANSCRIPTS WHERE TRANSCRIPT_ID = ?";
        PreparedStatement fetchByIdStmt = database.prepareStatement(fetchByIdSql);
        fetchByIdStmt.setString(1, transcriptId);
        ResultSet transcriptResults = database.query(fetchByIdStmt);
        if(transcriptResults.next()){

        }
        return null;
    }

    public JSONArray fetchQueue() throws SQLException {
        String fetchQueueSql = "SELECT TIMESTAMP, PSU_ID, HS_EXT_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, CAMPUS, CITIZENSHIP, DOCUMENT_ID, CHECKED_OUT FROM TRANSCRIPT_QUEUE";
        PreparedStatement fetchQueueStmt = database.prepareStatement(fetchQueueSql);
        ResultSet fetchQueueResults = database.query(fetchQueueStmt);
        //TODO: move to non-JSON object array ~ for DEMO purposes only
        JSONArray transcripts = new JSONArray();
        while(fetchQueueResults.next()){
            transcripts.put(new JSONObject(){{
                put("TIMESTAMP", fetchQueueResults.getTimestamp("TIMESTAMP").toString());
                put("PSU_ID", fetchQueueResults.getString("PSU_ID"));
                put("HS_EXT_ID", fetchQueueResults.getString("HS_EXT_ID"));
                put("FIRST_NAME", fetchQueueResults.getString("FIRST_NAME"));
                put("MIDDLE_NAME", fetchQueueResults.getString("MIDDLE_NAME"));
                put("LAST_NAME", fetchQueueResults.getString("LAST_NAME"));
                put("CAMPUS", fetchQueueResults.getString("CAMPUS"));
                put("CITIZENSHIP", fetchQueueResults.getString("CITIZENSHIP"));
                put("DOCUMENT_ID", fetchQueueResults.getString("DOCUMENT_ID"));
                put("CHECKED_OUT", fetchQueueResults.getBoolean("CHECKED_OUT"));
            }});
        }
        return transcripts;
    }

    /**
     * Fetch the results of a transcript in the transcript queue
     * @param docId id of the requesting transcript document
     * @return results JSON object
     * @throws SQLException sql exception in the query of the results
     */
    public JSONObject fetchResults(String docId) throws SQLException {
        String fetchTranscriptResultSql = "SELECT RESULTS FROM TRANSCRIPT_QUEUE WHERE DOCUMENT_ID = ?";
        PreparedStatement fetchTranscriptResultStmt = database.prepareStatement(fetchTranscriptResultSql);
        fetchTranscriptResultStmt.setString(1, docId);
        ResultSet results = database.query(fetchTranscriptResultStmt);
        results.next();
        Blob resultBlob = results.getBlob("RESULTS");
        byte[] resultBytes = resultBlob.getBytes(1, (int)resultBlob.length());
        return new JSONObject(new String(resultBytes));
    }

    /**
     * Insert a transcript record into the database
     * @param transcript transcript to insert
     */
    public void putTranscript(Transcript transcript) throws SQLException {
        String putTranscriptSql = "INSERT INTO TRANSCRIPTS (TRANSCRIPT_ID, TRANSCRIPT_FILE, METADATA) VALUES (?, ?, ?)";
        PreparedStatement putTranscriptStmt = database.prepareStatement(putTranscriptSql);
        putTranscriptStmt.setString(1, transcript.getDocId());
        //Create transcript image blob
        Blob imageFileBlob = database.getBlob();
        imageFileBlob.setBytes(1, transcript.getImageBlob());
        putTranscriptStmt.setBlob(2, imageFileBlob);
        //Create metadata object blob
        Blob metaDataBlob = database.getBlob();
        metaDataBlob.setBytes(1, transcript.getMetaDataBlob());
        putTranscriptStmt.setBlob(3, metaDataBlob);
        database.nonQuery(putTranscriptStmt);
    }
}
