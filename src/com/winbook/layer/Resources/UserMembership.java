/**
 * 
 */
package com.winbook.layer.Resources;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.UserMembershipDAO;

/**
 * @author Nupul
 *
 */
public class UserMembership extends ServerResource {

	private UserMembershipDAO membershipDAO;
	private Map<String, String> membershipRestrictionMap;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		membershipDAO = WinbookConfig.getSpringWebApplicationContext().getBean("UserMembershipDAO", UserMembershipDAO.class);
		membershipRestrictionMap = new HashMap<String, String>();
		membershipRestrictionMap.put("students.fhnw.ch", "FHNW");
		membershipRestrictionMap.put("fhnw.ch", "FHNW");
		membershipRestrictionMap.put("uzh.ch","UZH");
		membershipRestrictionMap.put("access.uzh.ch","UZH");
		membershipRestrictionMap.put("usc.edu","USC"); 
	}

	@Post  
	public void addMembership(Representation entity)
	{
		Form projectMembership = new Form(entity);
		String userEmail = projectMembership.getFirstValue("email");
		String projectName = projectMembership.getFirstValue("project");
		String[] domain = userEmail.split("@");
		
		if(!grantMembership(domain[1], projectName))
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "You can only join the projects allowed by the administrator. Please contact admin if incorrect");
			return;
		}
		
		if(membershipDAO.addMembership(userEmail, projectName))
			setStatus(Status.SUCCESS_CREATED);
		else
			setStatus(Status.SERVER_ERROR_INTERNAL,"Unable to join project. Please inform administrator of the same.");
	}
	
	private boolean grantMembership(String domain, String projectName)
	{
		
		return true; //grant everybody membership for everything for now.
		
		
//		if(membershipRestrictionMap.containsKey(domain))
//		{
//			if(projectName.contains(membershipRestrictionMap.get(domain)) || projectName.equalsIgnoreCase("myproject"))
//				return true;
//
//			return false;
//		}
//		else //deny membership to everybody else except if joining the test project - need not be so strict and should be removed for new clients non-academic members 
//		{
//			if(projectName.equalsIgnoreCase("myproject"))
//				return true;
//			
//			return false;
//		}
//			
	}
	
}
