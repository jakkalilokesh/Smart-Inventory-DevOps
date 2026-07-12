<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Reports & Data Management - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="reports" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="Reports & Data Management" />
    </jsp:include>
    
    <style>
        .report-section-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .report-card {
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            backdrop-filter: blur(12px);
            padding: 24px;
            border-radius: 12px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: all 0.3s ease;
            border-top: 4px solid var(--primary-color);
        }
        .report-card:hover {
            transform: translateY(-4px);
            box-shadow: var(--shadow-lg);
        }
        .report-card.valuation { border-top-color: var(--success-color); }
        .report-card.transactions { border-top-color: var(--warning-color); }
        .report-card.imports { border-top-color: #8b5cf6; }
        .report-card h4 {
            margin: 0 0 12px 0;
            color: var(--text-primary);
            font-size: 1.2rem;
            font-weight: 600;
        }
        .report-card p {
            margin: 0 0 20px 0;
            color: var(--text-muted);
            font-size: 0.9rem;
            line-height: 1.5;
        }
        .report-actions {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        .btn-export {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 10px 16px;
            border-radius: 6px;
            font-size: 0.875rem;
            font-weight: 500;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.2s;
            border: none;
        }
        .btn-pdf {
            background-color: #ef4444;
            color: white;
        }
        .btn-pdf:hover { background-color: #dc2626; }
        .btn-excel {
            background-color: #10b981;
            color: white;
        }
        .btn-excel:hover { background-color: #059669; }
        .btn-csv {
            background-color: var(--primary-color);
            color: white;
        }
        .btn-csv:hover { opacity: 0.9; }
        .btn-json {
            background-color: #4b5563;
            color: white;
        }
        .btn-json:hover { background-color: #374151; }
        
        .import-box {
            background: rgba(255, 255, 255, 0.02);
            border: 2px dashed var(--border-color);
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            margin-top: 15px;
        }
        .import-box input[type="file"] {
            display: none;
        }
        .import-label {
            display: inline-block;
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 500;
            color: var(--text-primary);
            margin-bottom: 10px;
        }
        .import-label:hover {
            background: rgba(255, 255, 255, 0.05);
        }
        .template-links {
            margin-top: 10px;
            font-size: 0.8rem;
        }
        .template-links a {
            color: var(--primary-color);
            text-decoration: none;
            margin: 0 5px;
        }
        .template-links a:hover {
            text-decoration: underline;
        }
    </style>
            
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

            <!-- Quick Stats -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon primary">
                        <i class="fas fa-box"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Total Products</h5>
                        <h3>${inventorySummary.totalProducts}</h3>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon danger">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Low Stock Alert</h5>
                        <h3>${lowStockSummary.lowStockProducts}</h3>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon success">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Inventory Value</h5>
                        <h3><fmt:formatNumber value="${inventorySummary.totalInventoryValue}" type="currency"/></h3>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon warning">
                        <i class="fas fa-chart-line"></i>
                    </div>
                    <div class="stat-info">
                        <h5>Potential Profit</h5>
                        <h3><fmt:formatNumber value="${inventorySummary.totalPotentialProfit}" type="currency"/></h3>
                    </div>
                </div>
            </div>

            <!-- Reports Section Grid -->
            <div class="report-section-grid">
                <!-- Products Report Card -->
                <div class="report-card">
                    <div>
                        <h4>Products Report</h4>
                        <p>Generate a complete active products list with details like SKU, barcode, pricing, category, and current stock level.</p>
                    </div>
                    <div class="report-actions">
                        <a href="${pageContext.request.contextPath}/reports/export?module=products&format=pdf" class="btn-export btn-pdf">
                            <i class="fas fa-file-pdf"></i> PDF
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/export?module=products&format=excel" class="btn-export btn-excel">
                            <i class="fas fa-file-excel"></i> Excel
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/export?module=products&format=csv" class="btn-export btn-csv">
                            <i class="fas fa-file-csv"></i> CSV
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/export?module=products&format=json" class="btn-export btn-json">
                            <i class="fas fa-code"></i> JSON
                        </a>
                    </div>
                </div>

                <!-- Inventory Valuation Card -->
                <div class="report-card valuation">
                    <div>
                        <h4>Inventory Valuation</h4>
                        <p>Detailed valuation of current inventory holdings categorized by buying costs, selling values, and potential margins.</p>
                    </div>
                    <div class="report-actions">
                        <a href="${pageContext.request.contextPath}/reports/export?module=valuation&format=pdf" class="btn-export btn-pdf">
                            <i class="fas fa-file-pdf"></i> PDF
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/export?module=valuation&format=excel" class="btn-export btn-excel">
                            <i class="fas fa-file-excel"></i> Excel
                        </a>
                    </div>
                </div>

                <!-- Transaction Log Card -->
                <div class="report-card transactions">
                    <div>
                        <h4>Transaction Logs (Last 30 Days)</h4>
                        <p>Download full history of warehouse transactions including stock-in, stock-out, and stock adjustments with reference details.</p>
                    </div>
                    <div class="report-actions">
                        <a href="${pageContext.request.contextPath}/reports/export?module=transactions&format=pdf" class="btn-export btn-pdf">
                            <i class="fas fa-file-pdf"></i> PDF
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/export?module=transactions&format=excel" class="btn-export btn-excel">
                            <i class="fas fa-file-excel"></i> Excel
                        </a>
                    </div>
                </div>

                <!-- Data Bulk Import Card -->
                <div class="report-card imports">
                    <div>
                        <h4>Bulk Data Import</h4>
                        <p>Upload a CSV/JSON file to bulk import or update Products, Suppliers, or Categories. Make sure column headers match requirements.</p>
                    </div>
                    <form action="${pageContext.request.contextPath}/reports/import" method="POST" enctype="multipart/form-data">
                        <div style="margin-bottom: 12px;">
                            <label for="entityType" style="font-size: 0.85rem; font-weight: 600; color: #4b5563;">Select Entity:</label>
                            <select name="entityType" id="entityType" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #d1d5db; margin-top: 4px;">
                                <option value="PRODUCT">Products</option>
                                <option value="SUPPLIER">Suppliers</option>
                                <option value="CATEGORY">Categories</option>
                            </select>
                        </div>
                        <div class="import-box">
                            <label for="file" class="import-label">Choose CSV/JSON File</label>
                            <input type="file" name="file" id="file" required>
                            <div id="file-name-display" style="font-size: 0.8rem; color: #4b5563;">No file selected</div>
                            
                            <div class="template-links">
                                <span>Get Template:</span>
                                <a href="${pageContext.request.contextPath}/reports/template?entityType=PRODUCT">Products</a> |
                                <a href="${pageContext.request.contextPath}/reports/template?entityType=SUPPLIER">Suppliers</a> |
                                <a href="${pageContext.request.contextPath}/reports/template?entityType=CATEGORY">Categories</a>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 12px;">
                            <i class="fas fa-upload"></i> Process Import
                        </button>
                    </form>
                </div>
            </div>

            <!-- Interactive Charts Section -->
            <h3 style="margin-top: 2rem; margin-bottom: 1rem; color: var(--text-primary);">Visual Analytics</h3>
            <div class="report-section-grid" style="margin-bottom: 2rem;">
                <div class="card">
                    <div class="card-header">
                        <h4>Category Distribution (Products)</h4>
                    </div>
                    <div style="position: relative; height: 300px; width: 100%; display: flex; justify-content: center; align-items: center;">
                        <canvas id="categoryDistChart"></canvas>
                    </div>
                </div>
                <div class="card">
                    <div class="card-header">
                        <h4>Stock Value by Category</h4>
                    </div>
                    <div style="position: relative; height: 300px; width: 100%;">
                        <canvas id="categoryValChart"></canvas>
                    </div>
                </div>
            </div>

            <!-- Export Other Modules -->
            <div class="card">
                <div class="card-header">
                    <h4>Suppliers & Categories Data Export</h4>
                </div>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px; padding: 15px 0;">
                    <div style="background: rgba(255, 255, 255, 0.02); border: 1px solid var(--border-color); padding: 15px; border-radius: 8px;">
                        <h5 style="margin-top: 0; color: var(--text-primary); margin-bottom: 12px;">Suppliers Data</h5>
                        <div class="report-actions">
                            <a href="${pageContext.request.contextPath}/reports/export?module=suppliers&format=csv" class="btn-export btn-csv">
                                <i class="fas fa-file-csv"></i> CSV
                            </a>
                            <a href="${pageContext.request.contextPath}/reports/export?module=suppliers&format=json" class="btn-export btn-json">
                                <i class="fas fa-code"></i> JSON
                            </a>
                        </div>
                    </div>
                    <div style="background: rgba(255, 255, 255, 0.02); border: 1px solid var(--border-color); padding: 15px; border-radius: 8px;">
                        <h5 style="margin-top: 0; color: var(--text-primary); margin-bottom: 12px;">Categories Data</h5>
                        <div class="report-actions">
                            <a href="${pageContext.request.contextPath}/reports/export?module=categories&format=csv" class="btn-export btn-csv">
                                <i class="fas fa-file-csv"></i> CSV
                            </a>
                            <a href="${pageContext.request.contextPath}/reports/export?module=categories&format=json" class="btn-export btn-json">
                                <i class="fas fa-code"></i> JSON
                            </a>
                        </div>
                    </div>
                </div>
            </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // file selection display listener
        const fileInput = document.getElementById('file');
        if (fileInput) {
            fileInput.addEventListener('change', function(e) {
                var fileName = e.target.files[0] ? e.target.files[0].name : "No file selected";
                document.getElementById('file-name-display').textContent = fileName;
            });
        }

        // Category Distribution Chart
        const distCtx = document.getElementById('categoryDistChart').getContext('2d');
        const distLabels = [];
        const distData = [];
        
        <c:forEach items="${inventorySummary.categoryDistribution}" var="entry">
            distLabels.push("${entry.key}");
            distData.push(${entry.value});
        </c:forEach>
        
        new Chart(distCtx, {
            type: 'doughnut',
            data: {
                labels: distLabels,
                datasets: [{
                    data: distData,
                    backgroundColor: [
                        'rgba(99, 102, 241, 0.7)',  // Indigo
                        'rgba(168, 85, 247, 0.7)',  // Purple
                        'rgba(236, 72, 153, 0.7)',  // Pink
                        'rgba(244, 63, 94, 0.7)',   // Rose
                        'rgba(34, 197, 94, 0.7)',   // Green
                        'rgba(59, 130, 246, 0.7)',  // Blue
                        'rgba(234, 179, 8, 0.7)'    // Yellow
                    ],
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    borderWidth: 2
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
                                family: "'Plus Jakarta Sans', sans-serif",
                                size: 11
                            }
                        }
                    }
                }
            }
        });

        // Category Values Chart
        const valCtx = document.getElementById('categoryValChart').getContext('2d');
        const valLabels = [];
        const valData = [];
        
        <c:forEach items="${inventorySummary.categoryValues}" var="entry">
            valLabels.push("${entry.key}");
            valData.push(${entry.value});
        </c:forEach>
        
        new Chart(valCtx, {
            type: 'bar',
            data: {
                labels: valLabels,
                datasets: [{
                    label: 'Inventory Value ($)',
                    data: valData,
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
                            color: '#94a3b8'
                        }
                    },
                    y: {
                        grid: {
                            color: 'rgba(255, 255, 255, 0.05)'
                        },
                        ticks: {
                            color: '#94a3b8'
                        }
                    }
                }
            }
        });
    });
</script>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
