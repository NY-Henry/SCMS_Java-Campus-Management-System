package models;

import java.time.LocalDate;

/**
 * Student class extending Person - Demonstrates Inheritance
 */
public class Student extends Person {
    private int studentId;
    private String registrationNumber;
    private String program;
    private int yearOfStudy;
    private int semester;
    private LocalDate enrollmentDate;
    private double feeBalance;
    private double gpa;
    private String status;

    // Constructors
    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String phoneNumber,
            String registrationNumber, String program) {
        super(firstName, lastName, phoneNumber);
        this.registrationNumber = registrationNumber;
        this.program = program;
        this.status = "ACTIVE";
    }

    // Implementing abstract methods - Polymorphism
    @Override
    public String getRole() {
        return "STUDENT";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Student Information ===");
        System.out.println("Name: " + getFullName());
        System.out.println("Reg Number: " + registrationNumber);
        System.out.println("Program: " + program);
        System.out.println("Year: " + yearOfStudy + ", Semester: " + semester);
        System.out.println("GPA: " + gpa);
        System.out.println("Fee Balance: UGX " + feeBalance);
    }

    // Overriding toString - Polymorphism
    @Override
    public String toString() {
        return "Student{" +
                "regNumber='" + registrationNumber + '\'' +
                ", name='" + getFullName() + '\'' +
                ", program='" + program + '\'' +
                ", gpa=" + gpa +
                '}';
    }

    // Business logic methods
    public boolean canRegisterCourse() {
        return "ACTIVE".equals(status) && feeBalance <= 1000000; // Allow registration if balance < 1M
    }

    public String getAcademicStanding() {
        if (gpa >= 4.5)
            return "First Class";
        if (gpa >= 3.6)
            return "Second Upper";
        if (gpa >= 3.0)
            return "Second Lower";
        if (gpa >= 2.5)
            return "Pass";
        return "Retake";
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public double getFeeBalance() {
        return feeBalance;
    }

    public void setFeeBalance(double feeBalance) {
        this.feeBalance = feeBalance;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
