<%@ page isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jforum" uri="http://www.jforum.net/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- 
BEGIN ERROR STACK TRACE
<% 
java.io.StringWriter strWriter = new java.io.StringWriter();
java.io.PrintWriter writer = new java.io.PrintWriter(strWriter);
exception.printStackTrace(writer);
out.println(strWriter); 
%>
END ERROR STACK TRACE
-->

<html>
<head>
<style type="text/css">@import url(<jforum:templateResource item="/styles/style.css"/>);</style>
<style type="text/css">@import url(<jforum:templateResource item="/styles/en_US.css"/>);</style>
<title>JForum</title>
</head>
<body class="en_US">

<table width="100%" border="0">
	<tr>
		<td>
			<table cellspacing="0" cellpadding="0" width="100%" border="0">
				<tr>
					<td width="100%" align="center" valign="middle">
						<span class="maintitle">Ooops - An error has ocurred</span>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%" cellspacing="0" cellpadding="10" border="0" align="center">
				<tr>
					<td class="bodyline">
						<br />
						<table width="100%" cellspacing="2" cellpadding="2" border="0" align="center">
							<tr>
								<td align="left" class="nav"><a class="nav" href="<jforum:url address="/forums/list"/>">Forum Index</a></td>
							</tr>
						</table>
			  
						<table class="forumline" width="100%" cellspacing="1" cellpadding="4" border="0">
							<tr>
								<th class="thhead" height="25"><b>Error</b></th>
							</tr>
							
							<tr>
								<td class="row1">
									<table width="100%" cellspacing="0" cellpadding="1" border="0">
										<tr>
											<td>&nbsp;</td>
										</tr>
				  
										<tr>
											<td align="center" class="gen">
												<p style="color:red; font-weight:bold">An error has occurred.</p>
												<p>For detailed error information, please open the page source code, and contact the forum Administrator.</p>
												<pre class="pre-error"><i>${fn:escapeXml(pageContext.errorData.throwable)}</i></pre>
											</td>
										</tr>
							  
										<tr>
											<td>&nbsp;</td>
										</tr>
			
										<tr>
											<td align="center"><a class="nav" href="<jforum:url address="/forums/list"/>">Forum Index</a> </td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
		
		</td>
	</tr>
</table>
</body>
</html>