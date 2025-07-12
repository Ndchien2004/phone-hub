<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sign in</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        .otp-input {
            width: 48px;
            height: 48px;
            text-align: center;
            font-size: 20px;
            border: 1px solid #d1d5db; /* Tailwind's border-gray-300 */
            border-radius: 0.375rem;    /* rounded */
            outline: none;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        .otp-input:focus {
            border-color: #3b82f6;      /* Tailwind's blue-500 */
            box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
        }
    </style>
</head>
<body class="bg-gray-100 flex justify-center items-center min-h-screen" data-context-path="${pageContext.request.contextPath}">

<div class="bg-white rounded-xl shadow-lg p-8 w-full max-w-md">
    <h2 class="text-2xl font-bold text-center mb-1">Sign in</h2>
    <p class="text-center text-sm text-gray-600 mb-4">Don't have an account? <a href="${pageContext.request.contextPath}/register-account" class="text-blue-600 hover:underline">Sign up</a></p>

    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
    <div class="text-red-600 text-sm text-center mb-2"><%= error %></div>
    <% } %>

    <c:if test="${googleLinked == true}">
        <script>
            window.addEventListener('DOMContentLoaded', function () {
                document.getElementById('googleLinkModal').classList.remove('hidden');
            });
        </script>
        <div id="googleLinkModal" class="fixed inset-0 bg-black bg-opacity-50 z-50 hidden flex justify-center items-center">
            <div class="bg-white p-6 rounded-xl shadow-xl w-96 relative">
                <button onclick="document.getElementById('googleLinkModal').classList.add('hidden')" class="absolute top-2 right-2 text-gray-500 text-xl">&times;</button>
                <h3 class="text-lg font-semibold mb-2 text-center">Google-linked account</h3>
                <p class="text-sm text-center mb-4">This account was created using Google.<br/>Would you like to:</p>
                <div class="flex flex-col gap-3">
                    <a href="${pageContext.request.contextPath}/set-password-request?email=${email}" class="bg-blue-500 text-white text-center py-2 rounded hover:bg-blue-600">Set a password</a>
                    <a href="${pageContext.request.contextPath}/login-with-google" class="border border-red-500 text-red-500 text-center py-2 rounded hover:bg-red-50">Continue with Google</a>
                </div>
            </div>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.successMessage}">
        <div class="text-green-600 text-sm text-left mb-2">
            ${sessionScope.successMessage}
        </div>
        <c:remove var="successMessage" scope="session" />
    </c:if>

    <c:if test="${param.timeout == 'true'}">
        <div class="text-red-600 text-sm text-center mb-2">Your session has expired. Please log in again.</div>
    </c:if>

    <c:if test="${param.error == 'notLoggedIn'}">
        <div class="text-red-600 text-sm text-center mb-2">You must be logged in to access this page.</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login-with-email" method="post" class="space-y-4">
        <div>
            <label for="email" class="block text-sm font-medium mb-1">Email</label>
            <input type="email" id="email" name="email" required
                   value="${email != null ? email : ''}"
                   placeholder="Email"
                   class="w-full border border-gray-300 rounded px-4 py-2">
        </div>
        <div>
            <label for="password" class="block text-sm font-medium mb-1">Password</label>
            <input type="password" id="password" name="password" required
                    placeholder="Password"
                   class="w-full border border-gray-300 rounded px-4 py-2">
        </div>
        <div class="flex justify-between items-center text-sm">
            <label class="flex items-center gap-2">
                <input type="checkbox" name="rememberMe" class="accent-blue-500">
                Remember me
            </label>
            <a href="#" onclick="openForgotModal()" class="text-blue-600 hover:underline">Forgot password?</a>
        </div>
        <button type="submit" class="bg-blue-500 hover:bg-blue-600 text-white py-2 rounded w-full font-semibold">
            Log in
        </button>
    </form>

    <div class="flex items-center justify-center my-4 text-gray-500 text-sm">
        <div class="border-t border-gray-300 flex-grow mr-3"></div>
        or
        <div class="border-t border-gray-300 flex-grow ml-3"></div>
    </div>

    <a href="${pageContext.request.contextPath}/login-with-google"
       class="bg-red-500 hover:bg-red-600 text-white py-2 rounded w-full flex items-center justify-center gap-3">
        <img src="https://developers.google.com/identity/images/g-logo.png" alt="Google" class="w-5 h-5">
        Sign in with Google
    </a>
