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

        /* ---- CẬP NHẬT: STYLE LẠI KHỐI HIỂN THỊ SẢN PHẨM ---- */
        /* Tìm và thay thế toàn bộ khối CSS cho .product-summary-item bằng khối này */

        .product-summary-item {
            display: flex; /* Bật Flexbox để căn chỉnh */
            align-items: center; /* Căn giữa tất cả các phần tử theo chiều dọc */
            gap: 15px;
            margin-bottom: 15px;
            padding-bottom: 15px; /* Thêm padding dưới để tạo khoảng cách */
            border-bottom: 1px solid var(--border-color); /* Thêm đường kẻ phân cách */
        }

        /* Ẩn đường kẻ của sản phẩm cuối cùng để không bị thừa */
        .product-summary-item:last-child {
            margin-bottom: 0;
            padding-bottom: 0;
            border-bottom: none;
        }

        .product-summary-item img {
            width: 60px;
            height: 60px;
            object-fit: contain;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            flex-shrink: 0; /* Ngăn ảnh bị co lại khi tên sản phẩm quá dài */
        }

        /* Khối tên và giá sản phẩm */
        .product-summary-info {
            /* Khối này sẽ tự động chiếm không gian ở giữa */
            flex-grow: 1;
        }

        .product-summary-info .name {
            font-weight: 500;
            margin-bottom: 5px;
            line-height: 1.4; /* Cải thiện hiển thị nếu tên sản phẩm dài và xuống dòng */
        }

        .product-summary-info .price {
            font-weight: 700;
            color: var(--primary-red);
        }

        /* Khối "Số lượng" - Điểm chính của sự thay đổi */
        .product-summary-quantity {
            /* Đẩy khối này sang hẳn bên phải */
            margin-left: auto;
            /* Style lại cho văn bản phụ */
            font-size: 13px;
            color: var(--subtext-color);
            flex-shrink: 0; /* Ngăn không bị co lại */
            padding-left: 15px; /* Thêm khoảng đệm trái để không bị dính vào tên sản phẩm */
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

        /* ----- LOGIC CSS CHO VIỆC CHUYỂN STEP ----- */
        /* Mặc định (Step 1) */
        #step1-summary-view {
            display: none;
        }

        #payment-method-section {
            display: none;
        }

        #step2-button-wrapper {
            display: none;
        }

        /* Khi ở Step 2 (thêm class on-step-2 vào form) */
        #checkout-form.on-step-2 #step1-view {
            display: none;
        }

        #checkout-form.on-step-2 #step1-summary-view {
            display: block;
        }

        #checkout-form.on-step-2 #payment-method-section {
            display: block;
        }

        #checkout-form.on-step-2 #step1-button-wrapper {
            display: none;
        }

        #checkout-form.on-step-2 #step2-button-wrapper {
            display: flex;
        }

        /* ----- HẾT LOGIC CSS ----- */

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

        /* ----- CẬP NHẬT CUỐI CÙNG: CSS CHO VOUCHER (Đồng bộ màu đỏ) ----- */
        /* Thay thế khối CSS voucher cũ bằng khối này */
        .voucher-section {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid var(--border-color);
        }

        .voucher-input-group {
            display: flex;
            gap: 10px;
        }

        .voucher-input-group input {
            flex-grow: 1;
        }

        .btn-apply-voucher {
            /* THAY ĐỔI: Chuyển sang màu đỏ đồng bộ với web */
            background-color: var(--primary-red);
            color: white;
            border: none;
            padding: 12px 20px;
            font-size: 14px;
            font-weight: 500;
            border-radius: 8px;
            cursor: pointer;
            white-space: nowrap;
            transition: opacity 0.2s;
        }

        .btn-apply-voucher:hover {
            opacity: 0.9;
        }

        .btn-apply-voucher:disabled {
            /* THAY ĐỔI: Màu khi bị vô hiệu hóa cũng theo tone đỏ */
            background-color: #f07f89;
            opacity: 0.8;
            cursor: not-allowed;
        }

        .voucher-message {
            font-size: 13px;
            margin-top: 8px;
            min-height: 15px;
        }

        .voucher-message.success {
            color: var(--primary-green, #28a745);
        }

        .voucher-message.error {
            color: var(--primary-red);
        }

        /* Giữ nguyên CSS để ẩn/hiện dòng giảm giá */
        #voucher-discount-row {
            display: none;
        }

        /* ----- BỔ SUNG: CSS CHO GIAO DIỆN GIỎ HÀNG TRỐNG ----- */

        /* Định dạng lại container chính khi giỏ hàng trống */
        .checkout-container.is-empty {
            justify-content: center; /* Căn giữa nội dung theo chiều ngang */
            padding-top: 40px; /* Thêm khoảng trống ở trên cho thoáng */
            padding-bottom: 40px;
        }

        /* Định dạng lại cột bên trái khi giỏ hàng trống */
        .checkout-container.is-empty .checkout-left {
            flex: none; /* Bỏ thuộc tính co giãn */
            width: 100%;
            max-width: 550px; /* Giới hạn độ rộng tối đa để không bị bè ngang */
        }

        /* Style lại phần thông báo giỏ hàng trống */
        .empty-cart-section {
            padding: 40px 20px; /* Tăng padding để có nhiều không gian hơn */
            text-align: center;
        }

        /* Bổ sung icon giỏ hàng lớn ở trên */
        .empty-cart-section::before {
            content: '\f07a'; /* Mã Unicode của icon fa-shopping-cart */
            font-family: 'Font Awesome 6 Free';
            font-weight: 900; /* Cần thiết để icon hiển thị đúng */
            font-size: 60px;
            color: #e0e0e0; /* Màu xám nhạt, không quá nổi bật */
            display: block;
            margin-bottom: 25px;
        }

        .empty-cart-section p {
            font-size: 16px; /* Cho chữ to hơn một chút */
            color: var(--subtext-color);
            margin-bottom: 25px;
        }

        /* Style lại nút cho đẹp hơn trong ngữ cảnh này */
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
            transition: opacity 0.2s;
        }

        .btn-shop-now:hover {
            opacity: 0.9;
        }

        /* ---- BỔ SUNG: CHỐNG "NHÁY" TRANG ---- */
        /* Mặc định ẩn nội dung chính đi */
        .checkout-container {
            visibility: hidden;
            opacity: 0;
            transition: opacity 0.2s ease-in-out;
        }

        /* Thêm class này bằng JS sau khi đã kiểm tra xong để hiển thị nội dung */
        .checkout-container.js-loaded {
            visibility: visible;
            opacity: 1;
        }

        /* ---- BỔ SUNG: CSS cho nút Scroll to Top ---- */
        #scrollToTopBtn {
            /* Trạng thái mặc định: ẩn đi */
            opacity: 0;
            visibility: hidden;

            /* Vị trí cố định ở góc dưới bên phải */
            position: fixed;
            bottom: 30px;
            right: 30px;
            z-index: 999;

            /* Thiết kế nút */
            background-color: var(--primary-red); /* Dùng màu đỏ chủ đạo của bạn */
            color: white;
            border: none;
            outline: none;
            cursor: pointer;

            /* Kích thước và hình dạng */
            width: 45px;
            height: 45px;
            border-radius: 50%; /* Bo tròn thành hình tròn */

            /* Căn giữa icon */
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;

            /* Hiệu ứng và bóng đổ */
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            transition: opacity 0.3s, visibility 0.3s, background-color 0.3s;
        }

        #scrollToTopBtn:hover {
            background-color: #a70012; /* Màu đỏ sậm hơn khi di chuột qua */
        }

        /* Class 'show' sẽ được thêm bằng JS để nút hiện ra */
        #scrollToTopBtn.show {
            opacity: 1;
            visibility: visible;
        }
    </style>
