(function(GoalApp){
	var root = this;

	var dispatcher = _.clone(Backbone.Events);

	var GoalModel = Backbone.Model.extend({
		defaults: {
			weight: 50,
			revision: 1,
			forPrioritization: true,
			details: null
		},

		urlRoot: 'Wall/Criteria/Business'
	});

	var GoalModels = Backbone.Collection.extend({
		model: GoalModel,
	});

	var CloseableView = Backbone.View.extend({
		initialize: function(){
			dispatcher.on('benefits:close',this.close,this);
		},

		close: function(){
			this.undelegateEvents()
			this.off();
			this.remove();
		}
	});

	var GoalInputView = CloseableView.extend({
		initialize: function(){
			CloseableView.prototype.initialize.apply(this, arguments);
			_.bindAll(this);
			//initialize values for the char counter
			this.minInputLength = 5;
			this.maxInputLength = 60;
			this.counterSpan = this.$el.children('span');
			this.button = this.$el.children('button');
			this.input = this.$el.children('input#goalName');

			this.defaultValue = "Enter a success-criteria/goal here...";
			this.button.prop('disabled',true);

			this.render();
		},

		render: function(){
			this.input.val(this.defaultValue);
			this.input.addClass('empty');
		},

		events: {
			"click button":"addGoal",
			"keyup input" :"charCount",
			"focus input" :"clearInput",
			"blur input" :"clearInput",
		},

		clearInput: function(e){
			
			if(this.input.val()===this.defaultValue)
			{
				this.input.val('');
				this.input.removeClass('empty');
			}

			else if(e && e.type==="focusout")
			{
				if(this.input.val().length==0)
					this.resetInput();
			}
		},

		charCount: function(e){
			var length = this.input.val().length;
			textLengthCounter(length, this.minInputLength, this.maxInputLength, this.counterSpan, this.button);
			if(e && e.which==13 && length >= this.minInputLength && length <= this.maxInputLength)
			{
				this.input.blur();
				this.addGoal();
			}
		},

		addGoal: function(e){
			var loading = new WinbookPageData.LoadingAnimation({DOMElement:this.button});
			var title = this.input.val(),
			goal = new GoalModel(),
			self = this;
			goal.save({'title':title}, {
				success: function(model, response){
					self.collection.add(model);
					loading.unrender();
					self.refocusInput();
				},
				
				error: function(model, response){
					loading.unrender();
				}
			});
		},

		refocusInput: function(){
			this.input.val('');
			this.input.focus();
			this.charCount(); //to initialize the counting and disable the button.
		},
		
		resetInput: function(){
			this.input.val(this.defaultValue);
			this.input.addClass('empty');
		}

	});

	var GoalView = CloseableView.extend({
		initialize: function(){
			CloseableView.prototype.initialize.apply(this, arguments);
			_.bindAll(this);
			this.model.on('change',this.render);
			this.model.on('destroy',this.unrender);
			this.successSlider = this.$el.find('.successSlider');
			this.weightCounter = this.$el.find('.weightCounter')
			this.renderSlider();
		},

		renderSlider: function(){
			var self = this;
			this.successSlider.slider({
				range:'min',
				min:0,
				max:100
			});

			this.render();

		},

		render: function(){
			var self = this;
			this.successSlider.slider("value",this.model.get('weight'));
			this.weightCounter.text('('+this.model.get('weight')+')');
		},

		unrender: function(){
			this.undelegateEvents();
			this.off();
			$(this.el).fadeOut("medium", function(){
				$(this).remove();
			});
		},

		events: {
			"slide .successSlider":"updateWeight",
			"slidestop .successSlider":"syncWeight",
			"click .delete":"deleteGoal"
		},

		deleteGoal: function(e){
			e.preventDefault();
			this.model.destroy({wait:true});
		},

		updateWeight: function(event, ui){
			this.model.set('weight',ui.value);
		},

		syncWeight: function(event, ui){
			this.model.save({weight:ui.value},{error: this.saveError});
		},

		saveError: function(model, response){
			if(response.status===409)
			{
				var obj = JSON.parse(response.responseText),
				attr = model.changedAttributes(obj);

				//If only revision/version has been updated, ignore change and return
				if(_.size(attr)==1 && _.has(attr,'revision'))
					return;

				delete attr.revision;
				this.model.set(obj);
				dispatcher.trigger('saveError', model, response, attr);

			}

			else if(response.status===410)
			{
				dispatcher.trigger('goalDeleted', model, response);
				this.model.trigger('destroy');
			}
		}
	});

	var GoalListView = CloseableView.extend({

		initialize: function(){
			CloseableView.prototype.initialize.apply(this, arguments);
			_.bindAll(this);
			this._template = _.template($('#goalTemplate').html()),
			this.infoLi = this.$el.find('#info-li');
			this.collection.on('add',this.addGoal);
			this.collection.on('remove',this.goalRemoved);
			this.render();
		},

		/*
		 * Render the goal after it has been created by the user and successfully saved on server.
		 */
		addGoal: function(goalModel) {
			var goalEl = $(this._template(goalModel.toJSON())).hide().prependTo(this.$el);
			goalEl.fadeIn("medium");

			var view = new GoalView({el:goalEl, model:goalModel});

			if(this.infoLi.is(':visible'))
				this.infoLi.fadeOut('fast');
		},
		
		goalRemoved: function(){
			if(this.collection.length==0)
				this.infoLi.fadeIn('slow');
		},

		render: function(){
			var self = this;
			this.$el.find('.post.goal').each(function(){

				var goalModel = new GoalModel({
					id: $(this).data('winbook-goalid'),
					weight: $(this).data('winbook-weight'),
					forPrioritization: $(this).data('winbook-prioritization'),
					title: $.trim($(this).find('.title').text()),
					revision: $(this).data('winbook-version')
				});

				self.collection.add(goalModel,{silent:true}); //prevent triggering of addGoal.

				new GoalView({el:$(this), model:goalModel});
			});

			return this;

		}
	});

	var ErrorView = CloseableView.extend({
		_errorTemplate: '<div title = "Goal Save Error"><%= statusCode %> <%= statusMsg %><br>'
			+'<strong>"<%= title %>"</strong> was updated by another user and has been reset to the new value(s)<br>'
			+ '<% _.each(attr, function(key, value){ %>'
			+'<%= value %> : <%= key %> <br>'
			+ '<% }); %>'
			+'</div>',

			_deleteTemplate: '<div title = "Goal Deleted"><%= statusCode %> <%= statusMsg %><br>'
				+'<strong>"<%= title %>"</strong> was deleted by another user and has been removed from view<br>'
				+'</div>',


				initialize: function(){
					CloseableView.prototype.initialize.apply(this, arguments);
					_.bindAll(this);
					dispatcher.on('saveError', this.saveError);
					dispatcher.on('goalDeleted', this.deleteError);
					this.errorTemplate = _.template(this._errorTemplate);
					this.deleteTemplate = _.template(this._deleteTemplate);
				},

				saveError: function(goal, response, changedAttributes){
					var obj = {title:goal.get('title'),statusCode:response.status, statusMsg:response.statusText, attr:changedAttributes};
					this.render(this.errorTemplate, obj);

				},

				deleteError: function(goal, response){
					var obj = {title:goal.get('title'), statusCode:response.status, statusMsg:response.statusText};
					this.render(this.deleteTemplate, obj);
				},

				render: function(template, data){
					$(template(data)).dialog({
						modal:true,
						buttons: {
							OK: function(){
								$(this).dialog("close");
							}
						}
					});
				}
	});

	var MenuBarView = CloseableView.extend({
		el:'#menuBar',
		initialize: function(){
			CloseableView.prototype.initialize.apply(this, arguments);
			_.bindAll(this);
		},

		events: {
			"click .menuItem":"menuAction"
		},

		menuAction: function(e) {
			if($(e.target).data('winbook-action').toLowerCase()==='prioritize')
				this.prioritize();
		},

		prioritize: function() {
			$.get(WinbookPageData.wallName+"/Prioritization/Criteria?type=business", function(result, status){
				$('#main').append(result);
				var categoryListing = $('.categoryListing');
				categoryListing.addClass('listOfItems');
				$('.MMF',categoryListing).addClass('item');
				new Topsis.AppView({
					el:$('#prioritizationLightbox'),
					wallName:WinbookPageData.wallName,
					itemType:'mmf',
					items: $(categoryListing)
				});
			});
		}

	});	

	var VisionModel = Backbone.Model.extend({
		urlRoot:'../',
		toJSON: function(){
			return {vision:this.get('vision')};
		}
	});
	
	var VisionView = Backbone.View.extend({
		el:'#postArea',
		
		defaultText: "Enter Vision Here",
		
		initialize: function(){
			_.bindAll(this);
			this.counterSpan = this.$('#visionLength');
			this.button = this.$('#postVision');
			this.visionHolder = this.$('#visionHolder');
			this.visionText = this.$('#visionText');
			this.minLength = 15;
			this.maxLength = 600;
			
			this.visionText.autoResize({
				animate:false,
				extraSpace: 0
			});
			
			var vision = $('#vision').html();
			
			if(vision.length>0)
			{
				this.model.set('vision', vision);
				this.visionHolder.append(vision);
			}
			else
				this.visionHolder.append(this.defaultText);
			
		},
		
		events: {
			"click #visionHolder":"addEditVision",
			"keyup #visionText":"wordCounter",
			"click #postVision":"postVision"
		},
		
		addEditVision: function(){
			this.toggleView();
			
			if(this.visionHolder.text()===this.defaultText)
				this.visionText.text('');
			else
				this.visionText.text(this.visionHolder.text());
			
			this.visionText.focus();
		},
		
		wordCounter: function(e){
			if(e.which==27)
			{
				this.toggleView();
				return;
			}
			textLengthCounter(this.visionText.val().length, this.minLength, this.maxLength, this.counterSpan, this.button);
		},
		
		postVision: function(){
			var length = this.visionText.val().length;
			if( length >= this.minLength && length<=this.maxLength)
			{
				var text = this.visionText.val();
				text.replace(/\s\s+/g,' ');
				text = $.trim(text);
				this.visionText.text(text);
				this.visionHolder.text(text);
				this.model.set('vision',text);
				this.model.save();
				this.toggleView();
			}
		},
		
		toggleView: function(){
			this.visionHolder.toggle();
			this.visionText.toggle();
			this.button.toggle();
		},
		
	});

	var GoalAppView = Backbone.View.extend({
		el:'#main',

		initialize: function(){
			_.bindAll(this);
			this.render();
		},

		render: function(){
			//var menuBarView = new MenuBarView(),
			var	errorView = new ErrorView(),
				goalModels = new GoalModels(),
				goalListView = new GoalListView({el:$('.listOfPosts'), collection:goalModels}),
				goalInputView = new GoalInputView({el:$('#goalInput'), collection:goalModels}),
				visionModel = new VisionModel({id:$('#projectName').val()}),
				visionView = new VisionView({model:visionModel});

			return this;
		},

		unrender: function(){
			dispatcher.trigger('benefits:close');
		}
	});
	
	GoalApp.create = function(){
		this.app = new GoalAppView();
	};
	
	GoalApp.close = function(){
		this.app.unrender();
	};

}).call(this, this.GoalApp = this.GoalApp || {});



