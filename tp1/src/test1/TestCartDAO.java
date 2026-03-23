package test1;

import dao.CartDAO;
import model.Cart;
import model.CartItem;
import model.Product;

public class TestCartDAO {
    public static void main(String[] args) {
        CartDAO cartDAO = new CartDAO();

        int clientId = 1;   // il faut que ce client existe dans la base
        int productId = 1;  // il faut que ce produit existe dans la base

        // 1. Récupérer ou créer le panier du client
        Cart cart = cartDAO.getCartByClient(clientId);

        if (cart == null) {
            System.out.println("Erreur : panier introuvable.");
            return;
        }

        System.out.println("Panier trouvé/créé avec ID : " + cart.getId());

        // 2. Créer un produit minimal pour l’ajout
        Product product = new Product(
            productId,
            "Produit test",
            "Description test",
            "image.jpg",
            100.0,
            10
        );

        // 3. Créer un CartItem
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);

        // 4. Ajouter au panier
        boolean added = cartDAO.addItem(cart.getId(), item);
        System.out.println("Ajout produit au panier : " + added);

        // 5. Relire le panier
        Cart updatedCart = cartDAO.getCartByClient(clientId);
        if (updatedCart != null) {
            System.out.println("Nombre d'articles dans le panier : " + updatedCart.getItems().size());
            System.out.println("Total panier : " + updatedCart.calculateTotal());
        }

        // 6. Supprimer le produit
        boolean removed = cartDAO.removeItem(cart.getId(), productId);
        System.out.println("Suppression produit : " + removed);

        // 7. Vider le panier
        boolean cleared = cartDAO.clear(cart.getId());
        System.out.println("Panier vidé : " + cleared);
    }
}