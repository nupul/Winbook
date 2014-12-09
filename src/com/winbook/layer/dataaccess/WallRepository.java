package com.winbook.layer.dataaccess;

import java.util.List;

import com.winbook.domainobjects.Issue;
import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;

public class WallRepository {

	private String projectName, wallName;
	
	private IWinConditionDAO wcDAO;
	private IssueDAO issueDAO;	
	
	public WallRepository(String projectName, String wallName)
	{
		this.projectName = projectName;
		this.wallName = wallName;
		
		wcDAO = new WinConditionDAO(projectName, wallName);
		issueDAO = new IssueDAO(projectName, wallName);
	}
	
	public List<WinCondition> getCompleteWIOATree(String projectName, String wallName) throws NoSuchProjectExistsException, NoWallForProjectExistsException
	{
		
		List<WinCondition> listOfWinConditions = wcDAO.getAllWinConditions();
		List<Issue> listOfIssues = issueDAO.getAllIssuesForProject();
		
		
		
		return listOfWinConditions;
		
	}
	
}
