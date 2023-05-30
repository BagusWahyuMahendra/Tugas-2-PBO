import java.io.IOException;
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
            } else if (method.equals("PUT")) {
                if (path.matches("/users/\\d+")) {
                    handleUpdateUser(exchange);
                    return;
                }
            } else if (method.equals("DELETE") && path.matches("/users/\\d+")) {
                if (path.matches("/users/\\d+")) {
                    handleDeleteUser(exchange);
                    return;
                }
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
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT * FROM tb_users WHERE id_user = ?")) {

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
                    return;
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
            sendErrorResponse(exchange, 404, "User not found");
        }


        private void handleCreateUser(HttpExchange exchange) throws IOException {
//            if (!validateApiKey(exchange)) {
//                sendErrorResponse(exchange, 401, "Unauthorized");
//                return;
//            }

            String requestBody = Request.getRequestData(exchange);
            try {
                JSONObject userObject = new JSONObject(requestBody);
                String firstName = userObject.getString("first_name");
                String lastName = userObject.getString("last_name");
                String email = userObject.getString("email");
                String phoneNumber = userObject.getString("phone_number");
                String type = userObject.getString("type");
                int userId = userObject.getInt("id_user");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "INSERT INTO tb_users (first_name, last_name, email, phone_number, type, id_user) " +
                                     "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, phoneNumber);
                    statement.setString(5, type);
                    statement.setInt(6, userId);
                    statement.executeUpdate();

                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        sendErrorResponse(exchange, 500, "Failed to create user");
                        return;
                    }
                }

                // Create the response object
                JSONObject response = new JSONObject();
                response.put("message", "User created successfully");
                response.put("id_user", userId);

                sendResponse(exchange, 201, response.toString());
            } catch (JSONException e) {
                sendErrorResponse(exchange, 400, "Invalid request body");
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleUpdateUser(HttpExchange exchange) throws IOException {
            //            if (!validateApiKey(exchange)) {
            //                sendErrorResponse(exchange, 401, "Unauthorized");
            //                return;
            //            }

            String path = exchange.getRequestURI().getPath();
            int userId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            String requestBody = Request.getRequestData(exchange);
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

    static class ProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (path.equals("/products")) {
                    handleGetProducts(exchange);
                    return;
                } else if (path.matches("/products/\\d+")) {
                    handleGetProductById(exchange);
                    return;
                }
                 else if (path.matches("/products/users/\\d+")) {
                    handleGetProductByUser(exchange);
                    return;
            }
            } else if (method.equals("POST") && path.equals("/products")) {
                handleCreateProduct(exchange);
                return;
            } else if (method.equals("PUT") && path.matches("/products/\\d+")) {
                handleUpdateProduct(exchange);
                return;
            } else if (method.equals("DELETE") && path.matches("/products/\\d+")) {
                handleDeleteProduct(exchange);
                return;
            }

            sendErrorResponse(exchange, 404, "Not Found");
        }

        private void handleGetProducts(HttpExchange exchange) throws IOException {
            try (Connection connection = Database.connect();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM tb_products")) {

                JSONArray productsArray = new JSONArray();

                while (resultSet.next()) {
                    JSONObject productObject = new JSONObject();
                    productObject.put("id_product", resultSet.getInt("id_product"));
                    productObject.put("id_user", resultSet.getInt("id_user"));
                    productObject.put("title", resultSet.getString("title"));
                    productObject.put("description", resultSet.getString("description"));
                    productObject.put("price", resultSet.getDouble("price"));
                    productObject.put("stok", resultSet.getInt("stok"));

                    productsArray.put(productObject);
                }

                sendResponse(exchange, 200, productsArray.toString());
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetProductById(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int productId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tb_products WHERE id_product = ?")) {

                statement.setInt(1, productId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    JSONObject productObject = new JSONObject();
                    productObject.put("id_product", resultSet.getInt("id_product"));
                    productObject.put("id_user", resultSet.getInt("id_user"));
                    productObject.put("title", resultSet.getString("title"));
                    productObject.put("description", resultSet.getString("description"));
                    productObject.put("price", resultSet.getDouble("price"));
                    productObject.put("stok", resultSet.getInt("stok"));

                    sendResponse(exchange, 200, productObject.toString());
                } else {
                    sendErrorResponse(exchange, 404, "Product not found");
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }


        private void handleGetProductByUser(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int userId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tb_products WHERE id_user = ?")) {

                statement.setInt(1, userId);

                ResultSet resultSet = statement.executeQuery();
                JSONArray productsArray = new JSONArray();

                while (resultSet.next()) {
                    JSONObject productObject = new JSONObject();
                    productObject.put("id_product", resultSet.getInt("id_product"));
                    productObject.put("id_user", resultSet.getInt("id_user"));
                    productObject.put("title", resultSet.getString("title"));
                    productObject.put("description", resultSet.getString("description"));
                    productObject.put("price", resultSet.getDouble("price"));
                    productObject.put("stok", resultSet.getInt("stok"));

                    productsArray.put(productObject);
                }

                sendResponse(exchange, 200, productsArray.toString());
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }




        private void handleCreateProduct(HttpExchange exchange) throws IOException {
            String requestBody = Request.getRequestData(exchange);
            try {
                JSONObject productObject = new JSONObject(requestBody);
                int userId = productObject.getInt("id_user");
                String title = productObject.getString("title");
                String description = productObject.getString("description");
                double price = productObject.getDouble("price");
                int stok = productObject.getInt("stok");
                int productId = productObject.getInt("id_product");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "INSERT INTO tb_products (id_user, title, description, price, stok, id_product) " +
                                     "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, userId);
                    statement.setString(2, title);
                    statement.setString(3, description);
                    statement.setDouble(4, price);
                    statement.setInt(5, stok);
                    statement.setInt(6, productId);

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows > 0) {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            productId = generatedKeys.getInt(1);
                            JSONObject responseObj = new JSONObject();
                            responseObj.put("id_product", productId);
                            responseObj.put("message", "Product created successfully");
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

        private void handleUpdateProduct(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int productId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            String requestBody = Request.getRequestData(exchange);
            try {
                JSONObject productObject = new JSONObject(requestBody);
                int userId = productObject.getInt("id_user");
                String title = productObject.getString("title");
                String description = productObject.getString("description");
                double price = productObject.getDouble("price");
                int stok = productObject.getInt("stok");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "UPDATE tb_products SET id_user = ?, title = ?, description = ?, price = ?, stok = ? WHERE id_product = ?")) {

                    statement.setInt(1, userId);
                    statement.setString(2, title);
                    statement.setString(3, description);
                    statement.setDouble(4, price);
                    statement.setInt(5, stok);
                    statement.setInt(6, productId);

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows > 0) {
                        JSONObject responseObj = new JSONObject();
                        responseObj.put("message", "Product updated successfully");
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

        private void handleDeleteProduct(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int productId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM tb_products WHERE id_product = ?")) {

                statement.setInt(1, productId);

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("message", "Product deleted successfully");
                    sendResponse(exchange, 200, responseObj.toString());
                    return;
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
            }

            sendErrorResponse(exchange, 404, "Product not found");
        }
    }

    static class OrdersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (path.equals("/orders")) {
                    handleGetOrders(exchange);
                    return;
                } else if (path.matches("/orders/\\d+")) {
                    handleGetOrderById(exchange);
                    return;
                }
            } else if (method.equals("POST") && path.equals("/orders")) {
                handleCreateOrder(exchange);
                return;
            } else if (method.equals("PUT") && path.matches("/orders/\\d+")) {
                handleUpdateOrder(exchange);
                return;
            } else if (method.equals("DELETE") && path.matches("/orders/\\d+")) {
                handleDeleteOrder(exchange);
                return;
            }

            sendErrorResponse(exchange, 404, "Not Found");
        }

        private void handleGetOrders(HttpExchange exchange) throws IOException {
            try (Connection connection = Database.connect();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM tb_orders")) {

                JSONArray ordersArray = new JSONArray();

                while (resultSet.next()) {
                    JSONObject orderObject = new JSONObject();
                    orderObject.put("id_order", resultSet.getInt("id_order"));
                    orderObject.put("id_user", resultSet.getInt("id_user"));
                    orderObject.put("note", resultSet.getString("note"));
                    orderObject.put("total", resultSet.getDouble("total"));
                    orderObject.put("discount", resultSet.getDouble("discount"));
                    orderObject.put("is_paid", resultSet.getBoolean("is_paid"));

                    ordersArray.put(orderObject);
                }

                sendResponse(exchange, 200, ordersArray.toString());
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetOrderById(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int orderId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tb_orders WHERE id_order = ?")) {

                statement.setInt(1, orderId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    JSONObject orderObject = new JSONObject();
                    orderObject.put("id_order", resultSet.getInt("id_order"));
                    orderObject.put("id_user", resultSet.getInt("id_user"));
                    orderObject.put("note", resultSet.getString("note"));
                    orderObject.put("total", resultSet.getDouble("total"));
                    orderObject.put("discount", resultSet.getDouble("discount"));
                    orderObject.put("is_paid", resultSet.getBoolean("is_paid"));

                    sendResponse(exchange, 200, orderObject.toString());
                } else {
                    sendErrorResponse(exchange, 404, "Order not found");
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleCreateOrder(HttpExchange exchange) throws IOException {
            String requestBody = Request.getRequestData(exchange);
            try {
                JSONObject orderObject = new JSONObject(requestBody);
                int orderId = orderObject.getInt("id_order");
                int userId = orderObject.getInt("id_user");
                String note = orderObject.getString("note");
                double total = orderObject.getDouble("total");
                double discount = orderObject.getDouble("discount");
                boolean isPaid = orderObject.getBoolean("is_paid");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "INSERT INTO tb_orders (id_order ,id_user, note, total, discount, is_paid) " +
                                     "VALUES (? , ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, orderId);
                    statement.setInt(2, userId);
                    statement.setString(3, note);
                    statement.setDouble(4, total);
                    statement.setDouble(5, discount);
                    statement.setBoolean(6, isPaid);

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows > 0) {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
//                            orderId = generatedKeys.getInt(1);
                            JSONObject responseObj = new JSONObject();
                            responseObj.put("id_order", orderId);
                            responseObj.put("message", "Order created successfully");
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

        private void handleUpdateOrder(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int orderId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            String requestBody = Request.getRequestData(exchange);
            try {
                JSONObject orderObject = new JSONObject(requestBody);
                int userId = orderObject.getInt("id_user");
                String note = orderObject.getString("note");
                double total = orderObject.getDouble("total");
                double discount = orderObject.getDouble("discount");
                boolean isPaid = orderObject.getBoolean("is_paid");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "UPDATE tb_orders SET id_user = ?, note = ?, total = ?, discount = ?, is_paid = ? WHERE id_order = ?")) {

                    statement.setInt(1, userId);
                    statement.setString(2, note);
                    statement.setDouble(3, total);
                    statement.setDouble(4, discount);
                    statement.setBoolean(5, isPaid);
                    statement.setInt(6, orderId);

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows > 0) {
                        JSONObject responseObj = new JSONObject();
                        responseObj.put("message", "Order updated successfully");
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

        private void handleDeleteOrder(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int orderId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM tb_orders WHERE id_order = ?")) {

                statement.setInt(1, orderId);

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("message", "Order deleted successfully");
                    sendResponse(exchange, 200, responseObj.toString());
                    return;
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
            }

            sendErrorResponse(exchange, 404, "Order not found");
        }
    }

    static class AddressHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (path.equals("/addresses")) {
                    handleGetAddresses(exchange);
                    return;
                } else if (path.matches("/addresses/\\d+")) {
                    handleGetAddressById(exchange);
                    return;
                }
            } else if (method.equals("POST") && path.equals("/addresses")) {
                handleCreateAddress(exchange);
                return;
            } else if (method.equals("PUT") && path.matches("/addresses/\\d+")) {
                handleUpdateAddress(exchange);
                return;
            } else if (method.equals("DELETE") && path.matches("/addresses/\\d+")) {
                handleDeleteAddress(exchange);
                return;
            }

            sendErrorResponse(exchange, 404, "Not Found");
        }

        private void handleGetAddresses(HttpExchange exchange) throws IOException {
            try (Connection connection = Database.connect();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM tb_address")) {

                JSONArray addressesArray = new JSONArray();

                while (resultSet.next()) {
                    JSONObject addressObject = new JSONObject();
                    addressObject.put("id_user", resultSet.getInt("id_user"));
                    addressObject.put("type", resultSet.getString("type"));
                    addressObject.put("line1", resultSet.getString("line1"));
                    addressObject.put("line2", resultSet.getString("line2"));
                    addressObject.put("city", resultSet.getString("city"));
                    addressObject.put("province", resultSet.getString("province"));
                    addressObject.put("postcode", resultSet.getString("postcode"));

                    addressesArray.put(addressObject);
                }

                sendResponse(exchange, 200, addressesArray.toString());
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetAddressById(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int addressId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tb_address WHERE id_user = ?")) {

                statement.setInt(1, addressId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    JSONObject addressObject = new JSONObject();
                    addressObject.put("id_user", resultSet.getInt("id_user"));
                    addressObject.put("type", resultSet.getString("type"));
                    addressObject.put("line1", resultSet.getString("line1"));
                    addressObject.put("line2", resultSet.getString("line2"));
                    addressObject.put("city", resultSet.getString("city"));
                    addressObject.put("province", resultSet.getString("province"));
                    addressObject.put("postcode", resultSet.getString("postcode"));

                    sendResponse(exchange, 200, addressObject.toString());
                } else {
                    sendErrorResponse(exchange, 404, "Address not found");
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleCreateAddress(HttpExchange exchange) throws IOException {
            String requestBody = Request.getRequestData(exchange);
            try {
                JSONObject addressObject = new JSONObject(requestBody);
                int userId = addressObject.getInt("id_user");
                String type = addressObject.getString("type");
                String line1 = addressObject.getString("line1");
                String line2 = addressObject.getString("line2");
                String city = addressObject.getString("city");
                String province = addressObject.getString("province");
                String postcode = addressObject.getString("postcode");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "INSERT INTO tb_address (id_user, type, line1, line2, city, province, postcode) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, userId);
                    statement.setString(2, type);
                    statement.setString(3, line1);
                    statement.setString(4, line2);
                    statement.setString(5, city);
                    statement.setString(6, province);
                    statement.setString(7, postcode);

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows > 0) {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int addressId = generatedKeys.getInt(1);
                            JSONObject responseObj = new JSONObject();
                            responseObj.put("id_user", addressId);
                            responseObj.put("message", "Address created successfully");
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

        private void handleUpdateAddress(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int addressId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            String requestBody = Request.getRequestData(exchange);
            try {
                JSONObject addressObject = new JSONObject(requestBody);
                int userId = addressObject.getInt("id_user");
                String type = addressObject.getString("type");
                String line1 = addressObject.getString("line1");
                String line2 = addressObject.getString("line2");
                String city = addressObject.getString("city");
                String province = addressObject.getString("province");
                String postcode = addressObject.getString("postcode");

                try (Connection connection = Database.connect();
                     PreparedStatement statement = connection.prepareStatement(
                             "UPDATE tb_address SET id_user = ?, type = ?, line1 = ?, line2 = ?, city = ?, province = ?, postcode = ? WHERE id_user = ?")) {

                    statement.setInt(1, userId);
                    statement.setString(2, type);
                    statement.setString(3, line1);
                    statement.setString(4, line2);
                    statement.setString(5, city);
                    statement.setString(6, province);
                    statement.setString(7, postcode);
                    statement.setInt(8, addressId);

                    int affectedRows = statement.executeUpdate();
                    if (affectedRows > 0) {
                        JSONObject responseObj = new JSONObject();
                        responseObj.put("message", "Address updated successfully");
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

        private void handleDeleteAddress(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            int addressId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));

            try (Connection connection = Database.connect();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM tb_address WHERE id_user = ?")) {

                statement.setInt(1, addressId);

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("message", "Address deleted successfully");
                    sendResponse(exchange, 200, responseObj.toString());
                    return;
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
            }

            sendErrorResponse(exchange, 404, "Address not found");
        }
    }


}
