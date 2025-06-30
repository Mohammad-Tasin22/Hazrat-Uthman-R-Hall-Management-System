import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentManagement extends JFrame {
  private JTable table;
  private DefaultTableModel model;

  public StudentManagement() {
    setTitle("Student Management");
    setSize(800, 400);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    model = new DefaultTableModel(
        new String[] { "Student ID", "Name", "Password", "Room No", "Checked In", "Payment Status" }, 0);
    table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);
    add(scrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();

    JButton addButton = new JButton("Add");
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    JButton refreshButton = new JButton("Refresh");

    buttonPanel.add(addButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(refreshButton);

    add(buttonPanel, BorderLayout.SOUTH);

    // Load initial data
    loadStudents();

    // Add button actions
    addButton.addActionListener(e -> addStudent());
    editButton.addActionListener(e -> editStudent());
    deleteButton.addActionListener(e -> deleteStudent());
    refreshButton.addActionListener(e -> loadStudents());
  }

  private void loadStudents() {
    model.setRowCount(0);
    try (Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

      while (rs.next()) {
        model.addRow(new Object[] {
            rs.getString("student_id"),
            rs.getString("name"),
            rs.getString("password"),
            rs.getString("room_no"),
            rs.getBoolean("checkin_status"),
            rs.getString("payment_status")
        });
      }

    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private void addStudent() {
    StudentForm form = new StudentForm(this, "Add Student", null);
    form.setVisible(true);
    if (form.isSaved()) {
      loadStudents();
    }
  }

  private void editStudent() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "Select a student to edit.");
      return;
    }
    String studentId = (String) model.getValueAt(selectedRow, 0);
    StudentForm form = new StudentForm(this, "Edit Student", studentId);
    form.setVisible(true);
    if (form.isSaved()) {
      loadStudents();
    }
  }

  private void deleteStudent() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "Select a student to delete.");
      return;
    }
    String studentId = (String) model.getValueAt(selectedRow, 0);

    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Student ID: " + studentId + "?");
    if (confirm != JOptionPane.YES_OPTION)
      return;

    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement("DELETE FROM students WHERE student_id=?")) {
      pst.setString(1, studentId);
      pst.executeUpdate();
      loadStudents();
      JOptionPane.showMessageDialog(this, "Student deleted successfully.");
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}
