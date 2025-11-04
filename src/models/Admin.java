package models;

/**
 * Admin class extending Person - Demonstrates Inheritance
 */
public class Admin extends Person {
    private int adminId;
    private String employeeNumber;
    private String department;
    private int accessLevel;

    // Constructors
    public Admin() {
        super();
    }

    public Admin(String firstName, String lastName, String phoneNumber,
            String employeeNumber, String department) {
        super(firstName, lastName, phoneNumber);
        this.employeeNumber = employeeNumber;
        this.department = department;
        this.accessLevel = 1;
    }

    // Implementing abstract methods - Polymorphism
    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Administrator Information ===");
        System.out.println("Name: " + getFullName());
        System.out.println("Employee Number: " + employeeNumber);
        System.out.println("Department: " + department);
        System.out.println("Access Level: " + accessLevel);
    }

    // Overriding toString - Polymorphism
    @Override
    public String toString() {
        return "Admin{" +
                "empNumber='" + employeeNumber + '\'' +
                ", name='" + getFullName() + '\'' +
                ", department='" + department + '\'' +
                ", accessLevel=" + accessLevel +
                '}';
    }

    // Business logic methods
    public boolean hasFullAccess() {
        return accessLevel >= 3;
    }

    public boolean canManageUsers() {
        return accessLevel >= 2;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }
}
