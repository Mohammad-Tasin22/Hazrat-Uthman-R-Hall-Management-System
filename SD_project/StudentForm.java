import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentForm extends JDialog {
  private JTextField idField, nameField, passwordField, roomField, paymentField;
  private JCheckBox checkinBox;
  private boolean saved = false;
  private String existingId = null;

  public StudentForm(Frame parent, String title, String studentId) {
    super(parent, title, true);
    setSize(400, 300);
    setLayout(new GridBagLayout());
    setLocationRelativeTo(parent);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel idLabel = new JLabel("Student ID:");
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(idLabel, gbc);

    idField = new JTextField();
    gbc.gridx = 1;
    gbc.gridy = 0;
    add(idField, gbc);

    JLabel nameLabel = new JLabel("Name:");
    gbc.gridx = 0;
    gbc.gridy = 1;
    add(nameLabel, gbc);

    nameField = new JTextField();
    gbc.gridx = 1;
    gbc.gridy = 1;
    add(nameField, gbc);

    JLabel passLabel = new JLabel("Password:");
    gbc.gridx = 0;
    gbc.gridy = 2;
    add(passLabel, gbc);

    passwordField = new JTextField();
    gbc.gridx = 1;
    gbc.gridy = 2;
    add(passwordField, gbc);

    JLabel roomLabel = new JLabel("Room No:");
    gbc.gridx = 0;
    gbc.gridy = 3;
    add(roomLabel, gbc);

    roomField = new JTextField();
    gbc.gridx = 1;
    gbc.gridy = 3;
    add(roomField, gbc);

    JLabel paymentLabel = new JLabel("Payment Status:");
    gbc.gridx = 0;
    gbc.gridy = 4;
    add(paymentLabel, gbc);

    paymentField = new JTextField();
    gbc.gridx = 1;
    gbc.gridy = 4;
    add(paymentField, gbc);

    JLabel checkinLabel = new JLabel("Checked In:");
    gbc.gridx = 0;
    gbc.gridy = 5;
    add(checkinLabel, gbc);

    checkinBox = new JCheckBox();
    gbc.gridx = 1;
    gbc.gridy = 5;
    add(checkinBox, gbc);

    JButton saveButton = new JButton("Save");
    gbc.gridx = 1;
    gbc.gridy = 6;
    add(saveButton, gbc);

    saveButton.addActionListener(e -> saveStudent());

    // If editing, load existing data
    if (studentId != null) {
      loadStudent(studentId);
      idField.setEditable(false);
      existingId = studentId;
    }
  }

  private void loadStudent(String studentId) {
    try (Connection conn = DBConnection.getConnection();
        PreparedStatement pst = conn.prepareStatement("SELECT * FROM students WHERE student_id=?")) {
      pst.setString(1, studentId);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        idField.setText(rs.getString("student_id"));
        nameField.setText(rs.getString("name"));
        passwordField.setText(rs.getString("password"));
        roomField.setText(rs.getString("room_no"));
        paymentField.setText(rs.getString("payment_status"));
        checkinBox.setSelected(rs.getBoolean("checkin_status"));
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private void saveStudent() {
    String id = idField.getText();
    String name = nameField.getText();
    String password = passwordField.getText();
    String room = roomField.getText();
    String payment = paymentField.getText();
    boolean checkedIn = checkinBox.isSelected();

    if (id.isEmpty() || name.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Student ID and Name are required.");
      return;
    }

    try (Connection conn = DBConnection.getConnection()) {
      if (existingId == null) {
        // Insert
        PreparedStatement pst = conn.prepareStatement("INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)");
        pst.setString(1, id);
        pst.setString(2, name);
        pst.setString(3, password);
        pst.setString(4, room);
        pst.setBoolean(5, checkedIn);
        pst.setString(6, payment);
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "Student added successfully.");
      } else {
        // Update
        PreparedStatement pst = conn.prepareStatement(
            "UPDATE students SET name=?, password=?, room_no=?, checkin_status=?, payment_status=? WHERE student_id=?");
        pst.setString(1, name);
        pst.setString(2, password);
        pst.setString(3, room);
        pst.setBoolean(4, checkedIn);
        pst.setString(5, payment);
        pst.setString(6, existingId);
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "Student updated successfully.");
      }
      saved = true;
      dispose();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public boolean isSaved() {
    return saved;
  }
}
