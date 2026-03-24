package Client;

public class AppSession {

    private int clientId;
    private String role;
    private String orderUUID;
    private double lastOrderTotal;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrderUUID() {
        return orderUUID;
    }

    public void setOrderUUID(String orderUUID) {
        this.orderUUID = orderUUID;
    }

    public double getLastOrderTotal() {
        return lastOrderTotal;
    }

    public void setLastOrderTotal(double lastOrderTotal) {
        this.lastOrderTotal = lastOrderTotal;
    }

    public void clearOrderData() {
        this.orderUUID = null;
        this.lastOrderTotal = 0.0;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
}