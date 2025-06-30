<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách đơn hàng - CellphoneS</title>
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
        }

        .container {
            max-width: 1200px;
            margin: 20px auto;
            padding: 0 20px;
        }

        .page-title {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .page-title h1 {
            color: #d70018;
            font-size: 28px;
            margin-bottom: 10px;
        }

        .stats-section {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .stats-info {
            background: #e7f3ff;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid #d70018;
        }

        .stats-info h3 {
            color: #d70018;
            margin-bottom: 5px;
        }

        .order-list {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .order-card {
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s ease;
        }

        .order-card:hover {
            transform: translateY(-2px);
        }

        .order-header {
            background: #f8f9fa;
            padding: 15px 20px;
            border-bottom: 1px solid #eee;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 10px;
        }

        .order-info {
            display: flex;
            gap: 20px;
            align-items: center;
            flex-wrap: wrap;
        }

        .order-id {
            font-weight: bold;
            color: #d70018;
            font-size: 16px;
        }

        .order-date {
            color: #666;
            font-size: 14px;
        }

        .order-status {
            background: #e7f3ff;
            color: #0066cc;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
        }

        .order-total {
            font-weight: bold;
            color: #d70018;
            font-size: 18px;
        }

        .order-body {
            padding: 20px;
        }

        .products-section {
            margin-bottom: 15px;
        }

        .products-section h4 {
            color: #333;
            margin-bottom: 15px;
            font-size: 16px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .product-list {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .product-item {
            display: flex;
            gap: 15px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            border: 1px solid #eee;
        }

        .product-image {
            width: 80px;
            height: 80px;
            border-radius: 8px;
            object-fit: cover;
            border: 1px solid #ddd;
            background: #fff;
        }

        .product-details {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .product-name {
            font-weight: 500;
            color: #333;
            font-size: 16px;
            line-height: 1.4;
        }

        .product-quantity {
            color: #666;
            font-size: 14px;
        }

        .product-price {
            font-weight: bold;
            color: #d70018;
            font-size: 16px;
        }

        .order-summary {
            border-top: 1px solid #eee;
            padding-top: 15px;
            margin-top: 15px;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            font-size: 14px;
        }

        .summary-label {
            color: #666;
        }

        .summary-value {
            font-weight: 500;
            color: #333;
        }

        .total-row {
            border-top: 1px solid #eee;
            padding-top: 8px;
            margin-top: 8px;
            font-size: 16px;
        }

        .total-row .summary-label {
            font-weight: bold;
            color: #333;
        }

        .total-row .summary-value {
            color: #d70018;
            font-weight: bold;
            font-size: 18px;
        }

        .order-actions {
            padding: 15px 20px;
            background: #f8f9fa;
            border-top: 1px solid #eee;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 10px;
        }

        .order-meta {
            color: #666;
            font-size: 14px;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background: #d70018;
            color: white;
        }

        .btn-primary:hover {
            background: #b50015;
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

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .empty-state i {
            font-size: 64px;
            color: #ccc;
            margin-bottom: 20px;
        }

        .empty-state h3 {
            color: #666;
            margin-bottom: 10px;
        }

        .empty-state p {
            color: #999;
            margin-bottom: 20px;
        }

        @media (max-width: 768px) {
            .order-header {
                flex-direction: column;
                align-items: flex-start;
            }

            .order-info {
                width: 100%;
                justify-content: space-between;
            }

            .product-item {
                flex-direction: column;
                text-align: center;
            }

            .product-image {
                align-self: center;
            }

            .order-actions {
                flex-direction: column;
                align-items: stretch;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <!-- Page Title -->
    <div class="page-title">
        <h1><i class="fas fa-shopping-bag"></i> Danh sách đơn hàng</h1>
        <p>Quản lý và theo dõi tất cả các đơn hàng trong hệ thống</p>
    </div>

    <!-- Stats Section -->
    <div class="stats-section">
        <div class="stats-info">
            <h3><i class="fas fa-chart-bar"></i> Thống kê</h3>
            <p>Tổng số đơn hàng: <strong>${totalOrders}</strong></p>
        </div>
    </div>

    <!-- Order List -->
    <div class="order-list">
        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-state">
                    <i class="fas fa-shopping-bag"></i>
                    <h3>Chưa có đơn hàng nào</h3>
                    <p>Chưa có đơn hàng nào trong hệ thống.</p>
                    <a href="/products" class="btn btn-primary">
                        <i class="fas fa-shopping-cart"></i> Xem sản phẩm
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="order" items="${orders}">
                    <div class="order-card">
                        <!-- Order Header -->
                        <div class="order-header">
                            <div class="order-info">
                                <span class="order-id">
                                    <i class="fas fa-receipt"></i>
                                    Đơn hàng #${order.orderId}
                                </span>
                                <span class="order-date">
                                    <i class="fas fa-calendar"></i>
                                    ${order.formattedOrderDate}
                                </span>
                                <span class="order-status">
                                    <i class="fas fa-info-circle"></i>
                                    Đã đặt hàng
                                </span>
                            </div>
                            <div class="order-total">
                                <fmt:formatNumber value="${order.totalMoney}" type="currency"
                                                  currencySymbol="₫" maxFractionDigits="0" />
                            </div>
                        </div>

                        <!-- Order Body -->
                        <div class="order-body">
                            <!-- Products Section -->
                            <div class="products-section">
                                <h4><i class="fas fa-box"></i> Sản phẩm trong đơn hàng</h4>
                                <div class="product-list">
                                    <c:choose>
                                        <c:when test="${not empty order.orderItems}">
                                            <c:forEach var="item" items="${order.orderItems}">
                                                <div class="product-item">
                                                    <img src="${not empty item.productImageUrl ? item.productImageUrl : 'https://via.placeholder.com/80x80?text=No+Image'}"
                                                         alt="${item.productName}" class="product-image">
                                                    <div class="product-details">
                                                        <div class="product-name">${item.productName}</div>
                                                        <div class="product-quantity">
                                                            <i class="fas fa-cubes"></i> Số lượng: ${item.quantity}
                                                        </div>
                                                        <div class="product-price">
                                                            <fmt:formatNumber value="${item.price}" type="currency"
                                                                              currencySymbol="₫" maxFractionDigits="0" />
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="product-item">
                                                <img src="https://via.placeholder.com/80x80?text=No+Image"
                                                     alt="Sản phẩm" class="product-image">
                                                <div class="product-details">
                                                    <div class="product-name">Thông tin sản phẩm không có sẵn</div>
                                                    <div class="product-quantity">
                                                        <i class="fas fa-cubes"></i> Số lượng: 1
                                                    </div>
                                                    <div class="product-price">
                                                        <fmt:formatNumber value="${order.totalMoney}" type="currency"
                                                                          currencySymbol="₫" maxFractionDigits="0" />
                                                    </div>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Order Summary -->
                            <div class="order-summary">
                                <c:if test="${order.discount > 0}">
                                    <c:set var="discountAmount" value="${order.totalMoney * order.discount / (100 - order.discount)}" />
                                    <c:set var="subtotal" value="${order.totalMoney + discountAmount}" />

                                    <div class="summary-row">
                                        <span class="summary-label">Tạm tính:</span>
                                        <span class="summary-value">
                                            <fmt:formatNumber value="${subtotal}" type="currency"
                                                              currencySymbol="₫" maxFractionDigits="0" />
                                        </span>
                                    </div>
                                    <div class="summary-row">
                                        <span class="summary-label">Giảm giá (${order.discount}%):</span>
                                        <span class="summary-value" style="color: #28a745;">
                                            -<fmt:formatNumber value="${discountAmount}" type="currency"
                                                               currencySymbol="₫" maxFractionDigits="0" />
                                        </span>
                                    </div>
                                </c:if>
                                <div class="summary-row total-row">
                                    <span class="summary-label">Tổng cộng:</span>
                                    <span class="summary-value">
                                        <fmt:formatNumber value="${order.totalMoney}" type="currency"
                                                          currencySymbol="₫" maxFractionDigits="0" />
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Order Actions -->
                        <div class="order-actions">
                            <div class="order-meta">
                                <i class="fas fa-clock"></i>
                                <span>Đặt hàng: ${order.formattedOrderDateShort}</span>
                                <c:if test="${not empty order.note}">
                                    <span style="margin-left: 15px;">
                                        <i class="fas fa-sticky-note"></i>
                                        Ghi chú: ${order.note}
                                    </span>
                                </c:if>
                            </div>
                            <div class="action-buttons">
                                <a href="${pageContext.request.contextPath}/orders/${order.orderId}" class="btn btn-outline">
                                    <i class="fas fa-eye"></i> Xem chi tiết
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Add hover effects to order cards
        const orderCards = document.querySelectorAll('.order-card');
        orderCards.forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.15)';
            });

            card.addEventListener('mouseleave', function() {
                this.style.boxShadow = '0 2px 8px rgba(0, 0, 0, 0.1)';
            });
        });

        // Add click handler for product items
        const productItems = document.querySelectorAll('.product-item');
        productItems.forEach(item => {
            item.addEventListener('click', function() {
                // Add any click behavior for products if needed
                console.log('Product item clicked');
            });
        });
    });
</script>
</body>
</html>