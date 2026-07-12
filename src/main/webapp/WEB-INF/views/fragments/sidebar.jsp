<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="sidebar">
    <div class="sidebar-header">
        <h2><i class="fas fa-warehouse"></i> SmartInventory</h2>
        <button class="sidebar-close" aria-label="Close Sidebar" style="background: none; border: none; color: var(--text-muted); font-size: 1.25rem; cursor: pointer; display: none;"><i class="fas fa-times"></i></button>
    </div>
    <ul class="sidebar-nav">
        <li>
            <a href="${pageContext.request.contextPath}/dashboard" class="${param.activeTab == 'dashboard' ? 'active' : ''}">
                <i class="fas fa-tachometer-alt"></i> Dashboard
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/categories/" class="${param.activeTab == 'categories' ? 'active' : ''}">
                <i class="fas fa-folder"></i> Categories
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/products/" class="${param.activeTab == 'products' ? 'active' : ''}">
                <i class="fas fa-box"></i> Products
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/suppliers/" class="${param.activeTab == 'suppliers' ? 'active' : ''}">
                <i class="fas fa-truck"></i> Suppliers
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/inventory/" class="${param.activeTab == 'inventory' ? 'active' : ''}">
                <i class="fas fa-warehouse"></i> Inventory
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/reports/dashboard" class="${param.activeTab == 'reports' ? 'active' : ''}">
                <i class="fas fa-chart-bar"></i> Reports
            </a>
        </li>
        <c:if test="${sessionScope.user.roleName == 'ADMIN'}">
            <li>
                <a href="${pageContext.request.contextPath}/users/" class="${param.activeTab == 'users' ? 'active' : ''}">
                    <i class="fas fa-users"></i> Users
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/audit-logs/" class="${param.activeTab == 'audit-logs' ? 'active' : ''}">
                    <i class="fas fa-history"></i> Audit Logs
                </a>
            </li>
        </c:if>
        <li>
            <a href="${pageContext.request.contextPath}/profile/" class="${param.activeTab == 'profile' ? 'active' : ''}">
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
