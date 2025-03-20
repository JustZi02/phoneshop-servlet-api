<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Details">
    <p>${cart}</p>
    <c:if test="${not empty message}">
        ${message}
    </c:if>
    <c:if test="${not empty errorMessage}">
        ${errorMessage}
    </c:if>
    <p>
            ${product.description}
    </p>

    <form method="post">
        <table>
            <tr>
                <td>Image</td>
                <td><img src="${product.imageUrl}" alt=""></td>
            </tr>
            <tr>
                <td>Code</td>
                <td>${product.code}</td>
            </tr>
            <tr>
                <td>Price</td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td>Stock</td>
                <td class="price">${product.stock}</td>
            </tr>
            <tr>
                <td>Quantity</td>
                <td>
                    <label class="quantity">
                        <input name="quantity" value="${not empty errorMessage ? param.quantity : 1}">
                    </label>
                    <c:if test="${not empty errorMessage}">
                        <div>
                                ${errorMessage}
                        </div>
                    </c:if>
                </td>
            </tr>

        </table>
        <button>Add to cart</button>
    </form>

</tags:master>