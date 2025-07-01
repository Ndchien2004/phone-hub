<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn hàng #${order.orderId} - CellphoneS</title>
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
            max-width: 1200px;
            margin: 20px auto;
            padding: 0 20px;
        }

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
        }

        .breadcrumb a:hover {
            text-decoration: underline;
        }

        .page-header {
            background: white;
            padding: 25px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .page-title {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 15px;
        }

        .page-title h1 {
            color: #d70018;
            font-size: 28px;
        }

        .order-status {
            padding: 8px 16px;
            border-radius: 25px;
            font-weight: 500;
            font-size: 14px;
        }

        .status-pending {
            background: #fff3cd;
            color: #856404;
        }

        .status-processing {
            background: #d4edda;
            color: #155724;
        }

        .status-shipped {
            background: #cce4f7;
            color: #004085;
        }

        .status-delivered {
            background: #d1ecf1;
            color: #0c5460;
        }

        .status-cancelled {
            background: #f8d7da;
            color: #721c24;
        }

        .status-default {
            background: #e7f3ff;
            color: #0066cc;
        }

        .order-meta {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            color: #666;
        }

        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .main-content {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 20px;
        }

        .order-items-section {
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .section-header {
            background: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #eee;
        }

        .section-header h2 {
            color: #333;
            font-size: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .section-content {
            padding: 20px;
        }

        .product-item {
            display: flex;
            gap: 20px;
            padding: 20px;
            border: 1px solid #eee;
            border-radius: 8px;
            margin-bottom: 15px;
            background: #fafafa;
            transition: all 0.3s ease;
        }

        .product-item:hover {
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
        }

        .product-image {
            width: 120px;
            height: 120px;
            border-radius: 8px;
            object-fit: cover;
            border: 1px solid #ddd;
            background: white;
        }

        .product-details {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .product-name {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }

        .product-specs {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 10px;
            margin: 10px 0;
        }

        .spec-item {
            background: white;
            padding: 8px 12px;
            border-radius: 6px;
            border: 1px solid #e0e0e0;
            font-size: 13px;
        }

        .spec-label {
            font-weight: 500;
            color: #666;
        }

        .spec-value {
            color: #333;
            margin-left: 5px;
        }

        .product-pricing {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #eee;
        }

        .price-info {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }

        .price-original {
            color: #999;
            text-decoration: line-through;
            font-size: 14px;
        }

        .price-sale {
            color: #28a745;
            font-weight: 500;
            font-size: 16px;
        }

        .price-order {
            color: #d70018;
            font-weight: bold;
            font-size: 16px;
        }

        .quantity-total {
            text-align: right;
        }

        .quantity {
            color: #666;
            font-size: 14px;
            margin-bottom: 5px;
        }

        .total-price {
            color: #d70018;
            font-weight: bold;
            font-size: 18px;
        }

        .order-summary {
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            height: fit-content;
        }

        .summary-content {
            padding: 20px;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;
        }

        .summary-row:last-child {
            border-bottom: none;
            border-top: 2px solid #d70018;
            margin-top: 10px;
            padding-top: 15px;
            font-weight: bold;
            font-size: 18px;
            color: #d70018;
        }

        .summary-label {
            color: #666;
            font-weight: 500;
        }

        .summary-value {
            font-weight: 600;
            color: #333;
        }

        .customer-info {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            margin-top: 20px;
        }

        .customer-details {
            padding: 20px;
        }

        .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #f0f0f0;
        }

        .info-row:last-child {
            border-bottom: none;
        }

        .info-label {
            color: #666;
            font-weight: 500;
            min-width: 120px;
        }

        .info-value {
            color: #333;
            text-align: right;
            flex: 1;
        }

        .actions {
            margin-top: 20px;
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 10px 20px;
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
        }

        .btn-primary {
            background: #d70018;
            color: white;
        }

        .btn-primary:hover {
            background: #b50015;
        }

        .btn-danger {
            background: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background: #c82333;
            transform: translateY(-1px);
        }

        .btn-outline {
            background: transparent;
            color: #d70018;
            border: 1px solid #d70018;
        }

        .btn-outline:hover {
            background: #d70018;
            color: white;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #545b62;
        }

        /* Modal styles */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            animation: fadeIn 0.3s ease;
        }

        .modal-content {
            background-color: white;
            margin: 15% auto;
            padding: 0;
            border-radius: 12px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
            animation: slideIn 0.3s ease;
            overflow: hidden;
        }

        .modal-header {
            background: linear-gradient(135deg, #dc3545, #c82333);
            color: white;
            padding: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .modal-header h3 {
            margin: 0;
            font-size: 20px;
        }

        .modal-body {
            padding: 25px;
            text-align: center;
        }

        .modal-body p {
            font-size: 16px;
            margin-bottom: 20px;
            color: #666;
            line-height: 1.6;
        }

        .modal-body .order-info {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            margin: 15px 0;
            border-left: 4px solid #d70018;
        }

        .modal-footer {
            padding: 20px 25px;
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            background: #f8f9fa;
        }

        .close {
            color: white;
            float: right;
            font-size: 24px;
            font-weight: bold;
            cursor: pointer;
            margin-left: auto;
        }

        .close:hover {
            opacity: 0.7;
        }

        /* Toast styles */
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

        /* Animations */
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        @keyframes slideIn {
            from { transform: translateY(-50px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }

        @keyframes slideInRight {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        @keyframes slideOutRight {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }

        /* Loading spinner */
        .spinner {
            border: 2px solid #f3f3f3;
            border-top: 2px solid #d70018;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            animation: spin 1s linear infinite;
            display: inline-block;
            margin-right: 8px;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* Order content styles */
        .order-content {
            background: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        @media (max-width: 768px) {
            .main-content {
                grid-template-columns: 1fr;
            }

            .product-item {
                flex-direction: column;
                text-align: center;
            }

            .product-specs {
                grid-template-columns: 1fr;
            }

            .product-pricing {
                flex-direction: column;
                align-items: center;
                gap: 15px;
            }

            .quantity-total {
                text-align: center;
            }

            .page-title {
                flex-direction: column;
                align-items: flex-start;
            }

            .order-meta {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <!-- Breadcrumb -->
    <nav class="breadcrumb">
        <a href="${pageContext.request.contextPath}/orders">
            <i class="fas fa-arrow-left"></i> Quay lại danh sách đơn hàng
        </a>
    </nav>

    <!-- Page Header -->
    <div class="page-header">
        <div class="page-title">
            <h1>
                <i class="fas fa-receipt"></i>
                Chi tiết đơn hàng #${order.orderId}
            </h1>
            <c:choose>
                <c:when test="${not empty order.status and not empty order.status.description}">
                    <c:set var="statusClass" value="status-default" />
                    <c:choose>
                        <c:when test="${fn:containsIgnoreCase(order.status.name, 'pending') or fn:containsIgnoreCase(order.status.description, 'chờ')}">
                            <c:set var="statusClass" value="status-pending" />
                        </c:when>
                        <c:when test="${fn:containsIgnoreCase(order.status.name, 'processing') or fn:containsIgnoreCase(order.status.description, 'xử lý')}">
                            <c:set var="statusClass" value="status-processing" />
                        </c:when>
                        <c:when test="${fn:containsIgnoreCase(order.status.name, 'shipped') or fn:containsIgnoreCase(order.status.description, 'giao')}">
                            <c:set var="statusClass" value="status-shipped" />
                        </c:when>
                        <c:when test="${fn:containsIgnoreCase(order.status.name, 'delivered') or fn:containsIgnoreCase(order.status.description, 'hoàn thành')}">
                            <c:set var="statusClass" value="status-delivered" />
                        </c:when>
                        <c:when test="${fn:containsIgnoreCase(order.status.name, 'cancelled') or fn:containsIgnoreCase(order.status.description, 'hủy')}">
                            <c:set var="statusClass" value="status-cancelled" />
                        </c:when>
                    </c:choose>
                    <span class="order-status ${statusClass}">
                        <i class="fas fa-info-circle"></i>
                        ${order.status.description}
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="order-status status-default">
                        <i class="fas fa-info-circle"></i>
                        Chưa xác định
                    </span>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="order-meta">
            <div class="meta-item">
                <i class="fas fa-calendar"></i>
                <span>Ngày đặt: ${order.formattedOrderDate}</span>
            </div>
            <div class="meta-item">
                <i class="fas fa-credit-card"></i>
                <span>Thanh toán: ${not empty order.paymentMethod.description ? order.paymentMethod.description : 'Chưa xác định'}</span>
            </div>
            <div class="meta-item">
                <i class="fas fa-box"></i>
                <span>Số sản phẩm: ${totalItems}</span>
            </div>
            <div class="meta-item">
                <i class="fas fa-money-bill-wave"></i>
                <span>Tổng tiền: <fmt:formatNumber value="${totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0" /></span>
            </div>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Order Items Section -->
        <div class="order-items-section">
            <div class="section-header">
                <h2>
                    <i class="fas fa-shopping-cart"></i>
                    Sản phẩm trong đơn hàng (${totalItems})
                </h2>
            </div>
            <div class="section-content">
                <c:choose>
                    <c:when test="${not empty orderItems}">
                        <c:forEach var="item" items="${orderItems}">
                            <div class="product-item">
                                <img src="${not empty item.product.image ? item.product.image : 'https://via.placeholder.com/120x120?text=No+Image'}"
                                     alt="${item.product.productName}" class="product-image">

                                <div class="product-details">
                                    <h3 class="product-name">${item.product.productName}</h3>

                                    <c:if test="${not empty item.product.briefInfo}">
                                        <p style="color: #666; font-size: 14px; margin: 5px 0;">
                                                ${item.product.briefInfo}
                                        </p>
                                    </c:if>

                                    <div class="product-specs">
                                        <div class="spec-item">
                                            <span class="spec-label">Màu sắc:</span>
                                            <span class="spec-value">${item.product.color}</span>
                                        </div>
                                        <div class="spec-item">
                                            <span class="spec-label">Bộ nhớ:</span>
                                            <span class="spec-value">${item.product.memory}</span>
                                        </div>
                                    </div>

                                    <div class="product-pricing">
                                        <div class="price-info">
                                            <c:if test="${item.product.priceOrigin > item.product.priceSale}">
                                                <div class="price-original">
                                                    Giá gốc: <fmt:formatNumber value="${item.product.priceOrigin}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                                                </div>
                                            </c:if>
                                            <div class="price-order">
                                                Giá bán: <fmt:formatNumber value="${item.product.priceSale}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                                            </div>
                                        </div>
                                        <div class="quantity-total">
                                            <div class="quantity">
                                                <i class="fas fa-cubes"></i>
                                                Số lượng: ${item.quantity}
                                            </div>
                                            <div class="total-price">
                                                <fmt:formatNumber value="${item.totalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div style="text-align: center; padding: 40px; color: #666;">
                            <i class="fas fa-box-open" style="font-size: 48px; margin-bottom: 15px;"></i>
                            <p>Không có sản phẩm nào trong đơn hàng này.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Sidebar -->
        <div>
            <!-- Order Summary -->
            <div class="order-summary">
                <div class="section-header">
                    <h2>
                        <i class="fas fa-calculator"></i>
                        Tóm tắt đơn hàng
                    </h2>
                </div>
                <div class="summary-content">
                    <c:if test="${order.discount > 0}">
                        <c:set var="discountAmount" value="${order.totalMoney * order.discount / (100 - order.discount)}" />
                        <c:set var="subtotal" value="${order.totalMoney + discountAmount}" />

                        <div class="summary-row">
                            <span class="summary-label">Tạm tính:</span>
                            <span class="summary-value">
                                <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                            </span>
                        </div>
                        <div class="summary-row">
                            <span class="summary-label">Giảm giá (${order.discount}%):</span>
                            <span class="summary-value" style="color: #28a745;">
                                -<fmt:formatNumber value="${discountAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                            </span>
                        </div>
                    </c:if>
                    <div class="summary-row">
                        <span class="summary-label">Tổng cộng:</span>
                        <span class="summary-value">
                            <fmt:formatNumber value="${order.totalMoney}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                        </span>
                    </div>
                </div>
            </div>

            <!-- Customer Information -->
            <div class="customer-info">
                <div class="section-header">
                    <h2>
                        <i class="fas fa-user"></i>
                        Thông tin khách hàng
                    </h2>
                </div>
                <div class="customer-details">
                    <div class="info-row">
                        <span class="info-label">Họ tên:</span>
                        <span class="info-value">${order.fullName}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Email:</span>
                        <span class="info-value">${order.email}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Số điện thoại:</span>
                        <span class="info-value">${order.phoneNumber}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Địa chỉ:</span>
                        <span class="info-value">${order.address}</span>
                    </div>
                    <c:if test="${not empty order.note}">
                        <div class="info-row">
                            <span class="info-label">Ghi chú:</span>
                            <span class="info-value">${order.note}</span>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Actions -->
            <div class="actions">
                <c:if test="${canCancel}">
                    <button id="cancelOrderBtn" class="btn btn-danger">
                        <i class="fas fa-times"></i> Hủy đơn hàng
                    </button>
                </c:if>

                <!-- Nút thay đổi địa chỉ - chỉ hiển thị khi đơn hàng ở trạng thái chờ xử lý hoặc đang xử lý -->
                    <button id="changeAddressBtn" class="btn btn-outline">
                        <i class="fas fa-map-marker-alt"></i> Thay đổi địa chỉ
                    </button>
            </div>
        </div>
    </div>
</div>

<!-- Cancel Order Modal -->
<div id="cancelModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <i class="fas fa-exclamation-triangle"></i>
            <h3>Xác nhận hủy đơn hàng</h3>
            <span class="close">&times;</span>
        </div>
        <div class="modal-body">
            <p>Bạn có chắc chắn muốn hủy đơn hàng này không?</p>
            <div class="order-info">
                <strong>Đơn hàng #${order.orderId}</strong><br>
                Tổng tiền: <fmt:formatNumber value="${order.totalMoney}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
            </div>
            <p style="color: #dc3545; font-weight: 500;">
                <i class="fas fa-info-circle"></i>
                Hành động này không thể hoàn tác!
            </p>
        </div>
        <div class="modal-footer">
            <button id="cancelConfirmBtn" class="btn btn-danger">
                <i class="fas fa-check"></i> Xác nhận hủy
            </button>
            <button id="cancelCancelBtn" class="btn btn-outline">
                <i class="fas fa-times"></i> Không hủy
            </button>
        </div>
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
        // Add hover effects to product items
        const productItems = document.querySelectorAll('.product-item');
        productItems.forEach(item => {
            item.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-3px)';
                this.style.boxShadow = '0 6px 12px rgba(0, 0, 0, 0.15)';
            });

            item.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(-2px)';
                this.style.boxShadow = '0 4px 8px rgba(0, 0, 0, 0.1)';
            });
        });

        // Print functionality
        window.addEventListener('beforeprint', function() {
            document.body.style.fontSize = '12px';
        });

        window.addEventListener('afterprint', function() {
            document.body.style.fontSize = '';
        });
    });

    document.addEventListener('DOMContentLoaded', function() {
        const cancelOrderBtn = document.getElementById('cancelOrderBtn');
        const cancelModal = document.getElementById('cancelModal');
        const cancelConfirmBtn = document.getElementById('cancelConfirmBtn');
        const cancelCancelBtn = document.getElementById('cancelCancelBtn');
        const closeModal = document.querySelector('.close');
        const toast = document.getElementById('toast');
        const toastIcon = document.getElementById('toastIcon');
        const toastMessage = document.getElementById('toastMessage');
        const toastClose = document.querySelector('.toast-close');

        // Show modal
        if (cancelOrderBtn) {
            cancelOrderBtn.addEventListener('click', function() {
                cancelModal.style.display = 'block';
            });
        }

        // Hide modal
        function hideModal() {
            cancelModal.style.display = 'none';
        }

        cancelCancelBtn.addEventListener('click', hideModal);
        closeModal.addEventListener('click', hideModal);

        // Close modal when clicking outside
        window.addEventListener('click', function(event) {
            if (event.target === cancelModal) {
                hideModal();
            }
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

        // Handle cancel order
        cancelConfirmBtn.addEventListener('click', function() {
            const originalText = this.innerHTML;

            // Show loading state
            this.innerHTML = '<div class="spinner"></div>Đang hủy...';
            this.disabled = true;

            // Send cancel request
            fetch('${pageContext.request.contextPath}/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'action=cancel&orderId=${order.orderId}'
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showToast(data.message, false);
                        hideModal();

                        // Reload page after a short delay to show updated status
                        setTimeout(() => {
                            window.location.reload();
                        }, 2000);
                    } else {
                        showToast(data.message, true);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showToast('Đã xảy ra lỗi khi hủy đơn hàng. Vui lòng thử lại.', true);
                })
                .finally(() => {
                    // Restore button state
                    this.innerHTML = originalText;
                    this.disabled = false;
                });
        });
    });

</script>

</body>
</html>