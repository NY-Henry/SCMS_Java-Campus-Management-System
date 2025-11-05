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
    private JPanel announcementsContainer;

    public AnnouncementsPanel(MySQLDatabase db, String role) {
        this.role = role;
        this.announcementService = new AnnouncementService(db);

        initializeUI();
        loadAnnouncements();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Announcements");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadAnnouncements());
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

    private void loadAnnouncements() {
        announcementsContainer.removeAll();

        List<Announcement> announcements;

        // Load announcements based on role
        System.out.println("DEBUG AnnouncementsPanel: Loading announcements for role: " + role);
        if ("STUDENT".equalsIgnoreCase(role)) {
            announcements = announcementService.getAnnouncementsForStudents();
        } else if ("LECTURER".equalsIgnoreCase(role)) {
            announcements = announcementService.getAnnouncementsForLecturers();
        } else {
            announcements = List.of(); // Empty list for other roles
        }

        System.out.println("DEBUG AnnouncementsPanel: Received " + announcements.size() + " announcements");

        if (announcements.isEmpty()) {
            JLabel noAnnouncementsLabel = new JLabel("No announcements at this time.");
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

        JLabel titleLabel = new JLabel(announcement.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Metadata (posted by, date, audience)
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        metaPanel.setBackground(Color.WHITE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        String dateStr = announcement.getPostedAt().format(formatter);

        JLabel metaLabel = new JLabel(
                String.format("Posted by %s | %s | %s",
                        announcement.getPostedByName(),
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
            expiryLabel.setForeground(new Color(231, 76, 60));
            card.add(expiryLabel, BorderLayout.SOUTH);
        }

        return card;
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
