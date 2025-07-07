import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentDashboard extends JFrame {
    private String studentId;
    private JFrame loginFrame;
    private JTable roomTable, paymentTable;
    private DefaultTableModel roomModel, paymentModel;
    private JLabel roomStatusLabel;

    public StudentDashboard(String studentId, JFrame loginFrame) {
        this.studentId = studentId;
        this.loginFrame = loginFrame;
        setTitle("Student Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Header Panel
        JPanel header = new JPanel(new FlowLayout());
        header.setBackground(new Color(240, 240, 240));
        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center Panel with Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Profile", createProfilePanel());
        tabbedPane.addTab("Rooms", createRoomsPanel());
        tabbedPane.addTab("Payments", createPaymentsPanel());
        add(tabbedPane, BorderLayout.CENTER);

        // Footer Panel with Logout
        JPanel footer = new JPanel(new FlowLayout());
        footer.setBackground(new Color(240, 240, 240));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });
        footer.add(logout);
        add(footer, BorderLayout.SOUTH);

        loadRooms();
        loadPayments();
        loadRoomStatus();
        setVisible(true);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton editProfile = new JButton("Edit Profile");
        JButton deleteAccount = new JButton("Delete Account");

        editProfile.addActionListener(e -> editProfile());
        deleteAccount.addActionListener(e -> deleteAccount());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(editProfile, gbc);
        gbc.gridy = 1;
        panel.add(deleteAccount, gbc);

        roomStatusLabel = new JLabel("Room Status: Not Allocated");
        gbc.gridy = 2;
        panel.add(roomStatusLabel, gbc);

        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        roomModel = new DefaultTableModel(new Object[]{"Room ID", "Status", "Capacity"}, 0);
        roomTable = new JTable(roomModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton requestBooking = new JButton("Request Room Booking");
        requestBooking.addActionListener(e -> requestRoomBooking());
        buttonPanel.add(requestBooking);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        paymentModel = new DefaultTableModel(new Object[]{"Month", "Amount", "Status"}, 0);
        paymentTable = new JTable(paymentModel);
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadRooms() {
        roomModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT room_id, status, capacity FROM rooms WHERE status = 'free'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roomModel.addRow(new Object[]{rs.getInt("room_id"), rs.getString("status"), rs.getInt("capacity")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms.");
        }
    }

    private void requestRoomBooking() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to book.");
            return;
        }
        int roomId = (int) roomModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            // Update student with room request status
            String sql1 = "UPDATE students SET room_id = ?, status = 'Requested' WHERE student_id = ?";
            PreparedStatement stmt1 = conn.prepareStatement(sql1);
            stmt1.setInt(1, roomId);
            stmt1.setString(2, studentId);
            stmt1.executeUpdate();

            // Mark room as occupied (pending admin approval)
            String sql2 = "UPDATE rooms SET status = 'Requested' WHERE room_id = ?";
            PreparedStatement stmt2 = conn.prepareStatement(sql2);
            stmt2.setInt(1, roomId);
            stmt2.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Room booking requested! Awaiting admin approval.");
            loadRooms();
            loadRoomStatus();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error requesting room booking.");
        }
    }

    private void editProfile() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM students WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = JOptionPane.showInputDialog(this, "Enter new Name:", rs.getString("name"));
                String password = JOptionPane.showInputDialog(this, "Enter new Password:", rs.getString("password"));
                if (name != null && password != null) {
                    sql = "UPDATE students SET name = ?, password = ? WHERE student_id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setString(2, password);
                    stmt.setString(3, studentId);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Profile updated!");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating profile.");
        }
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete your account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM students WHERE student_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, studentId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Account deleted!");
                dispose();
                new LoginFrame();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting account.");
            }
        }
    }

    private void loadPayments() {
        paymentModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT month, amount, status FROM payments WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                paymentModel.addRow(new Object[]{rs.getString("month"), rs.getDouble("amount"), rs.getString("status")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading payments.");
        }
    }

    private void loadRoomStatus() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT room_id, status FROM students WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("room_id") != 0) {
                roomStatusLabel.setText("Room Status: Allocated (Room ID: " + rs.getInt("room_id") + ")");
            } else {
                roomStatusLabel.setText("Room Status: Not Allocated");
            }
        } catch (SQLException e) {
            roomStatusLabel.setText("Room Status: Error loading status");
        }
    }
}