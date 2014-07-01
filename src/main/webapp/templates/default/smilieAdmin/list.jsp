<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form accept-charset="${encoding}" name="form" action="<jforum:url address='/adminSmilies/delete'/>" method="post">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="4" height="25"><jforum:i18n key="Smilies.List.Title"/></th>
	</tr>

	<c:forEach items="${smilies}" var="smilie">
		<tr class="highlight">
			<td class="row1" width="8%"><img src="<c:url value='/images/smilies/${smilie.diskName}'/>"/></td>
			<td class="row2 gen">${smilie.code}</td>
			<td class="row2 gen">
				<a href="<jforum:url address='/adminSmilies/edit?smilieId=${smilie.id}'/>"><jforum:i18n key="Smilies.List.Edit"/></a>
			</td>
			<td class="row2"><input type="checkbox" name="smiliesId" value="${smilie.id}"/></td>
		</tr>
	</c:forEach>

	<tr align="center">
		<td class="catbottom" colspan="4" height="28">
			<input class="mainoption" type="button" value="<jforum:i18n key="Smilies.List.ClickToNew"/>" name="button" onClick="document.location = '<jforum:url address='/adminSmilies/add'/>';"/>
			&nbsp;&nbsp;
			<input class="mainoption" type="submit" value="<jforum:i18n key="Smilies.List.ClickToDelete"/>" name="submit"/>
		</td>
	</tr>
</table>

</form>
   
