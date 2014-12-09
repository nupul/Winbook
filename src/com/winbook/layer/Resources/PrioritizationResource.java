package com.winbook.layer.Resources;

import java.util.List;

import javax.sql.DataSource;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.winbook.DTO.CriterionDTO;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.CriteriaDAO;
import com.winbook.representations.PrioritizationRepresentation;

public class PrioritizationResource extends BaseResource {

	private Form queryParams;
	private CriteriaDAO dao;
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		queryParams = getRequest().getResourceRef().getQueryAsForm();
		dao = WinbookConfig.getSpringWebApplicationContext().getBean("CriteriaDAO", CriteriaDAO.class);
	}

	/*
	 * get list of criteria for particular
	 */
	@Get("html")
	public Representation getPrioritizationTemplate()
	{
		List<CriterionDTO> listOfCriteria = dao.getPrioritizationCriteria(getProjectName(), getWallName(), queryParams.getFirstValue("type"));
		PrioritizationRepresentation repr = new PrioritizationRepresentation();
		return repr.getPrioritizationTemplate(listOfCriteria);
	}
	
}
