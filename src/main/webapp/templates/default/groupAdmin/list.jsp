<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="isExternalUserManagement"><jforum:settings key="external.user.management"/></c:set>

<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form accept-charset="${encoding}" name="form" action="<jforum:url address='/adminGroups/delete'/>" method="post">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="middle" colspan="4" height="25"><jforum:i18n key="Groups.List.Title"/></th>
	</tr>

	<tr>
		<td class='row2 gen' width="38%"><b><jforum:i18n key="Groups.List.groupName"/></b></td>

		<c:if test="${'true' != isExternalUserManagement}">
			<td class='row2 gen' align="center"><b><jforum:i18n key="Action"/></b></td>
		</c:if>	
		<c:if test="${userSession.roleManager.administrator}">
			<td class='row2 gen' align="center" width="10%"><b><jforum:i18n key="Delete"/></b></td>
		</c:if>
		
		<td class='row2 gen' align="center"><b><jforum:i18n key="Security"/></b></td>
	</tr>

	<c:forEach items="${groups}" var="group">
		<c:if test="${userSession.roleManager.administrator || userSession.roleManager.isGroupManager$1[group.id]}">
			<tr class="highlight">
				<td class="row1 gen">${group.name}</td>

				<c:if test="${'true' != isExternalUserManagement}">
					<td class="row1 gen" align="center"><a href="<jforum:url address='/adminGroups/edit?groupId=${group.id}'/>"><jforum:i18n key="Groups.List.Edit"/></a></td>
				</c:if>	
				<c:if test="${userSession.roleManager.administrator}">
					<td class="row1" align="center"><input type="checkbox" name="groupId" value="${group.id}"/></td>
				</c:if>
								
				<td class="row1 gen" align="center"><a href="<jforum:url address='/adminGroups/permissions?groupId=${group.id}'/>"><jforum:i18n key="Permissions"/></a></td>
			</tr>
		</c:if>
	</c:forEach>

		<tr align="center">
			<td class="catbottom" colspan="4" height="28">
				<c:if test="${'true' != isExternalUserManagement}">
					<input class="mainoption" type="button" value="<jforum:i18n key='Groups.List.ClickToNew'/>" onClick="document.location = '<jforum:url address='/adminGroups/add'/>';"/>
					&nbsp;&nbsp;
				</c:if>
				<c:if test="${userSession.roleManager.administrator}">
					<input class="mainoption" type="submit" value="<jforum:i18n key='Groups.List.ClickToDelete'/>"/>
				</c:if>
			</td>
		</tr>

</table>
</form>
     
