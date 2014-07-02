<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:import url="../header.jsp"/>

<script type="text/javascript">
page = {}
page.search = {}

window.addEventListener('load', function(){ page.search.load() }, false);

page.search.load = function() {
	f('sort').value = 'DATE';
	f('query').focus();
	page.search.enableDisableOrderBy();
}

page.search.enableDisableOrderBy = function() {
	var disabled = f('sort').value == 'RELEVANCE';
	
	var func = disabled ? 'setAttribute' : 'removeAttribute';
	
	f('sort_asc')[func]('disabled', true);
	f('sort_desc')[func]('disabled', true);
}

function f(i) {
	return document.getElementById(i);
}
</script>

<form id="search" name="search" action="<jforum:url address='/search/execute'/>" method="get">
	
	<table cellspacing="2" cellpadding="2" width="100%" align="center" border="0">
		<tr>
			<td align="left">
				<span class="nav"><a class="nav" href="<jforum:url address="/forums/list"/>"><jforum:i18n key="ForumListing.forumIndex"/></a></span>
			</td>
	    </tr>
	</table>

	<table class="forumline" cellspacing="1" cellpadding="4" width="100%" border="0">
		<tr>
			<th class="thhead" colspan="4" height="25"><jforum:i18n key="Search.terms"/></th>
		</tr>
		
		<tr>
			<td class="row1" style="text-align: right; white-space: nowrap">
				<span class="gen"><jforum:i18n key="Search.typeKeywords"/></span>
			</td>
			<td class="row2">
				<span class="genmed">
					<c:if test="${parseError}">
						<span class="error_message">
							<jforum:i18n key="Search.invalid_search_query"/>: ${parseErrorMessage}
						</span>
					</c:if>
					
					<input id="query" name="params.query" type="text" class="post" style="width: 100%; font-size: 18px" size="30"/>
				</span>
			</td>
			<td class="row2" valign="top" colspan="2">
				<span class="genmed">
					<input name="params.matchType" type="radio" value="AND" checked="checked" id="all_terms"/>
					<label for="all_terms"><jforum:i18n key="Search.allTerms"/></label>
				</span>
				<br />
				<span class="genmed">
					<input type="radio" value="OR" name="params.matchType" id="any_term"/>
					<label for="any_term"><jforum:i18n key="Search.anyTerm"/></label>
				</span>
				</p>
			</td>
        </tr>
        <tr>
			<th class="thhead" colspan="4" height="25"><jforum:i18n key="Search.options"/></th>
        </tr>
		
        <tr>
			<td class="row1" align="right" nowrap="nowrap"><span class="gen"><jforum:i18n key="Search.forum"/>:&nbsp;</span></td>
			<td class="row2">
				<span class="genmed">
				<select class="post" name="params.forum.id" id="forums">
					<option value="0" selected="selected"><jforum:i18n key="Search.allAvailable"/></option>
					
					<jforum:displayCategories items="${categories}" roleManager="${userSession.roleManager}" var="category">
						<optgroup label="${category.name}">
							<jforum:displayForums items="${category.forums}" roleManager="${userSession.roleManager}" var="forum">
								<option value="${forum.id}">${forum.name}</option>
							</jforum:displayForums>
						</optgroup>
					</jforum:displayCategories>
				</select>
				</span>
			</td>
			<td class="row1" align="right"><span class="gen"><jforum:i18n key="Search.orderBy"/>:&nbsp;</span></td>
			<td class="row2" valign="middle" nowrap="nowrap">
				<span class="genmed">
				<select class="post" name="params.sort" id="sort" onChange="page.search.enableDisableOrderBy();">
					<option value="RELEVANCE"><jforum:i18n key="Search.relevance"/></option>
					<option value="DATE"selected="selected"><jforum:i18n key="Search.postDate"/></option>
				</select>
				<br />
				<input type="radio" value="ASC" name="params.sortType" id="sort_asc"/><label for="sort_asc"><jforum:i18n key="Search.ascending"/></label><br />
				<input type="radio" checked="checked" value="DESC" name="params.sortType" id="sort_desc"/><label for="sort_desc"><jforum:i18n key="Search.descending"/></label>
				</span>
			</td>
		</tr>
		<tr>
			<td class="catbottom" align="center" colspan="4" height="28">
				<input class="liteoption" type="submit" value="<jforum:i18n key="Search.search"/>"/>
			</td>
		</tr>
	</table>
</form>
<c:import url="../footer.jsp"/>