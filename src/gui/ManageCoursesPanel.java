package gui;

import database.MySQLDatabase;
import models.Course;
import services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.List;

public class ManageCoursesPanel extends JPanel {
    private CourseService courseService;
    private MySQLDatabase db;
    private JTable coursesTable;
    private DefaultTableModel tableModel;

    public ManageCoursesPanel(CourseService courseService) {
        this.courseService = courseService;
        this.db = MySQLDatabase.getInstance();
        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Manage Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] columns = { "ID", "Code", "Course Name", "Credits", "Department", "Year", "Semester" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setRowHeight(30);
        coursesTable.setFont(new Font("Arial", Font.PLAIN, 13));

        // Style table header with better visibility
        coursesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        coursesTable.getTableHeader().setBackground(new Color(52, 73, 94));
        coursesTable.getTableHeader().setForeground(Color.BLACK);
        coursesTable.getTableHeader().setOpaque(true);
        coursesTable.getTableHeader().setReorderingAllowed(false);
        coursesTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton addButton = new JButton("Add New Course");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.addActionListener(e -> showAddCourseDialog());

        JButton editButton = new JButton("Edit Course");
        editButton.setBackground(new Color(243, 156, 18));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.addActionListener(e -> showEditCourseDialog());

        JButton deleteButton = new JButton("Delete Course");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteSelectedCourse());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> loadCourses());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = courseService.getAllCourses();

        for (Course course : courses) {
            tableModel.addRow(new Object[] {
                    course.getCourseId(),
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits(),
                    course.getDepartment(),
                    course.getYearLevel(),
                    course.getSemester()
            });
        }
    }

    private void showAddCourseDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Add New Course", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField codeField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField creditsField = new JTextField(15);
        JTextField deptField = new JTextField(15);
        JTextField yearField = new JTextField(15);
        JTextField semesterField = new JTextField(15);

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Course Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Credits:"), gbc);
        gbc.gridx = 1;
        panel.add(creditsField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(deptField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Year Level:"), gbc);
        gbc.gridx = 1;
        panel.add(yearField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        panel.add(semesterField, gbc);

        JButton saveButton = new JButton("Save Course");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                Course course = new Course();
                course.setCourseCode(codeField.getText().trim());
                course.setCourseName(nameField.getText().trim());
                course.setCredits(Integer.parseInt(creditsField.getText().trim()));
                course.setDepartment(deptField.getText().trim());
                course.setYearLevel(Integer.parseInt(yearField.getText().trim()));
                course.setSemester(Integer.parseInt(semesterField.getText().trim()));

                if (courseService.addCourse(course)) {
                    JOptionPane.showMessageDialog(dialog, "Course added successfully!");
                    dialog.dispose();

                    // Refresh the table with a small delay
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        loadCourses();
                    });
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add course!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditCourseDialog() {
        int selectedRow = coursesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get course details
        int courseId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            db.connect();

            // Fetch full course details
            String query = "SELECT course_code, course_name, credits, department, year_level, semester, max_capacity " +
                    "FROM courses WHERE course_id = " + courseId;

            ResultSet rs = db.fetchData(query);

            if (rs == null || !rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Error: Could not fetch course details.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create dialog
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Course", true);
            dialog.setSize(450, 500);
            dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 8, 8, 8);

            // Pre-populate fields
            JTextField codeField = new JTextField(rs.getString("course_code"), 20);
            codeField.setEnabled(false); // Course code cannot be changed
            JTextField nameField = new JTextField(rs.getString("course_name"), 20);
            JTextField creditsField = new JTextField(String.valueOf(rs.getInt("credits")), 20);
            JTextField deptField = new JTextField(rs.getString("department"), 20);
            JTextField yearField = new JTextField(String.valueOf(rs.getInt("year_level")), 20);
            JTextField semesterField = new JTextField(String.valueOf(rs.getInt("semester")), 20);
            JTextField capacityField = new JTextField(String.valueOf(rs.getInt("max_capacity")), 20);

            rs.close();
            db.disconnect();

            int row = 0;

            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel("Course Code:"), gbc);
            gbc.gridx = 1;
            panel.add(codeField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel("Course Name:"), gbc);
            gbc.gridx = 1;
            panel.add(nameField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel("Credits:"), gbc);
            gbc.gridx = 1;
            panel.add(creditsField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel("Department:"), gbc);
            gbc.gridx = 1;
            panel.add(deptField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel("Year Level:"), gbc);
            gbc.gridx = 1;
            panel.add(yearField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel("Semester:"), gbc);
            gbc.gridx = 1;
            panel.add(semesterField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel("Max Capacity:"), gbc);
            gbc.gridx = 1;
            panel.add(capacityField, gbc);

            row++;
            JButton saveButton = new JButton("Save Changes");
            saveButton.setBackground(new Color(243, 156, 18));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);

            saveButton.addActionListener(e -> {
                // Validate inputs
                if (nameField.getText().trim().isEmpty() ||
                        creditsField.getText().trim().isEmpty() ||
                        deptField.getText().trim().isEmpty() ||
                        yearField.getText().trim().isEmpty() ||
                        semesterField.getText().trim().isEmpty() ||
                        capacityField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all fields!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    db.connect();

                    String updateQuery = "UPDATE courses SET course_name = ?, credits = ?, department = ?, " +
                            "year_level = ?, semester = ?, max_capacity = ? WHERE course_id = " + courseId;

                    boolean success = db.executePreparedQuery(updateQuery, new Object[] {
                            nameField.getText().trim(),
                            Integer.parseInt(creditsField.getText().trim()),
                            deptField.getText().trim(),
                            Integer.parseInt(yearField.getText().trim()),
                            Integer.parseInt(semesterField.getText().trim()),
                            Integer.parseInt(capacityField.getText().trim())
                    });

                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Course updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();

                        SwingUtilities.invokeLater(() -> {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            loadCourses();
                        });
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Failed to update course!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter valid numbers for Credits, Year, Semester, and Capacity!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    db.disconnect();
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(new Color(149, 165, 166));
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);
            cancelButton.addActionListener(e -> dialog.dispose());

            row++;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(buttonPanel, gbc);

            dialog.add(panel);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading course details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            db.disconnect();
        }
    }

    private void deleteSelectedCourse() {
        int selectedRow = coursesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get course details
        int courseId = (int) tableModel.getValueAt(selectedRow, 0);
        String courseCode = (String) tableModel.getValueAt(selectedRow, 1);
        String courseName = (String) tableModel.getValueAt(selectedRow, 2);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this course?\n\n" +
                        "Course Code: " + courseCode + "\n" +
                        "Course Name: " + courseName + "\n\n" +
                        "WARNING: This will delete:\n" +
                        "- Course record\n" +
                        "- All course registrations\n" +
                        "- All grades for this course\n\n" +
                        "This action CANNOT be undone!",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            db.connect();

            // Delete in correct order (due to foreign key constraints)
            // 1. Delete grades
            String deleteGrades = "DELETE FROM grades WHERE course_id = " + courseId;
            db.executeUpdate(deleteGrades);

            // 2. Delete course registrations
            String deleteRegistrations = "DELETE FROM course_registrations WHERE course_id = " + courseId;
            db.executeUpdate(deleteRegistrations);

            // 3. Delete course
            String deleteCourse = "DELETE FROM courses WHERE course_id = " + courseId;
            db.executeUpdate(deleteCourse);

            db.disconnect();

            JOptionPane.showMessageDialog(this,
                    "Course deleted successfully!\n\n" +
                            "Course: " + courseName + "\n" +
                            "Code: " + courseCode,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the table
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                loadCourses();
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting course: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            db.disconnect();
        }
    }
}
