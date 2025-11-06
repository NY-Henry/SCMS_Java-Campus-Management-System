# Question 3: Event-Driven Programming and GUI Explanation

## Smart Campus Management System (SCMS)

### Event-Driven Architecture and User Interface Design

---

## 1. INTRODUCTION TO EVENT-DRIVEN PROGRAMMING

### What is Event-Driven Programming?

Event-driven programming is a programming paradigm where the flow of the program is determined by events such as user actions (button clicks, text input, menu selections), sensor outputs, or messages from other programs. In our SCMS, we use Java Swing's event-driven model to create an interactive desktop application.

### Key Components:

- **Events**: Actions that occur (button clicks, key presses, mouse movements)
- **Event Sources**: Components that generate events (buttons, text fields, menus)
- **Event Listeners**: Objects that wait for and respond to events
- **Event Handlers**: Methods that execute when events occur

---

## 2. EVENT-DRIVEN ARCHITECTURE IN SCMS

### 2.1 Login Form Event Handling

#### Location: `LoginForm.java`

**Login Button Click Event:**

```java
// Event Source: loginButton
loginButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        handleLogin();  // Event Handler
    }
});
```

**How It Works:**

1. **Event Source**: `loginButton` (JButton component)
2. **Event Listener**: `ActionListener` interface implementation
3. **Event**: User clicks the login button
4. **Event Handler**: `handleLogin()` method executes
5. **Response**: Validates credentials, authenticates user, opens appropriate dashboard

**Enter Key Event (Password Field):**

```java
// Alternative trigger - pressing Enter in password field
passwordField.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        handleLogin();  // Same handler, different event source
    }
});
```

**Benefit**: User can press Enter instead of clicking button - improved UX.

---

### 2.2 Asynchronous Event Handling with SwingWorker

**Background Processing for Login:**

```java
private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    // Validation
    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter both username and password!",
            "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Disable button and show loading state
    loginButton.setEnabled(false);
    loginButton.setText("Logging in...");

    // Background thread to prevent UI freezing
    SwingWorker<Person, Void> worker = new SwingWorker<Person, Void>() {
        @Override
        protected Person doInBackground() throws Exception {
            // Long-running operation in background
            return authService.login(username, password);
        }

        @Override
        protected void done() {
            try {
                Person user = get();

                if (user != null) {
                    // Successful login
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
```

**Event-Driven Concepts Demonstrated:**

1. **Immediate Response**: Button disabled instantly when clicked
2. **Background Processing**: Database operations run in separate thread
3. **UI Responsiveness**: Main UI thread remains responsive
4. **Callback Handling**: `done()` method called when background task completes
5. **State Management**: Button text and enabled state updated appropriately

---

### 2.3 Mouse Events - Hover Effects

#### Location: `LoginForm.java`, `StudentRegistrationForm.java`

**Button Hover Effects:**

```java
loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (loginButton.isEnabled()) {
            loginButton.setBackground(new Color(70, 130, 180).darker());
        }
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
        if (loginButton.isEnabled()) {
            loginButton.setBackground(new Color(70, 130, 180));
        }
    }
});
```

**Event Flow:**

- **Event**: Mouse pointer enters/exits button area
- **Listener**: `MouseAdapter` (convenience class for MouseListener)
- **Response**: Background color changes dynamically
- **Result**: Visual feedback for interactive elements

---

### 2.4 Student Registration Form Events

#### Location: `StudentRegistrationForm.java`

**Multiple Event Sources, Single Goal:**

```java
// Register button event
registerButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        handleRegistration();
    }
});

// Cancel button event
cancelButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();  // Close form
    }
});
```

**Registration Handler with Validation Events:**

```java
private void handleRegistration() {
    try {
        // Get form data from various event sources
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String regNumber = regNumberField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String program = (String) programCombo.getSelectedItem();
        int year = Integer.parseInt((String) yearCombo.getSelectedItem());
        int semester = Integer.parseInt((String) semesterCombo.getSelectedItem());

        // Validation - throws exceptions on invalid input
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

        // Disable button during processing
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        // Perform registration
        boolean success = authService.registerStudent(
            username, password, email, firstName, lastName,
            phone, regNumber, program, year, semester);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Registration successful!\nYou can now login with your credentials.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            throw new Exception("Registration failed! Username or registration number may already exist.");
        }

    } catch (IllegalArgumentException e) {
        // Validation errors
        JOptionPane.showMessageDialog(this,
            e.getMessage(),
            "Validation Error", JOptionPane.WARNING_MESSAGE);
    } catch (Exception e) {
        // Other errors
        JOptionPane.showMessageDialog(this,
            e.getMessage(),
            "Registration Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        // Always restore button state
        registerButton.setEnabled(true);
        registerButton.setText("Register");
    }
}
```

