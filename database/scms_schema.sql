-- Smart Campus Management System Database Schema
-- Drop existing database if exists
DROP DATABASE IF EXISTS scms_db;
CREATE DATABASE scms_db;
USE scms_db;

-- Table: Users (Base table for authentication)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'LECTURER', 'ADMIN') NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_role (role)
);

-- Table: Persons (Personal information)
CREATE TABLE persons (
    person_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    phone_number VARCHAR(20),
    address TEXT,
    profile_picture_path VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Table: Students
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    person_id INT UNIQUE NOT NULL,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    program VARCHAR(100) NOT NULL,
    year_of_study INT NOT NULL,
    semester INT NOT NULL,
    enrollment_date DATE NOT NULL,
    fee_balance DECIMAL(10, 2) DEFAULT 0.00,
    gpa DECIMAL(3, 2) DEFAULT 0.00,
    status ENUM('ACTIVE', 'SUSPENDED', 'GRADUATED', 'WITHDRAWN') DEFAULT 'ACTIVE',
    FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE,
    INDEX idx_reg_number (registration_number),
    INDEX idx_program (program)
);

-- Table: Lecturers
CREATE TABLE lecturers (
    lecturer_id INT PRIMARY KEY AUTO_INCREMENT,
    person_id INT UNIQUE NOT NULL,
    employee_number VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    qualification VARCHAR(100),
    hire_date DATE NOT NULL,
    office_location VARCHAR(50),
    status ENUM('ACTIVE', 'ON_LEAVE', 'RETIRED') DEFAULT 'ACTIVE',
    FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE,
    INDEX idx_emp_number (employee_number),
    INDEX idx_department (department)
);

-- Table: Admins
CREATE TABLE admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    person_id INT UNIQUE NOT NULL,
    employee_number VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(100) NOT NULL,
    access_level INT DEFAULT 1,
    FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE
);

-- Table: Courses
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INT NOT NULL,
    department VARCHAR(100) NOT NULL,
    semester INT NOT NULL,
    year_level INT NOT NULL,
    max_capacity INT DEFAULT 50,
    lecturer_id INT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id) ON DELETE SET NULL,
    INDEX idx_course_code (course_code),
    INDEX idx_department (department)
);

-- Table: Course Registrations
CREATE TABLE course_registrations (
    registration_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    academic_year VARCHAR(10) NOT NULL,
    semester INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('REGISTERED', 'DROPPED', 'COMPLETED') DEFAULT 'REGISTERED',
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (student_id, course_id, academic_year, semester),
    INDEX idx_student_course (student_id, course_id)
);

-- Table: Grades
CREATE TABLE grades (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    registration_id INT UNIQUE NOT NULL,
    coursework_marks DECIMAL(5, 2) DEFAULT 0.00,
    exam_marks DECIMAL(5, 2) DEFAULT 0.00,
    total_marks DECIMAL(5, 2) DEFAULT 0.00,
    letter_grade VARCHAR(2),
    grade_points DECIMAL(3, 2),
    remarks TEXT,
    uploaded_by INT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES course_registrations(registration_id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES lecturers(lecturer_id) ON DELETE SET NULL,
    INDEX idx_registration (registration_id)
);

-- Table: Attendance
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    registration_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL,
    remarks TEXT,
    recorded_by INT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES course_registrations(registration_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES lecturers(lecturer_id) ON DELETE SET NULL,
    UNIQUE KEY unique_attendance (registration_id, attendance_date),
    INDEX idx_date (attendance_date)
);

-- Table: Announcements
CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    posted_by INT NOT NULL,
    target_audience ENUM('ALL', 'STUDENTS', 'LECTURERS', 'SPECIFIC_COURSE') NOT NULL,
    course_id INT,
    posted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (posted_by) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    INDEX idx_audience (target_audience),
    INDEX idx_posted_at (posted_at)
);

-- Table: Payments
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method ENUM('CASH', 'BANK_TRANSFER', 'MOBILE_MONEY', 'CARD') NOT NULL,
    reference_number VARCHAR(50) UNIQUE NOT NULL,
    purpose VARCHAR(100),
    academic_year VARCHAR(10) NOT NULL,
    semester INT NOT NULL,
    processed_by INT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES admins(admin_id) ON DELETE SET NULL,
    INDEX idx_student (student_id),
    INDEX idx_payment_date (payment_date)
);

