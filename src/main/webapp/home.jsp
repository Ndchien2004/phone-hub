<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Commerce Store</title>
    <!-- Bootstrap 5.3.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        .navbar-brand {
            font-weight: bold;
            font-size: 1.5rem;
        }
        .sidebar {
            background-color: #f8f9fa;
            min-height: calc(100vh - 56px);
            padding: 20px 0;
        }
        .product-card {
            border: 1px solid #dee2e6;
            border-radius: 8px;
            transition: transform 0.2s;
        }
        .product-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .product-image {
            height: 200px;
            background-color: #f8f9fa;
            display: flex;
            align-items: center;
            justify-content: center;
            border-bottom: 1px solid #dee2e6;
        }
        .price {
            color: red;
            font-weight: bold;
            font-size: 1.1rem;
        }
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
        }
        .form-check-input:checked {
            background-color: #007bff;
            border-color: #007bff;
        }
        .sidebar-section {
            margin-bottom: 25px;
        }
        .sidebar-section h6 {
            font-weight: bold;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-secondary">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Logo</a>
        <div class="ms-auto">
            <button class="btn btn-outline-light me-2">
                <i class="bi bi-cart"></i>
                <span class="badge bg-danger">3</span>
            </button>
            <button class="btn btn-primary">Register</button>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 sidebar">
            <!-- Search Section -->
            <div class="sidebar-section">
                <h6>Search Products</h6>
                <form action="<%-- TODO: Add servlet URL for search --%>" method="GET">
                    <div class="input-group mb-3">
                        <input type="text" class="form-control" placeholder="Search by product name..." name="search" value="${param.search}">
                        <button class="btn btn-primary" type="submit">Search</button>
                    </div>
                </form>
            </div>

            <!-- Categories Section -->
            <div class="sidebar-section">
                <h6>Categories</h6>
                <form action="<%-- TODO: Add servlet URL for category filter --%>" method="GET">
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="checkbox" value="electronics" id="electronics" name="category">
                        <label class="form-check-label" for="electronics">Electronics</label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="checkbox" value="clothing" id="clothing" name="category">
                        <label class="form-check-label" for="clothing">Clothing</label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="checkbox" value="books" id="books" name="category">
                        <label class="form-check-label" for="books">Books</label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="checkbox" value="home-garden" id="home-garden" name="category">
                        <label class="form-check-label" for="home-garden">Home & Garden</label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="checkbox" value="sports" id="sports" name="category">
                        <label class="form-check-label" for="sports">Sports</label>
                    </div>
                </form>
            </div>

            <!-- Price Range Section -->
            <div class="sidebar-section">
                <h6>Price Range</h6>
                <form action="<%-- TODO: Add servlet URL for price filter --%>" method="GET">
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="radio" name="priceRange" id="under25" value="0-25">
                        <label class="form-check-label" for="under25">Under $25</label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="radio" name="priceRange" id="25-50" value="25-50">
                        <label class="form-check-label" for="25-50">$25 - $50</label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="radio" name="priceRange" id="50-100" value="50-100">
                        <label class="form-check-label" for="50-100">$50 - $100</label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="radio" name="priceRange" id="over100" value="100+">
                        <label class="form-check-label" for="over100">$100+</label>
                    </div>
                </form>
            </div>

            <!-- Sort By Section -->
            <div class="sidebar-section">
                <h6>Sort By</h6>
                <form action="<%-- TODO: Add servlet URL for sorting --%>" method="GET">
                    <select class="form-select mb-3" name="sortBy">
                        <option value="price-low-high" ${param.sortBy == 'price-low-high' ? 'selected' : ''}>Price: Low to High</option>
                        <option value="price-high-low" ${param.sortBy == 'price-high-low' ? 'selected' : ''}>Price: High to Low</option>
                        <option value="name-a-z" ${param.sortBy == 'name-a-z' ? 'selected' : ''}>Name: A to Z</option>
                        <option value="name-z-a" ${param.sortBy == 'name-z-a' ? 'selected' : ''}>Name: Z to A</option>
                    </select>
                    <button type="submit" class="btn btn-primary btn-sm">Apply Sort</button>
                </form>
            </div>
        </div>

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10">
            <div class="container-fluid py-4">
                <!-- Products Grid -->
                <div class="row">

                    <c:forEach var="product" items="${page.content}" varStatus="status">
                        <div class="col-md-4 col-lg-3 mb-4">
                            <div class="card product-card h-100">
                                <div class="product-image">
                                    <c:choose>
                                        <c:when test="${product != null and not empty product.imageUrl}">
                                            <img src="${product.imageUrl}" alt="${product.name}" class="img-fluid" style="max-height: 180px;">
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Product's Image</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="card-body d-flex flex-column">
                                    <h6 class="card-title">${product.name}</h6>
                                    <p class="price mt-auto mb-2">
                                        <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true" maxFractionDigits="2" minFractionDigits="0" /> VNĐ
                                    </p>
                                    <form action="<%-- TODO: Add servlet URL for add to cart --%>" method="POST">
                                        <input type="hidden" name="productId" value="${product.id}">
                                        <input type="hidden" name="quantity" value="1">
                                        <button type="submit" class="btn btn-primary btn-sm w-100">Add to Cart</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <!-- Pagination -->
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${page.currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${page.currentPage - 1}" tabindex="-1">← Prev</a>
                        </li>

                        <c:forEach begin="1" end="${page.totalPages}" var="p">
                            <li class="page-item ${p == page.currentPage ? 'active' : ''}">
                                <a class="page-link" href="?page=${p}">${p}</a>
                            </li>
                        </c:forEach>

                        <li class="page-item ${page.currentPage == page.totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${page.currentPage + 1}">Next →</a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap 5.3.3 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
