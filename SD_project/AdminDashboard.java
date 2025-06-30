import javax.swing.*;

public class AdminDashboard extends JFrame {
  public AdminDashboard() {
    setTitle("Admin Dashboard");
    setSize(500, 400);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JLabel label = new JLabel("Welcome, Admin!", SwingConstants.CENTER);
    add(label);
  }
}
