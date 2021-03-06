class SliderModel extends Backbone.Model

class CriterionView extends Backbone.View
	initialize: =>
		@model = new SliderModel
		@model.set
			'id':$(this.el).attr('id')
			'sliderValue':@$('.slider').data('winbook-slidervalue')
			'criterion':@$('.criterionName').text()
			'direction':@$('.directionOfPreference').data('winbook-direction-up')
		@render()
	render: =>
		this.$('.slider').slider
			orientation:'vertical'
			value:@model.get 'sliderValue'
			range:'min'
			min:0
			max:100
		sliderDirNode = @$('.arrow')
		if @model.get('direction') is 1
			sliderDirNode.toggleClass 'ui-icon-arrowthick-1-n'
		else
			sliderDirNode.toggleClass 'ui-icon-arrowthick-1-s'
		return @
	events:
        	"click .directionOfPreference" : "flipDirection"
	        "slide .slider" : "sliderChange"
        	"slidechange .slider" : "sliderStop"
	flipDirection: =>
		console.log "Flipping...xxx"
		spanNode = @$('.arrow')
		spanNode.toggleClass('ui-icon-arrowthick-1-n').toggleClass('ui-icon-arrowthick-1-s')
		if spanNode.hasClass('ui-icon-arrowthick-1-n')
			@model.set "direction":1
		else
			@model.set "direction":0
		return @
	sliderChange: (event, ui) =>
		console.log "Slider value being changed to "+ui.value+" for criterion --> "+@model.get('id')
		return @
        
	sliderStop: (event, ui) =>
		console.log "Slider value stopped at val = "+ui.value
		return @

class window.CriteriaView extends Backbone.View
	el: '#criteriaContainer'
    
	initialize: =>
		@$('.criterion').each ->
			new CriterionView el:$ @
			console.log "creating views..."
			return
		return @

