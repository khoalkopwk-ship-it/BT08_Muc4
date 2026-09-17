<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${category.categoryId == null ? 'Thêm' : 'Cập nhật'} danh mục | EcoMart Admin</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css"></head>
<body class="admin-layout">
<c:set var="activeMenu" value="category" scope="request"/><jsp:include page="/WEB-INF/views/fragments/admin-nav.jsp"/>
<div class="admin-main">
    <header class="admin-topbar"><strong>Hệ thống quản trị EcoMart</strong><span>${category.categoryId == null ? 'Danh mục mới' : 'Chỉnh sửa danh mục'}</span></header>
    <main class="admin-content">
        <div class="admin-title"><div><h1>${category.categoryId == null ? 'Thêm danh mục' : 'Cập nhật danh mục'}</h1><p>${category.categoryId == null ? 'Tạo một nhóm sản phẩm mới cho cửa hàng.' : 'Điều chỉnh tên, trạng thái hoặc icon danh mục.'}</p></div></div>
        <div class="panel">
            <form:form cssClass="form-grid" method="post" modelAttribute="category" enctype="multipart/form-data" action="${pageContext.request.contextPath}/admin/categories/save">
                <form:hidden path="categoryId"/>
                <form:errors path="*" cssClass="alert-eco alert-eco--error form-group--full" element="div"/>
                <div class="form-group"><label for="categoryName">Tên danh mục</label><form:input id="categoryName" path="categoryName" cssClass="form-control-eco"/><form:errors path="categoryName" cssClass="field-error"/></div>
                <div class="form-group"><label for="status">Trạng thái</label><form:select id="status" path="status" cssClass="form-control-eco"><form:option value="1">Hoạt động</form:option><form:option value="0">Tạm ẩn</form:option></form:select></div>
                <div class="form-group form-group--full"><label for="iconFile">Icon danh mục</label><input id="iconFile" type="file" name="iconFile" class="form-control-eco" accept=".jpg,.jpeg,.png,.gif,.webp"><c:if test="${not empty category.icon}"><img class="preview-image" src="${pageContext.request.contextPath}/${category.icon}" alt="Icon hiện tại"></c:if></div>
                <div class="form-actions"><button class="btn-eco" type="submit">Lưu danh mục</button><a class="btn-eco btn-eco--light" href="${pageContext.request.contextPath}/admin/categories">Quay lại</a></div>
            </form:form>
        </div>
    </main>
</div>
</body></html>
