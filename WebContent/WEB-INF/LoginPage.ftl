<!DOCTYPE HTML>
<html>
	<head profile="http://www.w3.org/2005/10/profile">
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Winbook - Collaborative Win Win Analysis: Login, Sign Up</title>
	<link rel="icon" type="image/png" href="${baseRef}/images/site-icon.png"/>
	<style>
		
	body {
		margin: 0;
		padding: 0;
	}
	
	#notificationBar {
		top: 1px;
		left: 50%;
		height:20px;
		background-color: #FFF1A8;
		border-radius: 3px;
		padding:1px;
		font-family: "arial,sans-serif";
		font-weight: bold;
		color: black;
		text-align: center;
		font-size: 15px;
		display: none;
	}
		
	#bluebar {
		position:absolute;
		min-width: 1024px;
		width:100%;
		height: 96px;
		background-color:#3B5998;
		z-index:-1;
	}
	
	#container {
		width: 100%;
		min-width:1024px;
		min-height: 600px;
	}
	
	#contentContainer {
		background: url('${baseRef}/images/gradient.png');
		background-repeat-x: repeat;
		background-repeat-y: no-repeat;
		height: 600px;
		width: 100%;
		clear:both;
	}
		
	
	#footerContainer {
		text-align: center;
		color: gray;
	}
		
	#loginArea {
		height: 90px;
		width: 1024px;
		margin-left:auto;
		margin-right:auto;
	}	
	
	#contentHolder {
		width: 1024px;
		margin-left:auto;
		margin-right:auto;
	}
	
	#collaborationImageHolder {
		width:500px;
		position:relative;
		top:50px;
		float: left;
	}
	
	#collaborationImageHolder img {
		width:inherit;
		opacity: 0.3;
		filter:alpha(opacity=30); /*For IE*/
	}
	
	#logoArea {
		float:left;
		padding: 3px;
		margin:0;
	}
	
	#logoArea h1 {
		font-weight:bold;
		font-size:40px;
		padding:2px;
		font-family:Georgia, "Times New Roman", Times, serif;
		color:#FFF;
	}
	
	#loginMenu {
		float: right;
		color: white;
		font-weight: normal;
		padding:5px 0 5px;
		position:relative;
		top: 14px;
		left: 3px;
	}
	
	#loginMenu a:link, #loginMenu a:visited{
		color:#98A9CA;
	}
		
	
	input#username, input#password {
		border-color: #1D2A5;
		border-width: 1px;
		width:145px;
		height:15px;
		padding:3px;
	}
	
	button#login, #signUpButton {
		background-color: #5B74A8;
		border-color:#29447E #29447E #1A356E;
		color: white;
		font-weight: bold;
		height: 25px;
		cursor: pointer;
	}
	
	td {
		padding-left:10px;
	}
	
	#signUpArea {
		float:right;
		width: 450px;
	}
	
	.signUpTable {
		width:400px;
		float:right;
	}
	
	label {
		position:relative;
		font-size:14px;
		padding-right:3px;
	}
	
	.labelCell {
		width:135px;
		text-align: right;
	}
	
	.displayFont {
		font-family: "lucida grande",tahoma,verdana,arial,sans-serif;
		color: #1D2A5B;
	}
	
	.roleLabel {
		font-size:14px;
	}
	
	.signUpTable .textfield {
		width: 240px;
		height:20px;
		padding:6px;
		margin-top:2px;
		border: 1px solid #96A6C5;
		font-size:16px;
	}
	
	#signUpInfo {
		width: 388px;
		float: right;
		border-bottom: 1px solid #9AAFCA;
		margin-bottom: 8px;
	}
	
	#signUpLogo {
		font-size: 18px;
		font-weight: bold;
		color: #0E385F;
		margin-bottom: 8px;	
	}
	
	#info {
		font-size:16px;
		margin:0 0 8px 0;
	}
	
	#signUpButton {
		margin:8px 0 0 133px;
		width: 120px;
		height: 30px;
		
		background-image: linear-gradient(bottom, #002D63 16%, #3B5898 58%, #36659E 79%);
		background-image: -o-linear-gradient(bottom, #002D63 16%, #3B5898 58%, #36659E 79%);
		background-image: -moz-linear-gradient(bottom, #002D63 16%, #3B5898 58%, #36659E 79%);
		background-image: -webkit-linear-gradient(bottom, #002D63 16%, #3B5898 58%, #36659E 79%);
		background-image: -ms-linear-gradient(bottom, #002D63 16%, #3B5898 58%, #36659E 79%);
		
		background-image: -webkit-gradient(
			linear,
			left bottom,
			left top,
			color-stop(0.16, #002D63),
			color-stop(0.58, #3B5898),
			color-stop(0.79, #36659E)
		);


	}
	#signUpButton:active {
		background-image: linear-gradient(bottom, #36659E 16%, #3B5898 58%, #002D63 100%);
		background-image: -o-linear-gradient(bottom, #36659E 16%, #3B5898 58%, #002D63 100%);
		background-image: -moz-linear-gradient(bottom, #36659E 16%, #3B5898 58%, #002D63 100%);
		background-image: -webkit-linear-gradient(bottom, #36659E 16%, #3B5898 58%, #002D63 100%);
		background-image: -ms-linear-gradient(bottom, #36659E 16%, #3B5898 58%, #002D63 100%);
		
		background-image: -webkit-gradient(
			linear,
			left bottom,
			left top,
			color-stop(0.16, #36659E),
			color-stop(0.58, #3B5898),
			color-stop(1, #002D63)
		);

	}
	
	#signUpErrors {
		margin: 20px 0;
		line-height:15px;
		background-color: #FFEBE8;
		border: 1px solid red;
		text-align: center;
		width: 388px;
		padding: 7px 3px 7px 3px;
		float: right;
		font-size:small;
		display:none;
	}
	
	.optionalInfo {
		font-size: 11px;
		margin:1px 0;
		padding:0;
		position:absolute;
		top: 1.2em;
		right: 10px;
		
	}
	</style>
	
		<script type="text/javascript" src="${baseRef}/scripts/jquery.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/underscore.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/backbone.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/webtoolkit.sha1.js">
		</script>
		<script type="text/javascript" src="${baseRef}/scripts/login.js">
		</script>
		
	
	<script type="text/javascript">
	
	$(document).ready(function(){
		var loginHandler = homePage.createLoginView();
		var signUpForm = homePage.createSignUpView();
	});
	
	</script>
	
	</head>
	
	<body>
	<div id="notificationBar"><#if message??>${message}</#if></div>
	
	<div id="bluebar">
	</div>
	
	<div id="container">
		<div id="loginContainer">
			<div id="loginArea">
				<div id = "logoArea">
					<h1>Winbook</h1>
				</div>
				<div id="loginMenu" data-login = "${baseRef}/Login" data-login-action="${baseRef}/projects">
						<table>
							<tbody>
								<tr>
									<td>Email</td>
									<td>Password</td>
								</tr>
								<tr>
									<td><input id="username" name="username" type="text"/></td>
									<td><input id="password" name="password" type="password"></td>
									<td><button id="login">Login</button></td>
								</tr>
								<tr>
									<td><a href = "/EmailFYP/ChangePassword">Forgot your Password?</a></td>
									<td></td>
								</tr>
							</tbody>
						</table>
				</div>
			</div>
		</div>	
		
		<div id = "contentContainer">
			<div id = "contentHolder">
				<div id = "collaborationImageHolder">
					<img src="${baseRef}/images/collaboration.gif"/>
				</div>
				<div id = "signUpArea" class="displayFont">
					<div id="signUpInfo">
						<p id="signUpLogo">Sign Up</p>
						<p id="info"><u>Winbook</u>: A tool for collaborative brainstorming & negotiations</p> 
					</div>
						<div>
							<table class="signUpTable" cellpadding="1" cellspacing="1">
					<form id="signUpForm" action="${baseRef}/User" method="post">
							<tbody>
								<tr>
									<td class="labelCell"><label for="firstName">First Name :</label></td>
									<td><input type="text" class="textfield" id="firstName" name="firstName" data-winbook-isOptional="no"/></td>
								</tr>
								<tr>
									<td class="labelCell"><label for="lastName">Last Name :</label></td>
									<td><input type="text" class="textfield" id="lastName" name="lastName" data-winbook-isOptional="no"/></td>
								</tr>
								<tr>
									<td class="labelCell"><label for="nickName">Display Name : <span class="optionalInfo"><em>(optional)</em></span></label></td>
									<td><input type="text" class="textfield" id="nickName" name="nickName" data-winbook-isOptional="yes"/></td>
								</tr>
								<tr>
									<td class="labelCell"><label for="avatar">Avatar URL : <span class="optionalInfo"><em>(optional)</em></span></label></td>
									<td><input type="text" class="textfield" id="avatar" name="avatar" data-winbook-isOptional="yes"/></td>
								</tr>
								<tr>
									<td class="labelCell"><label for="email">Your Email :</label></td>
									<td><input type="text" class="textfield" id="email" name="email" data-winbook-isOptional="no"/></td>
								</tr>
								<tr>
									<td class="labelCell"><label for="newpassword">New Password :</label></td>
									<td><input type="password" class="textfield" id="newpassword" name="newpassword" data-winbook-isOptional="no"/></td>
								</tr>
								<tr>
									<td class="labelCell"><label>Select Role :</label></td>
									<td>
										<div class="roleSelection" data-winbook-isOptional="no">
											<input type="radio" name="role" value="client"/> <span class="roleLabel">Client</span>
											<input type="radio" name="role" value="shaper"/> <span class="roleLabel">Shaper</span>
											<input type="radio" name="role" value="member" checked/> <span class="roleLabel">Team Member</span>
										</div>
									</td>
								</tr>
							</tbody>
					</form>
							<tfoot>
								<tr>
									<td colspan="2"><button id="signUpButton">Sign Up</button></td>
								</tr>
							</tfoot>
							</table>
						</div>
					<div id="signUpErrors"></div>
					
					</div>
				</div>
			</div>
		</div>
		
		<div id = "footerContainer">
			This site is best viewed in Chrome, Firefox & IE 7+ (untested). There seem to be problems with Safari - we are working to resolve them soon.
		</div>	
	</div>	
	</body>
	
</html>
	
	
