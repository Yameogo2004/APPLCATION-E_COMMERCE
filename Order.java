package e_commerce;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Order {

    private int id;
    private String orderUUID;
    private double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItem> items = new ArrayList<>();
    private Payment payment;

    public Order(int id) {
        this.id = id;
        this.orderUUID = UUID.randomUUID().toString();
        this.status = "pending";
        this.createdAt = LocalDateTime.now();
        this.totalPrice = 0;
    }

    public int getId() { return id; }
    public String getOrderUUID() { return orderUUID; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public List<OrderItem> getItems() { return items; }

    public void ajouterItem(OrderItem item) {
        items.add(item);
        calculTotal();
    }

    public double calculTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.calculSubtotal();
        }
        this.totalPrice = total;
        return total;
    }

    public void validerCommande() {
        status = "validated";
        System.out.println("Commande validée.");
    }

    public void payerCommande(Payment payment) {
        this.payment = payment;
        status = "paid";
        payment.effectuerPaiement();
    }

    public void cancelOrder() {
        status = "cancelled";
        System.out.println("Commande annulée.");
    }
}