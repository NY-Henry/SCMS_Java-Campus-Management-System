package gui;

import database.MySQLDatabase;
import models.Admin;
import models.Payment;
import services.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Manage Payments Panel - Admin can view all payment records with filters
 */
public class ManagePaymentsPanel extends JPanel {
    private Admin admin;
    private PaymentService paymentService;
    private MySQLDatabase db;
    private JTable paymentsTable;
    private DefaultTableModel tableModel;
    private JTextField studentSearchField;
    private JTextField dateFromField;
    private JTextField dateToField;
    private JLabel totalPaymentsLabel;

    public ManagePaymentsPanel(Admin admin, PaymentService paymentService) {
        this.admin = admin;
        this.paymentService = paymentService;
        this.db = MySQLDatabase.getInstance();
        initializeUI();
        loadPayments();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(236, 240, 241));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Payment Records");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton recordPaymentBtn = new JButton("+ Record Payment");
        recordPaymentBtn.setBackground(new Color(46, 204, 113));
        recordPaymentBtn.setForeground(Color.WHITE);
        recordPaymentBtn.setFont(new Font("Arial", Font.BOLD, 14));
        recordPaymentBtn.setFocusPainted(false);
        recordPaymentBtn.setBorderPainted(false);
        recordPaymentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        recordPaymentBtn.addActionListener(e -> showRecordPaymentDialog());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(recordPaymentBtn, BorderLayout.EAST);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel studentLabel = new JLabel("Student:");
        studentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        studentSearchField = new JTextField(15);
        studentSearchField.setPreferredSize(new Dimension(200, 30));

        JLabel dateFromLabel = new JLabel("From:");
        dateFromLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateFromField = new JTextField(10);
        dateFromField.setPreferredSize(new Dimension(120, 30));
        dateFromField.setToolTipText("Format: YYYY-MM-DD");

        JLabel dateToLabel = new JLabel("To:");
        dateToLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateToField = new JTextField(10);
        dateToField.setPreferredSize(new Dimension(120, 30));
        dateToField.setToolTipText("Format: YYYY-MM-DD");

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> loadPayments());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearFilters());

        filterPanel.add(studentLabel);
        filterPanel.add(studentSearchField);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(dateFromLabel);
        filterPanel.add(dateFromField);
        filterPanel.add(dateToLabel);
        filterPanel.add(dateToField);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(searchBtn);
        filterPanel.add(clearBtn);

        // Table Panel
        String[] columnNames = { "Payment ID", "Date", "Reg Number", "Student Name",
                "Amount", "Method", "Reference", "Purpose", "Year",
                "Semester", "Processed By" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentsTable = new JTable(tableModel);
        paymentsTable.setRowHeight(30);
        paymentsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        paymentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        paymentsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        paymentsTable.getTableHeader().setForeground(Color.WHITE);
        paymentsTable.setSelectionBackground(new Color(52, 152, 219));
        paymentsTable.setSelectionForeground(Color.WHITE);

        // Set column widths
        paymentsTable.getColumnModel().getColumn(0).setPreferredWidth(80); // ID
        paymentsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        paymentsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Reg Number
        paymentsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Student Name
        paymentsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Amount
        paymentsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Method
        paymentsTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Reference
        paymentsTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Purpose
        paymentsTable.getColumnModel().getColumn(8).setPreferredWidth(80); // Year
        paymentsTable.getColumnModel().getColumn(9).setPreferredWidth(80); // Semester
        paymentsTable.getColumnModel().getColumn(10).setPreferredWidth(150); // Processed By

        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(new Color(236, 240, 241));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        totalPaymentsLabel = new JLabel("Total Payments: UGX 0.00");
        totalPaymentsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPaymentsLabel.setForeground(new Color(46, 204, 113));

        summaryPanel.add(totalPaymentsLabel);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private void loadPayments() {
        tableModel.setRowCount(0);

        String studentFilter = studentSearchField.getText().trim();
        String dateFrom = dateFromField.getText().trim();
        String dateTo = dateToField.getText().trim();

        List<Payment> payments = paymentService.getAllPayments(
                studentFilter.isEmpty() ? null : studentFilter,
                dateFrom.isEmpty() ? null : dateFrom,
                dateTo.isEmpty() ? null : dateTo);

        double totalAmount = 0.0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        for (Payment payment : payments) {
            Object[] row = {
                    payment.getPaymentId(),
                    payment.getPaymentDate().format(dateFormatter),
                    payment.getRegistrationNumber(),
                    payment.getStudentName(),
                    String.format("%.2f", payment.getAmount()),
                    payment.getPaymentMethodDisplay(),
                    payment.getReferenceNumber(),
                    payment.getPurpose(),
                    payment.getAcademicYear(),
                    payment.getSemester(),
                    payment.getProcessedByName() != null ? payment.getProcessedByName() : "N/A"
            };
            tableModel.addRow(row);
            totalAmount += payment.getAmount();
        }

        totalPaymentsLabel.setText(String.format("Total Payments: UGX %.2f (%d records)",
                totalAmount, payments.size()));

        System.out.println("Loaded " + payments.size() + " payment records");
    }

    private void clearFilters() {
        studentSearchField.setText("");
        dateFromField.setText("");
        dateToField.setText("");
        loadPayments();
    }

    private void showRecordPaymentDialog() {
        RecordPaymentDialog dialog = new RecordPaymentDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                admin, paymentService);
        dialog.setVisible(true);

        // Refresh table after dialog closes
        if (dialog.isPaymentRecorded()) {
            loadPayments();
        }
    }
}
