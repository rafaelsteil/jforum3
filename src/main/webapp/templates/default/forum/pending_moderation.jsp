<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${fn:length(forum.topicsPendingModeration) > 0}">
	<script type="text/javascript">
	function viewPending(id) {
		var tr = document.getElementById("tr_pending_" + id);
		var d = tr.style.display
		tr.style.display = (d == "none" ? "" : "none");
	}
	</script>
	<form action="<jforum:url address='/jforum'/>?module=moderation&action=approve" method="post" accept-charset="${encoding}">
	<input type="hidden" name="forumId" value="${forum.id}" />
	
	<table width="70%"class="forumline" align="center" cellspacing="1" cellpadding="4">
		<tr>
			<td class="bg_yellow" align="center" style="height: 30px" colspan="2"><span class="gensmal" style="font-size: 11px; "><b><jforum:i18n key="Moderation.checkQueue"/></b></span></td>
		</tr>
	
		<c:forEach items="${forum.topicsPendingModeration}" var="topic" varStatus="status">
			<c:choose>
				<c:when test="${status.index % 2 == 0}">
					<c:set var="rowColor" value=""/>
				</c:when>
				<c:otherwise>
					<c:set var="rowColor" value="bg_small_yellow"/>
				</c:otherwise>
			</c:choose>
	
			<tr class="${rowColor}">
				<td width="90%">
					<c:choose>
						<c:when test="${topic.totalReplies > 0}">
							<a href="<jforum:url address='/topics/list/${topic.id}'/>" class="gen">${topic.subject}</a>
						</c:when>
						<c:otherwise>
							<span class="gen">${topic.subject}</span>
						</c:otherwise>
					</c:choose>
				</td>
				<td align="center"><span class="gen"><a href="javascript:viewPending(${topic.id});"><b><jforum:i18n key="Moderation.Admin.view"/></b></a></span></td>
			</tr>
	
			<!-- Messages -->
			<tr id="tr_pending_${topic.id}" style="display: none;">
				<td colspan="2">
					<table width="95%" align="right">
						<c:forEach items="${topic.posts}" var="post" varStatus="postStatus">
							<tr><td><span class="gensmall"><b><jforum:i18n key="PostShow.author"/></b>: <a href="<jforum:url address='/user/profile/${post.user.id}'/>">${post.user.username}</a></span></td></tr>
							<tr><td><span class="gensmall"><jforum:displayFormattedMessage post="${post}"/></span></td></tr>
	
							<tr>
								<td colspan="2" align="right">
									<span class="gensmall">
										<input type="radio" id="status_approve_${post.id}" name="info[${postStatus.index}].status" value="0" /><label for="status_approve_${post.id}"><jforum:i18n key="Moderation.Admin.aprove"/></label>&nbsp;
										<input type="radio" id="status_defer_${post.id}" name="info[${postStatus.index}].status" value="1" checked="checked" /><label for="status_defer_${post.id}"><jforum:i18n key="Moderation.Admin.defer"/></label>&nbsp;
										<input type="radio" id="status_deny_${post.id}" name="info[${postStatus.index}].status" value="2" /><label for="status_deny_${post.id}"><jforum:i18n key="Moderation.Admin.reject"/></label>&nbsp;
										<input type="hidden" name="info[${postStatus.index}].postId" value="${post.id}" />
									</span>
								</td>
							</tr>
	
							<c:if test="${!postStatus.last}">
								<tr>
									<td colspan="2" height="1" class="spacerow"><img src="<jforum:templateResource item='/images/spacer.gif'/>" alt="" width="1" height="1" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</table>
				</td>
			</tr>
		</c:forEach>
	
		<!-- Submit -->
		<tr>
			<td colspan="2" align="center"><input type="submit" class="mainoption" value="<jforum:i18n key='Moderation.Admin.submit'/>" /></td>
		</tr>
	</table>
	</form>
</c:if>