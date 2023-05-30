import java.io.IOException;
import java.io.OutputStream;
import io.github.cdimascio.dotenv.Dotenv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    private static final String API_KUNCI;

    static {
        Dotenv dotenv = Dotenv.configure().directory(".env").load();
        API_KUNCI = dotenv.get("API_KUNCI");
    }

    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("PUT")) {
                String apiKey = System.getenv(API_KUNCI);
                String requestApiKey = exchange.getRequestHeaders().getFirst("Authorization");

                if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
                    sendResponse(exchange, 401, "Unauthorized");
                    return;
                }
                sendResponse(exchange, 200, "Request authorized. Data added to database.");
            } else {
                sendResponse(exchange, 404, "Not Found");
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    static boolean validateApiKey(HttpExchange exchange) {
        String apiKey = exchange.getRequestHeaders().getFirst("api_kunci");
        return apiKey != null && apiKey.equals(API_KUNCI);
    }

}
