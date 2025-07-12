package controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.entity.District;
import model.entity.Province;
import model.entity.User;
import model.entity.Ward;
import proxy.CloudinaryProxy;
import service.user.LocationService;
import service.user.UserService;

import java.io.*;
import java.util.*;


@MultipartConfig
@WebServlet(name = "UserServlet", urlPatterns = {"/update-profile", "/change-password", "/change-avatar", "/mark-delete-avatar", "/clear-temp-avatar"})
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=notLoggedIn");
            return;
        }

        if (uri.endsWith("/update-profile")) {
            Object allowResetPassword = session.getAttribute("allowResetPassword");
            if (allowResetPassword != null) {
                session.removeAttribute("allowResetPassword");
            }
            handleLoadProfile(request, response, user);
            return;
        }

        if (uri.endsWith("/change-password")) {
            request.getRequestDispatcher("/WEB-INF/view/user/change-password.jsp").forward(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=notLoggedIn");
            return;
        }

        if (uri.endsWith("/update-profile")) {
            handleUpdateProfile(request, response);
            return;
        }

        if (uri.endsWith("/change-password")) {
            handleChangePassword(request, response);
        }

        if (uri.endsWith("/change-avatar")) {
            System.out.println("UserServlet change-avatar");
            handleChangeAvatar(request, response);
        }

        if (uri.endsWith("/mark-delete-avatar")) {
            System.out.println("UserServlet Customize Toolbar…");
            handleMarkDeleteAvatar(request, response);
        }

        if (uri.endsWith("/clear-temp-avatar")) {
            handleClearTempAvatar(request, response);
            return;
        }
    }

    private void handleLoadProfile(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        System.out.println("-------------handleLoadProfile-------------");

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        System.out.println("user: " + user.toString());
        LocationService locationService = new LocationService();

        List<Province> provinces = locationService.getAllProvinces();
        List<District> districts = locationService.getDistrictsByProvince(user.getProvinceCode());
        List<Ward> wards = locationService.getWardsByDistrict(user.getDistrictCode());

        request.setAttribute("user", user);
        request.setAttribute("provinces", provinces);
        request.setAttribute("districts", districts);
        request.setAttribute("wards", wards);

//        request.getRequestDispatcher("/WEB-INF/view/user/update-profile.jsp").forward(request, response);
        request.getRequestDispatcher("/WEB-INF/view/user/update-profile.jsp").forward(request, response);
    }

    private void handleChangeAvatar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("-------------handleChangeAvatar-------------");

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("/WEB-INF/view/user/login.jsp");
            return;
        }
        boolean hasExistingAvatar = currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty();

        // Ensure we can receive multipart/form-data
        request.setCharacterEncoding("UTF-8");

        // Get file part from the request
        Part filePart = request.getPart("avatar");
        System.out.println("Part name: " + filePart.getName());
        System.out.println("Submitted file name: " + filePart.getSubmittedFileName());

        // Upload to Cloudinary
        CloudinaryProxy cloudinaryProxy = new CloudinaryProxy();
