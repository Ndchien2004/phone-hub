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
      /* BỔ SUNG: Màu nền cho khối chi tiết */
      --light-red-bg: #fff5f6;
    }

    body {
      font-family: 'Roboto', sans-serif;
      background-color: var(--background-color);
      color: var(--text-color);
      margin: 0;
      font-size: 14px;
    }

    /* BỔ SUNG: Wrapper để căn giữa nội dung */
    .page-wrapper {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 40px 20px;
      box-sizing: border-box;
      min-height: 100vh;
    }

    /* BỔ SUNG: Header đơn giản */
    .confirmation-header {
      margin-bottom: 30px;
      font-size: 24px;
      font-weight: 700;
      color: var(--primary-red);
    }
    .confirmation-header a {
      color: inherit;
      text-decoration: none;
    }

    .confirmation-card {
      background-color: white;
      border-radius: 12px;
      padding: 30px 40px;
      border: 1px solid var(--border-color);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      max-width: 600px;
      width: 100%;
      text-align: center;
    }

    .icon-success {
      font-size: 50px;
      color: var(--primary-green);
      width: 80px;
      height: 80px;
      border-radius: 50%;
      background-color: #eaf6ec;
      display: inline-flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 20px;
      /* BỔ SUNG: Animation cho icon */
      animation: pop-in 0.5s cubic-bezier(0.68, -0.55, 0.27, 1.55) forwards;
    }

    /* BỔ SUNG: Keyframes cho animation */
    @keyframes pop-in {
      0% { transform: scale(0); opacity: 0; }
      100% { transform: scale(1); opacity: 1; }
    }


    h1 {
      font-size: 22px;
      font-weight: 700;
      margin-top: 0;
      margin-bottom: 15px;
    }

    .message {
      font-size: 15px;
      color: var(--subtext-color);
      margin-bottom: 25px;
      line-height: 1.6;
    }

    /* THAY ĐỔI: Style lại khối chi tiết đơn hàng */
    .order-details {
      text-align: left;
      background-color: var(--light-red-bg);
      border: 1px solid var(--border-color);
      border-radius: 8px;
      padding: 20px;
      margin-top: 25px;
    }

    .detail-item {
      display: flex;
      justify-content: space-between;
      align-items: center; /* Căn giữa theo chiều dọc */
      margin-bottom: 15px;
      font-size: 14px;
    }
    .detail-item:last-child {
      margin-bottom: 0;
    }

    .detail-item .label {
      color: var(--subtext-color);
    }

    .detail-item .value {
      font-weight: 500;
      text-align: right; /* Đảm bảo giá trị căn phải */
    }

    /* THAY ĐỔI: Nhấn mạnh mã đơn hàng và tổng tiền */
    .detail-item .value.order-id {
      font-weight: 700;
      color: var(--primary-red);
      font-size: 16px;
    }

    .detail-item .value.total {
      font-weight: 700;
      font-size: 20px;
      color: var(--primary-red);
    }

    /* THAY ĐỔI: Bố cục nút bấm linh hoạt */
    .actions {
      margin-top: 30px;
      display: flex;
      justify-content: center;
      gap: 15px;
      flex-wrap: wrap; /* Cho phép xuống dòng nếu cần */
    }

    .btn {
      display: inline-block;
      padding: 12px 25px; /* Giảm nhẹ padding */
      font-size: 15px;
      font-weight: 700;
      border-radius: 8px;
      text-decoration: none;
      cursor: pointer;
      border: 1px solid transparent;
      transition: all 0.2s;
      text-transform: uppercase;
      flex-grow: 1; /* Cho các nút tự co giãn */
      max-width: 250px; /* Giới hạn độ rộng tối đa của nút */
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

    /* BỔ SUNG: Responsive cho màn hình nhỏ */
    @media (max-width: 576px) {
      .page-wrapper {
        padding: 20px 15px;
      }
      .confirmation-card {
        padding: 25px 20px;
      }
      .actions {
        flex-direction: column; /* Xếp chồng nút trên mobile */
      }
      .btn {
        max-width: 100%; /* Cho nút rộng hết cỡ trên mobile */
      }
    }

  </style>
</head>
<body>

<div class="page-wrapper">
  <!-- BỔ SUNG: Header -->
  <header class="confirmation-header">
    <a href="${pageContext.request.contextPath}/">SmartphoneStore</a>
  </header>

  <div class="confirmation-card">
    <div class="icon-success">
      <i class="fa-solid fa-check"></i>
    </div>

    <%-- Kiểm tra xem có đối tượng 'order' không để cá nhân hóa lời chào --%>
    <c:if test="${not empty requestScope.order}">
      <h1>Đặt hàng thành công!</h1>
      <p class="message">
        Cảm ơn bạn, <strong>${order.fullName}</strong>, đã mua hàng. Đơn hàng của bạn đã được ghi nhận.
        Chúng tôi sẽ liên hệ để xác nhận trong thời gian sớm nhất.
      </p>
      <div class="order-details">
        <div class="detail-item">
          <span class="label">Mã đơn hàng:</span>
          <!-- THAY ĐỔI: Thêm class để nhấn mạnh mã đơn hàng -->
          <span class="value order-id">#${order.orderId}</span>
        </div>
        <div class="detail-item">
          <span class="label">Số điện thoại:</span>
          <span class="value">${order.phoneNumber}</span>
        </div>
        <div class="detail-item">
          <span class="label">Giao đến:</span>
          <span class="value">${order.address}</span>
        </div>
        <hr style="border: none; border-top: 1px dashed var(--border-color); margin: 10px 0;">
        <div class="detail-item">
          <span class="label">Tổng thanh toán:</span>
          <span class="value total">
            <fmt:formatNumber value="${order.totalMoney}" type="currency" currencySymbol="₫"/>
          </span>
        </div>
      </div>
    </c:if>

    <%-- Nếu không có đối tượng order, chỉ hiển thị thông báo chung --%>
    <c:if test="${empty requestScope.order}">
      <h1>Thanh toán hoàn tất!</h1>
      <p class="message">Thanh toán của bạn đang được xử lý hoặc đã hoàn tất. Vui lòng kiểm tra lịch sử đơn hàng để xem chi tiết.</p>
    </c:if>

    <div class="actions">
      <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Tiếp tục mua sắm</a>
      <a href="#" class="btn btn-primary">Xem lịch sử đơn hàng</a>
    </div>
  </div>
</div>

</body>
</html>