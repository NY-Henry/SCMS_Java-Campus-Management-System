package gui;

import database.MySQLDatabase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

public class ManageLecturersPanel extends JPanel {
    private JTable lecturersTable;
    private DefaultTableModel tableModel;
    private MySQLDatabase db;

    public ManageLecturersPanel() {
        this.db = MySQLDatabase.getInstance();
        initializeUI();
        loadLecturers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Manage Lecturers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] columns = { "ID", "Employee #", "Full Name", "Department", "Specialization", "Office" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lecturersTable = new JTable(tableModel);
        lecturersTable.setRowHeight(30);
        lecturersTable.setFont(new Font("Arial", Font.PLAIN, 13));

        // Style table header with better visibility
        lecturersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        lecturersTable.getTableHeader().setBackground(new Color(52, 73, 94));
        lecturersTable.getTableHeader().setForeground(Color.BLACK);
        lecturersTable.getTableHeader().setOpaque(true);
        lecturersTable.getTableHeader().setReorderingAllowed(false);
        lecturersTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(lecturersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> loadLecturers());

        JButton addButton = new JButton("Add Lecturer");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.addActionListener(e -> showAddLecturerDialog());

        JButton editButton = new JButton("Edit Lecturer");
        editButton.setBackground(new Color(243, 156, 18));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.addActionListener(e -> showEditLecturerDialog());

        JButton deleteButton = new JButton("Delete Lecturer");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteSelectedLecturer());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadLecturers() {
        tableModel.setRowCount(0);

        try {
            // Ensure fresh connection
            if (!db.isConnected()) {
                db.connect();
            }

            String query = "SELECT l.lecturer_id, l.employee_number, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as full_name, " +
                    "l.department, l.specialization, l.office_location " +
                    "FROM lecturers l " +
                    "JOIN persons p ON l.person_id = p.person_id " +
                    "ORDER BY l.employee_number";

            ResultSet rs = db.fetchData(query);

            while (rs != null && rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("lecturer_id"),
                        rs.getString("employee_number"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("specialization"),
                        rs.getString("office_location")
                });
            }

            // Close ResultSet properly
            if (rs != null) {
                rs.close();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading lecturers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddLecturerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Lecturer", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
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
        JTextField employeeNumberField = new JTextField(20);
        JTextField departmentField = new JTextField(20);
        JTextField specializationField = new JTextField(20);
        JTextField qualificationField = new JTextField(20);
        JTextField officeField = new JTextField(20);

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
        formPanel.add(new JLabel("Employee Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(employeeNumberField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        formPanel.add(departmentField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 1;
        formPanel.add(specializationField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Qualification:"), gbc);
        gbc.gridx = 1;
        formPanel.add(qualificationField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Office Location:"), gbc);
        gbc.gridx = 1;
        formPanel.add(officeField, gbc);

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
                    employeeNumberField.getText().trim().isEmpty() ||
                    departmentField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add lecturer
            try {
                db.connect();

                // 1. Insert into users table
                String insertUser = "INSERT INTO users (username, password, role, email) VALUES (?, ?, 'LECTURER', ?)";
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

                // 3. Insert into lecturers table
                String insertLecturer = "INSERT INTO lecturers (person_id, employee_number, department, specialization, qualification, hire_date, office_location) "
                        +
                        "VALUES (?, ?, ?, ?, ?, CURDATE(), ?)";
                boolean success = db.executePreparedQuery(insertLecturer, new Object[] {
                        personId,
                        employeeNumberField.getText().trim(),
                        departmentField.getText().trim(),
                        specializationField.getText().trim(),
                        qualificationField.getText().trim(),
                        officeField.getText().trim()
                });

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            "Lecturer added successfully!\n\n" +
                                    "Username: " + usernameField.getText().trim() + "\n" +
                                    "Employee Number: " + employeeNumberField.getText().trim(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                    loadLecturers();
                } else {
                    throw new Exception("Failed to create lecturer record");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding lecturer: " + ex.getMessage(),
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

    private void deleteSelectedLecturer() {
        int selectedRow = lecturersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a lecturer to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get lecturer details
        int lecturerId = (int) tableModel.getValueAt(selectedRow, 0);
        String employeeNumber = (String) tableModel.getValueAt(selectedRow, 1);
        String fullName = (String) tableModel.getValueAt(selectedRow, 2);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this lecturer?\n\n" +
                        "Employee Number: " + employeeNumber + "\n" +
                        "Name: " + fullName + "\n\n" +
                        "WARNING: This will delete:\n" +
                        "- Lecturer record\n" +
                        "- All assigned courses (will be unassigned)\n" +
                        "- User account\n\n" +
                        "This action CANNOT be undone!",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            db.connect();

            // Get person_id and user_id for this lecturer
            String getIdsQuery = "SELECT l.person_id, p.user_id " +
                    "FROM lecturers l " +
                    "JOIN persons p ON l.person_id = p.person_id " +
                    "WHERE l.lecturer_id = " + lecturerId;
            ResultSet rs = db.fetchData(getIdsQuery);

            int personId = 0;
            int userId = 0;

            if (rs != null && rs.next()) {
                personId = rs.getInt("person_id");
                userId = rs.getInt("user_id");
            }

            if (personId == 0 || userId == 0) {
                JOptionPane.showMessageDialog(this,
                        "Error: Could not find lecturer details.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Delete in correct order
            // 1. Unassign courses (set lecturer_id to NULL)
            String unassignCourses = "UPDATE courses SET lecturer_id = NULL WHERE lecturer_id = " + lecturerId;
            db.executeUpdate(unassignCourses);

            // 2. Delete lecturer record
            String deleteLecturer = "DELETE FROM lecturers WHERE lecturer_id = " + lecturerId;
            db.executeUpdate(deleteLecturer);

            // 3. Delete person record
            String deletePerson = "DELETE FROM persons WHERE person_id = " + personId;
            db.executeUpdate(deletePerson);

            // 4. Delete user account
            String deleteUser = "DELETE FROM users WHERE user_id = " + userId;
            db.executeUpdate(deleteUser);

            // Close and reopen connection
            db.disconnect();

            JOptionPane.showMessageDialog(this,
                    "Lecturer deleted successfully!\n\n" +
                            "Lecturer: " + fullName + "\n" +
                            "Employee Number: " + employeeNumber,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the table
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                loadLecturers();
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting lecturer: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            db.disconnect();
        }
    }

    private void showEditLecturerDialog() {
        int selectedRow = lecturersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a lecturer to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get lecturer details
        int lecturerId = (int) tableModel.getValueAt(selectedRow, 0);
        String employeeNumber = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            db.connect();

            // Fetch full lecturer details
            String query = "SELECT u.username, u.email, p.first_name, p.last_name, p.phone_number, " +
                    "l.employee_number, l.department, l.specialization, l.qualification, l.office_location " +
                    "FROM lecturers l " +
                    "JOIN persons p ON l.person_id = p.person_id " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "WHERE l.lecturer_id = " + lecturerId;

            ResultSet rs = db.fetchData(query);

            if (rs == null || !rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Error: Could not fetch lecturer details.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create dialog
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Lecturer", true);
            dialog.setSize(500, 600);
            dialog.setLocationRelativeTo(this);
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
            JTextField employeeNumberField = new JTextField(rs.getString("employee_number"), 20);
            employeeNumberField.setEnabled(false); // Employee number cannot be changed
            JTextField departmentField = new JTextField(rs.getString("department"), 20);
            JTextField specializationField = new JTextField(
                    rs.getString("specialization") != null ? rs.getString("specialization") : "", 20);
            JTextField qualificationField = new JTextField(
                    rs.getString("qualification") != null ? rs.getString("qualification") : "", 20);
            JTextField officeField = new JTextField(
                    rs.getString("office_location") != null ? rs.getString("office_location") : "", 20);

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
            formPanel.add(new JLabel("Employee Number:"), gbc);
            gbc.gridx = 1;
            formPanel.add(employeeNumberField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Department:"), gbc);
            gbc.gridx = 1;
            formPanel.add(departmentField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Specialization:"), gbc);
            gbc.gridx = 1;
            formPanel.add(specializationField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Qualification:"), gbc);
            gbc.gridx = 1;
            formPanel.add(qualificationField, gbc);

            row++;
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Office Location:"), gbc);
            gbc.gridx = 1;
            formPanel.add(officeField, gbc);

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
                        departmentField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update lecturer
                try {
                    db.connect();

                    // Get person_id and user_id
                    String getIdsQuery = "SELECT l.person_id, p.user_id " +
                            "FROM lecturers l " +
                            "JOIN persons p ON l.person_id = p.person_id " +
                            "WHERE l.lecturer_id = " + lecturerId;
                    ResultSet idsRs = db.fetchData(getIdsQuery);

                    if (idsRs == null || !idsRs.next()) {
                        throw new Exception("Could not find lecturer IDs");
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

                    // 3. Update lecturers table
                    String updateLecturer = "UPDATE lecturers SET department = ?, specialization = ?, qualification = ?, office_location = ? WHERE lecturer_id = "
                            + lecturerId;
                    boolean success = db.executePreparedQuery(updateLecturer, new Object[] {
                            departmentField.getText().trim(),
                            specializationField.getText().trim(),
                            qualificationField.getText().trim(),
                            officeField.getText().trim()
                    });

                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Lecturer updated successfully!\n\n" +
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
                            loadLecturers();
                        });
                    } else {
                        throw new Exception("Failed to update lecturer record");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error updating lecturer: " + ex.getMessage(),
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
                    "Error loading lecturer details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            db.disconnect();
        }
    }
}
