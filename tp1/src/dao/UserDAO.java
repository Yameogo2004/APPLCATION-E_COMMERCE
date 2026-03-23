package dao;

import database.DatabaseConnection;
import model.Admin;
import model.Client;
import model.User;

import java.sql.*;

public class UserDAO {

    private Connection connection;

    // ── Constructeur ──────────────────────────
    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // ── Trouver un user par email (pour le login) ──
    public User findByEmail(String email) {
        String sql = "SELECT u.*, c.address, c.phone, c.ville " +
                     "FROM users u LEFT JOIN clients c ON u.id = c.id " +
                     "WHERE u.email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if (role.equals("client")) {
                    Client client = new Client(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("ville")
                    );
                    client.setId(rs.getInt("id"));
                    return client;
                } else {
                    Admin admin = new Admin(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                    admin.setId(rs.getInt("id"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.out.println(" Erreur find  ByEmail : " + e.getMessage());
        }
        return null;
    }

    // ── Enregistrer un nouveau client ──────────
    public boolean save(Client client) {
        String sqlUser   = "INSERT INTO users (nom, prenom, email, password, role) " +
                           "VALUES (?, ?, ?, ?, 'client')";
        String sqlClient = "INSERT INTO clients (id, address, phone, ville) " +
                           "VALUES (?, ?, ?, ?)";
        try {
            // Désactiver l'auto-commit pour démarrer la transaction
            connection.setAutoCommit(false);

            PreparedStatement psUser = connection.prepareStatement(
                sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, client.getNom());
            psUser.setString(2, client.getPrenom());
            psUser.setString(3, client.getEmail());
            psUser.setString(4, client.getPassword());
            psUser.executeUpdate();

            ResultSet keys = psUser.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                client.setId(id);

                PreparedStatement psClient = connection.prepareStatement(sqlClient);
                psClient.setInt(1, id);
                psClient.setString(2, client.getAddress());
                psClient.setString(3, client.getPhone());
                psClient.setString(4, client.getVille());
                psClient.executeUpdate();
            }

            // Les deux INSERT ont réussi → on valide
            connection.commit();
            System.out.println("Client enregistré avec succès !");
            return true;

        } catch (SQLException e) {
            // Un INSERT a échoué → on annule tout
            try { connection.rollback(); } catch (SQLException ex) {
                System.out.println("Erreur rollback : " + ex.getMessage());
            }
            System.out.println("Erreur save (transaction annulée) : " + e.getMessage());
            return false;

        } finally {
            // Toujours remettre l'auto-commit à true après
            try { connection.setAutoCommit(true); } catch (SQLException e) {
                System.out.println("Erreur reset autoCommit : " + e.getMessage());
            }
        }
    }

    // ── Vérifier si un email existe déjà ───────
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(" Erreur email exist : " + e.getMessage());
        }
        return false;
    }
}