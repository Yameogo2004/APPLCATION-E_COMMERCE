package ui;

import Client.ClientSocketService;

import javax.swing.*;

public class MainUI {

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ClientSocketService clientService = new ClientSocketService();
            new LoginFrame(clientService).setVisible(true);
        });
    }
}