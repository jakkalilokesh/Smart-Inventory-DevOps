<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Users - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="users" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="User Management" />
    </jsp:include>

    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">
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
        </
    </div>

    
</main>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
