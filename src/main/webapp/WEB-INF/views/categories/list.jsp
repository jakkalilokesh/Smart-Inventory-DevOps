<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Categories - SmartInventory</title>
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
                <li><a href="${pageContext.request.contextPath}/categories/" class="active"><i class="fas fa-folder"></i> Categories</a></li>
                <li><a href="${pageContext.request.contextPath}/products/"><i class="fas fa-box"></i> Products</a></li>
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
                    <h3>Categories</h3>
                </div>
                <div class="top-nav-right">
                    <a href="${pageContext.request.contextPath}/categories/create" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Add Category
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
                <form action="${pageContext.request.contextPath}/categories/" method="get">
                    <div class="search-bar">
                        <input type="text" name="keyword" class="search-input" placeholder="Search categories..." value="${keyword}">
                        <select name="status">
                            <option value="">All Status</option>
                            <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="INACTIVE" ${status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                        </select>
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                </form>
            </div>
            
            <!-- Categories Table -->
            <div class="card">
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Category Name</th>
                                <th>Description</th>
                                <th>Parent Category</th>
                                <th>Status</th>
                                <th>Created At</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${categories.data}" var="category">
                                <tr>
                                    <td>${category.categoryId}</td>
                                    <td>${category.categoryName}</td>
                                    <td>${category.description}</td>
                                    <td>${category.parentCategoryName ne null ? category.parentCategoryName : '-'}</td>
                                    <td>
                                        <span class="badge ${category.status == 'ACTIVE' ? 'badge-success' : 'badge-secondary'}">
                                            ${category.status}
                                        </span>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${category.createdAt}" pattern="MMM dd, yyyy"/>
                                    </td>
                                    <td>
                                        <div class="btn-group">
                                            <a href="${pageContext.request.contextPath}/categories/view/${category.categoryId}" class="btn btn-sm btn-info">View</a>
                                            <a href="${pageContext.request.contextPath}/categories/edit/${category.categoryId}" class="btn btn-sm btn-warning">Edit</a>
                                            <a href="${pageContext.request.contextPath}/categories/delete/${category.categoryId}" class="btn btn-sm btn-danger btn-delete" data-confirm="Are you sure you want to delete this category?">Delete</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${categories.data.isEmpty()}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 2rem;">No categories found</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                
                <!-- Pagination -->
                <c:if test="${categories.totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${categories.hasPreviousPage()}">
                            <a href="?page=${categories.currentPage - 1}&keyword=${keyword}&status=${status}">Previous</a>
                        </c:if>
                        
                        <c:forEach begin="1" end="${categories.totalPages}" var="i">
                            <c:choose>
                                <c:when test="${i == categories.currentPage}">
                                    <span class="active">${i}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="?page=${i}&keyword=${keyword}&status=${status}">${i}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        
                        <c:if test="${categories.hasNextPage()}">
                            <a href="?page=${categories.currentPage + 1}&keyword=${keyword}&status=${status}">Next</a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
