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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title, balance info, and refresh button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Left section - Title and balance info
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setOpaque(false);

        JLabel titleLabel = new JLabel("Fee Balance");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Balance info rows
        JPanel infoRows = new JPanel();
        infoRows.setLayout(new BoxLayout(infoRows, BoxLayout.Y_AXIS));
        infoRows.setOpaque(false);
        infoRows.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel balanceRow = createInfoRow("Current Balance",
                String.format("UGX %.2f", student.getFeeBalance()),
                student.getFeeBalance() > 0 ? new Color(220, 80, 80) : new Color(70, 130, 180));

        JPanel totalPaidRow = createInfoRow("Total Paid", "UGX 0.00", new Color(70, 130, 180));
        // Get the value label for later updates
        totalPaidLabel = (JLabel) ((JPanel) totalPaidRow.getComponent(1)).getComponent(0);

        infoRows.add(balanceRow);
        infoRows.add(Box.createRigidArea(new Dimension(0, 10)));
        infoRows.add(totalPaidRow);

        leftSection.add(titleLabel);
        leftSection.add(infoRows);

        // Right section - Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);

        JButton refreshButton = createMinimalButton("Refresh", new Color(100, 100, 110));
        refreshButton.addActionListener(e -> loadPaymentHistory());

        buttonPanel.add(refreshButton);

        topPanel.add(leftSection, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Payment History Section
        JPanel historySection = new JPanel(new BorderLayout());
        historySection.setOpaque(false);
        historySection.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel historyLabel = new JLabel("Payment History");
        historyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        historyLabel.setForeground(new Color(45, 45, 45));
        historyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        String[] columnNames = { "Date", "Amount", "Method", "Reference", "Purpose", "Year", "Semester" };
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
        paymentsTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Date
        paymentsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Amount
        paymentsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Method
        paymentsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Reference
        paymentsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Purpose
        paymentsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Year
        paymentsTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Semester

        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        historySection.add(historyLabel, BorderLayout.NORTH);
        historySection.add(scrollPane, BorderLayout.CENTER);

        // Main content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(historySection, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createInfoRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(500, 25));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(new Color(120, 120, 120));

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setOpaque(false);
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueText.setForeground(valueColor);
        valuePanel.add(valueText);

        row.add(labelText, BorderLayout.WEST);
        row.add(valuePanel, BorderLayout.EAST);

        return row;
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

    }
}
