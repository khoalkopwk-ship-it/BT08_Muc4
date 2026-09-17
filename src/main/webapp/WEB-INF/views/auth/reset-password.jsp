<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Mật khẩu mới | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css"></head>
<body>
<main class="auth-page">
    <section class="auth-visual"><div><h1>Bảo vệ tài khoản</h1><p>Đặt mật khẩu mới đủ mạnh và không chia sẻ mật khẩu với người khác.</p></div></section>
    <section class="auth-panel"><div class="auth-card">
        <a class="brand" href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="EcoMart"><span>UTE EcoMart</span></a>
        <h2>Đặt mật khẩu mới</h2><p class="auth-card__lead">Mật khẩu cần có từ 6 đến 100 ký tự.</p>
        <c:if test="${not empty error}"><div class="alert-eco alert-eco--error"><c:out value="${error}"/></div></c:if>
        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/reset-password">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <div><label for="password">Mật khẩu mới</label><input id="password" type="password" name="password" minlength="6" maxlength="100" required autocomplete="new-password"></div>
            <div><label for="confirmPassword">Xác nhận mật khẩu</label><input id="confirmPassword" type="password" name="confirmPassword" minlength="6" maxlength="100" required autocomplete="new-password"></div>
            <button class="btn-eco" type="submit">Cập nhật mật khẩu</button>
        </form>
    </div></section>
</main>
</body></html>
