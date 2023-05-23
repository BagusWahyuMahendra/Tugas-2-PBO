
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.URI;


public class Server {
    private HttpServer server;

    public Server(int port) throws Exception{
        server = HttpServer.create(new InetSocketAddress(port),128 );
        HttpContext context = server.createContext("/", new RequestHandler());
        server.start();
    }

    private class RequestHandler implements HttpHandler {
        public void handle (HttpExchange httpExchange) throws IOException{
            PrintStream out = new PrintStream( (httpExchange.getResponseBody()));
            handle(httpExchange, out);
        }
    }

    private void handle(HttpExchange httpExchange, PrintStream out){
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();
        System.out.println("path : %s\n", path);
        Server.processHttpExchange(httpExchange);
    }

    public static void processHttpExchange(HttpExchange httpExchange){
        Request req = new Request(httpExchange);
        System.out.println(req.getBody());

    }

}
