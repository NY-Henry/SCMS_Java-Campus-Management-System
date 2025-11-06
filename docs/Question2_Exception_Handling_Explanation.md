# Question 2: Exception Handling and Control Structures Explanation

## Smart Campus Management System (SCMS)
### Exception Handling, Conditional Logic, and Loops

---

## 1. EXCEPTION HANDLING IN SCMS

### Overview
Exception handling is a critical aspect of robust software development. Our SCMS implements comprehensive exception handling using Java's try-catch-finally blocks and try-with-resources statements to manage errors gracefully and ensure system stability.

---

## 2. EXCEPTION HANDLING STRATEGIES IMPLEMENTED

### 2.1 Database Operations Exception Handling

#### Location: `MySQLDatabase.java`

**Try-Catch-Finally Pattern:**
```java
public boolean connect() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        System.out.println("Database connected successfully!");
        return true;
    } catch (ClassNotFoundException e) {
        System.err.println("MySQL JDBC Driver not found!");
        e.printStackTrace();
        return false;
    } catch (SQLException e) {
        System.err.println("Database connection failed!");
        e.printStackTrace();
        return false;
    }
}
```

**Exceptions Handled:**
- `ClassNotFoundException`: Thrown when JDBC driver class is not found
- `SQLException`: Thrown when database connection fails

**Error Recovery:**
- Returns boolean to indicate success/failure
- Logs error details to console
- Prevents application crash
- Allows graceful degradation

---

### 2.2 Try-With-Resources for Auto-Closing

#### Location: `CourseService.java` - Registration Feature

**Automatic Resource Management:**
```java
public boolean registerForCourse(int studentId, int courseId, String academicYear, int semester) {
    String query = "INSERT INTO course_registrations (student_id, course_id, academic_year, semester) " +
                   "VALUES (?, ?, ?, ?)";
    
    try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
        stmt.setInt(1, studentId);
        stmt.setInt(2, courseId);
        stmt.setString(3, academicYear);
        stmt.setInt(4, semester);
        
        int result = stmt.executeUpdate();
        return result > 0;
        
    } catch (SQLException e) {
        System.err.println("Error registering for course: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
    // PreparedStatement automatically closed here, even if exception occurs
}
```

**Benefits of Try-With-Resources:**
- Automatic closure of `PreparedStatement` resource
- Guaranteed cleanup even if exception occurs
- Prevents resource leaks
- Cleaner, more readable code
- No need for explicit finally block

---

### 2.3 Authentication Service Exception Handling

#### Location: `AuthenticationService.java`

**Multi-Level Exception Handling:**
```java
public Person login(String username, String password) {
    String query = "SELECT u.*, p.* FROM users u " +
                   "JOIN persons p ON u.user_id = p.user_id " +
                   "WHERE u.username = ? AND u.password = ? AND u.is_active = TRUE";
    
    try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
        stmt.setString(1, username);
        stmt.setString(2, password);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String role = rs.getString("role");
                
                // Load appropriate user type based on role
                switch (role) {
                    case "STUDENT":
                        return loadStudentDetails(rs);
                    case "LECTURER":
                        return loadLecturerDetails(rs);
                    case "ADMIN":
                        return loadAdminDetails(rs);
                    default:
                        return null;
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Login error: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}
```

**Nested Try-With-Resources:**
- Outer try manages `PreparedStatement`
- Inner try manages `ResultSet`
- Both resources automatically closed in reverse order
- Exception in either block handled gracefully

---

### 2.4 Input Validation with Exception Handling

#### Location: `StudentRegistrationForm.java`

**Try-Catch with Custom Exception Messages:**
```java
private void handleRegistration() {
    try {
        // Get form data
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String regNumber = regNumberField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation with IllegalArgumentException
        if (firstName.isEmpty() || lastName.isEmpty() || regNumber.isEmpty() ||
            phone.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("All fields are required!");
        }
        
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match!");
        }
        
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters!");
        }
        
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format!");
        }
        
        // Perform registration
        boolean success = authService.registerStudent(...);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Registration successful!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            throw new Exception("Registration failed! Username may already exist.");
        }
        
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this,
            e.getMessage(),
            "Validation Error", JOptionPane.WARNING_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            e.getMessage(),
            "Registration Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        registerButton.setEnabled(true);
        registerButton.setText("Register");
    }
}
```

