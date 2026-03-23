package ui;

import Client.ClientSocketService;

import javax.swing.*;
import java.awt.*;

public class AddCategoryFrame extends JFrame {

    private final ClientSocketService clientService;
    private final ManageCategoriesFrame parent;
    private final ShopFrame shopFrame;

    private JTextField nameField;
    private JTextArea descriptionArea;

    public AddCategoryFrame(ClientSocketService clientService, ManageCategoriesFrame parent, ShopFrame shopFrame) {
        this.clientService = clientService;
        this.parent = parent;
        this.shopFrame = shopFrame;
        initUI();
    }

    private void initUI() {
        setTitle("Add Category");
        setSize(450, 320);
        setLocationRelativeTo(null);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setOpaque(false);

        nameField = UITheme.textField();
        descriptionArea = new JTextArea(5, 20);

        form.add(createFieldPanel("Nom", nameField));
        form.add(createFieldPanel("Description", new JScrollPane(descriptionArea)));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton saveBtn = UITheme.primaryButton("Save");
        JButton closeBtn = UITheme.blueButton("Close");

        actions.add(saveBtn);
        actions.add(closeBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);

        saveBtn.addActionListener(e -> saveCategory());
        closeBtn.addActionListener(e -> dispose());
    }

    private JPanel createFieldPanel(String label, Component field) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(Color.WHITE);

        panel.add(jLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void saveCategory() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom obligatoire.");
            return;
        }

        String response = clientService.adminAddCategory(name, description);

        if ("ADMIN_ADD_CATEGORY_SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, "Catégorie ajoutée.");
            parent.loadCategories();
            if (shopFrame != null) shopFrame.reloadProducts();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur : " + response);
        }
    }
}