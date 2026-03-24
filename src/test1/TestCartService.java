package test1;

import model.CartItem;
import model.Product;
import service.CartService;

public class TestCartService {
    public static void main(String[] args) {
        CartService cartService = new CartService();

        Product product = new Product(
            1,
            "Smartphone",
            "Test produit",
            "img.jpg",
            1000.0,
            5
        );

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);

        boolean added = cartService.addItemToCart(1, item);
        System.out.println("Ajout article : " + added);

        double total = cartService.calculateCartTotal(1);
        System.out.println("Total panier : " + total);
    }
}