-- Table: System Logs
CREATE TABLE system_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_timestamp (timestamp)
);

-- Insert Default Admin User
INSERT INTO users (username, password, role, email) 
VALUES ('admin', 'admin123', 'ADMIN', 'admin@ndejje.ac.ug');

INSERT INTO persons (user_id, first_name, last_name, date_of_birth, gender, phone_number, address) 
VALUES (1, 'System', 'Administrator', '1980-01-01', 'MALE', '+256700000000', 'Ndejje University');

INSERT INTO admins (person_id, employee_number, department, access_level) 
VALUES (1, 'EMP001', 'IT Department', 3);

-- Insert Sample Lecturers
INSERT INTO users (username, password, role, email) VALUES 
('lec001', 'password123', 'LECTURER', 'drsmith@ndejje.ac.ug'),
('lec002', 'password123', 'LECTURER', 'profkagwa@ndejje.ac.ug');

INSERT INTO persons (user_id, first_name, last_name, date_of_birth, gender, phone_number) VALUES 
(2, 'John', 'Smith', '1975-05-15', 'MALE', '+256701111111'),
(3, 'Sarah', 'Kagwa', '1980-08-20', 'FEMALE', '+256702222222');

INSERT INTO lecturers (person_id, employee_number, department, specialization, hire_date, office_location) VALUES 
(2, 'LEC001', 'Computing', 'Software Engineering', '2010-01-15', 'Block A, Room 101'),
(3, 'LEC002', 'Computing', 'Database Systems', '2012-03-20', 'Block A, Room 102');

-- Insert Sample Students
INSERT INTO users (username, password, role, email) VALUES 
('stu001', 'password123', 'STUDENT', 'jdoe@students.ndejje.ac.ug'),
('stu002', 'password123', 'STUDENT', 'mnakato@students.ndejje.ac.ug');

INSERT INTO persons (user_id, first_name, last_name, date_of_birth, gender, phone_number) VALUES 
(4, 'John', 'Doe', '2002-03-10', 'MALE', '+256703333333'),
(5, 'Mary', 'Nakato', '2001-07-25', 'FEMALE', '+256704444444');

INSERT INTO students (person_id, registration_number, program, year_of_study, semester, enrollment_date, fee_balance) VALUES 
(4, 'BIT/2023/001', 'Bachelor of Information Technology', 2, 1, '2023-08-01', 500000.00),
(5, 'BCS/2023/002', 'Bachelor of Computer Science', 2, 1, '2023-08-01', 300000.00);

-- Insert Sample Courses
INSERT INTO courses (course_code, course_name, description, credits, department, semester, year_level, lecturer_id) VALUES 
('BIT2104', 'Object Oriented Programming (Java)', 'Advanced Java programming with OOP concepts', 4, 'Computing', 1, 2, 1),
('BCS2102', 'Database Management Systems', 'Relational database design and SQL', 4, 'Computing', 1, 2, 2),
('BIT2103', 'Data Structures and Algorithms', 'Core data structures and algorithmic thinking', 4, 'Computing', 1, 2, 1),
('BCS2101', 'Web Technologies', 'HTML, CSS, JavaScript and web development', 3, 'Computing', 1, 2, 2);

-- Insert Sample Course Registrations
INSERT INTO course_registrations (student_id, course_id, academic_year, semester, status) VALUES 
(1, 1, '2025/2026', 1, 'REGISTERED'),
(1, 2, '2025/2026', 1, 'REGISTERED'),
(2, 1, '2025/2026', 1, 'REGISTERED'),
(2, 3, '2025/2026', 1, 'REGISTERED');

-- Insert Sample Grades
INSERT INTO grades (registration_id, coursework_marks, exam_marks, total_marks, letter_grade, grade_points, uploaded_by) VALUES 
(1, 28.00, 65.00, 93.00, 'A', 5.00, 1),
(3, 25.00, 60.00, 85.00, 'A', 5.00, 1);

-- Insert Sample Announcement
INSERT INTO announcements (title, content, posted_by, target_audience) VALUES 
('Welcome to Semester I 2025/2026', 'Welcome all students to the new academic semester. Classes begin on November 4th, 2025.', 1, 'ALL');

