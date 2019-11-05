package main.handlers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class TokenRequestHandler extends HandlerPrototype implements HttpHandler {
    public TokenRequestHandler() {
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
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            //Generate JWT token
            token = JWT.create().withIssuer("localhost:2020").sign(algorithm);
        } catch (UnsupportedEncodingException uEE) {
            uEE.printStackTrace();
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
