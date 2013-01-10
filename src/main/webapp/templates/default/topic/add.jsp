<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:import url="../header.jsp"/>

<c:set var="attachmentsEnabled" value="${userSession.roleManager.isAttachmentsAlllowed$1[forum.id]}"/>

<script type="text/javascript">
var CONTEXTPATH = "<c:url value='/'/>";
var SERVLET_EXTENSION  = "<jforum:settings key='servlet.extension'/>"; 
</script>

<style type="text/css">@import url( <jforum:templateResource item="/styles/tabs.css"/> );</style>
<style type="text/css">@import url( <jforum:templateResource item="/styles/SyntaxHighlighter.css"/> );</style>

<script type="text/javascript" src="<jforum:templateResource item='/js/jquery.js'/>"></script>
<script type="text/javascript" src="<jforum:templateResource item='/js/post.js'/>"></script>
 
<script type="text/javascript">
<%@include file="../js/bbcode_help.js"%>

<c:if test="${attachmentsEnabled}">
	<%@include file="../js/attachments.js"%>
</c:if>
</script>

<script type="text/javascript">
var i18nDeletePollOption = '<jforum:i18n key="PostForm.pollDeleteOption"/>';
var i18nPollOption = '<jforum:i18n key="PostForm.pollOption"/>';

function validatePostForm(f) {
	if (f["post.subject"].value == "") {
		alert("<jforum:i18n key='PostForm.subjectEmpty'/>");
		f["post.subject"].focus();
		
		return false;
	}
	
	if (createPollOptionsForSubmit) {
		createPollOptionsForSubmit();
	}
	
	if (f["post.text"].value.replace(/^\s*|\s*$/g, "").length == 0) {
		alert("<jforum:i18n key='PostForm.textEmpty'/>");
		f["post.text"].focus();
		
		return false;
	}

	<c:if test="${isPrivateMessage}">
	if (f.toUsername.value == "") {
		alert("<jforum:i18n key='PrivateMessage.toUserIsEmpy'/>");
		f.toUsername.focus();

		return false;
	}
	</c:if>

	<c:if test="${logModeration}">
	if (f.log_description.value == "") {
		alert("<jforum:i18n key='ModerationLog.reasonIsEmpty'/>");
		f.log_description.focus();

		return false;
	}
	</c:if>
	
	$("#icon_saving").css("display", "inline");
	$("#btnPreview").attr("disabled", "disabled");
	$("#btnSubmit").attr("disabled", "disabled").val("<jforum:i18n key='PostForm.saving'/>...");
	
	return true;
}

function openFindUserWindow() {
	var w = window.open("<jforum:url address='/pm/findUser'/>", "_findUser", "height=250,resizable=yes,width=400");
	w.focus();
}

function smiliePopup() {
	var w = window.open("<jforum:url address='/jforum'/>?module=topics&action=listSmilies", "smilies", "width=300, height=300, toolbars=no, scrollbars=yes");
	w.focus();
}
</script>

<c:set var="saveAction" value="addSave"/>
<c:set var="saveModule" value="topics"/>

<c:choose>
	<c:when test="${isPrivateMessage}">
		<c:set var="saveModule" value="pm"/>
		<c:set var="saveAction" value="sendSave"/>
	</c:when>
	<c:when test="${isReply}">
		<c:set var="saveAction" value="replySave"/>
	</c:when>
	<c:when test="${isEdit}">
		<c:set var="saveModule" value="posts"/>
		<c:set var="saveAction" value="editSave"/>
	</c:when>
</c:choose>
<form action="<jforum:url address='/${saveModule}/${saveAction}'/>" method="post" enctype="multipart/form-data" name="post" id="post" onSubmit="return validatePostForm(this)">

<c:if test="${!isPrivateMessage}">
	<input type="hidden" name="topic.forum.id" value="${forum.id}" />
</c:if>

<c:if test="${isEdit}">
	<input type="hidden" name="post.id" value="${post.id}" />
	<input type="hidden" name="post.topic.id" value="${post.topic.id}" />
</c:if>

<c:if test="${isReply}">
	<input type="hidden" name="topic.id" value="${topic.id}" />
