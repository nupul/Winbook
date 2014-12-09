package com.winbook.layer.Resources;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.security.Role;

import com.winbook.DTO.ProjectVisionDTO;
import com.winbook.domainobjects.User;
import com.winbook.layer.accessControl.WinbookEnroler;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.ProjectDAO;
import com.winbook.layer.dataaccess.WallDAO;

public class ProjectResource extends BaseResource {

	private Role userRole;
	private User accessingUser;
	private ProjectDAO projectDAO;
	private WallDAO wallDAO;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		userRole = WinbookEnroler.getRole(getAuthorEmail(), getProjectName());
		accessingUser = WinbookEnroler.getUserDetails(getAuthorEmail());
		projectDAO = WinbookConfig.getSpringWebApplicationContext().getBean("ProjectDAO", ProjectDAO.class);
		wallDAO = WinbookConfig.getSpringWebApplicationContext().getBean("WallDAO", WallDAO.class);
	}

	@Get("html")
	public Representation getProjectPage()
	{
		String errorMsg;
		if(projectDAO.projectExists(getProjectName()))
		{
			if(wallDAO.wallExists(getProjectName(), getWallName()))
			{
				WallPage page = new WallPage(getProjectName(), getWallName());
				String vision = projectDAO.getVision(getProjectName()).getVision();
				Map<String, Object> wall = new HashMap<String, Object>();
				
				wall.put("baseRef", getRequest().getRootRef());
				wall.put("project", page.getProjectName());
				wall.put("wall", page.getWallName());
				wall.put("title", page.getPageTitle());
				wall.put("categories", page.getCategories());
				wall.put("role", userRole.getName());
				wall.put("user", accessingUser);
				wall.put("vision", vision);
				return new TemplateRepresentation("Project.ftl",WinbookConfig.getFreemarkerConfiguration(),wall,MediaType.TEXT_HTML);
			}
			
			else
			{
				errorMsg = "<strong>No Wall by the name: '"+getWallName()+"' exists for project: "+getProjectName()+"</strong>";
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST, errorMsg);
				return null;
			}
		}
		else
		{
			errorMsg = "<strong>No project by the name: '"+getProjectName()+"' exists in the system</strong>";
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, errorMsg);
			return null;
		}
		
		
	}
	
	@Put("json")
	public ProjectVisionDTO updateProjectVision(ProjectVisionDTO dto)
	{
		if(projectDAO.updateVision(getProjectName(),dto))
		{
			setStatus(Status.SUCCESS_ACCEPTED);
			return dto;
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_CONFLICT);
			return projectDAO.getVision(getProjectName());
		}
	}
	
}///:~
