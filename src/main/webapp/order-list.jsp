
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đơn hàng của tôi - CellphoneS</title>
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

        .page-header {
            background: white;
            padding: 25px;
            border-radius: 12px;
            margin-bottom: 20px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .page-header h1 {
            color: #d70018;
            font-size: 28px;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .page-header p {
            color: #666;
            font-size: 16px;
        }

        .orders-summary {
            background: linear-gradient(135deg, #d70018, #ff6b35);
            color: white;
            padding: 20px;
            border-radius: 12px;
            margin-bottom: 25px;
            text-align: center;
        }

        .orders-summary h3 {
            font-size: 32px;
            margin-bottom: 5px;
        }

        .orders-summary p {
            opacity: 0.9;
            font-size: 16px;
        }

        .order-list {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .order-card {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            border-left: 4px solid #d70018;
        }

        .order-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }

        .order-header {
            background: #f8f9fa;
            padding: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            border-bottom: 1px solid #eee;
        }

        .order-basic-info {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .order-id {
            font-weight: bold;
            color: #d70018;
            font-size: 18px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .order-date {
            color: #666;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .order-status-amount {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            gap: 10px;
        }

        .order-status {
            padding: 8px 16px;
            border-radius: 25px;
            font-size: 13px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        /* Trạng thái màu sắc */
        .status-pending {
            background: #fff3cd;
            color: #856404;
            border: 1px solid #ffeaa7;
        }

        .status-processing {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .status-shipped {
            background: #cce4f7;
            color: #004085;
            border: 1px solid #b3d7ff;
        }

        .status-delivered {
            background: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }

        .status-cancelled {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f1b0b7;
        }

        .status-default {
            background: #e7f3ff;
            color: #0066cc;
            border: 1px solid #b3d7ff;
        }

        .order-total {
            font-weight: bold;
            color: #d70018;
            font-size: 20px;
        }

        .order-body {
            padding: 20px;
        }

        .products-summary {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 20px;
        }

        .product-preview {
            display: flex;
            align-items: center;
            gap: 12px;
            background: #f8f9fa;
            padding: 12px;
            border-radius: 8px;
            flex: 1;
            min-width: 250px;
        }

        .product-image {
            width: 60px;
            height: 60px;
            border-radius: 8px;
            object-fit: cover;
            border: 1px solid #ddd;
            background: white;
        }

        .product-info {
            flex: 1;
        }

        .product-name {
            font-weight: 500;
            color: #333;
            font-size: 14px;
            line-height: 1.3;
            margin-bottom: 4px;
        }

        .product-quantity {
            color: #666;
            font-size: 12px;
            display: flex;
            align-items: center;
            gap: 4px;
        }

        .more-products {
            display: flex;
            align-items: center;
            justify-content: center;
            background: #e9ecef;
            color: #666;
            padding: 12px;
            border-radius: 8px;
            min-width: 120px;
            font-size: 13px;
            text-align: center;
        }

        .order-summary {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 15px;
            border-top: 1px solid #eee;
        }

        .summary-info {
            display: flex;
            gap: 20px;
            align-items: center;
            flex-wrap: wrap;
        }

        .summary-item {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            color: #666;
        }

        .summary-value {
            font-weight: 600;
            color: #333;
        }

        .order-actions {
            display: flex;
            gap: 10px;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
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
            transform: translateY(-1px);
        }

        .empty-state {
            text-align: center;
            padding: 80px 20px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .empty-state i {
            font-size: 64px;
            color: #ccc;
            margin-bottom: 20px;
        }

        .empty-state h3 {
            color: #666;
            margin-bottom: 15px;
            font-size: 24px;
        }

        .empty-state p {
            color: #999;
            margin-bottom: 25px;
            font-size: 16px;
        }

        /* Status tracking */
        .status-tracking {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 10px;
        }

        .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #ccc;
        }

        .status-dot.active {
            background: #28a745;
        }

        .status-dot.processing {
            background: #ffc107;
        }

        .status-dot.shipped {
            background: #17a2b8;
        }

        .status-dot.cancelled {
            background: #dc3545;
        }

        @media (max-width: 768px) {
            .container {
                padding: 0 15px;
            }

            .order-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 15px;
            }

            .order-status-amount {
                align-items: flex-start;
                width: 100%;
                flex-direction: row;
                justify-content: space-between;
            }

            .products-summary {
                flex-direction: column;
            }

            .product-preview {
                min-width: auto;
            }

            .order-summary {
                flex-direction: column;
                align-items: flex-start;
                gap: 15px;
            }

            .summary-info {
                flex-direction: column;
                align-items: flex-start;
                gap: 10px;
            }

            .order-actions {
                width: 100%;
                justify-content: center;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <!-- Page Header -->
    <div class="page-header">
        <h1>
            <i class="fas fa-shopping-bag"></i>
            Đơn hàng của tôi
        </h1>
        <p>Theo dõi và quản lý các đơn hàng bạn đã đặt</p>
    </div>

    <!-- Orders Summary -->
    <div class="orders-summary">
        <h3>${totalOrders}</h3>
        <p><i class="fas fa-receipt"></i> Tổng số đơn hàng đã đặt</p>
    </div>

    <!-- Order List -->
    <div class="order-list">
        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-state">
                    <i class="fas fa-shopping-bag"></i>
                    <h3>Chưa có đơn hàng nào</h3>
                    <p>Bạn chưa có đơn hàng nào. Hãy khám phá và mua sắm những sản phẩm yêu thích!</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                        <i class="fas fa-shopping-cart"></i> Mua sắm ngay
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="order" items="${orders}">
                    <div class="order-card">
                        <!-- Order Header -->
                        <div class="order-header">
                            <div class="order-basic-info">
                                <div class="order-id">
                                    <i class="fas fa-receipt"></i>
                                    Đơn hàng #${order.orderId}
                                </div>
                                <div class="order-date">
                                    <i class="fas fa-calendar-alt"></i>
                                    Đặt ngày ${order.formattedOrderDateShort}
                                </div>
                            </div>
                            <div class="order-status-amount">
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
                                        <div class="order-status ${statusClass}">
                                            <i class="fas fa-info-circle"></i>
                                                ${order.status.description}
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="order-status status-default">
                                            <i class="fas fa-info-circle"></i>
                                            Chưa xác định
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="order-total">
                                    <fmt:formatNumber value="${order.totalMoney}" type="currency"
                                                      currencySymbol="₫" maxFractionDigits="0" />
                                </div>
                            </div>
                        </div>

                        <!-- Order Body -->
                        <div class="order-body">
                            <!-- Products Summary -->
                            <div class="products-summary">
                                <c:choose>
                                    <c:when test="${not empty order.orderItems}">
                                        <!-- Hiển thị tối đa 2 sản phẩm đầu -->
                                        <c:forEach var="item" items="${order.orderItems}" end="1">
                                            <div class="product-preview">
                                                <img src="${not empty item.product.image ? item.product.image : 'https://via.placeholder.com/60x60?text=No+Image'}"
                                                     alt="${item.product.productName}" class="product-image">
                                                <div class="product-info">
                                                    <div class="product-name">${item.product.productName}</div>
                                                    <div class="product-quantity">
                                                        <i class="fas fa-cubes"></i>
                                                        Số lượng: ${item.quantity}
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <!-- Hiển thị số sản phẩm còn lại nếu có -->
                                        <c:if test="${fn:length(order.orderItems) > 2}">
                                            <div class="more-products">
                                                <i class="fas fa-plus-circle"></i>
                                                <span>+${fn:length(order.orderItems) - 2} sản phẩm khác</span>
                                            </div>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="product-preview">
                                            <img src="https://via.placeholder.com/60x60?text=No+Image"
                                                 alt="Sản phẩm" class="product-image">
                                            <div class="product-info">
                                                <div class="product-name">Thông tin sản phẩm không có sẵn</div>
                                                <div class="product-quantity">
                                                    <i class="fas fa-cubes"></i>
                                                    Số lượng: 1
                                                </div>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- Order Summary -->
                            <div class="order-summary">
                                <div class="summary-info">
                                    <div class="summary-item">
                                        <i class="fas fa-box"></i>
                                        <span class="summary-value">${fn:length(order.orderItems)}</span>
                                        sản phẩm
                                    </div>
                                    <div class="summary-item">
                                        <i class="fas fa-credit-card"></i>
                                            ${not empty order.paymentMethod.description ? order.paymentMethod.description : 'Chưa xác định'}
                                    </div>
                                    <c:if test="${order.discount > 0}">
                                        <div class="summary-item">
                                            <i class="fas fa-tag"></i>
                                            Giảm <span class="summary-value" style="color: #28a745;">${order.discount}%</span>
                                        </div>
                                    </c:if>
                                </div>
                                <div class="order-actions">
                                    <a href="${pageContext.request.contextPath}/orders/${order.orderId}" class="btn btn-outline">
                                        <i class="fas fa-eye"></i> Xem chi tiết
                                    </a>
                                </div>
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
                this.style.transform = 'translateY(-4px)';
                this.style.boxShadow = '0 12px 30px rgba(0, 0, 0, 0.2)';
            });

            card.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(-2px)';
                this.style.boxShadow = '0 8px 25px rgba(0, 0, 0, 0.15)';
            });
        });

        // Add click handler for product previews
        const productPreviews = document.querySelectorAll('.product-preview');
        productPreviews.forEach(preview => {
            preview.addEventListener('mouseenter', function() {
                this.style.background = '#f0f0f0';
                this.style.transform = 'scale(1.02)';
            });

            preview.addEventListener('mouseleave', function() {
                this.style.background = '#f8f9fa';
                this.style.transform = 'scale(1)';
            });
        });

        // Status tracking animation
        const statusDots = document.querySelectorAll('.status-dot');
        statusDots.forEach(dot => {
            if (dot.classList.contains('active')) {
                dot.style.animation = 'pulse 2s infinite';
            }
        });
    });

    // CSS animation for status dots
    const style = document.createElement('style');
    style.textContent = `
        @keyframes pulse {
            0% { opacity: 1; }
            50% { opacity: 0.5; }
            100% { opacity: 1; }
        }
    `;
    document.head.appendChild(style);
</script>
</body>
</html>