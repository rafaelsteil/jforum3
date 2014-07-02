<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>
<style type="text/css">
td {
	font-size: 14px;
}
div.scroll {
	height: 150px;
	overflow: auto;
}
</style>

<script type="text/javascript" src="<jforum:templateResource item='/js/shiftClick.js?3'/>"></script>

<c:set var="isAdministrator" value="${userSession.roleManager.administrator}"/>

<form accept-charset="${encoding}" id="formPermissions" action="<jforum:url address="/adminGroups/permissionsSave"/>" method="post">
<input type="hidden" name="groupId" value="${group.id}"/>

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
			<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.isAdministrator"/></td>
			<td class="row2" valign="middle" align="left">
				<jforum:booleanPermissionSection name="role_b$administrator" value="${roleManager.administrator}"/>
			</td>
		</tr>
		
		<!-- ***************** -->
		<!-- Co administration -->
		<!-- ***************** -->
		<!-- 
		<tr>
			<td class="row3 gen" colspan="3"><b><jforum:i18n key="Permissions.coAdministration"/></b></td>
		</tr>
		
		<tr>
			<td class="row2">&nbsp;</td>
			<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.isCoAdministrator"/></td>
			<td class="row2" valign="middle" align="left">
				<jforum:booleanPermissionSection name="role_b$coAdministrator" value="${roleManager.isCoAdministrator$}"/>
			</td>
		</tr>
		
		<tr>
			<td class="row2">&nbsp;</td>
			<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.canManageForums"/></td>
			<td class="row2" valign="middle" align="left">
				<c:choose>
					<c:when test="${roleManager.roleExists$1['canManageForums']}">
						<jforum:booleanPermissionSection name="role_b$canManageForums" value="${roleManager.get$['canManageForums'].getRoleValues$}"/>
					</c:when>
					<c:otherwise>

					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		
		<tr>
			<td class="row2">&nbsp;</td>
			<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.chooseGroups"/></td>
			<td class="row2" valign="middle" align="left">
				<div class="scroll">
					<jforum:choicePermissionSection roleName="group" items="${groups}" name="role_b$groups" selected="${roleManager.getRoleValuesAsList$1['groups']}"/>
				</div>
			</td>
		</tr>
		-->
	</c:if>
		
	<!-- ********** -->
	<!-- Categories -->
	<!-- ********** -->
	<tr>
		<td class="row3 gen" colspan="3"><b><jforum:i18n key="Permissions.categories"/></b></td>
	</tr>

	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.chooseCategories"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:choicePermissionSection roleName="category" items="${categories}" name="role_m$category" selected="${roleManager.getRoleValuesAsList$['category']}"/>
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
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.chooseForums"/></td>
		<td class="row2" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="role_m$forum" selected="${roleManager.getRoleValuesAsList$['forum']}"/>
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
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.replyOnlyDescription"/></td>
		<td class="row2" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="role_b$forum_reply_only" selected="${roleManager.getRoleValuesAsList$['forum_reply_only']}"/>
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
			<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.interactOtherGroups"/></td>
			<td class="row2" valign="middle" align="left">
				<jforum:booleanPermissionSection name="role_b$interact_other_groups" value="${roleManager.roleExists$1['interact_other_groups']}"/>
			</td>
		</tr>
	</c:if>
	
	<!-- Post only with moderator online -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.postOnlyWithModeratorOnline"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$post_only_with_moderator_online" value="${roleManager.postOnlyWithModeratorOnline}"/>
		</td>
	</tr>
	
	<!-- Access to private messages -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.accessToPrivateMessages"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$private_message" value="${roleManager.privateMessageEnabled}"/>
		</td>
	</tr>
	
	<!-- Access to user listing -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.accessToUserListing"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$user_listing" value="${roleManager.userListingEnabled}"/>
		</td>
	</tr>
	
	<!-- Access to view profiles -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.accessToViewProfiles"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$view_profile" value="${roleManager.canViewProfile}"/>
		</td>
	</tr>
	
	<!-- Can have profile pictures -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.canHaveProfilePictures"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$profile_picture" value="${roleManager.canHaveProfilePicture}"/>
		</td>
	</tr>

	<!-- Sticky / Announcements -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.stickyDescription"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$create_sticky_announcement_topics" value="${roleManager.canCreateStickyAnnouncementTopics}"/>
		</td>
	</tr>
	
	<!-- Create Poll Topics -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.pollDescription"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$poll_create" value="${roleManager.canCreatePolls}"/>
		</td>
	</tr>
	
	<!-- Vote on Polls -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.pollVoteDescription"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$poll_vote" value="${roleManager.canVoteOnPolls}"/>
		</td>
	</tr>
	
	<!-- Can only send PM to mdoerators -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.pmOnlyToModerators"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$pm_only_to_moderators" value="${roleManager.canOnlyContactModerators}"/>
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
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.readOnlyDescription"/></td>
		<td class="row2" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="role_m$forum_read_only" selected="${roleManager.getRoleValuesAsList$['forum_read_only']}"/>
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
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.moderationRepliesDescription"/></td>
		<td class="row2" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="role_m$moderate_replies" selected="${roleManager.getRoleValuesAsList$['moderate_replies']}"/>
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
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.htmlDescription"/></td>
		<td class="row2" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="role_m$html_allowed" selected="${roleManager.getRoleValuesAsList$['html_allowed']}"/>
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
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.attachmentsDescription"/></td>
		<td class="row2" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="role_m$attachments_enabled" selected="${roleManager.getRoleValuesAsList$['attachments_enabled']}"/>
			</div>
		</td>
	</tr>
	
	<!-- Allow download -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.attachmentsDownload"/></td>
		<td class="row2" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="role_m$attachments_download" selected="${roleManager.getRoleValuesAsList$['attachments_download']}"/>
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
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.isModerator"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$moderator" value="${roleManager.moderator}"/>
		</td>
	</tr>
	
	<!-- Can approve / deny messages -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.moderationApprove"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$approve_messages" value="${roleManager.canApproveMessages}"/>
		</td>
	</tr>
	
	<!-- Forums allowed to moderate -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.moderateForums"/></td>
		<td class="row2" valign="middle" align="left">
			<div class="scroll">
				<jforum:categoryForumPermissionSection categories="${categories}" name="role_m$moderate_forum" selected="${roleManager.getRoleValuesAsList$['moderate_forum']}"/>
			</div>
		</td>
	</tr>
	
	<!-- Remove messages -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.canRemovePosts"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$post_delete" value="${roleManager.canDeletePosts}"/>
		</td>
	</tr>
	
	<!-- Edit messages -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.canEdit"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$post_edit" value="${roleManager.canEditPosts}"/>
		</td>
	</tr>
	
	<!-- Topic moving -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.canMove"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$topic_move" value="${roleManager.canMoveTopics}"/>
		</td>
	</tr>
	
	<!-- Topic lock / unlock -->
	<tr>
		<td class="row2">&nbsp;</td>
		<td class="row2" valign="top" width="33%"><jforum:i18n key="Permissions.canLockUnlock"/></td>
		<td class="row2" valign="middle" align="left">
			<jforum:booleanPermissionSection name="role_b$topic_lock_unlock" value="${roleManager.canLockUnlockTopics}"/>
		</td>
	</tr>

	<tr align="center">
		<td class="catbottom" colspan="3" height="28"><input class="mainoption" type="submit" value="<jforum:i18n key='PermissionControl.save'/>"/></td>
	</tr>
</table>

</form>
  
<script language="javascript">
enhanceCheckboxes(document.getElementById("formPermissions"));
</script>

