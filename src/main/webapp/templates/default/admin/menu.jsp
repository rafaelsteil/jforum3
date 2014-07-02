<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=${charset}" />
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>
</head>
<body text="#000000" vlink="#5493b4" link="#006699" bgcolor="#e5e5e5">
<table cellspacing="0" cellpadding="4" width="100%" align="center" border="0">
	<tr>
		<td align="middle">
			<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
				<tr>
					<th class="thhead" height="25"><b><jforum:i18n key="Admin.administration"/></b></th>
				</tr>

				<tr><td class="row1"><a id="forumIndex" class="genmed" target='_top' href="<jforum:url address='/forums/list'/>"><jforum:i18n key="Admin.forumIndex"/></a></td></tr>
				<tr><td class="row1"><a id="forumIndex" class="genmed" target='main' href="<jforum:url address='/admin/main'/>"><jforum:i18n key="Admin.adminIndex"/></a></td></tr>
				<tr>
					<td class="catsides" height="28"><span class="cattitle"><jforum:i18n key="Admin.forumAdmin"/></span></td>
				</tr>
				
				<tr>
					<td class="row1"><a id="groups" class="genmed" href="<jforum:url address='/adminGroups/list'/>" target="main"><jforum:i18n key="Admin.groups"/></a></td>
				</tr>
				
				<tr>
					<td class="row1"><a id="users" class="genmed" href="<jforum:url address='/adminUsers/list'/>" target="main"><jforum:i18n key="Admin.users"/></a></td>
				</tr>
				
				<c:if test="${userSession.roleManager.administrator}">
					<tr>
						<td class="row1"><a id="categories" class="genmed" href="<jforum:url address='/adminCategories/list'/>" target="main"><jforum:i18n key="Admin.categories"/></a></td>
					</tr>
				</c:if>

				<tr>
					<td class="row1"><a id="forums" class="genmed" href="<jforum:url address='/adminForums/list'/>" target="main"><jforum:i18n key="Admin.forums"/></a></td>
				</tr>
				
				<c:if test="${userSession.roleManager.administrator}">
					<tr>
						<td class="row1">
							<p><a id="rankings" class="genmed" href="<jforum:url address='/adminRankings/list'/>" target="main"><jforum:i18n key="Admin.rankings"/></a></p>
						</td>
					</tr>
					<tr>
						<td class="row1">
							<p><a id="badword" class="genmed" href="<jforum:url address='/adminBadWord/list'/>" target="main"><jforum:i18n key="Admin.badWord"/></a></p>
						</td>
					</tr>
			
					<tr>
						<td class="row1"><p><a id="configurations" class="genmed" href="<jforum:url address='/adminConfig/list'/>" target="main"><jforum:i18n key="Admin.configurations"/></a></p></td>
					</tr>
			
					<tr>
						<td class="row1"><p><a id="smilies" class="genmed" href="<jforum:url address='/adminSmilies/list'/>" target="main"><jforum:i18n key="Admin.smilies"/></a></p></td>
					</tr>

					<!-- 
					<tr>
						<td class="row1"><p><a id="avatars" class="genmed" href="<jforum:url address='/adminAvatar/list'/>" target="main"><jforum:i18n key="Admin.avatars"/></a></p></td>
					</tr>
					 -->
				
					<tr>
						<td class="row1"><p><a id="smilies" class="genmed" href="<jforum:url address='/adminSearchStats/list'/>" target="main"><jforum:i18n key="Admin.search"/></a></p></td>
					</tr>
				
					<tr>
						<td class="row1"><p><a id="smilies" class="genmed" href="<jforum:url address='/jforum'/>?module=hibernate&action=list" target="main"><jforum:i18n key="Admin.databaseStats"/></a></p></td>
					</tr>

					<!--
					<tr>
						<td class="row1">
							<p></p>
							<table width="100%" cellspacing="0" cellpadding="0">
								<tr><td colspan="2"><span class="genmed"><jforum:i18n key="Admin.attachments"/></span></td></tr>
								<tr>
									<td width="20px">&nbsp;</td>
									<td>
										<a id="attachments" class="gensmall" href="<jforum:url address='/adminAttachments/configurations'/>" target="main"><jforum:i18n key="Admin.attachConfigurations"/></a>
									</td>
								</tr>
	
								<tr>
									<td width="20px">&nbsp;</td>
									<td>
										<a id="attachments" class="gensmall" href="<jforum:url address='/adminAttachments/quotaLimit'/>" target="main"><jforum:i18n key="Admin.attachQuota"/></a>
									</td>
								</tr>
	
								<tr>
									<td width="20px">&nbsp;</td>
									<td>
										<a id="attachments" class="gensmall" href="<jforum:url address='/adminAttachments/extensionGroups'/>" target="main"><jforum:i18n key="Admin.attachExtensionGroups"/></a>
									</td>
								</tr>
	
								<tr>
									<td width="20px">&nbsp;</td>
									<td>
										<a id="attachments" class="gensmall" href="<jforum:url address='/adminAttachments/extensions'/>" target="main"><jforum:i18n key="Admin.attachExtensions"/></a>
									</td>
								</tr>
							</table>
							
						</td>
					</tr>
					-->
				</c:if>
			</table>
		</td>
	</tr>
</table>

<div align="center"><span class="copyright">Powered by <a class="copyright" href="http://www.jforum.net/" target="_blank">JForum</a></span></div>
<br />

<iframe src="<c:url value='/ping_session.jsp'/>" height="0" width="0" frameborder="0" scrolling="no"></iframe>

</body>
</html>
