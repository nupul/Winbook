<!DOCTYPE HTML>
<html>
	<head profile="http://www.w3.org/2005/10/profile">
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>${title}</title>
		<link rel="icon" type="image/png" href="${baseRef}/images/site-icon.png"/>
		<link rel="stylesheet" type="text/css" href="${baseRef}/styles/winbookwall.css" />
		<link rel="stylesheet" type="text/css" href="${baseRef}/styles/ui-lightness/jquery-ui-1.8.21.custom.css" />

		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/jquery-ui-1.8.21.custom.min.js">
		</script>
		<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.3.3/underscore-min.js"> <!--${baseRef}/scripts/underscore.js"> -->
		</script>
		<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"> <!-- ${baseRef}/scripts/backbone.js"> -->
		</script>
		<script type="text/javascript" src="http://code.highcharts.com/highcharts.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/autogrow.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/json2.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/mColorPicker/javascripts/mColorPicker.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/jQLabel/jqlabel.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/wincInit.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/winc.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/functions.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/wall.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/wallRouter.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/benefits.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/TOPSIS.js">
		</script> 
		<script type="text/javascript" src="${baseRef}/scripts/analytics.js">
		</script>
		
		<script type="text/html" id="winConditionTemplate">
			<li	class="post wincondition" data-winbook-status="open" data-winbook-wcid="<%= wcid %>" data-winbook-itemid="<%= wcid %>">
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

		<script type="text/html" id="goalTemplate">
			<li class="post goal" data-winbook-goalid=<%= id %> data-winbook-weight=<%= weight %> data-winbook-prioritization=<%= forPrioritization %>>
				<div class="postContainer">
					<div class="postDetailsContainer" style="width: 90%;padding: 5px;margin-left: 20px;">
						<div class="goalHoverMenu">
							<a class="prioritize">
								<img class="hoverButton prioritizeIcon" src="${baseRef}/images/forPrioritization.png">
							</a> 
							<a class="delete">
								<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png">
							</a>
						</div>
						<ul class="postDetails">
							<li>
								<span class="title"><%= title %></span>
								<span class="weightCounter"></span>
							</li>
							<li class="slider">
								<div class="successSlider"></div>
							</li>
							<li class="details">
							</li>
						</ul>
					</div>
				</div>
			</li>
		</script>
			
		<script type="text/html" id="loadingTemplate">
			<h2>Loading</h2>
			<img src = "../../images/ajax-loader-2.gif"/>
		</script>	
		
		<script type="text/html" id="vision"><#if vision?has_content>${vision}</#if></script>	
				
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
		
		$(document).ready(function() {
			$('body').ajaxError(function(event, response, settings, exception){
				if(response.status==403)
					alert("You are not authorized to perform this request");
			});
			WinbookPageData.init();
			WallApp.init(); 
			WinConditionApp.createLabelManager();
			WallApp.startRouter('/${baseRef}/projects/${project}');		
		});
	
		</script>
		
	</head>
	<body>
		
		<div id="bluebar">
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
						<a href="#WinConditions">Win Conditions</a>
					</li>
					<li>
						<a href="#Benefits">Benefits </a>
					</li>
					<li>
						<a href="#Analytics">Analytics </a>
					</li>
				</ul>
			</div>
			<div id="main">
				
			</div> <!-- End #main div -->
			<div id="rightColumn">
				<h4>Categories:</h4>
				<span id="categoryLength" style="height:1.3em; display:block; margin:0; font-size:small;"> </span>
				<input type="text" id="categoryName" value=""/>
				<input id="categoryColor" value="#3b5998" data-text="hidden" data-hex="true"/>
				<div id="MMF-select">
					<input id="isMMF" type="checkbox"/><label>Minimum Marketable Feature</label>
				</div>
				<button id="createCategoryButton">
					Create Category
				</button>
				<ul class="categoryListing">
				<#if categories?has_content>
				<#list categories as category>					
					<#if category.MMF>
					<li class="category MMF" data-winbook-itemid = ${category.id?c} data-winbook-categoryid=${category.id?c}>
					<#else>
					<li class="category" data-winbook-categoryid="${category.id?c}">
					</#if>
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
		<input type="hidden" id="projectName" value="${project}"/>
		
		
	
	</body>
</html>

