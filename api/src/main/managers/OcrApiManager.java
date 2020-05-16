package main.managers;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Manager for interactions between the server and OCR API
 * @author Joel Seidel
 */
public class OcrApiManager {

    //Constant URL for OCR API
    private final String internalUrl = "http://localhost:5000";

    public OcrApiManager() { }

    /**
     * Make a connection to and perform OCR request with a file on the OCR API
     * @param fileStr string representation of the file to be posted to the OCR API
     * @return String response from the OCR API ~ that is ~ the result of the OCR operation
     * @throws IOException thrown if there is a critical error in communication between this server and the OCR API
     */
    public String doOcr(String fileStr) throws IOException {
        //Create the request object and send to the OCR API
        HttpURLConnection ocrConn = sendOcrRequest(fileStr);
        //Does the request code tell us everything is good?
        int responseCode = ocrConn.getResponseCode();
        if(responseCode != 200){
            //There was an error with making the OCR request
            throw new IOException();
        }
        //Read the response to the OCR request and return the result
        return readOcrResponse(ocrConn);
    }

    /**
     * Create the HTTP Connection object between this server and the OCR API
     * @return HTTPConnection object pointed at the OCR server
     * @throws IOException If there is an issue with the connection
     */
    private HttpURLConnection createOCR_APIConnection() throws IOException {
        //Create the HTTP Connection object
        HttpURLConnection httpClient = (HttpURLConnection)new URL(internalUrl).openConnection();
        //Set required parameters of the request object
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setDoOutput(true);
        return httpClient;
    }

    /**
     * Write the parameters to the OCR API ~ that is ~ write the output stream of parameters
     * @param fileStr string representation of the file being sent to the OCR API
     * @return Updated HttpURLConnection awaiting the results of the OCR process
     * @throws IOException thrown if there is a critical error in communication between this server and the OCR API
     */
    private HttpURLConnection sendOcrRequest(String fileStr) throws IOException {
        //Create connection to the OCR API
        HttpURLConnection ocrConn = createOCR_APIConnection();
        //Attempt to write the parameters of the request to the connection output stream
        try (OutputStream os = ocrConn.getOutputStream()){
            //Create argument wrapper JSON object and add the file parameter
            JSONObject ocrRequestArgs = new JSONObject().put("file", fileStr);
            //Write to the connection output stream
            os.write(ocrRequestArgs.toString().getBytes());
            os.flush();
        }
        return ocrConn;
    }

    /**
     * Read the result of the OCR request ~ that is ~ the result of the OCR process
     * @param ocrConn connection with the OCR API
     * @return Response string from the OCR API
     * @throws IOException thrown if there is a critical error in reading the input stream from the connection to the OCR API
     */
    private String readOcrResponse(HttpURLConnection ocrConn) throws IOException {
        //Attempt to read the result of the OCR request from the connection input stream
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ocrConn.getInputStream()))){
            //Build the string response from the lines of the OCR response
            StringBuilder ocrResponse = new StringBuilder();
            String ocrResponseLine;
            while((ocrResponseLine = in.readLine()) != null) {
                ocrResponse.append(ocrResponseLine);
            }
            return ocrResponse.toString();
        }
    }
}
