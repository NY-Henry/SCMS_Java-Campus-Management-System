-- Insert sample system logs for testing

-- Admin login logs
INSERT INTO system_logs (user_id, action, details, ip_address, timestamp) 
VALUES 
(1, 'LOGIN', 'User System Administrator (ADMIN) logged in successfully', '192.168.1.1', NOW() - INTERVAL 2 DAY),
(1, 'VIEW', 'Viewed: Student Report - All active students', '192.168.1.1', NOW() - INTERVAL 2 DAY),
(1, 'EXPORT', 'Report generated: Student Statistics', '192.168.1.1', NOW() - INTERVAL 2 DAY),
(1, 'LOGOUT', 'User System Administrator (ADMIN) logged out', '192.168.1.1', NOW() - INTERVAL 2 DAY);

-- Student login logs (assuming user_id 2 is a student)
INSERT INTO system_logs (user_id, action, details, ip_address, timestamp) 
VALUES 
(2, 'LOGIN', 'User John Doe (STUDENT) logged in successfully', '192.168.1.10', NOW() - INTERVAL 1 DAY),
(2, 'VIEW', 'Viewed: Personal Dashboard', '192.168.1.10', NOW() - INTERVAL 1 DAY),
(2, 'VIEW', 'Viewed: Courses - Enrolled courses', '192.168.1.10', NOW() - INTERVAL 1 DAY),
(2, 'UPDATE', 'Profile updated by John Doe', '192.168.1.10', NOW() - INTERVAL 1 DAY),
(2, 'LOGOUT', 'User John Doe (STUDENT) logged out', '192.168.1.10', NOW() - INTERVAL 1 DAY);

-- Lecturer actions (assuming user_id 3 is a lecturer)
INSERT INTO system_logs (user_id, action, details, ip_address, timestamp) 
VALUES 
(3, 'LOGIN', 'User Dr. Jane Smith (LECTURER) logged in successfully', '192.168.1.20', NOW() - INTERVAL 5 HOUR),
(3, 'CREATE', 'Announcement created: \'Important Exam Notice\' for STUDENT', '192.168.1.20', NOW() - INTERVAL 5 HOUR),
(3, 'CREATE', 'Grade submitted: A for Student XYZ in Web Technologies', '192.168.1.20', NOW() - INTERVAL 4 HOUR),
(3, 'VIEW', 'Viewed: My Courses', '192.168.1.20', NOW() - INTERVAL 3 HOUR),
(3, 'LOGOUT', 'User Dr. Jane Smith (LECTURER) logged out', '192.168.1.20', NOW() - INTERVAL 3 HOUR);

-- Admin actions today
INSERT INTO system_logs (user_id, action, details, ip_address, timestamp) 
VALUES 
(1, 'LOGIN', 'User System Administrator (ADMIN) logged in successfully', '192.168.1.1', NOW() - INTERVAL 2 HOUR),
(1, 'CREATE', 'Record created: Student - New registration STU/2024/001', '192.168.1.1', NOW() - INTERVAL 1 HOUR),
(1, 'CREATE', 'Payment processed: UGX 500000.00 from John Doe via BANK_TRANSFER', '192.168.1.1', NOW() - INTERVAL 1 HOUR),
(1, 'VIEW', 'Viewed: Financial Reports', '192.168.1.1', NOW() - INTERVAL 30 MINUTE),
(1, 'EXPORT', 'Data exported: Financial Report', '192.168.1.1', NOW() - INTERVAL 20 MINUTE);

-- Failed login attempts
INSERT INTO system_logs (user_id, action, details, ip_address, timestamp) 
VALUES 
(NULL, 'LOGIN_FAILED', 'Failed login attempt for username: wronguser', '192.168.1.50', NOW() - INTERVAL 3 HOUR),
(NULL, 'LOGIN_FAILED', 'Failed login attempt for username: admin123', '192.168.1.51', NOW() - INTERVAL 2 HOUR),
(NULL, 'LOGIN_FAILED', 'Failed login attempt for username: student', '192.168.1.52', NOW() - INTERVAL 1 HOUR);

-- More recent activities
INSERT INTO system_logs (user_id, action, details, ip_address, timestamp) 
VALUES 
(2, 'LOGIN', 'User John Doe (STUDENT) logged in successfully', '192.168.1.10', NOW() - INTERVAL 15 MINUTE),
(2, 'VIEW', 'Viewed: Announcements', '192.168.1.10', NOW() - INTERVAL 10 MINUTE),
(2, 'VIEW', 'Viewed: Grade Report', '192.168.1.10', NOW() - INTERVAL 5 MINUTE);
