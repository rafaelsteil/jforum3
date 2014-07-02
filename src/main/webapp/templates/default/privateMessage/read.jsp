<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tbody>
		<tr>
			<td class="bodyline">
				<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0">
                  <tbody>
                    <tr>
                      <td valign="top" align="center" width="100%">
                        <table cellspacing="2" cellpadding="2" border="0">
                          <tbody>
                            <tr valign="middle">
                              <td> <a href="<jforum:url address='/pm/inbox'/>"> <img src="<jforum:templateResource item="/images/msg_inbox.gif"/>" alt="[Inbox]" /> </a></td>
                              <td> <a href="<jforum:url address='/pm/inbox'/>"> <span class="cattitle"><jforum:i18n key='PrivateMessage.inbox'/> &nbsp;</span> </a> </td>
                              <td> <a href="<jforum:url address='/pm/sent'/>"> <img src="<jforum:templateResource item="/images/msg_sentbox.gif"/>" alt="[Sentbox]" />  </a></td>
                              <td> <a href="<jforum:url address='/pm/sent'/>"> <span class="cattitle"><jforum:i18n key='PrivateMessage.sentbox'/></span></a><span class="cattitle"> &nbsp;</span></td>
                            </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                  </tbody>
                </table>
                
				<br clear="all" />
				
				<form action="<jforum:url address='/pm/delete'/>" method="post">
					<table cellspacing="2" cellpadding="2" width="100%" border="0">
						<tbody>
							<tr>
								<td valign="middle"><a href="<jforum:url address='/pm/reply?id=${pm.id}'/>" class="icon_reply"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>
								</td>
								<td width="100%">
									<span class="nav">&nbsp;<a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a></span>
								</td>
							</tr>
						</tbody>
					</table>
					<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
						<tbody>
							<tr>
								<th class="thhead" nowrap="nowrap" colspan="3"><jforum:i18n key='PrivateMessage.inboxMessage'/></th>
							</tr>
							<tr>
								<td class="row2" width="10%"><span class="genmed"><jforum:i18n key='PrivateMessage.from'/>:</span></td>
								<td class="row2" colspan="2"><span class="genmed"><a class="nav" href="<jforum:url address='/user/profile/${pm.fromUser.id}'/>">${pm.fromUser.username}</a></span></td>
							</tr>
							<tr>
								<td class="row2" width="10%"><span class="genmed"><jforum:i18n key='PrivateMessage.to'/>:</span></td>
								<td class="row2" colspan="2"><span class="genmed"><a class="nav" href="<jforum:url address='/user/profile/${pm.toUser.id}'/>">${pm.toUser.username}</a></span></td>
							</tr>
							<tr>
								<td class="row2" width="10%"><span class="genmed"><jforum:i18n key='PrivateMessage.date'/>:</span></td>
								<td class="row2" colspan="2"><span class="genmed">${pm.date}</span></td>
							</tr>
							<tr>
								<td class="row2" width="10%"><span class="genmed"><jforum:i18n key='PrivateMessage.subject'/>:</span></td>
								<td class="row2"><span class="genmed">${post.subject}</span></td>
								<td class="row2" nowrap="nowrap" align="right">
									<a href="<jforum:url address='/pm/quote?id=${pm.id}'/>" class="icon_quote"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>
								</td>
							</tr>
							<tr>
								<td class="row1" valign="top" colspan="3">
									<span class="postbody">
										<jforum:displayFormattedMessage post="${post}"/>
										
										<c:if test="${pm.fromUser.attachSignature && not empty pm.fromUser.signature && post.signatureEnabled}">
											<hr/>
											<span class="gensmall">${pm.fromUser.signature}</span>	
										</c:if>
									</span>
								</td>
							</tr>
							
							<tr>
								<td class="catbottom" align="right" colspan="3" height="28">
									<input type="hidden" value="${pm.id}" name="ids" />
									&nbsp; <input class="liteoption" type="submit" value="<jforum:i18n key='PrivateMessage.removeMessage'/>" />
								</td>
							</tr>
						</tbody>
					</table>
					<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
						<tbody>
							<tr>
								<td><a href="<jforum:url address='/pm/reply?id=${pm.id}'/>" class="icon_reply"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a></td>
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