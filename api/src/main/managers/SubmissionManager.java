package main.managers;

import main.data.DatabaseInteraction;
import main.types.Applicant;
import main.types.Field;
import main.types.Submission;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Character.digit;

/**
 * Manage data interactions with submitted fields
 */
public class SubmissionManager {
    private DatabaseInteraction database;

    /**
     * Constructor to initialize database connection
     */
    public SubmissionManager() {
        database = new DatabaseInteraction();
    }

    private String storageSecurityKey;

    /**
     * Create the records to store a submissions ~ this includes fields and the file
     *
     * @param submittingUser  applicant making the submissions
     * @param file            Base64 representation of the submitted file
     * @param submittedFields list of field objects submitted by the user
     * @return success boolean
     */
    public boolean createNewSubmission(Applicant submittingUser, String file, List<Field> submittedFields) {
        //Generate/collect properties of this submission
        String submissionUID = getSubmissionUUID();
        String fileRelPath = getStorageRelativeFilePath(submissionUID);
        String encryptedFile = encryptFile(file);
        createSubmissionRecord(submittingUser, submissionUID);
        //Store the transcript image/file
        if (storeFile(submissionUID, encryptedFile, fileRelPath)) {
            //Parse the fields and add to the field records
            createFieldRecords(submissionUID, submittedFields);
            return true;
        } else {
            //Something went wrong with file system or something. I don't know. The logs will
            return false;
        }
    }

