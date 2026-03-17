package e_commerce;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    private Connection conn;

    public OrderItemDAO() throws SQLException {
        this.conn = database.DatabaseConnection.getConnection(); 
        
    }

    // ── Ajouter un item ──────────────────────
    public void save(OrderItem item, int orderId) throws SQLException {

        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, item.getProduct().getIdProduct());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getUnitPrice());

            ps.executeUpdate();
        }
    }

    // ── Récupérer items d’une commande ───────
    public List<OrderItem> findByOrder(int orderId) throws SQLException {

        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                // 🔥 On met NULL pour éviter erreurs constructeur
                OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        null, // order
                        null, // product
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                );

                items.add(item);
            }
        }
        return items;
    }

    // ── Supprimer ────────────────────────────
    public void delete(int orderItemId) throws SQLException {

        String sql = "DELETE FROM order_items WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderItemId);
            ps.executeUpdate();
        }
    }
}