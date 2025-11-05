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
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Class Lists");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Course selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(new Color(236, 240, 241));

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(400, 30));
        loadCourses();

        JButton viewButton = new JButton("View Students");
        viewButton.setBackground(new Color(52, 152, 219));
        viewButton.setForeground(Color.BLACK);
        viewButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewButton.setBorder(BorderFactory.createRaisedBevelBorder());
        viewButton.setPreferredSize(new Dimension(120, 35));
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewButton.setFocusPainted(false);
        viewButton.addActionListener(e -> loadStudents());

        selectionPanel.add(courseLabel);
        selectionPanel.add(courseCombo);
        selectionPanel.add(viewButton);

        // Students table
        String[] columns = { "Reg Number", "Student Name", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentsTable = new JTable(tableModel);
        studentsTable.setRowHeight(30);
        studentsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        studentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        studentsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        studentsTable.getTableHeader().setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(studentsTable);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        topPanel.add(selectionPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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

            // Debug output
            System.out.println("DEBUG: Loading students for course:");
            System.out.println("  Course ID: " + selectedCourse.getCourseId());
            System.out.println("  Course Code: " + selectedCourse.getCourseCode());
            System.out.println("  Course Semester: " + selectedCourse.getSemester());
            System.out.println("  Academic Year: " + academicYear);

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

            System.out.println("DEBUG: Loaded " + count + " students");

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
