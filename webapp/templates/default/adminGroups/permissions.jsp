<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>
<style type="text/css">
div.scroll {
	height: 150px;
	overflow: auto;
}
</style>

<script type="text/javascript" src="<jforum:templateResource item='/js/shiftClick.js'/>"></script>

<c:set var="isAdministrator" value="${userSession.roleManager.administrator}"/>

<form accept-charset="${encoding}" id="formPermissions" action="<jforum:url address="/jforum"/>?module=adminGroups&action=permissionsSave" method="post">
<input type="hidden" name="groupId" value="${group.id}">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="3" height="25"><jforum:i18n key='PermissionControl.groupTitle'/> - "<i>${group.name}</i>"</th>
	</tr>

	<!-- ************** -->
	<!-- Administration -->
	<!-- ************** -->
	<c:if test="${isAdministrator}">
		<tr>
			<td class="row3 gen" colspan="3"><b><jforum:i18n key="Permissions.administration"/></b></td>
		</tr>
	
		<tr>
			<td class="row2">&nbsp;</td>
			<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.isAdministrator"/></td>
			<td class="row2 gensmall" valign="middle" align="left">
				<jforum:booleanPermissionSection name="permission.administrator" value="${permissions.administrator}"/>
			</td>
		</tr>
		
		<!-- ***************** -->
		<!-- Co administration -->
		<!-- ***************** -->
		<tr>
			<td class="row3 gen" colspan="3"><b><jforum:i18n key="Permissions.coAdministration"/></b></td>
		</tr>
		
		<tr>
			<td class="row2">&nbsp;</td>
			<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.isCoAdministrator"/></td>
			<td class="row2 gensmall" valign="middle" align="left">
				<jforum:booleanPermissionSection name="permission.coAdministrator" value="${permissions.coAdministrator}"/>
			</td>
		</tr>
		
		<tr>
			<td class="row2">&nbsp;</td>
			<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.canManageForums"/></td>
			<td class="row2 gensmall" valign="middle" align="left">
				<jforum:booleanPermissionSection name="permission.canManageForums" value="${permissions.canManageForums}"/>
			</td>
		</tr>
		
		<tr>
			<td class="row2">&nbsp;</td>
			<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.chooseGroups"/></td>
			<td class="row2 gensmall" valign="middle" align="left">
				<div class="scroll">
					<jforum:choicePermissionSection roleName="group" items="${groups}" name="permission.groups" selected="${permissions.allowedGroups}"/>
				</div>
			</td>
		</tr>
	</c:if>
		
	<!-- ********** -->
	<!-- Categories -->
	<!-- ********** -->
	<tr>
		<td class="row3 gen" colspan="3"><b><jforum:i18n key="Permissions.categories"/></b></td>
	</tr>

	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.chooseCategories"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:choicePermissionSection roleName="category" items="${categories}" name="permission.categories" selected="${permissions.allowedCategories}"/>
		</td>
	</tr>

	<!-- ****** -->
	<!-- Forums -->
	<!-- ****** -->
	<tr>
		<td class="row3 gen" colspan="3"><b><jforum:i18n key="Permissions.forums"/></b></td>
	</tr>

	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.chooseForums"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="permission.forums" selected="${permissions.allowedForums}"/>
			</div>
		</td>
	</tr>
	
	<!-- ********** -->
	<!-- Reply Only -->
	<!-- ********** -->
	<tr>
		<td class="row1 gen" colspan="3"><b><jforum:i18n key="Permissions.replyOnly"/></b></td>
	</tr>

	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.replyOnlyDescription"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="permission.replyOnly" selected="${permissions.replyOnly}"/>
			</div>
		</td>
	</tr>
	
	<!-- ******* -->
	<!-- General -->
	<!-- ******* -->
	<tr>
		<td class="row1 gen" colspan="3"><b><jforum:i18n key="Permissions.general"/></b></td>
	</tr>
	
	<c:if test="${isAdministrator}">
		<!-- Interact with other groups -->
		<tr>
			<td class="row2">&nbsp;</td>
			<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.interactOtherGroups"/></td>
			<td class="row2 gensmall" valign="middle" align="left">
				<jforum:booleanPermissionSection name="permission.canInteractOtherGroups" value="${permissions.canInteractOtherGroups}"/>
			</td>
		</tr>
	</c:if>
	
	<!-- Post only with moderator online -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.postOnlyWithModeratorOnline"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.postOnlyWithModeratorOnline" value="${permissions.postOnlyWithModeratorOnline}"/>
		</td>
	</tr>
	
	<!-- Access to private messages -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.accessToPrivateMessages"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.privateMessageAllowed" value="${permissions.privateMessageAllowed}"/>
		</td>
	</tr>
	
	<!-- Access to user listing -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.accessToUserListing"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.userListingAllowed" value="${permissions.userListingAllowed}"/>
		</td>
	</tr>
	
	<!-- Access to view profiles -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.accessToViewProfiles"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.canViewProfile" value="${permissions.canViewProfile}"/>
		</td>
	</tr>
	
	<!-- Can have profile pictures -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.canHaveProfilePictures"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.canHaveProfilePicture" value="${permissions.canHaveProfilePicture}"/>
		</td>
	</tr>

	<!-- Sticky / Announcements -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.stickyDescription"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.stickyAnnouncement" value="${permissions.canCreateStickyAnnouncement}"/>
		</td>
	</tr>
	
	<!-- Create Poll Topics -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.pollDescription"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.poll" value="${permissions.canCreatePoll}"/>
		</td>
	</tr>
	
	<!-- Vote on Polls -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.pollVoteDescription"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.pollVote" value="${permissions.allowPollVote}"/>
		</td>
	</tr>
	
	<!-- Can only send PM to mdoerators -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.pmOnlyToModerators"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.pmOnlyToModerators" value="${permissions.pmOnlyToModerators}"/>
		</td>
	</tr>
	
	<!-- ********* -->
	<!-- Read Only -->
	<!-- ********* -->
	<tr>
		<td class="row1 gen" colspan="3"><b><jforum:i18n key="Permissions.readOnly"/></b></td>
	</tr>

	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.readOnlyDescription"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="permission.readOnly" selected="${permissions.readOnlyForums}"/>
			</div>
		</td>
	</tr>
	
	<!-- ********************* -->
	<!-- Moderation of replies -->
	<!-- ********************* -->
	<tr>
		<td class="row1 gen" colspan="3"><b><jforum:i18n key="Permissions.moderationReplies"/></b></td>
	</tr>

	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.moderationRepliesDescription"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="permission.moderatedReplies" selected="${permissions.moderatedReplies}"/>
			</div>
		</td>
	</tr>
	
	<!-- **** -->
	<!-- HTML -->
	<!-- **** -->
	<tr>
		<td class="row1 gen" colspan="3"><b><jforum:i18n key="Permissions.html"/></b></td>
	</tr>

	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.htmlDescription"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="permission.html" selected="${permissions.html}"/>
			</div>
		</td>
	</tr>
	
	<!-- *********** -->
	<!-- Attachments -->
	<!-- *********** -->
	<tr>
		<td class="row1 gen" colspan="3"><b><jforum:i18n key="Permissions.attachments"/></b></td>
	</tr>

	<!-- Allowed forums -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.attachmentsDescription"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="permission.attachments" selected="${permissions.attachments}"/>
			</div>
		</td>
	</tr>
	
	<!-- Allow download -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.attachmentsDownload"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="permission.attachmentsDownload" selected="${permissions.downloadAttachments}"/>
			</div>
		</td>
	</tr>
	
	<!-- ********** -->
	<!-- Moderation -->
	<!-- ********** -->
	<tr>
		<td class="row1 gen" colspan="3"><b><jforum:i18n key="Permissions.moderation"/></b></td>
	</tr>
	
	<!-- Is moderator -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.isModerator"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.moderator" value="${permissions.moderator}"/>
		</td>
	</tr>
	
	<!-- Can approve / deny messages -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.moderationApprove"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.canApproveMessages" value="${permissions.canApproveMessages}"/>
		</td>
	</tr>
	
	<!-- Forums allowed to moderate -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.moderateForums"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="permission.moderateForums" selected="${permissions.moderateForums}"/>
			</div>
		</td>
	</tr>
	
	<!-- Remove messages -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.canRemovePosts"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.canRemovePosts" value="${permissions.canRemovePosts}"/>
		</td>
	</tr>
	
	<!-- Edit messages -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.canEdit"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.canEditPosts" value="${permissions.canEditPosts}"/>
		</td>
	</tr>
	
	<!-- Topic moving -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.canMove"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.canMoveTopics" value="${permissions.canMoveTopics}"/>
		</td>
	</tr>
	
	<!-- Topic lock / unlock -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2 gensmall" valign="top" width="33%"><jforum:i18n key="Permissions.canLockUnlock"/></td>
		<td class="row2 gensmall" valign="middle" align="left">
			<jforum:booleanPermissionSection name="permission.canLockUnlock" value="${permissions.canLockUnlock}"/>
		</td>
	</tr>

	<tr align="center">
		<td class="catbottom" colspan="3" height="28"><input class="mainoption" type="submit" value="<jforum:i18n key='PermissionControl.save'/>"></td>
	</tr>
</table>

</form>
  
<script language="javascript">
enhanceCheckboxes(document.getElementById("formPermissions"));
</script>

