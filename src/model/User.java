package model;

import java.util.Date;

public abstract class User {

    // ── Attributs ──────────────────────────────
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private Date createdAt;

    // ── Constructeur ───────────────────────────
    public User(String nom, String prenom, String email, String password, String role) {
        this.nom       = nom;
        this.prenom    = prenom;
        this.email     = email;
        this.password  = password;
        this.role      = role;
        this.createdAt = new Date();
    }

    // ── Méthodes abstraites ────────────────────
    // Chaque sous-classe (Client, Admin) DOIT les implémenter
    public abstract boolean login(String email, String password);
    public abstract void logout();

    // ── Getters & Setters ──────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // ── toString ───────────────────────────────
    @Override
    public String toString() {
        return "User{id=" + id + ", nom='" + nom + "', prenom='" + prenom +
               "', email='" + email + "', role='" + role + "'}";
    }
}