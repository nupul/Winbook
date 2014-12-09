(function(WinConditionApp) {

	WinConditionApp.initWall = function(){
		
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
	
		$('.listOfPosts').delegate('.postDetailsContainer','hover',function(e){
			e.stopPropagation();
	
			if($(this).find('textarea').length === 0) //don't show hover menu if an edit/update form is being displayed.
				$(this).find('.hoverMenu').toggle( e.type === 'mouseenter');
			
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
	
			var wincondition = encodeURIComponent($('#wincondition').val());	
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
	
					WinConditionApp.getListOfPosts().addWinCondition($('.listOfPosts').children(':first'));
	
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
	}; //End initWall
	
}).call(this, this.WinConditionApp = this.WinConditionApp || {});