<%@ tag body-content="empty" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>

<%@ attribute name="name" required="true" %>
<%@ attribute name="items" required="true" type="java.util.Collection"%>
<%@ attribute name="selected" required="true" type="java.util.Collection"%>
<%@ attribute name="roleName" required="false" %>

<c:forEach items="${items}" var="item">
	<c:if test="${empty roleName || userSession.roleManager.administrator || userSession.roleManager.roleExists$2[roleName][item.id]}">
		<input type="checkbox" name="${name}" value="${item.id}" id="${name}_${item.id}" <c:if test="${jforum:contains(selected, item.id)}">checked</c:if>/>&nbsp;<label for="${name}_${item.id}">${item.name}</label><br/>
	</c:if>
</c:forEach>