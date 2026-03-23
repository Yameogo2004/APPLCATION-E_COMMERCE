package ui;

import Client.AppSession;
import Client.ClientSocketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final ClientSocketService clientService;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame(ClientSocketService clientService) {
        this.clientService = clientService;
        initUI();
    }

    private void initUI() {
        setTitle("ChriOnline - Login");
        setSize(760, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new GridBagLayout());

        JPanel card = UITheme.cardPanel();
        card.setPreferredSize(new Dimension(460, 430));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(24, 36, 24, 36)
        ));

        JLabel icon = new JLabel("🛒");
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 38));
        icon.setForeground(Color.WHITE);

        JLabel title = new JLabel("Login");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.titleFont());

        emailField = createStyledTextField("Email");
        passwordField = createStyledPasswordField("Mot de passe");

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(new Color(255, 120, 120));
        statusLabel.setFont(UITheme.smallFont());

        JButton loginBtn = UITheme.primaryButton("Login");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(340, 48));
        loginBtn.setPreferredSize(new Dimension(340, 48));

        JButton registerBtn = UITheme.blueButton("Create Account");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(340, 48));
        registerBtn.setPreferredSize(new Dimension(340, 48));

        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(22));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(registerBtn);

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
        field.setBackground(new Color(58, 62, 74));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                title
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField(String title) {
        JPasswordField field = UITheme.passwordField();
        field.setMaximumSize(new Dimension(340, 52));
        field.setPreferredSize(new Dimension(340, 52));
        field.setBackground(new Color(58, 62, 74));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                title
        ));
        return field;
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!clientService.connect()) {
            statusLabel.setText("Serveur inaccessible.");
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
                statusLabel.setText("Réponse serveur invalide.");
            }
        } else {
            statusLabel.setText("Email ou mot de passe incorrect.");
        }
    }
}