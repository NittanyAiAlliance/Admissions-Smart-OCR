package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import main.managers.LogManager;
import main.types.ErrorLog;
import main.types.Log;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

public class TokenRequestHandler extends HandlerPrototype implements HttpHandler {
    public TokenRequestHandler() {
        super();
        super.handlerName = "Token Request Handler";
    }
    @Override
    public void fulfillRequest(JSONObject requestParams) {
        //All token requests will succeed - return generated token
        returnActionSuccess(new JSONObject().put("token", createToken()));
    }

    /**
     * Generate token for client who has requested one
     * @return token string
     */
    private String createToken(){
        String token = "";
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            Properties verificationDigestProperties = new Properties();
            verificationDigestProperties.load(getClass().getResourceAsStream("auth.properties"));
            byte[] hash = digest.digest(verificationDigestProperties.getProperty("verificationKey").getBytes(StandardCharsets.UTF_8));
            token = Base64.getEncoder().encodeToString(hash);
            this.log.addContent("Hash verification token succeeded");
        } catch (NoSuchAlgorithmException | IOException nsaEx){
            returnActionFailure();
            ErrorLog errorLog = new ErrorLog(nsaEx, "Hash verification token failed");
            LogManager.writeErrorLog(errorLog);
            this.log.addContent(errorLog.toString());
        }
        return token;
    }
    /**
     * Overriden request validation method. Requests for tokens are always valid
     * @param requestParams This will be null as there are no request parameters
     * @return true - always.
     */
    @Override
    protected boolean isRequestValid(JSONObject requestParams) {
        return true;
    }
}
