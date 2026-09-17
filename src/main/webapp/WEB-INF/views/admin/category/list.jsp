<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Quản lý danh mục | EcoMart Admin</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css"></head>
<body class="admin-layout">
<c:set var="activeMenu" value="category" scope="request"/><jsp:include page="/WEB-INF/views/fragments/admin-nav.jsp"/>
<div class="admin-main">
    <header class="admin-topbar"><strong>Hệ thống quản trị EcoMart</strong><span><c:out value="${currentUser.fullname}"/></span></header>
    <main class="admin-content">
        <div class="admin-title"><div><h1>Quản lý danh mục</h1><p>Tìm kiếm, thêm, sửa, xóa và cập nhật icon.</p></div><a class="btn-eco" href="${pageContext.request.contextPath}/admin/categories/new">+ Thêm danh mục</a></div>
        <c:if test="${not empty success}"><div class="alert-eco alert-eco--success"><c:out value="${success}"/></div></c:if><c:if test="${not empty error}"><div class="alert-eco alert-eco--error"><c:out value="${error}"/></div></c:if>
        <div class="panel">
            <form class="admin-search" method="get" action="${pageContext.request.contextPath}/admin/categories"><input class="form-control-eco" name="keyword" value="${keyword}" placeholder="Tìm tên danh mục..."><button class="btn-eco" type="submit">Tìm kiếm</button><a class="btn-eco btn-eco--light" href="${pageContext.request.contextPath}/admin/categories">Làm mới</a></form>
            <table class="eco-table"><thead><tr><th>ID</th><th>Icon</th><th>Tên danh mục</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
            <c:forEach var="category" items="${result.content}"><tr><td>${category.categoryId}</td><td><c:if test="${not empty category.icon}"><img class="table-image" src="${pageContext.request.contextPath}/${category.icon}" alt="Icon"></c:if></td><td><strong><c:out value="${category.categoryName}"/></strong></td><td><span class="status ${category.status == 1 ? 'status--active' : 'status--inactive'}">${category.status == 1 ? 'Hoạt động' : 'Tạm ẩn'}</span></td><td><div class="actions"><a class="btn-eco btn-eco--light btn-eco--small" href="${pageContext.request.contextPath}/admin/categories/${category.categoryId}/edit">Sửa</a><form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/categories/${category.categoryId}/delete" onsubmit="return confirm('Xóa danh mục này?')"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button class="btn-eco btn-eco--danger btn-eco--small" type="submit">Xóa</button></form></div></td></tr></c:forEach>
            <c:if test="${result.totalElements == 0}"><tr><td colspan="5" style="text-align:center;padding:35px">Không có danh mục phù hợp.</td></tr></c:if>
            </tbody></table>
            <c:if test="${result.totalPages > 1}"><nav class="pagination-eco"><c:forEach begin="0" end="${result.totalPages - 1}" var="number"><c:url var="pageUrl" value="/admin/categories"><c:param name="keyword" value="${keyword}"/><c:param name="page" value="${number}"/></c:url><a class="${number == result.number ? 'active' : ''}" href="${pageUrl}">${number + 1}</a></c:forEach></nav></c:if>
        </div>
    </main>
</div>
</body></html>
