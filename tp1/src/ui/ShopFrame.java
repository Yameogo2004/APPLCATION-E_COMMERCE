package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ShopFrame extends JFrame {

    private final ClientSocketService clientService;
    private final AppSession session;

    private JTextField searchField;
    private JPanel productsContainer;
    private JLabel cartCountLabel;
    private JLabel resultLabel;

    private final List<String[]> allProducts = new ArrayList<>();
    private String selectedCategory = "Toutes";

    public ShopFrame(ClientSocketService clientService, AppSession session) {
        this.clientService = clientService;
        this.session = session;
        initUI();
        loadProducts();
        refreshCartCount();
    }

    private void initUI() {
        setTitle("ChriOnline Shop");
        setSize(1220, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topBar = UITheme.createSectionPanel();
        topBar.setLayout(new BorderLayout(10, 10));

        JLabel logo = new JLabel("ChriOnline Shop");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        JLabel welcome = new JLabel("Welcome, client #" + session.getClientId());
        welcome.setForeground(UITheme.TEXT);

        JButton logoutBtn = UITheme.blueButton("Logout");

        rightTop.add(welcome);
        rightTop.add(logoutBtn);

        topBar.add(logo, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        JPanel searchBar = UITheme.createSectionPanel();
        searchBar.setLayout(new BorderLayout(10, 10));

        searchField = UITheme.textField();
        searchField.setPreferredSize(new Dimension(420, 38));
        searchField.setToolTipText("Rechercher un produit");

        JButton searchBtn = UITheme.blueButton("Search");
        JButton categoriesBtn = UITheme.blueButton("Categories");
        JButton resetBtn = UITheme.blueButton("Reset");
        JButton cartBtn = UITheme.primaryButton("View Cart");
        cartCountLabel = new JLabel("Cart (0)");
        cartCountLabel.setForeground(Color.WHITE);
        cartCountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel rightSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightSearch.setOpaque(false);
        rightSearch.add(searchBtn);
        rightSearch.add(categoriesBtn);
        rightSearch.add(resetBtn);
        rightSearch.add(cartCountLabel);
        rightSearch.add(cartBtn);

        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(rightSearch, BorderLayout.EAST);

        resultLabel = UITheme.mutedLabel("Tous les produits");
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        resultPanel.setOpaque(false);
        resultPanel.add(resultLabel);

        productsContainer = new JPanel(new GridLayout(0, 3, 12, 12));
        productsContainer.setBackground(UITheme.BG);

        JScrollPane scrollPane = new JScrollPane(productsContainer);
        scrollPane.getViewport().setBackground(UITheme.BG);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(searchBar, BorderLayout.NORTH);
        center.add(resultPanel, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);
        content.add(center, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        root.add(topBar, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);

        ActionListener searchAction = e -> applyFilters();

        searchBtn.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        categoriesBtn.addActionListener(e -> openCategories());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            selectedCategory = "Toutes";
            applyFilters();
        });

        cartBtn.addActionListener(e -> {
            new CartFrame(clientService, session, this).setVisible(true);
            setVisible(false);
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(clientService).setVisible(true);
        });
    }

    private void loadProducts() {
        allProducts.clear();
        productsContainer.removeAll();

        String response = clientService.getProducts();

        if (response == null || response.startsWith("ERROR") || "NO_PRODUCTS".equals(response)) {
            JOptionPane.showMessageDialog(this, "Aucun produit.");
            return;
        }

        String[] products = response.split("\\|");
        for (String product : products) {
            String[] fields = product.split(";");
            if (fields.length >= 6) {
                allProducts.add(fields);
            }
        }

        applyFilters();
    }

    private void renderProducts(List<String[]> list) {
        productsContainer.removeAll();

        for (String[] p : list) {
            int id = Integer.parseInt(p[0]);
            String name = p[1];
            double price = Double.parseDouble(p[2]);
            String image = p[3];
            String category = p[4];
            int stock = Integer.parseInt(p[5]);

            ProductCard card = new ProductCard(
                    id,
                    name,
                    price,
                    image,
                    category,
                    stock,
                    clientService,
                    session,
                    this::refreshCartCount,
                    () -> openProductDetails(id)
            );

            productsContainer.add(card);
        }

        productsContainer.revalidate();
        productsContainer.repaint();

        resultLabel.setText("Résultats : " + list.size() + " produit(s) | Catégorie : " + selectedCategory);
    }

    private void applyFilters() {
        String key = searchField.getText().trim().toLowerCase();
        List<String[]> filtered = new ArrayList<>();

        for (String[] p : allProducts) {
            String name = p[1].toLowerCase();
            String category = p[4].toLowerCase();

            boolean matchesSearch = key.isEmpty() || name.contains(key) || category.contains(key);
            boolean matchesCategory = selectedCategory.equals("Toutes")
                    || p[4].equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory) {
                filtered.add(p);
            }
        }

        renderProducts(filtered);

        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun produit trouvé.");
        }
    }

    private void openProductDetails(int productId) {
        ProductDetailsFrame detailsFrame =
                new ProductDetailsFrame(clientService, session, productId, this::refreshCartCount);
        detailsFrame.setVisible(true);
    }

    private void openCategories() {
        Set<String> categorySet = new LinkedHashSet<>();
        for (String[] p : allProducts) {
            if (p.length >= 5) {
                categorySet.add(p[4]);
            }
        }

        List<String> categories = new ArrayList<>(categorySet);

        CategoriesFrame frame = new CategoriesFrame(categories, selected -> {
            selectedCategory = selected;
            applyFilters();
        });

        frame.setVisible(true);
    }

    private void refreshCartCount() {
        String response = clientService.getCart(session.getClientId());

        if ("CART_EMPTY".equals(response) || response == null || response.startsWith("ERROR")) {
            cartCountLabel.setText("Cart (0)");
            return;
        }

        String[] parts = response.split("\\|");
        for (String part : parts) {
            if (part.startsWith("Items=")) {
                String count = part.substring("Items=".length());
                cartCountLabel.setText("Cart (" + count + ")");
                return;
            }
        }

        cartCountLabel.setText("Cart (0)");
    }
}