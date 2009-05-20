<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form accept-charset="${encoding}" name="form" action="<jforum:url address='/adminBanning/remove'/>" method="post">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="middle" colspan="5" height="25"><jforum:i18n key="Banlist.Title"/></th>
	</tr>

	<tr>
		<td class="row2" align="center"><span class="gen"><b><jforum:i18n key="Banlist.Type"/></b></span></td>
		<td class="row2" align="center"><span class="gen"><b><jforum:i18n key="Banlist.Value"/></b></span></td>
		<td class="row2" align="center"><span class="gen"><b><jforum:i18n key="Delete"/></b></span></td>
	</tr>

	<c:forEach items="${banlist}" var="b">
		<tr>
			<td class="row1 gen" align="center">
				<c:choose>
					<c:when test="${b.email != null}">
						<jforum:i18n key="Banlist.Email"/>
					</c:when>
					<c:when test="${b.ip != null}">
						<jforum:i18n key="Banlist.IP"/>
					</c:when>
					<c:when test="${b.userId > 0}">
						<jforum:i18n key="Banlist.UserID"/>
					</c:when>
					<c:otherwise>
						<jforum:i18n key="Banlist.Unknown"/>
					</c:otherwise>
				</c:choose>
			</td>
			<td class="row1 gen" align="center">
				<c:choose>
					<c:when test="${b.email != null}">
						${b.email}
					</c:when>
					<c:when test="${b.ip != null}">
						${b.ip}
					</c:when>
					<c:when test="${b.userId > 0}">
						${b.userId}
					</c:when>
					<c:otherwise>
						<jforum:i18n key="Banlist.Unknown"/>
					</c:otherwise>
				</c:choose>
			</td>
			<td class="row1" align="center"><input type="checkbox" name="banlistId" value="${b.id}" /></td>
		</tr>
	</c:forEach>

	<tr align="center">
		<td class="catbottom" colspan="5" height="28">
			<input class="mainoption" type="button" value="<jforum:i18n key="Rank.List.ClickToNew"/>" name="button" onclick="document.location = '<jforum:url address='/adminBanning/add'/>';" />
			&nbsp;&nbsp;
			<input class="mainoption" type="submit" value="<jforum:i18n key="Rank.List.ClickToDelete"/>" name="submit" />
		</td>
	</tr>
</table>
</form>
   
