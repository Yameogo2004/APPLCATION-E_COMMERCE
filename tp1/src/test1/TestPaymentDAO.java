package test1;

import dao.PaymentDAO;
import model.Payment;

import java.time.LocalDateTime;

public class TestPaymentDAO {
    public static void main(String[] args) {
        PaymentDAO paymentDAO = new PaymentDAO();

        Payment payment = new Payment();
        payment.setOrderId(1); // il faut que la commande 1 existe dans orders
        payment.setMethod("card");
        payment.setAmount(250.0);
        payment.setStatus("success");
        payment.setPaidAt(LocalDateTime.now());

        boolean saved = paymentDAO.save(payment);

        System.out.println("Paiement enregistré : " + saved);
        System.out.println("ID paiement généré : " + payment.getId());
    }
}