import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
  private JComboBox<String> roleCombo;
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;

  public LoginForm() {
    setTitle("Hostel Management Login");
    setSize(400, 250);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel roleLabel = new JLabel("Login As:");
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(roleLabel, gbc);

    roleCombo = new JComboBox<>(new String[] { "Admin", "Student" });
    gbc.gridx = 1;
    gbc.gridy = 0;
    add(roleCombo, gbc);

    JLabel userLabel = new JLabel("Username:");
    gbc.gridx = 0;
    gbc.gridy = 1;
    add(userLabel, gbc);

    usernameField = new JTextField();
    gbc.gridx = 1;
    gbc.gridy = 1;
    add(usernameField, gbc);

    JLabel passLabel = new JLabel("Password:");
    gbc.gridx = 0;
    gbc.gridy = 2;
    add(passLabel, gbc);

    passwordField = new JPasswordField();
    gbc.gridx = 1;
    gbc.gridy = 2;
    add(passwordField, gbc);

    loginButton = new JButton("Login");
    gbc.gridx = 1;
    gbc.gridy = 3;
    add(loginButton, gbc);

    loginButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        login();
      }
    });
  }

  private void login() {
    String role = (String) roleCombo.getSelectedItem();
    String username = usernameField.getText();
    String password = String.valueOf(passwordField.getPassword());

    try (Connection conn = DBConnection.getConnection()) {
      String query;
      if (role.equals("Admin")) {
        query = "SELECT * FROM admins WHERE username=? AND password=?";
      } else {
        query = "SELECT * FROM students WHERE student_id=? AND password=?";
      }

      PreparedStatement pst = conn.prepareStatement(query);
      pst.setString(1, username);
      pst.setString(2, password);

      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        JOptionPane.showMessageDialog(this, "Login Successful as " + role);
        dispose();
        if (role.equals("Admin")) {
          new AdminDashboard().setVisible(true);
        } else {
          new StudentDashboard(username).setVisible(true);
        }
      } else {
        JOptionPane.showMessageDialog(this, "Invalid credentials.");
      }

    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
  }
}
