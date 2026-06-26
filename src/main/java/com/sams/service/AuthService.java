package com.sams.service;

import com.sams.dao.UserDAO;
import com.sams.model.User;

import java.sql.SQLException;

/**
 * SERVICE LAYER — authentication and current-session user.
 */
public class AuthService {

    private static AuthService instance;
    private User loggedInUser;

    private final UserDAO userDAO = new UserDAO();

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    /**
     * Attempt login. Returns true on success; call getLoggedInUser() afterwards.
     */
    public boolean login(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        User user = userDAO.login(username.trim(), password.trim());
        if (user != null) {
            loggedInUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isAdmin() {
        return loggedInUser != null && loggedInUser.getRole() == User.Role.ADMIN;
    }

    public boolean isLecturer() {
        return loggedInUser != null && loggedInUser.getRole() == User.Role.LECTURER;
    }
}
