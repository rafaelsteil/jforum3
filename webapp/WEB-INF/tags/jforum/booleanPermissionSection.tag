<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>

<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" type="java.lang.Boolean"%>
<%@ attribute name="defaultValue" required="false" type="java.lang.Boolean"%>

<select name="${name}">
	<option value="false" <c:if test="${!value}">selected</c:if>><jforum:i18n key="No"/></option>
	<option value="true" <c:if test="${value}">selected</c:if>><jforum:i18n key="Yes"/></option>
</select>