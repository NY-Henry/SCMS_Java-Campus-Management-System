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
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("My Teaching Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] columns = { "Course Code", "Course Name", "Credits", "Year", "Semester", "Capacity" };
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
        coursesTable.getTableHeader().setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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
