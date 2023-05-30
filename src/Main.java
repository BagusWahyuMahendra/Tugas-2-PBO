import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        // Mengatur port untuk API, Port sesuai dengan 3 akhiran NIM
        int port = 8002;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        // Menginisialisasi server dengan port yang telah ditentukan
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Penanganan rute path dari pengguna, menampilkan semua entitas pada Database
        server.createContext("/users", new Response.UsersHandler());
        server.createContext("/products", new Response.ProductsHandler());
        server.createContext("/orders", new Response.OrdersHandler());
        server.createContext("/addresses", new Response.AddressHandler());

        server.setExecutor(null);
        server.createContext("/api/data", new Server.DataHandler());
        server.start();
        System.out.println("Listening on port: "+port);
    }
}
