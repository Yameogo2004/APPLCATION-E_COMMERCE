package service;

import dao.CartDAO;
import model.Cart;
import model.CartItem;

public class CartService {

    private CartDAO cartDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
    }

    // ── Récupérer le panier d’un client ───────────────────────
    public Cart getCartByClient(int clientId) {
        return cartDAO.getCartByClient(clientId);
    }

    // ── Ajouter un article au panier ──────────────────────────
    public boolean addItemToCart(int clientId, CartItem item) {
        Cart cart = cartDAO.getCartByClient(clientId);

        if (cart == null) {
            System.out.println("Panier introuvable.");
            return false;
        }

        if (item == null || item.getProduct() == null) {
            System.out.println("Article invalide.");
            return false;
        }

        if (item.getQuantity() <= 0) {
            System.out.println("Quantité invalide.");
            return false;
        }

        return cartDAO.addItem(cart.getId(), item);
    }

    // ── Supprimer un article du panier ────────────────────────
    public boolean removeItemFromCart(int clientId, int productId) {
        Cart cart = cartDAO.getCartByClient(clientId);

        if (cart == null) {
            System.out.println("Panier introuvable.");
            return false;
        }

        return cartDAO.removeItem(cart.getId(), productId);
    }

    // ── Vider le panier ───────────────────────────────────────
    public boolean clearCart(int clientId) {
        Cart cart = cartDAO.getCartByClient(clientId);

        if (cart == null) {
            System.out.println("Panier introuvable.");
            return false;
        }

        return cartDAO.clear(cart.getId());
    }

    // ── Calculer le total du panier ───────────────────────────
    public double calculateCartTotal(int clientId) {
        Cart cart = cartDAO.getCartByClient(clientId);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0.0;
        }

        return cart.calculateTotal();
    }

    // ── Vérifier si le panier est vide ────────────────────────
    public boolean isCartEmpty(int clientId) {
        Cart cart = cartDAO.getCartByClient(clientId);

        return cart == null || cart.getItems() == null || cart.getItems().isEmpty();
    }
}