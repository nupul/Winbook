//creating a 'namespace'
var homePage = {
		createLoginView: function() {
			var LoginView = Backbone.View.extend({
				minFieldLength: 5,

				el: '#loginMenu',

				initialize: function(){
					_.bindAll(this, "login","isLengthValid");

				this.notificationBar = 	$('#notificationBar');
				
				if(!this.notificationBar.is(':empty'))
					this.notificationBar.show();
				
				},

				events: {
					"click #login" : "login",
					"keyup #username" : "checkEnter",
					"keyup #password" : "checkEnter"
						
				},

				login: function(){
					this.notificationBar.hide();
					var username = $('#username').val();
					var password = $('#password').val();
					var self = this;
					if(this.isLengthValid(username, password))
					{
						$.ajax({
							type: 'POST',
							url: $(self.el).data('login'),
							data: 'username='+username+'&password='+SHA1(password),
							success: function(result, status){
								window.location = $(self.el).data('login-action');
							},
							error: function(result, status) {
								self.notificationBar.text("Invalid Username/Password")
								self.notificationBar.fadeIn("medium");
							}
							
						
						});
					}	

				},
				
				checkEnter: function(event) {
					if(event.which == 13)
						this.login();
				},

				isLengthValid: function(username,password) {
					if(username.length<this.minFieldLength || password.length < this.minFieldLength)
					{
						this.notificationBar.text("ERROR: Username/Password must be at least 5 characters long...");
						this.notificationBar.fadeIn("medium");
						return false;
					}	

					return true;

				}
			});

			return new LoginView();
		},

		createSignUpView: function() {
			var SignUpView = Backbone.View.extend({
				el: '#signUpArea',

				initialize: function() {
					_.bindAll(this, "signUp", "validateFields");
					this.signUpError = $('#signUpErrors');
				},

				events: {
					"click #signUpButton" : "signUp"
				},

				signUp: function(){

					if(this.validateFields()===true)
						$('#signUpForm').submit();
				},

				validateFields: function() {
					var validateEmail = function(email){ 
						var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/; 
						return email.match(re) 
					}

					var validateURL = function(url){
						var re = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
						return url.match(re);
					}

					var email = $('#email').val();
					var errorFound = false;

					var minFieldLength = 2;
					var minPasswordLength = 5;
					var that = this;
					$('.textfield').each(function(){

						//check to make sure mandatory fields are not blank.
						if($(this).data('winbook-isOptional')==="no" && $(this).val().length==0)
						{
							that.signUpError.text("You must fill in all non-optional fields");
							errorFound = true;
							return false;
						}

						var text = $(this).val();

						switch ($(this).attr('id'))
						{
						case "newpassword":
							if(text.length < minPasswordLength)
							{
								that.signUpError.text("Password must be at least "+minPasswordLength+" characters long");
								errorFound = true;
								return false;
							}
							break;

						case "email":
							if(!validateEmail(text))
							{
								that.signUpError.text("Please enter a valid email address");
								errorFound = true;
								return false;
							}
							break;

						case "avatar":
							if(text.length > 0 && !validateURL(text))
							{
								that.signUpError.text("Please enter a valid URL for your Avatar e.g.: http://www.mysite.com/avatar")
								errorFound = true;
								return false;
							}
							break;

						default:
							if(text.length > 0 && text.length < minFieldLength)
							{
								that.signUpError.text("Fields must contain at least "+minFieldLength+" characters");
								errorFound = true;
								return false;
							}
						break;

						}

					});

					if(errorFound)
					{
						this.signUpError.fadeIn("medium");
						return false;
					}

					var textPassword = $('#newpassword').val();
					$('#newpassword').val(SHA1(textPassword));
					return true;
				}
			});

			return new SignUpView();
		}
};