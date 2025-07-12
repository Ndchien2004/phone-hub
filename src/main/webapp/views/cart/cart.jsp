<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Manage Cart</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
  <style>
    .quantity-input {
      width: 60px;
      text-align: center;
    }
    .btn-update {
      min-width: 80px;
    }
    .debug-info {
      background-color: #f8f9fa;
      border: 1px solid #dee2e6;
      border-radius: 0.375rem;
      padding: 1rem;
      margin-bottom: 1rem;
      font-family: monospace;
      font-size: 0.875rem;
    }
  </style>
</head>
<body class="bg-light">

<div class="container my-5">
  <div class="card shadow rounded-4">
    <div class="card-body">

      <!-- Debug Information -->
      <div class="debug-info">
        <h6>🔍 DEBUG INFO:</h6>
        <p><strong>Cart Object:</strong> ${cart}</p>
        <p><strong>Cart Items:</strong> ${cart.items}</p>
        <p><strong>Cart Items Count:</strong> ${fn:length(cart.items)}</p>
        <p><strong>Cart Total Money:</strong> ${cart.totalMoney}</p>
        <p><strong>Session Cart ID:</strong> ${sessionScope.cartId}</p>
        <p><strong>Success Message:</strong> ${success}</p>
        <p><strong>Error Message:</strong> ${error}</p>
      </div>

      <!-- Success/Error Messages -->
      <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
          <i class="fas fa-check-circle me-2"></i>
            ${success}
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
      </c:if>

      <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
          <i class="fas fa-exclamation-triangle me-2"></i>
            ${error}
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
      </c:if>

      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold">Manage Cart</h3>
        <a href="cart?action=removeAll" class="btn btn-danger"
           onclick="return confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')">
          <i class="fas fa-trash me-2"></i>Remove All
        </a>
      </div>

      <div class="table-responsive">
        <table class="table align-middle">
          <thead class="table-light">
          <tr>
            <th>Image</th>
            <th>Name</th>
            <th>Type</th>
            <th>Quantity</th>
            <th>Amount</th>
            <th>Remove</th>
          </tr>
          </thead>
          <tbody>
          <c:choose>
            <c:when test="${not empty cart and not empty cart.items}">
              <c:forEach var="item" items="${cart.items}">
                <tr>
                  <td style="width: 80px;">
                    <img src="${item.image}" alt="Product" class="img-thumbnail" style="width: 64px; height: 64px; object-fit: cover;">
                  </td>
                  <td>${item.productName}</td>
                  <td>
                    <span class="badge bg-secondary">${item.color}</span>
                  </td>
                  <td style="width: 140px;">
                    <form action="cart" method="post" style="display: inline;">
                      <input type="hidden" name="action" value="updateQuantity">
                      <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                      <div class="d-flex align-items-center">
                        <form method="post">
                          <input type="number"
                                 class="form-control form-control-sm quantity-input"
                                 name="quantity"
                                 value="${item.quantity}"
                                 min="1"
                                 max="20">
                          <button type="submit" class="btn btn-sm btn-outline-primary ms-2 btn-update">
                            <i class="fas fa-sync-alt"></i>
                          </button>
                        </form>
                      </div>
                    </form>
                  </td>
                  <td class="item-total">
                    <fmt:formatNumber value="${item.price * item.quantity}" type="currency" currencySymbol="$" />
                  </td>
                  <td>
                    <a href="cart?action=remove&cartItemId=${item.cartItemId}"
                       class="btn btn-sm btn-danger"
                       onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này?')">
                      <i class="fas fa-trash"></i>
                    </a>
                  </td>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr>
                <td colspan="6" class="text-center py-4">
                  <i class="fas fa-shopping-cart fa-3x text-muted mb-3"></i>
                  <h5 class="text-muted">Giỏ hàng trống</h5>
                  <a href="home" class="btn btn-primary">Tiếp tục mua sắm</a>
                </td>
              </tr>
            </c:otherwise>
          </c:choose>
          </tbody>
        </table>
      </div>

      <c:if test="${not empty cart and not empty cart.items}">
        <div class="d-flex justify-content-end mt-4">
          <div class="text-end">
            <h5>Total Price:</h5>
            <h4 class="text-primary fw-bold">
              <fmt:formatNumber value="${cart.totalMoney}" type="currency" currencySymbol="$" />
            </h4>
            <!-- ======================= DÒNG ĐÃ SỬA ======================= -->
            <a href="${pageContext.request.contextPath}/proceed-to-checkout" class="btn btn-success btn-lg mt-2">
              <i class="fas fa-credit-card me-2"></i> Proceed to Checkout
            </a>
            <!-- ========================================================== -->
          </div>
        </div>
      </c:if>

    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>