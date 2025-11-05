package gui;

import database.MySQLDatabase;
import models.Lecturer;
import services.AnnouncementService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Panel for lecturers to post announcements
 */
public class PostAnnouncementPanel extends JPanel {
    private final MySQLDatabase db;
    private final AnnouncementService announcementService;
    private final Lecturer lecturer;

    private JTextField titleField;
    private JTextArea contentArea;
    private JComboBox<String> audienceCombo;
    private JComboBox<CourseItem> courseCombo;
    private JCheckBox expiryCheckbox;
    private JSpinner expirySpinner;
    private JLabel courseLabel;

    public PostAnnouncementPanel(MySQLDatabase db, Lecturer lecturer) {
        this.db = db;
        this.lecturer = lecturer;
        this.announcementService = new AnnouncementService(db);

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initializeComponents();
    }

    private void initializeComponents() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Post New Announcement");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        int row = 0;

        // Title field
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lblTitle = new JLabel("Title:");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(lblTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        titleField = new JTextField();
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(titleField, gbc);

        row++;

        // Target Audience
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lblAudience = new JLabel("Target Audience:");
        lblAudience.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(lblAudience, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        String[] audiences = { "All Users", "Students Only", "Lecturers Only", "Specific Course" };
        audienceCombo = new JComboBox<>(audiences);
        audienceCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        audienceCombo.addActionListener(e -> toggleCourseSelection());
        formPanel.add(audienceCombo, gbc);

        row++;

        // Course selection (initially hidden)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        courseLabel.setVisible(false);
        formPanel.add(courseLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        courseCombo.setVisible(false);
        loadLecturerCourses();
        formPanel.add(courseCombo, gbc);

        row++;

        // Content area
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblContent = new JLabel("Content:");
        lblContent.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(lblContent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        contentArea = new JTextArea(10, 40);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        formPanel.add(scrollPane, gbc);

        row++;

        // Expiry settings
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        expiryCheckbox = new JCheckBox("Set Expiry Date");
        expiryCheckbox.setFont(new Font("Arial", Font.BOLD, 14));
        expiryCheckbox.setBackground(Color.WHITE);
        expiryCheckbox.addActionListener(e -> toggleExpirySpinner());
        formPanel.add(expiryCheckbox, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        // Date spinner (days from now)
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(7, 1, 365, 1);
        expirySpinner = new JSpinner(spinnerModel);
        expirySpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        expirySpinner.setEnabled(false);

        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expiryPanel.setBackground(Color.WHITE);
        expiryPanel.add(expirySpinner);
        expiryPanel.add(new JLabel("days from now"));

        formPanel.add(expiryPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton postButton = new JButton("Post Announcement");
        postButton.setFont(new Font("Arial", Font.BOLD, 14));
        postButton.setBackground(new Color(46, 204, 113));
        postButton.setForeground(Color.WHITE);
        postButton.setFocusPainted(false);
        postButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        postButton.addActionListener(e -> postAnnouncement());
        buttonPanel.add(postButton);

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadLecturerCourses() {
        if (!db.isConnected()) {
            db.connect();
        }

        try {
            String sql = "SELECT c.course_id, c.course_code, c.course_name " +
                    "FROM courses c " +
                    "WHERE c.lecturer_id = ?";

            Object[] params = { lecturer.getLecturerId() };
            ResultSet rs = db.executePreparedSelect(sql, params);

            courseCombo.removeAllItems();
            courseCombo.addItem(new CourseItem(null, "-- Select Course --"));

            if (rs != null) {
                while (rs.next()) {
                    int courseId = rs.getInt("course_id");
                    String courseCode = rs.getString("course_code");
                    String courseName = rs.getString("course_name");
                    courseCombo.addItem(new CourseItem(courseId, courseCode + " - " + courseName));
                }
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleCourseSelection() {
        boolean isSpecificCourse = audienceCombo.getSelectedIndex() == 3;
        courseLabel.setVisible(isSpecificCourse);
        courseCombo.setVisible(isSpecificCourse);
    }

    private void toggleExpirySpinner() {
        expirySpinner.setEnabled(expiryCheckbox.isSelected());
    }

    private void postAnnouncement() {
        // Validate inputs
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a title for the announcement.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter content for the announcement.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get target audience
        String targetAudience;
        Integer courseId = null;

        switch (audienceCombo.getSelectedIndex()) {
            case 0:
                targetAudience = "ALL";
                break;
            case 1:
                targetAudience = "STUDENTS";
                break;
            case 2:
                targetAudience = "LECTURERS";
                break;
            case 3:
                targetAudience = "SPECIFIC_COURSE";
                CourseItem selectedCourse = (CourseItem) courseCombo.getSelectedItem();
                if (selectedCourse == null || selectedCourse.courseId == null) {
                    JOptionPane.showMessageDialog(this,
                            "Please select a course for the announcement.",
                            "Validation Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                courseId = selectedCourse.courseId;
                break;
            default:
                targetAudience = "ALL";
        }

        // Get expiry date if set
        Timestamp expiresAt = null;
        if (expiryCheckbox.isSelected()) {
            int daysFromNow = (Integer) expirySpinner.getValue();
            LocalDateTime expiryDateTime = LocalDateTime.now().plusDays(daysFromNow);
            expiresAt = Timestamp.valueOf(expiryDateTime);
        }

        // Post announcement
        boolean success = announcementService.postAnnouncement(
                title, content, lecturer.getUserId(), targetAudience, courseId, expiresAt);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Announcement posted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to post announcement. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        titleField.setText("");
        contentArea.setText("");
        audienceCombo.setSelectedIndex(0);
        expiryCheckbox.setSelected(false);
        expirySpinner.setValue(7);
        expirySpinner.setEnabled(false);
        courseCombo.setSelectedIndex(0);
        toggleCourseSelection();
    }

    /**
     * Helper class for course combo box items
     */
    private static class CourseItem {
        Integer courseId;
        String displayText;

        CourseItem(Integer courseId, String displayText) {
            this.courseId = courseId;
            this.displayText = displayText;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }
}
