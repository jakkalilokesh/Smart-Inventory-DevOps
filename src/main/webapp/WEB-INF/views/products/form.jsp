<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty product ? 'Add' : 'Edit'} Product - SmartInventory</title>
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
                <li><a href="${pageContext.request.contextPath}/products/" class="active"><i class="fas fa-box"></i> Products</a></li>
                <li><a href="${pageContext.request.contextPath}/suppliers/"><i class="fas fa-truck"></i> Suppliers</a></li>
                <li><a href="${pageContext.request.contextPath}/inventory/"><i class="fas fa-warehouse"></i> Inventory</a></li>
                <li><a href="${pageContext.request.contextPath}/reports/dashboard"><i class="fas fa-chart-bar"></i> Reports</a></li>
                <li><a href="${pageContext.request.contextPath}/profile/"><i class="fas fa-user"></i> Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </aside>
        
        <main class="main-content">
            <div class="top-nav">
                <div class="top-nav-left">
                    <h3>${empty product ? 'Add' : 'Edit'} Product</h3>
                </div>
                <div class="top-nav-right">
                    <a href="${pageContext.request.contextPath}/products/" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                </div>
            </div>
            
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>
            
            <div class="card">
                <form action="${pageContext.request.contextPath}/products/${empty product ? 'create' : 'update/' + product.productId}" method="post" data-validate>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="sku">SKU <span style="color: red;">*</span></label>
                            <input type="text" id="sku" name="sku" class="form-control" value="${product.sku}" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="barcode">Barcode</label>
                            <input type="text" id="barcode" name="barcode" class="form-control" value="${product.barcode}">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="productName">Product Name <span style="color: red;">*</span></label>
                        <input type="text" id="productName" name="productName" class="form-control" value="${product.productName}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="3">${product.description}</textarea>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="categoryId">Category <span style="color: red;">*</span></label>
                            <select id="categoryId" name="categoryId" class="form-control" required>
                                <option value="">Select Category</option>
                                <c:forEach items="${categories}" var="cat">
                                    <option value="${cat.categoryId}" ${product.categoryId == cat.categoryId ? 'selected' : ''}>${cat.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="supplierId">Supplier</label>
                            <select id="supplierId" name="supplierId" class="form-control">
                                <option value="">Select Supplier</option>
                                <c:forEach items="${suppliers}" var="sup">
                                    <option value="${sup.supplierId}" ${product.supplierId == sup.supplierId ? 'selected' : ''}>${sup.supplierName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="buyingPrice">Buying Price <span style="color: red;">*</span></label>
                            <input type="number" id="buyingPrice" name="buyingPrice" class="form-control" step="0.01" min="0" value="${product.buyingPrice}" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="sellingPrice">Selling Price <span style="color: red;">*</span></label>
                            <input type="number" id="sellingPrice" name="sellingPrice" class="form-control" step="0.01" min="0" value="${product.sellingPrice}" required>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="stockQuantity">Stock Quantity <span style="color: red;">*</span></label>
                            <input type="number" id="stockQuantity" name="stockQuantity" class="form-control" min="0" value="${product.stockQuantity}" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="minimumStock">Minimum Stock <span style="color: red;">*</span></label>
                            <input type="number" id="minimumStock" name="minimumStock" class="form-control" min="0" value="${product.minimumStock}" required>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="maximumStock">Maximum Stock</label>
                            <input type="number" id="maximumStock" name="maximumStock" class="form-control" min="0" value="${product.maximumStock}">
                        </div>
                        
                        <div class="form-group">
                            <label for="reorderLevel">Reorder Level <span style="color: red;">*</span></label>
                            <input type="number" id="reorderLevel" name="reorderLevel" class="form-control" min="0" value="${product.reorderLevel}" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="unit">Unit</label>
                        <input type="text" id="unit" name="unit" class="form-control" value="${product.unit}" placeholder="e.g., pcs, kg, liters">
                    </div>
                    
                    <c:if test="${not empty product}">
                        <div class="form-group">
                            <label for="status">Status</label>
                            <select id="status" name="status" class="form-control">
                                <option value="ACTIVE" ${product.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                                <option value="INACTIVE" ${product.status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                                <option value="DISCONTINUED" ${product.status == 'DISCONTINUED' ? 'selected' : ''}>Discontinued</option>
                            </select>
                        </div>
                    </c:if>
                    
                    <div class="btn-group">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> ${empty product ? 'Create' : 'Update'}
                        </button>
                        <a href="${pageContext.request.contextPath}/products/" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