</head>
<body>

<%@ include file="layout/navbar.jsp" %>

<%-- THAY ĐỔI: Thêm class="on-step-2" để mặc định hiển thị ở bước thanh toán --%>
<form id="checkout-form" method="post" novalidate class="on-step-2">
    <header class="checkout-header">
        <div class="header-main">
            <!-- ======================= ĐƯỜNG DẪN ĐÃ SỬA ======================= -->
            <a href="${pageContext.request.contextPath}/cart" class="back-arrow"><i class="fa-solid fa-arrow-left"></i></a>
            <!-- ================================================================ -->
            <h1 class="title" id="header-title">Thanh toán</h1>
        </div>
        <div class="checkout-steps">
            <span class="step" id="step1-indicator">1. THÔNG TIN</span>
            <span class="step active" id="step2-indicator">2. THANH TOÁN</span>
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

                <%-- View này chứa form nhập liệu, mặc định sẽ bị ẩn đi --%>
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
                                    PhoneHub</label>
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

                <%-- View này hiển thị thông tin tóm tắt, mặc định sẽ hiện ra --%>
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

                <%-- Mục thanh toán, mặc định sẽ hiện ra --%>
                <div class="card" id="payment-method-section">
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
                            <input class="form-check-input" type="radio" name="paymentMethod" id="payos" value="PAYOS">
                            <label class="form-check-label" for="vnpay">Thanh toán qua mã QR PayOS</label>
                        </div>
                    </div>
                    <div class="payment-method-option">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="vnpay" value="VNPAY">
                            <label class="form-check-label" for="vnpay">Thanh toán qua VNPAY (Thẻ ATM/Visa/QR)</label>
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
                        <div class="summary-row" id="voucher-discount-row">
                            <span class="label">Giảm giá Voucher</span>
                            <span class="value" id="voucher-discount-value" style="color: var(--primary-red);"></span>
                        </div>

                        <div class="summary-row total">
                            <span class="label">Tổng cộng</span>
                            <span class="value" id="final-total-value"><fmt:formatNumber
                                    value="${sessionScope.cart.finalTotal}"
                                    type="currency" currencySymbol="₫"/></span>
                        </div>
                    </div>
                    <%-- BỔ SUNG: Khu vực nhập voucher --%>
                    <div class="voucher-section">
                        <div class="voucher-input-group">
                            <input type="text" id="voucher-code-input" name="voucher_code"
                                   placeholder="Nhập mã giảm giá">
                            <button type="button" class="btn-apply-voucher" id="apply-voucher-btn">Áp dụng</button>
                        </div>
                        <div id="voucher-message" class="voucher-message"></div>
                    </div>

                    <div class="checkout-footer">
                        <%-- Nút cho Step 1 (Chỉnh sửa thông tin) --%>
                        <div id="step1-button-wrapper" class="button-wrapper">
                            <button type="button" id="next-step-btn" class="btn-submit">Tiếp tục</button>
                        </div>
                        <%-- Nút cho Step 2 (Thanh toán) --%>
                        <div id="step2-button-wrapper" class="button-wrapper">
                            <label class="terms-label">
                                <input type="checkbox" id="terms-checkbox">
                                <span>Tôi đồng ý với các <a href="dieu-khoan-giao-dich.html" target="_blank">điều khoản và điều kiện giao dịch</a> của cửa hàng.</span>
                            </label>
                            <button type="submit" id="submit-order-btn" class="btn-submit" disabled>Thanh toán</button>
                            <%--<button type="button" id="back-to-step1-btn" class="btn-secondary">Quay lại</button>--%>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</form>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        // --- CÁC BIẾN DOM ---
        const checkoutForm = document.getElementById('checkout-form');
        const nextStepBtn = document.getElementById('next-step-btn');
        <%--const backToStep1Btn = document.getElementById('back-to-step1-btn');--%>
        const headerTitle = document.getElementById('header-title');
        const step1Indicator = document.getElementById('step1-indicator');
        const step2Indicator = document.getElementById('step2-indicator');
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


        const voucherInput = document.getElementById('voucher-code-input');
        const applyVoucherBtn = document.getElementById('apply-voucher-btn');
        const voucherMessage = document.getElementById('voucher-message');
        const voucherDiscountRow = document.getElementById('voucher-discount-row');
        const voucherDiscountValue = document.getElementById('voucher-discount-value');
        const finalTotalValue = document.getElementById('final-total-value');

        const checkoutContainer = document.querySelector('.checkout-container');

        const scrollToTopBtn = document.getElementById('scrollToTopBtn');

        // Hàm kiểm tra vị trí cuộn để hiện/ẩn nút
        function scrollFunction() {
            // Hiện nút nếu người dùng cuộn xuống hơn 200px
            if (document.body.scrollTop > 200 || document.documentElement.scrollTop > 200) {
                scrollToTopBtn.classList.add('show');
            } else {
                scrollToTopBtn.classList.remove('show');
            }
        }

