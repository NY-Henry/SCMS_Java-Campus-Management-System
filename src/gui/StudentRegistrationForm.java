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
        setSize(650, 750);
        setLocationRelativeTo(parentForm);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)));

        JLabel headerLabel = new JLabel("Create Student Account");
        headerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        headerLabel.setForeground(new Color(45, 45, 45));
        headerPanel.add(headerLabel);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        // Row 0: First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel firstNameLabel = createFieldLabel("First Name");
        formPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        firstNameField = createTextField();
        formPanel.add(firstNameField, gbc);

        // Row 1: Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lastNameLabel = createFieldLabel("Last Name");
        formPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        lastNameField = createTextField();
        formPanel.add(lastNameField, gbc);

        // Row 2: Registration Number
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel regNumberLabel = createFieldLabel("Registration Number");
        formPanel.add(regNumberLabel, gbc);
        gbc.gridx = 1;
        regNumberField = createTextField();
        formPanel.add(regNumberField, gbc);

        // Row 3: Program
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel programLabel = createFieldLabel("Program");
        formPanel.add(programLabel, gbc);
        gbc.gridx = 1;
        String[] programs = {
                "Bachelor of Information Technology",
                "Bachelor of Computer Science",
                "Bachelor of Software Engineering",
                "Bachelor of Information Systems",
                "Bachelor of Business Administration",
                "Bachelor of Accounting",
                "Bachelor of Marketing",
                "Bachelor of Finance",
                "Bachelor of Civil Engineering",
                "Bachelor of Mechanical Engineering",
                "Bachelor of Electrical Engineering",
                "Bachelor of Architecture",
                "Bachelor of Medicine",
                "Bachelor of Pharmacy",
                "Bachelor of Nursing"
        };
        programCombo = createComboBox(programs);
        formPanel.add(programCombo, gbc);

        // Row 4: Year of Study
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel yearLabel = createFieldLabel("Year of Study");
        formPanel.add(yearLabel, gbc);
        gbc.gridx = 1;
        String[] years = { "1", "2", "3", "4" };
        yearCombo = createComboBox(years);
        formPanel.add(yearCombo, gbc);

        // Row 5: Semester
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel semesterLabel = createFieldLabel("Semester");
        formPanel.add(semesterLabel, gbc);
        gbc.gridx = 1;
        String[] semesters = { "1", "2" };
        semesterCombo = createComboBox(semesters);
        formPanel.add(semesterCombo, gbc);

        // Row 6: Phone
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel phoneLabel = createFieldLabel("Phone Number");
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        phoneField = createTextField();
        formPanel.add(phoneField, gbc);

        // Row 7: Email
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel emailLabel = createFieldLabel("Email");
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        emailField = createTextField();
        formPanel.add(emailField, gbc);

        // Row 8: Username
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel usernameLabel = createFieldLabel("Username");
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        usernameField = createTextField();
        formPanel.add(usernameField, gbc);

        // Row 9: Password
        gbc.gridx = 0;
        gbc.gridy = 9;
        JLabel passwordLabel = createFieldLabel("Password");
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        passwordField = createPasswordField();
        formPanel.add(passwordField, gbc);

        // Row 10: Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 10;
        JLabel confirmPasswordLabel = createFieldLabel("Confirm Password");
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        confirmPasswordField = createPasswordField();
        formPanel.add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 40, 30, 40));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));

        registerButton = createMinimalButton("Register", new Color(70, 130, 180));
        registerButton.setPreferredSize(new Dimension(130, 40));

        cancelButton = createMinimalButton("Cancel", new Color(100, 100, 110));
        cancelButton.setPreferredSize(new Dimension(130, 40));

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

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(120, 120, 120));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(new Color(45, 45, 45));
        field.setPreferredSize(new Dimension(300, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(new Color(45, 45, 45));
        field.setPreferredSize(new Dimension(300, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setForeground(new Color(45, 45, 45));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(300, 38));
        combo.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        return combo;
    }

    private JButton createMinimalButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.darker());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });

        return button;
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
