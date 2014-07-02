<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
	<td valign="middle" nowrap="nowrap"> 
		<a href="<jforum:url address='/user/profile/${post.user.id}'/>" class="icon_profile"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>
		<a href="<jforum:url address='/pm/sendTo/${post.user.id}'/>" class="icon_pm"><img src="<c:url value='/images/transp.gif'/>" alt="" /></a>

		<c:if test="${user.isViewEmailEnabled && not empty user.email}">
			<#assign e = user.email.split("@")/>

			<c:if test="${e.length == 2}">
				<a href="javascript:sendEmail('${e[0]}', '${e[1]}');"><img src="<jforum:templateResource item='/images/icon_email.gif'/>" alt="[Email]" /></a>
			</c:if>
		</c:if>
		
		<c:if test="${not empty user.webSite}">
			<a href="${fn:escapeXml(user.webSite)}" target="_new"><img src="<jforum:templateResource item='/images/icon_www.gif'/>" alt="[www]"/></a>
		</c:if>
		
		<c:if test="${not empty user.yim}">
			<a href="http://edit.yahoo.com/config/send_webmesg?.target=${fn:escapeXml(user.yim)}&amp;.src=pg"><img src="<jforum:templateResource item='/images/icon_yim.gif'/>" alt="[Yahoo!]" /></a>
		</c:if>

		<c:if test="${not empty user.aim}">
			<a target="_new" href="https://my.screenname.aol.com/_cqr/login/login.psp"><img src="<jforum:templateResource item='/images/icon_aim.gif'/>" border="0" alt="aim icon"/></a>
		</c:if>
		
		<c:if test="${not empty user.msn}">
			<a href="<jforum:url address='/user/profile/${user.id}'/>"><img src="<jforum:templateResource item='/images/icon_msnm.gif'/>" alt="[MSN]"  /></a>
		</c:if>
	</td>
</tr>
</table>