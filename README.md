# Smart Campus Management System (SCMS)

## Ndejje University - OOP Final Project

### Project Overview

The Smart Campus Management System is a comprehensive desktop-based Java application designed to digitalize campus operations at Ndejje University. The system integrates key academic and administrative functions for Students, Lecturers, and Administrators.

### System Features

#### Student Features:

- ✅ Register and drop courses
- ✅ View grades and GPA
- ✅ Check fee balances
- ✅ Update profile details
- ✅ View announcements
- ✅ Export grades to CSV

#### Lecturer Features:

- ✅ View assigned courses
- ✅ Upload and update student grades
- ✅ View class lists
- ✅ Post announcements
- ✅ View student attendance

#### Administrator Features:

- ✅ Manage users (students, lecturers)
- ✅ Manage courses
- ✅ Generate summary reports
- ✅ Monitor payments
- ✅ View system logs

### Technology Stack

- **Language:** Java (JDK 8 or higher)
- **GUI Framework:** Swing
- **Database:** MySQL 8.0+
- **Database Connectivity:** JDBC
- **Build System:** Manual compilation or IDE (Eclipse/IntelliJ IDEA/VS Code)

### OOP Concepts Demonstrated

1. **Inheritance**

   - `Person` (abstract base class)
   - `Student`, `Lecturer`, `Admin` extend `Person`

2. **Encapsulation**

   - Private fields with public getters/setters
   - Data hiding in all model classes

3. **Polymorphism**

   - Method overriding (`getRole()`, `displayInfo()`, `toString()`)
   - Interface implementation (`DatabaseOperations`)

4. **Abstraction**

   - Abstract `Person` class with abstract methods
   - `DatabaseOperations` interface

5. **Exception Handling**
   - Try-catch-finally blocks
   - Try-with-resources for file operations
   - Custom validation with exceptions

### Project Structure

```
OOP Final/
├── database/
│   └── scms_schema.sql          # Database schema and sample data
├── lib/                          # MySQL JDBC Driver (add manually)
│   └── mysql-connector-java-8.0.x.jar
├── src/
│   ├── Main.java                 # Application entry point
│   ├── database/
│   │   ├── DatabaseOperations.java    # Interface
│   │   └── MySQLDatabase.java         # Implementation
│   ├── models/
│   │   ├── Person.java           # Abstract base class
│   │   ├── Student.java
│   │   ├── Lecturer.java
│   │   ├── Admin.java
│   │   ├── Course.java
│   │   ├── CourseRegistration.java
│   │   ├── Grade.java
│   │   └── Announcement.java
│   ├── services/
│   │   ├── AuthenticationService.java
│   │   ├── CourseService.java
│   │   └── GradeService.java
│   ├── utils/
│   │   └── SessionManager.java
│   └── gui/
│       ├── LoginForm.java
│       ├── StudentRegistrationForm.java
│       ├── StudentDashboard.java
│       ├── LecturerDashboard.java
│       ├── AdminDashboard.java
│       └── [Other GUI panels...]
└── README.md
```

### Setup Instructions

#### 1. Database Setup

**Step 1:** Install MySQL Server

- Download and install MySQL 8.0+ from https://dev.mysql.com/downloads/
- Set root password during installation

**Step 2:** Create Database

```bash
# Open MySQL Command Line Client or MySQL Workbench
mysql -u root -p

# Run the schema file
source /path/to/database/scms_schema.sql

# Or in MySQL Workbench:
# File > Open SQL Script > Select scms_schema.sql > Execute
```

**Step 3:** Verify Database

```sql
USE scms_db;
SHOW TABLES;
SELECT * FROM users;
```

#### 2. MySQL JDBC Driver Setup

**Download JDBC Driver:**

- Visit: https://dev.mysql.com/downloads/connector/j/
- Download MySQL Connector/J (Platform Independent)
- Extract the JAR file

**Add to Project:**

- Copy `mysql-connector-java-8.0.x.jar` to the `lib/` folder
- Add to classpath (see compilation section)

#### 3. Database Configuration

Edit `src/database/MySQLDatabase.java` if needed:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/scms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String DB_USER = "root";      // Your MySQL username
private static final String DB_PASSWORD = "";      // Your MySQL password
```

#### 4. Compilation and Execution

**Option A: Using Command Line**

```bash
# Navigate to project directory
cd "c:\Users\HP\Desktop\OOP Final Papeer\OOP Final"

