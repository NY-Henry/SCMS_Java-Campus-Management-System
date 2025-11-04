package services;

import database.MySQLDatabase;
import models.Course;
import models.CourseRegistration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for course management and registration operations
 * Demonstrates exception handling and business logic
 */
public class CourseService {
    private MySQLDatabase db;

    public CourseService() {
        this.db = MySQLDatabase.getInstance();
    }

    /**
     * Register a student for a course with validation
     * Demonstrates exception handling with try-catch-finally
     */
    public boolean registerCourse(int studentId, int courseId, String academicYear, int semester) {
        try {
            // Validate course exists and is active
            Course course = getCourseById(courseId);
            if (course == null || !course.isActive()) {
                throw new IllegalArgumentException("Course not found or inactive!");
            }

            // Check for duplicate registration
            if (isAlreadyRegistered(studentId, courseId, academicYear, semester)) {
                throw new IllegalStateException("Already registered for this course!");
            }

            // Check course capacity
            int currentEnrollment = getCurrentEnrollment(courseId, academicYear, semester);
            if (currentEnrollment >= course.getMaxCapacity()) {
                throw new IllegalStateException("Course is full! Maximum capacity reached.");
            }

            // Register the student
            String query = "INSERT INTO course_registrations (student_id, course_id, academic_year, semester, status) "
                    +
                    "VALUES (?, ?, ?, ?, 'REGISTERED')";

            boolean success = db.executePreparedQuery(query,
                    new Object[] { studentId, courseId, academicYear, semester });

            if (success) {
                System.out.println("Successfully registered for course: " + course.getCourseCode());
            }

            return success;

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Registration Error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during course registration!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Drop a course registration
     */
    public boolean dropCourse(int registrationId, int studentId) {
        try {
            // Verify the registration belongs to the student
            String checkQuery = "SELECT registration_id FROM course_registrations " +
                    "WHERE registration_id = ? AND student_id = ? AND status = 'REGISTERED'";

            ResultSet rs = db.executePreparedSelect(checkQuery, new Object[] { registrationId, studentId });

            if (rs == null || !rs.next()) {
                throw new IllegalArgumentException("Invalid registration or already dropped!");
            }

            // Update status to DROPPED
            String updateQuery = "UPDATE course_registrations SET status = 'DROPPED' WHERE registration_id = ?";
            boolean success = db.executePreparedQuery(updateQuery, new Object[] { registrationId });

            if (success) {
                System.out.println("Course dropped successfully!");
            }

            return success;

        } catch (IllegalArgumentException e) {
            System.err.println("Drop Error: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error dropping course!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all available courses for a specific year and semester
     */
    public List<Course> getAvailableCourses(int yearLevel, int semester) {
        List<Course> courses = new ArrayList<>();

        try {
            String query = "SELECT c.*, CONCAT(p.first_name, ' ', p.last_name) as lecturer_name " +
                    "FROM courses c " +
                    "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                    "LEFT JOIN persons p ON l.person_id = p.person_id " +
                    "WHERE c.year_level = ? AND c.semester = ? AND c.is_active = TRUE " +
                    "ORDER BY c.course_code";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { yearLevel, semester });

            while (rs != null && rs.next()) {
                Course course = mapResultSetToCourse(rs);
                courses.add(course);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching available courses!");
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Get all courses (for admin)
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();

        try {
            String query = "SELECT c.*, CONCAT(p.first_name, ' ', p.last_name) as lecturer_name " +
                    "FROM courses c " +
                    "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                    "LEFT JOIN persons p ON l.person_id = p.person_id " +
                    "ORDER BY c.department, c.course_code";

            ResultSet rs = db.fetchData(query);

            while (rs != null && rs.next()) {
                Course course = mapResultSetToCourse(rs);
                courses.add(course);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all courses!");
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Get student's registered courses
     */
    public List<CourseRegistration> getStudentRegistrations(int studentId, String academicYear, int semester) {
        List<CourseRegistration> registrations = new ArrayList<>();

        try {
            String query = "SELECT cr.*, c.course_code, c.course_name, c.credits, " +
                    "g.total_marks, g.letter_grade, g.grade_points " +
                    "FROM course_registrations cr " +
                    "JOIN courses c ON cr.course_id = c.course_id " +
                    "LEFT JOIN grades g ON cr.registration_id = g.registration_id " +
                    "WHERE cr.student_id = ? AND cr.academic_year = ? AND cr.semester = ? " +
                    "ORDER BY c.course_code";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { studentId, academicYear, semester });

            while (rs != null && rs.next()) {
                CourseRegistration reg = new CourseRegistration();
                reg.setRegistrationId(rs.getInt("registration_id"));
                reg.setStudentId(rs.getInt("student_id"));
                reg.setCourseId(rs.getInt("course_id"));
                reg.setAcademicYear(rs.getString("academic_year"));
                reg.setSemester(rs.getInt("semester"));
                reg.setStatus(rs.getString("status"));
                reg.setCourseCode(rs.getString("course_code"));
                reg.setCourseName(rs.getString("course_name"));
                reg.setCredits(rs.getInt("credits"));

                registrations.add(reg);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching student registrations!");
            e.printStackTrace();
        }

        return registrations;
    }

    /**
     * Get courses taught by a lecturer
     */
    public List<Course> getLecturerCourses(int lecturerId) {
        List<Course> courses = new ArrayList<>();

        try {
            String query = "SELECT * FROM courses WHERE lecturer_id = ? AND is_active = TRUE ORDER BY course_code";
            ResultSet rs = db.executePreparedSelect(query, new Object[] { lecturerId });

            while (rs != null && rs.next()) {
                Course course = mapResultSetToCourse(rs);
                courses.add(course);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching lecturer courses!");
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Get students enrolled in a course
     */
    public List<CourseRegistration> getCourseEnrollments(int courseId, String academicYear, int semester) {
        List<CourseRegistration> enrollments = new ArrayList<>();

        try {
            String query = "SELECT cr.*, s.registration_number, " +
                    "CONCAT(p.first_name, ' ', p.last_name) as student_name " +
                    "FROM course_registrations cr " +
                    "JOIN students s ON cr.student_id = s.student_id " +
                    "JOIN persons p ON s.person_id = p.person_id " +
                    "WHERE cr.course_id = ? AND cr.academic_year = ? AND cr.semester = ? " +
                    "AND cr.status = 'REGISTERED' " +
                    "ORDER BY student_name";

            ResultSet rs = db.executePreparedSelect(query, new Object[] { courseId, academicYear, semester });

            while (rs != null && rs.next()) {
                CourseRegistration enrollment = new CourseRegistration();
                enrollment.setRegistrationId(rs.getInt("registration_id"));
                enrollment.setStudentId(rs.getInt("student_id"));
                enrollment.setCourseId(rs.getInt("course_id"));
                enrollment.setStudentName(rs.getString("student_name"));
                enrollment.setRegistrationNumber(rs.getString("registration_number"));
                enrollment.setStatus(rs.getString("status"));

                enrollments.add(enrollment);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching course enrollments!");
            e.printStackTrace();
        }

        return enrollments;
    }

    /**
     * Add a new course
     */
    public boolean addCourse(Course course) {
        try {
            String query = "INSERT INTO courses (course_code, course_name, description, credits, " +
                    "department, semester, year_level, max_capacity, lecturer_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            return db.executePreparedQuery(query, new Object[] {
                    course.getCourseCode(), course.getCourseName(), course.getDescription(),
                    course.getCredits(), course.getDepartment(), course.getSemester(),
                    course.getYearLevel(), course.getMaxCapacity(),
                    course.getLecturerId() > 0 ? course.getLecturerId() : null
            });

        } catch (Exception e) {
            System.err.println("Error adding course!");
            e.printStackTrace();
            return false;
        }
    }

    // Helper methods

    private Course getCourseById(int courseId) throws SQLException {
        String query = "SELECT * FROM courses WHERE course_id = ?";
        ResultSet rs = db.executePreparedSelect(query, new Object[] { courseId });

        if (rs != null && rs.next()) {
            return mapResultSetToCourse(rs);
        }
        return null;
    }

    private boolean isAlreadyRegistered(int studentId, int courseId, String academicYear, int semester)
            throws SQLException {
        String query = "SELECT COUNT(*) as count FROM course_registrations " +
                "WHERE student_id = ? AND course_id = ? AND academic_year = ? " +
                "AND semester = ? AND status = 'REGISTERED'";

        ResultSet rs = db.executePreparedSelect(query, new Object[] { studentId, courseId, academicYear, semester });

        if (rs != null && rs.next()) {
            return rs.getInt("count") > 0;
        }
        return false;
    }

    private int getCurrentEnrollment(int courseId, String academicYear, int semester) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM course_registrations " +
                "WHERE course_id = ? AND academic_year = ? AND semester = ? AND status = 'REGISTERED'";

        ResultSet rs = db.executePreparedSelect(query, new Object[] { courseId, academicYear, semester });

        if (rs != null && rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setDescription(rs.getString("description"));
        course.setCredits(rs.getInt("credits"));
        course.setDepartment(rs.getString("department"));
        course.setSemester(rs.getInt("semester"));
        course.setYearLevel(rs.getInt("year_level"));
        course.setMaxCapacity(rs.getInt("max_capacity"));
        course.setActive(rs.getBoolean("is_active"));

        int lecturerId = rs.getInt("lecturer_id");
        course.setLecturerId(lecturerId);

        try {
            String lecturerName = rs.getString("lecturer_name");
            course.setLecturerName(lecturerName);
        } catch (SQLException e) {
            // Lecturer name not in result set
        }

        return course;
    }
}
