package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class CategoriesFrame extends JFrame {

    public CategoriesFrame(List<String> categories, Consumer<String> onCategorySelected) {
        setTitle("Catégories");
        setSize(350, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = UITheme.darkPanel();
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Catégories", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.titleFont());

        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Toutes");
        for (String c : categories) {
            model.addElement(c);
        }

        JList<String> list = new JList<>(model);
        list.setBackground(UITheme.CARD_2);
        list.setForeground(Color.WHITE);
        list.setSelectionBackground(UITheme.BLUE);
        list.setFont(UITheme.normalFont());

        JScrollPane scrollPane = new JScrollPane(list);

        JButton chooseBtn = UITheme.primaryButton("Choisir");
        JButton closeBtn = UITheme.blueButton("Fermer");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottom.setOpaque(false);
        bottom.add(chooseBtn);
        bottom.add(closeBtn);

        chooseBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                if (onCategorySelected != null) {
                    onCategorySelected.accept(selected);
                }
                dispose();
            }
        });

        closeBtn.addActionListener(e -> dispose());

        root.add(title, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }
}