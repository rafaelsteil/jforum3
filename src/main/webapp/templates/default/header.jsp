<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<jforum:settings key='encoding'/>" />
<meta http-equiv="Pragma" content="no-cache"/>
<meta http-equiv="Expires" content="-1"/>
<style type="text/css">@import url(<jforum:templateResource item="/styles/style.css"/>);</style>

<c:if test="${not empty language}">
	<style type="text/css">@import url(<jforum:templateResource item="/styles/${language}.css"/>);</style>
</c:if>

<c:set var="customCss"><jforum:settings key="custom.css"/></c:set>
<c:set var="isExternalUserManagement"><jforum:settings key="external.user.management"/></c:set>

<c:if test="${not empty customCss}">
	<style type="text/css">@import url(<c:url value="/${customCss}"/>);</style>
</c:if>

<script type="text/javascript" src="<jforum:templateResource item='/js/jquery.js'/>"></script>
<title><c:out value="${pageTitle}" default="JForum"/></title>
</head>
<body class="<jforum:settings key='i18n.board.default'/>">

<c:set var="homepageLink"><jforum:settings key='homepage.link'/></c:set>

<table width="100%" border="0">
	<tr>
		<td>
			<table cellspacing="0" cellpadding="0" width="100%" border="0" style="display: ;">
				<tr>
					<td width="100%" align="center" valign="middle">
						<span class="maintitle"><jforum:settings key='forum.page.title'/></span>
						<table cellspacing="0" cellpadding="2" border="0">
							<tr>
								<td valign="top" nowrap="nowrap" align="center" class="mainmenu">&nbsp;
									<img src="<jforum:templateResource item='/images/icon_mini_search.gif'/>" alt="[Search]"/>
									<a id="search" href="<jforum:url address="/search/filters"/>"><strong><jforum:i18n key="ForumBase.search"/></strong></a> &nbsp;

									<img src="<jforum:templateResource item='/images/icon_mini_recentTopics.gif'/>" alt="[Recent Topics]" />
									<a id="latest" href="<jforum:url address='/recentTopics/listNew'/>"><jforum:i18n key="ForumBase.recentTopicsNew"/></a> &nbsp;
										
									<c:if test="${userSession.roleManager.userListingEnabled}">
										<img src="<jforum:templateResource item='/images/icon_mini_members.gif'/>" alt="[Members]" />&nbsp;
										<a id="latest2" href="<jforum:url address='/user/list'/>"><jforum:i18n key="ForumBase.usersList"/></a> &nbsp;
									</c:if>

									<!--
									<img src="<jforum:templateResource item='/images/icon_mini_groups.gif'/>" alt="[Groups]" />&nbsp;
									<a id="backtosite" href="${homepageLink}"><jforum:i18n key="ForumBase.backToSite"/></a>&nbsp;
									-->
									
									<c:if test="${userSession.logged}">
										<br/>
										<a id="myprofile" href="<jforum:url address='/user/edit/${userSession.user.id}'/>"><img src="<jforum:templateResource item='/images/icon_mini_profile.gif'/>" border="0" alt="[Profile]" /><jforum:i18n key="ForumBase.profile"/></a>&nbsp;
										
										<c:if test="${userSession.roleManager.moderator}">
											<img src="<jforum:templateResource item='/images/icon_mini_message.gif'/>"/>
											<a href="<jforum:url address="/postReport/list"/>" <c:if test="${totalPostReports > 0}">style="color: #FF0000; font-weight: bold;"</c:if>><jforum:i18n key="PostReport.menuLink" total="${totalPostReports}"/></a>&nbsp;
										</c:if>

										<c:if test="${userSession.roleManager.privateMessageEnabled}">
											<a id="privatemessages" href="<jforum:url address='/pm/inbox'/>"><img src="<jforum:templateResource item='/images/icon_mini_message.gif'/>" border="0" alt="[Message]" />
												<c:set var="totalPrivateMessages" value="${userSession.user.totalUnreadPrivateMessages}"/>
												<c:if test="${totalPrivateMessages > 0}">
													<strong><jforum:i18n key="ForumBase.newPm"/>: (${totalPrivateMessages})</strong>
												</c:if>
												
												<c:if test="${totalPrivateMessages == 0}">
													<jforum:i18n key="ForumBase.privateMessages"/>
												</c:if>
											</a>&nbsp;
											</span>
										</c:if>

										<c:if test="${!sso}">
											<a id="logout" href="<jforum:url address='/user/logout'/>"><img src="<jforum:templateResource item='/images/icon_mini_login.gif'/>" border="0" alt="[Login]" /> <jforum:i18n key="ForumBase.logout"/> [${userSession.user.username}] </a></span>
										</c:if>

										<c:if test="${sso && ssoLogout != null}">
											<a id="logout" href="${ssoLogout}" target="top"><img src="<jforum:templateResource item='/images/icon_mini_login.gif'/>" border="0" alt="[Logout]" /> <jforum:i18n key="ForumBase.logout"/> [${userSession.user.username}] </a></span>
										</c:if>						
									</c:if>
	
									<c:if test="${!userSession.logged}">
										<br/>
										<c:if test="${'true' != isExternalUserManagement}">
											<a id="register" href="<jforum:url address='/user/insert'/>"><img src="<jforum:templateResource item='/images/icon_mini_register.gif'/>" border="0" alt="[Register]" /> <jforum:i18n key="ForumBase.register"/></a>&nbsp;/&nbsp;</span>
										</c:if>
										<a id="login" href="<jforum:url address='/user/login'/>"><img src="<jforum:templateResource item='/images/icon_mini_login.gif'/>" border="0" alt="[Login]" /> <jforum:i18n key="ForumBase.login"/></a>&nbsp; </span>
									</c:if>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
