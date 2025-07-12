package model.dao.user;

import model.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * DAO class for handling User-related database operations.
 */
public class UserDAO extends DBContext1 {

    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    /**
     * Get user by email.
     * Used for login, registration validation, forgot password, etc.
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM [User] WHERE email = ? AND isDelete = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.error("Error while getting user by email: " + email, e);
        }

        return null;
    }

    /**
     * Get user by userId (primary key).
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM [User] WHERE userId = ? AND isDelete = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.error("Error while getting user by ID: " + userId, e);
        }

        return null;
    }

    /**
     * Insert a new user (used for email-based signup or Google signup).
     */
    public boolean insertUser(User user) {
        String sql = "INSERT INTO [User] (fullName, email, password, phoneNumber, gender, googleId, avatarUrl, " + "provinceCode, districtCode, wardCode, addressDetail, resetPasswordToken, resetPasswordExpiry, " + "roleId, createdAt, updatedAt, isDelete) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE(), 0)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhoneNumber());
            ps.setObject(5, user.getGender());
            ps.setString(6, user.getGoogleId());
            ps.setString(7, user.getAvatarUrl());
            ps.setString(8, user.getProvinceCode());
            ps.setString(9, user.getDistrictCode());
            ps.setString(10, user.getWardCode());
            ps.setString(11, user.getAddressDetail());
            ps.setString(12, user.getResetPasswordToken());
            ps.setTimestamp(13, user.getResetPasswordExpiry());
            ps.setInt(14, user.getRoleId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.error("Error inserting user: " + user.getEmail(), e);
        }

        return false;
    }

    /**
     * Update user's resetPasswordToken and resetPasswordExpiry for forgot password flow.
     */
    public boolean updateResetPasswordToken(String email, String token, Timestamp expiry) {
        String sql = "UPDATE [User] SET resetPasswordToken = ?, resetPasswordExpiry = ?, updatedAt = GETDATE() " + "WHERE email = ? AND isDelete = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, expiry);
            ps.setString(3, email);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.error("Error updating reset token for email: " + email, e);
        }

        return false;
    }

    /**
     * Map a ResultSet row to a User entity object.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("userId"));
        u.setFullName(rs.getString("fullName"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setPhoneNumber(rs.getString("phoneNumber"));
        u.setGender(rs.getObject("gender") != null ? rs.getBoolean("gender") : null);
        u.setGoogleId(rs.getString("googleId"));
        u.setAvatarUrl(rs.getString("avatarUrl"));
        u.setProvinceCode(rs.getString("provinceCode"));
        u.setDistrictCode(rs.getString("districtCode"));
        u.setWardCode(rs.getString("wardCode"));
        u.setAddressDetail(rs.getString("addressDetail"));
        u.setResetPasswordToken(rs.getString("resetPasswordToken"));
        u.setResetPasswordExpiry(rs.getTimestamp("resetPasswordExpiry"));
        u.setRoleId(rs.getInt("roleId"));
        u.setCreatedAt(rs.getTimestamp("createdAt"));
        u.setUpdatedAt(rs.getTimestamp("updatedAt"));
        u.setDelete(rs.getBoolean("isDelete"));
        return u;
    }

    public void updateGoogleAcc(User user) throws SQLException {
        String sql = "UPDATE [User] SET googleId = ?, avatarUrl = ?, updatedAt = ? WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getGoogleId());
            stmt.setString(2, user.getAvatarUrl());
            stmt.setTimestamp(3, user.getUpdatedAt());
            stmt.setString(4, user.getEmail());  // Xác định user bằng email

            stmt.executeUpdate();
        }
    }

    public void updatePassword(User user) {
        String sql = "UPDATE [User] SET password = ?, updatedAt = ? WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getPassword()); // Mã hóa nếu cần
            stmt.setTimestamp(2, user.getUpdatedAt());
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the avatar URL of the user with the specified user ID.
     *
     * @param userId    the ID of the user to update
     * @param avatarUrl the new avatar URL to set
     * @return true if the update was successful, false otherwise
     */
    public boolean updateAvatar(int userId, String avatarUrl) {
        String sql = "UPDATE [User] SET avatarUrl = ?, updatedAt = GETDATE() WHERE userId = ? AND isDelete = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set parameters for the update
            ps.setString(1, avatarUrl);
            ps.setInt(2, userId);

            // Execute update and return result
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating avatar for userId=" + userId, e);
            return false;
        }
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE [dbo].[User]\n" +
                "   SET [fullName] = ?\n" +
                "      ,[email] = ?\n" +
                "      ,[phoneNumber] = ?\n" +
                "      ,[gender] = ?\n" +
                "      ,[googleId] = ?\n" +
                "      ,[avatarUrl] = ?\n" +
                "      ,[provinceCode] = ?\n" +
                "      ,[districtCode] = ?\n" +
                "      ,[wardCode] = ?\n" +
                "      ,[addressDetail] = ?\n" +
                "      ,[updatedAt] = ? WHERE userId = ? AND isDelete = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setObject(4, user.getGender());
            ps.setString(5, user.getGoogleId());
            ps.setString(6, user.getAvatarUrl());
            ps.setString(7, user.getProvinceCode());
            ps.setString(8, user.getDistrictCode());
            ps.setString(9, user.getWardCode());
            ps.setString(10, user.getAddressDetail());
            ps.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(12, user.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update the password for a user by their userId
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE [User] SET password = ?, updatedAt = GETDATE() WHERE userId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // Set new password and user ID
            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            // Execute update and check result
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Main method to test getUserByEmail() and DB connection.
     */
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail("admin@example.com");
        if (user != null) {
            System.out.println("User found: " + user.getFullName());
        } else {
            System.out.println("No user found.");
        }
    }
}
