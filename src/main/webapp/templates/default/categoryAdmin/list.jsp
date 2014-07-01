<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<form action="<jforum:url address='/jforum'/>?module=adminCategories&action=delete" method="post" name="form" id="form">

<table class="forumline" cellspacing="0" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="center" colspan="35" height="25"><jforum:i18n key='Category.List.Title'/></th>
	</tr>

	<c:if test="${errorMessage}">
		<tr>
			<td align="center" colspan="5"><span class="gensmall">${errorMessage}</span></td>
		</tr>
	</c:if>

	<c:forEach items="${categories}" var="category" varStatus="status">
		<tr>
			<td class="catleft" width="38%"><span class="cattitle">${category.name}</span></td>
			<td class="catleft" align="center"><span class="gen"><a href="<jforum:url address='/adminCategories/edit?categoryId=${category.id}'/>"><jforum:i18n key='Category.List.Edit'/></a></span></td>
			<td class="catleft" align="center" width="10%"><input type="checkbox" name="categoriesId" value="${category.id}" /></td>
			
			<td class="catleft" align="center" width="10%">
				<c:if test="${!status.first}">
					<input type="button" value="<jforum:i18n key='up'/>" class="mainoption" onClick="document.location = '<jforum:url address='/adminCategories/up/${category.id}'/>';"/>
				</c:if>
			</td>

			<td class="catleft" align="center" width="10%">
				<c:if test="${!status.last}">
					<input type="button" value="<jforum:i18n key='down'/>" class="mainoption" onClick="document.location = '<jforum:url address='/adminCategories/down/${category.id}'/>';"/>
				</c:if>
			</td>
		</tr>

		<tr>
			<td colspan="5" class="row1">
				<table width="100%">
					<c:forEach items="${category.forums}" var="forum">
						<tr class="highlight">
							<td>&nbsp;</td>
							<td class="row1 gen" width="100%">${forum.name}</td>
							<td>&nbsp;</td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</c:forEach>

	<tr align="center">
		<td class="catbottom" colspan="5" height="28">
			<input class="mainoption" type="button" value="<jforum:i18n key='Category.List.ClickToNew'/>" onclick="document.location = '<jforum:url address='/adminCategories/add'/>';" />
			&nbsp;&nbsp;
			<input class="mainoption" type="submit" value="<jforum:i18n key='Category.List.ClickToDelete'/>" />
		</td>
	</tr>
</table>
</form>
