<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<html>
<head>
<title>JForum - Administration interface</title>
</head>

<frameset rows="*" cols="170,*" framespacing="0" frameborder="NO" border="0">
	<frame src="<jforum:url address='/jforum'/>?module=admin&action=menu" name="leftFrame" scrolling="NO" noresize>
	<frame src="<jforum:url address='/jforum'/>?module=admin&action=main" name="main">
</frameset>
</html>
