<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table cellspacing="3" cellpadding="0" border="0">
	<tr>
		<td align="left" width="20"><img class="icon_folder_new" src="<c:url value='/images/transp.gif'/>" alt="[New Folder]" /></td>
		<td class="gensmall bg"><jforum:i18n key="ForumIndex.newMessages"/></td>
		<td>&nbsp;&nbsp;</td>
		<td align="center" width="20"><img class="icon_folder" src="<c:url value='/images/transp.gif'/>" alt="[Folder]" /></td>
		<td class="gensmall bg"><jforum:i18n key="ForumIndex.noNewMessages"/></td>
		<td>&nbsp;&nbsp;</td>
		<td align="center" width="20"><img class="icon_folder_announce" src="<c:url value='/images/transp.gif'/>" alt="[Announce Folder]" /></td>
		<td class="gensmall bg"><jforum:i18n key="ForumIndex.announce"/></td>
	</tr>

	<tr>
		<td align="center" width="20"><img class="icon_folder_new_hot" src="<c:url value='/images/transp.gif'/>" alt="[New Hot]" /></td>
		<td class="gensmall bg"><jforum:i18n key="ForumIndex.newMessagesHot"/></td>
		<td>&nbsp;&nbsp;</td>
		<td align="center" width="20"><img class="icon_folder_hot" src="<c:url value='/images/transp.gif'/>" alt="[Hot]"/></td>
		<td class="gensmall bg"><jforum:i18n key="ForumIndex.noNewMessagesHot"/></td>
		<td>&nbsp;&nbsp;</td>
		<td align="center" width="20"><img class="icon_folder_sticky" src="<c:url value='/images/transp.gif'/>" alt="[Sticky]" /></td>
		<td class="gensmall bg"><jforum:i18n key="ForumIndex.sticky"/></td>
	</tr>

	<tr>
		<td align="center" width="20"><img class="icon_folder_lock_new" src="<c:url value='/images/transp.gif'/>" alt="[Lock New]" /></td>
		<td class="gensmall bg"><jforum:i18n key="ForumIndex.newMessagesBlocked"/></td>
		<td>&nbsp;&nbsp;</td>
		<td align="center" width="20"><img class="icon_folder_lock" src="<c:url value='/images/transp.gif'/>" alt="[Lock]" /></td>
		<td class="gensmall bg"><jforum:i18n key="ForumIndex.noNewMessagesBlocked"/></td>
		<td>&nbsp;&nbsp;</td>
		<td align="center" width="20"><img class="icon_topic_move" src="<c:url value='/images/transp.gif'/>" alt="[Lock]" /></td>
		<td class="gensmall bg"><jforum:i18n key="ModerationLog.typeMoved"/></td>
	</tr>
</table>