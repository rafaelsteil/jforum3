<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

</td>
</tr>

	<c:if test="${userSession.roleManager.administrator || userSession.roleManager.coAdministrator}">
		<tr>
			<td align="center">
				<span class="gen"><a id="adminpanel" href="<jforum:url address='/admin/index'/>"><jforum:i18n key="ForumBase.admin"/></a></span>
			</td>
		</tr>
	</c:if>

	<tr>
		<td align="center">
			<span class="copyright">Powered by <a class="copyright" href="http://www.jforum.net/" target="_blank">JForum <jforum:settings key="version"/></a> &copy; <a class="copyright" href="http://www.jforum.net/team.jsp" target="_blank">JForum Team</a></span>
		</td>
	</tr>
</table>
<iframe src="<c:url value='/ping_session.jsp'/>" height="0" width="0" frameborder="0" scrolling="no"></iframe>
</body>
</html>
