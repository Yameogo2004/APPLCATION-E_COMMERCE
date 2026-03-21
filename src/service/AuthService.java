package service;

import DAO.UserDAO;
import model.Client;
import model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthService {

    private UserDAO userDAO;

    // ── Constructeur ──────────────────────────
    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // ── Générer un salt aléatoire ─────────────
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // ── Hash mot de passe avec SHA-256 + Salt ─
    public String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur hash : " + e.getMessage());
            return null;
        }
    }

    // ── Vérifier le mot de passe ──────────────
    public boolean checkPassword(String password, String storedHash) {
        // storedHash format : "salt:hash"
        String[] parts = storedHash.split(":");
        if (parts.length != 2) return false;
        String salt = parts[0];
        String hash = parts[1];
        return hash.equals(hashPassword(password, salt));
    }

    // ── Login ─────────────────────────────────
    public User login(String email, String password) {
        if (email == null || email.isEmpty() ||
            password == null || password.isEmpty()) {
            System.out.println("Email ou mot de passe vide !");
            return null;
        }

        User user = userDAO.findByEmail(email);

        if (user == null) {
            System.out.println("Aucun compte trouvé avec cet email.");
            return null;
        }

        if (checkPassword(password, user.getPassword())) {
            System.out.println("Connexion réussie ! Bienvenue " + user.getNom());
            return user;
        } else {
            System.out.println("Mot de passe incorrect.");
            return null;
        }
    }

    // ── Inscription ───────────────────────────
    public boolean register(String nom, String prenom, String email,
                            String password, String address,
                            String phone, String ville) {
        if (nom.isEmpty() || prenom.isEmpty() ||
            email.isEmpty() || password.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs !");
            return false;
        }

        if (userDAO.emailExists(email)) {
            System.out.println("Cet email est déjà utilisé !");
            return false;
        }

        // Générer salt + hasher le mot de passe
        String salt = generateSalt();
        String hashedPassword = salt + ":" + hashPassword(password, salt);

        Client client = new Client(nom, prenom, email,
                                   hashedPassword, address, phone, ville);
        boolean success = userDAO.save(client);

        if (success) {
            System.out.println("Inscription réussie ! Bienvenue " + nom);
        }
        return success;
    }
}
