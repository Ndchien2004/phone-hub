<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${pageTitle != null ? pageTitle : 'Manager Dashboard'} - Phone Hub</title>

            <!-- Bootstrap 5.3 -->
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <!-- Font Awesome 6.4 -->
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <!-- SweetAlert2 -->
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.7.0/dist/sweetalert2.min.css">
            <!-- Custom CSS -->
            <style>
                :root {
                    --primary-color: #4f46e5;
                    --primary-light: #6366f1;
                    --primary-dark: #3730a3;
                    --secondary-color: #64748b;
                    --success-color: #10b981;
                    --warning-color: #f59e0b;
                    --danger-color: #ef4444;
                    --info-color: #3b82f6;
                    --light-bg: #f8fafc;
                    --border-color: #e2e8f0;
                    --text-muted: #64748b;
                    --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
                    --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1);
                    --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1);
                    --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                }

                * {
                    box-sizing: border-box;
                }

                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                    background-color: var(--light-bg);
                    color: #1e293b;
                    line-height: 1.6;
                }

                /* Sidebar Styles */
                .sidebar {
                    position: fixed;
                    top: 0;
                    left: 0;
                    height: 100vh;
                    width: 280px;
                    background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
                    color: white;
                    transition: var(--transition);
                    z-index: 1000;
                    overflow-y: auto;
                }

                .sidebar.collapsed {
                    width: 80px;
                }

                .sidebar-header {
                    padding: 1.5rem;
                    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
                    display: flex;
                    align-items: center;
                    gap: 0.75rem;
                }

                .sidebar-logo {
                    width: 40px;
                    height: 40px;
                    background: rgba(255, 255, 255, 0.2);
                    border-radius: 8px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 1.25rem;
                    flex-shrink: 0;
                }

                .sidebar-title {
                    font-size: 1.25rem;
                    font-weight: 700;
                    transition: var(--transition);
                }

                .sidebar.collapsed .sidebar-title {
                    opacity: 0;
                    width: 0;
                    overflow: hidden;
                }

                .sidebar-nav {
                    padding: 1rem 0;
                }

                .nav-item {
                    margin: 0.25rem 0;
                }

                .nav-link {
                    display: flex;
                    align-items: center;
                    gap: 0.75rem;
                    padding: 0.75rem 1.5rem;
                    color: rgba(255, 255, 255, 0.8);
                    text-decoration: none;
                    transition: var(--transition);
                    border-radius: 0;
                    position: relative;
                }

                .nav-link:hover {
                    background: rgba(255, 255, 255, 0.1);
                    color: white;
                }

                .nav-link.active {
                    background: rgba(255, 255, 255, 0.15);
                    color: white;
                }

                .nav-link.active::before {
                    content: '';
                    position: absolute;
                    left: 0;
                    top: 0;
                    bottom: 0;
                    width: 3px;
                    background: white;
                }

                .nav-icon {
                    width: 20px;
                    text-align: center;
                    flex-shrink: 0;
                }

                .sidebar.collapsed .nav-text {
                    opacity: 0;
                    width: 0;
                    overflow: hidden;
                }

                /* Main Content */
                .main-content {
                    margin-left: 280px;
                    min-height: 100vh;
                    transition: var(--transition);
                    background-color: var(--light-bg);
                }

                .sidebar.collapsed+.main-content {
                    margin-left: 80px;
                }

                .topbar {
                    background: white;
                    border-bottom: 1px solid var(--border-color);
                    padding: 1rem 2rem;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    box-shadow: var(--shadow-sm);
                    position: sticky;
                    top: 0;
                    z-index: 100;
                }

                .sidebar-toggle {
                    background: none;
                    border: none;
                    color: var(--text-muted);
                    font-size: 1.25rem;
                    cursor: pointer;
                    transition: var(--transition);
                }

                .sidebar-toggle:hover {
                    color: var(--primary-color);
                }

                .topbar-actions {
                    display: flex;
                    align-items: center;
                    gap: 1rem;
                }

                .content-wrapper {
                    padding: 2rem;
                    max-width: 1400px;
                    margin: 0 auto;
                }

                /* Enhanced Card Styles */
                .card {
                    background: white;
                    border: 1px solid var(--border-color);
                    border-radius: 12px;
                    box-shadow: var(--shadow-sm);
                    transition: var(--transition);
                }

                .card:hover {
                    box-shadow: var(--shadow-md);
                }

                .card-header {
                    background: var(--light-bg);
                    border-bottom: 1px solid var(--border-color);
                    border-radius: 12px 12px 0 0;
                    padding: 1.25rem 1.5rem;
                    font-weight: 600;
                }

                /* Enhanced Button Styles */
                .btn {
                    border-radius: 8px;
                    font-weight: 500;
                    transition: var(--transition);
                    display: inline-flex;
                    align-items: center;
                    gap: 0.5rem;
                    text-decoration: none;
                }

                .btn:focus {
                    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
                }

                .btn-primary {
                    background: var(--primary-color);
                    border-color: var(--primary-color);
                }

                .btn-primary:hover {
                    background: var(--primary-dark);
                    border-color: var(--primary-dark);
                    transform: translateY(-1px);
                }

                /* Enhanced Form Styles */
                .form-control,
                .form-select {
                    border-radius: 8px;
                    border: 1px solid var(--border-color);
                    transition: var(--transition);
                    font-size: 0.875rem;
                }

                .form-control:focus,
                .form-select:focus {
                    border-color: var(--primary-color);
                    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
                }

                .form-label {
                    font-weight: 500;
                    color: #374151;
                    margin-bottom: 0.5rem;
                }

                /* Enhanced Table Styles */
                .table {
                    border-collapse: separate;
                    border-spacing: 0;
                }

                .table th {
                    background: var(--light-bg);
                    font-weight: 600;
                    color: #374151;
                    border-bottom: 2px solid var(--border-color);
                    white-space: nowrap;
                }

                .table td {
                    vertical-align: middle;
                    border-bottom: 1px solid var(--border-color);
                }

                .table tbody tr:hover {
                    background-color: rgba(79, 70, 229, 0.02);
                }

                /* Product Image Styles */
                .product-image {
                    width: 60px;
                    height: 60px;
                    object-fit: cover;
                    border-radius: 8px;
                    border: 1px solid var(--border-color);
                }

                /* Badge Styles */
                .badge {
                    font-weight: 500;
                    border-radius: 6px;
                    padding: 0.375rem 0.75rem;
                }

                /* Loading States */
                .loading {
                    opacity: 0.6;
                    pointer-events: none;
                }

                .spinner-border-sm {
                    width: 1rem;
                    height: 1rem;
                }

                /* Accessibility Improvements */
                .sr-only {
                    position: absolute;
                    width: 1px;
                    height: 1px;
                    padding: 0;
                    margin: -1px;
                    overflow: hidden;
                    clip: rect(0, 0, 0, 0);
                    white-space: nowrap;
                    border: 0;
                }

                /* Focus indicators */
                .btn:focus-visible,
                .form-control:focus-visible,
                .form-select:focus-visible {
                    outline: 2px solid var(--primary-color);
                    outline-offset: 2px;
                }

                /* Mobile Responsiveness */
                @media (max-width: 768px) {
                    .sidebar {
                        transform: translateX(-100%);
                    }

                    .sidebar.show {
                        transform: translateX(0);
                    }

                    .main-content {
                        margin-left: 0;
                    }

                    .content-wrapper {
                        padding: 1rem;
                    }

                    .topbar {
                        padding: 1rem;
                    }
                }

                /* Animation Classes */
                .fade-in {
                    animation: fadeIn 0.3s ease-in;
                }

                @keyframes fadeIn {
                    from {
                        opacity: 0;
                        transform: translateY(10px);
                    }

                    to {
                        opacity: 1;
                        transform: translateY(0);
                    }
                }

                .slide-in {
                    animation: slideIn 0.3s ease-out;
                }

                @keyframes slideIn {
                    from {
                        transform: translateX(-20px);
                        opacity: 0;
                    }

                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
            </style>
        </head>

        <body>
            <!-- Sidebar -->
            <nav class="sidebar" id="sidebar" role="navigation" aria-label="Main navigation">
                <div class="sidebar-header">
                    <div class="sidebar-logo">
                        <i class="fas fa-mobile-alt" aria-hidden="true"></i>
                    </div>
                    <div class="sidebar-title">Phone Hub</div>
                </div>

                <ul class="sidebar-nav list-unstyled">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/dashboard" class="nav-link"
                            aria-label="Dashboard">
                            <i class="nav-icon fas fa-tachometer-alt" aria-hidden="true"></i>
                            <span class="nav-text">Dashboard</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/products"
                            class="nav-link ${pageTitle == 'Product Management' ? 'active' : ''}"
                            aria-label="Product Management" ${pageTitle=='Product Management' ? 'aria-current="page"'
                            : '' }>
                            <i class="nav-icon fas fa-box" aria-hidden="true"></i>
                            <span class="nav-text">Products</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/orders" class="nav-link"
                            aria-label="Order Management">
                            <i class="nav-icon fas fa-shopping-cart" aria-hidden="true"></i>
                            <span class="nav-text">Orders</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/categories" class="nav-link"
                            aria-label="Category Management">
                            <i class="nav-icon fas fa-tags" aria-hidden="true"></i>
                            <span class="nav-text">Categories</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/users" class="nav-link"
                            aria-label="User Management">
                            <i class="nav-icon fas fa-users" aria-hidden="true"></i>
                            <span class="nav-text">Users</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/settings" class="nav-link"
                            aria-label="Settings">
                            <i class="nav-icon fas fa-cog" aria-hidden="true"></i>
                            <span class="nav-text">Settings</span>
                        </a>
                    </li>
                </ul>
            </nav>

            <!-- Main Content -->
            <main class="main-content" role="main">
                <!-- Top Bar -->
                <header class="topbar">
                    <div class="d-flex align-items-center">
                        <button class="sidebar-toggle me-3" onclick="toggleSidebar()" aria-label="Toggle sidebar"
                            type="button">
                            <i class="fas fa-bars" aria-hidden="true"></i>
                        </button>
                        <nav aria-label="Breadcrumb">
                            <ol class="breadcrumb mb-0">
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/manager/dashboard">Manager</a>
                                </li>
                                <c:if test="${not empty pageTitle}">
                                    <li class="breadcrumb-item active" aria-current="page">${pageTitle}</li>
                                </c:if>
                            </ol>
                        </nav>
                    </div>

                    <div class="topbar-actions">
                        <button class="btn btn-outline-secondary btn-sm" type="button" aria-label="Notifications">
                            <i class="fas fa-bell" aria-hidden="true"></i>
                            <span class="badge bg-danger rounded-pill ms-1">3</span>
                        </button>
                        <div class="dropdown">
                            <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button"
                                data-bs-toggle="dropdown" aria-expanded="false" aria-label="User menu">
                                <i class="fas fa-user me-1" aria-hidden="true"></i>
                                Manager
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="#">Profile</a></li>
                                <li><a class="dropdown-item" href="#">Settings</a></li>
                                <li>
                                    <hr class="dropdown-divider">
                                </li>
                                <li><a class="dropdown-item" href="#">Logout</a></li>
                            </ul>
                        </div>
                    </div>
                </header>

                <!-- Content Area -->
                <div class="content-wrapper fade-in">
                    <!-- Main content will be injected here -->
                    <c:choose>
                        <c:when test="${not empty contentPage}">
                            <jsp:include page="${contentPage}" />
                        </c:when>
                        <c:otherwise>
                            <!-- Default content if no specific page is included -->
                            <div class="alert alert-info" role="alert">
                                <i class="fas fa-info-circle me-2" aria-hidden="true"></i>
                                Welcome to the Phone Hub Manager Dashboard
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>

            <!-- Loading Overlay -->
            <div id="loadingOverlay" class="position-fixed top-0 start-0 w-100 h-100 d-none"
                style="background: rgba(0,0,0,0.5); z-index: 9999;">
                <div class="d-flex justify-content-center align-items-center h-100">
                    <div class="spinner-border text-primary" aria-hidden="true"></div>
                    <span class="sr-only">Loading...</span>
                </div>
            </div>

            <!-- Toast Container -->
            <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;">
                <!-- Toasts will be inserted here -->
            </div>

            <!-- Bootstrap JS -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <!-- SweetAlert2 -->
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.7.0/dist/sweetalert2.all.min.js"></script>

            <!-- Custom JavaScript -->
            <script>
                // Sidebar toggle functionality
                function toggleSidebar() {
                    const sidebar = document.getElementById('sidebar');
                    sidebar.classList.toggle('collapsed');
                    localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('collapsed'));
                }

                // Restore sidebar state
                document.addEventListener('DOMContentLoaded', function () {
                    const isCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
                    if (isCollapsed) {
                        document.getElementById('sidebar').classList.add('collapsed');
                    }
                });

                // Mobile sidebar toggle
                function toggleMobileSidebar() {
                    const sidebar = document.getElementById('sidebar');
                    sidebar.classList.toggle('show');
                }

                // Loading state management
                function showLoading() {
                    document.getElementById('loadingOverlay').classList.remove('d-none');
                }

                function hideLoading() {
                    document.getElementById('loadingOverlay').classList.add('d-none');
                }

                // Toast notification system
                function showToast(type, title, message) {
                    const toastContainer = document.querySelector('.toast-container');
                    const toastId = 'toast-' + Date.now();

                    const toast = document.createElement('div');
                    toast.className = `toast align-items-center text-bg-${type} border-0`;
                    toast.id = toastId;
                    toast.setAttribute('role', 'alert');
                    toast.setAttribute('aria-live', 'assertive');
                    toast.setAttribute('aria-atomic', 'true');

                    toast.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">
                        <strong>${title}</strong><br>
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                            data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            `;

                    toastContainer.appendChild(toast);

                    const bsToast = new bootstrap.Toast(toast, {
                        autohide: true,
                        delay: 5000
                    });

                    bsToast.show();

                    // Remove toast element after it's hidden
                    toast.addEventListener('hidden.bs.toast', () => {
                        toast.remove();
                    });
                }

                // Enhanced message system using SweetAlert2
                function showMessage(type, message, title = '') {
                    const config = {
                        icon: type,
                        title: title || (type === 'success' ? 'Success!' : type === 'error' ? 'Error!' : 'Info'),
                        text: message,
                        showConfirmButton: true,
                        timer: type === 'success' ? 3000 : undefined,
                        timerProgressBar: true
                    };

                    Swal.fire(config);
                }

                // Confirm delete with enhanced styling
                function confirmDelete(callback, title = 'Delete Item', text = 'This action cannot be undone!') {
                    Swal.fire({
                        title: title,
                        text: text,
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#ef4444',
                        cancelButtonColor: '#64748b',
                        confirmButtonText: 'Yes, delete it!',
                        cancelButtonText: 'Cancel',
                        reverseButtons: true,
                        focusCancel: true
                    }).then((result) => {
                        if (result.isConfirmed) {
                            callback();
                        }
                    });
                }

                // Form validation helper
                function validateForm(formElement) {
                    const requiredFields = formElement.querySelectorAll('[required]');
                    let isValid = true;

                    requiredFields.forEach(field => {
                        if (!field.value.trim()) {
                            field.classList.add('is-invalid');
                            isValid = false;
                        } else {
                            field.classList.remove('is-invalid');
                        }
                    });

                    return isValid;
                }

                // Auto-hide alerts
                document.addEventListener('DOMContentLoaded', function () {
                    const alerts = document.querySelectorAll('.alert[data-bs-dismiss="alert"]');
                    alerts.forEach(alert => {
                        setTimeout(() => {
                            const bsAlert = new bootstrap.Alert(alert);
                            bsAlert.close();
                        }, 5000);
                    });
                });

                // Accessibility: Focus management
                document.addEventListener('keydown', function (e) {
                    // ESC key to close modals
                    if (e.key === 'Escape') {
                        const modals = document.querySelectorAll('.modal.show');
                        modals.forEach(modal => {
                            const bsModal = bootstrap.Modal.getInstance(modal);
                            if (bsModal) bsModal.hide();
                        });
                    }
                });

                // Enhanced AJAX error handling
                function handleAjaxError(xhr, status, error) {
                    console.error('AJAX Error:', { xhr, status, error });

                    let message = 'An unexpected error occurred. Please try again.';

                    if (xhr.status === 404) {
                        message = 'The requested resource was not found.';
                    } else if (xhr.status === 403) {
                        message = 'You do not have permission to perform this action.';
                    } else if (xhr.status === 500) {
                        message = 'Server error. Please contact support if the problem persists.';
                    }

                    showMessage('error', message);
                    hideLoading();
                }
            </script>
        </body>

        </html>