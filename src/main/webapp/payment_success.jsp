<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Thanh toán thành công</title>
</head>
<body>
<h2>Thanh toán thành công!</h2>
<p>Đơn hàng: ${orderId}</p>
<p>Số tiền: ${amount} VND</p>
<p>Mã giao dịch: ${transactionNo}</p>
<p>Ngân hàng: ${bankCode}</p>
<a href="/phone_hub_war_exploded/">Quay về trang chủ</a>
</body>
</html>