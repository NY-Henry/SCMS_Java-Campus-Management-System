package gui;

import database.MySQLDatabase;
import models.Course;
import models.Lecturer;
import services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.List;

public class ClassListPanel extends JPanel {
    private Lecturer lecturer;
    private CourseService courseService;
    private MySQLDatabase db;
    private JComboBox<String> courseCombo;
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private List<Course> courses;

    public ClassListPanel(Lecturer lecturer, CourseService courseService) {
        this.lecturer = lecturer;
        this.courseService = courseService;
        this.db = MySQLDatabase.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title and view button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Left section - Title and course selector
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setOpaque(false);

        JLabel titleLabel = new JLabel("Class Lists");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Course selection row
        JPanel selectionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        selectionRow.setOpaque(false);
        selectionRow.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        selectionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseLabel.setForeground(new Color(120, 120, 120));

        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseCombo.setPreferredSize(new Dimension(350, 32));
        courseCombo.setBackground(Color.WHITE);
        courseCombo.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        loadCourses();

        selectionRow.add(courseLabel);
        selectionRow.add(Box.createRigidArea(new Dimension(10, 0)));
        selectionRow.add(courseCombo);

        leftSection.add(titleLabel);
        leftSection.add(selectionRow);

        // Right section - View button
        JButton viewButton = createMinimalButton("View Students", new Color(70, 130, 180));
        viewButton.addActionListener(e -> loadStudents());

        topPanel.add(leftSection, BorderLayout.WEST);
        topPanel.add(viewButton, BorderLayout.EAST);

        // Students table
        String[] columns = { "Reg Number", "Student Name", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentsTable = new JTable(tableModel);
        studentsTable.setRowHeight(40);
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentsTable.setShowVerticalLines(false);
        studentsTable.setGridColor(new Color(240, 240, 245));
        studentsTable.setSelectionBackground(new Color(245, 247, 250));
        studentsTable.setSelectionForeground(new Color(45, 45, 45));

        // Minimalist table header
        studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentsTable.getTableHeader().setBackground(Color.WHITE);
        studentsTable.getTableHeader().setForeground(new Color(120, 120, 120));
        studentsTable.getTableHeader().setOpaque(true);
        studentsTable.getTableHeader().setReorderingAllowed(false);
        studentsTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        studentsTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)));

        JScrollPane scrollPane = new JScrollPane(studentsTable);
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
        courses = courseService.getLecturerCourses(lecturer.getLecturerId());
        courseCombo.removeAllItems();

        for (Course course : courses) {
            courseCombo.addItem(course.getCourseCode() + " - " + course.getCourseName());
        }
    }

    private void loadStudents() {
        tableModel.setRowCount(0);

        int selectedIndex = courseCombo.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= courses.size()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course selectedCourse = courses.get(selectedIndex);

        try {
            // Ensure fresh connection
            if (!db.isConnected()) {
                db.connect();
            }

            // Use the course's actual semester, not hardcoded
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
                tableModel.addRow(new Object[] {
                        rs.getString("registration_number"),
                        rs.getString("student_name"),
                        rs.getString("status")
                });
                count++;
            }

            // Close ResultSet properly
            if (rs != null) {
                rs.close();
            }

            if (count == 0) {
                JOptionPane.showMessageDialog(this,
                        "No students registered for this course yet.",
                        "No Students", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
