(function(AnalyticsApp){

	var HistoryModel = Backbone.Model.extend({
		//Need to get rid of the direct reference to the wall.
		urlRoot:'Wall'+'/Analytics'
	});
	
	var HistoryView = Backbone.View.extend({
		initialize: function(){
			_.bindAll(this);
			this.model.on('change',this.render);
		},
		
		render: function(){
			console.log("Progress-->");
			console.log(this.model.get('progress'));
			var options = {
					chart: {
						renderTo: 'historyContainer'
					},
					
					title: {
						text: 'Negotiation History From Project Start'
					},
					
					xAxis: {
						type: 'datetime',
						dateTimeLabelFormats: {
							day: '%e. %b'
						}
					},
					
					yAxis: {
						title: {
							text: 'Count'
						},
						
						min: 0
					},
					
					series: [
					         {name:'Open win conditions'},
					         {name:'Agreed win conditions'},
					         {name:'Potentially agreed win conditions'}
					]
					
			};
			
			var date = new Date(), now = new Date(), open = [], agreed = [], maybe = [];
			
			_.each(this.model.get('progress'), function(value, key, list){
				date = new Date();
				date.setDate(now.getDate() - Number(key));
				var utc = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
				open.push([utc, Number(value.open)]);
				agreed.push([utc, Number(value.agreed)]);
				maybe.push([utc, Number(value.maybe)]);
				
			});
			
			options.series[0].data = open;
			options.series[1].data = agreed;
			options.series[2].data = maybe;

			options.colors = ['#AA4643', '#89A54E', '#F6A828']
			
			var chart = new Highcharts.Chart(options);
		}
	});
	
	
	var AppView = Backbone.View.extend({
		el: '#main',
		
		initialize: function(){
			var historyEl = this.$el.append('<div id="historyContainer"></div>');
			this.render();
		},
		
		render: function(){
			var historyModel = new HistoryModel();
			historyModel.fetch();
			new HistoryView({el:this.historyEl, model:historyModel});
			
		},
		
		unrender: function(){
			console.log("close app");
		}
	});
	
	AnalyticsApp.create = function(){
		this.app = new AppView();
	};
	
	AnalyticsApp.close = function(){
		this.app.unrender();
	}
	
	
}).call(this, this.AnalyticsApp = this.AnalyticsApp || {})
