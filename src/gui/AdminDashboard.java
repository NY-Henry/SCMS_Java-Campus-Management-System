package gui;

import database.MySQLDatabase;
import models.Admin;
import services.CourseService;
import services.LogService;
import services.PaymentService;
import utils.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

/**
 * Admin Dashboard - Main interface for administrators
 */
public class AdminDashboard extends JFrame {
    private Admin admin;
    private JPanel contentPanel;
    private CourseService courseService;
    private PaymentService paymentService;
    private LogService logService;
    private MySQLDatabase db;
    private JButton activeButton; // Track currently active menu button

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        this.courseService = new CourseService();
        this.logService = new LogService();
        this.paymentService = new PaymentService();
        this.db = MySQLDatabase.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - SCMS");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top bar
        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(236, 240, 241));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        showDashboardHome();

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(52, 73, 94));
        topBar.setPreferredSize(new Dimension(1200, 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("NDEJJE UNIVERSITY - Admin Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel(admin.getFullName() + " (Administrator)");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.addActionListener(e -> logout());

        userPanel.add(userLabel);
        userPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        userPanel.add(logoutBtn);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(250, 750));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        addMenuItem(sidebar, "Dashboard", e -> showDashboardHome());
        addMenuItem(sidebar, "Manage Students", e -> showManageStudents());
        addMenuItem(sidebar, "Manage Lecturers", e -> showManageLecturers());
        addMenuItem(sidebar, "Manage Courses", e -> showManageCourses());
        addMenuItem(sidebar, "Reports", e -> showReports());
        addMenuItem(sidebar, "Payment Records", e -> showPayments());
        addMenuItem(sidebar, "System Logs", e -> showLogs());

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void addMenuItem(JPanel sidebar, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 50));
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(44, 62, 80));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(new Color(44, 62, 80));
                }
            }
        });

        button.addActionListener(e -> {
            // Reset previous active button
            if (activeButton != null) {
                activeButton.setBackground(new Color(44, 62, 80));
            }
            // Set new active button
            activeButton = button;
            button.setBackground(new Color(52, 73, 94));

            // Execute the action
            action.actionPerformed(e);
        });

        sidebar.add(button);
    }

    private void showDashboardHome() {
        contentPanel.removeAll();

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);
        homePanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Top section - Welcome message
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        welcomeLabel.setForeground(new Color(45, 45, 45));

        JLabel subtitleLabel = new JLabel("Welcome back, " + admin.getFullName());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(120, 120, 120));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        topSection.add(titlePanel, BorderLayout.WEST);

        // Statistics section - Minimalist cards
        JPanel statsSection = new JPanel(new GridLayout(2, 2, 30, 30));
        statsSection.setOpaque(false);
        statsSection.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        // Load dynamic statistics from database
        int totalStudents = getStatCount("SELECT COUNT(*) as count FROM students WHERE status = 'ACTIVE'");
        int totalLecturers = getStatCount("SELECT COUNT(*) as count FROM lecturers WHERE status = 'ACTIVE'");
        int totalCourses = getStatCount("SELECT COUNT(*) as count FROM courses");
        int activeUsers = getStatCount("SELECT COUNT(*) as count FROM users WHERE is_active = TRUE");

        statsSection.add(createMinimalStatCard("Students", String.valueOf(totalStudents), "\u25CF"));
        statsSection.add(createMinimalStatCard("Lecturers", String.valueOf(totalLecturers), "\u25A0"));
        statsSection.add(createMinimalStatCard("Courses", String.valueOf(totalCourses), "\u25B2"));
        statsSection.add(createMinimalStatCard("Active Users", String.valueOf(activeUsers), "\u2713"));

        // Main content panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.add(topSection, BorderLayout.NORTH);
        mainContent.add(statsSection, BorderLayout.CENTER);

        homePanel.add(mainContent, BorderLayout.CENTER);

        contentPanel.add(homePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createMinimalStatCard(String title, String value, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(250, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        // Icon/Emoji
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 28));
        iconLabel.setForeground(new Color(100, 100, 120));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(new Color(45, 45, 45));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 248, 250));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(250, 250, 252));
            }
        });

        return card;
    }

    private void showManageStudents() {
        contentPanel.removeAll();
        contentPanel.add(new ManageStudentsPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManageLecturers() {
        contentPanel.removeAll();
        contentPanel.add(new ManageLecturersPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManageCourses() {
        contentPanel.removeAll();
        contentPanel.add(new ManageCoursesPanel(courseService));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReports() {
        contentPanel.removeAll();
        contentPanel.add(new ReportsPanel(db, paymentService));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPayments() {
        contentPanel.removeAll();
        contentPanel.add(new ManagePaymentsPanel(admin, paymentService));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showLogs() {
        contentPanel.removeAll();
        contentPanel.add(new SystemLogsPanel(db));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Log the logout action
            logService.logLogout(admin.getUserId(), admin.getFullName(), "ADMIN");

            SessionManager.getInstance().logout();
            dispose();
            new LoginForm().setVisible(true);
        }
    }

    /**
     * Helper method to get count statistics from database
     */
    private int getStatCount(String query) {
        try {
            // Ensure fresh connection
            if (!db.isConnected()) {
                db.connect();
            }

            ResultSet rs = db.fetchData(query);

            if (rs != null && rs.next()) {
                int count = rs.getInt("count");
                rs.close();
                return count;
            }

            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            System.err.println("Error fetching stat count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0; // Return 0 if query fails
    }
}
