<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 
<html>
<head>
<title><jforum:i18n key='PrivateMessage.findUser'/></title>
<meta http-equiv="Content-Type" content="text/html; charset=${encoding}" />
<style type="text/css">@import url( <jforum:templateResource item="/styles/style.css"/> );</style>
</head>

<body>

<script type="text/javascript">
function chooseUsername() {
	var u = document.search.selectedUsername;

	opener.document.forms['post'].toUserId.value = u.value
	opener.document.forms['post'].toUsername.value = u[u.selectedIndex].text;

	opener.focus();
	window.close();
}
</script>

<form action="<jforum:url address="/pm/findUser"/>" method="post" name="search" id="search" accept-charset="${encoding}">
	<table cellspacing="0" cellpadding="10" width="100%" border="0">
		<tr>
			<td>
				<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
					<tr>
						<th class="thhead" height="25"><jforum:i18n key='PrivateMessage.findingUser'/></th>
					</tr>
					<tr>
						<td class="row1" valign="top">
							<span class="genmed"><br />
							<input type="text" class="post" name="username" value="${username}"/>&nbsp; 
							<input class="liteoption" type="submit" value="<jforum:i18n key='ForumBase.search'/>" />
							</span><br />
							<span class="gensmall"><jforum:i18n key='PrivateMessage.searchTip'/></span><br />
							<br />
							
							<c:if test="${fn:length(users) > 0}">
								<select name="selectedUsername">
									<c:forEach items="${users}" var="user">
										<option value="${user.id}">${user.username}</option>
									</c:forEach>
								</select>
								
								&nbsp;<input type="button" value="<jforum:i18n key='PrivateMessage.select'/>" class="liteoption" onclick="chooseUsername();" />
							</c:if>
							
							<center><span class="genmed"><a class="genmed" href="javascript:window.close();"><jforum:i18n key='closeWindow'/></a></span></center>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</form>
</body>
</html>