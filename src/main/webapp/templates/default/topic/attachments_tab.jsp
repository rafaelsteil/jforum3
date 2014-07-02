<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td colspan="2" id="tdAttachPanel" align="center">
			<input type="hidden" name="edit_attach_ids" />
			<input type="hidden" name="delete_attach" />
			<input type="hidden" name="total_attachments" id="total_attachments" />

			<table border="0" cellpadding="3" cellspacing="0" width="100%" id="tblAttachments">
				<!--
				<tr>
					<td class="gensmall">
						<b><jforum:i18n key='Attachments.maxToAttach'/>:</b> ${maxAttachments}
						<c:set var="maxSize" value="${maxAttachmentsSize / 1024}"/>
						<c:if test="${maxSize > 1}">
							/ 
							<b><jforum:i18n key='Attachments.maxSize'/>:</b> <font color="red">${maxSize} kb</font>
						</c:if>
					</td>
				</tr>
				-->
				<tr>
					<td align="center">
						<div id="edit_attach"></div>

						<div id="attachmentFields">
						</div>			   
					</td>
				</tr>
				<tr>
					<td align="center" class="row3"><input type="button" name="add_attach" value="<jforum:i18n key='PostForm.AddAnotherFile'/>" class="mainoption" onclick="addAttachmentFields()" /></td>
				</tr>

				<c:if test="${!post.hasAttachments}">
					<script type="text/javascript">addAttachmentFields();</script>
				</c:if>
			</table>
		</td>
	</tr>
</table>

<c:if test="${post.hasAttachments}">
	<script type="text/javascript">editAttachments();</script>
</c:if>