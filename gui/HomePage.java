
package gui;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {
    public HomePage() {
        setTitle("Hotel Management System - Home");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 244, 248));
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Hotel Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(33, 33, 33));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        buttonPanel.setBackground(new Color(240, 244, 248));

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");
        JButton exitBtn = new JButton("Exit");

        styleSmallButton(loginBtn);
        styleSmallButton(signupBtn);
        styleSmallButton(exitBtn);

        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);
        buttonPanel.add(exitBtn);

        add(welcomeLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> {
            new LoginPage();
            dispose();
        });

        signupBtn.addActionListener(e -> {
            new SignupPage();
            dispose();
        });

        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void styleSmallButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(new Color(63, 81, 181));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(48, 63, 159)));
    }
}
