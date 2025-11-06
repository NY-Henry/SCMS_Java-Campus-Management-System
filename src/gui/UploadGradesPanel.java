package gui;

import database.MySQLDatabase;
import models.Course;
import models.CourseRegistration;
import models.Lecturer;
import services.CourseService;
import services.GradeService;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UploadGradesPanel extends JPanel {
    private Lecturer lecturer;
    private CourseService courseService;
    private GradeService gradeService;
    private MySQLDatabase db;
    private JComboBox<String> courseCombo;
    private JComboBox<String> studentCombo;
    private JTextField courseworkField, examField;
    private JTextArea remarksArea;
    private List<Course> courses;
    private List<CourseRegistration> students;

    public UploadGradesPanel(Lecturer lecturer, CourseService courseService, GradeService gradeService) {
        this.lecturer = lecturer;
        this.courseService = courseService;
        this.gradeService = gradeService;
        this.db = MySQLDatabase.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Upload Grades");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setMaximumSize(new Dimension(700, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Course selection
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel courseLabel = new JLabel("Select Course");
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseLabel.setForeground(new Color(120, 120, 120));
        formPanel.add(courseLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseCombo.setPreferredSize(new Dimension(400, 35));
        courseCombo.setBackground(Color.WHITE);
        courseCombo.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        formPanel.add(courseCombo, gbc);

        row++;

        // Student selection
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel studentLabel = new JLabel("Select Student");
        studentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentLabel.setForeground(new Color(120, 120, 120));
        formPanel.add(studentLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        studentCombo = new JComboBox<>();
        studentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentCombo.setPreferredSize(new Dimension(400, 35));
        studentCombo.setBackground(Color.WHITE);
        studentCombo.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        formPanel.add(studentCombo, gbc);

        // Load courses and add listener AFTER both combos are initialized
        loadCourses();
        courseCombo.addActionListener(e -> loadStudentsForCourse());

        row++;

        // Coursework marks
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel courseworkLabel = new JLabel("Coursework Marks (Max 40)");
        courseworkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseworkLabel.setForeground(new Color(120, 120, 120));
        formPanel.add(courseworkLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        courseworkField = new JTextField();
        courseworkField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseworkField.setPreferredSize(new Dimension(400, 35));
        courseworkField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(courseworkField, gbc);

        row++;

        // Exam marks
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel examLabel = new JLabel("Exam Marks (Max 60)");
        examLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        examLabel.setForeground(new Color(120, 120, 120));
        formPanel.add(examLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        examField = new JTextField();
        examField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        examField.setPreferredSize(new Dimension(400, 35));
        examField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(examField, gbc);

        row++;

        // Remarks
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel remarksLabel = new JLabel("Remarks");
        remarksLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        remarksLabel.setForeground(new Color(120, 120, 120));
        formPanel.add(remarksLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.WEST;
        remarksArea = new JTextArea(4, 20);
        remarksArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        remarksArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setPreferredSize(new Dimension(400, 90));
        remarksScroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        formPanel.add(remarksScroll, gbc);

        row++;

        // Submit button
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        JButton submitButton = createMinimalButton("Upload Grade", new Color(70, 130, 180));
        submitButton.addActionListener(e -> uploadGrade());
        formPanel.add(submitButton, gbc);

        // Container
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(titleLabel, BorderLayout.NORTH);
        container.add(formPanel, BorderLayout.CENTER);

        add(container, BorderLayout.NORTH);
    }

    private JButton createMinimalButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadCourses() {
        courses = courseService.getLecturerCourses(lecturer.getLecturerId());
        courseCombo.removeAllItems();

        for (Course course : courses) {
            courseCombo.addItem(course.getCourseCode() + " - " + course.getCourseName());
        }

        if (!courses.isEmpty()) {
            loadStudentsForCourse();
        }
    }

    private void loadStudentsForCourse() {
        studentCombo.removeAllItems();
        students = new ArrayList<>();

        int selectedIndex = courseCombo.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= courses.size()) {
            return;
        }

        Course selectedCourse = courses.get(selectedIndex);

        try {
            // Ensure fresh connection
            if (!db.isConnected()) {
                db.connect();
            }

            String academicYear = "2025/2026";

            String query = "SELECT cr.registration_id, cr.student_id, cr.status, " +
                    "s.registration_number, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as student_name " +
                    "FROM course_registrations cr " +
                    "JOIN students s ON cr.student_id = s.student_id " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "WHERE cr.course_id = ? AND cr.academic_year = ? " +
                    "AND cr.status = 'REGISTERED' " +
                    "ORDER BY student_name";

            ResultSet rs = db.executePreparedSelect(query, new Object[] {
                    selectedCourse.getCourseId(),
                    academicYear
            });

            int count = 0;
            while (rs != null && rs.next()) {
                CourseRegistration enrollment = new CourseRegistration();
                enrollment.setRegistrationId(rs.getInt("registration_id"));
                enrollment.setStudentId(rs.getInt("student_id"));
                enrollment.setStudentName(rs.getString("student_name"));
                enrollment.setRegistrationNumber(rs.getString("registration_number"));
                enrollment.setStatus(rs.getString("status"));

                students.add(enrollment);
                studentCombo.addItem(enrollment.getRegistrationNumber() + " - " + enrollment.getStudentName());
                count++;
            }

            // Close ResultSet properly
            if (rs != null) {
                rs.close();
            }

            if (count == 0) {
                studentCombo.addItem("-- No students registered --");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void uploadGrade() {
        try {
            int studentIndex = studentCombo.getSelectedIndex();
            if (studentIndex < 0) {
                JOptionPane.showMessageDialog(this, "Please select a student!");
                return;
            }

            double coursework = Double.parseDouble(courseworkField.getText().trim());
            double exam = Double.parseDouble(examField.getText().trim());
            String remarks = remarksArea.getText().trim();

            int registrationId = students.get(studentIndex).getRegistrationId();

            boolean success = gradeService.uploadGrade(
                    registrationId, coursework, exam, lecturer.getLecturerId(), remarks);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Grade uploaded successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                courseworkField.setText("");
                examField.setText("");
                remarksArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to upload grade!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric marks!",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
