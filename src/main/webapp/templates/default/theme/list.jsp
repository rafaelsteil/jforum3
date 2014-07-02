<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form name="form" action="<jforum:url address='/jforum'/>?module=adminThemes&action=delete" method="post">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="4" height="25"><jforum:i18n key="Groups.List.Title"/></th>
	</tr>

	<c:if test="${errormessage}">
		<tr>
			<td class="row2 nav" colspan="4" align="center">
				<font color="#ff0000"><b>${errorMessage}</b></font>
			</td>
		</tr>
	</c:if>

	<tr>
		<td class='row2 gen' width="38%"><b><jforum:i18n key="Groups.List.groupName"/></b></td>

		<c:if test="${'true' != isExternalUserManagement}">
			<td class='row2 gen' align="center"><b><jforum:i18n key="Action"/></b></td>
			
			<c:if test="${userSession.roleManager.administrator}">
				<td class='row2 gen' align="center" width="10%"><b><jforum:i18n key="Delete"/></b></td>
			</c:if>
		</c:if>
		<td class='row2 gen' align="center"><b><jforum:i18n key="Security"/></b></td>
	</tr>

	<c:forEach items="${groups}" var="group">
		<c:if test="${userSession.roleManager.administrator || userSession.roleManager.isGroupManager$1[group.id]}">
			<tr class="highlight">
				<td class="row1 gen">${group.name}</td>

				<c:if test="${'true' != isExternalUserManagement}">
					<td class="row1 gen" align="center"><a href="<jforum:url address='/adminGroups/edit/${group.id}'/>"><jforum:i18n key="Groups.List.Edit"/></a></td>
					
					<c:if test="${userSession.roleManager.administrator}">
						<td class="row1" align="center"><input type="checkbox" name="groupId" value="${group.id}"/></td>
					</c:if>
				</c:if>				
				<td class="row1 gen" align="center"><a href="<jforum:url address='/adminGroups/permissions/${group.id}'/>"><jforum:i18n key="Permissions"/></a></td>
			</tr>
		</c:if>
	</c:forEach>

	<c:if test="${'true' != isExternalUserManagement && userSession.roleManager.administrator}">
		<tr align="center">
			<td class="catbottom" colspan="4" height="28">
				<input class="mainoption" type="button" value="<jforum:i18n key='Groups.List.ClickToNew'/>" onClick="document.location = '<jforum:url address='/adminGroups/add'/>';"/>
				&nbsp;&nbsp;
				<input class="mainoption" type="submit" value="<jforum:i18n key='Groups.List.ClickToDelete'/>"/>
			</td>
		</tr>
	</c:if>
</table>
</form>
     
