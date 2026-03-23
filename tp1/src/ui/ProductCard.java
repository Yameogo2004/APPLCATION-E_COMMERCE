package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProductCard extends JPanel {

    private final int productId;
    private final String productName;
    private final double price;
    private final String imagePath;
    private final String category;
    private final int stock;

    private int quantity = 1;

    public ProductCard(int productId, String productName, double price, String imagePath,
                       String category, int stock,
                       ClientSocketService clientService,
                       AppSession session,
                       Runnable onCartChanged,
                       Runnable onShowDetails) {

        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imagePath = imagePath;
        this.category = category;
        this.stock = stock;

        setLayout(new BorderLayout(12, 10));
        setBackground(UITheme.CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setPreferredSize(new Dimension(320, 155));

        JLabel imageLabel = new JLabel(UITheme.loadProductImage(imagePath, 95, 95));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(productName);
        nameLabel.setForeground(UITheme.TEXT);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel categoryLabel = UITheme.mutedLabel(category);
        JLabel stockLabel = UITheme.mutedLabel("Stock : " + stock);

        JLabel priceLabel = new JLabel(String.format("%.2f DH", price));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        qtyPanel.setOpaque(false);

        JButton minusBtn = new JButton("-");
        JButton plusBtn = new JButton("+");
        JButton addBtn = new JButton("🛒");
        JButton detailBtn = new JButton("Details");

        styleSmallButton(minusBtn, UITheme.CARD_2);
        styleSmallButton(plusBtn, UITheme.CARD_2);
        styleSmallButton(addBtn, UITheme.BLUE);
        styleSmallButton(detailBtn, UITheme.GREEN);

        JTextField qtyField = new JTextField("1", 2);
        qtyField.setHorizontalAlignment(JTextField.CENTER);
        qtyField.setEditable(false);
        qtyField.setBackground(new Color(70, 74, 84));
        qtyField.setForeground(Color.WHITE);
        qtyField.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        qtyField.setPreferredSize(new Dimension(36, 28));

        minusBtn.addActionListener(e -> {
            if (quantity > 1) {
                quantity--;
                qtyField.setText(String.valueOf(quantity));
            }
        });

        plusBtn.addActionListener(e -> {
            if (quantity < stock) {
                quantity++;
                qtyField.setText(String.valueOf(quantity));
            }
        });

        addBtn.addActionListener(e -> {
            String response = clientService.addToCart(session.getClientId(), productId, quantity);

            if ("CART_ADD_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, productName + " ajouté au panier.");
                if (onCartChanged != null) onCartChanged.run();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : " + response);
            }
        });

        detailBtn.addActionListener(e -> {
            if (onShowDetails != null) onShowDetails.run();
        });

        MouseAdapter detailsClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onShowDetails != null) onShowDetails.run();
            }
        };

        imageLabel.addMouseListener(detailsClick);
        nameLabel.addMouseListener(detailsClick);

        qtyPanel.add(minusBtn);
        qtyPanel.add(qtyField);
        qtyPanel.add(plusBtn);
        qtyPanel.add(addBtn);
        qtyPanel.add(detailBtn);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(stockLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(qtyPanel);

        add(imageLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
    }

    private void styleSmallButton(JButton button, Color bg) {
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}