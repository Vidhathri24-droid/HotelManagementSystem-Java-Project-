package gui;

import service.BookingService;
import service.BillingService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private JTextArea bookingsArea;
    private JTextField searchField, roomIdField, roomTypeField, priceField,
            branchField, peopleField, namesField, customerUsernameField;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(236, 239, 241));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(224, 242, 241));
        searchField = new JTextField(30);
        styleTextField(searchField);

        JButton searchBtn = new JButton("Search Bookings");
        JButton viewAllBtn = new JButton("View All Bookings");
        JButton logoutBtn = new JButton("Logout");

        styleButton(searchBtn);
        styleButton(viewAllBtn);
        styleButton(logoutBtn);

        topPanel.add(new JLabel("Search by Username:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(viewAllBtn);
        topPanel.add(logoutBtn);

        // Control panel with GridBagLayout
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 144, 156)),
                "Manage Rooms & Bookings"));
        controlPanel.setBackground(new Color(232, 245, 233));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        roomIdField = new JTextField(30);
        roomTypeField = new JTextField(30);
        priceField = new JTextField(30);
        branchField = new JTextField(30);
        peopleField = new JTextField(30);
        namesField = new JTextField(30);
        customerUsernameField = new JTextField(30);

        int row = 0;
        addLabeledField(controlPanel, gbc, "Room ID:", roomIdField, row++);
        addLabeledField(controlPanel, gbc, "Room Type:", roomTypeField, row++);
        addLabeledField(controlPanel, gbc, "Price:", priceField, row++);
        addLabeledField(controlPanel, gbc, "Branch ID:", branchField, row++);
        addLabeledField(controlPanel, gbc, "No. of People:", peopleField, row++);
        addLabeledField(controlPanel, gbc, "Customer Names (comma-separated):", namesField, row++);
        addLabeledField(controlPanel, gbc, "Customer Username:", customerUsernameField, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(controlPanel.getBackground());

        JButton addRoomBtn = new JButton("Add Room");
        JButton removeRoomBtn = new JButton("Remove Room");
        JButton checkAvailableRoomsBtn = new JButton("Check Available Rooms");
        JButton bookRoomBtn = new JButton("Book Room for Guests");

        styleButton(addRoomBtn);
        styleButton(removeRoomBtn);
        styleButton(checkAvailableRoomsBtn);
        styleButton(bookRoomBtn);

        buttonPanel.add(addRoomBtn);
        buttonPanel.add(removeRoomBtn);
        buttonPanel.add(checkAvailableRoomsBtn);
        buttonPanel.add(bookRoomBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        controlPanel.add(buttonPanel, gbc);

        // Bookings output
        bookingsArea = new JTextArea();
        bookingsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        bookingsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(bookingsArea);
        scrollPane.setPreferredSize(new Dimension(850, 250));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 144, 156)),
                "Admin Output"));

        add(topPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Action Listeners
        searchBtn.addActionListener(e -> {
            String name = searchField.getText().trim();
            if (!name.isEmpty()) {
                List<String> bookings = BookingService.getBookingsByCustomerName(name);
                bookingsArea.setText("");
                for (String b : bookings) bookingsArea.append(b + "\n");
                if (bookings.isEmpty()) {
                    bookingsArea.setText("No bookings found for user: " + name);
                }
            }
        });

        viewAllBtn.addActionListener(e -> {
            List<String> bookings = BookingService.getAllBookings();
            bookingsArea.setText("");
            for (String b : bookings) bookingsArea.append(b + "\n");
        });

        logoutBtn.addActionListener(e -> {
            new HomePage();
            dispose();
        });

        addRoomBtn.addActionListener(e -> {
            try {
                int branchId = Integer.parseInt(branchField.getText());
                String type = roomTypeField.getText();
                double price = Double.parseDouble(priceField.getText());
                boolean result = BookingService.addRoom(branchId, type, price);
                bookingsArea.setText(result ? "Room added successfully." : "Failed to add room.");
            } catch (Exception ex) {
                bookingsArea.setText("Invalid input for adding room.");
            }
        });

        removeRoomBtn.addActionListener(e -> {
            try {
                int roomId = Integer.parseInt(roomIdField.getText());
                boolean result = BookingService.removeRoom(roomId);
                bookingsArea.setText(result ? "Room removed successfully." : "Failed to remove room.");
            } catch (Exception ex) {
                bookingsArea.setText("Invalid Room ID.");
            }
        });

        checkAvailableRoomsBtn.addActionListener(e -> {
            List<String> rooms = BookingService.getAllAvailableRooms();
            bookingsArea.setText("");
            for (String r : rooms) bookingsArea.append(r + "\n");
            if (rooms.isEmpty()) {
                bookingsArea.setText("No available rooms currently.");
            }
        });

        bookRoomBtn.addActionListener(e -> {
            try {
                String customerUsername = customerUsernameField.getText().trim();
                int userId = BookingService.getUserIdByUsername(customerUsername);
                if (userId == -1) {
                    bookingsArea.setText("User not found: " + customerUsername);
                    return;
                }

                int roomId = Integer.parseInt(roomIdField.getText());
                String in = JOptionPane.showInputDialog(this, "Enter check-in date (YYYY-MM-DD):");
                String out = JOptionPane.showInputDialog(this, "Enter check-out date (YYYY-MM-DD):");
                int people = Integer.parseInt(peopleField.getText());
                String names = namesField.getText().trim();

                if (names.split(",").length != people) {
                    bookingsArea.setText("Number of names must match number of people.");
                    return;
                }

                double total = BillingService.calculateBill(roomId, in, out, people);
                int bookingId = BookingService.bookRoomWithGuestsAndReturnId(userId, roomId, in, out, total, names);
                bookingsArea.setText(bookingId != -1
                        ? "Room booked for guests successfully. Booking ID: " + bookingId
                        : "Booking failed.");
            } catch (Exception ex) {
                bookingsArea.setText("Invalid input for booking.");
            }
        });

        setVisible(true);
    }

    // ========== Styling Helpers ==========

    private void styleButton(JButton button) {
        button.setBackground(new Color(100, 181, 246));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(66, 165, 245)));
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(320, 30));  // Ensure fixed width and height
        field.setBackground(new Color(250, 250, 250));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 189, 189)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void addLabeledField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        styleTextField(field);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}
