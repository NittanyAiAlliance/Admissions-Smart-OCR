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

    @Override
    public Log toLog(){
        return new Log();
    }
}
