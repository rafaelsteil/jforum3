<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

<script type="text/javascript" src="<jforum:templateResource item='/js/pagination.js'/>"></script>

<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
	<tr>
		<td align="left">
			<span class="nav"><a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a></span>
		</td>
	</tr>
</table>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<c:set var="paginationData">
		<tr>
			<td colspan="7">
				<jforum:pagination info="${pagination}"/>
			</td>
		</tr>
	</c:set>
				
	${paginationData}

	<tr>
	  <th class="thcornerl" nowrap="nowrap">#</th>
	  <th class="thtop" nowrap="nowrap">&nbsp;<jforum:i18n key='User.username'/>&nbsp;</th>
	  <th class="thtop" nowrap="nowrap">&nbsp;<jforum:i18n key='UserProfile.privateMessage'/>&nbsp;</th>	  
	  <th class="thtop" nowrap="nowrap">&nbsp;<jforum:i18n key='email'/>&nbsp;</th>
	  <th class="thtop" nowrap="nowrap">&nbsp;<jforum:i18n key='UserProfile.from'/>&nbsp;</th>
	  <th class="thtop" nowrap="nowrap">&nbsp;<jforum:i18n key='UserProfile.registrationDate'/>&nbsp;</th>
	  <th class="thtop" nowrap="nowrap">&nbsp;<jforum:i18n key='User.numMessages'/>&nbsp;</th>
	</tr>

	<c:forEach items="${users}" var="user">
		<tr align="center">		  
			<td class="row2 gen">${user.id}</span></td>			
			<td class="row2 gen"><a href="<jforum:url address='/user/profile/${user.id}'/>">${user.username}</a></td>
			<td class="row2 gen"><a href="<jforum:url address='/pm/sendTo/${user.id}'/>" class="icon_pm"><img src="<c:url value="/images/transp.gif"/>" alt="" /></a></td>
			<td class="row2 gen">
				<c:choose>
					<c:when test="${user.viewEmailEnabled && not empty user.email}">
						<a href="#" onclick="document.location = 'mailto:' + showEmail('TODO');"><img src="<jforum:templateResource item='/images/icon_email.gif'/>" alt="[Email]" /></a>
					</c:when>
					<c:otherwise>&nbsp;</c:otherwise>
				</c:choose>
			</td>
			<td class="row2 gen">${user.from}</td>
			<td class="row2 gen">${user.registrationDate}</td>
			<td class="row2 gen">${user.totalPosts}</td>
		</tr>
	</c:forEach>

	${paginationData}
</table>

<c:import url="../footer.jsp"/>