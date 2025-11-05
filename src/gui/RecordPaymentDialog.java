package gui;

import database.MySQLDatabase;
import models.Admin;
import models.Payment;
import models.Student;
import services.PaymentService;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Record Payment Dialog - Admin can record new student payments
 */
public class RecordPaymentDialog extends JDialog {
    private Admin admin;
    private PaymentService paymentService;
    private MySQLDatabase db;
    private boolean paymentRecorded = false;

    private JComboBox<String> studentCombo;
    private JTextField amountField;
    private JTextField referenceField;
    private JComboBox<String> paymentMethodCombo;
    private JTextField purposeField;
    private JComboBox<String> yearCombo;
    private JComboBox<String> semesterCombo;
    private JLabel currentBalanceLabel;

    private List<Student> students;

    public RecordPaymentDialog(JFrame parent, Admin admin, PaymentService paymentService) {
        super(parent, "Record Student Payment", true);
        this.admin = admin;
        this.paymentService = paymentService;
        this.db = MySQLDatabase.getInstance();
        initializeUI();
        loadStudents();
    }

    private void initializeUI() {
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Record Student Payment");
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
        gbc.insets = new Insets(8, 5, 8, 5);

        int row = 0;

        // Student selection
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Student:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        studentCombo = new JComboBox<>();
        studentCombo.setPreferredSize(new Dimension(300, 30));
        studentCombo.addActionListener(e -> updateCurrentBalance());
        formPanel.add(studentCombo, gbc);

        row++;

        // Current balance display
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Current Balance:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        currentBalanceLabel = new JLabel("UGX 0.00");
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentBalanceLabel.setForeground(new Color(231, 76, 60));
        formPanel.add(currentBalanceLabel, gbc);

        row++;

        // Amount
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Amount (UGX):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(amountField, gbc);

        row++;

        // Payment Method
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Payment Method:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        paymentMethodCombo = new JComboBox<>(new String[] { "CASH", "BANK_TRANSFER", "MOBILE_MONEY", "CARD" });
        paymentMethodCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(paymentMethodCombo, gbc);

        row++;

        // Reference Number
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Reference Number:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel refPanel = new JPanel(new BorderLayout(5, 0));
        refPanel.setBackground(Color.WHITE);
        referenceField = new JTextField();
        referenceField.setPreferredSize(new Dimension(200, 30));
        JButton generateRefBtn = new JButton("Generate");
        generateRefBtn.setBackground(new Color(149, 165, 166));
        generateRefBtn.setForeground(Color.WHITE);
        generateRefBtn.setFocusPainted(false);
        generateRefBtn.setBorderPainted(false);
        generateRefBtn.addActionListener(e -> generateReferenceNumber());
        refPanel.add(referenceField, BorderLayout.CENTER);
        refPanel.add(generateRefBtn, BorderLayout.EAST);
        formPanel.add(refPanel, gbc);

        row++;

        // Purpose
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Purpose:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        purposeField = new JTextField("Tuition Fee");
        purposeField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(purposeField, gbc);

        row++;

        // Academic Year
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Academic Year:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        yearCombo = new JComboBox<>(new String[] { "2025/2026", "2024/2025", "2023/2024" });
        yearCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(yearCombo, gbc);

        row++;

        // Semester
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Semester:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        semesterCombo = new JComboBox<>(new String[] { "1", "2" });
        semesterCombo.setPreferredSize(new Dimension(300, 30));
        formPanel.add(semesterCombo, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Record Payment");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> recordPayment());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        // Layout
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStudents() {
        students = new ArrayList<>();
        studentCombo.removeAllItems();

        try {
            if (!db.isConnected()) {
                db.connect();
            }

            String query = "SELECT s.student_id, s.registration_number, s.fee_balance, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as student_name " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "WHERE s.status = 'ACTIVE' " +
                    "ORDER BY student_name";

            ResultSet rs = db.executePreparedSelect(query, new Object[] {});

            while (rs != null && rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setRegistrationNumber(rs.getString("registration_number"));
                student.setFirstName(rs.getString("student_name"));
                student.setFeeBalance(rs.getDouble("fee_balance"));

                students.add(student);
                studentCombo.addItem(student.getRegistrationNumber() + " - " + student.getFirstName());
            }

            if (rs != null) {
                rs.close();
            }

            if (students.isEmpty()) {
                studentCombo.addItem("-- No active students --");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateCurrentBalance() {
        int selectedIndex = studentCombo.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < students.size()) {
            Student student = students.get(selectedIndex);
            currentBalanceLabel.setText(String.format("UGX %.2f", student.getFeeBalance()));
            currentBalanceLabel.setForeground(
                    student.getFeeBalance() > 0 ? new Color(231, 76, 60) : new Color(46, 204, 113));
        }
    }

    private void generateReferenceNumber() {
        String reference = paymentService.generateReferenceNumber();
        referenceField.setText(reference);
    }

    private void recordPayment() {
        // Validation
        int selectedIndex = studentCombo.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= students.size()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String amountStr = amountField.getText().trim();
        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter payment amount",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive amount",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reference = referenceField.getText().trim();
        if (reference.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter or generate a reference number",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if reference already exists
        if (paymentService.referenceExists(reference)) {
            JOptionPane.showMessageDialog(this,
                    "Reference number already exists. Please use a different one.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create payment object
        Student student = students.get(selectedIndex);
        Payment payment = new Payment();
        payment.setStudentId(student.getStudentId());
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
        payment.setReferenceNumber(reference);
        payment.setPurpose(purposeField.getText().trim());
        payment.setAcademicYear((String) yearCombo.getSelectedItem());
        payment.setSemester(Integer.parseInt((String) semesterCombo.getSelectedItem()));
        payment.setProcessedBy(admin.getAdminId());

        // Record payment
        boolean success = paymentService.recordPayment(payment);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Payment recorded successfully!\n" +
                            "Student: " + student.getFirstName() + "\n" +
                            "Amount: UGX " + String.format("%.2f", amount) + "\n" +
                            "Reference: " + reference,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            paymentRecorded = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to record payment. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isPaymentRecorded() {
        return paymentRecorded;
    }
}
