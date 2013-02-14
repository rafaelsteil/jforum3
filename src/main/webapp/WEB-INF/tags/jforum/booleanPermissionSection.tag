<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>

<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" type="java.lang.Boolean"%>
<%@ attribute name="defaultValue" required="false" type="java.lang.Boolean"%>

<input type="checkbox" name="${name}" value="true" <c:if test="${value}">checked="checked"</c:if>/>
