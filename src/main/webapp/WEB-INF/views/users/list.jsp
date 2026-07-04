<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - SmartInventory</title>
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
                    <h3>User Management</h3>
                </div>
                <div class="top-nav-right">
                    <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Add User
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
                <form action="${pageContext.request.contextPath}/users/" method="get">
                    <div class="search-bar">
                        <input type="text" name="keyword" class="search-input" placeholder="Search users..." value="${keyword}">
                        <select name="role">
                            <option value="">All Roles</option>
                            <option value="1" ${role == '1' ? 'selected' : ''}>Admin</option>
                            <option value="2" ${role == '2' ? 'selected' : ''}>Manager</option>
                            <option value="3" ${role == '3' ? 'selected' : ''}>Staff</option>
                        </select>
                        <select name="status">
                            <option value="">All Status</option>
                            <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="INACTIVE" ${status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                        </select>
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                </form>
            </div>
            
            <div class="card">
                <div class="table-container">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Last Login</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${users.data}" var="user">
                                <tr>
                                    <td>${user.userId}</td>
                                    <td>${user.username}</td>
                                    <td>${user.firstName} ${user.lastName}</td>
                                    <td>${user.email}</td>
                                    <td>
                                        <span class="badge badge-info">${user.roleName}</span>
                                    </td>
                                    <td>
                                        <span class="badge ${user.status == 'ACTIVE' ? 'badge-success' : 'badge-secondary'}">
                                            ${user.status}
                                        </span>
                                    </td>
                                    <td>
                                        <c:if test="${user.lastLogin ne null}">
                                            <fmt:formatDate value="${user.lastLogin}" pattern="MMM dd, yyyy"/>
                                        </c:if>
                                        <c:if test="${user.lastLogin eq null}">Never</c:if>
                                    </td>
                                    <td>
                                        <div class="btn-group">
                                            <a href="${pageContext.request.contextPath}/users/view/${user.userId}" class="btn btn-sm btn-info">View</a>
                                            <a href="${pageContext.request.contextPath}/users/edit/${user.userId}" class="btn btn-sm btn-warning">Edit</a>
                                            <a href="${pageContext.request.contextPath}/users/delete/${user.userId}" class="btn btn-sm btn-danger btn-delete" data-confirm="Are you sure you want to delete this user?">Delete</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${users.data.isEmpty()}">
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 2rem;">No users found</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                
                <c:if test="${users.totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${users.hasPreviousPage()}">
                            <a href="?page=${users.currentPage - 1}&keyword=${keyword}&role=${role}&status=${status}">Previous</a>
                        </c:if>
                        <c:forEach begin="1" end="${users.totalPages}" var="i">
                            <c:choose>
                                <c:when test="${i == users.currentPage}">
                                    <span class="active">${i}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="?page=${i}&keyword=${keyword}&role=${role}&status=${status}">${i}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        <c:if test="${users.hasNextPage()}">
                            <a href="?page=${users.currentPage + 1}&keyword=${keyword}&role=${role}&status=${status}">Next</a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
