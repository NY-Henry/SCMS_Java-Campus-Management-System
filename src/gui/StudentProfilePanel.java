package gui;

import database.MySQLDatabase;
import models.Student;

import javax.swing.*;
import java.awt.*;

/**
 * Student profile display panel with edit capability
 */
public class StudentProfilePanel extends JPanel {
    private Student student;
    private MySQLDatabase db;

    public StudentProfilePanel(Student student) {
        this.student = student;
        this.db = MySQLDatabase.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title Panel with Edit Button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(236, 240, 241));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton editButton = new JButton("Edit Profile");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.addActionListener(e -> showEditProfileDialog());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(editButton, BorderLayout.EAST);

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
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(titlePanel);
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

    private void showEditProfileDialog() {
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            // Fetch current email from database
            String emailQuery = "SELECT email FROM users WHERE user_id = ?";
            java.sql.ResultSet rs = db.executePreparedSelect(emailQuery, new Object[] { student.getUserId() });

            String currentEmail = "";
            if (rs != null && rs.next()) {
                currentEmail = rs.getString("email");
                rs.close();
            }

            final String userEmail = currentEmail;

            // Create dialog
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Edit Profile", true);
            dialog.setSize(600, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            // Title Panel
            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(new Color(52, 152, 219));
            titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel titleLabel = new JLabel("Edit My Profile");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setForeground(Color.WHITE);
            titlePanel.add(titleLabel);

            // Form Panel
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 5, 10, 5);

            int row = 0;

            // Full Name (Read-only)
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel("Full Name:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JTextField nameField = new JTextField(student.getFullName());
            nameField.setPreferredSize(new Dimension(300, 30));
            nameField.setEditable(false);
            nameField.setBackground(new Color(240, 240, 240));
            formPanel.add(nameField, gbc);

            row++;

            // Phone Number (Editable)
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel("Phone Number:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JTextField phoneField = new JTextField(student.getPhoneNumber());
            phoneField.setPreferredSize(new Dimension(300, 30));
            formPanel.add(phoneField, gbc);

            row++;

            // Gender (Editable)
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel("Gender:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JComboBox<String> genderCombo = new JComboBox<>(new String[] { "MALE", "FEMALE", "OTHER" });
            genderCombo.setSelectedItem(student.getGender());
            genderCombo.setPreferredSize(new Dimension(300, 30));
            formPanel.add(genderCombo, gbc);

            row++;

            // Email (Editable)
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            formPanel.add(new JLabel("Email:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JTextField emailField = new JTextField(userEmail);
            emailField.setPreferredSize(new Dimension(300, 30));
            formPanel.add(emailField, gbc);

            row++;

            // Address (Editable)
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            formPanel.add(new JLabel("Address:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.anchor = GridBagConstraints.CENTER;
            JTextArea addressArea = new JTextArea(student.getAddress(), 3, 20);
            addressArea.setLineWrap(true);
            addressArea.setWrapStyleWord(true);
            addressArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            JScrollPane addressScroll = new JScrollPane(addressArea);
            addressScroll.setPreferredSize(new Dimension(300, 80));
            formPanel.add(addressScroll, gbc);

            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);

            JButton saveBtn = new JButton("Save Changes");
            saveBtn.setBackground(new Color(46, 204, 113));
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
            saveBtn.setFocusPainted(false);
            saveBtn.setBorderPainted(false);
            saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            saveBtn.addActionListener(e -> {
                // Validation
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressArea.getText().trim();
                String gender = (String) genderCombo.getSelectedItem();

                if (phone.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Phone number and email are required.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Basic email validation
                if (!email.contains("@") || !email.contains(".")) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a valid email address.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Confirm update
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Save changes to your profile?",
                        "Confirm Update",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        if (!db.isConnected()) {
                            db.connect();
                        }

                        // Update persons table
                        String updatePersonQuery = "UPDATE persons SET phone_number = ?, gender = ?, address = ? " +
                                "WHERE person_id = ?";
                        boolean personUpdated = db.executePreparedQuery(updatePersonQuery,
                                new Object[] { phone, gender, address, student.getPersonId() });

                        // Update users table (email)
                        String updateUserQuery = "UPDATE users SET email = ? WHERE user_id = ?";
                        boolean userUpdated = db.executePreparedQuery(updateUserQuery,
                                new Object[] { email, student.getUserId() });

                        if (personUpdated && userUpdated) {
                            // Update student object
                            student.setPhoneNumber(phone);
                            student.setGender(gender);
                            student.setAddress(address);

                            JOptionPane.showMessageDialog(dialog,
                                    "Profile updated successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);

                            // Refresh the profile display
                            removeAll();
                            initializeUI();
                            revalidate();
                            repaint();

                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                    "Failed to update profile. Please try again.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Error updating profile: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(new Color(149, 165, 166));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
            cancelBtn.setFocusPainted(false);
            cancelBtn.setBorderPainted(false);
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelBtn.addActionListener(e -> dialog.dispose());

            buttonPanel.add(saveBtn);
            buttonPanel.add(cancelBtn);

            // Layout
            dialog.add(titlePanel, BorderLayout.NORTH);
            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading profile data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
