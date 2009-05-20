<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="../header.jsp"/>

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
					<th class="thhead" height="25"><b><jforum:i18n key='Information'/></b></th>
				</tr>
				
				<tr>
					<td class="row1">
						<table width="100%" cellspacing="0" cellpadding="1" border="0">
							<tr>
								<td>&nbsp;</td>
							</tr>
	  
							<tr>
								<td align="center" class="gen">${message}</td>
							</tr>
				  
							<tr>
								<td>&nbsp;</td>
							</tr>

							<tr>
								<td align="center"><a class="nav" href="<jforum:url address="/forums/list"/>"><jforum:i18n key='ForumListing.forumIndex'/></a> </td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<c:import url="../footer.jsp"/>