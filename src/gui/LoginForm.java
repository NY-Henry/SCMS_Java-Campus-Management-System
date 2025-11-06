package gui;

import database.MySQLDatabase;
import models.Admin;
import models.Lecturer;
import models.Person;
import models.Student;
import services.AuthenticationService;
import services.LogService;
import utils.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login Form - Entry point of the application
 * Demonstrates GUI design with Swing and event-driven programming
 */
public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private AuthenticationService authService;
    private LogService logService;

    public LoginForm() {
        // Initialize database connection
        MySQLDatabase db = MySQLDatabase.getInstance();
        if (!db.connect()) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to database!\nPlease make sure the database server(eg. WAMP..) is running, and also the sql file in the database folder in the root our project was imported properly.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        authService = new AuthenticationService();
        logService = new LogService();
        initializeUI();
    }

    private void initializeUI() {
        // Frame settings
        setTitle("SCMS - Smart Campus Management System");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with clean white background
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new GridBagLayout());

        // Login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setPreferredSize(new Dimension(400, 520));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Logo/Title
        JLabel titleLabel = new JLabel("NDEJJE UNIVERSITY");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Smart Campus Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Note label with HTML for line wrapping
        JLabel noteLabel = new JLabel(
                "<html><center>(**Check the HOW_To_RUN.txt file in our project folder<br>for the initial login credentials)</center></html>");
        noteLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        noteLabel.setForeground(new Color(140, 140, 140));
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Separator line
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(320, 1));
        separator.setForeground(new Color(230, 230, 235));

        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(new Color(120, 120, 120));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(320, 40));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setForeground(new Color(45, 45, 45));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(new Color(120, 120, 120));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(320, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(new Color(45, 45, 45));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setMaximumSize(new Dimension(320, 44));
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (loginButton.isEnabled()) {
                    loginButton.setBackground(new Color(70, 130, 180).darker());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (loginButton.isEnabled()) {
                    loginButton.setBackground(new Color(70, 130, 180));
                }
            }
        });

        // Register button
        registerButton = new JButton("Create Student Account");
        registerButton.setMaximumSize(new Dimension(320, 40));
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerButton.setForeground(new Color(100, 100, 110));
        registerButton.setBackground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(250, 250, 252));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(Color.WHITE);
            }
        });

        // Add components to login panel
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        loginPanel.add(subtitleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(noteLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        loginPanel.add(separator);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        loginPanel.add(usernameLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        loginPanel.add(registerButton);

        // Add login panel to main panel
        mainPanel.add(loginPanel);

        // Add main panel to frame
        add(mainPanel);

        // Event listeners - Event-driven programming
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationForm();
            }
        });

        // Allow Enter key to login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    /**
     * Handle login button click - Event-driven programming
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password!",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show loading
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Perform login in background thread to avoid UI freeze
        SwingWorker<Person, Void> worker = new SwingWorker<Person, Void>() {
            @Override
            protected Person doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    Person user = get();

                    if (user != null) {
                        // Log successful login
                        logService.logLogin(user.getUserId(), username, user.getRole());

                        // Store user in session
                        SessionManager.getInstance().setCurrentUser(user);

                        // Open appropriate dashboard based on role
                        openDashboard(user);

                        // Close login form
                        dispose();

                    } else {
                        // Log failed login attempt
                        logService.logFailedLogin(username);

                        JOptionPane.showMessageDialog(LoginForm.this,
                                "Invalid username or password!",
                                "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Error during login: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                }
            }
        };

        worker.execute();
    }

    /**
     * Open appropriate dashboard based on user role - Polymorphism
     */
    private void openDashboard(Person user) {
        if (user instanceof Student) {
            new StudentDashboard((Student) user).setVisible(true);
        } else if (user instanceof Lecturer) {
            new LecturerDashboard((Lecturer) user).setVisible(true);
        } else if (user instanceof Admin) {
            new AdminDashboard((Admin) user).setVisible(true);
        }
    }

    /**
     * Open student registration form
     */
    private void openRegistrationForm() {
        new StudentRegistrationForm(this).setVisible(true);
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}
