# SMART CAMPUS MANAGEMENT SYSTEM (SCMS)

## Part 5: Challenges Faced and Solutions

---

## 10. CHALLENGES FACED DURING DEVELOPMENT

### 10.1 Infrastructure and Environment Challenges

#### 10.1.1 Power Outages (Load Shedding)

**Challenge:**
During the 6-day development period, we experienced frequent power outages typical of Kampala's electricity supply issues. On Day 3, we lost 4 hours of work when power went off at Ndejje University's computer lab without UPS backup. This affected our ability to maintain consistent development progress and test database connectivity.

**Impact:**

- Lost uncommitted code changes
- Interrupted database connection testing
- Delayed integration testing by half a day
- Team members working from home in areas like Nansana and Bweyogerere faced even longer outages (up to 8 hours)

**Solution Implemented:**

```java
// Implemented auto-save functionality for forms
private void setupAutoSave() {
    Timer autoSaveTimer = new Timer(60000, e -> {  // Every 1 minute
        saveFormDataToLocalFile();
        System.out.println("Form data auto-saved at: " + LocalDateTime.now());
    });
    autoSaveTimer.start();
}

// Added connection retry mechanism for database
public boolean connectWithRetry(int maxRetries) {
    int attempts = 0;
    while (attempts < maxRetries) {
        try {
            if (connect()) {
                return true;
            }
        } catch (SQLException e) {
            attempts++;
            System.out.println("Connection attempt " + attempts + " failed. Retrying...");
            try {
                Thread.sleep(2000);  // Wait 2 seconds before retry
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
    return false;
}
```

**Lessons Learned:**

- Always commit and push code frequently to GitHub
- Implement auto-save features for long forms
- Use laptop batteries as temporary UPS
- Work offline when possible and sync when power returns

---

#### 10.1.2 Internet Connectivity Issues

**Challenge:**
MTN and Airtel data bundles ran out faster than expected due to frequent GitHub pushes, Maven dependency downloads, and MySQL documentation browsing. At UGX 1,000 per 100MB, data costs became a concern for team members. Additionally, campus WiFi at Ndejje was unreliable, especially during peak hours (12 PM - 2 PM).

**Impact:**

- Difficulty downloading JDBC drivers (mysql-connector-java-8.0.33.jar - 2.4MB)
- Slow GitHub repository cloning
- Limited access to online documentation (Stack Overflow, JavaDocs)

**Solution:**

1. **Offline Documentation:** Downloaded Java API docs and MySQL reference manual to local drive
2. **Resource Sharing:** Team members shared downloaded JARs via WhatsApp and flash disks
3. **Smart Git Usage:**

   ```bash
   # Used shallow clones to save data
   git clone --depth 1 https://github.com/NY-Henry/SCMS_Java-Campus-Management-System.git

   # Compressed files before pushing
   git config core.compression 9
   ```

4. **Batch Operations:** Combined multiple commits before pushing to save data
5. **Campus WiFi Strategy:** Came to campus early (7 AM) when WiFi was faster

---

### 10.2 Technical Challenges

#### 10.2.1 MySQL Database Installation Issues

**Challenge:**
Some team members using older laptops (2GB RAM) struggled with XAMPP performance. One member's laptop running Windows 7 couldn't install MySQL 8.0, requiring MySQL 5.7 compatibility adjustments.

**Error Encountered:**

```
Error: Public Key Retrieval is not allowed
```

**Solution:**
Modified database connection string to handle different MySQL versions:

```java
// Original connection string
private static final String DB_URL = "jdbc:mysql://localhost:3306/scms_db";

// Modified for compatibility
private static final String DB_URL =
    "jdbc:mysql://localhost:3306/scms_db" +
    "?allowPublicKeyRetrieval=true" +
    "&useSSL=false" +
    "&serverTimezone=Africa/Nairobi";  // East African Time (UTC+3)

// Added database initialization check
public void initializeDatabase() {
    try {
        // Check if database exists
        String checkDB = "CREATE DATABASE IF NOT EXISTS scms_db " +
                        "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        executeUpdate(checkDB);

        // Import schema from file
        File schemaFile = new File("database/scms_schema.sql");
        if (schemaFile.exists()) {
            System.out.println("Database schema found. Initializing...");
            executeSQLFile(schemaFile);
        }
    } catch (SQLException e) {
        System.err.println("Database initialization failed: " + e.getMessage());
    }
}
```

**Workaround for Low-Spec Machines:**

- Used SQLite as alternative for development (lighter weight)
- Shared remote MySQL instance hosted on team leader's laptop
- Optimized queries to reduce memory usage

