package models;

import java.time.LocalDate;

/**
 * Lecturer class extending Person - Demonstrates Inheritance
 */
public class Lecturer extends Person {
    private int lecturerId;
    private String employeeNumber;
    private String department;
    private String specialization;
    private String qualification;
    private LocalDate hireDate;
    private String officeLocation;
    private String status;

    // Constructors
    public Lecturer() {
        super();
    }

    public Lecturer(String firstName, String lastName, String phoneNumber,
            String employeeNumber, String department) {
        super(firstName, lastName, phoneNumber);
        this.employeeNumber = employeeNumber;
        this.department = department;
        this.status = "ACTIVE";
    }

    // Implementing abstract methods - Polymorphism
    @Override
    public String getRole() {
        return "LECTURER";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Lecturer Information ===");
        System.out.println("Name: " + getFullName());
        System.out.println("Employee Number: " + employeeNumber);
        System.out.println("Department: " + department);
        System.out.println("Specialization: " + specialization);
        System.out.println("Office: " + officeLocation);
    }

    // Overriding toString - Polymorphism
    @Override
    public String toString() {
        return "Lecturer{" +
                "empNumber='" + employeeNumber + '\'' +
                ", name='" + getFullName() + '\'' +
                ", department='" + department + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }

    // Business logic methods
    public boolean canUploadGrades() {
        return "ACTIVE".equals(status);
    }

    // Getters and Setters
    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
