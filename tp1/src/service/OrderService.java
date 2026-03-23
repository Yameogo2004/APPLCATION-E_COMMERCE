package service;

import java.util.List;
import java.sql.SQLException;
import java.util.UUID;

import dao.OrderDAO;
import dao.OrderItemDAO;
import model.CartItem;
import model.Order;
import model.OrderItem;

public class OrderService {

    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;

    public OrderService() throws SQLException {
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
    }

    // Crée une commande à partir du panier
    public Order createOrder(int clientId, List<CartItem> cartItems) throws SQLException {
        Order order = new Order(0);
        order.setOrderUUID(UUID.randomUUID().toString());
        order.setStatus("pending");

        for (CartItem c : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setProduct(c.getProduct());
            oi.setQuantity(c.getQuantity());
            oi.setUnitPrice(c.getProduct().getPrice());
            order.ajouterItem(oi);
        }

        orderDAO.save(order, clientId);
        return order;
    }

    // Récupère une commande par UUID
    public Order getOrderByUUID(String uuid) throws SQLException {
        return orderDAO.findByUUID(uuid);
    }

    // Met à jour le statut
    public void updateStatus(int orderId, String newStatus) throws SQLException {
        orderDAO.updateStatus(orderId, newStatus);
    }
}