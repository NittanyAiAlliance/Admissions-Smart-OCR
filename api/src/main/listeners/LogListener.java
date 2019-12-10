package main.listeners;

import main.managers.LogManager;
import main.types.ErrorLog;
import main.types.Log;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LogListener {
    private static BlockingQueue<Log> commitQueue = new ArrayBlockingQueue<>(1024);
    public static void startListener() {
        Thread logQueueListener = new Thread( () -> {
            LogManager logManager = new LogManager();
            while(true){
                try {
                    Log thisLog = commitQueue.take();

                } catch (InterruptedException iEx) {
                    iEx.printStackTrace();
                    logManager.writeErrorLog(new ErrorLog(iEx, "API log listener interrupted"));
                }
            }
        });
    }
}
