<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${isEdit}">
	<c:set var="topicPrefix" value="post."/>
</c:if>	

<table cellspacing="0" cellpadding="1" border="0" class="genmed">
	<tr id="pollQuestion">
		<td><jforum:i18n key="PostForm.pollQuestion"/></b></td>
		<td><input type="text" name="${topicPrefix}topic.poll.label" maxlength="255" size="50" value="${topic.poll.label}"></input></td>
	</tr>
	
	<tr id="pollOptionWithAdd">
		<td>
			<jforum:i18n key="PostForm.pollAddOption"/>
		</td>
		<td>
			<input type="button" onclick="javascript:addPollOption()" value="<jforum:i18n key="PostForm.pollAddOption"/>"></input>
		</td>
	</tr>
	
	<tr>
		<td><jforum:i18n key="PostForm.pollRunFor"/></td>
		<td>
			<input type="text" name="${topicPrefix}topic.poll.length" id="pollLength" maxlength="5" size="4" value="<c:if test="${not empty topic.poll}">${topic.poll.length}</c:if>"/>&nbsp;<jforum:i18n key="PostForm.pollDays"/>
			<span class="gensmall"><jforum:i18n key="PostForm.pollDaysDescription"/></span>
		</td>
	</tr>
</table>

<c:choose>
	<c:when test="${empty topic.poll}">
		<script type="text/javascript">addPollOption();</script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript">
			<c:forEach items="${topic.poll.options}" var="option">
				addPollOption(${option.id}, '${option.text}');
			</c:forEach>
		</script>
	</c:otherwise>
</c:choose>