**Exception Types Handled:**
- `IllegalArgumentException`: For validation errors
- `Exception`: For general registration errors

**Finally Block Usage:**
- Ensures button is re-enabled regardless of success or failure
- Resets button text to original state
- Guarantees UI consistency

---

### 2.5 Grade Calculation Exception Handling

#### Location: `GradeService.java`

**Safe Numeric Operations:**
```java
public String calculateLetterGrade(double totalMarks) {
    try {
        if (totalMarks < 0 || totalMarks > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        }
        
        if (totalMarks >= 80) return "A";
        else if (totalMarks >= 75) return "B+";
        else if (totalMarks >= 70) return "B";
        else if (totalMarks >= 65) return "C+";
        else if (totalMarks >= 60) return "C";
        else if (totalMarks >= 55) return "D+";
        else if (totalMarks >= 50) return "D";
        else return "F";
        
    } catch (IllegalArgumentException e) {
        System.err.println("Invalid marks: " + e.getMessage());
        return "F";
    }
}

public boolean uploadGrade(int registrationId, double courseworkMarks, double examMarks, 
                          String remarks, int uploadedBy) {
    try {
        // Validate marks
        if (courseworkMarks < 0 || courseworkMarks > 40) {
            throw new IllegalArgumentException("Coursework marks must be between 0 and 40");
        }
        if (examMarks < 0 || examMarks > 60) {
            throw new IllegalArgumentException("Exam marks must be between 0 and 60");
        }
        
        double totalMarks = courseworkMarks + examMarks;
        String letterGrade = calculateLetterGrade(totalMarks);
        double gradePoints = calculateGradePoints(letterGrade);
        
        // Database operation with try-with-resources
        String query = "INSERT INTO grades (registration_id, coursework_marks, exam_marks, " +
                      "total_marks, letter_grade, grade_points, remarks, uploaded_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, registrationId);
            stmt.setDouble(2, courseworkMarks);
            stmt.setDouble(3, examMarks);
            stmt.setDouble(4, totalMarks);
            stmt.setString(5, letterGrade);
            stmt.setDouble(6, gradePoints);
            stmt.setString(7, remarks);
            stmt.setInt(8, uploadedBy);
            
            return stmt.executeUpdate() > 0;
        }
        
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), 
            "Validation Error", JOptionPane.WARNING_MESSAGE);
        return false;
    } catch (SQLException e) {
        System.err.println("Error uploading grade: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
```

---

## 3. CONDITIONAL STRUCTURES IMPLEMENTED

### 3.1 Grade Calculation with If-Else Ladder

```java
public String calculateLetterGrade(double totalMarks) {
    if (totalMarks >= 80) return "A";
    else if (totalMarks >= 75) return "B+";
    else if (totalMarks >= 70) return "B";
    else if (totalMarks >= 65) return "C+";
    else if (totalMarks >= 60) return "C";
    else if (totalMarks >= 55) return "D+";
    else if (totalMarks >= 50) return "D";
    else return "F";
}
```

### 3.2 Role-Based Dashboard Selection (Switch Statement)

```java
public Person login(String username, String password) {
    // ... query execution ...
    
    String role = rs.getString("role");
    switch (role) {
        case "STUDENT":
            return loadStudentDetails(rs);
        case "LECTURER":
            return loadLecturerDetails(rs);
        case "ADMIN":
            return loadAdminDetails(rs);
        default:
            return null;
    }
}
```

### 3.3 Course Registration Validation

