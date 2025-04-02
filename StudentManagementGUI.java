import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class College {
    public final static String c_name = "GPA";
}

public class StudentManagementGUI extends JFrame {
    private Connection conn;
    private JTextField Enrollmentdata_check;
    private JTextField Enrollmentdata_add;
    private JTextField Enrollmentdata_check_result;

    private JTextField nameField;
    private JTextField nameField1;

    private JTextField spiField;
    private JTextField cgpaField;
    private JTextArea resultAreaCheckResult;
    private JTextArea CheckDetailsArea;
    private JTextArea resultAreaAllStudents;
    private JTextArea resultAreaTopStudents;

    public StudentManagementGUI() {
        connectToDatabase();
        createUI();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/clg", "root", "");
            System.out.println("Connecting With Database");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createUI() {
        setTitle("Student Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Add Student", createAddStudentPanel());
        tabbedPane.addTab("Check Details", createCheckDetailsPanel());
        tabbedPane.addTab("Print All Students", createPrintAllStudentsPanel());
        tabbedPane.addTab("Check Result", createCheckResultPanel());
        tabbedPane.addTab("Top 3 Students", createTopStudentsPanel());

        add(tabbedPane);

        setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Welcome to Student Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        JButton buttonCollegeName = new JButton("Print College Name");
        buttonCollegeName.setToolTipText("Print the name of the college");
        buttonCollegeName.addActionListener(e -> JOptionPane.showMessageDialog(this, College.c_name, "College Name", JOptionPane.INFORMATION_MESSAGE));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonCollegeName);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAddStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel enrollmentLabel = new JLabel("Enrollment Number:");
        Enrollmentdata_add = new JTextField();
        // nameField1 = new JTextField();

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel spiLabel = new JLabel("SPI:");
        spiField = new JTextField();
        JLabel cgpaLabel = new JLabel("CGPA:");
        cgpaField = new JTextField();

        inputPanel.add(enrollmentLabel);
        inputPanel.add(Enrollmentdata_add);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(spiLabel);
        inputPanel.add(spiField);
        inputPanel.add(cgpaLabel);
        inputPanel.add(cgpaField);

        JButton addButton = new JButton("Add");
        addButton.setToolTipText("Add the student to the database");
        addButton.addActionListener(e -> {
            String enrollment = Enrollmentdata_add.getText();
            String name = nameField.getText();
            float spi = Float.parseFloat(spiField.getText());
            float cgpa = Float.parseFloat(cgpaField.getText());

            if (enrollment == null || enrollment.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enrollment number cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Enrolment Field Value: " + Enrollmentdata_add.getText()); // Debugging
            System.out.println("Captured Enrollment: " + enrollment); // Debugging

            addStudent(enrollment, name, spi, cgpa);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addStudent(String enrollment, String name, float spi, float cgpa) {
        try {
            System.out.println("Enrollment: " + enrollment); // Debugging
            System.out.println("Name: " + name); // Debugging
            System.out.println("SPI: " + spi); // Debugging
            System.out.println("CGPA: " + cgpa); // Debugging

            PreparedStatement insertps = conn.prepareStatement(
                    "INSERT INTO `student` (`Enrollment No`, `Name`, `SPI`, `CGPA`) VALUES (?, ?, ?, ?);");
            insertps.setString(1, enrollment);
            insertps.setString(2, name);
            insertps.setFloat(3, spi);
            insertps.setFloat(4, cgpa);

            int insertRow = insertps.executeUpdate();
            if (insertRow > 0) {
                JOptionPane.showMessageDialog(this, insertRow + " Data Inserted", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add student", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createCheckDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel enrollmentLabel = new JLabel("Enter Enrollment No:");
        Enrollmentdata_check = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.add(enrollmentLabel);
        inputPanel.add(Enrollmentdata_check);

        JButton checkButton = new JButton("Check Details");
        checkButton.setToolTipText("Check details of the student");
        checkButton.addActionListener(e -> {
            String enrollment = Enrollmentdata_check.getText();
            checkDetails(enrollment);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkButton);

        CheckDetailsArea = new JTextArea();
        CheckDetailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(CheckDetailsArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private void checkDetails(String enrollment) {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM `student` WHERE `Enrollment No` = ?");
            pst.setString(1, enrollment);
            ResultSet rs = pst.executeQuery();
            StringBuilder details = new StringBuilder();
            while (rs.next()) {
                details.append(rs.getString(1)).append("  ").append(rs.getString(2)).append("  ")
                        .append(rs.getFloat(3)).append("  ").append(rs.getFloat(4)).append("\n");
            }
            CheckDetailsArea.setText(details.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve details", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createPrintAllStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JButton printButton = new JButton("Print All Students");
        printButton.setToolTipText("Print details of all students");
        printButton.addActionListener(e -> printAllStudents());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(printButton);

        resultAreaAllStudents = new JTextArea();
        resultAreaAllStudents.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultAreaAllStudents);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void printAllStudents() {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM `student`;");
            ResultSet rs = pst.executeQuery();
            StringBuilder allStudents = new StringBuilder();
            while (rs.next()) {
                allStudents.append(rs.getString(1)).append("  ").append(rs.getString(2)).append("  ")
                        .append(rs.getFloat(3)).append("  ").append(rs.getFloat(4)).append("\n");
            }
            resultAreaAllStudents.setText(allStudents.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve students", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createCheckResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel enrollmentLabel = new JLabel("Enter Enrollment No:");
        Enrollmentdata_check_result = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.add(enrollmentLabel);
        inputPanel.add(Enrollmentdata_check_result);

        JButton checkButton = new JButton("Check Result");
        checkButton.setToolTipText("Check result of the student");
        checkButton.addActionListener(e -> {
            String enrollment = Enrollmentdata_check_result.getText();
            checkResult(enrollment);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkButton);

        resultAreaCheckResult = new JTextArea();
        resultAreaCheckResult.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultAreaCheckResult);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private void checkResult(String enrollment) {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT SPI, CGPA FROM `student` WHERE `Enrollment No` = ?");
            pst.setString(1, enrollment);
            ResultSet rs = pst.executeQuery();
            StringBuilder result = new StringBuilder("SPI  CGPA\n");
            while (rs.next()) {
                result.append(rs.getFloat(1)).append("  ").append(rs.getFloat(2)).append("\n");
            }
            resultAreaCheckResult.setText(result.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve result", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createTopStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JButton topButton = new JButton("Check Top 3 Students");
        topButton.setToolTipText("Check the top 3 students based on SPI");
        topButton.addActionListener(e -> checkTopStudents());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(topButton);

        resultAreaTopStudents = new JTextArea();
        resultAreaTopStudents.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultAreaTopStudents);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void checkTopStudents() {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM `student` ORDER BY SPI DESC LIMIT 3");
            ResultSet rs = pst.executeQuery();
            StringBuilder topStudents = new StringBuilder();
            while (rs.next()) {
                topStudents.append(rs.getString(1)).append("  ").append(rs.getString(2)).append("  ")
                        .append(rs.getFloat(3)).append("  ").append(rs.getFloat(4)).append("\n");
            }
            resultAreaTopStudents.setText(topStudents.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve top students", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementGUI());
    }
}



