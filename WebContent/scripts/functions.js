function drawLabel(labelElement)
{
	var maxLabelTextLength = 40;
	var labelText = $(labelElement).text();
	if(labelText.length > maxLabelTextLength)
	{
		trimmedText = labelText.substring(0,maxLabelTextLength-4);
		trimmedText+="...";
		$(labelElement).text(trimmedText);
	}
	colorValue = $(labelElement).data('winbook-labelcolor');
	$(labelElement).jQLabel({backgroundColor:colorValue});
}


function addNewCategory(showBusyIndicator)
{
	
	var categoryNameEl = $('#categoryName'),
		categoryColorEl = $('#categoryColor'),
		mmfEl = $('#isMMF');
		categoryName = categoryNameEl.val(),
		categoryColor = categoryColorEl.val(),
		isMMF = mmfEl.is(':checked'),
		button = $('#createCategoryButton');
	var lastMMF, el;
	
	$.ajax({
		type:"POST",
		url:WinbookPageData.wallName+"/Categories",
		data: "categoryName="+encodeURIComponent(categoryName)+"&color="+categoryColor+"&isMMF="+isMMF,
		success: function(result, status){
			categoryLabel = tmpl("categoryTemplate",{catId:result, catName:categoryName, catCode:categoryColor});

			if(isMMF)
			{
				el = $(categoryLabel).hide().insertBefore('ul.categoryListing > li:first-child');
				el.fadeIn(1000).find('.labelColorPicker').mColorPicker();
				el.addClass("MMF");
				el.attr('data-winbook-itemid',result);
			}
			else
			{
				lastMMF = $('li.category.MMF').last();
				if(lastMMF.length==0)
				{
					el = $(categoryLabel).hide().insertBefore('ul.categoryListing > li:first-child');
					el.fadeIn(2000).find('.labelColorPicker').mColorPicker();
				}

				else
				{
					el = $(categoryLabel).hide().insertAfter(lastMMF);
					el.fadeIn(1000).find('.labelColorPicker').mColorPicker();
				}
			}
			
			if(WinConditionApp.hasLabelManager)
				WinConditionApp.getLabelManager().addNewLabel(el);

			categoryNameEl.attr('disabled',false);
			mmfEl.attr('disabled', false);
			categoryNameEl.val('');
			showBusyIndicator.unrender();
			categoryNameEl.focus();
		},
		error: function(xhr, status)
		{
			alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
			categoryNameEl.attr('disabled',false);
			button.attr('disabled',false);
			mmfEl.attr('disabled', false);
			showBusyIndicator.unrender();
		}
	});

	//disable access until reply is received
	categoryNameEl.attr('disabled',true);
	button.attr('disabled',true);
	mmfEl.attr('disabled', true);
}


function textLengthCounter(numChars, minChars, maxChars, counterReference, buttonToDisable){

	//numChars = $(this).val().length;
	var left = minChars - numChars;

	var more = numChars - maxChars;

	if(left>0 && left<minChars)
	{
		counterReference.css('color','#5C5C5C');
		counterReference.text('Enter at least '+left+' more characters');
		buttonToDisable.attr('disabled',true);
	}	

	else if(more > 0)
	{
		counterReference.css('color','red');
		counterReference.text('Max '+maxChars+' chars: Delete '+more+' more chars');
		buttonToDisable.attr('disabled',true);
	}				

	else if(numChars >= minChars && numChars <=maxChars)
	{	
		counterReference.text('');
		buttonToDisable.attr('disabled',false);
	}

	else if(numChars==0)
	{
		counterReference.text('');
		buttonToDisable.attr('disabled',true);
	}
}


function updatePost(currentPostNode)
{
	var updateURL;
	var updateData = currentPostNode.find('.editForm').eq(0).val();

	if(updateData.length < 10)
	{
		alert("Post must contain at least 10 characters. Please revise");
		return;
	}			

	//default base URL is the win condition. Everything is hierarchical w.r.t. it
	updateURL = WinbookPageData.wallName+"/WinConditions/";

	if(currentPostNode.hasClass("wincondition"))
	{
		wcid = currentPostNode.data("winbook-wcid");
		updateURL = updateURL+wcid;
	}
	else if(currentPostNode.hasClass("issue"))
	{
		wcid = currentPostNode.closest('.wincondition').data("winbook-wcid");
		issueId = currentPostNode.data("winbook-issueid");
		updateURL = updateURL+wcid+"/Issues/"+issueId;

	}

	else if(currentPostNode.hasClass("option"))
	{
		wcid = currentPostNode.closest('.wincondition').data("winbook-wcid");
		issueId = currentPostNode.closest('.issue').data("winbook-issueid");
		optionId = currentPostNode.data("winbook-optionid");
		updateURL = updateURL+wcid+"/Issues/"+issueId+"/Options/"+optionId;
	}

	$.ajax({
		type:"PUT",
		url:updateURL,
		data:"update="+encodeURIComponent(updateData),
		success: function(result, status){						
			currentPostNode.find('.hoverMenu').eq(0).css('z-index',WinbookPageData.hoverMenuZIndex);
			currentPostNode.find('.dataArea').eq(0).remove();
			currentPostNode.find('.postDetails').eq(0).show();
			currentPostNode.find('.postData').html(updateData);
		},

		error: function(xhr, status){
			alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
		}	

	});

}//end updatePost


