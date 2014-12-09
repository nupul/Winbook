<script type="text/javascript" src="${baseRef}/scripts/jquery.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/autogrow.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/mColorPicker/javascripts/mColorPicker.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/jQLabel/jqlabel.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/json2.js">
		</script>
		
			
		<script type="text/javascript">

		$(document).ready( function() {
		configTextArea();

		//draw labels
		$(".category").jQLabel();
		$.fn.mColorPicker.init.replace = false;

		//instantiate color picker
		$("input#categoryColor").mColorPicker({
		imageFolder:'${baseRef}/images/mColorPicker/'
		});

		//send win condition to server
		$('#postwincondition').bind('click',function(){

		var wincondition = $('#wincondition').val();
		var wallName = $('#wallName').val();
		var postData = "wincondition="+wincondition+"&"+"wall="+wallName;

		$.ajax({
		type:"POST",
		url: "WinCondition",
		username: "nkukreja@usc.edu",
		password: "nkukreja",
		data:postData,
		dataType:"json",
		success:addNewWinCondition

		});

		$('#wincondition').attr('disabled',true);
		$('#postwincondition').attr('disabled',true);
		});

		//capture event to delete win condition and perform ajax request for the same.
		$('a[name="delete"]').live('click',function(){

		var wcidToDelete = $(this).parent().data("winbook-wcid");
		var wallName = $('#wallName').val();

		var deleteData = {"wcid":wcidToDelete, "action":"delete",wall:wallName};
		//alert("Delete Win Condition id:"+wcidToDelete);

		$.ajax({
		type:"DELETE",
		url:"WinCondition",
		username:"nkukreja@usc.edu",
		password:"nkukreja",
		contentType:"application/json; charset=utf-8",
	data:JSON.stringify(deleteData),
	success:function(result, status){
	deleteWinCondition(result, status, wcidToDelete)}

	});
	});

	});

	function deleteWinCondition(result, status, wcid) {
		//alert("Deleting WCid: "+wcid+"result = "+result+" status = "+status);

		$('[data-winbook-wcid='+wcid+']').fadeOut("slow", function() {
			$(this).remove();
		});
	}

	function addNewWinCondition(result, status) {
		//alert(result.wcid+" "+result.timestamp);

		content = $('#wincondition').val();
		userDisplayName = $('#userDisplayName').val();
		userAvatar = $('#userAvatar').val();

		var s = tmpl("winConditionTemplate", {wcid:result.wcid, text:content, displayName:userDisplayName, avatar:userAvatar});

		$('.listOfPosts').children(':first').before(s).fadeIn(1000);

		$('#wincondition').attr('disabled',false);
		$('#postwincondition').attr('disabled',false);
		textAreaDefault($('#wincondition'));

	}

	function configTextArea() {
		$('textarea#wincondition').autoResize({
			animate:false,
			extraSpace:0

		});

		textAreaDefault($('#wincondition'));

		$('textarea#wincondition').focus( function() {

			$(this).text('');
			$(this).css("color","black");
			$('#postwincondition').attr('disabled',false);

		} );
		$('textarea#wincondition').blur( function() {

			var text = $(this).val();

			if(text=="") {
				textAreaDefault($(this));
			}
		});
	}// end configTextArea()

	function textAreaDefault(textarea) {
		var defaultText = "Enter your Win Condition/Requirement/Expectation here...";
		$(textarea).text('');
		$(textarea).text(defaultText);
		$(textarea).css("color","gray");
		$('#postwincondition').attr('disabled',true);
	}

	/****************************************/
	// Simple JavaScript Templating
	// John Resig - http://ejohn.org/ - MIT Licensed
	(function() {
		var cache = {};

		this.tmpl = function tmpl(str, data) {
			// Figure out if we're getting a template, or if we need to
			// load the template - and be sure to cache the result.
			var fn = !/\W/.test(str) ?
			cache[str] = cache[str] ||
			tmpl(document.getElementById(str).innerHTML) :

			// Generate a reusable function that will serve as a template
			// generator (and which will be cached).
			new Function("obj",
			"var p=[],print=function(){p.push.apply(p,arguments);};" +

			// Introduce the data as local variables using with(){}
			"with(obj){p.push('" +

			// Convert the template into pure JavaScript
			str.replace(/[\r\t\n]/g, " ")
			.replace(/'(?=[^%]*%>)/g,"\t")
			.split("'").join("\\'")
			.split("\t").join("'")
			.replace(/<%=(.+?)%>/g, "',$1,'")
			.split("<%").join("');")
			.split("%>").join("p.push('")
			+ "');}return p.join('');");
			// Provide some basic currying to the user
			return data ? fn(data) : fn;
		};
	})();
	/****************************************/

		</script>
		
		<script type="text/html" id="winConditionTemplate">
		<li	class="post winconditionpost" data-winbook-wcStatus="open" data-winbook-wcid="<%= wcid %>">
						<a name="delete">
						<img class="hoverButton deleteButton" src="${baseRef}/images/deleteredicon.png"/>
						</a>
						<a href="#edit">
						<img class="hoverButton editButton" src="${baseRef}/images/editpencil.png"/>
						</a>
						<div class="checkbox">
							<input type="checkbox">
						</div>
						<div class="postContainer">

							<div class="avatarColumn">
								<a href="#">
								<img src="<%= avatar %>"/>
								<!-- Need to add author's avatar's url-->
								</a>
								<div class="authorName">
									<a href="#"><%= displayName %></a>
								</div>
							</div>
							<div class="postDetailsContainer">
								<ul class="postDetails">

									<li>
										<ul class="categorizationDetails">
											<li class="wcid">
												Win Condition (WC_<%= wcid %>):
											</li>
											
										</ul>
									</li>
									<div class="clear">
									</div>
									<li class="postData">
										<%= text %>
									</li>
									<li>
										<ul class="actionsNavBar">
											<li>
												<a href="#" class="actionNavBarLink">Agree</a>
												<span class="dotSeparator">.</span>
											</li>
											<li>
												<a href="#" class="actionNavBarLink">Comment</a>
												<span class="dotSeparator">.</span>
											</li>
											<li>
												<a href="#" class="actionNavBarLink">Raise Issue/Concern/Risk</a>
											</li>
										</ul>
									</li>
								</ul>
							</div>
						</div>
						<div class="clear">
						</div>
					</li>					
		</script>
		