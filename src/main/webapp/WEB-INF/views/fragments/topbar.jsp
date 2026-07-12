<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="top-nav">
    <div class="top-nav-left" style="display: flex; align-items: center; gap: 1rem;">
        <button class="sidebar-toggle" aria-label="Toggle Sidebar" style="background: none; border: none; color: white; font-size: 1.25rem; cursor: pointer; display: none; padding: 0.25rem 0.5rem; border-radius: 4px; transition: background 0.2s;"><i class="fas fa-bars"></i></button>
        <h3>${param.pageTitle}</h3>
    </div>
    <div class="top-nav-right">
        <div class="user-info">
            <div class="user-avatar">${sessionScope.user.firstName.substring(0,1)}${sessionScope.user.lastName.substring(0,1)}</div>
            <div class="user-details">
                <strong>${sessionScope.user.firstName} ${sessionScope.user.lastName}</strong>
                <small>${sessionScope.user.roleName}</small>
            </div>
        </div>
    </div>
</div>
