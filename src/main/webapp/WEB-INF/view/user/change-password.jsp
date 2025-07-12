<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><c:choose><c:when test="${empty user.password}">Set Password</c:when><c:otherwise>Change Password</c:otherwise></c:choose></title>
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
<body class="bg-gray-100 p-6 flex justify-center" data-context-path="${pageContext.request.contextPath}">
<div class="bg-white rounded-lg shadow-lg p-6 w-fit">
    <form action="${pageContext.request.contextPath}/change-password" method="post">
        <div class="flex gap-10 items-start">
             <!-- Left Column: Navigation -->
                <div class="flex flex-col gap-4 min-w-[200px]">

                    <a href="${pageContext.request.contextPath}/update-profile"
                       class="text-red-500 border border-red-500 text-center font-semibold px-4 py-2 rounded w-full
                              hover:border-2 hover:border-red-700 transition duration-200">
                        Update Profile
                    </a>

                    <c:choose>
                        <c:when test="${empty user.password || sessionScope.allowResetPassword == true}">
                            <button disabled
                                    class="bg-blue-500 text-white font-semibold px-4 py-2 rounded w-full">
                                Set Password
                            </button>
                        </c:when>
                        <c:otherwise>
                            <button disabled
                                    class="bg-blue-500 text-white font-semibold px-4 py-2 rounded w-full">
                                Change Password
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>

            <!-- Right Column: Form -->
            <div class="w-[450px]">
                <h2 class="text-2xl font-semibold text-center mb-6">
                    <c:choose>
                        <c:when test="${(empty user.password) || allowResetPassword == true}">Set Password</c:when>
                        <c:otherwise>Change Password</c:otherwise>
                    </c:choose>
                </h2>

                <c:if test="${not empty successMessage}">
                    <div class="text-green-600 text-sm mb-3">${successMessage}</div>
                </c:if>
                <c:if test="${not empty sessionScope.successMessage}">
                     <c:remove var="successMessage" scope="session"/>
                </c:if>

                <c:if test="${not empty user.password and sessionScope.allowResetPassword != true}">
                    <label class="block text-sm">Current Password <span class="text-red-500">*</span></label>
                    <div class="relative mb-1">
                        <input type="password" name="currentPassword" placeholder="Current Password" value="${currentPassword}"
                               class="w-full border rounded px-3 py-2 pr-10">
                        <img src="https://cdn-icons-png.flaticon.com/512/709/709612.png" alt="Show"
                             class="eye-icon" onclick="toggleVisibility(this)">
                    </div>
                    <c:if test="${not empty currentPasswordError}">
                        <div class="text-red-600 text-sm mb-2">${currentPasswordError}</div>
                    </c:if>
                    <div class="flex justify-end mb-2">
                        <a href="#" onclick="openOtpModal()"
                            class="text-sm text-blue-500 hover:underline mb-4 inline-block flex justify-end">Forgot password?</a>
                    </div>
                </c:if>

                <label class="block text-sm">New Password <span class="text-red-500">*</span></label>
                <div class="relative mb-1">
                    <input type="password" name="newPassword" placeholder="New Password"
                           class="w-full border rounded px-3 py-2 pr-10" id="newPassword" value="${newPassword}">
                    <img src="https://cdn-icons-png.flaticon.com/512/709/709612.png" alt="Show"
                         class="eye-icon" onclick="toggleVisibility(this)">
                </div>
                <c:if test="${not empty newPasswordError}">
                    <div class="text-red-600 text-sm mb-2">${newPasswordError}</div>
                </c:if>
                <div class="grid grid-cols-2 gap-x-6 password-rules" id="passwordRules">
                    <ul class="space-y-1">
                        <li class="${noSpaces == false ? 'rule-invalid' : 'rule-valid'}">• no spaces</li>
                        <li class="${hasLower == false ? 'rule-invalid' : 'rule-valid'}">• at least 1 lowercase character</li>
                        <li class="${hasDigit == false ? 'rule-invalid' : 'rule-valid'}">• at least 1 number</li>
                    </ul>
                    <ul class="space-y-1">
                        <li class="${lengthMin == false ? 'rule-invalid' : 'rule-valid'}">• 6 characters minimum</li>
                        <li class="${hasUpper == false ? 'rule-invalid' : 'rule-valid'}">• at least 1 uppercase character</li>
                        <li class="${hasSpecial == false ? 'rule-invalid' : 'rule-valid'}">• at least 1 special character</li>
                    </ul>
                </div>

                <label class="block text-sm mt-3">Confirm New Password <span class="text-red-500">*</span></label>
                <div class="relative mb-1">
                    <input type="password" name="confirmPassword" placeholder="Confirm New Password"
                           class="w-full border rounded px-3 py-2 pr-10" value="${confirmPassword}">
                    <img src="https://cdn-icons-png.flaticon.com/512/709/709612.png" alt="Show"
                         class="eye-icon" onclick="toggleVisibility(this)">
                </div>
                <c:if test="${not empty confirmPasswordError}">
                    <div class="text-red-600 text-sm mb-2">${confirmPasswordError}</div>
                </c:if>

                <!-- Save Button Aligned Right -->
                <div class="flex justify-end mt-4">
                    <button type="submit" class="bg-blue-500 hover:bg-blue-600 text-white font-semibold px-6 py-2 rounded">
                        Save
                    </button>
                </div>
            </div>
        </div>
    </form>
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

<script src="${pageContext.request.contextPath}/javascript/user/change-password.js"></script>
</body>
</html>
