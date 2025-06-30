<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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
        }

        .container {
            max-width: 1000px;
            margin: 20px auto;
            padding: 0 20px;
        }

        .header {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            color: #d70018;
            font-size: 24px;
        }

        .back-btn {
            background: #6c757d;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: background 0.3s;
        }

        .back-btn:hover {
            background: #545b62;
        }

        .order-info-card {
            background: white;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .order-header {
            border-bottom: 2px solid #f1f1f1;
            padding-bottom: 20px;
            margin-bottom: 25px;
        }

        .order-meta {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }

        .meta-item {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }

        .meta-label {
            color: #666;
            font-size: 14px;
            font-weight: 500;
        }

        .meta-value {
            color: #333;
            font-size: 16px;
            font-weight: 600;
        }

        .meta-value.price {
            color: #d70018;
            font-size: 18px;
        }

        .meta-value.status {
            background: #e7f3ff;
            color: #0066cc;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 14px;
            display: inline-block;
            width: fit-content;
        }

        .section-title {
            font-size: 18px;
            color: #333;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .customer-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .customer-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }

        .customer-item {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .customer-label {
            color: #555;
            font-weight: 500;
            font-size: 14px;
        }

        .customer-value {
            color: #333;
            font-size: 15px;
            word-wrap: break-word;
        }

        .products-section {
            margin-bottom: 20px;
        }

        .product-item {
            display: flex;
            gap: 20px;
            padding: 20px;
            border: 1px solid #eee;
            border-radius: 8px;
            margin-bottom: 15px;
            background: #fafafa;
            transition: transform 0.2s ease;
        }

        .product-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .product-image {
            width: 100px;
            height: 100px;
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
            font-weight: 600;
            color: #333;
            font-size: 16px;
            line-height: 1.4;
        }

        .product-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 10px;
        }

        .product-quantity {
            color: #666;
            font-size: 14px;
            background: white;
            padding: 4px 8px;
            border-radius: 4px;
            border: 1px solid #ddd;
        }

        .product-price {
            font-weight: bold;
            color: #d70018;
            font-size: 16px;
        }

        .summary-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #d70018;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding: 8px 0;
        }

        .summary-label {
            color: #555;
            font-weight: 500;
        }

        .summary-value {
            font-weight: 600;
            color: #333;
        }

        .total-row {
            border-top: 2px solid #ddd;
            padding-top: 15px;
            margin-top: 15px;
            font-size: 18px;
        }

        .total-row .summary-value {
            color: #d70018;
            font-size: 20px;
        }

        .actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            font-weight: 500;
        }

        .btn-primary {
            background: #d70018;
            color: white;
        }

        .btn-primary:hover {
            background: #b50015;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #545b62;
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

        .alert {
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }

        .alert-info {
            background: #d1ecf1;
            color: #0c5460;
            border-left: 4px solid #17a2b8;
        }

        @media (max-width: 768px) {
            .header {
                flex-direction: column;
                gap: 15px;
                text-align: center;
            }

            .order-meta {
                grid-template-columns: 1fr;
            }

            .customer-grid {
                grid-template-columns: 1fr;
            }

            .product-item {
                flex-direction: column;
                text-align: center;
            }

            .product-image {
                align-self: center;
            }

            .product-meta {
                justify-content: center;
                text-align: center;
            }

            .actions {
                flex-direction: column;
                align-items: center;
            }

            .btn {
                width: 100%;
                max-width: 300px;
                justify-content: center;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <!-- Header -->
    <div class="header">
        <h1><i class="fas fa-receipt"></i> Chi tiết đơn hàng #${order.orderId}</h1>
        <a href="/orders" class="back-btn">
            <i class="fas fa-arrow-left"></i> Quay lại danh sách
        </a>
    </div>

    <!-- Order Information Card -->
    <div class="order-info-card">
        <!-- Order Header -->
        <div class="order-header">
            <div class="order-meta">
                <div class="meta-item">
                    <span class="meta-label">Mã đơn hàng</span>
                    <span class="meta-value">#${order.orderId}</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">Ngày đặt hàng</span>
                    <span class="meta-value">${order.formattedOrderDate}</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">Trạng thái</span>
                    <span class="meta-value status">Đã đặt hàng</span>
                </div>
                <div class="meta-item">
                    <span class="meta-label">Tổng tiền</span>
                    <span class="meta-value price">
                        <fmt:formatNumber value="${order.totalMoney}" type="currency"
                                          currencySymbol="₫" maxFractionDigits="0" />
                    </span>
                </div>
            </div>
        </div>

        <!-- Customer Information -->
        <div class="customer-section">
            <h3 class="section-title">
                <i class="fas fa-user"></i> Thông tin khách hàng
            </h3>
            <div class="customer-grid">
                <div class="customer-item">
                    <span class="customer-label">Họ và tên</span>
                    <span class="customer-value">${order.fullName}</span>
                </div>
                <div class="customer-item">
                    <span class="customer-label">Email</span>
                    <span class="customer-value">${order.email}</span>
                </div>
                <div class="customer-item">
                    <span class="customer-label">Số điện thoại</span>
                    <span class="customer-value">${order.phoneNumber}</span>
                </div>
                <div class="customer-item">
                    <span class="customer-label">Địa chỉ giao hàng</span>
                    <span class="customer-value">${order.address}</span>
                </div>
                <c:if test="${not empty order.note}">
                    <div class="customer-item">
                        <span class="customer-label">Ghi chú</span>
                        <span class="customer-value">${order.note}</span>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Products Section -->
        <div class="products-section">
            <h3 class="section-title">
                <i class="fas fa-box"></i> Sản phẩm đã đặt
            </h3>

            <c:choose>
                <c:when test="${not empty order.orderItems}">
                    <c:forEach var="item" items="${order.orderItems}">
                        <div class="product-item">
                            <img src="${not empty item.productImageUrl ? item.productImageUrl : 'https://via.placeholder.com/100x100?text=No+Image'}"
                                 alt="${item.productName}" class="product-image">
                            <div class="product-details">
                                <div class="product-name">${item.productName}</div>
                                <div class="product-meta">
                                    <span class="product-quantity">
                                        <i class="fas fa-cubes"></i> Số lượng: ${item.quantity}
                                    </span>
                                    <span class="product-price">
                                        <fmt:formatNumber value="${item.price}" type="currency"
                                                          currencySymbol="₫" maxFractionDigits="0" />
                                    </span>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i>
                        Không có thông tin chi tiết sản phẩm
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Order Summary -->
        <div class="summary-section">
            <h3 class="section-title">
                <i class="fas fa-calculator"></i> Tóm tắt đơn hàng
            </h3>

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

    <!-- Action Buttons -->
    <div class="actions">
        <a href="/orders" class="btn btn-secondary">
            <i class="fas fa-list"></i> Về danh sách đơn hàng
        </a>
        <a href="/orders/${order.orderId}/print" class="btn btn-outline" target="_blank">
            <i class="fas fa-print"></i> In đơn hàng
        </a>
        <button class="btn btn-primary" onclick="window.print()">
            <i class="fas fa-download"></i> In trang này
        </button>
    </div>
</div>

<script>
    // Add print functionality
    function printOrder() {
        window.print();
    }

    // Add some interactive features
    document.addEventListener('DOMContentLoaded', function() {
        // Add smooth scrolling for internal links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                document.querySelector(this.getAttribute('href')).scrollIntoView({
                    behavior: 'smooth'
                });
            });
        });

        // Add hover effects to product items
        const productItems = document.querySelectorAll('.product-item');
        productItems.forEach(item => {
            item.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-2px)';
                this.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.15)';
            });

            item.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
                this.style.boxShadow = '0 2px 4px rgba(0, 0, 0, 0.1)';
            });
        });
    });
</script>
</body>
</html>