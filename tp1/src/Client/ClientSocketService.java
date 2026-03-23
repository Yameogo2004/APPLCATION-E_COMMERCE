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
                if (!connect()) return "ERROR:SERVER_UNREACHABLE";
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
        return sendRequest("LOGIN:" + email + ":" + password);
    }

    public String register(String nom, String prenom, String email, String password,
                           String address, String phone, String ville) {
        return sendRequest("REGISTER:" + nom + ":" + prenom + ":" + email + ":" +
                password + ":" + address + ":" + phone + ":" + ville);
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

    public String clearCart(int clientId) {
        return sendRequest("CART_CLEAR:" + clientId);
    }

    public String checkout(int clientId) {
        return sendRequest("CHECKOUT:" + clientId);
    }

    public String pay(String uuid, String method) {
        return sendRequest("PAYMENT:" + uuid + ":" + method);
    }
    
    public String sendOtp(String email) {
        return sendRequest("SEND_OTP:" + email);
    }

    public String verifyOtp(String email, String code) {
        return sendRequest("VERIFY_OTP:" + email + ":" + code);
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
}