package gui;

import database.MySQLDatabase;
import models.Student;
import services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

/**
 * Panel showing student's enrolled courses
 */
public class StudentCoursesPanel extends JPanel {
    private Student student;
    private CourseService courseService;
    private MySQLDatabase db;
    private JTable coursesTable;
    private DefaultTableModel tableModel;

    public StudentCoursesPanel(Student student, CourseService courseService) {
        this.student = student;
        this.courseService = courseService;
        this.db = MySQLDatabase.getInstance();
        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("My Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));

        // Action buttons panel (top right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton dropButton = createMinimalButton("Drop Course", new Color(220, 80, 80));
        dropButton.addActionListener(e -> dropCourse());

        JButton refreshButton = createMinimalButton("Refresh", new Color(100, 100, 110));
        refreshButton.addActionListener(e -> loadCourses());

        buttonPanel.add(dropButton);
        buttonPanel.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = { "Course Code", "Course Name", "Credits", "Status", "Registration ID" };
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

    private void loadCourses() {
        tableModel.setRowCount(0);

        try {
            // Ensure fresh connection
            if (!db.isConnected()) {
                db.connect();
            }

            String academicYear = "2025/2026";

            // Load registered courses directly from database
            String query = "SELECT cr.registration_id, cr.status, c.course_code, c.course_name, c.credits " +
                    "FROM course_registrations cr " +
                    "JOIN courses c ON cr.course_id = c.course_id " +
                    "WHERE cr.student_id = ? AND cr.academic_year = ? AND cr.status = 'REGISTERED' " +
                    "ORDER BY c.course_code";

            ResultSet rs = db.executePreparedSelect(query, new Object[] {
                    student.getStudentId(),
                    academicYear
            });

            while (rs != null && rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getString("status"),
                        rs.getInt("registration_id")
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

                // Refresh the table immediately on EDT
                SwingUtilities.invokeLater(() -> loadCourses());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to drop course!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
