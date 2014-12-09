package com.winbook.layer.Resources;

import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.winbook.DTO.GoalDTO;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.GoalDAO;

public class GoalResource extends BaseResource {
	
	private int goalId;
	private GoalDAO goalDAO;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		if(getRequestAttributes().containsKey("id"))
			goalId = Integer.valueOf(getRequestAttributes().get("id").toString());
		
		goalDAO = WinbookConfig.getSpringWebApplicationContext().getBean("GoalDAO", GoalDAO.class);
	}

	@Post("json")
	public GoalDTO addGoal(GoalDTO goal)
	{
		GoalDTO dto = goalDAO.addGoal(goal, getProjectName(), getWallName());
		setStatus(Status.SUCCESS_CREATED);
		return dto;
	}
	
	@Put("json")
	public GoalDTO updateGoal(GoalDTO goal)
	{
		if(!goalDAO.updateGoal(goal))
		{
			GoalDTO updatedGoal = null;
			
			try {
				updatedGoal = goalDAO.findById(goal.getId());
				setStatus(Status.CLIENT_ERROR_CONFLICT, "Stale Model - Goal was concurrently updated by another user");
			} catch (EmptyResultDataAccessException e) {
				setStatus(Status.CLIENT_ERROR_GONE, "Deleted Model - Goal was concurrently deleted by another user");
			}
			
			return updatedGoal;
		}
		
		goal.nextRevision();
		return goal;
		
	}
	
	@Delete("json")
	public void deleteGoal()
	{
		
		if(!goalDAO.deleteGoal(goalId))
			setStatus(Status.CLIENT_ERROR_GONE, "Deleted Model - Goal was concurrently deleted by another user");
		else
			setStatus(Status.SUCCESS_ACCEPTED);
	}
	
	
}
