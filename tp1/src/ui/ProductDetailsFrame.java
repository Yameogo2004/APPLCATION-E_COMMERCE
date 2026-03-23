package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import java.awt.*;

public class ProductDetailsFrame extends JFrame {

    public ProductDetailsFrame(ClientSocketService clientService, AppSession session, int productId, Runnable onCartChanged) {
        setTitle("Détails du produit");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String response = clientService.getProduct(productId);

        if (response == null || response.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "Impossible de charger les détails du produit.");
            dispose();
            return;
        }

        String[] parts = response.split(";");
        if (parts.length < 7) {
            JOptionPane.showMessageDialog(this, "Format détail produit invalide.");
            dispose();
            return;
        }

        String name = parts[1];
        double price = Double.parseDouble(parts[2]);
        String description = parts[3];
        int stock = Integer.parseInt(parts[4]);
        String image = parts[5];
        String category = parts[6];

        JPanel content = UITheme.cardPanel();
        content.setLayout(new BorderLayout(20, 20));

        JLabel imageLabel = new JLabel(UITheme.loadProductImage(image, 260, 260));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel categoryLabel = UITheme.mutedLabel("Catégorie : " + category);
        JLabel stockLabel = UITheme.mutedLabel("Stock : " + stock);

        JLabel priceLabel = new JLabel(String.format("%.2f DH", price));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(UITheme.CARD_2);
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setFont(UITheme.normalFont());
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(280, 150));

        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottomButtons.setOpaque(false);

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Math.max(stock, 1), 1));

        JButton addCartBtn = UITheme.primaryButton("Ajouter au panier");
        JButton closeBtn = UITheme.blueButton("Fermer");

        addCartBtn.addActionListener(e -> {
            int qty = (Integer) quantitySpinner.getValue();
            String addResponse = clientService.addToCart(session.getClientId(), productId, qty);

            if ("CART_ADD_SUCCESS".equals(addResponse)) {
                JOptionPane.showMessageDialog(this, "Produit ajouté au panier.");
                if (onCartChanged != null) onCartChanged.run();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : " + addResponse);
            }
        });

        closeBtn.addActionListener(e -> dispose());

        bottomButtons.add(UITheme.label("Quantité :"));
        bottomButtons.add(quantitySpinner);
        bottomButtons.add(addCartBtn);
        bottomButtons.add(closeBtn);

        info.add(nameLabel);
        info.add(Box.createVerticalStrut(10));
        info.add(categoryLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(stockLabel);
        info.add(Box.createVerticalStrut(14));
        info.add(priceLabel);
        info.add(Box.createVerticalStrut(14));
        info.add(descriptionScroll);
        info.add(Box.createVerticalStrut(14));
        info.add(bottomButtons);

        content.add(imageLabel, BorderLayout.WEST);
        content.add(info, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }
}