**Event-Driven Features:**

- Multiple input fields as event sources
- Validation triggered by button click
- Exception handling provides user feedback
- Button state changes during processing
- Dialog boxes for success/error messages

---

### 2.5 Course Registration Panel Events

#### Location: `CourseRegistrationPanel.java`

**Table Selection and Button Click Events:**

```java
// Initialize components
JTable coursesTable = new JTable(tableModel);
JButton refreshButton = createMinimalButton("↻", new Color(100, 100, 110));
JButton registerButton = createMinimalButton("Register for Selected Course",
                                            new Color(70, 130, 180));

// Refresh button event
refreshButton.addActionListener(e -> loadAvailableCourses());

// Register button event
registerButton.addActionListener(e -> {
    int selectedRow = coursesTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this,
            "Please select a course to register!",
            "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Get selected course details
    String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
    String courseName = (String) tableModel.getValueAt(selectedRow, 1);
    int courseId = getSelectedCourseId(selectedRow);

    // Confirm registration
    int confirm = JOptionPane.showConfirmDialog(this,
        "Register for " + courseCode + " - " + courseName + "?",
        "Confirm Registration",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        registerForCourse(courseId);
    }
});
```

**Event Chain:**

1. User clicks on table row → Selection event
2. User clicks "Register" button → Action event
3. System validates selection
4. Confirmation dialog shown → User decision event
5. If confirmed → Registration process triggered
6. Result displayed → Information dialog event

---

### 2.6 Dashboard Navigation Events

#### Location: `StudentDashboard.java`, `LecturerDashboard.java`

**Sidebar Menu Events:**

```java
private void addMenuItem(JPanel sidebar, String text, ActionListener action) {
    JButton menuItem = new JButton(text);
    menuItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    menuItem.setForeground(new Color(45, 45, 45));
    menuItem.setBackground(Color.WHITE);
    menuItem.setFocusPainted(false);
    menuItem.setBorderPainted(false);
    menuItem.setHorizontalAlignment(SwingConstants.LEFT);
    menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
    menuItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

    // Add action listener
    menuItem.addActionListener(action);

    // Add hover effect
    menuItem.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            menuItem.setBackground(new Color(245, 247, 250));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            menuItem.setBackground(Color.WHITE);
        }
    });

    sidebar.add(menuItem);
}
```

**Usage with Lambda Expressions:**

```java
// Create menu items with inline event handlers
addMenuItem(sidebar, "🏠 Home", e -> showDashboardHome());
addMenuItem(sidebar, "📚 My Courses", e -> showMyCourses());
addMenuItem(sidebar, "➕ Register Courses", e -> showCourseRegistration());
addMenuItem(sidebar, "📊 View Grades", e -> showGrades());
addMenuItem(sidebar, "💰 Fee Balance", e -> showFeeBalance());
addMenuItem(sidebar, "👤 My Profile", e -> showProfile());
addMenuItem(sidebar, "📢 Announcements", e -> showAnnouncements());
addMenuItem(sidebar, "🚪 Logout", e -> logout());
```

**Event-Driven Benefits:**

- Clean, readable code with lambda expressions
- Each menu item triggers different panel display
- Hover effects provide visual feedback
- Consistent event handling pattern

---

### 2.7 Grade Upload Events (Lecturer Side)

#### Location: `UploadGradesPanel.java`

**Multi-Component Event Coordination:**

```java
// Course selection event
courseCombo.addActionListener(e -> {
    loadStudentsForCourse();
});

// Student selection event
studentCombo.addActionListener(e -> {
    // Enable input fields when student selected
    courseworkField.setEnabled(true);
    examField.setEnabled(true);
    remarksArea.setEnabled(true);
    uploadButton.setEnabled(true);
});

// Upload button event
uploadButton.addActionListener(e -> {
    try {
        // Get input values
        double coursework = Double.parseDouble(courseworkField.getText().trim());
        double exam = Double.parseDouble(examField.getText().trim());
        String remarks = remarksArea.getText().trim();

        // Validate marks
        if (coursework < 0 || coursework > 40) {
            throw new IllegalArgumentException("Coursework marks must be between 0 and 40");
        }
        if (exam < 0 || exam > 60) {
            throw new IllegalArgumentException("Exam marks must be between 0 and 60");
        }

        // Get selected IDs
        int registrationId = getSelectedRegistrationId();

        // Upload grade
        boolean success = gradeService.uploadGrade(
            registrationId, coursework, exam, remarks, lecturer.getLecturerId());

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Grade uploaded successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            clearForm();
        } else {
            throw new Exception("Failed to upload grade!");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Please enter valid numeric values for marks!",
            "Invalid Input", JOptionPane.ERROR_MESSAGE);
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this,
            e.getMessage(),
            "Validation Error", JOptionPane.WARNING_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error uploading grade: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
});
```

