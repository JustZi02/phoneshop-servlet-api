<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:if test="${not empty sessionScope.recentProducts}">
    <h3>Recently viewed products</h3>
    <c:forEach var="product" items="${sessionScope.recentProducts}">
        <table class="product-history">
            <tr>
                <td><img class="product-history" src="${product.imageUrl}" alt=""></td>
            </tr>
            <tr>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">${product.description}</a>
                </td>
            </tr>
            <tr>
                <td>
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
        </table>
    </c:forEach>
</c:if>



