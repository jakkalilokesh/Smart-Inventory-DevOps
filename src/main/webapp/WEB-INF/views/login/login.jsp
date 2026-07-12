<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - SmartInventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <h1>SmartInventory</h1>
                <p>Enterprise Inventory Management System</p>
            </div>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">
                    ${error}
                </div>
            </c:if>
            
            <form class="login-form" action="${pageContext.request.contextPath}/auth/login" method="post">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required autofocus>
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                
                <div class="form-group">
                    <label>
                        <input type="checkbox" name="rememberMe"> Remember me
                    </label>
                </div>
                
                <button type="submit" class="login-btn">Login</button>
            </form>
            
            <div style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; border-top: 1px solid var(--border-color); padding-top: 1.25rem;">
                <p style="color: var(--text-muted); margin-bottom: 0.75rem;">Default credentials:</p>
                <code style="display: block; color: var(--secondary-color); font-size: 0.8rem; margin-bottom: 0.25rem;">admin / admin123 (Admin)</code>
                <code style="display: block; color: var(--secondary-color); font-size: 0.8rem; margin-bottom: 1.25rem;">staff / staff123 (Staff)</code>
                <p style="color: var(--text-muted);">Don't have an account? <a href="${pageContext.request.contextPath}/auth/register" style="color: var(--primary-color); text-decoration: none; font-weight: 600;">Register here</a></p>
            </div>
        </div>
    </div>
</body>
</html>
