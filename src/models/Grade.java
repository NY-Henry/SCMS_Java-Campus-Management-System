package models;

import java.time.LocalDateTime;

/**
 * Grade class representing student grades
 */
public class Grade {
    private int gradeId;
    private int registrationId;
    private double courseworkMarks;
    private double examMarks;
    private double totalMarks;
    private String letterGrade;
    private double gradePoints;
    private String remarks;
    private int uploadedBy;
    private LocalDateTime uploadedAt;

    // Additional display fields
    private String studentName;
    private String courseCode;
    private String courseName;

    // Constructors
    public Grade() {
    }

    public Grade(int registrationId, double courseworkMarks, double examMarks) {
        this.registrationId = registrationId;
        this.courseworkMarks = courseworkMarks;
        this.examMarks = examMarks;
        this.totalMarks = courseworkMarks + examMarks;
        calculateLetterGrade();
    }

    // Business logic methods
    public void calculateLetterGrade() {
        if (totalMarks >= 90) {
            letterGrade = "A";
            gradePoints = 5.0;
        } else if (totalMarks >= 80) {
            letterGrade = "B";
            gradePoints = 4.0;
        } else if (totalMarks >= 70) {
            letterGrade = "C";
            gradePoints = 3.0;
        } else if (totalMarks >= 60) {
            letterGrade = "D";
            gradePoints = 2.0;
        } else if (totalMarks >= 50) {
            letterGrade = "E";
            gradePoints = 1.0;
        } else {
            letterGrade = "F";
            gradePoints = 0.0;
        }
    }

    public boolean isPassing() {
        return totalMarks >= 50;
    }

    public String getPerformanceLevel() {
        if (totalMarks >= 90)
            return "Excellent";
        if (totalMarks >= 80)
            return "Very Good";
        if (totalMarks >= 70)
            return "Good";
        if (totalMarks >= 60)
            return "Satisfactory";
        if (totalMarks >= 50)
            return "Pass";
        return "Fail";
    }

    // Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public double getCourseworkMarks() {
        return courseworkMarks;
    }

    public void setCourseworkMarks(double courseworkMarks) {
        this.courseworkMarks = courseworkMarks;
        this.totalMarks = courseworkMarks + examMarks;
        calculateLetterGrade();
    }

    public double getExamMarks() {
        return examMarks;
    }

    public void setExamMarks(double examMarks) {
        this.examMarks = examMarks;
        this.totalMarks = courseworkMarks + examMarks;
        calculateLetterGrade();
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
        calculateLetterGrade();
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public double getGradePoints() {
        return gradePoints;
    }

    public void setGradePoints(double gradePoints) {
        this.gradePoints = gradePoints;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(int uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "course=" + courseCode +
                ", total=" + totalMarks +
                ", grade=" + letterGrade +
                '}';
    }
}
