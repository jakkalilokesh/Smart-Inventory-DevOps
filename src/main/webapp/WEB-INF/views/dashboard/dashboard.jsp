<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Dashboard - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="dashboard" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="Dashboard" />
    </jsp:include>
            
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">
                    ${requestScope.error}
                </div>
            </c:if>
            
            <c:if test="${not empty requestScope.success}">
                <div class="alert alert-success">
                    ${requestScope.success}
                </div>
            </c:if>
            
            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon primary">
                        <i class="fas fa-box"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Total Products</h5>
                        <h3>${dashboard.totalProducts}</h3>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon success">
                        <i class="fas fa-folder"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Total Categories</h5>
                        <h3>${dashboard.totalCategories}</h3>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon warning">
                        <i class="fas fa-truck"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Total Suppliers</h5>
                        <h3>${dashboard.totalSuppliers}</h3>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon danger">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Low Stock</h5>
                        <h3>${dashboard.lowStockProducts}</h3>
                    </div>
                </div>
            </div>
            
            <!-- Inventory Value Card -->
            <div class="card">
                <div class="card-header">
                    <h4>Inventory Value</h4>
                </div>
                <div class="stats-grid" style="margin-bottom: 0;">
                    <div class="stat-card">
                        <div class="stat-icon primary">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                        <div class="stat-info">
                            <h5>Total Value</h5>
                            <h3><fmt:formatNumber value="${dashboard.totalInventoryValue}" type="currency"/></h3>
                        </div>
                    </div>
                    
                    <div class="stat-card">
                        <div class="stat-icon success">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <div class="stat-info">
                            <h5>Potential Profit</h5>
                            <h3><fmt:formatNumber value="${dashboard.totalPotentialProfit}" type="currency"/></h3>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Charts Section -->
            <div class="stats-grid" style="grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); margin-bottom: 1.5rem;">
                <div class="card" style="margin-bottom: 0;">
                    <div class="card-header">
                        <h4>Stock Status Overview</h4>
                    </div>
                    <div style="position: relative; height: 260px; display: flex; align-items: center; justify-content: center; padding: 1rem;">
                        <canvas id="stockStatusChart"></canvas>
                    </div>
                </div>
                <div class="card" style="margin-bottom: 0;">
                    <div class="card-header">
                        <h4>System Entities Summary</h4>
                    </div>
                    <div style="position: relative; height: 260px; display: flex; align-items: center; justify-content: center; padding: 1rem;">
                        <canvas id="entitiesChart"></canvas>
                    </div>
                </div>
            </div>
            
            <!-- Low Stock Products -->
            <c:if test="${not empty dashboard.lowStockList and not dashboard.lowStockList.isEmpty()}">
                <div class="card">
                    <div class="card-header">
                        <h4>Low Stock Products</h4>
                        <a href="${pageContext.request.contextPath}/products/?status=ACTIVE" class="btn btn-sm btn-primary">View All</a>
                    </div>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>SKU</th>
                                    <th>Product Name</th>
                                    <th>Stock</th>
                                    <th>Min Stock</th>
                                    <th>Needed</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${dashboard.lowStockList}" var="product">
                                    <tr>
                                        <td>${product.sku}</td>
                                        <td>${product.productName}</td>
                                        <td>${product.stockQuantity}</td>
                                        <td>${product.minimumStock}</td>
                                        <td>${product.neededQuantity}</td>
                                        <td>
                                            <span class="badge badge-danger">Low Stock</span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>
            
            <!-- Recent Activities -->
            <c:if test="${not empty dashboard.recentActivities and not dashboard.recentActivities.isEmpty()}">
                <div class="card">
                    <div class="card-header">
                        <h4>Recent Activities</h4>
                    </div>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>User</th>
                                    <th>Action</th>
                                    <th>Module</th>
                                    <th>Description</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${dashboard.recentActivities}" var="activity">
                                    <tr>
                                        <td>${activity.username}</td>
                                        <td>${activity.action}</td>
                                        <td>${activity.module}</td>
                                        <td>${activity.description}</td>
                                        <td>
                                            <fmt:parseDate value="${activity.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                            <fmt:formatDate value="${parsedDateTime}" pattern="MMM dd, yyyy HH:mm" />
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            // Stock Status Chart (Doughnut)
            const stockCtx = document.getElementById('stockStatusChart').getContext('2d');
            const totalProducts = ${dashboard.totalProducts};
            const lowStock = ${dashboard.lowStockProducts};
            const healthyStock = Math.max(0, totalProducts - lowStock);
    
            new Chart(stockCtx, {
                type: 'doughnut',
                data: {
                    labels: ['Healthy Stock', 'Low Stock'],
                    datasets: [{
                        data: [healthyStock, lowStock],
                        backgroundColor: [
                            'rgba(16, 185, 129, 0.6)',
                            'rgba(244, 63, 94, 0.6)'
                        ],
                        borderColor: [
                            'rgba(16, 185, 129, 1)',
                            'rgba(244, 63, 94, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom',
                            labels: {
                                color: '#94a3b8',
                                font: {
                                    family: 'Plus Jakarta Sans',
                                    size: 12
                                }
                            }
                        }
                    },
                    cutout: '70%'
                }
            });
    
            // Entities Summary Chart (Bar)
            const entitiesCtx = document.getElementById('entitiesChart').getContext('2d');
            new Chart(entitiesCtx, {
                type: 'bar',
                data: {
                    labels: ['Products', 'Categories', 'Suppliers'],
                    datasets: [{
                        label: 'Total Registered',
                        data: [${dashboard.totalProducts}, ${dashboard.totalCategories}, ${dashboard.totalSuppliers}],
                        backgroundColor: 'rgba(99, 102, 241, 0.6)',
                        borderColor: 'rgba(99, 102, 241, 1)',
                        borderWidth: 1,
                        borderRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        x: {
                            grid: {
                                color: 'rgba(255, 255, 255, 0.05)'
                            },
                            ticks: {
                                color: '#94a3b8',
                                font: {
                                    family: 'Plus Jakarta Sans'
                                }
                            }
                        },
                        y: {
                            grid: {
                                color: 'rgba(255, 255, 255, 0.05)'
                            },
                            ticks: {
                                color: '#94a3b8',
                                font: {
                                    family: 'Plus Jakarta Sans'
                                },
                                stepSize: 1
                            }
                        }
                    }
                }
            });
        });
    </script>
</main>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
