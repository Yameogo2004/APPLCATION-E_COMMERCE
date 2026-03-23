package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShopFrame extends LanguageAwareFrame {

    private final ClientSocketService clientService;
    private final AppSession session;

    private JTextField searchField;
    private JPanel productsContainer;
    private JLabel cartCountLabel;
    private JLabel welcomeLabel;
    private JPanel categoriesPanel;
    private JScrollPane productsScrollPane;
    private JButton categoriesBtn;
    private JButton cartBtn;
    private JButton logoutBtn;
    private JButton languageBtn;
    private JLabel logo;
    private JLabel resultLabel;
    private JButton searchBtn;

    private final List<String[]> allProducts = new ArrayList<>();
    private String currentCategory = "Tous";

    public ShopFrame(ClientSocketService clientService, AppSession session) {
        this.clientService = clientService;
        this.session = session;
        initUI();
        loadProducts();
        refreshCartCount();
    }

    private void initUI() {
        setTitle(LanguageManager.getInstance().getText("shop.title"));
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Appliquer l'orientation pour l'arabe
        applyOrientation();

        // Panel principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG);

        // ========== TOP BAR ==========
        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);

        // ========== CONTENU PRINCIPAL (SplitPane pour sidebar + produits) ==========
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setBackground(UITheme.BG);

        // Sidebar gauche (catégories)
        JPanel sidebar = createSidebar();
        splitPane.setLeftComponent(sidebar);

        // Zone droite (produits)
        JPanel rightArea = createProductsArea();
        splitPane.setRightComponent(rightArea);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }
    
    private void applyOrientation() {
        if (LanguageManager.getCurrentLanguage() == LanguageManager.Language.ARABIC) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        } else {
            setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(new Color(35, 38, 46));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        // Logo
        logo = new JLabel("🏪 ChriOnline");
        logo.setForeground(UITheme.GOLD);
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        // Barre de recherche
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(35, 38, 46));
        searchPanel.setPreferredSize(new Dimension(500, 42));
        
        searchField = new JTextField();
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(Color.BLACK);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        searchBtn = new JButton(LanguageManager.getInstance().getText("shop.search"));
        searchBtn.setBackground(UITheme.GOLD);
        searchBtn.setForeground(new Color(35, 38, 46));
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        
        // Droite
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(new Color(35, 38, 46));
        
        welcomeLabel = new JLabel(LanguageManager.getInstance().getText("shop.welcome") + " #" + session.getClientId());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        cartCountLabel = new JLabel("🛒 (0)");
        cartCountLabel.setForeground(UITheme.GOLD);
        cartCountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Bouton Catégories
        categoriesBtn = new JButton("📂 " + LanguageManager.getInstance().getText("shop.categories"));
        categoriesBtn.setBackground(UITheme.BLUE);
        categoriesBtn.setForeground(Color.WHITE);
        categoriesBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        categoriesBtn.setFocusPainted(false);
        categoriesBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        categoriesBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cartBtn = new JButton("🛒 " + LanguageManager.getInstance().getText("shop.cart"));
        cartBtn.setBackground(UITheme.GREEN);
        cartBtn.setForeground(Color.WHITE);
        cartBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        cartBtn.setFocusPainted(false);
        cartBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        cartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutBtn = new JButton(LanguageManager.getInstance().getText("shop.logout"));
        logoutBtn.setBackground(UITheme.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
     // Ajouter dans la section rightPanel, avant le bouton langue
        JButton profileBtn = new JButton("👤 " + LanguageManager.getInstance().getText("profile.title"));
        profileBtn.setBackground(new Color(35, 38, 46));
        profileBtn.setForeground(Color.WHITE);
        profileBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        profileBtn.setFocusPainted(false);
        profileBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        profileBtn.addActionListener(e -> {
            new ProfileFrame(clientService, session, this).setVisible(true);
            setVisible(false);
        });

        rightPanel.add(profileBtn);
        
        // Bouton langue
        languageBtn = new JButton(LanguageManager.getCurrentLanguage().getFlag() + " " + 
                                   LanguageManager.getCurrentLanguage().getDisplayName());
        languageBtn.setBackground(new Color(35, 38, 46));
        languageBtn.setForeground(Color.WHITE);
        languageBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        languageBtn.setFocusPainted(false);
        languageBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        languageBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        languageBtn.addActionListener(e -> {
            JPopupMenu langMenu = new JPopupMenu();
            for (LanguageManager.Language lang : LanguageManager.Language.values()) {
                JMenuItem item = new JMenuItem(lang.getFlag() + " " + lang.getDisplayName());
                item.addActionListener(ev -> {
                    LanguageManager.setLanguage(lang);
                    languageBtn.setText(lang.getFlag() + " " + lang.getDisplayName());
                });
                langMenu.add(item);
            }
            langMenu.show(languageBtn, 0, languageBtn.getHeight());
        });
        
        rightPanel.add(welcomeLabel);
        rightPanel.add(cartCountLabel);
        rightPanel.add(categoriesBtn);
        rightPanel.add(cartBtn);
        rightPanel.add(languageBtn);
        rightPanel.add(logoutBtn);
        
        topBar.add(logo, BorderLayout.WEST);
        topBar.add(searchPanel, BorderLayout.CENTER);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        // Actions
        searchBtn.addActionListener(e -> filterProducts());
        searchField.addActionListener(e -> filterProducts());
        
        categoriesBtn.addActionListener(e -> {
            Set<String> uniqueCategories = new HashSet<>();
            for (String[] p : allProducts) {
                uniqueCategories.add(p[4]);
            }
            List<String> catList = new ArrayList<>(uniqueCategories);
            
            new CategoriesFrame(catList, category -> {
                currentCategory = category;
                filterProducts();
                refreshCategoryButtons();
            }).setVisible(true);
        });
        
        cartBtn.addActionListener(e -> {
            new CartFrame(clientService, session, this).setVisible(true);
            setVisible(false);
        });
        
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                LanguageManager.getInstance().getText("cart.clear.confirm"), 
                "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame(clientService).setVisible(true);
            }
        });
        
        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UITheme.CARD);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        sidebar.setPreferredSize(new Dimension(280, 0));

        // Titre
        JLabel title = new JLabel(LanguageManager.getInstance().getText("shop.categories").toUpperCase());
        title.setForeground(UITheme.GOLD);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Panel des catégories avec scroll
        categoriesPanel = new JPanel();
        categoriesPanel.setBackground(UITheme.CARD);
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        
        JScrollPane catScroll = new JScrollPane(categoriesPanel);
        catScroll.setBackground(UITheme.CARD);
        catScroll.setBorder(null);
        catScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        catScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        sidebar.add(title, BorderLayout.NORTH);
        sidebar.add(catScroll, BorderLayout.CENTER);
        
        return sidebar;
    }

    private JPanel createProductsArea() {
        JPanel productsArea = new JPanel(new BorderLayout());
        productsArea.setBackground(UITheme.BG);
        productsArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header avec nombre de produits
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        resultLabel = new JLabel(LanguageManager.getInstance().getText("shop.all"));
        resultLabel.setForeground(UITheme.TEXT);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(resultLabel, BorderLayout.WEST);
        
        productsArea.add(header, BorderLayout.NORTH);
        
        // Container des produits
        productsContainer = new JPanel();
        productsContainer.setBackground(UITheme.BG);
        productsContainer.setLayout(new GridLayout(0, 4, 15, 20));
        
        productsScrollPane = new JScrollPane(productsContainer);
        productsScrollPane.setBackground(UITheme.BG);
        productsScrollPane.getViewport().setBackground(UITheme.BG);
        productsScrollPane.setBorder(null);
        productsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        productsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        productsArea.add(productsScrollPane, BorderLayout.CENTER);
        
        return productsArea;
    }

    private void loadProducts() {
        allProducts.clear();

        String response = clientService.getProducts();

        if (response == null || response.startsWith("ERROR") || "NO_PRODUCTS".equals(response)) {
            JOptionPane.showMessageDialog(this, LanguageManager.getInstance().getText("shop.empty"));
            return;
        }

        String[] products = response.split("\\|");
        Set<String> categories = new HashSet<>();
        
        for (String product : products) {
            String[] fields = product.split(";");
            if (fields.length >= 3) {
                String[] fullFields = new String[6];
                fullFields[0] = fields[0]; // id
                fullFields[1] = fields[1]; // name
                fullFields[2] = fields[2]; // price
                fullFields[3] = fields.length > 3 ? fields[3] : "default.png"; // image
                fullFields[4] = fields.length > 4 ? fields[4] : "General"; // category
                fullFields[5] = fields.length > 5 ? fields[5] : "10"; // stock
                allProducts.add(fullFields);
                categories.add(fullFields[4]);
            }
        }

        buildCategoriesPanel(categories);
        renderProducts(allProducts);
    }
    
    private void buildCategoriesPanel(Set<String> categories) {
        categoriesPanel.removeAll();
        
        // Bouton "Tous"
        JButton allBtn = createCategoryButton("🏠 " + LanguageManager.getInstance().getText("shop.all"), "Tous");
        allBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        allBtn.setMaximumSize(new Dimension(250, 40));
        categoriesPanel.add(allBtn);
        categoriesPanel.add(Box.createVerticalStrut(10));
        
        // Séparateur
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(250, 1));
        categoriesPanel.add(sep);
        categoriesPanel.add(Box.createVerticalStrut(10));
        
        // Trier les catégories par ordre alphabétique
        List<String> sortedCategories = new ArrayList<>(categories);
        sortedCategories.sort(String::compareToIgnoreCase);
        
        for (String category : sortedCategories) {
            if (!category.equals("General")) {
                JButton catBtn = createCategoryButton(getCategoryIcon(category) + " " + category, category);
                catBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
                catBtn.setMaximumSize(new Dimension(250, 38));
                categoriesPanel.add(catBtn);
                categoriesPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        categoriesPanel.revalidate();
        categoriesPanel.repaint();
    }
    
    private String getCategoryIcon(String category) {
        String catLower = category.toLowerCase();
        if (catLower.contains("tablette")) return "📱";
        if (catLower.contains("inform") || catLower.contains("informatique")) return "💻";
        if (catLower.contains("audio")) return "🎧";
        if (catLower.contains("gaming")) return "🎮";
        if (catLower.contains("ordi")) return "🖥️";
        if (catLower.contains("access")) return "🔌";
        if (catLower.contains("phone")) return "📱";
        if (catLower.contains("laptop")) return "💻";
        return "📦";
    }
    
    private JButton createCategoryButton(String text, String categoryValue) {
        JButton btn = new JButton(text);
        btn.setBackground(UITheme.CARD);
        btn.setForeground(UITheme.TEXT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        
        if (currentCategory.equals(categoryValue)) {
            btn.setBackground(UITheme.BLUE);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        }
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!currentCategory.equals(categoryValue)) {
                    btn.setBackground(UITheme.CARD_2);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!currentCategory.equals(categoryValue)) {
                    btn.setBackground(UITheme.CARD);
                }
            }
        });
        
        btn.addActionListener(e -> {
            currentCategory = categoryValue;
            filterProducts();
            refreshCategoryButtons();
        });
        
        return btn;
    }
    
    private void refreshCategoryButtons() {
        Component[] components = categoriesPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String btnText = btn.getText();
                String btnCategory = btnText.contains(" ") ? btnText.substring(btnText.indexOf(" ") + 1) : btnText;
                
                if (btnCategory.equals(currentCategory) || 
                    (currentCategory.equals("Tous") && btnText.contains(LanguageManager.getInstance().getText("shop.all")))) {
                    btn.setBackground(UITheme.BLUE);
                    btn.setForeground(Color.WHITE);
                    btn.setFont(new Font("SansSerif", Font.BOLD, 13));
                } else {
                    btn.setBackground(UITheme.CARD);
                    btn.setForeground(UITheme.TEXT);
                    btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
                }
            }
        }
    }

    private void renderProducts(List<String[]> list) {
        productsContainer.removeAll();
        
        if (list.isEmpty()) {
            JLabel emptyLabel = new JLabel("🔍 " + LanguageManager.getInstance().getText("shop.empty"));
            emptyLabel.setForeground(UITheme.MUTED);
            emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            productsContainer.add(emptyLabel);
        } else {
            int columns = getResponsiveColumns();
            productsContainer.setLayout(new GridLayout(0, columns, 15, 20));
            
            for (String[] p : list) {
                int id = Integer.parseInt(p[0]);
                String name = p[1];
                double price = Double.parseDouble(p[2]);
                String image = p[3];
                String category = p[4];
                int stock = Integer.parseInt(p[5]);

                ProductCard card = new ProductCard(
                        id, name, price, image, category, stock,
                        clientService, session, this::refreshCartCount
                );
                productsContainer.add(card);
            }
        }
        
        productsContainer.revalidate();
        productsContainer.repaint();
        productsScrollPane.getVerticalScrollBar().setValue(0);
    }
    
    private int getResponsiveColumns() {
        int width = getWidth();
        if (width > 1600) return 5;
        if (width > 1200) return 4;
        if (width > 900) return 3;
        return 2;
    }

    private void filterProducts() {
        String searchKey = searchField.getText().trim().toLowerCase();

        List<String[]> filtered = new ArrayList<>();
        for (String[] p : allProducts) {
            String name = p[1].toLowerCase();
            String category = p[4];
            
            boolean categoryMatch = currentCategory.equals("Tous") || category.equals(currentCategory);
            boolean searchMatch = searchKey.isEmpty() || name.contains(searchKey);
            
            if (categoryMatch && searchMatch) {
                filtered.add(p);
            }
        }

        renderProducts(filtered);
        resultLabel.setText(filtered.size() + " " + LanguageManager.getInstance().getText("shop.all").toLowerCase());
    }

    public void refreshCartCount() {
        String response = clientService.getCart(session.getClientId());

        if ("CART_EMPTY".equals(response) || response == null || response.startsWith("ERROR")) {
            cartCountLabel.setText("🛒 (0)");
            return;
        }

        String[] parts = response.split("\\|");
        for (String part : parts) {
            if (part.startsWith("Items=")) {
                String count = part.substring("Items=".length());
                cartCountLabel.setText("🛒 (" + count + ")");
                return;
            }
        }

        cartCountLabel.setText("🛒 (0)");
    }
    
    @Override
    public void refreshTexts() {
        setTitle(LanguageManager.getInstance().getText("shop.title"));
        welcomeLabel.setText(LanguageManager.getInstance().getText("shop.welcome") + " #" + session.getClientId());
        categoriesBtn.setText("📂 " + LanguageManager.getInstance().getText("shop.categories"));
        cartBtn.setText("🛒 " + LanguageManager.getInstance().getText("shop.cart"));
        logoutBtn.setText(LanguageManager.getInstance().getText("shop.logout"));
        searchBtn.setText(LanguageManager.getInstance().getText("shop.search"));
        resultLabel.setText(LanguageManager.getInstance().getText("shop.all"));
        
        // Appliquer l'orientation
        applyOrientation();
        
        // Rafraîchir les catégories
        Set<String> categories = new HashSet<>();
        for (String[] p : allProducts) {
            categories.add(p[4]);
        }
        buildCategoriesPanel(categories);
        
        // Rafraîchir les produits (les cartes se mettront à jour via leur propre refresh)
        renderProducts(allProducts);
        
     // Après renderProducts(allProducts); ajoutez cette ligne pour mettre à jour les cartes existantes
        for (Component comp : productsContainer.getComponents()) {
            if (comp instanceof ProductCard) {
                ((ProductCard) comp).refreshTexts();
            }
        }
        
        revalidate();
        repaint();
    }
}