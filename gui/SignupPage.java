package gui;

import javax.swing.*;
import java.awt.*;
import service.AuthService;

public class SignupPage extends JFrame {
    public SignupPage() {
        setTitle("Sign Up");
        setSize(420, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(236, 239, 241));
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 33, 33));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(userLabel, gbc);
        gbc.gridx = 1; formPanel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy++; formPanel.add(passLabel, gbc);
        gbc.gridx = 1; formPanel.add(passField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(236, 239, 241));
        JButton signupBtn = new JButton("Sign Up");
        JButton backBtn = new JButton("Back");

        styleButton(signupBtn);
        styleButton(backBtn);

        buttonPanel.add(signupBtn);
        buttonPanel.add(backBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        signupBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (AuthService.signup(username, password)) {
                JOptionPane.showMessageDialog(this, "Signup successful! Please log in.");
                new LoginPage();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists.");
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