# Compile all Java files
javac -d bin -cp "lib/*" src/**/*.java src/*.java

# Run the application
java -cp "bin;lib/*" Main
```

**Option B: Using IDE (Recommended)**

**IntelliJ IDEA:**

1. Open IntelliJ IDEA
2. File > Open > Select project folder
3. Right-click `lib` folder > Add as Library
4. Right-click `Main.java` > Run 'Main.main()'

**Eclipse:**

1. File > Open Projects from File System
2. Select project folder
3. Right-click project > Build Path > Add External Archives
4. Select `mysql-connector-java-8.0.x.jar`
5. Right-click `Main.java` > Run As > Java Application

**VS Code:**

1. Install Java Extension Pack
2. Open project folder
3. Add JDBC driver to classpath in `.vscode/settings.json`:

```json
{
  "java.project.referencedLibraries": ["lib/**/*.jar"]
}
```

4. Run > Run Without Debugging (or press F5)

### Default Login Credentials

#### Administrator:

- Username: `admin`
- Password: `admin123`

#### Lecturer:

- Username: `lec001`
- Password: `password123`

#### Student:

- Username: `stu001`
- Password: `password123`

**Note:** Change these passwords in production!

### Usage Guide

#### For Students:

1. **First-time users:** Click "Create Student Account" on login screen
2. **Login** with your credentials
3. **Dashboard:** View your GPA, fee balance, and academic standing
4. **Register Courses:** Navigate to "Register Courses" and select courses
5. **View Grades:** Check your grades and export to CSV
6. **Drop Courses:** Go to "My Courses" and drop unwanted courses

#### For Lecturers:

1. **Login** with your credentials
2. **View Courses:** See all courses assigned to you
3. **Upload Grades:** Select course, student, and enter marks
4. **View Class Lists:** See all students enrolled in your courses
5. **Post Announcements:** Communicate with students

#### For Administrators:

1. **Login** with admin credentials
2. **Manage Users:** View and manage students and lecturers
3. **Manage Courses:** Add new courses and view existing ones
4. **Generate Reports:** Access system reports and statistics
5. **Monitor System:** View logs and payment records

### File I/O Features

The system demonstrates file operations:

- **Export Grades to CSV:** Students can export their grades
- **Export Class Lists:** Lecturers can export student lists
- Files are saved in user-selected locations

### Database Schema Highlights

**Main Tables:**

- `users` - Authentication and user roles
- `persons` - Personal information
- `students` - Student-specific data
- `lecturers` - Lecturer-specific data
- `admins` - Administrator data
- `courses` - Course catalog
- `course_registrations` - Student enrollments
- `grades` - Student grades
- `announcements` - System announcements
- `payments` - Fee payment records
- `system_logs` - Activity logging

**Views:**

- `vw_student_details` - Complete student information
- `vw_lecturer_details` - Complete lecturer information
- `vw_course_registrations` - Enrollment details with grades

**Stored Procedures:**

- `sp_calculate_student_gpa` - Calculate and update GPA
- `sp_update_fee_balance` - Update student fee balance
- `sp_register_course` - Register student with validation

### Exception Handling Examples

**1. Database Connection:**

```java
try {
    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
} catch (SQLException e) {
    System.err.println("Failed to connect: " + e.getMessage());
}
```

**2. File Operations:**

```java
try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
    writer.write(data);
} catch (IOException e) {
    System.err.println("Error writing file: " + e.getMessage());
}
```

**3. Input Validation:**

```java
try {
    if (coursework < 0 || coursework > 40) {
        throw new IllegalArgumentException("Invalid marks!");
    }
} catch (IllegalArgumentException e) {
    JOptionPane.showMessageDialog(this, e.getMessage());
}
```

### Event-Driven Programming

The GUI uses event listeners:

```java
loginButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        handleLogin();
    }
});
```

### Troubleshooting

**Problem:** Database connection fails

- **Solution:** Check MySQL is running, verify credentials in `MySQLDatabase.java`

**Problem:** JDBC Driver not found

- **Solution:** Ensure `mysql-connector-java-8.0.x.jar` is in `lib/` and added to classpath

**Problem:** GUI not displaying properly

- **Solution:** Ensure JDK 8+ is installed, update graphics drivers

**Problem:** Slow performance

- **Solution:** Increase JVM heap size: `java -Xmx512m -cp "bin;lib/*" Main`

### Future Enhancements

- [ ] Attendance tracking system
- [ ] Email notifications
- [ ] Mobile application
- [ ] Biometric authentication
- [ ] Real-time chat system
- [ ] Advanced reporting with charts
- [ ] Payment gateway integration

### Project Deliverables

✅ **Question 1: OOP Design (25 Marks)**

- UML class diagram (create using draw.io or similar)
- Java source files with inheritance, encapsulation, polymorphism
- Abstraction explanation

✅ **Question 2: Application Logic (25 Marks)**

- Course registration with validation
- Exception handling throughout
- Grade calculation with letter grades

✅ **Question 3: File I/O & GUI (25 Marks)**

- CSV export functionality
- Complete GUI with JavaFX/Swing
- Event-driven programming

✅ **Question 4: Database Integration (25 Marks)**

- DatabaseOperations interface
- MySQLDatabase implementation
- CRUD operations
- Functional prototype

### Credits

**Project:** Smart Campus Management System
**Institution:** Ndejje University
**Department:** Department of Computing
**Course:** Object Oriented Programming (Java)
**Academic Year:** 2025/2026 Semester I

### License

This project is developed for educational purposes as part of the OOP course requirements.

---

**For questions or support, contact your course instructor or TA.**

**Good luck with your presentation and defense! 🎓**
