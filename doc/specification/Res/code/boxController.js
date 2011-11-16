/* 
BoxController
Note: Requires boxSettings.js also be included

Revisions
2003-09-13: Jeff edited. Added expand all of type.
2003-09-15: Jeff added cookie code.

Boxes are the bordered divs with a header and body, and a expand/collapse button.
Box types:
partialMap, subTopic, callouts, comments, taskInformation, legend.

If you collapse a subTopic box on one page, you collapse all subTopic boxes that and other pages. Same with expanding.
Note: There can be more than one box of each type on the page.

In HTML, the structure expected is:

div.{boxType}Area (useful for CSS selector of different types of boxes, e.g. div.{boxType}Area div.box)
	div class="collapsiblebox" id="{boxType}{index}" (1+) 
		div.header onclick = "boxController.expandOrCollapse(this,'{boxType}')" (0-1) 
			span.title
			span.commands  ? (mike)
				span.command  ? (mike)
					img.expandOrCollapseButton src="{buttonsPath}{collapseButtonUrl} or {expandButtonUrl}"
		div.body
			(varies)
	
	Note: Visibility of div.body (i.e. style="display:block" or style="display:none") is overwritten by code.
	Note: Button graphics as well.									
	
 */

function init() {
	boxController = new BoxController("Res/images/", "box_collapse_button.gif","box_expand_button.gif", true);
	boxController.init();
}

// BoxController class

function BoxController (buttonsPath, collapseButtonFilename,expandButtonFilename, changeAllOfType) {
	this.objectClass = "BoxController";
	this.buttonsPath = buttonsPath;
	this.collapseButtonUrl = buttonsPath + collapseButtonFilename;
	this.expandButtonUrl = buttonsPath + expandButtonFilename;
	this.changeAllOfType = changeAllOfType; // if true, all boxes of a type are expanded or collapsed together, not just clicked one
	this.boxTypes = null;
}


BoxController.prototype.init = function() {
	// initialize boxTypes
	this.boxTypes = INITIAL_BOX_SETTINGS; // defaults loaded from boxSettings.js

	// check if cookie has stored states
	var cookieString = this.getCookieString();
	if (cookieString!=null) {
		// for each boxType, get stored value from cookie
		for (var i=0; i < this.boxTypes.length; i++) {
			var boxType = this.boxTypes[i];
			var value = getValueFromCookieString ( cookieString, boxType.name );
			if (value!=null) {
				boxType.state = value=="true" ? true : false;
			}
		}
	}
	
	
	// for each boxType, change state of all boxes of that type
	for (var i=0; i < this.boxTypes.length; i++) {
		this.changeStateOfAllBoxesOfType( this.boxTypes[i].name, this.boxTypes[i].state );
	}
}

BoxController.prototype.dumpBoxTypes = function() {
	var s = "BoxTypes\n";
	for (var i=0; i < this.boxTypes.length; i++) {
		s += this.boxTypes[i].name + "," + this.boxTypes[i].state+ "\n";
	}
	return s;
}
	
BoxController.prototype.saveStateToCookie = function() {
	// stores state of controller in cookie
	var cookieString = "";

	// for each box type...
	for (var i=0; i < this.boxTypes.length; i++ ) {
		var boxType = this.boxTypes[i];
		if (cookieString!="") cookieString += "&";
		cookieString += boxType.name + ":" + escape(boxType.state); 
	}

	// Cookie properties (if needed)
	// Currently assumes that all html pages are in the same directory together, otherwise cookie's path property would need to be set
	// Currently that cookie should expire when user closes the browser (expires property)
	// save cookie; will be only accessible to this html page
	var cookieName = this.objectClass;
	document.cookie = cookieName + "=" + cookieString; 
}

BoxController.prototype.getCookieString = function() {
	// checks saved state of controller from cookie; returns string of values or null if desired cookie does not exist
	var cookieName = this.objectClass;
	var allCookies = document.cookie;
	if (allCookies=="") return null;
	
	// extract the named cookie we want
	var start = allCookies.indexOf(cookieName + "=");
	if (start == -1) return null;
	
	start += cookieName.length + 1;  // skip over name and = sign
	var end = allCookies.indexOf(";", start);
	if (end==-1) end = allCookies.length;
	var cookieString = allCookies.substring(start,end);
	return cookieString;
}

BoxController.prototype.changeStateOfAllBoxesOfType = function( boxType, newState ) {
	// changes all boxes on the page of boxType to newState ("expand" or "collapse") 
	// loop through all boxes of boxType
	var boxIndex = 0;
	while (document.getElementById (boxType + boxIndex) ) {
		// change state
		this.changeStateOfBoxOnPage ( document.getElementById (boxType + boxIndex), newState );
		boxIndex++;
	}
	// store new state in array
	for (var i=0; i < this.boxTypes.length; i++ ) {
		var boxTypeItem = this.boxTypes[i];
		if (boxTypeItem.name == boxType) {
			boxTypeItem.state = newState;
			break;
		}
	}
}

BoxController.prototype.expandOrCollapse = function( headerElement, boxType ) {
	// Note: Having one routine for both expand and collapse let's the function that html calls be the same
	// Uses img's src attribute to determine which command is being called
	// First get the button element; may be the elementActivated or within the elementActivated (like an anchor element)
	var buttonElement = getFirstDescendentOrSelfOfClass ( headerElement, "expandOrCollapseButton");
	if (!buttonElement) return;
		
	var currentImageUrl = buttonElement.src;
	if (!currentImageUrl) return;

	// if src value contains url of collapse button, this is, presumably a collapse command
	var newState = false;
	if (currentImageUrl.lastIndexOf(this.collapseButtonUrl)==-1) newState = true;
	
	// change state of all boxes of this type ...
	if (this.changeAllOfType) {
		this.changeStateOfAllBoxesOfType (boxType,newState);
		this.saveStateToCookie();
	}
	// or just this one box
	else {
		var boxElement = headerElement.parentNode;
		if (boxElement) this.changeStateOfBoxOnPage(boxElement,newState);
	}
	
}

BoxController.prototype.changeStateOfBoxOnPage = function( boxElement, makeOpen) {
	// change UI of box on the html page
	
	// get button element
	var buttonElement = getFirstDescendentOrSelfOfClass( boxElement, "expandOrCollapseButton");
	if (!buttonElement) return false; // if not found, give up
	// get body element
	var bodyElement = getFirstDescendentOrSelfOfClass( boxElement, "body");
	if (!bodyElement) return false; // if not found, give up
	
	// expand or collapse body element
	showOrHideElement(bodyElement,makeOpen);
	
	// update img to be an appropriate button
	if (buttonElement.src) {
		// if opening, make it the collapse button; if not opening, make it the expand button
		buttonElement.src = makeOpen ? this.collapseButtonUrl : this.expandButtonUrl;
	}
}

