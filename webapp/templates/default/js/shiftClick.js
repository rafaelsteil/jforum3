// Shift-click Checkboxes
// By Jason Davis, jasonkarldavis@gmail.com
function enhanceCheckboxes(parent) {
	parent._shiftKey  = false;
	parent._lastCheck = null;

	parent.addEventListener("click", function(event) {
		parent._shiftKey = event.shiftKey;
	}, false);

	parent.addEventListener("change", function(event) {
		var currentIndex, checkbox, start, end;
		
		if (event.target.nodeName.toUpperCase() == "LABEL") {
			if (event.target.hasAttribute("for"))
				checkbox = document.getElementById(event.target.getAttribute("for")) ||
				           document.getElementsByName(event.target.getAttribute("for")).item(0);
			else
				checkbox = event.target.getElementsByTagName("input").item(0);
		}
		else
			checkbox = event.target;

		if (parent._shiftKey && parent._lastCheck != null) {
			for (var i = 0; i < checkbox.form.elements.length; i++) {
				if (checkbox.form.elements[i] == checkbox) {
					start = checkbox;
					end   = parent._lastCheck;
					currentIndex = i;
					break;
				}
				else if (checkbox.form.elements[i] == parent._lastCheck) {
					start = parent._lastCheck;
					end   = checkbox;
					currentIndex = i;
					break;
				}
			}

			for (currentIndex += 1; currentIndex < checkbox.form.elements.length && checkbox.form.elements[currentIndex] != end; currentIndex++) {				
				if (checkbox.form.elements[currentIndex].type == "checkbox") {
					checkbox.form.elements[currentIndex].checked = true;
				}
			}
			
			parent._lastCheck = end;
		}
		else
			parent._lastCheck = checkbox;

		parent._shiftKey = false;
	}, false);
}