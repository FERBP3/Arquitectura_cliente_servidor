/**
* Computación distribuida
* José Fernando Brigido Pablo
*/

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class Test {
    public static void main(String[] args) {
        try{
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/api", new Handler());
            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("Servidor iniciado en localhost:8000");
        } catch(Exception e){
            System.out.println(e);
        }

    }

}