    /**
     * Create a record of the submission ~ that is the user making the submissions
     *
     * @param submittingUser the applicant making the submission and their general information for localization
     * @param submissionUID  the unique identifier of the overall submission
     */
    private void createSubmissionRecord(Applicant submittingUser, String submissionUID) {
        String createSubmissionSql = "INSERT INTO SUBMISSIONS (SUBMISSION_ID, FIRST_NAME, LAST_NAME, HIGH_SCHOOL_NAME, CEEB_CODE) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement createSubmissionStmt = database.prepareStatement(createSubmissionSql);
        try {
            //Prepare the submissions statement and perform nonquery
            createSubmissionStmt.setString(1, submissionUID);
            createSubmissionStmt.setString(2, submittingUser.getFirstName());
            createSubmissionStmt.setString(3, submittingUser.getLastName());
            createSubmissionStmt.setString(4, submittingUser.getHighSchoolName());
            createSubmissionStmt.setString(5, submittingUser.getCeebCode());
            database.nonQuery(createSubmissionStmt);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    /**
     * Store the transcript image/file
     *
     * @param submissionUID unique identifier of the submission ~ that is ~ what it is referred to in data
     * @param encryptedFile Base64 representation of the transcript file encypted with server storage security key
     * @param filePath      constructed file path of this file ~ that is ~ where is the data being written to
     * @return were all of the storage task components successful?
     */
    private boolean storeFile(String submissionUID, String encryptedFile, String filePath) {
        //Write the transcript file to the file system, then link the relative path to its data UID
        return writeEncryptedFileToFileSystem(encryptedFile, filePath) && setFileSystemPointerToData(submissionUID, filePath);
    }

    /**
     * Write the encrypted file string to the server file system at the specified path
     *
     * @param encryptedFile encrypted string of the transcript image/file
     * @param filePath      constructed file path pointing to the server file system
     * @return was this task successful?
     */
    private boolean writeEncryptedFileToFileSystem(String encryptedFile, String filePath) {
        try {
            File transcriptFile = new File(filePath);
            if (!transcriptFile.createNewFile()) {
                //This means file collision, which (theoretically) cannot occur given the way files are named
                throw new IOException();
            }
            //Write the encrypted file string into the specified file path in the server file system
            FileWriter transcriptFileWriter = new FileWriter(transcriptFile);
            transcriptFileWriter.write(encryptedFile);
            return true;
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            return false;
        }
    }

    /**
     * Link the submission UID to the relative file path in the database
     *
     * @param submissionUID unique id of the submission ~ this is how it is referred in data
     * @param filePath      the pointer to the server file system
     * @return was this task successful?
     */
    private boolean setFileSystemPointerToData(String submissionUID, String filePath) {
        //Create insert pointer statement
        String insertFileSql = "INSERT INTO TRANSCRIPT_IMG (UUID, REL_PATH) VALUES (?, ?)";
        PreparedStatement insertFileStmt = database.prepareStatement(insertFileSql);
        try {
            //Prepare statement and perform nonquery
            insertFileStmt.setString(1, submissionUID);
            insertFileStmt.setString(2, filePath);
            database.nonQuery(insertFileStmt);
            return true;
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return false;
        }
    }

    /**
     * Encrypt the transcript file with the storage security key
     *
     * @param fileStr Base64 representation of the file
     * @return encrypted base64 representation of the file
     */
    private String encryptFile(String fileStr) {
        String encryptedFileStr = "";
        try {
            //Convert the security key to hexadecimal to make into AES key
            int length = storageSecurityKey.substring(0, 32).length();
            byte[] securityKeyBytes = new byte[length / 2];
            for (int i = 0; i < length; i += 2) {
                securityKeyBytes[i / 2] = (byte) ((digit(storageSecurityKey.charAt(i), 16) << 4) | digit(storageSecurityKey.charAt(i + 1), 16));
            }
            //Create the encryption key
            Key encryptKey = new SecretKeySpec(securityKeyBytes, "AES");
            //Configure the cipher with AES
            Cipher encryptFileCipher = Cipher.getInstance("AES");
            encryptFileCipher.init(Cipher.ENCRYPT_MODE, encryptKey);
            //Do encryption
            byte[] encryptedFileByteArr = encryptFileCipher.doFinal(fileStr.getBytes());
            encryptedFileStr = new String(encryptedFileByteArr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encryptedFileStr;
    }

    /**
     * Create and insert records for the parsed fields from the transcript submission
     *
     * @param submissionUID unique ID of the transcript ~ the PK from data
     * @param fields        field objects representing fields pulled from the transcript
     */
    private void createFieldRecords(String submissionUID, List<Field> fields) {
        String insertFieldSql = "INSERT INTO SUBMITTED_FIELDS (SUBMISSION_ID, IS_VALID, EXPECTED_VALUE, ACTUAL_VALUE) VALUES (?, ?, ?, ?)";
        PreparedStatement insertFieldStmt = database.prepareStatement(insertFieldSql);
        //Create statement for each of the fields to batch insert
        fields.forEach(thisField -> {
            try {
                //Create record for this field
                insertFieldStmt.setString(1, submissionUID);
                insertFieldStmt.setBoolean(2, thisField.getValuesMatch());
                insertFieldStmt.setString(3, thisField.getExpected());
                insertFieldStmt.setString(4, thisField.getActual());
                insertFieldStmt.addBatch();
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
        });
        //Execute the batch insert of all fields
        try {
            insertFieldStmt.executeBatch();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    /**
     * Generate a unique identifier for this transaction.
     * This is an arbitrary value that doesn't really matter, but hides the row id implementation
     *
     * @return a UID for this submission
     */
    private String getSubmissionUUID() {
        String hashToken = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //The unique ID of the transaction is the time in milliseconds
            String currentTime = Long.toString(Calendar.getInstance().getTimeInMillis());
            byte[] hash = digest.digest(currentTime.getBytes(StandardCharsets.UTF_8));
            hashToken = Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hashToken;
    }

    /**
     * Construct the storage path for an transcript data file
     *
     * @param submissionUID unique identifier for this transcript
     * @return storage location path string for the submitted file
     */
    private String getStorageRelativeFilePath(String submissionUID) {
        Properties storageProperties = new Properties();
        try {
            storageProperties.load(getClass().getResourceAsStream("server_storage.properties"));
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
        this.storageSecurityKey = storageProperties.getProperty("security_key");
        return System.getProperty("user.home") + File.separator + storageProperties.getProperty("dir") + File.separator + submissionUID.replaceAll("[^a-zA-Z0-9]", "") + storageProperties.getProperty("file_type");
    }

    /**
     * Get all submissions
     *
     * @return List of submission objects
     */
    public List<Submission> getSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        String getSubmissionsSql = "SELECT SUBMISSIONS.SUBMISSION_ID, FIRST_NAME, LAST_NAME, HIGH_SCHOOL_NAME, CEEB_CODE, SUBMISSION_TIMESTAMP, IS_VALID, EXPECTED_VALUE, ACTUAL_VALUE FROM SUBMISSIONS INNER JOIN SUBMITTED_FIELDS ON SUBMISSIONS.SUBMISSION_ID = SUBMITTED_FIELDS.SUBMISSION_ID";
        PreparedStatement getSubmissionsStmt = database.prepareStatement(getSubmissionsSql);
        try {
            //Get the submissions results ~ this will have them all, if there are any
            ResultSet submissionsResults = database.query(getSubmissionsStmt);
            submissions = parseSubmissionRecords(submissionsResults);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return submissions;
    }

    /**
     * Overload to return a single submission object
     *
     * @param id id of the submission
     * @return submission object for the given ID
     */
    public Submission getSubmissions(String id) {
        String getSubmissionsSql = "SELECT SUBMISSIONS.SUBMISSION_ID, FIRST_NAME, LAST_NAME, HIGH_SCHOOL_NAME, CEEB_CODE, SUBMISSION_TIMESTAMP, IS_VALID, EXPECTED_VALUE, ACTUAL_VALUE FROM SUBMISSIONS INNER JOIN SUBMITTED_FIELDS ON SUBMISSIONS.SUBMISSION_ID = SUBMITTED_FIELDS.SUBMISSION_ID WHERE SUBMISSIONS.SUBMISSION_ID = ?";
        PreparedStatement getSubmissionsStmt = database.prepareStatement(getSubmissionsSql);
        try {
            getSubmissionsStmt.setString(1, id);
            //Get the submissions results ~ this will have them all, if there are any
            ResultSet submissionsResults = database.query(getSubmissionsStmt);
            //Parse the submission which will be in the 0th element of the submission records array
            return parseSubmissionRecords(submissionsResults).get(0);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return null;
    }

    /**
     * Read the submission records from a result set and create a corresponding list of submission objects
     * @param subResults result set from the submission query
     * @return List of submission objects parsed from the result set
     */
    private List<Submission> parseSubmissionRecords(ResultSet subResults) throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        String currentSubmissionId = "";
        Submission thisSubmission = null;
        while(subResults.next()){
            String thisSubmissionId = subResults.getString("SUBMISSION_ID");
            if(thisSubmissionId.equals(currentSubmissionId)) {
                //This another field within the current submission
                thisSubmission.addField(parseFieldFromRecord(subResults));
            } else {
                //This is a new submission entirely
                if(thisSubmission != null){
                    //This submission is real, it should be added to the submission list
                    submissions.add(thisSubmission);
                }
                //Set the new submission id
                currentSubmissionId = thisSubmissionId;
                //Get the submission object from this field
                thisSubmission = parseSubmissionFromRecord(subResults);
                //Add the first field of this submission from this record
                thisSubmission.addField(parseFieldFromRecord(subResults));
            }
        }
        //If there are submissions in this query, the current one must be added here outside the loop
        if(thisSubmission != null){
            //There is a submission, add it to the submissions array
            submissions.add(thisSubmission);
        }
        return submissions;
    }

    /**
     * Parse a single submission from a record within a result set. This creates a submission object to describe the next n number of fields
     * @param subResults result set pointed at the desired submission
     * @return submission object representation of the submission in the result set
     * @throws SQLException thrown if there is an issue with pulling the properties from the ResultSet
     */
    private Submission parseSubmissionFromRecord(ResultSet subResults) throws SQLException {
        //Get the submission properties from the result set
        String submissionId = subResults.getString("SUBMISSION_ID");
        String firstName = subResults.getString("FIRST_NAME");
        String lastName = subResults.getString("LAST_NAME");
        String highSchoolName = subResults.getString("HIGH_SCHOOL_NAME");
        String ceebCode = subResults.getString("CEEB_CODE");
        String timeStamp = subResults.getString("SUBMISSION_TIMESTAMP");
        //Instantiate new submission object and return result
        return new Submission(submissionId, firstName, lastName, highSchoolName, ceebCode, timeStamp);
    }

    /**
     * Get the field object from the pointed record in the result set ~ this ignores the submission
     * @param subResults Result set pointed at the record to parse
     * @return Field object representation of the record field
     * @throws SQLException thrown if there is an issue with pulling the fields from the ResultSet
     */
    private Field parseFieldFromRecord(ResultSet subResults) throws SQLException {
        //Get the field properties from the result set
        boolean isValid = subResults.getBoolean("IS_VALID");
        String expectedValue = subResults.getString("EXPECTED_VALUE");
        String actualValue = subResults.getString("ACTUAL_VALUE");
        //Instantiate and return a field object representation of this record
        return new Field(expectedValue, actualValue, isValid);
    }
}