# SMART CAMPUS MANAGEMENT SYSTEM (SCMS)

## Part 4: Key Features Implementation (Code Snippets)

---

## 9. KEY FEATURES IMPLEMENTATION

### 9.1 Authentication and Login Feature

#### 9.1.1 User Login with Exception Handling

**File:** `AuthenticationService.java`

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

                // Polymorphism - return appropriate subclass based on role
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

**Key OOP Concepts:**

- **Exception Handling:** Try-with-resources for automatic resource management
- **Polymorphism:** Method returns Person but actual object can be Student, Lecturer, or Admin
- **Encapsulation:** Private database connection, public interface method

---

### 9.2 Course Registration Feature

#### 9.2.1 Course Registration with Validation

**File:** `CourseService.java`

```java
public boolean registerForCourse(int studentId, int courseId,
                                String academicYear, int semester) {
    // Check if already registered
    if (isAlreadyRegistered(studentId, courseId, academicYear, semester)) {
        JOptionPane.showMessageDialog(null,
            "You are already registered for this course!",
            "Duplicate Registration", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // Check course capacity
    if (isCourseAtCapacity(courseId, academicYear, semester)) {
        JOptionPane.showMessageDialog(null,
            "This course is full!",
            "Course Full", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Perform registration
    String query = "INSERT INTO course_registrations " +
                   "(student_id, course_id, academic_year, semester) " +
                   "VALUES (?, ?, ?, ?)";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
        stmt.setInt(1, studentId);
        stmt.setInt(2, courseId);
        stmt.setString(3, academicYear);
        stmt.setInt(4, semester);

        int result = stmt.executeUpdate();

        if (result > 0) {
            JOptionPane.showMessageDialog(null,
                "Course registered successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
    } catch (SQLException e) {
        System.err.println("Error registering for course: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Registration failed: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    return false;
}

private boolean isAlreadyRegistered(int studentId, int courseId,
                                   String academicYear, int semester) {
    String query = "SELECT COUNT(*) as count FROM course_registrations " +
                   "WHERE student_id = ? AND course_id = ? " +
                   "AND academic_year = ? AND semester = ? " +
                   "AND status = 'REGISTERED'";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
        stmt.setInt(1, studentId);
        stmt.setInt(2, courseId);
        stmt.setString(3, academicYear);
        stmt.setInt(4, semester);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("count") > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}
```

**Key Programming Concepts:**

- **Validation:** Multiple checks before performing operation
- **Exception Handling:** Try-catch with user-friendly messages
- **CRUD Operations:** INSERT operation with prepared statements
- **Looping/Conditional:** If-else statements for validation logic

---

### 9.3 Grade Calculation and Upload

#### 9.3.1 Letter Grade Calculation with Conditional Logic

**File:** `GradeService.java`

```java
public String calculateLetterGrade(double totalMarks) {
    // Input validation
    if (totalMarks < 0 || totalMarks > 100) {
        throw new IllegalArgumentException("Marks must be between 0 and 100");
    }

    // Conditional logic - if-else ladder
    if (totalMarks >= 80) {
        return "A";
    } else if (totalMarks >= 75) {
        return "B+";
    } else if (totalMarks >= 70) {
        return "B";
    } else if (totalMarks >= 65) {
        return "C+";
    } else if (totalMarks >= 60) {
        return "C";
    } else if (totalMarks >= 55) {
        return "D+";
    } else if (totalMarks >= 50) {
        return "D";
    } else {
        return "F";
    }
}

public double calculateGradePoints(String letterGrade) {
    // Switch statement for grade points mapping
    switch (letterGrade) {
        case "A": return 5.0;
        case "B+": return 4.5;
        case "B": return 4.0;
        case "C+": return 3.5;
        case "C": return 3.0;
        case "D+": return 2.5;
        case "D": return 2.0;
        case "F": return 0.0;
        default: return 0.0;
    }
}
```

#### 9.3.2 Grade Upload with Exception Handling

**File:** `GradeService.java`

