<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="My Profile - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="profile" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="My Profile" />
    </jsp:include>

    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">
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
        </
    </div>

    
</main>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
