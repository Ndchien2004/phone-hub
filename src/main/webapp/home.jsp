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
        .product-image {
            height: 200px;
            background-color: #f8f9fa;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 1px dashed #dee2e6;
            color: #6c757d;
            font-size: 14px;
        }
        .price-original {
            text-decoration: line-through;
            color: #6c757d;
            font-size: 0.9em;
        }
        .price-sale {
            color: #dc3545;
            font-weight: bold;
            font-size: 1.1em;
        }
        .sidebar {
            background-color: #f8f9fa;
            border-right: 1px solid #dee2e6;
            min-height: calc(100vh - 56px);
        }
        .cart-badge {
            position: absolute;
            top: -8px;
            right: -8px;
            background-color: #dc3545;
            color: white;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            font-size: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    </style>
</head>
<body>
<!-- Header Navbar -->
<nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
    <div class="container-fluid">
        <!-- Logo -->
        <a class="navbar-brand fw-bold" href="#">PhoneStore</a>

        <!-- Right side items -->
        <div class="d-flex align-items-center">
            <!-- Cart Icon -->
            <div class="position-relative me-3">
                <i class="fas fa-shopping-cart fa-lg text-primary"></i>
                <span class="cart-badge" id="cartCount">3</span>
            </div>

            <!-- Register Button -->
            <button class="btn btn-outline-primary">Đăng ký</button>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row">
        <!-- Sidebar -->
<%--        <form action="search" method="get">--%>
        <div class="col-md-3 col-lg-2 sidebar p-3">
            <h5 class="mb-3">Tìm kiếm</h5>

            <!-- Search Name -->
            <div class="mb-3">
                <label for="searchName" class="form-label">Tên sản phẩm</label>
                <input type="text" class="form-control" id="searchName" name="keyword" placeholder="Nhập tên điện thoại..." value="${keyword != null ? keyword : ''}">
            </div>

            <!-- Category Search -->
            <div class="mb-3">
                <label class="form-label">Danh mục</label>
                <c:forEach var="category" items="${categories}">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" value="${category.name}" id="${category.settingId}">
                        <label class="form-check-label" for="${category.name}">${category.name}</label>
                    </div>
                </c:forEach>
            </div>

            <!-- Price Range -->
            <div class="mb-3">
                <label class="form-label">Giá</label>
                <div class="row g-2">
                    <div class="col-6">
                        <input type="number" class="form-control form-control-sm" placeholder="Từ" id="priceFrom">
                    </div>
                    <div class="col-6">
                        <input type="number" class="form-control form-control-sm" placeholder="Đến" id="priceTo">
                    </div>
                </div>
            </div>

            <!-- Sort Options -->
            <div class="mb-3">
                <label for="sortBy" class="form-label">Sắp xếp theo</label>
                <select class="form-select" id="sortBy">
                    <option value="">Chọn sắp xếp</option>
                    <option value="name-asc">Tên A-Z</option>
                    <option value="name-desc">Tên Z-A</option>
                    <option value="price-asc">Giá thấp đến cao</option>
                    <option value="price-desc">Giá cao đến thấp</option>
                </select>
            </div>

            <!-- Search Button -->
            <button class="btn btn-primary w-100" type="submit">
                <i class="fas fa-search me-2"></i>Tìm kiếm
            </button>
        </div>
<%--        </form>--%>
        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 p-4">
            <h2 class="mb-4">Điện thoại nổi bật</h2>

            <!-- Products Grid -->
            <div class="row g-4" id="productsContainer">

                <!--Test truc tiep du lieu lay duoc-->
                <%--<p>Hiện có: ${fn:length(pageResult.content)} sản phẩm</p>
                <c:forEach var="product" items="${pageResult.content}">
                    ${product}<br>
                </c:forEach>--%>
                <%--<c:forEach var="category" items="${categories}">
                    ${category.name}
                </c:forEach>--%>

                <!-- Product Card -->
                <c:forEach var="product" items="${pageResult.content}">
                <div class="col-lg-3 col-md-4 col-sm-6">
                    <div class="card h-100 shadow-sm">
                        <div class="product-image">
                            <img src="${product.imageUrl}.jpg" alt="Ảnh sản phẩm" class="img-fluid" style="max-height: 100%;">
                        </div>
                        <div class="card-body d-flex flex-column">
                            <h6 class="card-title">${product.name} || ${product.briefInfo}</h6>
                            <div class="mb-2">
                                <span class="price-original"><fmt:formatNumber value="${product.priceOrigin}" type="number" groupingUsed="true" maxFractionDigits="2" minFractionDigits="0" /> VNĐ</span>
                                <div class="price-sale"><fmt:formatNumber value="${product.priceSale}" type="number" groupingUsed="true" maxFractionDigits="2" minFractionDigits="0" /> VNĐ</div>
                            </div>
                            <div class="mt-auto">
                                <button class="btn btn-outline-primary btn-sm w-100 mb-2" onclick="addToCart(1)">
                                    <i class="fas fa-cart-plus me-1"></i>Thêm vào giỏ
                                </button>
                                <button class="btn btn-primary btn-sm w-100">
                                    <i class="fas fa-bolt me-1"></i>Mua ngay
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                </c:forEach>
            </div>
                <!-- Pagination -->
            <c:if test="${pageResult.totalPages > 1}">
                <nav>
                    <ul class="pagination justify-content-center mt-4">

                        <!-- Previous -->
                        <c:choose>
                        <c:when test="${pageResult.currentPage == 1}">
                        <li class="page-item disabled">
                            </c:when>
                            <c:otherwise>
                        <li class="page-item">
                            </c:otherwise>
                            </c:choose>
                            <a class="page-link" href="?page=${pageResult.currentPage - 1}">&laquo;</a>
                        </li>

                        <!-- Pages -->
                        <c:forEach var="i" begin="1" end="${pageResult.totalPages}">
                            <li class="page-item ${pageResult.currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="?page=${i}">${i}</a>
                            </li>
                        </c:forEach>

                        <!-- Next -->
                        <c:choose>
                        <c:when test="${pageResult.currentPage == pageResult.totalPages}">
                        <li class="page-item disabled">
                            </c:when>
                            <c:otherwise>
                        <li class="page-item">
                            </c:otherwise>
                            </c:choose>
                            <a class="page-link" href="?page=${pageResult.currentPage + 1}">&raquo;</a>
                        </li>

                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let cartCount = 3;

    function addToCart(productId) {
        cartCount++;
        document.getElementById('cartCount').textContent = cartCount;

        // Show toast notification
        showToast('Đã thêm sản phẩm vào giỏ hàng!');
    }

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

    function searchProducts() {
        const searchName = document.getElementById('searchName').value;
        const categories = Array.from(document.querySelectorAll('input[type="checkbox"]:checked')).map(cb => cb.value);
        const priceFrom = document.getElementById('priceFrom').value;
        const priceTo = document.getElementById('priceTo').value;
        const sortBy = document.getElementById('sortBy').value;

        console.log('Searching with:', {
            name: searchName,
            categories: categories,
            priceRange: { from: priceFrom, to: priceTo },
            sort: sortBy
        });

        showToast('Tìm kiếm đã được thực hiện!');
    }

    // Add event listener for Enter key on search input
    document.getElementById('searchName').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchProducts();
        }
    });
</script>
</body>
</html>