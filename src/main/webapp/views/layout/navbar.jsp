<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title>Tiêu đề trang của bạn</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Exo+2:wght@700&display=swap" rel="stylesheet">

</head>
<style>
    /* Biến màu cho dễ tùy chỉnh */
    :root {
        --cps-red: #d70018;
        --cps-dark-red: #a70012;
        --cps-text-white: #fff;
        --cps-text-gray: #4a4a4a;
        --cps-border-color: #e0e0e0;
    }

    /* Reset mặc định cho body để navbar dính sát lề */
    body {
        margin: 0;
        font-family: 'Roboto', sans-serif; /* Đảm bảo font chữ đồng bộ */
    }

    /* Container chính của Navbar */
    .main-header {
        background-color: var(--cps-red);
        color: var(--cps-text-white);
        padding: 10px 0;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        position: sticky;
        top: 0;
        z-index: 1000;
    }

    .navbar-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 15px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 20px;
    }

    /* Cột trái: Logo và địa điểm */
    .navbar-left {
        display: flex;
        align-items: center;
        gap: 20px;
    }

    .logo a {
        font-size: 28px;
        font-weight: 700;
        color: var(--cps-text-white);
        text-decoration: none;
        letter-spacing: -1px;
    }

    .location-picker {
        background-color: var(--cps-dark-red);
        padding: 8px 12px;
        border-radius: 8px;
        font-size: 13px;
        display: flex;
        align-items: center;
        gap: 5px;
        cursor: pointer;
    }

    .location-picker .fa-location-dot {
        font-size: 16px;
    }

    /* Cột giữa: Thanh tìm kiếm */
    .navbar-center {
        flex-grow: 1;
        max-width: 450px;
    }

    .search-bar {
        display: flex;
        background-color: var(--cps-text-white);
        border-radius: 8px;
        overflow: hidden;
    }

    .search-bar input {
        border: none;
        outline: none;
        padding: 10px 15px;
        font-size: 14px;
        width: 100%;
        color: var(--cps-text-gray);
    }

    .search-bar input::placeholder {
        color: #aaa;
    }

    .search-bar button {
        background: none;
        border: none;
        padding: 0 15px;
        cursor: pointer;
        color: var(--cps-text-gray);
        font-size: 16px;
    }

    /* Cột phải: Các nút hành động */
    .navbar-right {
        display: flex;
        align-items: center;
        gap: 20px;
    }

    .action-link {
        color: var(--cps-text-white);
        text-decoration: none;
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 13px;
        font-weight: 500;
        transition: opacity 0.2s;
    }

    .action-link:hover {
        opacity: 0.8;
    }

    .action-link i {
        font-size: 24px;
    }

    /* Điều chỉnh cho nút giỏ hàng và người dùng có text nhỏ hơn */
    .action-link.user-link, .action-link.cart-link {
        flex-direction: column;
        gap: 2px;
        text-align: center;
    }

    .action-link.user-link i, .action-link.cart-link i {
        font-size: 20px;
        margin-bottom: 2px;
    }

    .logo .logo-text {
        /* Sử dụng font chữ đặc biệt từ Google Fonts */
        font-family: 'Exo 2', sans-serif;
        font-size: 28px;
        font-weight: 700;
        color: #fff;
        text-decoration: none;
        letter-spacing: -1px; /* Giảm khoảng cách giữa các chữ cái một chút */

        /* Hiệu ứng "nghệ thuật" giống CellphoneS */
        /* Tạo một lớp bóng mờ màu trắng để tạo cảm giác phát sáng nhẹ từ bên trong */
        text-shadow: 0px 0px 8px rgba(255, 255, 255, 0.25);

        transition: text-shadow 0.3s;
    }

    .logo .logo-text:hover {
        /* Tăng hiệu ứng phát sáng khi di chuột qua */
        text-shadow: 0px 0px 16px rgba(255, 255, 255, 0.4);
    }

    .user-menu {
        position: relative;
        display: inline-block;
    }

    .dropdown-menu {
        display: none;
        position: absolute;
        background-color: #fff;
        border: 1px solid #ccc;
        min-width: 120px;
        z-index: 10;
        padding: 8px 0;
    }

    .dropdown-menu a {
        display: block;
        padding: 8px 16px;
        text-decoration: none;
        color: #333;
    }

    .dropdown-menu a:hover {
        background-color: #f0f0f0;
    }

    .user-menu:hover .dropdown-menu {
        display: block;
    }

</style>

<%-- BỔ SUNG: HTML cho navbar --%>
<header class="main-header">
    <nav class="navbar-container">
        <!-- Cột trái: Logo và địa điểm -->
        <div class="navbar-left">
            <!-- SỬ DỤNG KHỐI LOGO TEXT ĐÃ ĐƯỢC STYLE -->
            <div class="logo">
                <a href="${pageContext.request.contextPath}/" class="logo-text">PhoneHub</a>
            </div>
            <div class="location-picker">
                <i class="fa-solid fa-location-dot"></i>
                <span>Xem giá tại<br>Hà Nội</span>
            </div>
        </div>

        <!-- Cột giữa: Thanh tìm kiếm -->
        <div class="navbar-center">
            <form action="${pageContext.request.contextPath}/search" method="get" class="search-bar">
                <input type="text" name="q" placeholder="Bạn cần tìm gì?">
                <button type="submit"><i class="fa-solid fa-magnifying-glass"></i></button>
            </form>
        </div>

        <!-- Cột phải: Các nút hành động -->
        <div class="navbar-right">
            <a href="tel:18002097" class="action-link">
                <i class="fa-solid fa-phone"></i>
                <span>Gọi mua hàng<br>1800.2097</span>
            </a>
            <a href="#" class="action-link">
                <i class="fa-solid fa-location-dot"></i>
                <span>Cửa hàng<br>gần bạn</span>
            </a>
            <a href="#" class="action-link">
                <i class="fa-solid fa-truck-fast"></i>
                <span>Tra cứu<br>đơn hàng</span>
            </a>
            <a href="${pageContext.request.contextPath}/cart" class="action-link cart-link">
                <i class="fa-solid fa-cart-shopping"></i>
                <span>Giỏ hàng</span>
            </a>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="user-menu">
                            <a href="#" class="action-link user-link">
                                <i class="fa-regular fa-user"></i>
                                <span>${sessionScope.user.fullName}</span>
                            </a>
                            <div class="dropdown-menu">
                                <a href="${pageContext.request.contextPath}/update-profile">User Profile</a>
                                <a href="${pageContext.request.contextPath}/logout">Log out</a>
                            </div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="action-link user-link">
                        <i class="fa-regular fa-user"></i>
                        <span>Đăng nhập</span>
                    </c:otherwise>
                </c:choose>
            </a>
        </div>
    </nav>
</header>