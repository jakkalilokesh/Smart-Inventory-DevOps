<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products - SmartInventory</title>
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
                <li><a href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/categories/"><i class="fas fa-folder"></i> Categories</a></li>
                <li><a href="${pageContext.request.contextPath}/products/" class="active"><i class="fas fa-box"></i> Products</a></li>
                <li><a href="${pageContext.request.contextPath}/suppliers/"><i class="fas fa-truck"></i> Suppliers</a></li>
                <li><a href="${pageContext.request.contextPath}/inventory/"><i class="fas fa-warehouse"></i> Inventory</a></li>
                <li><a href="${pageContext.request.contextPath}/reports/dashboard"><i class="fas fa-chart-bar"></i> Reports</a></li>
                <li><a href="${pageContext.request.contextPath}/profile/"><i class="fas fa-user"></i> Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </aside>
        
        <!-- Main Content -->
        <main class="main-content">
            <div class="top-nav">
                <div class="top-nav-left">
                    <h3>Products</h3>
                </div>
                <div class="top-nav-right">
                    <a href="${pageContext.request.contextPath}/products/create" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Add Product
                    </a>
                </div>
            </div>
            
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>
            
            <c:if test="${not empty requestScope.success}">
                <div class="alert alert-success">${requestScope.success}</div>
            </c:if>
            
            <!-- Search Bar -->
            <div class="card">
                <form action="${pageContext.request.contextPath}/products/" method="get">
                    <div class="search-bar">
                        <input type="text" name="keyword" class="search-input" placeholder="Search products..." value="${keyword}">
                        <select name="category">
                            <option value="">All Categories</option>
                            <c:forEach items="${categories}" var="cat">
                                <option value="${cat.categoryName}" ${selectedCategory == cat.categoryName ? 'selected' : ''}>${cat.categoryName}</option>
                            </c:forEach>
                        </select>
                        <select name="supplier">
                            <option value="">All Suppliers</option>
                            <c:forEach items="${suppliers}" var="sup">
                                <option value="${sup.supplierName}" ${selectedSupplier == sup.supplierName ? 'selected' : ''}>${sup.supplierName}</option>
                            </c:forEach>
                        </select>
                        <select name="status">
                            <option value="">All Status</option>
                            <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="INACTIVE" ${status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                            <option value="DISCONTINUED" ${status == 'DISCONTINUED' ? 'selected' : ''}>Discontinued</option>
                        </select>
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                </form>
            </div>
            
            <!-- Products Table -->
            <div class="card">
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>SKU</th>
                                <th>Product Name</th>
                                <th>Category</th>
                                <th>Supplier</th>
                                <th>Stock</th>
                                <th>Buying Price</th>
                                <th>Selling Price</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${products.data}" var="product">
                                <tr>
                                    <td>${product.sku}</td>
                                    <td>${product.productName}</td>
                                    <td>${product.categoryName}</td>
                                    <td>${product.supplierName ne null ? product.supplierName : '-'}</td>
                                    <td>
                                        <span class="${product.stockQuantity <= product.minimumStock ? 'text-danger' : ''}">
                                            ${product.stockQuantity}
                                        </span>
                                    </td>
                                    <td><fmt:formatNumber value="${product.buyingPrice}" type="currency"/></td>
                                    <td><fmt:formatNumber value="${product.sellingPrice}" type="currency"/></td>
                                    <td>
                                        <span class="badge ${product.status == 'ACTIVE' ? 'badge-success' : product.status == 'DISCONTINUED' ? 'badge-danger' : 'badge-secondary'}">
                                            ${product.status}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="btn-group">
                                            <a href="${pageContext.request.contextPath}/products/view/${product.productId}" class="btn btn-sm btn-info">View</a>
                                            <a href="${pageContext.request.contextPath}/products/edit/${product.productId}" class="btn btn-sm btn-warning">Edit</a>
                                            <a href="${pageContext.request.contextPath}/products/delete/${product.productId}" class="btn btn-sm btn-danger btn-delete" data-confirm="Are you sure you want to delete this product?">Delete</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${products.data.isEmpty()}">
                                <tr>
                                    <td colspan="9" style="text-align: center; padding: 2rem;">No products found</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                
                <!-- Pagination -->
                <c:if test="${products.totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${products.hasPreviousPage()}">
                            <a href="?page=${products.currentPage - 1}&keyword=${keyword}&category=${selectedCategory}&supplier=${selectedSupplier}&status=${status}">Previous</a>
                        </c:if>
                        
                        <c:forEach begin="1" end="${products.totalPages}" var="i">
                            <c:choose>
                                <c:when test="${i == products.currentPage}">
                                    <span class="active">${i}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="?page=${i}&keyword=${keyword}&category=${selectedCategory}&supplier=${selectedSupplier}&status=${status}">${i}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        
                        <c:if test="${products.hasNextPage()}">
                            <a href="?page=${products.currentPage + 1}&keyword=${keyword}&category=${selectedCategory}&supplier=${selectedSupplier}&status=${status}">Next</a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
