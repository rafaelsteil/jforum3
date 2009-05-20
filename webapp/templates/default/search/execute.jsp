<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:import url="../header.jsp"/>

<script type="text/javascript" src="<jforum:templateResource item="/js/pagination.js"/>"></script>
<style type="text/css">@import url( <jforum:templateResource item='/styles/SyntaxHighlighter.css'/> );</style>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline" valign="top">
			<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
				<tr>
					<td valign="bottom" align="left" colspan="2">
						<span class="maintitle">
							<jforum:i18n key="Search.searchResults"/>:
							${pagination.totalRecords} 
							
							<c:choose>
								<c:when test="${pagination.totalRecords == 1}">
									<jforum:i18n key="Search.recordFound"/>
								</c:when>
								<c:otherwise>
									<jforum:i18n key="Search.recordsFound"/>
								</c:otherwise>
							</c:choose>
						</span>
						<br />
					</td>
				</tr>
				<tr>
					<td align="left" valign="middle">		  
						<a class="nav" href="<jforum:url address="/forums/list"/>"><jforum:i18n key="ForumListing.forumIndex"/></a>
					</td>
					<td class="nav" valign="bottom" nowrap="nowrap" align="right">
						<c:set var="paginationLinks">
							<jforum:pagination info="${pagination}" isSearch="true" searchParams="${searchParams}"/>
						</c:set>
						${paginationLinks}
					</td>
				</tr>
			</table>

			<table class="forumline" cellspacing="2" cellpadding="5" width="100%" border="0">
				<c:if test="${pagination.totalRecords == 0}">
					<tr>
						<td class="gen">
							<jforum:i18n key="Search.noResults"/> <a href="<jforum:url address="/search/filters"/>"><jforum:i18n key="Search.clickHere"/></a> <jforum:i18n key="Search.newSearch"/>
						</td>
					</tr>
				</c:if>
				
				<c:if test="${pagination.totalRecords > 0}">
					<c:forEach items="${results}" var="post">
						<tr>
							<td class="postinfo">
								<span class="gen">
									<img class="icon_folder" src="<c:url value='/images/transp.gif'/>" alt=""/>
									
									<c:set var="postUrl"><jforum:url address="/topics/preList/${post.topic.id}/${post.id}"/></c:set>

									<strong>
										<a href="${postUrl}">${fn:escapeXml(post.subject)}</a>
									</strong>, 
									<jforum:i18n key="Search.postedOn"/> <a href="<jforum:url address="/forums/show/${post.forum.id}"/>">${post.forum.name}</a>, 
									<jforum:i18n key="Search.postedAt"/> ${post.date}, 
									<jforum:i18n key="Search.postedBy"/> <a href="<jforum:url address="/user/profile/${post.user.id}"/>">${fn:escapeXml(post.user.username)}</a>									
								</span>
							</td>
						</tr>

						<tr>
							<td class="row1 gen">
								<jforum:displayFormattedMessage post="${post}"/>
								<br />
								<div align="right"><a href="${postUrl}"><jforum:i18n key="Search.viewMessage"/></a></div>
							</td>
						</tr>

						<tr>
							<td height="1" colspan="2" class="spacerow"><img width="1" height="1" alt="" src="<jforum:templateResource item="/images/spacer.gif"/>"/></td>
						</tr>
					</c:forEach>
				</c:if>
			</table>

			<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
				<tr>
					<td align="left" valign="middle">
						<span class="nav">
		  					<a class="nav" href="<jforum:url address="/forums/list"/>"><jforum:i18n key="ForumListing.forumIndex"/></a>
						</span>
					</td>
					<td valign="middle" nowrap="nowrap" align="right">
						${paginationLinks}
					</td>
				</tr>
				<tr>
					<td align="left" colspan="2"></td>
				</tr>
			</table>

			<table cellspacing="0" cellpadding="0" width="100%" border="0">
				<tr>
					<td align="right">
						<c:import url="../forums_combo.jsp"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<%@ include file="../highlighter.jsp" %>
<c:import url="../footer.jsp"/>