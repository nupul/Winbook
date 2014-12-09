package com.winbook.layer.Resources;

import java.util.ArrayList;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.winbook.DTO.GoalDTO;
import com.winbook.domainobjects.Category;
import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.MissingOrIncorrectDataException;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.CategoryDAO;
import com.winbook.layer.dataaccess.GoalDAO;
import com.winbook.layer.dataaccess.IGenericDAO;
import com.winbook.layer.dataaccess.IWinConditionDAO;
import com.winbook.layer.dataaccess.WinConditionDAO;

public class WallPage {
	
	private static final String winbook = "Winbook";
	private String projectName;
	private String wallName;
	IWinConditionDAO winConditionDAO;
	IGenericDAO<Category> categoryDAO;
	GoalDAO goalDAO;
	
	public WallPage(String projectName, String wallName) {

		String firstLetterOfProject = projectName.substring(0, 1).toUpperCase();
		String remainderProjectString = projectName.substring(1);
		
		String firstLetterOfWallName = wallName.substring(0, 1).toUpperCase();
		String remainderWallName = wallName.substring(1);
		
		this.projectName = firstLetterOfProject + remainderProjectString;
		this.wallName = firstLetterOfWallName + remainderWallName;
		
		winConditionDAO = new WinConditionDAO(this.projectName, this.wallName);
		categoryDAO = new CategoryDAO(projectName, wallName);
		goalDAO = WinbookConfig.getSpringWebApplicationContext().getBean("GoalDAO", GoalDAO.class);
		
	}
	public String getWallName()
	{
		return wallName;
	}
	public String getProjectName()
	{
		return projectName;
	}
	public String getPageTitle()
	{
		projectName.toLowerCase();
		wallName.toLowerCase();
				
		return winbook+": "+projectName+"/"+wallName;
				
	}
	
	public List<WinCondition> getWinConditions() throws NoSuchProjectExistsException, NoWallForProjectExistsException 
	{
		return winConditionDAO.getAllWinConditions();
			
	}
	
	public List<Category> getCategories()
	{
		List<Category> listOfCategories = null;
		listOfCategories =  categoryDAO.getAllItemsForProject();
		
		return listOfCategories;
	}

	public List<GoalDTO> getGoals()
	{
		return goalDAO.getGoalsForProject(projectName, wallName, "business");
	}
	

}
