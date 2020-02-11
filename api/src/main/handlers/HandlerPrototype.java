package main.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import main.managers.LogManager;
import main.types.ErrorLog;
import main.types.ILoggable;
import main.types.Log;
import main.types.LogType;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Properties;

public abstract class HandlerPrototype implements ILoggable {
    protected String[] requiredKeys;
    protected String response;
    protected String handlerName;
    protected Log log;
    private LogManager logManager;

    HandlerPrototype(){
        log = new Log();
        logManager = new LogManager();
    }

    JSONObject getParameterObject(HttpExchange httpExchange) throws IOException {
        //Fetch the parameter text from the request
        InputStream paramInStream = httpExchange.getRequestBody();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] inBuffer = new byte[2048];
        int readBytes;
        //Read the parameter text in to the byte array and convert to string
        while ((readBytes = paramInStream.read(inBuffer)) != -1) {
            byteArrayOutputStream.write(inBuffer, 0, readBytes);
        }
        String jsonString = byteArrayOutputStream.toString();
        if(!jsonString.equals("")){
            return new JSONObject(jsonString);
        } else {
            return null;
        }
    }

    void displayRequestValidity(boolean isValidRequest){
        this.log.setRequestValidity(isValidRequest);
        //Display to server console ~ debugging TODO: remove for production
        System.out.println(isValidRequest ? "Valid Request" : "Invalid Request");
    }

    /**
     * Validate authentication token from request
     * @param token token string from request parameters
     * @return does this token match the API authentication code?
     */
    private boolean isTokenValid(String token){
        try {
            //Load authentication properties from auth file
            Properties verificationProps = new Properties();
            verificationProps.load(getClass().getResourceAsStream("auth.properties"));
            //Hash authentication key from auth file to SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(verificationProps.getProperty("verificationKey").getBytes(StandardCharsets.UTF_8));
            String serverToken = Base64.getEncoder().encodeToString(hash);
            //Compare hashed token to request parameter
            if(token.equals(serverToken)){
                this.log.setHashVerifyValidity(true);
                return true;
            } else {
                this.log.setHashVerifyValidity(false);
                return false;
            }
        } catch (Exception ex){
            ErrorLog errorLog = new ErrorLog(ex, "Fatal error in token validation");
            return false;
        }
    }

    /**
     * Determine request parameters and tokenization validity
     * @param requestParams JSON object containing the parameters sent with the request
     * @return does the request have parameters, do the have all the required parameters, and does it have a valid token?
     */
    protected boolean isRequestValid(JSONObject requestParams) {
        if (requestParams == null) {
            //Request did not come with parameters, is invalid
            this.log.setContent("Missing request params");
            this.log.setRequestValidity(false);
            this.log.setHashVerifyValidity(false);
            return false;
        }
        //Determine log validity and log
        boolean isRequestValid = !requestParams.has("token") || isTokenValid(requestParams.getString("token"));
        this.log.setRequestValidity(isRequestValid);
        if(!isRequestValid){
            this.log.setContent("Invalid token");
            this.log.setHashVerifyValidity(false);
        }
        for (String requiredKey : requiredKeys) {
            if (!requestParams.has(requiredKey)) {
                //Missing a required key, request is invalid
                this.log.setContent("Missing required key : " + requiredKey);
                this.log.setRequestValidity(false);
                return false;
            }
        }
        return isRequestValid;
    }

    /**
     * Entry point for handler. Get parameters, verify request validity, fulfill request, return response to client
     * @param httpExchange inherited from super class, set from client with params
     * @throws IOException thrown if there is an issue with writing response data to client
     */
    public void handle(HttpExchange httpExchange) throws IOException {
        //Create a new log for this handler transaction
        this.log = new Log();
        //Get parameters from client
        JSONObject requestParams = getParameterObject(httpExchange);
        //Determine validity of request parameters and validate token
        boolean isValidRequest = isRequestValid(requestParams);
        //Display in server console validity of the request for testing purposes
        displayRequestValidity(isValidRequest);
        if (isValidRequest) {
            //Request was valid, fulfill the request with params
            fulfillRequest(requestParams);
            this.log.setContent(this.response);
        } else {
            //Request was invalid, set response to reflect this
            this.response = "invalid request";
        }
        //Create response to client
        int responseCode = isValidRequest ? 200 : 400;
        Headers headers = httpExchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        httpExchange.sendResponseHeaders(responseCode, this.response.length());
        //Write response to the client
        OutputStream os = httpExchange.getResponseBody();
        os.write(this.response.getBytes());
        os.close();
        //Commit the log of this transaction to the database
        this.logManager.writeLog(this.log);
    }

    /**
     * Abstract fulfill request method fragment, forces handler override
     * @param requestParams parameters associated with the request
     */
    public abstract void fulfillRequest(JSONObject requestParams);

    /**
     * Getter for response property
     * @return response property
     */
    public String getResponse(){
        return this.response;
    }

    /**
     * The action was invalid in some way
     */
    protected void returnActionFailure(){ this.response = new JSONObject().put("success", false).toString(); }

    /**
     * The action was invalid and the exception that broke it has something that it wants to say
     * @param errorDesc error description
     */
    protected void returnActionFailure(String errorDesc){
        JSONObject returnObj = new JSONObject().put("success", false);
        returnObj.put("message", errorDesc);
        this.response = returnObj.toString();
    }

    /**
     * The action was successful, report back to the client
     */
    protected void returnActionSuccess(){ this.response = new JSONObject().put("success", true).toString(); }

    /**
     * The action was successful and returned a value, report back to the client
     * @param returnArgs return arguments for the return to client
     */
    protected void returnActionSuccess(JSONObject returnArgs){
        returnArgs.put("success", true);
        this.response = returnArgs.toString();
    }

    @Override
    public Log toLog(){
        return this.log;
    }
}
