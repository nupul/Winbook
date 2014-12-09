package com.winbook.layer.Resources;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import com.winbook.layer.controller.WinbookConfig;
import com.winbook.representations.RepresentationFactory;

public class ProjectsResource extends BaseResource {
	
	
	
	@Get
	public Representation getProjects()
	{
		RepresentationFactory factory = WinbookConfig.getSpringWebApplicationContext().getBean("RepresentationFactory",RepresentationFactory.class);
		return factory.getProjectListForUserRepresentation(getAuthorEmail());
		
	}

}
