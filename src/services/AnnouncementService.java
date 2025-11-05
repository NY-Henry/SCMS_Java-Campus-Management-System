package services;

import database.MySQLDatabase;
import models.Announcement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing announcements
 */
public class AnnouncementService {
    private final MySQLDatabase db;

    public AnnouncementService(MySQLDatabase db) {
        this.db = db;
    }

    /**
     * Posts a new announcement
     */
    public boolean postAnnouncement(String title, String content, int postedBy,
            String targetAudience, Integer courseId,
            Timestamp expiresAt) {
        if (!db.isConnected()) {
            db.connect();
        }

        String sql = "INSERT INTO announcements (title, content, posted_by, target_audience, course_id, expires_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Object[] params = { title, content, postedBy, targetAudience, courseId, expiresAt };
        return db.executePreparedQuery(sql, params);
    }

    /**
     * Gets all announcements for students (ALL and STUDENTS targeted)
     */
    public List<Announcement> getAnnouncementsForStudents() {
        if (!db.isConnected()) {
            db.connect();
        }

        String sql = "SELECT a.announcement_id, a.title, a.content, a.posted_by, " +
                "CONCAT(p.first_name, ' ', p.last_name) as posted_by_name, " +
                "a.target_audience, a.course_id, c.course_name, " +
                "a.posted_at, a.expires_at, a.is_active " +
                "FROM announcements a " +
                "JOIN users u ON a.posted_by = u.user_id " +
                "JOIN persons p ON u.person_id = p.person_id " +
                "LEFT JOIN courses c ON a.course_id = c.course_id " +
                "WHERE a.is_active = TRUE " +
                "AND (a.target_audience = 'ALL' OR a.target_audience = 'STUDENTS') " +
                "AND (a.expires_at IS NULL OR a.expires_at > NOW()) " +
                "ORDER BY a.posted_at DESC";

        System.out.println("DEBUG: Fetching announcements for STUDENTS");
        List<Announcement> result = executeAnnouncementQuery(sql, null);
        System.out.println("DEBUG: Found " + result.size() + " announcements for students");
        return result;
    }

    /**
     * Gets all announcements for lecturers (ALL and LECTURERS targeted)
     */
    public List<Announcement> getAnnouncementsForLecturers() {
        if (!db.isConnected()) {
            db.connect();
        }

        String sql = "SELECT a.announcement_id, a.title, a.content, a.posted_by, " +
                "CONCAT(p.first_name, ' ', p.last_name) as posted_by_name, " +
                "a.target_audience, a.course_id, c.course_name, " +
                "a.posted_at, a.expires_at, a.is_active " +
                "FROM announcements a " +
                "JOIN users u ON a.posted_by = u.user_id " +
                "JOIN persons p ON u.person_id = p.person_id " +
                "LEFT JOIN courses c ON a.course_id = c.course_id " +
                "WHERE a.is_active = TRUE " +
                "AND (a.target_audience = 'ALL' OR a.target_audience = 'LECTURERS') " +
                "AND (a.expires_at IS NULL OR a.expires_at > NOW()) " +
                "ORDER BY a.posted_at DESC";

        System.out.println("DEBUG: Fetching announcements for LECTURERS");
        List<Announcement> result = executeAnnouncementQuery(sql, null);
        System.out.println("DEBUG: Found " + result.size() + " announcements for lecturers");
        return result;
    }

