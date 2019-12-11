package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.types.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Random;

public class FilePostHandler extends HandlerPrototype implements HttpHandler {
    public FilePostHandler(){
        super.handlerName = "File Post Handler";
        super.requiredKeys = new String[] { "file", "token" };
    }

    @Override
    public void fulfillRequest(JSONObject requestParams) {
        //Get image file string from request parameters
        String imgBlobString = requestParams.getString("file");
        //Write image data to file
        String dataFileName = writeImageToFile(imgBlobString);
        String scriptPath = System.getProperty("user.home");
        //Create invoke command for python script with image encoding as command line arg
        //ProcessBuilder pb = new ProcessBuilder("python", scriptPath + "/img-ocr.py", dataFileName);
        try {
            //Invoke python script
            //Process p = pb.start();
            //BufferedReader pyIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder csvString = new StringBuilder();
            String csvLine = "";
            //Read script return
            //p.waitFor();
            //while((csvLine = pyIn.readLine()) != null) {
                System.out.println(csvLine);
                csvString.append(csvLine);
            //}
            //TODO: remove before commit
            JSONObject csvStrObj = new JSONObject();
            JSONArray fieldArray = new JSONArray();
            fieldArray.put("Class Name");
            fieldArray.put("Year");
            fieldArray.put("Final Grade");
            fieldArray.put("Credits");
            csvStrObj.put("fieldArray", fieldArray);
            JSONArray classRecordArray = new JSONArray();
            JSONObject classRecord = new JSONObject();
            classRecord.put("classname", "Math");
            classRecord.put("year", 1998);
            classRecord.put("finalgrade", "A+");
            classRecord.put("credits", 2);
            classRecordArray.put(classRecord);
            JSONObject class2Record = new JSONObject();
            class2Record.put("classname", "Art");
            class2Record.put("year", 1998);
            class2Record.put("finalgrade", "B");
            class2Record.put("credits", 4);
            classRecordArray.put(class2Record);
            csvStrObj.put("classRecords", classRecordArray);
            returnActionSuccess(csvStrObj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final String uploadTempFilePath = System.getProperty("user.home") + "/upload-temp";

    /**
     * Write the image base 64 string to a temp file
     * @param imgStr image base 64 string
     * @return name of the file the image data was written into
     */
    private String writeImageToFile(String imgStr){
        //Get the image base 64 encoding and convert to image byte array
        byte[] imgData = Base64.getDecoder().decode(imgStr.split(",")[1]);
        String dataFile = uploadTempFilePath + "/" + getTempFileRandomName();
        Path tempFile = Paths.get(dataFile);
        try {
            Files.createFile(tempFile);
            System.out.println("Creating file: " + dataFile);
        } catch (FileAlreadyExistsException faeEx){
            //In the extremely unlikely event of a random file name collision, make another name
            //This will happen 1 out of every 4.76 * 10^28 iterations
            dataFile = uploadTempFilePath + "/" + getTempFileRandomName();
            tempFile = Paths.get(dataFile);
        } catch (IOException ioEx){
            ioEx.printStackTrace();
        }
        try {
            //Write image data to a randomly named temporary file
            Files.write(tempFile, imgData);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
        return dataFile;
    }

    /**
     * Get a random name for the temporary file
     * @return random name for temp file
     */
    private String getTempFileRandomName() {
        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 16) {
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        return salt.toString() + ".bin";
    }

    @Override
    public Log toLog(){
        return new Log();
    }
}
