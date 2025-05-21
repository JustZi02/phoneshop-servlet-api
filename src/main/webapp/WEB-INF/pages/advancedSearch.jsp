<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>

<tags:master pageTitle="Advanced Search">
    <h1>
        Advanced Search
    </h1>
    <form>
        <p>
            Description:
            <label>
                <input name="description">
            </label>
            <select name="searchType">
                <option>All words</option>
                <option>Any word</option>
            </select>
        </p>
        <p>
            Min Price:
            <label>
                <input name="minPrice">
            </label>
        </p>
        <p>
            Max Price:
            <label>
                <input name="maxPrice">
            </label>
        </p>
        <button>Search</button>
    </form>
    <c:if test="${not empty products}">
        <table>
            <tr>
                <td>Image</td>
                <td>Description</td>
                <td>Price</td>
            </tr>
            <c:forEach var="product" items="${products}">
                <tr>
                    <td>
                        <img class="product-tile" src="${product.imageUrl}" alt="">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">${product.description}</a>
                    </td>
                    <td class="price">
                        <fmt:formatNumber value="${product.price}" type="currency"
                                          currencySymbol="${product.currency.symbol}"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

</tags:master>

