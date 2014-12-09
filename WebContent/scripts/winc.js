(function(WinConditionApp){
	/*
	 * Backbone.js was learnt "on the way" - as a need for better organizing the code for the labeling functionality. As a result it was
	 * retrofitted to the current design/architecture and that should be evident with how the code/classes are designed. There were already 
	 * functions in place for creating the posts' DOM and capturing edit/delete events etc., Backbone suggests that all this happen 
	 * 'through the view' - but at this time now it'll be a great effort to refactor everything to backbone's classes. 
	 * Thus Backbone's class-based structure is coexisting with the functional manipulation of jqeury/DOM. 
	 * This is not a problem since both codes are well written :) I'm documenting this to make 
	 * it explicit to avoid any confusion. You may see some 'functions' sharing functionality with their classes or functions invoking 
	 * the corresponding class/object methods to invoke backbone's functionality - an artifact of retrofitting. The functions would not
	 * be required if everything was through backbone's classes. In some cases, the functions were partially working e.g.,
	 * for raising issue/suggesting options but not for agreement/comments etc., Different backbone view's were created to handle that
	 * leading to cleaner code but keeping some 'legacy' code (functions) to handle what they were already doing. It may be refactored completely
	 * to be a part of backbone's classes but some functions will just continue to exist - especially the templating function tmpl()
	 * and a few others for which no views are created but just handled by the functions. After this you'll find more and more code designed
	 * with backbone since it's MUCHHHHHHHHHHHHH cleaner and more intuitive and more object oriented. 
	 * 
	 */	


	/******************************************* BACKBONE BASED CODE FOR LABEL APPLICATION ***************************************/

	/*
	 * Design decision(s): The updating of the labels will happen in bulk rather than one at a time as is common practice when using 
	 * Backbone. The SelectedPosts Collection will be sent after the post/label states have been updated.
	 * The format of the JSON sent to the server is of the following type: For each post an array of labels indicating their corresponding 
	 * actions that need to be executed at the server. The serve ignores duplicates and labels with action as "none" (This may be optimized by
	 * 	only sending non-none-action labels. This comment may not be updated if that is indeed done and will be evident in the code in the 
	 * toJSON method that is overriden probably or somewhere else).
	 * 
	 * [
	 *   {
	 * 	 	postId:1,
	 *	  	labels:[{id:3, action:"none"}, {id:4, action:"add"}, {id:5, action:"delete"}]
	 *   },
	 * 
	 * 	 {...}, {...}
	 * ]
	 * 
	 * This means that simply adding/removing from the collection of labels and binding to those events won't cut it. Since labels are explicitly
	 * removed only after the response from the server is obtained. Not knowing which label to remove (at the server-side) unless it's explicitly
	 * marked with action:"delete" complicates the situation (at the client side) - need to create a custom event signalling completion of 
	 * processing after which all this (i.e., deletion related tasks) need to be done i.e., syncing the internal collection of labels for each 
	 * post. The 'action' part is primarily for the server. This approach was chosen over sending multiple requests to the server for 
	 * addition/removal of each label individually - would lead to too many requests and wouldn't be all that beneficial
	 */

//	label id and corresponding action to take
	var PostLabel = Backbone.Model.extend({
		defaults: {
			id: -1,
			action:"none"
		}
	});

//	list of labels corresponding to each post
	var PostLabels = Backbone.Collection.extend({model:PostLabel});

//	A model of each 'selected post' with it's corresponding id and its list of labels (if any)
	var PostModel = Backbone.Model.extend({
		defaults : {
			id: -1
		},			
		initialize: function(){
			_.bindAll(this, "addLabel","updateLabel");
			this.listOfLabels = new PostLabels();
		},

		addLabel: function(label) {
			//Ignore duplicates; _.include combination doesn't work with it's ===. It's 2:23 AM and am unable to figure it out :) 
			var oldLabel = _.detect(this.listOfLabels.models,function(aLabel){
				return (aLabel.get('id')===label.get('id'));
			});

			if(_.isUndefined(oldLabel))	
				this.listOfLabels.add([{id:label.id}]);	
		},


		/*
		 * This is faking overloading. Basically a label may be signalled for deletion either by id or the label object itself.
		 * The former is done when the user actually deletes the category from the page. The 'id' of the label deleted is then 
		 * passed around until it reaches the PostModel. Rather than having another method do this:
		 * call method like myview.removeLabel(1,{removeById:true}); or if passing the PostLabel object just call 
		 * myview.remove(label) where label is instance of postLabel. The check for 'options' makes it an optional argument.
		 * http://stackoverflow.com/questions/456177/function-overloading-in-javascript-best-practices
		 * You can extend the functionality without affecting existing code :) Changing the interface but nothing will break :)
		 */
		removeLabel: function(label,options) {
			if(options && options.removeById===true)
			{
				var labelToRemove = _.detect(this.listOfLabels.models, function(aLabel){
					return(aLabel.get('id')===label)
				});
				if(labelToRemove)
					this.listOfLabels.remove(labelToRemove);
			}
			else
				this.listOfLabels.remove(label);	



		},

		/*
		 * Update the corresponding action associated with the labels i.e., whether an existing label needs to be deleted
		 * or a new label needs to be added to this Post
		 */
		updateLabel: function(labelId, updateAction){
			var label = _.detect(this.listOfLabels.models,function(aLabel){
				return (aLabel.get('id')==labelId);	
			});

			//only if new label is to be added create one if it doesn't exist. 
			if(_.isUndefined(label) && updateAction==="add")
			{
				var newLabel = new PostLabel({id:labelId, action:updateAction});
				this.listOfLabels.add(newLabel);
				return;
			}

			else if(label && updateAction==="delete")
				label.set({action:updateAction});

		},

		/*
		 * overriding toJSON to cater for nested collection (PostLabels) in model (PostModel)
		 * This can either be called directly or by the parent collection when calling JSON.stringify(...);
		 * When called on a collection, it in turn calls it on each of it's contained models. However, it doesn't cater for 
		 * nested collections. Backbone internally uses _.clone(...) method of underscore to create a shallow copy of the 
		 * model's attributes hash, hence you have to override toJSON to JSON-ify nested collections/models
		 */			
		toJSON: function() {
			var postJSON = {
					postId: this.get('id'),
					labels: this.listOfLabels.toJSON()
			};

			return postJSON;
		},

		//POC: Triggering a custom event
		syncSuccess: function() {
			this.trigger("updateSuccess");
		}
	});

//	Collection of selected posts.
	var SelectedPosts = Backbone.Collection.extend({
		model:PostModel,

		initialize: function() {
			_.bindAll(this,"updatePosts");
		},

		//The actual update should only happen after a successful response from the server.
		updatePosts: function(updatedLabels){

			var self = this;
			_.each(updatedLabels, function(label){
				_.each(self.models, function(aSelectedPost){
					var updateAction;

					if(label.get('newState')===window.LabelStateTransition.NONE)
						updateAction="delete";

					else if(label.get('newState')===window.LabelStateTransition.TOTAL)
						updateAction="add";

					aSelectedPost.updateLabel(label.get('id'),updateAction);

				});
			});

			var jsonUpdate = JSON.stringify(this);
			//alert(jsonUpdate);

			$.ajax({
				url:WinbookPageData.wallName+'/WinConditionCategorization',
				type:'PUT',
				contentType:'application/json',
				data: jsonUpdate,
				success: function(result, status) {
					//POC
					_.each(self.models, function(post){
						post.syncSuccess();
					});
				},

				error: function(xhr, status){
					alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
				}

			});//end ajax

		}//end updatePosts 

	});//end SelectedPosts Collection

//	The collection of posts that have been checked
	var listOfSelectedPosts = new SelectedPosts();

//	Global constant - to keep track of the state transitions of the labels' new and old states
	window.LabelStateTransition = {};
	window.LabelStateTransition.PARTIAL = "partial";
	window.LabelStateTransition.TOTAL = "total";
	window.LabelStateTransition.NONE = "none";

//	a Model for each label/category on the page.
	var LabelModel = Backbone.Model.extend({
		defaults: {
			isPartial: false,
			currentState: window.LabelStateTransition.NONE,
			newState: window.LabelStateTransition.NONE,
			count: 0
		}

	});		


//	A view of "each" label on the label Menu
	var LabelView = Backbone.View.extend({	

		initialize: function() {
			_.bindAll(this,"render","update","toggleSelection","unrender","deleteCategoryLabel","incrementSelectionCount","decrementSelectionCount","isUpdated","updateLabelState","updateLabelColor","showPostsInCategory");
			this.id = this.options.labelId;
			this.model = new LabelModel({
				id:this.options.labelId,
				name:this.options.labelName,
				color:this.options.labelColor
			});
			this.isSelectionToggled = false;
		},

		events: {
			"click .labelCheckBox" : "toggleSelection",
			"click a.delete" : "deleteCategoryLabel",
			"colorpicked input.labelColorPicker" : "updateLabelColor",
			"click .labelDescriptor" : "showPostsInCategory"
		},

		showPostsInCategory: function(event){
			var labelId = this.id;
			this.trigger("showPostsInCategory",labelId);
		},

		updateLabelColor: function(event){
			var newColor = $(event.target).val();
			/*
			 * Aeed to send the span since mColorPicker hides the input.labelColorPicker and renders using span.labelColorPicker which it creates
			 * jquery get's the position of hidden elements as 0,0 for relative positioning and thus the busy animation could be offset by 
			 * the wrong amount.
			 */
			var showBusy = new WinbookPageData.LoadingAnimation({DOMElement:$(event.target).next('span.labelColorPicker')});
			var self = this;
			$.ajax({
				url:WinbookPageData.wallName+"/Categories/"+self.id,
				type: "PUT",
				data:"color="+newColor,
				success: function(result, status){
					self.model.set({color:newColor});
					self.trigger("categoryLabelUpdated",{id:self.id,color:newColor});
					showBusy.unrender();
				},
				//must be able to revert back to old color. needs to store old color in class object/LabelModel
				error: function(xhr, status)
				{
					//specific for mColorPicker that adds a span for the styling of the color picker.
					alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
					$(self.el).find('span.labelColorPicker').css({"background-color":self.model.get('color')});
					$(self.el).find('input.labelColorPicker').val(self.model.get('color'));
					showBusy.unrender();
				}

			});
		},

		deleteCategoryLabel: function(event){
			var self = this;
			$.ajax({
				url:WinbookPageData.wallName+"/Categories/"+this.model.get('id'),
				type:"DELETE",
				success: function(result, status){
					self.trigger('categoryLabelDeleted',self.id);
					$(self.el).fadeOut("slow",function(){
						self.model=null;
						$(self.el).remove();	
					});
				},

				error: function(xhr, status)
				{
					alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
				}
			});

		},

		render: function(){
			$(this.el).find('input.labelColorPicker').fadeOut("slow");
			$(this.el).find('.labelCheckBox').fadeIn("slow");
			this.update();
			return this;
		},

		unrender: function() {
			$(this.el).find('input.labelColorPicker').show();
			$(this.el).find('.labelCheckBox').fadeOut("slow");
			this.isSelectionToggled = false;

			//reset model to default values.
			this.model.set(this.model.defaults);
		},

		update: function(numPostsSelected){
			var labelCount = this.model.get('count');

			//Rest processing if no posts are selected.
			if(numPostsSelected==0)
				this.isSelectionToggled=false;

			//if labelCount==0 only toggle b/w all/none. Reset other classes
			if(labelCount==0)
			{
				this.model.set({
					isPartial:false
				});
				//If this label has been toggled by the user, then don't make UI changes but only update it's partial applicability status
				if(this.isSelectionToggled)
					return;

				if($(this.el).find('.labelCheckBox').hasClass("hasPartial"))
					$(this.el).find('.labelCheckBox').removeClass("hasPartial");

				if($(this.el).find('.labelCheckBox').hasClass("allSelected"))
					$(this.el).find('.labelCheckBox').removeClass("allSelected");

				//set current/new state to the same value ==> no state change	
				this.model.set({
					currentState : window.LabelStateTransition.NONE,
					newState : window.LabelStateTransition.NONE
				});
			}
			//else if labelCount < numPostsSelected implies partial selection allowed
			else if(numPostsSelected - labelCount > 0)
			{
				this.model.set({
					isPartial:true
				});

				//If this label has been toggled by the user, then don't make UI changes but only update it's partial applicability status
				if(this.isSelectionToggled)
					return;

				if($(this.el).find('.labelCheckBox').hasClass("allSelected"))
					$(this.el).find('.labelCheckBox').removeClass("allSelected");

				$(this.el).find('.labelCheckBox').addClass("hasPartial");

				this.model.set({
					currentState : window.LabelStateTransition.PARTIAL,
					newState : window.LabelStateTransition.PARTIAL
				});
			}

			//else render it as "all selected" but no partial selection allowed.
			else if(labelCount == numPostsSelected)
			{
				this.model.set({
					isPartial:false
				});

				//If this label has been toggled by the user, then don't make UI changes but only update it's partial applicability status
				if(this.isSelectionToggled)
					return;

				if($(this.el).find('.labelCheckBox').hasClass("hasPartial"))
					$(this.el).find('.labelCheckBox').removeClass("hasPartial");

				$(this.el).find('.labelCheckBox').addClass("allSelected");

				this.model.set({
					currentState : window.LabelStateTransition.TOTAL,
					newState : window.LabelStateTransition.TOTAL
				});
			}


		},

		/*
		 * This method does a 3 way toggle between Partial -> Total -> None -> Partial | Total
		 * Toggling from None -> Partial is only possible if a partial selection exists else it only toggles to Total
		 * Also need to update the newState to check if a transition actually happended on that label 
		 * i.e., did the user toggle the selection
		 */
		toggleSelection: function(event) {
			this.isSelectionToggled = true;
			var item = event.target;
			if($(item).hasClass("allSelected"))
			{
				this.model.set({newState : window.LabelStateTransition.NONE});
				$(item).toggleClass("allSelected");
			}

			else if($(item).hasClass("hasPartial"))
			{
				this.model.set({newState : window.LabelStateTransition.TOTAL});
				$(item).toggleClass("hasPartial");
				$(item).toggleClass("allSelected");
			}
			else
			{
				if(this.model.get('isPartial')===true)
				{
					this.model.set({newState : window.LabelStateTransition.PARTIAL});
					$(item).toggleClass("hasPartial");
				}
				else
				{
					this.model.set({newState : window.LabelStateTransition.TOTAL});
					$(item).toggleClass("allSelected");
				}
			}

		},

		incrementSelectionCount: function() {
			this.model.set({
				count:this.model.get('count')+1
			});
		},

		decrementSelectionCount: function() {
			this.model.set({
				count:this.model.get('count')<=0 ? 0 : this.model.get('count')-1
			});
		},

		isUpdated: function() {
			if(this.model.get('currentState') !== this.model.get('newState'))
				return true;
			else
				return false;
		},

		updateLabelState: function(){
			this.model.set({
				currentState:this.model.get('newState')
			});
		}
	});


	/*
	 * The actual menu display for all labels - also incharge of controlling the display of the labels: all, partial, none.
	 * 
	 * Design decision: LabelMenuView is the parent/container of the individual LabelViews. They could communicate purely through events
	 * nullifying the need for the parent to maintain a reference to all the aggregate views. This though a better model would complicate 
	 * debugging. This is the class that interacts with SelectedPosts and tells it to perform the updates. If an event based model is chosen
	 * then the children i.e., LabelViews will be responsible for adding the updated labels to the SelectedPost (see LabelMenuView's menuAction() 
	 * to get a clear picture). May refactor accordingly later. But this design has been chosen for now. It's good enough and more towards the
	 * OOP design paradigm than event based.
	 * 
	 * HOWEVER: I have bound LabelMenuView to a 'labelDeleted' event of each of it's 'children' - It could be done purely OO by passing parent reference
	 * to child and calling parent's method. The event based technique is only chosen as a proof of concept i.e., just to have an example
	 * of how two views can communicate with each other and what is it that needs to be wired together. A good reference: 
	 * http://stackoverflow.com/questions/5784141/how-to-pass-data-from-one-view-to-another-with-custom-events
	 */
	var LabelMenuView = Backbone.View.extend({

		el:'.categoryListing',

		initialize: function(){
			_.bindAll(this);
			this.isRendered = false;
			listOfSelectedPosts.bind('add',this.postAdded);
			listOfSelectedPosts.bind('remove',this.postRemoved);
			listOfSelectedPosts.bind('updateSuccess',this.updateLabelStates);
			//this.render();	//self render the view for this test
			this.setOfLabelViews = [];
			var self = this;
			$(this.el).find('.category').each(function(){

				self.addNewLabel($(this));
			});

			this.viewAllEl = $('.all','.categoryListing');

		},

		events : {
			"click .categoryMenuButton" : "menuAction",
			"click .all" : "showAllPosts"
		},

		addNewLabel: function(labelElement){
			var id = labelElement.data('winbook-categoryid');
			var name =  labelElement.find('.labelDescriptor').text();
			var color = labelElement.find('.labelColorPicker').val();
			var aLabelView = new LabelView({el:labelElement,labelId:id, labelName:name, labelColor:color});
			aLabelView.bind('categoryLabelDeleted',this.deleteLabelViewRef);
			aLabelView.bind('categoryLabelUpdated',this.updateLabelView);
			aLabelView.bind('showPostsInCategory',this.showPostsInCategory);

			if(this.isRendered)
				aLabelView.render();

			this.setOfLabelViews.push(aLabelView);
		},

		showAllPosts: function(event) {
			$(event.target).hide();//toggleClass("hide");
			this.trigger("showAllPosts");
		},

		showPostsInCategory: function(categoryId){
			//Ignore event if categorization is in process...
			if($(this.el).hasClass("isCategorizing"))
				return;

			if($(this.viewAllEl).is(':hidden'))
				$(this.viewAllEl).show();

			this.trigger("showPostsInCategory",categoryId);
		},

		deleteLabelViewRef: function(viewId) {
			//alert("deleting label id = "+viewId);
			var viewToDelete = _.detect(this.setOfLabelViews,function(view){
				return (view.id === viewId);
			});

			this.setOfLabelViews.splice(_.indexOf(this.setOfLabelViews,viewToDelete),1);
			viewToDelete = null; //for GC
			//pass on the event for other views to use (i.e., to delete the label from their post(s))
			this.trigger('categoryLabelDeleted',viewId);
		},

		//just a messenger - continue to forward the event
		updateLabelView: function(label){
			this.trigger('categoryLabelUpdated',label);
		},

		render: function(){
			$(this.el).toggleClass("isCategorizing");
			_.each(this.setOfLabelViews,function(aLabelView){
				aLabelView.render();
			});
			$(this.el).find('#categoryMenuButtons').fadeIn("slow");
			this.isRendered = true;
		},

		unrender: function() {
			$(this.el).toggleClass("isCategorizing");
			$(this.el).find('#categoryMenuButtons').hide();
			//$(this.el).css({"background-color":"#FFFFFF"});


			//unrender each of the LabelViews too
			_.each(this.setOfLabelViews,function(view){
				view.unrender();
			});	

			//empty the collection of selected posts
			listOfSelectedPosts.reset();
			this.isRendered = false;

		},

		postAdded: function(post){
			//alert"added post to collection"+post.get('id')+" Total #Posts = "+listOfSelectedPosts.length);
			var listOfLabelsOfPost = post.listOfLabels;
			var self = this;

			//increment the count of labels by one for each post having that label
			_(listOfLabelsOfPost.models).each(function(aLabel){

				var labelView = _.detect(self.setOfLabelViews, function(view){
					return (view.id === aLabel.get('id'));
				});

				labelView.incrementSelectionCount();

			});

			//Ask the label views to update their display
			_(this.setOfLabelViews).each(function(view){
				view.update(listOfSelectedPosts.length);
			});

		},

		postRemoved: function(post){
			//alert"removing post: "+post.get('id')+" Total #Posts = "+listOfSelectedPosts.length);
			var listOfLabelsOfPost = post.listOfLabels;
			var self = this;
			//decrement the count of labels by one for each post having that label, that is removed from the selection
			_(listOfLabelsOfPost.models).each(function(aLabel){

				var labelView = _.detect(self.setOfLabelViews, function(view){
					return (view.id === aLabel.get('id'));
				});

				labelView.decrementSelectionCount();

			});

			_(this.setOfLabelViews).each(function(view){
				view.update(listOfSelectedPosts.length);
			});
		},

		menuAction: function(event){
			var button = event.target;

			if($(button).data('winbook-action')=="cancelCategorization")
				this.unrender();

			else if($(button).data('winbook-action')=="applyCategorization")
			{
				var updatedViews = _.select(this.setOfLabelViews, function(view){
					return (view.isUpdated()===true);
				});

				//extract the model of each view to pass to listOfSelectedPosts.updatePosts(...)
				var updatedLabels = _.pluck(updatedViews,'model');

				if(updatedLabels.length!=0)
					listOfSelectedPosts.updatePosts(updatedLabels);
				else
					alert("Nothing to update - Either no posts selected or categorization unchanged");
			}
		},

		/*
		 * Update the current state of each of the labelModels to the newState - to perform correct labelStateTransitions & toggling
		 * Invoked when listOfSelectedPosts has successfully synced all data with the backend.
		 */
		updateLabelStates: function() {
			_.each(this.setOfLabelViews, function(view){
				view.updateLabelState();
			});

			this.unrender();
		}

	});

//	View to show Tabbed separated values (or CSV if the need be)
	var TSVView = Backbone.View.extend({
		el: '#TSVLayer',
		initialize: function(){
			_.bindAll(this);
			$(this.el).css("position","absolute");
			$(this.el).css("top", (($(window).height() - $(this.el).outerHeight()) / 2) + $(window).scrollTop() + "px");
			$(this.el).css("left", (($(window).width() - $(this.el).outerWidth()) / 2) + $(window).scrollLeft() + "px");
			$(this.el).hide();
		},

		events : {
			"click button":"unrender"
		},

		render: function() {
			var self = this;
			var tsvText="";
			$('.wincondition').each(function(index){

				if($(this).is(':visible'))
				{
					var id = "WC_"+$(this).data('winbook-wcid');
					var content = $.trim($(this).find('.postData').eq(0).text());
					content = content.replace(/\n/g,' ');
					var status = $(this).data('winbook-status');
					//var data = $.trim($(this).find('.postData').text());
					tsvText += id+"\t"+content+"\t"+status+"\n";
				}

			});
			$('#TSVData').text(tsvText);
			$(this.el).show();
		},

		unrender: function() {
			$(this.el).hide();
		}
	});

	var MenuBarView = Backbone.View.extend({
		el:'#menuBar',
		initialize: function(){
			_.bindAll(this);
			this.categoryLabelManager = this.options.labelMenu;
			this.TSV = new TSVView();
		},
		events: {
			"click .menuItem":"menuAction",
			"click #checkAll":"toggleCheckboxes"
		},

		toggleCheckboxes: function(event){
			if($(event.target).is(':checked'))
			{
				$('.wcCheckbox').each(function(){
					if(!$(this).is(':checked') && $(this).parents('.post').is(':visible'))
						$(this).trigger('click');
				});
			}

			else 
			{
				$('.wcCheckbox').each(function(){
					if($(this).is(':checked') && $(this).parents('.post').is(':visible'))
						$(this).trigger('click');
				});
			}
		},

		menuAction: function(event) {

			switch($(event.target).data('winbook-action').toLowerCase())
			{
			case "categorize":
				if(!this.categoryLabelManager.isRendered)
				{
					this.categoryLabelManager.render();
					this.options.allPosts.getSelectedPosts();
				}
				else
					this.categoryLabelManager.unrender();
				break;

			case "showEquilibrium".toLowerCase():
				this.options.allPosts.toggleEquilibrium();
			break;

			case "exportTSV".toLowerCase():
				this.showTSV();
			break;

//			case "Prioritize".toLowerCase():
//				this.prioritize();
//			break;
			}	
		},

		showTSV: function() {
			this.TSV.render();
		},

//		prioritize: function() {
//			$.get(WinbookPageData.wallName+"/Prioritization/Criteria?type=technical", function(result, status){
//				$('#main').append(result);
//				$('.listOfPosts').addClass('listOfItems');
//				new Topsis.AppView({
//					el:$('#prioritizationLightbox'), 
//					wallName:WinbookPageData.wallName, 
//					itemType:'wincondition',
//					items: $('.listOfPosts')
//				});
//			});
//		}
	});



	var AgreementsView = Backbone.View.extend({
		initialize: function(){
			_.bindAll(this);
			this.isMarked = false;
			this.listOfStakeholdersAgreeing = this.$('.stakeholdersAgreeing');
		},

		render: function() {

			if(this.listOfStakeholdersAgreeing.length!=0)
			{
				var html = this.listOfStakeholdersAgreeing.html();
				this.listOfStakeholdersAgreeing.html(html.replace(WinbookPageData.userDisplayName,"You"));

				var agreementId = $(this.el).find('li:contains("You")').data('winbook-agreementid');

				if(agreementId)
					this.trigger('UserInAgreement',agreementId);
			}	

		},

		addAgreement: function(agreementId) {
			this.unMarkAgreementAsAgreeable();

			var agreementLI = '<li data-winbook-agreementid="<%= id %>"><%= name %></li>';
			var agreeToThisLI = '<li>&nbsp;<em>agree to this</em>&nbsp;</li>';

			var listItem;

			if(this.listOfStakeholdersAgreeing.length===0)
				this.listOfStakeholdersAgreeing = $('<ul class="stakeholdersAgreeing"></ul>').appendTo(this.el);

			var numChildren = this.listOfStakeholdersAgreeing.children().length;

			//can never be one child since "agree to this" is an li element				
			if(numChildren >= 2)
			{
				listItem = _.template(agreementLI,{id:agreementId,name:"You,&nbsp;"});
				$(listItem).prependTo(this.listOfStakeholdersAgreeing);
			}		 	
			else
			{
				listItem = _.template(agreementLI,{id: agreementId, name:"You"});
				$(this.listOfStakeholdersAgreeing).prepend(agreeToThisLI).prepend(listItem);
			}

			if($(this.el).is(":hidden"))
				$(this.el).fadeIn("medium"); 

		},

		removeAgreement: function() {
			this.unMarkAgreementAsAgreeable();

			var removeLastChild = false;
			var lastChild = this.$('li:last','.stakeholdersAgreeing');

			//if only the user agrees to it then remove his/her name + the "agree to this" li
			if($(this.el).find('li').length==2)
				removeLastChild = true;

			var self = this;
			$(this.el).find('li:contains("You")').fadeOut("medium",function(){
				$(this).remove();

				if(removeLastChild)
				{
					lastChild.remove();
					$(self.el).hide();
				}	
			});
		},

		markAgreementAsAgreeable: function(){

			this.isMarked = true;

			if(this.listOfStakeholdersAgreeing.length===0)
				this.listOfStakeholdersAgreeing = $('<ul class="stakeholdersAgreeing"></ul>').appendTo(this.el);

			$('<li class="mark"><em>(Marked for potential agreement by shaper)</em></li>').appendTo(this.listOfStakeholdersAgreeing);

			if($(this.el).is(":hidden"))
				$(this.el).fadeIn("medium");				
		},

		unMarkAgreementAsAgreeable: function() {

			//remove if marked for potential agreement
			if(this.isMarked)
			{
				this.$('li.mark').remove();
				this.isMarked = false;
			}	

		}	

	});

	var WCActionNavBarView = Backbone.View.extend({
		initialize: function(){
			_.bindAll(this,'actionHandler','postAgreement','postDisagreement','markAsDisagree','getURLForRequest','mayAgree','closeIssue','openIssue','raiseIssue','suggestOption');

			var agreementsList = $(this.el).parent().siblings('.Agreements');
			this.agreementsView = new AgreementsView({el:agreementsList});
			this.agreementsView.bind('UserInAgreement',this.markAsDisagree);
			this.agreementsView.render();
		},

		events: {
			"click .actionNavBarLink":"actionHandler"
		},

		actionHandler: function(event){
			event.stopPropagation();
			event.preventDefault();
			var action = $(event.target).data('winbook-action');

			switch(action.toLowerCase())
			{
			case "Agree".toLowerCase():
				//alert("Log agreement for WC "+this.id);
				this.postAgreement($(event.target));
			break;

			case "Disagree".toLowerCase():
				this.postDisagreement($(event.target));
			break;

			case "Comment".toLowerCase():
				alert("Commenting feature to be added for WC_ "+this.id);
			break;

			case "MayAgree".toLowerCase():
				this.mayAgree($(event.target));
			break;

			case "CloseIssue".toLowerCase():
				this.closeIssue($(event.target));
			break;

			case "OpenIssue".toLowerCase():
				this.openIssue($(event.target));
			break;

			case "Issue".toLowerCase():
				this.raiseIssue($(event.target));
			break;					

			case "Option".toLowerCase():
				this.suggestOption($(event.target));
			break;
			}
		},

		raiseIssue: function(actionNode){
			var formType="issue";
			var buttonText = formType.charAt(0).toUpperCase() + formType.slice(1);		
			var winConditionNode = actionNode.closest('.wincondition');
			var dataForm = tmpl("dataFormTemplate",{displayName:WinbookPageData.userDisplayName, avatar:WinbookPageData.userAvatar, postType:formType, postTypeButtonText:buttonText});
			//createInputForm(dataForm, winConditionNode); //now redundant
			var issueList = $('._listOfIssues',winConditionNode);
			var child = issueList.children(':first');
			if(child.hasClass('dataForm'))			
				child.remove();	
			else
			{
				issueList.prepend(dataForm);
				$('.dataArea',issueList).focus();
			}	
		},

		suggestOption: function(actionNode) {
			var formType="option";
			var buttonText = formType.charAt(0).toUpperCase() + formType.slice(1);		
			var issueNode = actionNode.closest('.issue');
			var dataForm = tmpl("dataFormTemplate",{displayName:WinbookPageData.userDisplayName, avatar:WinbookPageData.userAvatar, postType:formType, postTypeButtonText:buttonText});
			var optionsList = $('._listOfOptions',issueNode);
			var child = optionsList.children(':first');
			if(child.hasClass('dataForm'))				
				child.remove();					
			else
			{
				optionsList.prepend(dataForm);
				$('.dataArea',optionsList).focus();
			}	

		},

		//Select URL based on whether the agreement is for WinCondition or Option or Issue
		getURLForRequest: function(agreementNode, options) {
			var URL,list,wcid;

			if(agreementNode.closest('.post').hasClass("wincondition"))
				URL = WinbookPageData.wallName+"/WinConditions/"+this.id;

			else if (agreementNode.closest('.post').hasClass("option"))
			{
				//list = agreementNode.parents('._listOfIssues');
				//wcid = list.data('winbook-wcid');
				var issueId = agreementNode.parents('.issue').data('winbook-issueid');

				wcid = agreementNode.parents('.wincondition').data('winbook-wcid');

				URL = WinbookPageData.wallName+"/WinConditions/"+wcid+"/Issues/"+issueId+"/Options/"+this.id;
			}

			else if(agreementNode.closest('.post').hasClass("issue"))
			{
				//list = agreementNode.parents('.listOfIssues');
				wcid = agreementNode.parents('.wincondition').data('winbook-wcid');

				URL = WinbookPageData.wallName+"/WinConditions/"+wcid+"/Issues/"+this.id;

			}

			if(options)
			{
				//Specifies if the URL should be constructed with /Agreements
				if(options.withAgreements)
					URL = URL + "/Agreements";

				//For sending disgareement. Need to know which agreement is being revoked. Hence appendAgreementId
				if(options.appendAgreementId)
					URL = URL + "/"+agreementNode.data('winbook-agreementid');
			}



			return URL;

		},

		closeIssue: function(issueNode) {
			var showAnim = new WinbookPageData.LoadingAnimation({DOMElement:issueNode});
			var URL = this.getURLForRequest(issueNode);
			var self = this;

			$.ajax({
				url:URL,
				type: "PUT",
				data:"status=closed",
				success: function(result, status) {
					issueNode.text("Open Issue");
					issueNode.data('winbook-action','OpenIssue');
					$(self.el).closest('.issue').attr('data-winbook-status','closed');  //mark issue as closed.
					self.$('[data-winbook-action="Option"]').toggleClass("noOptionsAllowed");	//prevent posting of further options
					showAnim.unrender();
				},
				error: function(xhr, status){
					alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
					showAnim.unrender();
				}
			});
		},

		openIssue: function(issueNode) {
			var showAnim = new WinbookPageData.LoadingAnimation({DOMElement:issueNode});
			var URL = this.getURLForRequest(issueNode);
			var self = this;

			$.ajax({
				url:URL,
				type:"PUT",
				data:"status=open",
				success: function(result, status) {
					issueNode.text("Close Issue");
					issueNode.data('winbook-action','CloseIssue');
					$(self.el).closest('.issue').attr('data-winbook-status','open');
					self.$('[data-winbook-action="Option"]').toggleClass("noOptionsAllowed");
					showAnim.unrender();
				},

				error: function(xhr, status){
					alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
					showAnim.unrender();
				}
			});

		},

		mayAgree: function(agreementNode){

			var showAnim = new WinbookPageData.LoadingAnimation({DOMElement:agreementNode});
			var URL = this.getURLForRequest(agreementNode);
			var self = this;

			$.ajax({
				url:URL,
				type: "PUT",
				data: "agreement=maybe",
				success: function(result, status) {
					agreementNode.parent('.potentialAgreement').hide();
					$(self.el).parents('.wincondition').attr("data-winbook-status","maybe");
					showAnim.unrender();
					self.agreementsView.markAgreementAsAgreeable();
				},

				error: function(xhr, status){
					alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
					showAnim.unrender();
				} 
			})

		},

		postAgreement: function(agreementNode){
			var showAnim = new WinbookPageData.LoadingAnimation({DOMElement:agreementNode});
			var URL = this.getURLForRequest(agreementNode,{withAgreements:true, appendAgreementId:false});
			var self =this;

			$.ajax({
				url:URL,
				type: "POST",					
				success: function(result, status) {
					//alert("Agreement logged: Id "+result);
					agreementNode.text("Disagree");
					agreementNode.data('winbook-action',"Disagree");
					agreementNode.data('winbook-agreementid',result);

					if(self.$('.potentialAgreement').is(":visible"))
						self.$('.potentialAgreement').hide();

					showAnim.unrender();
					self.agreementsView.addAgreement(result);
				},

				error: function(xhr, status){
					alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
					showAnim.unrender();
				}
			});

			if (window.WinbookPageData.userRole === "shaper")
			{
				URL = this.getURLForRequest(agreementNode);
				$.ajax({
					url:URL,
					type: "PUT",	
					data: "agreement=agree",				
					success: function(result, status) {
						$(self.el).closest('.post').attr("data-winbook-status","agree");
					},

					error: function(xhr, status){
						alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
					}
				});

			}

		},

		postDisagreement: function(disagreementNode) {
			var showAnim = new WinbookPageData.LoadingAnimation({DOMElement:disagreementNode});
			var URL = this.getURLForRequest(disagreementNode,{withAgreements:true, appendAgreementId:true}); 
			var self=this;

			$.ajax({
				url:URL,
				type: "DELETE",
				success: function(result,status) {
					disagreementNode.text("Agree");
					disagreementNode.data('winbook-action',"Agree");
					disagreementNode.removeData('winbook-agreementId');
					self.$('.potentialAgreement').show();
					showAnim.unrender();
					self.agreementsView.removeAgreement();
				},

				error: function(xhr, status){
					alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
					showAnim.unrender();
				}
			});

			if (window.WinbookPageData.userRole === "shaper")
			{
				URL = this.getURLForRequest(disagreementNode);
				$.ajax({
					url:URL,
					type: "PUT",
					data: "agreement=open",					
					success: function(result, status) {
						$(self.el).closest('.post').attr("data-winbook-status","open");
					},

					error: function(xhr, status){
						alert("Status: "+xhr.status+" = "+xhr.statusText+": "+xhr.responseText);
					}
				});
			}
		},

		markAsDisagree: function(agreementId)
		{
			var agreementNode = this.$('[data-winbook-action="Agree"]');
			agreementNode.text("Disagree");
			agreementNode.attr('data-winbook-action','Disagree');
			agreementNode.data('winbook-agreementid',agreementId);
			this.$('.potentialAgreement').hide();
		}   

	});

	var WCPostView = Backbone.View.extend({			

		initialize: function() {
			_.bindAll(this,"render","postClicked","addPostToSelection","removeLabel","updateLabelView","showPostInCategory");
			this.id = $(this.el).data('winbook-wcid');
			this.model = new PostModel({id:this.id});

			this.actionNavBar = new WCActionNavBarView({
				el:$('.actionsNavBar',this.el).eq(0),
				id: this.id
			});

			var self = this;
			$(this.el).find('.categoryLabel').each(function(index){
				var aLabel = new PostLabel({id:$(this).data('winbook-categoryid')});
				self.model.addLabel(aLabel);
			});
			//POC: Listening to custom event
			this.model.bind('updateSuccess',this.render);
		},

		events: {
			"click .wcCheckbox":"postClicked"
		},

		showPostInCategory: function(categoryId){
			var items = $(this.el).find('[data-winbook-categoryid="'+categoryId+'"]');

			if(items.length > 0)
			{
				$(this.el).show();
				//$('.listOfIssues[data-winbook-wcid="'+this.id+'"]').show();
				//$('.listOfOptions[data-winbook-wcid="'+this.id+'"]').show();
			}
			else
			{
				$(this.el).hide();
				//$('.listOfIssues[data-winbook-wcid="'+this.id+'"]').hide();
				//$('.listOfOptions[data-winbook-wcid="'+this.id+'"]').hide();
			}	
		},

		render: function(){
			//alert("Render called for Post: "+this.model.get('id'));
			//ignore labels with action = "none" - only have to work with add/delete (can also use _.select(...) if you wish)
			var labelsToUpdate = _.reject(this.model.listOfLabels.models,function(label){
				return (label.get('action')==="none");
			});

			if(_.isEmpty(labelsToUpdate))
				return this;

			//capturing the "this" context to be used inside _.each to refer to this.el as self.el
			var self = this;

			_(labelsToUpdate).each(function(label){
				if(label.get('action')==="delete")
				{
					$(self.el).find('.categoryLabel[data-winbook-categoryid='+label.get('id')+']').fadeOut("medium",function(){$(this).remove()});
					self.model.removeLabel(label);
				}	

				else if(label.get('action')==="add")
				{
					//get the details of the label from the category listing
					var categoryLabel = $('.category[data-winbook-categoryid="'+label.get('id')+'"]','.categoryListing');
					var color = categoryLabel.children('.labelColorPicker').val();
					var name = $.trim(categoryLabel.children('.labelDescriptor').text());

					//create the corresponding html element 
					var labelElement = tmpl("labelViewTemplate",{labelId:label.get('id'),labelColor:color,labelName:name});
					//...and append it to the current list of labels
					$(self.el).find('.categorizationDetails').append(labelElement);

					/*
					 * style the label as per the rest of the document 
					 * For some reason drawLabel(labelElement) doesn't seem to work. Seems jQlabel goofs up on "out of DOM" elements
					 * Hence the need to query the DOM to locate the last added label and explicitly pass it to drawLabel...
					 */
					drawLabel($(self.el).find('.categorizationDetails').children('.categoryLabel:last'));

					//set label action to "none" - to prevent future conflicts i.e., to prevent a resent of the "add" request
					label.set({action:"none"});
				}


			});

			return this;

		},

		postClicked: function(event) {

			if($(event.target).is(':checked'))				
			{
				//Re-process all the labels, since new labels could be added from last time.
				var self = this;
				$(this.el).find('.categoryLabel').each(function(index){
					var aLabel = new PostLabel({id:$(this).data('winbook-categoryid')});
					self.model.addLabel(aLabel);
				});

				$(this.el).toggleClass("isCategorizing");

				//add post to collection iff categorization is in progress.
				if($('.categoryListing').hasClass("isCategorizing"))
					listOfSelectedPosts.add(this.model);
			}

			else
			{
				$(this.el).toggleClass("isCategorizing");

				//remove post from collection iff categorization is in progress.
				if($('.categoryListing').hasClass("isCategorizing"))
					listOfSelectedPosts.remove(this.model);	
			}


		},

		addPostToSelection: function() {
			var checkbox = $(this.el).find('.wcCheckbox');

			if($(checkbox).is(':checked') && $(this.el).is(':visible'))
				listOfSelectedPosts.add(this.model);
		},

		//Overriding remove - deleting item from listOfSelectedPosts if selected during categorization and calling the View's remove() method
		remove: function() {
			if($(this.el).find('.wcCheckbox').is(':checked') && $('.categoryListing').hasClass("isCategorizing"))
				listOfSelectedPosts.remove(this.model);

			Backbone.View.prototype.remove.call();
		},

		removeLabel: function(labelId){
			this.model.removeLabel(labelId,{removeById:true});
			$(this.el).find('.categoryLabel[data-winbook-categoryid='+labelId+']').fadeOut("slow",function(){
				$(this).remove();
			});
		},

		updateLabelView: function(label){
			if(label.color)
				$(this.el).find('.categoryLabel[data-winbook-categoryid='+label.id+']').jQLabel({backgroundColor:label.color});
		}
	});

	var OptionPostView = Backbone.View.extend({
		initialize: function(){
			this.id = $(this.el).data('winbook-optionid');
			this.actionNavBar = new WCActionNavBarView({
				el:$('.actionsNavBar',this.el).eq(0),
				id: this.id
			});
		}

	});

	var IssuePostView = Backbone.View.extend({
		initialize: function() {
			this.id = $(this.el).data('winbook-issueid');
			this.actionNavBar = new WCActionNavBarView({
				el:$('.actionsNavBar',this.el).eq(0),
				id: this.id
			});
		}

	});

	var ListOfPostsView = Backbone.View.extend ({
		el:'.listOfPosts',

		initialize: function(){
			_.bindAll(this,"addWinCondition","getSelectedPosts","removeWinCondition","deleteCategoryLabel","updateLabelView","addOption","showPostsInCategory","showAllPosts","addIssue");
			this.listOfWCPostViews = [];
			this.listOfOptionViews = [];
			this.listOfIssueViews = [];
			this.labelManager = this.options.labelManager;
			this.labelManager.bind('categoryLabelDeleted',this.deleteCategoryLabel);
			this.labelManager.bind('categoryLabelUpdated',this.updateLabelView);
			this.labelManager.bind('showPostsInCategory',this.showPostsInCategory);
			this.labelManager.bind('showAllPosts',this.showAllPosts);

			var self = this;
			$(this.el).find('.wincondition').each(function(){
				var aView = new WCPostView({el:$(this),id:$(this).data('winbook-wcid')});
				self.listOfWCPostViews.push(aView);
			});	

			this.$('.option').each(function(){
				self.addOption($(this));
			});

			this.$('.issue').each(function(){
				self.addIssue($(this));
			});
		},

		showAllPosts: function() {
			this.$('.wincondition').show();
			this.$('._listOfIssues').show();
			this.$('._listOfOptions').show();
		},

		showPostsInCategory: function(categoryId) {
			_.each(this.listOfWCPostViews, function(view){
				view.showPostInCategory(categoryId);
			});

		},


		//Tell each view to remove the label if it has it.
		deleteCategoryLabel: function(labelId){
			_.each(this.listOfWCPostViews, function(view){
				view.removeLabel(labelId);
			});
		},

		updateLabelView: function(label) {
			//alert("List of posts need to update label id "+label.id+" to color hex: "+label.color);
			_(this.listOfWCPostViews).each(function(view){
				view.updateLabelView(label);
			});
		},

		//MAY NEED TO ADD REMOVE OPTION METHOD TOO...
		addOption: function(option){
			var aView = new OptionPostView({el:option});
			this.listOfOptionViews.push(aView);
		},


		//MAY NEED TO ADD REMOVE ISSUE METHOD TOO...
		addIssue: function(issue) {
			var aView = new IssuePostView({el:issue});
			this.listOfIssueViews.push(aView);
		},

		addWinCondition: function(winCondition) {
			var aView = new WCPostView({el:winCondition,id:winCondition.data('winbook-wcid')});
			this.listOfWCPostViews.push(aView);
		},

		removeWinCondition: function(winCondition){
			var wcView = _.detect(this.listOfWCPostViews, function(view){
				return (view.id==winCondition.data('winbook-wcid'));
			});
			//remove view from internal array				
			var index = _.indexOf(this.listOfWCPostViews,wcView);
			this.listOfWCPostViews.splice(index,1);

			wcView.remove();
			wcView = null; //for GC
		},

		//get list of selected posts. Delegates to each view to add itself if the post is selected
		getSelectedPosts: function() {
			_.each(this.listOfWCPostViews, function(aView){
				aView.addPostToSelection();
			});
		},

		toggleEquilibrium: function() {
			$(this.el).toggleClass("equilibrium");
		}

	});

	/*
	 * WinConditionApp is just an object handling the creation of the various components. References of one component
	 * are passed to another in a class OOAD fashion. Ideally it should've been event based but this was
	 * the first Backbone-based app and coming up the learning curve and hence best-practices weren't well understood.
	 * More so: WinConditionApp was written with Backbone v0.5.3. BB was substantially modified/updated for v0.9.2 and
	 * many patterns like an event bus made easy to create using a single line of code. Thus a good but rather 
	 * difficult refactoring would be to make these components event-based so as to be instantiable individually.
	 */
	
	WinConditionApp.createLabelManager = function(){
		this.labelMenuView =  new LabelMenuView(); 
	};

	WinConditionApp.create = function(){
		this.initWall(); //defined in an external file: wallInit.js
		this.postListView = new ListOfPostsView({labelManager:this.labelMenuView});
		this.menuView = new MenuBarView({labelMenu:this.labelMenuView, allPosts:this.postListView});
	};

	WinConditionApp.close = function(){
		this.postListView.undelegateEvents();
		this.postListView.off();
		this.postListView.remove();

		this.menuView.undelegateEvents();
		this.menuView.off();
		this.menuView.remove();
	};
	
	WinConditionApp.hasLabelManager = function(){
		if(this.lableMenuView)
			return true;
		else
			return false;
	};
	
	WinConditionApp.getLabelManager = function(){
		return this.labelMenuView;
	};
	
	WinConditionApp.getListOfPosts = function(){
		return this.postListView;
	};
	
	WinConditionApp.getMenu = function(){
		return this.menuView;
	}

}).call(this, this.WinConditionApp = this.WinConditionApp || {});
