<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
	@import url("<jforum:templateResource item='/tag/css/tagging.css'/>");
</style>
<table width="100%" cellspacing="0" cellpadding="2" border="0" align="center">
	<tbody><tr>
		<td align="left" height="13px"><span class="gensmall"></span><span class="gensmall"></span></td>
	</tr>
	</tbody>
</table>
<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
	<tr>
		<th class="th-title" nowrap="nowrap" align="left">&nbsp;<jforum:i18n key='Tag.hot.tag'/>&nbsp;</th>
	</tr>
	<tr>
		<td>
			<c:forEach items="${tags}" var="entry">
				<span class="tagging-list-item">
					<a href="<jforum:url address="/tag/find/${entry.key}" encode="true"/>" class="${entry.value }">${entry.key}</a>
				</span>
			</c:forEach>	
		</td>
	</tr>
</table>  