package main.listeners;

import main.managers.OcrApiManager;
import main.managers.TranscriptManager;
import main.types.Transcript;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class OCRQueueListener {
    private static BlockingQueue<Transcript> transcriptQueue = new ArrayBlockingQueue<>(1024);
    public static void startListener() {
        Thread transcriptQueueListenerThread = new Thread(() -> {
            TranscriptManager transcriptManager = new TranscriptManager();
            OcrApiManager ocrApiManager = new OcrApiManager();
            while(true){
                try {
                    Transcript transcript = transcriptQueue.take();
                    String resultString = ocrApiManager.doOcr(transcript.getImageString());
                    transcriptManager.putResults(resultString, transcript);
                } catch (InterruptedException | IOException | SQLException iEx) {
                    iEx.printStackTrace();
                }
            }
        });
        transcriptQueueListenerThread.start();
    }

    public static void enqueue(Transcript transcript) {
        try {
            transcriptQueue.put(transcript);
        } catch (InterruptedException iEx) {
            iEx.printStackTrace();
        }
    }
}
