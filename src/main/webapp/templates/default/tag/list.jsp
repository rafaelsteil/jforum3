<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>
<style type="text/css">
	.path-text{
		font-size:11px;
	}
	.th-title{
		border-width:1px;
	}
</style>

<table cellspacing="0" cellpadding="10" width="100%" align="center" border="0">
	<tr>
		<td class="bodyline" valign="top">
		
			<c:set var="breadCrumb">
				<table cellspacing="0" cellpadding="2" width="100%" align="center" border="0">
					<tr>
						<td valign="bottom" align="left">
							<a class="nav" href="<jforum:url address='/forums/list'/>"><jforum:i18n key='ForumListing.forumIndex'/></a>
							<span class="path-text">&raquo;</span>
							<a id="latest3" class="nav" href="<jforum:url address='/tag/list'/>"><jforum:i18n key='Tag.tag'/></a>
						</td>
						<td>&nbsp;
						</td>
					</tr>
				</table>
			</c:set>
			
			${breadCrumb}

			<c:import url="tagCloud.jsp"></c:import>
			
			${breadCrumb}

		</td>
	</tr>
</table>

<c:import url="../footer.jsp"/>