**Event Cascade:**

1. **Course selected** → Students for that course loaded
2. **Student selected** → Input fields enabled
3. **Upload clicked** → Validation → Database update → Success message → Form cleared

---

## 3. FILE I/O EVENT-DRIVEN OPERATIONS

### Export Button Events

**Example: Export Student Data**

```java
JButton exportButton = createMinimalButton("Export to CSV", new Color(70, 130, 180));

exportButton.addActionListener(e -> {
    try {
        // File chooser dialog - user interaction event
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Student Data");
        fileChooser.setSelectedFile(new File("students.csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Export data
            exportToCSV(fileToSave);

            JOptionPane.showMessageDialog(this,
                "Data exported successfully to:\n" + fileToSave.getAbsolutePath(),
                "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this,
            "Error exporting data: " + ex.getMessage(),
            "Export Error", JOptionPane.ERROR_MESSAGE);
    }
});
```

**Event Flow:**

1. User clicks export button
2. File chooser dialog opens
3. User selects save location
4. Data written to file
5. Success/error dialog shown

---

## 4. EVENT-DRIVEN ADVANTAGES IN SCMS

### 4.1 User Experience Benefits

- **Immediate Feedback**: Buttons change appearance on hover
- **Non-Blocking Operations**: UI remains responsive during database operations
- **Clear State Indication**: Button text changes during processing ("Logging in...")
- **Intuitive Navigation**: Click-based interaction familiar to users

### 4.2 Code Organization Benefits

- **Separation of Concerns**: Event handlers separate from business logic
- **Reusability**: Same handler can respond to multiple event sources
- **Maintainability**: Easy to add/modify event responses
- **Testability**: Event handlers can be tested independently

### 4.3 System Architecture Benefits

- **Loose Coupling**: GUI components don't directly depend on each other
- **Scalability**: New features added by adding new event listeners
- **Flexibility**: Event handling logic easily modified without changing UI
- **Asynchronous Processing**: Background tasks don't freeze UI

---

## 5. EVENT LISTENER TYPES USED IN SCMS

| Listener Type    | Purpose                              | Example Usage                 |
| ---------------- | ------------------------------------ | ----------------------------- |
| `ActionListener` | Button clicks, menu items, Enter key | Login button, Register button |
| `MouseListener`  | Mouse events (enter, exit, click)    | Button hover effects          |
| `ItemListener`   | Combo box selection changes          | Course/student selection      |
| `WindowListener` | Window open/close events             | Save data on window close     |
| `KeyListener`    | Keyboard input events                | Input validation, shortcuts   |

---

## 6. BEST PRACTICES IMPLEMENTED

### 6.1 Lambda Expressions for Concise Code

```java
// Modern approach
addMenuItem(sidebar, "Home", e -> showDashboardHome());

// vs. Traditional approach
addMenuItem(sidebar, "Home", new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        showDashboardHome();
    }
});
```

### 6.2 SwingWorker for Long Operations

- Prevents UI freezing
- Provides progress updates
- Handles thread safety

### 6.3 Exception Handling in Event Handlers

- Catches exceptions at UI boundary
- Displays user-friendly messages
- Logs technical details

### 6.4 Event Handler Naming Convention

- `handleLogin()` - clear purpose
- `loadAvailableCourses()` - descriptive
- `registerForCourse()` - action-oriented

---

## 7. CONCLUSION

The Smart Campus Management System demonstrates professional event-driven programming practices:

✅ **Responsive UI**: Background processing prevents freezing  
✅ **User Feedback**: Visual cues for all interactions  
✅ **Error Handling**: Graceful exception handling in all event handlers  
✅ **Clean Architecture**: Separation between UI events and business logic  
✅ **Modern Practices**: Lambda expressions, SwingWorker, try-with-resources

The event-driven architecture makes the application interactive, responsive, and user-friendly while maintaining clean, maintainable code structure.

---

**Document Created:** November 6, 2025  
**Project:** Smart Campus Management System (SCMS)  
**Course:** BIT 2104 / BCS 2102 - Object-Oriented Programming (Java)  
**Question:** 3 - File I/O, GUI, and Event-Driven Programming
