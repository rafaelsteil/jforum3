function verifyModerationCheckedTopics() {
	var f = document.formModeration.topicIds;
	
	if (f.length == undefined)	 {
		if (f.checked) {
			return true;
		}
	}

	for (var i = 0; i < f.length; i++) {
		if (f[i].checked) {
			return true;
		}
	}
	
	alert("${I18n.getMessage('Moderation.SelectTopics')}");
	return false;
}

function submitModerationMove() {
	if (verifyModerationCheckedTopics()) {
		document.formModeration.action += "askMoveDestination";
		document.formModeration.submit();
	}
	
	return false;
}

function submitModerationDelete() {
	var status = verifyModerationCheckedTopics() 
		&& confirm("${I18n.getMessage('Moderation.ConfirmDelete')}");

	if (status) {
		document.formModeration.action += "deleteTopics";
		document.formModeration.submit();
		return true;
	}

	return false;
}

function submitModerationLockUnlock()  {
	var status = verifyModerationCheckedTopics();
	
	if (status) {
		document.formModeration.action += "lockUnlock";
		document.formModeration.submit();
		return true;
	}

	return false;
}

function askModerationReason() {
	var message = prompt("${I18n.getMessage('ModerationLog.changeReason')}");

	if (message == null || message == "") {
		alert("${I18n.getMessage('ModerationLog.reasonIsEmpty')}");
		return false;
	}
	else {
		document.formModeration.log_description.value = message;
	}

	return true;
}

var oldClasses = {}

function changeTrClass(from, trIndex)
{
	var tr = from.parentNode.parentNode;
	trIndex = trIndex.toString();

	if (from.checked) {
		tr.className = "moderation_highlight";
		oldClasses[trIndex] = new Array();

		for (var i = 0; i < tr.childNodes.length; i++) {
			var node = tr.childNodes[i];

			if (node.nodeName.toUpperCase() == "TD") {
				oldClasses[trIndex].push(node.className);
				node.className = "";
			}
		}
	}
	else {
		tr.className = "";

		for (var i = tr.childNodes.length - 1; i >= 0; i--) {
			var node = tr.childNodes[i];

			if (node.nodeName.toUpperCase() == "TD") {
				node.className = oldClasses[trIndex].pop();
			}
		}
	}	
}