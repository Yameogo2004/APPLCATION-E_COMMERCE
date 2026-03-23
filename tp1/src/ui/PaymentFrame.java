package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {

    private final ClientSocketService clientService;
    private final AppSession session;
    private final JFrame backHome;

    private JComboBox<String> methods;
    private JLabel statusLabel;

    public PaymentFrame(ClientSocketService clientService, AppSession session, JFrame backHome) {
        this.clientService = clientService;
        this.session = session;
        this.backHome = backHome;
        initUI();
    }

    private void initUI() {
        setTitle("Payment");
        setSize(650, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new GridBagLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UITheme.cardPanel();
        card.setPreferredSize(new Dimension(500, 300));
        card.setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("Secure Payment", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.titleFont());

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new GridLayout(4, 1, 8, 8));

        JLabel orderLbl = new JLabel("Order UUID : " + safe(session.getOrderUUID()));
        orderLbl.setForeground(Color.WHITE);
        orderLbl.setFont(UITheme.normalFont());

        JLabel totalLbl = new JLabel(String.format("Total : %.2f DH", session.getLastOrderTotal()));
        totalLbl.setForeground(Color.WHITE);
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel methodLbl = new JLabel("Payment method:");
        methodLbl.setForeground(Color.WHITE);
        methodLbl.setFont(UITheme.normalFont());

        methods = new JComboBox<>(new String[]{"card", "especes"});
        methods.setBackground(UITheme.CARD_2);
        methods.setForeground(Color.BLACK);
        methods.setFont(UITheme.normalFont());

        infoPanel.add(orderLbl);
        infoPanel.add(totalLbl);
        infoPanel.add(methodLbl);
        infoPanel.add(methods);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(255, 120, 120));
        statusLabel.setFont(UITheme.smallFont());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setOpaque(false);

        JButton payBtn = UITheme.primaryButton("Confirm Payment");
        JButton homeBtn = UITheme.blueButton("Back to Shop");

        buttonPanel.add(payBtn);
        buttonPanel.add(homeBtn);

        card.add(title, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setOpaque(false);
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        card.add(southPanel, BorderLayout.SOUTH);

        root.add(card);
        setContentPane(root);

        payBtn.addActionListener(e -> handlePayment());
        homeBtn.addActionListener(e -> {
            dispose();
            backHome.setVisible(true);
        });
    }

    private void handlePayment() {
        if (session.getOrderUUID() == null || session.getOrderUUID().isBlank()) {
            statusLabel.setText("Aucune commande à payer.");
            return;
        }

        String method = methods.getSelectedItem().toString();
        String response = clientService.pay(session.getOrderUUID(), method);

        if (response == null) {
            statusLabel.setText("Aucune réponse du serveur.");
            return;
        }

        if (response.startsWith("PAYMENT_SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Paiement réussi.");
            session.clearOrderData();
            dispose();
            backHome.setVisible(true);
        } else {
            statusLabel.setText("Échec paiement : " + response);
            JOptionPane.showMessageDialog(this, "Échec paiement : " + response);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}