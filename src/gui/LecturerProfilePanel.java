package gui;

import models.Lecturer;

import javax.swing.*;
import java.awt.*;

public class LecturerProfilePanel extends JPanel {
    private Lecturer lecturer;

    public LecturerProfilePanel(Lecturer lecturer) {
        this.lecturer = lecturer;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel profileCard = new JPanel();
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setBackground(Color.WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));
        profileCard.setMaximumSize(new Dimension(800, 500));

        addProfileField(profileCard, "Full Name", lecturer.getFullName());
        addProfileField(profileCard, "Employee Number", lecturer.getEmployeeNumber());
        addProfileField(profileCard, "Department", lecturer.getDepartment());
        addProfileField(profileCard, "Specialization", lecturer.getSpecialization());
        addProfileField(profileCard, "Qualification", lecturer.getQualification());
        addProfileField(profileCard, "Office Location", lecturer.getOfficeLocation());
        addProfileField(profileCard, "Phone Number", lecturer.getPhoneNumber());
        addProfileField(profileCard, "Status", lecturer.getStatus());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(236, 240, 241));
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(profileCard);

        add(container, BorderLayout.NORTH);
    }

    private void addProfileField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setMaximumSize(new Dimension(740, 35));

        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel valueComponent = new JLabel(value != null ? value : "N/A");
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 15));

        fieldPanel.add(labelComponent);
        fieldPanel.add(valueComponent);

        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }
}
