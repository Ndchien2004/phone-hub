<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!-- Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="fas fa-eye me-2"></i>Product Details</h2>
                <div>
                    <a href="${pageContext.request.contextPath}/manager/products?action=edit&id=${product.productId}"
                        class="btn btn-warning me-2">
                        <i class="fas fa-edit me-2"></i>Edit Product
                    </a>
                    <a href="${pageContext.request.contextPath}/manager/products" class="btn btn-secondary">
                        <i class="fas fa-arrow-left me-2"></i>Back to Products
                    </a>
                </div>
            </div>

            <div class="row">
                <!-- Product Image -->
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-body text-center">
                            <c:choose>
                                <c:when test="${not empty product.imageUrl}">
                                    <img src="${product.imageUrl}" alt="${product.productName}"
                                        class="img-fluid rounded mb-3" style="max-height: 400px;">
                                </c:when>
                                <c:otherwise>
                                    <div class="p-5 border rounded bg-light mb-3">
                                        <i class="fas fa-image fa-4x text-muted mb-3"></i>
                                        <p class="text-muted">No image available</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <!-- Stock Status -->
                            <div class="text-center">
                                <c:choose>
                                    <c:when test="${product.quantity > 10}">
                                        <span class="badge bg-success fs-6">In Stock (${product.quantity})</span>
                                    </c:when>
                                    <c:when test="${product.quantity > 0}">
                                        <span class="badge bg-warning fs-6">Low Stock (${product.quantity})</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger fs-6">Out of Stock</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Product Information -->
                <div class="col-md-8">
                    <div class="card">
                        <div class="card-body">
                            <h3 class="card-title mb-3">${product.productName}</h3>

                            <c:if test="${not empty product.briefInfo}">
                                <p class="text-muted fs-5 mb-3">${product.briefInfo}</p>
                            </c:if>

                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <h5>Product Details</h5>
                                    <table class="table table-borderless">
                                        <tr>
                                            <td class="fw-bold">Product ID:</td>
                                            <td>#${product.productId}</td>
                                        </tr>
                                        <tr>
                                            <td class="fw-bold">Color:</td>
                                            <td><span class="badge bg-secondary">${product.color}</span></td>
                                        </tr>
                                        <tr>
                                            <td class="fw-bold">Memory:</td>
                                            <td><span class="badge bg-info">${product.memory}</span></td>
                                        </tr>
                                        <tr>
                                            <td class="fw-bold">Quantity:</td>
                                            <td>${product.quantity} units</td>
                                        </tr>
                                    </table>
                                </div>

                                <div class="col-md-6">
                                    <h5>Pricing</h5>
                                    <div class="mb-3">
                                        <div class="fs-4 fw-bold text-danger">
                                            <fmt:formatNumber value="${product.priceSale}" type="currency"
                                                currencySymbol="₫" groupingUsed="true" />
                                        </div>
                                        <c:if test="${product.priceOrigin > product.priceSale}">
                                            <div class="text-muted text-decoration-line-through">
                                                <fmt:formatNumber value="${product.priceOrigin}" type="currency"
                                                    currencySymbol="₫" groupingUsed="true" />
                                            </div>
                                            <c:set var="discount"
                                                value="${((product.priceOrigin - product.priceSale) * 100) / product.priceOrigin}" />
                                            <span class="badge bg-gradient bg-success fs-6">
                                                <fmt:formatNumber value="${discount}" maxFractionDigits="0" />% OFF
                                            </span>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <c:if test="${not empty product.description}">
                                <h5>Description</h5>
                                <div class="border rounded p-3 bg-light">
                                    <p class="mb-0">${product.description}</p>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Timestamps -->
                    <div class="card mt-3">
                        <div class="card-body">
                            <h6 class="card-title">Timeline</h6>
                            <div class="row">
                                <div class="col-md-6">
                                    <small class="text-muted">Created:</small>
                                    <div class="fw-bold">
                                        <i class="fas fa-calendar-plus me-2"></i>
                                        ${product.formattedCreatedAtWithTime}
                                    </div>
                                </div>
                                <c:if test="${not empty product.updatedAt}">
                                    <div class="col-md-6">
                                        <small class="text-muted">Last Updated:</small>
                                        <div class="fw-bold">
                                            <i class="fas fa-calendar-edit me-2"></i>
                                            ${product.formattedUpdatedAtWithTime}
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>

                    <!-- Actions -->
                    <div class="card mt-3">
                        <div class="card-body">
                            <h6 class="card-title">Actions</h6>
                            <div class="d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/manager/products?action=edit&id=${product.productId}"
                                    class="btn btn-warning">
                                    <i class="fas fa-edit me-2"></i>Edit Product
                                </a>
                                <button type="button" class="btn btn-danger"
                                    onclick="deleteProduct(${product.productId})">
                                    <i class="fas fa-trash me-2"></i>Delete Product
                                </button>
                                <a href="${pageContext.request.contextPath}/products/${product.productId}"
                                    class="btn btn-info" target="_blank">
                                    <i class="fas fa-external-link-alt me-2"></i>View on Store
                                </a>
                            </div>
                        </div>
                    </div>
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
                                        window.location.href = '${pageContext.request.contextPath}/manager/products';
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