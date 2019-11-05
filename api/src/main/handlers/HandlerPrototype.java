package main.handlers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import java.io.*;

public abstract class HandlerPrototype {
    protected String[] requiredKeys;
    protected String response;
    protected String handlerName;

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
        if(isValidRequest){
            System.out.println("Valid Request");
        } else {
            System.out.println("Invalid Request");
        }
    }

    private boolean isTokenValid(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("localhost:2020")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("Token " + token + " was verified");
            return true;
        } catch (UnsupportedEncodingException useEx){
            return false;
        }
    }

    protected boolean isRequestValid(JSONObject requestParams) {
        if (requestParams == null) {
            //Request did not come with parameters, is invalid
            System.out.println("Request Params Null");
            return false;
        }
        for (String requiredKey : requiredKeys) {
            if (!requestParams.has(requiredKey)) {
                //Missing a required key, request is invalid
                System.out.println("Request Params Missing Key " + requiredKey);
                return false;
            }
        }
        return !requestParams.has("token") || isTokenValid(requestParams.getString("token"));
    }

    /**
     * Entry point for handler. Get parameters, verify request validity, fulfill request, return response to client
     * @param httpExchange inherited from super class, set from client with params
     * @throws IOException thrown if there is an issue with writing response data to client
     */
    public void handle(HttpExchange httpExchange) throws IOException {
        //Get parameters from client
        JSONObject requestParams = getParameterObject(httpExchange);
        //Determine validity of request parameters and validate token
        boolean isValidRequest = isRequestValid(requestParams);
        //Display in server console validity of the request for testing purposes
        displayRequestValidity(isValidRequest);
        if (isValidRequest) {
            //Request was valid, fulfill the request with params
            fulfillRequest(requestParams);
        } else {
            //Request was invalid, set response to reflect this
            this.response = "invalid request";
        }
        //Create response to client
        int responseCode = isValidRequest ? 200 : 400;
        Headers headers = httpExchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        httpExchange.sendResponseHeaders(responseCode, this.response.length());
        System.out.println("Response to " + handlerName + ": " + this.response);
        //Write response to the client
        OutputStream os = httpExchange.getResponseBody();
        os.write(this.response.getBytes());
        os.close();
    }

    public abstract void fulfillRequest(JSONObject requestParams);

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
}
