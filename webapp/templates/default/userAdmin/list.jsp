<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>
<script type="text/javascript" src="<jforum:templateResource item='/js/pagination.js'/>"></script>

<c:if test="${not empty pagination}">
	<c:set var="paginationData">
		<jforum:pagination info="${pagination}"/>
	</c:set>
</c:if>
<c:set var="isExternalUserManagement"><jforum:settings key="external.user.management"/></c:set>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="6" height="25"><jforum:i18n key="User.adminTitle"/></th>
	</tr>

	<tr>
		<td class="gen gensmall" align="center" colspan="6">
			<form id="formusersearch" action="<jforum:url address='/adminUsers/search'/>" accept-charset="${encoding}" method="post">
				<jforum:i18n key="User.searchByUsername"/>: <input type="text" name="username" value="${username}" />
				&nbsp;
				<jforum:i18n key="User.searchByGroup"/>: 
				GROUPS
				<input type="submit" value="<jforum:i18n key='ForumBase.search'/>" class="mainoption"/>
			</form>
		</td>
	</tr>
	
	<tr>
		<td class="gensmall" colspan="6" align="right">
			${paginationData}
		</td>
	</tr>
  	
  	<form accept-charset="${encoding}" name="form" action="<jforum:url address='/adminUsers/lockUnlock'/>" method="post">
	<input type="hidden" name="page" value="${pagination.thisPage}" />
  
	<tr>
		<td class='row2 gen' width="10%" align="center"><b><jforum:i18n key="User.id"/></b></td>
		<td class='row2 gen'><b><jforum:i18n key="User.username"/></b></td>
		<c:if test="${'true' != isExternalUserManagement}">
			<td class='row2 gen' align="center"><b>&nbsp;</b></td>
			<td class='row2 gen' align="center"><b>&nbsp;</b></td>
		</c:if>
		<td class='row2 gen' width="10%" align="center"><b><jforum:i18n key="Lock"/></b></td>
		<td class='row2 gen' width="10%" align="center"><b><jforum:i18n key="Unlock"/></b></td>
	</tr>

	<c:forEach items="${users}" var="user">
		<tr class="highlight">
			<td class="row1 gen" align="center">${user.id}</td>
			<td class="row1 gen">${user.username}</td>
			<c:if test="${'true' != isExternalUserManagement}">
				<td class="row1 gen" align="center"><a href="<jforum:url address='/adminUsers/edit'/>?userId=${user.id}"><jforum:i18n key="Edit"/></a></td>
				<td class="row1 gen" align="center"><a href="<jforum:url address='/adminUsers/groups'/>?userId=${user.id}"><jforum:i18n key="User.Groups"/></a></td>
			</c:if>
			<c:choose>
				<c:when test="${user.deleted}">
					<td class="row1" align="center">&nbsp;</td>
					<td class="row1" align="center"><input type="checkbox" name="userIds" value="${user.id}" /></td>
				</c:when>
				<c:otherwise>
					<td class="row1" align="center"><input type="checkbox" name="userIds" value="${user.id}" /></td>
					<td class="row1" align="center">&nbsp;</td>
				</c:otherwise>
			</c:choose>
		</tr>
	</c:forEach>

	<tr align="center">
		<td class="catbottom" colspan="6">
			<input class="mainoption" type="submit" value="<jforum:i18n key='Lock'/> / <jforum:i18n key='Unlock'/>" name="submit" />
		</td>
	</tr>
</table>
</form>

<table width="100%">
	<tr>
		<td align="right">
			${paginationData}
		</td>
	</tr>
</table>
