import gui.LoginForm;

import javax.swing.*;

/**
 * Main entry point for Smart Campus Management System (SCMS)
 * 
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

                System.out.println("GROUP 1 MEMBERS:");
                System.out.println("NYOMORE HENRY\t\t23/2/370/W/736");
                System.out.println("MUKIIBI SHADIA\t\t23/2/370/W/620");
                System.out.println("KISENGULA LEONARD\t24/2/370/W/513");
                System.out.println("OBOTH JULIUS CARLTON\t24/2/370/W/527");
                System.out.println("AIJUKA ARNOLD");
                System.out.println("===================================");
                System.out.println("Starting application...");

                // Create and display login form
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
}