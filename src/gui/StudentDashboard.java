package gui;

import database.MySQLDatabase;
import models.Student;
import services.CourseService;
import services.GradeService;
import utils.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Student Dashboard - Main interface for students
 * Demonstrates complex GUI with multiple panels and navigation
 */
public class StudentDashboard extends JFrame {
    private Student student;
    private JPanel contentPanel;
    private JLabel welcomeLabel;
    private CourseService courseService;
    private GradeService gradeService;
    private MySQLDatabase db;
    private JButton activeButton; // Track currently active menu button

    public StudentDashboard(Student student) {
        this.student = student;
        this.db = MySQLDatabase.getInstance();
        this.courseService = new CourseService();
        this.gradeService = new GradeService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Student Dashboard - SCMS");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
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

        // Show dashboard home by default
        showDashboardHome();

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Create top navigation bar
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(52, 73, 94));
        topBar.setPreferredSize(new Dimension(1200, 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Logo and title
        JLabel titleLabel = new JLabel("NDEJJE UNIVERSITY - Student Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel(student.getFullName() + " (" + student.getRegistrationNumber() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

    /**
     * Create sidebar with navigation menu
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(250, 750));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Menu items
        addMenuItem(sidebar, "Dashboard", e -> showDashboardHome());
        addMenuItem(sidebar, "My Courses", e -> showMyCourses());
        addMenuItem(sidebar, "Register Courses", e -> showCourseRegistration());
        addMenuItem(sidebar, "View Grades", e -> showGrades());
        addMenuItem(sidebar, "Fee Balance", e -> showFeeBalance());
        addMenuItem(sidebar, "My Profile", e -> showProfile());
        addMenuItem(sidebar, "Announcements", e -> showAnnouncements());

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    /**
     * Helper method to add menu item
     */
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

        // Hover effect
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

    /**
     * Show dashboard home with summary
     */
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

        JLabel subtitleLabel = new JLabel("Welcome back, " + student.getFirstName());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(120, 120, 120));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        topSection.add(titlePanel, BorderLayout.WEST);

        // Info section - Clean list with all student data
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setOpaque(false);
        infoSection.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        infoSection.add(createInfoRow("Registration Number", student.getRegistrationNumber()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Program", student.getProgram()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Year of Study", "Year " + student.getYearOfStudy()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Semester", "Semester " + student.getSemester()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("GPA", String.format("%.2f", student.getGpa())));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Fee Balance", String.format("%.0f UGX", student.getFeeBalance())));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Status", student.getStatus()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Academic Standing", student.getAcademicStanding()));

        // Main content panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.add(topSection, BorderLayout.NORTH);
        mainContent.add(infoSection, BorderLayout.CENTER);

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

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 28));
        iconLabel.setForeground(new Color(100, 100, 120));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Value
        JLabel valueLabel = new JLabel(value);
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

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(new Color(120, 120, 120));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueText.setForeground(new Color(45, 45, 45));

        row.add(labelText, BorderLayout.WEST);
        row.add(valueText, BorderLayout.EAST);

        return row;
    }

    /**
     * Create statistics card
     */
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
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);

        return card;
    }

    /**
     * Show my courses panel
     */
    private void showMyCourses() {
        contentPanel.removeAll();
        contentPanel.add(new StudentCoursesPanel(student, courseService));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Show course registration panel
     */
    private void showCourseRegistration() {
        contentPanel.removeAll();
        contentPanel.add(new CourseRegistrationPanel(student, courseService));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Show grades panel
     */
    private void showGrades() {
        contentPanel.removeAll();
        contentPanel.add(new StudentGradesPanel(student, gradeService));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Show fee balance panel
     */
    private void showFeeBalance() {
        contentPanel.removeAll();
        contentPanel.add(new StudentFeeBalancePanel(student));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Show profile panel
     */
    private void showProfile() {
        contentPanel.removeAll();
        contentPanel.add(new StudentProfilePanel(student));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Show announcements panel
     */
    private void showAnnouncements() {
        contentPanel.removeAll();
        contentPanel.add(new AnnouncementsPanel(db, "STUDENT"));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Logout and return to login screen
     */
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
