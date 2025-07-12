package controller.user;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.entity.User;
import proxy.EmailProxy;
import service.user.EmailService;
import service.user.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AuthenServlet", urlPatterns = {"/login-with-email", "/login", "/login-with-google", "/register-account", "/register-with-email", "/register-with-google", "/register", "/verify-otp", "/set-password-request", "/reset-password", "/logout", "/send-otp", "/check-email"})
public class AuthenServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.endsWith("/login")) {
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
            return;
        }

        if (uri.endsWith("/login-with-google")) {
            request.getRequestDispatcher("/google-login").forward(request, response);
            return;
        }

        if (uri.endsWith("/register-account") || uri.endsWith("/register")) {
            request.getRequestDispatcher("/WEB-INF/view/user/register-account.jsp").forward(request, response);
            return;
        }

        if (uri.endsWith("/register-with-google")) {
            request.getRequestDispatcher("/google-register").forward(request, response);
            return;
        }

        if (uri.endsWith("/reset-password")) {
            request.getRequestDispatcher("/WEB-INF/view/user/reset-password.jsp").forward(request, response);
        }

        if (uri.endsWith("/logout")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/home");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.endsWith("/login-with-email")) {
            handleLoginWithEmail(request, response);
            return;
        }

        if (uri.endsWith("/register-with-email")) {
            handleRegisterWithEmail(request, response);
            return;
        }

        if (uri.endsWith("/verify-otp")) {
            handleVerifyOtp(request, response);
        }

        if (uri.endsWith("/reset-password")) {
            handleResetPassword(request, response);
            return;
        }

        if (uri.endsWith("/check-email")) {
            handleCheckEmail(request, response);
        }

        if (uri.endsWith("/send-otp")) {
            handleSendOtp(request, response);
        }

    }

    /**
     * Handles login using email and password.
     * Supports fallback for Google-registered accounts without password.
     */
    private void handleLoginWithEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = new UserService();

        // Step 1: Get email and password from request
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Step 2: Look up user by email
        User user = userService.findUserByEmail(email);

        // Step 3: If no such user exists, return with error
        if (user == null) {
            request.setAttribute("email", email);
            request.setAttribute("error", "Account does not exist.");
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
            return;
        }

        // Step 4: If user was created via Google and has no password
        if (user.getGoogleId() != null && user.getPassword() == null) {
            request.setAttribute("email", email);
            request.setAttribute("googleLinked", true); // Used to trigger UI for setting password
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
            return;
        }

        // Step 5: Proceed with normal email/password login
        User authenticatedUser = userService.loginWithEmail(email, password);

        if (authenticatedUser != null) {
            request.getSession().setAttribute("user", authenticatedUser);
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            request.setAttribute("email", email);
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        }
    }

    private void handleRegisterWithEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("-------------handleRegisterWithEmail-------------");
        HttpSession session = request.getSession();
        UserService userService = new UserService();

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        System.out.println("fullName: " + fullName);
        System.out.println("email: " + email);
        System.out.println("password: " + password);
        System.out.println("confirmPassword: " + confirmPassword);

        Map<String, String> errors = userService.validateRegisterData(fullName, email, password, confirmPassword);

        // Check email exists
        if (userService.findUserByEmail(email) != null) {
            errors.put("email", "Email is already registered");
        }

        if (!errors.isEmpty()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", false);
            jsonResponse.put("errors", errors);

            response.getWriter().write(new Gson().toJson(jsonResponse));
            return;
        }

        // Store data in session
        session.setAttribute("otpPurpose", "register");
        session.setAttribute("otpEmail", email);
        session.setAttribute("registerFullName", fullName);
        session.setAttribute("registerPassword", password);

        // Ready for client to call /send-otp
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true}");
    }

    private void handleSendOtp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("-------------handleSendOtp-------------");
        HttpSession session = request.getSession();

        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            System.out.println(user);
        } else {
            System.out.println("User is null");
        }

