<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" scope="request"><jforum:settings key="forum.name"/> - ${topic.subject}</c:set>

<c:import url="../header.jsp"/>

<c:set var="canEditSomeMessage" value="false"/>
<c:set var="attachmentsEnabled" value="${roleManager.getCanDownloadAttachments$1[forum.id] || roleManager.isAttachmentsAlllowed$1[forum.id]}"/>

<script type="text/javascript" src="<jforum:templateResource item='/js/post_show.js'/>"></script>
<script type="text/javascript" src="<jforum:templateResource item='/js/post.js'/>"></script>
<script type="text/javascript" src="<jforum:templateResource item='/js/pagination.js'/>"></script>
<script type="text/javascript" src="<jforum:templateResource item='/js/watch.js'/>"></script>
<style type="text/css">@import url( <jforum:templateResource item='/styles/SyntaxHighlighter.css'/> );</style>

<c:if test="${userSession.logged}">
	<script type="text/javascript" src="<jforum:templateResource item='/js/jquery-ui.js'/>"></script>
	<style type="text/css">@import url( <jforum:templateResource item='/styles/ui.core.css'/> );</style>
	<style type="text/css">@import url( <jforum:templateResource item='/styles/ui.dialog.css'/> );</style>
	<style type="text/css">@import url( <jforum:templateResource item='/styles/ui.theme.css'/> );</style>
</c:if>

<script type="text/javascript">
<c:if test="${roleManager.canDeletePosts}">
	function confirmDelete(postId) {
		if (confirm("<jforum:i18n key='Moderation.ConfirmPostDelete'/>")) {
			<c:set var="moderationLogEnabled"><jforum:settings key="moderation.logging.enabled"/></c:set>
		
			<c:if test="${moderationLogEnabled}">
				var reason = prompt("<jforum:i18n key='ModerationLog.changeReason'/>");
	
				if (reason == null || reason == "") {
					alert("<jforum:i18n key='ModerationLog.reasonIsEmpty'/>");
					return false;
				}
				else {
					var link = document.getElementById("delete" + postId);
					link.href += "&log_description=" + encodeURIComponent(reason) + "&log_type=1";
				}
			</c:if>

			return true;
		}
		
		return false;
	}
</c:if>

<c:if test="${userSession.logged}">
	$(document).ready(function() {
		$("#reportAbuse").dialog({
			autoOpen: false, 
			modal: true, 
			resizable: true, 
			title: '<jforum:i18n key="PostReport.reportAbuse"/>', 
			overlay: {
				backgroundColor: '#000',
				opacity: 0.5
			},
			buttons: { 
				'<jforum:i18n key="PostReport.save"/>': function() { sendReportAbuse(); },
				'<jforum:i18n key="PostReport.cancel"/>': function() { $(this).dialog("close"); }
			},
			width: 400,
			height: navigator.userAgent.indexOf("MSIE") > -1 ? 330 : 270,
			open: function() {
				$("#reportAbuse").html(reportAbuseContents);
			},
			close: function() {
				$("#reportAbuse").html("");
			}
		});
	});
	
	function reportAbuse(postId) {
		$("#report_saving").css("display", "none");
		$("#reportAbuse").dialog("open");
		$("#reportSaveStatus").html("");
		$("#reportDescription").val("");
		$("#reportPostId").val(postId);
	}
	
	function sendReportAbuse() {
		$("#report_saving").css("display", "inline");
		$("#reportSaveStatus").html('<jforum:i18n key="PostReport.saving"/>');
		
		$.post('<jforum:url address="/jforum"/>?module=postReport&action=report', 
			{ postId: $("#reportPostId").val(), description: $("#reportDescription").val() }, 
			function() {
				$("#report_saving").css("display", "none");
				$("#reportSaveStatus").html('<jforum:i18n key="PostReport.saved"/>');
				window.setTimeout(function() { $("#reportAbuse").dialog("close"); }, 700);
			});
	}
	
	var reportAbuseContents = '<form>' +
		'	<input type="hidden" id="reportPostId">' +
		'	<label for="reportDescription"><jforum:i18n key="PostReport.reportReason"/></label>' +
		'	<textarea style="height: 85px;" id="reportDescription"></textarea>' +
		'	<img src="<c:url value="/templates/default/images/indicator.gif"/>" style="display: none;" id="report_saving">&nbsp;<span class="gen" id="reportSaveStatus"></span>' +
		'</form>';
