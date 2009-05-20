<#list attachments as a>
	<#assign hasThumb = a.hasThumb()/>

	<table width="70%" border="<#if !hasThumb || thumbShowBox>1<#else>0</#if>" cellpadding="2" cellspacing="0" class="attachtable" align="center">
		<#if hasThumb>
			<tr>
				<td width="100%" colspan="3" align="center" class="attachrow">
					<#if !thumbShowBox>
						<a href="${JForumContext.encodeURL("/posts/downloadAttach/${a.id}")}">
					</#if>
					<img src="${contextPath}/${a.thumbPath()}"" alt="[Thumb - ${a.info.realFilename}]" border="0"/>
					<#if !thumbShowBox></a></#if>
				</td>
			</tr>
		</#if>

		<#if !hasThumb || thumbShowBox>
			<tr>
				<td width="15%" class="attachrow"><span class="genmed">&nbsp;${I18n.getMessage("Attachments.filename")}</span></td>
				<td width="75%" class="attachrow"><span class="genmed">${a.info.realFilename}</span></td>
				
				<td rowspan="4" align="center">
					<img src="${contextPath}/templates/${templateName}/images/icon_disk.gif" alt="[Disk]" />
					<a href="${JForumContext.encodeURL("/posts/downloadAttach/${a.id}")}" class="gensmall"><b>${I18n.getMessage("Attachments.download")}</b></a>
				</td>
			</tr>

			<tr>
				<td width="15%" class="attachrow"><span class="genmed">&nbsp;${I18n.getMessage("Attachments.description")}</span></td>
				<td width="75%" class="attachrow"><span class="genmed">${a.info.comment?default(I18n.getMessage("Attachments.noDescription"))?html}</span></td>
				
			</tr>	

			<tr>
				<td class="attachrow"><span class="genmed">&nbsp;${I18n.getMessage("Attachments.filesize")}</span></td>
				<td class="attachrow">
					<span class="genmed">
						<#if (a.info.filesize < 1024)>
							${a.info.filesize} bytes
						<#else>
							${a.info.filesize / 1024} Kbytes
						</#if>
					</span>
				</td>
			</tr>

			<tr>
				<td class="attachrow"><span class="genmed">&nbsp;${I18n.getMessage("Attachments.totalDownload")}:</span></td>
				<td class="attachrow"><span class="genmed">&nbsp;${a.info.downloadCount} ${I18n.getMessage("Attachments.time")}</span></td>
			</tr>
		</#if>
	</table>
	<br />
</#list>
