package model;

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
    
    public Order() {
        this.orderUUID = UUID.randomUUID().toString();
        this.status = "pending";
        this.createdAt = LocalDateTime.now();
        this.totalPrice = 0;
    }
    

    public LocalDateTime getCreatedAt() {
		return createdAt;
	}




	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}




	public void setId(int id) {
		this.id = id;
	}




	public void setOrderUUID(String orderUUID) {
		this.orderUUID = orderUUID;
	}




	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}




	public void setStatus(String status) {
		this.status = status;
	}




	public void setItems(List<OrderItem> items) {
		this.items = items;
	}




	public void setPayment(Payment payment) {
		this.payment = payment;
	}




	// ===== Getters =====
    public int getId() { return id; }
    public String getOrderUUID() { return orderUUID; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public List<OrderItem> getItems() { return items; }
    public Payment getPayment() { return payment; }

    // ===== Ajouter un item et recalculer le total =====
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

    // ===== Gestion du statut de la commande =====
    public void validerCommande() {
        status = "validated";
        System.out.println("Commande validée.");
    }

    public void payerCommande(Payment payment) {
        this.payment = payment;

        // Utiliser processPayment() de Payment
        if (payment.processPayment()) {
            status = "paid";
            System.out.println("Paiement réussi !");
        } else {
            status = "pending"; // ou "failed" selon ton choix
            System.out.println("Paiement échoué !");
        }
    }

    public void cancelOrder() {
        status = "cancelled";
        System.out.println("Commande annulée.");
    }

  
}





