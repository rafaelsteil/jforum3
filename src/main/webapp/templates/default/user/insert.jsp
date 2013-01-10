<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

<script type="text/javascript">
function checkemail(str) {
    var filter=/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i
    return filter.test(str);
}

function validateRegistrationForm(f) {
	if (f["user.username"].value == "") {
		alert("<jforum:i18n key='User.fillUsername'/>");
		f["user.username"].focus();
		
		return false;
	}
	
	if (f["user.email"].value == "" || !checkemail(f["user.email"].value)) {
        alert("<jforum:i18n key='User.fillEmail'/>");
        f["user.email"].focus();

        return false;
    }
	
	if (f["user.password"].value == "") {
		alert("<jforum:i18n key='User.fillPassword'/>");
		f["user.password"].focus();
		
		return false;
	}
	
	if (f.password_confirm.value == "") {
		alert("<jforum:i18n key='User.fillPasswordConfirmation'/>");
		f.password_confirm.focus();
		
		return false;
	}
	
	if (f["user.password"].value != f.password_confirm.value) {
		alert("<jforum:i18n key='User.passwordNotMatch'/>");
		return false;
	}
	
	return true;
}

function newCaptcha() {
	document.getElementById("captcha_img").src = "<jforum:url address='/jforum'/>?module=captcha&action=regenerate&timestamp=" + new Date().getTime();
}
</script>
<form id="formregister" accept-charset="${encoding}" name="formregister" action="<jforum:url address='/user/insertSave'/>" method="post" onsubmit="return validateRegistrationForm(this);">

<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
	<tr>
		<td align="left">
			<span class="nav"><a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a></span>
		</td>
	</tr>
</table>
		
<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="middle" colspan="2" height="25"><jforum:i18n key='User.registerInformation'/></th>
	</tr>
        
	<tr>
		<td class="row2" colspan="2" align="center"><span class="gensmall"><font color="red"><jforum:i18n key='User.requiredFields'/></font></span></td>
	</tr>
        
	<tr>
		<td class="row1 gen" width="38%" align="right"><jforum:i18n key='User.user'/>: *</td>
		<td class="row2"><input class="post" type="text" style="WIDTH: 200px" maxlength="25" size="25" name="user.username" value="${user.username}"/></td>
	</tr>
        
	<tr>
		<td class="row1" align="right"><span class="gen"><jforum:i18n key='User.emailAddress'/>: *</span></td>
		<td class="row2"><input class="post" type="text" style="WIDTH: 200px" maxlength="255" size="25" name="user.email" value="${user.email}"/></td>
	</tr>
        
	<tr>
		<td class="row1" align="right"><span class="gen"><jforum:i18n key='User.password'/>: *</span></td>
		<td class="row2"><input name="user.password" type="password" value="${user.password}" class="post" id="password" style="WIDTH: 200px" size="25" maxlength="100" /> </td>
	</tr>
        
	<tr>
		<td class="row1" align="right"><span class="gen"><jforum:i18n key='User.confirmPassword'/>: * </span></td>
		<td class="row2"><input class="post" style="WIDTH: 200px" type="password" value="${user.password}" maxlength="100" size="25" name="password_confirm" /> </td>
	</tr>

	<c:if test="${captcha_reg}">
		<tr>
			<td class="row1" width="38%" valign="top"><span class="gen"><jforum:i18n key='User.captchaResponse'/>: *</span></td>
			<td class="row2">
				<input class="post" type="text" style="WIDTH: 200px; font-weight: bold;" maxlength="25" size="25" name="captchaResponse" /> 
				<p><img src="<jforum:url address='/captcha/generate/${timestamp}'/>" border="0" align="top" alt="Captcha unavailable" id="captcha_img"/></p>
				<span class="gensmall"><jforum:i18n key='User.hardCaptchaPart1'/> <a href="#newCaptcha" onClick="newCaptcha()"><b><jforum:i18n key='User.hardCaptchaPart2'/></b></a></span>
			</td>
		</tr>
	</c:if>
						
	<c:if test="${not empty error}">
		<tr>
			<td class="row2" colspan="2" align="center"><span class="gen"><font color="#ff0000"><b><jforum:i18n key="${error}"/></b></font></span></td>
		</tr>
	</c:if>
        
	<tr align="center">
		<td class="catbottom" colspan="2" height="28">
			<input class="mainoption" type="submit" value="<jforum:i18n key='User.submit'/>" name="submit" />&nbsp;&nbsp;
			<input class="liteoption" type="reset" value="<jforum:i18n key='User.reset'/>" name="reset" />
		</td>
	</tr>
</table>
</form>

<c:import url="../footer.jsp"/>
