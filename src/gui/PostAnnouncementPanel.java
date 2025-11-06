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

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        setBackground(Color.WHITE);

        initializeComponents();
    }

    private void initializeComponents() {
        // Title
        JLabel titleLabel = new JLabel("Post New Announcement");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Title field
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblTitle = new JLabel("Title");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(120, 120, 120));
        formPanel.add(lblTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleField.setPreferredSize(new Dimension(500, 35));
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(titleField, gbc);

        row++;

        // Target Audience
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblAudience = new JLabel("Target Audience");
        lblAudience.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAudience.setForeground(new Color(120, 120, 120));
        formPanel.add(lblAudience, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] audiences = { "All Users", "Students Only", "Lecturers Only", "Specific Course" };
        audienceCombo = new JComboBox<>(audiences);
        audienceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        audienceCombo.setPreferredSize(new Dimension(500, 35));
        audienceCombo.setBackground(Color.WHITE);
        audienceCombo.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        audienceCombo.addActionListener(e -> toggleCourseSelection());
        formPanel.add(audienceCombo, gbc);

        row++;

        // Course selection (initially hidden)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        courseLabel = new JLabel("Select Course");
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseLabel.setForeground(new Color(120, 120, 120));
        courseLabel.setVisible(false);
        formPanel.add(courseLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseCombo.setPreferredSize(new Dimension(500, 35));
        courseCombo.setBackground(Color.WHITE);
        courseCombo.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        courseCombo.setVisible(false);
        loadLecturerCourses();
        formPanel.add(courseCombo, gbc);

        row++;

        // Content area
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblContent = new JLabel("Content");
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblContent.setForeground(new Color(120, 120, 120));
        formPanel.add(lblContent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        contentArea = new JTextArea(8, 40);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        formPanel.add(scrollPane, gbc);

        row++;

        // Expiry settings
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        expiryCheckbox = new JCheckBox("Set Expiry Date");
        expiryCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        expiryCheckbox.setForeground(new Color(120, 120, 120));
        expiryCheckbox.setOpaque(false);
        expiryCheckbox.addActionListener(e -> toggleExpirySpinner());
        formPanel.add(expiryCheckbox, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        // Date spinner (days from now)
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(7, 1, 365, 1);
        expirySpinner = new JSpinner(spinnerModel);
        expirySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        expirySpinner.setPreferredSize(new Dimension(80, 30));
        expirySpinner.setEnabled(false);

        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        expiryPanel.setOpaque(false);
        expiryPanel.add(expirySpinner);
        expiryPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        JLabel daysLabel = new JLabel("days from now");
        daysLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        daysLabel.setForeground(new Color(120, 120, 120));
        expiryPanel.add(daysLabel);

        formPanel.add(expiryPanel, gbc);

        // Container
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(titleLabel, BorderLayout.NORTH);
        container.add(formPanel, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton postButton = createMinimalButton("Post Announcement", new Color(70, 130, 180));
        postButton.addActionListener(e -> postAnnouncement());
        buttonPanel.add(postButton);

        JButton clearButton = createMinimalButton("Clear", new Color(149, 165, 166));
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createMinimalButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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
