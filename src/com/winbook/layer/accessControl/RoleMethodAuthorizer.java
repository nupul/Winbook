package com.winbook.layer.accessControl;

import java.util.Map;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.security.Authorizer;
import org.restlet.security.Role;

public class RoleMethodAuthorizer extends Authorizer {

	
	@Override
	protected boolean authorize(Request req, Response resp) {
		//If it's a get request then no need for further authorization. 
		if(req.getMethod().equals(Method.GET))
			return true;
		else
		{
			Map<String, Object> attribs = Request.getCurrent().getAttributes();
			//Map<String, Object> attribs2 = getContext().getAttributes();
			Map<String, Object> attribs3 = req.getAttributes();
			
			String authorEmail = req.getClientInfo().getUser().getIdentifier();
			String projectName = req.getAttributes().get("project").toString();
			
			Role userRole = WinbookEnroler.getRole(authorEmail, projectName);
			
			//forbid updates to resources if done by non-members of project
			if(userRole.equals(WinbookEnroler.NON_MEMBER))
				return false;
			//for everybody else, return true
			return true;
		}
		
		
	}

}
