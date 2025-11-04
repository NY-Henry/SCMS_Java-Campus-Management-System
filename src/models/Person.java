package models;

import java.time.LocalDate;

/**
 * Abstract base class representing a Person in the system
 * Demonstrates Abstraction and Encapsulation
 */
public abstract class Person {
    // Private fields - Encapsulation
    private int personId;
    private int userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String address;
    private String profilePicturePath;

    // Constructors
    public Person() {
    }

    public Person(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    // Abstract method - must be implemented by subclasses (Abstraction)
    public abstract String getRole();

    // Abstract method for displaying specific information
    public abstract void displayInfo();

    // Common method - Polymorphism (can be overridden)
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and Setters - Encapsulation
    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    @Override
    public String toString() {
        return "Person{" +
                "personId=" + personId +
                ", name='" + getFullName() + '\'' +
                ", phone='" + phoneNumber + '\'' +
                '}';
    }
}
