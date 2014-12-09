/*
 * This script is just doing the job of $(document).ready(...); Just removing it from the HTML file and placing the relevant content
 * under it's own separate file.
 */

(function(WallApp, WinbookPageData){
	//Global animation loader for replacing dom element with a spinner
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
				"background": "#FFFFFF url(../../images/loading_round.gif) no-repeat center center"
				});
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
	
	
	WinbookPageData.init = function(){
		WinbookPageData = WinbookPageData || {};
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
	};
	

		
		WallApp.init = function(){
		//instantiate color picker
		$('input#categoryColor, .labelColorPicker').mColorPicker();
		$.fn.mColorPicker.init.replace = false;	
		$.fn.mColorPicker.init.enhancedSwatches = false;
		
		$('#createCategoryButton').attr('disabled',true);
		$('#categoryName').keyup(function(e){
			
			length = $(this).val().length;
			minLength = WinbookPageData.minCategoryLength;
			maxLength = WinbookPageData.maxCategoryLength;
			categoryButton = $('#createCategoryButton');
			counterSpan = $('#categoryLength');
			
			textLengthCounter(length, minLength, maxLength, counterSpan, categoryButton);
			
			if(length >= minLength && length <= maxLength && e.which==13)
				$('#createCategoryButton').click();
		});
		
		
		//get category name and color value
		$("#createCategoryButton").bind('click',function(){
			var showBusy = new WinbookPageData.LoadingAnimation({DOMElement:$(this)});
			addNewCategory(showBusy);
		});
	};
	
}).call(this, this.WallApp = this.WallApp || {}, this.WinbookPageData = this.WinbookPageData || {});