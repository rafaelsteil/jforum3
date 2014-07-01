<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

<form action="<jforum:url address="/jforum"/>?module=moderation&action=moveTopics" method="post" name="formModeration" id="formModeration" onsubmit="return askModerationReason();" accept-charset="${encoding}">
<input type="hidden" name="returnUrl" value="${returnUrl}" />

<c:forEach items="${topicIds}" var="topicId">
	<input type="hidden" name="topicIds" value="${topicId}"/>
</c:forEach>

<table width="100%" cellspacing="0" cellpadding="10" border="0" align="center">
	<tr>
		<td class="bodyline">
			<br />
			<table width="100%" cellspacing="2" cellpadding="2" border="0" align="center">
				<tr>
					<td align="left" class="nav"><a class="nav" href="<jforum:url address="/forums/list"/>"><jforum:i18n key='ForumListing.forumIndex'/></a></td>
				</tr>
			</table>
  
			<table class="forumline" width="100%" cellspacing="1" cellpadding="4" border="0">
				<tr>
					<th class="thhead" height="25"><b><jforum:i18n key='PostForm.movingTopic'/></b></th>
				</tr>
				
				<tr>
					<td class="row1">
						<table width="100%" cellspacing="0" cellpadding="1" border="0">
							<tr>
								<td>&nbsp;</td>
							</tr>  
							
							<tr>
								<td align="center">
									<span class="gen">
									<jforum:i18n key='PostForm.moveToForum'/>
									
									<select name="toForumId">
										<jforum:displayCategories items="${categories}" var="category" roleManager="${userSession.roleManager}">
						                    <optgroup label="${category.name}">
						                    	<jforum:displayForums items="${category.forums}" var="f" roleManager="${userSession.roleManager}">
													<option value="${f.id}" <c:if test="${f.id == fromForumId}">disabled</c:if>>${f.name}</option>
												</jforum:displayForums>
						                    </optgroup>
										</jforum:displayCategories>
									</select>
									</span>
								</td>
							</tr>
							
							<tr>
								<td align="center">
									<br />
									<input type="submit" class="liteoption" value="<jforum:i18n key='move'/>" />
									&nbsp;
									<input type="button" value="<jforum:i18n key='previousPage'/>" onclick="history.go(-1)" class="liteoption" />
								</td>
							</tr>
				
						   <tr>
							  <td>&nbsp;</td>
						  </tr>
							
							<tr>
								<td align="center">
									<a class="nav" href="${contextPath}/forums/list${extension}"><jforum:i18n key='ForumListing.forumIndex'/></a>
									- <a class="nav" href="javascript:history.go(-1)"><jforum:i18n key='previousPage'/></a>
								</td>
							</tr>
						</table>
					</td>
				</tr>  
			</table>  
		</td>
	</tr>
</table>
</form>

<c:import url="../footer.jsp"/>