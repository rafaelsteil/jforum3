function watchTopic(url, message) {
	if (confirm(message)) {
		document.location = url;
	}
}

var watchForum = watchTopic;