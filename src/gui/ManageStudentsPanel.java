package gui;

import database.MySQLDatabase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

public class ManageStudentsPanel extends JPanel {
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private MySQLDatabase db;

    public ManageStudentsPanel() {
        this.db = MySQLDatabase.getInstance();
        initializeUI();
        loadStudents();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Manage Students");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] columns = { "ID", "Reg Number", "Full Name", "Program", "Year", "GPA", "Fee Balance", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentsTable = new JTable(tableModel);
        studentsTable.setRowHeight(30);
        studentsTable.setFont(new Font("Arial", Font.PLAIN, 13));

        // Style table header with better visibility
        studentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        studentsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        studentsTable.getTableHeader().setForeground(Color.BLACK);
        studentsTable.getTableHeader().setOpaque(true);
        studentsTable.getTableHeader().setReorderingAllowed(false);
        studentsTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> loadStudents());

        JButton addButton = new JButton("Add Student");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.addActionListener(e -> showAddStudentDialog());

        JButton editButton = new JButton("Edit Student");
        editButton.setBackground(new Color(243, 156, 18));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.addActionListener(e -> showEditStudentDialog());

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteSelectedStudent());

        JButton updateFeeButton = new JButton("Update Fee Balance");
        updateFeeButton.setBackground(new Color(155, 89, 182));
        updateFeeButton.setForeground(Color.WHITE);
        updateFeeButton.setFocusPainted(false);
        updateFeeButton.setBorderPainted(false);
        updateFeeButton.addActionListener(e -> showUpdateFeeBalanceDialog());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateFeeButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStudents() {
        tableModel.setRowCount(0);

        try {
            // Ensure fresh connection
            if (!db.isConnected()) {
                db.connect();
            }

            String query = "SELECT s.student_id, s.registration_number, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as full_name, " +
                    "s.program, s.year_of_study, s.gpa, s.fee_balance, s.status " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "ORDER BY s.registration_number";

            ResultSet rs = db.fetchData(query);

            while (rs != null && rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("student_id"),
                        rs.getString("registration_number"),
                        rs.getString("full_name"),
                        rs.getString("program"),
                        rs.getInt("year_of_study"),
                        String.format("%.2f", rs.getDouble("gpa")),
                        String.format("%.2f", rs.getDouble("fee_balance")),
                        rs.getString("status")
                });
            }

            // Close ResultSet properly
            if (rs != null) {
                rs.close();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get student details
        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        String regNumber = (String) tableModel.getValueAt(selectedRow, 1);
        String fullName = (String) tableModel.getValueAt(selectedRow, 2);

        // Debug output
        System.out.println("DEBUG: Attempting to delete student_id = " + studentId);
        System.out.println("DEBUG: Registration Number = " + regNumber);
        System.out.println("DEBUG: Full Name = " + fullName);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student?\n\n" +
                        "Student ID: " + studentId + "\n" +
                        "Registration Number: " + regNumber + "\n" +
                        "Name: " + fullName + "\n\n" +
                        "WARNING: This will delete:\n" +
                        "- Student record\n" +
                        "- All course registrations\n" +
                        "- All grades\n" +
                        "- All payment records\n" +
                        "- User account\n\n" +
                        "This action CANNOT be undone!",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            System.out.println("DEBUG: Deletion cancelled by user");
            return;
        }

        try {
            db.connect();

            // Get person_id and user_id for this student (need to join with persons table)
            String getIdsQuery = "SELECT s.person_id, p.user_id " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "WHERE s.student_id = " + studentId;
            System.out.println("DEBUG: Executing query: " + getIdsQuery);
            ResultSet rs = db.fetchData(getIdsQuery);

            int personId = 0;
            int userId = 0;

            if (rs != null && rs.next()) {
                personId = rs.getInt("person_id");
                userId = rs.getInt("user_id");
                System.out.println("DEBUG: Found person_id = " + personId + ", user_id = " + userId);
            }

            if (personId == 0 || userId == 0) {
                JOptionPane.showMessageDialog(this,
                        "Error: Could not find student details.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("DEBUG: ERROR - person_id or user_id is 0");
                return;
            }

            // Delete in correct order (due to foreign key constraints)
            int rowsAffected = 0;

            // 1. Delete grades
            String deleteGrades = "DELETE FROM grades WHERE student_id = " + studentId;
            System.out.println("DEBUG: Executing: " + deleteGrades);
            rowsAffected = db.executeUpdate(deleteGrades);
            System.out.println("DEBUG: Deleted " + rowsAffected + " grades");

            // 2. Delete course registrations
            String deleteRegistrations = "DELETE FROM course_registrations WHERE student_id = " + studentId;
            System.out.println("DEBUG: Executing: " + deleteRegistrations);
            rowsAffected = db.executeUpdate(deleteRegistrations);
            System.out.println("DEBUG: Deleted " + rowsAffected + " registrations");

            // 3. Delete payments
            String deletePayments = "DELETE FROM payments WHERE student_id = " + studentId;
            System.out.println("DEBUG: Executing: " + deletePayments);
            rowsAffected = db.executeUpdate(deletePayments);
            System.out.println("DEBUG: Deleted " + rowsAffected + " payments");

            // 4. Delete attendance records
            String deleteAttendance = "DELETE FROM attendance WHERE student_id = " + studentId;
            System.out.println("DEBUG: Executing: " + deleteAttendance);
            rowsAffected = db.executeUpdate(deleteAttendance);
            System.out.println("DEBUG: Deleted " + rowsAffected + " attendance records");

            // 5. Delete student record
            String deleteStudent = "DELETE FROM students WHERE student_id = " + studentId;
            System.out.println("DEBUG: Executing: " + deleteStudent);
            rowsAffected = db.executeUpdate(deleteStudent);
            System.out.println("DEBUG: Deleted " + rowsAffected + " student record(s)");

            // 6. Delete person record
            String deletePerson = "DELETE FROM persons WHERE person_id = " + personId;
            System.out.println("DEBUG: Executing: " + deletePerson);
            rowsAffected = db.executeUpdate(deletePerson);
            System.out.println("DEBUG: Deleted " + rowsAffected + " person record(s)");

            // 7. Delete user account
            String deleteUser = "DELETE FROM users WHERE user_id = " + userId;
            System.out.println("DEBUG: Executing: " + deleteUser);
            rowsAffected = db.executeUpdate(deleteUser);
            System.out.println("DEBUG: Deleted " + rowsAffected + " user record(s)");

            // Close and reopen connection to ensure fresh state
            db.disconnect();

            JOptionPane.showMessageDialog(this,
                    "Student deleted successfully!\n\n" +
                            "Student: " + fullName + "\n" +
                            "Registration: " + regNumber,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            System.out.println("DEBUG: Deletion completed successfully");

            // Refresh the table with a small delay to ensure DB is updated
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100); // Small delay to ensure DB is updated
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                loadStudents();
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting student: " + e.getMessage() + "\n\n" +
                            "The student may have related records that prevent deletion.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("DEBUG: ERROR during deletion:");
            e.printStackTrace();
            db.disconnect();
        }
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Student", true);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField emailField = new JTextField(20);
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField regNumberField = new JTextField(20);
        JTextField programField = new JTextField(20);
        JComboBox<Integer> yearComboBox = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
        JComboBox<Integer> semesterComboBox = new JComboBox<>(new Integer[] { 1, 2 });
        JTextField feeBalanceField = new JTextField("0.00", 20);

        // Add labels and fields
        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Registration Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(regNumberField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Program:"), gbc);
        gbc.gridx = 1;
        formPanel.add(programField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Year of Study:"), gbc);
        gbc.gridx = 1;
        formPanel.add(yearComboBox, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        formPanel.add(semesterComboBox, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Fee Balance:"), gbc);
        gbc.gridx = 1;
        formPanel.add(feeBalanceField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (usernameField.getText().trim().isEmpty() ||
                    passwordField.getPassword().length == 0 ||
                    emailField.getText().trim().isEmpty() ||
                    firstNameField.getText().trim().isEmpty() ||
                    lastNameField.getText().trim().isEmpty() ||
                    regNumberField.getText().trim().isEmpty() ||
                    programField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add student
            try {
                db.connect();

                // 1. Insert into users table
                String insertUser = "INSERT INTO users (username, password, role, email) VALUES (?, ?, 'STUDENT', ?)";
                int userId = db.executeInsertAndGetId(insertUser, new Object[] {
                        usernameField.getText().trim(),
                        new String(passwordField.getPassword()),
                        emailField.getText().trim()
                });

                if (userId == -1) {
                    throw new Exception("Failed to create user account");
                }

                // 2. Insert into persons table
                String insertPerson = "INSERT INTO persons (user_id, first_name, last_name, phone_number) VALUES (?, ?, ?, ?)";
                int personId = db.executeInsertAndGetId(insertPerson, new Object[] {
                        userId,
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        phoneField.getText().trim()
                });

                if (personId == -1) {
                    throw new Exception("Failed to create person record");
                }

                // 3. Insert into students table
                String insertStudent = "INSERT INTO students (person_id, registration_number, program, year_of_study, semester, enrollment_date, fee_balance) "
                        +
                        "VALUES (?, ?, ?, ?, ?, CURDATE(), ?)";
                boolean success = db.executePreparedQuery(insertStudent, new Object[] {
                        personId,
                        regNumberField.getText().trim(),
                        programField.getText().trim(),
                        yearComboBox.getSelectedItem(),
                        semesterComboBox.getSelectedItem(),
                        Double.parseDouble(feeBalanceField.getText().trim())
                });

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            "Student added successfully!\n\n" +
                                    "Username: " + usernameField.getText().trim() + "\n" +
                                    "Registration Number: " + regNumberField.getText().trim(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();

                    // Refresh the table
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        loadStudents();
                    });
                } else {
                    throw new Exception("Failed to create student record");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Invalid fee balance format! Please enter a valid number.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding student: " + ex.getMessage(),
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

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditStudentDialog() {
        int selectedRow = studentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get student details
        int studentId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            db.connect();

            // Fetch full student details
            String query = "SELECT u.username, u.email, p.first_name, p.last_name, p.phone_number, " +
                    "s.registration_number, s.program, s.year_of_study, s.semester, s.fee_balance, s.status " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "WHERE s.student_id = " + studentId;

            ResultSet rs = db.fetchData(query);

            if (rs == null || !rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Error: Could not fetch student details.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create dialog
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Student", true);
            dialog.setSize(500, 700);
            dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
            dialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Form fields - pre-populated with existing data
            JTextField usernameField = new JTextField(rs.getString("username"), 20);
            usernameField.setEnabled(false); // Username cannot be changed
            JTextField emailField = new JTextField(rs.getString("email"), 20);
            JTextField firstNameField = new JTextField(rs.getString("first_name"), 20);
            JTextField lastNameField = new JTextField(rs.getString("last_name"), 20);
            JTextField phoneField = new JTextField(
                    rs.getString("phone_number") != null ? rs.getString("phone_number") : "", 20);
            JTextField regNumberField = new JTextField(rs.getString("registration_number"), 20);
            regNumberField.setEnabled(false); // Registration number cannot be changed
            JTextField programField = new JTextField(rs.getString("program"), 20);
            JComboBox<Integer> yearComboBox = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
            yearComboBox.setSelectedItem(rs.getInt("year_of_study"));
            JComboBox<Integer> semesterComboBox = new JComboBox<>(new Integer[] { 1, 2 });
            semesterComboBox.setSelectedItem(rs.getInt("semester"));
            JTextField feeBalanceField = new JTextField(String.format("%.2f", rs.getDouble("fee_balance")), 20);
            JComboBox<String> statusComboBox = new JComboBox<>(
                    new String[] { "ACTIVE", "SUSPENDED", "GRADUATED", "WITHDRAWN" });
            statusComboBox.setSelectedItem(rs.getString("status"));

            rs.close();
            db.disconnect();

            // Add labels and fields
            int row = 0;

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            formPanel.add(usernameField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            formPanel.add(emailField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("First Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(firstNameField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Last Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(lastNameField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Phone Number:"), gbc);
            gbc.gridx = 1;
            formPanel.add(phoneField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Registration Number:"), gbc);
            gbc.gridx = 1;
            formPanel.add(regNumberField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Program:"), gbc);
            gbc.gridx = 1;
            formPanel.add(programField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Year of Study:"), gbc);
            gbc.gridx = 1;
            formPanel.add(yearComboBox, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Semester:"), gbc);
            gbc.gridx = 1;
            formPanel.add(semesterComboBox, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Fee Balance:"), gbc);
            gbc.gridx = 1;
            formPanel.add(feeBalanceField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            formPanel.add(statusComboBox, gbc);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);

            JButton saveButton = new JButton("Save Changes");
            saveButton.setBackground(new Color(243, 156, 18));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);
            saveButton.addActionListener(e -> {
                // Validate inputs
                if (emailField.getText().trim().isEmpty() ||
                        firstNameField.getText().trim().isEmpty() ||
                        lastNameField.getText().trim().isEmpty() ||
                        programField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update student
                try {
                    db.connect();

                    // Get person_id and user_id
                    String getIdsQuery = "SELECT s.person_id, p.user_id " +
                            "FROM students s " +
                            "JOIN persons p ON s.person_id = p.person_id " +
                            "WHERE s.student_id = " + studentId;
                    ResultSet idsRs = db.fetchData(getIdsQuery);

                    if (idsRs == null || !idsRs.next()) {
                        throw new Exception("Could not find student IDs");
                    }

                    int personId = idsRs.getInt("person_id");
                    int userId = idsRs.getInt("user_id");
                    idsRs.close();

                    // 1. Update users table
                    String updateUser = "UPDATE users SET email = ? WHERE user_id = " + userId;
                    db.executePreparedQuery(updateUser, new Object[] {
                            emailField.getText().trim()
                    });

                    // 2. Update persons table
                    String updatePerson = "UPDATE persons SET first_name = ?, last_name = ?, phone_number = ? WHERE person_id = "
                            + personId;
                    db.executePreparedQuery(updatePerson, new Object[] {
                            firstNameField.getText().trim(),
                            lastNameField.getText().trim(),
                            phoneField.getText().trim()
                    });

                    // 3. Update students table
                    String updateStudent = "UPDATE students SET program = ?, year_of_study = ?, semester = ?, fee_balance = ?, status = ? WHERE student_id = "
                            + studentId;
                    boolean success = db.executePreparedQuery(updateStudent, new Object[] {
                            programField.getText().trim(),
                            yearComboBox.getSelectedItem(),
                            semesterComboBox.getSelectedItem(),
                            Double.parseDouble(feeBalanceField.getText().trim()),
                            statusComboBox.getSelectedItem()
                    });

                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Student updated successfully!\n\n" +
                                        "Name: " + firstNameField.getText() + " " + lastNameField.getText(),
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        dialog.dispose();

                        // Refresh the table
                        SwingUtilities.invokeLater(() -> {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            loadStudents();
                        });
                    } else {
                        throw new Exception("Failed to update student record");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Invalid fee balance format! Please enter a valid number.",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error updating student: " + ex.getMessage(),
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

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading student details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            db.disconnect();
        }
    }

    private void showUpdateFeeBalanceDialog() {
        int selectedRow = studentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to update fee balance.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get student details
        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        String regNumber = (String) tableModel.getValueAt(selectedRow, 1);
        String fullName = (String) tableModel.getValueAt(selectedRow, 2);

        try {
            if (!db.isConnected()) {
                db.connect();
            }

            // Fetch current fee balance
            String query = "SELECT fee_balance FROM students WHERE student_id = ?";
            ResultSet rs = db.executePreparedSelect(query, new Object[] { studentId });

            if (rs == null || !rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Error: Could not fetch student details.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double currentBalance = rs.getDouble("fee_balance");
            rs.close();

            // Create dialog
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Update Fee Balance", true);
            dialog.setSize(500, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            // Title Panel
            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(new Color(155, 89, 182));
            titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel titleLabel = new JLabel("Update Student Fee Balance");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            titlePanel.add(titleLabel);

            // Form Panel
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 5, 10, 5);

            int row = 0;

            // Student Info
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel("Student:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JLabel studentLabel = new JLabel(regNumber + " - " + fullName);
            studentLabel.setFont(new Font("Arial", Font.BOLD, 14));
            formPanel.add(studentLabel, gbc);

            row++;

            // Current Balance
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel("Current Balance:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JLabel currentBalanceLabel = new JLabel(String.format("UGX %.2f", currentBalance));
            currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
            currentBalanceLabel.setForeground(currentBalance > 0 ? new Color(231, 76, 60) : new Color(46, 204, 113));
            formPanel.add(currentBalanceLabel, gbc);

            row++;

            // New Balance
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel("New Balance (UGX):"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JTextField newBalanceField = new JTextField(String.format("%.2f", currentBalance));
            newBalanceField.setPreferredSize(new Dimension(300, 30));
            newBalanceField.setFont(new Font("Arial", Font.PLAIN, 14));
            formPanel.add(newBalanceField, gbc);

            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);

            JButton updateBtn = new JButton("Update Balance");
            updateBtn.setBackground(new Color(46, 204, 113));
            updateBtn.setForeground(Color.WHITE);
            updateBtn.setFont(new Font("Arial", Font.BOLD, 14));
            updateBtn.setFocusPainted(false);
            updateBtn.setBorderPainted(false);
            updateBtn.addActionListener(e -> {
                String newBalanceStr = newBalanceField.getText().trim();

                if (newBalanceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a balance amount.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double newBalance;
                try {
                    newBalance = Double.parseDouble(newBalanceStr);
                    if (newBalance < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a valid non-negative amount.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Confirm update
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        String.format("Update fee balance from UGX %.2f to UGX %.2f?", currentBalance, newBalance),
                        "Confirm Update",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        String updateQuery = "UPDATE students SET fee_balance = ? WHERE student_id = ?";
                        boolean success = db.executePreparedQuery(updateQuery,
                                new Object[] { newBalance, studentId });

                        if (success) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Fee balance updated successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadStudents(); // Refresh table
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                    "Failed to update fee balance.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Error updating fee balance: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(new Color(149, 165, 166));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
            cancelBtn.setFocusPainted(false);
            cancelBtn.setBorderPainted(false);
            cancelBtn.addActionListener(e -> dialog.dispose());

            buttonPanel.add(updateBtn);
            buttonPanel.add(cancelBtn);

            // Layout
            dialog.add(titlePanel, BorderLayout.NORTH);
            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading student details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
