package com.winbook.layer.Resources;

import javax.sql.DataSource;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.winbook.DTO.CriterionScoreDTO;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.CriterionScoreDAO;

public class CriterionScoreResource extends BaseResource {

	private int itemId; //item for which goals are to be sought
	private int criterionId; //criterion against which the item is scored
	private String itemType; //Whether win condition or MMF/categories
	private CriterionScoreDAO dao;
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		dao  = WinbookConfig.getSpringWebApplicationContext().getBean("CriterionScoreDAO", CriterionScoreDAO.class);
		itemId = Integer.parseInt(getRequestAttributes().get("itemId").toString());
		criterionId = Integer.parseInt(getRequestAttributes().get("id").toString());
		itemType = getRequestAttributes().get("itemType").toString();
		dao.setItemTypeForScoring(itemType);
		
	}
	
	@Get("json")
	public CriterionScoreDTO getScore(){
		
		CriterionScoreDTO dto = null;
		
		try {
			dto = dao.find(itemId, criterionId);
		} catch (EmptyResultDataAccessException e) {
			/*
			 * create an empty dto initialized with defaults and set to corresponding item and criterion Id
			 */
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			dto = new CriterionScoreDTO();
			dto.setItemId(itemId);
			dto.setCriterionId(criterionId);
		}
		
		
		return dto;
	}

	/*
	 * Optimistically update score - if updation fails, fetch new score and return to user. If score for particular
	 * item/criterion is not found implies either the corresponding criterion or item has been deleted by a
	 * concurrent user.
	 */
	@Put("json")
	public CriterionScoreDTO updateScore(CriterionScoreDTO criterionScore)
	{
		
		if(!dao.updateScore(criterionScore))
		{
			CriterionScoreDTO updatedDTO = null;
			try {
				updatedDTO = dao.find(itemId, criterionId);
				setStatus(Status.CLIENT_ERROR_CONFLICT," Stale Model - Criterion Score was concurrently updated by another user");
			} catch (EmptyResultDataAccessException e) {
				setStatus(Status.CLIENT_ERROR_GONE, "Deleted Model - Item "+itemId+" or corresponding criterion has been deleted.");
			
			}
			return updatedDTO;
		}
		
		criterionScore.nextRevision();
		return criterionScore;
	}
	
	@Post("json")
	public CriterionScoreDTO addScore(CriterionScoreDTO criterionScore)
	{
		
		try {
			dao.addScore(criterionScore);
		} catch (DuplicateKeyException e) {
			//Implies a user concurrently updated the score of the particular item. Fetch new item and send to client
			try {
				setStatus(Status.CLIENT_ERROR_CONFLICT,"Concurrency Conflict score for Item "+itemId+" was concurrently updated by another user");
				return dao.find(itemId, criterionId);
			} catch (EmptyResultDataAccessException e1) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Deleted Model - Item "+itemId+" or corresponding criterion has been deleted.");
			}
		} catch (DataIntegrityViolationException e) {
			/*
			 * If someone deleted the criterion/item before someone else was able to save to it. I.e. A get was 
			 * a success (i.e. a 404 was thrown, implying that the client must send a post request for creating the score)
			 * but a subsequent post caused a foreign key exception to be thrown implying that either
			 * an item or a particular criterion was removed before the score-resource could be created
			 */
				setStatus(Status.CLIENT_ERROR_GONE, "Deleted Model - Item/Criterion has been deleted by concurrent user");
		}
		
		return criterionScore;
	}
}
