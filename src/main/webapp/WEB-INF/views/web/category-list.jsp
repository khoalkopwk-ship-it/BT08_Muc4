<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Danh mục sản phẩm | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/ecomart.css"></head>
<body class="store-page">
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>
<main class="store-main page-section"><div class="container-eco">
    <div class="page-heading"><span class="eyebrow" style="background:var(--eco-green-soft);color:var(--eco-green)">Khám phá EcoMart</span><h1>Danh mục sản phẩm</h1><p>Chọn nhóm sản phẩm phù hợp với nhu cầu của bạn.</p></div>
    <div class="category-grid">
        <c:forEach var="category" items="${categories}"><article class="category-card">
            <c:choose><c:when test="${not empty category.icon}"><img src="${pageContext.request.contextPath}/${category.icon}" alt="${category.categoryName}"></c:when><c:otherwise><div class="category-card__placeholder">EcoMart</div></c:otherwise></c:choose>
            <div><h2><c:out value="${category.categoryName}"/></h2><p>Danh mục đang hoạt động</p></div>
        </article></c:forEach>
        <c:if test="${empty categories}"><div class="empty-state">Hiện chưa có danh mục sản phẩm.</div></c:if>
    </div>
</div></main>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
</body></html>
