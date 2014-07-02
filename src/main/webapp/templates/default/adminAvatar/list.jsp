<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form accept-charset="${encoding}" name="form" action="<jforum:url address='/jforum'/>?module=adminAvatar&action=delete" method="post">

<c:set var="allowUloadAvatar"><jforum:settings key="avatar.allow.upload"/></c:set>
<c:set var="allowGalleryAvatar"><jforum:settings key="avatar.allow.gallery"/></c:set>

<c:if test="${allowGalleryAvatar}">
	<c:set var="avatar_gallery_path"><jforum:settings key="avatar.gallery.dir"/></c:set>
	<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
		<tr>
			<th class="thhead" valign="middle" colspan="3" height="25"><jforum:i18n key="Avatars.List.Title.Gallery"/></th>
		</tr>
	
		<c:forEach items="${GalleryAvatars}" var="avatar">
			<tr class="highlight">
				<td class="row1"><img src="<c:url value='${avatar_gallery_path}/${avatar}'/>" width="${avatar.width }" height="${avatar.height }"/></td>
				<td class="row2 gen">
					<a href="<jforum:url address='/adminAvatar/edit/${avatar.id}'/>"><jforum:i18n key="Avatars.List.Edit"/></a>
				</td>
				<td class="row2"><input type="checkbox" name="avatarId" value="${avatar.id}"/></td>
			</tr>
		</c:forEach>
	</table>
	
	<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
		<tr align="center">
			<td class="catbottom" colspan="4" height="28">
				<input class="mainoption" type="button" value="<jforum:i18n key="Avatars.List.ClickToNew"/>" name="button" onClick="document.location = '<jforum:url address='/adminAvatar/add'/>';"/>
			</td>
		</tr>
	</table>
</c:if>

<c:if test="${allowUloadAvatar}">
	<c:set var="avatar_upload_path"><jforum:settings key="avatar.upload.dir"/></c:set>
	<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
		<tr>
			<th class="thhead" valign="middle" colspan="3" height="25"><jforum:i18n key="Avatars.List.Title.Upload"/></th>
		</tr>
	
		<c:forEach items="${UploadedAvatars}" var="avatar">
			<tr class="highlight">
				<td class="row1"><img src="<c:url value='${avatar_upload_path}/${avatar}'/>" width="${avatar.width }" height="${avatar.height }"/></td>
				<td class="row2 gen">
					<a href="<jforum:url address='/adminAvatar/edit/${avatar.id}'/>"><jforum:i18n key="Avatars.List.Edit"/></a>
				</td>
				<td class="row2"><input type="checkbox" name="avatarId" value="${avatar.id}"/></td>
			</tr>
		</c:forEach>
	
	</table>
</c:if>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr align="center">
		<td class="catbottom" colspan="4" height="28">
			<input class="mainoption" type="submit" value="<jforum:i18n key="Avatars.List.ClickToDelete"/>" name="submit"/>
		</td>
	</tr>
</table>

</form>
   
