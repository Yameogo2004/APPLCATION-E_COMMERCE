package service;

import dao.UserDAO;
import model.Client;
import model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

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

    public boolean checkPassword(String password, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) return false;

        String[] parts = storedHash.split(":");
        if (parts.length != 2) return false;

        String salt = parts[0];
        String hash = parts[1];
        return hash.equals(hashPassword(password, salt));
    }

    public User login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        User user = userDAO.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!"active".equalsIgnoreCase(user.getStatus())) {
            System.out.println("Compte non actif : " + email);
            return null;
        }

        if (checkPassword(password, user.getPassword())) {
            System.out.println("Connexion réussie ! Bienvenue " + user.getNom());
            return user;
        }

        return null;
    }

    public boolean registerPending(String nom, String prenom, String email,
                                   String password, String address,
                                   String phone, String ville) {
        if (nom == null || nom.isBlank() ||
                prenom == null || prenom.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            return false;
        }

        if (userDAO.emailExists(email)) {
            return false;
        }

        String salt = generateSalt();
        String hashedPassword = salt + ":" + hashPassword(password, salt);

        Client client = new Client(nom, prenom, email, hashedPassword, address, phone, ville);
        return userDAO.savePendingClient(client);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }

    public boolean isAccountActive(String email) {
        return userDAO.isAccountActive(email);
    }
}