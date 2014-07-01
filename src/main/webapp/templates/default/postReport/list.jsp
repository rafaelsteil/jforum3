<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="pageTitle" scope="request"><jforum:settings key="forum.name"/> - Post abuse report</c:set>
<c:import url="../header.jsp"/>
<script type="text/javascript" src="<jforum:templateResource item='/js/pagination.js'/>"></script>

<script type="text/javascript">
function deleteReport(reportId) {
	if (confirm("<jforum:i18n key="PostReport.confirmDelete"/>")) {
		document.location = "<jforum:url address="/jforum"/>?module=postReport&action=delete&reportId=" + reportId;
	}
}

function loadResolvedReports(url) {
	$("#resolvedReportsRow").hide();
	$("#loadingCell").html('<img src="<jforum:templateResource item="/images/indicator.gif"/>"> <jforum:i18n key="PostShow.loading"/>...');
	$("#loadResolvedReportsMessage").show();
	
	if (!url) {
		url = '<jforum:url address="/jforum"/>?module=postReport&action=listResolved';
	}

	$.get(url, function(data) {
		$("#loadResolvedReportsMessage").hide();
		$("#resolvedReportsRow").html(data).show();
		changePaginationLinks();
	});
}

function changePaginationLinks() {
	$(".pagination a").click(function() {
		loadResolvedReports($(this).attr("href"));
		return false;
	});
}
</script>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline">
			<table cellspacing="2" cellpadding="2" width="100%" align="center">
				<tr>
					<td valign="bottom" align="left" colspan="2">
						<a class="maintitle" href="<jforum:url address='/forums/list'/>"><jforum:i18n key="ForumListing.forumIndex"/></a> &raquo;
						<a class="maintitle" href="<jforum:url address='/postReport/list'/>"><jforum:i18n key="PostReport.listTitle"/></a>
					</td>
				</tr>
			</table>
			
			<table width="100%">
				<!-- Unresolved reports -->
				<tr>
					<td>
						<h3><img src="<jforum:templateResource item="/images/shield_red.png"/>"/> <span style="color: red"><jforum:i18n key="PostReport.unresolvedReports"/></span></h3>
					</td>
				</tr>
				
				<tr>
					<td>
						<table class="forumline" cellspacing="1" cellpadding="2" width="100%" border="0">
							<c:set var="reportHeader">
								<tr>
									<th># <jforum:i18n key="PostReport.postId"/></th>
									<th><jforum:i18n key="PostReport.poster"/></th>
									<th><jforum:i18n key="PostReport.reportDate"/></th>
									<th><jforum:i18n key="PostReport.reportReason"/></th>
									<th><jforum:i18n key="PostReport.reportUser"/></th>
									<th><jforum:i18n key="PostReport.action"/></th>
								</tr>
							</c:set>
							
							${reportHeader}
							<c:set var="showResolveLink" value="true"/>
							<%@ include file="foreachReports_inc.jsp" %>
						</table>
					</td>
				</tr>
				
				<!-- Resolved reports -->
				<tr>
					<td>
						<h3><img src="<jforum:templateResource item="/images/shield_green.png"/>"/> <span style="color: red"><jforum:i18n key="PostReport.resolvedReports"/></span></h3>
					</td>
				</tr>
				
				<tr>
					<td>
						<table class="forumline" cellspacing="1" cellpadding="2" width="100%" border="0">
							<tr id="loadResolvedReportsMessage">
								<td colspan="6" align="center" id="loadingCell">
									<a href="javascript:loadResolvedReports();" class="gen"><img src="<jforum:templateResource item="/images/arrow_right_green.png"/>"/> <jforum:i18n key="PostReport.clickToLoadResolved"/></a>
								</td>
							</tr>
							
							<tr id="resolvedReportsRow" style="display: none;">
							</tr>
						</table>
					</td>
				</tr>
			</table>
			
			
		</td>
	</tr>
</table>

<c:import url="../footer.jsp"/>