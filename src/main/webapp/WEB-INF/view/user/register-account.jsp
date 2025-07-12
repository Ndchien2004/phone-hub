<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        .eye-icon {
            cursor: pointer;
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            width: 20px;
            height: 20px;
        }
        .password-rules {
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
        .rule-valid {
            color: #16a34a; /* green-600 */
        }
        .rule-invalid {
            color: #dc2626; /* red-600 */
        }
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
<body class="bg-gray-100 min-h-screen flex items-center justify-center" data-context-path="${pageContext.request.contextPath}">
<div class="w-full max-w-md px-6 py-8 bg-white rounded-2xl shadow-md">
    <h2 class="text-2xl font-bold text-center mb-2">Sign up</h2>
    <p class="text-center text-sm text-gray-600 mb-6">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login" class="text-blue-600 hover:underline">Log in</a>
    </p>

    <!-- Register Form -->
    <form id="registerForm" method="POST" class="space-y-4">
        <!-- Full Name -->
        <div>
            <label for="fullName" class="block text-sm font-medium text-gray-700">Full Name</label>
            <input type="text" id="fullName" name="fullName"
                   class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                   placeholder="Full Name"
                   value="${fullName != null ? fullName : ''}" required>
            <span id="fullNameError" class="text-red-500 error-message"></span>
        </div>

        <!-- Email -->
        <div>
            <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
            <input type="email" id="email" name="email"
                   class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                   placeholder="Email"
                   value="${email != null ? email : ''}" required>
            <span id="emailError" class="text-red-500 error-message"></span>
        </div>

        <!-- Password -->
        <div>
            <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
            <input type="password" id="password" name="password"
                   class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                   placeholder="Password" required>
             <span id="passwordError" class="text-red-500 error-message"></span>
        </div>

        <!-- Confirm Password -->
        <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700">Confirm Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword"
                   class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                   placeholder="Confirm Password" required>
            <span id="confirmPasswordError" class="text-red-500 error-message"></span>
        </div>

        <!-- Submit Button -->
        <div>
            <button type="submit"
                    class="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition duration-200">
                Register
            </button>
        </div>
    </form>

    <!-- OR Divider -->
    <div class="flex items-center my-6">
        <hr class="flex-grow border-gray-300">
        <span class="mx-3 text-gray-500 text-sm">or</span>
        <hr class="flex-grow border-gray-300">
    </div>

    <!-- Google Signup -->
    <div>
        <a href="${pageContext.request.contextPath}/register-with-google"
           class="w-full block bg-red-500 text-white text-center py-2 rounded-lg hover:bg-red-600 transition duration-200">
            Sign up with Google
        </a>
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

<script src="${pageContext.request.contextPath}/javascript/user/register-account.js"></script>
</body>
</html>
