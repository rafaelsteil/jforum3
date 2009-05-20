<%@page errorPage="error.jsp" %>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<jforum:settings key='encoding'/>" />
<meta http-equiv="Pragma" content="no-cache"/>
<meta http-equiv="Expires" content="-1"/>
<style type="text/css">@import url(<jforum:templateResource item="/styles/style.css"/>);</style>
</head>

<body bgcolor="#FFFFFF" text="#000000" link="#01336B" vlink="#01336B">
<span class="gen"><a name="top"></a></span>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tbody>
		<tr>
			<td class="bodyline">
				<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
					<tbody>
						<tr>
							<th class="thhead" nowrap="nowrap" colspan="3"><jforum:i18n key='PrivateMessage.inboxMessage'/></th>
						</tr>
						<tr>
							<td class="row2 genmed"><jforum:i18n key='PrivateMessage.from'/>:</td>
							<td class="row2 genmed" width="100%" colspan="2"><b>${pm.fromUser.username}</b></td>
						</tr>
						<tr>
							<td class="row2 genmed"><jforum:i18n key='PrivateMessage.to'/>:</td>
							<td class="row2 genmed" width="100%" colspan="2"><b>${pm.toUser.username}</b></td>
						</tr>
						<tr>
							<td class="row2 genmed"><jforum:i18n key='PrivateMessage.date'/>:</td>
							<td class="row2 genmed" width="100%" colspan="2"><b>${pm.date}</b></td>
						</tr>
						<tr>
							<td class="row2 genmed"><jforum:i18n key='PrivateMessage.subject'/>:</td>
							<td class="row2 genmed" width="100%">${pm.subject}</td>
							<td class="row2" nowrap="nowrap" align="right">&nbsp;</td>
						</tr>
						<tr>
							<td class="row1" valign="top" colspan="3">
								<span class="postbody">
									<jforum:displayFormattedMessage post="${post}"/>
								</span>
							</td>
						</tr>
					</tbody>
				</table>
		  </td>
		</tr>
	</tbody>
</table>

</body>
</html>