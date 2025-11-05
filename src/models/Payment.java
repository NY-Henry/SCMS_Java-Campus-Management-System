package models;

import java.time.LocalDate;

/**
 * Payment model - Represents a student fee payment
 */
public class Payment {
    private int paymentId;
    private int studentId;
    private String studentName;
    private String registrationNumber;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod; // CASH, BANK_TRANSFER, MOBILE_MONEY, CARD
    private String referenceNumber;
    private String purpose;
    private String academicYear;
    private int semester;
    private int processedBy;
    private String processedByName;

    // Constructors
    public Payment() {
    }

    public Payment(int studentId, double amount, LocalDate paymentDate,
            String paymentMethod, String referenceNumber, String purpose,
            String academicYear, int semester, int processedBy) {
        this.studentId = studentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
        this.purpose = purpose;
        this.academicYear = academicYear;
        this.semester = semester;
        this.processedBy = processedBy;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(int processedBy) {
        this.processedBy = processedBy;
    }

    public String getProcessedByName() {
        return processedByName;
    }

    public void setProcessedByName(String processedByName) {
        this.processedByName = processedByName;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", semester=" + semester +
                '}';
    }

    /**
     * Format amount as currency
     */
    public String getFormattedAmount() {
        return String.format("UGX %.2f", amount);
    }

    /**
     * Get display string for payment method
     */
    public String getPaymentMethodDisplay() {
        if (paymentMethod == null)
            return "";
        return paymentMethod.replace("_", " ");
    }
}
