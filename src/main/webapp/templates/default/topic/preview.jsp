<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>

<a name="preview"></a>
<table class="forumline" width="100%" cellspacing="1" cellpadding="4" border="0" id="previewTable">
	<tr>
		<th height="25" class="thhead"><jforum:i18n key='PostForm.preview'/></th>
	</tr>
	<tr>
		<td class="row1" height="100%">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" style="height:100%">
				<tr>
					<td><span class="postbody"><jforum:displayFormattedMessage post="${post}"/></span></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="spacerow" height="1"><img src="<jforum:templateResource item='/images/spacer.gif'/>" alt="" width="1" height="1" /></td>
	</tr>
</table>
