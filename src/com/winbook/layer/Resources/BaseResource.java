package com.winbook.layer.Resources;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.Role;

import com.winbook.layer.accessControl.WinbookEnroler;

public class BaseResource extends ServerResource {

	private String authorEmail, projectName, wallName;
	private Role userRole;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		authorEmail = getRequest().getClientInfo().getUser().getIdentifier();
		
		if(getRequestAttributes().containsKey("wallName"))
			wallName = getRequestAttributes().get("wallName").toString();

		if(getRequestAttributes().containsKey("project"))
			projectName = getRequestAttributes().get("project").toString();
		
		if(projectName != null)
			userRole = WinbookEnroler.getRole(authorEmail, projectName);
		
		/*
		 * Allow GETs for all roles including non-members. PUT, POST, DELETE only allowed for project members. 
		 * Basic level of security to prevent members of one project from making changes in another for which 
		 * they aren't members. 
		 */
		if(userRole!=null && userRole.equals(WinbookEnroler.NON_MEMBER))
		{
			if(getRequest().getMethod().equals(Method.GET))
				return;
			else
				setStatus(Status.CLIENT_ERROR_FORBIDDEN,"This operation is only allowed by project members");
		}
	}

	protected String getAuthorEmail() {
		return authorEmail;
	}

	protected String getProjectName() {
		return projectName;
	}

	protected String getWallName() {
		return wallName;
	}

	protected Role getUserRole() {
		return userRole;
	}
	
	
	
	
}
