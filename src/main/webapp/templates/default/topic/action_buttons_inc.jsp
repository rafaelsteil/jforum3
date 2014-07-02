<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="postinfo">
	<div class="date">
		<c:if test="${pagination.thisPage > 1}">
			<c:set var="startPage" value="${pagination.thisPage}/"/>
		</c:if>
		<a href="<jforum:url address='/topics/list/${startPage}${topic.id}'/>#${post.id}">
		<img src="<jforum:templateResource item='/images/icon_minipost_new.gif'/>" alt="[Post New]" /></a>${post.date}
	</div>
	<div class="subject">&nbsp;&nbsp; &nbsp;<strong><jforum:i18n key='PostShow.subject'/>:</strong> <a name="${post.id}">${fn:escapeXml(post.subject)}</a></div>
	<div class="action">
		<c:if test="${!topic.locked && !readonly}">
			<a href="<jforum:url address='/topics/quote?postId=${post.id}'/>" rel="nofollow" class="icon_quote"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>
		</c:if>

		<c:if test="${canEditCurrentMessage}">
			<a href="<jforum:url address='/posts/edit'/>?postId=${post.id}" rel="nofollow" class="icon_edit"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>  
		</c:if>

		<c:if test="${roleManager.canDeletePosts}">
			<a href="<jforum:url address='/posts/delete/'/>?postId=${post.id}" id="delete${post.id}" onclick="return confirmDelete(${post.id});"><img src="<jforum:templateResource item='/images/icon_delete.gif'/>" alt="[Delete]" /></a>  
		</c:if>

		<a class="nav" href="#top"><img src="<jforum:templateResource item='/images/icon_up.gif'/>" alt="[Up]" /></a>
	</div>
</div>