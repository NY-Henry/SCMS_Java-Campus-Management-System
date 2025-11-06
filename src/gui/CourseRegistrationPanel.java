package gui;

import database.MySQLDatabase;
import models.Student;
import services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

/**
 * Panel for registering new courses
 */
public class CourseRegistrationPanel extends JPanel {
    private Student student;
    private CourseService courseService;
    private MySQLDatabase db;
    private JTable coursesTable;
    private DefaultTableModel tableModel;

    public CourseRegistrationPanel(Student student, CourseService courseService) {
        this.student = student;
        this.courseService = courseService;
        this.db = MySQLDatabase.getInstance();
        initializeUI();
        loadAvailableCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Title section
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setOpaque(false);

        JLabel titleLabel = new JLabel("Register for Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Select a course and click Register to enroll");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        titleSection.add(titleLabel);
        titleSection.add(subtitleLabel);

        // Action buttons panel (top right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton registerButton = createMinimalButton("Register Course", new Color(70, 130, 180));
        registerButton.addActionListener(e -> registerForCourse());

        JButton refreshButton = createMinimalButton("Refresh", new Color(100, 100, 110));
        refreshButton.addActionListener(e -> loadAvailableCourses());

        buttonPanel.add(registerButton);
        buttonPanel.add(refreshButton);

        topPanel.add(titleSection, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = { "Course Code", "Course Name", "Credits", "Year", "Semester", "Lecturer", "Department",
                "Course ID" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setRowHeight(40);
        coursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        coursesTable.setShowVerticalLines(false);
        coursesTable.setGridColor(new Color(240, 240, 245));
        coursesTable.setSelectionBackground(new Color(245, 247, 250));
        coursesTable.setSelectionForeground(new Color(45, 45, 45));

        // Minimalist table header
        coursesTable.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        coursesTable.getTableHeader().setBackground(Color.WHITE);
        coursesTable.getTableHeader().setForeground(new Color(120, 120, 120));
        coursesTable.getTableHeader().setOpaque(true);
        coursesTable.getTableHeader().setReorderingAllowed(false);
        coursesTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        coursesTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)));

        // Hide Course ID column
        coursesTable.getColumnModel().getColumn(7).setMinWidth(0);
        coursesTable.getColumnModel().getColumn(7).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createMinimalButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

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

    private void loadAvailableCourses() {
        tableModel.setRowCount(0);

        try {
            // Ensure fresh connection
            if (!db.isConnected()) {
                db.connect();
            }

            // Load ALL active courses (not filtered by student's year/semester)
            String query = "SELECT c.course_id, c.course_code, c.course_name, c.credits, " +
                    "c.year_level, c.semester, c.department, " +
                    "CONCAT(COALESCE(p.first_name, ''), ' ', COALESCE(p.last_name, '')) as lecturer_name " +
                    "FROM courses c " +
                    "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                    "LEFT JOIN persons p ON l.person_id = p.person_id " +
                    "WHERE c.is_active = TRUE " +
                    "ORDER BY c.year_level, c.semester, c.course_code";

            ResultSet rs = db.fetchData(query);

            while (rs != null && rs.next()) {
                String lecturerName = rs.getString("lecturer_name");
                if (lecturerName == null || lecturerName.trim().isEmpty()) {
                    lecturerName = "TBA";
                }

                tableModel.addRow(new Object[] {
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getInt("year_level"),
                        rs.getInt("semester"),
                        lecturerName,
                        rs.getString("department"),
                        rs.getInt("course_id")
                });
            }

            // Close ResultSet properly
            if (rs != null) {
                rs.close();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
        int credits = (int) tableModel.getValueAt(selectedRow, 2);
        int yearLevel = (int) tableModel.getValueAt(selectedRow, 3);
        int courseSemester = (int) tableModel.getValueAt(selectedRow, 4);
        int courseId = (int) tableModel.getValueAt(selectedRow, 7);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Register for " + courseCode + " - " + courseName + "?\n\n" +
                        "Year: " + yearLevel + ", Semester: " + courseSemester + ", Credits: " + credits,
                "Confirm Registration", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String academicYear = "2025/2026";

            boolean success = courseService.registerCourse(
                    student.getStudentId(), courseId, academicYear, courseSemester);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Successfully registered for " + courseCode + "!\n\n" +
                                "Course: " + courseName + "\n" +
                                "Year: " + yearLevel + ", Semester: " + courseSemester + "\n" +
                                "Credits: " + credits,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Refresh the available courses list
                loadAvailableCourses();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed!\n\n" +
                                "Possible reasons:\n" +
                                "• You may already be registered for this course\n" +
                                "• The course may be full\n" +
                                "• The course may be inactive\n\n" +
                                "Check the console for detailed error messages.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
