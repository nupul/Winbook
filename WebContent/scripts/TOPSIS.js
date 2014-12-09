(function(){
	
	//root = window object and Attach the Topsis variable to Root and refer to it locally as Topis. Think of it like a namespace.
	var root = this; 
	var Topsis = root.Topsis = {};
	//Event dispatcher i.e. event-bus for communicating across different Models/Views/Collections 
	var dispatcher = _.clone(Backbone.Events);
	
	/*
	 * Create a BaseView for all classes to inherit from. "Intercept" Backbone's constructor
	 * by providing a hook for any custom initialization that needs to be done across views.
	 */
		//reference to Backbone.View's constructor
		var ctor = Backbone.View;
		//extend Backbone.View
		var BaseView = Backbone.View.extend({
			//override the constructor property
			constructor: function(options){
				//call Backbone.View's constructor (ctor) and just proxy the arguments to it
				ctor.apply(this, arguments);
				//perform initialization here
				dispatcher.on('close',this.close, this);
			},
			
			//Adding a custom close method inheritable by all 'children' of BaseView. 
			close: function(){
				//if an onClose 'handler' is defined by the class execute it - for any custom 'close' logic to be called
				if(this.onClose)
					this.onClose();
				
				this.off();
				this.undelegateEvents();
				this.remove();
			}
		});

	
	/*
	 * individual criterion score cell - indicating the item (win condition or MMF)
	 * and the criterion that it belongs to i.e. row/column it belongs to:
	 * Row-id = item-Id
	 * Col-id = criterion-Id
	 * BEWARE: models have property called model.collection which points to the first collection that they were added to
	 * i.e. ItemModel (row) in this case. This will NOT be overwritten when adding this model to another collection.
	 * However the urlRoot property of the parent collection is what is referred to in save/fetch. So if the order of
	 * adding is changed, it would refer to an incorrect element!! Resetting a collection clears all references
	 * of model.collection and it could be an inadvertent side effect if the order is not know. For that reason
	 * it's safer to directly provide the url property of this model than relying on urlRoot.
	 */
	var CriterionScore = Backbone.Model.extend({
		defaults: {
			score: 0,
			criterionId: -1,
			itemId: -1,
			revision: 1,
			normalizedScore: 0,
			distanceFromIdeal: -1,
			distanceFromNonIdeal: -1
		},
		
		
		idAttribute: "pseudoId",
		
		initialize: function(){
			_.bindAll(this);
		},
		
		saveIt: function(){
				this.save(this.changed,	{success: this.saveSuccess,	error: this.saveError});
		},
		
		saveSuccess:function() {
			if(this.isNew())
				this.set('pseudoId',this.get('itemId')+'_'+this.get('criterionId'));
			
			dispatcher.trigger('syncOver');
		},
		
		
		saveError: function(model, response){
			//CONFLICT: replace with updated model in case of concurrency conflict and inform the user
			switch(response.status)
			{
				case 409:
					//Trigger an error if the scores don't match.
					var obj = JSON.parse(response.responseText);
					if(Number(obj.score) !== Number(model.get('score')))
					{
						this.trigger('criterionScoreSaveError', model);
						dispatcher.trigger('criterionScoreSaveError');
					}
					else
						dispatcher.trigger('syncOver');
					this.set(obj);
					break;
				
				case 410:
					//GONE: either corresponding criterion (column) or item (row) has been deleted. 
					this.trigger('destroy', this);
					dispatcher.trigger('criterionScoreDeleted');
					break;
				default:
					//trigger to signal 'ajax over'
					dispatcher.trigger('syncOver');
					break;
			}
		},
		
		//The JSON representation to send to the server. Stripping out fields intended for calculations
		toJSON: function() {
			var jsonRepresentation = {
					itemId:this.get('itemId'),
					criterionId: this.get('criterionId'),
					score: this.get('score'),
					revision:this.get('revision')
			};
			
			return jsonRepresentation;
		}
	});

	/*
	 * Collection (i.e. a 'row') of scores for a particular item (win condition or MMF)
	 * i.e. each item is scored against multiple criteria. This collections holds the score for each criteria
	 * for the particular item
	 */
	var CriteriaScores = Backbone.Collection.extend({
		model:CriterionScore
	});

	/*
	 * Model representing the score of each item (win-condition or MMF) row, in essence. 
	 * Also contains the collection of criteriaScores for this item
	 */
	var ItemModel = Backbone.Model.extend({
		defaults: {
			id: -1,
			finalScore: -1
		},

		initialize: function(){
			_.bindAll(this);
			this.criteria = new CriteriaScores;
			this.criteria.on('criterionScoreSaveError',this.propagateError);
			this.criteria.on('destroy',this.removeCriterion);
			this.criteria.on('change:score', this.scoreUpdated);
		},

		scoreUpdated: function(criterionScore){
			this.trigger('scoreChanged', criterionScore);
		},
		
		//propagate error to collection of ItemModels to be picked up by view.
		propagateError: function(criterionScore, response) {
			this.trigger('criterionScoreSaveError', criterionScore);
		},
		
		//Inform consumers of model that a score was removed. No need to explicitly remove score. Done internally by collection
		removeCriterion: function(criterionScore){
			this.trigger('scoreRemoved',criterionScore);
			if(this.criteria.length===0)
				this.trigger('destroy');
		},
		
		//add a criterion score for this item 
		addCriterion: function(criterionScore) {
			this.criteria.add(criterionScore);
			this.trigger("scoreAdded", criterionScore);
		},

		saveIt: function() {
			this.criteria.each(function(criterionScore){
				criterionScore.saveIt();
			});
		},
		
		//update the score for a particular item as set by the user.
		updateCriterion: function(criterion) {
			var criterionToUpdate = this.criteria.find(function(criterionModel){
				return (criterionModel.get('criterionId') === criterion.id && criterionModel.get('score') !== criterion.newScore);
			});

			if(typeof criterionToUpdate==="undefined")
				return;

			criterionToUpdate.set({'score':criterion.newScore});
		},
		
		/* Compute the final priority score as per the below steps:
		 * 1. get distance from ideal S'
		 * 2. get distance from non-deal S"
		 * 3. final score =  S"/(S' + S")
		 */ 
		computeScore: function(){
			
			var sumOfSquaresFromIdeal = 0, sumOfSquaresFromNonIdeal = 0, 
				distanceFromIdeal, distanceFromNonIdeal, score=-1, denominator; 
			
			this.criteria.each(function(criterionScore){
				sumOfSquaresFromIdeal += Math.pow(Number(criterionScore.get('distanceFromIdeal')),2);
				sumOfSquaresFromNonIdeal += Math.pow(Number(criterionScore.get('distanceFromNonIdeal')),2);
			});
			
			//distances are vector distances and hence taking square root of sum of squares of each 'dimension'
			distanceFromIdeal = Math.sqrt(sumOfSquaresFromIdeal);
			distanceFromNonIdeal = Math.sqrt(sumOfSquaresFromNonIdeal);
			
			denominator = distanceFromIdeal + distanceFromNonIdeal;
			score = denominator===0 ? 0 : distanceFromNonIdeal / denominator;

			//using this.set: Triggers a change-event which is then picked up by the ItemView to update the score view
			this.set('finalScore', score);
			
			//this.change();
		}
		

	});

	/*
	 * Set of all item models. Acts as data structure mediating the mathematical computation of the items
	 * and adding a criterion score to a particular model
	 */
	var ItemModels = Backbone.Collection.extend({
		model:ItemModel,

		initialize: function() {
			_.bindAll(this);
			
			//subscribe to event dispatcher to be notified of starting the (re)computation of the TOPSIS Priority Scores
			//dispatcher.on('ComputePriorityScores',this.computeScores,this);
		},

		saveIt: function(){
			this.each(function(item){
				item.saveIt();
			});
		},
		
		//Add the criterion score for the specific itemModel
		addCriterionScore: function(criterionScore){
			var itemModel = this.get(criterionScore.get('itemId'));
			itemModel.addCriterion(criterionScore);
		},
		
		//Ask each itemModel to compute it's priority score.
		computeScores: function(){
			this.each(function(item){
				item.computeScore();
			});
		}
	});
	
	
	/*
	 * A view for each item (win conditions or MMFs) and associated scores.
	 */
	var ItemView = BaseView.extend({
		initialize: function(){
			var self = this;
			_.bindAll(this);
			this.model.on('change:finalScore',this.updatePriorityScore,this);
			this.model.on('scoreAdded scoreChanged', this.showScore);
			this.model.on('scoreRemoved', this.disableScore);
			this.model.on('destroy', function(){
				self.isDestroyed = true;
				dispatcher.trigger('itemDeleted');
			});
			this.model.on('criterionScoreSaveError',this.renderConflictScoreCell)
			dispatcher.on('deleteCriterion',this.unrenderCriterionScoreCell);
			/*
			 * save the final priority scores. Trigger syncOver irrespective of success/error
			 * since if updation fails, implies item was deleted, which would be known on the 
			 * next save cycle. So silently ignore the error but signal sync complete for the
			 * error manager to count-down appropriately.
			 */
			dispatcher.on('savePriorityScores',function(){
				this.model.save(this.model.changed, {
						success: function(){dispatcher.trigger('syncOver');},
						error: function(){dispatcher.trigger('syncOver');}
					});
			}, this);
			dispatcher.on('cleanup',this.unrender);
			this.setUpCriteriaScores();
		},
		
		unrender: function(){
			if(this.isDestroyed)
			{
				var parent = $(this.el).closest('.item'),
					self = this;
				parent.fadeOut("slow",function(){
					self.close();
					parent.remove();
				});
				
			}
		},
		
		
		unrenderCriterionScoreCell: function(id){
			this.$el.find(".criterionScore[data-winbook-criterionid="+id+"]").fadeOut(
					"slow", function(){
						$(this).remove();
					});
				
		},
		
		showScore: function(criterionScore){
			this.$el.find(".criterionScore[data-winbook-criterionid="+criterionScore.get('criterionId')+"] > input").val(criterionScore.get('score'));
		},
		
		disableScore: function(criterionScore){
			this.$el.find(".criterionScore[data-winbook-criterionid="+criterionScore.get('criterionId')+"] > input").prop('disabled',true);
		},
		
		renderConflict: function(criterionScore){
			//Find the corresponding cell in this row to highlight and set background to green.
			this.$el.find('.criterionScore').each(function(){
				if(criterionScore.get('criterionId')===$(this).data('winbook-criterionid'))
				{
					$(this).addClass('error');
					return false;
				}
			});
		},
		
		setUpCriteriaScores: function() {
			var itemId = this.model.get('id'),
				self = this;
		
			this.$('.criterionScore').each(function(){
				var criterionId = $(this).data('winbook-criterionid');
				var criterionScore = new CriterionScore();
				criterionScore.url = URLGenerator.getCriterionScoreURL(itemId, criterionId);
				criterionScore.fetch({
						success: self.fetchSuccess,
						error:self.fetchError
					});
			});
		},
		
		fetchSuccess: function(criterionScoreModel, response){
			//set a pseudo Id to mark this model as "existing"
			criterionScoreModel.set('pseudoId',criterionScoreModel.get('itemId')+'_'+criterionScoreModel.get('criterionId'));
			this.fetchOver(criterionScoreModel);
		},
		
		/*
		 * When no scores have been entered for a particular item i.e. a new item was added but was
		 * not prioritized. It is initialized with the defaults values and the item and criterion ids are set as 
		 * returned by the server. A 404 Not found is thrown by the server since the prioritization resource
		 * pertaining to the particular item wasn't found (i.e. non-existent)
		 */
		fetchError: function(criterionScoreModel, response){
			criterionScoreModel.set(JSON.parse(response.responseText));
			this.fetchOver(criterionScoreModel);
		},
		
		/*
		 * Add Criterion to internal ItemModel and trigger an event with the criterionScoreModel as an argument so that
		 * criteria columns can 'pick up' that cell and add it to their collection. See comment for CriterionScore
		 * for further details.
		 */
		fetchOver: function(criterionScoreModel){
			this.model.addCriterion(criterionScoreModel);
			dispatcher.trigger("criterionScoreFetched", criterionScoreModel);
			dispatcher.trigger('fetchOver');
		},
		

		events: {
			"blur input":"updateCriterionScore"
		},

		/*
		 * Update the criterion score if it's a valid number by delegating the updation to the ItemModel.
		 * Else show error and bring focus back on input element.
		 */
		updateCriterionScore: function(event) {

			isNumber = function(n) {
				return !isNaN(parseFloat(n)) && isFinite(n);
			};

			var value = $(event.target).val();
			var criterion = $(event.target).parent();


			if(isNumber(value))
			{
				criterion.removeClass('error'); //toggle if error existed before
				this.model.updateCriterion({id:criterion.data('winbook-criterionid'), newScore:value});

			}
			else
			{
				criterion.addClass('error'); //toggle if error DID NOT exist before
				$(event.target).focus();
			}
		},
		
		updatePriorityScore: function() {
			var priority, scale;
			priority = Number(this.model.get('finalScore'));
			this.$('.score').text(priority.toFixed(3));
			
			scale = (priority*10 + 10) * 10;
			this.$('.scoreScale').animate({'width':scale},1,"linear");
		},
		
		onClose: function(){
			this.model.off();
		}

	});

	/*
	 * Based on the number of criteria obtained add a columns for each item for inputting the 
	 * criterion scores. I.e. #cells for each item = #criteria.
	 * 
	 * Each 'cell' is associated with the corresponding criterion id and must be created in the same
	 * order as the criteria were created on the original page.
	 * 
	 * ItemViews appends the 'prioritization columns' to each item and instantiates a view for each 
	 * item for the appended prioritization element.
	 */
	var ItemViews = BaseView.extend({
		el: '#itemsHolder > .listOfItems',
		
		something: new ItemModels(),
		
		initialize: function(){
			_.bindAll(this);
			this.listOfItems = new ItemModels();
			dispatcher.on('save',this.listOfItems.saveIt);
			dispatcher.on('ComputePriorityScores',this.listOfItems.computeScores);
			dispatcher.on('sortItems',this.sortItems);
			this.isDesc = true;
			this.render();
		},
		
		sortItems: function(){
			var list = this.$('.item').get(),
				self = this;
			list.sort(function(a, b){
			 var val1 = parseFloat($(a).find('.score').text()),
			     val2 = parseFloat($(b).find('.score').text());
			 return (val1 < val2)? -1 : (val1 > val2) ? 1 : 0;
			});
			
			if(this.isDesc)
				list.reverse();
			this.isDesc = !this.isDesc;
			$.each(list, function(index, item){
				self.$el.append(item);
			});
		},
		
		//overriding close to prevent deletion of the elements :)
		close: function(){
			this.$el.find('.item').removeClass('item');
			this.off();
			this.undelegateEvents();
		},
		
		render: function(){
			var self = this;
			var scoreEl = this.createScoreTemplate({'criteria': this.options.criteriaIDs});
			
			this.$('.item').each(function(){
				$(this).append(scoreEl);
				var itemId = Number($(this).data('winbook-itemid'));
				var itemModel = new ItemModel({id:itemId});
					itemModel.url = URLGenerator.getItemURL(itemId);
				new ItemView({
						'el':$(this).children('.prioritization'),
						'model': itemModel
					});

				/*
				 * Attaches a criterion score to both the rows/columns it belongs to. That is the item and criterion 
				 * to which the score belongs. Item -*-----*- Criterion i.e. many-to-many relation between items and 
				 * criteria. Since the scores are 'shared' by the rows/cols (items/criteria) so a change in value
				 * must be 'detectable' by both the rows (i.e. itemModels) and columns (i.e. CriterionVector 
				 * belonging to a CriterionModel) for performing any independent computation(s)
				 */
				
				self.listOfItems.add(itemModel);
			});
			
			return this;
		},
		
		createScoreTemplate: function(criteria){
			var scoreTemplate = '<div class="prioritization">'
									+'<div class="scoreScale"></div>'
									+'<div class="priority"><span class="score"></span></div>'
									+'<div class="criteriaScores">'
										+'<% _.each(criteria, function(id){ %>'
											+'<div class="criterionScore" data-winbook-criterionid="<%= id %>" >' 
												+'<input type="text" maxlength="3"/>'
											+'</div>'
										+'<% }); %>'
										+'<div class="clear"></div>'
									+'</div>'
								+'</div>';
						
			return _.template(scoreTemplate, criteria);
		}
		
	});

	/*
	 * CriterionVector is the 'vector' of all scores belonging to that particular criterion. 
	 * Vector in this case is a mathematical vector and not just a collection of scores. 
	 */
	var CriterionVector = Backbone.Collection.extend({
		model:CriterionScore,
		
		initialize: function(){
			_.bindAll(this);
		},
		
		/*
		 * If for a criterion 'more is better' 
		 * 	==> Ideal  = Max value for that criterion
		 *  ==> Non-ideal = Min value for that criterion
		 * Else if 'less is better'
		 *  ==> Ideal = Min Value
		 *  ==> Non-ideal = Max Value
		 */
		computeIdealAndNonIdealScores: function(maxBetter) {
			var iterator, idealScore, nonIdealScore;

			//iterator for the max/min functions stating on what values to perform the max/min operation(s)
			iterator = function(criterion) {
				return Number(criterion.get('score'));
			};
			
			if(maxBetter)
			{
				idealScore = Number(this.max(iterator).get('normalizedScore'));
				nonIdealScore = Number(this.min(iterator).get('normalizedScore'));
			}
			else
			{
				idealScore = Number(this.min(iterator).get('normalizedScore'));
				nonIdealScore = Number(this.max(iterator).get('normalizedScore'));
			}
			
			
			
			//For each criterion score compute it's vector distance from ideal and non-ideal and set it.
			this.each(function(criterion){
				var distanceFromIdeal, distanceFromNonIdeal, normalizedScore;
				
				normalizedScore = Number(criterion.get('normalizedScore'));
				distanceFromIdeal = idealScore - normalizedScore;
				distanceFromNonIdeal = nonIdealScore - normalizedScore;
				
				criterion.set({
					'distanceFromIdeal':distanceFromIdeal,
					'distanceFromNonIdeal':distanceFromNonIdeal
				});
			
				
				
			});
			
		},
		
		/*
		 * Normalization: Convert this vector to a 'unit vector': 
		 * 	Divide each component vector i.e. score by it's magnitude
		 * Then compute the ideal and non ideal scores required for TOPSIS
		 */
		normalizeScores: function(options) {
			var sumOfSquares, vectorMagnitude, self=this;
			
			sumOfSquares = this.reduce(function(initVal, criterion){ 
				return initVal + Math.pow(Number(criterion.get('score')),2);
				},0); //0 is the initVal and the third param
			
			vectorMagnitude = Math.sqrt(sumOfSquares);
						
			this.each(function (criterion){
				var normalizedScore = Number(criterion.get('score')) * (options.criterionWeight/vectorMagnitude);
				criterion.set({'normalizedScore':normalizedScore});
				
				
			});
			
			this.computeIdealAndNonIdealScores(options.maxBetter);
		},
		
	});


	/*
	 * Each criterion model contains a reference to the set of vectors belonging to that particular model. 
	 */
	var CriterionModel = Backbone.Model.extend({
		defaults: {
			id: -1,
			weight:50,
			title:"none",
			maxBetter:1,
			revision:1
		},
		
		initialize: function() {
			_.bindAll(this);
			var self = this;
			this.vector =  new CriterionVector;
			this.vector.id = this.get('id');
			
			/*
			 * Propagate the event up to collection. The CriterionModel (this) doesn't have the necessary information
			 * to conduct the normalization all by itself. The collection of CriteriaModels must supply it the
			 * sumOfCriteriaWeights across all criteria for the normalization process. Hence the change in scores
			 * event is propagated as it's received, without any parameters since it's not required. 
			 * Only the intimation of change of score would suffice.
			 */
			this.vector.on('change:score', function(){
				this.trigger('change:score');
			},this)
			
		},
		
		saveIt: function(){
			this.save(this.changed,{success:this.saveSuccess, error:this.saveError})
		},
		
		saveSuccess: function(){
			dispatcher.trigger('syncOver');
		},
		
		saveError: function(model, response) {
			switch (response.status)
			{
				case 409:
					//replace with updated model in case of concurrency conflict and inform the user
					var obj = JSON.parse(response.responseText),
					attr = model.changedAttributes(obj);
					this.set(obj);
	
					//if only revision was changed, consider sync successful else trigger conflict
					if(attr && _.size(attr)==1 && _.has(attr,'revision'))
						dispatcher.trigger('syncOver');
					else
						dispatcher.trigger('criterionSaveError');
					break;
	
				case 410:
					/*
					 * else if the criterion has been deleted then remove the column/vector from the computation
					 * by triggering destroy. Not calling this.destroy(); since it'd send a DELETE request to the server
					 * when the resource has already been deleted.
					 */ 
					this.trigger('destroy',this);
					dispatcher.trigger('criterionDeleted');
					break;
				default:
					//if neither of the above errors, just signal completion.
					dispatcher.trigger('syncOver')
			}
		},
		
		//Add the criterionScore to the criterion 'vector'
		addCriterion: function(criterionScore) {
			this.vector.add(criterionScore);
		},
		
		/*
		 * Normalize the criterion weight (i.e. sliderValue) and set direction of preference (max/min) and 
		 * supply to vector for performing the normalization of the scores w.r.t. it.
		 */
		normalizeScores: function(sumOfCriteriaWeights){
			
			var isMax, normalizedWeight;
			
			isMax = this.get('maxBetter');
			normalizedWeight = sumOfCriteriaWeights===0 ? 0 : Number(this.get('weight'))/sumOfCriteriaWeights;
			
			this.vector.normalizeScores({
				'maxBetter': isMax,
				'criterionWeight': normalizedWeight		
			});
				
		}
	});

	/*
	 * The set of all CriterionModels and their corresponding vectors. 
	 * I.e. all the criterion columns (sliders + vector of scores)
	 * Responsible for caching/updating the sum of weights of all criteria for normalization of weights.
	 * Also acts as a data structure mediating the mathematical normalization of the criterion scores 
	 * and adding a criterion score to a particular model (which internally adds it to it's 'vector').
	 */
	var CriteriaModels = Backbone.Collection.extend({
		model:CriterionModel,
		sumOfCriteriaWeights: 0,
		
		initialize: function() {
			_.bindAll(this);
			this.on('change:score change:maxBetter',this.normalizeScores,this);
			this.on('change:weight',this.recomputeSumOfWeights, this);
			this.on('add',this.getSumOfWeights,this);
		},

		//caching the sum of weights when a new criterionModel is added to the collection
		getSumOfWeights: function(criterionModel){
			this.sumOfCriteriaWeights += Number(criterionModel.get('weight'));
		},
		
		/*
		 * when the slider value changes the sum of weights 
		 * must be recomputed for recalculating/normalizing the TOPSIS Scores
		 */
		recomputeSumOfWeights: function() {
			this.sumOfCriteriaWeights = this.reduce(function(initVal,criterionModel){
				return initVal + Number(criterionModel.get('weight'));
			},0); // 0 = initVal
			
			this.normalizeScores();
		},
		
		//Ask each criterionModel to normalize itself based on the sum of weights of the criteria
		normalizeScores: function() {
			var self=this;			
			this.each(function(criterionModel){
				criterionModel.normalizeScores(self.sumOfCriteriaWeights);
			});
			
			//Trigger informing completion of the normalization activity.
			dispatcher.trigger('ComputePriorityScores');
		},
		
		//Add criterion score to the criterionModel's vector of criteria scores.
		addCriterionScore: function(criterionScore) {
			var criterionModel = this.get(criterionScore.get('criterionId'));
			criterionModel.addCriterion(criterionScore);
		},
		
		saveIt: function() {
			this.each(function(criterion){
				criterion.saveIt();
			});
		}
		
	});

	/*
	 * A view for rendering the sliders, direction, name etc. of the Criterion's View and updating 
	 * the CriterionModel w.r.t. changes in the view i.e. direction, sliderChange.
	 */
	var CriterionView = BaseView.extend({
		initialize: function() {
			_.bindAll(this);
			this.model.on('change',this.render);
			this.model.on('change:revision',this.renderConflict);
			this.model.on('destroy',this.destroyed);
			dispatcher.on('criterionScoreFetched', this.addCriterionScore);
			dispatcher.on('cleanup',this.unrender)
			this.renderSlider(); //call only once on initialization
			this.render();	
		},

		
		addCriterionScore: function(criterionScoreModel){
			
			if(criterionScoreModel.get('criterionId') === this.model.get('id'))
				this.model.addCriterion(criterionScoreModel)

			return;
		},
		
		renderConflict: function(){
			var attr = this.model.changedAttributes(),
				self = this;
			//if only revision was changed, do nothing.
			if(_.size(attr)==1 && _.has(attr,'revision'))
				return;

			//else explicitly show the error in the view componenet.
			_.each(attr, function(value, key){
				switch(key){
				case 'weight':
					self.$el.addClass('error');
					self.$el.children().not('.slider').css("background-color","black");
					break;
				case 'title':
					self.$('.criterionName').addClass('error');
					break;
				case 'maxBetter':
					self.$('.directionOfPreference').addClass('error');
				}
			});
			console.log("====================> Apply changes here...")

		},
		
		
		renderSlider: function() {
			var self = this;
			//Caching the slider instance for rendering updates after server sync
			this.$('.slider').slider({
				orientation:'vertical',
				value:self.model.get('weight'),
				range:'min',
				min:0,
				max:100
			});
		},
		
		render: function() {
			var self = this;
			this.$('.slider').slider("value",self.model.get('weight'));
			this.sliderDirNode = this.sliderDirNode || this.$('.arrow');
			this.sliderDirNode.toggleClass('ui-icon-arrowthick-1-n', this.model.get('maxBetter'));
			this.sliderDirNode.toggleClass('ui-icon-arrowthick-1-s', !this.model.get('maxBetter'));
		},

		destroyed: function(){
			this.isDestroyed = true;
			this.model.set('weight',0);
			this.$('.slider').slider('disable');
		},
		
		unrender: function() {
			if(this.isDestroyed)
			{
				dispatcher.trigger('deleteCriterion',this.model.get('id'));
				var self = this;
				$(this.el).fadeOut("slow", function(){
					self.close();
				});
				
			}
	
		},
		
		events: {
			"click .directionOfPreference":"flipDirection",
			"slide .slider":"sliderChanged",
			"slidechange .slider":"sliderStop"
		},

		flipDirection: function(event) {
			
			var flip = !(this.model.get('maxBetter'));
			this.model.set({'maxBetter':flip});
		},

		sliderChanged: function(event, ui) {
			
			this.model.set({'weight':ui.value});
		},

		sliderStop: function(event, ui) {
			
			//this.model.set({'weight':ui.value});
		},
		
		onClose: function(){
			this.model.off();
		}

	});

	
	/*
	 * Initialize the views for each criterion. 
	 */
	var CriteriaView = BaseView.extend({
		el: '#criteriaContainer',
		
		initialize: function(){
			_.bindAll(this);
			this.criteriaIDs = [];
			this.listOfCriteria = new CriteriaModels();
			dispatcher.on('save',this.listOfCriteria.saveIt);
			//subscribe to event dispatcher to be notified of starting the (re)normalization of TOPSIS Scores
			dispatcher.on('StartTopsis',this.listOfCriteria.normalizeScores);
			this.render();
		},
		
		render: function(){
			var self = this;
			this.$('.criterion').each(function(){
				var criterionModel = new CriterionModel({
					'id':Number($(this).attr('id')), //MUST typecast to ensure numeric value since could be set as string when reading from page
					'weight':$('.slider',this).data('winbook-slidervalue'),
					'title':$('.criterionName', this).text(),
					'maxBetter':$('.directionOfPreference',this).data('winbook-direction-up'),
					'revision':Number($(this).data('winbook-version'))
				});
				
				criterionModel.urlRoot = URLGenerator.getCriterionURL();
				
				self.criteriaIDs.push(criterionModel.get('id'));
				
				//Add each criterion model to the 'column' of CriteriaModels
				self.listOfCriteria.add(criterionModel);
				new CriterionView({el:$(this), model:criterionModel});
			});
			return this;
		},
		
		getCriteriaIDs: function() {
			return this.criteriaIDs;
		}
	});
	
	
	/*
	 * This view must support keyboard navigation of arrow keys, enter key and tab
	 * <- Go left one cell
	 * -> Go right one cell. If end of row go to left most cell of next row
	 * up-arrow: go up one cell. If end of column, do nothing.
	 * down-arrow: go down one cell. If end of column, do nothing.
	 * Tab: Go right one cell. If end of row go to left most cell of next row (same as right arrow)
	 * Enter: Go down one cell. Same as down-arrow.
	 * 
	 */
	var App = Topsis.AppView = BaseView.extend({
		el:'#prioritizationLightbox',

		initialize: function() {
			_.bindAll(this);
			URLGenerator.wallName = this.options.wallName;
			URLGenerator.itemType = this.options.itemType;
			$(this.options.items).appendTo('#itemsHolder');
			dispatcher.on('hasDeletes', function(){
				this.$('#clearButton').button('option','disabled',false);
			}, this);
			this.counter = new counter({el:this.$('#messageBox'), model:new SyncCounter()});
			this.setUpViews();
			this.$('button').button();
			this.$('#clearButton').button('option','disabled',true);
		},
		
		events: {
			"click #saveButton":"saveIt",
			"click #closeButton":"closeApp",
			"click #clearButton":"removeDeletedElements",
			"click #sortButton":"sortItems"
		},
		
		saveIt: function() {
			
			this.$el.find('.error').removeClass('error');
			dispatcher.trigger('save');
		},
		
		closeApp: function(){
			var items = this.$el.find('.listOfItems');
			this.options.close.call(this.options.context, items);
			dispatcher.trigger('cleanup');
			dispatcher.trigger('close');
			dispatcher.off();
		},
		
		removeDeletedElements: function(e){
			dispatcher.trigger('cleanup');
			$(e.target).button('option','disabled',true);
		},
		
		sortItems: function(){
			dispatcher.trigger('sortItems');
		},
		
		setUpViews: function() {
			var criteriaView = new CriteriaView();
			var itemViews = new ItemViews({criteriaIDs:criteriaView.getCriteriaIDs()});
		}


	});///:~ App
	
	var counter = BaseView.extend({
		
		initialize: function(){
			_.bindAll(this);
			this.criteriaCount = $('.criterion').length;
			this.itemCount = $('.item').length;
			this.initCounter();
			dispatcher.on('criterionDeleted', this.logCriterionDeletion);
			dispatcher.on('criterionScoreDeleted',this.logScoreDeletion);
			dispatcher.on('criterionScoreSaveError criterionSaveError', this.logSaveError);
			dispatcher.on('itemDeleted', this.logItemDeletion);
			dispatcher.on('syncOver',this.logSyncOver);
			dispatcher.on('fetchOver', this.logFetchOver);
			dispatcher.on('save',this.startSave);
			this.model.on('syncComplete',this.syncComplete);
			this.model.on('saveComplete',this.saveComplete);
			this.model.on('change:totalSyncedCount',this.updateProgress);
			this.progress = this.$('#progressBar');
			this.progress.progressbar({value:0});
			this.message = this.$('#message');
			this.message.text('Loading...');
			this.$el.dialog({
				autoOpen:false,
				modal:true,
				minWidth:400,
				closeOnEscape: false
			});
			this.$el.dialog("open");
		},
		
		initCounter: function(){
			this.resetScoreCount();
			this.model.set(this.model.defaults);
			this.model.set('totalCount',this.scoreCount+this.criteriaCount+this.itemCount);
			this.model.set('itemCount',this.itemCount);
		},
		
		resetScoreCount: function(){
			this.scoreCount = this.criteriaCount * this.itemCount;
			this.totalScoreCount = this.scoreCount;
		},
		
		startSave: function(){
			this.message.text('Saving...');
			this.progress.progressbar("option", "value", 0);
			this.$el.dialog("open");
		},
		
		saveComplete: function(){
			this.message.text('Save Success');
			var self = this;
			this.$el.fadeTo(1000, 0.5, function(){
				$(this).fadeTo(1,1);
				self.$el.dialog('close');
			})
		},
		
		syncComplete: function(){
			var hasDeletes = false, 
				hasErrors = false,
				self = this;
			
			this.message.empty();
			this.progress.show();
			
			if(this.model.get('errorCount') > 0 )
			{
				this.message.append('<span><strong>+</strong> One or more items conflicted while saving (marked in red) and have been updated to the new value</span><br/>');
				hasErrors = true;
			}
			if(this.model.get('deleteCount') > 0)
			{
				this.message.append('<span><strong>+</strong> One or more items/criteria were deleted by a concurrent user and have been grayed out.</span> <br/>');
				hasDeletes = true;
			}
			
			if(!hasDeletes && !hasErrors)
			{
				this.message.text('Saving Priority Scores...');
				dispatcher.trigger('savePriorityScores')
			}
			else
			{
				this.$el.dialog("option","buttons",{"OK":function(){$(this).dialog("close");}});
				this.progress.hide();
			}
				
			this.$el.bind('dialogclose',function(event, ui){
				self.initCounter();
				if(hasDeletes)
					dispatcher.trigger("hasDeletes");
				
				self.$el.unbind('dialogclose');
			});
			
		},
		
		updateProgress: function(){
			var value = (this.model.get('totalSyncedCount')/this.model.get('totalCount')) * 100;
			this.progress.progressbar('option','value',value);
		},
		
		logFetchOver: function(){
			this.scoreCount--;
			var prog = 100 * (1-(this.scoreCount/this.totalScoreCount));
			this.progress.progressbar("option","value",prog);
			
			if(this.scoreCount <= 0)
			{
				$(this.el).dialog('close');
				dispatcher.trigger("StartTopsis");
				this.resetScoreCount();
			}
		},
		
		logSyncOver: function(){
			this.model.set('syncCount',this.model.get('syncCount')+1);
		},
		
		logItemDeletion: function(){
			this.model.set('deleteCount',this.model.get('deleteCount')+1);
			this.itemCount--;
		},
		
		logSaveError: function(){
			this.model.set('errorCount',this.model.get('errorCount')+1);
		},
		
		logScoreDeletion: function(){
			this.model.set('deleteCount',this.model.get('deleteCount')+1);
		},
		
		logCriterionDeletion: function(){
			this.model.set('deleteCount',this.model.get('deleteCount')+1);
			this.criteriaCount--;
		},
		
		onClose: function(){
			this.model.off();
			dispatcher.off(null, null, this);
		}
		
	});
	
	var SyncCounter = Backbone.Model.extend({
		defaults: {
			deleteCount:0,
			syncCount:0,
			errorCount:0,
			totalSyncedCount: 0
		},
		
		initialize: function(){
			_.bindAll(this);
			this.on('change:deleteCount change:syncCount change:errorCount', this.updateSyncedCount);
			this.on('change:totalSyncedCount',this.checkCompletion);
		},
		
		updateSyncedCount: function(){
			var sum = this.attributes.deleteCount + this.attributes.syncCount + this.attributes.errorCount;
			this.set('totalSyncedCount',sum);
		},
		
		checkCompletion: function(){
			if(this.attributes.totalSyncedCount == (this.attributes.totalCount - this.attributes.itemCount))
				this.trigger('syncComplete');
			else if(this.attributes.totalSyncedCount == this.attributes.totalCount)
				this.trigger('saveComplete');
		},
		
	});

	/*
	 * A Global URLGenerator that returns the appropriate URL endpoints for the models/collections to be used.
	 * Initialized by the application's AppView where wallName and itemType are passed by the
	 * component instantiating the prioritization.
	 */
	var URLGenerator = {
			wallName: null,
			itemType: null,
			
			getCriterionURL: function(){
				return this.wallName+'/Prioritization/Criteria';
			},
			
			getCriterionScoreURL: function(itemId, criterionId){
				return this.wallName+'/Items/'+this.itemType+'/'+itemId+'/GoalScores/'+criterionId;
			},
			
			getItemURL: function(itemId){
				return this.wallName+'/Items/'+this.itemType+'/'+itemId+'/Priority';
			}
	};
	
	
	
	
}).call(this);