---

#### 10.2.2 Date and Time Handling (Ugandan Context)

**Challenge:**
Initial implementation used default system timezone, causing inconsistencies. Ugandan time (EAT - UTC+3) needed explicit handling. Some computers showed enrollment dates incorrectly due to timezone mismatch.

**Problem Code:**

```java
// This caused issues
Date enrollmentDate = new Date();  // Used system default timezone
```

**Solution:**

```java
// Set explicit timezone for East Africa
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final ZoneId EAST_AFRICA_TIMEZONE = ZoneId.of("Africa/Kampala");

    public static LocalDateTime getCurrentEATTime() {
        return LocalDateTime.now(EAST_AFRICA_TIMEZONE);
    }

    public static String formatUgandanDate(LocalDate date) {
        // Format: 06 November 2025 (Ugandan preference)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String formatWithDayName(LocalDate date) {
        // Format: Wednesday, 06 November 2025
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
        return date.format(formatter);
    }
}
```

---

#### 10.2.3 Handling Ugandan Names in Database

**Challenge:**
Many Ugandan names contain special characters, multiple surnames, or tribal prefixes that caused validation issues:

- Names like "Ssemakula" (double 'S' - Luganda)
- Hyphenated names: "Akello-Odongo"
- Names with apostrophes: "Waiswa D'Great"
- Single names common in some tribes (e.g., "Ocheng")

**Initial Validation (Too Restrictive):**

```java
// This rejected many valid Ugandan names
private boolean isValidName(String name) {
    return name.matches("^[A-Za-z]+$");  // Only letters
}
```

**Improved Solution:**

```java
private boolean isValidUgandanName(String name) {
    // Allows spaces, hyphens, apostrophes, and handles single names
    if (name == null || name.trim().isEmpty()) {
        return false;
    }

    // Pattern for Ugandan names
    // - Allows letters (including double consonants like 'ss', 'kk')
    // - Allows spaces, hyphens, apostrophes
    // - Minimum 2 characters
    // - Allows single names (no surname required)
    String namePattern = "^[A-Za-z][A-Za-z\\s'-]{1,50}$";

    return name.trim().matches(namePattern);
}

// Made lastName optional for single-name cases
public Person(int userId, String username, String email,
              String firstName, String lastName) {
    this.userId = userId;
    this.username = username;
    this.email = email;
    this.firstName = firstName;
    this.lastName = (lastName != null && !lastName.isEmpty()) ? lastName : "";
}

@Override
public String getFullName() {
    return lastName.isEmpty() ? firstName : firstName + " " + lastName;
}
```

---

#### 10.2.4 Mobile Money Integration (Future Consideration)

**Challenge:**
Fee payment tracking required manual entry. Students requested integration with Mobile Money (MTN MoMo, Airtel Money) for real-time fee verification, but API access requires business registration and approval from telecoms.

**Current Workaround:**

```java
// Manual fee payment recording by admin
public boolean recordFeePayment(int studentId, double amount,
                               String paymentMethod, String referenceNumber) {
    try {
        // Validate payment method
        String[] validMethods = {"Cash", "Bank Transfer", "MTN Mobile Money",
                                "Airtel Money", "Bank Cheque"};
        if (!Arrays.asList(validMethods).contains(paymentMethod)) {
            throw new IllegalArgumentException("Invalid payment method");
        }

        // Record payment
        String query = "INSERT INTO fee_payments " +
                      "(student_id, amount, payment_date, payment_method, " +
                      "reference_number, recorded_by) " +
                      "VALUES (?, ?, NOW(), ?, ?, ?)";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setDouble(2, amount);
            stmt.setString(3, paymentMethod);
            stmt.setString(4, referenceNumber);
            stmt.setInt(5, SessionManager.getInstance().getCurrentUser().getUserId());

            int result = stmt.executeUpdate();

            if (result > 0) {
                // Update student's fee balance
                updateFeeBalance(studentId);
                return true;
            }
        }
    } catch (SQLException e) {
        System.err.println("Error recording payment: " + e.getMessage());
    }
    return false;
}
```

**Future Enhancement Plan:**

- Integrate MTN MoMo API once business approval obtained
- Add Airtel Money API integration
- Implement automated SMS notifications for payment confirmation

---

### 10.3 Design and User Experience Challenges

#### 10.3.1 Multi-Language Support Consideration

**Challenge:**
While English is the official language of instruction at Ndejje University, some administrative staff and students are more comfortable with Luganda or other local languages for system navigation.

**Current Implementation (English Only):**

