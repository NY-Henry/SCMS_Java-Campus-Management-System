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
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Upload Grades");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        formPanel.setMaximumSize(new Dimension(700, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Course selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Course:"), gbc);

        gbc.gridx = 1;
        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(courseCombo, gbc);

        // Student selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Select Student:"), gbc);

        gbc.gridx = 1;
        studentCombo = new JComboBox<>();
        studentCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(studentCombo, gbc);

        // Load courses and add listener AFTER both combos are initialized
        loadCourses();
        courseCombo.addActionListener(e -> loadStudentsForCourse());

        // Coursework marks
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Coursework Marks (Max 40):"), gbc);

        gbc.gridx = 1;
        courseworkField = new JTextField();
        courseworkField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(courseworkField, gbc);

        // Exam marks
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Exam Marks (Max 60):"), gbc);

        gbc.gridx = 1;
        examField = new JTextField();
        examField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(examField, gbc);

        // Remarks
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Remarks:"), gbc);

        gbc.gridx = 1;
        remarksArea = new JTextArea(3, 20);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        formPanel.add(remarksScroll, gbc);

        // Submit button
        gbc.gridx = 1;
        gbc.gridy = 5;
        JButton submitButton = new JButton("Upload Grade");
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setPreferredSize(new Dimension(150, 35));
        submitButton.addActionListener(e -> uploadGrade());
        formPanel.add(submitButton, gbc);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(236, 240, 241));
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(formPanel);

        add(container, BorderLayout.NORTH);
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

            // Debug output
            System.out.println("DEBUG UploadGrades: Loading students for course:");
            System.out.println("  Course ID: " + selectedCourse.getCourseId());
            System.out.println("  Course Code: " + selectedCourse.getCourseCode());

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

            System.out.println("DEBUG: Loaded " + count + " students");

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
