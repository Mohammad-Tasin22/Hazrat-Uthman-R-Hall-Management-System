import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JFrame loginFrame;
    private JTable studentTable, roomTable, paymentTable, complaintTable;
    private DefaultTableModel studentModel, roomModel, paymentModel, complaintModel;
    private JTabbedPane tabbedPane;

    public AdminDashboard(JFrame loginFrame) {
        this.loginFrame = loginFrame;
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Header Panel
        JPanel header = new JPanel(new FlowLayout());
        header.setBackground(new Color(240, 240, 240));
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center Panel with Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Students", createStudentsPanel());
        tabbedPane.addTab("Rooms", createRoomsPanel());
        tabbedPane.addTab("Payments", createPaymentsPanel());
        tabbedPane.addTab("Complaints", createComplaintsPanel());
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

        loadStudents();
        loadRooms();
        loadPayments();
        loadComplaints();
        setVisible(true);
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        studentModel = new DefaultTableModel(new Object[]{"Student ID", "Name", "Room ID", "Status", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        studentTable = new JTable(studentModel);
        studentTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        studentTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addStudent = new JButton("Add Student");
        addStudent.addActionListener(e -> addStudent());
        buttonPanel.add(addStudent);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        roomModel = new DefaultTableModel(new Object[]{"Room ID", "Status", "Capacity", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        roomTable = new JTable(roomModel);
        roomTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        roomTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(roomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addRoom = new JButton("Add Room");
        addRoom.addActionListener(e -> addRoom());
        buttonPanel.add(addRoom);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        paymentModel = new DefaultTableModel(new Object[]{"Student ID", "Month", "Amount", "Status", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        paymentTable = new JTable(paymentModel);
        paymentTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        paymentTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        complaintModel = new DefaultTableModel(new Object[]{"Complaint ID", "Student ID", "Description", "Status", "Response", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        complaintTable = new JTable(complaintModel);
        complaintTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        complaintTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(complaintTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT student_id, name, room_id, status FROM students";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                studentModel.addRow(new Object[]{rs.getString("student_id"), rs.getString("name"), rs.getString("room_id"), rs.getString("status"), "Edit/Delete"});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students.");
        }
    }

    private void addStudent() {
        String studentId = JOptionPane.showInputDialog(this, "Enter Student ID:");
        String name = JOptionPane.showInputDialog(this, "Enter Name:");
        String password = JOptionPane.showInputDialog(this, "Enter Password:");
        if (studentId != null && name != null && password != null && !studentId.isEmpty() && !name.isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO students (student_id, name, password) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, studentId);
                stmt.setString(2, name);
                stmt.setString(3, password);
                stmt.executeUpdate();
                loadStudents();
                JOptionPane.showMessageDialog(this, "Student added!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding student. ID may already exist.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "All fields are required.");
        }
    }

    private void loadRooms() {
        roomModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT room_id, status, capacity FROM rooms";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roomModel.addRow(new Object[]{rs.getInt("room_id"), rs.getString("status"), rs.getInt("capacity"), "Allocate"});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms.");
        }
    }

    private void addRoom() {
        String roomIdStr = JOptionPane.showInputDialog(this, "Enter Room ID:");
        String capacityStr = JOptionPane.showInputDialog(this, "Enter Capacity:");
        if (roomIdStr != null && capacityStr != null && !roomIdStr.isEmpty() && !capacityStr.isEmpty()) {
            try {
                int roomId = Integer.parseInt(roomIdStr);
                int capacity = Integer.parseInt(capacityStr);
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "INSERT INTO rooms (room_id, status, capacity) VALUES (?, 'free', ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, roomId);
                    stmt.setInt(2, capacity);
                    stmt.executeUpdate();
                    loadRooms();
                    JOptionPane.showMessageDialog(this, "Room added!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Room ID and Capacity.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding room. Room ID may already exist.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "All fields are required.");
        }
    }

    private void loadPayments() {
        paymentModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT student_id, month, amount, status FROM payments";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                paymentModel.addRow(new Object[]{rs.getString("student_id"), rs.getString("month"), rs.getDouble("amount"), rs.getString("status"), "Update"});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading payments.");
        }
    }

    private void loadComplaints() {
        complaintModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT complaint_id, student_id, description, status, response FROM complaints";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                complaintModel.addRow(new Object[]{rs.getInt("complaint_id"), rs.getString("student_id"), rs.getString("description"), rs.getString("status"), rs.getString("response"), "Update"});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading complaints.");
        }
    }

    // Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Button Editor
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                if (isPushed) {
                    String action = button.getText();
                    if (tabbedPane.getSelectedIndex() == 0) handleStudentAction(row, action);
                    else if (tabbedPane.getSelectedIndex() == 1) handleRoomAction(row, action);
                    else if (tabbedPane.getSelectedIndex() == 2) handlePaymentAction(row, action);
                    else if (tabbedPane.getSelectedIndex() == 3) handleComplaintAction(row, action);
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        private void handleStudentAction(int row, String action) {
            String studentId = (String) studentModel.getValueAt(row, 0);
            if ("Edit/Delete".equals(action)) {
                String[] options = {"Edit", "Delete"};
                int choice = JOptionPane.showOptionDialog(AdminDashboard.this, "Choose action:", "Student Management",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (choice == 0) editStudent(studentId);
                else if (choice == 1) deleteStudent(studentId);
            }
        }

        private void handleRoomAction(int row, String action) {
            int roomId = (int) roomModel.getValueAt(row, 0);
            if ("Allocate".equals(action)) {
                String studentId = JOptionPane.showInputDialog(AdminDashboard.this, "Enter Student ID to allocate:");
                if (studentId != null && !studentId.isEmpty()) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        conn.setAutoCommit(false);
                        String sql1 = "UPDATE students SET room_id = ?, status = 'Allocated' WHERE student_id = ?";
                        PreparedStatement stmt1 = conn.prepareStatement(sql1);
                        stmt1.setInt(1, roomId);
                        stmt1.setString(2, studentId);
                        stmt1.executeUpdate();

                        String sql2 = "UPDATE rooms SET status = 'occupied' WHERE room_id = ?";
                        PreparedStatement stmt2 = conn.prepareStatement(sql2);
                        stmt2.setInt(1, roomId);
                        stmt2.executeUpdate();

                        conn.commit();
                        loadRooms();
                        loadStudents(); // Refresh student list to reflect allocation
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Room allocated!");
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Error allocating room.");
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Please enter a valid Student ID.");
                }
            }
        }

        private void handlePaymentAction(int row, String action) {
            if ("Update".equals(action)) {
                String studentId = (String) paymentModel.getValueAt(row, 0);
                String month = (String) paymentModel.getValueAt(row, 1);
                String[] statuses = {"Unpaid", "Paid"};
                String status = (String) JOptionPane.showInputDialog(AdminDashboard.this, "Select Status:", "Update Payment",
                        JOptionPane.QUESTION_MESSAGE, null, statuses, "Unpaid");
                if (status != null) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "UPDATE payments SET status = ? WHERE student_id = ? AND month = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, status);
                        stmt.setString(2, studentId);
                        stmt.setString(3, month);
                        stmt.executeUpdate();
                        loadPayments();
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Payment status updated!");
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Error updating payment.");
                    }
                }
            }
        }

        private void handleComplaintAction(int row, String action) {
            if ("Update".equals(action)) {
                int complaintId = (int) complaintModel.getValueAt(row, 0);
                String response = JOptionPane.showInputDialog(AdminDashboard.this, "Enter Response:");
                String[] statuses = {"Pending", "Resolved"};
                String status = (String) JOptionPane.showInputDialog(AdminDashboard.this, "Select Status:", "Update Complaint",
                        JOptionPane.QUESTION_MESSAGE, null, statuses, "Pending");
                if (response != null && status != null) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "UPDATE complaints SET response = ?, status = ? WHERE complaint_id = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, response);
                        stmt.setString(2, status);
                        stmt.setInt(3, complaintId);
                        stmt.executeUpdate();
                        loadComplaints();
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Complaint updated!");
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Error updating complaint.");
                    }
                }
            }
        }

        private void editStudent(String studentId) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM students WHERE student_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, studentId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String name = JOptionPane.showInputDialog(AdminDashboard.this, "Enter new Name:", rs.getString("name"));
                    String password = JOptionPane.showInputDialog(AdminDashboard.this, "Enter new Password:", rs.getString("password"));
                    if (name != null && password != null) {
                        sql = "UPDATE students SET name = ?, password = ? WHERE student_id = ?";
                        stmt = conn.prepareStatement(sql);
                        stmt.setString(1, name);
                        stmt.setString(2, password);
                        stmt.setString(3, studentId);
                        stmt.executeUpdate();
                        loadStudents();
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Student updated!");
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Error editing student.");
            }
        }

        private void deleteStudent(String studentId) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM students WHERE student_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, studentId);
                stmt.executeUpdate();
                loadStudents();
                JOptionPane.showMessageDialog(AdminDashboard.this, "Student deleted!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Error deleting student.");
            }
        }
    }
}