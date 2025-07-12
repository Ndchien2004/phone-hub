<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Set Password</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 min-h-screen flex items-center justify-center">

<div class="bg-white shadow-lg rounded-xl p-8 w-full max-w-md">
    <h2 class="text-2xl font-semibold text-center mb-6">Set Your Password</h2>

    <!-- Error Message -->
    <c:if test="${not empty error}">
        <div class="bg-red-100 text-red-700 px-4 py-2 rounded mb-4 text-sm text-center">
            ${error}
        </div>
    </c:if>

    <!-- Form -->
    <form action="${pageContext.request.contextPath}/reset-password" method="post" class="space-y-4">

        <!-- New Password -->
        <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-1">New Password</label>
            <input type="password" id="password" name="password"
                    placeholder="New Password"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required minlength="6" />
        </div>

        <!-- Confirm Password -->
        <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">Confirm Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword"
                    placeholder="Confirm Password"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required minlength="6" />
        </div>

        <!-- Submit -->
        <div>
            <button type="submit"
                    class="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 transition">
                Save Password
            </button>
        </div>
    </form>

    <!-- Back to login -->
    <div class="text-center mt-4">
        <a href="${pageContext.request.contextPath}/javascript/user/login" class="text-blue-600 hover:underline text-sm">
            Back to login
        </a>
    </div>
</div>

</body>
</html>
