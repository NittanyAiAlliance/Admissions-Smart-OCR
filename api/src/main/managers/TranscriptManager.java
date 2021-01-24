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
     * @param ocrResponseObject formatted response object from the OCR API
     */
    public void putResults(JSONObject ocrResponseObject, Transcript transcript) throws SQLException {
        String insertTranscriptResultsSql = "INSERT INTO TRANSCRIPT_QUEUE (PSU_ID, RESULTS) VALUES (?, ?)";
        PreparedStatement insertTranscriptResultsStmt = database.prepareStatement(insertTranscriptResultsSql);
        //Set the transcript ID argument
        insertTranscriptResultsStmt.setString(1, transcript.getPsuId());
        //Set the OCR response object BLOB
        Blob resultsBlob = database.getBlob();
        resultsBlob.setBytes(1, ocrResponseObject.toString().getBytes());
        insertTranscriptResultsStmt.setBlob(2, resultsBlob);
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
        String fetchQueueSql = "SELECT TIMESTAMP, PSU_ID FROM TRANSCRIPT_QUEUE";
        PreparedStatement fetchQueueStmt = database.prepareStatement(fetchQueueSql);
        ResultSet fetchQueueResults = database.query(fetchQueueStmt);
        //TODO: move to non-JSON object array ~ for DEMO purposes only
        JSONArray transcripts = new JSONArray();
        while(fetchQueueResults.next()){
            transcripts.put(new JSONObject(){{
                put("TIMESTAMP", fetchQueueResults.getTimestamp("TIMESTAMP").toString());
                put("PSU_ID", fetchQueueResults.getString("PSU_ID"));
            }});
        }
        return transcripts;
    }

    public JSONObject fetchQueuedTranscripts() {
        return new JSONObject(){{
            put("COURSES", new JSONArray("[{\"credits\":\"1.00\",\"grade\":\"92\",\"generic_name\":\"SPANISH 3\",\"name\":\"SPANISH 3\"},{\"credits\":\"1.00\",\"grade\":\"80\",\"generic_name\":\"Others\",\"name\":\"CP BRIT LIT\"},{\"credits\":\"1.00\",\"grade\":\"98\",\"generic_name\":\"COMPUTER SCIENCE 1\",\"name\":\"AP COMPUTER SCIENCE\"},{\"credits\":\"1.00\",\"grade\":\"73\",\"generic_name\":\"PHYSICS\",\"name\":\"HNRS PHYSICS\"},{\"credits\":\".50\",\"grade\":\"76\",\"generic_name\":\"PHYSICS\",\"name\":\"HNRS PHYSICS LAB\"},{\"credits\":\"1.00\",\"grade\":\"89\",\"generic_name\":\"Others\",\"name\":\"Digital Electronics (DE)\"},{\"credits\":\"50\",\"grade\":\"P\",\"generic_name\":\"ENGLISH 11\",\"name\":\"DIRECTED STUDY 11\"},{\"credits\":\".50\",\"grade\":\"97\",\"generic_name\":\"ENGLISH 11\",\"name\":\"PE 11\"},{\"credits\":\"1.00\",\"grade\":\"86\",\"generic_name\":\"Others\",\"name\":\"HNRS PRECALC\"},{\"credits\":\"1.00\",\"grade\":\"83\",\"generic_name\":\"Others\",\"name\":\"HNRS WLDHIST\"},{\"credits\":\"1.00\",\"grade\":\"90\",\"generic_name\":\"Others\",\"name\":\"Easton Area High School CPLIT/COMP9\"},{\"credits\":\"1.00\",\"grade\":\"88\",\"generic_name\":\"BANKING AND FINANCE\",\"name\":\"Intro to Engineering and Design (IED)\"},{\"grade\":\"89\",\"generic_name\":\"ENGLISH 9\",\"name\":\"PE 9\"},{\"credits\":\"1.00\",\"grade\":\"96\",\"generic_name\":\"SPANISH 2\",\"name\":\"SPANISH 2\"},{\"credits\":\"1.00\",\"grade\":\"83\",\"generic_name\":\"Others\",\"name\":\"HNS USHIST I\"},{\"credits\":\"1.00\",\"grade\":\"90\",\"generic_name\":\"COMPUTER APPLICATIONS\",\"name\":\"Honors Computer Science Principles\"},{\"grade\":\"50\",\"generic_name\":\"COMPUTER SCIENCE 1\",\"name\":\"HNRS E & SPACE SCIENCE LAB 90\"},{\"credits\":\"1.00\",\"grade\":\"73\",\"generic_name\":\"GEOMETRY\",\"name\":\"HNRS GEOMTRY\"},{\"credits\":\"50\",\"grade\":\"P\",\"generic_name\":\"ENGLISH 9\",\"name\":\"DIRECTED STUDY 9\"},{\"credits\":\"1.00\",\"grade\":\"88\",\"generic_name\":\"EARTH SCIENCE\",\"name\":\"HNRS E & SPACE SCIENCE\"},{\"credits\":\"1.00\",\"grade\":\"84\",\"generic_name\":\"Others\",\"name\":\"Easton Area High School CP AMER LIT\"},{\"credits\":\"1.00\",\"grade\":\"91\",\"generic_name\":\"COMPUTER SCIENCE 2\",\"name\":\"HNRS ALG 2\"},{\"credits\":\"1.00\",\"grade\":\"81\",\"generic_name\":\"BIOLOGY\",\"name\":\"HNRS BIOLOGY\"},{\"credits\":\"50\",\"grade\":\"P\",\"generic_name\":\"ENGLISH 10\",\"name\":\"DIRECTED STUDY 10\"},{\"credits\":\"50\",\"grade\":\"80\",\"generic_name\":\"HEALTH EDUCATION\",\"name\":\"HEALTH 10\"},{\"grade\":\"84\",\"generic_name\":\"Others\",\"name\":\"PE 10 FITNESS\"},{\"credits\":\"1.00\",\"grade\":\"93\",\"generic_name\":\"Others\",\"name\":\"Principles of Engineering (POE)\"},{\"generic_name\":\"BIOLOGY\",\"name\":\"HNRS BIOLOGY LAB 81 50\"},{\"credits\":\"1.00\",\"grade\":\"95\",\"generic_name\":\"COMPUTER SCIENCE 2\",\"name\":\"HNS USHIST 2\"}],\"name\":\"\"}]"));
            put("RECEIVED_TIMESTAMP", "2020-12-01 01:14:12.0");
            put("PROCESSED_TIMESTAMP", "2020-12-01 03:32:15.0");
            put("HIGH_SCHOOL", new JSONObject() {{
                put("NAME", "Boyertown Area Senior High School");
                put("ADDRESS", "20 Monroe St Boyertown, PA 19512");
                put("PHONE", "610-334-8020");
                put("CEEB", "359580");
            }});
            put("STUDENT", new JSONObject() {{
                put("FIRST_NAME", "Joel");
                put("LAST_NAME", "Seidel");
                put("PSU_ID", "JDS6294");
            }});
        }};
    }
}