```java
JOptionPane.showMessageDialog(this,
    "Course registered successfully!",
    "Success", JOptionPane.INFORMATION_MESSAGE);
```

**Future Enhancement:**

```java
public class LanguageManager {
    private static final Map<String, Map<String, String>> translations = new HashMap<>();
    private static String currentLanguage = "en";  // Default: English

    static {
        // English translations
        Map<String, String> english = new HashMap<>();
        english.put("course_success", "Course registered successfully!");
        english.put("login_failed", "Invalid username or password");
        english.put("welcome", "Welcome to SCMS");
        translations.put("en", english);

        // Luganda translations
        Map<String, String> luganda = new HashMap<>();
        luganda.put("course_success", "Ekibiina kiwandiisiddwa bulungi!");
        luganda.put("login_failed", "Erinnya ly'omukozesa oba ekigambo kya siri kyabulungi");
        luganda.put("welcome", "Tukusanyukidde mu SCMS");
        translations.put("lg", luganda);
    }

    public static String getText(String key) {
        return translations.get(currentLanguage).getOrDefault(key, key);
    }

    public static void setLanguage(String lang) {
        currentLanguage = lang;
    }
}
```

---

#### 10.3.2 Academic Calendar Synchronization

**Challenge:**
Ugandan universities follow unique academic calendar patterns:

- Two semesters per year (Semester I: August-December, Semester II: January-May)
- Recess term (June-July) for retakes and short courses
- Public holidays affect class schedules (e.g., Independence Day, Martyrs Day)

**Solution:**

```java
public class AcademicCalendar {
    private static final String CURRENT_ACADEMIC_YEAR = "2025/2026";

    public static int getCurrentSemester() {
        Month currentMonth = LocalDate.now().getMonth();

        // Semester I: August - December
        if (currentMonth.getValue() >= 8 && currentMonth.getValue() <= 12) {
            return 1;
        }
        // Semester II: January - May
        else if (currentMonth.getValue() >= 1 && currentMonth.getValue() <= 5) {
            return 2;
        }
        // Recess term: June - July
        else {
            return 0;  // Recess (no regular semester)
        }
    }

    public static String getCurrentAcademicYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();

        // Academic year starts in August
        if (now.getMonthValue() >= 8) {
            return year + "/" + (year + 1);
        } else {
            return (year - 1) + "/" + year;
        }
    }

    public static boolean isPublicHoliday(LocalDate date) {
        // Ugandan public holidays
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // Fixed holidays
        if (month == 1 && day == 1) return true;   // New Year
        if (month == 1 && day == 26) return true;  // NRM Liberation Day
        if (month == 3 && day == 8) return true;   // International Women's Day
        if (month == 5 && day == 1) return true;   // Labour Day
        if (month == 6 && day == 3) return true;   // Martyrs Day
        if (month == 6 && day == 9) return true;   // National Heroes Day
        if (month == 10 && day == 9) return true;  // Independence Day
        if (month == 12 && day == 25) return true; // Christmas
        if (month == 12 && day == 26) return true; // Boxing Day

        // Variable holidays (Eid, Easter) - simplified check
        return false;
    }
}
```

---

### 10.4 Team Collaboration Challenges

#### 10.4.1 Version Control Conflicts

**Challenge:**
Working as a team of 3-4 members on shared codebase led to frequent Git merge conflicts, especially when multiple people edited the same panel files simultaneously. Communication via WhatsApp groups sometimes delayed conflict resolution.

**Common Conflict:**

```
<<<<<<< HEAD
loginButton.setBackground(new Color(70, 130, 180));
=======
loginButton.setBackground(Color.BLUE);
>>>>>>> feature/new-design
```

**Solution Strategy:**

1. **Clear Division of Work:**

   - Member 1: Student-related panels
   - Member 2: Lecturer-related panels
   - Member 3: Admin panels and database
   - Member 4: Services and utilities

2. **Git Workflow:**

```bash
# Before starting work
git pull origin main
git checkout -b feature/my-task

# After completing task
git add .
git commit -m "Implemented student dashboard - [Your Name]"
git push origin feature/my-task

# Created pull request on GitHub for review
```

3. **Daily Sync Meetings:**
   - WhatsApp video calls at 8 PM daily
   - Discussed progress and potential conflicts
   - Coordinated who would work on which files

---

#### 10.4.2 Different Development Environments

**Challenge:**
Team members had different setups:

- Windows 10, Windows 11, Windows 7
- Different Java versions (JDK 8, JDK 11, JDK 17)
- Different IDEs (NetBeans, Eclipse, IntelliJ IDEA, VS Code)
- XAMPP vs WAMP for MySQL

