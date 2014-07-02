<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=${encoding};"  />
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>
</head>
<body bgcolor="#E5E5E5" text="#000000">

<a name="top"></a>

<c:if test="${roleManager.administrator}">
	<h1><jforum:i18n key="ForumListing.stats"/></h1>
	
	<table width="100%" cellpadding="4" cellspacing="1" border="0" class="forumline gen">
	  <tr>
		<th width="25%" nowrap="nowrap" height="25" class="thCornerL"><jforum:i18n key="Admin.welcome.statistic"/></th>
		<th width="25%" height="25" class="thTop"><jforum:i18n key="Admin.welcome.value"/></th>
	
		<th width="25%" nowrap="nowrap" height="25" class="thTop"><jforum:i18n key="Admin.welcome.statistic"/></th>
		<th width="25%" height="25" class="thCornerR"><jforum:i18n key="Admin.welcome.value"/></th>
	  </tr>
	 
	  <tr>
		<td class="row1" nowrap="nowrap"><jforum:i18n key="Admin.welcome.numPosts"/></td>
		<td class="row1"><b>${stats.posts}</b></td>
		<td class="row1" nowrap="nowrap"><jforum:i18n key="Admin.welcome.postsPerDay"/></td>
	
		<td class="row2"><b><fmt:formatNumber type="number" maxFractionDigits="2" value="${stats.postsPerDay}"/></b></td>
	  </tr>
	  <tr>
		<td class="row2" nowrap="nowrap"><jforum:i18n key="Admin.welcome.numTopics"/></td>
		<td class="row2"><b>${stats.topics}</b></td>
		<td class="row2" nowrap="nowrap"><jforum:i18n key="Admin.welcome.topicsPerDay"/></td>
		<td class="row2"><b><fmt:formatNumber type="number" maxFractionDigits="2" value="${stats.topicsPerDay}"/></b></td>
	
	  </tr>
	  <tr>
		<td class="row1" nowrap="nowrap"><jforum:i18n key="Admin.welcome.numUsers"/></td>
		<td class="row1"><b>${stats.users}</b></td>
		<td class="row1" nowrap="nowrap"><jforum:i18n key="Admin.welcome.usersPerDay"/></td>
		<td class="row1"><b><fmt:formatNumber type="number" maxFractionDigits="2" value="${stats.usersPerDay}"/></b></td>
	  </tr>
	</table>
</c:if>

<c:if test="${roleManager.administrator}">
	<h1><jforum:i18n key="ForumListing.whoIsOnline"/></h1>
	
	<div ${totalLoggedUsers > 10?'style="overflow: auto; width: 100%; height: 280px;"':''}>
		<table width="100%" cellpadding="4" cellspacing="1" border="0" class="forumline">
			<tr>
				<th width="20%" class="thCornerL" height="25">&nbsp;Username&nbsp;</th>
				<th width="20%" height="25" class="thTop">&nbsp;<jforum:i18n key="Admin.welcome.logIn"/>&nbsp;</th>
				<th width="20%" class="thTop">&nbsp;<jforum:i18n key="Admin.welcome.lastUpdated"/>&nbsp;</th>
				<th width="20%" height="25" class="thCornerR">&nbsp;<jforum:i18n key="Admin.welcome.ipAddress"/>&nbsp;</th>
			</tr>
		
		    <c:forEach var="s" items ="${sessions}" varStatus="status">
				<c:set var = "row"  value = "${status.count%2==0?'row1':'row2' }" />
				<tr>
					<td width="20%" class="${row}">
						<span class="gen"><a href="<jforum:url address='/forums'/>?module=adminUsers&action=edit&id=${s.user.id}" class="gen">
						${s.user.username}</a></span>
					</td>
					<td width="20%" align="center" class="${row}">&nbsp;<span class="gen">${s.creationTime}</span>&nbsp;</td>
					<td width="20%" align="center" nowrap="nowrap" class="${row}">&nbsp;<span class="gen">${s.lastAccessedDate}</span>&nbsp;</td>
					<td width="20%" class="${row}">&nbsp;<span class="gen"><a href="http://network-tools.com/default.asp?host=${s.ip}" class="gen" target="_blank">${s.ip}</a></span>&nbsp;</td>
				</tr>
		    </c:forEach>
		
		  <tr>
			<td colspan="5" height="1" class="row3"><img src="../templates/subSilver/images/spacer.gif" width="1" height="1" alt="."/></td>
		  </tr>
		</table>
	</div>
</c:if>

</body>
</html>
