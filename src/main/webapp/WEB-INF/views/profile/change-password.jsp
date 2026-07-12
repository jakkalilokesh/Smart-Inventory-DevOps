<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Change Password - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="profile" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="Change Password" />
    </jsp:include>

    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">
        <a href="${pageContext.request.contextPath}/profile/" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Back
        </a>
        </div>
        </div>
        <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger">${requestScope.error}</div>
        </c:if>
        <div class="card">
        <form action="${pageContext.request.contextPath}/profile/change-password" method="post" data-validate>
        <div class="form-group">
        <label for="currentPassword">Current Password <span style="color: red;">*</span></label>
        <input type="password" id="currentPassword" name="currentPassword" class="form-control" required>
        </div>
        <div class="form-group">
        <label for="newPassword">New Password <span style="color: red;">*</span></label>
        <input type="password" id="newPassword" name="newPassword" class="form-control" required minlength="8">
        <small style="color: #6c757d;">Password must be at least 8 characters long</small>
        </div>
        <div class="form-group">
        <label for="confirmPassword">Confirm New Password <span style="color: red;">*</span></label>
        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required minlength="8">
        </div>
        <div class="btn-group">
        <button type="submit" class="btn btn-warning">
        <i class="fas fa-key"></i> Change Password
        </button>
        <a href="${pageContext.request.contextPath}/profile/" class="btn btn-secondary">Cancel</a>
        </div>
        </form>
        </div>
        </main>
        </div>
        <script src="${pageContext.request.contextPath}/js/app.js"></script>
        </body>
        </
    </div>

    
</main>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
