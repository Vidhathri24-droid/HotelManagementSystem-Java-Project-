package gui;

import javax.swing.*;
import java.awt.*;
import service.AuthService;

public class LoginPage extends JFrame {
    public LoginPage() {
        setTitle("Login");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(236, 239, 241));
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 33, 33));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"admin", "customer"});
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(roleLabel, gbc);
        gbc.gridx = 1; formPanel.add(roleBox, gbc);
        gbc.gridx = 0; gbc.gridy++; formPanel.add(userLabel, gbc);
        gbc.gridx = 1; formPanel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy++; formPanel.add(passLabel, gbc);
        gbc.gridx = 1; formPanel.add(passField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(236, 239, 241));

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");
        styleButton(loginBtn);
        styleButton(backBtn);

        buttonPanel.add(loginBtn);
        buttonPanel.add(backBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            String role = roleBox.getSelectedItem().toString();
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (AuthService.login(username, password, role)) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                if ("admin".equals(role)) {
                    new AdminDashboard();
                } else {
                    new CustomerDashboard();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });

        backBtn.addActionListener(e -> {
            new HomePage();
            dispose();
        });

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(100, 30));
        button.setBackground(new Color(63, 81, 181));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(48, 63, 159)));
    }
}
