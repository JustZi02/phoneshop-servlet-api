<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="order" required="true" type="com.es.phoneshop.model.order.Order" %>
<%@ attribute name="errors" required="true" type="java.util.Map" %>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tr>
<td>${label}<span style="color:red">*</span></td>
<td>
    <c:set var="error" value="${errors[name]}"/>
    <input
            type="${type}"
            name="${name}"
            value="${not empty error ? param[name] : order[name]}"
            <c:if test="${type eq 'tel'}">
                pattern="\+375(29|25|33|44)\d{7}"
                placeholder="+375XXXXXXXXX"
            </c:if>
    />
    <c:if test="${not empty error}">
        <div class="error">
                ${error}
        </div>
    </c:if>
</td>
</tr>