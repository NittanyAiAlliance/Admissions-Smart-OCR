package main.handlers;

import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class FilePostHandler extends HandlerPrototype implements HttpHandler {
    public FilePostHandler(){
        super.handlerName = "File Post Handler";
        super.requiredKeys = new String[] { "file", "token" };
    }

    @Override
    public void fulfillRequest(JSONObject requestParams) {
        //Get image file string from
        String imgBlobString = requestParams.getString("file");
        String scriptPath = System.getProperty("user.home");
        ProcessBuilder pb = new ProcessBuilder("python", scriptPath + "/csv.py", imgBlobString);
        try {
            Process p = pb.start();
            BufferedReader pyIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String pyReturn = pyIn.readLine();
            System.out.println(pyReturn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
