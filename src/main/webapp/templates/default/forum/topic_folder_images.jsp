<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:remove var="topicUnread"/>

<c:if test="${!userSession.isTopicRead$1[topic]}">
	<c:set var="topicUnread" value="_new"/>
</c:if>

<c:choose>
	<c:when test="${!topic.hasMoved || inSearch || (not empty forum && topic.forum.id == forum.id)}">
		<c:choose>
			<c:when test="${topic.locked}">
				<img class="icon_folder_lock${topicUnread}" src="<c:url value='/images/transp.gif'/>" alt="" />
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${topic.announce}">
						<img class="icon_folder_announce${topicUnread}" src="<c:url value='/images/transp.gif'/>" alt="" />
					</c:when>
					
					<c:when test="${topic.sticky}">
						<img class="icon_folder_sticky${topicUnread}" src="<c:url value='/images/transp.gif'/>" alt="" />
					</c:when>
					
					<c:otherwise>
						<c:choose>
							<c:when test="${topic.totalReplies >= topicHotBegin}">
								<img class="icon_folder${topicUnread}_hot" src="<c:url value='/images/transp.gif'/>" alt="" />
							</c:when>
							<c:otherwise>
								<img class="icon_folder${topicUnread}" src="<c:url value='/images/transp.gif'/>" alt="" />
							</c:otherwise>
						</c:choose>
					</c:otherwise>
					
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<img class="icon_topic_move" src="<c:url value='/images/transp.gif'/>" alt="" />
	</c:otherwise>
</c:choose>
