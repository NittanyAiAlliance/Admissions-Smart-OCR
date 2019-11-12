package main.managers;

import main.data.DatabaseInteraction;
import main.types.ErrorLog;
import main.types.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogManager {
    private DatabaseInteraction database;
    private Path errorLogFile;
    public LogManager(){
        database = new DatabaseInteraction();
        errorLogFile = Paths.get(System.getProperty("user.home") + "/logs/api_error_log.log");
        try {
            if(!Files.exists(errorLogFile)){
                Files.createFile(errorLogFile);
            }
        } catch (IOException ioEx) {
            System.out.println("Could not access the error logging file");
        }
    }

    /**
     * Write a standard transaction log to the database
     * @param thisLog
     */
    public void writeLog(Log thisLog){
        //Prepare log write statement
        String writeLogSql = "INSERT INTO TRANSACTION_LOGS(TYPE, CONTENT, TIMESTAMP) VALUES (?, ?, ?))";
        PreparedStatement writeLogStmt = database.prepareStatement(writeLogSql);
        try {
            writeLogStmt.setInt(1, thisLog.getType().ordinal());
            writeLogStmt.setString(2, thisLog.getContent());
            writeLogStmt.setTimestamp(3, new Timestamp(thisLog.getTimestamp().getTime()));
            //Run log entry statement
            database.nonQuery(writeLogStmt);
        } catch (SQLException sqlEx) {
            //Something went wrong - log the error to the error output
            this.writeErrorLog(new ErrorLog(sqlEx, "Writing transaction log failed"));
        }
    }

    /**
     * An exception has occurred in the API - uh oh. Write an error log to the error log file
     * @param log error log object
     */
    public void writeErrorLog(ErrorLog log){

    }
}
