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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title and edit button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));

        JButton editButton = createMinimalButton("Edit Profile", new Color(70, 130, 180));
        editButton.addActionListener(e -> showEditProfileDialog());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(editButton, BorderLayout.EAST);

        // Profile information section - Clean rows
        JPanel profileSection = new JPanel();
        profileSection.setLayout(new BoxLayout(profileSection, BoxLayout.Y_AXIS));
        profileSection.setOpaque(false);
        profileSection.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));

        addProfileRow(profileSection, "Full Name", student.getFullName());
        addProfileRow(profileSection, "Registration Number", student.getRegistrationNumber());
        addProfileRow(profileSection, "Program", student.getProgram());
        addProfileRow(profileSection, "Year of Study", "Year " + student.getYearOfStudy());
        addProfileRow(profileSection, "Semester", "Semester " + student.getSemester());
        addProfileRow(profileSection, "Phone Number", student.getPhoneNumber());
        addProfileRow(profileSection, "Gender", student.getGender());
        addProfileRow(profileSection, "Status", student.getStatus());
        addProfileRow(profileSection, "GPA", String.format("%.2f", student.getGpa()));
        addProfileRow(profileSection, "Fee Balance", String.format("UGX %.2f", student.getFeeBalance()));

        // Wrapper for profile section
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.add(topPanel, BorderLayout.NORTH);
        contentWrapper.add(profileSection, BorderLayout.CENTER);

        add(contentWrapper, BorderLayout.NORTH);
    }

    private void addProfileRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false);
        rowPanel.setMaximumSize(new Dimension(800, 40));
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 245)),
                BorderFactory.createEmptyBorder(12, 0, 12, 0)));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelComponent.setForeground(new Color(120, 120, 120));

        JLabel valueComponent = new JLabel(value != null ? value : "N/A");
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueComponent.setForeground(new Color(45, 45, 45));

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.EAST);

        panel.add(rowPanel);
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
