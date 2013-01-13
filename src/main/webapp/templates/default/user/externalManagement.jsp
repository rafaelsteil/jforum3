<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="jforum" uri="http://www.jforum.net/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="refresh" content="3;url=${redirectURL}" />
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
						<img src="<jforum:templateResource item="/images/logo.jpg"/>" vspace="1" border="0" alt="[Logo]" />
						<br>
						<span class="maintitle">Ooops - Function Disabled.</span>
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
												<p style="color:red; font-weight:bold">Users and Groups are administered centrally from the Single Sign On 
													Administration Console. Please contact your SSO Admin to make changes..</p>
												<p>Forward to forum index page in 3 seconds!</p>
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