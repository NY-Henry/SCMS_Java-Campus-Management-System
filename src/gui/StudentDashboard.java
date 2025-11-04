package gui;

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

    public StudentDashboard(Student student) {
        this.student = student;
        this.courseService = new CourseService();
        this.gradeService = new GradeService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Student Dashboard - SCMS");
        setSize(1200, 750);
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
        addMenuItem(sidebar, "🏠 Dashboard", e -> showDashboardHome());
        addMenuItem(sidebar, "📚 My Courses", e -> showMyCourses());
        addMenuItem(sidebar, "➕ Register Courses", e -> showCourseRegistration());
        addMenuItem(sidebar, "📊 View Grades", e -> showGrades());
        addMenuItem(sidebar, "💰 Fee Balance", e -> showFeeBalance());
        addMenuItem(sidebar, "👤 My Profile", e -> showProfile());
        addMenuItem(sidebar, "📢 Announcements", e -> showAnnouncements());

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
                button.setBackground(new Color(52, 73, 94));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
            }
        });

        button.addActionListener(action);
        sidebar.add(button);
    }

    /**
     * Show dashboard home with summary
     */
    private void showDashboardHome() {
        contentPanel.removeAll();

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(new Color(236, 240, 241));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome back, " + student.getFirstName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        homePanel.add(welcomeLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setMaximumSize(new Dimension(1100, 120));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatCard("GPA", String.format("%.2f", student.getGpa()),
                new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Year", "Year " + student.getYearOfStudy(),
                new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Semester", "Semester " + student.getSemester(),
                new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Fee Balance", String.format("UGX %.0f", student.getFeeBalance()),
                new Color(231, 76, 60)));

        homePanel.add(statsPanel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Quick info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        infoPanel.setMaximumSize(new Dimension(1100, 300));

        JLabel infoTitle = new JLabel("Student Information");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel regNum = new JLabel("Registration Number: " + student.getRegistrationNumber());
        JLabel program = new JLabel("Program: " + student.getProgram());
        JLabel status = new JLabel("Status: " + student.getStatus());
        JLabel standing = new JLabel("Academic Standing: " + student.getAcademicStanding());

        regNum.setFont(new Font("Arial", Font.PLAIN, 15));
        program.setFont(new Font("Arial", Font.PLAIN, 15));
        status.setFont(new Font("Arial", Font.PLAIN, 15));
        standing.setFont(new Font("Arial", Font.PLAIN, 15));

        infoPanel.add(infoTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(regNum);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(program);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(status);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(standing);

        homePanel.add(infoPanel);

        contentPanel.add(homePanel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
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

        JPanel feePanel = new JPanel();
        feePanel.setLayout(new BoxLayout(feePanel, BoxLayout.Y_AXIS));
        feePanel.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Fee Balance Information");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel balanceCard = new JPanel();
        balanceCard.setLayout(new BoxLayout(balanceCard, BoxLayout.Y_AXIS));
        balanceCard.setBackground(Color.WHITE);
        balanceCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        balanceCard.setMaximumSize(new Dimension(600, 200));

        JLabel balanceLabel = new JLabel("Current Balance");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel amountLabel = new JLabel(String.format("UGX %.2f", student.getFeeBalance()));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 36));
        amountLabel.setForeground(student.getFeeBalance() > 0 ? new Color(231, 76, 60) : new Color(46, 204, 113));

        balanceCard.add(balanceLabel);
        balanceCard.add(Box.createRigidArea(new Dimension(0, 15)));
        balanceCard.add(amountLabel);

        feePanel.add(title);
        feePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        feePanel.add(balanceCard);

        contentPanel.add(feePanel, BorderLayout.NORTH);
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
        contentPanel.add(new AnnouncementsPanel("STUDENT"));
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
