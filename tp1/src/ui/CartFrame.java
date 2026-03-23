package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CartFrame extends JFrame {

    private final ClientSocketService clientService;
    private final AppSession session;
    private final JFrame backFrame;

    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel;

    public CartFrame(ClientSocketService clientService, AppSession session, JFrame backFrame) {
        this.clientService = clientService;
        this.session = session;
        this.backFrame = backFrame;
        initUI();
        loadCart();
    }

    private void initUI() {
        setTitle("Shopping Cart");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Shopping Cart");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel top = UITheme.cardPanel();
        top.setLayout(new BorderLayout());
        top.add(title, BorderLayout.WEST);

        model = new DefaultTableModel(new Object[]{"ID", "Produit", "Quantité", "Sous-total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setBackground(UITheme.CARD_2);
        table.setForeground(Color.WHITE);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(UITheme.CARD);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(UITheme.BLUE);
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UITheme.CARD_2);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JPanel bottom = UITheme.cardPanel();
        bottom.setLayout(new BorderLayout());

        totalLabel = new JLabel("Total: 0.00 DH");
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);

        JButton removeBtn = UITheme.blueButton("Remove Selected");
        JButton checkoutBtn = UITheme.primaryButton("Checkout");
        JButton clearBtn = UITheme.dangerButton("Clear Cart");
        JButton backBtn = UITheme.blueButton("Back");

        buttons.add(backBtn);
        buttons.add(removeBtn);
        buttons.add(checkoutBtn);
        buttons.add(clearBtn);

        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);

        clearBtn.addActionListener(e -> {
            String res = clientService.clearCart(session.getClientId());
            if ("CART_CLEAR_SUCCESS".equals(res)) {
                loadCart();
            } else {
                JOptionPane.showMessageDialog(this, res);
            }
        });

        removeBtn.addActionListener(e -> removeSelectedProduct());

        backBtn.addActionListener(e -> {
            backFrame.setVisible(true);
            dispose();
        });

        checkoutBtn.addActionListener(e -> checkout());
    }

    private void loadCart() {
        model.setRowCount(0);

        String response = clientService.getCart(session.getClientId());

        if ("CART_EMPTY".equals(response)) {
            totalLabel.setText("Total: 0.00 DH");
            return;
        }

        if (response == null || response.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, response);
            return;
        }

        String total = "0.00";
        String[] parts = response.split("\\|");

        for (String part : parts) {
            if (part.startsWith("Total=")) {
                total = part.substring("Total=".length());
            } else if (part.startsWith("ProductId=")) {
                String productId = extract(part, "ProductId=", ",Product=");
                String productName = extract(part, ",Product=", ",Qty=");
                String qty = extract(part, ",Qty=", ",Subtotal=");
                String subtotal = part.substring(part.indexOf(",Subtotal=") + 10);

                model.addRow(new Object[]{productId, productName, qty, subtotal});
            }
        }

        totalLabel.setText("Total: " + total + " DH");
    }

    private String extract(String source, String start, String end) {
        int s = source.indexOf(start);
        int e = source.indexOf(end);

        if (s == -1 || e == -1 || e <= s) {
            return "";
        }

        return source.substring(s + start.length(), e);
    }

    private void removeSelectedProduct() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un produit à supprimer.");
            return;
        }

        try {
            int productId = Integer.parseInt(model.getValueAt(row, 0).toString());
            String response = clientService.removeFromCart(session.getClientId(), productId);

            if ("CART_REMOVE_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Produit supprimé du panier.");
                loadCart();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : " + response);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Impossible de supprimer ce produit.");
        }
    }

    private void checkout() {
        String response = clientService.checkout(session.getClientId());

        if (response == null || response.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, response);
            return;
        }

        String[] parts = response.split(";");
        if (parts.length == 3 && "ORDER_CREATED".equals(parts[0])) {
            session.setOrderUUID(parts[1]);
            session.setLastOrderTotal(Double.parseDouble(parts[2]));

            dispose();
            new PaymentFrame(clientService, session, backFrame).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Erreur checkout.");
        }
    }
}