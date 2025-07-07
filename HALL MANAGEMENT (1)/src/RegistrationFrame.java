import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationFrame extends JFrame {
    private JTextField studentIdField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JFrame loginFrame;

    public RegistrationFrame(JFrame loginFrame) {
        this.loginFrame = loginFrame;
        setTitle("Student Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(350, 300);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Student ID
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        studentIdField = new JTextField(15);
        add(studentIdField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        add(nameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        // Register Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String studentId = studentIdField.getText();
            String name = nameField.getText();
            String password = new String(passwordField.getPassword());

            if (studentId.isEmpty() || name.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            if (registerStudent(studentId, name, password)) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");
                dispose();
            }
        });
        add(registerButton, gbc);

        // Cancel Button
        gbc.gridy = 4;
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton, gbc);

        setVisible(true);
    }

    private boolean registerStudent(String studentId, String name, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            // Insert student
            String sql1 = "INSERT INTO students (student_id, name, password) VALUES (?, ?, ?)";
            PreparedStatement stmt1 = conn.prepareStatement(sql1);
            stmt1.setString(1, studentId);
            stmt1.setString(2, name);
            stmt1.setString(3, password);
            stmt1.executeUpdate();

            // Initialize payment for the current month (e.g., July 2025)
            String currentMonth = "July 2025";
            String sql2 = "INSERT INTO payments (student_id, month, amount, status) VALUES (?, ?, 500.00, 'Unpaid')";
            PreparedStatement stmt2 = conn.prepareStatement(sql2);
            stmt2.setString(1, studentId);
            stmt2.setString(2, currentMonth);
            stmt2.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Registration failed. Student ID may already exist.");
            return false;
        }
    }
}