//        response.setContentType("application/json");
        PrintWriter out = response.getWriter();


        // Step 1: Get and validate otpPurpose
        String otpPurpose = request.getParameter("otpPurpose");
        System.out.println("otpPurpose: " + otpPurpose);
        if (otpPurpose == null || otpPurpose.isBlank()) {
            out.write("{\"success\": false, \"error\": \"OTP purpose is required.\"}");
            return;
        }
        otpPurpose = otpPurpose.trim();
        session.setAttribute("otpPurpose", otpPurpose);
        System.out.println("otpPurpose: " + otpPurpose);

        // Step 2: Determine email source
        String email = request.getParameter("email");
        UserService userService = new UserService();

        // Case 3: Logged-in user changing password → no email in request, use session
        if ("resetPassword".equals(otpPurpose) && (email == null || email.isBlank())) {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
                out.write("{\"success\": false, \"error\": \"Email not found in session.\"}");
                return;
            }
            email = user.getEmail();
            System.out.println("Email from session: " + email);
        }

        // Case 1, 2, 4: Email provided in request (register, forgot when not logged in, verify email)
        if (email != null && !email.isBlank()) {
            email = email.trim().toLowerCase();
            System.out.println("Email from request: " + email);

            // Validate email format
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!email.matches(emailRegex)) {
                out.write("{\"success\": false, \"error\": \"Invalid email format.\"}");
                return;
            }

            // For register or verifyNewEmail, check that email is NOT in use
            if ("register".equals(otpPurpose) || "verifyNewEmail".equals(otpPurpose)) {
                User existing = userService.findUserByEmail(email);
                if (existing != null) {
                    out.write("{\"success\": false, \"error\": \"Email already in use.\"}");
                    return;
                }
            }

            // For resetPassword (forgot password), check that email EXISTS
            if ("resetPassword".equals(otpPurpose) && (request.getParameter("email") != null)) {
                User user = userService.findUserByEmail(email);
                if (user == null) {
                    out.write("{\"success\": false, \"error\": \"Email not found.\"}");
                    return;
                }
            }
        }

        // Final email check
        if (email == null || email.isBlank()) {
            out.write("{\"success\": false, \"error\": \"Email is required.\"}");
            return;
        }

        // Step 3: Generate OTP
        EmailService emailService = new EmailService();
        EmailProxy emailProxy = new EmailProxy();
        String otp = emailService.generateOtpCode();
        System.out.println("Generated OTP: " + otp);

        // Step 4: Send OTP email
        boolean sent = emailProxy.sendOtpEmail(email, otp, otpPurpose);
        if (!sent) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            out.write("{\"success\": false, \"error\": \"Failed to send OTP. Please try again.\"}");
            return;
        }

        // Step 5: Save OTP session info
        session.setAttribute("otp", otp);
        session.setAttribute("otpEmail", email);
        session.setAttribute("otpExpiry", System.currentTimeMillis() + 60 * 1000);
        session.setAttribute("otpAttempts", 0);
        System.out.println("session otp: " + session.getAttribute("otp"));
        System.out.println("session otpEmail: " + session.getAttribute("otpEmail"));
        System.out.println("session otpPurpose: " + session.getAttribute("otpPurpose"));
        System.out.println("session otpExpiry: " + session.getAttribute("otpExpiry"));
        System.out.println("session otpAttempts " + session.getAttribute("otpAttempts"));

        // Step 6: Response
        response.setContentType("application/json");
        out.write("{\"success\": true}");
    }

    private void handleVerifyOtp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("-------------handleVerifyOtp-------------");
        UserService userService = new UserService();
        HttpSession session = request.getSession();

        // Step 1: Retrieve OTP-related session data
        String sessionOtp = (String) session.getAttribute("otp");
        String email = (String) session.getAttribute("otpEmail");
        Long expiry = (Long) session.getAttribute("otpExpiry");
        Integer attempts = (Integer) session.getAttribute("otpAttempts");
        String purpose = (String) session.getAttribute("otpPurpose");
        System.out.println("sessionOtp = " + sessionOtp);
        System.out.println("email = " + email);
        System.out.println("expiry = " + expiry);
        System.out.println("attempts = " + attempts);
        System.out.println("purpose = " + purpose);

        if (attempts == null) attempts = 0;

        // Step 2: Extract OTP from request
        String userInputOtp = extractOtpFromRequest(request);
        System.out.println("userInputOtp = " + userInputOtp);
        if (userInputOtp == null) {
            sendOtpError(response, "Please enter all 6 digits.");
            return;
        }

        // Step 3: Check expiration
        if (expiry == null || System.currentTimeMillis() > expiry) {
            sendOtpError(response, "OTP has expired. Please request a new code.");
            return;
        }

        // Step 4: Check match
        if (!userInputOtp.equals(sessionOtp)) {
            session.setAttribute("otpAttempts", attempts + 1);
            sendOtpError(response, "Invalid verification code. Please try again.");
            return;
        }

        // Step 5: OTP is correct → Clear OTP-related session data (some values preserved if needed)
        session.removeAttribute("otp");
        session.removeAttribute("otpExpiry");
        session.removeAttribute("otpAttempts");

        // Step 6: Act based on purpose
        switch (purpose) {
            case "setPassword":
                response.sendRedirect(request.getContextPath() + "/set-password");
                return;

            case "register":
                String fullName = (String) session.getAttribute("registerFullName");
                String password = (String) session.getAttribute("registerPassword");

                User newUser = new User();
                newUser.setFullName(fullName);
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.setRoleId(1);

                boolean success = userService.registerUser(newUser);
                if (!success) {
                    sendOtpError(response, "Failed to create account. Please try again.");
                    return;
                }

                session.removeAttribute("registerFullName");
                session.removeAttribute("registerPassword");
                session.setAttribute("successMessage", "Registration successful! You can now log in.");
                System.out.println("successMessage: " + session.getAttribute("successMessage"));
//                response.sendRedirect(request.getContextPath() + "/login");

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": true, \"redirect\": \"" + request.getContextPath() + "/login\"}");
                return;

            case "verifyNewEmail":
                session.setAttribute("newEmailVerified", true);
                session.setAttribute("verifiedEmail", email);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"message\": \"Email verified successfully.\"}");
                return;

            case "resetPassword":
                // ✅ Quên mật khẩu → Cho phép đặt lại mật khẩu
                session.setAttribute("allowResetPassword", true);
                session.setAttribute("otpEmail", email); // giữ lại email để xác định user sau
                session.setAttribute("successMessage", "Email verified. Please set your new password.");
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true}");
                return;

            default:
                sendOtpError(response, "Unknown verification context.");
        }
    }


    /**
     * Extracts the 6-digit OTP code from the request.
     */
    private String extractOtpFromRequest(HttpServletRequest request) {
        for (int i = 1; i <= 6; i++) {
            System.out.println("d" + i + " = " + request.getParameter("d" + i));
        }

        StringBuilder otpBuilder = new StringBuilder();
        for (int i = 1; i <= 6; i++) {
            String digit = request.getParameter("d" + i);
            if (digit == null || digit.trim().isEmpty()) {
                return null;
            }
            otpBuilder.append(digit.trim());
        }
        return otpBuilder.toString();
    }

    /**
     * Sends an error response in either HTML or JSON depending on request type.
     */
    private void sendOtpError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
    }

    private void handleCheckEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("-------------handleCheckEmail-------------");
        UserService userService = new UserService();
        EmailProxy emailProxy = new EmailProxy();

        String email = request.getParameter("email");

        // 1. Kiểm tra định dạng
        if (!UserService.isEmailValid(email)) {
            System.out.println("Invalid email");
            response.setStatus(400);
            response.getWriter().write("Invalid email format");
            return;
        }

        // 2. Kiểm tra email đã tồn tại chưa
        if (userService.findUserByEmail(email) != null) {
            System.out.println("Email already exists");
            response.setStatus(200);
            response.getWriter().write("Exist");
        } else {
            System.out.println("Email does not exist");
            response.setStatus(200);
            response.getWriter().write("Not exist");
        }
    }

    /**
     * Handles password update after OTP verification for Google-linked accounts.
     */
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("otpEmail");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate session email
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Step 1: Validate password fields
        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/WEB-INF/view/user/set-password.jsp").forward(request, response);
            return;
        }

        // (Optional) Validate password strength
        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters.");
            request.getRequestDispatcher("/WEB-INF/view/user/set-password.jsp").forward(request, response);
            return;
        }

        // Step 2: Update password in database
        UserService userService = new UserService();
        User user = userService.findUserByEmail(email);

        if (user == null || user.getGoogleId() == null) {
            request.setAttribute("error", "Invalid account.");
            request.getRequestDispatcher("/WEB-INF/view/user/set-password.jsp").forward(request, response);
            return;
        }

        // Step 3: Update and save
        user.setPassword(password); // Nên mã hóa nếu có hashing
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userService.updatePassword(user);

        // Step 4: Clear session & redirect to login
        session.removeAttribute("otpEmail");
        session.setAttribute("successMessage", "Password set successfully. You can now log in using email and password.");
        response.sendRedirect(request.getContextPath() + "/login");
    }

}