<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Inventory - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="inventory" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="Inventory Management" />
    </jsp:include>

    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">
        <a href="${pageContext.request.contextPath}/inventory/stock-in" class="btn btn-success">
        <i class="fas fa-plus"></i> Stock In
        </a>
        <a href="${pageContext.request.contextPath}/inventory/stock-out" class="btn btn-warning">
        <i class="fas fa-minus"></i> Stock Out
        </a>
        <a href="${pageContext.request.contextPath}/inventory/adjust" class="btn btn-info">
        <i class="fas fa-edit"></i> Adjust
        </a>
        <a href="${pageContext.request.contextPath}/inventory/history" class="btn btn-secondary">
        <i class="fas fa-history"></i> History
        </a>
        </div>
        </div>
        <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger">${requestScope.error}</div>
        </c:if>
        <c:if test="${not empty requestScope.success}">
        <div class="alert alert-success">${requestScope.success}</div>
        </c:if>
        <!-- Low Stock Alert -->
        <c:if test="${not empty lowStockProducts and not lowStockProducts.isEmpty()}">
        <div class="card" style="border-left: 4px solid #e74c3c;">
        <div class="card-header">
        <h4 style="color: #e74c3c;"><i class="fas fa-exclamation-triangle"></i> Low Stock Alert</h4>
        </div>
        <div class="table-container">
        <table class="table">
        <thead>
        <tr>
        <th>SKU</th>
        <th>Product Name</th>
        <th>Current Stock</th>
        <th>Minimum Stock</th>
        <th>Needed</th>
        <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${lowStockProducts}" var="product">
        <tr>
        <td>${product.sku}</td>
        <td>${product.productName}</td>
        <td style="color: #e74c3c; font-weight: bold;">${product.stockQuantity}</td>
        <td>${product.minimumStock}</td>
        <td>${product.neededQuantity}</td>
        <td>
        <a href="${pageContext.request.contextPath}/inventory/stock-in" class="btn btn-sm btn-success">Add Stock</a>
        </td>
        </tr>
        </c:forEach>
        </tbody>
        </table>
        </div>
        </div>
        </c:if>
        <!-- Recent Transactions -->
        <div class="card">
        <div class="card-header">
        <h4>Recent Transactions</h4>
        <a href="${pageContext.request.contextPath}/inventory/history" class="btn btn-sm btn-primary">View All</a>
        </div>
        <div class="table-container">
        <table class="table">
        <thead>
        <tr>
        <th>Type</th>
        <th>Product</th>
        <th>Quantity</th>
        <th>Previous</th>
        <th>New</th>
        <th>Reference</th>
        <th>Date</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${transactions}" var="transaction">
        <tr>
        <td>
        <span class="badge ${transaction.transactionType == 'STOCK_IN' ? 'badge-success' : transaction.transactionType == 'STOCK_OUT' ? 'badge-warning' : 'badge-info'}">
        ${transaction.transactionTypeDisplay}
        </span>
        </td>
        <td>${transaction.productName} (${transaction.sku})</td>
        <td>${transaction.quantity}</td>
        <td>${transaction.previousQuantity}</td>
        <td>${transaction.newQuantity}</td>
        <td>${transaction.referenceNumber ne null ? transaction.referenceNumber : '-'}</td>
        <td>
        <fmt:formatDate value="${transaction.transactionDate}" pattern="MMM dd, yyyy HH:mm"/>
        </td>
        </tr>
        </c:forEach>
        <c:if test="${transactions.isEmpty()}">
        <tr>
        <td colspan="7" style="text-align: center; padding: 2rem;">No transactions found</td>
        </tr>
        </c:if>
        </tbody>
        </table>
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
