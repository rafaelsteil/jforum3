<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="info" required="true" type="net.jforum.entities.util.Pagination" %>
<%@ attribute name="isSearch" required="false" type="java.lang.Boolean" %>
<%@ attribute name="searchParams" required="false" type="net.jforum.entities.util.SearchParams"%>
<%@ attribute name="showGotoBox" required="false" type="java.lang.Boolean" %>

<c:if test="${info.totalRecords > info.recordsPerPage}">
	<div class="pagination">
		<!-- Previous page  -->
		<c:if test="${info.thisPage > 1}">
			<c:choose>
				<c:when test="${!isSearch}">
					<c:if test="${info.thisPage - 1 > 0}">
						<c:set var="extraArgs" value="/${info.thisPage - 1}"/>
					</c:if>
					
					<c:if test="${info.id > 0}">
						<c:set var="extraArgs" value="${extraArgs}/${info.id}"/>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:set var="extraArgs" value="AAA"/>
				</c:otherwise>
			</c:choose>
			
			<a href="<jforum:url address='${info.baseUrl}${extraArgs}'/>">&#9668;</a>
		</c:if>
		
		<c:if test="${info.totalPages < 10}">
			<c:forEach begin="1" end="${info.totalPages}" var="page">
				<%@ include file="pageLink.tagf" %>
			</c:forEach>
		</c:if>
		
		<c:if test="${info.totalPages >= 10}">
			<!-- Always write the first 3 links -->
			<c:forEach begin="1" end="3" var="page">
				<%@ include file="pageLink.tagf" %>
			</c:forEach>
			
			<!-- Intemediate links -->
			<c:choose>
				<c:when test="${info.thisPage > 1 && info.thisPage < info.totalPages}">
					<c:if test="${info.thisPage > 5}">
						<span class="gensmall">...</span>
					</c:if>
					
					<c:choose>
						<c:when test="${info.thisPage > 4}">
							<c:set var="min" value="${info.thisPage - 1}"/>
						</c:when>
						<c:otherwise>
							<c:set var="min" value="4"/>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${info.thisPage < info.totalPages - 4}">
							<c:set var="max" value="${info.thisPage + 2}"/>
						</c:when>
						<c:otherwise>
							<c:set var="max" value="${info.totalPages - 2}"/>
						</c:otherwise>
					</c:choose>
					
					<c:if test="${max >= min + 1}">
						<c:forEach begin="${min}" end="${max - 1}" var="page">
							<%@ include file="pageLink.tagf" %>
						</c:forEach>
					</c:if>
					
					<c:if test="${info.thisPage < info.totalPages - 4}">
						<span class="gensmall">...</span>
					</c:if>
				</c:when>
				<c:otherwise>
					<span class="gensmall">...</span>
				</c:otherwise>
			</c:choose>
			
			<!-- Write the last 3 links -->
			<c:forEach begin="${info.totalPages - 2}" end="${info.totalPages}" var="page">
				<%@ include file="pageLink.tagf" %>
			</c:forEach>
		</c:if>
		
		<c:remove var="extraArgs"/>
		<!-- Next page -->
		<c:if test="${info.thisPage < info.totalPages}">
			<c:set var="extraArgs" value="/${info.thisPage + 1}"/>
			
			<c:if test="${info.id > 0}">
				<c:set var="extraArgs" value="${extraArgs}/${info.id}"/>
			</c:if>
			
			<a href="<jforum:url address='${info.baseUrl}${extraArgs}'/>">&#9658;</a>
		</c:if>
		
		<c:if test="${showGotoBox}">
			<a href="#goto" onClick="return overlay(this, 'goToBox', 'rightbottom');"><jforum:i18n key='ForumIndex.goToGo'/></a>
			<div id="goToBox">
				<div class="title"><jforum:i18n key='goToPage'/>...</div>
				<div class="form">
					<input type="text" style="width: 50px;" id="pageToGo"/>
					<input type="button" value=" <jforum:i18n key='ForumIndex.goToGo'/> " onClick="goToAnotherPage(${totalPages}, ${recordsPerPage}, '${contextPath}', '${moduleName}', '${action}', ${id}, '${extension}');"/>
					<input type="button" value="<jforum:i18n key='cancel'/>" onClick="document.getElementById('goToBox').style.display = 'none';"/>
				</div>
			</div>
		</c:if>
	</div>
</c:if>