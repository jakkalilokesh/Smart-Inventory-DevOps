<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports & Data Management - SmartInventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .report-section-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .report-card {
            background: #fff;
            padding: 24px;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06);
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: all 0.3s ease;
            border-top: 4px solid #3b82f6;
        }
        .report-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -2px rgba(0,0,0,0.05);
        }
        .report-card.valuation { border-top-color: #10b981; }
        .report-card.transactions { border-top-color: #f59e0b; }
        .report-card.imports { border-top-color: #8b5cf6; }
        .report-card h4 {
            margin: 0 0 12px 0;
            color: #1f2937;
            font-size: 1.2rem;
            font-weight: 600;
        }
        .report-card p {
            margin: 0 0 20px 0;
            color: #6b7280;
            font-size: 0.9rem;
            line-height: 1.5;
        }
        .report-actions {
            display: flex;
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
            background-color: #3b82f6;
            color: white;
        }
        .btn-csv:hover { background-color: #2563eb; }
        .btn-json {
            background-color: #4b5563;
            color: white;
        }
        .btn-json:hover { background-color: #374151; }
        
        .import-box {
            background: #f9fafb;
            border: 2px dashed #d1d5db;
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
            background: #fff;
            border: 1px solid #d1d5db;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 500;
            color: #374151;
            margin-bottom: 10px;
        }
        .import-label:hover {
            background: #f3f4f6;
        }
        .template-links {
            margin-top: 10px;
            font-size: 0.8rem;
        }
        .template-links a {
            color: #3b82f6;
            text-decoration: none;
            margin: 0 5px;
        }
        .template-links a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>SmartInventory</h2>
            </div>
            <ul class="sidebar-nav">
                <li>
                    <a href="${pageContext.request.contextPath}/dashboard">
                        <i class="fas fa-tachometer-alt"></i> Dashboard
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/categories/">
                        <i class="fas fa-folder"></i> Categories
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/products/">
                        <i class="fas fa-box"></i> Products
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/suppliers/">
                        <i class="fas fa-truck"></i> Suppliers
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/inventory/">
                        <i class="fas fa-warehouse"></i> Inventory
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/reports/dashboard" class="active">
                        <i class="fas fa-chart-bar"></i> Reports
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/profile/">
                        <i class="fas fa-user"></i> Profile
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/auth/logout">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </a>
                </li>
            </ul>
        </aside>
        
        <!-- Main Content -->
        <main class="main-content">
            <div class="top-nav">
                <div class="top-nav-left">
                    <h3>Reports & Data Management</h3>
                </div>
                <div class="top-nav-right">
                    <div class="user-info">
                        <div class="user-avatar">${user.firstName.charAt(0)}${user.lastName.charAt(0)}</div>
                        <div>
                            <strong>${user.firstName} ${user.lastName}</strong><br>
                            <small>${user.roleName}</small>
                        </div>
                    </div>
                </div>
            </div>
            
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

            <!-- Export Other Modules -->
            <div class="card">
                <div class="card-header">
                    <h4>Suppliers & Categories Data Export</h4>
                </div>
                <div style="display: flex; gap: 20px; padding: 15px 0;">
                    <div style="flex: 1; background: #f9fafb; padding: 15px; border-radius: 8px;">
                        <h5 style="margin-top: 0;">Suppliers Data</h5>
                        <div class="report-actions">
                            <a href="${pageContext.request.contextPath}/reports/export?module=suppliers&format=csv" class="btn-export btn-csv">
                                <i class="fas fa-file-csv"></i> CSV
                            </a>
                            <a href="${pageContext.request.contextPath}/reports/export?module=suppliers&format=json" class="btn-export btn-json">
                                <i class="fas fa-code"></i> JSON
                            </a>
                        </div>
                    </div>
                    <div style="flex: 1; background: #f9fafb; padding: 15px; border-radius: 8px;">
                        <h5 style="margin-top: 0;">Categories Data</h5>
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
    </div>
    
    <script>
        document.getElementById('file').addEventListener('change', function(e) {
            var fileName = e.target.files[0] ? e.target.files[0].name : "No file selected";
            document.getElementById('file-name-display').textContent = fileName;
        });
    </script>
</body>
</html>
