package main.listeners;

import main.managers.OcrApiManager;
import main.managers.TranscriptManager;
import main.types.Transcript;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TranscriptQueueListener {
    private static BlockingQueue<String> transcriptQueue = new ArrayBlockingQueue<>(1024);
    public static void startListener(){
        Thread transcriptQueueListenerThread = new Thread(() -> {
            TranscriptManager transcriptManager = new TranscriptManager();
            OcrApiManager ocrApiManager = new OcrApiManager();
            while(true){
                try {
                    String transcriptId = transcriptQueue.take();
                    Transcript transcript = transcriptManager.fetchById(transcriptId);
                    String ocrResponse = null;
                    try {
                        ocrResponse = ocrApiManager.doOcr(transcript.getFile());
                    } catch(IOException ioEx){
                        ioEx.printStackTrace();
                    }
                    JSONObject ocrResponseObject = ocrApiManager.formatResponse(ocrResponse);
                    try {
                        transcriptManager.putResults(ocrResponseObject, transcript);
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                } catch (InterruptedException | SQLException iEx) {
                    iEx.printStackTrace();
                }
            }
        });
        transcriptQueueListenerThread.start();
    }

    /**
     * Put a transcript id in the blocking queue to wait for the listener thread
     * @param transcriptId transcript identifier for this listener to fetch when its time
     */
    public static void enqueue(String transcriptId){
        try {
            transcriptQueue.put(transcriptId);
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        }
    }
}