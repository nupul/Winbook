(function CriteriaHandler(){	 

	var SliderModel = Backbone.Model.extend({});

	var CriterionView = Backbone.View.extend({
		initialize: function() {
			_.bindAll(this, 'render','flipDirection');
			this.model = new SliderModel;
			this.model.set({
				'id':this.$el.attr('id'),
				'sliderValue':this.$('.slider').data('winbook-slidervalue'),
				'criterion':this.$('.criterionName').text(),
				'direction':this.$('.directionOfPreference').data('winbook-direction-up')
			});
			this.render();	
		},

		render: function() {
			var that = this;	
			this.$('.slider').slider({
				orientation:'vertical',
				value:that.model.get('sliderValue'),
				range:'min',
				min:0,
				max:100
			});

			var sliderDirNode = this.$('.arrow');
			if (this.model.get('direction')===1)
				sliderDirNode.toggleClass('ui-icon-arrowthick-1-n');
			else
				sliderDirNode.toggleClass('ui-icon-arrowthick-1-s');
		},

		events: {
			"click .directionOfPreference":"flipDirection",
			"slide .slider":"sliderChanged",
			"slidechange .slider":"sliderStop",
		},

		flipDirection: function(event) {
			console.log("Flipping...");
			var spanNode = this.$('.arrow');
			spanNode.toggleClass('ui-icon-arrowthick-1-n').toggleClass('ui-icon-arrowthick-1-s');

			if(spanNode.hasClass('ui-icon-arrowthick-1-n'))
				this.model.set({'direction':1});
			else
				this.model.set({'direction':0});
		},

		sliderChanged: function(event, ui) {
			console.log("Slider value being changed to "+ui.value+" for Criterion --> "+this.model.get('id'));
		},

		sliderStop: function(event, ui) {
			console.log("Slider value stopped at val = "+ui.value);
		}

	});

	var CriteriaView = Backbone.View.extend({
		el:'#criteriaContainer',
		initialize: function(){
			this.$('.criterion').each(function(){
				new CriterionView({el:$(this)});
				console.log("crating views...");
			});
		}
		
	});
	
	
	Prioritization.setUp = function(){
		new CriteriaView;
		$('.listOfPosts').appendTo('#winconditionsHolder');
	}


	
})(window.Prioritization = window.Prioritization || {});

