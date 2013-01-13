<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<table class="forumline" cellspacing="0" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" colspan="2" valign="middle" colspan="35" height="25"><jforum:i18n key='ShoutBox.List.Title'/></th>
	</tr>

	<c:if test="${errorMessage}">
		<tr>
			<td align="center" colspan="2" ><span class="gensmall">${errorMessage}</span></td>
		</tr>
	</c:if>

	<c:forEach items="${shoutboxes}" var="shoutbox">
		<c:if test="${roleManager.administrator || roleManager.isCategoryAllowed$1[shoutbox.category.id]}">
			<tr class="highlight">
				<td class="row1"><span class="cattitle">${shoutbox.category.name}</span></td>
				<td class="row2 gen">
					<a href="<jforum:url address='/adminShoutBox/edit/${shoutbox.id}'/>"><jforum:i18n key="ShoutBox.List.Edit"/></a>
				</td>
			</tr>
		</c:if>
	</c:forEach>

</table>
