package services;

import database.MySQLDatabase;
import java.net.InetAddress;

/**
 * LogService - Central service for logging system activities
 */
public class LogService {
    private MySQLDatabase db;

    public LogService() {
        this.db = MySQLDatabase.getInstance();
    }

    /**
     * Log a system action
     *
     * @param userId  The user ID performing the action (can be null for system
     *                actions)
     * @param action  The action type (e.g., LOGIN, LOGOUT, CREATE, UPDATE, DELETE,
     *                VIEW)
     * @param details Detailed description of the action
     */
    public void logAction(Integer userId, String action, String details) {
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            String ipAddress = getIpAddress();

            String sql = "INSERT INTO system_logs (user_id, action, details, ip_address) VALUES (?, ?, ?, ?)";
            db.executePreparedQuery(sql, new Object[] { userId, action, details, ipAddress });

        } catch (Exception e) {
            System.err.println("Error logging action: " + e.getMessage());
            // Don't throw exception to avoid disrupting normal operations
        }
    }

    /**
     * Log a login action
     */
    public void logLogin(int userId, String username, String role) {
        logAction(userId, "LOGIN", "User " + username + " (" + role + ") logged in successfully");
    }

    /**
     * Log a logout action
     */
    public void logLogout(int userId, String username, String role) {
        logAction(userId, "LOGOUT", "User " + username + " (" + role + ") logged out");
    }

    /**
     * Log a failed login attempt
     */
    public void logFailedLogin(String username) {
        logAction(null, "LOGIN_FAILED", "Failed login attempt for username: " + username);
    }

    /**
     * Log student registration
     */
    public void logStudentRegistration(int userId, String studentName, String regNumber) {
        logAction(userId, "REGISTER",
                "New student registered: " + studentName + " (Reg: " + regNumber + ")");
    }

    /**
     * Log course registration
     */
    public void logCourseRegistration(int userId, String studentName, String courseName) {
        logAction(userId, "REGISTER",
                "Course registration: " + studentName + " enrolled in " + courseName);
    }

    /**
     * Log payment processing
     */
    public void logPayment(int userId, String studentName, double amount, String method) {
        logAction(userId, "CREATE",
                String.format("Payment processed: UGX %.2f from %s via %s", amount, studentName, method));
    }

    /**
     * Log grade submission
     */
    public void logGradeSubmission(int userId, String studentName, String courseName, String grade) {
        logAction(userId, "CREATE",
                "Grade submitted: " + grade + " for " + studentName + " in " + courseName);
    }

    /**
     * Log profile update
     */
    public void logProfileUpdate(int userId, String userName) {
        logAction(userId, "UPDATE", "Profile updated by " + userName);
    }

    /**
     * Log announcement creation
     */
    public void logAnnouncementCreation(int userId, String title, String audience) {
        logAction(userId, "CREATE",
                "Announcement created: '" + title + "' for " + audience);
    }

    /**
     * Log announcement deletion
     */
    public void logAnnouncementDeletion(int userId, String title) {
        logAction(userId, "DELETE", "Announcement deleted: '" + title + "'");
    }

    /**
     * Log report generation
     */
    public void logReportGeneration(int userId, String reportType) {
        logAction(userId, "EXPORT", "Report generated: " + reportType);
    }

    /**
     * Log data export
     */
    public void logDataExport(int userId, String dataType) {
        logAction(userId, "EXPORT", "Data exported: " + dataType);
    }

    /**
     * Log record deletion
     */
    public void logRecordDeletion(int userId, String recordType, String recordId) {
        logAction(userId, "DELETE",
                "Record deleted: " + recordType + " (ID: " + recordId + ")");
    }

    /**
     * Log record creation
     */
    public void logRecordCreation(int userId, String recordType, String details) {
        logAction(userId, "CREATE", "Record created: " + recordType + " - " + details);
    }

    /**
     * Log record update
     */
    public void logRecordUpdate(int userId, String recordType, String recordId) {
        logAction(userId, "UPDATE",
                "Record updated: " + recordType + " (ID: " + recordId + ")");
    }

    /**
     * Log view action (accessing sensitive information)
     */
    public void logView(int userId, String viewType, String details) {
        logAction(userId, "VIEW", "Viewed: " + viewType + " - " + details);
    }

    /**
     * Get the current IP address
     */
    private String getIpAddress() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostAddress();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
