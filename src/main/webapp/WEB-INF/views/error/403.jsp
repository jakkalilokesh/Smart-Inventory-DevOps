<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - Access Denied</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
    <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center;">
        <div style="background: white; padding: 3rem; border-radius: 10px; text-align: center; max-width: 500px; box-shadow: 0 10px 40px rgba(0,0,0,0.2);">
            <div style="font-size: 6rem; color: #e74c3c; margin-bottom: 1rem;">
                <i class="fas fa-lock"></i>
            </div>
            <h1 style="color: #2c3e50; margin-bottom: 1rem;">403</h1>
            <h2 style="color: #34495e; margin-bottom: 1rem;">Access Denied</h2>
            <p style="color: #7f8c8d; margin-bottom: 2rem;">
                You don't have permission to access this resource. Please contact your administrator if you believe this is an error.
            </p>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                <i class="fas fa-home"></i> Go to Dashboard
            </a>
        </div>
    </div>
</body>
</html>
