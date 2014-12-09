package com.winbook.layer.Resources;

import javax.sql.DataSource;

import org.restlet.data.Status;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.winbook.DTO.CriterionDTO;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.CriteriaDAO;

public class CriterionResource extends BaseResource {
	
	private int criterionId;
	private CriteriaDAO dao;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		criterionId = Integer.valueOf(getRequestAttributes().get("id").toString());
		dao = WinbookConfig.getSpringWebApplicationContext().getBean("CriteriaDAO", CriteriaDAO.class);
	}
	
	/*
	 * Update Criterion Optimistically - if optimistic updating fails fetch new criterion and return to user. 
	 * If criterion concurrently deleted by another user return existing criterion informing user about the same.
	 */
	@Put("json") 
	public CriterionDTO updateCriterion(CriterionDTO criterion)
	{
		
		if(!dao.updatePrioritizationCriterion(criterion))
		{
			CriterionDTO updatedDTO = null;
			try {
					updatedDTO = dao.findById(criterionId);
					setStatus(Status.CLIENT_ERROR_CONFLICT, "Stale Model - Criterion: "+criterion.getTitle()+" has been updated by another user.");
			} catch (EmptyResultDataAccessException e) {
					setStatus(Status.CLIENT_ERROR_GONE,"Deleted Model - Criterion: "+criterion.getTitle()+" has been deleted by another user. Setting weight to 0 to discard from prioritization");
					
			}
			
			return updatedDTO;
		}
		
		criterion.nextRevision();
		return criterion;
	
	}
	

}///:~
