package controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.User;
import proxy.GoogleProxy;
import service.user.UserService;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Servlet to handle Google Sign-In and Sign-Up using OAuth2.
 */
@WebServlet(name = "GoogleServlet", urlPatterns = {"/google-login", "/google-register", "/google-callback"})
public class GoogleServlet extends HttpServlet {
    /**
     * Handle GET requests for Google login/register/callback.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.endsWith("/google-login") || uri.endsWith("/google-register")) {
            redirectToGoogleOAuth(response);
            return;
        }

        // Handle callback after user grants Google access
        if (uri.endsWith("/google-callback")) {
            handleGoogleCallback(request, response);
        }

    }

    /**
     * Redirects user to Google's OAuth2 consent screen (login or registration).
     *
     * @param response HTTP response to send redirect
     * @throws IOException if redirect fails
     */
    private void redirectToGoogleOAuth(HttpServletResponse response) throws IOException {
        GoogleProxy googleProxy = new GoogleProxy();

        // Build Google OAuth2 URL (same for login and register)
        String url = googleProxy.buildAuthorizationUrl();
        response.sendRedirect(url);
    }

    /**
     * Handle callback from Google after login or registration.
     * - Fetch Google user info from the code
     * - Check if user exists → Login
     * - If not exists → Register then Login
     */
    private void handleGoogleCallback(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GoogleProxy googleProxy = new GoogleProxy();
        UserService userService = new UserService();

        // Get the code returned from Google
        String code = request.getParameter("code");

        try {
            // Step 1: Get user info from Google using the code
            User googleUser = googleProxy.getUserFromAuthorizationCode(code);

            // Step 2: Check if the user already exists
            User existingUser = userService.findUserByEmail(googleUser.getEmail());

            // Step 3: Register if not exist
            if (existingUser == null) {
                existingUser = userService.registerGoogleUser(googleUser);
            } else {
                if (existingUser.getGoogleId() == null) {
                    existingUser.setGoogleId(googleUser.getGoogleId());
                    existingUser.setAvatarUrl(googleUser.getAvatarUrl());
                    existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    userService.updateGoogleAcc(existingUser);
                }
            }

            // Step 4: Save user to session
            request.getSession().setAttribute("user", existingUser);

            // Step 5: Redirect to home page
            response.sendRedirect(request.getContextPath() + "/home");

        } catch (Exception e) {
            // Log error and forward back to login page with error message
            e.printStackTrace();
            request.setAttribute("error", "Google login failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        }
    }
}
