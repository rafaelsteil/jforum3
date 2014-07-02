<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>
<script type="text/javascript" src="<jforum:templateResource item='/js/jquery.js'/>"></script>

<style type="text/css">
.bottomLine {
	border-bottom: 1px solid #000;
}

.leftLine {
	border-left: 1px solid #000;
}
</style>

<script type="text/javascript">
function confirmCancel(f) {
	if (confirm("<jforum:i18n key='SearchStats.confirmCancel'/>")) {
		f.submit();
	}
}
</script>

<table border="0" class="forumline gen" width="100%">
	<tr>
		<th colspan="2"><jforum:i18n key='SearchStats.title'/></th>
	</tr>

	<c:if test="${indexExists}">
		<tr>
			<td width="200"><strong><jforum:i18n key='SearchStats.totalPostsInDatabase'/>:</strong> </td>
			<td><i>${totalMessages}</i></td>
		</tr>

		<tr>
			<td class="row3"><strong><jforum:i18n key='SearchStats.indexLocation'/>:</strong></td>
			<td class="row3"><i>${indexLocation}</i></td>
		</tr>
	</c:if>

	<tr>
		<td width="200"><strong><jforum:i18n key='SearchStats.indexExists'/>:</strong></td>
		<td>
			<c:choose>
				<c:when test="${!indexExists}">
					<i><jforum:i18n key='No'/></i>					
				</c:when>
				<c:otherwise>
					<i><jforum:i18n key='Yes'/></i>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	
	<c:if test="${indexExists}">
		<tr>
			<td class="row3"><strong><jforum:i18n key='SearchStats.lastModified'/>:</strong></td>
			<td class="row3"><i>${lastModified}</i></td>
		</tr>

		<tr>
			<td><strong><jforum:i18n key='SearchStats.numberOfDocs'/>:</strong> </td>
			<td><i>${numberOfDocs}</i></td>
		</tr>

		<tr>
			<td><strong><jforum:i18n key='SearchStats.isLocked'/></strong> </td>
			<td>
				<i>
					<c:choose>
						<c:when test="${isLocked}">
							<jforum:i18n key='Yes'/>
						</c:when>
						<c:otherwise>
							<jforum:i18n key='No'/>
						</c:otherwise>
					</c:choose>
				</i>
			</td>
		</tr>

		<tr>
			<td class="row3"><strong><jforum:i18n key='SearchStats.isPostIndexed'/></strong> </td>
			<td class="gensmall row3">
				<jforum:i18n key='SearchStats.enterPostId'/>: 
				<input type="text" id="checkPostId"/>
				<input type="button" value="<jforum:i18n key='SearchStats.check'/>" onClick="checkIfPostExists();"/>
			</td>
		</tr>
	</c:if>
</table>

<br />

<c:if test="${indexExists}">
	<c:choose>
		<c:when test="${!currentlyIndexing}">
			<c:set var="action" value="rebuildIndex"/>
		</c:when>
		<c:otherwise>
			<c:set var="action" value="cancelIndexing"/>
		</c:otherwise>
	</c:choose>

	<form action="<jforum:url address="/jforum"/>">
	<input type="hidden" name="module" value="adminSearchStats"/>
	<input type="hidden" name="action" value="${action}"/>

	<table border="0" class="forumline gen" width="100%">
		<tr>
			<th colspan="2"><jforum:i18n key='SearchStats.reIndex'/></th>
		</tr>

		<c:if test="${currentlyIndexing}">
			<tr>
				<td colspan="2">
					<p>
						<li><strong><jforum:i18n key='SearchStats.currentlyIndexing'/></strong></li>
					</p>

					<p>
						<li><jforum:i18n key='SearchStats.wishToCancel'/> <input type="button" onclick="confirmCancel(this.form);" value="<jforum:i18n key='SearchStats.cancelIndexing'/>" style="font-weight: bold; color: red;" /></li>
					</p>
				</td>
			</tr>
			
			<script type="text/javascript">
				window.setTimeout(function() { document.location = "<jforum:url address='/adminSearchStats/list'/>"; }, 5000);
			</script>
		</c:if>
		
		<c:if test="${!currentlyIndexing}">
			<tr>
				<td colspan="2" align="right" class="cat">
					<input type="submit" value="<jforum:i18n key='SearchStats.start'/>" style="width: 100px; font-weight: bold;" />
				</td>
			</tr>
		</c:if>
	</table>

	</form>
</c:if>