```java
public boolean uploadGrade(int registrationId, double courseworkMarks,
                          double examMarks, String remarks, int uploadedBy) {
    try {
        // Validate marks ranges
        if (courseworkMarks < 0 || courseworkMarks > 40) {
            throw new IllegalArgumentException(
                "Coursework marks must be between 0 and 40");
        }
        if (examMarks < 0 || examMarks > 60) {
            throw new IllegalArgumentException(
                "Exam marks must be between 0 and 60");
        }

        // Calculate total and grades
        double totalMarks = courseworkMarks + examMarks;
        String letterGrade = calculateLetterGrade(totalMarks);
        double gradePoints = calculateGradePoints(letterGrade);

        // Database operation
        String query = "INSERT INTO grades " +
                      "(registration_id, coursework_marks, exam_marks, " +
                      "total_marks, letter_grade, grade_points, remarks, uploaded_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE " +
                      "coursework_marks = ?, exam_marks = ?, total_marks = ?, " +
                      "letter_grade = ?, grade_points = ?, remarks = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            // Insert values
            stmt.setInt(1, registrationId);
            stmt.setDouble(2, courseworkMarks);
            stmt.setDouble(3, examMarks);
            stmt.setDouble(4, totalMarks);
            stmt.setString(5, letterGrade);
            stmt.setDouble(6, gradePoints);
            stmt.setString(7, remarks);
            stmt.setInt(8, uploadedBy);

            // Update values (for ON DUPLICATE KEY UPDATE)
            stmt.setDouble(9, courseworkMarks);
            stmt.setDouble(10, examMarks);
            stmt.setDouble(11, totalMarks);
            stmt.setString(12, letterGrade);
            stmt.setDouble(13, gradePoints);
            stmt.setString(14, remarks);

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

**Key Concepts Demonstrated:**

- **Exception Handling:** Try-catch-finally pattern
- **Validation:** Input range checking
- **Conditional Logic:** If-else ladder for grade calculation
- **Switch Statement:** Grade points mapping
- **CRUD Operations:** INSERT with ON DUPLICATE KEY UPDATE

---

### 9.4 Student Data Loading with Loops

#### 9.4.1 Loading Student List

**File:** `UserService.java`

```java
public List<Student> getAllStudents() {
    List<Student> students = new ArrayList<>();

    String query = "SELECT * FROM vw_student_details WHERE is_active = TRUE";

    try (PreparedStatement stmt = db.getConnection().prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        // While loop - process each row in result set
        while (rs.next()) {
            Student student = new Student(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("first_name"),
                rs.getString("last_name")
            );

            // Set additional properties
            student.setStudentId(rs.getInt("student_id"));
            student.setRegistrationNumber(rs.getString("registration_number"));
            student.setProgram(rs.getString("program"));
            student.setYearOfStudy(rs.getInt("year_of_study"));
            student.setSemester(rs.getInt("semester"));
            student.setFeeBalance(rs.getDouble("fee_balance"));
            student.setGpa(rs.getDouble("gpa"));
            student.setStatus(rs.getString("student_status"));

            students.add(student);
        }

    } catch (SQLException e) {
        System.err.println("Error loading students: " + e.getMessage());
        e.printStackTrace();
    }

    return students;
}
```

**Key Concepts:**

- **Looping:** While loop to iterate through ResultSet
- **Collections:** ArrayList to store multiple Student objects
- **Exception Handling:** Try-with-resources pattern
- **Encapsulation:** Using setters to populate object

---

### 9.5 GUI Event-Driven Programming

#### 9.5.1 Login Button Event Handler

**File:** `LoginForm.java`

```java
private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    // Input validation
    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter both username and password!",
            "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Disable button during processing
    loginButton.setEnabled(false);
    loginButton.setText("Logging in...");

    // Background processing with SwingWorker
    SwingWorker<Person, Void> worker = new SwingWorker<Person, Void>() {
        @Override
        protected Person doInBackground() throws Exception {
            // Long-running operation in background thread
            return authService.login(username, password);
        }

        @Override
        protected void done() {
            try {
                Person user = get();

                if (user != null) {
                    // Success
                    logService.logLogin(user.getUserId(), username, user.getRole());
                    SessionManager.getInstance().setCurrentUser(user);
                    openDashboard(user);
                    dispose();
                } else {
                    // Failed login
                    logService.logFailedLogin(username);
                    JOptionPane.showMessageDialog(LoginForm.this,
                        "Invalid username or password!",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LoginForm.this,
                    "Error during login: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Always restore button state
                loginButton.setEnabled(true);
                loginButton.setText("LOGIN");
            }
        }
    };

    worker.execute();
}

// Event listener attachment
loginButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        handleLogin();
    }
});
```

**Key Concepts:**

- **Event-Driven Programming:** ActionListener for button clicks
- **Asynchronous Processing:** SwingWorker for non-blocking operations
- **Exception Handling:** Finally block ensures button restoration
- **User Feedback:** Immediate UI response and status updates

---

### 9.6 Database Interface Implementation

#### 9.6.1 DatabaseOperations Interface

**File:** `DatabaseOperations.java`

```java
public interface DatabaseOperations {
    /**
     * Connect to the database
     * @return true if connection successful, false otherwise
     */
    boolean connect();

    /**
     * Get the current database connection
     * @return Connection object
     */
    Connection getConnection();

    /**
     * Execute a SELECT query
     * @param query SQL query string
     * @return ResultSet containing query results
     */
    ResultSet executeQuery(String query);

    /**
     * Execute an INSERT, UPDATE, or DELETE query
     * @param query SQL query string
     * @return true if successful, false otherwise
     */
    boolean executeUpdate(String query);

