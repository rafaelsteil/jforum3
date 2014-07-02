<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<c:set var="saveAction" value="editsave"/>


<c:if test="${empty name}">
	<c:set var="saveAction" value="save"/>
</c:if>


<form accept-charset="${encoding}" name="form" action="<jforum:url address='/jforum'/>?module=adminTag&action=${saveAction}" method="post">

<c:if test="${not empty name}">
  <input type="hidden" name="oldTag" value="${name}"/>
</c:if>


<table class="forumline" cellspacing="1" cellpadding="3" width="100%">
	<tr>
		<th class="thhead" valign="middle" colspan="2" height="25"><jforum:i18n key='Tag.Form.Title'/></th>
	</tr>
        
	<tr>
		<td class="row1 gen" width="38%"><jforum:i18n key='Tag.Form.Name'/></td>
		<td class="row2"><input type="text" name="newTag" value="${name}"/></td>
	</tr>
	
	<tr>
		<td class="catbottom" colspan="2" height="28"><input class="mainoption" type="submit" value="<jforum:i18n key='Tag.Form.Save'/>"/></td>
	</tr>
</table>
</form>
  
