<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<script language="javascript">
function checkGroups()  {
	return true;
	
	if (document.form.groups.selectedIndex == -1) {
		alert("<jforum:i18n key='PermissionControl.atLeastOne'/>");
		return false;
	}

	return true;
}
</script>

<c:set var="saveAction" value="addSave"/>

<c:if test="${not empty category}">
	<c:set var="saveAction" value="editSave"/>
</c:if>

<form action="<jforum:url address='/adminCategories/${saveAction}'/>" method="post" name="form" id="form" accept-charset="${encoding}" onSubmit="return checkGroups()">

<c:if test="${not empty category}">
	<input type="hidden" name="category.id" value="${category.id}" />
</c:if>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="2" height="25"><jforum:i18n key="Category.Form.Title"/></th>
	</tr>

	<tr>
		<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Category.Form.CategoryName"/></span></td>
		<td class="row2"><input class="post" style="width: 200px;" maxlength="100" size="25" name="category.name" value="${category.name}" /></td>
	</tr>

	<tr>
		<td class="row1 gen" width="38%"><jforum:i18n key="Category.Form.Moderate"/></td>
		<td class="row2 gensmall">
			<input class="post" type="radio" name="category.moderated" value="false" <c:if test="${!category.moderated}">checked="checked"</c:if>/>&nbsp;<jforum:i18n key="User.no"/>
			&nbsp;&nbsp;
			<input class="post" type="radio" name="category.moderated" value="true" <c:if test="${category.moderated}">checked="checked"</c:if>/>&nbsp;<jforum:i18n key="User.yes"/>
		</td>
	</tr>
	
	<c:if test="${false && empty category}">
		<tr>
			<td class="row1 gen"><jforum:i18n key="Category.Form.ChooseGroup"/></td>
			<td><@lib.selectFieldGroups "groups", groups, 0, true, selectedList/></td>
		</tr>
	</c:if>
	
	<tr align="center">
		<td class="catbottom" colspan="2" height="28">
			<input class="mainoption" type="submit" value="<jforum:i18n key="Category.Form.ClickToUpdate"/>" name="submit" />
		</td>
	</tr>
</table>
</form>