//        String uploadedUrl = cloudinaryProxy.uploadAvatar(filePart, currentUser.getUserId());
        String uploadedUrl = cloudinaryProxy.uploadAvatar(filePart, currentUser.getUserId(), session, hasExistingAvatar);
        System.out.println("uploaded url: " + uploadedUrl);

        // Return the uploaded image URL as plain text
        response.setContentType("text/plain");
        response.getWriter().write(uploadedUrl);
    }

    private void handleMarkDeleteAvatar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("-------------handleMarkDeleteAvatar-------------");

        // Set encoding để nhận UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // B1: Nhận avatarUrl hiện tại từ request
        String avatarUrl = request.getParameter("avatarUrl");
        System.out.println("Avatar marked for deletion: " + avatarUrl);

        // B2: Lưu tạm vào session để xử lý sau khi user bấm "Save"
        HttpSession session = request.getSession();
        session.setAttribute("pendingDeletedAvatar", avatarUrl);

        // B3: Trả về avatar mặc định để cập nhật UI
        String returnedUrl = "";

        response.setContentType("text/plain");
        response.getWriter().write(returnedUrl);
    }

    /**
     * Handles updating user profile information.
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("-------------handleUpdateProfile-------------");
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("/WEB-INF/view/user/login.jsp");
            return;
        }

        CloudinaryProxy cloudinaryProxy = new CloudinaryProxy();

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Step 1: Extract all form parameters
        String avatarUrl = request.getParameter("avatarUrl");
        System.out.println("avatarUrl: " + avatarUrl);
        String fullName = request.getParameter("fullName");
        String genderStr = request.getParameter("gender");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String provinceCode = request.getParameter("provinceCode");
        String districtCode = request.getParameter("districtCode");
        String wardCode = request.getParameter("wardCode");
        String addressDetail = request.getParameter("addressDetail");

        // Step 2: Validate input values
        UserService userService = new UserService();
        Map<String, String> errors = new HashMap<>();

        if (!fullName.equalsIgnoreCase(currentUser.getFullName()) && userService.validateFullName(fullName) != null) {
            Map<String, String> fullNameErrors = userService.validateFullName(fullName);
            errors.putAll(fullNameErrors);
        }
        if (userService.validatePhoneNumber(phoneNumber) != null) {
            Map<String, String> phoneNumberErrors = userService.validatePhoneNumber(phoneNumber);
            errors.putAll(phoneNumberErrors);
        }

        // Step 3: Handle validation failure
        if (!errors.isEmpty()) {
            User tempUser = new User();
            tempUser.setUserId(currentUser.getUserId());
            tempUser.setPassword(currentUser.getPassword());
            tempUser.setGoogleId(currentUser.getGoogleId());

            tempUser.setAvatarUrl(avatarUrl);
            tempUser.setFullName(fullName);
            tempUser.setGender("true".equals(genderStr));
            tempUser.setPhoneNumber(phoneNumber);
            tempUser.setEmail(email);
            tempUser.setProvinceCode(provinceCode);
            tempUser.setDistrictCode(districtCode);
            tempUser.setWardCode(wardCode);
            tempUser.setAddressDetail(addressDetail);

            LocationService locationService = new LocationService();
            List<Province> provinces = locationService.getAllProvinces();
            List<District> districts = locationService.getDistrictsByProvince(provinceCode);
            List<Ward> wards = locationService.getWardsByDistrict(districtCode);
            request.setAttribute("provinces", provinces);
            request.setAttribute("districts", districts);
            request.setAttribute("wards", wards);

            request.setAttribute("errors", errors);
            request.setAttribute("user", tempUser);
            request.getRequestDispatcher("update-profile.jsp").forward(request, response);
            return;
        }
        System.out.println("valid");

        // Step 4: If valid → update user object
        if (!email.equalsIgnoreCase(currentUser.getEmail())) {
            currentUser.setGoogleId(""); // reset liên kết Google nếu đổi email
        }

        // Step 4.1: If avatar was deleted by user, remove from Cloudinary
        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty() && (avatarUrl == null || avatarUrl.isEmpty())) {
            System.out.println("🧹 Avatar was deleted. Proceed to remove old image from Cloudinary");
            try {
                cloudinaryProxy.deleteImage(currentUser.getAvatarUrl());
                System.out.println("✅ Old avatar removed from Cloudinary");
            } catch (Exception e) {
                System.err.println("❌ Failed to delete old avatar from Cloudinary");
                e.printStackTrace();
            }
        }

        if (session.getAttribute("pendingAvatarUrl") != null && currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
            String pendingAvatarUrl = (String) session.getAttribute("pendingAvatarUrl");
            String originalAvatarUrl = currentUser.getAvatarUrl();
            System.out.println("pendingAvatarUrl: " + pendingAvatarUrl);
            System.out.println("originalAvatarUrl: " + originalAvatarUrl);
            if (!pendingAvatarUrl.equals(originalAvatarUrl)) {
                avatarUrl = cloudinaryProxy.renameAvatarUrl(pendingAvatarUrl, originalAvatarUrl);
            }
        }

        currentUser.setAvatarUrl(avatarUrl);
        currentUser.setFullName(fullName);
        currentUser.setGender("true".equals(genderStr));
        currentUser.setPhoneNumber(phoneNumber);
        currentUser.setEmail(email);
        currentUser.setProvinceCode(provinceCode);
        currentUser.setDistrictCode(districtCode);
        currentUser.setWardCode(wardCode);
        currentUser.setAddressDetail(addressDetail);

        // Step 5: Persist update to database
        userService.updateProfile(currentUser);

        // Reload fresh user from DB
        User updatedUser = userService.findById(currentUser.getUserId());
        session.setAttribute("user", updatedUser);

        // Step 6: Forward back to profile page
        request.setAttribute("success", true);
        session.setAttribute("successMessage", "Profile updated successfully!");
        response.sendRedirect("update-profile");
    }

    private void handleClearTempAvatar(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("-------------handleClearTempAvatar-------------");
        HttpSession session = request.getSession(false);
        if (session != null) {
            String pendingAvatarUrl = (String) session.getAttribute("pendingAvatarUrl");
            if (pendingAvatarUrl != null) {
                CloudinaryProxy cloudinaryProxy = new CloudinaryProxy();
                try {
                    cloudinaryProxy.deleteImage(pendingAvatarUrl);
                    System.out.println("✅ Old avatar removed from Cloudinary");
                } catch (Exception e) {
                    System.err.println("❌ Failed to delete old avatar from Cloudinary");
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("-------------handleChangePassword-------------");
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Set attributes to preserve entered values
        request.setAttribute("currentPassword", currentPassword);


        boolean allowResetPassword = Boolean.TRUE.equals(session.getAttribute("allowResetPassword"));
        boolean hasPasswordInDb = currentUser.getPassword() != null && !currentUser.getPassword().isEmpty();
        boolean hasError = false;

        // ===== 1. Validate current password =====
        if (!allowResetPassword && hasPasswordInDb) {
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                request.setAttribute("currentPasswordError", "Current password is required.");
                hasError = true;
            } else if (!currentPassword.equals(currentUser.getPassword())) {
                request.setAttribute("currentPasswordError", "Current password is incorrect.");
                hasError = true;
            }

            if (hasError) {
                request.getRequestDispatcher("/WEB-INF/view/user/change-password.jsp").forward(request, response);
                return;
            }

            request.setAttribute("newPassword", newPassword);
            request.setAttribute("confirmPassword", confirmPassword);
        }

        // ===== 2. Validate new password =====
        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("newPasswordError", "New password is required.");
            hasError = true;
        } else {
            if (hasPasswordInDb && newPassword.equals(currentUser.getPassword())) {
                request.setAttribute("newPasswordError", "New password must be different from current password.");
                hasError = true;
            }

            Map<String, Boolean> rules = new LinkedHashMap<>();
            rules.put("noSpaces", !newPassword.trim().isEmpty() && !newPassword.contains(" "));
            rules.put("lengthMin", newPassword.length() >= 6);
            rules.put("hasLower", newPassword.matches(".*[a-z].*"));
            rules.put("hasUpper", newPassword.matches(".*[A-Z].*"));
            rules.put("hasDigit", newPassword.matches(".*\\d.*"));
            rules.put("hasSpecial", newPassword.matches(".*[^a-zA-Z0-9].*"));

            for (Map.Entry<String, Boolean> rule : rules.entrySet()) {
                request.setAttribute(rule.getKey(), rule.getValue());
                if (!rule.getValue()) hasError = true;
            }
        }

        // ===== 2. Validate confirm new password =====
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("confirmPasswordError", "Confirm password is required.");
            hasError = true;
        }

        // ===== 4. Confirm password match =====
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("confirmPasswordError", "Confirm password is required.");
            hasError = true;
        } else if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("confirmPasswordError", "Passwords do not match.");
            hasError = true;
        }

        // ===== 4. Handle error =====
        if (hasError) {
            request.getRequestDispatcher("/WEB-INF/view/user/change-password.jsp").forward(request, response);
            return;
        }

        // ===== 5. Update password =====
        // String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        currentUser.setPassword(newPassword);
        new UserService().updatePassword(currentUser.getUserId(), newPassword);

        session.setAttribute("user", currentUser);
        session.setAttribute("successMessage", "Password changed successfully.");

        // Reset flags & temp attributes
        session.removeAttribute("allowResetPassword");
        request.removeAttribute("currentPassword");
        request.removeAttribute("newPassword");
        request.removeAttribute("confirmPassword");

//        response.sendRedirect(request.getContextPath() + "/change-password");
        request.getRequestDispatcher("/WEB-INF/view/user/change-password.jsp").forward(request, response);
    }

}