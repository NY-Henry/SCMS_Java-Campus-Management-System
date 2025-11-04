package utils;

import models.Person;

/**
 * Singleton class to manage user session
 * Stores the currently logged-in user's information
 */
public class SessionManager {
    private static SessionManager instance;
    private Person currentUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(Person user) {
        this.currentUser = user;
    }

    public Person getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public void logout() {
        currentUser = null;
    }
}
