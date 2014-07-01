<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<c:set var="saveAction" value="addSave"/>

<c:if test="${not empty avatar}">
	<c:set var="saveAction" value="editSave"/>
</c:if>

<form accept-charset="${encoding}" name="form" action="<jforum:url address='/jforum'/>?module=adminAvatar&action=${saveAction}" method="post" enctype="multipart/form-data">

<c:if test="${not empty avatar}">
	<input type="hidden" name="avatar.id" value="${avatar.id}"/>
</c:if>
<input type="hidden" name="avatar.avatarType" value="AVATAR_GALLERY"/>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="middle" colspan="2" height="25"><jforum:i18n key='Avatar.Form.Title'/></th>
	</tr>
	
	<tr>
		<td class="row1 gen"><jforum:i18n key='Avatar.Form.Url'/></td>
		<td class="row2">
			<input type="file" class="post" style="width: 200px" maxlength="25" size="25" name="image"/>
		</td>
	</tr>
	
	<tr align="center">
		<td class="catbottom" colspan="2" height="28">
			<input class="mainoption" type="submit" value="<jforum:i18n key='Avatar.Form.ClickToUpdate'/>"/>
		</td>
	</tr>

</table>
</form>
