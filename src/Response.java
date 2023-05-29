import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONPropertyName;
public class Response {
    static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (path.equals("/users")) {
                    handleGetUsers(exchange);
                    return;
                } else if (path.matches("/users/\\d+")) {
                    handleGetUserById(exchange);
                    return;
                }
            } else if (method.equals("POST") && path.equals("/users")) {
                handleCreateUser(exchange);
                return;
            } else if (method.equals("PUT") && path.matches("/users/\\d+")) {
                handleUpdateUser(exchange);
                return;
            } else if (method.equals("DELETE") && path.matches("/users/\\d+")) {
                handleDeleteUser(exchange);
                return;
            }

            sendErrorResponse(exchange, 404, "Not Found");
        }

        private void handleGetUsers(HttpExchange exchange) throws IOException {
            //            if (!validateApiKey(exchange)) {
            //                sendErrorResponse(exchange, 401, "Unauthorized");
            //                return;
            //            }

            try (Connection connection = Database.connect();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM tb_users")) {

                JSONArray usersArray = new JSONArray();

                while (resultSet.next()) {
                    JSONObject userObject = new JSONObject();
                    userObject.put("id_user", resultSet.getInt("id_user"));
                    userObject.put("first_name", resultSet.getString("first_name"));
                    userObject.put("last_name", resultSet.getString("last_name"));
                    userObject.put("email", resultSet.getString("email"));
                    userObject.put("phone_number", resultSet.getString("phone_number"));
                    userObject.put("type", resultSet.getString("type"));

                    usersArray.put(userObject);
                }

                sendResponse(exchange, 200, usersArray.toString());
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetUserById(HttpExchange exchange) throws IOException {
            //            if (!validateApiKey(exchange)) {
            //                sendErrorResponse(exchange, 401, "Unauthorized");
            //                return;
            //            }

            String path = exchange.getRequestURI().getPath();
            int userId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tb_users WHERE id_user = ?")) {

                statement.setInt(1, userId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    JSONObject userObject = new JSONObject();
                    userObject.put("id_user", resultSet.getInt("id_user"));
                    userObject.put("first_name", resultSet.getString("first_name"));
                    userObject.put("last_name", resultSet.getString("last_name"));
                    userObject.put("email", resultSet.getString("email"));
                    userObject.put("phone_number", resultSet.getString("phone_number"));
                    userObject.put("type", resultSet.getString("type"));

                    sendResponse(exchange, 200, userObject.toString());
                } else {
                    sendErrorResponse(exchange, 404, "User not found");
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleCreateUser(HttpExchange exchange) throws IOException {
            //            if (!validateApiKey(exchange)) {
            //                sendErrorResponse(exchange, 401, "Unauthorized");
            //                return;
            //            }

            String requestBody = getRequestData(exchange);
            try {
                JSONObject userObject = new JSONObject(requestBody);
                String firstName = userObject.getString("first_name");
                String lastName = userObject.getString("last_name");
                String email = userObject.getString("email");
                String phoneNumber = userObject.getString("phone_number");
                String type = userObject.getString("type");
                int id = userObject.getInt("id_user");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "INSERT INTO tb_users (first_name, last_name, email, phone_number, type, id_user) " +
                                     "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, phoneNumber);
                    statement.setString(5, type);
                    statement.setInt(6, id);

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows > 0) {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);
                            JSONObject responseObj = new JSONObject();
                            responseObj.put("id_user", userId);
                            responseObj.put("message", "User created successfully");
                            sendResponse(exchange, 201, responseObj.toString());
                            return;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sendErrorResponse(exchange, 400, "Bad Request");
        }

        private void handleUpdateUser(HttpExchange exchange) throws IOException {
            //            if (!validateApiKey(exchange)) {
            //                sendErrorResponse(exchange, 401, "Unauthorized");
            //                return;
            //            }

            String path = exchange.getRequestURI().getPath();
            int userId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            String requestBody = getRequestData(exchange);
            try {
                JSONObject userObject = new JSONObject(requestBody);
                String firstName = userObject.getString("first_name");
                String lastName = userObject.getString("last_name");
                String email = userObject.getString("email");
                String phoneNumber = userObject.getString("phone_number");
                String type = userObject.getString("type");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "UPDATE tb_users SET first_name = ?, last_name = ?, email = ?, phone_number = ?, type = ? WHERE id_user = ?")) {

                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, phoneNumber);
                    statement.setString(5, type);
                    statement.setInt(6, userId);

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows > 0) {
                        JSONObject responseObj = new JSONObject();
                        responseObj.put("message", "User updated successfully");
                        sendResponse(exchange, 200, responseObj.toString());
                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sendErrorResponse(exchange, 400, "Bad Request");
        }

        private void handleDeleteUser(HttpExchange exchange) throws IOException {
            //            if (!validateApiKey(exchange)) {
            //                sendErrorResponse(exchange, 401, "Unauthorized");
            //                return;
            //            }

            String path = exchange.getRequestURI().getPath();
            int userId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM tb_users WHERE id_user = ?")) {

                statement.setInt(1, userId);

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("message", "User deleted successfully");
                    sendResponse(exchange, 200, responseObj.toString());
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }

            sendErrorResponse(exchange, 404, "User not found");
        }
    }
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }


    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        JSONObject errorObject = new JSONObject();
        try {
            errorObject.put("error", message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        sendResponse(exchange, statusCode, errorObject.toString());
    }
    //    private static boolean validateApiKey(HttpExchange exchange) {
    //        String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
    //        return apiKey != null && apiKey.equals(API_KEY);
    //    }

    private static String getRequestData(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder requestData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestData.append(line);
        }

        return requestData.toString();
    }
}
