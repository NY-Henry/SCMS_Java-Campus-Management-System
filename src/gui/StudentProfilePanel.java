package gui;

import models.Student;

import javax.swing.*;
import java.awt.*;

/**
 * Student profile display panel
 */
public class StudentProfilePanel extends JPanel {
    private Student student;

    public StudentProfilePanel(Student student) {
        this.student = student;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Profile card
        JPanel profileCard = new JPanel();
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setBackground(Color.WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));
        profileCard.setMaximumSize(new Dimension(800, 600));

        addProfileField(profileCard, "Full Name", student.getFullName());
        addProfileField(profileCard, "Registration Number", student.getRegistrationNumber());
        addProfileField(profileCard, "Program", student.getProgram());
        addProfileField(profileCard, "Year of Study", "Year " + student.getYearOfStudy());
        addProfileField(profileCard, "Semester", "Semester " + student.getSemester());
        addProfileField(profileCard, "Phone Number", student.getPhoneNumber());
        addProfileField(profileCard, "Gender", student.getGender());
        addProfileField(profileCard, "Status", student.getStatus());
        addProfileField(profileCard, "GPA", String.format("%.2f", student.getGpa()));
        addProfileField(profileCard, "Fee Balance", String.format("UGX %.2f", student.getFeeBalance()));

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
