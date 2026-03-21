package model;

public class Administrateur extends User {

    // ── Constructeur ──────────────────────────
    public Administrateur(String nom, String prenom, String email, String password) {
        super(nom, prenom, email, password, "admin");
    }

    // ── Implémentation des méthodes abstraites de User ──
    @Override
    public boolean login(String email, String password) {
        return this.getEmail().equals(email) && this.getPassword().equals(password);
    }

    @Override
    public void logout() {
        System.out.println("Administrateur " + getNom() + " déconnecté.");
    }

    // ── Méthodes spécifiques à l'Admin ────────
    public void addProduct() {
        System.out.println("Produit ajouté.");
    }

    public void updateProduct() {
        System.out.println("Produit modifié.");
    }

    public void deleteProduct() {
        System.out.println("Produit supprimé.");
    }

    public void manageUsers() {
        System.out.println("Gestion des utilisateurs.");
    }

    public void manageOrders() {
        System.out.println("Gestion des commandes.");
    }

    public void updateOrderStatus(String status) {
        System.out.println("Statut commande mis à jour : " + status);
    }

    // ── toString ──────────────────────────────
    @Override
    public String toString() {
        return "Administrateur{nom='" + getNom() + "', prenom='" + getPrenom() +
               "', email='" + getEmail() + "'}";
    }
}