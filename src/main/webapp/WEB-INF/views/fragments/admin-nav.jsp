<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="admin-sidebar">
    <a class="brand" href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="EcoMart"><span>EcoMart Admin</span></a>
    <div class="admin-sidebar__label">Quản lý cửa hàng</div>
    <nav>
        <a href="${pageContext.request.contextPath}/home">⌂ Trang chủ</a>
        <a class="${activeMenu eq 'category' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/categories">▦ Danh mục</a>
        <a class="${activeMenu eq 'product' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/products">▣ Sản phẩm</a>
        <a class="${activeMenu eq 'user' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">♙ Người dùng</a>
    </nav>
    <div class="admin-sidebar__label">Tài khoản</div>
    <nav><form class="inline-form" method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button type="submit">↪ Đăng xuất</button></form></nav>
</aside>
