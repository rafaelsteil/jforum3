<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form name="form" action="<jforum:url address='/jforum'/>?module=adminTag&action=delete" method="post">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="middle" colspan="3" height="25"><jforum:i18n key="Tag.Form.Title"/></th>
	</tr>

	<tr>
		<td class="row2" align="center"><span class="gen"><b><jforum:i18n key="Tag.tag"/></b></span></td>
		<td class="row2" align="center"><span class="gen"><b><jforum:i18n key="Edit"/></b></span></td>
		<td class="row2" align="center"><span class="gen"><b><jforum:i18n key="Delete"/></b></span></td>
	</tr>

	<c:forEach items="${tags}" var="tag">
		<tr>
			<td class="row1 gen" align="center">${tag}</td>
			<td class="row1 gen" align="center"><a href="<jforum:url address='/jforum'/>?module=adminTag&action=edit&name=${tag}"><jforum:i18n key="Edit"/></a></td>
			<td class="row1" align="center"><input type="checkbox" name="tags" value="${tag}" /></td>
		</tr>
	</c:forEach>

	<tr align="center">
		<td class="catbottom" colspan="3" height="28">
			<input class="mainoption" type="submit" value="<jforum:i18n key="Rank.List.ClickToDelete"/>" name="submit" />
		</td>
	</tr>
</table>
</form>
   
