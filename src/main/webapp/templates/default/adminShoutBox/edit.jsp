<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form action="<jforum:url address='/jforum'/>?module=adminShoutBox&action=editSave" method="post" name="form" id="form">

<input type="hidden" name="shoutbox.id" value="${shoutbox.id}" />
<input type="hidden" name="shoutbox.category.id" value="${shoutbox.category.id}" />

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="middle" colspan="2" height="25"><jforum:i18n key="ShoutBox.Form.Title"/></th>
	</tr>

	<tr>
		<td class="row1" width="38%"><span class="gen"><jforum:i18n key="ShoutBox.Form.ShoutLength"/></span></td>
		<td class="row2"><input class="post" style="width: 200px;" maxlength="100" size="25" name="shoutbox.shoutLength" value="${shoutbox.shoutLength}" /></td>
	</tr>
	
	<tr>
		<td class="row1 gen" width="38%"><jforum:i18n key="ShoutBox.Form.IsAllowAnonymous"/></td>
		<td class="row2 gensmall">
			<input class="post" type="radio" name="shoutbox.allowAnonymous" value="false" ${shoutbox.allowAnonymous?"":"checked=\"checked\""}/>&nbsp;<jforum:i18n key="User.no"/>
			&nbsp;&nbsp;
			<input class="post" type="radio" name="shoutbox.allowAnonymous" value="true" ${shoutbox.allowAnonymous?"checked=\"checked\"":""}/>&nbsp;<jforum:i18n key="User.yes"/>
		</td>
	</tr>
	<tr>
		<td class="row1 gen" width="38%"><jforum:i18n key="ShoutBox.Form.IsDisabled"/></td>
		<td class="row2 gensmall">
			<input class="post" type="radio" name="shoutbox.disabled" value="false" ${shoutbox.disabled?"":"checked=\"checked\""}/>&nbsp;<jforum:i18n key="User.no"/>
			&nbsp;&nbsp;
			<input class="post" type="radio" name="shoutbox.disabled" value="true" ${shoutbox.disabled?"checked=\"checked\"":""}/>&nbsp;<jforum:i18n key="User.yes"/>
		</td>
	</tr>
	
	<tr align="center">
		<td class="catbottom" colspan="2" height="28">
			<input class="mainoption" type="submit" value="<jforum:i18n key="ShoutBox.Form.ClickToUpdate"/>" name="submit" />
		</td>
	</tr>
</table>
</form>
