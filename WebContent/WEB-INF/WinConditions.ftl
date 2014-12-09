<!--	<div id="main"> 	-->
<div id="postArea">
		<textarea id="wincondition">Enter your Win Condition/Requirement/Expectation here...</textarea>
		<span id="winconditionLength"></span>
		<button id="postwincondition" disabled="true">Post</button>
		 
</div>
<div class="clear">
</div>

<div id="menuBar">
	<ul>
		<li class="masterCheckbox"><input type="checkbox" id="checkAll"/></li>
		<li class="menuItem" data-winbook-action="categorize">Categorize</li>
		<li class="menuItem" data-winbook-action="toggleVisibility">Show/Hide <span>&#9660;</span></li>
		<li class="menuItem" data-winbook-action="showEquilibrium">Equilibrium</li>
		<li class="menuItem" data-winbook-action="exportTSV">Export TSV</li>
		<li class="menuItem" data-winbook-action="prioritize">
		<a href="#WinConditions/prioritize" style="text-decoration:none;">Prioritize</a></li>
	</ul>
</div>

<div class="clear">
</div>


<ul class="listOfPosts">
	<#if winconditions?has_content>
	<#list winconditions as wc>

	<!-- Start of win condition Post-->
	<li class="post wincondition" data-winbook-status="${wc.status}" data-winbook-wcid="${wc.id?c}" data-winbook-itemid=${wc.id?c}>
	
		<div class="checkbox">
			<input type="checkbox" class="wcCheckbox">
		</div>
		<div class="postContainer">
			<div class="avatarColumn">
				<a href="#">
				<#if wc.author.avatarURL == "DEFAULT_AVATAR">
					<img src="${baseRef}/images/defaultavatar.jpg"/>
				<#else>
					<img src="${wc.author.avatarURL}">
				</#if>
				<!-- Need to add author's avatar's url-->
				</a>
				<div class="authorName">
					<a href="#">${wc.author.displayName}</a>
				</div>
			</div>
			<div class="postDetailsContainer">
				<div class="hoverMenu">
					<a class="edit" data-winbook-edit="wincondition">
					<img class="hoverButton editIcon" src="${baseRef}/images/editpencil.png"/>
					</a>
					<a class="delete" data-winbook-delete="wincondition">
					<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
					</a>
				</div>
				<ul class="postDetails">
					<li>
						<ul class="categorizationDetails">
							<li>
								<strong>Win Condition</strong> (<span class="wcid">WC_${wc.id?c}</span>):
							</li>
							<#if wc.categories??>
							<#list wc.categories as category>
							<li class="categoryLabel" data-winbook-categoryid="${category.id?c}" data-winbook-labelcolor="${category.hexColorCode}" title="${category.categoryName}">
								${category.categoryName}
							</li>											
							</#list>
							</#if>
						</ul>
					</li>
					<div class="clear">
					</div>
					<li class="postData">
						${wc.winCondition}
					</li>
					<li>
						<ul class="actionsNavBar">
							<li>
								<a class="actionNavBarLink" data-winbook-action="Agree">Agree</a>
								<span class="dotSeparator">.</span>
							</li>
							<#if role == "shaper">
								<li class="potentialAgreement" <#if wc.status="maybe">style="display:none;"</#if>>
									<a class="actionNavBarLink" data-winbook-action="MayAgree">Potentially Agreeable</a>
									<span class="dotSeparator">.</span>
								</li>
							</#if>
							<li>
								<a class="actionNavBarLink" data-winbook-action="Comment">Comment</a>
								<span class="dotSeparator">.</span>
							</li>
							<li>
								<a class="actionNavBarLink" data-winbook-action="Issue">Raise Issue/Concern/Risk</a>
							</li>
						</ul>
					</li>
					<li class="Agreements">
					<#if wc.agreements??>
						<ul class="stakeholdersAgreeing">
							<#list wc.agreements as agreement>
								<li data-winbook-agreementid="${agreement.id?c}">${agreement.author.displayName}<#if (agreement_has_next)>,&nbsp;</#if></li>
							</#list>
							<#if (wc.agreements?size) != 0>
								<li>&nbsp;<em>agree to this</em></li>
							</#if>
							<#if wc.status=="maybe">
								<li class="mark"><em>(Marked for potential agreement by shaper)</em></li>
							</#if>
						</ul>
						
					<#elseif wc.status=="maybe">
						<ul class="stakeholdersAgreeing">
							<li class="mark"><em>(Marked for potential agreement by shaper)</em></li>
						</ul>
						
					</#if>
					</li>
				</ul>
			</div>
		</div>
		<div class="clear">
		</div>
		<ul class="_listOfIssues">
			<#if wc.issues??>
			<#list wc.issues as issue>
				<!-- Start of Issue Post -->
				<li class="post issue" data-winbook-status="${issue.status}" data-winbook-issueid="${issue.id?c}">
					<div class="postContainer">

						<div class="avatarColumn">

							<a href="#">
							<#if issue.author.avatarURL == "DEFAULT_AVATAR">
								<img src="${baseRef}/images/defaultavatar.jpg"/>
							<#else>
								<img src="${issue.author.avatarURL}">
							</#if>
							</a>
							<div class="authorName">
								<a href="#">${issue.author.displayName}</a>
							</div>
						</div>
						<div class="postDetailsContainer">
						<div class="hoverMenu">
							<a class="edit" data-winbook-edit="issue">
							<img class="hoverButton editIcon" src="${baseRef}/images/editpencil.png"/>
							</a>
							<a class="delete" data-winbook-delete="issue">
							<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
							</a>
						</div>
							<ul class="postDetails">

								<li>
									<strong>Issue</strong> (<span class="issueid">Iss_${issue.id?c}</span>):
								</li>
								<li class="postData">
									${issue.issue}
								</li>
								<li>
									<ul class="actionsNavBar">
										<li>
										<#if role == "shaper">
											<#if issue.status=="open">
												<a class="actionNavBarLink" data-winbook-action="CloseIssue">Close Issue</a>
											<#elseif issue.status=="closed">
												<a class="actionNavBarLink" data-winbook-action="OpenIssue">Open Issue</a>
											</#if>	
										</#if>
										<span class="dotSeparator">.</span>
										</li>
										<li>
											<a class="actionNavBarLink" data-winbook-action="Comment">Comment</a>
											<span class="dotSeparator">.</span>
										</li>
										<li>
											<a class="actionNavBarLink <#if issue.status=="closed">noOptionsAllowed</#if>" data-winbook-action="Option">Suggest Option/Alternative(s)</a>
										</li>
									</ul>
								</li>
							</ul>
						</div>
					</div>
					<div class="clear">
					</div>
					
					<ul class="_listOfOptions">
						<#if issue.options??>
						<#list issue.options as option>
						<!-- Start of Option Post -->
						<li class="post option" data-winbook-status="${option.status}" data-winbook-optionid="${option.id?c}">
							<div class="postContainer">

								<div class="avatarColumn">
									<a href="#">
									<#if option.author.avatarURL == "DEFAULT_AVATAR">
										<img src="${baseRef}/images/defaultavatar.jpg"/>
									<#else>
										<img src="${option.author.avatarURL}">
									</#if>
									</a>
									<div class="authorName">
										<a href="#">${option.author.displayName}</a>
									</div>
								</div>
								<div class="postDetailsContainer">
									<div class="hoverMenu">
										<a class="edit" data-winbook-edit="option">
										<img class="hoverButton editIcon" src="${baseRef}/images/editpencil.png"/>
										</a>
										<a class="delete" data-winbook-delete="option">
										<img class="hoverButton deleteIcon" src="${baseRef}/images/deleteredicon.png"/>
										</a>
									</div>
									<ul class="postDetails">
										<li>
											<strong>Option</strong> (<span class="optionid">Opt_${option.id?c}</span>):
										</li>
										<li class="postData">
											${option.option}
										</li>
										<li>
											<ul class="actionsNavBar">
												<li>
													<a class="actionNavBarLink" data-winbook-action="Agree">Agree</a>
													<span class="dotSeparator">.</span>
												</li>
												<li>
													<a class="actionNavBarLink" data-winbook-action="Comment">Comment</a>
												</li>
											</ul>
										</li>
										<li class="Agreements">
										<#if option.agreements??>
											<ul class="stakeholdersAgreeing">
											<#list option.agreements as agreement>
												<li data-winbook-agreementid="${agreement.id?c}">${agreement.author.displayName}<#if (agreement_has_next)>,&nbsp;</#if></li>
											</#list>
											<#if (option.agreements?size) != 0>
												<li>&nbsp;<em>agree to this</em></li>
											</#if>
											</ul>
										</#if>
										</li>
									</ul>
								</div>
							</div>
							<div class="clear">
							</div>
						</li> <!--End of Option Post-->
						</#list>
						</#if>
					</ul> <!-- End list of options for issue-->
					
				</li> <!-- End of Issue Post -->

			</#list>	
			</#if>	
		</ul> <!--end list of issues for win condition-->
		
	</li> <!--end Win condition post-->
	</#list>
	<#else> <!-- Inform user about zero win conditions -->
		<li class="post" style="width:90%; margin-left:auto; margin-right:auto; color:gray;" id="infomessage">
			There are no win conditions for this project. Use the above text box to add a win condition(s)
		</li>
	</#if>
</ul> <!-- End listOfPosts-->
<!-- </div>  End #main div -->
