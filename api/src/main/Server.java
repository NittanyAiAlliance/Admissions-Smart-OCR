package main;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import main.handlers.*;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class Server {
    private static final int API_PORT = 2020;
    public static void main(String args[]){
        System.out.println("Begin server start");
        try {
            //Start server
            initServer();
            System.out.println("Completed server start - SUCCESS");
            System.out.println("Server listening on port: " + API_PORT);
        } catch (Exception ex){
            //Server start failed
            System.out.println("Completed server start - FAILURE");
            ex.printStackTrace();
        }
    }

    /**
     * Configure and launch HTTPS server
     * @throws Exception exception thrown during server start
     */
    private static void initServer() throws Exception {
        //Create server and address
        HttpsServer server = HttpsServer.create(new InetSocketAddress(API_PORT), 0);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        //Initialize keystore
        char[] password = "password".toCharArray();
        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream inputStream = Server.class.getClassLoader().getResourceAsStream("main/data/testkey.jks");
        keyStore.load(inputStream, password);
        //Create key manager factory and init
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, password);
        //Create trust manager factory and init
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        //Create HTTPS context and parameters
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext){
            public void configure(HttpsParameters params){
                try{
                    //Init SSL context
                    SSLContext context = SSLContext.getDefault();
                    SSLEngine engine = context.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());
                    //Fetch default parameters
                    SSLParameters defaultSSLParams = context.getDefaultSSLParameters();
                    params.setSSLParameters(defaultSSLParams);
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        server = createHandlerContexts(server);
        server.setExecutor(null);
        server.start();
    }

    /**
     * Create endpoint contexts and configure handler class routing
     * @param server server object without endpoint configuration
     * @return server object with endpoint configuration
     */
    private static HttpsServer createHandlerContexts(HttpsServer server){
        server.createContext("/token/get", new TokenRequestHandler());
        server.createContext("/file/post", new FilePostHandler());
        server.createContext("/submission/post", new SubmissionHandler());
        server.createContext("/submission/get", new GetSubmissionHandler());
        server.createContext("/queue/fetch", new GetTranscriptQueueHandler());
        server.createContext("/queue/fetch/one", new GetQueuedTranscriptHandler());
        return server;
    }
}
