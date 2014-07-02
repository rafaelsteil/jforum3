<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<c:set var="saveAction" value="addSave"/>

<c:if test="${not empty group}">
	<c:set var="saveAction" value="editSave"/>
</c:if>

<form accept-charset="${encoding}" name="form" action="<jforum:url address='/adminGroups/${saveAction}'/>" method="post">

<c:if test="${not empty group}">
  <input type="hidden" name="group.id" value="${group.id}"/>
</c:if>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%">
	<tr>
		<th class="thhead" valign="center" colspan="2" height="25"><jforum:i18n key='Groups.Form.Title'/></th>
	</tr>
        
	<tr>
		<td class="row1 gen" width="38%"><jforum:i18n key='Groups.Form.GroupName'/></td>
		<td class="row2"><input type="text" name="group.name" value="${group.name}"/></td>
	</tr>
	
	<tr>
		<td class="row1 gen"><jforum:i18n key='Groups.Form.Description'/></td>
		<td class="row2"><textarea rows="4" cols="30" wrap="virtual" name="group.description">${group.description}</textarea></td>
	</tr>
	
	<tr>
		<td class="catbottom" colspan="2" height="28"><input class="mainoption" type="submit" value="<jforum:i18n key='Groups.Form.ClickToUpdate'/>"/></td>
	</tr>
</table>
</form>
  
