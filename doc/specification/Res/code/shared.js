// Shared routines

function openPopup(url,windowName,features) {
	window.open(url,windowName,features);
}

function preloadImage(Url) {
	var i = new Image();
	i.src = Url;
}

function showOrHideElement(element,show) {
	element.style.display = show ? "block" : "none";
}

/* unused
function getFirstAncestorOfClass (sourceElement, className) {
	// recursively search for ancestor of sourceElement that matches className
	var elementBeingTested = sourceElement.parentNode;
	if (elementBeingTested.className == className) return elementBeingTested;
	if (!elementBeingTested.className) return null;	// if run out of elements (like at document) stop
	return getFirstAncestorOfClass(elementBeingTested, className);
}
*/

function getFirstDescendentOrSelfOfClass (sourceElement, className) {
	// recursively search for descendent of sourceElement that matches className
	// test self
	if (sourceElement.className == className) return sourceElement;
	// test children
	var child = sourceElement.firstChild;
	if (child) {
		while (child) {
			var elementBeingTested = getFirstDescendentOrSelfOfClass (child, className);
			if (elementBeingTested) return elementBeingTested;
			child = child.nextSibling;
		}
	}
	return null;
}

function getElement(frameObject,elementId) {
	if (document.getElementById) return frameObject.document.getElementById(elementId);
	if (document.all) return frameObject.document.all[elementId];
	if (document.layers) return frameObject.document.layers[elementId];
	return null;
}

function getElementsStyleObject(frameObject,elementId) {
	if (document.getElementById) return frameObject.document.getElementById(elementId).style;
	if (document.all) return frameObject.document.all[elementId].style;
	if (document.layers) return frameObject.document.layers[elementId];
	return null;
}

function getWindowHeight(frameObject) {
	if (document.all) return frameObject.document.body.clientHeight; // IE on Mac and Windows
	if (document.layers) return frameObject.document.clientHeight;
}

function getWindowWidth(frameObject) {
	if (document.all) return frameObject.document.body.clientWidth; // IE on Mac and Windows
	if (document.layers) return frameObject.document.clientWidth;
}
	
function trace (anObject) {
	alert(listObject(anObject));
}

function listObject(theObject) {
	var m = '';
	for (prop in theObject) {
		m+= prop + ":" + theObject[prop] + "\n";
		//* if theObject[prop] == 
	}
	return(m);
}

function wasLeftButton(e) {
	// takes event object (e) and decides if left button was pressed (as opposed to middle wheel button)
	var buttonPressed = /* (navigator.appName=="Netscape") ?  e.which : */ e.button;
	if (buttonPressed == 1 | buttonPressed == 0 ) return true;
	return false;
}

function appendToCookieString ( cookieString, property, value ) {
	if (cookieString!="") cookieString += "&";
	cookieString += property + ":" + escape(value);
}

function getValueFromCookieString ( cookieString, property) {
	// extract value of given property from encoding like this: "property1:value1&property2:value2"
	var pos = cookieString.indexOf(property); // at start of property label
	if (pos==-1) return null;
	pos += property.length + 1; // at start of value
	var start = pos;
	pos = cookieString.indexOf("&",pos+1);
	// if "&" not found, must be last property:value pair -- end of value is end of cookieString
	// else end of value is just before "&"
	var end = (pos==-1) ? cookieString.length : pos;
	var value = cookieString.substring(start,end);
	return unescape(value);
}
	

// SystemInfo Class
// class to handle system check (browser, etc.)
// Thanks to http://www.xs4all.nl/~ppk/js/detect.html for this code
// TODO: Rewrite?
/* 
function SystemInfo() {
	this.detect = navigator.userAgent.toLowerCase();
	this.OS = null;
	this.browser = null;
	this.version = null;
	//this.subVersion = null;
	this.total = null;
	this.thestring = null;
	this.place = null;

	if (this.checkIt('konqueror')) {
		this.browser = "Konqueror";
		this.OS = "Linux";
	}
	else if (this.checkIt('safari')) {
		this.browser = "Safari"
		//this.subVersion = this.detect.substring(8,12);
	}
	else if (this.checkIt('omniweb')) this.browser = "OmniWeb"
	else if (this.checkIt('opera')) this.browser = "Opera"
	else if (this.checkIt('webtv')) this.browser = "WebTV";
	else if (this.checkIt('icab')) this.browser = "iCab"
	else if (this.checkIt('msie')) this.browser = "Internet Explorer"
	else if (!this.checkIt('compatible')) {
		this.browser = "Netscape Navigator"
		this.version = this.detect.charAt(8);
	}
	else this.browser = "An unknown browser";

	if (!this.version) this.version = this.detect.charAt(this.place + this.thestring.length);
	
	if (!this.OS) {
		if (this.checkIt('linux')) this.OS = "Linux";
		else if (this.checkIt('x11')) this.OS = "Unix";
		else if (this.checkIt('mac')) this.OS = "Mac"
		else if (this.checkIt('win')) this.OS = "Windows"
		else this.OS = "an unknown operating system";
	}
}

SystemInfo.prototype.checkIt = function(string) {
	this.place = this.detect.indexOf(string) + 1;
	this.thestring = string;
	return this.place; // HACK: Weird
}

*/

// Saving state using cookies

/*
expires
domain

document.cookie = "version=" + escape(document.lastModified) + "; expires=" + 
// cookie values may not include semicolons, commas, or whitespace


*/
