package services;

import database.MySQLDatabase;
import models.Payment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Payment Service - Business logic for payment operations
 */
public class PaymentService {
    private MySQLDatabase db;

    public PaymentService() {
        this.db = MySQLDatabase.getInstance();
    }

    /**
     * Record a new payment and update student fee balance
     */
    public boolean recordPayment(Payment payment) {
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            // Start transaction
            db.executeUpdate("START TRANSACTION");

            // Insert payment record
            String insertQuery = "INSERT INTO payments (student_id, amount, payment_date, " +
                    "payment_method, reference_number, purpose, academic_year, semester, processed_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Object[] params = {
                    payment.getStudentId(),
                    payment.getAmount(),
                    payment.getPaymentDate(),
                    payment.getPaymentMethod(),
                    payment.getReferenceNumber(),
                    payment.getPurpose(),
                    payment.getAcademicYear(),
                    payment.getSemester(),
                    payment.getProcessedBy()
            };

            boolean success = db.executePreparedQuery(insertQuery, params);

            if (success) {
                // Update student fee balance (reduce by payment amount)
                String updateBalanceQuery = "UPDATE students SET fee_balance = fee_balance - ? WHERE student_id = ?";
                db.executePreparedQuery(updateBalanceQuery,
                        new Object[] { payment.getAmount(), payment.getStudentId() });

                // Commit transaction
                db.executeUpdate("COMMIT");
                System.out.println("Payment recorded successfully. Reference: " + payment.getReferenceNumber());
                return true;
            } else {
                db.executeUpdate("ROLLBACK");
                return false;
            }

        } catch (Exception e) {
            try {
                db.executeUpdate("ROLLBACK");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.err.println("Error recording payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all payments with optional filters
     */
    public List<Payment> getAllPayments(String studentFilter, String dateFromFilter, String dateToFilter) {
        List<Payment> payments = new ArrayList<>();

        try {
            if (!db.isConnected()) {
                db.connect();
            }

            StringBuilder query = new StringBuilder(
                    "SELECT p.payment_id, p.student_id, p.amount, p.payment_date, " +
                            "p.payment_method, p.reference_number, p.purpose, p.academic_year, " +
                            "p.semester, p.processed_by, " +
                            "s.registration_number, " +
                            "CONCAT(per.first_name, ' ', per.last_name) as student_name, " +
                            "CONCAT(adm_per.first_name, ' ', adm_per.last_name) as processed_by_name " +
                            "FROM payments p " +
                            "JOIN students s ON p.student_id = s.student_id " +
                            "JOIN persons per ON s.person_id = per.person_id " +
                            "LEFT JOIN admins a ON p.processed_by = a.admin_id " +
                            "LEFT JOIN persons adm_per ON a.person_id = adm_per.person_id " +
                            "WHERE 1=1 ");

            List<Object> params = new ArrayList<>();

            // Add filters
            if (studentFilter != null && !studentFilter.trim().isEmpty()) {
                query.append("AND (s.registration_number LIKE ? OR " +
                        "CONCAT(per.first_name, ' ', per.last_name) LIKE ?) ");
                String searchPattern = "%" + studentFilter + "%";
                params.add(searchPattern);
                params.add(searchPattern);
            }

            if (dateFromFilter != null && !dateFromFilter.trim().isEmpty()) {
                query.append("AND p.payment_date >= ? ");
                params.add(dateFromFilter);
            }

            if (dateToFilter != null && !dateToFilter.trim().isEmpty()) {
                query.append("AND p.payment_date <= ? ");
                params.add(dateToFilter);
            }

            query.append("ORDER BY p.payment_date DESC, p.payment_id DESC");

            ResultSet rs = db.executePreparedSelect(query.toString(), params.toArray());

            while (rs != null && rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }

            if (rs != null) {
                rs.close();
            }

        } catch (SQLException e) {
            System.err.println("Error fetching payments: " + e.getMessage());
            e.printStackTrace();
        }

        return payments;
    }

    /**
     * Get payments for a specific student
     */
    public List<Payment> getStudentPayments(int studentId) {
        List<Payment> payments = new ArrayList<>();

        try {
            if (!db.isConnected()) {
                db.connect();
            }

            String query = "SELECT p.payment_id, p.student_id, p.amount, p.payment_date, " +
                    "p.payment_method, p.reference_number, p.purpose, p.academic_year, " +
                    "p.semester, p.processed_by, " +
                    "s.registration_number, " +
                    "CONCAT(per.first_name, ' ', per.last_name) as student_name, " +
                    "CONCAT(adm_per.first_name, ' ', adm_per.last_name) as processed_by_name " +
                    "FROM payments p " +
                    "JOIN students s ON p.student_id = s.student_id " +
                    "JOIN persons per ON s.person_id = per.person_id " +
                    "LEFT JOIN admins a ON p.processed_by = a.admin_id " +
                    "LEFT JOIN persons adm_per ON a.person_id = adm_per.person_id " +
                    "WHERE p.student_id = ? " +
                    "ORDER BY p.payment_date DESC, p.payment_id DESC";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { studentId });

            while (rs != null && rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }

            if (rs != null) {
                rs.close();
            }

        } catch (SQLException e) {
            System.err.println("Error fetching student payments: " + e.getMessage());
            e.printStackTrace();
        }

        return payments;
    }

    /**
     * Get payment by reference number
     */
    public Payment getPaymentByReference(String referenceNumber) {
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            String query = "SELECT p.payment_id, p.student_id, p.amount, p.payment_date, " +
                    "p.payment_method, p.reference_number, p.purpose, p.academic_year, " +
                    "p.semester, p.processed_by, " +
                    "s.registration_number, " +
                    "CONCAT(per.first_name, ' ', per.last_name) as student_name, " +
                    "CONCAT(adm_per.first_name, ' ', adm_per.last_name) as processed_by_name " +
                    "FROM payments p " +
                    "JOIN students s ON p.student_id = s.student_id " +
                    "JOIN persons per ON s.person_id = per.person_id " +
                    "LEFT JOIN admins a ON p.processed_by = a.admin_id " +
                    "LEFT JOIN persons adm_per ON a.person_id = adm_per.person_id " +
                    "WHERE p.reference_number = ?";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { referenceNumber });

            if (rs != null && rs.next()) {
                Payment payment = mapResultSetToPayment(rs);
                rs.close();
                return payment;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching payment by reference: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get total payments for a student
     */
    public double getTotalPaymentsByStudent(int studentId) {
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            String query = "SELECT COALESCE(SUM(amount), 0) as total FROM payments WHERE student_id = ?";
            ResultSet rs = db.executePreparedSelect(query, new Object[] { studentId });

            if (rs != null && rs.next()) {
                double total = rs.getDouble("total");
                rs.close();
                return total;
            }

        } catch (SQLException e) {
            System.err.println("Error calculating total payments: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Generate unique reference number
     */
    public String generateReferenceNumber() {
        return "PAY-" + System.currentTimeMillis();
    }

    /**
     * Check if reference number already exists
     */
    public boolean referenceExists(String referenceNumber) {
        try {
            if (!db.isConnected()) {
                db.connect();
            }

            String query = "SELECT COUNT(*) as count FROM payments WHERE reference_number = ?";
            ResultSet rs = db.executePreparedSelect(query, new Object[] { referenceNumber });

            if (rs != null && rs.next()) {
                int count = rs.getInt("count");
                rs.close();
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking reference number: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Map ResultSet to Payment object
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setStudentId(rs.getInt("student_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setReferenceNumber(rs.getString("reference_number"));
        payment.setPurpose(rs.getString("purpose"));
        payment.setAcademicYear(rs.getString("academic_year"));
        payment.setSemester(rs.getInt("semester"));
        payment.setProcessedBy(rs.getInt("processed_by"));
        payment.setRegistrationNumber(rs.getString("registration_number"));
        payment.setStudentName(rs.getString("student_name"));
        payment.setProcessedByName(rs.getString("processed_by_name"));
        return payment;
    }
}
