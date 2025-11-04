# Quick Start Guide - Running in VS Code

## ✅ Prerequisites Checklist

1. ✅ **Java JDK 8+** - VS Code Java extensions installed
2. ✅ **MySQL JDBC Driver** - Already in `lib/mysql-connector-j-9.5.0.jar`
3. ✅ **VS Code Java Extension Pack** - Required for running Java
4. ⚠️ **MySQL Server** - Must be running
5. ⚠️ **Database Setup** - Must execute `database/scms_schema.sql`

## 🚀 Steps to Run in VS Code

### Step 1: Install VS Code Java Extension (if not installed)

1. Press `Ctrl+Shift+X` to open Extensions
2. Search for "Extension Pack for Java"
3. Click Install (by Microsoft)

### Step 2: Setup MySQL Database

**Option A - Using MySQL Workbench:**

1. Open MySQL Workbench
2. Connect to your local MySQL server
3. File → Open SQL Script
4. Navigate to `database/scms_schema.sql`
5. Click the lightning bolt ⚡ to execute
6. Verify: You should see `scms_db` database created

**Option B - Using Command Line:**

```bash
# Open Git Bash or Command Prompt
mysql -u root -p
# Enter your MySQL password

# Then run:
source database/scms_schema.sql
# Or on Windows:
\. database/scms_schema.sql
```

### Step 3: Configure Database Credentials

Open `src/database/MySQLDatabase.java` and update (if needed):

```java
private static final String DB_USER = "root";     // Your MySQL username
private static final String DB_PASSWORD = "";     // Your MySQL password (if you have one)
```

### Step 4: Run the Application

**Method 1 - Using Run Button (Easiest):**

1. Open `src/Main.java`
2. You'll see a "Run" button above the `main` method
3. Click "Run" or "Debug"
4. The login window should appear!

**Method 2 - Using F5:**

1. Press `F5` (or Ctrl+F5 for Run without Debugging)
2. Select "Java" if prompted
3. The application will launch

**Method 3 - Using Terminal:**

```bash
# Compile all files
javac -d bin -cp "lib/*" src/**/*.java src/*.java src/**/**/*.java

# Run the application
java -cp "bin;lib/*" Main
```

### Step 5: Login

Use these default credentials:

**Administrator:**

- Username: `admin`
- Password: `admin123`

**Lecturer:**

- Username: `lec001`
- Password: `password123`

**Student:**

- Username: `stu001`
- Password: `password123`

Or create a new student account using the "Create Student Account" button.

## 🔧 Troubleshooting

### Problem: "MySQL service is not running"

**Solution:**

- Windows: Open Services → Find MySQL80 → Click Start
- Or: Open MySQL Workbench and start the server

### Problem: "Cannot find MySQL driver"

**Solution:**

- Make sure `lib/mysql-connector-j-9.5.0.jar` exists
- Check `.vscode/settings.json` has: `"java.project.referencedLibraries": ["lib/**/*.jar"]`
- Reload VS Code window: `Ctrl+Shift+P` → "Developer: Reload Window"

### Problem: "Access denied for user 'root'"

**Solution:**

- Update the password in `src/database/MySQLDatabase.java`
- Line 15: Change `DB_PASSWORD` to your MySQL password

### Problem: "Database 'scms_db' doesn't exist"

**Solution:**

- You haven't run the SQL schema file yet
- Execute `database/scms_schema.sql` in MySQL (see Step 2)

### Problem: GUI doesn't appear

**Solution:**

- Make sure you're running `Main.java` (not another file)
- Check terminal for error messages
- Try cleaning and rebuilding: `Ctrl+Shift+P` → "Java: Clean Java Language Server Workspace"

## 📁 Project Structure

```
src/
├── Main.java              ← START HERE (Run this file)
├── database/
│   ├── DatabaseOperations.java
│   └── MySQLDatabase.java
├── models/
├── services/
├── utils/
└── gui/
    └── LoginForm.java     ← First window that appears
```

## ✨ Quick Test

After running, you should see:

1. A login window (500x400px)
2. Username and password fields
3. "Login" and "Create Student Account" buttons

Try logging in with `admin` / `admin123` to see the admin dashboard!

## 🎯 Next Steps

Once logged in:

- **Students**: Register for courses, view grades, export to CSV
- **Lecturers**: Upload grades, view class lists, post announcements
- **Admins**: Manage users, courses, view reports

---

**Need help? Check the main README.md for detailed documentation.**
