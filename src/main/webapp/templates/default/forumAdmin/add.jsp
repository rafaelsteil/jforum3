<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<c:set var="saveAction" value="addSave"/>

<c:if test="${not empty forum}">
	<c:set var="saveAction" value="editSave"/>
</c:if>

<form action="<jforum:url address='/adminForums/${saveAction}'/>" method="post" name="form" id="form" accept-charset="${encoding}" onSubmit="return checkInput();">

<script language="javascript">
function checkCategory() {
	if (document.form["forum.category.id"].selectedIndex < 0) {
		alert("<jforum:i18n key='Forums.Form.ChooseCategory'/>");
		return false;
	}

	return true;
}

function checkInput() {
	return checkCategory();
}
</script>

<c:if test="${not empty forum}">
	<input type="hidden" name="forum.id" value="${forum.id}" />
</c:if>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="2" height="25"><jforum:i18n key='Forums.Form.Title'/></th>
	</tr>
        
	<tr>
		<td class="row1" width="20%"><span class="gen"><jforum:i18n key='Forums.Form.ForumName'/></span></td>
		<td class="row2"><input class="post" style="width: 200px" maxlength="150" size="25" name="forum.name" value="${forum.name}" /></td>
	</tr>

	<tr>
		<td class="row1" width="38%"><span class="gen"><jforum:i18n key='Forums.Form.Moderate'/></span></td>
		<td class="row2 gensmall">
			<input class="post" type="radio" name="forum.moderated" value="true" <c:if test="${forum.moderated}">checked</c:if>/>&nbsp;<jforum:i18n key='User.yes'/>
			&nbsp;&nbsp;
			<input class="post" type="radio" name="forum.moderated" value="false" <c:if test="${!forum.moderated}">checked</c:if> />&nbsp;<jforum:i18n key='User.no'/>
		</td>
	</tr>
	
	<tr>
		<td class="row1" width="38%"><span class="gen"><jforum:i18n key='Forums.Form.anonymousPosts'/></span></td>
		<td class="row2 gensmall">
			<input class="post" type="radio" name="forum.allowAnonymousPosts" value="true" <c:if test="${forum.allowAnonymousPosts}">checked</c:if>/>&nbsp;<jforum:i18n key='User.yes'/>
			&nbsp;&nbsp;
			<input class="post" type="radio" name="forum.allowAnonymousPosts" value="false" <c:if test="${!forum.allowAnonymousPosts}">checked</c:if> />&nbsp;<jforum:i18n key='User.no'/>
		</td>
	</tr>

	<tr>
		<td class="row1 gen"><jforum:i18n key='Forums.Form.Category'/></td>
		<td class="row2">
			<select name="forum.category.id">
				<c:forEach items="${categories}" var="category">
					<c:if test="${roleManager.administrator || roleManager.isCategoryAllowed$1[category.id]}">
						<option value="${category.id}" <c:if test="${not empty forum && forum.category.id == category.id}">selected</c:if>>${category.name}</option>
					</c:if>
				</c:forEach>
			</select>
		</td>
	</tr>

	<c:if test="${forumTimeLimitedEnable}">
	<tr>
		<td class="row1"><span class="gen"><jforum:i18n key='Forums.Form.LimitedTime'/></span></td>
		<td class="row2"><input class="post" style="width: 200px" maxlength="150" size="25" name="forumLimitedTime" value="${forumLimitedTime}" /></td>
	</tr>
	</c:if>
	<tr>
		<td class="row1"><span class="gen"><jforum:i18n key='Forums.Form.Description'/></span></td>
		<td class="row2"><textarea name="forum.description" cols="40" rows="6" class="post" style="width: 200px">${forum.description}</textarea></td>
	</tr>
	
	<c:if test="${false && not empty forum}">
		<tr>
			<td class="row1"><span class="gen"><jforum:i18n key='Forums.Form.Permissions'/></span></td>
			<td class="row2">
				<table width="100%">
					<tr>
						<td width="50%" class="gensmall"><b><jforum:i18n key='Forums.Form.RestrictAccessToGroup'/></b></td>
						<td><@lib.selectFieldGroups "groupsAccess", groups, 0, true, selectedList/></td>
					</tr>
	
					<tr>
						<td class="gensmall"><b><jforum:i18n key='Forums.Form.RestrictAnonymousPostToGroup'/></b></td>
						<td><@lib.selectFieldGroups "groupsAnonymous", groups, 0, true, selectedList/></td>
					</tr>
	
					<tr>
						<td class="gensmall"><b><jforum:i18n key='Forums.Form.MarkAsReadOnlyToGroup'/></b></td>
						<td><@lib.selectFieldGroups "groupsReadOnly", groups, 0, true, selectedList/></td>
					</tr>
	
					<tr>
						<td class="gensmall"><b><jforum:i18n key='Forums.Form.RestrictHtmlToGroup'/></b></td>
						<td><@lib.selectFieldGroups "groupsHtml", groups, 0, true, selectedList/></td>
					</tr>
				</table>
			</td>
		</tr>
	</c:if>

	<tr align="center">
		<td class="catbottom" colspan="2" height="28"><input class="mainoption" type="submit" value="<jforum:i18n key='Forums.Form.ClickToUpdate'/>" name="submit" /></td>
	</tr>

</table>
</form>

