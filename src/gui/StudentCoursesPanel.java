package gui;

import models.CourseRegistration;
import models.Student;
import services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel showing student's enrolled courses
 */
public class StudentCoursesPanel extends JPanel {
    private Student student;
    private CourseService courseService;
    private JTable coursesTable;
    private DefaultTableModel tableModel;

    public StudentCoursesPanel(Student student, CourseService courseService) {
        this.student = student;
        this.courseService = courseService;
        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("My Registered Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Table
        String[] columns = { "Course Code", "Course Name", "Credits", "Status", "Registration ID" };
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

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton dropButton = new JButton("Drop Selected Course");
        dropButton.setBackground(new Color(231, 76, 60));
        dropButton.setForeground(Color.WHITE);
        dropButton.setFocusPainted(false);
        dropButton.setBorderPainted(false);
        dropButton.addActionListener(e -> dropCourse());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> loadCourses());

        buttonPanel.add(dropButton);
        buttonPanel.add(refreshButton);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCourses() {
        tableModel.setRowCount(0);

        String academicYear = "2025/2026";
        int semester = student.getSemester();

        List<CourseRegistration> registrations = courseService.getStudentRegistrations(
                student.getStudentId(), academicYear, semester);

        for (CourseRegistration reg : registrations) {
            tableModel.addRow(new Object[] {
                    reg.getCourseCode(),
                    reg.getCourseName(),
                    reg.getCredits(),
                    reg.getStatus(),
                    reg.getRegistrationId()
            });
        }
    }

    private void dropCourse() {
        int selectedRow = coursesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to drop!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
        String courseName = (String) tableModel.getValueAt(selectedRow, 1);
        int registrationId = (int) tableModel.getValueAt(selectedRow, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to drop " + courseCode + " - " + courseName + "?",
                "Confirm Drop", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = courseService.dropCourse(registrationId, student.getStudentId());

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Course dropped successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to drop course!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
