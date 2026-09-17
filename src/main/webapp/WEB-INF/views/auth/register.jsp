<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Đăng ký | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css"></head>
<body>
<main class="auth-page">
    <section class="auth-visual"><div><h1>Tham gia EcoMart</h1><p>Tạo tài khoản và xác thực email bằng OTP để bắt đầu trải nghiệm mua sắm.</p></div></section>
    <section class="auth-panel"><div class="auth-card">
        <a class="brand" href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="EcoMart"><span>UTE EcoMart</span></a>
        <h2>Tạo tài khoản</h2><p class="auth-card__lead">Điền đầy đủ thông tin để nhận mã xác nhận qua email.</p>
        <c:if test="${not empty error}"><div class="alert-eco alert-eco--error"><c:out value="${error}"/></div></c:if>
        <form:form cssClass="auth-form" method="post" modelAttribute="registerForm" action="${pageContext.request.contextPath}/register">
            <form:errors path="*" cssClass="alert-eco alert-eco--error" element="div"/>
            <div class="auth-form__grid">
                <div><label for="fullname">Họ và tên</label><form:input id="fullname" path="fullname" autocomplete="name"/><form:errors path="fullname" cssClass="field-error"/></div>
                <div><label for="username">Tên đăng nhập</label><form:input id="username" path="username" autocomplete="username"/><form:errors path="username" cssClass="field-error"/></div>
                <div><label for="email">Email</label><form:input id="email" path="email" type="email" autocomplete="email"/><form:errors path="email" cssClass="field-error"/></div>
                <div><label for="phone">Số điện thoại</label><form:input id="phone" path="phone" autocomplete="tel"/><form:errors path="phone" cssClass="field-error"/></div>
                <div><label for="password">Mật khẩu</label><form:password id="password" path="password" autocomplete="new-password"/><form:errors path="password" cssClass="field-error"/></div>
                <div><label for="confirmPassword">Xác nhận mật khẩu</label><form:password id="confirmPassword" path="confirmPassword" autocomplete="new-password"/><form:errors path="confirmPassword" cssClass="field-error"/></div>
            </div>
            <button class="btn-eco" type="submit">Gửi mã OTP</button>
        </form:form>
        <div class="auth-links"><a href="${pageContext.request.contextPath}/login">← Đã có tài khoản</a></div>
    </div></section>
</main>
</body></html>
