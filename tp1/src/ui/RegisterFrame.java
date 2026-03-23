package ui;

import Client.ClientSocketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final ClientSocketService clientService;
    private final JFrame backFrame;

    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField villeField;
    private JLabel statusLabel;

    public RegisterFrame(ClientSocketService clientService, JFrame backFrame) {
        this.clientService = clientService;
        this.backFrame = backFrame;
        initUI();
    }

    private void initUI() {
        setTitle("ChriOnline - Register");
        setSize(760, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new GridBagLayout());

        JPanel card = UITheme.cardPanel();
        card.setPreferredSize(new Dimension(460, 560));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(22, 35, 22, 35)
        ));

        JLabel title = new JLabel("Create Account");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.titleFont());

        JLabel subtitle = new JLabel("Inscription client");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(UITheme.MUTED);
        subtitle.setFont(UITheme.normalFont());

        nomField = createStyledTextField("Nom");
        prenomField = createStyledTextField("Prénom");
        emailField = createStyledTextField("Email");
        passwordField = createStyledPasswordField("Mot de passe");
        addressField = createStyledTextField("Adresse");
        phoneField = createStyledTextField("Téléphone");
        villeField = createStyledTextField("Ville");

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(new Color(255, 120, 120));
        statusLabel.setFont(UITheme.smallFont());

        JButton registerBtn = UITheme.primaryButton("Register");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(340, 45));
        registerBtn.setPreferredSize(new Dimension(340, 45));

        JButton backBtn = UITheme.blueButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(340, 45));
        backBtn.setPreferredSize(new Dimension(340, 45));

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(nomField);
        card.add(Box.createVerticalStrut(12));
        card.add(prenomField);
        card.add(Box.createVerticalStrut(12));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(12));
        card.add(addressField);
        card.add(Box.createVerticalStrut(12));
        card.add(phoneField);
        card.add(Box.createVerticalStrut(12));
        card.add(villeField);
        card.add(Box.createVerticalStrut(12));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(backBtn);

        root.add(card);
        setContentPane(root);

        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            backFrame.setVisible(true);
            dispose();
        });
    }

    private JTextField createStyledTextField(String title) {
        JTextField field = UITheme.textField();
        field.setMaximumSize(new Dimension(340, 48));
        field.setPreferredSize(new Dimension(340, 48));
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
        field.setMaximumSize(new Dimension(340, 48));
        field.setPreferredSize(new Dimension(340, 48));
        field.setBackground(new Color(58, 62, 74));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                title
        ));
        return field;
    }

    private void register() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String ville = villeField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()
                || address.isEmpty() || phone.isEmpty() || ville.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!clientService.connect()) {
            statusLabel.setText("Serveur inaccessible.");
            return;
        }

        String response = clientService.register(
                nom,
                prenom,
                email,
                password,
                address,
                phone,
                ville
        );

        if ("REGISTER_SUCCESS".equals(response)) {
            // Au lieu de montrer directement "Inscription réussie", ouvrir la fenêtre OTP
            OtpFrame otpFrame = new OtpFrame(clientService, email, this);
            otpFrame.setVisible(true);
            // Optionnel : masquer le RegisterFrame pendant la saisie OTP
            this.setVisible(false);
        } else {
            statusLabel.setText("Erreur : " + response);
        }
    }
}