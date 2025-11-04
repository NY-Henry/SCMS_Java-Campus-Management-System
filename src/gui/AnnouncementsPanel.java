package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Announcements panel - simplified version
 */
public class AnnouncementsPanel extends JPanel {
    private String audience;

    public AnnouncementsPanel(String audience) {
        this.audience = audience;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Announcements");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel announcementCard = new JPanel();
        announcementCard.setLayout(new BoxLayout(announcementCard, BoxLayout.Y_AXIS));
        announcementCard.setBackground(Color.WHITE);
        announcementCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        announcementCard.setMaximumSize(new Dimension(900, 150));

        JLabel annTitle = new JLabel("Welcome to Semester I 2025/2026");
        annTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel annContent = new JLabel("<html>Welcome all students to the new academic semester. " +
                "Classes begin on November 4th, 2025. Please ensure all fees are cleared.</html>");
        annContent.setFont(new Font("Arial", Font.PLAIN, 14));

        announcementCard.add(annTitle);
        announcementCard.add(Box.createRigidArea(new Dimension(0, 10)));
        announcementCard.add(annContent);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(236, 240, 241));
        container.add(titleLabel);
        container.add(announcementCard);

        add(container, BorderLayout.NORTH);
    }
}
