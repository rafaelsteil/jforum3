<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:import url="../header.jsp"/>

<script type="text/javascript">
function validatePostForm(f) {
	if (f["tag"].value == "") {
		f["tag"].focus();
		f["tag"].style.background="red";
		return false;
	}
	$("#icon_saving").css("display", "inline");
	return true;
}
</script>

<form action="<jforum:url address='/jforum'/>?module=tag&action=replySave" method="post" name="post" id="post" onSubmit="return validatePostForm(this)">
<input type="hidden" name="topic.id" value="${topic.id}" />
<input type="hidden" name="topic.forum.id" value="${forum.id}" /> <%-- For Security Checking --%>
	<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
		<tr>
			<td align="left">
				<span class="nav">
					<a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a> &raquo;
	            	<a class="nav" href="<jforum:url address='/forums/show/${forum.id}'/>">${forum.name} </a> &raquo;
					<a class="nav" href="<jforum:url address='/topics/list/${topic.id}'/>">${fn:escapeXml(topic.subject)} </a>
				</span>
			</td>
		</tr>
	</table>

	<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
		<tr>
			<th class="thhead" colspan="2" height="25">
				<strong>
				<jforum:i18n key='Tag.reply'/> "${topic.subject}"
				</strong>
			</th>
		</tr>
		<tr>
			<td class="row1 gen" width="15%">
				<strong><jforum:i18n key='Tag.existed'/></strong>
			</td>
			
			<td class="row2" width="85%">
				<input type="text" tabindex="2" name="existed.tag" value="${tags}" style="width:60%;" disabled/>
			</td>
		</tr>

		<tr>
			<td class="row1" valign="top">
				<span class="gen"><strong><jforum:i18n key='Tag.add'/></strong></span>
			</td>

			<td class="row2" valign="top">
				<input type="text" tabindex="3" name="tag" value="" style="width:60%;"/> 
				<em class="helpline" style="font-size:11px;"><jforum:i18n key='Tag.intro'/></em>
			</td>
		</tr>

		<tr>
			<td align="center" height="28" colspan="2" class="catbottom">
				<input class="mainoption" id="btnSubmit" accesskey="s" tabindex="6" type="submit" value="<jforum:i18n key='PostForm.submit'/>"/>
				<img src="<c:url value='/images/transp.gif'/>" id="icon_saving"/>
			</td>
		</tr>
	</table>
</form>

<c:import url="../footer.jsp"/>