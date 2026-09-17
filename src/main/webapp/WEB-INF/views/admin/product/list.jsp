<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Quản lý sản phẩm | EcoMart Admin</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css"></head>
<body class="admin-layout">
<c:set var="activeMenu" value="product" scope="request"/><jsp:include page="/WEB-INF/views/fragments/admin-nav.jsp"/>
<div class="admin-main">
    <header class="admin-topbar"><strong>Hệ thống quản trị EcoMart</strong><span><c:out value="${currentUser.fullname}"/></span></header>
    <main class="admin-content">
        <div class="admin-title"><div><h1>Quản lý sản phẩm</h1><p>Tìm kiếm và cập nhật kho sản phẩm của cửa hàng.</p></div><a class="btn-eco" href="${pageContext.request.contextPath}/admin/products/new">+ Thêm sản phẩm</a></div>
        <c:if test="${not empty success}"><div class="alert-eco alert-eco--success"><c:out value="${success}"/></div></c:if><c:if test="${not empty error}"><div class="alert-eco alert-eco--error"><c:out value="${error}"/></div></c:if>
        <div class="panel">
            <form class="admin-search" method="get" action="${pageContext.request.contextPath}/admin/products"><input class="form-control-eco" name="keyword" value="${keyword}" placeholder="Tìm tên sản phẩm..."><button class="btn-eco" type="submit">Tìm kiếm</button><a class="btn-eco btn-eco--light" href="${pageContext.request.contextPath}/admin/products">Làm mới</a></form>
            <table class="eco-table"><thead><tr><th>ID</th><th>Ảnh</th><th>Tên sản phẩm</th><th>Giá</th><th>Danh mục</th><th>Thao tác</th></tr></thead><tbody>
            <c:forEach var="product" items="${result.content}"><tr><td>${product.productId}</td><td><c:if test="${not empty product.image}"><img class="table-image" src="${pageContext.request.contextPath}/${product.image}" alt="Ảnh sản phẩm"></c:if></td><td><strong><c:out value="${product.productName}"/></strong></td><td><span class="price" style="font-size:16px"><fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/> ₫</span></td><td><c:out value="${product.category.categoryName}"/></td><td><div class="actions"><a class="btn-eco btn-eco--light btn-eco--small" href="${pageContext.request.contextPath}/admin/products/${product.productId}/edit">Sửa</a><form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/products/${product.productId}/delete" onsubmit="return confirm('Xóa sản phẩm này?')"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button class="btn-eco btn-eco--danger btn-eco--small" type="submit">Xóa</button></form></div></td></tr></c:forEach>
            <c:if test="${result.totalElements == 0}"><tr><td colspan="6" style="text-align:center;padding:35px">Không có sản phẩm phù hợp.</td></tr></c:if>
            </tbody></table>
            <c:if test="${result.totalPages > 1}"><nav class="pagination-eco"><c:forEach begin="0" end="${result.totalPages - 1}" var="number"><c:url var="pageUrl" value="/admin/products"><c:param name="keyword" value="${keyword}"/><c:param name="page" value="${number}"/></c:url><a class="${number == result.number ? 'active' : ''}" href="${pageUrl}">${number + 1}</a></c:forEach></nav></c:if>
        </div>
    </main>
</div>
</body></html>
