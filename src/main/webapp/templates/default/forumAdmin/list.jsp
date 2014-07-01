<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form action="<jforum:url address='/jforum'/>?module=adminForums&action=delete" method="post" name="form" id="form">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="6" height="25"><jforum:i18n key="Forums.List.Title"/></th>
	</tr>
	
	<jforum:displayCategories items="${categories}" var="category" roleManager="${userSession.roleManager}">
		<tr>
			<td colspan="6" class="catleft catTitle">${category.name}</td>
		</tr>
		
		<jforum:displayForums items="${category.forums}" var="forum" roleManager="${userSession.roleManager}">
			<tr class="highlight">
				<td width="10" class="row1">&nbsp;</td>
				<td class="row1 forumLink">${forum.name}</td>
				<td class="row1 gen" align="center">
					<a id="forumEdit" href="<jforum:url address='/adminForums/edit?forumId=${forum.id}'/>"><jforum:i18n key="Forums.List.Edit"/></a>
				</td>
				
				<c:if test="${roleManager.administrator}">
					<td class="row2" align="center" width="10%"><input type="checkbox" name="forumsId" value="${forum.id}" /></td>
				</c:if>
				
				<td class="row2" align="center" width="10%">
					&nbsp;
					<c:if test="${forumCounter > 1}">
						<input type="button" value="<jforum:i18n key="up"/>" class="mainoption" onClick="document.location = '<jforum:url address='/adminForums/up/${forum.id}'/>';"/>
					</c:if>
				</td>
				
				<td class="row2" align="center" width="10%">
					&nbsp;
					<c:if test="${forumCounter < category.forums.size$0}">
						<input type="button" value="<jforum:i18n key="down"/>" class="mainoption" onClick="document.location = '<jforum:url address='/adminForums/down/${forum.id}'/>';"/>
					</c:if>
				</td>
			</tr>
		</jforum:displayForums>
	</jforum:displayCategories>

	<tr align="center">
		<td class="catbottom" colspan="6" height="28">
			<input class="mainoption" type="button" value="<jforum:i18n key="Forums.List.ClickToNew"/>" id="btn_insert" name="button" onclick="document.location = '<jforum:url address='/adminForums/add'/>';" />
			&nbsp;&nbsp;
			
			<c:if test="${roleManager.administrator}">
				<input class="mainoption" type="submit" value="<jforum:i18n key="Forums.List.ClickToDelete"/>" name="submit" />
			</c:if>
		</td>
	</tr>
</table>
</form>
