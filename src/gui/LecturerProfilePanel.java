package gui;

import models.Lecturer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LecturerProfilePanel extends JPanel {
    private Lecturer lecturer;

    public LecturerProfilePanel(Lecturer lecturer) {
        this.lecturer = lecturer;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        add(titleLabel, BorderLayout.NORTH);

        // Profile content panel
        JPanel profileContent = new JPanel();
        profileContent.setLayout(new BoxLayout(profileContent, BoxLayout.Y_AXIS));
        profileContent.setBackground(Color.WHITE);

        addProfileRow(profileContent, "Full Name", lecturer.getFullName());
        addProfileRow(profileContent, "Employee Number", lecturer.getEmployeeNumber());
        addProfileRow(profileContent, "Department", lecturer.getDepartment());
        addProfileRow(profileContent, "Specialization", lecturer.getSpecialization());
        addProfileRow(profileContent, "Qualification", lecturer.getQualification());
        addProfileRow(profileContent, "Office Location", lecturer.getOfficeLocation());
        addProfileRow(profileContent, "Phone Number", lecturer.getPhoneNumber());
        addProfileRow(profileContent, "Status", lecturer.getStatus());

        JScrollPane scrollPane = new JScrollPane(profileContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addProfileRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)),
                new EmptyBorder(15, 0, 15, 0)));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelComponent.setForeground(new Color(120, 120, 120));
        labelComponent.setPreferredSize(new Dimension(180, 30));

        JLabel valueComponent = new JLabel(value != null ? value : "N/A");
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueComponent.setForeground(new Color(45, 45, 45));

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);

        panel.add(rowPanel);
    }
}
