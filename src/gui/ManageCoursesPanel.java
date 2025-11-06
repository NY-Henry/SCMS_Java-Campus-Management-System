package gui;

import database.MySQLDatabase;
import models.Course;
import services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title and action buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));

        // Action buttons panel (top right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton addButton = createMinimalButton("+ Add Course", new Color(70, 130, 180));
        addButton.addActionListener(e -> showAddCourseDialog());

        JButton editButton = createMinimalButton("Edit", new Color(100, 100, 110));
        editButton.addActionListener(e -> showEditCourseDialog());

        JButton deleteButton = createMinimalButton("Delete", new Color(220, 80, 80));
        deleteButton.addActionListener(e -> deleteSelectedCourse());

        JButton assignButton = createMinimalButton("Assign Lecturer", new Color(100, 100, 110));
        assignButton.addActionListener(e -> showAssignLecturerDialog());

        JButton refreshButton = createMinimalButton("Refresh", new Color(100, 100, 110));
        refreshButton.addActionListener(e -> loadCourses());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(assignButton);
        buttonPanel.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Table setup
        String[] columns = { "ID", "Code", "Course Name", "Credits", "Department", "Year", "Semester", "Lecturer" };
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

        // Hover effect
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

            String query = "SELECT c.course_id, c.course_code, c.course_name, c.credits, " +
                    "c.department, c.year_level, c.semester, " +
                    "CONCAT(COALESCE(p.first_name, ''), ' ', COALESCE(p.last_name, '')) as lecturer_name " +
                    "FROM courses c " +
                    "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                    "LEFT JOIN persons p ON l.person_id = p.person_id " +
                    "ORDER BY c.course_code";

            ResultSet rs = db.fetchData(query);

            while (rs != null && rs.next()) {
                String lecturerName = rs.getString("lecturer_name");
                if (lecturerName == null || lecturerName.trim().isEmpty()) {
                    lecturerName = "Unassigned";
                }

                tableModel.addRow(new Object[] {
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getString("department"),
                        rs.getInt("year_level"),
                        rs.getInt("semester"),
                        lecturerName
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
                    dialog.dispose();
                    JOptionPane.showMessageDialog(ManageCoursesPanel.this, "Course added successfully!");

                    // Refresh the table immediately on EDT
                    SwingUtilities.invokeLater(() -> loadCourses());
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

                    db.disconnect(); // Disconnect immediately after update

                    if (success) {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(ManageCoursesPanel.this,
                                "Course updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh the table immediately on EDT
                        SwingUtilities.invokeLater(() -> loadCourses());
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Failed to update course!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter valid numbers for Credits, Year, Semester, and Capacity!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    db.disconnect();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
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

            // Refresh the table immediately on EDT
            SwingUtilities.invokeLater(() -> loadCourses());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting course: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            db.disconnect();
        }
    }

    private void showAssignLecturerDialog() {
        int selectedRow = coursesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to assign a lecturer!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (int) tableModel.getValueAt(selectedRow, 0);
        String courseCode = (String) tableModel.getValueAt(selectedRow, 1);
        String courseName = (String) tableModel.getValueAt(selectedRow, 2);
        String currentLecturer = (String) tableModel.getValueAt(selectedRow, 7);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Assign Lecturer", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(236, 240, 241));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel courseLabel = new JLabel("Course: " + courseCode + " - " + courseName);
        courseLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel currentLabel = new JLabel("Current Lecturer: " + currentLecturer);
        currentLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        infoPanel.add(courseLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(currentLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Lecturer:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> lecturerCombo = new JComboBox<>();
        lecturerCombo.addItem("-- Unassign Lecturer --");

        // Load all active lecturers
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            String query = "SELECT l.lecturer_id, CONCAT(p.first_name, ' ', p.last_name) as full_name, " +
                    "l.department " +
                    "FROM lecturers l " +
                    "JOIN persons p ON l.person_id = p.person_id " +
                    "WHERE l.status = 'ACTIVE' " +
                    "ORDER BY p.first_name, p.last_name";

            ResultSet rs = db.fetchData(query);

            while (rs != null && rs.next()) {
                int lecturerId = rs.getInt("lecturer_id");
                String fullName = rs.getString("full_name");
                String dept = rs.getString("department");

                lecturerCombo.addItem(lecturerId + " - " + fullName + " (" + dept + ")");
            }

            if (rs != null) {
                rs.close();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog,
                    "Error loading lecturers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        formPanel.add(lecturerCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton assignButton = new JButton("Assign");
        assignButton.setBackground(new Color(155, 89, 182));
        assignButton.setForeground(Color.WHITE);
        assignButton.setFocusPainted(false);
        assignButton.addActionListener(e -> {
            String selectedItem = (String) lecturerCombo.getSelectedItem();

            try {
                db.connect();

                Integer lecturerId = null;

                if (selectedItem != null && !selectedItem.startsWith("--")) {
                    // Extract lecturer ID from the combo box item
                    lecturerId = Integer.parseInt(selectedItem.split(" - ")[0]);
                }

                String updateQuery;
                if (lecturerId == null) {
                    // Unassign lecturer
                    updateQuery = "UPDATE courses SET lecturer_id = NULL WHERE course_id = " + courseId;
                } else {
                    // Assign lecturer
                    updateQuery = "UPDATE courses SET lecturer_id = " + lecturerId + " WHERE course_id = " + courseId;
                }

                int rowsAffected = db.executeUpdate(updateQuery);
                db.disconnect();

                if (rowsAffected > 0) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(ManageCoursesPanel.this,
                            lecturerId == null ? "Lecturer unassigned successfully!"
                                    : "Lecturer assigned successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh the table
                    SwingUtilities.invokeLater(() -> loadCourses());
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Failed to assign lecturer!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                db.disconnect();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);

        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
