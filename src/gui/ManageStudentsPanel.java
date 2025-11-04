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

        String[] columns = { "ID", "Reg Number", "Full Name", "Program", "Year", "GPA", "Status" };
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
        studentsTable.getTableHeader().setForeground(Color.WHITE);

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

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteSelectedStudent());

        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);

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
                    "s.program, s.year_of_study, s.gpa, s.status " +
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
}
