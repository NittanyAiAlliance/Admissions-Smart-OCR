package main.managers;

import main.data.DatabaseInteraction;
import main.types.ErrorLog;
import main.types.InteractionLog;
import main.types.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogManager {
    private DatabaseInteraction database;
    private static final Path errorLogFile = Paths.get(System.getProperty("user.home") + "/logs/api_error_log.log");
    public LogManager(){
        database = new DatabaseInteraction();
    }

    /**
     * Write a standard transaction log to the database
     * @param thisLog log object to write
     */
    public void writeLog(Log thisLog){
        //Prepare log write statement
        String writeLogSql = "INSERT INTO TRANSACTION_LOGS(TYPE, REQUEST_VALIDITY, HASH_VERIFY_VALIDITY, RESPONSE_CONTENT, TIMESTAMP) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement writeLogStmt = database.prepareStatement(writeLogSql);
        try {
            writeLogStmt.setInt(1, thisLog.getType().ordinal());
            writeLogStmt.setBoolean(2, thisLog.getRequestValidity());
            writeLogStmt.setBoolean(3, thisLog.getHashVerifyValidity());
            writeLogStmt.setString(4, thisLog.getContent());
            writeLogStmt.setTimestamp(5, new Timestamp(thisLog.getTimestamp().getTime()));
            //Run log entry statement
            database.nonQuery(writeLogStmt);
        } catch (SQLException sqlEx) {
            //Something went wrong - log the error to the error output
            writeErrorLog(new ErrorLog(sqlEx, "Writing transaction log failed"));
        }
    }

    public void write(InteractionLog log) {
        String writeLogSql = "INSERT INTO INTERACTION_LOGS(TITLE, DESCRIPTION, UID, TIMESTAMP) VALUES (?, ?, ?, ?)";
        PreparedStatement writeLogStmt = database.prepareStatement(writeLogSql);
        try {
            writeLogStmt.setString(1, log.getTitle());
            writeLogStmt.setString(2, log.getDescription());
            writeLogStmt.setString(3, log.getUser());
            writeLogStmt.setTimestamp(4, new Timestamp(log.getTimeStamp().getTime()));
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    /**
     * An exception has occurred in the API - uh oh. Write an error log to the error log file
     * @param log error log object
     */
    public static void writeErrorLog(ErrorLog log){
        try {
            if(!Files.exists(errorLogFile)){
                Files.createFile(errorLogFile);
            }
            Files.write(errorLogFile, log.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ioEx) {
            System.out.println("Could not access the error logging file");
        }
    }
}
