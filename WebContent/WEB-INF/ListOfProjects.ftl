<html>
	<head profile="http://www.w3.org/2005/10/profile">
		<title>List of Projects</title>
		<link rel="icon" type="image/png" href="${baseRef}/images/site-icon.png"/>
		<link rel="stylesheet" type="text/css" href="${baseRef}/styles/winbookwall.css"  />
		<script type="text/javascript" src="${baseRef}/scripts/jquery.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/underscore.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/backbone.js">
		</script>
		
		<script type="text/javascript">
			$(document).ready(function(){
				
				var ProjectMembershipView = Backbone.View.extend({
					initialize: function() {
						_.bindAll(this,"joinProject");
					},
					
					events: {
						"click .joinButton" : "joinProject"
					},
					
					joinProject: function(event) {
						var projectName = $.trim(this.$('.projectName').text());
 						
 						$.ajax({
 							type:"POST",
 							url: "User/Membership",
 							data:"email="+$('#userEmail').val()+"&project="+projectName,
 							success: function(result, status) {
 								var tdCell = $(event.target).parent();
 								tdCell.html("Member");
 								$(event.target).remove();
 							},
 							error: function(xhr, status)
							{
								if(xhr.status === 400)
									alert("You can only join the projects allowed by the administrator. Please contact the admin if you think this is a mistake");
								else
									alert("Status: "+xhr.status+"\nAn Unknown error occured. Please contact administrator with the above status code");
							}
 							
 						});
 						
					}
				});
				
				
				//Create view for each row
				$('.projectMembershipStatus').each(function(){
					var aView = new ProjectMembershipView({el:$(this)});
				});
				
			});
		</script>
		
	</head>
	
	<body>
		
		<div id="bluebar">
		</div>
		<div id="container">
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
				</div>
				<div class="clear">
				</div>
				<hr style="{height:1px; color:#3B5998;}"/>

			</div>
			<div id="main">
				<table id="listOfProjects" cellpadding="1" cellpadding="1">
					<thead>
						<tr>
							<th class="projectName">Project Name</th>
							<th class="projectSubtitle">Title</th>
							<th class="membershipStatus">Status</th>
						</tr>
					</thead>
					<tbody>
					<#if user.projectsMemberOf?has_content>
					<#list user.projectsMemberOf as project>
						<tr class="projectMembershipStatus">
							<td class="projectName">${project.name}</td>
							<td class="projectSubtitle"><a href="${baseRef}/projects/${project.name}/wall">${project.subtitle}</a></td>
  							<td class="membershipStatus">Member</td>
						</tr>
					</#list>
					</#if>
					
					<#if user.projectsNotMemberOf?has_content>
					<#list user.projectsNotMemberOf as project>
						<tr class="projectMembershipStatus">
							<td class="projectName">${project.name}</td>
							<td class="projectSubtitle"><a href="${baseRef}/projects/${project.name}/wall">${project.subtitle}</a></td>
							<td class="membershipStatus"><button class="joinButton">Join</button></td>
						</tr>
					</#list>
					</#if>	
					</tbody>
				</table>
			</div>
			<input type="hidden" id="userEmail" value="${user.emailId}"/>
		</body>	
</html>