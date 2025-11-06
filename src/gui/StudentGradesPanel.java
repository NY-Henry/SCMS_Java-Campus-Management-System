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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with title, GPA, and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Left section - Title and GPA
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setOpaque(false);

        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(45, 45, 45));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel gpaLabel = new JLabel(String.format("Current GPA: %.2f", student.getGpa()));
        gpaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gpaLabel.setForeground(new Color(120, 120, 120));
        gpaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gpaLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        leftSection.add(titleLabel);
        leftSection.add(gpaLabel);

        // Action buttons panel (top right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton exportButton = createMinimalButton("Export CSV", new Color(70, 130, 180));
        exportButton.addActionListener(e -> exportGrades());

        JButton refreshButton = createMinimalButton("Refresh", new Color(100, 100, 110));
        refreshButton.addActionListener(e -> loadGrades());

        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);

        topPanel.add(leftSection, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = { "Course Code", "Course Name", "Coursework", "Exam", "Total", "Grade", "Points" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setRowHeight(40);
        gradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gradesTable.setShowVerticalLines(false);
        gradesTable.setGridColor(new Color(240, 240, 245));
        gradesTable.setSelectionBackground(new Color(245, 247, 250));
        gradesTable.setSelectionForeground(new Color(45, 45, 45));

        // Minimalist table header
        gradesTable.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gradesTable.getTableHeader().setBackground(Color.WHITE);
        gradesTable.getTableHeader().setForeground(new Color(120, 120, 120));
        gradesTable.getTableHeader().setOpaque(true);
        gradesTable.getTableHeader().setReorderingAllowed(false);
        gradesTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        gradesTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)));

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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
