package e_commerce;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderService {

    private OrderDAO orderDAO;

    public OrderService() throws Exception {
        this.orderDAO = new OrderDAO();
    }

    /**
     * Valide une commande : 
     * - transforme les CartItems en OrderItems
     * - calcule le total
     * - génère UUID et date
     * - enregistre en DB
     */
    public Order validateOrder(int clientId, List<CartItem> cartItems) throws Exception {

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Panier vide !");
        }

        double total = 0;
        Order order = new Order(); // ✅ constructeur vide

        // 🔄 transformation panier → commande
        for (CartItem c : cartItems) {
            total += c.calculateSubtotal(); // calcule price * quantity

            OrderItem oi = new OrderItem();
            oi.setProductId(c.getProduct().getIdProduct());
            oi.setQuantity(c.getQuantity());
            oi.setPrice(c.getProduct().getPrice());

            order.getItems().add(oi);
        }

        // 🔹 UUID + total + statut + date
        order.setOrderUUID(UUID.randomUUID().toString());
        order.setTotalPrice(total);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        // 💾 sauvegarde en DB via DAO
        orderDAO.save(order, clientId);

        return order;
    }

    /**
     * Récupère l'historique des commandes d’un client
     */
    public List<Order> getOrdersByClient(int clientId) throws Exception {
        return orderDAO.findByClient(clientId);
    }

    /**
     * Met à jour le statut d’une commande
     */
    public void updateOrderStatus(int orderId, String newStatus) throws Exception {
        orderDAO.updateStatus(orderId, newStatus);
    }
}