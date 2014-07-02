<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<script language="javascript">
function resize()
{
	var h = document.form.thumbH.value;
	var w = document.form.thumbW.value;
	
	if ((h > 30 && h < 350) && (w > 30 && w < 350)){
		document.getElementById("avatarPreview").style.width = w + "px";                   
		document.getElementById("avatarPreview").style.height = h + "px";          
	}
}

function changeValue(field, value)
{
	var f = eval("document.form." + field)
	f.value = value;
	resize();
}

function testEmail()
{
	if ($("#address").val()) {
		$("#mailButton").val("<jforum:i18n key="Config.Form.SmtpTestSending"/>...").attr("disabled", "disabled");

		var params = {
			sender: $("#sender").val(),
			host: $("#host").val(),
			port: $("#port").val(),
			auth: $("#auth").val(),
			ssl: $("#ssl").val(),
			username: $("#username").val(),
			password: $("#password").val(),
			to: $("#address").val()
		};

		$.ajax({
			type:"POST",
			url:"$contextPath/jforum${extension}?module=ajax&action=sendTestMail",
			data:params,
			dataType:"script",
			global:false
		});
	}
}
</script>

<form action="<jforum:url address='/adminConfig/save'/>" method="post" name="form" id="form">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
<tr>
	<th class="thhead" valign="middle" colspan="2" height="25"><jforum:i18n key='AdminConfig.Title'/></th>
</tr>

<!-- General Settings -->
<tr>
	<td class="catsides" colspan="2"><span class="gen"><b><jforum:i18n key="Config.Form.General"/></b></span></td>
</tr>
<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.ForumURL"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="50" name="p_forum.link" value='${config.getValue("forum.link")}' /></span></td>
</tr>

<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.SiteURL"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="50" name="p_homepage.link" value='${config.getValue("homepage.link")}' /></span></td>
</tr>

<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.forumName"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="50" name="p_forum.name" value='${config.getValue("forum.name")}' /></span></td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.ForumPageTitle"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="50" name="p_forum.page.title" value='${config.getValue("forum.page.title")}' /></span></td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.MetaTag.description"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="50" name="p_forum.page.metatag.description" value='${config.getValue("forum.page.metatag.description")}' /></span></td>
</tr>

<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.MetaTag.keywords"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="50" name="p_forum.page.metatag.keywords" value='${config.getValue("forum.page.metatag.keywords")}' /></span></td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.encoding"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="10" name="p_encoding" value='${config.getValue("encoding")}' /></span></td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.I18n"/></span></td>
	<td class="row1" width="38%">
		<span class="gen">
		<select name="p_i18n.board.default">
			<c:forEach items="${locales}" var="locale">
				<option value="${locale}" <c:if test="${locale == config.getValue$1['i18n.board.default']}">selected</c:if>>${locale}</option>
			</c:forEach>
		</select>
		</span>
	</td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.TopicsPerPage"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="10" name="p_topicsPerPage" value='${config.getValue("topicsPerPage")}' /></span></td>
</tr>

<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.PostsPerPage"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="10" name="p_postsPerPage" value='${config.getValue("postsPerPage")}' /></span></td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.UsersPerPage"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="10" name="p_usersPerPage" value='${config.getValue("usersPerPage")}' /></span></td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.postsNewDelay"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="10" name="p_posts.new.delay" value='${config.getValue("posts.new.delay")}' /></span></td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.htmlTags"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="50" name="p_html.tags.welcome" value='${config.getValue("html.tags.welcome")}' /></span></td>
</tr>

<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.CaptchaDuringRegistration"/></span></td>
	<td class="row2" width="38%">
		<span class="gen">
			<select name="p_captcha.registration">
				<option value="true" <c:if test="${config.getValue$1['captcha.registration'] == 'true'}">selected</c:if>><jforum:i18n key="True"/></option>
				<option value="false" <c:if test="${config.getValue$1['captcha.registration'] == 'false'}">selected</c:if>><jforum:i18n key="False"/></option>
			</select>
		</span>
	</td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.DisableRegistraion"/></span></td>
	<td class="row1" width="38%">
		<span class="gen">
			<select name="p_registration.enabled">
				<option value="true" <c:if test="${config.getValue$1['registration.enabled'] == 'true'}">selected</c:if>><jforum:i18n key="True"/></option>
				<option value="false" <c:if test="${config.getValue$1['registration.enabled'] == 'false'}">selected</c:if>><jforum:i18n key="False"/></option>
			</select>
		</span>
	</td>
</tr>

<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.EnableRSS"/></span></td>
	<td class="row2" width="38%">
		<span class="gen">
			<select name="p_rss.enabled">
				<option value="true" <c:if test="${config.getValue$1['rss.enabled'] == 'true'}">selected</c:if>><jforum:i18n key="True"/></option>
				<option value="false" <c:if test="${config.getValue$1['rss.enabled'] == 'false'}">selected</c:if>><jforum:i18n key="False"/></option>
			</select>
		</span>
	</td>
</tr>

<!-- Avatar -->
<tr>
	<td class="catsides" colspan="2"><span class="gen"><b><jforum:i18n key="Config.Form.avatar"/></b></span></td>
</tr>

