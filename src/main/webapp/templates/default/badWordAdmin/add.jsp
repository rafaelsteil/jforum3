<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<c:set var="saveAction" value="addSave"/>

<c:if test="${not empty word}">
	<c:set var="saveAction" value="editSave"/>
</c:if>

<form accept-charset="${encoding}" name="form" action="<jforum:url address='/adminBadWord/${saveAction}'/>" method="post">

<c:if test="${not empty word}">
  <input type="hidden" name="word.id" value="${word.id}"/>
</c:if>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%">
	<tr>
		<th class="thhead" valign="center" colspan="2" height="25"><jforum:i18n key='BadWord.Form.Title'/></th>
	</tr>
        
	<tr>
		<td class="row1 gen" width="38%"><jforum:i18n key='BadWord.Form.Word'/></td>
		<td class="row2"><input type="text" name="word.word" value="${word.word}"/></td>
	</tr>
	
	<tr>
		<td class="row1 gen"><jforum:i18n key='BadWord.Form.Replacement'/></td>
		<td class="row2"><input type="text" name="word.replacement" value="${word.replacement}"/></td>
	</tr>
	
	<tr>
		<td class="catbottom" colspan="2" height="28"><input class="mainoption" type="submit" value="<jforum:i18n key='BadWord.Form.Save'/>"/></td>
	</tr>
</table>
</form>
  
