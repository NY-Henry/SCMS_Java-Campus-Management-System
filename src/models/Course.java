package models;

/**
 * Course class representing a course in the system
 */
public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private int credits;
    private String department;
    private int semester;
    private int yearLevel;
    private int maxCapacity;
    private int lecturerId;
    private String lecturerName;
    private boolean isActive;

    // Constructors
    public Course() {
    }

    public Course(String courseCode, String courseName, int credits, String department) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
        this.isActive = true;
        this.maxCapacity = 50;
    }

    // Business logic methods
    public boolean hasCapacity(int currentEnrollment) {
        return currentEnrollment < maxCapacity;
    }

    public String getFullCourseInfo() {
        return courseCode + " - " + courseName + " (" + credits + " credits)";
    }

    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
