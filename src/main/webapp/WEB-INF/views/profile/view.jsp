<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - SmartInventory</title>
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
                <li><a href="${pageContext.request.contextPath}/profile/" class="active"><i class="fas fa-user"></i> Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </aside>
        
        <main class="main-content">
            <div class="top-nav">
                <div class="top-nav-left">
                    <h3>My Profile</h3>
                </div>
                <div class="top-nav-right">
                    <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-primary">
                        <i class="fas fa-edit"></i> Edit Profile
                    </a>
                    <a href="${pageContext.request.contextPath}/profile/change-password" class="btn btn-warning">
                        <i class="fas fa-key"></i> Change Password
                    </a>
                </div>
            </div>
            
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>
            
            <c:if test="${not empty requestScope.success}">
                <div class="alert alert-success">${requestScope.success}</div>
            </c:if>
            
            <div class="card">
                <div class="card-header">
                    <h4>Profile Information</h4>
                </div>
                <div style="display: flex; gap: 2rem; align-items: flex-start;">
                    <div style="flex-shrink: 0;">
                        <div class="user-avatar" style="width: 100px; height: 100px; font-size: 2rem;">
                            ${user.firstName.charAt(0)}${user.lastName.charAt(0)}
                        </div>
                    </div>
                    <div style="flex: 1;">
                        <table class="table">
                            <tr>
                                <th style="width: 150px;">Username:</th>
                                <td>${user.username}</td>
                            </tr>
                            <tr>
                                <th>Full Name:</th>
                                <td>${user.firstName} ${user.lastName}</td>
                            </tr>
                            <tr>
                                <th>Email:</th>
                                <td>${user.email}</td>
                            </tr>
                            <tr>
                                <th>Phone:</th>
                                <td>${user.phone ne null ? user.phone : '-'}</td>
                            </tr>
                            <tr>
                                <th>Role:</th>
                                <td>
                                    <span class="badge badge-info">${user.roleName}</span>
                                </td>
                            </tr>
                            <tr>
                                <th>Status:</th>
                                <td>
                                    <span class="badge ${user.status == 'ACTIVE' ? 'badge-success' : 'badge-secondary'}">
                                        ${user.status}
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <th>Last Login:</th>
                                <td>
                                    <c:if test="${user.lastLogin ne null}">
                                        <fmt:formatDate value="${user.lastLogin}" pattern="MMM dd, yyyy HH:mm"/>
                                    </c:if>
                                    <c:if test="${user.lastLogin eq null}">
                                        Never
                                    </c:if>
                                </td>
                            </tr>
                            <tr>
                                <th>Member Since:</th>
                                <td>
                                    <fmt:formatDate value="${user.createdAt}" pattern="MMM dd, yyyy"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
