# SMART CAMPUS MANAGEMENT SYSTEM (SCMS)

## Part 3: Class Diagram and Architecture Overview

---

## 4. SYSTEM ARCHITECTURE

### 4.1 Architecture Overview

The Smart Campus Management System follows a layered architecture pattern, separating concerns into distinct layers:

```
┌─────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                      │
│              (GUI - Java Swing Components)               │
│  LoginForm, StudentDashboard, LecturerDashboard, etc.   │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                   SERVICE LAYER                          │
│              (Business Logic & Validation)               │
│  AuthenticationService, CourseService, GradeService, etc.│
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                    MODEL LAYER                           │
│                  (Domain Objects)                        │
│      Person, Student, Lecturer, Course, Grade, etc.     │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                 DATA ACCESS LAYER                        │
│            (Database Operations - JDBC)                  │
│    DatabaseOperations Interface, MySQLDatabase Impl     │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                  DATABASE LAYER                          │
│                  (MySQL Database)                        │
│         Tables, Views, Stored Procedures, etc.          │
└─────────────────────────────────────────────────────────┘
```

### 4.2 Layer Responsibilities

#### 4.2.1 Presentation Layer (GUI)

**Purpose:** User interface and user interaction handling

**Components:**

- Login and Registration Forms
- Dashboard Panels (Student, Lecturer, Admin)
- Feature-specific Panels (Courses, Grades, Announcements, etc.)

**Responsibilities:**

- Display data to users
- Capture user input
- Handle UI events (button clicks, selections)
- Validate input format
- Provide visual feedback

**Key Classes:**

- `LoginForm.java`
- `StudentRegistrationForm.java`
- `StudentDashboard.java`
- `LecturerDashboard.java`
- `AdminDashboard.java`
- Various panel classes (CourseRegistrationPanel, GradesPanel, etc.)

#### 4.2.2 Service Layer (Business Logic)

**Purpose:** Business logic, validation, and coordination

**Components:**

- Authentication Service
- Course Management Service
- Grade Management Service
- Announcement Service
- User Management Service
- Log Service

**Responsibilities:**

- Implement business rules
- Validate business logic
- Coordinate between GUI and data layers
- Handle complex operations
- Manage transactions

**Key Classes:**

- `AuthenticationService.java`
- `CourseService.java`
- `GradeService.java`
- `AnnouncementService.java`
- `UserService.java`
- `LogService.java`

#### 4.2.3 Model Layer (Domain Objects)

**Purpose:** Represent business entities

**Components:**

- User entities (Person, Student, Lecturer, Admin)
- Academic entities (Course, CourseRegistration, Grade)
- Communication entities (Announcement)

**Responsibilities:**

- Encapsulate business data
- Provide data access methods
- Maintain object state
- Implement domain logic

**Key Classes:**

- `Person.java` (abstract base)
- `Student.java`
- `Lecturer.java`
- `Admin.java`
- `Course.java`
- `CourseRegistration.java`
- `Grade.java`
- `Announcement.java`

#### 4.2.4 Data Access Layer (Database Operations)

**Purpose:** Database connectivity and operations

**Components:**

- Database connection management
- Query execution
- Result set processing
- Transaction handling

**Responsibilities:**

- Establish database connections
- Execute SQL queries
- Handle database exceptions
- Manage connection pool
- Implement CRUD operations

**Key Classes:**

- `DatabaseOperations.java` (interface)
- `MySQLDatabase.java` (implementation)

#### 4.2.5 Database Layer

**Purpose:** Data persistence

**Components:**

- Tables for data storage
- Views for complex queries
- Stored procedures for business logic
- Triggers for data integrity

**Database Objects:**

- Tables: users, persons, students, lecturers, admins, courses, grades, etc.
- Views: vw_student_details, vw_lecturer_details, vw_course_registrations
- Stored Procedures: sp_calculate_student_gpa, sp_register_course, etc.

---

## 5. CLASS DIAGRAM

### 5.1 Core Class Structure

#### 5.1.1 Person Hierarchy (Inheritance)

```
                    ┌──────────────────┐
                    │  <<abstract>>    │
                    │     Person       │
                    ├──────────────────┤
                    │ - userId         │
                    │ - username       │
                    │ - email          │
                    │ - firstName      │
                    │ - lastName       │
                    │ - dateOfBirth    │
                    │ - gender         │
                    │ - phoneNumber    │
                    │ - address        │
                    │ - role           │
                    ├──────────────────┤
                    │ + getFullName()  │
                    │ + getUserId()    │
                    │ + getUsername()  │
                    │ + getters/setters│
                    └────────┬─────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
            ↓                ↓                ↓
    ┌──────────────┐ ┌──────────────┐ ┌─────────────┐
    │   Student    │ │   Lecturer   │ │    Admin    │
    ├──────────────┤ ├──────────────┤ ├─────────────┤
    │ - studentId  │ │ - lecturerId │ │ - adminId   │
    │ - regNumber  │ │ - empNumber  │ │ - empNumber │
    │ - program    │ │ - department │ │ - department│
    │ - year       │ │ - specializ. │ │ - access    │
    │ - semester   │ │ - qualif.    │ │   Level     │
    │ - feeBalance │ │ - hireDate   │ ├─────────────┤
    │ - gpa        │ │ - office     │ │ + getAdminId│
    │ - status     │ │ - status     │ │ + getters/  │
    ├──────────────┤ ├──────────────┤ │   setters   │
    │ + getStudentId│ │ + getLecturerId│ └───────────┘
    │ + getRegNum  │ │ + getEmpNum  │
    │ + getProgram │ │ + getDept    │
    │ + getters/   │ │ + getters/   │
    │   setters    │ │   setters    │
    └──────────────┘ └──────────────┘
```

