<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - SmartInventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="dashboard-container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>SmartInventory</h2>
            </div>
            <ul class="sidebar-nav">
                <li>
                    <a href="${pageContext.request.contextPath}/dashboard" class="active">
                        <i class="fas fa-tachometer-alt"></i> Dashboard
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/categories/">
                        <i class="fas fa-folder"></i> Categories
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/products/">
                        <i class="fas fa-box"></i> Products
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/suppliers/">
                        <i class="fas fa-truck"></i> Suppliers
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/inventory/">
                        <i class="fas fa-warehouse"></i> Inventory
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/reports/dashboard">
                        <i class="fas fa-chart-bar"></i> Reports
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/profile/">
                        <i class="fas fa-user"></i> Profile
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/auth/logout">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </a>
                </li>
            </ul>
        </aside>
        
        <!-- Main Content -->
        <main class="main-content">
            <div class="top-nav">
                <div class="top-nav-left">
                    <h3>Dashboard</h3>
                </div>
                <div class="top-nav-right">
                    <div class="user-info">
                        <div class="user-avatar">${user.firstName.charAt(0)}${user.lastName.charAt(0)}</div>
                        <div>
                            <strong>${user.firstName} ${user.lastName}</strong><br>
                            <small>${user.roleName}</small>
                        </div>
                    </div>
                </div>
            </div>
            
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">
                    ${requestScope.error}
                </div>
            </c:if>
            
            <c:if test="${not empty requestScope.success}">
                <div class="alert alert-success">
                    ${requestScope.success}
                </div>
            </c:if>
            
            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon primary">
                        <i class="fas fa-box"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Total Products</h5>
                        <h3>${dashboard.totalProducts}</h3>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon success">
                        <i class="fas fa-folder"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Total Categories</h5>
                        <h3>${dashboard.totalCategories}</h3>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon warning">
                        <i class="fas fa-truck"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Total Suppliers</h5>
                        <h3>${dashboard.totalSuppliers}</h3>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon danger">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Low Stock</h5>
                        <h3>${dashboard.lowStockProducts}</h3>
                    </div>
                </div>
            </div>
            
            <!-- Inventory Value Card -->
            <div class="card">
                <div class="card-header">
                    <h4>Inventory Value</h4>
                </div>
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon primary">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                        <div class="stat-info">
                            <h5>Total Value</h5>
                            <h3><fmt:formatNumber value="${dashboard.totalInventoryValue}" type="currency"/></h3>
                        </div>
                    </div>
                    
                    <div class="stat-card">
                        <div class="stat-icon success">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <div class="stat-info">
                            <h5>Potential Profit</h5>
                            <h3><fmt:formatNumber value="${dashboard.totalPotentialProfit}" type="currency"/></h3>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Low Stock Products -->
            <c:if test="${not empty dashboard.lowStockList and not dashboard.lowStockList.isEmpty()}">
                <div class="card">
                    <div class="card-header">
                        <h4>Low Stock Products</h4>
                        <a href="${pageContext.request.contextPath}/products/?status=ACTIVE" class="btn btn-sm btn-primary">View All</a>
                    </div>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>SKU</th>
                                    <th>Product Name</th>
                                    <th>Stock</th>
                                    <th>Min Stock</th>
                                    <th>Needed</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${dashboard.lowStockList}" var="product">
                                    <tr>
                                        <td>${product.sku}</td>
                                        <td>${product.productName}</td>
                                        <td>${product.stockQuantity}</td>
                                        <td>${product.minimumStock}</td>
                                        <td>${product.neededQuantity}</td>
                                        <td>
                                            <span class="badge badge-danger">Low Stock</span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>
            
            <!-- Recent Activities -->
            <c:if test="${not empty dashboard.recentActivities and not dashboard.recentActivities.isEmpty()}">
                <div class="card">
                    <div class="card-header">
                        <h4>Recent Activities</h4>
                    </div>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>User</th>
                                    <th>Action</th>
                                    <th>Module</th>
                                    <th>Description</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${dashboard.recentActivities}" var="activity">
                                    <tr>
                                        <td>${activity.username}</td>
                                        <td>${activity.action}</td>
                                        <td>${activity.module}</td>
                                        <td>${activity.description}</td>
                                        <td>
                                            <fmt:formatDate value="${activity.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
