package gui;

import database.MySQLDatabase;
import services.PaymentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Reports panel for administrative reporting and analytics
 */
public class ReportsPanel extends JPanel {
    private final MySQLDatabase db;

    // Report data labels
    private JLabel totalStudentsLabel;
    private JLabel activeStudentsLabel;
    private JLabel totalLecturersLabel;
    private JLabel totalCoursesLabel;

    private JLabel totalFeesLabel;
    private JLabel totalPaidLabel;
    private JLabel totalOutstandingLabel;
    private JLabel totalPaymentsLabel;

    private JLabel totalAnnouncementsLabel;
    private JLabel activeAnnouncementsLabel;

    public ReportsPanel(MySQLDatabase db, PaymentService paymentService) {
        this.db = db;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        initializeUI();
        loadReportData();
    }

    private void initializeUI() {
        // Top panel with title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton exportBtn = createMinimalButton("Export", new Color(70, 130, 180));
        exportBtn.addActionListener(e -> exportStudentReport());

        JButton refreshButton = createMinimalButton("Refresh", new Color(100, 100, 110));
        refreshButton.addActionListener(e -> loadReportData());

        buttonPanel.add(exportBtn);
        buttonPanel.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Main content with tabbed sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setOpaque(false);

        tabbedPane.addTab("Students", createStudentReportTable());
        tabbedPane.addTab("Financial", createFinancialReportTable());
        tabbedPane.addTab("Academic", createAcademicReportTable());
        tabbedPane.addTab("System", createSystemActivityTable());

        add(tabbedPane, BorderLayout.CENTER);
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

    private JPanel createStudentReportTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = { "Metric", "Count" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createMinimalTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFinancialReportTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = { "Metric", "Amount" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createMinimalTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAcademicReportTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = { "Metric", "Count" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createMinimalTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSystemActivityTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = { "Metric", "Count" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createMinimalTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createMinimalTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 245));
        table.setSelectionBackground(new Color(245, 247, 250));
        table.setSelectionForeground(new Color(45, 45, 45));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(new Color(120, 120, 120));
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)));

