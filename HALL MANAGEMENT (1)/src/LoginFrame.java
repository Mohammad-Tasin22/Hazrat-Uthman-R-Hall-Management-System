import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public LoginFrame() {
        setTitle("Hall Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(350, 250);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username/Student ID:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        String[] roles = {"Student", "Admin"};
        roleCombo = new JComboBox<>(roles);
        add(roleCombo, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (authenticate(username, password, role)) {
                dispose();
                if (role.equals("Student")) {
                    new StudentDashboard(username, this);
                } else {
                    new AdminDashboard(this);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials!");
            }
        });
        add(loginButton, gbc);

        // Register Button (for Students only)
        gbc.gridx = 1; gbc.gridy = 3;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            if (roleCombo.getSelectedItem().equals("Student")) {
                new RegistrationFrame(this);
            } else {
                JOptionPane.showMessageDialog(this, "Registration is for students only!");
            }
        });
        add(registerButton, gbc);

        setVisible(true);
    }

    private boolean authenticate(String username, String password, String role) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = role.equals("Student") ?
                    "SELECT * FROM students WHERE student_id = ? AND password = ?" :
                    "SELECT * FROM admins WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}