<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tbody>
		<tr>
			<td class="bodyline">
				<script type="text/javascript">
				function select_switch(status) {
					for (i = 0; i < document.privmsgs.length; i++) {
						document.privmsgs.elements[i].checked = status;
					}
				}
				</script>

				<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0">
					<tbody>
						<tr>
							<td valign="top" align="center" width="100%">
								<table cellspacing="2" cellpadding="2" border="0">
									<tbody>
										<tr valign="middle">
											<td>
												<c:if test="${!inbox}">
													<a href="<jforum:url address='/pm/inbox'/>">
												</c:if>
												
												<img src="<jforum:templateResource item="/images/msg_inbox.gif"/>" border="0" alt="[Inbox]" />
												
												<c:if test="${!inbox}">
													</a>
												</c:if>
											</td>
											<td>
												<c:if test="${!inbox}">
													<a href="<jforum:url address='/pm/inbox'/>">
												</c:if>
												
												<span class="cattitle"><jforum:i18n key='PrivateMessage.inbox'/> &nbsp;</span>
												
												<c:if test="${!inbox}">
													</a>
												</c:if>
											</td>
											<td>
												<c:if test="${!sentbox}">
													<a href="<jforum:url address='/pm/sent'/>">
												</c:if>
												
												<img src="<jforum:templateResource item="/images/msg_sentbox.gif"/>" border="0" alt="[Sent]" />
												
												<c:if test="${!sentbox}">
													</a>
												</c:if>
											</td>
											<td>
												 <c:if test="${!sentbox}">
													<a href="<jforum:url address='/pm/sent'/>">
												</c:if>
												
												<span class="cattitle"><jforum:i18n key='PrivateMessage.sentbox'/>&nbsp;</span>
												
												<c:if test="${!sentbox}">
													</a>
												</c:if>
											</td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
					</tbody>
				</table>
				
				<br clear="all" />
				
				<form action="<jforum:url address='/pm/delete'/>" method="post" name="privmsgs" id="privmsgs">
					
					<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
						<tbody>
							<tr>
								<td valign="middle" align="left">
									<a href="<jforum:url address='/pm/send'/>" class="icon_new_topic"><img src="<c:url value='/images/transp.gif'/>" alt="New Topic" /></a>
								</td>
								<td align="left" width="100%">
									 <a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a>
								</td>
							</tr>
						</tbody>
					</table>
					
					<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
						<tbody>
							<tr>
								<th class="thcornerl" nowrap="nowrap" width="5%" height="25">
									&nbsp;&nbsp;
								</th>
								<th class="thtop" nowrap="nowrap" width="55%">
									&nbsp;<jforum:i18n key='PrivateMessage.subject'/>&nbsp;
								</th>
								<th class="thtop" nowrap="nowrap" width="20%">
									<c:choose>
										<c:when test="${inbox}">
											&nbsp;<jforum:i18n key='PrivateMessage.from'/>&nbsp;										
										</c:when>
										<c:otherwise>
											&nbsp;<jforum:i18n key='PrivateMessage.to'/>&nbsp;
										</c:otherwise>
									</c:choose>
								</th>
								<th class="thtop" nowrap="nowrap" width="15%">
									&nbsp;<jforum:i18n key='PrivateMessage.date'/>&nbsp;
								</th>
								<th class="thcornerr" nowrap="nowrap" width="5%">
									&nbsp;&nbsp;
								</th>
							</tr>
							
							<c:forEach items="${privateMessages}" var="pm">
								<tr>
									<td class="row1" valign="middle" align="center" width="5%">
										<c:choose>
											<c:when test="${pm.isNew$}">
												<img src="<jforum:templateResource item="/images/folder_new.gif"/>" alt="New Folder" />
											</c:when>
											<c:otherwise>
												<img src="<jforum:templateResource item="/images/folder.gif"/>" alt="Folder" />
											</c:otherwise>
										</c:choose>
									</td>
									<td class="row1" valign="middle" width="55%"><span class="topictitle">&nbsp;<a class="topictitle" href="<jforum:url address='/pm/read/${pm.id}'/>">${pm.subject}</a></span></td>
									<td class="row1" valign="middle" align="center" width="20%">
										<c:choose>
											<c:when test="${inbox}">
												<span class="name">&nbsp;<a class="name" href="<jforum:url address='/user/profile/${pm.fromUser.id}'/>">${pm.fromUser.username}</a></span>
											</c:when>
											<c:otherwise>
												<span class="name">&nbsp;<a class="name" href="<jforum:url address='/user/profile/${pm.toUser.id}'/>">${pm.toUser.username}</a></span>	
											</c:otherwise>
										</c:choose>
									</td>
									<td class="row1" valign="middle" align="center" width="15%"><span class="postdetails">${pm.date}</span></td>
									<td class="row1" valign="middle" align="center" width="5%"><span class="postdetails"><input type="checkbox" value="${pm.id}" name="ids" /></span></td>
								</tr>
							</c:forEach>
							
							<tr>
								<td class="catbottom" align="right" colspan="5" height="28">
									&nbsp; <input class="liteoption" type="submit" value="<jforum:i18n key='PrivateMessage.deleteSelected'/>"/>
								</td></tr>
						</tbody>
					</table>
					
					<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
						<tbody>
							<tr>
								<td valign="middle" align="left">
									<a href="<jforum:url address='/pm/send'/>" class="icon_new_topic nav"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>
								</td>
								<td valign="middle" align="left" width="100%">
									<a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a>
								</td>
								<td valign="top" nowrap="nowrap" align="right">
									<b><span class="gensmall"><a class="gensmall" href="javascript:select_switch(true);"><jforum:i18n key='PrivateMessage.checkAll'/></a> :: 
									<a class="gensmall" href="javascript:select_switch(false);"><jforum:i18n key='PrivateMessage.uncheckAll'/></a></span></b>
									<br />
									<span class="nav">
										<br />
									</span>
								</td>
							</tr>
						</tbody>
					</table>
				</form>
				<div align="center"></div>
			</td>
		</tr>
	</tbody>
</table>

<c:import url="../footer.jsp"/>