<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.avatarSize"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="10" name="p_avatarMaxKbSize" value='${config.getValue("avatarMaxKbSize")}' /></span></td>
</tr>
<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.avatarMaxWidth"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="10"name="p_avatar.maxWidth" value='${config.getValue("avatar.maxWidth")}' onchange="changeValue('thumbW', this.value)"/></span></td>
</tr>
<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.avatarMaxHeight"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="10"name="p_avatar.maxHeight" value='${config.getValue("avatar.maxHeight")}' onchange="changeValue('thumbH', this.value)"/></span></td>
</tr>
<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.avatarPreview"/></span></td>
	<td class="row2" width="38%">
		<input type="hidden" name="thumbH" value='${config.getValue("avatar.maxHeight")}'/>	
		<input type="hidden" name="thumbW" value='${config.getValue("avatar.maxWidth")}'/>
		<div id="avatarPreview" style="align:center; border:1px solid orange;">&nbsp;</div>
	</td>
</tr>

<!-- Mail -->
<tr>
	<td class="catsides" colspan="2"><span class="gen"><b><jforum:i18n key="Config.Form.Mail"/></b></span></td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.Charset"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" size="10" name="p_mail.charset" value='${config.getValue("mail.charset")}' /></span></td>
</tr>
<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.Sender"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="50" name="p_mail.sender" id="sender" value='${config.getValue("mail.sender")}' /></span></td>
</tr>
<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.MailHost"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="50" name="p_mail.smtp.host" id="host" value='${config.getValue("mail.smtp.host")}' /></span></td>
</tr>
<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.MailPort"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="10" name="p_mail.smtp.port" id="port" value='${config.getValue("mail.smtp.port")}' /></span></td>
</tr>
<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.SmtpAuth"/></span></td>
	<td class="row1" width="38%">
		<select name="p_mail.smtp.auth" id="auth">
			<option value="false" <c:if test="${config.getValue$1['mail.smtp.auth'] == 'false'}">selected</c:if>><jforum:i18n key="False"/></option>
			<option value="true" <c:if test="${config.getValue$1['mail.smtp.auth'] == 'true'}">selected</c:if>><jforum:i18n key="True"/></option>
		</select>
	</td>
</tr>
<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.MailSSL"/></span></td>
	<td class="row1" width="38%">
		<select name="p_mail.smtp.ssl" id="ssl">
			<option value="false" <c:if test="${config.getValue$1['mail.smtp.ssl'] == 'false'}">selected</c:if>><jforum:i18n key="False"/></option>
			<option value="true" <c:if test="${config.getValue$1['mail.smtp.ssl'] == 'true'}">selected</c:if>><jforum:i18n key="True"/></option>
		</select>
	</td>
</tr>
<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.SmtpUsername"/></span></td>
	<td class="row2" width="38%"><span class="gen"><input type="text" size="50" name="p_mail.smtp.username" id="username" value='${config.getValue("mail.smtp.username")}' /></span></td>
</tr>
<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.SmtpPassword"/></span></td>
	<td class="row1" width="38%"><span class="gen"><input type="text" name="p_mail.smtp.password" id="password" value='${config.getValue("mail.smtp.password")}' /></span></td>
</tr>
<tr>
	<td class="row1" width="38%"><span class="gen"><font color="green"><jforum:i18n key="Config.Form.SmtpTest"/></font></span></td>
	<td class="row1" width="38%">
		<span class="gen">E-mail: </span> <input type="text" id="address"/>&nbsp;
		<input type="button" value="<jforum:i18n key="Config.Form.SmtpTest"/>" class="mainoption" id="mailButton" onClick="testEmail();"/>
	</td>
</tr>

<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.MessageFormat"/></span></td>
	<td class="row1" width="38%">
		<select name="p_mail.messageFormat">
			<option value="text" <c:if test="${config.getValue$1['mail.messageFormat'] == 'text'}">selected</c:if>>Text</option>
			<option value="html" <c:if test="${config.getValue$1['mail.messageFormat'] == 'html'}">selected</c:if>>HTML</option>
		</select>
	</td>
</tr>

<tr>
	<td class="row2" width="38%"><span class="gen"><jforum:i18n key="Config.Form.EmailNotifyAnswers"/></span></td>
	<td class="row2" width="38%">
		<select name="p_mail.notify.answers">
			<option value="false" <c:if test="${config.getValue$1['mail.notify.answers'] == 'false'}">selected</c:if>><jforum:i18n key="False"/></option>
			<option value="true" <c:if test="${config.getValue$1['mail.notify.answers'] == 'true'}">selected</c:if>><jforum:i18n key="True"/></option>
		</select>
	</td>
</tr>
<tr>
	<td class="row1" width="38%"><span class="gen"><jforum:i18n key="Config.Form.UserEmailAuth"/></span></td>
	<td class="row1" width="38%">
		<select name="p_mail.user.email.auth">
			<option value="false" <c:if test="${config.getValue$1['mail.user.email.auth'] == 'false'}">selected</c:if>><jforum:i18n key="False"/></option>
			<option value="true" <c:if test="${config.getValue$1['mail.user.email.aith'] == 'true'}">selected</c:if>><jforum:i18n key="True"/></option>
		</select>
	</td>
</tr>


<tr>
	<td class="catsides" colspan="2" align="center"><input type="submit" value="<jforum:i18n key="Update"/>" class="mainoption" />
	</td>
</tr>

</table>
</form>

<script language="javascript">resize();</script>
