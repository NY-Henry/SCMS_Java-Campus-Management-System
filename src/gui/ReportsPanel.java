package gui;

import database.MySQLDatabase;
import services.PaymentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initializeUI();
        loadReportData();
    }

    private void initializeUI() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("System Reports & Analytics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(46, 204, 113));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadReportData());
        titlePanel.add(refreshButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Main content with scroll
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(236, 240, 241));

        // Student Statistics Section
        mainContent.add(createStudentReportSection());
        mainContent.add(Box.createVerticalStrut(20));

        // Financial Reports Section
        mainContent.add(createFinancialReportSection());
        mainContent.add(Box.createVerticalStrut(20));

        // Academic Reports Section
        mainContent.add(createAcademicReportSection());
        mainContent.add(Box.createVerticalStrut(20));

        // System Activity Section
        mainContent.add(createSystemActivitySection());
        mainContent.add(Box.createVerticalStrut(20));

        // Export Options Section
        mainContent.add(createExportSection());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStudentReportSection() {
        JPanel section = new JPanel(new GridLayout(2, 2, 15, 15));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "Student Statistics",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(52, 152, 219)));
        section.setBorder(BorderFactory.createCompoundBorder(
                section.getBorder(),
                new EmptyBorder(15, 15, 15, 15)));

        // Total Students Card
        totalStudentsLabel = new JLabel("0");
        section.add(createStatCard("Total Students", totalStudentsLabel, new Color(52, 152, 219)));

        // Active Students Card
        activeStudentsLabel = new JLabel("0");
        section.add(createStatCard("Active Students", activeStudentsLabel, new Color(46, 204, 113)));

        // Inactive Students (calculated)
        JLabel inactiveStudentsLabel = new JLabel("0");
        section.add(createStatCard("Inactive Students", inactiveStudentsLabel, new Color(231, 76, 60)));

        // Students by Program (placeholder)
        JButton viewDetailsButton = new JButton("View Detailed Report");
        viewDetailsButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewDetailsButton.setBackground(new Color(52, 152, 219));
        viewDetailsButton.setForeground(Color.BLACK);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewDetailsButton.addActionListener(e -> showStudentDetailedReport());

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewDetailsButton);
        section.add(buttonPanel);

        return section;
    }

    private JPanel createFinancialReportSection() {
        JPanel section = new JPanel(new GridLayout(2, 2, 15, 15));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
                "Financial Reports",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(46, 204, 113)));
        section.setBorder(BorderFactory.createCompoundBorder(
                section.getBorder(),
                new EmptyBorder(15, 15, 15, 15)));

        // Total Fees Expected
        totalFeesLabel = new JLabel("KES 0.00");
        section.add(createStatCard("Total Fees Expected", totalFeesLabel, new Color(52, 152, 219)));

        // Total Collected
        totalPaidLabel = new JLabel("KES 0.00");
        section.add(createStatCard("Total Collected", totalPaidLabel, new Color(46, 204, 113)));

        // Outstanding Balance
        totalOutstandingLabel = new JLabel("KES 0.00");
        section.add(createStatCard("Outstanding Balance", totalOutstandingLabel, new Color(231, 76, 60)));

        // Total Transactions
        totalPaymentsLabel = new JLabel("0");
        section.add(createStatCard("Total Transactions", totalPaymentsLabel, new Color(155, 89, 182)));

        return section;
    }

    private JPanel createAcademicReportSection() {
        JPanel section = new JPanel(new GridLayout(2, 2, 15, 15));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
                "Academic Reports",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(155, 89, 182)));
        section.setBorder(BorderFactory.createCompoundBorder(
                section.getBorder(),
                new EmptyBorder(15, 15, 15, 15)));

        // Total Courses
        totalCoursesLabel = new JLabel("0");
        section.add(createStatCard("Total Courses", totalCoursesLabel, new Color(155, 89, 182)));

        // Total Lecturers
        totalLecturersLabel = new JLabel("0");
        section.add(createStatCard("Total Lecturers", totalLecturersLabel, new Color(52, 152, 219)));

        // Course Registrations
        JLabel totalRegistrationsLabel = new JLabel("0");
        section.add(createStatCard("Course Registrations", totalRegistrationsLabel, new Color(46, 204, 113)));

        // Grades Entered
        JLabel totalGradesLabel = new JLabel("0");
        section.add(createStatCard("Grades Entered", totalGradesLabel, new Color(230, 126, 34)));

        return section;
    }

    private JPanel createSystemActivitySection() {
        JPanel section = new JPanel(new GridLayout(1, 3, 15, 15));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 126, 34), 2),
                "System Activity",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(230, 126, 34)));
        section.setBorder(BorderFactory.createCompoundBorder(
                section.getBorder(),
                new EmptyBorder(15, 15, 15, 15)));

        // Total Announcements
        totalAnnouncementsLabel = new JLabel("0");
        section.add(createStatCard("Total Announcements", totalAnnouncementsLabel, new Color(52, 152, 219)));

        // Active Announcements
        activeAnnouncementsLabel = new JLabel("0");
        section.add(createStatCard("Active Announcements", activeAnnouncementsLabel, new Color(46, 204, 113)));

        // Total Users
        JLabel totalUsersLabel = new JLabel("0");
        section.add(createStatCard("Total Users", totalUsersLabel, new Color(155, 89, 182)));

        return section;
    }

    private JPanel createExportSection() {
        JPanel section = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(127, 140, 141), 2),
                "Export Reports",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(127, 140, 141)));
        section.setBorder(BorderFactory.createCompoundBorder(
                section.getBorder(),
                new EmptyBorder(15, 15, 15, 15)));

        JButton exportStudentsBtn = createExportButton("Export Student Report");
        exportStudentsBtn.setBackground(Color.WHITE);
        exportStudentsBtn.setForeground(Color.BLACK);
        exportStudentsBtn.addActionListener(e -> exportStudentReport());
        section.add(exportStudentsBtn);

        JButton exportFinancialBtn = createExportButton("Export Financial Report");
        exportFinancialBtn.setBackground(Color.WHITE);
        exportFinancialBtn.setForeground(Color.BLACK);
        exportFinancialBtn.addActionListener(e -> exportFinancialReport());
        section.add(exportFinancialBtn);

        JButton exportAcademicBtn = createExportButton("Export Academic Report");
        exportAcademicBtn.setBackground(Color.WHITE);
        exportAcademicBtn.setForeground(Color.BLACK);
        exportAcademicBtn.addActionListener(e -> exportAcademicReport());
        section.add(exportAcademicBtn);

        return section;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JButton createExportButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(220, 40));
        return button;
    }

    private void loadReportData() {
        if (!db.isConnected()) {
            db.connect();
        }

        try {
            // Load Student Statistics
            loadStudentStatistics();

            // Load Financial Statistics
            loadFinancialStatistics();

            // Load Academic Statistics
            loadAcademicStatistics();

            // Load System Activity Statistics
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
            // Total Students
            String sql = "SELECT COUNT(*) as total FROM students";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                totalStudentsLabel.setText(String.valueOf(rs.getInt("total")));
                rs.close();
            }

            // Active Students
            sql = "SELECT COUNT(*) as total FROM students WHERE status = 'Active'";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                activeStudentsLabel.setText(String.valueOf(rs.getInt("total")));
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFinancialStatistics() {
        try {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "KE"));

            // Total Fees (sum of all fee balances - this represents what's owed)
            String sql = "SELECT SUM(fee_balance) as total FROM students";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                double total = rs.getDouble("total");
                totalOutstandingLabel.setText(currencyFormat.format(total));
                rs.close();
            }

            // Total Payments (amount paid)
            sql = "SELECT SUM(amount) as total, COUNT(*) as count FROM payments";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                double totalPaid = rs.getDouble("total");
                int paymentCount = rs.getInt("count");
                totalPaidLabel.setText(currencyFormat.format(totalPaid));
                totalPaymentsLabel.setText(String.valueOf(paymentCount));
                rs.close();
            }

            // Calculate total fees expected (paid + outstanding)
            try {
                String paidText = totalPaidLabel.getText().replaceAll("[^0-9.]", "");
                String outstandingText = totalOutstandingLabel.getText().replaceAll("[^0-9.]", "");
                double paid = Double.parseDouble(paidText);
                double outstanding = Double.parseDouble(outstandingText);
                totalFeesLabel.setText(currencyFormat.format(paid + outstanding));
            } catch (Exception e) {
                totalFeesLabel.setText("KES 0.00");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAcademicStatistics() {
        try {
            // Total Courses
            String sql = "SELECT COUNT(*) as total FROM courses";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                totalCoursesLabel.setText(String.valueOf(rs.getInt("total")));
                rs.close();
            }

            // Total Lecturers
            sql = "SELECT COUNT(*) as total FROM lecturers";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                totalLecturersLabel.setText(String.valueOf(rs.getInt("total")));
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSystemActivityStatistics() {
        try {
            // Total Announcements
            String sql = "SELECT COUNT(*) as total FROM announcements";
            ResultSet rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                totalAnnouncementsLabel.setText(String.valueOf(rs.getInt("total")));
                rs.close();
            }

            // Active Announcements
            sql = "SELECT COUNT(*) as total FROM announcements WHERE is_active = TRUE";
            rs = db.executePreparedSelect(sql, new Object[] {});
            if (rs != null && rs.next()) {
                activeAnnouncementsLabel.setText(String.valueOf(rs.getInt("total")));
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStudentDetailedReport() {
        JOptionPane.showMessageDialog(this,
                "Detailed Student Report\n\nThis will show:\n" +
                        "- Students by program\n" +
                        "- Students by year of study\n" +
                        "- GPA distribution\n" +
                        "- Status breakdown",
                "Student Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportStudentReport() {
        JOptionPane.showMessageDialog(this,
                "Student report will be exported to CSV format.\n" +
                        "Location: reports/student_report_" + System.currentTimeMillis() + ".csv",
                "Export Student Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportFinancialReport() {
        JOptionPane.showMessageDialog(this,
                "Financial report will be exported to CSV format.\n" +
                        "Location: reports/financial_report_" + System.currentTimeMillis() + ".csv",
                "Export Financial Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportAcademicReport() {
        JOptionPane.showMessageDialog(this,
                "Academic report will be exported to CSV format.\n" +
                        "Location: reports/academic_report_" + System.currentTimeMillis() + ".csv",
                "Export Academic Report",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
