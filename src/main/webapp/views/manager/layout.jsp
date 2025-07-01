<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Phone Hub - Manager Dashboard</title>

            <!-- Bootstrap 5 CSS -->
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
            <!-- Font Awesome -->
            <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
            <!-- SweetAlert2 -->
            <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11.7.32/dist/sweetalert2.min.css" rel="stylesheet">

            <style>
                :root {
                    --primary-color: #6366f1;
                    --primary-dark: #4f46e5;
                    --success-color: #10b981;
                    --warning-color: #f59e0b;
                    --danger-color: #ef4444;
                    --info-color: #3b82f6;
                    --gray-50: #f9fafb;
                    --gray-100: #f3f4f6;
                    --gray-200: #e5e7eb;
                    --gray-300: #d1d5db;
                    --gray-800: #1f2937;
                    --gray-900: #111827;
                    --sidebar-width: 280px;
                    --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
                    --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
                }

                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
                    background-color: var(--gray-50);
                    color: var(--gray-800);
                    line-height: 1.6;
                }

                /* Accessibility Focus Styles */
                *:focus {
                    outline: 2px solid var(--primary-color);
                    outline-offset: 2px;
                    border-radius: 4px;
                }

                /* Sidebar Styles */
                .sidebar {
                    position: fixed;
                    top: 0;
                    left: 0;
                    width: var(--sidebar-width);
                    height: 100vh;
                    background: linear-gradient(135deg, var(--gray-900) 0%, var(--gray-800) 100%);
                    color: white;
                    transform: translateX(-100%);
                    transition: transform 0.3s ease;
                    z-index: 1050;
                    box-shadow: var(--shadow-lg);
                }

                .sidebar.active {
                    transform: translateX(0);
                }

                .sidebar-header {
                    padding: 1.5rem;
                    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
                }

                .sidebar-brand {
                    font-size: 1.5rem;
                    font-weight: 700;
                    color: white;
                    text-decoration: none;
                    display: flex;
                    align-items: center;
                    gap: 0.75rem;
                }

                .sidebar-nav {
                    padding: 1rem 0;
                }

                .nav-item {
                    margin-bottom: 0.25rem;
                }

                .nav-link {
                    display: flex;
                    align-items: center;
                    padding: 0.75rem 1.5rem;
                    color: rgba(255, 255, 255, 0.8);
                    text-decoration: none;
                    transition: all 0.2s ease;
                    border-radius: 0;
                    position: relative;
                }

                .nav-link:hover,
                .nav-link.active {
                    color: white;
                    background: rgba(255, 255, 255, 0.1);
                    backdrop-filter: blur(10px);
                }

                .nav-link.active::before {
                    content: '';
                    position: absolute;
                    left: 0;
                    top: 0;
                    bottom: 0;
                    width: 4px;
                    background: var(--primary-color);
                }

                .nav-link i {
                    width: 20px;
                    margin-right: 0.75rem;
                    text-align: center;
                }

                /* Main Content */
                .main-content {
                    margin-left: 0;
                    min-height: 100vh;
                    transition: margin-left 0.3s ease;
                }

                .main-content.sidebar-open {
                    margin-left: var(--sidebar-width);
                }

                /* Top Navigation */
                .top-nav {
                    background: white;
                    padding: 1rem 1.5rem;
                    border-bottom: 1px solid var(--gray-200);
                    box-shadow: var(--shadow-md);
                    position: sticky;
                    top: 0;
                    z-index: 1040;
                }

                .sidebar-toggle {
                    background: none;
                    border: none;
                    font-size: 1.25rem;
                    color: var(--gray-800);
                    padding: 0.5rem;
                    border-radius: 6px;
                    transition: background-color 0.2s ease;
                }

                .sidebar-toggle:hover {
                    background-color: var(--gray-100);
                }

                /* Content Area */
                .content-area {
                    padding: 2rem 1.5rem;
                    max-width: 1400px;
                    margin: 0 auto;
                }

                /* Enhanced Cards */
                .card {
                    border: none;
                    border-radius: 12px;
                    box-shadow: var(--shadow-md);
                    transition: all 0.2s ease;
                    background: white;
                }

                .card:hover {
                    box-shadow: var(--shadow-lg);
                    transform: translateY(-2px);
                }

                .card-header {
                    background: var(--gray-50);
                    border-bottom: 1px solid var(--gray-200);
                    border-radius: 12px 12px 0 0 !important;
                    padding: 1.25rem 1.5rem;
                }

                /* Enhanced Buttons */
                .btn {
                    border-radius: 8px;
                    font-weight: 500;
                    padding: 0.625rem 1.25rem;
                    transition: all 0.2s ease;
                    border: none;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    gap: 0.5rem;
                }

                .btn:focus {
                    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.3);
                }

                .btn-primary {
                    background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
                    color: white;
                }

                .btn-primary:hover {
                    background: linear-gradient(135deg, var(--primary-dark) 0%, #3730a3 100%);
                    transform: translateY(-1px);
                }

                .btn-success {
                    background: var(--success-color);
                    color: white;
                }

                .btn-warning {
                    background: var(--warning-color);
                    color: white;
                }

                .btn-danger {
                    background: var(--danger-color);
                    color: white;
                }

                .btn-info {
                    background: var(--info-color);
                    color: white;
                }

                .btn-secondary {
                    background: var(--gray-300);
                    color: var(--gray-800);
                }

                .btn-outline-primary {
                    border: 2px solid var(--primary-color);
                    color: var(--primary-color);
                    background: transparent;
                }

                .btn-outline-primary:hover {
                    background: var(--primary-color);
                    color: white;
                }

                /* Enhanced Form Controls */
                .form-control,
                .form-select {
                    border: 2px solid var(--gray-200);
                    border-radius: 8px;
                    padding: 0.75rem 1rem;
                    transition: all 0.2s ease;
                    background: white;
                }

                .form-control:focus,
                .form-select:focus {
                    border-color: var(--primary-color);
                    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
                }

                .form-label {
                    font-weight: 600;
                    color: var(--gray-800);
                    margin-bottom: 0.5rem;
                }

                /* Enhanced Tables */
                .table {
                    border-radius: 8px;
                    overflow: hidden;
                }

                .table thead th {
                    background: var(--gray-50);
                    border: none;
                    font-weight: 600;
                    color: var(--gray-800);
                    padding: 1rem;
                }

                .table tbody tr {
                    transition: background-color 0.2s ease;
                }

                .table tbody tr:hover {
                    background-color: var(--gray-50);
                }

                .table tbody td {
                    padding: 1rem;
                    border-color: var(--gray-200);
                    vertical-align: middle;
                }

                /* Enhanced Badges */
                .badge {
                    border-radius: 6px;
                    padding: 0.375rem 0.75rem;
                    font-weight: 500;
                    font-size: 0.875rem;
                }

                /* Loading States */
                .loading {
                    position: relative;
                    pointer-events: none;
                }

                .loading::after {
                    content: '';
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    width: 20px;
                    height: 20px;
                    border: 2px solid transparent;
                    border-top: 2px solid var(--primary-color);
                    border-radius: 50%;
                    animation: spin 1s linear infinite;
                    transform: translate(-50%, -50%);
                }

                @keyframes spin {
                    0% {
                        transform: translate(-50%, -50%) rotate(0deg);
                    }

                    100% {
                        transform: translate(-50%, -50%) rotate(360deg);
                    }
                }

                /* Responsive Design */
                @media (max-width: 768px) {
                    .sidebar {
                        width: 100%;
                    }

                    .main-content.sidebar-open {
                        margin-left: 0;
                    }

                    .content-area {
                        padding: 1rem;
                    }
                }

                /* Skip Link for Accessibility */
                .skip-link {
                    position: absolute;
                    top: -40px;
                    left: 6px;
                    background: var(--primary-color);
                    color: white;
                    padding: 8px;
                    text-decoration: none;
                    border-radius: 4px;
                    z-index: 1000;
                }

                .skip-link:focus {
                    top: 6px;
                }

                /* Product Image Styles */
                .product-image {
                    width: 60px;
                    height: 60px;
                    object-fit: cover;
                    border-radius: 8px;
                }

                /* Action Button Group */
                .btn-group-actions .btn {
                    padding: 0.375rem 0.75rem;
                    margin: 0 0.125rem;
                }

                /* Price Display */
                .price-display {
                    font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', monospace;
                }

                /* Status Indicators */
                .status-indicator {
                    position: relative;
                    padding-left: 1.5rem;
                }

                .status-indicator::before {
                    content: '';
                    position: absolute;
                    left: 0;
                    top: 50%;
                    width: 8px;
                    height: 8px;
                    border-radius: 50%;
                    transform: translateY(-50%);
                }

                .status-indicator.status-success::before {
                    background: var(--success-color);
                }

                .status-indicator.status-warning::before {
                    background: var(--warning-color);
                }

                .status-indicator.status-danger::before {
                    background: var(--danger-color);
                }
            </style>
        </head>

        <body>
            <!-- Skip Link for Screen Readers -->
            <a href="#main-content" class="skip-link">Skip to main content</a>

            <!-- Sidebar -->
            <nav class="sidebar" id="sidebar" role="navigation" aria-label="Main navigation">
                <div class="sidebar-header">
                    <a href="${pageContext.request.contextPath}/manager/dashboard" class="sidebar-brand">
                        <i class="fas fa-mobile-alt" aria-hidden="true"></i>
                        <span>Phone Hub</span>
                    </a>
                </div>
                <ul class="sidebar-nav list-unstyled">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/dashboard" class="nav-link">
                            <i class="fas fa-tachometer-alt" aria-hidden="true"></i>
                            <span>Dashboard</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/products" class="nav-link active"
                            aria-current="page">
                            <i class="fas fa-box" aria-hidden="true"></i>
                            <span>Products</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/orders" class="nav-link">
                            <i class="fas fa-shopping-cart" aria-hidden="true"></i>
                            <span>Orders</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/customers" class="nav-link">
                            <i class="fas fa-users" aria-hidden="true"></i>
                            <span>Customers</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/analytics" class="nav-link">
                            <i class="fas fa-chart-bar" aria-hidden="true"></i>
                            <span>Analytics</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/manager/settings" class="nav-link">
                            <i class="fas fa-cog" aria-hidden="true"></i>
                            <span>Settings</span>
                        </a>
                    </li>
                </ul>
            </nav>

            <!-- Main Content -->
            <div class="main-content" id="mainContent">
                <!-- Top Navigation -->
                <header class="top-nav">
                    <div class="d-flex justify-content-between align-items-center">
                        <div class="d-flex align-items-center">
                            <button class="sidebar-toggle" id="sidebarToggle" aria-label="Toggle sidebar navigation">
                                <i class="fas fa-bars" aria-hidden="true"></i>
                            </button>
                            <nav aria-label="Breadcrumb" class="ms-3">
                                <ol class="breadcrumb mb-0">
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a>
                                    </li>
                                    <li class="breadcrumb-item active" aria-current="page">Products</li>
                                </ol>
                            </nav>
                        </div>
                        <div class="d-flex align-items-center gap-3">
                            <div class="dropdown">
                                <button class="btn btn-outline-primary dropdown-toggle" type="button"
                                    data-bs-toggle="dropdown" aria-expanded="false" aria-label="User menu">
                                    <i class="fas fa-user-circle me-2" aria-hidden="true"></i>
                                    Admin
                                </button>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item"
                                            href="${pageContext.request.contextPath}/manager/profile">
                                            <i class="fas fa-user me-2" aria-hidden="true"></i>Profile
                                        </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                            <i class="fas fa-sign-out-alt me-2" aria-hidden="true"></i>Logout
                                        </a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </header>

                <!-- Content Area -->
                <main class="content-area" id="main-content">
                    <!-- Page Content Will Be Inserted Here -->
                    <jsp:include page="${param.page != null ? param.page : 'products/list.jsp'}" />
                </main>
            </div>

            <!-- Bootstrap JS -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
            <!-- SweetAlert2 -->
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.7.32/dist/sweetalert2.all.min.js"></script>

            <script>
                // Sidebar Toggle
                const sidebarToggle = document.getElementById('sidebarToggle');
                const sidebar = document.getElementById('sidebar');
                const mainContent = document.getElementById('mainContent');

                // Auto-open sidebar on desktop
                if (window.innerWidth > 768) {
                    sidebar.classList.add('active');
                    mainContent.classList.add('sidebar-open');
                }

                sidebarToggle.addEventListener('click', function () {
                    sidebar.classList.toggle('active');
                    mainContent.classList.toggle('sidebar-open');

                    // Update aria-expanded attribute for accessibility
                    const isExpanded = sidebar.classList.contains('active');
                    sidebarToggle.setAttribute('aria-expanded', isExpanded);
                });

                // Close sidebar when clicking outside on mobile
                document.addEventListener('click', function (event) {
                    if (window.innerWidth <= 768) {
                        if (!sidebar.contains(event.target) && !sidebarToggle.contains(event.target)) {
                            sidebar.classList.remove('active');
                            mainContent.classList.remove('sidebar-open');
                            sidebarToggle.setAttribute('aria-expanded', 'false');
                        }
                    }
                });

                // Keyboard navigation for sidebar
                sidebar.addEventListener('keydown', function (event) {
                    if (event.key === 'Escape') {
                        sidebar.classList.remove('active');
                        mainContent.classList.remove('sidebar-open');
                        sidebarToggle.setAttribute('aria-expanded', 'false');
                        sidebarToggle.focus();
                    }
                });

                // Global utility functions
                function showMessage(type, message, title = null) {
                    const icons = {
                        success: 'success',
                        error: 'error',
                        warning: 'warning',
                        info: 'info'
                    };

                    Swal.fire({
                        icon: icons[type] || 'info',
                        title: title || (type.charAt(0).toUpperCase() + type.slice(1)),
                        text: message,
                        confirmButtonColor: '#6366f1',
                        timer: type === 'success' ? 3000 : null,
                        timerProgressBar: true,
                        showCloseButton: true
                    });
                }

                function confirmDelete(callback, title = 'Are you sure?', text = 'This action cannot be undone.') {
                    Swal.fire({
                        title: title,
                        text: text,
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#ef4444',
                        cancelButtonColor: '#6b7280',
                        confirmButtonText: 'Yes, delete it!',
                        cancelButtonText: 'Cancel',
                        focusCancel: true
                    }).then((result) => {
                        if (result.isConfirmed && callback) {
                            callback();
                        }
                    });
                }

                // Loading state utility
                function setLoading(element, isLoading) {
                    if (isLoading) {
                        element.classList.add('loading');
                        element.disabled = true;
                    } else {
                        element.classList.remove('loading');
                        element.disabled = false;
                    }
                }

                // Form validation utilities
                function validateForm(formElement) {
                    const inputs = formElement.querySelectorAll('input[required], select[required], textarea[required]');
                    let isValid = true;

                    inputs.forEach(input => {
                        if (!input.value.trim()) {
                            input.classList.add('is-invalid');
                            isValid = false;
                        } else {
                            input.classList.remove('is-invalid');
                        }
                    });

                    return isValid;
                }

                // Auto-resize textareas
                document.addEventListener('DOMContentLoaded', function () {
                    const textareas = document.querySelectorAll('textarea[data-auto-resize]');
                    textareas.forEach(textarea => {
                        textarea.addEventListener('input', function () {
                            this.style.height = 'auto';
                            this.style.height = this.scrollHeight + 'px';
                        });
                    });
                });

                // Initialize tooltips
                document.addEventListener('DOMContentLoaded', function () {
                    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                    tooltipTriggerList.map(function (tooltipTriggerEl) {
                        return new bootstrap.Tooltip(tooltipTriggerEl);
                    });
                });
            </script>
        </body>

        </html>
        border-top: none;
        }

        .btn-action {
        margin-right: 5px;
        }

        .product-image {
        width: 60px;
        height: 60px;
        object-fit: cover;
        border-radius: 8px;
        }

        .price {
        font-weight: bold;
        color: #e74c3c;
        }

        .discount-badge {
        background: linear-gradient(45deg, #ff6b6b, #ffa726);
        color: white;
        border: none;
        }
        </style>
        </head>

        <body>
            <!-- Sidebar -->
            <nav class="sidebar">
                <div class="p-3 text-white">
                    <h4><i class="fas fa-mobile-alt me-2"></i>Phone Hub</h4>
                    <small>Manager Dashboard</small>
                </div>
                <ul class="nav nav-pills flex-column">
                    <li class="nav-item">
                        <a class="nav-link ${pageContext.request.requestURI.contains('/products') ? 'active' : ''}"
                            href="${pageContext.request.contextPath}/manager/products">
                            <i class="fas fa-box me-2"></i>Product Management
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/orders">
                            <i class="fas fa-shopping-cart me-2"></i>Order Management
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/customers">
                            <i class="fas fa-users me-2"></i>Customer Management
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/analytics">
                            <i class="fas fa-chart-bar me-2"></i>Analytics
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/settings">
                            <i class="fas fa-cog me-2"></i>Settings
                        </a>
                    </li>
                </ul>

                <div class="position-absolute bottom-0 p-3 w-100">
                    <a href="${pageContext.request.contextPath}/logout" class="nav-link text-white">
                        <i class="fas fa-sign-out-alt me-2"></i>Logout
                    </a>
                </div>
            </nav>

            <!-- Main Content -->
            <div class="main-content">
                <jsp:include page="${content}" />
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
            <script>
                // Format currency
                function formatCurrency(amount) {
                    return new Intl.NumberFormat('vi-VN', {
                        style: 'currency',
                        currency: 'VND'
                    }).format(amount);
                }

                // Show success/error messages
                function showMessage(type, message) {
                    Swal.fire({
                        icon: type,
                        title: type === 'success' ? 'Success!' : 'Error!',
                        text: message,
                        timer: 3000,
                        showConfirmButton: false
                    });
                }

                // Confirm delete action
                function confirmDelete(callback) {
                    Swal.fire({
                        title: 'Are you sure?',
                        text: "You won't be able to revert this!",
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#d33',
                        cancelButtonColor: '#3085d6',
                        confirmButtonText: 'Yes, delete it!'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            callback();
                        }
                    });
                }
            </script>
        </body>

        </html>