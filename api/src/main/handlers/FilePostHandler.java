package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.types.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
        String imgStr = requestParams.getString("file");
        String urlWithParams = "http://localhost:5000?img_str=" + imgStr;
        //Create OCR API request
        try{
            String ocrCsv = getOCRRequest(urlWithParams);
            System.out.println(ocrCsv);
        } catch (IOException ioEx){
            ioEx.printStackTrace();
        }

    }

    private String getOCRRequest(String reqUrl) throws IOException{
        //Create connection to local OCR api
        HttpURLConnection httpClient = (HttpURLConnection) new URL(reqUrl).openConnection();
        httpClient.setRequestMethod("GET");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = httpClient.getResponseCode();
        if(responseCode != 200){
            System.out.println("Could not connect to internal API");
        }
        //Read request response ~ expecting OCR csv
        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))){
            StringBuilder response = new StringBuilder();
            String line;
            while((line = in.readLine()) != null){
                response.append(line);
            }
            return response.toString();
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