// Hàm xử lý việc cuộn lên đầu trang một cách mượt mà
        function backToTop() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth' // Hiệu ứng cuộn mượt
            });
        }

// Gán sự kiện scroll cho cửa sổ trình duyệt
        window.onscroll = function () {
            scrollFunction();
        };

        // BỔ SUNG: Hàm xử lý áp dụng voucher
        function handleApplyVoucher() {
            const code = voucherInput.value.trim().toUpperCase();
            if (!code) {
                voucherMessage.textContent = 'Vui lòng nhập mã voucher.';
                voucherMessage.className = 'voucher-message error';
                return;
            }

            applyVoucherBtn.disabled = true;
            applyVoucherBtn.textContent = 'Đang...';
            voucherMessage.textContent = '';

            // --- LOGIC GỌI BACKEND ---
            // Bạn cần tạo một Servlet (ví dụ: /apply-voucher) để xử lý logic này.
            // Servlet nhận mã voucher, kiểm tra và trả về JSON.

            const formData = new FormData(checkoutForm);
            // formData.append('voucherCode', code); // name="voucher_code" đã có trong form

            // Thay 'apply-voucher-url' bằng URL thực tế của bạn
            fetch('apply-voucher-url', {
                method: 'POST',
                body: new URLSearchParams(formData)
            })
                .then(response => {
                    if (!response.ok) {
                        // Thử parse lỗi từ JSON backend trả về
                        return response.json().then(err => {
                            throw new Error(err.message || 'Mã voucher không hợp lệ hoặc đã hết hạn')
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    // Backend cần trả về JSON có cấu trúc như sau khi thành công:
                    // { success: true, discountAmount: 50000, newFinalTotal: 29450000, message: "Áp dụng thành công!" }
                    if (data.success) {
                        voucherMessage.textContent = data.message;
                        voucherMessage.className = 'voucher-message success';

                        voucherDiscountRow.style.display = 'flex'; // Hiện dòng giảm giá
                        voucherDiscountValue.textContent = `- ${formatter.format(data.discountAmount)}`;
                        finalTotalValue.textContent = formatter.format(data.newFinalTotal);

                        voucherInput.disabled = true; // Vô hiệu hóa input sau khi áp dụng
                        applyVoucherBtn.textContent = 'Đã áp dụng';
                    } else {
                        throw new Error(data.message || 'Mã voucher không hợp lệ');
                    }
                })
                .catch(error => {
                    voucherMessage.textContent = error.message;
                    voucherMessage.className = 'voucher-message error';
                    applyVoucherBtn.disabled = false;
                    applyVoucherBtn.textContent = 'Áp dụng';
                });
        }

// BỔ SUNG: Helper để format tiền tệ
        const formatter = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'});

        // --- CÁC HÀM VALIDATE ---
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

        // --- LOGIC CHÍNH ĐÃ SỬA ---

        /**
         * Kiểm tra "âm thầm" xem tất cả thông tin có hợp lệ hay không.
         * Không hiển thị lỗi ra giao diện, chỉ trả về true/false.
         */
        function isAllInformationValid() {
            const isNameOk = /^[\p{L}\s]{2,50}$/u.test(recipientName.value.trim());
            const isPhoneOk = /^0\d{9}$/.test(recipientPhone.value.trim());
            const isEmailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(recipientEmail.value.trim());
            const isAddressOk = shippingAddress.value.trim() !== '';
            const isCityOk = shippingCity.value.trim() !== '';
            const isDistrictOk = shippingDistrict.value.trim() !== '';
            const isWardOk = shippingWard.value.trim() !== '';

            return isNameOk && isPhoneOk && isEmailOk && isAddressOk && isCityOk && isDistrictOk && isWardOk;
        }

        /**
         * Hàm trung tâm để quản lý trạng thái của nút "Thanh toán".
         */
        function updateSubmitButtonState() {
            const infoValid = isAllInformationValid();
            const termsChecked = termsCheckbox.checked;
            submitBtn.disabled = !(infoValid && termsChecked);
        }

        // Cập nhật thông tin tóm tắt
        function updateSummaryView() {
            summaryName.textContent = recipientName.value || 'Chưa có thông tin';
            summaryPhone.textContent = recipientPhone.value || 'Chưa có thông tin';
            summaryEmail.textContent = recipientEmail.value || 'Chưa có thông tin';

            const addressParts = [
                shippingAddress.value,
                shippingWard.value ? shippingWard.options[shippingWard.selectedIndex].text : '',
                shippingDistrict.value ? shippingDistrict.options[shippingDistrict.selectedIndex].text : '',
                shippingCity.value ? shippingCity.options[shippingCity.selectedIndex].text : ''
            ].filter(part => part && part.trim() !== '' && part.trim() !== '-- Chọn Phường/Xã --' && part.trim() !== '-- Chọn Quận/Huyện --' && part.trim() !== '-- Chọn Tỉnh/Thành phố --');

            if (addressParts.length > 0) {
                summaryAddress.textContent = addressParts.join(', ');
            } else {
                summaryAddress.textContent = 'Chưa có thông tin';
            }


            if (note.value.trim()) {
                summaryNote.textContent = `Ghi chú: ${note.value.trim()}`;
                summaryNote.style.display = 'block';
            } else {
                summaryNote.style.display = 'none';
            }
        }

        // Chuyển sang màn hình Step 2 (Thanh toán)
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

            updateSummaryView();

            checkoutForm.classList.add('on-step-2');
            headerTitle.textContent = 'Thanh toán';
            step1Indicator.classList.remove('active');
            step2Indicator.classList.add('active');
            window.scrollTo({top: 0, behavior: 'smooth'});

            // Sau khi quay lại Step 2 thành công, kiểm tra lại trạng thái nút submit.
            updateSubmitButtonState();
        }

        // Quay về màn hình Step 1 (Chỉnh sửa thông tin)
        function goToStep1() {
            checkoutForm.classList.remove('on-step-2');
            headerTitle.textContent = 'Thông tin';
            step2Indicator.classList.remove('active');
            step1Indicator.classList.add('active');
            window.scrollTo({top: 0, behavior: 'smooth'});
        }

        // Xử lý khi submit form
        function handleFormSubmit(event) {
            event.preventDefault();

            // Kiểm tra lần cuối cùng trước khi gửi
            if (!isAllInformationValid() || !termsCheckbox.checked) {
                // Hiển thị thông báo nếu người dùng cố tình submit khi nút bị vô hiệu hóa (ví dụ: qua console)
                alert('Vui lòng cung cấp đầy đủ thông tin và đồng ý với điều khoản giao dịch.');
                return;
            }

            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span></span> Đang xử lý...';

            setTimeout(() => {
                const formData = new FormData(checkoutForm);
                const paymentMethod = formData.get('paymentMethod');
                if (paymentMethod === 'COD') {
                    checkoutForm.action = 'checkout';
                    checkoutForm.submit();
                } else if (paymentMethod === 'PAYOS') {
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
                            submitBtn.disabled = !termsCheckbox.checked;
                            submitBtn.innerHTML = 'Thanh toán';
                        });
                } else if (paymentMethod === 'VNPAY') {
                    fetch('create-vnpay-link', {
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
                            if (data && data.paymentUrl) {
                                window.location.href = data.paymentUrl;
                            } else {
                                throw new Error('Không nhận được link thanh toán VNPAY.');
                            }
                        })
                        .catch(error => {
                            console.error('Lỗi tạo link VNPAY:', error);
                            alert('Lỗi: ' + error.message);
                            submitBtn.disabled = false;
                            submitBtn.innerHTML = 'Thanh toán';
                        });
                }
            }, 100);
        }

        // --- GÁN SỰ KIỆN ---

        scrollToTopBtn.addEventListener('click', backToTop);

        // BỔ SUNG: Gán sự kiện cho nút voucher
        if (applyVoucherBtn) applyVoucherBtn.addEventListener('click', handleApplyVoucher);

        if (nextStepBtn) nextStepBtn.addEventListener('click', goToStep2);
        // if (backToStep1Btn) backToStep1Btn.addEventListener('click', goToStep1);
        if (editCustomerInfo) editCustomerInfo.addEventListener('click', goToStep1);
        if (editShippingInfo) editShippingInfo.addEventListener('click', goToStep1);

        if (checkoutForm) checkoutForm.addEventListener('submit', handleFormSubmit);

        // Khi checkbox thay đổi, gọi hàm kiểm tra tổng hợp.
        if (termsCheckbox) {
            termsCheckbox.addEventListener('change', updateSubmitButtonState);
        }

        if (cartIsEmpty) {
            const checkoutRightColumn = document.querySelector('.checkout-right');

            // Thêm class 'is-empty' để kích hoạt CSS cho giao diện giỏ hàng trống
            if (checkoutContainer) {
                checkoutContainer.classList.add('is-empty');
            }

            // Ẩn cột bên phải đi
            if (checkoutRightColumn) {
                checkoutRightColumn.style.display = 'none';
            }

            // Ẩn các thành phần không cần thiết khác
            if (document.getElementById('step1-view')) document.getElementById('step1-view').style.display = 'none';
            if (document.getElementById('step1-summary-view')) document.getElementById('step1-summary-view').style.display = 'none';
            if (document.getElementById('payment-method-section')) document.getElementById('payment-method-section').style.display = 'none';
        } else {
            // Nếu giỏ hàng KHÔNG trống, thì chỉ cần chạy logic khởi tạo ban đầu
            // (Trong trường hợp của bạn, bạn muốn nó ở Step 2, nên logic này đảm bảo nó hoạt động đúng)
            updateSummaryView();
            updateSubmitButtonState();
        }

