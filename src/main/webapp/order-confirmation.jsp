<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
  <title>Đặt hàng thành công</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
  <style>
    :root {
      --primary-red: #d70018;
      --primary-green: #28a745;
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
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      padding: 20px;
      box-sizing: border-box;
    }

    .confirmation-card {
      background-color: white;
      border-radius: 12px;
      padding: 40px;
      border: 1px solid var(--border-color);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      max-width: 600px;
      width: 100%;
      text-align: center;
    }

    .icon-success {
      font-size: 60px;
      color: var(--primary-green);
      width: 90px;
      height: 90px;
      border-radius: 50%;
      background-color: #eaf6ec;
      display: inline-flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 20px;
    }

    h1 {
      font-size: 24px;
      font-weight: 700;
      margin-top: 0;
      margin-bottom: 10px;
    }

    .message {
      font-size: 16px;
      color: var(--subtext-color);
      margin-bottom: 30px;
      line-height: 1.6;
    }

    .order-details {
      text-align: left;
      border-top: 1px solid var(--border-color);
      padding-top: 20px;
      margin-top: 20px;
    }

    .detail-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      font-size: 15px;
    }

    .detail-item .label {
      color: var(--subtext-color);
    }

    .detail-item .value {
      font-weight: 500;
    }

    .detail-item .value.total {
      font-weight: 700;
      font-size: 20px;
      color: var(--primary-red);
    }

    .actions {
      margin-top: 30px;
      display: flex;
      flex-direction: column;
      gap: 15px;
    }

    .btn {
      display: inline-block;
      padding: 14px 20px;
      font-size: 16px;
      font-weight: 700;
      border-radius: 8px;
      text-decoration: none;
      cursor: pointer;
      border: 1px solid transparent;
      transition: all 0.2s;
      text-transform: uppercase;
    }

    .btn-primary {
      background-color: var(--primary-red);
      color: white;
    }

    .btn-primary:hover {
      opacity: 0.9;
    }

    .btn-secondary {
      background-color: white;
      color: var(--text-color);
      border-color: var(--border-color);
    }

    .btn-secondary:hover {
      background-color: #f9f9f9;
    }
  </style>
</head>
<body>

<div class="confirmation-card">
  <div class="icon-success">
    <i class="fa-solid fa-check"></i>
  </div>
  <h1>Đặt hàng thành công!</h1>
  <p class="message">
    Cảm ơn bạn đã mua hàng tại SmartphoneStore. Đơn hàng của bạn đã được ghi nhận.
    Chúng tôi sẽ liên hệ với bạn để xác nhận trong thời gian sớm nhất.
  </p>

  <%-- Kiểm tra xem có đối tượng 'order' trong request không --%>
  <c:if test="${not empty requestScope.order}">
    <div class="order-details">
      <div class="detail-item">
        <span class="label">Mã đơn hàng:</span>
        <span class="value">#${order.orderId}</span>
      </div>
      <div class="detail-item">
        <span class="label">Người nhận:</span>
        <span class="value">${order.fullName}</span>
      </div>
      <div class="detail-item">
        <span class="label">Số điện thoại:</span>
        <span class="value">${order.phoneNumber}</span>
      </div>
      <div class="detail-item">
        <span class="label">Giao đến:</span>
        <span class="value">${order.address}</span>
      </div>
      <div class="detail-item">
        <span class="label">Tổng thanh toán:</span>
        <span class="value total">
                    <fmt:formatNumber value="${order.totalMoney}" type="currency" currencySymbol="₫"/>
                </span>
      </div>
    </div>
  </c:if>

  <%-- Nếu không có đối tượng order (ví dụ, người dùng F5 lại trang), chỉ hiển thị thông báo chung --%>
  <c:if test="${empty requestScope.order}">
    <p class="message">Thanh toán của bạn đang được xử lý hoặc đã hoàn tất.</p>
  </c:if>

  <div class="actions">
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Tiếp tục mua sắm</a>
    <a href="#" class="btn btn-secondary">Xem lịch sử đơn hàng</a>
  </div>
</div>

</body>
</html>