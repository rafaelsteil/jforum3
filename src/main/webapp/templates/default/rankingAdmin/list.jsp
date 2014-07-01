<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form name="form" action="<jforum:url address='/adminRankings/delete'/>" method="post">

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="5" height="25"><jforum:i18n key="Rank.List.Title"/></th>
	</tr>

	<tr>
		<td class='row2 gen'><b><jforum:i18n key="Rank.List.rankName"/></b></td>
		<td class='row2 gen' align="center"><b><jforum:i18n key="Rank.List.minPosts"/></b></td>
		<td class='row2 gen' align="center"><b><jforum:i18n key="Rank.Form.Special"/></b></td>
		<td class='row2 gen' align="center"><b><jforum:i18n key="Action"/></b></td>
		<td class='row2 gen' align="center"><b><jforum:i18n key="Delete"/></b></td>
	</tr>

	<c:forEach items="${rankings}" var="rank">
		<tr class="highlight">
			<td class="row1 gen" width="38%">${rank.title}</td>
			<td width="26%" class="row1 gen" align="center">${rank.min}</td>
			<td width="26%" class="row1 gen" align="center">
				<c:choose>
					<c:when test="${rank.special}">
						<jforum:i18n key="Yes"/>
					</c:when>
					<c:otherwise>
						<jforum:i18n key="No"/>
					</c:otherwise>
				</c:choose>
			</td>
			<td width="26%" class="row1 gen" align="center"><a href="<jforum:url address='/adminRankings/edit?rankingId=${rank.id}'/>"><jforum:i18n key="Rank.List.Edit"/></a></td>
			<td width="10%" class="row1" align="center"><input type="checkbox" name="rankingsId" value="${rank.id}"/></td>
		</tr>
	</c:forEach>

	<tr align="center">
		<td class="catbottom" colspan="5" height="28">
			<input class="mainoption" type="button" value="<jforum:i18n key="Rank.List.ClickToNew"/>" onClick="document.location = '<jforum:url address='/adminRankings/add'/>';"/>
			&nbsp;&nbsp;
			<input class="mainoption" type="submit" value="<jforum:i18n key="Rank.List.ClickToDelete"/>" name="submit"/>
		</td>
	</tr>
</table>

</form>
   
