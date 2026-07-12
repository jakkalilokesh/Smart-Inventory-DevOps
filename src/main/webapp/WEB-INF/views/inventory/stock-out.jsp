<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Stock Out - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="inventory" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="Stock Out" />
    </jsp:include>

    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">
        <a href="${pageContext.request.contextPath}/inventory/" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Back
        </a>
        </div>
        </div>
        <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger">${requestScope.error}</div>
        </c:if>
        <div class="card">
        <form action="${pageContext.request.contextPath}/inventory/stock-out" method="post" data-validate>
        <div class="form-group">
        <label for="productId">Product <span style="color: red;">*</span></label>
        <select id="productId" name="productId" class="form-control" required>
        <option value="">Select Product</option>
        <c:forEach items="${products}" var="product">
        <option value="${product.productId}">${product.sku} - ${product.productName} (Stock: ${product.stockQuantity})</option>
        </c:forEach>
        </select>
        </div>
        <div class="form-row">
        <div class="form-group">
        <label for="quantity">Quantity <span style="color: red;">*</span></label>
        <input type="number" id="quantity" name="quantity" class="form-control" min="1" required>
        </div>
        <div class="form-group">
        <label for="unitPrice">Unit Price</label>
        <input type="number" id="unitPrice" name="unitPrice" class="form-control" step="0.01" min="0">
        </div>
        </div>
        <div class="form-group">
        <label for="referenceNumber">Reference Number</label>
        <input type="text" id="referenceNumber" name="referenceNumber" class="form-control">
        </div>
        <div class="form-group">
        <label for="notes">Notes</label>
        <textarea id="notes" name="notes" class="form-control" rows="3"></textarea>
        </div>
        <div class="btn-group">
        <button type="submit" class="btn btn-warning">
        <i class="fas fa-minus"></i> Remove Stock
        </button>
        <a href="${pageContext.request.contextPath}/inventory/" class="btn btn-secondary">Cancel</a>
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
