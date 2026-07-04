<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty category ? 'Add' : 'Edit'} Category - SmartInventory</title>
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
                    <h3>${empty category ? 'Add' : 'Edit'} Category</h3>
                </div>
                <div class="top-nav-right">
                    <a href="${pageContext.request.contextPath}/categories/" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                </div>
            </div>
            
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>
            
            <div class="card">
                <form action="${pageContext.request.contextPath}/categories/${empty category ? 'create' : 'update/' + category.categoryId}" method="post" data-validate>
                    <div class="form-group">
                        <label for="categoryName">Category Name <span style="color: red;">*</span></label>
                        <input type="text" id="categoryName" name="categoryName" class="form-control" 
                               value="${category.categoryName}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="3">${category.description}</textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="parentCategoryId">Parent Category</label>
                        <select id="parentCategoryId" name="parentCategoryId" class="form-control">
                            <option value="">None (Root Category)</option>
                            <!-- Parent categories would be loaded dynamically -->
                        </select>
                    </div>
                    
                    <c:if test="${not empty category}">
                        <div class="form-group">
                            <label for="status">Status</label>
                            <select id="status" name="status" class="form-control">
                                <option value="ACTIVE" ${category.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                                <option value="INACTIVE" ${category.status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>
                    </c:if>
                    
                    <div class="btn-group">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> ${empty category ? 'Create' : 'Update'}
                        </button>
                        <a href="${pageContext.request.contextPath}/categories/" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