</c:if>
</script>

<c:if test="${userSession.logged}">
	<div id="reportAbuse" style="display: none;">
	</div>
</c:if>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline">
			<table cellspacing="2" cellpadding="2" width="100%" border="0">
				<tr>
					<td valign="middle" align="left" colspan="2">
						<span class="maintitle"><a href="<jforum:url address='/topics/list/${topic.id}'/>" name="top" class="maintitle" id="top">${fn:escapeXml(topic.subject)}</a></span>
						<c:if test="${rssEnabled}">
							&nbsp;<a href="<jforum:url address='/rss/topicPosts/${topic.id}'/>"><img src="<jforum:templateResource item='/images/xml_button.gif'/>" border="0" alt="XML" /></a>
						</c:if>
					</td>
				</tr>
			</table>

			<c:set var="actionButtonAndPagination">
				<table cellspacing="2" cellpadding="2" width="100%" border="0">
					<tr>
						<c:set var="canPostReply" value="${!readonly 
							&& (!roleManager.postOnlyWithModeratorOnline || (roleManager.postOnlyWithModeratorOnline && isModeratorOnline))}"/>
							
						<c:choose>
							<c:when test="${topic.locked}">
								<td width="8%" align="left" valign="bottom" nowrap="nowrap">
									<span class="icon_reply_locked"><img src="<c:url value='/images/transp.gif'/>" alt="" /></span>
								</td>
							</c:when>
							<c:when test="${canPostReply}">
								<td width="8%" align="left" valign="bottom" nowrap="nowrap">
									<a href="<jforum:url address='/topics/reply?topicId=${topic.id}'/>" rel="nofollow" class="icon_reply nav"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>
								</td>
							</c:when>
						</c:choose>
	
						<td valign="middle" align="left" class="nav">
							<a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/> </a> 
	            			&raquo; <a class="nav" href="<jforum:url address='/forums/show/${forum.id}'/>">${forum.name} </a>
						</td>
						<td valign="middle" align="right">
							<c:set var="paginationData">
								<jforum:pagination info="${pagination}"/>
							</c:set>
							
							${paginationData}
						</td>
					</tr>
				</table>
			</c:set>
			
			${actionButtonAndPagination}

			<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
				<c:if test="${topic.pollEnabled}">
					<tr>
						<td class="cathead cattitle" align="center" colspan="2" nowrap="nowrap" width="100%"><jforum:i18n key='PostShow.pollTitle'/></td>
					</tr>
					<tr>
						<td class="row1" colspan="2" align="center">
							<c:choose>
								<c:when test="${topic.poll.open && canVoteOnPolls && !viewPollResults}">
									<form action="<jforum:url address='/jforum'/>?module=topics&action=vote" method="post">
									<input type="hidden" name="pollId" value="${topic.poll.id}" />
									<input type="hidden" name="topicId" value="${topic.id}" />
									<div class="poll">
										<span class="strong">${topic.poll.label}</span>
										<table class="poll">
										
											<c:forEach items="${topic.poll.options}" var="option">
												<tr>
													<td><input type="radio" name="optionId" value="${option.id}">${option.text}</input></td>
												</tr>
											</c:forEach>
										</table>
										<input type="submit" value="<jforum:i18n key='PostShow.pollVote'/>"></input><br />
										<span class="gensmall" align="center"><a href="<jforum:url address='/topics/list/${topic.id}'/>?viewPollResults=true"><jforum:i18n key='PostShow.showPollResults'/></a></span>
									</div>
									</form>
								</c:when>
								<c:otherwise>
									<%@ include file="poll_results.jsp" %>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:if>
				
				<tr>
					<th class="thleft" nowrap="nowrap" width="150" height="26"><jforum:i18n key='PostShow.author'/></th>
					<th class="thright" nowrap="nowrap" width="100%"><jforum:i18n key='PostShow.messageTitle'/></th>
				</tr>

				<!-- POST LISTING --> 
				<c:forEach items="${posts}" var="post" varStatus="info">
					<c:set var="canEditCurrentMessage" value="${userSession.logged && (roleManager.administrator 
						|| (roleManager.moderator && roleManager.isForumAllowed$1[topic.forum.id] && roleManager.canEditPosts)
						|| (!topic.locked && post.user.id == userSession.user.id))}"/>
				
					<c:choose>
						<c:when test="${info.count % 2 == 0}">
							<c:set var="rowColor" value="row1"/>
						</c:when>
						<c:otherwise>
							<c:set var="rowColor" value="row2"/>
						</c:otherwise>
					</c:choose>
	
					<tr>
						<td colspan="2">
							<%@include file="action_buttons_inc.jsp"%>
						</td>
					</tr>

					<tr>
						<!-- Username -->
						<c:set var="rowspan" value="2"/>
						<c:set var="useSignature" value="false"/>
						
						<c:if test="${post.user.attachSignature && not empty post.user.signature && post.signatureEnabled}">
							<c:set var="useSignature" value="true"/>
							<c:set var="rowspan" value="3"/>
						</c:if>

						<td class="${rowColor} gen" valign="top" align="left" rowspan="${rowspan}">
							<%@include file="user_inc.jsp"%>
							
							<c:if test="${userSession.logged}">
								<a href="javascript:reportAbuse(${post.id});"><img src="<jforum:templateResource item="/images/report_abuse.gif"/>"> <jforum:i18n key="PostReport.reportAbuse"/></a>
							</c:if>
						</td>
		
						<!-- Message -->
						<td class="${rowColor}" valign="top" id="post_text_${post.id}">
							<span class="postbody">
								<c:choose>
									<c:when test="${canEditCurrentMessage}">
										<c:set var="canEditSomeMessage" value="true"/>
										<div class="edit_area" id="${post.id}"><jforum:displayFormattedMessage post="${post}"/></div>
									</c:when>
									<c:otherwise>
										<jforum:displayFormattedMessage post="${post}"/>
									</c:otherwise>
								</c:choose>
							</span>
							
							<!-- Attachments -->
							<c:if test="${post.hasAttachments && attachmentsEnabled}">
								<%@ include file="show_attachments_inc.jsp" %>
							</c:if>
							
							<c:if test="${post.editCount > 0 && not empty post.editDate}">
								<c:choose>
									<c:when test="${post.editCount == 1}">
										<c:set var="editCountMessage" value="PostShow.editCountSingle"/>
									</c:when>
									<c:otherwise>
										<c:set var="editCountMessage" value="PostShow.editCountMany"/>
									</c:otherwise>
								</c:choose>
								<br/><br/>
								<em><span class="gensmall"><jforum:i18n key='${editCountMessage}' editCount='${post.editCount}' editDate='${post.editDate}'/> </span></em>
							</c:if>
						</td>
					</tr>

					<c:if test="${useSignature}">
						<tr>
							<td colspan="2" class="${rowColor} gensmall" width="100%" height="28"><hr/>
								<jforum:formatSignature signature="${fn:escapeXml(post.user.signature)}"/>
							</td>
						</tr>
					</c:if>
		
					<tr> 
						<td class="${rowColor}" valign="bottom" nowrap="nowrap" height="28" width="100%">
							<c:if test="${post.user.id != anonymousUserId}">
								<%@include file="user_profile_inc.jsp"%>
							</c:if>					
						</td>
					</tr>
		
					<tr>
						<td class="spacerow" colspan="2" height="1"><img src="<jforum:templateResource item='/images/spacer.gif'/>" alt="" width="1" height="1" /></td>
					</tr>
				</c:forEach>
				<!-- END OF POST LISTING -->
		
				<tr align="center">
					<td class="catbottom" colspan="2" height="28">
						<table cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td align="center"><span class="gensmall">&nbsp;</span></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		
			${actionButtonAndPagination}
			
			<table width="100%" align="center">
				<c:if test="${canPostReply}">
					<tr>
						<td colspan="3">
							<script type="text/javascript">
							function activateQuickReply() {
								$("#quickReply").slideToggle('slow', function() {
									window.scrollBy(0, 1000);
								});
							}
		
							function validatePostForm(f) {
								if (f["post.text"].value.replace(/^\s*|\s*$/g, "").length == 0) {
									alert("<jforum:i18n key='PostForm.textEmpty'/>");
									f.message.focus();
								
									return false;
								}
							
								$("#icon_saving").css("display", "inline");
								$("#btnSubmit").attr("disabled", "disabled").val("<jforum:i18n key='PostForm.saving'/>...");
							
								return true;
							}
							</script>

							<form action="<jforum:url address='/topics/replySave'/>" method="post" name="post" id="post" onsubmit="return validatePostForm(this);" accept-charset="${encoding}">
								<input type="hidden" name="topic.forum.id" value="${forum.id}" />
								<input type="hidden" name="topic.id" value="${topic.id}" />
								<input type="hidden" name="postOptions.disableHtml" value="true" />
								
								<c:set var="replySubject"><jforum:i18n key="Message.replyPrefix"/> ${fn:replace(topic.subject, '"', '')} </c:set>
								<input type="hidden" name="post.subject" value="${replySubject}"/>
								
								<c:if test="${userSession.logged}">
									<c:if test="${userSession.user.attachSignature}">
										<input type="hidden" name="postOptions.appendSignature" value="true">
									</c:if>
									
									<c:if test="${user.session.notifyReply}">
										<input type="hidden" name="postOptions.notifyReplies" value="true">
									</c:if>
								</c:if>

								<table width="100%">
									<tr>
										<td align="center">
											<img src="<jforum:templateResource item='/images/icon_mini_message.gif'/>" align="middle" alt="Message" />
											<span class="nav"><a href="javascript:activateQuickReply()"><jforum:i18n key='PostForm.quickReply'/></a></span>
										</td>
									</tr>
								</table>
								
								<p align="center" style="display: none;" id="quickReply">
									<table>
										<tr>
											<td align="center">
												<textarea class="post" style="width: 350px" name="post.text" rows="10" cols="35" onkeyup="enterText(this);" onclick="enterText(this);" onselect="enterText(this);" onblur="leaveText();"></textarea>
											</td>
										</tr>
										<c:if test="${needCaptcha}">
											<tr>
												<td>
													<img border="0" align="middle" id="captcha_img"/>
													<br />
													<span class="gensmall"><jforum:i18n key='User.captchaResponse'/></span>
													<input type="text" class="post" style="width: 80px; font-weight: bold;" maxlength="25" name="captcha_anwser" /> 
													<br />
													<span class="gensmall"><jforum:i18n key='User.hardCaptchaPart1'/> <a href="#newCaptcha" onClick="newCaptcha()"><b><jforum:i18n key='User.hardCaptchaPart2'/></b></a></span>
												</td>
											</tr>
										</c:if>
										<tr>
											<td align="right" valign="center">
												<input type="submit" id="btnSubmit" value="<jforum:i18n key='PostForm.submit'/>" class="mainoption" />
												<img src="<c:url value='/images/transp.gif'/>" id="icon_saving">
											</td>
										</tr>
									</table>
								</p>
							</form>
							</p>
						</td>
					</tr>
				</c:if>

				<c:if test="${roleManager.moderator}">
					<form action="<jforum:url address='/jforum'/>?module=moderation&action=" method="post" name="formModeration" id="formModeration">
					<input type="hidden" name="returnUrl" value="<jforum:url address='${pagination.baseUrl}/${pagination.thisPage}/${topic.id}'/>" />
					<input type="hidden" name="topicIds" value="${topic.id}" />
					<input type="hidden" name="forumId" value="${forum.id}" />

					<tr>
						<td align="left" colspan="3">
							<%@ include file="moderation_images.jsp" %>
						</td>
					</tr>
					</form>
				</c:if>
			</table>

			<table cellspacing="0" cellpadding="0" width="100%" border="0">
				<tr>
					<td align="left" valign="top" class="gensmall">
						<c:if test="${userSession.logged}">
							<c:choose>
								<c:when test="${isUserWatchingTopic}">
									<a href="<jforum:url address='/topics/unwatch/${pagination.thisPage}/${topic.id}'/>">
									<img src="<jforum:templateResource item='/images/unwatch.gif'/>" align="middle" alt="Watch" />&nbsp;<jforum:i18n key="PostShow.unwatch"/></a>
								</c:when>
								<c:otherwise>
									<a href="#watch" onClick="watchTopic('<jforum:url address='/topics/watch/${pagination.thisPage}/${topic.id}'/>', '<jforum:i18n key='PostShow.confirmWatch'/>');">
									<img src="<jforum:templateResource item='/images/watch.gif'/>" align="middle" alt="Watch" />&nbsp;<jforum:i18n key="PostShow.watch"/></a>
								</c:otherwise>
							</c:choose>
						</c:if>
					</td>
					<td align="right">
						<%@include file="../forums_combo.jsp"%>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<a name="quick"></a>

<%@ include file="../highlighter.jsp" %>

<c:import url="../footer.jsp"/>