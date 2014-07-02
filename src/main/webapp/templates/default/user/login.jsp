<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

<form action="<jforum:url address='/user/authenticateUser'/>" method="post" name="loginform" id="loginform" accept-charset="${encoding}">
  
  <c:if test="${not empty returnPath}">
  	<input type="hidden" name="returnPath" value="${returnPath}" />
  </c:if>
  
<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
	<tr>
		<td class="nav" align="left"><a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a></td>
	</tr>
</table>

<table class="forumline" cellspacing="1" cellpadding="4" width="100%" align="center" border="0">
	<tr>
		<th class="thhead" nowrap="nowrap" height="25"><jforum:i18n key='Login.enterUsername'/></th>
	</tr>

	<tr>
		<td class="row1">
			<table cellspacing="1" cellpadding="3" width="100%" border="0">
				<tr>
					<td align="center" colspan="2">&nbsp;</td>
				</tr>
				
				<c:if test="${invalidLogin}">
					<tr>
						<td align="center" width="100%" colspan="2">
							<span class="gen" id="invalidlogin">
	  							<font color="red"><jforum:i18n key='Login.invalidLogin'/></font>
	  						</span>
	  					</td>
	  				</tr>
				</c:if>
				
				<tr>
					<td align="right" width="45%"><span class="gen"><jforum:i18n key='Login.user'/>:</span></td>
					<td><input class="post" maxlength="40" size="25" name="username" type="text"/> </td>
				</tr>
				
				<tr>
					<td align="right"><span class="gen"><jforum:i18n key='Login.password'/>:</span></td>
					<td><input class="post" type="password" maxlength="25" size="25" name="password" /> </td>
				</tr>

				<c:set var="autoLoginEnabled"><jforum:settings key="auto.login.enabled"/></c:set>

				<c:if test="${autoLoginEnabled}">
					<tr align="center">
						<td colspan="2"><span class="gen"><label for="autologin"><jforum:i18n key='Login.autoLogon'/>: </label><input type="checkbox" id="autologin" name="autoLogin" value="true" /></span></td>
					</tr>
				</c:if>

				<tr align="center">
					<td colspan="2">
						<input type="hidden" name="redirect" />
						<input class="mainoption" type="submit" value="<jforum:i18n key='Login.enter'/>" name="login" />
					</td>
				</tr>

				<tr align="center">
					<td colspan="2" class="gensmall">
						<a href="<jforum:url address='/user/lostPassword'/>"><jforum:i18n key='Login.lostPassword'/></a>
						 | 
						<a href="<jforum:url address='/user/activateManual'/>"><jforum:i18n key='ActivateAccount.activate'/></a>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>

<c:import url="../footer.jsp"/>