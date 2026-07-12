<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="${empty product ? 'Add' : 'Edit'} Product - SmartInventory" />
</jsp:include>

<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
    <jsp:param name="activeTab" value="products" />
</jsp:include>

<main class="main-content">
    <jsp:include page="/WEB-INF/views/fragments/topbar.jsp">
        <jsp:param name="pageTitle" value="${empty product ? 'Add' : 'Edit'} Product" />
    </jsp:include>
            
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>
            
            <div style="display: flex; justify-content: flex-start; margin-bottom: 1.25rem;">
                <a href="${pageContext.request.contextPath}/products/" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Back to Products
                </a>
            </div>
            
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
<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
