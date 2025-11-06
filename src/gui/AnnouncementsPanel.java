package gui;

import database.MySQLDatabase;
import models.Announcement;
import services.AnnouncementService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Announcements panel - displays announcements based on user role
 */
public class AnnouncementsPanel extends JPanel {
    private final AnnouncementService announcementService;
    private final String role;
    private final Integer currentUserId;
    private JPanel announcementsContainer;

    public AnnouncementsPanel(MySQLDatabase db, String role) {
        this(db, role, null);
    }

    public AnnouncementsPanel(MySQLDatabase db, String role, Integer currentUserId) {
        this.role = role;
        this.currentUserId = currentUserId;
        this.announcementService = new AnnouncementService(db);

        initializeUI();
        loadAnnouncements();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Top panel with title and refresh button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("Announcements");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));

        // Refresh button
        JButton refreshButton = createMinimalButton("Refresh", new Color(100, 100, 110));
        refreshButton.addActionListener(e -> loadAnnouncements());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Announcements container with scroll
        announcementsContainer = new JPanel();
        announcementsContainer.setLayout(new BoxLayout(announcementsContainer, BoxLayout.Y_AXIS));
        announcementsContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(announcementsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
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

    private void loadAnnouncements() {
        announcementsContainer.removeAll();

        List<Announcement> announcements;

        // Load announcements based on role
        if ("STUDENT".equalsIgnoreCase(role)) {
            announcements = announcementService.getAnnouncementsForStudents();
        } else if ("LECTURER".equalsIgnoreCase(role)) {
            announcements = announcementService.getAnnouncementsForLecturers();
        } else {
            announcements = List.of(); // Empty list for other roles
        }

        if (announcements.isEmpty()) {
            JLabel noAnnouncementsLabel = new JLabel("No announcements at this time.");
            noAnnouncementsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noAnnouncementsLabel.setForeground(new Color(120, 120, 120));
            noAnnouncementsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            announcementsContainer.add(Box.createVerticalStrut(50));
            announcementsContainer.add(noAnnouncementsLabel);
        } else {
            for (Announcement announcement : announcements) {
                JPanel card = createAnnouncementCard(announcement);
                announcementsContainer.add(card);
                announcementsContainer.add(Box.createVerticalStrut(15));
            }
        }

        announcementsContainer.revalidate();
        announcementsContainer.repaint();
    }

    private JPanel createAnnouncementCard(Announcement announcement) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(new Color(250, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                new EmptyBorder(20, 25, 20, 25)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Header panel with title and delete button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(announcement.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(45, 45, 45));

        // Add delete button if current user is the creator
        if (currentUserId != null && currentUserId.equals(announcement.getPostedBy())) {
            JButton deleteButton = createMinimalButton("Delete", new Color(220, 80, 80));
            deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            deleteButton.addActionListener(e -> deleteAnnouncement(announcement));
            headerPanel.add(deleteButton, BorderLayout.EAST);
        }

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Metadata
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        String dateStr = announcement.getPostedAt().format(formatter);

        JLabel metaLabel = new JLabel(
                String.format("Posted by %s • %s • %s",
                        announcement.getPostedByName(),
                        dateStr,
                        formatAudience(announcement)));
        metaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        metaLabel.setForeground(new Color(120, 120, 120));
        metaLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JTextArea contentArea = new JTextArea(announcement.getContent());
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setForeground(new Color(60, 60, 60));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setBorder(null);
        contentPanel.add(contentArea, BorderLayout.CENTER);

        // Expiry info (if applicable)
        if (announcement.getExpiresAt() != null) {
            JLabel expiryLabel = new JLabel("Expires: " +
                    announcement.getExpiresAt().format(formatter));
            expiryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            expiryLabel.setForeground(new Color(220, 80, 80));
            expiryLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
            contentPanel.add(expiryLabel, BorderLayout.SOUTH);
        }

        // Assemble card
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(headerPanel);
        topSection.add(metaLabel);

        card.add(topSection, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private void deleteAnnouncement(Announcement announcement) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this announcement?\n\nTitle: " + announcement.getTitle(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = announcementService.deactivateAnnouncement(announcement.getAnnouncementId());

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Announcement deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadAnnouncements(); // Refresh the list
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete announcement. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String formatAudience(Announcement announcement) {
        String audience = announcement.getTargetAudience();

        switch (audience) {
            case "ALL":
                return "All Users";
            case "STUDENTS":
                return "Students";
            case "LECTURERS":
                return "Lecturers";
            case "SPECIFIC_COURSE":
                return announcement.getCourseName() != null ? "Course: " + announcement.getCourseName()
                        : "Specific Course";
            default:
                return audience;
        }
    }
}
