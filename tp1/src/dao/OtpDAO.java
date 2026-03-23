package dao;

import database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;

public class OtpDAO {

    private Connection connection;

    public OtpDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Sauvegarder un nouveau code OTP
    public boolean saveOtp(String email, String code) {
        // Supprimer les anciens codes de cet email d'abord
        deleteOtpByEmail(email);

        String sql = "INSERT INTO otp_codes (email, code, created_at, expires_at, used) " +
                     "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 10 MINUTE), FALSE)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, code);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur saveOtp : " + e.getMessage());
            return false;
        }
    }

    // Vérifier si le code est valide (pas expiré, pas utilisé)
    public boolean verifyOtp(String email, String code) {
        String sql = "SELECT id FROM otp_codes " +
                     "WHERE email = ? AND code = ? " +
                     "AND used = FALSE " +
                     "AND expires_at > NOW()";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, code);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Marquer le code comme utilisé
                int id = rs.getInt("id");
                markAsUsed(id);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur verifyOtp : " + e.getMessage());
        }
        return false;
    }

    // Marquer le code comme utilisé
    private void markAsUsed(int id) {
        String sql = "UPDATE otp_codes SET used = TRUE WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur markAsUsed : " + e.getMessage());
        }
    }

    // Supprimer les anciens codes d'un email
    public void deleteOtpByEmail(String email) {
        String sql = "DELETE FROM otp_codes WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur deleteOtpByEmail : " + e.getMessage());
        }
    }

    // Activer le compte après vérification OTP réussie
    public boolean activateAccount(String email) {
        String sql = "UPDATE users SET status = 'active' WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur activateAccount : " + e.getMessage());
            return false;
        }
    }
}