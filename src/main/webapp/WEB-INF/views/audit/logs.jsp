<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Audit Logs - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="audit-logs" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="Audit Logs" />
    </jsp:include>

    <!-- Session Feedback Messages -->
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert-success">${sessionScope.success}</div>
        <c:remove var="success" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger">${sessionScope.error}</div>
        <c:remove var="error" scope="session" />
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger">${requestScope.error}</div>
    </c:if>

    <!-- Filters & Search -->
    <div class="card">
        <form action="${pageContext.request.contextPath}/audit-logs/" method="get">
            <div class="search-bar">
                <input type="text" name="keyword" class="search-input" placeholder="Search by description, user, IP..." value="${keyword}">
                
                <select name="moduleFilter">
                    <option value="">All Modules</option>
                    <option value="AUTHENTICATION" ${moduleFilter == 'AUTHENTICATION' ? 'selected' : ''}>Authentication</option>
                    <option value="PRODUCT" ${moduleFilter == 'PRODUCT' ? 'selected' : ''}>Product</option>
                    <option value="CATEGORY" ${moduleFilter == 'CATEGORY' ? 'selected' : ''}>Category</option>
                    <option value="SUPPLIER" ${moduleFilter == 'SUPPLIER' ? 'selected' : ''}>Supplier</option>
                    <option value="INVENTORY" ${moduleFilter == 'INVENTORY' ? 'selected' : ''}>Inventory</option>
                    <option value="USER" ${moduleFilter == 'USER' ? 'selected' : ''}>User Management</option>
                </select>

                <select name="actionFilter">
                    <option value="">All Actions</option>
                    <option value="LOGIN" ${actionFilter == 'LOGIN' ? 'selected' : ''}>LOGIN</option>
                    <option value="LOGOUT" ${actionFilter == 'LOGOUT' ? 'selected' : ''}>LOGOUT</option>
                    <option value="CREATE" ${actionFilter == 'CREATE' ? 'selected' : ''}>CREATE</option>
                    <option value="UPDATE" ${actionFilter == 'UPDATE' ? 'selected' : ''}>UPDATE</option>
                    <option value="DELETE" ${actionFilter == 'DELETE' ? 'selected' : ''}>DELETE</option>
                    <option value="STOCK_IN" ${actionFilter == 'STOCK_IN' ? 'selected' : ''}>STOCK_IN</option>
                    <option value="STOCK_OUT" ${actionFilter == 'STOCK_OUT' ? 'selected' : ''}>STOCK_OUT</option>
                    <option value="ADJUST" ${actionFilter == 'ADJUST' ? 'selected' : ''}>ADJUST</option>
                </select>

                <button type="submit" class="btn btn-primary">Search</button>
            </div>
        </form>
    </div>

    <!-- Logs Table -->
    <div class="card">
        <div class="table-container">
            <table class="table">
                <thead>
                    <tr>
                        <th style="width: 15%;">Timestamp</th>
                        <th style="width: 12%;">User</th>
                        <th style="width: 12%;">Action</th>
                        <th style="width: 15%;">Module</th>
                        <th style="width: 26%;">Description</th>
                        <th style="width: 10%;">IP Address</th>
                        <th style="width: 10%;">Details</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${logs.data}" var="log">
                        <tr>
                            <td>
                                <fmt:parseDate value="${log.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                <fmt:formatDate value="${parsedDateTime}" pattern="MMM dd, yyyy HH:mm:ss" />
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty log.username}">
                                        <strong>${log.username}</strong>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">SYSTEM</span>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="badge 
                                        ${log.action == 'LOGIN' || log.action == 'LOGOUT' ? 'badge-info' : ''}
                                        ${log.action == 'CREATE' || log.action == 'STOCK_IN' ? 'badge-success' : ''}
                                        ${log.action == 'UPDATE' || log.action == 'ADJUST' ? 'badge-warning' : ''}
                                        ${log.action == 'DELETE' || log.action == 'STOCK_OUT' ? 'badge-danger' : ''}
                                    ">
                                        ${log.action}
                                    </span>
                                </td>
                                <td>
                                    <small style="text-transform: uppercase; font-weight: 600; color: var(--text-muted);">
                                        ${log.module}
                                    </small>
                                </td>
                                <td>${log.description}</td>
                                <td><code>${log.ipAddress ne null ? log.ipAddress : '-'}</code></td>
                                <td>
                                    <c:if test="${not empty log.userAgent}">
                                        <button class="btn btn-sm btn-secondary" onclick="alert('User Agent:\n' + '${log.userAgent}')" title="${log.userAgent}">
                                            <i class="fas fa-laptop-code"></i> Info
                                        </button>
                                    </c:if>
                                </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${logs.data.isEmpty()}">
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 3rem;">No audit logs found.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <c:if test="${logs.totalPages > 1}">
            <div class="pagination">
                <c:if test="${logs.hasPreviousPage()}">
                    <a href="?page=${logs.currentPage - 1}&keyword=${keyword ne null ? keyword : ''}&moduleFilter=${moduleFilter ne null ? moduleFilter : ''}&actionFilter=${actionFilter ne null ? actionFilter : ''}">Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${logs.totalPages}" var="i">
                    <c:choose>
                        <c:when test="${i == logs.currentPage}">
                            <span class="active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="?page=${i}&keyword=${keyword ne null ? keyword : ''}&moduleFilter=${moduleFilter ne null ? moduleFilter : ''}&actionFilter=${actionFilter ne null ? actionFilter : ''}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${logs.hasNextPage()}">
                    <a href="?page=${logs.currentPage + 1}&keyword=${keyword ne null ? keyword : ''}&moduleFilter=${moduleFilter ne null ? moduleFilter : ''}&actionFilter=${actionFilter ne null ? actionFilter : ''}">Next</a>
                </c:if>
            </div>
        </c:if>
    </div>

    <!-- Cleanup Panel -->
    <div class="card" style="border-top: 4px solid var(--danger-color); max-width: 500px; margin-top: 2rem;">
        <div class="card-header">
            <h4 style="color: var(--danger-color);"><i class="fas fa-trash-alt"></i> Clean Up Old Logs</h4>
        </div>
        <form action="${pageContext.request.contextPath}/audit-logs/clear" method="post" onsubmit="return confirm('Are you absolutely sure you want to permanently delete activity logs older than the selected timeframe? This action is irreversible!');">
            <div class="form-group" style="margin-top: 1rem;">
                <label for="daysToKeep">Keep logs for at least:</label>
                <select id="daysToKeep" name="daysToKeep" class="form-control" style="width: 100%;">
                    <option value="30">30 Days</option>
                    <option value="60">60 Days</option>
                    <option value="90">90 Days</option>
                    <option value="180">180 Days</option>
                </select>
            </div>
            <button type="submit" class="btn btn-danger" style="margin-top: 1rem;">
                <i class="fas fa-broom"></i> Run Cleanup
            </button>
        </form>
    </div>
</main>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
