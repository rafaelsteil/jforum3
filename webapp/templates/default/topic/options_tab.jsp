<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table cellspacing="0" cellpadding="1" border="0" class="genmed">
	<c:choose>
		<c:when test="${roleManager.isHtmlAllowed$1[forum.id]}">
			<tr>
				<td><input type="checkbox" id="disable_html" name="postOptions.disableHtml" value="true" <c:if test="${!post.htmlEnabled}">checked</c:if> /></td>
				<td><label for="disable_html"><jforum:i18n key='PostForm.disableHtml'/></label></td>
			</tr>
		</c:when>
		<c:otherwise>
			<input type="hidden" name="postOptions.disableHtml" value="true" />
		</c:otherwise>
	</c:choose>
	
	<tr>
		<td><input type="checkbox" id="disable_bbcode" name="postOptions.disableBbCode" value="true" <c:if test="${!post.bbCodeEnabled}">checked</c:if> /> </td>
		<td><label for="disable_bbcode"><jforum:i18n key='PostForm.disableBbCode'/></label></td>
	</tr>
	<tr>
		<td><input type="checkbox" id="disable_smilies" name="postOptions.disableSmilies" value="true" <c:if test="${!post.smiliesEnabled}">checked</c:if> /> </td>
		<td><label for="disable_smilies"><jforum:i18n key='PostForm.disableSmilies'/></label></td>
	</tr>

	<c:if test="${userSession.logged}">
		<tr>
			<td><input type="checkbox" id="attach_sig" name="postOptions.appendSignature" value="true" <c:if test="${post.signatureEnabled}">checked</c:if> /> </td>
			<td><label for="attach_sig"><jforum:i18n key='PostForm.appendSignature'/></label></td>
		</tr>

		<c:if test="${not empty forum && !isEdit}">
			<tr>
				<td><input type="checkbox" id="notify" name="postOptions.notifyReplies" value="true" <c:if test="${userSession.user.notifyReply}">checked</c:if> /> </td>
				<td><label for="notify"><jforum:i18n key='PostForm.notifyReplies'/></label></td>
			</tr>
		</c:if>
	</c:if>

	<c:choose>
		<c:when test="${not empty topic && not empty forum && post.id == topic.firstPost.id && roleManager.canCreateStickyAnnouncementTopics}">
			<tr>
				<td colspan="2">
					<jforum:i18n key='PostForm.setTopicAs'/>:
					<input type="radio" value="0" id="topic_type0" name="postOptions.topicType" <c:if test="${topic.normal}">checked</c:if> /><label for="topic_type0"><jforum:i18n key='PostForm.setTopicAsNormal'/></label>&nbsp;&nbsp;
					<input type="radio" value="1" id="topic_type1" name="postOptions.topicType" <c:if test="${topic.sticky}">checked</c:if> /><label for="topic_type1"><jforum:i18n key='PostForm.setTopicAsSticky'/></label>&nbsp;&nbsp;
					<input type="radio" value="2" id="topic_type2" name="postOptions.topicType" <c:if test="${topic.announce}">checked</c:if> /><label for="topic_type2"><jforum:i18n key='PostForm.setTopicAsAnnounce'/></label>&nbsp;&nbsp;
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<input type="hidden" name="topic.type" value="0" />
		</c:otherwise>
	</c:choose>
</table>