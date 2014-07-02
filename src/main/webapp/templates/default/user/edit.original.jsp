<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

<script type="text/javascript">
function validateForm() {
	var f = document.post;

	if (f.new_password.value != f.password_confirm.value) {
		alert("<jforum:i18n key='User.passwordNotMatch'/>");
		return false;
	}

	return true;
}

//<#include "js/utils.js"/>
</script>

<form action="<jforum:url address="/jforum"/>?module=user&action=editSave" method="post" enctype="multipart/form-data" name="post" id="post" accept-charset="${encoding}" onsubmit="return validateForm();">
<input type="hidden" name="user.id" value="${user.id}" />

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline">

			<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
				<!-- 
				<#assign message = ""/>
				<#if editDone?default(false)>
					<#assign message = I18n.getMessage("User.editDone")/>
				<#elseif warns?exists>
					<#list warns as w>
						<#assign message = message + w + "<br />"/>
					</#list>
				</#if>

				<#if (message?length > 0)>
				<tr>
					<td class="row2" align="center" colspan="2">
						<span class="gens">
							<b><font color="<#if warns?exists>red<#else>green</#if>">${message}</font></b>
						</span>
					</td>
				</tr>
				</#if>
				 -->
				 
				<tr>
					<td colspan="2"><a class="forumlink" href="<jforum:url address="/forums/list"/>"><jforum:i18n key='ForumListing.forumIndex'/></a></td>
				</tr>

				<tr>
					<th class="thhead" valign="middle" align="center" colspan="2" height="25"><jforum:i18n key='User.registerInformation'/></th>
				</tr>
				<tr>
					<td class="row2 gensmall" colspan="2" align="center"><font color="red"><jforum:i18n key='User.requiredFields'/></font></td>
				</tr>
			
				<tr>
					<td class="row1" width="38%"><span class="gen"><jforum:i18n key='User.user'/>: </span></td>
					<td class="row2 gen">
						<c:choose>
							<c:when test="${userSession.roleManager.administrator && !sso}">
								<input class="post" type="text" name="user.username" value="${user.username}" />
							</c:when>
							<c:otherwise>
								${user.username}
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			
				<tr>
					<td class="row1 gen"><jforum:i18n key='User.emailAddress'/>: *</td>
					<td class="row2 gen">
						<c:choose>
							<c:when test="${!sso}">
								<input type="text" class="post" style="WIDTH: 200px" maxlength="255" size="25" name="user.email" value="${user.email}" />
							</c:when>
							<c:otherwise>
								${user.email}
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			
			<c:if test="${!sso}"> <%-- enable change password when not sso --%>
				<c:if test="${!userSession.roleManager.administrator}">
					<tr>
						<td class="row1">
							<span class="gen"><jforum:i18n key='User.currentPassword'/>: *</span><br />
							<span class="gensmall"><jforum:i18n key='User.needConfirmPassword'/></span>
						</td>
						<td class="row2"><input name="current_password" type="password" class="post" id="current_password" style="WIDTH: 200px" size="25" maxlength="100" /> </td>
					</tr>
				</c:if>

				<tr>
					<td class="row1">
						<span class="gen"><jforum:i18n key='User.newPassword'/>: *</span><br />
						<span class="gensmall"><jforum:i18n key='User.newPasswordIsOptional'/></span>
					</td>
					<td class="row2"><input class="post" style="WIDTH: 200px" type="password" maxlength="100" size="25" name="new_password" /> </td>
				</tr>
			
				<tr>
					<td class="row1">
						<span class="gen"><jforum:i18n key='User.confirmPassword'/>: * </span><br />
						<span class="gensmall"><jforum:i18n key='User.newPasswordIsOptional'/></span>
					</td>
					<td class="row2"><input class="post" style="WIDTH: 200px" type="password" maxlength="100" size="25" name="password_confirm" /> </td>
				</tr>
			</c:if>

				<tr>
					<th class="thsides" valign="middle" colspan="2" height="25"><jforum:i18n key='User.preferencesInfo'/></th>
				</tr>

				<tr>
					<td class="row2" colspan="2" align="center"><span class="gensmall"><font color="green"><jforum:i18n key='User.infoWillBePublicVisible'/></font></span></td>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.aim'/>:</span></td>
					<td class="row2"><input type="text" class="post" style="WIDTH: 300px" maxlength="255" name="user.aim" value="${user.aim}" /> </td>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.msn'/>:</span></td>
					<td class="row2"><input type="text" class="post" style="WIDTH: 300px" maxlength="255" name="user.msn" value="${user.msn}" /> </td>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.yahoo'/>:</span></td>
					<td class="row2"><input type="text" class="post" style="WIDTH: 300px" maxlength="255" name="user.yim" value="${user.yim}" /> </td>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.www'/>:</span></td>
					<td class="row2"><input type="text" class="post" style="WIDTH: 300px" maxlength="255" size="25" name="user.website" value="${user.website}" /> </td>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.from'/>:</span></td>
					<td class="row2"><input type="text" class="post" style="WIDTH: 300px" maxlength="100" size="25" name="user.from" value="${user.from}" /> </td>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.job'/>:</span></td>
					<td class="row2"><input type="text" class="post" style="WIDTH: 300px" maxlength="100" size="25" name="user.occupation"  value="${user.occupation}" /> </td>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.interests'/>:</span></td>
					<td class="row2"><input type="text" class="post" style="WIDTH: 300px" maxlength="150" size="35" name="user.interests" value="${user.interests}" /> </td>
				</tr>
				<tr>
					<td class="row1">
						<span class="gen"><jforum:i18n key='User.biography'/>:</span><br />
					</td>
					<td class="row2"><textarea class="post" style="WIDTH: 300px" name="user.biography" rows="8" cols="30">${user.biography}</textarea></td>
				</tr>
				<tr>
					<td class="row1">
						<span class="gen"><jforum:i18n key='User.signature'/>:</span><br />
						<span class="gensmall"><jforum:i18n key='User.signatureDef'/></span><br />
					</td>
					<td class="row2"><textarea class="post" style="WIDTH: 300px" name="user.signature" rows="6" cols="30">${user.signature}</textarea></td>
				</tr>
				<tr>
					<th class="thsides" valign="middle" colspan="2" height="25"><jforum:i18n key='User.preferences'/></th>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.showEmail'/>:</span></td>
					<td class="row2">
						<input type="radio" value="true" name="user.viewEmailEnabled" <c:if test="${user.viewEmailEnabled}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp;
						 
						<input type="radio" value="false" name="user.viewEmailEnabled" <c:if test="${!user.viewEmailEnabled}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>
				
				<tr>
					<td class="row1">
						<span class="gen"><jforum:i18n key='User.sendPostReplyNotify'/>:</span><br />
						<span class="gensmall"><jforum:i18n key='User.sendPostReplyNotifyDescription'/></span>
					</td>
					<td class="row2">
						<input type="radio" value="true" name="user.notifyReply" <c:if test="${user.notifyReply}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp;
						 
						<input type="radio" value="false" name="user.notifyReply" <c:if test="${!user.notifyReply}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>

				<tr>
					<td class="row1">
						<span class="gen"><jforum:i18n key='User.notifyAlways'/>:</span><br />
						<span class="gensmall"><jforum:i18n key='User.notifyAlwaysDescription'/></span>
					</td>
					<td class="row2">
						<input type="radio" value="true" name="user.notifyAlways" <c:if test="${user.notifyAlways}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp; 
						
						<input type="radio" value="false" name="user.notifyAlways" <c:if test="${!user.notifyAlways}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>

				<tr>
					<td class="row1">
						<span class="gen"><jforum:i18n key='User.notifyText'/>:</span>
					</td>
					<td class="row2">
						<input type="radio" value="true" name="user.notifyText" <c:if test="${user.notifyText}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp;
						 
						<input type="radio" value="false" name="user.notifyText" <c:if test="${!user.notifyText}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>

				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.privateMessagesNotify'/>:</span></td>
					<td class="row2">
						<input type="radio" value="true" name="user.notifyPrivateMessages" <c:if test="${user.notifyPrivateMessages}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp; 
						
						<input type="radio" value="false" name="user.notifyPrivateMessages" <c:if test="${!user.notifyPrivateMessages}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>
				
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.alwaysAttachSignature'/>:</span></td>
					<td class="row2">
						<input type="radio" value="true" name="user.attachSignature" <c:if test="${user.attachSignature}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp; 
						
						<input type="radio" value="false" name="user.attachSignature" <c:if test="${!user.attachSignature}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.allowHtml'/>:</span></td>
					<td class="row2">
						<input type="radio" value="true" name="user.htmlEnabled" <c:if test="${user.htmlEnabled}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp; 
						
						<input type="radio"value="false" name="user.htmlEnabled" <c:if test="${!user.htmlEnabled}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>

				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.allowBbCode'/>:</span></td>
					<td class="row2">
						<input type="radio" value="true" name="user.bbCodeEnabled" <c:if test="${user.bbCodeEnabled}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp; 
						
						<input type="radio" value="false" name="user.bbCodeEnabled" <c:if test="${!user.bbCodeEnabled}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>

				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.allowSmilies'/>:</span></td>
					<td class="row2">
						<input type="radio" value="true" name="user.smiliesEnabled" <c:if test="${user.smiliesEnabled}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.yes'/></span>&nbsp;&nbsp; 
						
						<input type="radio" value="false" name="user.smiliesEnabled" <c:if test="${!user.smiliesEnabled}">checked</c:if>/> 
						<span class="gen"><jforum:i18n key='User.no'/></span>
					</td>
				</tr>
			
				<tr>
					<td class="row1"><span class="gen"><jforum:i18n key='User.langPreference'/>:</span></td>
					<td class="row2">
						<select name="user.lang">
							<option value="" <c:if test="${empty user.lang}">selected</c:if>><jforum:i18n key='User.Lang.default'/></option>
							<%-- 
							<option value="en_US" <#if u.lang == "en_US">selected="selected"</#if>><jforum:i18n key='User.Lang.en_US'/></option>
							<option value="zh_TW" <#if u.lang == "zh_TW">selected="selected"</#if>><jforum:i18n key='User.Lang.zh_TW'/></option>
							<option value="zh_CN" <#if u.lang == "zh_CN">selected="selected"</#if>><jforum:i18n key='User.Lang.zh_CN'/></option>							
							<option value="fr_FR" <#if u.lang == "fr_FR">selected="selected"</#if>><jforum:i18n key='User.Lang.fr_FR'/></option>
							<option value="de_DE" <#if u.lang == "de_DE">selected="selected"</#if>><jforum:i18n key='User.Lang.de_DE'/></option>
							<option value="nl_NL" <#if u.lang == "nl_NL">selected="selected"</#if>><jforum:i18n key='User.Lang.nl_NL'/></option>
							<option value="pt_PT" <#if u.lang == "pt_PT">selected="selected"</#if>><jforum:i18n key='User.Lang.pt_PT'/></option>
							<option value="pt_BR" <#if u.lang == "pt_BR">selected="selected"</#if>><jforum:i18n key='User.Lang.pt_BR'/></option>
							<option value="ru_RU" <#if u.lang == "ru_RU">selected="selected"</#if>><jforum:i18n key='User.Lang.ru_RU'/></option>
							<option value="fi_FI" <#if u.lang == "fi_FI">selected="selected"</#if>><jforum:i18n key='User.Lang.fi_FI'/></option>
							<option value="vi_VN" <#if u.lang == "vi_VN">selected="selected"</#if>><jforum:i18n key='User.Lang.vi_VN'/></option>
							<option value="es_ES" <#if u.lang == "es_ES">selected="selected"</#if>><jforum:i18n key='User.Lang.es_ES'/></option>
							<option value="pl_PL" <#if u.lang == "pl_PL">selected="selected"</#if>><jforum:i18n key='User.Lang.pl_PL'/></option>
							<option value="nb_NO" <#if u.lang == "nb_NO">selected="selected"</#if>><jforum:i18n key='User.Lang.nb_NO'/></option>
							<option value="it_IT" <#if u.lang == "it_IT">selected="selected"</#if>><jforum:i18n key='User.Lang.it_IT'/></option>
							<option value="hu_HU" <#if u.lang == "hu_HU">selected="selected"</#if>><jforum:i18n key='User.Lang.hu_HU'/></option>
							--%>
						</select>
					</td>
				</tr>

				<c:if test="${userSession.roleManager.administrator || userSession.roleManager.coAdministrator}">
					<tr>
						<td class="row1"><span class="gen"><jforum:i18n key='User.specialRanking'/>:</span></td>
						<td class="row2">
							<select name="rankingId">
								<option value=""><jforum:i18n key='User.noSpecialRanking'/></option>

								<c:forEach items="${rankings}" var="ranking">
									<c:if test="${ranking.special}">
										<option value="${ranking.id}" <c:if test="${not empty user.ranking && ranking.id == user.ranking.id}">selected</c:if>>${ranking.title}</option>
									</c:if>
								</c:forEach>
							</select>
						</td>
					</tr>
				</c:if>
				
				<c:if test="${userSession.roleManager.canHaveProfilePicture}">
					<tr>
						<th class="thsides" valign="middle" colspan="2" height="12"><jforum:i18n key='User.avatarControlPanel'/></th>
					</tr>
					<tr>
						<td class="row1" colspan="2">
						<table cellspacing="2" cellpadding="0" width="70%" align="center" border="0" style="margin-left:0;">
							<tr>
								<td width="40%"><span class="gensmall"><jforum:i18n key='User.avatarDesc'/></span></td>
								<td align="left">
									<c:if test="${not empty user.avatar}">
										<span class="gensmall"><jforum:i18n key='User.currentAvatar'/></span><br />
										<br />

									    <c:choose>
									    	<c:when test="${user.customizeAvatar}">
												<c:set var="avatar_upload_path"><jforum:settings key="avatar.upload.dir"/></c:set>
												<img src="<c:url value='${avatar_upload_path}/${user.avatar}'/>" border="0" alt="[Avatar]" /><br />
									      </c:when>
											<c:otherwise>
												<c:set var="avatar_gallery_path"><jforum:settings key="avatar.gallery.dir"/></c:set>
												<img src="<c:url value='${avatar_gallery_path}/${user.avatar}'/>" border="0" alt="[Avatar]" /><br />
											</c:otherwise>
									    </c:choose>										
										<%-- 
										<script type="text/javascript"> 
										var avatarBkp = "";
										
										function clearAvatar(f) {
											if (f.avatardel.checked) {
												avatarBkp = f.avatarUrl.value;
												f.avatarUrl.value = '';
											}
											else {
												f.avatarUrl.value = avatarBkp;
											}
										}
										</script>

										<input type="checkbox" name="avatardel" onclick="clearAvatar(this.form)"/>&nbsp;
							
										<span class="gensmall"><jforum:i18n key='User.removeAvatar'/></span>--%>
									</c:if>
								</td>
							</tr>
						</table>
					</td>
					</tr>
	
					<c:set var="allowUloadAvatar"><jforum:settings key="avatar.allow.upload"/></c:set>
					<c:set var="allowGalleryAvatar"><jforum:settings key="avatar.allow.gallery"/></c:set>
	
					<c:if test="${userSession.roleManager.administrator || allowGalleryAvatar}">
						<c:set var="avatar_gallery_path"><jforum:settings key="avatar.gallery.dir"/></c:set>
						<c:set var="cols"><jforum:settings key="avatar.display.width"/></c:set>
						
						<tr><td colspan="2">
							<h3>
								<jforum:i18n key='User.avatarLocalGallery'/>
							</h3>
							
							<div id="gallery" style="max-height:250px;overflow-y:scroll;">
								<input type="radio" name="avatarId" value="" id="noAvatar"/> <label for="noAvatar"><jforum:i18n key="User.noAvatar"/></label>
								
								<c:forEach items="${avatars}" var="avatar" varStatus="status">
									<label for="av-${status.count}" ${(status.count>cols && status.count%cols == 1 )?"style='clear:left'":"" }>
										<img alt="" src="<c:url value='${avatar_gallery_path}/${avatar}'/>"/><br/>
										<input type="radio" value="${avatar.id }" id="av-${status.count}" name="avatarId"/>
									</label>
								</c:forEach>
							</div>
						</td></tr>
					</c:if>
	
					<c:if test="${userSession.roleManager.administrator || allowUloadAvatar}">
						<tr>
							<td class="row1">
								<span class="gen"><jforum:i18n key='User.avatarFromPc'/>:</span>
							</td>
							<td class="row2">
								<input style="WIDTH: 200px;" type="file" name="uploadfile" id="avatar"/>
							</td>
						</tr>
					</c:if>
				</c:if>

				<tr>
					<td class="catbottom" align="center" colspan="2" height="28">
						<input class="mainoption" type="submit" value="<jforum:i18n key='User.submit'/>" name="submit" />&nbsp;&nbsp;
						<input class="liteoption" type="reset" value="<jforum:i18n key='User.reset'/>" name="reset" />
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>

<c:import url="../footer.jsp"/>
