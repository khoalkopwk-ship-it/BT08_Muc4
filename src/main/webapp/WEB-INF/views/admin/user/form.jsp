<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${user.id == null ? 'Thêm' : 'Cập nhật'} người dùng | EcoMart Admin</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css"></head>
<body class="admin-layout">
<c:set var="activeMenu" value="user" scope="request"/><jsp:include page="/WEB-INF/views/fragments/admin-nav.jsp"/>
<div class="admin-main">
    <header class="admin-topbar"><strong>Hệ thống quản trị EcoMart</strong><span>${user.id == null ? 'Người dùng mới' : 'Chỉnh sửa người dùng'}</span></header>
    <main class="admin-content">
        <div class="admin-title"><div><h1>${user.id == null ? 'Thêm người dùng' : 'Cập nhật người dùng'}</h1><p>Quản lý thông tin đăng nhập, vai trò và trạng thái tài khoản.</p></div></div>
        <div class="panel"><form:form cssClass="form-grid" method="post" modelAttribute="user" action="${pageContext.request.contextPath}/admin/users/save">
            <form:hidden path="id"/><form:errors path="*" cssClass="alert-eco alert-eco--error form-group--full" element="div"/>
            <div class="form-group"><label for="username">Tên đăng nhập</label><form:input id="username" path="username" cssClass="form-control-eco"/><form:errors path="username" cssClass="field-error"/></div>
            <div class="form-group"><label for="password">Mật khẩu</label><form:password id="password" path="password" cssClass="form-control-eco"/><form:errors path="password" cssClass="field-error"/><small>Khi cập nhật, để trống để giữ mật khẩu cũ.</small></div>
            <div class="form-group"><label for="fullname">Họ và tên</label><form:input id="fullname" path="fullname" cssClass="form-control-eco"/><form:errors path="fullname" cssClass="field-error"/></div>
            <div class="form-group"><label for="email">Email</label><form:input id="email" path="email" type="email" cssClass="form-control-eco"/><form:errors path="email" cssClass="field-error"/></div>
            <div class="form-group"><label for="phone">Số điện thoại</label><form:input id="phone" path="phone" cssClass="form-control-eco"/><form:errors path="phone" cssClass="field-error"/></div>
            <div class="form-group"><label for="role">Vai trò</label><form:select id="role" path="role" cssClass="form-control-eco"><form:option value="USER">USER</form:option><form:option value="ADMIN">ADMIN</form:option></form:select><form:errors path="role" cssClass="field-error"/></div>
            <div class="form-group form-group--full"><div class="checkbox-group"><form:checkbox id="enabled" path="enabled"/><label for="enabled">Tài khoản đang hoạt động</label></div></div>
            <div class="form-actions"><button class="btn-eco" type="submit">Lưu người dùng</button><a class="btn-eco btn-eco--light" href="${pageContext.request.contextPath}/admin/users">Quay lại</a></div>
        </form:form></div>
    </main>
</div>
</body></html>
