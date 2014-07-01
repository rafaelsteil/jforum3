<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<title>Smilies</title>
<style type="text/css">@import url(<jforum:templateResource item="/styles/style.css"/>);</style>
</head>
<body bgcolor="#f7f7f8">

<c:set var="smiliesDir"><c:url value="/"/><jforum:settings key="smilie.image.dir"/></c:set>

<table>
	<tr>
		<td align="center">
			<font class="gen"><jforum:i18n key="PostForm.smilieHelp"/></font>
		</td>
	</tr>

	<tr>	
		<td>
			<table width="100%">
				<tr>
					<td style="line-height: 27px;" valign="top" align="left" width="300px">
						<c:forEach items="${smilies}" var="s">
							<a href="#" onclick="opener.emoticon('${s.code}'); window.focus();"><img src="${smiliesDir}/${s.diskName}"/></a>&nbsp;&nbsp;
						</c:forEach>
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td valign="bottom" align="center">
			<input type="button" value="<jforum:i18n key="closeWindow"/>" onclick="window.close();" class="mainoption" />
		</td>
	</tr>
</table>

</body>
</html>