```java
public boolean canRegisterForCourse(int studentId, int courseId) {
    // Check if already registered
    if (isAlreadyRegistered(studentId, courseId)) {
        return false;
    }
    
    // Check course capacity
    if (isCourseAtCapacity(courseId)) {
        return false;
    }
    
    // Check prerequisites
    if (!hasPrerequisites(studentId, courseId)) {
        return false;
    }
    
    return true;
}
```

---

## 4. LOOPING STRUCTURES IMPLEMENTED

### 4.1 For Loop - Loading Course List

```java
public void loadAvailableCourses() {
    List<Course> courses = courseService.getAvailableCourses(yearOfStudy, semester);
    
    for (Course course : courses) {
        tableModel.addRow(new Object[]{
            course.getCourseCode(),
            course.getCourseName(),
            course.getCredits(),
            course.getLecturerName(),
            "Register"
        });
    }
}
```

### 4.2 While Loop - Processing ResultSet

```java
public List<Student> getAllStudents() {
    List<Student> students = new ArrayList<>();
    String query = "SELECT * FROM vw_student_details WHERE is_active = TRUE";
    
    try (PreparedStatement stmt = db.getConnection().prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            Student student = new Student(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("first_name"),
                rs.getString("last_name")
            );
            student.setStudentId(rs.getInt("student_id"));
            student.setRegistrationNumber(rs.getString("registration_number"));
            // ... set other fields ...
            
            students.add(student);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return students;
}
```

### 4.3 Enhanced For Loop - Displaying Announcements

```java
private void loadAnnouncements() {
    announcementsContainer.removeAll();
    List<Announcement> announcements = announcementService.getActiveAnnouncements(role, userId);
    
    for (Announcement announcement : announcements) {
        JPanel card = createAnnouncementCard(announcement);
        announcementsContainer.add(card);
        announcementsContainer.add(Box.createVerticalStrut(15));
    }
    
    announcementsContainer.revalidate();
    announcementsContainer.repaint();
}
```

### 4.4 Do-While Loop - Retry Logic

```java
public boolean connectWithRetry(int maxAttempts) {
    int attempts = 0;
    boolean connected = false;
    
    do {
        attempts++;
        connected = connect();
        
        if (!connected && attempts < maxAttempts) {
            System.out.println("Retrying connection... Attempt " + attempts);
            try {
                Thread.sleep(2000); // Wait 2 seconds before retry
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    } while (!connected && attempts < maxAttempts);
    
    return connected;
}
```

---

## 5. EXCEPTION HANDLING BEST PRACTICES APPLIED

### 5.1 Specific Exception Catching
✅ Catch specific exceptions before general ones
✅ Different handling for different exception types

### 5.2 Resource Management
✅ Use try-with-resources for auto-closing
✅ Prevent resource leaks
✅ Guarantee cleanup operations

### 5.3 User-Friendly Error Messages
✅ Display meaningful error messages to users
✅ Log technical details for developers
✅ Don't expose system internals to users

### 5.4 Graceful Degradation
✅ System continues functioning after errors
✅ Fallback mechanisms in place
✅ No abrupt application crashes

### 5.5 Finally Block Usage
✅ Cleanup operations in finally blocks
✅ UI state restoration
✅ Resource deallocation

---

## 6. SUMMARY

The Smart Campus Management System demonstrates comprehensive exception handling throughout its architecture:

### Exception Handling Coverage:
- ✅ Database connection failures
- ✅ SQL execution errors
- ✅ Input validation errors
- ✅ Authentication failures
- ✅ File I/O operations
- ✅ Resource management

### Control Structures:
- ✅ If-else for conditional logic
- ✅ Switch for role-based routing
- ✅ For loops for collection iteration
- ✅ While loops for ResultSet processing
- ✅ Do-while for retry mechanisms

### Result:
A robust, production-ready application that handles errors gracefully, provides clear feedback to users, and maintains system stability even when unexpected situations occur.

---

**Document Created:** November 6, 2025  
**Project:** Smart Campus Management System (SCMS)  
**Course:** BIT 2104 / BCS 2102 - Object-Oriented Programming (Java)  
**Question:** 2 - Exception Handling and Control Structures