    /**
     * Close the database connection
     */
    void closeConnection();
}
```

#### 9.6.2 MySQLDatabase Implementation

**File:** `MySQLDatabase.java`

```java
public class MySQLDatabase implements DatabaseOperations {
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/scms_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection connection;
    private static MySQLDatabase instance;

    // Private constructor for Singleton pattern
    private MySQLDatabase() {}

    // Singleton getInstance method
    public static synchronized MySQLDatabase getInstance() {
        if (instance == null) {
            instance = new MySQLDatabase();
        }
        return instance;
    }

    @Override
    public boolean connect() {
        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
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

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public ResultSet executeQuery(String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Query execution failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean executeUpdate(String query) {
        try {
            Statement stmt = connection.createStatement();
            int result = stmt.executeUpdate(query);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Update execution failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**Key OOP Concepts:**

- **Interface:** Abstract contract for database operations
- **Implementation:** Concrete class implementing interface methods
- **Singleton Pattern:** Single instance of database connection
- **Encapsulation:** Private fields, public methods
- **Exception Handling:** Try-catch blocks for SQL exceptions

---

### 9.7 Inheritance Example

#### 9.7.1 Person Base Class

**File:** `Person.java`

```java
public class Person {
    // Private fields - Encapsulation
    private int userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String address;
    private String role;

    // Constructor
    public Person(int userId, String username, String email,
                  String firstName, String lastName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Public methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // ... other getters and setters ...

    @Override
    public String toString() {
        return "User: " + username + " (" + role + ")";
    }
}
```

#### 9.7.2 Student Child Class

**File:** `Student.java`

```java
public class Student extends Person {  // Inheritance
    // Additional fields specific to Student
    private int studentId;
    private String registrationNumber;
    private String program;
    private int yearOfStudy;
    private int semester;
    private Date enrollmentDate;
    private double feeBalance;
    private double gpa;
    private String status;

    // Constructor calling parent constructor
    public Student(int userId, String username, String email,
                   String firstName, String lastName) {
        super(userId, username, email, firstName, lastName);
        setRole("STUDENT");
    }

    // Student-specific methods
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    // ... other getters and setters ...

    // Method Overriding - Polymorphism
    @Override
    public String toString() {
        return "Student: " + getFullName() + " (" + registrationNumber + ")";
    }
}
```

**Key OOP Concepts:**

- **Inheritance:** Student extends Person
- **Encapsulation:** Private fields with public getters/setters
- **Polymorphism:** Overriding toString() method
- **Constructor Chaining:** super() calls parent constructor

---

### 9.8 File I/O - Export to CSV

#### 9.8.1 Export Student Data

**File:** `AdminDashboard.java` (or similar)

```java
private void exportStudentsToCSV(List<Student> students, File file)
        throws IOException {
    // Try-with-resources for automatic file closing
    try (FileWriter writer = new FileWriter(file);
         BufferedWriter bw = new BufferedWriter(writer)) {

        // Write CSV header
        bw.write("Registration Number,Name,Program,Year,Semester,GPA,Fee Balance,Status");
        bw.newLine();

        // For-each loop to iterate through students
        for (Student student : students) {
            // Build CSV row
            String row = String.format("%s,%s,%s,%d,%d,%.2f,%.2f,%s",
                student.getRegistrationNumber(),
                student.getFullName(),
                student.getProgram(),
                student.getYearOfStudy(),
                student.getSemester(),
                student.getGpa(),
                student.getFeeBalance(),
                student.getStatus()
            );

            bw.write(row);
            bw.newLine();
        }

        bw.flush();

    } catch (IOException e) {
        System.err.println("Error exporting to CSV: " + e.getMessage());
        throw e;  // Re-throw to caller
    }
}

// Event handler for export button
exportButton.addActionListener(e -> {
    try {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Student Data");
        fileChooser.setSelectedFile(new File("students.csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            List<Student> students = userService.getAllStudents();

            exportStudentsToCSV(students, fileToSave);

            JOptionPane.showMessageDialog(this,
                "Data exported successfully!",
                "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this,
            "Error exporting data: " + ex.getMessage(),
            "Export Error", JOptionPane.ERROR_MESSAGE);
    }
});
```

**Key Concepts:**

- **File I/O:** FileWriter and BufferedWriter for file operations
- **Try-with-Resources:** Automatic resource management
- **Exception Handling:** IOException handling
- **Looping:** For-each loop to iterate through collection
- **String Formatting:** String.format() for CSV formatting

---

**Next Section:** Challenges and Solutions

---

**Document Created:** November 6, 2025  
**Project:** Smart Campus Management System (SCMS)  
**Course:** BIT 2104 / BCS 2102 - Object-Oriented Programming (Java)
