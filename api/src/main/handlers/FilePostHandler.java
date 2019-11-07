package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        ProcessBuilder pb = new ProcessBuilder("python", scriptPath + "/csv.py", dataFileName);
        try {
            //Invoke python script
            Process p = pb.start();
            BufferedReader pyIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder csvString = new StringBuilder();
            String csvLine = null;
            //Read script return
            while((csvLine = pyIn.readLine()) != null) {
                csvString.append(csvLine);
            }
            JSONObject csvStrObj = new JSONObject();
            csvStrObj.put("csvStr", csvString.toString());
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
        byte imgData[]  = imgStr.getBytes();
        String dataFile = uploadTempFilePath + "/" + getTempFileRandomName();
        Path tempFile = Paths.get(dataFile);
        try {

            Files.createFile(tempFile);
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
}