function postDataToWall(postType, dataAreaToReplaceWithContent)
{
	var content = dataAreaToReplaceWithContent.children('.dataArea').val();

	if(content.length < 10)
	{
		alert("Post must contain at least 10 characters. Please revise");
		return;
	}

	var postDetailsContainer = dataAreaToReplaceWithContent.parent();
	var wcid = postDetailsContainer.closest('.wincondition').data('winbook-wcid');
	var URL = WinbookPageData.wallName+"/WinConditions/"+wcid;
	var post = postDetailsContainer.closest('.post');
	var postData;

	if(post.hasClass("issue"))
	{
		URL = URL + "/Issues";
		postData = "Issue="+encodeURIComponent(content);
	}
	else if (post.hasClass("option"))
	{
		issueId = postDetailsContainer.closest('.issue').data('winbook-issueid');
		URL = URL +"/Issues/"+issueId+"/Options";
		postData = "Option="+encodeURIComponent(content);

	}


	$.ajax({

		type:"POST",
		url:URL,
		data:postData,
		success:function(result, status)
		{
			var postNode = postDetailsContainer.closest('.post');						
			postDetailsContainer.parents('.dataForm').toggleClass('dataForm');
			postDetailsContainer.children('.hoverMenu').css('z-index',WinbookPageData.hoverMenuZIndex);
			dataAreaToReplaceWithContent.remove();
			switch(postType.toLowerCase())
			{
			case "Issue".toLowerCase(): 
				postDetailsContainer.append(tmpl("issuePostDetailsTemplate",{id:result.issueid, issue:content}));
			/*
			 * must use .attr for setting data attribute since data set by .data resides in $.cache and is NOT immediately a part of DOM. 
			 * $('[data-winbook-*]=??).remove() would fail. 
			 */
			postNode.attr("data-winbook-status","open");
			postNode.attr("data-winbook-issueid",result.issueid);
			postNode.append('<ul class = _listOfOptions></ul>'); //add placeholder for list of options
			WinConditionApp.getListOfPosts().addIssue(postNode);
			break;

			case "Option".toLowerCase():
				postDetailsContainer.append(tmpl("optionPostDetailsTemplate",{id:result.optionid, option:content}));
			postNode.attr("data-winbook-status","open");
			postNode.attr("data-winbook-optionid",result.optionid);
			WinConditionApp.getListOfPosts().addOption(postNode);
			break;


			}
		},//end success function

		error: function(xhr, status){
			alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
		}	
	});//end ajax


}// end postDataToWall


//call the autoResize plugin to configure the text area for resize without scroll
function configTextArea() {
	$('textarea#wincondition').css("color","gray");
	$('textarea#wincondition').autoResize({
		animate:false,
		extraSpace:0

	});	

	$('textarea#wincondition').focus( function() {
		var text = $(this).val();
		var defaultText = WinbookPageData.defaultWCTextAreaValue;

		if(text.toLowerCase()== defaultText.toLowerCase())
			$(this).val('');

		$(this).css("color","black");

	} );

	$('textarea#wincondition').blur( function() {

		if($(this).val().length==0)
		{
			$(this).val("Enter your Win Condition/Requirement/Expectation here...");
			$(this).css('color','gray');
		}	

	});
}// end configTextArea()


function actionNavBarHandler(actionLink) {

	var action = actionLink.data("winbook-action");

	var formType, dataForm, buttonText;


	switch(action.toLowerCase()) {
	case "Issue".toLowerCase():
		formType="issue";
	var winConditionNode = actionLink.parents('li.wincondition');
	buttonText = formType.charAt(0).toUpperCase() + formType.slice(1);		
	dataForm = tmpl("dataFormTemplate",{displayName:WinbookPageData.userDisplayName, avatar:WinbookPageData.userAvatar, postType:formType, postTypeButtonText:buttonText});
	//createInputForm(dataForm, winConditionNode); //now redundant
	var issueList = $('._listOfIssues',winConditionNode);
	var child = issueList.children(':first');
	if(child.hasClass('dataForm'))			
		child.remove();	
	else
		issueList.prepend(dataForm);
	break;

	case "Option".toLowerCase():
		formType="option";
	var issueNode = actionLink.parents('li.issue');
	buttonText = formType.charAt(0).toUpperCase() + formType.slice(1);		
	dataForm = tmpl("dataFormTemplate",{displayName:WinbookPageData.userDisplayName, avatar:WinbookPageData.userAvatar, postType:formType, postTypeButtonText:buttonText});
	//createInputForm(dataForm, issueNode);
	var optionsList = $('._listOfOptions',issueNode);
	var child = optionsList.children(':first');
	if(child.hasClass('dataForm'))				
		child.remove();					
	else
		optionsList.prepend(dataForm);
	break;

	}


}//end ActionNavBarHandler