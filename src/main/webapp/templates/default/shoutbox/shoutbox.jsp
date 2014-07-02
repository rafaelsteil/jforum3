<%@ taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript" src="<jforum:templateResource item='/js/jquery.form.js'/>" ></script>
<script type="text/javascript" src="<jforum:templateResource item='/shoutbox/js/shoutbox.jsp'/>"></script>
<link href="<jforum:templateResource item='/shoutbox/css/shoutbox.css'/>" rel="stylesheet" type="text/css" media="all" />

<form action="<jforum:url address='/shoutbox/shout'/>" name="shoutbox-form" id="shoutbox-form" method="post" >
	<input type="hidden" name="shoutBoxId" id="shoutBoxId" value="${CurrentShoutBox.id }"/>
	<div class="window-container">
	<div class="max-window" id="shout-box" style="display:block;left:300px;width:200px;">
		<div class="title-bar">
			<span class="widown-title"><jforum:i18n key="ShoutBox.SHOUTBOX"/></span>
			<span class="window-button maximize-button" id="button-shout-box"></span>
		</div>
		<div class="topictitle" style="padding:5px;height:18px;display:none;">
			<span id="shoutbox-response"></span>
			<span>
				<jforum:i18n key="ShoutBox.NAME"/>
				<input type="text" id="shout-name" name="shout.shouterName" maxlength="30" size="12" value="${userSession.user.username}" DISABLED />
				<jforum:i18n key="ShoutBox.MESSAGE"/>
				<input type="text" id="shout-message" name="shout.message" maxlength="250" size="35"/> 
				<input type="submit" value="Submit" name="submit" id="submit" class="button" />
			</span>
		</div>
		<div id="shoutbox-list" style="display:none;"></div>
	</div><!--[if lte IE 6.5]><iframe></iframe><![endif]-->
	</div>
</form>