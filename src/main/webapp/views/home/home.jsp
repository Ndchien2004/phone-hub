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

        /* CSS cho cart-badge đã có trong file navbar.jsp nên có thể không cần ở đây nữa,
           nhưng để lại cũng không sao */
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

<%@ include file="../layout/navbar.jsp" %>

<div class="container">
    <div class="row">

        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 sidebar p-3">
            <h5 class="mb-3">Tìm kiếm</h5>

            <!-- Search Name -->
            <form action="search" method="get">
                <div class="mb-3">
                    <label for="searchName" class="form-label bold">Tên sản phẩm</label>
                    <input type="text" class="form-control" id="searchName" name="keyword"
                           placeholder="Nhập tên điện thoại..." value="${keyword != null ? keyword : ''}">
                </div>

                <!-- Category Search -->
                <div class="mb-3">
                    <label class="form-label bold">Danh mục</label>
                    <c:forEach var="category" items="${categories}">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="${category.settingId}"
                                   id="${category.settingId}" name="category"
                                    <c:forEach var="id" items="${selectedCategories}">
                                        <c:if test="${id == category.settingId}">
                                            checked
                                        </c:if>
                                    </c:forEach> />
                            <label class="form-check-label" for="cat-${category.settingId}">${category.name}</label>
                        </div>
                    </c:forEach>
                </div>

                <!-- Price Range -->
                <div class="mb-3">
                    <label class="form-label bold">Giá</label>
                    <div class="row g-2">
                        <div class="col-6">
                            <input type="number" class="form-control form-control-sm" name="minPrice" placeholder="Từ" id="priceFrom"
                                   value="${minPrice}">
                        </div>
                        <div class="col-6">
                            <input type="number" class="form-control form-control-sm" name="maxPrice" placeholder="Đến" id="priceTo"
                                   value="${maxPrice}">
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
            </form>
        </div>

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 p-4">
            <h2 class="mb-4">Điện thoại nổi bật</h2>

            <!-- Products Grid -->
            <div class="row g-4" id="productsContainer">

                <!-- Product Card -->
                <c:forEach var="product" items="${pageResult.content}">
                    <div class="col-lg-3 col-md-4 col-sm-6">
                        <div class="card h-100 shadow-sm">
                            <div class="product-image">
                                <img src="${product.imageUrl}.jpg" alt="Ảnh sản phẩm" class="img-fluid"
                                     style="max-height: 100%;">
                            </div>
                            <div class="card-body d-flex flex-column">
                                <h6 class="card-title">${product.name} || ${product.briefInfo}</h6>
                                <div class="mb-2">
                                    <span class="price-original"><fmt:formatNumber value="${product.priceOrigin}"
                                                                                   type="number" groupingUsed="true"
                                                                                   maxFractionDigits="2"
                                                                                   minFractionDigits="0"/> VNĐ</span>
                                    <div class="price-sale"><fmt:formatNumber value="${product.priceSale}" type="number"
                                                                              groupingUsed="true" maxFractionDigits="2"
                                                                              minFractionDigits="0"/> VNĐ
                                    </div>
                                </div>

                                    <%--Add to Cart Button--%>
                                <div class="mt-auto">
                                    <form action="${pageContext.request.contextPath}/add-to-cart" method="get">
                                        <input type="hidden" name="productId" value="${product.id}">
                                        <input type="hidden" name="quantity" value="1">
                                        <input type="hidden" name="price" value="${product.priceSale}">
                                        <button type="submit" class="btn btn-outline-primary btn-sm w-100 mb-2">
                                            <i class="fas fa-cart-plus me-1"></i> Thêm vào giỏ
                                        </button>
                                    </form>
                                    <button class="btn btn-primary btn-sm w-100">
                                        <i class="fas fa-bolt me-1"></i>Mua ngay
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Keep the value from search to navigate page if have more than 1 page-->
            <c:set var="baseUrl" value="search?keyword=${keyword}" />
            <c:forEach var="cat" items="${selectedCategories}">
                <c:set var="baseUrl" value="${baseUrl}&category=${cat}" />
            </c:forEach>

            <c:if test="${not empty param.minPrice}">
                <c:set var="baseUrl" value="${baseUrl}&minPrice=${param.minPrice}" />
            </c:if>

            <c:if test="${not empty param.maxPrice}">
                <c:set var="baseUrl" value="${baseUrl}&maxPrice=${param.maxPrice}" />
            </c:if>

            <!-- Pagination -->
            <c:if test="${pageResult.totalPages > 1}">
                <nav>
                    <ul class="pagination justify-content-center mt-4">

                        <!-- Previous -->
                        <li class="page-item ${pageResult.currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${baseUrl}&page=${pageResult.currentPage - 1}">«</a>
                        </li>

                        <!-- Page Numbers -->
                        <c:forEach var="i" begin="1" end="${pageResult.totalPages}">
                            <li class="page-item ${pageResult.currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="${baseUrl}&page=${i}">${i}</a>
                            </li>
                        </c:forEach>

                        <!-- Next -->
                        <li class="page-item ${pageResult.currentPage == pageResult.totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${baseUrl}&page=${pageResult.currentPage + 1}">»</a>
                        </li>

                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

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