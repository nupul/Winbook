<!-- <div id="main"> -->
<div id="postArea">
	<span><strong>Vision:</strong></span><br/>
	<div id="visionHolder"></div>
	<textarea id="visionText"></textarea>	
	<span id="visionLength"></span>
	<button id="postVision" disabled="disabled">Post</button>	 
</div>
<div class="clear">
</div>

<div id="menuBar">
	<ul>
		<li class="menuItem" data-winbook-action="prioritize">
			<a href="#Benefits/prioritize" style="text-decoration:none;">Prioritize</a>
		</li>
	</ul>
</div>

<div class="clear">
</div>

<div id="goalInput">
	<input type="text" id="goalName"/>
	<button>Add Goal</button>
	<span></span>
</div>

<ul class="listOfPosts">
<#if goals?has_content>
<#list goals as goal>
	<li class="post goal" data-winbook-goalid=${goal.id} data-winbook-weight=${goal.weight} data-winbook-prioritization=${goal.forPrioritization?string} data-winbook-version=${goal.revision?c}>
		<div class="postContainer">
			<div class="postDetailsContainer" style="width: 90%;padding: 5px;margin-left: 20px;">
				<div class="goalHoverMenu">
					<a class="prioritize">
						<img class="hoverButton prioritizeIcon" src="${baseRef}/images/forPrioritization.png">
					</a> 
					<a class="delete">
						<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png">
					</a>
				</div>
				<ul class="postDetails">
					<li>
						<span class="title">${goal.title}</span>
						<span class="weightCounter"></span>
					</li>
					<li class="slider">
						<div class="successSlider"></div>
					</li>
					<li class="details">
					</li>
				</ul>
			</div>
		</div>
	</li>
</#list>

<#else>
	<li id="info-li" class="post">
		Add Goals for the project using the above input box
	</li>
</#if>
	
</ul> <!-- End listOfPosts-->
<!-- </div> End #main div -->
