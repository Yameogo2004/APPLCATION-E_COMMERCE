package server;

import java.io.*;
import java.net.Socket;
import java.util.List;

import model.Cart;
import model.CartItem;
import model.Order;
import model.Payment;
import model.Product;
import model.User;

import service.AuthService;
import service.CartService;
import service.OrderService;
import service.PaymentService;
import service.ProductService;

public class ClientHandler extends Thread {

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
                out.println(handleRequest(request));
            }

        } catch (IOException e) {
            System.out.println("Client déconnecté : " + clientSocket.getInetAddress());
        } finally {
            closeResources();
        }
    }

    private String handleRequest(String request) {
        try {
            if (request == null || request.trim().isEmpty()) {
                return "ERROR:EMPTY_REQUEST";
            }

            if (request.equalsIgnoreCase("PING")) {
                return "PONG";
            }

            // ===== PANIER =====
            if (request.startsWith("CART_ADD:")) {
                return handleCartAdd(request);
            }
            if (request.startsWith("CART_REMOVE:")) {
                return handleCartRemove(request);
            }
            if (request.startsWith("CART_GET:")) {
                return handleCartGet(request);
            }
            if (request.startsWith("CART_CLEAR:")) {
                return handleCartClear(request);
            }

            // ===== PRODUITS =====
            if (request.equalsIgnoreCase("GET_PRODUCTS")) {
                return handleGetProducts();
            }
            if (request.startsWith("GET_PRODUCT:")) {
                return handleGetProduct(request);
            }

            // ===== CHECKOUT =====
            if (request.startsWith("CHECKOUT:")) {
                return handleCheckout(request);
            }

            // ===== PAYMENT =====
            if (request.startsWith("PAYMENT:")) {
                return handlePayment(request);
            }

            // ===== AUTH =====
            if (request.startsWith("LOGIN:")) {
                return handleLogin(request);
            }
            if (request.startsWith("REGISTER:")) {
                return handleRegister(request);
            }

            return "ERROR:UNKNOWN_COMMAND";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR:EXCEPTION_OCCURRED";
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
            e.printStackTrace();
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
            e.printStackTrace();
            return "ERROR:CART_REMOVE_EXCEPTION";
        }
    }

    private String handleCartGet(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 2) return "ERROR:CART_GET_FORMAT";

            int clientId = Integer.parseInt(parts[1]);
            Cart cart = cartService.getCartByClient(clientId);

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                return "CART_EMPTY";
            }

            StringBuilder response = new StringBuilder();
            response.append("CART_DETAILS")
                    .append("|CartID=").append(cart.getId())
                    .append("|Items=").append(cart.getItems().size())
                    .append("|Total=").append(cart.calculateTotal());

            for (CartItem item : cart.getItems()) {
                if (item.getProduct() != null) {
                    response.append("|ProductId=").append(item.getProduct().getIdProduct())
                            .append(",Product=").append(safe(item.getProduct().getName()))
                            .append(",Qty=").append(item.getQuantity())
                            .append(",Subtotal=").append(item.calculateSubtotal());
                } else {
                    response.append("|ProductId=0,Product=UNKNOWN,Qty=").append(item.getQuantity())
                            .append(",Subtotal=0.0");
                }
            }

            return response.toString();

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR:CART_GET_EXCEPTION";
        }
    }

    private String handleCartClear(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 2) return "ERROR:CART_CLEAR_FORMAT";

            int clientId = Integer.parseInt(parts[1]);

            boolean cleared = cartService.clearCart(clientId);
            return cleared ? "CART_CLEAR_SUCCESS" : "ERROR:CART_CLEAR_FAILED";

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR:CART_CLEAR_EXCEPTION";
        }
    }

    // ── HANDLERS PRODUITS ─────────────────────────────

    private String handleGetProducts() {
        try {
            List<Product> products = productService.getAllProducts();

            if (products == null || products.isEmpty()) {
                return "NO_PRODUCTS";
            }

            StringBuilder sb = new StringBuilder();

            for (Product p : products) {
                String categoryName = "Sans catégorie";
                if (p.getCategory() != null && p.getCategory().getName() != null) {
                    categoryName = p.getCategory().getName();
                }

                sb.append(p.getIdProduct()).append(";")
                  .append(safe(p.getName())).append(";")
                  .append(p.getPrice()).append(";")
                  .append(safe(p.getImage())).append(";")
                  .append(safe(categoryName)).append(";")
                  .append(p.getStock())
                  .append("|");
            }

            return sb.substring(0, sb.length() - 1);

        } catch (Exception e) {
            e.printStackTrace();
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

            String categoryName = "Sans catégorie";
            if (p.getCategory() != null && p.getCategory().getName() != null) {
                categoryName = p.getCategory().getName();
            }

            return p.getIdProduct() + ";" +
                   safe(p.getName()) + ";" +
                   p.getPrice() + ";" +
                   safe(p.getDescription()) + ";" +
                   p.getStock() + ";" +
                   safe(p.getImage()) + ";" +
                   safe(categoryName);

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (Exception e) {
            e.printStackTrace();
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

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                return "ERROR:CART_EMPTY";
            }

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

    // ── HANDLERS AUTH ─────────────────────────────

    private String handleLogin(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 3) return "ERROR:LOGIN_FORMAT";

            String email = parts[1];
            String password = parts[2];

            User user = authService.login(email, password);
            if (user != null) {
                return "LOGIN_SUCCESS:" + user.getId() + ":" + user.getRole();
            } else {
                return "ERROR:LOGIN_FAILED";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR:LOGIN_EXCEPTION";
        }
    }

    private String handleRegister(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length != 8) return "ERROR:REGISTER_FORMAT";

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
            e.printStackTrace();
            return "ERROR:REGISTER_EXCEPTION";
        }
    }

    private String safe(String value) {
        if (value == null) return "";
        return value.replace(";", ",").replace("|", "/");
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