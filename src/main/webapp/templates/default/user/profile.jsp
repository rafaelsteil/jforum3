<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:import url="../header.jsp"/>

<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
    <tr>
      <td align="left"><a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a></td>
    </tr>
</table>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" align="center" border="0">
	<tr>
		<th class="thhead" nowrap="nowrap" colspan="2" height="25"><jforum:i18n key='UserProfile.profileFor'/> :: ${fn:escapeXml(user.username)}</th>
	</tr>

	<tr>
		<td class="catleft gen" align="center" width="40%" height="28"><b><jforum:i18n key='UserProfile.avatar'/></b></td>
		<td class="catright gen" align="center" width="60%"><b><jforum:i18n key='UserProfile.allAbout'/> ${fn:escapeXml(user.username)}</b></td>
    </tr>

	<tr>
		<td class="row1" valign="top" align="center">
			<c:if test="${roleManager.canHaveProfilePicture && not empty user.avatar}">
				<c:if test='${not empty user.avatar}'>
					<c:choose>
				      <c:when test="${user.customizeAvatar}">
						<c:set var="avatar_upload_path"><jforum:settings key="avatar.upload.dir"/></c:set>
						<img src="<c:url value='${avatar_upload_path}/${user.avatar }'/>" border="0" alt="[Avatar]" /><br />
				      </c:when>
				      <c:otherwise>
						<c:set var="avatar_gallery_path"><jforum:settings key="avatar.gallery.dir"/></c:set>
						<img src="<c:url value='${avatar_gallery_path}/${user.avatar }'/>" border="0" alt="[Avatar]" /><br />
				      </c:otherwise>
				    </c:choose>
				</c:if>
			</c:if>
			
			<span class="postdetails">
				<c:if test="${user.id != anonymousUserId}">
					${jforum:rankingTitle(rankings, user)}
				</c:if>	
			</span>
		</td>

		<td class="row1" valign="top" rowspan="3">
			<table cellspacing="1" cellpadding="3" width="100%" border="0">
				<tr>
					<td valign="middle" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.registrationDate'/>:&nbsp;</span></td>
					<td width="100%"><b><span class="gen">${user.registrationDate}</span></b></td>
				</tr>
				
				<tr>
					<td valign="top" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.totalMessages'/>:&nbsp;</span></td>
					<td valign="top">
						<c:choose>
							<c:when test='${nposts > 0}'>
								<b><a class="gen" href="<jforum:url address='/posts/listByUser/${user.id}'/>">[${nposts}] <jforum:i18n key='PostShow.userPosts'/> ${fn:escapeXml(user.username)}</a> </b>
							</c:when>
							<c:otherwise>							
								<span class="gen"><jforum:i18n key='PostShow.noUserCreatedPosts'/></span>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td valign="top" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='ForumListing.userCreatedTopics'/>:</span></td>
					<td>
						<c:choose>
							<c:when test='${ntopics > 0}'>
								<b><a class="gen" href="<jforum:url address='/recentTopics/showTopicsByUser/${user.id}'/>">[${ntopics}] <jforum:i18n key='ForumListing.userTopics'/> ${fn:escapeXml(user.username)}</a> </b>
							</c:when>
							<c:otherwise>
								<span class="gensmall"><jforum:i18n key='ForumListing.noUserCreatedTopics'/></span>
							</c:otherwise>				
						</c:choose>
						
						</td>
				</tr>
				
				<c:if test='${not empty user.from}'>
					<tr>
						<td valign="middle" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.from'/>:&nbsp;</span></td>
						<td><b><span class="gen">${fn:escapeXml(user.from)}</span></b></td>
					</tr>
				</c:if>
            
				<c:if test='${not empty user.website}'>
					<tr>
						<td valign="middle" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.webSite'/>:&nbsp;</span></td>
						<td><span class="gen"><b><a href="${user.website}" target="_new">${fn:escapeXml(user.website)}</a></b></span></td>
					</tr>
				</c:if>
            
				<c:if test='${not empty u.occupation}'>
					<tr>
						<td valign="middle" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.occupation'/>:&nbsp;</span></td>
						<td><b><span class="gen">${fn:escapeXml(user.occupation)}</span></b></td>
					</tr>
				</c:if>
            
				<c:if test='${not empty u.interests}'>
					<tr>
						<td valign="top" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.interests'/>:</span></td>
						<td><b><span class="gen">${fn:escapeXml(user.interests)}</span></b></td>
					</tr>
				</c:if>
            
				<c:if test='${not empty u.biography}'>
					<tr>
						<td valign="top" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.biography'/>:</span></td>
						<td><b><span class="gen">${fn:escapeXml(user.biography)}</span></b></td>
					</tr>
				</c:if>
			</table>
		</td>
	</tr>
    
	<tr>
		<td class="catleft" align="center" height="28"><b><span class="gen"><jforum:i18n key='UserProfile.contact'/> ${user.username}</span></b></td>
	</tr>
    
	<tr>
		<td class="row1" valign="top">
			<table cellspacing="1" cellpadding="3" width="100%" border="0">
			<c:if test='${u.viewEmailEnabled && not empty u.email}'>
			<tr>
				<td valign="middle" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.emailAddress'/>:</span></td>
				<td class="row1 gen" valign="middle" width="100%">
					<b>
					<#assign e = u.email.split("@")/>
					<a href="#" onclick="document.location = 'mailto:' + showEmail('{e[0]}', '{e[1]}');"><img src="<jforum:templateResource item='/images/icon_email.gif'/>" border="0" /></a>
					</b>
				</td>
			</tr>
			</c:if>

			<c:if test='${user.id != 1}'>
				<tr>
					<td valign="middle" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='UserProfile.privateMessage'/>:</span></td>
					<td class="row1" valign="middle">
						<b><span class="gen">
						<a href="<jforum:url address='/pm/sendTo/${user.id}'/>" class="icon_pm"><img src="<c:url value='/images/transp.gif'/>"  alt="" /></a>
						</span></b>
					</td>
				</tr>
			</c:if>
            
			<c:if test='${not empty user.msn}'>
				<tr>
					<td valign="middle" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='User.msn'/>:</span></td>
					<td class="row1" valign="middle"><span class="gen">${fn:escapeXml(user.msn)}</span></td>
				</tr>
			</c:if>
            
			<c:if test='${not empty user.yim}'>
				<tr>
					<td valign="middle" nowrap="nowrap" align="right"><span class="gen"><jforum:i18n key='User.yahoo'/>:</span></td>
					<td class="row1" valign="middle"><span class="gen">${fn:escapeXml(user.yim)}</span></td>
				</tr>
			</c:if>
			</table>
		</td>
	</tr>
</table>

<c:import url="../footer.jsp"/>
