<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty user ? 'Add' : 'Edit'} User - SmartInventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="dashboard-container">
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>SmartInventory</h2>
            </div>
            <ul class="sidebar-nav">
                <li><a href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/categories/"><i class="fas fa-folder"></i> Categories</a></li>
                <li><a href="${pageContext.request.contextPath}/products/"><i class="fas fa-box"></i> Products</a></li>
                <li><a href="${pageContext.request.contextPath}/suppliers/"><i class="fas fa-truck"></i> Suppliers</a></li>
                <li><a href="${pageContext.request.contextPath}/inventory/"><i class="fas fa-warehouse"></i> Inventory</a></li>
                <li><a href="${pageContext.request.contextPath}/reports/dashboard"><i class="fas fa-chart-bar"></i> Reports</a></li>
                <li><a href="${pageContext.request.contextPath}/users/" class="active"><i class="fas fa-users"></i> Users</a></li>
                <li><a href="${pageContext.request.contextPath}/profile/"><i class="fas fa-user"></i> Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </aside>
        
        <main class="main-content">
            <div class="top-nav">
                <div class="top-nav-left">
                    <h3>${empty user ? 'Add' : 'Edit'} User</h3>
                </div>
                <div class="top-nav-right">
                    <a href="${pageContext.request.contextPath}/users/" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                </div>
            </div>
            
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>
            
            <div class="card">
                <form action="${pageContext.request.contextPath}/users/${empty user ? 'create' : 'update/' + user.userId}" method="post" data-validate>
                    <div class="form-group">
                        <label for="username">Username <span style="color: red;">*</span></label>
                        <input type="text" id="username" name="username" class="form-control" 
                               value="${user.username}" ${not empty user ? 'readonly' : ''} required>
                        <c:if test="${not empty user}">
                            <small style="color: #6c757d;">Username cannot be changed</small>
                        </c:if>
                    </div>
                    
                    <c:if test="${empty user}">
                        <div class="form-group">
                            <label for="password">Password <span style="color: red;">*</span></label>
                            <input type="password" id="password" name="password" class="form-control" required minlength="8">
                            <small style="color: #6c757d;">Minimum 8 characters, must include uppercase, lowercase, and number</small>
                        </div>
                    </c:if>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="firstName">First Name <span style="color: red;">*</span></label>
                            <input type="text" id="firstName" name="firstName" class="form-control" value="${user.firstName}" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="lastName">Last Name <span style="color: red;">*</span></label>
                            <input type="text" id="lastName" name="lastName" class="form-control" value="${user.lastName}" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email <span style="color: red;">*</span></label>
                        <input type="email" id="email" name="email" class="form-control" value="${user.email}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="phone">Phone</label>
                        <input type="tel" id="phone" name="phone" class="form-control" value="${user.phone}">
                    </div>
                    
                    <div class="form-group">
                        <label for="roleId">Role <span style="color: red;">*</span></label>
                        <select id="roleId" name="roleId" class="form-control" required>
                            <option value="">Select Role</option>
                            <option value="1" ${user.roleId == 1 ? 'selected' : ''}>Admin</option>
                            <option value="2" ${user.roleId == 2 ? 'selected' : ''}>Manager</option>
                            <option value="3" ${user.roleId == 3 ? 'selected' : ''}>Staff</option>
                        </select>
                    </div>
                    
                    <c:if test="${not empty user}">
                        <div class="form-group">
                            <label for="status">Status</label>
                            <select id="status" name="status" class="form-control">
                                <option value="ACTIVE" ${user.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                                <option value="INACTIVE" ${user.status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>
                    </c:if>
                    
                    <div class="btn-group">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> ${empty user ? 'Create' : 'Update'}
                        </button>
                        <a href="${pageContext.request.contextPath}/users/" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
