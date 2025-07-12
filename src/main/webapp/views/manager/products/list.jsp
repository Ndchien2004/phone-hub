<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!-- Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="fas fa-box me-2"></i>Product Management</h2>
                <a href="${pageContext.request.contextPath}/manager/products?action=add" class="btn btn-primary">
                    <i class="fas fa-plus me-2"></i>Add New Product
                </a>
            </div>

            <!-- Search and Filter -->
            <div class="card mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/manager/products" class="row g-3">
                        <input type="hidden" name="action" value="search">
                        <div class="col-md-6">
                            <div class="input-group">
                                <input type="text" class="form-control" name="keyword" value="${keyword}"
                                    placeholder="Search products...">
                                <button class="btn btn-outline-secondary" type="submit">
                                    <i class="fas fa-search"></i>
                                </button>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <select class="form-select" name="category">
                                <option value="">All Categories</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.settingId}">${category.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select class="form-select" name="pageSize" onchange="this.form.submit()">
                                <option value="10" ${pageSize==10 ? 'selected' : '' }>10 per page</option>
                                <option value="25" ${pageSize==25 ? 'selected' : '' }>25 per page</option>
                                <option value="50" ${pageSize==50 ? 'selected' : '' }>50 per page</option>
                            </select>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Products Table -->
            <div class="card">
                <div class="card-body">
                    <c:if test="${not empty param.success}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            <c:choose>
                                <c:when test="${param.success == 'created'}">Product created successfully!</c:when>
                                <c:when test="${param.success == 'updated'}">Product updated successfully!</c:when>
                                <c:when test="${param.success == 'deleted'}">Product deleted successfully!</c:when>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${empty products}">
                        <div class="text-center py-5">
                            <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                            <h5 class="text-muted">No products found</h5>
                            <p class="text-muted">Start by adding your first product.</p>
                            <a href="${pageContext.request.contextPath}/manager/products?action=add"
                                class="btn btn-primary">
                                <i class="fas fa-plus me-2"></i>Add Product
                            </a>
                        </div>
                    </c:if>

                    <c:if test="${not empty products}">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>Image</th>
                                        <th>Product Name</th>
                                        <th>Color</th>
                                        <th>Memory</th>
                                        <th>Quantity</th>
                                        <th>Price</th>
                                        <th>Discount</th>
                                        <th>Created</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="product" items="${products}">
                                        <tr>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty product.imageUrl}">
                                                        <img src="${product.imageUrl}" alt="${product.productName}"
                                                            class="product-image">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div
                                                            class="product-image bg-light d-flex align-items-center justify-content-center">
                                                            <i class="fas fa-image text-muted"></i>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <strong>${product.productName}</strong>
                                                <c:if test="${not empty product.briefInfo}">
                                                    <br><small class="text-muted">${product.briefInfo}</small>
                                                </c:if>
                                            </td>
                                            <td>
                                                <span class="badge bg-secondary">${product.color}</span>
                                            </td>
                                            <td>
                                                <span class="badge bg-info">${product.memory}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.quantity > 10}">
                                                        <span class="badge bg-success">${product.quantity}</span>
                                                    </c:when>
                                                    <c:when test="${product.quantity > 0}">
                                                        <span class="badge bg-warning">${product.quantity}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">Out of Stock</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="price">
                                                    <fmt:formatNumber value="${product.priceSale}" type="currency"
                                                        currencySymbol="₫" groupingUsed="true" />
                                                </div>
                                                <c:if test="${product.priceOrigin > product.priceSale}">
                                                    <small class="text-muted text-decoration-line-through">
                                                        <fmt:formatNumber value="${product.priceOrigin}" type="currency"
                                                            currencySymbol="₫" groupingUsed="true" />
                                                    </small>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${product.priceOrigin > product.priceSale}">
                                                    <c:set var="discount"
                                                        value="${((product.priceOrigin - product.priceSale) * 100) / product.priceOrigin}" />
                                                    <span class="badge discount-badge">
                                                        <fmt:formatNumber value="${discount}" maxFractionDigits="0" />%
                                                    </span>
                                                </c:if>
                                            </td>
                                            <td>
                                                ${product.formattedCreatedAt}
                                            </td>
                                            <td>
                                                <div class="btn-group" role="group">
                                                    <a href="${pageContext.request.contextPath}/manager/products?action=view&id=${product.productId}"
                                                        class="btn btn-sm btn-outline-info btn-action" title="View">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/manager/products?action=edit&id=${product.productId}"
                                                        class="btn btn-sm btn-outline-warning btn-action" title="Edit">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <button type="button" class="btn btn-sm btn-outline-danger"
                                                        onclick="deleteProduct(${product.productId})" title="Delete">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <nav aria-label="Product pagination">
                                <ul class="pagination justify-content-center mt-4">
                                    <c:if test="${currentPage > 1}">
                                        <li class="page-item">
                                            <a class="page-link"
                                                href="${pageContext.request.contextPath}/manager/products?page=${currentPage-1}&pageSize=${pageSize}">
                                                <i class="fas fa-chevron-left"></i>
                                            </a>
                                        </li>
                                    </c:if>

                                    <c:forEach begin="1" end="${totalPages}" var="page">
                                        <c:if test="${page >= currentPage-2 && page <= currentPage+2}">
                                            <li class="page-item ${page == currentPage ? 'active' : ''}">
                                                <a class="page-link"
                                                    href="${pageContext.request.contextPath}/manager/products?page=${page}&pageSize=${pageSize}">
                                                    ${page}
                                                </a>
                                            </li>
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${currentPage < totalPages}">
                                        <li class="page-item">
                                            <a class="page-link"
                                                href="${pageContext.request.contextPath}/manager/products?page=${currentPage+1}&pageSize=${pageSize}">
                                                <i class="fas fa-chevron-right"></i>
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </nav>

                            <div class="text-center text-muted">
                                Showing ${(currentPage-1)*pageSize + 1} to ${currentPage*pageSize > totalProducts ?
                                totalProducts : currentPage*pageSize}
                                of ${totalProducts} products
                            </div>
                        </c:if>
                    </c:if>
                </div>
            </div>

            <script>
                function deleteProduct(productId) {
                    confirmDelete(function () {
                        fetch('${pageContext.request.contextPath}/manager/products', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            },
                            body: 'action=delete&productId=' + productId
                        })
                            .then(response => response.json())
                            .then(success => {
                                if (success) {
                                    showMessage('success', 'Product deleted successfully!');
                                    setTimeout(() => {
                                        location.reload();
                                    }, 1500);
                                } else {
                                    showMessage('error', 'Failed to delete product!');
                                }
                            })
                            .catch(error => {
                                showMessage('error', 'An error occurred while deleting the product!');
                            });
                    });
                }
            </script>