</c:if>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline">
			<div id="previewMessage"></div>

			<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
				<tr>
					<td align="left">
						<span class="nav">
							<a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a>

							<c:if test="${!isPrivateMessage}">
								&raquo; <a class="nav" href="<jforum:url address='/forums/show/${forum.id}'/>">${forum.name}</a>
								
								<c:if test="${isReply || isEdit}">
									&raquo; <a class="nav" href="<jforum:url address='/topics/list/${topic.id}'/>">${topic.subject}</a>
								</c:if>
							</c:if>
						</span>
					</td>
				</tr>
			</table>
		
			<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
				<tr>
					<th class="thhead" colspan="2" height="25">
						<strong>
						<c:if test="${!isPrivateMessage}">
							<c:choose>
								<c:when test="${isReply || isEdit}">
									<c:if test="${isEdit}">
										<jforum:i18n key='PostForm.edit'/> "${topic.subject}"
									</c:if>
									
									<c:if test="${isReply}">
										<jforum:i18n key='PostForm.reply'/> "${topic.subject}"
									</c:if>
								</c:when>
								<c:otherwise>
									<jforum:i18n key='PostForm.title'/>
								</c:otherwise>
							</c:choose>
						</c:if>
						
						<c:if test="${isPrivateMessage}">
							<c:choose>
								<c:when test="${isPrivateMessageReply}">
									<jforum:i18n key='PrivateMessage.reply'/>
								</c:when>
								<c:otherwise>
									<jforum:i18n key='PrivateMessage.title'/>
								</c:otherwise>
							</c:choose>
						</c:if>
						</strong>
					</th>
				</tr>

				<c:if test="${isPrivateMessage}">
					<c:set var="toUserId" value="0"/>
					<c:set var="toUsername" value=""/>
					
					<c:choose>
						<c:when test="${isPrivateMessageReply || isPrivateMessageQuote}">
							<c:set var="toUserId" value="${pm.fromUser.id}"/>
							<c:set var="toUsername" value="${pm.fromUser.username}"/>
						</c:when>
						<c:when test="${not empty pmRecipient}">
							<c:set var="toUserId" value="${pmRecipient.id}"/>
							<c:set var="toUsername" value="${pmRecipient.username}"/>
						</c:when>
					</c:choose>
					
					<tr>
						<td class="row1" width="15%"><span class="gen"><strong><jforum:i18n key='PrivateMessage.user'/></strong></span></td>
						<td class="row2" width="85%">
							<input type="text" class="post" size="25" name="toUsername" value="${toUsername}"/>&nbsp;
							<input type="button" value="<jforum:i18n key='PrivateMessage.findUser'/>" name="findUser" class="liteoption" onclick="openFindUserWindow(); return false;" />
							<input type="hidden" name="toUserId" value="${toUserId}" />
						</td>
					</tr>
				</c:if>

				<c:if test="${not empty errorMessage}">
					<tr>
						<td colspan="2" align="center" class="gen"><font color="#ff0000"><strong>${errorMessage}</strong></font></td>
					</tr>
				</c:if>

				<tr>
					<td class="row1 gen" width="15%" align="right">
						<strong><jforum:i18n key='PostForm.subject'/></strong>
					</td>
					
					<c:choose>
						<c:when test="${isReply}">
							<c:set var="subject"><jforum:i18n key="Message.replyPrefix"/> ${topic.subject}</c:set>
						</c:when>
						<c:when test="${isPrivateMessageReply || isPrivateMessageQuote}">
							<c:set var="replyPrefix"><jforum:i18n key="Message.replyPrefix"/></c:set>
							
							<c:choose>
								<c:when test="${fn:contains(pm.subject, replyPrefix)}">
									<c:set var="subject">${pm.subject}</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="subject">${replyPrefix} ${pm.subject}</c:set>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test="${not empty post.subject}">
							<c:set var="subject" value="${post.subject}"/>
						</c:when>
					</c:choose>
					
					<td class="row2" width="85%">
						<input class="subject" type="text" tabindex="2" maxlength="100" name="post.subject" value="${subject}" /> 
					</td>
				</tr>

				<tr>
					<!-- Smilies -->
					<td class="row1" valign="top" align="right">
						<span class="gen"><strong><jforum:i18n key='PostForm.body'/></strong></span>
						<%@ include file="smilies.jsp" %>
					</td>

					<!-- BB Codes, textarea -->
					<td class="row2" valign="top">
						<div class="gen">
							<table cellspacing="0" cellpadding="2" border="0" width="100%">
								<!-- bb code -->
								<%@ include file="bbcode.jsp" %>
								
								<!-- Color, Fonts -->
								<%@ include file="colors_fonts.jsp" %>

								<!-- Help box -->
								<tr>
									<td>
										<input name="helpbox" class="helpline" readonly="readonly" style="FONT-SIZE: 10px; WIDTH: 100%" value="<jforum:i18n key='PostForm.helplineTip'/>" size="45" maxlength="100" /> 
									</td>
								</tr>
								
								<!-- Textarea -->
								<c:if test="${isEdit || isQuote || isPrivateMessageQuote}">
									<c:choose>
										<c:when test="${isPrivateMessageQuote}">
											<c:set var="text" value='[quote="${pm.fromUser.username}"]${pm.text}[/quote]'/>
										</c:when>
										<c:when test="${isEdit}">
											<c:set var="text" value="${post.text}"/>
										</c:when>
										<c:when test="${isQuote}">
											<c:set var="text" value='[quote="${post.user.username}"]${post.text}[/quote]'/>
										</c:when>										
									</c:choose>
								</c:if>
								
								<tr>
									<td valign="top">
										<textarea class="message" onkeyup="storeCaret(this);" onclick="storeCaret(this);" onselect="storeCaret(this);" tabindex="3" name="post.text" rows="15"  cols="35">${text}</textarea> 
									</td>
								</tr>
							</table>
						</div> 
					</td>
				</tr>

				<!-- Options -->
				<tr>
					<td class="row1">&nbsp;</td>
					<td class="row2">
						<div id="tabs10">
							<ul>
							    <li target="postOptions" class="current"><a href="javascript:void(0);" onClick="activateTab('postOptions', this);"><span>Options</span></a></li>

								<c:if test="${roleManager.canCreatePolls}">
									<li target="postPoll"><a href="javascript:void(0);" onClick="activateTab('postPoll', this);"><span>Poll</span></a></li>
								</c:if>
								
								<c:if test="${attachmentsEnabled}">
								    <li target="postAttachments"><a href="javascript:void(0);" onClick="activateTab('postAttachments', this);"><span>Attachments</span></a></li>
								</c:if>
							</ul>
						</div>

						<!-- Post Options -->
						<div id="postOptions" class="postTabContents">
							<%@include file="options_tab.jsp"%>
						</div>

						<!-- Poll tab -->
						<c:if test="${roleManager.canCreatePolls && (isNewTopic || (isEdit && post.id == topic.firstPost.id))}">
							<div id="postPoll" class="postTabContents" style="display: none;">
								<%@include file="poll_tab.jsp"%>
							</div>
						</c:if>

						<!-- Attachments tab -->
						<c:if test="${attachmentsEnabled || post.hasAttachments}">
							<div id="postAttachments" class="postTabContents" style="display: none; ">
								<%@include file="attachments_tab.jsp"%>
							</div>
						</c:if>
					</td>
				</tr>

				<c:if test="${needCaptcha}">
					<tr>
						<td class="row1" valign="middle"><span class="gensmall"><strong><jforum:i18n key='User.captchaResponse'/>:</strong></span></td>
						<td class="row2">
							<input type="text" class="post" style="width: 100px; font-weight: bold;" maxlength="25" size="25" name="captcha_anwser" /> 
							<img src="<jforum:url address='/captcha/generate/${timestamp}'/>" border="0" align="middle" id="captcha_img" alt="Captcha unavailable" />
							<br />
							<span class="gensmall"><jforum:i18n key='User.hardCaptchaPart1'/> <a href="#newCaptcha" onClick="newCaptcha()"><strong><jforum:i18n key='User.hardCaptchaPart2'/></strong></a></span>
						</td>
					</tr>
				</c:if>

				<c:if test="${logModeration}">
					<tr>
						<td align="center" class="row1 gen"><strong><jforum:i18n key='ModerationLog.moderationLog'/></strong></td>
						<td class="row2 genmed"><jforum:i18n key='ModerationLog.changeReason'/> <input type="text" name="log_description" size="50" /><input type="hidden" name="log_type" value="2" /></td>
					</tr>
				</c:if>

				<c:if test="${not empty error}">
					<tr>
						<td class="row1">&nbsp;</td>
						<td class="row2"><span class="gen"><font color="red"><strong>${error}</strong></font></span></td>
					</tr>
				</c:if>
				
				<tr>
					<td align="center" height="28" colspan="2" class="catbottom">
						<input class="mainoption" id="btnPreview" tabindex="5" type="button" value="<jforum:i18n key='PostForm.preview'/>" onclick="previewMessage();" />&nbsp;
						<input class="mainoption" id="btnSubmit" accesskey="s" tabindex="6" type="submit" value="<jforum:i18n key='PostForm.submit'/>"/>
						<img src="<c:url value='/images/transp.gif'/>" id="icon_saving">
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<c:if test="${isReply || isEdit || isPrivateMessageReply}">
		<tr>
		<td colspan="2">
			<table border="0" cellpadding="3" cellspacing="0" width="100%" class="forumline">
				<tr>
					<th class="cathead" height="28" align="center"><strong><span class="cattitle"><jforum:i18n key='PostShow.topicReview'/></span></strong></th>
				</tr>
		
				<tr>
					<td class="row1">
						<c:choose>
							<c:when test="${isPrivateMessageReply}">
								<iframe width="100%" height="300" frameborder="0" src="<jforum:url address='/pm/review'/>?id=${pm.id}"></iframe>
							</c:when>
							<c:otherwise>
								<iframe width="100%" height="300" frameborder="0" src="<jforum:url address='/topics/replyReview'/>?topicId=${topic.id}" ></iframe>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</td>
		</tr>
	</c:if>
</table>

</form>

<c:import url="../footer.jsp"/>