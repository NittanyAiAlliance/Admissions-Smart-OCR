package main.managers;

import main.data.DatabaseInteraction;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParseErrorManager {
    private DatabaseInteraction database;
    public ParseErrorManager(){
        database = new DatabaseInteraction();
    }

    /**
     * Add a new parse error record to the database
     * @param actual what the user put
     * @param expected what we thought it was
     */
    public void addParseError(String actual, String expected){
        String addParseErrorSql = "INSERT INTO PARSE_ERRORS (ACTUAL, EXPECTED) VALUES (?, ?)";
        PreparedStatement addParseErrorStmt = database.prepareStatement(addParseErrorSql);
        try {
            addParseErrorStmt.setString(1, actual);
            addParseErrorStmt.setString(2, expected);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        database.nonQuery(addParseErrorStmt);
    }
}
