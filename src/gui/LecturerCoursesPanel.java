package gui;

import models.Course;
import models.Lecturer;
import services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LecturerCoursesPanel extends JPanel {
    private Lecturer lecturer;
    private CourseService courseService;
    private JTable coursesTable;
    private DefaultTableModel tableModel;

    public LecturerCoursesPanel(Lecturer lecturer, CourseService courseService) {
        this.lecturer = lecturer;
        this.courseService = courseService;
        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title and refresh button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("My Teaching Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));

        // Refresh button
        JButton refreshButton = createMinimalButton("\u21BB", new Color(100, 100, 110));
        refreshButton.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
        refreshButton.addActionListener(e -> loadCourses());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        // Table
        String[] columns = { "Course Code", "Course Name", "Credits", "Year", "Semester", "Capacity" };
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
        List<Course> courses = courseService.getLecturerCourses(lecturer.getLecturerId());

        for (Course course : courses) {
            tableModel.addRow(new Object[] {
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits(),
                    "Year " + course.getYearLevel(),
                    "Sem " + course.getSemester(),
                    course.getMaxCapacity()
            });
        }
    }
}
