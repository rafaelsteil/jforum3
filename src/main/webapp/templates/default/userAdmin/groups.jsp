<%@ taglib prefix="jforum" uri="http://www.jforum.net/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form accept-charset="${encoding}" action="<jforum:url address="/adminUsers/groupsSave"/>" method="post">
	<input type="hidden" name="userId" value="${user.id}"/>
	
	<table class="forumline" cellspacing="1" cellpadding="3" width="100%">
		<tr>
			<th class="thhead" colspan="2" height="25"><jforum:i18n key="User.GroupsFor" username="${user.username}"/></th>
		</tr>
	        
		<tr>
			<td class="row1 gen" width="38%"><jforum:i18n key='Groups.Form.GroupName'/></td>
			<td class="row2 gen">
				<c:forEach items="${groups}" var="group">
					<c:if test="${userSession.roleManager.administrator || userSession.roleManager.isGroupManager$1[group.id]}">
						<input  type="checkbox" name="groupIds" id="group_${group.id}" value="${group.id}" <c:if test="${jforum:contains(user.groups, group)}">checked</c:if>/>&nbsp;<label for="group_${group.id}">${group.name}</label><br/>
					</c:if>
				</c:forEach>
			</td>
		</tr>
	
		<tr align="center">
			<td class="catbottom" colspan="2" height="28">
				<input class="mainoption" type="submit" value="<jforum:i18n key='Groups.Form.ClickToUpdate'/>"/>
			</td>
		</tr>
	</table>
</form>
  
