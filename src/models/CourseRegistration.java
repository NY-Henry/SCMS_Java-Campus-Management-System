package models;

import java.time.LocalDateTime;

/**
 * CourseRegistration class representing student course enrollments
 */
public class CourseRegistration {
    private int registrationId;
    private int studentId;
    private int courseId;
    private String academicYear;
    private int semester;
    private LocalDateTime registrationDate;
    private String status; // REGISTERED, DROPPED, COMPLETED

    // Additional fields for display
    private String studentName;
    private String registrationNumber;
    private String courseCode;
    private String courseName;
    private int credits;

    // Constructors
    public CourseRegistration() {
    }

    public CourseRegistration(int studentId, int courseId, String academicYear, int semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.status = "REGISTERED";
        this.registrationDate = LocalDateTime.now();
    }

    // Business logic methods
    public boolean isActive() {
        return "REGISTERED".equals(status);
    }

    public boolean canBeDropped() {
        return "REGISTERED".equals(status);
    }

    // Getters and Setters
    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
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

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "student=" + studentName +
                ", course=" + courseCode +
                ", status=" + status +
                '}';
    }
}
