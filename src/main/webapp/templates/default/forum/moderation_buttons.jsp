<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${roleManager.canDeletePosts}">
	<input type="button" value="&nbsp;&nbsp;<jforum:i18n key='Delete'/>&nbsp;&nbsp;" class="liteoption" onclick="return submitModerationDelete();" />
</c:if>

<c:if test="${roleManager.canMoveTopics}">
	<input type="button" name="topicMove" value="&nbsp;&nbsp;<jforum:i18n key='move'/>&nbsp;&nbsp;" class="liteoption" onclick="return submitModerationMove();" />
</c:if>

<c:if test="${roleManager.canLockUnlockTopics}">
	<input type="button" value="&nbsp;&nbsp;<jforum:i18n key='Lock'/>&nbsp;&nbsp;" class="liteoption" onclick="return submitModerationLockUnlock();" />
	<input type="button" value="&nbsp;&nbsp;<jforum:i18n key='Unlock'/>&nbsp;&nbsp;" class="liteoption" onclick="return submitModerationLockUnlock();" />
</c:if>
