package gui;

import database.MySQLDatabase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * System Logs Panel - View and filter system activity logs
 */
public class SystemLogsPanel extends JPanel {
    private MySQLDatabase db;
    private JTable logsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private JTextField searchField;
    private JLabel totalLogsLabel;
    private JLabel filteredLogsLabel;
    private TableRowSorter<DefaultTableModel> sorter;

    public SystemLogsPanel(MySQLDatabase db) {
        this.db = db;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));

        initializeComponents();
        loadLogs();
    }

    private void initializeComponents() {
        // Top Panel with title and stats
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("System Logs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setOpaque(false);

        totalLogsLabel = new JLabel("Total Logs: 0");
        totalLogsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        filteredLogsLabel = new JLabel("Filtered: 0");
        filteredLogsLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        statsPanel.add(totalLogsLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        statsPanel.add(filteredLogsLabel);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel filterLabel = new JLabel("Filter by Action:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 12));

        String[] filterOptions = {
                "All Actions",
                "LOGIN",
                "LOGOUT",
                "CREATE",
                "UPDATE",
                "DELETE",
                "VIEW",
                "EXPORT",
                "REGISTER"
        };

        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setPreferredSize(new Dimension(150, 30));
        filterComboBox.addActionListener(e -> applyFilter());

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 30));
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadLogs());

        JButton clearFilterBtn = new JButton("Clear Filters");
        clearFilterBtn.setPreferredSize(new Dimension(120, 30));
        clearFilterBtn.setBackground(new Color(149, 165, 166));
        clearFilterBtn.setForeground(Color.WHITE);
        clearFilterBtn.setFocusPainted(false);
        clearFilterBtn.addActionListener(e -> clearFilters());

        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        filterPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filterPanel.add(refreshBtn);
        filterPanel.add(clearFilterBtn);

        return filterPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        String[] columns = {"Log ID", "Timestamp", "User", "Role", "Action", "Details", "IP Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logsTable = new JTable(tableModel);
        logsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        logsTable.setRowHeight(30);
        logsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        logsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        logsTable.getTableHeader().setForeground(Color.WHITE);

        // Set column widths
        logsTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Log ID
        logsTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Timestamp
        logsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // User
        logsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Role
        logsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Action
        logsTable.getColumnModel().getColumn(5).setPreferredWidth(300); // Details
        logsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // IP Address

        // Add row sorter
        sorter = new TableRowSorter<>(tableModel);
        logsTable.setRowSorter(sorter);

        // Add double-click listener for details
        logsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = logsTable.getSelectedRow();
                    if (row >= 0) {
                        showLogDetails(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(logsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);

        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setPreferredSize(new Dimension(150, 35));
        exportBtn.setBackground(new Color(46, 204, 113));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFont(new Font("Arial", Font.BOLD, 12));
        exportBtn.setFocusPainted(false);
        exportBtn.addActionListener(e -> exportLogs());

        JButton clearOldLogsBtn = new JButton("Clear Old Logs");
        clearOldLogsBtn.setPreferredSize(new Dimension(150, 35));
        clearOldLogsBtn.setBackground(new Color(231, 76, 60));
        clearOldLogsBtn.setForeground(Color.WHITE);
        clearOldLogsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearOldLogsBtn.setFocusPainted(false);
        clearOldLogsBtn.addActionListener(e -> clearOldLogs());

        JButton viewStatsBtn = new JButton("View Statistics");
        viewStatsBtn.setPreferredSize(new Dimension(150, 35));
        viewStatsBtn.setBackground(new Color(155, 89, 182));
        viewStatsBtn.setForeground(Color.WHITE);
        viewStatsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        viewStatsBtn.setFocusPainted(false);
        viewStatsBtn.addActionListener(e -> showStatistics());

        actionPanel.add(viewStatsBtn);
        actionPanel.add(exportBtn);
        actionPanel.add(clearOldLogsBtn);

        return actionPanel;
    }

    private void loadLogs() {
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            tableModel.setRowCount(0);

            String sql = "SELECT l.log_id, l.timestamp, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as user_name, " +
                    "u.role, l.action, l.details, l.ip_address " +
                    "FROM system_logs l " +
                    "LEFT JOIN users u ON l.user_id = u.user_id " +
                    "LEFT JOIN persons p ON u.user_id = p.user_id " +
                    "ORDER BY l.timestamp DESC";

            ResultSet rs = db.executePreparedSelect(sql, new Object[]{});
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int count = 0;

            if (rs != null) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("log_id"),
                            dateFormat.format(rs.getTimestamp("timestamp")),
                            rs.getString("user_name") != null ? rs.getString("user_name") : "System",
                            rs.getString("role") != null ? rs.getString("role") : "SYSTEM",
                            rs.getString("action"),
                            rs.getString("details"),
                            rs.getString("ip_address") != null ? rs.getString("ip_address") : "N/A"
                    };
                    tableModel.addRow(row);
                    count++;
                }
                rs.close();
            }

            totalLogsLabel.setText("Total Logs: " + count);
            filteredLogsLabel.setText("Filtered: " + count);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading logs: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                // Filter by action type
                String selectedAction = (String) filterComboBox.getSelectedItem();
                String action = entry.getStringValue(4); // Action column
                if (!selectedAction.equals("All Actions") && !action.contains(selectedAction)) {
                    return false;
                }

                // Filter by search text
                String searchText = searchField.getText().toLowerCase();
                if (!searchText.isEmpty()) {
                    boolean found = false;
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).toLowerCase().contains(searchText)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return false;
                    }
                }

                return true;
            }
        };

        sorter.setRowFilter(filter);
        filteredLogsLabel.setText("Filtered: " + logsTable.getRowCount());
    }

    private void clearFilters() {
        filterComboBox.setSelectedIndex(0);
        searchField.setText("");
        applyFilter();
    }

    private void showLogDetails(int row) {
        int modelRow = logsTable.convertRowIndexToModel(row);

        String logId = tableModel.getValueAt(modelRow, 0).toString();
        String timestamp = tableModel.getValueAt(modelRow, 1).toString();
        String user = tableModel.getValueAt(modelRow, 2).toString();
        String role = tableModel.getValueAt(modelRow, 3).toString();
        String action = tableModel.getValueAt(modelRow, 4).toString();
        String details = tableModel.getValueAt(modelRow, 5).toString();
        String ipAddress = tableModel.getValueAt(modelRow, 6).toString();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Log Details", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font valueFont = new Font("Arial", Font.PLAIN, 14);

        addDetailRow(panel, "Log ID:", logId, labelFont, valueFont);
        addDetailRow(panel, "Timestamp:", timestamp, labelFont, valueFont);
        addDetailRow(panel, "User:", user, labelFont, valueFont);
        addDetailRow(panel, "Role:", role, labelFont, valueFont);
        addDetailRow(panel, "Action:", action, labelFont, valueFont);
        addDetailRow(panel, "IP Address:", ipAddress, labelFont, valueFont);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel detailsLabel = new JLabel("Details:");
        detailsLabel.setFont(labelFont);
        detailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(detailsLabel);

        JTextArea detailsArea = new JTextArea(details);
        detailsArea.setFont(valueFont);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setPreferredSize(new Dimension(550, 150));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollPane);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.setBackground(new Color(52, 152, 219));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());
        panel.add(closeBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value, Font labelFont, Font valueFont) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(labelFont);
        labelComp.setPreferredSize(new Dimension(120, 25));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(valueFont);

        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
    }

    private void exportLogs() {
        try {
            // Create reports directory if it doesn't exist
            java.io.File reportsDir = new java.io.File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String filename = "reports/system_logs_" + System.currentTimeMillis() + ".csv";
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);

            // Write header
            writer.println("SYSTEM LOGS REPORT - Generated: " + new Date());
            writer.println();
            writer.println("Log ID,Timestamp,User,Role,Action,Details,IP Address");

            // Write data (visible rows only - respects filters)
            for (int i = 0; i < logsTable.getRowCount(); i++) {
                for (int j = 0; j < logsTable.getColumnCount(); j++) {
                    writer.print("\"" + logsTable.getValueAt(i, j).toString().replace("\"", "\"\"") + "\"");
                    if (j < logsTable.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }

            writer.close();

            JOptionPane.showMessageDialog(this,
                    "System logs exported successfully!\n\nLocation: " + filename,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error exporting logs: " + e.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearOldLogs() {
        String[] options = {"Last 7 Days", "Last 30 Days", "Last 90 Days", "Last Year", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "Delete logs older than:",
                "Clear Old Logs",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[4]);

        if (choice == 4 || choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        int days = 0;
        switch (choice) {
            case 0:
                days = 7;
                break;
            case 1:
                days = 30;
                break;
            case 2:
                days = 90;
                break;
            case 3:
                days = 365;
                break;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete logs older than " + days + " days?\nThis action cannot be undone!",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (!db.isConnected()) {
                    db.connect();
                }

                String sql = "DELETE FROM system_logs WHERE timestamp < DATE_SUB(NOW(), INTERVAL ? DAY)";
                boolean deleted = db.executePreparedQuery(sql, new Object[]{days});

                if (deleted) {
                    JOptionPane.showMessageDialog(this,
                            "Old log entries deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No logs were deleted.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                loadLogs();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error deleting logs: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStatistics() {
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Log Statistics", true);
            dialog.setSize(700, 600);
            dialog.setLocationRelativeTo(this);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(Color.WHITE);

            JLabel titleLabel = new JLabel("System Activity Statistics");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(titleLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Get statistics
            String[][] stats = new String[10][2];
            int index = 0;

            // Total logs
            ResultSet rs = db.executePreparedSelect("SELECT COUNT(*) as count FROM system_logs", new Object[]{});
            if (rs != null && rs.next()) {
                stats[index++] = new String[]{"Total Log Entries", String.valueOf(rs.getInt("count"))};
                rs.close();
            }

            // Logs today
            rs = db.executePreparedSelect(
                    "SELECT COUNT(*) as count FROM system_logs WHERE DATE(timestamp) = CURDATE()", new Object[]{});
            if (rs != null && rs.next()) {
                stats[index++] = new String[]{"Logs Today", String.valueOf(rs.getInt("count"))};
                rs.close();
            }

            // Logs this week
            rs = db.executePreparedSelect(
                    "SELECT COUNT(*) as count FROM system_logs WHERE timestamp >= DATE_SUB(NOW(), INTERVAL 7 DAY)",
                    new Object[]{});
            if (rs != null && rs.next()) {
                stats[index++] = new String[]{"Logs This Week", String.valueOf(rs.getInt("count"))};
                rs.close();
            }

            // Most active user
            rs = db.executePreparedSelect(
                    "SELECT CONCAT(p.first_name, ' ', p.last_name) as name, COUNT(*) as count " +
                            "FROM system_logs l " +
                            "JOIN users u ON l.user_id = u.user_id " +
                            "JOIN persons p ON u.user_id = p.user_id " +
                            "GROUP BY l.user_id ORDER BY count DESC LIMIT 1",
                    new Object[]{});
            if (rs != null && rs.next()) {
                stats[index++] = new String[]{"Most Active User", rs.getString("name") + " (" + rs.getInt("count") + " actions)"};
                rs.close();
            }

            // Most common action
            rs = db.executePreparedSelect(
                    "SELECT action, COUNT(*) as count FROM system_logs GROUP BY action ORDER BY count DESC LIMIT 1",
                    new Object[]{});
            if (rs != null && rs.next()) {
                stats[index++] = new String[]{"Most Common Action", rs.getString("action") + " (" + rs.getInt("count") + " times)"};
                rs.close();
            }

            // Login count today
            rs = db.executePreparedSelect(
                    "SELECT COUNT(*) as count FROM system_logs WHERE action = 'LOGIN' AND DATE(timestamp) = CURDATE()",
                    new Object[]{});
            if (rs != null && rs.next()) {
                stats[index++] = new String[]{"Logins Today", String.valueOf(rs.getInt("count"))};
                rs.close();
            }

            // Failed logins (if you track them)
            rs = db.executePreparedSelect(
                    "SELECT COUNT(*) as count FROM system_logs WHERE action LIKE '%FAILED%'", new Object[]{});
            if (rs != null && rs.next()) {
                stats[index++] = new String[]{"Failed Login Attempts", String.valueOf(rs.getInt("count"))};
                rs.close();
            }

            // Actions by role
            rs = db.executePreparedSelect(
                    "SELECT u.role, COUNT(*) as count FROM system_logs l " +
                            "JOIN users u ON l.user_id = u.user_id GROUP BY u.role",
                    new Object[]{});
            StringBuilder roleStats = new StringBuilder();
            if (rs != null) {
                while (rs.next()) {
                    if (roleStats.length() > 0) roleStats.append(", ");
                    roleStats.append(rs.getString("role")).append(": ").append(rs.getInt("count"));
                }
                rs.close();
                stats[index++] = new String[]{"Actions by Role", roleStats.toString()};
            }

            // Create stats display
            for (int i = 0; i < index; i++) {
                JPanel statRow = new JPanel(new BorderLayout(10, 10));
                statRow.setBackground(new Color(236, 240, 241));
                statRow.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                statRow.setMaximumSize(new Dimension(650, 50));
                statRow.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel label = new JLabel(stats[i][0]);
                label.setFont(new Font("Arial", Font.BOLD, 14));

                JLabel value = new JLabel(stats[i][1]);
                value.setFont(new Font("Arial", Font.PLAIN, 14));

                statRow.add(label, BorderLayout.WEST);
                statRow.add(value, BorderLayout.EAST);

                mainPanel.add(statRow);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JButton closeBtn = new JButton("Close");
            closeBtn.setPreferredSize(new Dimension(100, 35));
            closeBtn.setBackground(new Color(52, 152, 219));
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setFocusPainted(false);
            closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeBtn.addActionListener(e -> dialog.dispose());
            mainPanel.add(closeBtn);

            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            dialog.add(scrollPane);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading statistics: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
