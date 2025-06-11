package gui;

import service.BookingService;
import service.BillingService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private JComboBox<String> branchBox;
    private JTextArea roomArea;
    private JTextField roomIdField, checkInField, checkOutField, peopleField, usernamesField, billField;
    private int userId = 1; // Replace with actual session user ID

    public CustomerDashboard() {
        setTitle("Customer Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(236, 239, 241));
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(144, 164, 174)), "Booking Information"));
        inputPanel.setBackground(new Color(232, 234, 246));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        branchBox = new JComboBox<>();
        roomIdField = new JTextField(15);
        checkInField = new JTextField(15);
        checkOutField = new JTextField(15);
        peopleField = new JTextField(15);
        usernamesField = new JTextField(15);
        billField = new JTextField(10);
        billField.setEditable(false);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Select Branch:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(branchBox, gbc);

        gbc.gridx = 0; gbc.gridy++;
        inputPanel.add(new JLabel("Room ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(roomIdField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        inputPanel.add(new JLabel("Check-In Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(checkInField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        inputPanel.add(new JLabel("Check-Out Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(checkOutField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        inputPanel.add(new JLabel("No. of People:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(peopleField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        inputPanel.add(new JLabel("Usernames (comma-separated):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(usernamesField, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(120, 144, 156)), "Available Rooms"));
        roomArea = new JTextArea();
        roomArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        roomArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(roomArea);
        scrollPane.setPreferredSize(new Dimension(850, 250));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 239, 241));

        JButton loadRoomsBtn = new JButton("Load Available Rooms");
        JButton calcBillBtn = new JButton("Calculate Bill");
        JButton bookBtn = new JButton("Book Room");
        JButton cancelBtn = new JButton("Cancel Booking");
        JButton refreshBtn = new JButton("Refresh Availability");
        JButton logoutBtn = new JButton("Logout");

        styleButton(loadRoomsBtn);
        styleButton(calcBillBtn);
        styleButton(bookBtn);
        styleButton(cancelBtn);
        styleButton(refreshBtn);
        styleButton(logoutBtn);

        buttonPanel.add(loadRoomsBtn);
        buttonPanel.add(calcBillBtn);
        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(new JLabel("Bill:"));
        buttonPanel.add(billField);
        buttonPanel.add(logoutBtn);

        add(inputPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        List<String> branches = BookingService.getHotelBranches();
        for (String b : branches) branchBox.addItem(b);

        loadRoomsBtn.addActionListener(e -> {
            BookingService.refreshRoomAvailability();
            loadRooms();
        });

        calcBillBtn.addActionListener(e -> {
            try {
                int roomId = Integer.parseInt(roomIdField.getText());
                String in = checkInField.getText();
                String out = checkOutField.getText();
                int people = Integer.parseInt(peopleField.getText());
                double bill = BillingService.calculateBill(roomId, in, out, people);
                billField.setText("$" + String.format("%.2f", bill));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data for billing.");
            }
        });

        bookBtn.addActionListener(e -> {
            try {
                int roomId = Integer.parseInt(roomIdField.getText());
                String in = checkInField.getText();
                String out = checkOutField.getText();
                int people = Integer.parseInt(peopleField.getText());
                String usernames = usernamesField.getText();
                if (usernames.split(",").length != people) {
                    JOptionPane.showMessageDialog(this, "Number of usernames must match number of people.");
                    return;
                }
                double total = BillingService.calculateBill(roomId, in, out, people);
                int bookingId = BookingService.bookRoomWithGuestsAndReturnId(userId, roomId, in, out, total, usernames);
                if (bookingId != -1) {
                    JOptionPane.showMessageDialog(this, "Room booked successfully! Booking ID: " + bookingId);
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(this, "Booking failed.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error booking room.");
            }
        });

        cancelBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter Booking ID to cancel:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int bookingId = Integer.parseInt(input.trim());
                    if (BookingService.cancelBooking(bookingId)) {
                        JOptionPane.showMessageDialog(this, "Booking cancelled.");
                        loadRooms();
                    } else {
                        JOptionPane.showMessageDialog(this, "Cancellation failed or Booking ID not found.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric Booking ID.");
                }
            }
        });

        refreshBtn.addActionListener(e -> {
            BookingService.refreshRoomAvailability();
            loadRooms();
        });

        logoutBtn.addActionListener(e -> {
            new HomePage();
            dispose();
        });

        setVisible(true);
    }

    private void loadRooms() {
        String branch = (String) branchBox.getSelectedItem();
        List<String> rooms = BookingService.getAvailableRooms(branch);
        roomArea.setText("");
        for (String r : rooms) {
            roomArea.append(r + "\\n");
        }
        if (rooms.isEmpty()) {
            roomArea.setText("No available rooms at this branch.");
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(63, 81, 181));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(48, 63, 159)));
    }
}
