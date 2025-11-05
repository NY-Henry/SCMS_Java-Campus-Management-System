package gui;

import models.Payment;
import models.Student;
import services.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Student Fee Balance Panel - Enhanced view with payment history
 */
public class StudentFeeBalancePanel extends JPanel {
    private Student student;
    private PaymentService paymentService;
    private JTable paymentsTable;
    private DefaultTableModel tableModel;
    private JLabel totalPaidLabel;

    public StudentFeeBalancePanel(Student student) {
        this.student = student;
        this.paymentService = new PaymentService();
        initializeUI();
        loadPaymentHistory();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("Fee Balance & Payment History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Balance Card Panel
        JPanel balancePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        balancePanel.setBackground(new Color(236, 240, 241));
        balancePanel.setMaximumSize(new Dimension(900, 150));
        balancePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Current Balance Card
        JPanel currentBalanceCard = createInfoCard(
                "Current Balance",
                String.format("UGX %.2f", student.getFeeBalance()),
                student.getFeeBalance() > 0 ? new Color(231, 76, 60) : new Color(46, 204, 113));

        // Total Paid Card
        JPanel totalPaidCard = createInfoCard(
                "Total Paid",
                "UGX 0.00",
                new Color(46, 204, 113));
        // Get the valuePanel (component 2) and then the label inside it
        totalPaidLabel = (JLabel) ((JPanel) totalPaidCard.getComponent(2)).getComponent(0);

        balancePanel.add(currentBalanceCard);
        balancePanel.add(totalPaidCard);

        // Payment History Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(236, 240, 241));

        JLabel historyLabel = new JLabel("Payment History");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        historyLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columnNames = { "Date", "Amount", "Method", "Reference", "Purpose", "Year", "Semester" };
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
        paymentsTable.getTableHeader().setForeground(Color.BLACK);
        paymentsTable.setSelectionBackground(new Color(52, 152, 219));
        paymentsTable.setSelectionForeground(Color.WHITE);

        // Set column widths
        paymentsTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Date
        paymentsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Amount
        paymentsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Method
        paymentsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Reference
        paymentsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Purpose
        paymentsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Year
        paymentsTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Semester

        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        tablePanel.add(historyLabel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Main layout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(236, 240, 241));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        balancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleLabel);
        topPanel.add(balancePanel);

        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createInfoCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel valuePanel = new JPanel();
        valuePanel.setBackground(Color.WHITE);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(valueColor);
        valuePanel.add(valueLabel);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(valuePanel);

        return card;
    }

    private void loadPaymentHistory() {
        tableModel.setRowCount(0);

        List<Payment> payments = paymentService.getStudentPayments(student.getStudentId());
        double totalPaid = paymentService.getTotalPaymentsByStudent(student.getStudentId());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        for (Payment payment : payments) {
            Object[] row = {
                    payment.getPaymentDate().format(dateFormatter),
                    String.format("%.2f", payment.getAmount()),
                    payment.getPaymentMethodDisplay(),
                    payment.getReferenceNumber(),
                    payment.getPurpose(),
                    payment.getAcademicYear(),
                    payment.getSemester()
            };
            tableModel.addRow(row);
        }

        // Update total paid label
        totalPaidLabel.setText(String.format("UGX %.2f", totalPaid));

        if (payments.isEmpty()) {
            // Add empty row message
            Object[] emptyRow = { "No payment records found", "", "", "", "", "", "" };
            tableModel.addRow(emptyRow);
        }

        System.out.println("Loaded " + payments.size() + " payment records for student " +
                student.getRegistrationNumber());
    }
}
