package main.listeners;

import main.data.DatabaseInteraction;
import main.managers.LogManager;
import main.types.InteractionLog;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class InteractionLogListener {
    private static BlockingQueue<JSONObject> commitQueue = new ArrayBlockingQueue<>(1024);
    public static void startListener() {
        Thread interactionLogQueueListener = new Thread( () -> {
            DatabaseInteraction database = new DatabaseInteraction();
            while(true){
                try {
                    JSONObject currentCmd = commitQueue.take();
                    String updateStateSql = "UPDATE TRANSCRIPT_QUEUE SET CHECKED_OUT = ? WHERE DOCUMENT_ID = ?";
                    PreparedStatement updateStateStmt = database.prepareStatement(updateStateSql);
                    try {
                        updateStateStmt.setString(2, currentCmd.getString("did"));
                        updateStateStmt.setBoolean(1, currentCmd.getBoolean("checkedout"));
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                    database.nonQuery(updateStateStmt);
                } catch (InterruptedException iEx) {
                    iEx.printStackTrace();
                }
            }
        });
        interactionLogQueueListener.start();
    }

    public static void enqueue(JSONObject log) throws InterruptedException {
        commitQueue.put(log);
    }
}
