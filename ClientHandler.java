package server;

import dao.ProductDAO;
import model.Cart;
import model.CartItem;
import model.Product;
import service.CartService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import service.AuthService;
import model.User;

public class ClientHandler extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private CartService cartService;
    private ProductDAO productDAO;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.cartService = new CartService();
        this.productDAO = new ProductDAO();

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Erreur initialisation ClientHandler : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            out.println("CONNECTED_TO_CHRIONLINE_SERVER");

            String request;

            while ((request = in.readLine()) != null) {
                System.out.println("Requête reçue : " + request);

                String response = handleRequest(request);

                out.println(response);
            }

        } catch (IOException e) {
            System.out.println("Client déconnecté : " + socket.getInetAddress());
        } finally {
            closeResources();
        }
    }

    private String handleRequest(String request) {
        if (request == null || request.trim().isEmpty()) {
            return "ERROR:EMPTY_REQUEST";
        }

        // Test connexion simple
        if (request.equalsIgnoreCase("PING")) {
            return "PONG";
        }

        // ===== Partie panier (ta partie) =====
        if (request.startsWith("CART_ADD:")) {
            return handleCartAdd(request);
        }

        if (request.startsWith("CART_REMOVE:")) {
            return handleCartRemove(request);
        }

        if (request.startsWith("CART_GET:")) {
            return handleCartGet(request);
        }

        // ===== Parties des autres membres =====
        if (request.startsWith("LOGIN:")) {
            return handleLogin(request);
        }
        if (request.startsWith("REGISTER:")) {
            return handleRegister(request);
        }

        if (request.equalsIgnoreCase("GET_PRODUCTS") || request.startsWith("GET_PRODUCT:")) {
            return "NOT_IMPLEMENTED_PRODUCTS";
        }

        if (request.startsWith("CHECKOUT:") || request.startsWith("PAYMENT:")) {
            return "NOT_IMPLEMENTED_CHECKOUT_PAYMENT";
        }

        return "ERROR:UNKNOWN_COMMAND";
    }

    // =========================================================
    // HANDLER CART_ADD:clientId:productId:quantity
    // =========================================================
    private String handleCartAdd(String request) {
        try {
            String[] parts = request.split(":");

            if (parts.length != 4) {
                return "ERROR:CART_ADD_FORMAT";
            }

            int clientId = Integer.parseInt(parts[1]);
            int productId = Integer.parseInt(parts[2]);
            int quantity = Integer.parseInt(parts[3]);

            if (quantity <= 0) {
                return "ERROR:INVALID_QUANTITY";
            }

            Product product = productDAO.findById(productId);

            if (product == null) {
                return "ERROR:PRODUCT_NOT_FOUND";
            }

            if (product.getStock() < quantity) {
                return "ERROR:INSUFFICIENT_STOCK";
            }

            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);

            boolean added = cartService.addItemToCart(clientId, item);

            if (added) {
                return "CART_ADD_SUCCESS";
            } else {
                return "ERROR:CART_ADD_FAILED";
            }

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (IllegalArgumentException e) {
            return "ERROR:" + e.getMessage();
        } catch (Exception e) {
            return "ERROR:CART_ADD_EXCEPTION";
        }
    }

    // =========================================================
    // HANDLER CART_REMOVE:clientId:productId
    // =========================================================
    private String handleCartRemove(String request) {
        try {
            String[] parts = request.split(":");

            if (parts.length != 3) {
                return "ERROR:CART_REMOVE_FORMAT";
            }

            int clientId = Integer.parseInt(parts[1]);
            int productId = Integer.parseInt(parts[2]);

            boolean removed = cartService.removeItemFromCart(clientId, productId);

            if (removed) {
                return "CART_REMOVE_SUCCESS";
            } else {
                return "ERROR:CART_REMOVE_FAILED";
            }

        } catch (NumberFormatException e) {
            return "ERROR:INVALID_NUMBER_FORMAT";
        } catch (Exception e) {
            return "ERROR:CART_REMOVE_EXCEPTION";
        }
    }

    // =========================================================
    // HANDLER CART_GET:clientId
    // =========================================================
    private String handleCartGet(String request) {
        try {
            String[] parts = request.split(":");

            if (parts.length != 2) {
                return "ERROR:CART_GET_FORMAT";
            }

            int clientId = Integer.parseInt(parts[1]);

            Cart cart = cartService.getCartByClient(clientId);

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                return "CART_EMPTY";
            }

            StringBuilder response = new StringBuilder();
            response.append("CART_DETAILS");
            response.append("|CartID=").append(cart.getId());
            response.append("|Items=").append(cart.getItems().size());
            response.append("|Total=").append(cart.calculateTotal());

            for (CartItem item : cart.getItems()) {
                if (item.getProduct() != null) {
                    response.append("|Product=")
                            .append(item.getProduct().getName())
                            .append(",Qty=")
                            .append(item.getQuantity())
                            .append(",Subtotal=")
                            .append(item.calculateSubtotal());
                } else {
                    response.append("|Product=UNKNOWN")
                            .append(",Qty=")
                            .append(item.getQuantity())
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

    private void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Erreur fermeture ressources : " + e.getMessage());
        }
    }
 // =========================================================
 // HANDLER LOGIN:email:password
 // =========================================================
 private String handleLogin(String request) {
     try {
         String[] parts = request.split(":");
         if (parts.length != 3) {
             return "ERROR:LOGIN_FORMAT";
         }

         String email    = parts[1];
         String password = parts[2];

         AuthService authService = new AuthService();
         User user = authService.login(email, password);

         if (user != null) {
             return "LOGIN_SUCCESS:" + user.getId() + ":" + user.getRole();
         } else {
             return "ERROR:LOGIN_FAILED";
         }

     } catch (Exception e) {
         return "ERROR:LOGIN_EXCEPTION";
     }
 }

 // =========================================================
 // HANDLER REGISTER:nom:prenom:email:password:address:phone:ville
 // =========================================================
 private String handleRegister(String request) {
     try {
         String[] parts = request.split(":");
         if (parts.length != 9) {
             return "ERROR:REGISTER_FORMAT";
         }

         String nom      = parts[1];
         String prenom   = parts[2];
         String email    = parts[3];
         String password = parts[4];
         String address  = parts[5];
         String phone    = parts[6];
         String ville    = parts[7];

         AuthService authService = new AuthService();
         boolean success = authService.register(nom, prenom, email,
                                                password, address, phone, ville);
         if (success) {
             return "REGISTER_SUCCESS";
         } else {
             return "ERROR:REGISTER_FAILED";
         }

     } catch (Exception e) {
         return "ERROR:REGISTER_EXCEPTION";
     }
 }
}