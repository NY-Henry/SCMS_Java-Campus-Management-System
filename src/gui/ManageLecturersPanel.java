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
        lecturersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        lecturersTable.getTableHeader().setBackground(new Color(52, 73, 94));
        lecturersTable.getTableHeader().setForeground(Color.WHITE);

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

        buttonPanel.add(refreshButton);

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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading lecturers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
