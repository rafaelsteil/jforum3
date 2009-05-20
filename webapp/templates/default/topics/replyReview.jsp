<%@page errorPage="error.jsp" %>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<jforum:settings key='encoding'/>" />
<meta http-equiv="Pragma" content="no-cache"/>
<meta http-equiv="Expires" content="-1"/>
<script type="text/javascript" src="<jforum:templateResource item='/js/jquery.js'/>"></script>
<script type="text/javascript" src="<jforum:templateResource item='/js/post.js'/>"></script>
<style type="text/css">@import url(<jforum:templateResource item="/styles/style.css"/>);</style>

<c:set var="customCss"><jforum:settings key="custom.css"/></c:set>

<c:if test="${not empty customCss}">
	<style type="text/css">@import url(<c:url value="/${customCss}"/>);</style>
</c:if>

</head>

<body bgcolor="#FFFFFF" text="#000000" link="#01336B" vlink="#01336B">
<span class="gen"><a name="top"></a></span>

<table border="0" cellpadding="3" cellspacing="1" width="100%" class="forumline">
	<tr>
		<th class="thcornerl" width="150" height="26"><jforum:i18n key='PostShow.author'/></th>
		<th class="thcornerr"><jforum:i18n key='PostShow.messageTitle'/></th>
	</tr>

	<c:forEach items="${posts}" var="post" varStatus="info">
		<c:choose>
			<c:when test="${info.count % 2 == 0}">
				<c:set var="rowColor" value="row1"/>
			</c:when>
			<c:otherwise>
				<c:set var="rowColor" value="row2"/>
			</c:otherwise>
		</c:choose>

		<tr>
			<td align="left" valign="top" class="${rowColor}">
				<span class="name"><b>${post.user.username}</b></span>
			</td>
			
			<td class="${rowColor}" height="28" valign="top">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="100%">
							<img src="<jforum:templateResource item="/images/icon_minipost.gif"/>" width="12" height="9" border="0" alt="Post" />
							<span class="postdetails">${post.date}
							<span class="gen">&nbsp;</span>&nbsp;&nbsp;&nbsp;<jforum:i18n key='PostShow.subject'/>: ${post.subject} </span>
						</td>
					</tr>
					<tr>
						<td colspan="2"><hr /></td>
					</tr>
					<tr>
						<td colspan="2">
							<span class="postbody">
								<jforum:displayFormattedMessage post="${post}"/>
							</span>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" height="1" class="spacerow"><img src="<jforum:templateResource item="/images/spacer.gif"/>" alt="" width="1" height="1" /></td>
		</tr>
	</c:forEach>
</table>

<script type="text/javascript">
limitURLSize();
</script>

</body>
</html>