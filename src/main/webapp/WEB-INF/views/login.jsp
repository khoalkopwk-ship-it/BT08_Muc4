<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Đăng nhập | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css"></head>
<body>
<main class="auth-page">
    <section class="auth-visual"><div><h1>Chào mừng trở lại</h1><p>Đăng nhập để tiếp tục trải nghiệm mua sắm xanh cùng UTE EcoMart.</p></div></section>
    <section class="auth-panel"><div class="auth-card">
        <a class="brand" href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="EcoMart"><span>UTE EcoMart</span></a>
        <h2>Đăng nhập</h2><p class="auth-card__lead">Dùng tên đăng nhập hoặc email của bạn.</p>
        <c:if test="${param.error != null}"><div class="alert-eco alert-eco--error">Sai tài khoản hoặc mật khẩu.</div></c:if>
        <c:if test="${param.logout != null}"><div class="alert-eco alert-eco--success">Đăng xuất thành công.</div></c:if>
        <c:if test="${not empty success}"><div class="alert-eco alert-eco--success"><c:out value="${success}"/></div></c:if>
        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/login">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <div><label for="username">Tài khoản hoặc email</label><input id="username" name="username" required autofocus autocomplete="username"></div>
            <div><label for="password">Mật khẩu</label><input id="password" type="password" name="password" required autocomplete="current-password"></div>
            <button class="btn-eco" type="submit">Đăng nhập</button>
        </form>
        <div class="auth-links"><a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a><a href="${pageContext.request.contextPath}/register">Tạo tài khoản</a></div>
    </div></section>
</main>
</body></html>
