# Question 1: UML Class Diagram - SCMS System Design

## Smart Campus Management System (SCMS)

### Object-Oriented Class Structure

---

## UML Class Diagram (Textual Representation)

```
┌─────────────────────────────────────────────────────────────┐
│                         <<abstract>>                         │
│                           Person                             │
├─────────────────────────────────────────────────────────────┤
│ - userId: int                                               │
│ - username: String                                          │
│ - email: String                                             │
│ - firstName: String                                         │
│ - lastName: String                                          │
│ - dateOfBirth: LocalDate                                    │
│ - gender: String                                            │
│ - phoneNumber: String                                       │
│ - address: String                                           │
│ - role: String                                              │
├─────────────────────────────────────────────────────────────┤
│ + Person(userId, username, email, firstName, lastName)      │
│ + getFullName(): String                                     │
│ + getUserId(): int                                          │
│ + getUsername(): String                                     │
│ + getEmail(): String                                        │
│ + getRole(): String                                         │
│ + setters...                                                │
└─────────────────────────────────────────────────────────────┘
                              △
                              │
                    ┌─────────┴─────────┐
                    │                   │
                    │                   │
┌───────────────────┴──────┐   ┌────────┴──────────────────┐
│       Student            │   │       Lecturer            │
├──────────────────────────┤   ├───────────────────────────┤
│ - studentId: int         │   │ - lecturerId: int         │
│ - regNumber: String      │   │ - employeeNumber: String  │
│ - program: String        │   │ - department: String      │
│ - yearOfStudy: int       │   │ - specialization: String  │
│ - semester: int          │   │ - qualification: String   │
│ - enrollmentDate: Date   │   │ - hireDate: Date          │
│ - feeBalance: double     │   │ - officeLocation: String  │
│ - gpa: double            │   │ - status: String          │
│ - status: String         │   ├───────────────────────────┤
├──────────────────────────┤   │ + Lecturer(...)           │
│ + Student(...)           │   │ + getLecturerId(): int    │
│ + getStudentId(): int    │   │ + getEmployeeNumber(): Str│
│ + getRegistrationNum...  │   │ + getDepartment(): String │
│ + getProgram(): String   │   │ + getSpecialization(): Str│
│ + getYearOfStudy(): int  │   │ + getQualification(): Str │
│ + getSemester(): int     │   │ + getOfficeLocation(): Str│
│ + getFeeBalance(): double│   │ + getStatus(): String     │
│ + getGpa(): double       │   │ + setters...              │
│ + getStatus(): String    │   └───────────────────────────┘
│ + setters...             │
└──────────────────────────┘
         │
         │
┌────────┴──────────────────┐
│         Admin             │
├───────────────────────────┤
│ - adminId: int            │
│ - employeeNumber: String  │
│ - department: String      │
│ - accessLevel: int        │
├───────────────────────────┤
│ + Admin(...)              │
│ + getAdminId(): int       │
│ + getEmployeeNumber(): Str│
│ + getDepartment(): String │
│ + getAccessLevel(): int   │
│ + setters...              │
└───────────────────────────┘


┌─────────────────────────────────────────────────────────────┐
│                           Course                             │
├─────────────────────────────────────────────────────────────┤
│ - courseId: int                                             │
│ - courseCode: String                                        │
│ - courseName: String                                        │
│ - description: String                                       │
│ - credits: int                                              │
│ - department: String                                        │
│ - semester: int                                             │
│ - yearLevel: int                                            │
│ - maxCapacity: int                                          │
│ - lecturerId: int                                           │
│ - lecturerName: String                                      │
│ - isActive: boolean                                         │
├─────────────────────────────────────────────────────────────┤
│ + Course(courseId, courseCode, courseName, ...)            │
│ + getCourseId(): int                                        │
│ + getCourseCode(): String                                   │
│ + getCourseName(): String                                   │
│ + getDescription(): String                                  │
│ + getCredits(): int                                         │
│ + getDepartment(): String                                   │
│ + getSemester(): int                                        │
│ + getYearLevel(): int                                       │
│ + getMaxCapacity(): int                                     │
│ + getLecturerId(): int                                      │
│ + getLecturerName(): String                                 │
│ + isActive(): boolean                                       │
│ + setters...                                                │
└─────────────────────────────────────────────────────────────┘
                              △
                              │
                              │ uses
                              │
┌─────────────────────────────────────────────────────────────┐
│                    CourseRegistration                        │
├─────────────────────────────────────────────────────────────┤
│ - registrationId: int                                       │
│ - studentId: int                                            │
│ - courseId: int                                             │
│ - academicYear: String                                      │
│ - semester: int                                             │
│ - registrationDate: Timestamp                               │
│ - status: String                                            │
├─────────────────────────────────────────────────────────────┤
│ + CourseRegistration(...)                                   │
│ + getRegistrationId(): int                                  │
│ + getStudentId(): int                                       │
│ + getCourseId(): int                                        │
│ + getAcademicYear(): String                                 │
│ + getSemester(): int                                        │
│ + getRegistrationDate(): Timestamp                          │
│ + getStatus(): String                                       │
│ + setters...                                                │
└─────────────────────────────────────────────────────────────┘
                              △
                              │
                              │ has
                              │
┌─────────────────────────────────────────────────────────────┐
│                           Grade                              │
├─────────────────────────────────────────────────────────────┤
│ - gradeId: int                                              │
│ - registrationId: int                                       │
│ - courseworkMarks: double                                   │
│ - examMarks: double                                         │
│ - totalMarks: double                                        │
│ - letterGrade: String                                       │
│ - gradePoints: double                                       │
│ - remarks: String                                           │
│ - uploadedBy: int                                           │
│ - uploadedAt: Timestamp                                     │
├─────────────────────────────────────────────────────────────┤
│ + Grade(gradeId, registrationId, courseworkMarks, ...)     │
│ + getGradeId(): int                                         │
│ + getRegistrationId(): int                                  │
│ + getCourseworkMarks(): double                              │
│ + getExamMarks(): double                                    │
│ + getTotalMarks(): double                                   │
│ + getLetterGrade(): String                                  │
│ + getGradePoints(): double                                  │
│ + getRemarks(): String                                      │
│ + calculateLetterGrade(totalMarks): String                  │
│ + setters...                                                │
└─────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────┐
│                        Announcement                          │
├─────────────────────────────────────────────────────────────┤
│ - announcementId: int                                       │
│ - title: String                                             │
│ - content: String                                           │
│ - postedBy: int                                             │
│ - postedByName: String                                      │
│ - targetAudience: String                                    │
│ - courseId: Integer                                         │
│ - postedAt: LocalDateTime                                   │
│ - expiresAt: LocalDateTime                                  │
│ - isActive: boolean                                         │
├─────────────────────────────────────────────────────────────┤
│ + Announcement(...)                                         │
│ + getAnnouncementId(): int                                  │
│ + getTitle(): String                                        │
│ + getContent(): String                                      │
│ + getPostedBy(): int                                        │
│ + getPostedByName(): String                                 │
│ + getTargetAudience(): String                               │
│ + getCourseId(): Integer                                    │
│ + getPostedAt(): LocalDateTime                              │
│ + getExpiresAt(): LocalDateTime                             │
│ + isActive(): boolean                                       │
│ + isExpired(): boolean                                      │
│ + setters...                                                │
└─────────────────────────────────────────────────────────────┘
```

