package services;

import database.MySQLDatabase;
import models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Authentication Service for user login and registration
 */
public class AuthenticationService {
    private MySQLDatabase db;

    public AuthenticationService() {
        this.db = MySQLDatabase.getInstance();
    }

    /**
     * Authenticate user with username and password
     * 
     * @return Person object (Student, Lecturer, or Admin) if successful, null
     *         otherwise
     */
    public Person login(String username, String password) {
        try {
            // Query to fetch user data
            String query = "SELECT u.user_id, u.username, u.password, u.role, u.email, " +
                    "p.person_id, p.first_name, p.last_name, p.date_of_birth, " +
                    "p.gender, p.phone_number, p.address " +
                    "FROM users u " +
                    "JOIN persons p ON u.user_id = p.user_id " +
                    "WHERE u.username = ? AND u.is_active = TRUE";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { username });

            if (rs != null && rs.next()) {
                String storedPassword = rs.getString("password");

                // Validate password (in production, use hashing!)
                if (password.equals(storedPassword)) {
                    String role = rs.getString("role");

                    // Update last login
                    updateLastLogin(rs.getInt("user_id"));

                    // Log the login
                    logAction(rs.getInt("user_id"), "LOGIN", "User logged in successfully");

                    // Create appropriate Person object based on role
                    switch (role) {
                        case "STUDENT":
                            return fetchStudentDetails(rs.getInt("person_id"));
                        case "LECTURER":
                            return fetchLecturerDetails(rs.getInt("person_id"));
                        case "ADMIN":
                            return fetchAdminDetails(rs.getInt("person_id"));
                        default:
                            return null;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error during login!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Register a new student
     */
    public boolean registerStudent(String username, String password, String email,
            String firstName, String lastName, String phone,
            String registrationNumber, String program,
            int yearOfStudy, int semester) {
        try {
            // Check if username already exists
            String checkQuery = "SELECT user_id FROM users WHERE username = ?";
            ResultSet rs = db.executePreparedSelect(checkQuery, new Object[] { username });
            if (rs != null && rs.next()) {
                System.err.println("Username already exists!");
                return false;
            }

            // Check if registration number already exists
            checkQuery = "SELECT student_id FROM students WHERE registration_number = ?";
            rs = db.executePreparedSelect(checkQuery, new Object[] { registrationNumber });
            if (rs != null && rs.next()) {
                System.err.println("Registration number already exists!");
                return false;
            }

            // Insert into users table
            String insertUser = "INSERT INTO users (username, password, role, email) VALUES (?, ?, 'STUDENT', ?)";
            int userId = db.executeInsertAndGetId(insertUser, new Object[] { username, password, email });

            if (userId <= 0) {
                return false;
            }

            // Insert into persons table
            String insertPerson = "INSERT INTO persons (user_id, first_name, last_name, phone_number) VALUES (?, ?, ?, ?)";
            int personId = db.executeInsertAndGetId(insertPerson, new Object[] { userId, firstName, lastName, phone });

            if (personId <= 0) {
                return false;
            }

            // Insert into students table
            String insertStudent = "INSERT INTO students (person_id, registration_number, program, year_of_study, semester, enrollment_date, status) "
                    +
                    "VALUES (?, ?, ?, ?, ?, CURDATE(), 'ACTIVE')";
            boolean success = db.executePreparedQuery(insertStudent,
                    new Object[] { personId, registrationNumber, program, yearOfStudy, semester });

            if (success) {
                logAction(userId, "REGISTRATION", "New student registered: " + registrationNumber);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("Error during student registration!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetch complete student details
     */
    private Student fetchStudentDetails(int personId) throws SQLException {
        String query = "SELECT s.*, p.*, u.user_id, u.username, u.email " +
                "FROM students s " +
                "JOIN persons p ON s.person_id = p.person_id " +
                "JOIN users u ON p.user_id = u.user_id " +
                "WHERE s.person_id = ?";

        ResultSet rs = db.executePreparedSelect(query, new Object[] { personId });

        if (rs != null && rs.next()) {
            Student student = new Student();

            // Set Person fields
            student.setPersonId(rs.getInt("person_id"));
            student.setUserId(rs.getInt("user_id"));
            student.setFirstName(rs.getString("first_name"));
            student.setLastName(rs.getString("last_name"));
            student.setPhoneNumber(rs.getString("phone_number"));
            student.setAddress(rs.getString("address"));
            student.setGender(rs.getString("gender"));

            if (rs.getDate("date_of_birth") != null) {
                student.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
            }

            // Set Student fields
            student.setStudentId(rs.getInt("student_id"));
            student.setRegistrationNumber(rs.getString("registration_number"));
            student.setProgram(rs.getString("program"));
            student.setYearOfStudy(rs.getInt("year_of_study"));
            student.setSemester(rs.getInt("semester"));
            student.setFeeBalance(rs.getDouble("fee_balance"));
            student.setGpa(rs.getDouble("gpa"));
            student.setStatus(rs.getString("status"));

            if (rs.getDate("enrollment_date") != null) {
                student.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
            }

            return student;
        }

        return null;
    }

    /**
     * Fetch complete lecturer details
     */
    private Lecturer fetchLecturerDetails(int personId) throws SQLException {
        String query = "SELECT l.*, p.*, u.user_id, u.username, u.email " +
                "FROM lecturers l " +
                "JOIN persons p ON l.person_id = p.person_id " +
                "JOIN users u ON p.user_id = u.user_id " +
                "WHERE l.person_id = ?";

        ResultSet rs = db.executePreparedSelect(query, new Object[] { personId });

        if (rs != null && rs.next()) {
            Lecturer lecturer = new Lecturer();

            // Set Person fields
            lecturer.setPersonId(rs.getInt("person_id"));
            lecturer.setUserId(rs.getInt("user_id"));
            lecturer.setFirstName(rs.getString("first_name"));
            lecturer.setLastName(rs.getString("last_name"));
            lecturer.setPhoneNumber(rs.getString("phone_number"));
            lecturer.setAddress(rs.getString("address"));
            lecturer.setGender(rs.getString("gender"));

            if (rs.getDate("date_of_birth") != null) {
                lecturer.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
            }

            // Set Lecturer fields
            lecturer.setLecturerId(rs.getInt("lecturer_id"));
            lecturer.setEmployeeNumber(rs.getString("employee_number"));
            lecturer.setDepartment(rs.getString("department"));
            lecturer.setSpecialization(rs.getString("specialization"));
            lecturer.setQualification(rs.getString("qualification"));
            lecturer.setOfficeLocation(rs.getString("office_location"));
            lecturer.setStatus(rs.getString("status"));

            if (rs.getDate("hire_date") != null) {
                lecturer.setHireDate(rs.getDate("hire_date").toLocalDate());
            }

            return lecturer;
        }

        return null;
    }

    /**
     * Fetch complete admin details
     */
    private Admin fetchAdminDetails(int personId) throws SQLException {
        String query = "SELECT a.*, p.*, u.user_id, u.username, u.email " +
                "FROM admins a " +
                "JOIN persons p ON a.person_id = p.person_id " +
                "JOIN users u ON p.user_id = u.user_id " +
                "WHERE a.person_id = ?";

        ResultSet rs = db.executePreparedSelect(query, new Object[] { personId });

        if (rs != null && rs.next()) {
            Admin admin = new Admin();

            // Set Person fields
            admin.setPersonId(rs.getInt("person_id"));
            admin.setUserId(rs.getInt("user_id"));
            admin.setFirstName(rs.getString("first_name"));
            admin.setLastName(rs.getString("last_name"));
            admin.setPhoneNumber(rs.getString("phone_number"));
            admin.setAddress(rs.getString("address"));
            admin.setGender(rs.getString("gender"));

            if (rs.getDate("date_of_birth") != null) {
                admin.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
            }

            // Set Admin fields
            admin.setAdminId(rs.getInt("admin_id"));
            admin.setEmployeeNumber(rs.getString("employee_number"));
            admin.setDepartment(rs.getString("department"));
            admin.setAccessLevel(rs.getInt("access_level"));

            return admin;
        }

        return null;
    }

    /**
     * Update last login timestamp
     */
    private void updateLastLogin(int userId) {
        String query = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        db.executePreparedQuery(query, new Object[] { userId });
    }

    /**
     * Log user action
     */
    private void logAction(int userId, String action, String details) {
        String query = "INSERT INTO system_logs (user_id, action, details) VALUES (?, ?, ?)";
        db.executePreparedQuery(query, new Object[] { userId, action, details });
    }
}
