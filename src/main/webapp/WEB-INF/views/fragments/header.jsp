<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="store-header">
    <div class="container-eco store-header__row">
        <a class="brand" href="${pageContext.request.contextPath}/home">
            <img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="UTE EcoMart">
            <span>UTE EcoMart</span>
        </a>
        <nav class="main-nav" aria-label="Điều hướng chính">
            <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
            <a href="${pageContext.request.contextPath}/product">Sản phẩm</a>
            <a href="${pageContext.request.contextPath}/category">Danh mục</a>
            <c:if test="${currentUser != null && currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/categories">Quản trị</a>
            </c:if>
            <c:choose>
                <c:when test="${currentUser != null}">
                    <a class="nav-user" href="${pageContext.request.contextPath}/profile">Xin chào, <c:out value="${currentUser.fullname}"/></a>
                    <form class="nav-logout" method="post" action="${pageContext.request.contextPath}/logout">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <button type="submit">Đăng xuất</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                    <a class="btn-eco btn-eco--small" href="${pageContext.request.contextPath}/register">Đăng ký</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>
