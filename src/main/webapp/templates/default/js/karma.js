<#if karmaEnabled>
	function karmaVote(s, postId) {
		if (s.selectedIndex == 0) {
			return;
		}

		if (confirm("${I18n.getMessage("Karma.confirmVote")}")) {
			document.location = "${contextPath}/karma/insert/${start}/" + postId + "/" + s.value + "${extension}";
		}
		else {
			s.selectedIndex = 0;
		}
	}

	function karmaPointsCombo(postId)
	{
		document.write('<select name="karma" onchange="karmaVote(this,' + postId + ')">');
		document.write('<option value="">${I18n.getMessage("Karma.rateMessage")}</option>');

		for (var i = ${karmaMin}; i <= ${karmaMax}; i++) {
			document.write('<option value="' + i + '">' + i + '</option>');
		}

		document.write('</select>');
	}
</#if>