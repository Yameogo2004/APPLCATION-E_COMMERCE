package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final ClientSocketService clientService;
    private JTextField emailField;
    private JPanel passwordPanel;
    private JLabel statusLabel;
    private JLabel title;
    private JLabel subtitle;
    private JButton loginBtn;
    private JButton registerBtn;
    private JButton languageBtn;
    private JPanel card;

    public LoginFrame(ClientSocketService clientService) {
        this.clientService = clientService;
        initUI();
    }

    private void initUI() {
        setTitle(LanguageManager.getInstance().getText("login.title"));
        setSize(800, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new GridBagLayout());

        card = UITheme.cardPanel();
        card.setPreferredSize(new Dimension(480, 550));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(32, 42, 32, 42)
        ));

        JLabel icon = new JLabel("🛍️");
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 58));
        icon.setForeground(UITheme.GOLD);

        title = new JLabel(LanguageManager.getInstance().getText("login.title"));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));

        subtitle = new JLabel(LanguageManager.getInstance().getText("login.subtitle"));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(UITheme.MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        emailField = createStyledTextField(LanguageManager.getInstance().getText("login.email"));

        passwordPanel = UITheme.createPasswordFieldWithEye(LanguageManager.getInstance().getText("login.password"));
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordPanel.setMaximumSize(new Dimension(340, 60));
        passwordPanel.setPreferredSize(new Dimension(340, 60));

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(UITheme.RED);
        statusLabel.setFont(UITheme.smallFont());

        loginBtn = UITheme.primaryButton("🔐 " + LanguageManager.getInstance().getText("login.button"));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(340, 48));
        loginBtn.setPreferredSize(new Dimension(340, 48));

        registerBtn = UITheme.blueButton("📝 " + LanguageManager.getInstance().getText("login.register"));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(340, 48));
        registerBtn.setPreferredSize(new Dimension(340, 48));

        languageBtn = new JButton(LanguageManager.getCurrentLanguage().getFlag() + " " +
                LanguageManager.getCurrentLanguage().getDisplayName());
        languageBtn.setBackground(UITheme.CARD);
        languageBtn.setForeground(Color.WHITE);
        languageBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        languageBtn.setFocusPainted(false);
        languageBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        languageBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        languageBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        languageBtn.setMaximumSize(new Dimension(170, 35));

        languageBtn.addActionListener(e -> {
            JPopupMenu langMenu = new JPopupMenu();
            for (LanguageManager.Language lang : LanguageManager.Language.values()) {
                JMenuItem item = new JMenuItem(lang.getFlag() + " " + lang.getDisplayName());
                item.addActionListener(ev -> {
                    LanguageManager.setLanguage(lang);
                    refreshUI();
                });
                langMenu.add(item);
            }
            langMenu.show(languageBtn, 0, languageBtn.getHeight());
        });

        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(passwordPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(languageBtn);

        root.add(card);
        setContentPane(root);

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> {
            setVisible(false);
            new RegisterFrame(clientService, this).setVisible(true);
        });
    }

    private JTextField createStyledTextField(String title) {
        JTextField field = UITheme.textField();
        field.setMaximumSize(new Dimension(340, 52));
        field.setPreferredSize(new Dimension(340, 52));
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                title
        ));
        return field;
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        JPasswordField passwordField = UITheme.getPasswordFieldFromPanel(passwordPanel);
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText(LanguageManager.getInstance().getText("login.error.empty"));
            return;
        }

        if (!clientService.connect()) {
            statusLabel.setText(LanguageManager.getInstance().getText("login.error.server"));
            return;
        }

        String response = clientService.login(email, password);

        if (response != null && response.startsWith("LOGIN_SUCCESS")) {
            try {
                String[] parts = response.split(":");

                AppSession session = new AppSession();
                session.setClientId(Integer.parseInt(parts[1]));
                session.setRole(parts[2]);

                dispose();
                new ShopFrame(clientService, session).setVisible(true);

            } catch (Exception e) {
                statusLabel.setText(LanguageManager.getInstance().getText("login.error.invalid"));
            }
        } else {
            statusLabel.setText(LanguageManager.getInstance().getText("login.error.invalid"));
        }
    }

    private void refreshUI() {
        setTitle(LanguageManager.getInstance().getText("login.title"));
        title.setText(LanguageManager.getInstance().getText("login.title"));
        subtitle.setText(LanguageManager.getInstance().getText("login.subtitle"));
        languageBtn.setText(LanguageManager.getCurrentLanguage().getFlag() + " " +
                LanguageManager.getCurrentLanguage().getDisplayName());

        emailField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                LanguageManager.getInstance().getText("login.email")
        ));

        JPanel newPasswordPanel = UITheme.createPasswordFieldWithEye(LanguageManager.getInstance().getText("login.password"));
        newPasswordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPasswordPanel.setMaximumSize(new Dimension(340, 60));
        newPasswordPanel.setPreferredSize(new Dimension(340, 60));

        int index = -1;
        for (int i = 0; i < card.getComponentCount(); i++) {
            if (card.getComponent(i) == passwordPanel) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            card.remove(index);
            passwordPanel = newPasswordPanel;
            card.add(passwordPanel, index);
        }

        loginBtn.setText("🔐 " + LanguageManager.getInstance().getText("login.button"));
        registerBtn.setText("📝 " + LanguageManager.getInstance().getText("login.register"));

        card.revalidate();
        card.repaint();
    }
}