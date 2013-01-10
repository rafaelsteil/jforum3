<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td nowrap="nowrap">
			<span class="gensmall bg"><jforum:i18n key="ForumIndex.goTo"/>:&nbsp;</span>
			
			<form name="f">
				<select onchange="if (this.options[this.selectedIndex].value != -1) { document.location = '<c:url value='/forums/show/'/>' + this.options[this.selectedIndex].value + '<jforum:settings key='servlet.extension'/>'; }" name="select">
					<option value="-1" selected="selected"><jforum:i18n key="ForumIndex.goToSelectAForum"/></option>				
					
					<jforum:displayCategories items="${categories}" var="category" roleManager="${userSession.roleManager}">
	                    <optgroup label="${category.name}">
	                    	<jforum:displayForums items="${category.forums}" var="f" roleManager="${userSession.roleManager}">
								<option value="${f.id}">${f.name}</option>
							</jforum:displayForums>
	                    </optgroup>
					</jforum:displayCategories>
				</select>
				&nbsp;
				<input class="liteoption" type="button" value="<jforum:i18n key='ForumIndex.goToGo'/>" onclick="if(document.f.select.options[document.f.select.selectedIndex].value != -1){ document.location = '<c:url value='/forums/show/'/>'+ document.f.select.options[document.f.select.selectedIndex].value + '<jforum:settings key='servlet.extension'/>'; }" />
			</form>
		</td>
	</tr>
</table>