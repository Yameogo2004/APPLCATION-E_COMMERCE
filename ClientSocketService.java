package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocketService {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected = false;

    public ClientSocketService() {
    }

    public boolean connect() {
        try {
            if (connected && socket != null && !socket.isClosed()) {
                return true;
            }

            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String msg = in.readLine();
            connected = "CONNECTED_TO_SERVER".equals(msg);
            return connected;

        } catch (Exception e) {
            connected = false;
            return false;
        }
    }

    public String sendRequest(String request) {
        try {
            if (!connected || socket == null || socket.isClosed()) {
                if (!connect()) {
                    return "ERROR:SERVER_UNREACHABLE";
                }
            }

            out.println(request);
            String response = in.readLine();
            return response == null ? "ERROR:NO_RESPONSE" : response;

        } catch (Exception e) {
            connected = false;
            return "ERROR:COMMUNICATION";
        }
    }

    public String login(String email, String password) {
        return sendRequest("LOGIN:" + safe(email) + ":" + safe(password));
    }

    public String register(String nom, String prenom, String email, String password,
                           String address, String phone, String ville) {
        return sendRequest("REGISTER:" + safe(nom) + ":" + safe(prenom) + ":" + safe(email) + ":" +
                safe(password) + ":" + safe(address) + ":" + safe(phone) + ":" + safe(ville));
    }

    public String getProducts() {
        return sendRequest("GET_PRODUCTS");
    }

    public String getProduct(int productId) {
        return sendRequest("GET_PRODUCT:" + productId);
    }

    public String addToCart(int clientId, int productId, int quantity) {
        return sendRequest("CART_ADD:" + clientId + ":" + productId + ":" + quantity);
    }

    public String getCart(int clientId) {
        return sendRequest("CART_GET:" + clientId);
    }

    public String removeFromCart(int clientId, int productId) {
        return sendRequest("CART_REMOVE:" + clientId + ":" + productId);
    }

    // ✅ Nouveau pour le design de ton ami
    public String removeFromCartByName(int clientId, String productName) {
        return sendRequest("CART_REMOVE_BY_NAME:" + clientId + ":" + safe(productName));
    }

    public String clearCart(int clientId) {
        return sendRequest("CART_CLEAR:" + clientId);
    }

    public String checkout(int clientId) {
        return sendRequest("CHECKOUT:" + clientId);
    }

    public String pay(String uuid, String method) {
        return sendRequest("PAYMENT:" + safe(uuid) + ":" + safe(method));
    }

    public String makePayment(String uuid, String method) {
        return pay(uuid, method);
    }

    // =========================
    // ADMIN PRODUITS
    // =========================
    public String adminAddProduct(String name, String description, double price, int stock, String image, int categoryId) {
        return sendRequest("ADMIN_ADD_PRODUCT:" +
                safe(name) + ":" +
                safe(description) + ":" +
                price + ":" +
                stock + ":" +
                safe(image) + ":" +
                categoryId);
    }

    public String adminUpdateProduct(int productId, String name, String description, double price, int stock, String image, int categoryId) {
        return sendRequest("ADMIN_UPDATE_PRODUCT:" +
                productId + ":" +
                safe(name) + ":" +
                safe(description) + ":" +
                price + ":" +
                stock + ":" +
                safe(image) + ":" +
                categoryId);
    }

    public String adminDeleteProduct(int productId) {
        return sendRequest("ADMIN_DELETE_PRODUCT:" + productId);
    }

    // =========================
    // ADMIN CATEGORIES
    // =========================
    public String adminGetCategories() {
        return sendRequest("ADMIN_GET_CATEGORIES");
    }

    public String adminAddCategory(String name, String description) {
        return sendRequest("ADMIN_ADD_CATEGORY:" + safe(name) + ":" + safe(description));
    }

    public String adminUpdateCategory(int categoryId, String name, String description) {
        return sendRequest("ADMIN_UPDATE_CATEGORY:" + categoryId + ":" + safe(name) + ":" + safe(description));
    }

    public String adminDeleteCategory(int categoryId) {
        return sendRequest("ADMIN_DELETE_CATEGORY:" + categoryId);
    }

    // =========================
    // ADMIN USERS
    // =========================
    public String adminGetUsers() {
        return sendRequest("ADMIN_GET_USERS");
    }

    // =========================
    // ADMIN ORDERS
    // =========================
    public String adminGetOrders() {
        return sendRequest("ADMIN_GET_ORDERS");
    }

    public String adminUpdateOrderStatus(int orderId, String status) {
        return sendRequest("ADMIN_UPDATE_ORDER_STATUS:" + orderId + ":" + safe(status));
    }

    private String safe(String value) {
        if (value == null) return "";
        return value.replace(":", "-")
                .replace(";", ",")
                .replace("|", "/");
    }

    public void close() {
        try {
            connected = false;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception ignored) {
        }
    }
    public String getProfile(int userId) {
        return sendRequest("GET_PROFILE:" + userId);
    }

    public String updateProfile(int userId, String fullName, String email, String phone, String address, String city) {
        return sendRequest("UPDATE_PROFILE:" +
                userId + ":" +
                safe(fullName) + ":" +
                safe(email) + ":" +
                safe(phone) + ":" +
                safe(address) + ":" +
                safe(city));
    }
}