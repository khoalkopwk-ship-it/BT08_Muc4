<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Xác nhận OTP | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css"></head>
<body>
<main class="auth-page">
    <section class="auth-visual"><div><h1>Xác thực email</h1><p>Mã OTP có hiệu lực trong 5 phút. Hoàn tất bước này để kích hoạt tài khoản.</p></div></section>
    <section class="auth-panel"><div class="auth-card">
        <a class="brand" href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="EcoMart"><span>UTE EcoMart</span></a>
        <h2>Nhập mã OTP</h2><p class="auth-card__lead">Kiểm tra hộp thư và nhập 6 chữ số được gửi đến email đăng ký.</p>
        <c:if test="${not empty error}"><div class="alert-eco alert-eco--error"><c:out value="${error}"/></div></c:if>
        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/verify-otp">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <div><label for="otp">Mã OTP</label><input class="otp-input" id="otp" name="otp" inputmode="numeric" autocomplete="one-time-code" pattern="[0-9]{6}" minlength="6" maxlength="6" required autofocus></div>
            <button class="btn-eco" type="submit">Kích hoạt tài khoản</button>
        </form>
        <div class="auth-links"><a href="${pageContext.request.contextPath}/register">← Đăng ký lại</a></div>
    </div></section>
</main>
</body></html>
