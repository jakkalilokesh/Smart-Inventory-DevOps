<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Transaction History - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="inventory" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="Transaction History" />
    </jsp:include>

    <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-bottom: 1.25rem;">
        <a href="${pageContext.request.contextPath}/inventory/" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Back
        </a>
        </div>
        </div>
        <div class="card">
        <form action="${pageContext.request.contextPath}/inventory/history" method="get">
        <div class="search-bar">
        <select name="type">
        <option value="">All Types</option>
        <option value="STOCK_IN" ${type == 'STOCK_IN' ? 'selected' : ''}>Stock In</option>
        <option value="STOCK_OUT" ${type == 'STOCK_OUT' ? 'selected' : ''}>Stock Out</option>
        <option value="ADJUSTMENT" ${type == 'ADJUSTMENT' ? 'selected' : ''}>Adjustment</option>
        </select>
        <button type="submit" class="btn btn-primary">Filter</button>
        </div>
        </form>
        </div>
        <div class="card">
        <div class="table-container">
        <table class="table">
        <thead>
        <tr>
        <th>ID</th>
        <th>Type</th>
        <th>Product</th>
        <th>Quantity</th>
        <th>Previous</th>
        <th>New</th>
        <th>Unit Price</th>
        <th>Total Price</th>
        <th>Reference</th>
        <th>Performed By</th>
        <th>Date</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${transactions}" var="transaction">
        <tr>
        <td>${transaction.transactionId}</td>
        <td>
        <span class="badge ${transaction.transactionType == 'STOCK_IN' ? 'badge-success' : transaction.transactionType == 'STOCK_OUT' ? 'badge-warning' : 'badge-info'}">
        ${transaction.transactionTypeDisplay}
        </span>
        </td>
        <td>${transaction.productName} (${transaction.sku})</td>
        <td>${transaction.quantity}</td>
        <td>${transaction.previousQuantity}</td>
        <td>${transaction.newQuantity}</td>
        <td>
        <c:if test="${transaction.unitPrice ne null}">
        <fmt:formatNumber value="${transaction.unitPrice}" type="currency"/>
        </c:if>
        <c:if test="${transaction.unitPrice eq null}">-</c:if>
        </td>
        <td>
        <c:if test="${transaction.totalPrice ne null}">
        <fmt:formatNumber value="${transaction.totalPrice}" type="currency"/>
        </c:if>
        <c:if test="${transaction.totalPrice eq null}">-</c:if>
        </td>
        <td>${transaction.referenceNumber ne null ? transaction.referenceNumber : '-'}</td>
        <td>${transaction.performedByName}</td>
        <td>
        <fmt:formatDate value="${transaction.transactionDate}" pattern="MMM dd, yyyy HH:mm"/>
        </td>
        </tr>
        </c:forEach>
        <c:if test="${transactions.isEmpty()}">
        <tr>
        <td colspan="11" style="text-align: center; padding: 2rem;">No transactions found</td>
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
