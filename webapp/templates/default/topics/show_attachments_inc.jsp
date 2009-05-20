<c:set var="attachmentsDir"><jforum:settings key="attachments.upload.dir"/></c:set>

<c:forEach items="${post.attachments}" var="a">
	<c:set var="tableBorder" value="1"/>
	
	<c:if test="${a.hasThumb}">
		<c:set var="tableBorder" value="1"/>
	</c:if>

	<table width="70%" border="${tableBorder}" cellpadding="2" cellspacing="0" class="attachtable" align="center">
		<c:if test="${a.hasThumb}">
			<tr>
				<td width="100%" colspan="3" align="center" class="attachrow">
					<img src="<c:url value="/${attachmentsDir}/${a.physicalFilename}_thumb"/>" alt="[Thumb - ${a.realFilename}]" border="0"/>
				</td>
			</tr>
		</c:if>

		<tr>
			<td width="15%" class="attachrow genmed">&nbsp;<jforum:i18n key="Attachments.filename"/></td>
			<td width="75%" class="attachrow genmed">${a.realFilename}</td>
			
			<td rowspan="4" align="center">
				<img src="<jforum:templateResource item="/images/icon_disk.gif"/>" alt="[Disk]" />
				<a href="<jforum:url address="/topics/downloadAttachment/${a.id}"/>" class="gensmall"><b><jforum:i18n key="Attachments.download"/></b></a>
			</td>
		</tr>

		<tr>
			<td width="15%" class="attachrow genmed">&nbsp;<jforum:i18n key="Attachments.description"/></td>
			<td width="75%" class="attachrow genmed">${a.description}</td>
		</tr>	

		<tr>
			<td class="attachrow genmed">&nbsp;<jforum:i18n key="Attachments.filesize"/></td>
			<td class="attachrow genmed">
				<c:choose>
					<c:when test="${a.filesize < 1024}">
						<fmt:formatNumber type="number" maxFractionDigits="2" value="${a.filesize}"/> bytes
					</c:when>
					<c:otherwise>
						<fmt:formatNumber type="number" maxFractionDigits="2" value="${a.filesize / 1024}"/> kbytes
					</c:otherwise>
				</c:choose>
			</td>
		</tr>

		<tr>
			<td class="attachrow genmed">&nbsp;<jforum:i18n key="Attachments.totalDownload"/>:</td>
			<td class="attachrow genmed">&nbsp;${a.downloadCount} <jforum:i18n key="Attachments.time"/></td>
		</tr>
	</table>
	<br />
</c:forEach>
