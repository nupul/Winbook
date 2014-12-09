<!DOCTYPE HTML>
<html>
	<head><meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>${title}</title>
		<link rel="stylesheet" type="text/css" href="${baseRef}/styles/winbookwall.css" />
		<link rel="stylesheet" type="text/css" href="${baseRef}/styles/ui-lightness/jquery-ui-1.8.21.custom.css" />

		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/jquery-ui-1.8.21.custom.min.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/autogrow.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/mColorPicker/javascripts/mColorPicker.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/jQLabel/jqlabel.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/json2.js">
		</script>
		<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.3.3/underscore-min.js"> <!--${baseRef}/scripts/underscore.js"> -->
		</script>
		<script type="text/javascript" src="http://backbonejs.org/backbone.js"> <!-- ${baseRef}/scripts/backbone.js"> -->
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/functions.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/wall.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/TOPSIS.js">
		</script>
		<script type="text/html" id="winConditionTemplate">
			<li	class="post wincondition item" data-winbook-status="open" data-winbook-wcid="<%= wcid %>" data-winbook-itemid="<%= wcid %>">
				<div class="checkbox">
					<input type="checkbox" class="wcCheckbox">
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
					<div class="hoverMenu">
						<a class="edit" data-winbook-edit="wincondition">
						<img class="hoverButton editIcon" src="${baseRef}/images/editpencil.png"/>
						</a>
						<a class="delete" data-winbook-delete="wincondition">
						<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
						</a>
					</div>
						<ul class="postDetails">
							<li>
								<ul class="categorizationDetails">
									<li>
										<strong>Win Condition</strong> (<span class="wcid">WC_<%= wcid %></span>):
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
										<a href="#" class="actionNavBarLink" data-winbook-action="Agree">Agree</a>
										<span class="dotSeparator">.</span>
									</li>
									<#if role == "shaper">
										<li class="potentialAgreement">
											<a class="actionNavBarLink" data-winbook-action="MayAgree">Potentially Agreeable</a>
											<span class="dotSeparator">.</span>
										</li>
									</#if>
									<li>
										<a href="#" class="actionNavBarLink" data-winbook-action="Comment">Comment</a>
										<span class="dotSeparator">.</span>
									</li>
									<li>
										<a href="#" class="actionNavBarLink" data-winbook-action="Issue">Raise Issue/Concern/Risk</a>
									</li>
								</ul>
							</li>
							<li class="Agreements"></li>
						</ul>
					</div>
				</div>
				<div class="clear">
				</div>
				<ul class="_listOfIssues">
				</ul>
			</li>					
		</script>
		
		<!-- Template of the input form(s) for either Issue, option or comment-->
		<script type="text/html" id="dataFormTemplate">
			<li class="post <%= postType %> dataForm">
				<div class="postContainer">
					<div class="avatarColumn">
						<a href="#">
							<img src="<%= avatar %>"/>
						</a>
						<div class="authorName">
							<a href="#"><%= displayName %></a>
						</div>
					</div>
					
					<div class="postDetailsContainer">
						<div style="display:none;" class="hoverMenu">
							<a class="edit" data-winbook-edit="issue">
								<img class="hoverButton editIcon" src="${baseRef}/images/editpencil.png"/>
							</a>
							<a class="delete" data-winbook-delete="issue">
								<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
							</a>
						</div>
						<div class="dataArea">
							<textarea class="dataArea" name="data"></textarea>
							<button class="addDataButton"  disabled="disabled" data-winbook-adddata="<%= postTypeButtonText%>" >
								Add <%= postTypeButtonText %>
							</button>
							<button class="cancelDataButton">
								Cancel
							</button>
							<span class="charcount"></span>
							<div class="clear"></div>
						</div>    
					</div>
	  			</div>
			</li>
		</script>
		
		<script type="text/html" id="categoryTemplate">
			<li class="category" data-winbook-categoryid="<%= catId %>">
				<div class="hoverMenu">
					<a class="delete">
					<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
					</a>
				</div>
				<input class="labelColorPicker" value="<%= catCode %>" data-text="hidden" data-hex="true"/>
				<div class="labelCheckBox labelColorPicker"></div>
				<div class="labelDescriptor">
					<%= catName %>
				</div>
				<div class="clear"></div>
			</li>
		</script>
			
			
		<script type="text/html" id="labelViewTemplate">
			<li class="categoryLabel" data-winbook-categoryid="<%= labelId %>" data-winbook-labelcolor="<%= labelColor %>" title="<%= labelName %>">
				<%= labelName %>
			</li>	
		</script>
		
		<script type="text/html" id="issuePostDetailsTemplate">
		<ul class="postDetails">
			<li><strong>Issue</strong> (<span class="issueid">Issue Iss_<%= id %></span>):<li>
			<li class="postData"><%= issue %></li>
			<li>
				<ul class="actionsNavBar">
					<li><a class="actionNavBarLink" data-winbook-action="CloseIssue">Close Issue</a><span class="dotSeparator">.</span></li>
					<li><a class="actionNavBarLink" data-winbook-action="Comment">Comment</a><span class="dotSeparator">.</span></li>
					<li><a class="actionNavBarLink" data-winbook-action="Option">Suggest Option/Alternative(s)</a></li>
				</ul>
			</li>
		</ul>
		</script>
		
		<script type="text/html" id="optionPostDetailsTemplate">
		<ul class="postDetails">
			<li><strong>Option</strong> (<span class="optionid">Opt_<%= id %></span>):<li>
			<li class="postData"><%= option %></li>
			<li>
				<ul class="actionsNavBar">
					<li><a class="actionNavBarLink" data-winbook-action="Agree">Agree</a><span class="dotSeparator">.</span></li>
					<li><a class="actionNavBarLink" data-winbook-action="Comment">Comment</a></li>
				</ul>
			</li>
			<li class="Agreements"></li>
		</ul>
		</script>
				
		<script type="text/javascript">
		
		/******************Begin Microtemplate Engine**********************/
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
		/*******************End Microtemplate Engine**********************/
		
		/*
		 * Global namespace that will cache wallName, userDisplayName and userAvatar etc.,
		 */
		   var WinbookPageData ={};
		
			WinbookPageData.LoadingAnimation = Backbone.View.extend({
								
				initialize: function() {
					var DOMElement = this.options.DOMElement;
					var pos = DOMElement.position();
					var _width = DOMElement.outerWidth(true);
					var _height = DOMElement.outerHeight(true);
					
					$(this.el).css({
						"position":"absolute", 
						"left" : pos.left+"px",
						"top"  : pos.top+"px",
						"z-index" : 9999,
						"background-color":"#FFFCCC",
						"width" : _width+"px",
						"height": _height+"px",
						"background": "#FFFFFF url(${baseRef}/images/loading_round.gif) no-repeat center center"
						});
					//DOMElement.parent().append(this.el);
					DOMElement.parent().append(this.el);
						
					_.bindAll(this, 'render','unrender');
					this.render();
				},
				
				render: function() {
					$(this.el).show();
				},
				
				unrender: function() {
					$(this.el).remove();
				}
				
			});
		
		/******************************* JQuery's Ready function: ****************************************/
		$(document).ready( function() {
			
			WinbookPageData.wallName = $('#wallName').val();
			WinbookPageData.userDisplayName = $('#userDisplayName').val();
			WinbookPageData.userAvatar = $('#userAvatar').val();
			WinbookPageData.userRole = $('#userRole').val();
			WinbookPageData.maxWinConditionLength = 65535;
			WinbookPageData.maxCategoryLength = 60;
			WinbookPageData.minWinConditionLength = 15;
			WinbookPageData.minCategoryLength = 3;
			WinbookPageData.defaultWCTextAreaValue = "Enter your Win Condition/Requirement/Expectation here...";
			WinbookPageData.hoverMenuZIndex = 10;

			//var someAnim = new WinbookPageData.LoadingAnimation({DOMElement:$('#wincondition')});
				
			//handle clicks on the actions nav bar
			// $('.actionsNavBar').delegate('.actionNavBarLink','click', function(e){
			// 	actionNavBarHandler($(this));
			// 	return false;
			// });
			
			//$('.wincondition').draggable();

			// $('.slider').each(function(){
			// 	var val = $(this).data('winbook-slidervalue');
			// 		$(this).slider({
			// 		orientation:'vertical',
			// 		value:$(this).data("winbook-slidervalue"),
			// 		range:'min',
			// 		min:0,
			// 		max:100
			// 	});
			// });
			
			
			//Cancel event for the issue/option/comment dialog:
			$('.listOfPosts').delegate('.cancelDataButton','click', function(e){
				e.stopPropagation();
				var node = $(this).closest('.dataForm');
				node.fadeOut("fast",function(){
						$(this).remove();
					});
			
			});
			
			//post 'data' - issue/option/comment to wall
			$('.listOfPosts').delegate('.addDataButton','click', function(e){
				e.stopPropagation();
				postDataToWall($(this).data("winbook-adddata"), $(this).parents('.dataArea'));
			});
			
			//enable text area for autoresize
			configTextArea();
	
			//draw labels			
			$('.categoryLabel').each(function(){
				drawLabel(this);
			});
			
			//instantiate color picker
			$('input#categoryColor, .labelColorPicker').mColorPicker({
				imageFolder:'${baseRef}/images/mColorPicker/'
			});
			$.fn.mColorPicker.init.replace = false;	
			$.fn.mColorPicker.init.enhancedSwatches = false;
			
			$('.listOfPosts').delegate('.postDetailsContainer','hover',function(e){
				e.stopPropagation();
				
				if($(this).find('textarea').length === 0) //don't show hover menu if an edit/update form is being displayed.
					$(this).find('.hoverMenu').toggle( e.type === 'mouseenter');
			});
			
			$('#createCategoryButton').attr('disabled',true);
			$('#categoryName').keyup(function(){
				
				length = $(this).val().length;
				minLength = WinbookPageData.minCategoryLength;
				maxLength = WinbookPageData.maxCategoryLength;
				categoryButton = $('#createCategoryButton');
				counterSpan = $('#categoryLength');
				
				textLengthCounter(length, minLength, maxLength, counterSpan, categoryButton);
			});
			
			
			//get category name and color value
			$("#createCategoryButton").bind('click',function(){
				var showBusy = new WinbookPageData.LoadingAnimation({DOMElement:$(this)});
				addNewCategory(showBusy);
			});
	
	
			$('.dataArea > textarea').live('keyup', function(){
				length = $(this).val().length;
				var minLength = WinbookPageData.minWinConditionLength;
				var maxLength = WinbookPageData.maxWinConditionLength;
				var counterSpan = $('.charcount',$(this).parent());
				var postDataButton = $('.addDataButton, .updateButton',$(this).parent());
				
				textLengthCounter(length, minLength, maxLength, counterSpan, postDataButton);
				
			});
			
			$('#wincondition').keyup(function(){
				var length = $(this).val().length;				
				var minLength = WinbookPageData.minWinConditionLength;
				var maxLength = WinbookPageData.maxWinConditionLength;
				var counterSpan = $('#winconditionLength');
				var postWCButton = $('#postwincondition');
				
				textLengthCounter(length, minLength, maxLength, counterSpan, postWCButton);
			});			
			
	
			//send win condition to server
			$('#postwincondition').bind('click', function() {
	
				var wincondition = escape($('#wincondition').val());	
				var showBusy = new WinbookPageData.LoadingAnimation({DOMElement:$(this)});
	
				$.ajax({
					type:"POST",
					url:WinbookPageData.wallName+"/WinConditions",
					data:"wincondition="+wincondition,
					dataType:"json",
					success:function(result, status){
						var content = $('#wincondition').val();	
			
						var newWinCondition = tmpl("winConditionTemplate", {wcid:result.wcid, text:content, displayName:WinbookPageData.userDisplayName, avatar:WinbookPageData.userAvatar});
				
						$('.listOfPosts').children(':first').before(newWinCondition);
						
						window.listOfPostsView.addWinCondition($('.listOfPosts').children(':first'));
						
						//reset text area for posting new win condition
						$('textarea#wincondition').attr('disabled',false);
						$('textarea#wincondition').val(WinbookPageData.defaultWCTextAreaValue);
						$('textarea#wincondition').css("color","gray");
						$('#postwincondition').attr('disabled',true);
						showBusy.unrender();
						if($('#infomessage').is(':visible'))
							$('#infomessage').hide();
					},
					error: function(xhr, status)
					{
						if(status==303 || status==302)
							window.location.replace("/");
							
						alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
						$('textarea#wincondition').attr('disabled',false);
						showBusy.unrender();
					}
				});
				
				//$('#wincondition').attr('disabled',true);
				
					
			});
			
			
			//capture event to delete selected data item and perform ajax request for the same.
			$('a.delete').live('click', function(e) {
		
				e.stopPropagation();
				
				var deleteItem = $(this).closest('.post');
				
				//base URL
				var URL = WinbookPageData.wallName+"/WinConditions/"; 
				
				if(deleteItem.hasClass("wincondition"))
				{						
					if(confirm("Deleting this win condition will delete all associated Issues, Options & Comments! This action CANNOT BE UNDONE. Are you sure you want to delete it?"))
					{
						wcid = $(this).parents('.wincondition').data("winbook-wcid");
						URL = URL + wcid;
					}
					
					else
						return;
				}
				else if(deleteItem.hasClass("issue"))
					if(confirm("Deleting this Issue will delete all associated Options & Comments (if any). This action CANNOT BE UNDONE. Are you sure you want to delete it?"))
					{												
						issueId = $(this).parents('.issue').data("winbook-issueid");
						wcid = $(this).parents('.wincondition').data("winbook-wcid");
						URL = URL+wcid+"/Issues/"+issueId;
							
					}
					else
						return;
				else if(deleteItem.hasClass("option"))
				{
					optionId = $(this).parents('.option').data("winbook-optionid");
					issueId = $(this).parents('.issue').data("winbook-issueid");
					wcid = $(this).parents('.wincondition').data("winbook-wcid");
					URL = URL+wcid+"/Issues/"+issueId+"/Options/"+optionId;
				}
					
				else
					return;
				
				$.ajax({
							type:"DELETE",
							url:URL,
							success: function(result, status) {
								//deletePost(deleteItem);
								deleteItem.fadeOut("slow",function(){
									$(this).remove();
								});
							},
							error: function(xhr, status)
							{
								alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
							}
							
						});
				
				
				
				
			});///:~ END live('click') for a.delete
			
			
			$('a.edit').live('click', function(){
				
				var editItem = $(this).data("winbook-edit");
				var currentNode = $(this).closest('li.post');
				var dataToEdit = currentNode.find('.postData').eq(0).text();				
			
				var editForm = '<div class="dataArea"><textarea class="editForm">'+$.trim(dataToEdit)+'</textarea>'
									+'<button class="updateButton">Update</button>'
									+'<button class="cancelUpdateButton">Cancel</button>'
									+'<span class="charcount"></span></div>'
									+'<div class="clear"></div>'
									+'</div>'
					
				//The first postDetailsContainer encountered...	
				var postDetailsContainer = $('.postDetailsContainer',currentNode).eq(0);
				postDetailsContainer.children('.postDetails').hide().end().append(editForm);
				postDetailsContainer.children('.hoverMenu').css('z-index',-WinbookPageData.hoverMenuZIndex); //show/hide won't work since it'll be overriden by mouserover/mouseout event handlers
				postDetailsContainer.find('textarea').focus();
			});
			
			$('.cancelUpdateButton').live('click',function(){
				//The hovermenu immediately 'above' the dataArea - the first such child of the first parent from the cancelButton
				$(this).parent('.dataArea').siblings('.hoverMenu').css('z-index',WinbookPageData.hoverMenuZIndex);
				$(this).parent('.dataArea').siblings('.postDetails').show().end().remove();
			});
			
			$('.updateButton').live('click', function(){
				
				var currentNode = $(this).closest('.post');
				
				updatePost(currentNode);
								
			});
			
		
		window.labelMenuView = new LabelMenuView();
		window.listOfPostsView = new ListOfPostsView({labelManager:window.labelMenuView});
		window.menuBarView = new MenuBarView({labelMenu:window.labelMenuView, allPosts:window.listOfPostsView});

	}); ///:~ END $.ready();
		
		
		
		
		/*************************************************************************************************************** 
		 * 
		 * END - JQuery's Ready function 
		 * 
		 ***************************************************************************************************************/
		
	
	</script>
		
	</head>
	<body>
		
		<div id="bluebar">
		</div>
				
		<div id="TSVLayer">
			<span style="color:white;">Copy/Paste the following Tab-delimited values into a spreadsheet for further analysis</span>
			<textarea id="TSVData"></textarea>
			<br/>
			<button>Done</button>
		</div>
		<div id="container">

			<div class="clear">
			</div>
			
			<div id="header">
				<h1 class="logo">Winbook</h1>
				<ul id="navbar" name="navbar">
					<li>
						<a href="#">Home</a>
					</li>
					<li>
						<a href="#">Profile</a>
					</li>
					<li>
						<a href="${baseRef}/Logout">Logout</a>
					</li>
				</ul>
			</div>
			<div id="leftColumn">
				<div class="clear">
				</div>
				<div class="profileAvatar">
				<#if user.avatarURL == "DEFAULT_AVATAR">
					<img src="${baseRef}/images/defaultavatar.jpg"/>
				<#else>
					<img src="${user.avatarURL}">
				</#if>	
				</div>
				<div class="profileUser">
					${user.displayName}
					<br/>

					<a href="#">Edit my profile</a>
				</div>
				<div class="clear">
				</div>
				<hr/>

				<ul class="leftMenu">
					<li class="header">
						Wall(s):
					</li>
					<li>
						Win Conditions
					</li>
					<li>
						Benefits
					</li>
					<li class="header">
						Actions
					</li>
					<li>
						View Equilibrium
					</li>
					<li>
						Prioritization
					</li>
				</ul>
			</div>
			<div id="main">
				<div id="postArea">
						<textarea id="wincondition">Enter your Win Condition/Requirement/Expectation here...</textarea>
						<span id="winconditionLength"></span>
						<button id="postwincondition" disabled="true">Post</button>
						 
				</div>
				<div class="clear">
				</div>
				
				<div id="menuBar">
					<ul>
						<li class="masterCheckbox"><input type="checkbox" id="checkAll"/></li>
						<li class="menuItem" data-winbook-action="categorize">Categorize</li>
						<li class="menuItem" data-winbook-action="toggleVisibility">Show/Hide <span>&#9660;</span></li>
						<li class="menuItem" data-winbook-action="showEquilibrium">Equilibrium</li>
						<li class="menuItem" data-winbook-action="exportTSV">Export TSV</li>
						<li class="menuItem" data-winbook-action="prioritize">Prioritize</li>
					</ul>
				</div>
				
				<div class="clear">
				</div>
				
				
				<ul class="listOfPosts">
					<#if winconditions?has_content>
					<#list winconditions as wc>

					<!-- Start of win condition Post-->
					<li class="post wincondition item" data-winbook-status="${wc.status}" data-winbook-wcid="${wc.id?c}" data-winbook-itemid=${wc.id?c}>
					
						<div class="checkbox">
							<input type="checkbox" class="wcCheckbox">
						</div>
						<div class="postContainer">
							<div class="avatarColumn">
								<a href="#">
								<#if wc.author.avatarURL == "DEFAULT_AVATAR">
									<img src="${baseRef}/images/defaultavatar.jpg"/>
								<#else>
									<img src="${wc.author.avatarURL}">
								</#if>
								<!-- Need to add author's avatar's url-->
								</a>
								<div class="authorName">
									<a href="#">${wc.author.displayName}</a>
								</div>
							</div>
							<div class="postDetailsContainer">
								<div class="hoverMenu">
									<a class="edit" data-winbook-edit="wincondition">
									<img class="hoverButton editIcon" src="${baseRef}/images/editpencil.png"/>
									</a>
									<a class="delete" data-winbook-delete="wincondition">
									<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
									</a>
								</div>
								<ul class="postDetails">
									<li>
										<ul class="categorizationDetails">
											<li>
												<strong>Win Condition</strong> (<span class="wcid">WC_${wc.id?c}</span>):
											</li>
											<#if wc.categories??>
											<#list wc.categories as category>
											<li class="categoryLabel" data-winbook-categoryid="${category.id?c}" data-winbook-labelcolor="${category.hexColorCode}" title="${category.categoryName}">
												${category.categoryName}
											</li>											
											</#list>
											</#if>
										</ul>
									</li>
									<div class="clear">
									</div>
									<li class="postData">
										${wc.winCondition}
									</li>
									<li>
										<ul class="actionsNavBar">
											<li>
												<a class="actionNavBarLink" data-winbook-action="Agree">Agree</a>
												<span class="dotSeparator">.</span>
											</li>
											<#if role == "shaper">
												<li class="potentialAgreement" <#if wc.status="maybe">style="display:none;"</#if>>
													<a class="actionNavBarLink" data-winbook-action="MayAgree">Potentially Agreeable</a>
													<span class="dotSeparator">.</span>
												</li>
											</#if>
											<li>
												<a class="actionNavBarLink" data-winbook-action="Comment">Comment</a>
												<span class="dotSeparator">.</span>
											</li>
											<li>
												<a class="actionNavBarLink" data-winbook-action="Issue">Raise Issue/Concern/Risk</a>
											</li>
										</ul>
									</li>
									<li class="Agreements">
									<#if wc.agreements??>
										<ul class="stakeholdersAgreeing">
											<#list wc.agreements as agreement>
												<li data-winbook-agreementid="${agreement.id?c}">${agreement.author.displayName}<#if (agreement_has_next)>,&nbsp;</#if></li>
											</#list>
											<#if (wc.agreements?size) != 0>
												<li>&nbsp;<em>agree to this</em></li>
											</#if>
											<#if wc.status=="maybe">
												<li class="mark"><em>(Marked for potential agreement by shaper)</em></li>
											</#if>
										</ul>
										
									<#elseif wc.status=="maybe">
										<ul class="stakeholdersAgreeing">
											<li class="mark"><em>(Marked for potential agreement by shaper)</em></li>
										</ul>
										
									</#if>
									</li>
								</ul>
							</div>
						</div>
						<div class="clear">
						</div>
						<ul class="_listOfIssues">
							<#if wc.issues??>
							<#list wc.issues as issue>
								<!-- Start of Issue Post -->
								<li class="post issue" data-winbook-status="${issue.status}" data-winbook-issueid="${issue.id?c}">
									<div class="postContainer">
	
										<div class="avatarColumn">
	
											<a href="#">
											<#if issue.author.avatarURL == "DEFAULT_AVATAR">
												<img src="${baseRef}/images/defaultavatar.jpg"/>
											<#else>
												<img src="${issue.author.avatarURL}">
											</#if>
											</a>
											<div class="authorName">
												<a href="#">${issue.author.displayName}</a>
											</div>
										</div>
										<div class="postDetailsContainer">
										<div class="hoverMenu">
											<a class="edit" data-winbook-edit="issue">
											<img class="hoverButton editIcon" src="${baseRef}/images/editpencil.png"/>
											</a>
											<a class="delete" data-winbook-delete="issue">
											<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
											</a>
										</div>
											<ul class="postDetails">
	
												<li>
													<strong>Issue</strong> (<span class="issueid">Iss_${issue.id?c}</span>):
												</li>
												<li class="postData">
													${issue.issue}
												</li>
												<li>
													<ul class="actionsNavBar">
														<li>
														<#if role == "shaper">
															<#if issue.status=="open">
																<a class="actionNavBarLink" data-winbook-action="CloseIssue">Close Issue</a>
															<#elseif issue.status=="closed">
																<a class="actionNavBarLink" data-winbook-action="OpenIssue">Open Issue</a>
															</#if>	
														</#if>
														<span class="dotSeparator">.</span>
														</li>
														<li>
															<a class="actionNavBarLink" data-winbook-action="Comment">Comment</a>
															<span class="dotSeparator">.</span>
														</li>
														<li>
															<a class="actionNavBarLink <#if issue.status=="closed">noOptionsAllowed</#if>" data-winbook-action="Option">Suggest Option/Alternative(s)</a>
														</li>
													</ul>
												</li>
											</ul>
										</div>
									</div>
									<div class="clear">
									</div>
									
									<ul class="_listOfOptions">
										<#if issue.options??>
										<#list issue.options as option>
										<!-- Start of Option Post -->
										<li class="post option" data-winbook-status="${option.status}" data-winbook-optionid="${option.id?c}">
											<div class="postContainer">
	
												<div class="avatarColumn">
													<a href="#">
													<#if option.author.avatarURL == "DEFAULT_AVATAR">
														<img src="${baseRef}/images/defaultavatar.jpg"/>
													<#else>
														<img src="${option.author.avatarURL}">
													</#if>
													</a>
													<div class="authorName">
														<a href="#">${option.author.displayName}</a>
													</div>
												</div>
												<div class="postDetailsContainer">
													<div class="hoverMenu">
														<a class="edit" data-winbook-edit="option">
														<img class="hoverButton editIcon" src="${baseRef}/images/editpencil.png"/>
														</a>
														<a class="delete" data-winbook-delete="option">
														<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
														</a>
													</div>
													<ul class="postDetails">
														<li>
															<strong>Option</strong> (<span class="optionid">Opt_${option.id?c}</span>):
														</li>
														<li class="postData">
															${option.option}
														</li>
														<li>
															<ul class="actionsNavBar">
																<li>
																	<a class="actionNavBarLink" data-winbook-action="Agree">Agree</a>
																	<span class="dotSeparator">.</span>
																</li>
																<li>
																	<a class="actionNavBarLink" data-winbook-action="Comment">Comment</a>
																</li>
															</ul>
														</li>
														<li class="Agreements">
														<#if option.agreements??>
															<ul class="stakeholdersAgreeing">
															<#list option.agreements as agreement>
																<li data-winbook-agreementid="${agreement.id?c}">${agreement.author.displayName}<#if (agreement_has_next)>,&nbsp;</#if></li>
															</#list>
															<#if (option.agreements?size) != 0>
																<li>&nbsp;<em>agree to this</em></li>
															</#if>
															</ul>
														</#if>
														</li>
													</ul>
												</div>
											</div>
											<div class="clear">
											</div>
										</li> <!--End of Option Post-->
										</#list>
										</#if>
									</ul> <!-- End list of options for issue-->
									
								</li> <!-- End of Issue Post -->
	
							</#list>	
							</#if>	
						</ul> <!--end list of issues for win condition-->
						
					</li> <!--end Win condition post-->
					</#list>
					<#else> <!-- Inform user about zero win conditions -->
						<li class="post" style="width:90%; margin-left:auto; margin-right:auto; color:gray;" id="infomessage">
							There are no win conditions for this project. Use the above text box to add a win condition(s)
						</li>
					</#if>
				</ul> <!-- End listOfPosts-->
			</div> <!-- End #main div -->
			<div id="rightColumn">
				<h4>Categories:</h4>
				<span id="categoryLength" style="height:1.3em; display:block; margin:0; font-size:small;"> </span>
				<input type="text" id="categoryName"/>
				<input id="categoryColor" value="#3b5998" data-text="hidden" data-hex="true"/>
				<input id="isMMF" type="checkbox"/><label>Minimum Marketable Feature</label>
				<button id="createCategoryButton">
					Create Category
				</button>
				<ul class="categoryListing">
				<#if categories?has_content>
				<#list categories as category>					
					<li class="category <#if category.MMF>MMF</#if>" data-winbook-categoryid="${category.id?c}">
						<div class="hoverMenu">
							<a class="delete">
							<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
							</a>
						</div>
						<input class="labelColorPicker" value="${category.hexColorCode}" data-text="hidden" data-hex="true"/>
						<div class="labelCheckBox labelColorPicker"></div>
						<div class="labelDescriptor">
							${category.categoryName}
						</div>
						<div class="clear"></div>
					</li>
				</#list>
				</#if>
				<li class="category all">
						View All
					</li>
				<li id="categoryMenuButtons">
					<button class="menuItem categoryMenuButton" data-winbook-action="applyCategorization">Apply</button>
					<button class="menuItem categoryMenuButton" data-winbook-action="cancelCategorization">Cancel</button>
				</li>
					
				</ul>
			</div>
		</div>
		<input type="hidden" id="userDisplayName" value="${user.displayName}"/>
		<#if user.avatarURL == "DEFAULT_AVATAR">
			<input type="hidden" id="userAvatar" value="${baseRef}/images/defaultavatar.jpg"/>
		<#else>
			<input type="hidden" id="userAvatar" value="${user.avatarURL}"/>
		</#if>	
		
		<input type="hidden" id="wallName" value="${wall}"/>
		<input type="hidden" id="userRole" value="${role}"/>
		
		
	
	</body>
</html>