-- Create Views for Easy Data Retrieval

-- View: Complete Student Information
CREATE VIEW vw_student_details AS
SELECT 
    s.student_id, s.registration_number, s.program, s.year_of_study, s.semester,
    s.fee_balance, s.gpa, s.status as student_status,
    p.first_name, p.last_name, p.date_of_birth, p.gender, p.phone_number, p.address,
    u.username, u.email, u.is_active
FROM students s
JOIN persons p ON s.person_id = p.person_id
JOIN users u ON p.user_id = u.user_id;

-- View: Complete Lecturer Information
CREATE VIEW vw_lecturer_details AS
SELECT 
    l.lecturer_id, l.employee_number, l.department, l.specialization, l.qualification,
    l.office_location, l.status as lecturer_status,
    p.first_name, p.last_name, p.date_of_birth, p.gender, p.phone_number,
    u.username, u.email
FROM lecturers l
JOIN persons p ON l.person_id = p.person_id
JOIN users u ON p.user_id = u.user_id;

-- View: Course Registration with Details
CREATE VIEW vw_course_registrations AS
SELECT 
    cr.registration_id, cr.academic_year, cr.semester, cr.status as registration_status,
    s.student_id, s.registration_number, p.first_name, p.last_name,
    c.course_id, c.course_code, c.course_name, c.credits,
    g.total_marks, g.letter_grade, g.grade_points
FROM course_registrations cr
JOIN students s ON cr.student_id = s.student_id
JOIN persons p ON s.person_id = p.person_id
JOIN courses c ON cr.course_id = c.course_id
LEFT JOIN grades g ON cr.registration_id = g.registration_id;

-- Create Stored Procedures

-- Procedure: Calculate GPA for a student
DELIMITER //
CREATE PROCEDURE sp_calculate_student_gpa(IN p_student_id INT)
BEGIN
    DECLARE v_gpa DECIMAL(3, 2);
    
    SELECT ROUND(AVG(g.grade_points), 2) INTO v_gpa
    FROM course_registrations cr
    JOIN grades g ON cr.registration_id = g.registration_id
    WHERE cr.student_id = p_student_id AND cr.status = 'COMPLETED';
    
    UPDATE students SET gpa = IFNULL(v_gpa, 0.00) WHERE student_id = p_student_id;
END //

-- Procedure: Update Fee Balance
CREATE PROCEDURE sp_update_fee_balance(
    IN p_student_id INT,
    IN p_amount DECIMAL(10, 2)
)
BEGIN
    UPDATE students 
    SET fee_balance = fee_balance - p_amount 
    WHERE student_id = p_student_id;
END //

-- Procedure: Register Student for Course
CREATE PROCEDURE sp_register_course(
    IN p_student_id INT,
    IN p_course_id INT,
    IN p_academic_year VARCHAR(10),
    IN p_semester INT
)
BEGIN
    DECLARE v_count INT;
    DECLARE v_capacity INT;
    DECLARE v_enrolled INT;
    
    -- Check if already registered
    SELECT COUNT(*) INTO v_count
    FROM course_registrations
    WHERE student_id = p_student_id 
        AND course_id = p_course_id 
        AND academic_year = p_academic_year
        AND semester = p_semester
        AND status = 'REGISTERED';
    
    IF v_count > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Already registered for this course';
    END IF;
    
    -- Check course capacity
    SELECT max_capacity INTO v_capacity FROM courses WHERE course_id = p_course_id;
    
    SELECT COUNT(*) INTO v_enrolled
    FROM course_registrations
    WHERE course_id = p_course_id 
        AND academic_year = p_academic_year
        AND semester = p_semester
        AND status = 'REGISTERED';
    
    IF v_enrolled >= v_capacity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Course is full';
    END IF;
    
    -- Register the student
    INSERT INTO course_registrations (student_id, course_id, academic_year, semester)
    VALUES (p_student_id, p_course_id, p_academic_year, p_semester);
END //

DELIMITER ;

-- Grant privileges (adjust username as needed)
-- GRANT ALL PRIVILEGES ON scms_db.* TO 'scms_user'@'localhost' IDENTIFIED BY 'scms_password';
-- FLUSH PRIVILEGES;
