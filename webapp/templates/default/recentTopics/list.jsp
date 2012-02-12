<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="sectionTitle"><jforum:i18n key='ForumBase.${recentTopicsSectionKey}'/></c:set>
<c:set var="pageTitle">${sectionTitle}</c:set>
<%@ include file="../header.jsp" %>

<link rel="alternate" type="application/rss+xml" title="RSS" href="<jforum:url address='/rss/recentTopics'/>" />

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline" valign="top">
		
			<c:set var="breadCrumb">
				<table cellspacing="0" cellpadding="2" width="100%" align="center" border="0">
					<tr>
						<td valign="bottom" align="left">
							<a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a>
							&raquo;
							<a id="latest3" class="nav" href="<jforum:url address='/recentTopics/${action}'/>">${sectionTitle}</a>
							<c:set var="rssEnabled"><jforum:settings key="rss.enabled"/></c:set>
							<c:if test="${rssEnabled}">
								&nbsp;<a href="<jforum:url address='/rss/recentTopics'/>"><img src="<jforum:templateResource item='/images/xml_button.gif'/>" border="0" align="middle" alt="[XML]" /></a>
							</c:if>
						</td>
						<td>&nbsp;
						</td>
					</tr>
				</table>
			</c:set>
			
			${breadCrumb}
			
			<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
				<tr>
					<th class="thcornerl" nowrap="nowrap" align="center">&nbsp;<jforum:i18n key='Forums.Form.ForumName'/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" colspan="2" height="25">&nbsp;<jforum:i18n key='ForumIndex.topics'/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" width="50">&nbsp;<jforum:i18n key='ForumIndex.answers'/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" width="100">&nbsp;<jforum:i18n key='ForumIndex.author'/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" width="50">&nbsp;<jforum:i18n key='ForumIndex.views'/>&nbsp;</th>
					<th class="thcornerr" nowrap="nowrap" align="center">&nbsp;<jforum:i18n key='ForumIndex.lastMessage'/>&nbsp;</th>
				</tr>

				<!-- TOPICS LISTING -->
				<c:forEach items="${topics}" var="topic">
					<c:if test="${roleManager.isForumAllowed$1[topic.forum.id]}">					
						<tr class="highlight">
							<td class="row2" valign="middle" align="center"><a class="postdetails" href="<jforum:url address='/forums/show/${topic.forum.id}'/>">${topic.forum.name}</a></td>
							<td class="row1" valign="middle" align="center" width="20"><%@include file="../forum/topic_folder_images.jsp"%></td>
							<td class="row1" width="80%">
								<span class="topictitle">
								<a href="<jforum:url address='/topics/list/${topic.id}'/>">${topic.subject}</#if></a>
								</span>
		
								<!-- 
								<#if topic.paginate>
									<span class="gensmall">
									<br />
									<@pagination.littlePostPagination topic.id, postsPerPage, topic.totalReplies/>				
									</span>
								</#if>	
								 -->	
							</td>
		
							<td class="row2" valign="middle" align="center"><span class="postdetails">${topic.totalReplies}</span></td>
							<td class="row3" valign="middle" align="center">
								<span class="name"><a href="<jforum:url address='/user/profile/${topic.user.id}'/>">${topic.user.username}</a></span>
							</td>
		
							<td class="row2" valign="middle" align="center"><span class="postdetails">${topic.totalViews}</span></td>
							<td class="row3right" valign="middle" nowrap="nowrap" align="center">
								<span class="postdetails">${topic.lastPost.date}<br />
								<a href="<jforum:url address='/user/profile/${topic.lastPost.user.id}'/>">${topic.lastPost.user.username}</a>
								<a href="<jforum:url address='/posts/preList/${topic.id}/${topic.lastPost.id}'/>"><img src="<jforum:templateResource item='/images/icon_latest_reply.gif'/>" border="0" alt="Latest Reply" /></a></span>
							</td>
						</tr>
					</c:if>
				</c:forEach>	
				<!-- END OF TOPICS LISTING -->
			</table>  
			
			${breadCrumb}
		</td>
	</tr>
</table>

<c:import url="../footer.jsp"/>
