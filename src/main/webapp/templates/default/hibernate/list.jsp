<%@ page import="org.hibernate.stat.*, java.util.*"%>
<%@taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css">@import url( <jforum:templateResource item='/styles/style.css'/> );</style>

<% 
Statistics stats = (Statistics)request.getAttribute("stats");
 %>

<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="middle" colspan="5" height="25">Hibernate statistics</th>
	</tr>
	
	<tr>
		<td class="row1 gen">The number of prepared statements that were acquired</td>
		<td class="row1 gen">
			<%= stats.getPrepareStatementCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">The number of prepared statements that were released</td>
		<td class="row3 gen">
			<%= stats.getCloseStatementCount() %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of collections fetched</td>
		<td class="row1 gen">
			<%= stats.getCollectionFetchCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of collections loaded</td>
		<td class="row3 gen">
			<%= stats.getCollectionLoadCount() %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of collections recreated</td>
		<td class="row1 gen">
			<%= stats.getCollectionRecreateCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of collections removed</td>
		<td class="row3 gen">
			<%= stats.getCollectionRemoveCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of collections updated</td>
		<td class="row1 gen">
			<%= stats.getCollectionUpdateCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of connections asked by the sessions </td>
		<td class="row3 gen">
			<%= stats.getConnectCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of entity deletes</td>
		<td class="row1 gen">
			<%= stats.getEntityDeleteCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of entity fetchs</td>
		<td class="row3 gen">
			<%= stats.getEntityFetchCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of entity inserts</td>
		<td class="row1 gen">
			<%= stats.getEntityInsertCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of entity loads</td>
		<td class="row3 gen">
			<%= stats.getEntityLoadCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of entity updates</td>
		<td class="row1 gen">
			<%= stats.getEntityUpdateCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of flush executed by sessions (either implicit or explicit)</td>
		<td class="row3 gen">
			<%= stats.getFlushCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">The number of StaleObjectStateExceptions that occurred</td>
		<td class="row1 gen">
			<%= stats.getOptimisticFailureCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of cached queries successfully retrieved from cache</td>
		<td class="row3 gen">
			<%= stats.getQueryCacheHitCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of cached queries *not* found in cache</td>
		<td class="row1 gen">
			<%= stats.getQueryCacheMissCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of cacheable queries put in cache</td>
		<td class="row3 gen">
			<%= stats.getQueryCachePutCount() %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of executed queries</td>
		<td class="row1 gen">
			<%= stats.getQueryExecutionCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">The time in milliseconds of the slowest query.</td>
		<td class="row3 gen">
			<%= stats.getQueryExecutionMaxTime() %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">The query string for the slowest query.</td>
		<td class="row1 gen">
			<%= stats.getQueryExecutionMaxTimeQueryString()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of cacheable entities/collections successfully retrieved from the cache</td>
		<td class="row3 gen">
			<%= stats.getSecondLevelCacheHitCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of cacheable entities/collections not found in the cache and loaded from the database.</td>
		<td class="row1 gen">
			<%= stats.getSecondLevelCacheMissCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of cacheable entities/collections put in the cache</td>
		<td class="row3 gen">
			<%= stats.getSecondLevelCachePutCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">Global number of sessions opened</td>
		<td class="row1 gen">
			<%= stats.getSessionOpenCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">Global number of sessions closed</td>
		<td class="row3 gen">
			<%= stats.getSessionCloseCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">The time the statistics started being generated</td>
		<td class="row1 gen">
			<%= new java.util.Date(stats.getStartTime())  %>
		</td>
	</tr>
	
	<tr>
		<td class="row3 gen">The number of transactions we know to have been successful</td>
		<td class="row3 gen">
			<%= stats.getSuccessfulTransactionCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">The number of transactions we know to have completed</td>
		<td class="row1 gen">
			<%= stats.getTransactionCount()  %>
		</td>
	</tr>
</table>

<% 
String[] regionNames = stats.getSecondLevelCacheRegionNames();
Arrays.sort(regionNames);

for (String regionName : regionNames) {
	SecondLevelCacheStatistics secondLevelStats = stats.getSecondLevelCacheStatistics(regionName);
	
	%>
	<br/>
	
	<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
		<tr>
			<th class="thhead" valign="middle" colspan="2" height="25">Second-level for <i><%= regionName %></i></th>
		</tr>
	
	<tr>
		<td class="row1 gen" width="50%">The number of elements in memory</td>
		<td class="row1 gen">
			<%= secondLevelStats.getElementCountInMemory()   %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">The number of elements in disk</td>
		<td class="row1 gen">
			<%= secondLevelStats.getElementCountOnDisk()   %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">How many times the cache was hit</td>
		<td class="row1 gen">
			<%= secondLevelStats.getHitCount()   %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">How many times an entry was not found in the cache</td>
		<td class="row1 gen">
			<%= secondLevelStats.getMissCount()  %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">How many times there was an add to the cache</td>
		<td class="row1 gen">
			<%= secondLevelStats.getPutCount()   %>
		</td>
	</tr>
	
	<tr>
		<td class="row1 gen">The approximate size in memory</td>
		<td class="row1 gen">
			<% 
			long sizeInMemory = secondLevelStats.getSizeInMemory();
			out.println((sizeInMemory / 1024) + " Kb");
			%>
		</td>
	</tr>
	
	</table>
	
	<%
}
 %>


<br/>

<!-- Entity statitics -->
<table class="forumline" cellspacing="1" cellpadding="3" width="100%" border="0">
	<tr>
		<th class="thhead" valign="middle" colspan="2" height="25"><i>Entity Statistics</i></th>
	</tr>
	
	<% 
	String[] entityNames = stats.getEntityNames();
	Arrays.sort(entityNames);
	
	for (String entityName : entityNames) {
		EntityStatistics entityStats = stats.getEntityStatistics(entityName);
	 	%>
	 	<tr>
			<td class="row3 gen" colspan="2"><b><%= entityName %></b></td>
		</tr>
		
		<tr>
			<td class="row1 gen" width="50%">Delete count</td>
			<td class="row1 gen">
				<%= entityStats.getDeleteCount()   %>
			</td>
		</tr>
		
		<tr>
			<td class="row1 gen">Fetch count</td>
			<td class="row1 gen">
				<%= entityStats.getFetchCount()    %>
			</td>
		</tr>
		
		<tr>
			<td class="row1 gen">Insert count</td>
			<td class="row1 gen">
				<%= entityStats.getInsertCount()    %>
			</td>
		</tr>
		
		<tr>
			<td class="row1 gen">Load count</td>
			<td class="row1 gen">
				<%= entityStats.getLoadCount()    %>
			</td>
		</tr>
		
		<tr>
			<td class="row1 gen">Update count</td>
			<td class="row1 gen">
				<%= entityStats.getUpdateCount()    %>
			</td>
		</tr>
		
		<tr>
			<td class="row1 gen">StaleObjectStateExceptions count</td>
			<td class="row1 gen">
				<%= entityStats.getOptimisticFailureCount()    %>
			</td>
		</tr>
		<%
	} 
	 %>
</table>
