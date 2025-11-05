package gui;

import database.MySQLDatabase;
import models.Announcement;
import models.Lecturer;
import services.AnnouncementService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for lecturers to view and manage their own announcements
 */
public class MyAnnouncementsPanel extends JPanel {
    private final AnnouncementService announcementService;
    private final Lecturer lecturer;
    private JPanel announcementsContainer;

    public MyAnnouncementsPanel(MySQLDatabase db, Lecturer lecturer) {
        this.lecturer = lecturer;
        this.announcementService = new AnnouncementService(db);

        initializeUI();
        loadMyAnnouncements();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("My Announcements");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadMyAnnouncements());
        titlePanel.add(refreshButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Announcements container with scroll
        announcementsContainer = new JPanel();
        announcementsContainer.setLayout(new BoxLayout(announcementsContainer, BoxLayout.Y_AXIS));
        announcementsContainer.setBackground(new Color(236, 240, 241));

        JScrollPane scrollPane = new JScrollPane(announcementsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadMyAnnouncements() {
        announcementsContainer.removeAll();

        List<Announcement> announcements = announcementService.getAnnouncementsByLecturer(lecturer.getUserId());

        if (announcements.isEmpty()) {
            JLabel noAnnouncementsLabel = new JLabel("You haven't posted any announcements yet.");
            noAnnouncementsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noAnnouncementsLabel.setForeground(Color.GRAY);
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
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                new EmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Title and metadata panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        // Title with delete button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(announcement.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Delete button (always shown since these are user's own announcements)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        // Status indicator
        if (!announcement.isActive()) {
            JLabel deletedLabel = new JLabel("DELETED");
            deletedLabel.setFont(new Font("Arial", Font.BOLD, 11));
            deletedLabel.setForeground(new Color(127, 140, 141));
            deletedLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            buttonPanel.add(deletedLabel);
        } else if (announcement.isExpired()) {
            JLabel expiredLabel = new JLabel("EXPIRED");
            expiredLabel.setFont(new Font("Arial", Font.BOLD, 11));
            expiredLabel.setForeground(new Color(230, 126, 34));
            expiredLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            buttonPanel.add(expiredLabel);
        }

        // Delete button (only if active)
        if (announcement.isActive()) {
            JButton deleteButton = new JButton("Delete");
            deleteButton.setFont(new Font("Arial", Font.BOLD, 11));
            deleteButton.setBackground(new Color(231, 76, 60));
            deleteButton.setForeground(Color.RED);
            deleteButton.setFocusPainted(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            deleteButton.addActionListener(e -> deleteAnnouncement(announcement));
            buttonPanel.add(deleteButton);
        }

        titlePanel.add(buttonPanel, BorderLayout.EAST);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        // Metadata (date, audience, status)
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        metaPanel.setBackground(Color.WHITE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        String dateStr = announcement.getPostedAt().format(formatter);

        JLabel metaLabel = new JLabel(
                String.format("Posted on %s | %s",
                        dateStr,
                        formatAudience(announcement)));
        metaLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        metaLabel.setForeground(Color.GRAY);
        metaPanel.add(metaLabel);

        headerPanel.add(metaPanel, BorderLayout.SOUTH);
        card.add(headerPanel, BorderLayout.NORTH);

        // Content
        JTextArea contentArea = new JTextArea(announcement.getContent());
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(new EmptyBorder(10, 0, 0, 0));
        card.add(contentArea, BorderLayout.CENTER);

        // Expiry info (if applicable)
        if (announcement.getExpiresAt() != null) {
            JLabel expiryLabel = new JLabel("Expires: " +
                    announcement.getExpiresAt().format(formatter));
            expiryLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            expiryLabel.setForeground(announcement.isExpired() ? new Color(127, 140, 141) : new Color(231, 76, 60));
            card.add(expiryLabel, BorderLayout.SOUTH);
        }

        return card;
    }

    private void deleteAnnouncement(Announcement announcement) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this announcement?\n\nTitle: " + announcement.getTitle()
                        + "\n\nThis action cannot be undone.",
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
                loadMyAnnouncements(); // Refresh the list
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
                return "Students Only";
            case "LECTURERS":
                return "Lecturers Only";
            case "SPECIFIC_COURSE":
                return announcement.getCourseName() != null ? "Course: " + announcement.getCourseName()
                        : "Specific Course";
            default:
                return audience;
        }
    }
}
