<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pageTitle" scope="request"><jforum:settings key='forum.page.title'/></c:set>

<%@ include file="../header.jsp" %>

<table width="100%" align="center">
	<tr>
		<td width="100%" height="318" valign="top">
			<table cellspacing="0" cellpadding="2" width="100%" align="center" border="0">
				<tr>
					<td valign="bottom" align="left">
						<c:if test="${userSession.logged}">
							<span class="gensmall bg"><jforum:i18n key="ForumListing.lastVisit"/>: ${userSession.lastVisitDate}</span><br/>
						</c:if>
						
						<!-- 
						<span class="gensmall"><jforum:i18n key="ForumListing.date"/>: ${now}</span><br />
						 -->
						<span class="forumlink"><a class="forumlink" href="<jforum:url address='/forums/list'/>"><jforum:i18n key="ForumListing.forumIndex"/></a></span>
					</td>
					<td class="gensmall" valign="bottom" align="right">
						&nbsp;
						<c:if test="${userSession.logged}">
							<a class="gensmall" href="<jforum:url address='/forums/newMessages'/>"><jforum:i18n key="ForumListing.readLastVisitMessages"/></a>
						</c:if>
					</td>
				</tr>
			</table>

			<table class="forumline" cellspacing="1" cellpadding="2" width="100%" border="0">
				<tr>
					<th class="thcornerl" nowrap="nowrap" colspan="2" height="25" align="center" valign="middle">&nbsp;<jforum:i18n key="ForumListing.forums"/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" width="50">&nbsp;<jforum:i18n key="ForumListing.totalTopics"/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" width="50">&nbsp;<jforum:i18n key="ForumListing.totalMessages"/>&nbsp;</th>
					<th class="thcornerr" nowrap="nowrap">&nbsp;<jforum:i18n key="ForumListing.lastMessage"/>&nbsp;</th>
				</tr>
		  
				<!-- START FORUM LISTING -->
				<jforum:displayCategories items="${categories}" var="category" roleManager="${userSession.roleManager}">
					<tr>
						<td class="catleft" colspan="2" height="28"><span class="cattitle">${category.name}</span></td>
						<td class="catleft" align="right" colspan="3">&nbsp;</td>
					</tr>

					<jforum:displayForums items="${category.forums}" var="forum" roleManager="${userSession.roleManager}">
						<tr class="highlight">
							<td class="row1" valign="middle" align="center" height="50">
							
								<c:remove var="forumUnread"/>

								<c:if test="${!userSession.isForumRead$1[forum]}">
									<c:set var="forumUnread" value="_new"/>
								</c:if>
								
								<img src="<jforum:templateResource item='/images/folder${forumUnread}_big.gif'/>" alt="[Folder]" />
							</td>
							<td class="row1" width="100%" height="50">
								<span class="forumlink"><a class="forumlink" href="<jforum:url address='/forums/show/${forum.id}'/>">${forum.name}</a></span><br />
								<span class="genmed">
									${forum.description}
									
									<c:if test="${forum.moderated}">
										<br />
										<jforum:i18n key="ForumIndex.moderators"/>: 
										<c:forEach items="${forum.moderators}" var="moderator">
									  		<a href="<jforum:url address='/user/listGroup/${moderator.id}'/>">${moderator.name}</a>
										</c:forEach>
									</c:if>
								</span>
								<br />		
							</td>
							
							<c:set var="forumTotalTopics" value="${forum.totalTopics}"/>
							
							<td class="row2 gensmall" valign="middle" align="center" height="50">${forumTotalTopics}</td>
							<td class="row2 gensmall" valign="middle" align="center" height="50">
								<c:set var="forumTotalPosts" value="${forum.totalPosts}"/>
								
								<c:choose>
									<c:when test="${forumTotalPosts > 0}">
										${forumTotalPosts}
									</c:when>
									 <c:otherwise>
										<jforum:i18n key="ForumListing.noMessages"/>
									</c:otherwise>
								</c:choose>
							</td>
							<td class="row2 postdetails" valign="middle" nowrap="nowrap" align="center" height="50">
								<c:choose>
									<c:when test="${forumTotalTopics == 0}">
										<jforum:i18n key="ForumListing.noMessages"/>
									</c:when>
									<c:otherwise>
										${forum.lastPost.date}<br />
										
										<a href="<jforum:url address='/user/profile/${forum.lastPost.user.id}'/>">${fn:escapeXml(forum.lastPost.user.username)}</a> 
										<a href="<jforum:url address='/topics/list/${jforum:lastPage(forum.lastPost.topic.totalPosts, postsPerPage)}/${forum.lastPost.topic.id}'/>#${forum.lastPost.id}"><img src="<jforum:templateResource item='/images/icon_latest_reply.gif'/>" border="0" alt="[Latest Reply]" /></a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</jforum:displayForums>
				</jforum:displayCategories>		
				<!-- END OF FORUM LISTING -->
			</table>

			<table cellspacing="0" cellpadding="2" width="100%" align="center" border="0">
				<tr>
					<td align="left"><span class="gensmall"><a class="gensmall" href="">&nbsp;</a></span><span class="gensmall">&nbsp;</span></td>
				</tr>
			</table>
		
			<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
				<tr>
					<td class="cathead" colspan="2" height="28"><span class="cattitle"><jforum:i18n key="ForumListing.whoIsOnline"/></span></td>
				</tr>

				<tr>
					<td class="row1" valign="middle" align="center" rowspan="2"><img src="<jforum:templateResource item='/images/whosonline.gif'/>" alt="[Who's Online]"/></td>
					<td class="row1 gensmall" align="left" width="100%">
						<jforum:i18n key="ForumListing.totalMessagesInfo" total="${totalMessages}"/><br/>
						<jforum:i18n key="ForumListing.registeredUsers" total="${totalRegisteredUsers}"/><br/>
						<jforum:i18n key="ForumListing.newestUser"/> <a href="<jforum:url address='/user/profile/${lastRegisteredUser.id}'/>">${lastRegisteredUser.username}</a></span>
					</td>
				</tr>

				<tr>
					<td class="row1 gensmall" align="left">
						<jforum:i18n key="ForumListing.numberOfUsersOnline" 
							totalUsers="${totalLoggedUsers + totalAnonymousUsers}" 
							totalLogged="${totalLoggedUsers}"
							totalAnonymous="${totalAnonymousUsers}"/>
			
						[ <span class="admin"><jforum:i18n key="Administrator"/></span> ]&nbsp;[ <span class="moderator"><jforum:i18n key="Moderator"/></span> ]
						<br/>
						<jforum:i18n key="ForumListing.mostUsersEverOnline" 
							total="${mostUsersEverOnline.total}" 
							date="${mostUsersEverOnline.date}"/>
						<br />
						<jforum:i18n key="ForumListing.connectedUsers"/>: 
			
						<c:if test="${totalLoggedUsers == 0}">
							<jforum:i18n key="Guest"/>
						</c:if>
						
						<c:if test="${totalLoggedUsers > 0}">
							<c:forEach items="${onlineUsers}" var="us">
								<c:choose>
									<c:when test="${us.roleManager.administrator}">
										<c:set var="color" value="admin"/>
									</c:when>
									<c:when test="${us.roleManager.moderator}">
										<c:set var="color" value="moderator"/>
									</c:when>
									<c:otherwise>
										<c:set var="color" value=""/>
									</c:otherwise>
								</c:choose>
							
								<a href="<jforum:url address='/user/profile/${us.user.id}'/>"><span class="${color}">${fn:escapeXml(us.user.username)}</span></a>&nbsp;
							</c:forEach>
						</c:if>
					</td>
				</tr>
			</table>
			
			<br/>
        
			<c:if test="${!userSession.logged && !sso}">
				<form name="formlogin" accept-charset="<jforum:settings key='encoding'/>" action="<jforum:url address='/user/authenticateUser'/>" method="post">
					<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
						<tr>
							<td class="cathead" height="28"><a name="login2" id="login2"></a><span class="cattitle"><jforum:i18n key="Login.enter"/></span></td>
						</tr>

						<tr>
							<td class="row1" valign="middle" align="center" height="28">
								<span class="gensmall">
									<jforum:i18n key="Login.user"/>: <input class="post" size="10" name="username" /> 
									&nbsp;&nbsp;&nbsp;
									<jforum:i18n key="Login.password"/>: <input class="post" type="password" size="10" name="password" />
									
									<c:set var="autoLoginEnabled"><jforum:settings key="auto.login.enabled"/></c:set>
									 
									<c:if test="${autoLoginEnabled}">
										&nbsp;&nbsp; &nbsp;&nbsp;
										<label for="autologin"><jforum:i18n key="Login.autoLogon"/></label> <input class="text" type="checkbox" name="autoLogin" value="true" id="autologin"/>
									</c:if>
									&nbsp;&nbsp;&nbsp; 
									<input class="mainoption" type="submit" value="<jforum:i18n key="Login.enter"/>" name="login" /> 
								</span>
							</td>
						</tr>
					</table>
				</form>
			</c:if>

			<table cellspacing="3" cellpadding="0" align="center" border="0">
				<tr>
					<td align="center" width="20"><img src="<jforum:templateResource item='/images/folder_new_big.gif'/>" alt="[New Folder]" /></td>
					<td><span class="gensmall bg"><jforum:i18n key="ForumListing.newMessages"/></span></td>
					
					<td>&nbsp;&nbsp;</td>
					<td align="center" width="20"><img src="<jforum:templateResource item='/images/folder_big.gif'/>" alt="[Folder]" /></td>
					<td><span class="gensmall bg"><jforum:i18n key="ForumListing.noNewMessages"/></span></td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<c:import url="../footer.jsp"/>