    /**
     * Gets announcements for a specific course
     */
    public List<Announcement> getAnnouncementsByCourse(int courseId) {
        if (!db.isConnected()) {
            db.connect();
        }

        String sql = "SELECT a.announcement_id, a.title, a.content, a.posted_by, " +
                "CONCAT(p.first_name, ' ', p.last_name) as posted_by_name, " +
                "a.target_audience, a.course_id, c.course_name, " +
                "a.posted_at, a.expires_at, a.is_active " +
                "FROM announcements a " +
                "JOIN users u ON a.posted_by = u.user_id " +
                "JOIN persons p ON u.person_id = p.person_id " +
                "LEFT JOIN courses c ON a.course_id = c.course_id " +
                "WHERE a.is_active = TRUE " +
                "AND a.target_audience = 'SPECIFIC_COURSE' " +
                "AND a.course_id = ? " +
                "AND (a.expires_at IS NULL OR a.expires_at > NOW()) " +
                "ORDER BY a.posted_at DESC";

        Object[] params = { courseId };
        return executeAnnouncementQuery(sql, params);
    }

    /**
     * Gets all announcements posted by a specific lecturer
     */
    public List<Announcement> getAnnouncementsByLecturer(int lecturerId) {
        if (!db.isConnected()) {
            db.connect();
        }

        String sql = "SELECT a.announcement_id, a.title, a.content, a.posted_by, " +
                "CONCAT(p.first_name, ' ', p.last_name) as posted_by_name, " +
                "a.target_audience, a.course_id, c.course_name, " +
                "a.posted_at, a.expires_at, a.is_active " +
                "FROM announcements a " +
                "JOIN users u ON a.posted_by = u.user_id " +
                "JOIN persons p ON u.person_id = p.person_id " +
                "LEFT JOIN courses c ON a.course_id = c.course_id " +
                "WHERE a.posted_by = ? " +
                "ORDER BY a.posted_at DESC";

        Object[] params = { lecturerId };
        return executeAnnouncementQuery(sql, params);
    }

    /**
     * Deactivates an announcement
     */
    public boolean deactivateAnnouncement(int announcementId) {
        if (!db.isConnected()) {
            db.connect();
        }

        String sql = "UPDATE announcements SET is_active = FALSE WHERE announcement_id = ?";
        Object[] params = { announcementId };
        return db.executePreparedQuery(sql, params);
    }

    /**
     * Helper method to execute announcement queries and map results
     */
    private List<Announcement> executeAnnouncementQuery(String sql, Object[] params) {
        List<Announcement> announcements = new ArrayList<>();

        try {
            System.out.println("DEBUG: Executing query: " + sql);
            ResultSet rs = params != null ? db.executePreparedSelect(sql, params)
                    : db.executePreparedSelect(sql, new Object[] {});

            if (rs != null) {
                System.out.println("DEBUG: ResultSet is not null");
                int count = 0;
                while (rs.next()) {
                    count++;
                    Announcement announcement = new Announcement();
                    announcement.setAnnouncementId(rs.getInt("announcement_id"));
                    announcement.setTitle(rs.getString("title"));
                    announcement.setContent(rs.getString("content"));
                    announcement.setPostedBy(rs.getInt("posted_by"));
                    announcement.setPostedByName(rs.getString("posted_by_name"));
                    announcement.setTargetAudience(rs.getString("target_audience"));

                    // Handle nullable courseId
                    if (rs.getObject("course_id") != null) {
                        announcement.setCourseId(rs.getInt("course_id"));
                    }

                    announcement.setCourseName(rs.getString("course_name"));

                    // Convert Timestamp to LocalDateTime
                    Timestamp postedAt = rs.getTimestamp("posted_at");
                    if (postedAt != null) {
                        announcement.setPostedAt(postedAt.toLocalDateTime());
                    }

                    // Handle nullable expiresAt
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    if (expiresAt != null) {
                        announcement.setExpiresAt(expiresAt.toLocalDateTime());
                    }

                    announcement.setActive(rs.getBoolean("is_active"));

                    announcements.add(announcement);
                    System.out.println("DEBUG: Added announcement: " + announcement.getTitle());
                }
                System.out.println("DEBUG: Processed " + count + " rows from ResultSet");
                rs.close();
            } else {
                System.out.println("DEBUG: ResultSet is NULL!");
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Error in executeAnnouncementQuery:");
            e.printStackTrace();
        }

        return announcements;
    }
}
