package services;

import database.MySQLDatabase;
import models.Grade;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for grade management operations
 * Demonstrates file I/O operations for exporting grades
 */
public class GradeService {
    private MySQLDatabase db;

    public GradeService() {
        this.db = MySQLDatabase.getInstance();
    }

    /**
     * Upload grades for a student's course registration
     */
    public boolean uploadGrade(int registrationId, double courseworkMarks, double examMarks,
            int lecturerId, String remarks) {
        // Using try-with-resources for automatic resource management
        try {
            // Validate marks
            if (courseworkMarks < 0 || courseworkMarks > 40) {
                throw new IllegalArgumentException("Coursework marks must be between 0 and 40!");
            }
            if (examMarks < 0 || examMarks > 60) {
                throw new IllegalArgumentException("Exam marks must be between 0 and 60!");
            }

            double totalMarks = courseworkMarks + examMarks;

            // Create Grade object and calculate letter grade
            Grade grade = new Grade(registrationId, courseworkMarks, examMarks);

            // Check if grade already exists
            String checkQuery = "SELECT grade_id FROM grades WHERE registration_id = ?";
            ResultSet rs = db.executePreparedSelect(checkQuery, new Object[] { registrationId });

            boolean exists = (rs != null && rs.next());

            if (exists) {
                // Update existing grade
                String updateQuery = "UPDATE grades SET coursework_marks = ?, exam_marks = ?, " +
                        "total_marks = ?, letter_grade = ?, grade_points = ?, " +
                        "remarks = ?, uploaded_by = ?, uploaded_at = NOW() " +
                        "WHERE registration_id = ?";

                return db.executePreparedQuery(updateQuery, new Object[] {
                        courseworkMarks, examMarks, totalMarks, grade.getLetterGrade(),
                        grade.getGradePoints(), remarks, lecturerId, registrationId
                });
            } else {
                // Insert new grade
                String insertQuery = "INSERT INTO grades (registration_id, coursework_marks, exam_marks, " +
                        "total_marks, letter_grade, grade_points, remarks, uploaded_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                return db.executePreparedQuery(insertQuery, new Object[] {
                        registrationId, courseworkMarks, examMarks, totalMarks,
                        grade.getLetterGrade(), grade.getGradePoints(), remarks, lecturerId
                });
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Validation Error: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error uploading grade!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get grades for a specific student
     */
    public List<Grade> getStudentGrades(int studentId) {
        List<Grade> grades = new ArrayList<>();

        try {
            String query = "SELECT g.*, c.course_code, c.course_name, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as student_name " +
                    "FROM grades g " +
                    "JOIN course_registrations cr ON g.registration_id = cr.registration_id " +
                    "JOIN courses c ON cr.course_id = c.course_id " +
                    "JOIN students s ON cr.student_id = s.student_id " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "WHERE s.student_id = ? " +
                    "ORDER BY c.course_code";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { studentId });

            while (rs != null && rs.next()) {
                Grade grade = mapResultSetToGrade(rs);
                grades.add(grade);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching student grades!");
            e.printStackTrace();
        }

        return grades;
    }

    /**
     * Get grades for a specific course
     */
    public List<Grade> getCourseGrades(int courseId, String academicYear, int semester) {
        List<Grade> grades = new ArrayList<>();

        try {
            String query = "SELECT g.*, c.course_code, c.course_name, s.registration_number, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as student_name " +
                    "FROM grades g " +
                    "JOIN course_registrations cr ON g.registration_id = cr.registration_id " +
                    "JOIN courses c ON cr.course_id = c.course_id " +
                    "JOIN students s ON cr.student_id = s.student_id " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "WHERE cr.course_id = ? AND cr.academic_year = ? AND cr.semester = ? " +
                    "ORDER BY student_name";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { courseId, academicYear, semester });

            while (rs != null && rs.next()) {
                Grade grade = mapResultSetToGrade(rs);
                grades.add(grade);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching course grades!");
            e.printStackTrace();
        }

        return grades;
    }

    /**
     * Export student grades to CSV file
     * Demonstrates File I/O operations
     */
    public boolean exportGradesToCSV(int studentId, String studentName, String filePath) {
        BufferedWriter writer = null;

        try {
            // Get student grades
            List<Grade> grades = getStudentGrades(studentId);

            if (grades.isEmpty()) {
                System.out.println("No grades to export!");
                return false;
            }

            // Create and write to file
            writer = new BufferedWriter(new FileWriter(filePath));

            // Write header
            writer.write("NDEJJE UNIVERSITY - GRADE REPORT\n");
            writer.write("Student: " + studentName + "\n");
            writer.write("Generated: " + java.time.LocalDateTime.now() + "\n\n");
            writer.write("Course Code,Course Name,Coursework,Exam,Total,Grade,Points,Remarks\n");

            // Write grade data
            for (Grade grade : grades) {
                writer.write(String.format("%s,%s,%.2f,%.2f,%.2f,%s,%.2f,%s\n",
                        grade.getCourseCode(),
                        grade.getCourseName(),
                        grade.getCourseworkMarks(),
                        grade.getExamMarks(),
                        grade.getTotalMarks(),
                        grade.getLetterGrade(),
                        grade.getGradePoints(),
                        grade.getRemarks() != null ? grade.getRemarks() : ""));
            }

            System.out.println("Grades exported successfully to: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Error writing to file!");
            e.printStackTrace();
            return false;
        } finally {
            // Ensure file is closed - finally block always executes
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing file!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Export course grades to text file
     */
    public boolean exportCourseGradesToFile(int courseId, String courseCode, String courseName,
            String academicYear, int semester, String filePath) {
        // Using try-with-resources - automatic resource management
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            List<Grade> grades = getCourseGrades(courseId, academicYear, semester);

            if (grades.isEmpty()) {
                System.out.println("No grades to export!");
                return false;
            }

            // Write header
            writer.write("======================================\n");
            writer.write("NDEJJE UNIVERSITY - COURSE GRADE REPORT\n");
            writer.write("======================================\n\n");
            writer.write("Course: " + courseCode + " - " + courseName + "\n");
            writer.write("Academic Year: " + academicYear + " Semester: " + semester + "\n");
            writer.write("Generated: " + java.time.LocalDateTime.now() + "\n\n");
            writer.write("------------------------------------------------------------\n");
            writer.write(String.format("%-20s %-25s %10s %8s %8s %5s\n",
                    "Reg Number", "Student Name", "Coursework", "Exam", "Total", "Grade"));
            writer.write("------------------------------------------------------------\n");

            // Write grades
            double totalAverage = 0;
            for (Grade grade : grades) {
                writer.write(String.format("%-20s %-25s %10.2f %8.2f %8.2f %5s\n",
                        grade.getStudentName(), // Will contain reg number from query
                        grade.getStudentName(),
                        grade.getCourseworkMarks(),
                        grade.getExamMarks(),
                        grade.getTotalMarks(),
                        grade.getLetterGrade()));
                totalAverage += grade.getTotalMarks();
            }

            writer.write("------------------------------------------------------------\n");
            writer.write(String.format("Class Average: %.2f\n", totalAverage / grades.size()));
            writer.write("Total Students: " + grades.size() + "\n");

            System.out.println("Course grades exported successfully to: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Error exporting course grades!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calculate and update student GPA
     */
    public boolean updateStudentGPA(int studentId) {
        try {
            String query = "CALL sp_calculate_student_gpa(?)";
            return db.executePreparedQuery(query, new Object[] { studentId });

        } catch (Exception e) {
            System.err.println("Error updating GPA!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get class statistics for a course
     */
    public String getCourseStatistics(int courseId, String academicYear, int semester) {
        try {
            String query = "SELECT " +
                    "COUNT(*) as total_students, " +
                    "AVG(g.total_marks) as average, " +
                    "MAX(g.total_marks) as highest, " +
                    "MIN(g.total_marks) as lowest, " +
                    "SUM(CASE WHEN g.total_marks >= 50 THEN 1 ELSE 0 END) as passed " +
                    "FROM grades g " +
                    "JOIN course_registrations cr ON g.registration_id = cr.registration_id " +
                    "WHERE cr.course_id = ? AND cr.academic_year = ? AND cr.semester = ?";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { courseId, academicYear, semester });

            if (rs != null && rs.next()) {
                int total = rs.getInt("total_students");
                double avg = rs.getDouble("average");
                double highest = rs.getDouble("highest");
                double lowest = rs.getDouble("lowest");
                int passed = rs.getInt("passed");

                return String.format("Total: %d | Average: %.2f | Highest: %.2f | Lowest: %.2f | Passed: %d (%.1f%%)",
                        total, avg, highest, lowest, passed, (passed * 100.0 / total));
            }

        } catch (SQLException e) {
            System.err.println("Error calculating statistics!");
            e.printStackTrace();
        }

        return "No statistics available";
    }

    // Helper method
    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setGradeId(rs.getInt("grade_id"));
        grade.setRegistrationId(rs.getInt("registration_id"));
        grade.setCourseworkMarks(rs.getDouble("coursework_marks"));
        grade.setExamMarks(rs.getDouble("exam_marks"));
        grade.setTotalMarks(rs.getDouble("total_marks"));
        grade.setLetterGrade(rs.getString("letter_grade"));
        grade.setGradePoints(rs.getDouble("grade_points"));
        grade.setRemarks(rs.getString("remarks"));
        grade.setUploadedBy(rs.getInt("uploaded_by"));

        try {
            grade.setCourseCode(rs.getString("course_code"));
            grade.setCourseName(rs.getString("course_name"));
            grade.setStudentName(rs.getString("student_name"));
        } catch (SQLException e) {
            // Fields not in result set
        }

        return grade;
    }
}
