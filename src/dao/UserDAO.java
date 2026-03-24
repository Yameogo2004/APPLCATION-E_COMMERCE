package dao;

import database.DatabaseConnection;
import model.Admin;
import model.Client;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public User findByEmail(String email) {
        String sql = "SELECT u.*, c.address, c.phone, c.ville " +
                "FROM users u LEFT JOIN clients c ON u.id = c.id " +
                "WHERE u.email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                String status = rs.getString("status");

                if ("client".equalsIgnoreCase(role)) {
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
                    client.setRole(role);
                    client.setStatus(status);
                    return client;
                } else {
                    Admin admin = new Admin(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                    admin.setId(rs.getInt("id"));
                    admin.setRole(role);
                    admin.setStatus(status);
                    return admin;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur findByEmail : " + e.getMessage());
        }

        return null;
    }

    public boolean save(Client client) {
        return saveClientWithStatus(client, "active");
    }

    public boolean savePendingClient(Client client) {
        return saveClientWithStatus(client, "pending");
    }

    private boolean saveClientWithStatus(Client client, String status) {
        String sqlUser = "INSERT INTO users (nom, prenom, email, password, role, status) VALUES (?, ?, ?, ?, 'client', ?)";
        String sqlClient = "INSERT INTO clients (id, address, phone, ville) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psUser = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, client.getNom());
                psUser.setString(2, client.getPrenom());
                psUser.setString(3, client.getEmail());
                psUser.setString(4, client.getPassword());
                psUser.setString(5, status);
                psUser.executeUpdate();

                ResultSet keys = psUser.getGeneratedKeys();
                if (!keys.next()) {
                    connection.rollback();
                    return false;
                }

                int id = keys.getInt(1);
                client.setId(id);

                try (PreparedStatement psClient = connection.prepareStatement(sqlClient)) {
                    psClient.setInt(1, id);
                    psClient.setString(2, client.getAddress());
                    psClient.setString(3, client.getPhone());
                    psClient.setString(4, client.getVille());
                    psClient.executeUpdate();
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            System.out.println("Erreur save client : " + e.getMessage());
            return false;

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.out.println("Erreur emailExists : " + e.getMessage());
            return false;
        }
    }

    public boolean isAccountActive(String email) {
        String sql = "SELECT status FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return "active".equalsIgnoreCase(rs.getString("status"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur isAccountActive : " + e.getMessage());
        }

        return false;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        String sql = "SELECT u.*, c.address, c.phone, c.ville " +
                "FROM users u LEFT JOIN clients c ON u.id = c.id " +
                "ORDER BY u.id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String role = rs.getString("role");
                String status = rs.getString("status");

                if ("client".equalsIgnoreCase(role)) {
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
                    client.setRole(role);
                    client.setStatus(status);
                    users.add(client);
                } else {
                    Admin admin = new Admin(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                    admin.setId(rs.getInt("id"));
                    admin.setRole(role);
                    admin.setStatus(status);
                    users.add(admin);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur findAll users : " + e.getMessage());
        }

        return users;
    }

    public User findById(int userId) {
        String sql = "SELECT u.*, c.address, c.phone, c.ville " +
                "FROM users u LEFT JOIN clients c ON u.id = c.id " +
                "WHERE u.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                String status = rs.getString("status");

                if ("client".equalsIgnoreCase(role)) {
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
                    client.setRole(role);
                    client.setStatus(status);
                    return client;
                } else {
                    Admin admin = new Admin(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                    admin.setId(rs.getInt("id"));
                    admin.setRole(role);
                    admin.setStatus(status);
                    return admin;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur findById : " + e.getMessage());
        }

        return null;
    }

    public boolean updateProfile(int userId, String nomComplet, String email, String phone, String address, String ville) {
        try {
            connection.setAutoCommit(false);

            String nom = nomComplet;
            String prenom = "";

            if (nomComplet != null && nomComplet.trim().contains(" ")) {
                int idx = nomComplet.trim().indexOf(" ");
                prenom = nomComplet.trim().substring(0, idx).trim();
                nom = nomComplet.trim().substring(idx + 1).trim();
            }

            if (nom == null || nom.isBlank()) {
                nom = "SansNom";
            }

            String sqlUser = "UPDATE users SET nom = ?, prenom = ?, email = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
                ps.setString(1, nom);
                ps.setString(2, prenom);
                ps.setString(3, email);
                ps.setInt(4, userId);
                ps.executeUpdate();
            }

            String role = null;
            String roleSql = "SELECT role FROM users WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(roleSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    role = rs.getString("role");
                }
            }

            if ("client".equalsIgnoreCase(role)) {
                String checkClientSql = "SELECT id FROM clients WHERE id = ?";
                boolean exists = false;

                try (PreparedStatement ps = connection.prepareStatement(checkClientSql)) {
                    ps.setInt(1, userId);
                    ResultSet rs = ps.executeQuery();
                    exists = rs.next();
                }

                if (exists) {
                    String updateClientSql = "UPDATE clients SET address = ?, phone = ?, ville = ? WHERE id = ?";
                    try (PreparedStatement ps = connection.prepareStatement(updateClientSql)) {
                        ps.setString(1, address);
                        ps.setString(2, phone);
                        ps.setString(3, ville);
                        ps.setInt(4, userId);
                        ps.executeUpdate();
                    }
                } else {
                    String insertClientSql = "INSERT INTO clients (id, address, phone, ville) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = connection.prepareStatement(insertClientSql)) {
                        ps.setInt(1, userId);
                        ps.setString(2, address);
                        ps.setString(3, phone);
                        ps.setString(4, ville);
                        ps.executeUpdate();
                    }
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            System.out.println("Erreur updateProfile : " + e.getMessage());
            return false;

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }
}