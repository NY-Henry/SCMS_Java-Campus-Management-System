package models;

import java.time.LocalDateTime;

/**
 * Announcement class for system announcements
 */
public class Announcement {
    private int announcementId;
    private String title;
    private String content;
    private int postedBy;
    private String postedByName;
    private String targetAudience; // ALL, STUDENTS, LECTURERS, SPECIFIC_COURSE
    private Integer courseId;
    private String courseName;
    private LocalDateTime postedAt;
    private LocalDateTime expiresAt;
    private boolean isActive;

    // Constructors
    public Announcement() {
    }

    public Announcement(String title, String content, int postedBy, String targetAudience) {
        this.title = title;
        this.content = content;
        this.postedBy = postedBy;
        this.targetAudience = targetAudience;
        this.postedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Business logic methods
    public boolean isExpired() {
        if (expiresAt == null)
            return false;
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isVisibleTo(String role) {
        if (!isActive || isExpired())
            return false;

        switch (targetAudience) {
            case "ALL":
                return true;
            case "STUDENTS":
                return "STUDENT".equals(role);
            case "LECTURERS":
                return "LECTURER".equals(role);
            default:
                return false;
        }
    }

    // Getters and Setters
    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(int postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostedByName() {
        return postedByName;
    }

    public void setPostedByName(String postedByName) {
        this.postedByName = postedByName;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "title='" + title + '\'' +
                ", audience=" + targetAudience +
                ", posted=" + postedAt +
                '}';
    }
}
