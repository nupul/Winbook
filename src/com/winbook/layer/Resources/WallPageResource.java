package com.winbook.layer.Resources;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.Role;

import com.winbook.domainobjects.User;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;
import com.winbook.layer.accessControl.WinbookEnroler;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.UserDAO;

public class WallPageResource extends BaseResource {
	
	private WallPage wallPage;
	private String authorEmail, wallName, projectName;
	private Role userRole;
	private User accessingUser;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		authorEmail = getAuthorEmail();
		wallName = getWallName();
		projectName = getProjectName();
		userRole = WinbookEnroler.getRole(authorEmail, projectName);
		accessingUser = WinbookEnroler.getUserDetails(authorEmail);
	}



	/*
	 * Create a freemarker template representation of the wall page to be renedered.
	 */
	@Get("html")
	public Representation toHTML()
	{
		wallPage = new WallPage(projectName, wallName);
		
		Map<String, Object> wall = new HashMap<String, Object>();
		
		try {
			wall.put("baseRef", getRequest().getRootRef());
			wall.put("project", wallPage.getProjectName());
			wall.put("wall", wallPage.getWallName());
			wall.put("winconditions", wallPage.getWinConditions());
			wall.put("title", wallPage.getPageTitle());
			wall.put("categories", wallPage.getCategories());
			wall.put("role", userRole.getName());
			wall.put("user", accessingUser);
		} catch (NoSuchProjectExistsException e) {

			wall.put("title", "No Such Project Exists");
			setStatus(Status.CLIENT_ERROR_NOT_FOUND, "<b>No such Project exists: "+wallPage.getProjectName()+"</b>");
			return null;
			

		} catch (NoWallForProjectExistsException e) {
			
			wall.put("title", "No Such Wall Exists");
			setStatus(Status.CLIENT_ERROR_NOT_FOUND, "<b>No wall by the name: "+wallPage.getWallName()+" exists for Project: "+wallPage.getProjectName()+"</b>");
			return null;
			
		}
		
		setStatus(Status.SUCCESS_OK);
		return new TemplateRepresentation("WinWinWallPage.ftl",WinbookConfig.getFreemarkerConfiguration(),wall,MediaType.TEXT_HTML);
		
	}

}
