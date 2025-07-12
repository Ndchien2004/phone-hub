<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Update Profile</title>
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
<body class="bg-gray-100 flex justify-center py-10 px-6" data-context-path="${pageContext.request.contextPath}">
<form action="${pageContext.request.contextPath}  " method="post">
    <input type="hidden" id="avatarUrl" name="avatarUrl" value="${user.avatarUrl}" data-original="${user.avatarUrl}">
    <input type="hidden" id="newEmailVerified" name="newEmailVerified"
       value="${sessionScope.newEmailVerified}" data-original="${sessionScope.newEmailVerified}" />
    <input type="hidden" id="verifiedEmail" value="${sessionScope.verifiedEmail}">

    <div class="bg-white rounded-lg shadow-lg w-full max-w-[1600px] p-8">
        <h2 class="text-2xl font-semibold text-center mb-6">Update Profile</h2>

        <div class="grid grid-cols-12 gap-6">
            <!-- Column 1: Avatar -->
            <div class="col-span-3 flex flex-col items-center">
                <img id="currentAvatar"
                     src="${empty user.avatarUrl ? 'https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png' : user.avatarUrl}"
                     alt="Avatar"
                     class="w-40 h-40 rounded-full object-cover mb-2">

                <div id="avatarActions">
                    <c:choose>
                        <c:when test="${!empty user.avatarUrl}">
                            <!-- Đã có avatar -->
                            <div class="flex gap-4 mb-6">
                                <a id="change-avatar-link" href="#" onclick="openAvatarModal(); return false;" class="text-blue-500 hover:underline">Change</a>
                                <a id="delete-avatar-link" href="#" onclick="deleteAvatar(); return false;" class="text-red-500 hover:underline">Delete</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="hover:underline mb-6">
                                <!-- Chưa có avatar -->
                                <a id="add-avatar-link" href="#" onclick="openAvatarModal(); return false;" class="text-blue-500">Add avatar</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <button type="submit" class="bg-blue-500 hover:bg-blue-600 text-white font-semibold px-4 py-2 rounded mb-4 w-full">
                    Update Profile
                </button>

                <c:choose>
                    <c:when test="${empty user.password}">
                        <!-- Nếu password là null -->
                        <a href="${pageContext.request.contextPath}/change-password"
                            class="text-red-500 border border-red-500 text-center font-semibold px-4 py-2 rounded w-full
                            hover:border-2 hover:border-red-700 transition duration-200">
                        Set Password
                        </a>
                    </c:when>
                    <c:otherwise>
                        <!-- Nếu user đã có password -->
                         <a href="${pageContext.request.contextPath}/change-password"
                            class="text-red-500 border border-red-500 text-center font-semibold px-4 py-2 rounded w-full
                            hover:border-2 hover:border-red-700 transition duration-200">
                        Change Password
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Column 2 & 3: Form Grid -->
            <div class="col-span-9 grid grid-cols-2 gap-x-8">
                <!-- ✅ Success Message Row (chiếm toàn bộ chiều ngang) -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="col-span-2 text-green-600 text-sm mb-3">
                        ${sessionScope.successMessage}
                    </div>
                    <c:remove var="successMessage" scope="session"/>
                </c:if>

                <!-- Row 1 -->
                <div>
                    <label class="block mb-1 text-sm">Full Name <span class="text-red-500">*</span></label>
                    <input type="text" id="fullName" name="fullName" value="${user.fullName}" data-original="${user.fullName}"
                            class="w-full border rounded px-3 py-2 mb-4" placeholder="Full Name">
                </div>
                <div>
                    <label class="block mb-1 text-sm">Province/City</label>
                    <select id="province" name="provinceCode" data-original="${user.provinceCode}"
                            class="w-full border rounded px-3 py-2 mb-4"
                            onchange="onProvinceChange(this)"
                            data-url="${pageContext.request.contextPath}/api/districts">
                        <option value="" disabled ${empty user.provinceCode ? "selected" : ""}>Choose province</option>
                        <c:forEach items="${provinces}" var="province">
                            <option value="${province.provinceCode}"
                                    ${province.provinceCode == user.provinceCode ? "selected" : ""}>
                                ${province.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Row 2 -->
                <div>
                    <label class="block mb-1 text-sm">Gender</label>
                    <div id="genderContainer" class="flex items-center gap-4 mb-4" data-original="${user.gender}">
                        <label><input type="radio" name="gender" id="male" value="true"
                                      ${user.gender != null && user.gender ? "checked" : ""} class="mr-1"> Male</label>
                        <label><input type="radio" name="gender" id="female" value="false"
                                      ${user.gender != null && !user.gender ? "checked" : ""} class="mr-1"> Female</label>
                    </div>
                </div>
                <div>
                    <label class="block mb-1 text-sm">District</label>
                    <select id="district" name="districtCode" data-original="${user.districtCode}"
                            class="w-full border rounded px-3 py-2 mb-4"
                            onchange="onDistrictChange(this)"
                            data-url="${pageContext.request.contextPath}/api/wards">
                        <option value="" disabled ${empty user.districtCode ? "selected" : ""}>Choose District</option>
                        <c:forEach items="${districts}" var="district">
                            <option value="${district.districtCode}"
                                    ${district.districtCode == user.districtCode ? "selected" : ""}>
                                ${district.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Row 3 -->
                <div>
                    <label class="block mb-1 text-sm">Phone Number</label>
                    <input type="text" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}" data-original="${user.phoneNumber}"
                        class="w-full border rounded px-3 py-2 mb-4" placeholder="Phone Number">
                </div>
                <div>
                    <label class="block mb-1 text-sm">Ward</label>
                    <select id="ward" name="wardCode" data-original="${user.wardCode}"
                            class="w-full border rounded px-3 py-2 mb-4">
                        <option value="" disabled ${empty user.wardCode ? "selected" : ""}>Choose Ward</option>
                        <c:forEach items="${wards}" var="ward">
                            <option value="${ward.wardCode}"
                                    ${ward.wardCode == user.wardCode ? "selected" : ""}>
                                ${ward.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Row 4 -->
                <div>
                    <label class="block mb-1 text-sm">Email <span class="text-red-500">*</span></label>
                    <div class="flex items-center gap-2">
                        <input type="email" id="email" name="email"
                               class="w-full border rounded px-3 py-2"
                               value="${user.email}"
                               data-original="${user.email}">
                        <span id="email-status"
                              class="bg-green-600 text-white text-sm rounded w-[140px] h-[38px] flex items-center justify-center">
                            Verified
                        </span>
                    </div>
                </div>
                <div>
                    <label class="block mb-1 text-sm">Address Detail</label>
                    <input type="text" id="addressDetail" name="addressDetail" value="${user.addressDetail}" data-original="${user.addressDetail}"
                        class="w-full border rounded px-3 py-2" placeholder="Address Detail">
                </div>

                <!-- Row 5 -->
                <div class="gap-4 mt-6 pr-2 items-center">
                    <c:if test="${not empty errors}">
                        <div class="text-red-500 text-sm w-full">
                            <c:forEach var="err" items="${errors}">
                                <div class="mb-1">• ${err.value}</div>
                            </c:forEach>
                        </div>
                    </c:if>
                </div>
                <div class="flex justify-end gap-4 mt-6 pr-2 items-center">
                    <button id="saveButton" type="submit"
                            class="bg-blue-500 hover:bg-blue-600 text-white font-semibold px-6 py-2 rounded w-32">
                        Save
                    </button>
                    <a href="#" id="cancelBtn"
                       class="text-blue-500 border border-blue-500 text-center font-semibold px-6 py-2 rounded w-32
                              hover:border-2 hover:border-blue-700 transition duration-200">
                        Cancel
                    </a>
                </div>

            </div>
        </div>
    </div>
</form>

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

<!-- Avatar Upload Modal -->
<div id="avatarModal"
     class="hidden fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
  <div class="bg-white p-6 rounded shadow-md w-96 relative">
    <!-- Close button -->
    <button onclick="closeAvatarModal()" class="absolute top-2 right-3 text-gray-500 text-xl">&times;</button>

    <h3 class="text-lg font-semibold mb-4 text-center">Choose your new avatar</h3>

    <!-- File input -->
    <input type="file" id="avatarInput" accept="image/*"
           class="w-full mb-4" onchange="previewAvatar(event)">

    <!-- Preview -->
    <div class="flex justify-center mb-4">
      <img id="avatarPreview" src="" alt="Preview"
           class="w-32 h-32 rounded-full object-cover hidden">
    </div>

    <!-- Confirm button (aligned right) -->
    <div class="flex justify-end">
        <button onclick="uploadAvatar()"
          class="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600 transition">
            Upload
        </button>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/javascript/user/update-profile.js"></script>
</body>
</html>