</div>

<!-- 🔒 Forgot Password Modal (hidden by default) -->
<div id="forgotModal" class="hidden fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
  <!-- 📦 Modal Box -->
  <div class="bg-white rounded-lg shadow-lg w-full max-w-md p-6 relative animate-fadeIn">

    <!-- ❌ Close Button -->
    <button onclick="closeForgotModal()"
            class="absolute top-2 right-2 text-gray-400 hover:text-gray-600 text-xl font-bold">&times;</button>

    <!-- 📝 Modal Title -->
    <h2 class="text-xl font-semibold text-center mb-4">Forgot Password</h2>

    <!-- 📌 Instruction -->
    <p class="text-gray-600 text-sm text-center mb-4">
      Please enter your email address.
    </p>

    <!-- ⚠️ Error message (initially hidden) -->
    <div id="forgotError" class="text-red-500 text-sm mb-3 hidden text-left"></div>

    <!-- 📧 Email Input Field -->
    <input id="forgotEmail" type="email"
           class="w-full border rounded px-4 py-2 mb-4 focus:ring-2 focus:ring-blue-500 outline-none"
           placeholder="Enter your email">

    <!-- 📤 Submit Button -->
    <button onclick="submitForgotEmail()"
            class="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded w-full">
      Send Reset Code
    </button>

  </div>
</div>


<!-- OTP Verification Modal -->
<div id="otpModal"
     class="hidden fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm">

  <div class="relative bg-white p-6 rounded-xl shadow-xl w-[400px] max-w-full animate-fadeIn">

    <!-- ❌ Close button (top right) -->
    <button onclick="closeOtpModal()"
            class="absolute top-2 right-2 text-gray-400 hover:text-gray-600 text-lg font-bold">
      &times;
    </button>

    <!-- Title -->
    <h3 class="text-xl font-semibold text-center mb-3 text-gray-800">Enter Verification Code</h3>
    <p class="text-sm text-gray-600 text-center mb-5">We’ve sent a 6-digit code to your email.</p>

    <!-- Error message -->
    <div id="otpError" class="text-red-500 text-sm mb-3 hidden text-center"></div>

    <!-- OTP input fields -->
    <div class="flex justify-center gap-2 mb-6">
        <input type="text" maxlength="1" class="otp-input" name="d1">
        <input type="text" maxlength="1" class="otp-input" name="d2">
        <input type="text" maxlength="1" class="otp-input" name="d3">
        <input type="text" maxlength="1" class="otp-input" name="d4">
        <input type="text" maxlength="1" class="otp-input" name="d5">
        <input type="text" maxlength="1" class="otp-input" name="d6">
    </div>

    <!-- Countdown + Resend -->
    <div class="text-center text-sm text-gray-600 mt-4 mb-4">
        <div id="otpCountdown">Didn’t get the code? Retry in <span id="otpTimer">60</span>s</div>
        <div id="resendOtpLink" class="hidden">
            Didn’t get the code? <a href="#" onclick="handleResendOtp()" class="text-blue-600 hover:underline font-medium">Resend OTP</a>
        </div>
    </div>

    <!-- Confirm button (centered) -->
    <div class="flex justify-center">
      <button id="verifyOtpBtn"
              onclick="submitOtp()"
              class="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600 transition min-w-[120px]">
        Confirm
      </button>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/javascript/user/login.js"></script>
</body>
</html>
