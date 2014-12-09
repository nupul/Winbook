(function(WallApp){
	
	var PageRouter = Backbone.Router.extend({
		initialize: function(){
			_.bindAll(this);
			this.main = $('#main');
			this.MMFEl = $('#MMF-select');
			this.categories = $('.categoryListing');
			this.main.append($('#loadingTemplate').html());
		},
		
		routes: {
			"":"redirect",
			"Benefits" : "showGoals",
			"WinConditions":"showWCs",
			"Analytics":"showAnalytics",
			"Benefits/prioritize":"prioritizeMMFs",
			"WinConditions/prioritize":"prioritizeWCs"
		},
		
		//Temporary hack to force the page to go to benefits till root page is developed.			
		redirect: function(){
			this.navigate("Benefits",{trigger:true});
		},
		
		showGoals: function(){
			if(this.app)
				this.app.close();
			
			var self = this;
			this.main.load(WinbookPageData.wallName+'/Benefits', function(){
				self.app = GoalApp;
				GoalApp.create();
				self.MMFEl.children('#isMMF').prop('checked',true);
				self.MMFEl.hide();
				self.categories.children('.category').not('.MMF').hide();
			})
		},
		
		showWCs: function(){
			if(this.app)
				this.app.close();
			
			var self = this;
			this.main.load(WinbookPageData.wallName+'/WinConditions', function(){
				self.app = WinConditionApp;
				WinConditionApp.create();
				self.categories.children('.category').not('.all').show();
				self.MMFEl.show().children('#isMMF').prop('checked',false);
			});
		},
		
		showAnalytics: function(){
			if(this.app)
				this.app.close();
			
			this.main.empty();
			this.app = AnalyticsApp;
			this.app.create();
		},
		
		prioritizeWCs: function(){
			var self = this,
				items = $('.listOfPosts');
			$.get(WinbookPageData.wallName+"/Prioritization/Criteria?type=technical", function(result, status){
				self.main.append(result);
				items.addClass('listOfItems');
				items.children('.wincondition').addClass('item');
				
				this.prioritizationApp = new Topsis.AppView({
					wallName:WinbookPageData.wallName, 
					itemType:'wincondition',
					items: $('.listOfPosts'),
					close: function(items){
						self.main.append(items);
						self.navigate('WinConditions');
						delete self.prioritizationApp;
					},
					context:self
				});
			});
		},
		
		prioritizeMMFs: function(){
			var self = this;
				categoryListing = $('.categoryListing'),
				parent = categoryListing.parent();
			
			$.get(WinbookPageData.wallName+"/Prioritization/Criteria?type=business", function(result, status){
				self.main.append(result);
				categoryListing.addClass('listOfItems');
				$('.category',categoryListing).not('.MMF').hide();
				$('.MMF',categoryListing).addClass('item');
				
				this.prioritizationApp = new Topsis.AppView({
					wallName:WinbookPageData.wallName,
					itemType:'mmf',
					items: $(categoryListing),
					close: function(items){
							items.removeClass('.listOfItems');
							parent.append($(items));
							self.navigate('Benefits');
							delete self.prioritizationApp;
					},
					context:self
				});
			});
		}
	});
	
	WallApp.startRouter = function(appRoot){
		this.router = new PageRouter;
		Backbone.history.start({root:appRoot});
	};
	
	var AppManager = {};
	
	AppManager.addApp = function(app){
		this._app = app;
	};
	
	AppManager.addPrioritizationApp = function(prioritizationApp){
		if(this._app)
			this._app.prioritizationApp = prioritizationApp;
	};
	
	AppManager.closeApp = function(){
		if(this._app)
		{
			if(this._app.prioritizationApp)
			{
				this._app.prioritizationApp.closeApp();
				delete this._app.prioritizationApp;
			}
			
			this._app.close();
		}	
	};
	
	
	
}).call(this,this.WallApp = this.WallApp ||{});