<table class="poll">
	<tr>
		<td colspan="4" class="strong" align="center">${topic.poll.label}</td>
	</tr>
	<tr>
		<c:forEach items="${topic.poll.options}" var="option">
			<tr>
				<td>${option.text}</td>
				<td nowrap="nowrap" width="210">
					<img class="icon_vote_lcap" src="<c:url value="/images/transp.gif"/>" alt="" /><img src="<jforum:templateResource item="/images/voting_bar.gif"/>" width="${option.votePercentage * 2}" height="12" alt="" /><img class="icon_vote_rcap" src="<c:url value="/images/transp.gif"/>" alt=""/>
				</td>
				<td nowrap="nowrap" class="strong">${option.votePercentage}%</td>
				<td nowrap="nowrap">[ ${option.voteCount} ]</td>
			</tr>
		</c:forEach>
	</tr>
	<tr>
		<td colspan="4" class="strong" align="center"><jforum:i18n key="PostShow.pollTotalVotes"/> ${topic.poll.totalVotes}</td>
	</tr>
</table>