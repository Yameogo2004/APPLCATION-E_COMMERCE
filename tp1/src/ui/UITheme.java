package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UITheme {

    public static final Color BG = new Color(34, 37, 46);
    public static final Color CARD = new Color(45, 49, 60);
    public static final Color CARD_2 = new Color(53, 58, 71);
    public static final Color GREEN = new Color(89, 168, 79);
    public static final Color BLUE = new Color(67, 139, 208);
    public static final Color RED = new Color(197, 76, 76);
    public static final Color TEXT = new Color(240, 240, 240);
    public static final Color MUTED = new Color(180, 180, 180);
    public static final Color BORDER = new Color(70, 74, 84);
    public static final Color INPUT_BG = new Color(58, 62, 74);

    public static Font titleFont() {
        return new Font("SansSerif", Font.BOLD, 28);
    }

    public static Font subtitleFont() {
        return new Font("SansSerif", Font.BOLD, 18);
    }

    public static Font normalFont() {
        return new Font("SansSerif", Font.PLAIN, 14);
    }

    public static Font smallFont() {
        return new Font("SansSerif", Font.PLAIN, 12);
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton blueButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(BLUE);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(RED);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(normalFont());
        return field;
    }

    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(normalFont());
        return field;
    }

    public static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        return p;
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        return p;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT);
        l.setFont(normalFont());
        return l;
    }

    public static JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(smallFont());
        return l;
    }

    public static JPanel createSectionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return panel;
    }

    public static ImageIcon loadProductImage(String path, int width, int height) {
        try {
            if (path == null || path.trim().isEmpty()) {
                path = "image/default.jpg";
            }

            java.io.File file = new java.io.File(path);

            if (!file.exists()) {
                file = new java.io.File("image/default.jpg");
            }

            if (!file.exists()) {
                return new ImageIcon();
            }

            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);

        } catch (Exception e) {
            e.printStackTrace();
            return new ImageIcon();
        }
    }
}