package test;

import main.handlers.TokenRequestHandler;
import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

public class _Handlers {
    private static final String test_AWTString = "BW3n+Wyt7N1/5ijHQGsDXtVH2Xut36jNVqpFaTqfzQ8=";
    @Test
    public void requestToken(){
        System.out.println("Testing token request");
        TokenRequestHandler tokenRequestHandler = new TokenRequestHandler();
        tokenRequestHandler.fulfillRequest(new JSONObject());
        assertNotEquals("", tokenRequestHandler.getResponse());
        JSONObject requestResponse = new JSONObject(tokenRequestHandler.getResponse());
        assertTrue(requestResponse.getBoolean("success"));
        assertEquals(test_AWTString, requestResponse.getString("token"));
    }
}