**Solution:**
Created standardized setup documentation:

````markdown
# SCMS Development Environment Setup (Ndejje University)

## Required Software:

1. **Java JDK 11 or higher**

   - Download: https://www.oracle.com/java/technologies/downloads/
   - Set JAVA_HOME environment variable

2. **MySQL (via XAMPP)**

   - Download XAMPP: https://www.apachefriends.org/
   - Start Apache and MySQL services
   - Access phpMyAdmin: http://localhost/phpmyadmin

3. **Git**

   - Download: https://git-scm.com/
   - Configure: git config --global user.name "Your Name"

4. **IDE (Recommended: VS Code)**
   - Install Java Extension Pack
   - Install MySQL Extension

## Project Setup:

```bash
# Clone repository
git clone https://github.com/NY-Henry/SCMS_Java-Campus-Management-System.git

# Navigate to project
cd SCMS_Java-Campus-Management-System

# Import database
mysql -u root -p scms_db < database/scms_schema.sql

# Compile project
javac -d bin -cp "lib/*" src/**/*.java src/*.java

# Run project
java -cp "bin;lib/*" Main
```
````

````

---

### 10.5 Testing Challenges

#### 10.5.1 Limited Test Data

**Challenge:**
Needed realistic Ugandan student data for testing but manually creating hundreds of entries was time-consuming.

**Solution:**
Created data generation script:

```java
public class TestDataGenerator {
    private static final String[] UGANDAN_FIRST_NAMES = {
        "Moses", "Grace", "Robert", "Sarah", "John", "Mary", "David", "Esther",
        "Joseph", "Rebecca", "Samuel", "Ruth", "Isaac", "Lydia", "Joshua", "Miriam",
        "Emmanuel", "Patience", "Peter", "Faith", "Paul", "Hope", "James", "Joy"
    };

    private static final String[] UGANDAN_SURNAMES = {
        "Ssemakula", "Nakawala", "Mugisha", "Namuli", "Okello", "Achan",
        "Wasswa", "Nakato", "Kato", "Babirye", "Musoke", "Namutebi",
        "Lubega", "Nalongo", "Mukasa", "Nambi", "Kizito", "Nansubuga"
    };

    private static final String[] PROGRAMS = {
        "Bachelor of Information Technology",
        "Bachelor of Computer Science",
        "Bachelor of Business Administration",
        "Bachelor of Education",
        "Bachelor of Social Work"
    };

    public void generateStudents(int count) throws SQLException {
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            String firstName = UGANDAN_FIRST_NAMES[random.nextInt(UGANDAN_FIRST_NAMES.length)];
            String lastName = UGANDAN_SURNAMES[random.nextInt(UGANDAN_SURNAMES.length)];
            String regNumber = "2025/STU/" + String.format("%04d", i + 1);
            String username = "stu" + String.format("%04d", i + 1);
            String email = username + "@ndejjeuniversity.ac.ug";
            String program = PROGRAMS[random.nextInt(PROGRAMS.length)];
            int year = random.nextInt(4) + 1;  // Year 1-4

            insertStudent(username, email, firstName, lastName, regNumber, program, year);
        }

        System.out.println("Generated " + count + " test students successfully!");
    }
}
````

---

## 11. KEY TAKEAWAYS

### Technical Lessons:

1. ✅ **Exception handling is crucial** - Prevented crashes during power outages and network failures
2. ✅ **Singleton pattern for database** - Ensured single connection, reduced resource usage
3. ✅ **Input validation saves time** - Caught errors before database operations
4. ✅ **Modular code design** - Made collaboration easier with clear separation

### Soft Skills Learned:

1. ✅ **Team communication** - WhatsApp coordination was essential
2. ✅ **Time management** - 6-day deadline required strict planning
3. ✅ **Problem-solving** - Found creative solutions for infrastructure issues
4. ✅ **Adaptability** - Adjusted to power cuts, slow internet, different environments

### Cultural Context:

1. ✅ **Understanding local constraints** - Power, internet, data costs are real challenges
2. ✅ **Ugandan naming conventions** - System must handle local names properly
3. ✅ **Academic calendar differences** - System aligned with Ugandan university structure
4. ✅ **Payment methods** - Considered local preferences (Mobile Money)

---

**Next Section:** Conclusion and Recommendations

---

**Document Created:** November 6, 2025  
**Project:** Smart Campus Management System (SCMS)  
**Course:** BIT 2104 / BCS 2102 - Object-Oriented Programming (Java)  
**Institution:** Ndejje University, Kampala, Uganda