### 5.1.2 Academic Entities

```
┌──────────────────────────┐
│        Course            │
├──────────────────────────┤
│ - courseId               │
│ - courseCode             │
│ - courseName             │
│ - description            │
│ - credits                │
│ - department             │
│ - semester               │
│ - yearLevel              │
│ - maxCapacity            │
│ - lecturerId             │
│ - lecturerName           │
│ - isActive               │
├──────────────────────────┤
│ + getCourseId()          │
│ + getCourseCode()        │
│ + getCourseName()        │
│ + getters/setters        │
└──────────┬───────────────┘
           │
           │ is registered in
           │
           ↓
┌──────────────────────────┐
│   CourseRegistration     │
├──────────────────────────┤
│ - registrationId         │
│ - studentId              │
│ - courseId               │
│ - academicYear           │
│ - semester               │
│ - registrationDate       │
│ - status                 │
├──────────────────────────┤
│ + getRegistrationId()    │
│ + getStudentId()         │
│ + getCourseId()          │
│ + getters/setters        │
└──────────┬───────────────┘
           │
           │ has
           │
           ↓
┌──────────────────────────┐
│         Grade            │
├──────────────────────────┤
│ - gradeId                │
│ - registrationId         │
│ - courseworkMarks        │
│ - examMarks              │
│ - totalMarks             │
│ - letterGrade            │
│ - gradePoints            │
│ - remarks                │
│ - uploadedBy             │
│ - uploadedAt             │
├──────────────────────────┤
│ + calculateLetterGrade() │
│ + calculateGradePoints() │
│ + getters/setters        │
└──────────────────────────┘
```

### 5.1.3 Service Layer Classes

```
┌─────────────────────────────────────┐
│    AuthenticationService            │
├─────────────────────────────────────┤
│ - db: MySQLDatabase                 │
├─────────────────────────────────────┤
│ + login(username, password): Person │
│ + registerStudent(...): boolean     │
│ + logout(userId): void              │
│ - loadStudentDetails(): Student     │
│ - loadLecturerDetails(): Lecturer   │
│ - loadAdminDetails(): Admin         │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│         CourseService               │
├─────────────────────────────────────┤
│ - db: MySQLDatabase                 │
├─────────────────────────────────────┤
│ + getAllCourses(): List<Course>     │
│ + getCourseById(id): Course         │
│ + getAvailableCourses(): List       │
│ + registerForCourse(...): boolean   │
│ + dropCourse(...): boolean          │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│          GradeService               │
├─────────────────────────────────────┤
│ - db: MySQLDatabase                 │
├─────────────────────────────────────┤
│ + getGradesByStudent(id): List      │
│ + uploadGrade(...): boolean         │
│ + calculateLetterGrade(marks): Str  │
│ + calculateGradePoints(grade): dbl  │
│ + calculateGPA(studentId): double   │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│     AnnouncementService             │
├─────────────────────────────────────┤
│ - db: MySQLDatabase                 │
├─────────────────────────────────────┤
│ + getActiveAnnouncements(): List    │
│ + postAnnouncement(...): boolean    │
│ + deleteAnnouncement(id): boolean   │
│ + getAnnouncementsByLecturer(): List│
└─────────────────────────────────────┘
```

### 5.1.4 Database Interface and Implementation

```
┌─────────────────────────────────────┐
│    <<interface>>                    │
│    DatabaseOperations               │
├─────────────────────────────────────┤
│ + connect(): boolean                │
│ + getConnection(): Connection       │
│ + executeQuery(sql): ResultSet      │
│ + executeUpdate(sql): boolean       │
│ + closeConnection(): void           │
└─────────────────┬───────────────────┘
                  │
                  │ implements
                  │
                  ↓
┌─────────────────────────────────────┐
│       MySQLDatabase                 │
├─────────────────────────────────────┤
│ - DB_URL: String                    │
│ - DB_USER: String                   │
│ - DB_PASSWORD: String               │
│ - connection: Connection            │
│ - instance: MySQLDatabase (static)  │
├─────────────────────────────────────┤
│ + getInstance(): MySQLDatabase      │
│ + connect(): boolean                │
│ + getConnection(): Connection       │
│ + executeQuery(sql): ResultSet      │
│ + executeUpdate(sql): boolean       │
│ + closeConnection(): void           │
└─────────────────────────────────────┘
```

---

## 6. DESIGN PATTERNS IMPLEMENTED

### 6.1 Singleton Pattern

