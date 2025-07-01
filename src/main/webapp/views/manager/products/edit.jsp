<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!-- Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="fas fa-edit me-2"></i>Edit Product</h2>
                <a href="${pageContext.request.contextPath}/manager/products" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Back to Products
                </a>
            </div>

            <!-- Edit Product Form -->
            <div class="card">
                <div class="card-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/manager/products" method="post"
                        enctype="multipart/form-data" id="productForm">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="productId" value="${product.productId}">

                        <div class="row">
                            <!-- Left Column -->
                            <div class="col-md-8">
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="productName" class="form-label">Product Name <span
                                                class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="productName" name="productName"
                                            value="${product.productName}" required>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="categoryId" class="form-label">Category <span
                                                class="text-danger">*</span></label>
                                        <select class="form-select" id="categoryId" name="categoryId" required>
                                            <option value="">Select Category</option>
                                            <c:forEach var="category" items="${categories}">
                                                <option value="${category.settingId}"
                                                    ${product.categoryId==category.settingId ? 'selected' : '' }>
                                                    ${category.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="color" class="form-label">Color <span
                                                class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="color" name="color"
                                            value="${product.color}" required>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="memory" class="form-label">Memory <span
                                                class="text-danger">*</span></label>
                                        <select class="form-select" id="memory" name="memory" required>
                                            <option value="">Select Memory</option>
                                            <option value="64GB" ${product.memory=='64GB' ? 'selected' : '' }>64GB
                                            </option>
                                            <option value="128GB" ${product.memory=='128GB' ? 'selected' : '' }>128GB
                                            </option>
                                            <option value="256GB" ${product.memory=='256GB' ? 'selected' : '' }>256GB
                                            </option>
                                            <option value="512GB" ${product.memory=='512GB' ? 'selected' : '' }>512GB
                                            </option>
                                            <option value="1TB" ${product.memory=='1TB' ? 'selected' : '' }>1TB</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-md-4 mb-3">
                                        <label for="quantity" class="form-label">Quantity <span
                                                class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="quantity" name="quantity"
                                            value="${product.quantity}" min="0" required>
                                    </div>
                                    <div class="col-md-4 mb-3">
                                        <label for="priceOrigin" class="form-label">Original Price (₫) <span
                                                class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="priceOrigin" name="priceOrigin"
                                            value="${product.priceOrigin}" min="0" required>
                                    </div>
                                    <div class="col-md-4 mb-3">
                                        <label for="priceSale" class="form-label">Sale Price (₫) <span
                                                class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="priceSale" name="priceSale"
                                            value="${product.priceSale}" min="0" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="briefInfo" class="form-label">Brief Information</label>
                                    <input type="text" class="form-control" id="briefInfo" name="briefInfo"
                                        value="${product.briefInfo}" maxlength="500">
                                    <div class="form-text">Short description that appears in product listings</div>
                                </div>

                                <div class="mb-3">
                                    <label for="description" class="form-label">Description</label>
                                    <textarea class="form-control" id="description" name="description"
                                        rows="5">${product.description}</textarea>
                                    <div class="form-text">Detailed product description</div>
                                </div>
                            </div>

                            <!-- Right Column -->
                            <div class="col-md-4">
                                <div class="mb-3">
                                    <label for="image" class="form-label">Product Image</label>
                                    <input type="file" class="form-control" id="image" name="image" accept="image/*"
                                        onchange="previewImage(this)">
                                    <div class="form-text">Leave empty to keep current image. Max size: 5MB</div>
                                </div>

                                <!-- Current Image -->
                                <div class="mb-3">
                                    <div class="form-label fw-bold">Current Image</div>
                                    <div id="currentImage" class="text-center">
                                        <c:choose>
                                            <c:when test="${not empty product.imageUrl}">
                                                <img src="${product.imageUrl}" alt="${product.productName}"
                                                    class="img-fluid rounded" style="max-height: 200px;">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="p-4 border rounded bg-light">
                                                    <i class="fas fa-image fa-2x text-muted mb-2"></i>
                                                    <p class="text-muted mb-0">No image</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <!-- Image Preview -->
                                <div class="mb-3">
                                    <div id="imagePreview" class="text-center" style="display: none;">
                                        <div class="form-label fw-bold">New Image Preview</div>
                                        <img id="previewImg" src="" alt="Preview" class="img-fluid rounded"
                                            style="max-height: 200px;">
                                    </div>
                                </div>

                                <!-- Price Calculator -->
                                <div class="card bg-light">
                                    <div class="card-body">
                                        <h6 class="card-title">Price Summary</h6>
                                        <div class="mb-2">
                                            <small class="text-muted">Original Price:</small>
                                            <div id="originalPriceDisplay" class="fw-bold">₫ 0</div>
                                        </div>
                                        <div class="mb-2">
                                            <small class="text-muted">Sale Price:</small>
                                            <div id="salePriceDisplay" class="fw-bold text-danger">₫ 0</div>
                                        </div>
                                        <div>
                                            <small class="text-muted">Discount:</small>
                                            <div id="discountDisplay" class="fw-bold text-success">0%</div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Product Info -->
                                <div class="card bg-info bg-opacity-10 mt-3">
                                    <div class="card-body">
                                        <h6 class="card-title">Product Info</h6>
                                        <small class="text-muted">Product ID:</small>
                                        <div class="fw-bold">#${product.productId}</div>
                                        <small class="text-muted">Created:</small>
                                        <div class="fw-bold">
                                            ${product.formattedCreatedAtWithTime}
                                        </div>
                                        <c:if test="${not empty product.updatedAt}">
                                            <small class="text-muted">Last Updated:</small>
                                            <div class="fw-bold">
                                                ${product.formattedUpdatedAtWithTime}
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <hr>

                        <div class="d-flex justify-content-end">
                            <a href="${pageContext.request.contextPath}/manager/products"
                                class="btn btn-secondary me-2">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-2"></i>Update Product
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <script>
                function previewImage(input) {
                    const file = input.files[0];
                    const preview = document.getElementById('imagePreview');
                    const previewImg = document.getElementById('previewImg');

                    if (file) {
                        // Validate file size (5MB)
                        if (file.size > 5 * 1024 * 1024) {
                            showMessage('error', 'Image size must be less than 5MB');
                            input.value = '';
                            return;
                        }

                        // Validate file type
                        if (!file.type.startsWith('image/')) {
                            showMessage('error', 'Please select a valid image file');
                            input.value = '';
                            return;
                        }

                        const reader = new FileReader();
                        reader.onload = function (e) {
                            previewImg.src = e.target.result;
                            preview.style.display = 'block';
                        };
                        reader.readAsDataURL(file);
                    } else {
                        preview.style.display = 'none';
                    }
                }

                function formatPrice(price) {
                    return new Intl.NumberFormat('vi-VN', {
                        style: 'currency',
                        currency: 'VND'
                    }).format(price);
                }

                function updatePriceCalculator() {
                    const originalPrice = parseFloat(document.getElementById('priceOrigin').value) || 0;
                    const salePrice = parseFloat(document.getElementById('priceSale').value) || 0;

                    document.getElementById('originalPriceDisplay').textContent = formatPrice(originalPrice);
                    document.getElementById('salePriceDisplay').textContent = formatPrice(salePrice);

                    if (originalPrice > 0 && salePrice > 0 && originalPrice > salePrice) {
                        const discount = ((originalPrice - salePrice) / originalPrice) * 100;
                        document.getElementById('discountDisplay').textContent = discount.toFixed(0) + '%';
                    } else {
                        document.getElementById('discountDisplay').textContent = '0%';
                    }
                }

                // Add event listeners
                document.getElementById('priceOrigin').addEventListener('input', updatePriceCalculator);
                document.getElementById('priceSale').addEventListener('input', updatePriceCalculator);

                // Form validation
                document.getElementById('productForm').addEventListener('submit', function (e) {
                    const originalPrice = parseFloat(document.getElementById('priceOrigin').value) || 0;
                    const salePrice = parseFloat(document.getElementById('priceSale').value) || 0;

                    if (salePrice > originalPrice) {
                        e.preventDefault();
                        showMessage('error', 'Sale price cannot be higher than original price');
                        return false;
                    }

                    if (originalPrice <= 0 || salePrice <= 0) {
                        e.preventDefault();
                        showMessage('error', 'Prices must be greater than 0');
                        return false;
                    }
                });

                // Initialize price calculator
                updatePriceCalculator();
            </script>