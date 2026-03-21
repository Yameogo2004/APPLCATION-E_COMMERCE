package server;

import java.io.*;
import java.net.Socket;
import java.util.List;

import model.Cart;
import model.CartItem;
import model.Product;
import model.Order;
import model.Payment;
import model.User;

import service.CartService;
import service.ProductService;
import service.OrderService;
import service.PaymentService;
import service.AuthService;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private CartService cartService;
    private ProductService productService;
    private OrderService orderService;
    private PaymentService paymentService;
    private AuthService authService;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.cartService = new CartService();
        this.productService = new ProductService();
        this.paymentService = new PaymentService();
        this.authService = new AuthService();

        try {
            this.orderService = new OrderService();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Impossible d'initialiser OrderService");
            this.orderService = null;
        }

        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Erreur initialisation ClientHandler : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            out.println("CONNECTED_TO_SERVER");

            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Requête reçue : " + request);
                handleRequest(request);
            }

        } catch (IOException e) {
            System.out.println("Client déconnecté : " + clientSocket.getInetAddress());
        } finally {
            closeResources();
        }
    }

    private void handleRequest(String request) {
        try {
            if (request == null || request.trim().isEmpty()) {
                out.println("ERROR:EMPTY_REQUEST");
                return;
            }

            // Ping test
            if (request.equalsIgnoreCase("PING")) { out.println("PONG"); return; }

            // ===== PANIER =====
            if (request.startsWith("CART_ADD:")) { out.println(handleCartAdd(request)); return; }
            if (request.startsWith("CART_REMOVE:")) { out.println(handleCartRemove(request)); return; }
            if (request.startsWith("CART_GET:")) { out.println(handleCartGet(request)); return; }

            // ===== PRODUITS =====
            if (request.equalsIgnoreCase("GET_PRODUCTS")) { out.println(handleGetProducts()); return; }
            if (request.startsWith("GET_PRODUCT:")) { out.println(handleGetProduct(request)); return; }

            // ===== CHECKOUT =====
            if (request.startsWith("CHECKOUT:")) { out.println(handleCheckout(request)); return; }

            // ===== PAYMENT =====
            if (request.startsWith("PAYMENT:")) { out.println(handlePayment(request)); return; }

            // ===== AUTHENTIFICATION =====
            if (request.startsWith("LOGIN:")) { out.println(handleLogin(request)); return; }
            if (request.startsWith("REGISTER:")) { out.println(handleRegister(request)); return; }

            out.println("ERROR:UNKNOWN_COMMAND");

        } catch (Exception e) {
            out.println("ERROR:EXCEPTION_OCCURRED");
            e.printStackTrace();
        }
    }

    // ── HANDLERS PANIER ─────────────────────────────
    private String handleCartAdd(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 4) return "ERROR:CART_ADD_FORMAT";

            int clientId = Integer.parseInt(parts[1]);
            int productId = Integer.parseInt(parts[2]);
            int quantity = Integer.parseInt(parts[3]);

            if (quantity <= 0) return "ERROR:INVALID_QUANTITY";

            Product product = productService.getProductById(productId);
            if (product == null) return "ERROR:PRODUCT_NOT_FOUND";
            if (product.getStock() < quantity) return "ERROR:INSUFFICIENT_STOCK";

            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);

            boolean added = cartService.addItemToCart(clientId, item);
            return added ? "CART_ADD_SUCCESS" : "ERROR:CART_ADD_FAILED";

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (Exception e) {
            return "ERROR:CART_ADD_EXCEPTION";
        }
    }

    private String handleCartRemove(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 3) return "ERROR:CART_REMOVE_FORMAT";

            int clientId = Integer.parseInt(parts[1]);
            int productId = Integer.parseInt(parts[2]);

            boolean removed = cartService.removeItemFromCart(clientId, productId);
            return removed ? "CART_REMOVE_SUCCESS" : "ERROR:CART_REMOVE_FAILED";

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (Exception e) {
            return "ERROR:CART_REMOVE_EXCEPTION";
        }
    }

    private String handleCartGet(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 2) return "ERROR:CART_GET_FORMAT";

            int clientId = Integer.parseInt(parts[1]);
            Cart cart = cartService.getCartByClient(clientId);
            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) return "CART_EMPTY";

            StringBuilder response = new StringBuilder();
            response.append("CART_DETAILS")
                    .append("|CartID=").append(cart.getId())
                    .append("|Items=").append(cart.getItems().size())
                    .append("|Total=").append(cart.calculateTotal());

            for (CartItem item : cart.getItems()) {
                if (item.getProduct() != null) {
                    response.append("|Product=").append(item.getProduct().getName())
                            .append(",Qty=").append(item.getQuantity())
                            .append(",Subtotal=").append(item.calculateSubtotal());
                } else {
                    response.append("|Product=UNKNOWN,Qty=").append(item.getQuantity())
                            .append(",Subtotal=0.0");
                }
            }

            return response.toString();

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (Exception e) {
            return "ERROR:CART_GET_EXCEPTION";
        }
    }

    // ── HANDLERS PRODUITS ─────────────────────────────
    private String handleGetProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            StringBuilder sb = new StringBuilder();
            for (Product p : products) {
                sb.append(p.getIdProduct()).append(";")
                  .append(p.getName()).append(";")
                  .append(p.getPrice()).append("|");
            }
            return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "NO_PRODUCTS";
        } catch (Exception e) {
            return "ERROR:GET_PRODUCTS_FAILED";
        }
    }

    private String handleGetProduct(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 2) return "ERROR:GET_PRODUCT_FORMAT";

            int productId = Integer.parseInt(parts[1]);
            Product p = productService.getProductById(productId);
            if (p == null) return "ERROR:PRODUCT_NOT_FOUND";

            return p.getIdProduct() + ";" + p.getName() + ";" + p.getPrice() + ";" + p.getDescription() + ";" + p.getStock();

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (Exception e) {
            return "ERROR:GET_PRODUCT_EXCEPTION";
        }
    }

    // ── HANDLER CHECKOUT ─────────────────────────────
    private String handleCheckout(String request) {
        try {
            if (orderService == null) return "ERROR:ORDER_SERVICE_NOT_INITIALIZED";

            String[] parts = request.split(":");
            if (parts.length != 2) return "ERROR:CHECKOUT_FORMAT";

            int clientId = Integer.parseInt(parts[1]);
            Cart cart = cartService.getCartByClient(clientId);
            if (cart == null || cart.getItems().isEmpty()) return "ERROR:CART_EMPTY";

            Order order = orderService.createOrder(clientId, cart.getItems());
            cartService.clearCart(clientId);

            return "ORDER_CREATED;" + order.getOrderUUID() + ";" + order.getTotalPrice();

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR:CHECKOUT_EXCEPTION";
        }
    }

    // ── HANDLER PAYMENT ─────────────────────────────
    private String handlePayment(String request) {
        try {
            if (orderService == null) return "ERROR:ORDER_SERVICE_NOT_INITIALIZED";

            String[] parts = request.split(":");
            if (parts.length != 3) return "ERROR:PAYMENT_FORMAT";

            String orderUUID = parts[1];
            String method = parts[2];

            Order order = orderService.getOrderByUUID(orderUUID);
            if (order == null) return "ERROR:ORDER_NOT_FOUND";

            Payment payment = new Payment();
            payment.setOrderId(order.getId());
            payment.setMethod(method);
            payment.setAmount(order.getTotalPrice());
            payment.setStatus("pending");

            boolean success = paymentService.processPayment(payment);

            if (success) {
                orderService.updateStatus(order.getId(), "paid");
                return "PAYMENT_SUCCESS;" + order.getOrderUUID();
            } else {
                return "PAYMENT_FAILED;" + order.getOrderUUID();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR:PAYMENT_EXCEPTION";
        }
    }

    // ── HANDLERS AUTHENTIFICATION ─────────────────────────────
    private String handleLogin(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 3) return "ERROR:LOGIN_FORMAT";

            String email = parts[1];
            String password = parts[2];

            User user = authService.login(email, password);
            if (user != null) return "LOGIN_SUCCESS:" + user.getId() + ":" + user.getRole();
            else return "ERROR:LOGIN_FAILED";

        } catch (Exception e) {
            return "ERROR:LOGIN_EXCEPTION";
        }
    }

    private String handleRegister(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 9) return "ERROR:REGISTER_FORMAT";

            String nom = parts[1];
            String prenom = parts[2];
            String email = parts[3];
            String password = parts[4];
            String address = parts[5];
            String phone = parts[6];
            String ville = parts[7];

            boolean success = authService.register(nom, prenom, email, password, address, phone, ville);
            return success ? "REGISTER_SUCCESS" : "ERROR:REGISTER_FAILED";

        } catch (Exception e) {
            return "ERROR:REGISTER_EXCEPTION";
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.out.println("Erreur fermeture ressources : " + e.getMessage());
        }
    }
}
