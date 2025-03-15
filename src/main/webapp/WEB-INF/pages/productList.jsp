<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>
        Welcome to Expert-Soft training!
    </p>
    <form>
        <label>
            <input name="query" value="${param.query}">
        </label>
        <button>Search</button>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
                <tags:sortLink field="description" order="desc"/>
                <tags:sortLink field="description" order="asc"/>
            </td>
            <td class="price">
                Price
                <tags:sortLink field="price" order="desc"/>
                <tags:sortLink field="price" order="asc"/>
            </td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}" alt="">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">${product.description}</a>
                </td>
                <td class="tooltip">
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                    <span class="tooltiptext">
                        <strong>${product.description}</strong>
                        <br/>
                            <hr>
                                <strong>
                                    Start Date | Price
                                </strong>
                        <br/>
                        <c:forEach var="priceHistory" items="${product.priceHistory}">
                             <c:out value="${priceHistory.date}"/> |
                                <fmt:formatNumber value="${priceHistory.price}" type="currency"
                              currencySymbol="${product.currency.symbol}"/>
                         <br/>
                        </c:forEach>
                    </span>
                </td>
            </tr>
        </c:forEach>
    </table>
</tags:master>

