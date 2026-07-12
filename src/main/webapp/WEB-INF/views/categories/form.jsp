<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="${empty category ? 'Add' : 'Edit'} Category - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="categories" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="${empty category ? 'Add' : 'Edit'} Category" />
    </jsp:include>

    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">
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
        </
    </div>

    
</main>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
