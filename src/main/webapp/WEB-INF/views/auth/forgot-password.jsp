<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Quên mật khẩu | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css"></head>
<body>
<main class="auth-page">
    <section class="auth-visual"><div><h1>Khôi phục tài khoản</h1><p>UTE EcoMart sẽ gửi mã OTP đến email đã đăng ký để xác minh yêu cầu đổi mật khẩu.</p></div></section>
    <section class="auth-panel"><div class="auth-card">
        <a class="brand" href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="EcoMart"><span>UTE EcoMart</span></a>
        <h2>Quên mật khẩu</h2><p class="auth-card__lead">Nhập email đã dùng khi đăng ký.</p>
        <c:if test="${not empty error}"><div class="alert-eco alert-eco--error"><c:out value="${error}"/></div></c:if>
        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/forgot-password">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <div><label for="email">Email tài khoản</label><input id="email" type="email" name="email" value="${email}" required autofocus autocomplete="email"></div>
            <button class="btn-eco" type="submit">Gửi OTP đặt lại mật khẩu</button>
        </form>
        <div class="auth-links"><a href="${pageContext.request.contextPath}/login">← Quay lại đăng nhập</a></div>
    </div></section>
</main>
</body></html>
