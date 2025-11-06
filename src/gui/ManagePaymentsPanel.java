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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title and action buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("Payment Records");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));

        // Action buttons panel (top right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton recordPaymentBtn = createMinimalButton("+ Record Payment", new Color(70, 130, 180));
        recordPaymentBtn.addActionListener(e -> showRecordPaymentDialog());

        JButton refreshButton = createMinimalButton("Refresh", new Color(100, 100, 110));
        refreshButton.addActionListener(e -> loadPayments());

        buttonPanel.add(recordPaymentBtn);
        buttonPanel.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Search Panel (simplified)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel searchLabel = new JLabel("Search Student:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchLabel.setForeground(new Color(100, 100, 110));

        studentSearchField = new JTextField(25);
        studentSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 225), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        studentSearchField.addActionListener(e -> loadPayments());

        JButton searchBtn = createMinimalButton("Search", new Color(100, 100, 110));
        searchBtn.addActionListener(e -> loadPayments());

        JButton clearBtn = createMinimalButton("Clear", new Color(180, 180, 185));
        clearBtn.addActionListener(e -> clearFilters());

        searchPanel.add(searchLabel);
        searchPanel.add(studentSearchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        // Header Panel combining title and search
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        // Table setup
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
        paymentsTable.setRowHeight(40);
        paymentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentsTable.setShowVerticalLines(false);
        paymentsTable.setGridColor(new Color(240, 240, 245));
        paymentsTable.setSelectionBackground(new Color(245, 247, 250));
        paymentsTable.setSelectionForeground(new Color(45, 45, 45));

        // Minimalist table header
        paymentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paymentsTable.getTableHeader().setBackground(Color.WHITE);
        paymentsTable.getTableHeader().setForeground(new Color(120, 120, 120));
        paymentsTable.getTableHeader().setOpaque(true);
        paymentsTable.getTableHeader().setReorderingAllowed(false);
        paymentsTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        paymentsTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)));

        // Set column widths
        paymentsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        paymentsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        paymentsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        paymentsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        paymentsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        paymentsTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        paymentsTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        paymentsTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        paymentsTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        paymentsTable.getColumnModel().getColumn(9).setPreferredWidth(80);
        paymentsTable.getColumnModel().getColumn(10).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        totalPaymentsLabel = new JLabel("Total Payments: UGX 0.00");
        totalPaymentsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalPaymentsLabel.setForeground(new Color(70, 130, 180));

        summaryPanel.add(totalPaymentsLabel);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
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

        // Hover effect
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

    private void loadPayments() {
        tableModel.setRowCount(0);

        String studentFilter = studentSearchField.getText().trim();

        List<Payment> payments = paymentService.getAllPayments(
                studentFilter.isEmpty() ? null : studentFilter,
                null,
                null);

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

    }

    private void clearFilters() {
        studentSearchField.setText("");
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
