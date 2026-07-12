<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="${empty supplier ? 'Add' : 'Edit'} Supplier - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="suppliers" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="${empty supplier ? 'Add' : 'Edit'} Supplier" />
    </jsp:include>

    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">
        <a href="${pageContext.request.contextPath}/suppliers/" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Back
        </a>
        </div>
        </div>
        <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger">${requestScope.error}</div>
        </c:if>
        <div class="card">
        <form action="${pageContext.request.contextPath}/suppliers/${empty supplier ? 'create' : 'update/' + supplier.supplierId}" method="post" data-validate>
        <div class="form-group">
        <label for="supplierName">Supplier Name <span style="color: red;">*</span></label>
        <input type="text" id="supplierName" name="supplierName" class="form-control" value="${supplier.supplierName}" required>
        </div>
        <div class="form-group">
        <label for="contactPerson">Contact Person <span style="color: red;">*</span></label>
        <input type="text" id="contactPerson" name="contactPerson" class="form-control" value="${supplier.contactPerson}" required>
        </div>
        <div class="form-row">
        <div class="form-group">
        <label for="email">Email <span style="color: red;">*</span></label>
        <input type="email" id="email" name="email" class="form-control" value="${supplier.email}" required>
        </div>
        <div class="form-group">
        <label for="phone">Phone</label>
        <input type="tel" id="phone" name="phone" class="form-control" value="${supplier.phone}">
        </div>
        </div>
        <div class="form-group">
        <label for="address">Address</label>
        <input type="text" id="address" name="address" class="form-control" value="${supplier.address}">
        </div>
        <div class="form-row">
        <div class="form-group">
        <label for="city">City</label>
        <input type="text" id="city" name="city" class="form-control" value="${supplier.city}">
        </div>
        <div class="form-group">
        <label for="state">State</label>
        <input type="text" id="state" name="state" class="form-control" value="${supplier.state}">
        </div>
        </div>
        <div class="form-row">
        <div class="form-group">
        <label for="country">Country</label>
        <input type="text" id="country" name="country" class="form-control" value="${supplier.country}">
        </div>
        <div class="form-group">
        <label for="postalCode">Postal Code</label>
        <input type="text" id="postalCode" name="postalCode" class="form-control" value="${supplier.postalCode}">
        </div>
        </div>
        <div class="form-group">
        <label for="taxId">Tax ID</label>
        <input type="text" id="taxId" name="taxId" class="form-control" value="${supplier.taxId}">
        </div>
        <c:if test="${not empty supplier}">
        <div class="form-group">
        <label for="status">Status</label>
        <select id="status" name="status" class="form-control">
        <option value="ACTIVE" ${supplier.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
        <option value="INACTIVE" ${supplier.status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
        </select>
        </div>
        </c:if>
        <div class="btn-group">
        <button type="submit" class="btn btn-primary">
        <i class="fas fa-save"></i> ${empty supplier ? 'Create' : 'Update'}
        </button>
        <a href="${pageContext.request.contextPath}/suppliers/" class="btn btn-secondary">Cancel</a>
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
