<%@ tag body-content="empty" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>

<%@ attribute name="name" required="true" %>
<%@ attribute name="categories" required="true" type="java.util.Collection"%>
<%@ attribute name="selected" required="true" type="java.util.Collection"%>

<c:forEach items="${categories}" var="category">
	<c:if test="${userSession.roleManager.administrator || userSession.roleManager.isCategoryAllowed$1[category.id]}">
		<b>${category.name}</b><br/>
		<jforum:choicePermissionSection roleName="forum" items="${category.forums}" name="${name}" selected="${selected}"/>
		<br/>
	</c:if>
</c:forEach>
