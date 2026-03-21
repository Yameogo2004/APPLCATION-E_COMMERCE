package model;

import java.time.LocalDateTime;

public class Product {

    private int idProduct;
    private String name;
    private String description;
    private String imagePath; // <-- chemin relatif vers le dossier images
    private double price;
    private int stock;
    private LocalDateTime createdAt;
    private Category category;

    // ── Constructeur ─────────────────────────────
    public Product(int idProduct, String name, String description, String imagePath,
                   double price, int stock) {
        this.idProduct = idProduct;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath; // ex: "images/laptop.png"
        this.price = price;
        this.stock = stock;
        this.createdAt = LocalDateTime.now();
        this.category = null; 
    }

    // ── Getters & Setters ─────────────────────
    public int getIdProduct() { return idProduct; }
    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    // ── Méthodes ─────────────────────────────

    public void consulterProduit() {
        System.out.println("Produit : " + name);
        System.out.println("Description : " + description);
        System.out.println("Prix : " + price);
        System.out.println("Stock : " + stock);
        System.out.println("Image : " + imagePath);
        if (category != null) {
            System.out.println("Catégorie ID : " + category.getId());
        }
    }

    public void modifierStock(int newStock) {
        this.stock = newStock;
        System.out.println("Stock mis à jour : " + stock);
    }
}