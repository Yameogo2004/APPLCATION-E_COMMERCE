package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    private final ClientSocketService clientService;
    private final AppSession session;
    private final ShopFrame parentShopFrame;

    public AdminDashboardFrame(ClientSocketService clientService, AppSession session, ShopFrame parentShopFrame) {
        this.clientService = clientService;
        this.session = session;
        this.parentShopFrame = parentShopFrame;
        initUI();
    }

    private void initUI() {
        setTitle("Admin Dashboard");
        setSize(500, 420);
        setLocationRelativeTo(null);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new GridLayout(5, 1, 12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton productsBtn = UITheme.primaryButton("Manage Products");
        JButton categoriesBtn = UITheme.blueButton("Manage Categories");
        JButton usersBtn = UITheme.blueButton("Manage Users");
        JButton ordersBtn = UITheme.blueButton("Manage Orders");
        JButton closeBtn = UITheme.blueButton("Close");

        root.add(productsBtn);
        root.add(categoriesBtn);
        root.add(usersBtn);
        root.add(ordersBtn);
        root.add(closeBtn);

        setContentPane(root);

        productsBtn.addActionListener(e -> new ManageProductsFrame(clientService, parentShopFrame).setVisible(true));
        categoriesBtn.addActionListener(e -> new ManageCategoriesFrame(clientService, parentShopFrame).setVisible(true));
        usersBtn.addActionListener(e -> new ManageUsersFrame(clientService).setVisible(true));
        ordersBtn.addActionListener(e -> new ManageOrdersFrame(clientService).setVisible(true));
        closeBtn.addActionListener(e -> dispose());
    }
}