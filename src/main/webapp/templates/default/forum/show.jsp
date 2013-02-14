<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="pageTitle" scope="request"><jforum:settings key="forum.name"/> - ${forum.name}</c:set>
<c:import url="../header.jsp"/>

<c:set var="rssEnabled"><jforum:settings key="rss.enabled"/></c:set>

<script type="text/javascript" src="<jforum:templateResource item='/js/pagination.js'/>"></script>

<c:if test="${userSession.logged}">
	<script type="text/javascript" src="<jforum:templateResource item='/js/watch.js'/>"></script>
</c:if>

<c:if test="${roleManager.moderator}">
	<script type="text/Javascript" src="<jforum:templateResource item='/js/moderation.js'/>"></script>
</c:if>

<c:set var="canDownloadAttachments" value="${roleManager.getCanDownloadAttachments$1[forum.id]}"/>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline" valign="top">
			
			<c:if test="${roleManager.canApproveMessages}">
				<!-- Pending Moderation -->
				<%@ include file="pending_moderation.jsp" %>
			</c:if>
			
			<table cellspacing="2" cellpadding="2" width="100%" align="center">
				<tr>
					<td valign="bottom" align="left" colspan="2">
						<a class="maintitle" href="<jforum:url address='/forums/list'/>"><jforum:i18n key="ForumListing.forumIndex"/></a> &raquo;
						<a class="maintitle" href="<jforum:url address='/forums/show/${forum.id}'/>">${forum.name}</a>

						<c:if test="${rssEnabled}">
							<a href="<jforum:url address='/rss/forumTopics/${forum.id}'/>"><img src="<jforum:templateResource item='/images/xml_button.gif'/>" border="0" alt="[XML]" /></a>
							<br />
						</c:if>
					</td>
				</tr>
			</table>

			<table cellspacing="2" cellpadding="2" width="100%" align="center">
				<tr>
					<c:set var="colspan" value="2"/>
					
					<c:set var="displayNewTopicButton" value="${(userSession.logged || forum.allowAnonymousPosts)
						&& (!roleManager.isForumReadOnly$1[forum.id] && !roleManager.isForumReplyOnly$1[forum.id])
						&& (!roleManager.postOnlyWithModeratorOnline || (roleManager.postOnlyWithModeratorOnline && isModeratorOnline))}"/>
					
					<c:if test="${displayNewTopicButton}">
						<c:set var="colspan" value="0"/>
						
						<td valign="middle" align="left" width="50">
							<a href="<jforum:url address='/topics/add'/>?forumId=${forum.id}" rel="nofollow" class="icon_new_topic"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>
						</td>
					</c:if>

					<!-- 
					<form accept-charset="${encoding}" action="<jforum:url address='/jforum'/>" method="get" id="formSearch" name="formSearch">
					<input type="hidden" name="module" value="search"/>
					<input type="hidden" name="action" value="search"/>
					<input type="hidden" name="forum" value="${forum.id}">
					<input type="hidden" name="match_type" value="all">

					<td class="nav" valign="middle" align="left" colspan="${colspan}">
						<input type="text" onblur="if (this.value == '') this.value = '<jforum:i18n key="ForumIndex.searchThisForum"/>...';" onclick="if (this.value == '<jforum:i18n key="ForumIndex.searchThisForum"/>...') this.value = '';" value="<jforum:i18n key="ForumIndex.searchThisForum"/>..." size="20" name="search_keywords" class="inputSearchForum"/>
						<input type="submit" value="<jforum:i18n key="ForumBase.search"/>" class="liteoption">
					</td>

					</form>
					 -->

					<td class="nav" nowrap="nowrap" align="right">
						<c:set var="paginationData">
							<jforum:pagination info="${pagination}"/>
						</c:set>
						
						${paginationData}
					</td>
				</tr>
			</table>

			<c:if test="${roleManager.moderator}">
				<form action="<jforum:url address='/jforum'/>?module=moderation&action=" method="post" name="formModeration" id="formModeration" accept-charset="${encoding}">
				<input type="hidden" name="returnUrl" value="<jforum:url address='/forums/show/${pagination.thisPage}/${forum.id}'/>" />
				<input type="hidden" name="forumId" value="${forum.id}" />
			</c:if>

			<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
				<tr>
					<th class="thcornerl" nowrap="nowrap" align="center" colspan="2" height="25">&nbsp;<jforum:i18n key="ForumIndex.topics"/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" width="50">&nbsp;<jforum:i18n key="ForumIndex.answers"/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" width="100">&nbsp;<jforum:i18n key="ForumIndex.author"/>&nbsp;</th>
					<!--<th class="thtop" nowrap="nowrap" align="center" width="50">&nbsp;<jforum:i18n key="ForumIndex.views"/>&nbsp;</th>-->
					<th class="thcornerr" nowrap="nowrap" align="center">&nbsp;<jforum:i18n key="ForumIndex.lastMessage"/>&nbsp;</th>
	
					<c:if test="${roleManager.moderator}">
						<th class="thcorner" nowrap="nowrap" align="center">&nbsp;<jforum:i18n key="ForumIndex.moderation"/>&nbsp;</th>
					</c:if>
				</tr>
				
				<c:set var="topicHotBegin"><jforum:settings key="hot.topic.begin"/></c:set>

				<!-- TOPIC LISTING -->
				<c:forEach items="${topics}" var="topic" varStatus="topicStatus">
					<c:set var="row1" value="row1"/>
					<c:set var="row2" value="row2"/>
					<c:set var="row3" value="row3"/>
					
					<c:if test="${topic.sticky || topic.announce}">
						<c:set var="row1" value="row1Announce"/>
						<c:set var="row2" value="row2Announce"/>
						<c:set var="row3" value="row3Announce"/>
					</c:if>
				
					<tr class="highlight">
						<td class="${row1}" valign="middle" align="center" width="20">
							<%@include file="topic_folder_images.jsp"%>
						</td>
						<td class="${row1}" width="100%">
							<c:if test="${topic.hasAttachment && canDownloadAttachments}">
								<img src="<jforum:templateResource item='/images/icon_clip.gif'/>" align="middle" alt="[Clip]" />
							</c:if>

							<a href="<jforum:url address='/topics/list/${topic.id}'/>" class="topictitle">
								<c:if test="${topic.pollEnabled}"><jforum:i18n key="ForumListing.pollLabel"/></c:if>
								${fn:escapeXml(topic.subject)}
							</a>

							<!-- 
							<#if topic.paginate>
								<span class="gensmall">
								<br />
								<@pagination.littlePostPagination topic.id, postsPerPage, topic.totalReplies/>
								</span>
							</#if>
							 -->
						</td>

						<td class="${row2}" valign="middle"  align="center"><span class="postdetails">${topic.totalReplies}</span></td>
						<td class="${row3}" valign="middle"  align="center">
							<span class="name"><a href="<jforum:url address='/user/profile/${topic.user.id}'/>">${fn:escapeXml(topic.user.username)}</a></span>
						</td>

						<!--<td class="${row2}" valign="middle"  align="center"><span class="postdetails">${topic.totalViews}</span></td>-->
						<td class="${row3}" valign="middle"  nowrap="nowrap" align="center">
							<span class="postdetails">${topic.lastPost.date}<br />
							<a href="<jforum:url address='/user/profile/${topic.lastPost.user.id}'/>">${fn:escapeXml(topic.lastPost.user.username)}</a>
							<a href="<jforum:url address='/topics/list/${jforum:lastPage(topic.totalPosts, pagination.recordsPerPage)}/${topic.id}'/>#${topic.lastPost.id}"><img src="<jforum:templateResource item='/images/icon_latest_reply.gif'/>" border="0" alt="[Latest Reply]" /></a></span>
						</td>

						<c:if test="${roleManager.moderator}">
							<td class="${row2}" valign="middle" align="center">
								<c:choose>
									<c:when test="${topic.movedId != 0 && topic.forum.id != forum.id}">
										<input type="checkbox" disabled="disabled"/>
									</c:when>
									<c:otherwise>
										<input type="checkbox" name="topicIds" value="${topic.id}" onclick="changeTrClass(this, ${topicStatus.index});"/>
									</c:otherwise>
								</c:choose>
								
							</td>
						</c:if>
					</tr>
				</c:forEach>
				<!-- END OF TOPIC LISTING -->
				
				<tr align="center">
					<td class="catbottom" valign="middle"  align="right" colspan="<c:if test="${roleManager.moderator}">7</c:if><c:if test="${!roleManager.moderator || !openModeration}">6</c:if>" height="28">
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td align="center" class="gensmall">
									<%@ include file="moderation_buttons.jsp" %>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			<c:if test="${roleManager.moderator}">
				</form>
			</c:if>	

			<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
				<tr>
					<c:set var="colspan" value="2"/>
					
					<c:if test="${displayNewTopicButton}">
						<c:set var="colspan" value="0"/>
						
						<td valign="middle"  align="left" width="50">
							<a href="<jforum:url address='/topics/add'/>?forumId=${forum.id}" rel="nofollow" class="icon_new_topic"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>
						</td>
					</c:if>

					<td valign="middle"  align="left" colspan="${colspan}">
						<span class="nav">
						<a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key="ForumListing.forumIndex"/></a> &raquo;  <a class="nav" href="<jforum:url address='/forums/show/${forum.id}'/>">${forum.name}</a></span>
					</td>

					<td nowrap="nowrap" align="right" class="nav">${paginationData}</td>
				</tr>

				<tr>
					<td align="left" colspan="3"><span class="nav"></span></td>
				</tr>
			</table>

			<table cellspacing="0" cellpadding="5" width="100%" border="0">
				<tr>
					<!-- 
					<td align="left" class="gensmall">
						<c:if test="${userSession.logged}">
							<#if !watching>
								<#assign watchMessage = I18n.getMessage("ForumShow.watch")/>
								<a href="#watch" onClick="watchForum('<jforum:url address='/forums/watchForum/${forum.id}'/>', '<jforum:i18n key="ForumShow.confirmWatch"/>');">
							<#else>
								<#assign watchMessage = I18n.getMessage("ForumShow.unwatch")/>
								<a href="<jforum:url address='/forums/unwatchForum/${forum.id}'/>">
							</#if>
							<img src="<jforum:templateResource item='/images/watch.gif'/>" align="middle" alt="Watch" />&nbsp;${watchMessage}</a>
						</c:if>
					</td>
					 -->
					<td align="right">
						<c:import url="../forums_combo.jsp"></c:import>
					</td>
				</tr>
			</table>

			<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0">
				<tr>
					<td valign="top" align="left">
						<c:import url="folder_descriptions.jsp"/>
					</td>

					<c:if test="${forum.moderated}">
						<td align="right" class="gensmall">
							<jforum:i18n key="ForumIndex.forumAdmins"/>:
							<b>
							<c:forEach items="${forum.moderators}" var="moderator">
								<a href="<jforum:url address='/user/listGroup/${moderator.id}'/>">${moderator.name}</a>
							</c:forEach>
							</b>
						</td>
					</c:if>
				</tr>
			</table>
		</td>
	</tr>
</table>

<c:import url="../footer.jsp"/>
