<c:choose>
	<c:when test="${empty reports}">
		<tr>
			<td colspan="6" align="center" class="gen"><jforum:i18n key="PostReport.noReports"/></td>
		</tr>
	</c:when>
	<c:otherwise>
		<c:forEach items="${reports}" var="report">
			<tr class="highlight">
				<td class="row1 gen" align="center">${report.post.id}</td>
				<td class="row1 gen" align="center"><a href="<jforum:url address="/user/profile/${report.post.user.id}"/>">${report.post.user.username}</a></td>
				<td class="row1 gen" align="center">${report.date}</td>
				<td class="row1 gen">${report.description}</td>
				<td class="row1 gen" align="center"><a href="<jforum:url address="/user/profile/${report.user.id}"/>">${report.user.username}</a></td>
				<td class="row1 gen" align="center" nowrap>
					[<a href="<jforum:url address="/topics/preList/${report.post.topic.id}/${report.post.id}"/>"><jforum:i18n key="PostReport.viewMessage"/></a>]
					
					<c:if test="${showResolveLink}">
						[<a href="<jforum:url address="/jforum"/>?module=postReport&action=resolve&reportId=${report.id}"><jforum:i18n key="PostReport.resolve"/></a>]
					</c:if>
					
					[<a href="javascript:deleteReport(${report.id});"><jforum:i18n key="PostReport.deleteReport"/></a>]
				</td>
			</tr>
		</c:forEach>	
	</c:otherwise>
</c:choose>
