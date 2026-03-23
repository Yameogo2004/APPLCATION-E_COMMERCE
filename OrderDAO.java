package e_commerce;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;

public class OrderDAO {

    private Connection conn;

    public OrderDAO() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    // ── Enregistrer une commande ─────────────────────────────
    public void save(Order order, int clientId) throws SQLException {

        String sql = "INSERT INTO orders (order_uuid, client_id, total_price, status, created_at) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false); // 🔥 début transaction

            // 1. insertion commande
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getOrderUUID());
            ps.setInt(2, clientId);
            ps.setDouble(3, order.getTotalPrice());
            ps.setString(4, order.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(order.getCreatedAt()));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                order.setId(rs.getInt(1));
            }

            // 2. insertion des items
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                OrderItemDAO itemDAO = new OrderItemDAO();

                for (OrderItem item : order.getItems()) {
                    itemDAO.save(item, order.getId());
                }
            }

            conn.commit(); // ✅ valider transaction

        } catch (SQLException e) {
            conn.rollback(); // ❌ annuler tout si erreur
            e.printStackTrace();
            throw e;
        } finally {
            conn.setAutoCommit(true); // 🔄 remettre normal
        }
    }

    // ── Récupérer les commandes d’un client ─────────────────
    public List<Order> findByClient(int clientId) throws SQLException {

        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE client_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderUUID(rs.getString("order_uuid"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                // 🔥 charger les items
                OrderItemDAO itemDAO = new OrderItemDAO();
                List<OrderItem> items = itemDAO.findByOrder(order.getId());

                order.setItems(items);

                orders.add(order);
            }
        }

        return orders;
    }

    // ── Mettre à jour le statut ─────────────────────────────
    public void updateStatus(int orderId, String newStatus) throws SQLException {

        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, orderId);

            ps.executeUpdate();
        }
    }
}
