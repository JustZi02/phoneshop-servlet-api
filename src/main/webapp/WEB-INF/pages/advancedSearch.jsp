<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Advanced Search">
    <h1>
        Advanced Search
    </h1>

    <c:if test="${not empty minPriceError or not empty maxPriceError}">
        <div class="error">
            <p>We got some errors searching your results.</p>
        </div>
    </c:if>

    <form method="get">
    <p>
            Description:
            <label>
                <input name="description" value="${param.description}">
            </label>
        <select name="searchType">
            <c:forEach var="type" items="${searchTypes}">
                <option value="${type}"
                        <c:if test="${param['searchType'] == type}">
                            selected
                        </c:if>
                >${type}</option>
            </c:forEach>
        </select>



    </p>
        <p>
            Min Price:
            <label>
                <input name="minPrice" value="${param.minPrice}">
            </label>
            <c:if test="${not empty minPriceError}">
        <div class="error">${minPriceError}</div>
        </c:if>
        </p>
        <p>
            Max Price:
            <label>
                <input name="maxPrice" value="${param.maxPrice}">
            </label>
            <c:if test="${not empty maxPriceError}">
        <div class="error">${maxPriceError}</div>
        </c:if>
        </p>
        <button>Search</button>
    </form>
    <c:if test="${not empty products and empty errorMessage}">
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

