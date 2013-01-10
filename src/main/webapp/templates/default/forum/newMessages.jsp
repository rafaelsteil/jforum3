<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

<script type="text/javascript" src="<jforum:templateResource item="/js/pagination.js"/>"></script>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline" valign="top">
			<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
				<tr>
					<td valign="bottom" align="left" colspan="2" class="maintitle">
						<jforum:i18n key='Search.searchResults'/>:
						${pagination.totalRecords}
						
						<c:choose>
							<c:when test="${pagination.totalRecords == 1}">
								<jforum:i18n key='Search.recordFound'/>
							</c:when>
							<c:otherwise>
								<jforum:i18n key='Search.recordsFound'/>
							</c:otherwise>
						</c:choose>
						<br />
					</td>
				</tr>
				<tr>
					<td align="left" valign="middle">		  
						<a class="nav" href="<jforum:url address="/forums/list"/>"><jforum:i18n key='ForumListing.forumIndex'/></a>
					</td>

					<td valign="bottom" nowrap="nowrap" align="right">
						<c:set var="paginationLinks">
							<jforum:pagination info="${pagination}"/>
						</c:set>

						${paginationLinks}
					</td>
				</tr>
			</table>

			<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
				<tr>
					<th class="thcornerl" nowrap="nowrap" align="center" width="20">&nbsp;</th>
					<th class="thcornerl" nowrap="nowrap" align="center">&nbsp;<jforum:i18n key='Forums.Form.ForumName'/>&nbsp;</th>
					<th class="thcornerl" nowrap="nowrap" align="center">&nbsp;<jforum:i18n key='ForumIndex.topics'/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" width="50">&nbsp;<jforum:i18n key='ForumIndex.answers'/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" width="100">&nbsp;<jforum:i18n key='ForumIndex.author'/>&nbsp;</th>
					<th class="thtop" nowrap="nowrap" align="center" width="50">&nbsp;<jforum:i18n key='ForumIndex.views'/>&nbsp;</th>
					<th class="thcornerr" nowrap="nowrap" align="center" width="150">&nbsp;<jforum:i18n key='ForumIndex.lastMessage'/>&nbsp;</th>
				</tr>
			
				<!-- TOPICS LISTING -->
				<c:set var="inSearch" value="true"/>
				<c:forEach items="${results}" var="topic">
					<tr class="highlight">
						<td class="row1" valign="middle" align="center" width="20">
							<%@include file="topic_folder_images.jsp"%>
						</td>
						<td class="row1 topictitle">
							<a href="<jforum:url address="/forums/show/${topic.forum.id}"/>">${topic.forum.name}</a>
						</td>
						<td class="row1">
							<a href="<jforum:url address="/topics/list/${topic.id}"/>" class="topictitle">${topic.subject}</a>

							<!-- 
							<#if topic.paginate>
								<span class="gensmall">
									<br />
									<@pagination.littlePostPagination topic.id, postsPerPage, topic.totalReplies/>
								</span>
							</#if>
							 -->
						</td>
						<td class="row2 postdetails" valign="middle" align="center">
							${topic.totalReplies}
						</td>
						<td class="row3 name" valign="middle" align="center">
							<a href="<jforum:url address="/user/profile/${topic.user.id}"/>">${topic.user.username}</a>
						</td>
						<td class="row2 postdetails" valign="middle" align="center">
							${topic.totalViews}
						</td>
						<td class="row3" valign="middle" nowrap="nowrap" align="center">
							<span class="postdetails">${topic.lastPost.date}<br />
							<a href="<jforum:url address="/user/profile/${topic.lastPost.user.id}"/>">${topic.lastPost.user.username}</a>
							<a href="<jforum:url address='/topics/list/${jforum:lastPage(topic.totalPosts, pagination.recordsPerPage)}/${topic.id}'/>#${topic.lastPost.id}"><img src="<jforum:templateResource item='/images/icon_latest_reply.gif'/>" border="0" alt="[Latest Reply]" /></a></span>
							</span>
						</td>
					</tr>
				</c:forEach>
				<!-- END OF TOPICS LISTING -->

				<tr>
					<td class="catbottom" valign="middle" align="right" colspan="7" height="28">&nbsp;</td>
				</tr>
			</table>
		
			<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
				<tr>
					<td align="left" valign="middle">
						<a class="nav" href="<jforum:url address="/forums/list"/>"><jforum:i18n key='ForumListing.forumIndex'/></a>
					</td>
					<td valign="middle" nowrap="nowrap" align="right">
						${paginationLinks}
					</td>
				</tr>
				<tr>
					<td align="left" colspan="2">&nbsp;</td>
				</tr>
			</table>
			 
			<table cellspacing="0" cellpadding="0" width="100%" border="0">
				<tr>
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
					<td align="right">&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<c:import url="../footer.jsp"/>
