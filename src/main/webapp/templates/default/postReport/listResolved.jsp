<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="showResolveLink" value="false"/>

<c:set var="paginationData">
	<tr>
		<td colspan="6" align="right" class="row1"><jforum:pagination info="${pagination}" showGotoBox="false"/></td>
	</tr>
</c:set>

<td colspan="6">
	<table width="100%">
		<tr>
			<th># <jforum:i18n key="PostReport.postId"/></th>
			<th><jforum:i18n key="PostReport.poster"/></th>
			<th><jforum:i18n key="PostReport.reportDate"/></th>
			<th><jforum:i18n key="PostReport.reportReason"/></th>
			<th><jforum:i18n key="PostReport.reportUser"/></th>
			<th><jforum:i18n key="PostReport.action"/></th>
		</tr>
		
		${paginationData}
			<%@ include file="foreachReports_inc.jsp" %>
		${paginationData}		
	</table>
</td>
