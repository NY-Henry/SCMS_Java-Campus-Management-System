package gui;

import models.Admin;
import services.CourseService;
import utils.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Admin Dashboard - Main interface for administrators
 */
public class AdminDashboard extends JFrame {
    private Admin admin;
    private JPanel contentPanel;
    private CourseService courseService;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        this.courseService = new CourseService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - SCMS");
        setSize(1200, 750);
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

        addMenuItem(sidebar, "🏠 Dashboard", e -> showDashboardHome());
        addMenuItem(sidebar, "👥 Manage Students", e -> showManageStudents());
        addMenuItem(sidebar, "👨‍🏫 Manage Lecturers", e -> showManageLecturers());
        addMenuItem(sidebar, "📚 Manage Courses", e -> showManageCourses());
        addMenuItem(sidebar, "📊 Reports", e -> showReports());
        addMenuItem(sidebar, "💰 Payment Records", e -> showPayments());
        addMenuItem(sidebar, "📝 System Logs", e -> showLogs());

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
                button.setBackground(new Color(52, 73, 94));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
            }
        });

        button.addActionListener(action);
        sidebar.add(button);
    }

    private void showDashboardHome() {
        contentPanel.removeAll();

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(new Color(236, 240, 241));

        JLabel welcomeLabel = new JLabel("Welcome, Administrator!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        homePanel.add(welcomeLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setMaximumSize(new Dimension(1100, 120));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatCard("Total Students", "2", new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Total Lecturers", "2", new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Total Courses", "4", new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Active Users", "5", new Color(241, 196, 15)));

        homePanel.add(statsPanel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Info card
        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        infoCard.setMaximumSize(new Dimension(1100, 200));

        JLabel infoTitle = new JLabel("System Overview");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel info1 = new JLabel("• Access Level: " + admin.getAccessLevel() + " (Full Access)");
        JLabel info2 = new JLabel("• Department: " + admin.getDepartment());
        JLabel info3 = new JLabel("• System Status: All systems operational");

        info1.setFont(new Font("Arial", Font.PLAIN, 15));
        info2.setFont(new Font("Arial", Font.PLAIN, 15));
        info3.setFont(new Font("Arial", Font.PLAIN, 15));

        infoCard.add(infoTitle);
        infoCard.add(Box.createRigidArea(new Dimension(0, 15)));
        infoCard.add(info1);
        infoCard.add(Box.createRigidArea(new Dimension(0, 8)));
        infoCard.add(info2);
        infoCard.add(Box.createRigidArea(new Dimension(0, 8)));
        infoCard.add(info3);

        homePanel.add(infoCard);

        contentPanel.add(homePanel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(255, 255, 255, 200));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);

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

        JPanel panel = new JPanel();
        panel.setBackground(new Color(236, 240, 241));
        JLabel label = new JLabel("Reports - Feature Coming Soon");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPayments() {
        contentPanel.removeAll();

        JPanel panel = new JPanel();
        panel.setBackground(new Color(236, 240, 241));
        JLabel label = new JLabel("Payment Records - Feature Coming Soon");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showLogs() {
        contentPanel.removeAll();

        JPanel panel = new JPanel();
        panel.setBackground(new Color(236, 240, 241));
        JLabel label = new JLabel("System Logs - Feature Coming Soon");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().logout();
            dispose();
            new LoginForm().setVisible(true);
        }
    }
}