// BƯỚC CUỐI CÙNG VÀ QUAN TRỌNG NHẤT:
// Sau khi tất cả logic kiểm tra (if/else) đã hoàn tất,
// thêm class 'js-loaded' để hiển thị nội dung ra màn hình.
        if (checkoutContainer) {
            checkoutContainer.classList.add('js-loaded');
        }

        // Gán sự kiện validate khi người dùng rời khỏi ô input
        recipientName.addEventListener('blur', validateName);
        recipientPhone.addEventListener('blur', validatePhone);
        recipientEmail.addEventListener('blur', validateEmail);
        shippingAddress.addEventListener('blur', () => validateRequiredField(shippingAddress, 'Vui lòng nhập địa chỉ.'));
        shippingCity.addEventListener('change', () => validateRequiredField(shippingCity, 'Vui lòng chọn Tỉnh/Thành phố.'));
        shippingDistrict.addEventListener('change', () => validateRequiredField(shippingDistrict, 'Vui lòng chọn Quận/Huyện.'));
        shippingWard.addEventListener('change', () => validateRequiredField(shippingWard, 'Vui lòng chọn Phường/Xã.'));

        // --- KHỞI TẠO BAN ĐẦU ---

        // Cập nhật thông tin tóm tắt ngay khi tải trang
        updateSummaryView();

        // Ngay khi trang tải xong, gọi hàm kiểm tra tổng hợp để đặt trạng thái ban đầu cho nút "Thanh toán".
        updateSubmitButtonState();
    });
</script>

<!-- BỔ SUNG: Nút Scroll back to top -->
<button id="scrollToTopBtn" title="Lên đầu trang">
    <i class="fa-solid fa-arrow-up"></i>
</button>

</body>
</html>