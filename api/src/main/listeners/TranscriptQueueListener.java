package main.listeners;

import main.managers.ExternalDataManager;
import main.managers.OcrApiManager;
import main.managers.TranscriptManager;
import main.types.Transcript;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TranscriptQueueListener {
    private static BlockingQueue<JSONObject> transcriptQueue = new ArrayBlockingQueue<>(1024);
    public static void startListener(){
        Thread transcriptQueueListenerThread = new Thread(() -> {
            TranscriptManager transcriptManager = new TranscriptManager();
            OcrApiManager ocrApiManager = new OcrApiManager();
            ExternalDataManager externalDataManager = new ExternalDataManager();
            while(true){
                try {
                    //Get the enqueued transcript
                    JSONObject transcript = transcriptQueue.take();
                    //Request the image for this transcript from external data
                    String imageString = externalDataManager.requestTranscriptImage(transcript.getString("DOCUMENT_ID"));
                    //Create final transcript object with assembled pieces
                    Transcript thisTranscript = new Transcript(transcript.getString("DOCUMENT_ID"), transcript, imageString);
                    //Insert this transcript into the database now that we have all of the pieces
                    transcriptManager.putTranscript(thisTranscript);
                    OCRQueueListener.enqueue(thisTranscript);
                } catch (InterruptedException | SQLException iEx) {
                    iEx.printStackTrace();
                }
            }
        });
        transcriptQueueListenerThread.start();
    }

    /**
     * Put a transcript id in the blocking queue to wait for the listener thread
     * @param transcript transcript identifier for this listener to fetch when its time
     */
    public static void enqueue(JSONObject transcript){
        try {
            transcriptQueue.put(transcript);
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        }
    }
}