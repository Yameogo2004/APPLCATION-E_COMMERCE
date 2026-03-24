package test1;

import model.Payment;
import service.PaymentService;

public class TestPaymentService {
    public static void main(String[] args) {
        PaymentService paymentService = new PaymentService();

        Payment payment = new Payment();
        payment.setOrderId(1);
        payment.setMethod("card");
        payment.setAmount(1000.0);
        payment.setStatus("pending");

        boolean result = paymentService.processPayment(payment);
        System.out.println("Résultat paiement : " + result);
    }
}