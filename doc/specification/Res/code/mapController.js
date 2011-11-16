// MapController class
// Scrollable, switchable (in amount of detail) map
// Uses functions from shared.js
// Requires html page with two imgs for each level of detail; one of which is style="display:none" at start
// Requires mainFrame is scrollable (for Netscape/Mozilla to work

// MapController class

function MapController (frameObject, dragMultiplier) {
	this.objectClass = "MapController";		// used as name for cookie
	this.DRAG_THRESHOLD = 4;	// minimum distance before a drag can not be treated as a click; in pixels
	this.DRAG_MAX_FIRST_STEP = 10;	// largest distance permitted within single mousemove; used to detect invalid drag on IE	
	this.frameObject = frameObject;	// scrollable frame containing mapImages
	this.mapImages = [];			// array storing each mapImage (2 images)
	this.activeMapImage = 1;		// index of active/visible image; default to first map image
	this.dragMultiplier = dragMultiplier ? dragMultiplier : 1;	// 1 = default; 2 = 2x speed dragging; can be any number
	//this.temp = 0;
	//this.systemInfo = new SystemInfo();
}

MapController.prototype.init = function(initialMap) {
	// setup dragScrolling
	this.dragScrolling = false;					// indicates that user is dragging image around
	this.wasDraggedEnoughToNotBeAClick = false;
	this.mouseJustPressed = false;
	this.mouseDownX, this.mouseDownY = null;
	this.oldScrollX, this.oldScrollY = null;
	// Note: Changed following from document.onmousedown; fixes dragging on scroll bar triggering onmousedown in Mozilla/NS
	var mapContentElement = this.frameObject.document.getElementById("mapContent");
	if (!mapContentElement) mapContentElement = this.frameObject.document;
	mapContentElement.onmousedown = this.onMouseDown;
	//this.frameObject.document.onmousedown = this.onMouseDown;
	this.frameObject.document.onmousemove = this.onMouseMove;
	this.frameObject.document.onmouseup = this.onMouseUp;
	this.frameObject.document.onclick = this.onClick;
	this.frameObject.document.onscroll = this.onScroll;
	//
	var cookieValue = this.getCookieValue();
	//window.status = cookieValue;
	if (cookieValue==false) {
		// there was no saved state, so the map is presumably loading for the first time
		this.selectMapImage(initialMap); // select active map
	}
	else {
		// the state is saved; use the cookieValue to figure out the state
		this.initFromCookieValue(cookieValue);
	}
	//trace(this.systemInfo);
}

// Note: In following, e = event object passed in automatically 


MapController.prototype.onMouseDown = function(e) {
	// Note: Ends up running in context of the frame, so this = frameObject, not mapController
	// Get a reference mapController object to be able to use it here.
	var mc = parent.topFrame.mapController;	// HACK: Must be more elegant way to get reference.
	if (!e) var e = mc.frameObject.event;
	if (wasLeftButton(e)) {	 // TODO: Double-check wasLeftButton code; now only works for IE?
		mc.mouseDownX = /* e.x ? e.x : */ e.screenX;
		mc.mouseDownY = /* e.y ? e.y : */ e.screenY;
		// Note: In Mozilla/NS, the mainFrame must be scrollable for the .scrollLeft and .scrollTop properties to be accessible
		
		if (navigator.appName == "Microsoft Internet Explorer")
		{
		    mc.oldScrollX = mc.frameObject.document.body.scrollLeft;
		    mc.oldScrollY = mc.frameObject.document.body.scrollTop;
		    //window.status = "mc.frameObject.document.body.scrollTop:"+mc.frameObject.document.body.scrollLeft;
		}
		else
		{
		    mc.oldScrollX = mc.frameObject.window.scrollX;
		    mc.oldScrollY = mc.frameObject.window.scrollY;
		    //parent.document.title = "mc.frameObject.document.body.scrollTop:"+mc.frameObject.document.body.scrollLeft;
		}
	
		mc.dragScrolling = true;
		mc.mouseJustPressed = true;
		mc.wasDraggedEnoughToNotBeAClick = false;
		//window.status = "mouseDown: " + " | " + mc.mouseDownX + "," + mc.mouseDownY+ " | " + mc.oldScrollX + "," + mc.oldScrollY;
	}
	return false; // stops browser behavior of dragging image to copy it to the desktop (at least on Mac Safari)
}

MapController.prototype.onMouseMove = function(e) {
	var mc = parent.topFrame.mapController;	// HACK: Must be more elegant way to get reference.
	if (!e) var e = mc.frameObject.event;
	//window.status = "MouseMove:" + mapController.temp++;
	if (mc.dragScrolling) {
		if (!e) var e = mc.frameObject.event;
		var newMouseX = /* e.x ? e.x :*/ e.screenX;
		var newMouseY = /* e.y ? e.y : */ e.screenY;
		var newOffsetX = newMouseX - mc.mouseDownX;
		var newOffsetY = newMouseY - mc.mouseDownY;
		
		// Test if move from old to new position is unreasonably large, as in the case when
		// user drags scroll bar in IE -- then dragScrolling is true, but mouseUp is lost.
		// So, this code stops dragScrolling in that case.
		if (mc.mouseJustPressed) {
			if (Math.abs(newOffsetX) > mc.DRAG_MAX_FIRST_STEP | Math.abs(newOffsetY) > mc.DRAG_MAX_FIRST_STEP) {
				mc.dragScrolling = false;
				return false;
			}
			mc.mouseJustPressed = false;
		}
		
		//window.status = '(' + newOffsetX +',' + newOffsetY+')'
		if (Math.abs(newOffsetX) > mc.DRAG_THRESHOLD | Math.abs(newOffsetY) > mc.DRAG_THRESHOLD) mc.wasDraggedEnoughToNotBeAClick = true;
		var newScrollX = mc.oldScrollX - mc.dragMultiplier * newOffsetX;
		var newScrollY = mc.oldScrollY - mc.dragMultiplier * newOffsetY;
		
		mc.frameObject.scrollTo(newScrollX,newScrollY);
		//window.status = "dragging: " + " | " + newMouseX + "," + newMouseY+ " | " + newScrollX + "," + newScrollY + "old scroll: " + mc.oldScrollX + "," + mc.oldScrollY;
		return false;
	}
	else {
		//window.status = "not dragging "+ mapController.temp++ + navigator.appName;
	}
}

