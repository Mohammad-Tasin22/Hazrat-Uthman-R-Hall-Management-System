import javax.swing.*;

public class StudentDashboard extends JFrame {
  public StudentDashboard(String studentId) {
    setTitle("Student Dashboard");
    setSize(500, 400);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JLabel label = new JLabel("Welcome, Student ID: " + studentId, SwingConstants.CENTER);
    add(label);
  }
}