        return table;
    }

    private JPanel createStudentReportSection() {
        JPanel section = new JPanel(new GridLayout(2, 2, 20, 20));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Total Students Card
        totalStudentsLabel = new JLabel("0");
        section.add(createMinimalStatCard("Total Students", totalStudentsLabel, "\u25CF", new Color(70, 130, 180)));

        // Active Students Card
        activeStudentsLabel = new JLabel("0");
        section.add(createMinimalStatCard("Active Students", activeStudentsLabel, "\u25CF", new Color(100, 180, 100)));

        // Inactive Students
        JLabel inactiveStudentsLabel = new JLabel("0");
        section.add(
                createMinimalStatCard("Inactive Students", inactiveStudentsLabel, "\u25CF", new Color(200, 100, 100)));

        // View Details Button Card
        JPanel buttonCard = createMinimalStatCard("", new JLabel(""), "\u25B2", new Color(100, 100, 110));
        JButton viewDetailsButton = createMinimalButton("View Details", new Color(100, 100, 110));
        viewDetailsButton.addActionListener(e -> showStudentDetailedReport());
        buttonCard.removeAll();
        buttonCard.setLayout(new GridBagLayout());
        buttonCard.add(viewDetailsButton);
        section.add(buttonCard);

        return section;
    }

    private JPanel createFinancialReportSection() {
        JPanel section = new JPanel(new GridLayout(2, 2, 20, 20));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Total Fees Expected
        totalFeesLabel = new JLabel("UGX 0.00");
        section.add(createMinimalStatCard("Total Fees Expected", totalFeesLabel, "\u25A0", new Color(70, 130, 180)));

        // Total Collected
        totalPaidLabel = new JLabel("UGX 0.00");
        section.add(createMinimalStatCard("Total Collected", totalPaidLabel, "\u25A0", new Color(100, 180, 100)));

        // Outstanding Balance
        totalOutstandingLabel = new JLabel("UGX 0.00");
        section.add(createMinimalStatCard("Outstanding Balance", totalOutstandingLabel, "\u25A0",
                new Color(200, 100, 100)));

        // Total Transactions
        totalPaymentsLabel = new JLabel("0");
        section.add(
                createMinimalStatCard("Total Transactions", totalPaymentsLabel, "\u25A0", new Color(140, 100, 160)));

        return section;
    }

    private JPanel createAcademicReportSection() {
        JPanel section = new JPanel(new GridLayout(2, 2, 20, 20));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Total Courses
        totalCoursesLabel = new JLabel("0");
        section.add(createMinimalStatCard("Total Courses", totalCoursesLabel, "\u25B2", new Color(140, 100, 160)));

        // Total Lecturers
        totalLecturersLabel = new JLabel("0");
        section.add(createMinimalStatCard("Total Lecturers", totalLecturersLabel, "\u25B2", new Color(70, 130, 180)));

        // Course Registrations
        JLabel totalRegistrationsLabel = new JLabel("0");
        section.add(createMinimalStatCard("Course Registrations", totalRegistrationsLabel, "\u25B2",
                new Color(100, 180, 100)));

        // Grades Entered
        JLabel totalGradesLabel = new JLabel("0");
        section.add(createMinimalStatCard("Grades Entered", totalGradesLabel, "\u25B2", new Color(210, 140, 80)));

        return section;
    }

    private JPanel createSystemActivitySection() {
        JPanel section = new JPanel(new GridLayout(1, 3, 20, 20));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // Total Announcements
        totalAnnouncementsLabel = new JLabel("0");
        section.add(createMinimalStatCard("Total Announcements", totalAnnouncementsLabel, "\u2713",
                new Color(70, 130, 180)));

        // Active Announcements
        activeAnnouncementsLabel = new JLabel("0");
        section.add(createMinimalStatCard("Active Announcements", activeAnnouncementsLabel, "\u2713",
                new Color(100, 180, 100)));

        // Total Users
        JLabel totalUsersLabel = new JLabel("0");
        section.add(createMinimalStatCard("Total Users", totalUsersLabel, "\u2713", new Color(140, 100, 160)));

        return section;
    }

    private JPanel createExportSection() {
        JPanel section = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton exportStudentsBtn = createMinimalButton("Export Students", new Color(70, 130, 180));
        exportStudentsBtn.addActionListener(e -> exportStudentReport());
        section.add(exportStudentsBtn);

        JButton exportFinancialBtn = createMinimalButton("Export Financial", new Color(100, 100, 110));
        exportFinancialBtn.addActionListener(e -> exportFinancialReport());
        section.add(exportFinancialBtn);

        JButton exportAcademicBtn = createMinimalButton("Export Academic", new Color(100, 100, 110));
        exportAcademicBtn.addActionListener(e -> exportAcademicReport());
        section.add(exportAcademicBtn);

        return section;
    }

    private JPanel createMinimalStatCard(String title, JLabel valueLabel, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(250, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 24));
        iconLabel.setForeground(accentColor);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // Value
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(new Color(45, 45, 45));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(120, 120, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // Arrange vertically
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(iconLabel);
        contentPanel.add(valueLabel);
        contentPanel.add(titleLabel);

        card.add(contentPanel, BorderLayout.WEST);

        return card;
    }

    private JButton createExportButton(String text) {
        return createMinimalButton(text, new Color(70, 130, 180));
    }

    private void loadReportData() {
        if (!db.isConnected()) {
            db.connect();
        }

        try {
            loadStudentStatistics();
            loadFinancialStatistics();
            loadAcademicStatistics();
            loadSystemActivityStatistics();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading report data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentStatistics() {
        try {
            JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
            JPanel studentPanel = (JPanel) tabbedPane.getComponentAt(0);
            JScrollPane scrollPane = (JScrollPane) studentPanel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            // Total Students
            String sql = "SELECT COUNT(*) as total FROM students";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Total Students", rs.getInt("total") });
                rs.close();
            }

            // Active Students
            sql = "SELECT COUNT(*) as total FROM students WHERE status = 'Active'";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Active Students", rs.getInt("total") });
                rs.close();
            }

            // Inactive Students
            sql = "SELECT COUNT(*) as total FROM students WHERE status != 'Active'";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Inactive Students", rs.getInt("total") });
                rs.close();
            }

            // By Program
            sql = "SELECT program, COUNT(*) as count FROM students GROUP BY program";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null) {
                while (rs.next()) {
                    model.addRow(new Object[] { rs.getString("program"), rs.getInt("count") });
                }
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFinancialStatistics() {
        try {
            JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
            JPanel financialPanel = (JPanel) tabbedPane.getComponentAt(1);
            JScrollPane scrollPane = (JScrollPane) financialPanel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "UG"));

            // Total Outstanding
            String sql = "SELECT SUM(fee_balance) as total FROM students";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Outstanding Balance", currencyFormat.format(rs.getDouble("total")) });
                rs.close();
            }

            // Total Paid
            sql = "SELECT SUM(amount) as total FROM payments";
            rs = db.executePreparedSelect(sql, new Object[] {});
            double totalPaid = 0;
            if (rs != null && rs.next()) {
                totalPaid = rs.getDouble("total");
                model.addRow(new Object[] { "Total Collected", currencyFormat.format(totalPaid) });
                rs.close();
            }

            // Total Transactions
            sql = "SELECT COUNT(*) as count FROM payments";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Total Transactions", rs.getInt("count") });
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAcademicStatistics() {
        try {
            JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
            JPanel academicPanel = (JPanel) tabbedPane.getComponentAt(2);
            JScrollPane scrollPane = (JScrollPane) academicPanel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            // Total Courses
            String sql = "SELECT COUNT(*) as total FROM courses";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Total Courses", rs.getInt("total") });
                rs.close();
            }

            // Total Lecturers
            sql = "SELECT COUNT(*) as total FROM lecturers";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Total Lecturers", rs.getInt("total") });
                rs.close();
            }

            // Course Registrations
            sql = "SELECT COUNT(*) as total FROM course_registrations";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Course Registrations", rs.getInt("total") });
                rs.close();
            }

            // Grades Entered
            sql = "SELECT COUNT(*) as total FROM grades WHERE grade IS NOT NULL";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Grades Entered", rs.getInt("total") });
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSystemActivityStatistics() {
        try {
            JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
            JPanel systemPanel = (JPanel) tabbedPane.getComponentAt(3);
            JScrollPane scrollPane = (JScrollPane) systemPanel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            // Total Announcements
            String sql = "SELECT COUNT(*) as total FROM announcements";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Total Announcements", rs.getInt("total") });
                rs.close();
            }

            // Active Announcements
            sql = "SELECT COUNT(*) as total FROM announcements WHERE is_active = TRUE";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Active Announcements", rs.getInt("total") });
                rs.close();
            }

            // Total Users
            sql = "SELECT COUNT(*) as total FROM users WHERE is_active = TRUE";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                model.addRow(new Object[] { "Active Users", rs.getInt("total") });
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStudentDetailedReport() {
        // Create a detailed report dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detailed Student Report", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Detailed Student Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create table for detailed data
        String[] columns = { "Category", "Count", "Percentage" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            if (!db.isConnected()) {
                db.connect();
            }

            // Get total students
            String sql = "SELECT COUNT(*) as total FROM students";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            int totalStudents = 0;
            if (rs != null && rs.next()) {
                totalStudents = rs.getInt("total");
                rs.close();
            }

            if (totalStudents > 0) {
                // Students by Status
                sql = "SELECT status, COUNT(*) as count FROM students GROUP BY status";
                rs = db.executePreparedSelect(sql, new Object[] {});
                model.addRow(new Object[] { "=== BY STATUS ===", "", "" });
                if (rs != null) {
                    while (rs.next()) {
                        String status = rs.getString("status");
                        int count = rs.getInt("count");
                        double percentage = (count * 100.0) / totalStudents;
                        model.addRow(new Object[] { status, count, String.format("%.1f%%", percentage) });
                    }
                    rs.close();
                }

                // Students by Program
                model.addRow(new Object[] { "", "", "" });
                model.addRow(new Object[] { "=== BY PROGRAM ===", "", "" });
                sql = "SELECT program, COUNT(*) as count FROM students GROUP BY program";
                rs = db.executePreparedSelect(sql, new Object[] {});
                if (rs != null) {
                    while (rs.next()) {
                        String program = rs.getString("program");
                        int count = rs.getInt("count");
                        double percentage = (count * 100.0) / totalStudents;
                        model.addRow(new Object[] { program, count, String.format("%.1f%%", percentage) });
                    }
                    rs.close();
                }

                // Students by Year of Study
                model.addRow(new Object[] { "", "", "" });
                model.addRow(new Object[] { "=== BY YEAR OF STUDY ===", "", "" });
                sql = "SELECT year_of_study, COUNT(*) as count FROM students GROUP BY year_of_study ORDER BY year_of_study";
                rs = db.executePreparedSelect(sql, new Object[] {});
                if (rs != null) {
                    while (rs.next()) {
                        int year = rs.getInt("year_of_study");
                        int count = rs.getInt("count");
                        double percentage = (count * 100.0) / totalStudents;
                        model.addRow(new Object[] { "Year " + year, count, String.format("%.1f%%", percentage) });
                    }
                    rs.close();
                }

                // Students by Semester
                model.addRow(new Object[] { "", "", "" });
                model.addRow(new Object[] { "=== BY SEMESTER ===", "", "" });
                sql = "SELECT semester, COUNT(*) as count FROM students GROUP BY semester ORDER BY semester";
                rs = db.executePreparedSelect(sql, new Object[] {});
                if (rs != null) {
                    while (rs.next()) {
                        int semester = rs.getInt("semester");
                        int count = rs.getInt("count");
                        double percentage = (count * 100.0) / totalStudents;
                        model.addRow(
                                new Object[] { "Semester " + semester, count, String.format("%.1f%%", percentage) });
                    }
                    rs.close();
                }

                // GPA Distribution
                model.addRow(new Object[] { "", "", "" });
                model.addRow(new Object[] { "=== GPA DISTRIBUTION ===", "", "" });
                sql = "SELECT " +
                        "SUM(CASE WHEN gpa >= 3.7 THEN 1 ELSE 0 END) as first_class, " +
                        "SUM(CASE WHEN gpa >= 3.0 AND gpa < 3.7 THEN 1 ELSE 0 END) as second_upper, " +
                        "SUM(CASE WHEN gpa >= 2.5 AND gpa < 3.0 THEN 1 ELSE 0 END) as second_lower, " +
                        "SUM(CASE WHEN gpa >= 2.0 AND gpa < 2.5 THEN 1 ELSE 0 END) as pass, " +
                        "SUM(CASE WHEN gpa < 2.0 THEN 1 ELSE 0 END) as fail " +
                        "FROM students";
                rs = db.executePreparedSelect(sql, new Object[] {});
                if (rs != null && rs.next()) {
                    int firstClass = rs.getInt("first_class");
                    int secondUpper = rs.getInt("second_upper");
                    int secondLower = rs.getInt("second_lower");
                    int pass = rs.getInt("pass");
                    int fail = rs.getInt("fail");

                    if (firstClass > 0) {
                        double percentage = (firstClass * 100.0) / totalStudents;
                        model.addRow(new Object[] { "First Class (3.7-4.0)", firstClass,
                                String.format("%.1f%%", percentage) });
                    }
                    if (secondUpper > 0) {
                        double percentage = (secondUpper * 100.0) / totalStudents;
                        model.addRow(new Object[] { "Second Upper (3.0-3.7)", secondUpper,
                                String.format("%.1f%%", percentage) });
                    }
                    if (secondLower > 0) {
                        double percentage = (secondLower * 100.0) / totalStudents;
                        model.addRow(new Object[] { "Second Lower (2.5-3.0)", secondLower,
                                String.format("%.1f%%", percentage) });
                    }
                    if (pass > 0) {
                        double percentage = (pass * 100.0) / totalStudents;
                        model.addRow(new Object[] { "Pass (2.0-2.5)", pass, String.format("%.1f%%", percentage) });
                    }
                    if (fail > 0) {
                        double percentage = (fail * 100.0) / totalStudents;
                        model.addRow(new Object[] { "Below Pass (<2.0)", fail, String.format("%.1f%%", percentage) });
                    }
                    rs.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addRow(new Object[] { "Error loading data", "", "" });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(52, 152, 219));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void exportStudentReport() {
        try {
            // Create reports directory if it doesn't exist
            java.io.File reportsDir = new java.io.File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String filename = "reports/student_report_" + System.currentTimeMillis() + ".csv";
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);

            // Write header
            writer.println("STUDENT REPORT - Generated: " + new java.util.Date());
            writer.println();
            writer.println("Registration Number,Name,Program,Year,Semester,GPA,Fee Balance,Status");

            if (!db.isConnected()) {
                db.connect();
            }

            // Get all students
            String sql = "SELECT s.registration_number, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as name, " +
                    "s.program, s.year_of_study, s.semester, s.gpa, s.fee_balance, s.status " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "ORDER BY s.program, s.year_of_study, s.registration_number";

            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null) {
                while (rs.next()) {
                    writer.printf("%s,%s,%s,%d,%d,%.2f,%.2f,%s%n",
                            rs.getString("registration_number"),
                            rs.getString("name"),
                            rs.getString("program"),
                            rs.getInt("year_of_study"),
                            rs.getInt("semester"),
                            rs.getDouble("gpa"),
                            rs.getDouble("fee_balance"),
                            rs.getString("status"));
                }
                rs.close();
            }

            writer.close();

            JOptionPane.showMessageDialog(this,
                    "Student report exported successfully!\n\nLocation: " + filename,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + e.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportFinancialReport() {
        try {
            // Create reports directory if it doesn't exist
            java.io.File reportsDir = new java.io.File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String filename = "reports/financial_report_" + System.currentTimeMillis() + ".csv";
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);

            // Write header
            writer.println("FINANCIAL REPORT - Generated: " + new java.util.Date());
            writer.println();
            writer.println("PAYMENT TRANSACTIONS");
            writer.println(
                    "Payment ID,Date,Student Reg,Student Name,Amount,Method,Reference,Purpose,Year,Semester,Processed By");

            if (!db.isConnected()) {
                db.connect();
            }

            // Get all payments
            String sql = "SELECT p.payment_id, p.payment_date, s.registration_number, " +
                    "CONCAT(per.first_name, ' ', per.last_name) as student_name, " +
                    "p.amount, p.payment_method, p.reference_number, p.purpose, " +
                    "p.academic_year, p.semester, " +
                    "CONCAT(admin_per.first_name, ' ', admin_per.last_name) as processed_by " +
                    "FROM payments p " +
                    "JOIN students s ON p.student_id = s.student_id " +
                    "JOIN persons per ON s.person_id = per.person_id " +
                    "LEFT JOIN admins a ON p.processed_by = a.admin_id " +
                    "LEFT JOIN persons admin_per ON a.person_id = admin_per.person_id " +
                    "ORDER BY p.payment_date DESC";

            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null) {
                while (rs.next()) {
                    writer.printf("%d,%s,%s,%s,%.2f,%s,%s,%s,%s,%d,%s%n",
                            rs.getInt("payment_id"),
                            rs.getTimestamp("payment_date"),
                            rs.getString("registration_number"),
                            rs.getString("student_name"),
                            rs.getDouble("amount"),
                            rs.getString("payment_method"),
                            rs.getString("reference_number"),
                            rs.getString("purpose"),
                            rs.getString("academic_year"),
                            rs.getInt("semester"),
                            rs.getString("processed_by") != null ? rs.getString("processed_by") : "N/A");
                }
                rs.close();
            }

            // Add summary section
            writer.println();
            writer.println("STUDENT FEE BALANCES");
            writer.println("Registration Number,Student Name,Program,Fee Balance,Status");

            sql = "SELECT s.registration_number, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as name, " +
                    "s.program, s.fee_balance, s.status " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "WHERE s.fee_balance > 0 " +
                    "ORDER BY s.fee_balance DESC";

            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null) {
                while (rs.next()) {
                    writer.printf("%s,%s,%s,%.2f,%s%n",
                            rs.getString("registration_number"),
                            rs.getString("name"),
                            rs.getString("program"),
                            rs.getDouble("fee_balance"),
                            rs.getString("status"));
                }
                rs.close();
            }

            writer.close();

            JOptionPane.showMessageDialog(this,
                    "Financial report exported successfully!\n\nLocation: " + filename,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + e.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportAcademicReport() {
        try {
            // Create reports directory if it doesn't exist
            java.io.File reportsDir = new java.io.File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String filename = "reports/academic_report_" + System.currentTimeMillis() + ".csv";
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);

            // Write header
            writer.println("ACADEMIC REPORT - Generated: " + new java.util.Date());
            writer.println();
            writer.println("COURSES");
            writer.println("Course Code,Course Name,Credits,Lecturer,Department,Semester");

            if (!db.isConnected()) {
                db.connect();
            }

            // Get all courses
            String sql = "SELECT c.course_code, c.course_name, c.credits, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as lecturer_name, " +
                    "c.department, c.semester " +
                    "FROM courses c " +
                    "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                    "LEFT JOIN persons p ON l.person_id = p.person_id " +
                    "ORDER BY c.department, c.course_code";

            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null) {
                while (rs.next()) {
                    writer.printf("%s,%s,%d,%s,%s,%d%n",
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("credits"),
                            rs.getString("lecturer_name") != null ? rs.getString("lecturer_name") : "Not Assigned",
                            rs.getString("department"),
                            rs.getInt("semester"));
                }
                rs.close();
            }

            // Add course registration section
            writer.println();
            writer.println("COURSE REGISTRATIONS");
            writer.println("Course Code,Course Name,Total Students,Average Grade");

            sql = "SELECT c.course_code, c.course_name, " +
                    "COUNT(DISTINCT cr.student_id) as student_count, " +
                    "AVG(g.grade_value) as avg_grade " +
                    "FROM courses c " +
                    "LEFT JOIN course_registrations cr ON c.course_id = cr.course_id " +
                    "LEFT JOIN grades g ON cr.registration_id = g.registration_id " +
                    "GROUP BY c.course_id, c.course_code, c.course_name " +
                    "ORDER BY student_count DESC";

            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null) {
                while (rs.next()) {
                    double avgGrade = rs.getDouble("avg_grade");
                    writer.printf("%s,%s,%d,%s%n",
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("student_count"),
                            avgGrade > 0 ? String.format("%.2f", avgGrade) : "N/A");
                }
                rs.close();
            }

            // Add lecturer workload section
            writer.println();
            writer.println("LECTURER WORKLOAD");
            writer.println("Lecturer Name,Employee Number,Department,Number of Courses");

            sql = "SELECT CONCAT(p.first_name, ' ', p.last_name) as name, " +
                    "l.employee_number, l.department, " +
                    "COUNT(c.course_id) as course_count " +
                    "FROM lecturers l " +
                    "JOIN persons p ON l.person_id = p.person_id " +
                    "LEFT JOIN courses c ON l.lecturer_id = c.lecturer_id " +
                    "GROUP BY l.lecturer_id, name, l.employee_number, l.department " +
                    "ORDER BY course_count DESC";

            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null) {
                while (rs.next()) {
                    writer.printf("%s,%s,%s,%d%n",
                            rs.getString("name"),
                            rs.getString("employee_number"),
                            rs.getString("department"),
                            rs.getInt("course_count"));
                }
                rs.close();
            }

            writer.close();

            JOptionPane.showMessageDialog(this,
                    "Academic report exported successfully!\n\nLocation: " + filename,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + e.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
