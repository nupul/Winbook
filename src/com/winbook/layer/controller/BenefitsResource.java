package com.winbook.layer.controller;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.winbook.layer.Resources.BaseResource;
import com.winbook.layer.dataaccess.GoalDAO;

public class BenefitsResource extends BaseResource {

	private GoalDAO dao;
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		dao = WinbookConfig.getSpringWebApplicationContext().getBean("GoalDAO", GoalDAO.class);
	}

	@Get("html")
	public Representation getBenefits()
	{
		Map<String, Object> data = new HashMap<String, Object>(1);
		data.put("baseRef", getRequest().getRootRef());
		data.put("goals", dao.getGoalsForProject(getProjectName(), getWallName(), "business"));
		
		return new TemplateRepresentation("Benefits.ftl",WinbookConfig.getFreemarkerConfiguration(),data,MediaType.TEXT_HTML);
	}
	
	
	
}
