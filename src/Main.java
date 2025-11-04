import gui.LoginForm;

import javax.swing.*;

/**
 * Main entry point for Smart Campus Management System (SCMS)
 * Ndejje University - Academic Year 2025/2026
 * 
 * @author Your Group Name
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // Set System Look and Feel for better UI appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set Look and Feel: " + e.getMessage());
        }

        // Launch application on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("===================================");
                System.out.println("Smart Campus Management System (SCMS)");
                System.out.println("Ndejje University");
                System.out.println("===================================");
                System.out.println("Starting application...");

                // Create and display login form
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
}