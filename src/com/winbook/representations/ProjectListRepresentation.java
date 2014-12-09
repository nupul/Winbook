package com.winbook.representations;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;

import com.winbook.domainobjects.Project;
import com.winbook.domainobjects.User;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.ProjectDAO;
import com.winbook.layer.dataaccess.UserDAO;

public class ProjectListRepresentation {

	private ProjectDAO projectDAO;
	private UserDAO userDAO;
	
	public ProjectListRepresentation()
	{
		projectDAO = WinbookConfig.getSpringWebApplicationContext().getBean(ProjectDAO.class);
		userDAO = WinbookConfig.getSpringWebApplicationContext().getBean(UserDAO.class);
	}
	
	public TemplateRepresentation getListOfProjectsForUserRepresentation(String username)
	{
		Map<String, Object> data = new HashMap<String, Object>();
		
		Request req = Request.getCurrent();
		data.put("baseRef", req.getRootRef());

		User member = userDAO.getUser(username);
		member = projectDAO.populateProjectMembershipDetailsForUser(member);
		
		data.put("user", member);
		
		return new TemplateRepresentation("ListOfProjects.ftl", WinbookConfig.getFreemarkerConfiguration(),data,MediaType.TEXT_HTML);
	}
}
