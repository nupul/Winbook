		
<div id="prioritizationLightbox">
	<button id="saveButton">Save</button>
	<button id="closeButton">Close</button>
	<button id="sortButton">Sort</button>
	<button id="clearButton">Clean Up</button>
	<div id="prioritizationContainer">
		<div id="prioritizationInfo">
			Prioritize using TOPSIS...
			<div id="directionInfo">
				Select direction of preference of (of importance) for criterion :-->
			</div>
		</div>
		<div id="criteriaContainer">
		<#if criteria?has_content>
		<#list criteria as criterion>
			<div id=${criterion.id} class="criterion" data-winbook-version=${criterion.revision}>
				<span class="criterionName">${criterion.title}</span>
				<div class="slider" data-winbook-slidervalue=${criterion.weight}></div>
				<div class="directionOfPreference" data-winbook-direction-up=${criterion.maxBetter?string}>
					<span class="arrow ui-corner-all ui-icon ui-state-default" ></span>
				</div>	
			</div>
		</#list>
		</#if>
		</div>
		
		<div id="itemsHolder"></div>
		<div id="messageBox" title="Please Wait">
			<div id="message"></div>
			<div id="progressBar" style="height:20px;"></div>
		</div>
	</div>
</div>