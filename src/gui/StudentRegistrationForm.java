package gui;

import services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Student Registration Form
 * Demonstrates form validation and event handling
 */
public class StudentRegistrationForm extends JFrame {
    private JTextField usernameField, emailField, firstNameField, lastNameField;
    private JTextField phoneField, regNumberField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> programCombo, yearCombo, semesterCombo;
    private JButton registerButton, cancelButton;
    private AuthenticationService authService;
    private LoginForm parentForm;

    public StudentRegistrationForm(LoginForm parent) {
        this.parentForm = parent;
        this.authService = new AuthenticationService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Student Registration - SCMS");
        setSize(600, 700);
        setLocationRelativeTo(parentForm);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(600, 60));

        JLabel headerLabel = new JLabel("Create Student Account");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Row 0: First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);

        // Row 1: Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);

        // Row 2: Registration Number
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Registration Number:"), gbc);
        gbc.gridx = 1;
        regNumberField = new JTextField(20);
        formPanel.add(regNumberField, gbc);

        // Row 3: Program
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Program:"), gbc);
        gbc.gridx = 1;
        String[] programs = {
                "Bachelor of Information Technology",
                "Bachelor of Computer Science",
                "Bachelor of Software Engineering",
                "Bachelor of Information Systems"
        };
        programCombo = new JComboBox<>(programs);
        formPanel.add(programCombo, gbc);

        // Row 4: Year of Study
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Year of Study:"), gbc);
        gbc.gridx = 1;
        String[] years = { "1", "2", "3", "4" };
        yearCombo = new JComboBox<>(years);
        formPanel.add(yearCombo, gbc);

        // Row 5: Semester
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        String[] semesters = { "1", "2" };
        semesterCombo = new JComboBox<>(semesters);
        formPanel.add(semesterCombo, gbc);

        // Row 6: Phone
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Row 7: Email
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Row 8: Username
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Row 9: Password
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Row 10: Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 10;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * Handle registration form submission
     * Demonstrates input validation and exception handling
     */
    private void handleRegistration() {
        try {
            // Get form data
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String regNumber = regNumberField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String program = (String) programCombo.getSelectedItem();
            int year = Integer.parseInt((String) yearCombo.getSelectedItem());
            int semester = Integer.parseInt((String) semesterCombo.getSelectedItem());

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty() || regNumber.isEmpty() ||
                    phone.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("All fields are required!");
            }

            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match!");
            }

            if (password.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters!");
            }

            if (!email.contains("@")) {
                throw new IllegalArgumentException("Invalid email format!");
            }

            // Disable button during registration
            registerButton.setEnabled(false);
            registerButton.setText("Registering...");

            // Perform registration
            boolean success = authService.registerStudent(
                    username, password, email, firstName, lastName,
                    phone, regNumber, program, year, semester);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful!\nYou can now login with your credentials.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                throw new Exception("Registration failed! Username or registration number may already exist.");
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            registerButton.setEnabled(true);
            registerButton.setText("Register");
        }
    }
}
