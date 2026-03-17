package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // ── Informations de connexion ──────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/chrionline";
    private static final String USER     = "root";
    private static final String PASSWORD = "";  // ton mot de passe MySQL

    // ── Instance unique (Singleton) ────────────
    private static Connection connection = null;

    // ── Constructeur privé ─────────────────────
    // Personne ne peut faire "new DatabaseConnection()"
    private DatabaseConnection() {}

    // ── Obtenir la connexion ───────────────────
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à MySQL réussie !");
            } catch (SQLException e) {
                System.out.println("❌ Erreur de connexion : " + e.getMessage());
            }
        }
        return connection;
    }

    // ── Fermer la connexion ────────────────────
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("🔒 Connexion fermée.");
            } catch (SQLException e) {
                System.out.println("❌ Erreur fermeture : " + e.getMessage());
            }
        }
    }
}