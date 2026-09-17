<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title><c:out value="${product.productName}"/> | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/ecomart.css"></head>
<body class="store-page">
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>
<main class="store-main section"><div class="container-eco"><article class="detail-card">
    <div class="detail-image"><c:choose><c:when test="${not empty product.image}"><img src="${pageContext.request.contextPath}/${product.image}" alt="${product.productName}"></c:when><c:otherwise><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="Chưa có ảnh"></c:otherwise></c:choose></div>
    <div class="detail-info"><span class="eyebrow" style="background:var(--eco-green-soft);color:var(--eco-green)"><c:out value="${product.category.categoryName}"/></span><h1><c:out value="${product.productName}"/></h1><div class="price"><fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/> ₫</div><hr style="border:0;border-top:1px solid var(--eco-border);margin:24px 0"><h3>Mô tả sản phẩm</h3><p class="detail-description"><c:out value="${product.description}"/></p><a class="btn-eco btn-eco--light" href="${pageContext.request.contextPath}/product">← Quay lại danh sách</a></div>
</article></div></main>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
</body></html>
