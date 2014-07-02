<table cellspacing="0" cellpadding="1" border="0" align="center">
	<tr>
		<td valign="middle" align="center">
			<br />
			<table cellspacing="0" cellpadding="5" width="100" border="0">
				<tr align="center">
					<td class="gensmall" colspan="4"><strong><jforum:i18n key='PostForm.emoticons'/></strong></td>
				</tr>

				<tr>
					<c:forEach items="${smilies}" var="smilie" varStatus="loop">
						<c:if test="${loop.count <= 20}">
							<td>
								<a href="javascript:emoticon('${smilie.code}');"><img src="<c:url value='/images/smilies/${smilie.diskName}'/>"/></a>
							</td>
							
							<c:if test="${loop.count % 4 == 0}">
								</tr><tr>
							</c:if>
						</c:if>
					</c:forEach>
				</tr>
				
				<tr align="center">
					<td colspan="4">
						<span class="nav"><a href="#smilies" onclick="smiliePopup();return false;"><jforum:i18n key='PostForm.moreSmilies'/></a></span>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>