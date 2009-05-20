<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
function todo(name) { var todo = document.getElementById("moderationTodo"); todo.name = name; todo.value = "1"; }

function deleteTopic() {
	if (confirm("<jforum:i18n key='Moderation.ConfirmDelete'/>")) {
		//todo("topicRemove");
		document.formModeration.returnUrl.value = "<jforum:url address='/forums/show/${topic.forum.id}'/>";
		document.formModeration.action += "deleteTopics";
		document.formModeration.submit();
	}
}

function moveTopic() {
	//todo("topicMove");
	document.formModeration.action += "askMoveDestination";
	document.formModeration.submit();
}

function lockUnlock(lock) {
	//todo(lock ? "topicLock" : "topicUnlock");
	document.formModeration.action += "lockUnlock";
	document.formModeration.submit();
}
</script>

<c:if test="${roleManager.canDeletePosts}">
	<a href="javascript:deleteTopic();"><img class="icon_topic_delete" src="<c:url value='/images/transp.gif'/>" title="<jforum:i18n key='Delete'/>" alt="" /></a>
</c:if>

<c:if test="${roleManager.canMoveTopics}">
	<a href="javascript:moveTopic();"><img class="icon_topic_move" src="<c:url value='/images/transp.gif'/>" title="<jforum:i18n key='move'/>" alt="" /></a>
</c:if>

<c:if test="${roleManager.canLockUnlockTopics}">
	<c:if test="${topic.locked}">
		<a href="javascript:lockUnlock(false);"><img class="icon_topic_unlock" src="<c:url value='/images/transp.gif'/>" title="<jforum:i18n key='Unlock'/>" alt="" /></a>
	</c:if>
	<c:if test="${!topic.locked}">
		<a href="javascript:lockUnlock(true);"><img class="icon_topic_lock" src="<c:url value='/images/transp.gif'/>" title="<jforum:i18n key='Lock'/>" alt="" /></a>
	</c:if>
</c:if>
