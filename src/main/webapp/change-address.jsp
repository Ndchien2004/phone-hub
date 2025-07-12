<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thay đổi địa chỉ giao hàng - Đơn hàng #${order.orderId}</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            color: #333;
            line-height: 1.6;
        }

        .container {
            max-width: 800px;
            margin: 20px auto;
            padding: 0 20px;
        }

        /* Breadcrumb */
        .breadcrumb {
            background: white;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .breadcrumb a {
            color: #d70018;
            text-decoration: none;
            margin-right: 10px;
        }

        .breadcrumb a:hover {
            text-decoration: underline;
        }

        .breadcrumb span {
            color: #666;
            margin: 0 5px;
        }

        /* Page Header */
        .page-header {
            background: white;
            padding: 25px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .page-title {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 10px;
        }

        .page-title h1 {
            color: #d70018;
            font-size: 24px;
        }

        .page-subtitle {
            color: #666;
            font-size: 14px;
        }

        /* Form Container */
        .form-container {
            background: white;
            border-radius: 8px;
            padding: 30px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .form-section {
            margin-bottom: 30px;
        }

        .section-title {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        /* Current Address Display */
        .current-address {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #6c757d;
            margin-bottom: 20px;
        }

        .address-info {
            display: grid;
            gap: 10px;
        }

        .address-item {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .address-item i {
            color: #666;
            width: 20px;
            flex-shrink: 0;
        }

        /* Card styling for new form structure */
        .card {
            background: #ffffff;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }

        .card-title {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        /* Customer Info Fields */
        .customer-info-fields {
            display: grid;
            gap: 15px;
        }

        /* Shipping Method */
        .shipping-method-label {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 12px 15px;
            background: #e8f5e8;
            border: 1px solid #28a745;
            border-radius: 6px;
            color: #155724;
            font-weight: 500;
        }

        .shipping-method-label .icon {
            font-size: 18px;
        }

        /* Form Elements */
        .form-group {
            margin-bottom: 15px;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #333;
            font-size: 14px;
        }

        .form-label.required::after {
            content: ' *';
            color: #dc3545;
        }

        .form-control, input[type="text"], input[type="tel"], input[type="email"], select, textarea {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.3s ease;
            font-family: inherit;
        }

        .form-control:focus, input:focus, select:focus, textarea:focus {
            outline: none;
            border-color: #d70018;
            box-shadow: 0 0 0 2px rgba(215, 0, 24, 0.1);
        }

        .form-control.error, input.error, select.error, textarea.error {
            border-color: #dc3545;
        }

        .form-control.error:focus, input.error:focus, select.error:focus, textarea.error:focus {
            border-color: #dc3545;
            box-shadow: 0 0 0 2px rgba(220, 53, 69, 0.1);
        }

        /* Select styling */
        select {
            cursor: pointer;
            background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
            background-position: right 12px center;
            background-repeat: no-repeat;
            background-size: 16px;
            padding-right: 40px;
            -webkit-appearance: none;
            -moz-appearance: none;
            appearance: none;
        }

        select:disabled {
            background-color: #f8f9fa;
            color: #6c757d;
            cursor: not-allowed;
        }

        /* Textarea styling */
        textarea {
            resize: vertical;
            min-height: 80px;
        }

        /* Form Row Layout */
        .form-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }

        /* Error Messages */
        .error-message {
            color: #dc3545;
            font-size: 12px;
            margin-top: 5px;
            display: none;
        }

        .error-message.show {
            display: block;
        }

        /* Form Actions */
        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: flex-end;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }

        /* Buttons */
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            min-width: 120px;
            justify-content: center;
            border: 1px solid transparent;
        }

        .btn-primary {
            background: #d70018;
            color: white;
        }

        .btn-primary:hover {
            background: #b50015;
        }

        .btn-primary:disabled {
            background: #ccc;
            cursor: not-allowed;
        }

        .btn-outline {
            background: transparent;
            color: #6c757d;
            border: 1px solid #6c757d;
        }

        .btn-outline:hover {
            background: #6c757d;
            color: white;
        }

        /* Loading Spinner */
        .spinner {
            border: 2px solid #f3f3f3;
            border-top: 2px solid #d70018;
            border-radius: 50%;
            width: 16px;
            height: 16px;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* Toast Notifications */
        .toast {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1100;
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
            padding: 16px 20px;
            display: none;
            min-width: 300px;
            border-left: 4px solid #28a745;
            animation: slideInRight 0.3s ease;
        }

        .toast.error {
            border-left-color: #dc3545;
        }

        .toast-content {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .toast-icon {
            font-size: 20px;
            color: #28a745;
        }

        .toast.error .toast-icon {
            color: #dc3545;
        }

        .toast-message {
            flex: 1;
            font-weight: 500;
        }

        .toast-close {
            cursor: pointer;
            color: #666;
            font-size: 18px;
        }

        .toast-close:hover {
            color: #333;
        }

        /* Toast Animations */
        @keyframes slideInRight {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        @keyframes slideOutRight {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .container {
                padding: 0 15px;
            }

            .form-row {
                grid-template-columns: 1fr;
            }

            .form-actions {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }

            .page-title h1 {
                font-size: 20px;
            }

            .card {
                padding: 15px;
            }

            .form-container {
                padding: 20px;
            }
        }

        @media (max-width: 480px) {
            .page-header {
                padding: 20px;
            }

            .breadcrumb {
                padding: 12px 15px;
            }

            .breadcrumb a {
                display: inline-block;
                margin-bottom: 5px;
            }

            .toast {
                left: 10px;
                right: 10px;
                min-width: auto;
            }
        }

        /* Focus styles for better accessibility */
        .btn:focus {
            outline: 2px solid #d70018;
            outline-offset: 2px;
        }

        .btn-outline:focus {
            outline: 2px solid #6c757d;
            outline-offset: 2px;
        }

        /* Improved form field focus styles */
        input:focus, select:focus, textarea:focus {
            border-color: #d70018;
            box-shadow: 0 0 0 3px rgba(215, 0, 24, 0.1);
        }

        /* Enhanced select dropdown for better UX */
        select option {
            padding: 8px;
        }

        /* Better spacing for form elements */
        .form-group:last-child {
            margin-bottom: 0;
        }

        /* Improved card layout */
        .card:last-child {
            margin-bottom: 0;
        }
    </style>
</head>
<body>

<%@ include file="../layout/navbar.jsp" %>

<div class="container">
    <!-- Breadcrumb -->
    <nav class="breadcrumb">
        <a href="${pageContext.request.contextPath}/orders">
            <i class="fas fa-list"></i> Danh sách đơn hàng
        </a>
        <span>/</span>
        <a href="${pageContext.request.contextPath}/orders/${order.orderId}">
            <i class="fas fa-receipt"></i> Đơn hàng #${order.orderId}
        </a>
        <span>/</span>
        <span>Thay đổi địa chỉ</span>
    </nav>

    <!-- Page Header -->
    <div class="page-header">
        <div class="page-title">
            <h1>
                <i class="fas fa-map-marker-alt"></i>
                Thay đổi địa chỉ giao hàng
            </h1>
        </div>
        <div class="page-subtitle">
            Đơn hàng #${order.orderId} - Tổng tiền: <fmt:formatNumber value="${order.totalMoney}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
        </div>
    </div>

    <!-- Form Container -->
    <div class="form-container">
        <!-- Current Address Section -->
        <div class="form-section">
            <h3 class="section-title">
                <i class="fas fa-info-circle"></i>
                Địa chỉ hiện tại
            </h3>
            <div class="current-address">
                <div class="address-info">
                    <div class="address-item">
                        <i class="fas fa-user"></i>
                        <span><strong>Họ tên:</strong> ${order.fullName}</span>
                    </div>
                    <div class="address-item">
                        <i class="fas fa-phone"></i>
                        <span><strong>Số điện thoại:</strong> ${order.phoneNumber}</span>
                    </div>
                    <div class="address-item">
                        <i class="fas fa-envelope"></i>
                        <span><strong>Email:</strong> ${order.email}</span>
                    </div>
                    <div class="address-item">
                        <i class="fas fa-map-marker-alt"></i>
                        <span><strong>Địa chỉ:</strong> ${order.address}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- New Address Form -->
        <form id="changeAddressForm" method="POST" action="${pageContext.request.contextPath}/orders/change-address">
            <input type="hidden" name="orderId" value="${order.orderId}">

            <div id="step1-view">
                <div class="card">
                    <h3 class="card-title">Thông tin khách hàng</h3>
                    <div class="customer-info-fields">
                        <div class="form-group">
                            <label for="recipientName" class="form-label required">Tên người nhận</label>
                            <input type="text" id="recipientName" name="recipientName" placeholder="Nguyễn Văn A"
                                   value="${not empty sessionScope.user ? sessionScope.user.fullName : ''}"
                                   required minlength="2" maxlength="50" pattern="^[\p{L}\s]+$"
                                   title="Tên chỉ được chứa chữ cái (bao gồm dấu tiếng Việt) và khoảng trắng.">
                            <div class="error-message" id="recipientNameError"></div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="recipientPhone" class="form-label required">Số điện thoại</label>
                                <input type="tel" id="recipientPhone" name="recipientPhone" placeholder="09xxxxxxxx"
                                       value="${not empty sessionScope.user ? sessionScope.user.phone : ''}"
                                       required pattern="^0\d{9}$"
                                       title="Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.">
                                <div class="error-message" id="recipientPhoneError"></div>
                            </div>
                            <div class="form-group">
                                <label for="recipientEmail" class="form-label required">Email</label>
                                <input type="email" id="recipientEmail" name="recipientEmail"
                                       placeholder="nguyenvana@email.com"
                                       value="${not empty sessionScope.user ? sessionScope.user.email : ''}"
                                       required maxlength="100">
                                <div class="error-message" id="recipientEmailError"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="card">
                    <h3 class="card-title">Thông tin nhận hàng</h3>
                    <div class="shipping-method-label">
                        <span class="icon"><i class="fa-solid fa-truck-fast"></i></span>
                        <span>Giao hàng tận nơi</span>
                        <input type="hidden" name="shippingMethod" value="delivery">
                    </div>
                    <div id="delivery-fields">
                        <div class="form-group" style="margin-top: 15px;">
                            <label for="shippingAddress" class="form-label required">Địa chỉ nhận hàng (Số nhà,
                                Tên đường)</label>
                            <input type="text" id="shippingAddress" name="shippingAddress"
                                   placeholder="Ví dụ: 123 Phố Bà Triệu" required>
                            <div class="error-message" id="shippingAddressError"></div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="shippingCity" class="form-label required">Tỉnh/Thành phố</label>
                                <select id="shippingCity" name="shippingCity" required>
                                    <option value="">-- Chọn Tỉnh/Thành phố --</option>
                                    <c:forEach var="province" items="${requestScope.provinces}">
                                        <option value="${province.name}">${province.name}</option>
                                    </c:forEach>
                                </select>
                                <div class="error-message" id="shippingCityError"></div>
                            </div>
                            <div class="form-group">
                                <label for="shippingDistrict" class="form-label required">Quận/Huyện</label>
                                <select id="shippingDistrict" name="shippingDistrict" required>
                                    <option value="">-- Chọn Quận/Huyện --</option>
                                    <c:forEach var="district" items="${requestScope.districts}">
                                        <option value="${district.name}">${district.name}</option>
                                    </c:forEach>
                                </select>
                                <div class="error-message" id="shippingDistrictError"></div>
                            </div>
                            <div class="form-group">
                                <label for="shippingWard" class="form-label required">Phường/Xã</label>
                                <select id="shippingWard" name="shippingWard" required>
                                    <option value="">-- Chọn Phường/Xã --</option>
                                    <c:forEach var="ward" items="${requestScope.wards}">
                                        <option value="${ward.name}">${ward.name}</option>
                                    </c:forEach>
                                </select>
                                <div class="error-message" id="shippingWardError"></div>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <textarea name="note" placeholder="Ghi chú khác (nếu có)"></textarea>
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/order-detail?orderId=${order.orderId}" class="btn btn-outline">
                    <i class="fas fa-times"></i>
                    Hủy bỏ
                </a>
                <button type="submit" id="submitBtn" class="btn btn-primary">
                    <i class="fas fa-save"></i>
                    Lưu thay đổi
                </button>
            </div>
        </form>


    </div>
</div>

<!-- Toast Notification -->
<div id="toast" class="toast">
    <div class="toast-content">
        <i id="toastIcon" class="toast-icon fas fa-check-circle"></i>
        <span id="toastMessage" class="toast-message"></span>
        <i class="toast-close fas fa-times"></i>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.getElementById('changeAddressForm');
        const submitBtn = document.getElementById('submitBtn');
        const toast = document.getElementById('toast');
        const toastIcon = document.getElementById('toastIcon');
        const toastMessage = document.getElementById('toastMessage');
        const toastClose = document.querySelector('.toast-close');

        // Form validation
        function validateForm() {
            let isValid = true;

            // Validate full name
            const fullName = document.getElementById('fullName');
            if (fullName.value.trim().length < 2) {
                showFieldError('fullName', 'Họ tên phải có ít nhất 2 ký tự');
                isValid = false;
            } else {
                hideFieldError('fullName');
            }

            // Validate phone number
            const phoneNumber = document.getElementById('phoneNumber');
            const phoneRegex = /^[0-9]{10,11}$/;
            if (!phoneRegex.test(phoneNumber.value.trim())) {
                showFieldError('phoneNumber', 'Số điện thoại phải có 10-11 chữ số');
                isValid = false;
            } else {
                hideFieldError('phoneNumber');
            }

            // Validate email
            const email = document.getElementById('email');
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email.value.trim())) {
                showFieldError('email', 'Email không hợp lệ');
                isValid = false;
            } else {
                hideFieldError('email');
            }

            // Validate address
            const address = document.getElementById('address');
            if (address.value.trim().length < 10) {
                showFieldError('address', 'Địa chỉ phải có ít nhất 10 ký tự');
                isValid = false;
            } else {
                hideFieldError('address');
            }

            return isValid;
        }

        function showFieldError(fieldId, message) {
            const field = document.getElementById(fieldId);
            const errorDiv = document.getElementById(fieldId + 'Error');

            field.classList.add('error');
            errorDiv.textContent = message;
            errorDiv.style.display = 'block';
        }

        function hideFieldError(fieldId) {
            const field = document.getElementById(fieldId);
            const errorDiv = document.getElementById(fieldId + 'Error');

            field.classList.remove('error');
            errorDiv.style.display = 'none';
        }

        // Real-time validation
        ['fullName', 'phoneNumber', 'email', 'address'].forEach(fieldId => {
            document.getElementById(fieldId).addEventListener('blur', validateForm);
        });

        // Show toast
        function showToast(message, isError = false) {
            toastMessage.textContent = message;

            if (isError) {
                toast.classList.add('error');
                toastIcon.className = 'toast-icon fas fa-exclamation-circle';
            } else {
                toast.classList.remove('error');
                toastIcon.className = 'toast-icon fas fa-check-circle';
            }

            toast.style.display = 'block';

            setTimeout(() => {
                hideToast();
            }, 5000);
        }

        // Hide toast
        function hideToast() {
            toast.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => {
                toast.style.display = 'none';
                toast.style.animation = 'slideInRight 0.3s ease';
            }, 300);
        }

        toastClose.addEventListener('click', hideToast);

        // Handle form submission
        // Handle form submission - remove the existing fetch-based submission and replace with:
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            if (!validateForm()) {
                showToast('Vui lòng kiểm tra lại thông tin đã nhập', true);
                return;
            }

            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<div class="spinner"></div>Đang cập nhật...';
            submitBtn.disabled = true;

            // Submit the form normally (server-side handling)
            form.submit();
        });
    });
</script>

</body>
</html>