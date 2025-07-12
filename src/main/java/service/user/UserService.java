package service.user;

import model.dao.user.UserDAO;
import model.entity.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public static boolean isEmailValid(String email) {
        if (email == null || email.isBlank()) {
            return false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }
        return true;
    }

    /**
     * Find a user by email
     *
     * @param email The email address to search for
     * @return User object if found, otherwise null
     */
    public User findUserByEmail(String email) {
        // Delegate the database query to UserDAO
        return userDAO.getUserByEmail(email);
    }

    /**
     * Validates user registration input data.
     *
     * @param fullName        Full name of the user
     * @param email           Email address
     * @param password        Password
     * @param confirmPassword Password confirmation
     * @return A map of field -> error message if validation fails; empty map if valid
     */
    public Map<String, String> validateRegisterData(String fullName, String email, String password, String confirmPassword) {
        Map<String, String> errors = new HashMap<>();

        // Regex for full name (no digits or special characters, only letters and spaces)
        String fullNameRegex = "^[a-zA-ZÀ-ỹ\\s]+$";

        // Regex for password (at least one digit, one special char, one uppercase, 6-50 chars)
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,50}$";

        // Validate full name
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (fullName.length() > 50) {
            errors.put("fullName", "Full name must not exceed 50 characters.");
        } else if (!fullName.matches(fullNameRegex)) {
            errors.put("fullName", "Full name must not contain numbers or special characters.");
        }

        // Validate email
//        if (email == null || email.trim().isEmpty()) {
//            errors.put("email", "Email is required.");
//        } else if (!email.matches("/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$/")) {
//            errors.put("email", "Invalid email format.");
//        }
        if (email == null || email.isBlank()) {
            errors.put("email", "Email is required");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("email", "Invalid email format");
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password is required.");
        } else if (!password.matches(passwordRegex)) {
            errors.put("password", "Password must contain at least 1 uppercase letter, 1 digit, 1 special character, and be 6–50 characters long.");
        }

        // Confirm password
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            errors.put("confirmPassword", "Please confirm your password.");
        } else if (!confirmPassword.equals(password)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }

        return errors;
    }

    public void updateGoogleAcc(User user) throws SQLException {
        userDAO.updateGoogleAcc(user);
    }

    public void updatePassword(User user) {
        userDAO.updatePassword(user);
    }

    public boolean updateProfile(User user) {
        return userDAO.updateProfile(user);
    }

    /**
     * Updates the avatar URL for the user with the given ID.
     *
     * @param userId    the ID of the user whose avatar should be updated
     * @param avatarUrl the new avatar image URL to set
     * @return true if update is successful, false otherwise
     */
    public boolean updateAvatar(int userId, String avatarUrl) {
        // Call DAO method to perform the update
        return userDAO.updateAvatar(userId, avatarUrl);
    }

    /**
     * Validates that the full name only contains letters and spaces.
     * Disallows digits and special characters.
     */
//    public boolean isValidFullName(String fullName) {
//        if (fullName == null || fullName.trim().isEmpty()) return false;
//
//        // Only letters (including accents) and spaces allowed
//        return fullName.matches("^[\\p{L} ]+$");
//    }
    public Map<String, String> validateFullName(String fullName) {
        Map<String, String> errors = new HashMap<>();
        String fullNameRegex = "^[\\p{L} ]+$";

        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("errror", "Full name is required.");
        } else if (fullName.length() > 50) {
            errors.put("errror", "Full name must not exceed 50 characters.");
        } else if (!fullName.matches(fullNameRegex)) {
            errors.put("error", "Full name must not contain numbers or special characters.");
        }

        return errors;
    }

    /**
     * Validates the phone number to ensure it starts with '0' and has exactly 10 digits.
     */
    public Map<String, String> validatePhoneNumber(String phoneNumber) {
        Map<String, String> errors = new HashMap<>();

        phoneNumber = phoneNumber.trim();

        if (phoneNumber.trim().isEmpty()) {
            return errors;
        }
        if (!phoneNumber.matches("^\\d{10}$")) {
            errors.put("error1", "Phone number must be exactly 10 digits.");
        }
        if (!phoneNumber.startsWith("0")) {
            errors.put("error2", "Phone number must start with 0.");
        }

        return errors;
    }


    /**
     * Finds a user from the database by their ID.
     */
    public User findById(int userId) {
        return userDAO.getUserById(userId);
    }

    public void updatePassword(int userId, String newPassword) {
        new UserDAO().updatePassword(userId, newPassword);
    }

    // Login by email and password
    public User loginWithEmail(String email, String password) {
        // Get user from database
        User user = userDAO.getUserByEmail(email);

        // If user not found
        if (user == null) return null;

        // Compare plain password (add hashing later)
        if (!password.equals(user.getPassword())) return null;

        return user;
    }

    /**
     * Register a new user using Google account information.
     * If the email already exists, return the existing user instead.
     *
     * @param googleUser The user information from Google
     * @return The existing user or newly registered Google user
     */
    public User registerGoogleUser(User googleUser) {
        // Check if email already exists
        User existingUser = userDAO.getUserByEmail(googleUser.getEmail());

        // If exists, return the existing user
        if (existingUser != null) {
            return existingUser;
        }

        // Set default values for Google login
        googleUser.setPassword(null); // No password
        googleUser.setRoleId(1); // Default role: Customer
        googleUser.setDelete(false);
        googleUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        googleUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // Insert user using DAO
        boolean success = userDAO.insertUser(googleUser);

        // If insert successful, retrieve inserted user to return
        if (success) {
            return userDAO.getUserByEmail(googleUser.getEmail());
        }

        return null;
    }

    /**
     * Creates a new user by calling DAO layer
     *
     * @param user User object with registration data
     * @return true if insert successful, false otherwise
     */
    public boolean registerUser(User user) {
        return userDAO.insertUser(user);
    }

}

