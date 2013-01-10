/***********************************************
* Drop Down/ Overlapping Content- © Dynamic Drive (www.dynamicdrive.com)
* This notice must stay intact for legal use.
* Visit http://www.dynamicdrive.com/ for full source code
***********************************************/

function getposOffset(overlay, offsettype){
var totaloffset=(offsettype=="left")? overlay.offsetLeft : overlay.offsetTop;
var parentEl=overlay.offsetParent;
while (parentEl!=null){
totaloffset=(offsettype=="left")? totaloffset+parentEl.offsetLeft : totaloffset+parentEl.offsetTop;
parentEl=parentEl.offsetParent;
}
return totaloffset;
}

function overlay(curobj, subobjstr, opt_position){
if (document.getElementById){
var subobj=document.getElementById(subobjstr)
subobj.style.display=(subobj.style.display!="block")? "block" : "none"
var xpos=getposOffset(curobj, "left")+((typeof opt_position!="undefined" && opt_position.indexOf("right")!=-1)? -(subobj.offsetWidth-curobj.offsetWidth) : 0) 
var ypos=getposOffset(curobj, "top")+((typeof opt_position!="undefined" && opt_position.indexOf("bottom")!=-1)? curobj.offsetHeight : 0)
subobj.style.left=xpos+"px"
subobj.style.top=ypos+"px"
return false
}
else
return true
}

function overlayclose(subobj){
document.getElementById(subobj).style.display="none"
}

function goToAnotherPage(max, recordsPerPage, contextPath, moduleName, action, id, extension)
{
	var page = document.getElementById("pageToGo").value * 1;

	if (!isNaN(page) && page <= max && page > 0) {
		var path = contextPath + "/" + moduleName + "/" + action + "/" + ((page - 1) * recordsPerPage) + "/";
		
		if (id == -1) {
			path += extension;
		}
		else {
			path += id + extension;
		}

		document.location = path;
	}
}

function goToAnotherPageSearch(max, recordsPerPage, baseUrl)
{
	var page = document.getElementById("pageToGo").value * 1;

	if (!isNaN(page) && page <= max && page > 0) {
		document.location = baseUrl + "&start=" + ((page - 1) * recordsPerPage);
	}
}