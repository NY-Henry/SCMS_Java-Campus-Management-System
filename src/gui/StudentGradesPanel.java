package gui;

import models.Grade;
import models.Student;
import services.GradeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Panel displaying student grades
 * Demonstrates file export functionality
 */
public class StudentGradesPanel extends JPanel {
    private Student student;
    private GradeService gradeService;
    private JTable gradesTable;
    private DefaultTableModel tableModel;

    public StudentGradesPanel(Student student, GradeService gradeService) {
        this.student = student;
        this.gradeService = gradeService;
        initializeUI();
        loadGrades();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Table
        String[] columns = { "Course Code", "Course Name", "Coursework", "Exam", "Total", "Grade", "Points" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setRowHeight(30);
        gradesTable.setFont(new Font("Arial", Font.PLAIN, 13));
        gradesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        gradesTable.getTableHeader().setBackground(new Color(52, 73, 94));
        gradesTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton exportButton = new JButton("Export to CSV");
        exportButton.setBackground(new Color(46, 204, 113));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.setBorderPainted(false);
        exportButton.addActionListener(e -> exportGrades());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> loadGrades());

        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(new Color(236, 240, 241));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel gpaLabel = new JLabel(String.format("Current GPA: %.2f", student.getGpa()));
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 16));

        summaryPanel.add(gpaLabel);

        // Layout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(titleLabel);
        topPanel.add(summaryPanel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadGrades() {
        tableModel.setRowCount(0);

        List<Grade> grades = gradeService.getStudentGrades(student.getStudentId());

        for (Grade grade : grades) {
            tableModel.addRow(new Object[] {
                    grade.getCourseCode(),
                    grade.getCourseName(),
                    String.format("%.2f", grade.getCourseworkMarks()),
                    String.format("%.2f", grade.getExamMarks()),
                    String.format("%.2f", grade.getTotalMarks()),
                    grade.getLetterGrade(),
                    String.format("%.2f", grade.getGradePoints())
            });
        }
    }

    private void exportGrades() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Grades Report");
        fileChooser.setSelectedFile(new File(student.getRegistrationNumber() + "_grades.csv"));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }

            boolean success = gradeService.exportGradesToCSV(
                    student.getStudentId(),
                    student.getFullName(),
                    filePath);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Grades exported successfully to:\n" + filePath,
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to export grades!",
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
