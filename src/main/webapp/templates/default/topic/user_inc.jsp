<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<span class="genmed"><strong>${fn:escapeXml(post.user.username)}</strong></span>
<br />

<span class="gensmall">
<c:if test="${post.user.id != anonymousUserId}">
	${jforum:rankingTitle(rankings, post.user)}
	<br />
</c:if>

<c:if test="${userSession.roleManager.canHaveProfilePicture && not empty post.user.avatar}">
   <c:choose>
      <c:when test="${post.user.customizeAvatar}">
		<c:set var="avatar_upload_path"><jforum:settings key="avatar.upload.dir"/></c:set>
		<img src="<c:url value='${avatar_upload_path}/${post.user.avatar }'/>" border="0" alt="[Avatar]" /><br />
      </c:when>
      <c:otherwise>
		<c:set var="avatar_gallery_path"><jforum:settings key="avatar.gallery.dir"/></c:set>
		<img src="<c:url value='${avatar_gallery_path}/${post.user.avatar }'/>" border="0" alt="[Avatar]" /><br />
      </c:otherwise>
    </c:choose>
</c:if>
<br /> 

<c:if test="${post.user.id != 1}">
	<jforum:i18n key='PostShow.userRegistrationDate'/>: ${post.user.registrationDate}<br />
	<jforum:i18n key='PostShow.userTotalMessages'/>: ${post.user.totalPosts}
</c:if>
<br />

<c:if test="${not empty post.user.from}">
	<jforum:i18n key='PostShow.userFrom'/>: ${fn:escapeXml(post.user.from)}
	<br />
</c:if>

<c:if test="${not empty post.userIp && roleManager.moderator}">
	<jforum:i18n key='PostShow.userIP'/>: ${post.userIp}
	<br />
</c:if>

<c:if test="${post.user.id != 1}">
	<c:if test="${user.isOnline}">
		<span class="online"><jforum:i18n key='PostShow.userOnline'/></span>
	<#else>
		<span class="offline"><jforum:i18n key='PostShow.userOffline'/> </span>
	</c:if>
 </c:if>
</span>
<br />