<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
            
            <div style="text-align: center; margin-top: 1rem; color: #6c757d; font-size: 0.875rem;">
                <p>Default credentials:</p>
                <p>Admin: admin / admin123</p>
                <p>Manager: manager / manager123</p>
                <p>Staff: staff / staff123</p>
            </div>
        </div>
    </div>
</body>
</html>
