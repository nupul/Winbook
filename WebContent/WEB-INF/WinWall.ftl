<html>
<head>
	<title>${title}</title>
</head>
<body>
<h1>List of win conditions for ${project}. Wall name:${wall}</h1>
<#list winconditions as wc>
<p>
	<ul>
		<li>wc${wc.id}</li>
		<li>Posted by ${wc.author.firstName} ${wc.author.lastName}</li>
		<li>${wc.winCondition}</li>
		<li>Posted on: ${wc.timestamp}</li>
		<li>Status: ${wc.status}</li>
		<#if wc.issues??>
		  <#list wc.issues as i>
		  <span style="{position:relative; left:20px;}">
		  	++> ${i.issue}<br/>
		  	++> ${i.author.emailId}<br/>
		  </span>	
		  	<#if i.options??>
		  	
		  	<#list i.options as o>
		  	<span style="{position:relative; left:40px;}">
		  		+++++> ${o.option}<br/>
		  		+++++> ${o.author.emailId}<br/>
		  	</span>	
		  	</#list>

			</#if>
		  	
		  </#list>
		</#if>
	</ul>
</p>
</#list>

<p data-internal></p>
</body>
</html>