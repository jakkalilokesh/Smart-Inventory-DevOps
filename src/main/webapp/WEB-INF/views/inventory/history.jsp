<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction History - SmartInventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="dashboard-container">
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>SmartInventory</h2>
            </div>
            <ul class="sidebar-nav">
                <li><a href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/categories/"><i class="fas fa-folder"></i> Categories</a></li>
                <li><a href="${pageContext.request.contextPath}/products/"><i class="fas fa-box"></i> Products</a></li>
                <li><a href="${pageContext.request.contextPath}/suppliers/"><i class="fas fa-truck"></i> Suppliers</a></li>
                <li><a href="${pageContext.request.contextPath}/inventory/" class="active"><i class="fas fa-warehouse"></i> Inventory</a></li>
                <li><a href="${pageContext.request.contextPath}/reports/dashboard"><i class="fas fa-chart-bar"></i> Reports</a></li>
                <li><a href="${pageContext.request.contextPath}/profile/"><i class="fas fa-user"></i> Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </aside>
        
        <main class="main-content">
            <div class="top-nav">
                <div class="top-nav-left">
                    <h3>Transaction History</h3>
                </div>
                <div class="top-nav-right">
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
</html>