**Used in:** MySQLDatabase class

**Purpose:** Ensure only one database connection instance exists

**Implementation:**

```java
public class MySQLDatabase implements DatabaseOperations {
    private static MySQLDatabase instance;

    private MySQLDatabase() {
        // Private constructor
    }

    public static synchronized MySQLDatabase getInstance() {
        if (instance == null) {
            instance = new MySQLDatabase();
        }
        return instance;
    }
}
```

**Benefits:**

- Single connection point to database
- Prevents multiple connection overhead
- Thread-safe implementation
- Resource efficiency

### 6.2 MVC (Model-View-Controller) Pattern

**Separation of Concerns:**

**Model (M):**

- Domain objects (Person, Student, Course, etc.)
- Represents data and business logic
- Independent of UI

**View (V):**

- GUI components (JFrame, JPanel, JButton, etc.)
- Displays data to users
- Handles user interaction

**Controller (C):**

- Service classes (AuthenticationService, CourseService, etc.)
- Mediates between Model and View
- Handles business logic and validation

**Benefits:**

- Clear separation of concerns
- Easy to maintain and test
- Parallel development possible
- Reusable components

### 6.3 Factory Pattern (Implicit)

**Used in:** Dashboard creation based on user role

**Implementation:**

```java
private void openDashboard(Person user) {
    if (user instanceof Student) {
        new StudentDashboard((Student) user).setVisible(true);
    } else if (user instanceof Lecturer) {
        new LecturerDashboard((Lecturer) user).setVisible(true);
    } else if (user instanceof Admin) {
        new AdminDashboard((Admin) user).setVisible(true);
    }
}
```

**Benefits:**

- Runtime object creation based on type
- Flexible dashboard instantiation
- Easy to add new user types

### 6.4 Observer Pattern (Event Listeners)

**Used in:** GUI event handling

**Implementation:**

```java
loginButton.addActionListener(e -> handleLogin());
courseCombo.addActionListener(e -> loadStudentsForCourse());
```

**Benefits:**

- Loose coupling between components
- Event-driven architecture
- Easy to add new event handlers

---

## 7. CLASS RELATIONSHIPS

### 7.1 Inheritance Relationships

| Parent Class | Child Classes            | Relationship Type  |
| ------------ | ------------------------ | ------------------ |
| Person       | Student, Lecturer, Admin | IS-A (Inheritance) |

### 7.2 Association Relationships

| Class A            | Class B      | Relationship  | Multiplicity |
| ------------------ | ------------ | ------------- | ------------ |
| Student            | Course       | Registers for | Many-to-Many |
| Lecturer           | Course       | Teaches       | One-to-Many  |
| CourseRegistration | Grade        | Has           | One-to-One   |
| Person             | Announcement | Posts         | One-to-Many  |

### 7.3 Composition Relationships

| Container | Component | Relationship |
| --------- | --------- | ------------ |
| Dashboard | Panels    | Contains     |
| Service   | Database  | Uses         |

### 7.4 Dependency Relationships

| Dependent       | Dependency      | Type |
| --------------- | --------------- | ---- |
| GUI Classes     | Service Classes | Uses |
| Service Classes | Model Classes   | Uses |
| Service Classes | Database        | Uses |

---

## 8. PACKAGE STRUCTURE

```
src/
├── Main.java                          # Application entry point
├── models/                            # Domain objects
│   ├── Person.java
│   ├── Student.java
│   ├── Lecturer.java
│   ├── Admin.java
│   ├── Course.java
│   ├── CourseRegistration.java
│   ├── Grade.java
│   └── Announcement.java
├── services/                          # Business logic
│   ├── AuthenticationService.java
│   ├── CourseService.java
│   ├── GradeService.java
│   ├── AnnouncementService.java
│   ├── UserService.java
│   └── LogService.java
├── database/                          # Data access
│   ├── DatabaseOperations.java
│   └── MySQLDatabase.java
├── gui/                              # User interface
│   ├── LoginForm.java
│   ├── StudentRegistrationForm.java
│   ├── StudentDashboard.java
│   ├── LecturerDashboard.java
│   ├── AdminDashboard.java
│   ├── StudentCoursesPanel.java
│   ├── CourseRegistrationPanel.java
│   ├── StudentGradesPanel.java
│   ├── StudentFeeBalancePanel.java
│   ├── StudentProfilePanel.java
│   ├── LecturerCoursesPanel.java
│   ├── ClassListPanel.java
│   ├── UploadGradesPanel.java
│   ├── PostAnnouncementPanel.java
│   ├── MyAnnouncementsPanel.java
│   ├── AnnouncementsPanel.java
│   └── LecturerProfilePanel.java
└── utils/                            # Utilities
    └── SessionManager.java

database/
└── scms_schema.sql                   # Database schema

lib/
└── mysql-connector-java-*.jar        # JDBC driver
```

---

**Next Section:** Key Features Implementation with Code Snippets

---

**Document Created:** November 6, 2025  
**Project:** Smart Campus Management System (SCMS)  
**Course:** BIT 2104 / BCS 2102 - Object-Oriented Programming (Java)
