<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="order" required="true" %>

<a href="?field=${field}&order=${order}&query=${param.query}"
style="${field eq param.field and order eq param.order ? 'font-weight: bold' : ''}">${order}</a>