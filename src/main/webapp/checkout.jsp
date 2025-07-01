<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Thông tin đặt hàng</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary-red: #d70018;
            --primary-blue: #007bff;
            --background-color: #f8f9fa;
            --border-color: #e0e0e0;
            --text-color: #333;
            --subtext-color: #6c757d;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background-color: var(--background-color);
            color: var(--text-color);
            margin: 0;
            font-size: 14px;
        }

        .container-fluid {
            max-width: 1200px;
            margin: 0 auto;
        }

        .checkout-header {
            background-color: white;
            padding: 15px 20px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .header-main {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .header-main .back-arrow {
            font-size: 20px;
            color: var(--text-color);
            text-decoration: none;
        }

        .header-main .title {
            font-size: 20px;
            font-weight: 500;
            margin: 0;
        }

        .checkout-steps {
            display: flex;
            gap: 15px;
            font-weight: 500;
        }

        .checkout-steps .step {
            color: var(--subtext-color);
            padding-bottom: 15px;
        }

        .checkout-steps .step.active {
            color: var(--primary-red);
            border-bottom: 2px solid var(--primary-red);
            margin-bottom: -16px;
        }

        .checkout-container {
            display: flex;
            gap: 20px;
            padding: 20px;
            align-items: flex-start;
        }

        .checkout-left {
            flex: 2;
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .checkout-right {
            flex: 1;
            position: sticky;
            top: 20px;
        }

        .card {
            background-color: white;
            border-radius: 8px;
            padding: 20px;
            border: 1px solid var(--border-color);
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
        }

        .card-title {
            font-size: 16px;
            font-weight: 700;
            margin-top: 0;
            margin-bottom: 20px;
            text-transform: uppercase;
        }

        .product-summary-item {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 15px;
        }

        .product-summary-item:last-child {
            margin-bottom: 0;
        }

        .product-summary-item img {
            width: 60px;
            height: 60px;
            object-fit: contain;
            border: 1px solid var(--border-color);
            border-radius: 4px;
        }

        .product-summary-info .name {
            font-weight: 500;
            margin-bottom: 5px;
        }

        .product-summary-info .price {
            font-weight: 700;
            color: var(--primary-red);
        }

        .product-summary-quantity {
            color: var(--subtext-color);
        }

        .form-row {
            display: flex;
            gap: 15px;
        }

        .form-group {
            flex: 1;
            margin-bottom: 15px;
            position: relative;
        }

        label.form-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
        }

        input[type="text"], input[type="tel"], input[type="email"], select, textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            font-size: 14px;
            background-color: white;
            box-sizing: border-box;
            transition: border-color 0.2s;
        }

        input.is-invalid, select.is-invalid {
            border-color: var(--primary-red);
        }

        input:focus, select:focus, textarea:focus {
            outline: none;
            border-color: var(--primary-red);
            box-shadow: 0 0 0 2px rgba(215, 0, 24, 0.2);
        }

        .error-message {
            color: var(--primary-red);
            font-size: 13px;
            margin-top: 5px;
        }

        .shipping-method-label {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 15px;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            font-weight: 500;
            background-color: #f8f8f8;
        }

        .shipping-method-label .icon {
            font-size: 18px;
            color: var(--primary-red);
        }

        .vat-options {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .vat-options label {
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .payment-method-option {
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 1rem;
            margin-bottom: 0.5rem;
            cursor: pointer;
            transition: border-color 0.2s, background-color 0.2s;
        }

        .payment-method-option:has(input:checked) {
            border-color: var(--primary-red);
            background-color: #fff5f6;
            box-shadow: 0 0 0 1px var(--primary-red);
        }

        .payment-method-option .form-check {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .payment-method-option .form-check-input {
            margin: 0;
        }

        .payment-method-option .form-check-label {
            font-weight: 500;
        }

        .order-summary .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 12px;
            font-size: 14px;
        }

        .order-summary .summary-row.total {
            font-size: 18px;
            font-weight: 700;
            margin-top: 15px;
        }

        .order-summary .summary-row.total .value {
            color: var(--primary-red);
        }

        .checkout-footer {
            padding-top: 15px;
            border-top: 1px solid var(--border-color);
            margin-top: 20px;
        }

        .btn-submit {
            width: 100%;
            background-color: var(--primary-red);
            color: white;
            border: none;
            padding: 15px;
            font-size: 16px;
            font-weight: 700;
            border-radius: 8px;
            cursor: pointer;
            text-transform: uppercase;
            transition: opacity 0.2s, background-color 0.2s;
        }

        .btn-submit:hover {
            opacity: 0.9;
        }

        .empty-cart-section {
            text-align: center;
            padding: 20px 0;
        }

        .empty-cart-section p {
            color: var(--subtext-color);
            margin-bottom: 20px;
        }

        .btn-shop-now {
            display: inline-block;
            background-color: var(--primary-blue);
            color: white;
            padding: 14px 40px;
            font-size: 16px;
            font-weight: 700;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            text-transform: uppercase;
            cursor: pointer;
        }

        .btn-shop-now:hover {
            opacity: 0.9;
        }

        .checkout-footer .button-wrapper {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .btn-secondary {
            width: 100%;
            background-color: white;
            color: var(--text-color);
            border: 1px solid var(--border-color);
            padding: 15px;
            font-size: 16px;
            font-weight: 700;
            border-radius: 8px;
            cursor: pointer;
            text-transform: uppercase;
        }

        .btn-secondary:hover {
            background-color: #f9f9f9;
        }

        @media (max-width: 992px) {
            .checkout-container {
                flex-direction: column;
            }

            .checkout-right {
                position: static;
                width: 100%;
            }
        }

        @media (max-width: 768px) {
            .checkout-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 15px;
            }

            .checkout-steps .step.active {
                padding-bottom: 5px;
                margin-bottom: -16px;
            }

            .form-row {
                flex-direction: column;
                gap: 0;
            }
        }

        .terms-label {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            font-size: 13px;
            color: var(--subtext-color);
            cursor: pointer;
            padding: 15px 0;
        }

        .terms-label input[type="checkbox"] {
            margin-top: 2px;
            width: auto;
        }

        .terms-label a {
            color: var(--primary-blue);
            font-weight: 500;
            text-decoration: none;
        }

        .terms-label a:hover {
            text-decoration: underline;
        }

        .btn-submit:disabled {
            background-color: #f07f89;
            cursor: not-allowed;
            opacity: 0.8;
        }

        .form-label.required::after {
            content: " *";
            color: var(--primary-red);
            font-weight: bold;
        }

        #step1-summary-view {
            display: none;
        }

        #checkout-form.on-step-2 #step1-view {
            display: none;
        }

        #checkout-form.on-step-2 #step1-summary-view {
            display: block;
        }

        #checkout-form.on-step-2 #payment-method-section {
            display: block;
        }

        .info-summary-card {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .info-summary-block .title-wrapper {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }

        .info-summary-block .title-wrapper h4 {
            margin: 0;
            font-size: 14px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .info-summary-block .content {
            font-size: 14px;
            line-height: 1.6;
            color: var(--subtext-color);
        }

        .info-summary-block .content strong {
            font-weight: 500;
            color: var(--text-color);
        }

        .edit-link {
            color: var(--primary-blue);
            text-decoration: none;
            font-weight: 500;
            font-size: 13px;
            cursor: pointer;
        }

        .edit-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

<form id="checkout-form" method="post" novalidate>
    <header class="checkout-header">
        <div class="header-main">
            <a href="cart.jsp" class="back-arrow"><i class="fa-solid fa-arrow-left"></i></a>
            <h1 class="title" id="header-title">Thông tin</h1>
        </div>
        <div class="checkout-steps">
            <span class="step active" id="step1-indicator">1. THÔNG TIN</span>
            <span class="step" id="step2-indicator">2. THANH TOÁN</span>
        </div>
    </header>

    <div class="container-fluid">
        <c:if test="${not empty errorMessage}">
            <div style="background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; border-radius: 5px; padding: 10px; margin: 20px 20px 0;">
                    ${errorMessage}
            </div>
            <% session.removeAttribute("errorMessage"); %>
        </c:if>

        <div class="checkout-container">
            <div class="checkout-left">
                <div class="card">
                    <h3 class="card-title">SẢN PHẨM</h3>
                    <c:choose>
                        <c:when test="${not empty sessionScope.cart and not empty sessionScope.cart.items}">
                            <c:forEach var="cartItem" items="${sessionScope.cart.items}">
                                <div class="product-summary-item">
                                    <c:url value="${cartItem.product.imageUrl}" var="fullImageUrl"/>
                                    <img src="${pageContext.request.contextPath}/${cartItem.product.imageUrl}"
                                         alt="${cartItem.product.productName}">
                                    <div class="product-summary-info">
                                        <div class="name">${cartItem.product.productName}</div>
                                        <div class="price"><fmt:formatNumber value="${cartItem.price}" type="currency"
                                                                             currencySymbol="₫"/></div>
                                    </div>
                                    <div class="product-summary-quantity">Số lượng: ${cartItem.quantity}</div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-cart-section">
                                <p>Giỏ hàng của bạn đang trống.</p>
                                <a href="product-list.jsp" class="btn-shop-now">Tiếp tục mua sắm</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

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
                            <div>
                                <input type="checkbox" id="promo-email" name="promo-email">
                                <label for="promo-email" style="cursor:pointer;">Nhận email thông báo và ưu đãi từ
                                    CellphoneS</label>
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
                        <div class="form-group">
                            <p>Quý khách có muốn xuất hóa đơn công ty không?</p>
                            <div class="vat-options">
                                <label><input type="radio" name="vat_invoice" value="no" checked> Không</label>
                                <label><input type="radio" name="vat_invoice" value="yes"> Có</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="step1-summary-view">
                    <div class="card info-summary-card">
                        <div class="info-summary-block">
                            <div class="title-wrapper">
                                <h4>Thông tin khách hàng</h4>
                                <span class="edit-link" id="edit-customer-info">Thay đổi</span>
                            </div>
                            <div class="content">
                                <strong id="summary-name"></strong><br>
                                <span id="summary-phone"></span> - <span id="summary-email"></span>
                            </div>
                        </div>
                        <hr style="border: none; border-top: 1px solid var(--border-color); margin: 0;">
                        <div class="info-summary-block">
                            <div class="title-wrapper">
                                <h4>Địa chỉ nhận hàng</h4>
                                <span class="edit-link" id="edit-shipping-info">Thay đổi</span>
                            </div>
                            <div class="content">
                                <strong id="summary-address"></strong>
                                <div id="summary-note" style="margin-top: 5px; font-style: italic;"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card" id="payment-method-section" style="display: none;">
                    <h3 class="card-title">Phương Thức Thanh Toán</h3>
                    <div class="payment-method-option">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="cod" value="COD"
                                   checked>
                            <label class="form-check-label" for="cod">Thanh toán khi nhận hàng (COD)</label>
                        </div>
                    </div>
                    <div class="payment-method-option">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="vnpay" value="VNPAY">
                            <label class="form-check-label" for="vnpay">Thanh toán qua mã QR PayOS</label>
                        </div>
                    </div>
                </div>
            </div>

            <div class="checkout-right">
                <div class="card">
                    <div class="order-summary">
                        <div class="summary-row">
                            <span class="label">Tổng tiền hàng</span>
                            <span class="value"><fmt:formatNumber value="${sessionScope.cart.rawTotal}" type="currency"
                                                                  currencySymbol="₫"/></span>
                        </div>
                        <div class="summary-row">
                            <span class="label">Phí vận chuyển</span>
                            <span class="value"><fmt:formatNumber value="${sessionScope.cart.shippingFee}"
                                                                  type="currency" currencySymbol="₫"/></span>
                        </div>
                        <div class="summary-row">
                            <span class="label">Chiết khấu Smember</span>
                            <span class="value" style="color: var(--primary-red);">- <fmt:formatNumber
                                    value="${sessionScope.cart.discount}" type="currency" currencySymbol="₫"/></span>
                        </div>
                        <div class="summary-row total">
                            <span class="label">Tổng tiền tạm tính</span>
                            <span class="value"><fmt:formatNumber value="${sessionScope.cart.finalTotal}"
                                                                  type="currency" currencySymbol="₫"/></span>
                        </div>
                    </div>
                    <div class="checkout-footer">
                        <div id="step1-button-wrapper" class="button-wrapper">
                            <button type="button" id="next-step-btn" class="btn-submit">Tiếp tục</button>
                        </div>
                        <div id="step2-button-wrapper" class="button-wrapper" style="display: none;">
                            <label class="terms-label">
                                <input type="checkbox" id="terms-checkbox">
                                <span>Tôi đồng ý với các <a href="dieu-khoan-giao-dich.html" target="_blank">điều khoản và điều kiện giao dịch</a> của cửa hàng.</span>
                            </label>
                            <button type="submit" id="submit-order-btn" class="btn-submit" disabled>Thanh toán</button>
                            <button type="button" id="back-to-step1-btn" class="btn-secondary">Quay lại</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</form>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const checkoutForm = document.getElementById('checkout-form');
        const step1View = document.getElementById('step1-view');
        const step1SummaryView = document.getElementById('step1-summary-view');
        const paymentSection = document.getElementById('payment-method-section');
        const nextStepBtn = document.getElementById('next-step-btn');
        const backToStep1Btn = document.getElementById('back-to-step1-btn');
        const headerTitle = document.getElementById('header-title');
        const step1Indicator = document.getElementById('step1-indicator');
        const step2Indicator = document.getElementById('step2-indicator');
        const step1ButtonWrapper = document.getElementById('step1-button-wrapper');
        const step2ButtonWrapper = document.getElementById('step2-button-wrapper');
        const termsCheckbox = document.getElementById('terms-checkbox');
        const submitBtn = document.getElementById('submit-order-btn');
        const recipientName = document.getElementById('recipientName');
        const recipientPhone = document.getElementById('recipientPhone');
        const recipientEmail = document.getElementById('recipientEmail');
        const shippingAddress = document.getElementById('shippingAddress');
        const shippingCity = document.getElementById('shippingCity');
        const shippingDistrict = document.getElementById('shippingDistrict');
        const shippingWard = document.getElementById('shippingWard');
        const note = document.querySelector('textarea[name="note"]');
        const summaryName = document.getElementById('summary-name');
        const summaryPhone = document.getElementById('summary-phone');
        const summaryEmail = document.getElementById('summary-email');
        const summaryAddress = document.getElementById('summary-address');
        const summaryNote = document.getElementById('summary-note');
        const editCustomerInfo = document.getElementById('edit-customer-info');
        const editShippingInfo = document.getElementById('edit-shipping-info');
        const cartIsEmpty = !(${not empty sessionScope.cart and not empty sessionScope.cart.items});

        const showError = (input, message) => {
            input.classList.add('is-invalid');
            const errorDiv = document.getElementById(input.id + 'Error');
            if (errorDiv) errorDiv.textContent = message;
        };
        const clearError = (input) => {
            input.classList.remove('is-invalid');
            const errorDiv = document.getElementById(input.id + 'Error');
            if (errorDiv) errorDiv.textContent = '';
        };
        const validateName = () => {
            clearError(recipientName);
            const value = recipientName.value.trim();
            const unicodeNameRegex = /^[\p{L}\s]+$/u;
            if (value === '') {
                showError(recipientName, 'Vui lòng nhập họ và tên.');
                return false;
            }
            if (value.length < 2) {
                showError(recipientName, 'Họ và tên phải có ít nhất 2 ký tự.');
                return false;
            }
            if (!unicodeNameRegex.test(value)) {
                showError(recipientName, 'Tên chỉ được chứa chữ cái và khoảng trắng.');
                return false;
            }
            return true;
        };
        const validatePhone = () => {
            clearError(recipientPhone);
            const value = recipientPhone.value.trim();
            const phoneRegex = /^0\d{9}$/;
            if (value === '') {
                showError(recipientPhone, 'Vui lòng nhập số điện thoại.');
                return false;
            }
            if (!phoneRegex.test(value)) {
                showError(recipientPhone, 'Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0).');
                return false;
            }
            return true;
        };
        const validateEmail = () => {
            clearError(recipientEmail);
            const value = recipientEmail.value.trim();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (value === '') {
                showError(recipientEmail, 'Vui lòng nhập địa chỉ email.');
                return false;
            }
            if (!emailRegex.test(value)) {
                showError(recipientEmail, 'Địa chỉ email không đúng định dạng.');
                return false;
            }
            return true;
        };
        const validateRequiredField = (input, message) => {
            clearError(input);
            if (!input.value || input.value.trim() === '') {
                showError(input, message);
                return false;
            }
            return true;
        };

        function goToStep2() {
            const isNameValid = validateName();
            const isPhoneValid = validatePhone();
            const isEmailValid = validateEmail();
            const isAddressValid = validateRequiredField(shippingAddress, 'Vui lòng nhập địa chỉ.');
            const isCityValid = validateRequiredField(shippingCity, 'Vui lòng chọn Tỉnh/Thành phố.');
            const isDistrictValid = validateRequiredField(shippingDistrict, 'Vui lòng chọn Quận/Huyện.');
            const isWardValid = validateRequiredField(shippingWard, 'Vui lòng chọn Phường/Xã.');

            if (!isNameValid || !isPhoneValid || !isEmailValid || !isAddressValid || !isCityValid || !isDistrictValid || !isWardValid) {
                const firstInvalidField = checkoutForm.querySelector('.is-invalid');
                if (firstInvalidField) {
                    firstInvalidField.focus();
                    firstInvalidField.scrollIntoView({behavior: 'smooth', block: 'center'});
                }
                return;
            }

            summaryName.textContent = recipientName.value;
            summaryPhone.textContent = recipientPhone.value;
            summaryEmail.textContent = recipientEmail.value;

            const addressParts = [
                shippingAddress.value,
                shippingWard.value ? shippingWard.options[shippingWard.selectedIndex].text : '',
                shippingDistrict.value ? shippingDistrict.options[shippingDistrict.selectedIndex].text : '',
                shippingCity.value ? shippingCity.options[shippingCity.selectedIndex].text : ''
            ].filter(part => part && part.trim() !== '');

            const fullAddress = addressParts.join(', ');
            summaryAddress.textContent = fullAddress;

            if (note.value.trim()) {
                summaryNote.textContent = `Ghi chú: ${note.value.trim()}`;
                summaryNote.style.display = 'block';
            } else {
                summaryNote.style.display = 'none';
            }

            checkoutForm.classList.add('on-step-2');
            document.getElementById('payment-method-section').style.display = 'block';
            step1ButtonWrapper.style.display = 'none';
            step2ButtonWrapper.style.display = 'flex';
            headerTitle.textContent = 'Thanh toán';
            step1Indicator.classList.remove('active');
            step2Indicator.classList.add('active');
            window.scrollTo({top: 0, behavior: 'smooth'});
        }

        function goToStep1() {
            checkoutForm.classList.remove('on-step-2');
            document.getElementById('payment-method-section').style.display = 'none';
            step2ButtonWrapper.style.display = 'none';
            step1ButtonWrapper.style.display = 'flex';
            headerTitle.textContent = 'Thông tin';
            step2Indicator.classList.remove('active');
            step1Indicator.classList.add('active');
            window.scrollTo({top: 0, behavior: 'smooth'});
        }

        function handleFormSubmit(event) {
            event.preventDefault();
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span></span> Đang xử lý...';
            setTimeout(() => {
                const formData = new FormData(checkoutForm);
                const paymentMethod = formData.get('paymentMethod');
                if (paymentMethod === 'COD') {
                    checkoutForm.action = 'checkout';
                    checkoutForm.submit();
                } else if (paymentMethod === 'VNPAY') {
                    fetch('create-link', {
                        method: 'POST',
                        body: new URLSearchParams(formData)
                    })
                        .then(response => {
                            if (!response.ok) return response.json().then(err => {
                                throw new Error(err.error || 'Lỗi server')
                            });
                            return response.json();
                        })
                        .then(data => {
                            if (data && data.checkoutUrl) {
                                window.location.href = data.checkoutUrl;
                            } else {
                                throw new Error('Không nhận được link thanh toán.');
                            }
                        })
                        .catch(error => {
                            console.error('Fetch Error:', error);
                            alert('Lỗi: ' + error.message);
                            submitBtn.disabled = false;
                            submitBtn.innerHTML = 'Thanh toán';
                        });
                }
            }, 100);
        }

        if (nextStepBtn) nextStepBtn.addEventListener('click', goToStep2);
        if (backToStep1Btn) backToStep1Btn.addEventListener('click', goToStep1);
        if (editCustomerInfo) editCustomerInfo.addEventListener('click', goToStep1);
        if (editShippingInfo) editShippingInfo.addEventListener('click', goToStep1);

        if (checkoutForm) checkoutForm.addEventListener('submit', handleFormSubmit);
        if (termsCheckbox) termsCheckbox.addEventListener('change', function () {
            submitBtn.disabled = !this.checked;
        });

        if (cartIsEmpty) {
            if (step1ButtonWrapper) step1ButtonWrapper.style.display = 'none';
            if (step1View) step1View.style.display = 'none';
        }

        recipientName.addEventListener('blur', validateName);
        recipientPhone.addEventListener('blur', validatePhone);
        recipientEmail.addEventListener('blur', validateEmail);
        shippingAddress.addEventListener('blur', () => validateRequiredField(shippingAddress, 'Vui lòng nhập địa chỉ.'));
        shippingCity.addEventListener('change', () => validateRequiredField(shippingCity, 'Vui lòng chọn Tỉnh/Thành phố.'));
        shippingDistrict.addEventListener('change', () => validateRequiredField(shippingDistrict, 'Vui lòng chọn Quận/Huyện.'));
        shippingWard.addEventListener('change', () => validateRequiredField(shippingWard, 'Vui lòng chọn Phường/Xã.'));
    });
</script>

</body>
</html>