package gui;

import models.Course;
import models.Student;
import services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for registering new courses
 */
public class CourseRegistrationPanel extends JPanel {
    private Student student;
    private CourseService courseService;
    private JTable coursesTable;
    private DefaultTableModel tableModel;

    public CourseRegistrationPanel(Student student, CourseService courseService) {
        this.student = student;
        this.courseService = courseService;
        initializeUI();
        loadAvailableCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("Register for Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(52, 152, 219));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel infoLabel = new JLabel("Select a course and click Register to enroll");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        infoPanel.add(infoLabel);

        // Table
        String[] columns = { "Course Code", "Course Name", "Credits", "Lecturer", "Department", "Course ID" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setRowHeight(30);
        coursesTable.setFont(new Font("Arial", Font.PLAIN, 13));
        coursesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        coursesTable.getTableHeader().setBackground(new Color(52, 73, 94));
        coursesTable.getTableHeader().setForeground(Color.WHITE);

        // Hide Course ID column
        coursesTable.getColumnModel().getColumn(5).setMinWidth(0);
        coursesTable.getColumnModel().getColumn(5).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton registerButton = new JButton("Register for Selected Course");
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.addActionListener(e -> registerForCourse());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> loadAvailableCourses());

        buttonPanel.add(registerButton);
        buttonPanel.add(refreshButton);

        // Layout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titleLabel);
        topPanel.add(infoPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAvailableCourses() {
        tableModel.setRowCount(0);

        List<Course> courses = courseService.getAvailableCourses(
                student.getYearOfStudy(), student.getSemester());

        for (Course course : courses) {
            tableModel.addRow(new Object[] {
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits(),
                    course.getLecturerName() != null ? course.getLecturerName() : "TBA",
                    course.getDepartment(),
                    course.getCourseId()
            });
        }
    }

    private void registerForCourse() {
        int selectedRow = coursesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to register!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
        String courseName = (String) tableModel.getValueAt(selectedRow, 1);
        int courseId = (int) tableModel.getValueAt(selectedRow, 5);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Register for " + courseCode + " - " + courseName + "?",
                "Confirm Registration", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String academicYear = "2025/2026";
            int semester = student.getSemester();

            boolean success = courseService.registerCourse(
                    student.getStudentId(), courseId, academicYear, semester);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Successfully registered for " + courseCode + "!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed! You may already be registered or the course is full.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
