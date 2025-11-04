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

        buttonPanel.add(refreshButton);

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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
