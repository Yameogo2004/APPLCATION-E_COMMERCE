package e_commerce;

public class OrderItem {

    private int id;
    private Order order;
    private Product product;
    private int quantity;
    private double unitPrice;

    public OrderItem(int id, Order order, Product product, int quantity, double unitPrice) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double calculSubtotal() {
        return quantity * unitPrice;
    }
}
