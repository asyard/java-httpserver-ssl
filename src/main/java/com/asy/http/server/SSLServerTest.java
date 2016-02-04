package com.asy.http.server;

import com.asy.http.server.core.BasicHttpsServer;

/**
 * Created by asy
 */
public class SSLServerTest {

    private static int serverPort = 8085;
    private static int sslServerPort = 8090;

    public static String keystoreLocation = "C:\\cert\\basichttpserver.keystore";
    public static String keystorePassword = "123456";


    public static void main(String[] args) throws Exception {
        new BasicHttpsServer(serverPort, "", sslServerPort, "secure");
    }

}
