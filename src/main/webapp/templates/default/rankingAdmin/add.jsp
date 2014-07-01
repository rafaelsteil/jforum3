<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<script type="text/javascript">
function turnSpecial() {
	if (document.getElementById("rank_special").checked) {
		var e = document.getElementById("rank_min");
		e.disabled = true;
		e.style.backgroundColor = "#cccccc";
		e.value = "";
	}
	else {
		var e = document.getElementById("rank_min");
		e.disabled = false;
		e.style.backgroundColor = "white";
	}
}
</script>

<c:set var="action" value="addSave"/>

<c:if test="${not empty ranking}">
	<c:set var="action" value="editSave"/>
</c:if>

<form accept-charset="${encoding}" name="form" action="<jforum:url address="/adminRankings/${action}"/>" method="post">
	<c:if test="${not empty ranking}">
		<input type="hidden" name="ranking.id" value="${ranking.id}"/>
	</c:if>

	<table class="forumline" cellspacing="1" cellpadding="3" width="100%">
		<tr>
			<th class="thhead" colspan="2" height="25"><jforum:i18n key='Rank.Form.Title'/></th>
		</tr>

		<tr>
			<td class="row1 gen"><jforum:i18n key='Rank.Form.RankName'/></td>
			<td class="row2"><input name="ranking.title" type="text" value="${ranking.title}"/></td>
		</tr>
		
		<tr>
			<td class="row1" width="38%"><span class="gen"><jforum:i18n key='Rank.Form.Special'/></span></td>
			<td class="row2"><input name="ranking.special" value="true" type="checkbox" onClick="turnSpecial();" id="rank_special" <c:if test="${ranking.special}">checked</c:if> /></td>
		</tr>
	
		<tr>
			<td class="row1 gen" width="38%"><jforum:i18n key='Rank.Form.MinValue'/></td>
			<td class="row2"><input name="ranking.min" id="rank_min" type="text" value="${ranking.min}" size="6" maxlength="5"/></td>
		</tr>
	
		<tr align="center">
			<td class="catbottom" colspan="2">
				<input class="mainoption" type="submit" value="<jforum:i18n key='Rank.Form.ClickToUpdate'/>"/>
			</td>
		</tr>
	</table>
</form>


<c:if test="${ranking.special}">
	<script type="text/javascript">turnSpecial();</script>
</c:if>
