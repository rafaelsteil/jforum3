<%@ taglib prefix="jforum" uri="http://www.jforum.net/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<% 
response.setHeader("Content-Type","text/javascript");
response.setHeader("Cache-Control","public, max-age=2387898");
%>

var count    = 0;
var lastId   = 0; 
var totalEntry = <jforum:settings key="shoutbox.disply.shouts"/>;
var refreshTime=<jforum:settings key="shoutbox.refresh.time"/>;
var maxWidth = 500;
var minWidth = 200;
var shoutBoxId = 0;
var shoutBox;
var shoutBoxList;
var button;
var isNewMsg = false;//no need flash at first time

function prepare(shout) {
  var string = '<div class="shoutbox-list" id="list-'+shout.id+'">';

  if(shout.canDel)
	string += '<span class="shoutbox-list-del"/>';

  string +=  '<span class="shoutbox-list-time">'+shout.date+'</span>'
      + '<span class="shoutbox-list-nick">'+shout.name+':</span>'
      + '<span class="shoutbox-list-message">'+shout.message+'</span>'
      + '</div>';

  return string;
}

function success(response, status)  { 
  if(status == 'success') {
	var shoutBeans = response.shoutBeans;
	var errMsgs    = response.errMsgs;
	if(errMsgs.length){
		alert(errMsgs.join("\n"));
	}
	if(shoutBeans.length){
	    shoutBox.find('#shoutbox-response').html("<img src=<jforum:templateResource item='/shoutbox/images/accept.png'/> />");
	    shoutBox.find('#shout-message').attr("value", "").css("background", "white").focus();
	    addShout(response.shoutBeans[0],true);
	}
    timeoutID = setTimeout(refresh, refreshTime);
  }
}

function showNewMessge(){
	if(isMinimize() && isNewMsg){
		shoutBox.animate({opacity: 0.1,queue: false}, function(){
			shoutBox.animate({opacity: 1,queue: false}, function(){
		    	  showNewMessge();
		      });
	      });
	}
}

function hideNewMessage(){
}

function addShout(shout,own){
	lastId = shout.id;
	count++;

	if(!own && isMinimize()){//insert a new shout
		showNewMessge();
	}

    shoutBoxList.prepend(prepare(shout));
	
	var thisList = $('#list-' + shout.id);
    thisList.fadeIn('slow').find('.shoutbox-list-del').click(function(){
		$.getJSON("<jforum:url address='/shoutbox/delete'/>?shoutId="+shout.id, function(json) {
			var errMsgs    = json.errMsgs;
			if(errMsgs.length){
				alert(errMsgs.join("\n"));
			}else{
				 thisList.fadeOut("slow").remove();
			}
		  });
	});
    
    if(count > totalEntry){
    	shoutBoxList.find(":last").fadeOut("slow").remove();
    	count--;
    }
}

function validate(formData, jqForm, options) {
	var txtMessage = shoutBox.find("#shout-message");
	if (!txtMessage.val()) {
		txtMessage.css("background", "red");
		return false; 
	} 
    
  shoutBox.find('#shoutbox-response').html("<img src=<jforum:templateResource item='/shoutbox/images/act_indicator.gif'/> />");
  clearTimeout(timeoutID);
}

function refresh() {
  $.getJSON("<jforum:url address='/shoutbox/read'/>?lastId="+lastId+"&shoutBoxId="+shoutBoxId+"&rand="+Math.floor(Math.random() * 1000000), function(json) {
	var shoutBeans = json.shoutBeans;
	var errMsgs    = json.errMsgs;
	if(errMsgs.length){
		alert(errMsgs.join("\n"));
	}
    if(shoutBeans.length) {
      for(var i= shoutBeans.length -1; i >= 0; i--) {
    	  addShout(shoutBeans[i],false);
      }
      lastId   = shoutBeans[0].id;
    }
    isNewMsg = true; 
  });
  timeoutID = setTimeout(refresh, refreshTime);
}

function isMinimize(){
	return shoutBoxList.is(":hidden");
}

function toggleShoutBox(){
    var isHidden = isMinimize();
	
	if(isHidden){
		width = maxWidth;
		left  = "0";
		hideNewMessage();
	}else{
		width = minWidth;
		left  = "300px";
	}

	shoutBox.animate( { "width": width + "px", "left":left }, { queue:false, duration:500 });
	shoutBoxList.animate( { "height": "toggle" }, { queue:false, duration:500 });
	shoutBox.children(".topictitle").animate( {"height": "toggle"}, { queue:false, duration:500 });
	button.toggleClass("maximize-button").toggleClass("minimize-button");	
}

// wait for the DOM to be loaded 
$(document).ready(function() {
	
	shoutBox = $("#shout-box");
	shoutBoxList = shoutBox.children("#shoutbox-list");
	button  = shoutBox.find("#button-shout-box");
	
    var options = { 
      dataType:       'json',
      beforeSubmit:   validate,
      success:        success
    }; 

	var shoutBoxForm = $('#shoutbox-form');

    shoutBoxForm.ajaxForm(options);
    
    button.click(toggleShoutBox);
    
    shoutBoxId = shoutBoxForm.find('#shoutBoxId').val();
    timeoutID = setTimeout(refresh, 100);
});