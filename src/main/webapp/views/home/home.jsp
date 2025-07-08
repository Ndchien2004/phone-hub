<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phone Store - Trang chủ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        /* === BIẾN MÀU VÀ FONT CHỮ === */
        :root {
            --cps-red: #d70018;
            --cps-text-dark: #333;
            --cps-text-light: #6c757d;
            --cps-bg-light: #f8f9fa;
            --cps-border: #e9ecef;
            --cps-star-yellow: #ffc107;
            --cps-primary: #0d6efd;
        }

        body {
            background-color: #fff;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }

        /* === BỐ CỤC CHUNG === */
        .main-container {
            max-width: 1200px;
            margin: 0 auto;
        }
        .main-content { padding: 20px; }
        .section-title { font-size: 24px; font-weight: bold; color: var(--cps-text-dark); text-transform: uppercase; margin-bottom: 20px; }

        /* === CẢI TIẾN SIDEBAR BỘ LỌC === */
        .sidebar {
            background-color: #fff;
            border: 1px solid var(--cps-border);
            border-radius: 12px;
            padding: 20px;
            height: fit-content;
        }
        .filter-group {
            margin-bottom: 20px; /* Tạo khoảng cách giữa các nhóm filter */
            padding-bottom: 20px;
            border-bottom: 1px solid var(--cps-border);
        }
        .filter-group:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 0;
        }
        .filter-group .form-label {
            font-weight: 600;
            margin-bottom: 10px;
        }

        /* === CSS CHO THẺ SẢN PHẨM MỚI === */
        .product-card {
            background-color: #fff;
            border: 1px solid var(--cps-border);
            border-radius: 12px;
            overflow: hidden;
            transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
        }

        .card-img-container {
            position: relative; /* Quan trọng để định vị nút "Thêm giỏ hàng" */
            padding: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 220px;
        }
        .product-card-img { max-height: 100%; max-width: 100%; object-fit: contain; }
        .discount-badge { position: absolute; top: 10px; left: 10px; background-color: var(--cps-red); color: #fff; padding: 4px 8px; border-radius: 6px; font-size: 12px; font-weight: bold; }
        .installment-badge { position: absolute; top: 10px; right: 10px; background-color: var(--cps-bg-light); color: var(--cps-text-light); padding: 4px 8px; border-radius: 6px; font-size: 12px; }

        /* === NÚT THÊM GIỎ HÀNG MỚI === */
        .add-to-cart-btn {
            position: absolute;
            bottom: 10px;
            right: 10px;
            width: 36px;
            height: 36px;
            background-color: rgba(255, 255, 255, 0.85);
            border: 1px solid var(--cps-border);
            border-radius: 50%;
            color: var(--cps-primary);
            font-size: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.2s ease;
            backdrop-filter: blur(2px);
            z-index: 2;
        }
        .add-to-cart-btn:hover {
            background-color: var(--cps-primary);
            color: #fff;
            transform: scale(1.1);
        }

        .product-card .card-body { padding: 15px; display: flex; flex-direction: column; flex-grow: 1; }
        .product-title-link { text-decoration: none; color: inherit; }
        .product-title {
            font-size: 14px;
            font-weight: 600;
            color: var(--cps-text-dark);
            line-height: 1.4;
            height: 39.2px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
            margin-bottom: 10px;
        }
        .price-sale { font-size: 18px; font-weight: bold; color: var(--cps-red); }
        .price-original { font-size: 13px; color: var(--cps-text-light); text-decoration: line-through; margin-left: 8px; }
        .promo-info { background-color: var(--cps-bg-light); border: 1px solid var(--cps-border); border-radius: 8px; padding: 8px; font-size: 12px; color: var(--cps-text-dark); margin-top: 10px; }
        .card-footer-actions { margin-top: auto; padding-top: 10px; border-top: 1px solid var(--cps-border); display: flex; justify-content: space-between; align-items: center; font-size: 14px; }
        .rating { color: var(--cps-text-dark); }
        .rating .fa-star { color: var(--cps-star-yellow); }
        .wishlist { color: var(--cps-red); font-weight: 500; }
        .wishlist .fa-heart { margin-right: 4px; }
    </style>
</head>
<body>

<%@ include file="../layout/navbar.jsp" %>

<div class="main-container container-fluid mt-4">
    <div class="row">
        <!-- Sidebar -->
        <aside class="col-lg-3 d-none d-lg-block">
            <div class="sidebar">
                <h4 class="mb-3"><i class="fas fa-filter me-2"></i>Bộ lọc</h4>
                <form action="search" method="get">
                    <div class="filter-group">
                        <label for="searchName" class="form-label">Tên sản phẩm</label>
                        <input type="text" class="form-control" id="searchName" name="keyword" placeholder="Nhập tên..." value="${keyword}">
                    </div>
                    <div class="filter-group">
                        <label class="form-label">Danh mục</label>
                        <c:forEach var="category" items="${categories}">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" value="${category.settingId}" id="cat-${category.settingId}" name="category" <c:forEach var="id" items="${selectedCategories}"><c:if test="${id == category.settingId}">checked</c:if></c:forEach>>
                                <label class="form-check-label" for="cat-${category.settingId}">${category.name}</label>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="filter-group">
                        <label class="form-label">Khoảng giá</label>
                        <div class="row g-2">
                            <div class="col-6"><input type="number" class="form-control" name="minPrice" placeholder="Từ" value="${minPrice}"></div>
                            <div class="col-6"><input type="number" class="form-control" name="maxPrice" placeholder="Đến" value="${maxPrice}"></div>
                        </div>
                    </div>
                    <div class="filter-group">
                        <label for="sortBy" class="form-label">Sắp xếp</label>
                        <select class="form-select" id="sortBy" name="sortBy">
                            <option value="">Mặc định</option>
                            <option value="name-asc">Tên A-Z</option>
                            <option value="name-desc">Tên Z-A</option>
                            <option value="price-asc">Giá thấp đến cao</option>
                            <option value="price-desc">Giá cao đến thấp</option>
                        </select>
                    </div>
                    <button class="btn btn-primary w-100" type="submit"><i class="fas fa-check me-2"></i>Áp dụng</button>
                </form>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="col-lg-9 main-content">
            <h1 class="section-title">Điện thoại nổi bật</h1>
            <div class="row g-3" id="productsContainer">
                <c:forEach var="product" items="${pageResult.content}">
                    <div class="col-xl-3 col-lg-4 col-md-4 col-sm-6">
                        <div class="product-card">
                            <div class="card-img-container">
                                <c:if test="${product.priceOrigin > product.priceSale}">
                                    <c:set var="discountPercent" value="${(1 - (product.priceSale / product.priceOrigin)) * 100}" />
                                    <div class="discount-badge">Giảm <fmt:formatNumber value="${discountPercent}" maxFractionDigits="0"/>%</div>
                                </c:if>
                                <div class="installment-badge">Trả góp 0%</div>

                                    <%-- Link vào ảnh để xem chi tiết --%>
                                <a href="product-detail?id=${product.id}">
                                    <img src="${product.imageUrl}.jpg" alt="${product.name}" class="product-card-img">
                                </a>

                                    <%-- Nút thêm vào giỏ hàng được đặt ở góc --%>
                                <form action="${pageContext.request.contextPath}/add-to-cart" method="get" style="display: inline;">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <input type="hidden" name="quantity" value="1">
                                    <input type="hidden" name="price" value="${product.priceSale}">
                                    <button type="submit" class="add-to-cart-btn" title="Thêm vào giỏ hàng">
                                        <i class="fas fa-cart-plus"></i>
                                    </button>
                                </form>
                            </div>
                            <div class="card-body">
                                <a href="product-detail?id=${product.id}" class="product-title-link">
                                    <h3 class="product-title">${product.name}</h3>
                                </a>
                                <div class="price-block">
                                    <span class="price-sale"><fmt:formatNumber value="${product.priceSale}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ</span>
                                    <c:if test="${product.priceOrigin > product.priceSale}">
                                        <span class="price-original"><fmt:formatNumber value="${product.priceOrigin}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ</span>
                                    </c:if>
                                </div>
                                <div class="promo-info">Không phí chuyển đổi khi trả góp 0%...</div>
                                <div class="card-footer-actions">
                                    <div class="rating"><i class="fas fa-star"></i> 4.9</div>
                                    <div class="wishlist"><i class="far fa-heart"></i> Yêu thích</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <%-- Pagination giữ nguyên --%>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<%-- Script giữ nguyên --%>
</body>
</html>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function showToast(message) {
        // Create toast element
        const toastHtml = `
                <div class="toast align-items-center text-white bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
                    <div class="d-flex">
                        <div class="toast-body">
                            ${message}
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                </div>
            `;

        // Create toast container if it doesn't exist
        let toastContainer = document.getElementById('toastContainer');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'toastContainer';
            toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
            toastContainer.style.zIndex = '9999';
            document.body.appendChild(toastContainer);
        }

        // Add toast to container
        toastContainer.innerHTML = toastHtml;

        // Initialize and show toast
        const toastElement = toastContainer.querySelector('.toast');
        const toast = new bootstrap.Toast(toastElement);
        toast.show();
    }
</script>
</body>
</html>