MapController.prototype.onMouseUp = function(e) {
	var mc = parent.topFrame.mapController;	// HACK: Must be more elegant way to get reference.
	mc.dragScrolling = false;
	return false; // Doesn't seem to have any useful affect
}

MapController.prototype.onClick = function(e) {
	var mc = parent.topFrame.mapController;	// HACK: Must be more elegant way to get reference.
	if (mc.wasDraggedEnoughToNotBeAClick) {
		//window.status = "onclick return false";
		return false; // cancels onClick event; if mouseUp was on hotshop, navigation can not happen
	}
	return true;	
}

MapController.prototype.onScroll = function(e) {
	//window.status = "scroll";
	// Opera: Triggered when mapContent dragged
	// Mozilla/NS: Triggered when scroll bar dragged (but not mapContent dragged)
	// IE: Never triggered
	// Mozilla/NS on Macintosh: Triggered when scrollbar dragged and mapContent dragged
	
    var agt = navigator.userAgent.toLowerCase();
    if (agt.indexOf("macintosh") == -1 && navigator.appName != "Microsoft Internet Explorer")
    {
	    var mc = parent.topFrame.mapController;	// HACK: Must be more elegant way to get reference.
	    mc.dragScrolling = false;
        return false; // Doesn't seem to have any useful affect	
    }
}

MapController.prototype.addMapImage = function(imgId, centerX, centerY) {
	this.mapImages.push( new MapImage ( this.frameObject, imgId, centerX, centerY) );
}

MapController.prototype.selectMapImage = function(newActiveMapImage) {
	// make mapImageToSelect active and make others inactive
	var lastImage = this.mapImages.length - 1;
	for (var i=0; i <= lastImage; i++ ) {
		this.mapImages[i].makeActive( i==newActiveMapImage );
	}
	this.activeMapImage = newActiveMapImage;
	this.saveStateToCookie(); 
	this.scrollToCenter();
}


MapController.prototype.scrollToCenter = function() {
	// scrolls to the center of active map image
	var activeMapImage = this.mapImages[this.activeMapImage];
	var frameObject = this.frameObject;
	//trace(activeMapImage);
	this.frameObject.scrollTo( activeMapImage.centerX - (getWindowWidth(frameObject)/2), activeMapImage.centerY - (getWindowHeight(frameObject)/2));
}

MapController.prototype.saveStateToCookie = function() {
	// stores state of controller in cookie
	var cookieName = this.objectClass;
	var cookieValue = "";
	// save which map is selected/active
	cookieValue += "active:" + this.activeMapImage;
	// TODO: save scroll position
	// save cookie; will be only accessible to this html page
	document.cookie = cookieName + "=" + cookieValue; 
}

/*
MapController.prototype.savedStateExists = function() {
	// returns true is saved state (cookie) exists
}
*/

MapController.prototype.getCookieValue = function() {
	// checks saved state of controller from cookie; returns string of values or false if desired cookie does not exist
	var cookieName = this.objectClass;
	var allCookies = document.cookie;
	if (allCookies=="") return false;
	
	// extract the named cookie we want
	var start = allCookies.indexOf(cookieName + "=");
	if (start == -1) return false;
	
	start += cookieName.length + 1;  // skip over name and = sign
	var end = allCookies.indexOf(";", start);
	if (end==-1) end = allCookies.length;
	var cookieValue = allCookies.substring(start,end);
	return cookieValue;
}

MapController.prototype.initFromCookieValue = function(cookieValue) {
	// parses cookieValue; initializes object accorded to saved values
	/*
	var a = cookieValue.split("&"); // create array from name/value pairs delimited by ampersand
	for (var i=0; i < a.length; i++) {	// then break each pair into an array
		a[i] = a[i].split(":");
	}
	*/
	// TODO: Rewrite. Currently a hack.
		if (cookieValue=="active:1") this.selectMapImage(1);
		else this.selectMapImage(0);
		// this.selectMapImage(initialMap);
}

/*
MapController.prototype.moveTo = function(x,y) {
	// move image (i.e. scroll window)
	window.scrollTo(x,y);
}
*/

// MapImage class - one for each img/view of the map

function MapImage ( frameObject, imgId, centerX, centerY ) {
	this.objectClass = "MapImage";
	this.centerX = centerX;
	this.centerY = centerY;
	// get image reference
	this.imgElement = getElement(frameObject,imgId);
	this.imgStyle = getElementsStyleObject(frameObject,imgId);
}

MapImage.prototype.makeActive = function (newIsActiveState) {
	//this.trace();
	var newDisplayStyle = newIsActiveState ? "block" : "none";
	this.imgStyle.display = newDisplayStyle;
}

