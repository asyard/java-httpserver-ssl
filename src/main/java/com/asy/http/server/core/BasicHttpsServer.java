package com.asy.http.server.core;

import com.asy.http.server.SSLServerTest;
import com.sun.net.httpserver.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.logging.Logger;

/**
 * Created by asy
 */
public class BasicHttpsServer {

    private static final Logger logger = Logger.getLogger(BasicHttpsServer.class.getName());

    public BasicHttpsServer(int port, String contextPath, int sslPort, String sslContextPath) throws Exception {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/" + contextPath, new BasicHttpServerHandler());
        httpServer.setExecutor(null);
        httpServer.start();
        logger.info("Server started and listening port "+ port);

        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(sslPort), 0);
        httpsServer.createContext("/" + sslContextPath, new TLSBasicHttpServerHandler());
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(createSSLContext()));
        httpsServer.setExecutor(null);
        httpsServer.start();
        logger.info("Server (via TLS) started and listening port "+ sslPort);
    }

    private SSLContext createSSLContext() throws Exception {
        // initialize the keystore
        char[] password = SSLServerTest.keystorePassword.toCharArray();
        KeyStore ks = KeyStore.getInstance ("JKS");
        //InputStream is = BasicHttpsServer.class.getResourceAsStream("basichttpserver.keystore");
        FileInputStream fis = new FileInputStream(SSLServerTest.keystoreLocation);
        ks.load(fis, password);

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init (ks, password);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init (ks);

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance ("TLS");
        sslContext.init (kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext;
    }

    class BasicHttpServerHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "This is the response";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    class TLSBasicHttpServerHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "This is the secure response";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
