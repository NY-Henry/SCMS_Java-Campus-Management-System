package gui;

import database.MySQLDatabase;
import models.Lecturer;
import services.CourseService;
import services.GradeService;
import utils.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Lecturer Dashboard - Main interface for lecturers
 */
public class LecturerDashboard extends JFrame {
    private Lecturer lecturer;
    private JPanel contentPanel;
    private CourseService courseService;
    private GradeService gradeService;
    private MySQLDatabase db;
    private JButton activeButton; // Track currently active menu button

    public LecturerDashboard(Lecturer lecturer) {
        this.lecturer = lecturer;
        this.db = MySQLDatabase.getInstance();
        this.courseService = new CourseService();
        this.gradeService = new GradeService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Lecturer Dashboard - SCMS");
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

        JLabel titleLabel = new JLabel("NDEJJE UNIVERSITY - Lecturer Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel(lecturer.getFullName() + " (" + lecturer.getEmployeeNumber() + ")");
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
        addMenuItem(sidebar, "My Courses", e -> showMyCourses());
        addMenuItem(sidebar, "Class Lists", e -> showClassLists());
        addMenuItem(sidebar, "Upload Grades", e -> showUploadGrades());
        addMenuItem(sidebar, "Post Announcement", e -> showPostAnnouncement());
        addMenuItem(sidebar, "My Announcements", e -> showMyAnnouncements());
        addMenuItem(sidebar, "View Announcements", e -> showAnnouncements());
        addMenuItem(sidebar, "My Profile", e -> showProfile());

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

        JLabel subtitleLabel = new JLabel("Welcome back, Dr./Prof. " + lecturer.getLastName());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(120, 120, 120));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        topSection.add(titlePanel, BorderLayout.WEST);

        // Info section - Clean list with lecturer data
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setOpaque(false);
        infoSection.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        infoSection.add(createInfoRow("Full Name", lecturer.getFullName()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Department", lecturer.getDepartment()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Specialization", lecturer.getSpecialization()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Office Location", lecturer.getOfficeLocation()));
        infoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        infoSection.add(createInfoRow("Status", lecturer.getStatus()));

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

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(600, 25));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(new Color(120, 120, 120));

        JLabel valueText = new JLabel(value != null ? value : "N/A");
        valueText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueText.setForeground(new Color(45, 45, 45));

        row.add(labelText, BorderLayout.WEST);
        row.add(valueText, BorderLayout.EAST);

        return row;
    }

    private void addInfoLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 15));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void showMyCourses() {
        contentPanel.removeAll();
        contentPanel.add(new LecturerCoursesPanel(lecturer, courseService));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showClassLists() {
        contentPanel.removeAll();
        contentPanel.add(new ClassListPanel(lecturer, courseService));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUploadGrades() {
        try {
            contentPanel.removeAll();
            UploadGradesPanel uploadPanel = new UploadGradesPanel(lecturer, courseService, gradeService);
            contentPanel.add(uploadPanel);
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading Upload Grades panel: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPostAnnouncement() {
        contentPanel.removeAll();
        contentPanel.add(new PostAnnouncementPanel(db, lecturer));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMyAnnouncements() {
        contentPanel.removeAll();
        contentPanel.add(new MyAnnouncementsPanel(db, lecturer));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAnnouncements() {
        contentPanel.removeAll();
        contentPanel.add(new AnnouncementsPanel(db, "LECTURER", lecturer.getUserId()));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfile() {
        contentPanel.removeAll();
        contentPanel.add(new LecturerProfilePanel(lecturer));
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
