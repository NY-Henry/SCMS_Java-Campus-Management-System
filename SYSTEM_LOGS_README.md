# System Logs Feature Documentation

## Overview
The System Logs feature provides comprehensive activity tracking and monitoring for the Smart Campus Management System (SCMS). It records all major user actions, authentication events, and system operations.

## Features

### 1. **Comprehensive Logging**
- User login/logout tracking
- Failed login attempts monitoring
- Record creation, updates, and deletions
- Report generation and data exports
- Profile updates
- Announcement management
- Payment processing
- Grade submissions

### 2. **Log Viewing Interface**
- **Table Display**: Shows all logs in a sortable, filterable table
- **Columns**: Log ID, Timestamp, User, Role, Action, Details, IP Address
- **Real-time Updates**: Refresh button to load latest logs
- **Double-click Details**: Click any log entry to view full details in a dialog

### 3. **Filtering & Search**
- **Action Filter**: Filter by action type (LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW, EXPORT, REGISTER)
- **Text Search**: Search across all columns for specific keywords
- **Clear Filters**: Reset all filters to view all logs
- **Live Filtering**: Results update as you type

### 4. **Statistics**
The "View Statistics" button provides:
- Total log entries
- Logs today
- Logs this week
- Most active user
- Most common action
- Logins today
- Failed login attempts
- Actions by role breakdown

### 5. **Export Functionality**
- Export filtered logs to CSV format
- Includes all visible columns
- Timestamped filenames for organization
- Saved in `reports/` directory

### 6. **Maintenance**
**Clear Old Logs** feature allows administrators to delete logs older than:
- Last 7 Days
- Last 30 Days
- Last 90 Days
- Last Year

This helps maintain database performance and manage storage.

## Usage

### Accessing System Logs
1. Log in as Administrator
2. Click "System Logs" in the sidebar menu
3. The logs panel will display all recorded activities

### Viewing Log Details
1. Double-click any row in the logs table
2. A dialog will appear showing full details including:
   - Log ID
   - Timestamp
   - User
   - Role
   - Action
   - IP Address
   - Full details text

### Filtering Logs
1. **By Action**: Select action type from dropdown (e.g., "LOGIN", "CREATE")
2. **By Search**: Type keywords in the search field
3. **Combined**: Use both filters together for precise results
4. Click "Clear Filters" to reset

### Exporting Logs
1. Apply desired filters (optional)
2. Click "Export to CSV" button
3. A success message will show the file location
4. File is saved as `reports/system_logs_[timestamp].csv`

### Viewing Statistics
1. Click "View Statistics" button
2. A dialog displays comprehensive system activity metrics
3. Review user activity patterns and system usage

### Clearing Old Logs
1. Click "Clear Old Logs" button
2. Select time period (7, 30, 90, or 365 days)
3. Confirm the deletion
4. Old logs are permanently removed

## Integration with Other Features

### LogService Class
The `LogService` class provides centralized logging functionality used throughout the application:

```java
LogService logService = new LogService();

// Log user login
logService.logLogin(userId, username, role);

// Log user logout
logService.logLogout(userId, username, role);

// Log failed login
logService.logFailedLogin(username);

// Log custom action
logService.logAction(userId, "ACTION_TYPE", "Action details");
```

### Automatic Logging
The following actions are automatically logged:
- ✅ User authentication (login/logout)
- ✅ Failed login attempts
- ⚠️ Profile updates (requires integration)
- ⚠️ Payment processing (requires integration)
- ⚠️ Grade submissions (requires integration)
- ⚠️ Announcements (requires integration)
- ⚠️ Student/Lecturer registration (requires integration)
- ⚠️ Report generation (requires integration)

**Note**: Items marked with ⚠️ require the respective features to call `LogService` methods.

## Database Schema

```sql
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
```

## Sample Log Data

To populate sample logs for testing, run:
```sql
source database/sample_logs.sql;
```

This will create:
- 25+ sample log entries
- Mix of different action types
- Logs from different user roles
- Logs spanning multiple days
- Failed login attempts

## Security Considerations

1. **IP Address Tracking**: Logs capture the IP address for security auditing
2. **User Accountability**: All actions are tied to user accounts
3. **Audit Trail**: Complete history of system activities
4. **Failed Login Monitoring**: Track potential security threats
5. **Data Retention**: Old logs can be purged for compliance

## Performance Tips

1. **Regular Cleanup**: Use "Clear Old Logs" monthly to maintain performance
2. **Indexed Columns**: `user_id` and `timestamp` are indexed for fast queries
3. **Filtered Exports**: Filter before exporting to reduce file size
4. **Pagination**: Table loads all logs but consider pagination for large datasets

## Future Enhancements

Potential improvements:
- [ ] Real-time log streaming
- [ ] Email alerts for critical actions
- [ ] Advanced analytics dashboard
- [ ] Log level severity (INFO, WARNING, ERROR)
- [ ] User activity heatmaps
- [ ] Suspicious activity detection
- [ ] Role-based log access control
- [ ] Log archiving to external storage

## Troubleshooting

### Logs Not Appearing
- Check database connection
- Verify `system_logs` table exists
- Click "Refresh" button
- Check for SQL errors in console

### Export Not Working
- Ensure `reports/` directory has write permissions
- Check available disk space
- Verify file path is accessible

### Slow Performance
- Clear old logs regularly
- Apply filters before loading
- Check database indexes
- Consider log archiving

## Related Classes

- `SystemLogsPanel.java` - Main UI component
- `LogService.java` - Logging service layer
- `LoginForm.java` - Integrated login/logout logging
- `AdminDashboard.java` - Integrated logout logging

## Access Level

- **Admin Only**: System Logs feature is only accessible to administrators
- **Full Access**: Admins can view, export, and delete logs
- **Audit Protection**: Logs cannot be edited, only viewed or deleted

---

**Last Updated**: November 5, 2025
**Version**: 1.0
**Maintained By**: SCMS Development Team
