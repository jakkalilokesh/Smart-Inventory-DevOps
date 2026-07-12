<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Edit Profile - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="profile" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="Edit Profile" />
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
        <form action="${pageContext.request.contextPath}/profile/update" method="post" data-validate>
        <div class="form-group">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" class="form-control"
        value="${user.username}" readonly style="background-color: #f8f9fa;">
        <small style="color: #6c757d;">Username cannot be changed</small>
        </div>
        <div class="form-row">
        <div class="form-group">
        <label for="firstName">First Name <span style="color: red;">*</span></label>
        <input type="text" id="firstName" name="firstName" class="form-control"
        value="${user.firstName}" required>
        </div>
        <div class="form-group">
        <label for="lastName">Last Name <span style="color: red;">*</span></label>
        <input type="text" id="lastName" name="lastName" class="form-control"
        value="${user.lastName}" required>
        </div>
        </div>
        <div class="form-group">
        <label for="email">Email <span style="color: red;">*</span></label>
        <input type="email" id="email" name="email" class="form-control"
        value="${user.email}" required>
        </div>
        <div class="form-group">
        <label for="phone">Phone</label>
        <input type="tel" id="phone" name="phone" class="form-control"
        value="${user.phone}">
        </div>
        <div class="btn-group">
        <button type="submit" class="btn btn-primary">
        <i class="fas fa-save"></i> Save Changes
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