---

## Class Relationships

### 1. **Inheritance Hierarchy**

```
Person (Parent/Base Class)
   ├── Student (Child Class)
   ├── Lecturer (Child Class)
   └── Admin (Child Class)
```

### 2. **Associations**

- **Student** ←→ **Course** (Many-to-Many through CourseRegistration)
- **Lecturer** ←→ **Course** (One-to-Many)
- **CourseRegistration** ←→ **Grade** (One-to-One)
- **Announcement** ←→ **Person** (Many-to-One)
- **Announcement** ←→ **Course** (Many-to-One, optional)

### 3. **Dependencies**

- GUI classes depend on Model classes
- Service classes depend on Model and Database classes
- Database classes implement DatabaseOperations interface

---

## Key Design Principles Applied

### 1. **Inheritance**

- `Student`, `Lecturer`, and `Admin` all inherit from `Person`
- Common attributes (userId, username, email, name) are in the parent class
- Specialized attributes are in child classes

### 2. **Encapsulation**

- All fields are `private`
- Access through public getters and setters
- Data validation in setter methods

### 3. **Polymorphism**

- Method overriding (e.g., `toString()`, `equals()`)
- Different dashboard types for different user roles
- Interface implementation (DatabaseOperations)

### 4. **Abstraction**

- Person class serves as abstract base
- DatabaseOperations interface defines contract
- Service layer abstracts business logic from GUI

---

## Visual Representation Note

For a graphical UML diagram, you can use tools like:

- **Lucidchart** (https://www.lucidchart.com)
- **Draw.io** (https://app.diagrams.net)
- **PlantUML** (https://plantuml.com)
- **Visual Paradigm** (https://www.visual-paradigm.com)

Simply import this textual representation or create a new diagram based on the class structure shown above.

---

**Document Created:** November 6, 2025  
**Project:** Smart Campus Management System (SCMS)  
**Course:** BIT 2104 / BCS 2102 - Object-Oriented Programming (Java)
