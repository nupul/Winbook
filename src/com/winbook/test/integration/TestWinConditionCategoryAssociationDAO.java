package com.winbook.test.integration;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.winbook.domainobjects.Category;
import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;
import com.winbook.layer.dataaccess.WinConditionCategoryAssociationDAO;
import com.winbook.layer.dataaccess.WinConditionDAO;

import static org.junit.Assert.*;


public class TestWinConditionCategoryAssociationDAO {
	WinConditionCategoryAssociationDAO wcCategoryDao;

	@Test
	public void testAddCategoriesForWinConditions()
	{
		wcCategoryDao = new WinConditionCategoryAssociationDAO();
		List<WinCondition> listOfWinConditions = new ArrayList<WinCondition>();
		
		int numRows = 0;
		for(int i=48;i<50;i++)
		{
			WinCondition wc = new WinCondition(i);
						
			for(int j = 63; j<66;j++ )
			{
				//have a win condition without category object being set. i.e., null category to check if the DAO can still work
				//if(j==3)
				//	continue;
				
				wc.addCategory(new Category(j));
				numRows++;
			}
			
			listOfWinConditions.add(wc);
		}
			
		
		int numRowsInserted = wcCategoryDao.addWinConditionsToCategory(listOfWinConditions);
		
		assertTrue(numRows == numRowsInserted);
	}
	
	
	@Test
	public void testGetCategoriesForWinConditions() throws NoSuchProjectExistsException, NoWallForProjectExistsException
	{
		wcCategoryDao = new WinConditionCategoryAssociationDAO();
		WinConditionDAO wcDAO = new WinConditionDAO("ezbay", "wall");
		
		List<WinCondition> listOfWinConditions = wcDAO.getAllWinConditions();
		
		wcCategoryDao.getCategoriesForWinConditions(listOfWinConditions,"ezbay","wall");
		
		int numWCsWithCategories=0;
		for(WinCondition wc: listOfWinConditions)
		{
			if(wc.getCategories()==null || wc.getCategories().size()==0)
				continue;
			
			numWCsWithCategories++;
		}
		
		assertTrue("No win condition has category. May need to create appropriate data set in DB for test to succeed",(numWCsWithCategories>0 && numWCsWithCategories<=listOfWinConditions.size()));
		
//		for(WinCondition wc : listOfWinConditions)
//		{
//			if(wc.getCategories()==null || wc.getCategories().size()==0)
//				continue;
//			
//			for(Category c: wc.getCategories())
//				System.out.println(">>>>> "+wc.getId()+" Belongs to: "+c.getCategoryName()+" with Color: "+c.getHexColorCode());
//		}
	}
	
	@Test
	public void testDeleteCategoriesForWinConditions()
	{
		wcCategoryDao = new WinConditionCategoryAssociationDAO();
		List<WinCondition> listOfWinConditions = new ArrayList<WinCondition>();
		
		int numRows = 0;
		for(int i=48;i<50;i++)
		{
			WinCondition wc = new WinCondition(i);
			
			for(int j = 63; j<66;j++ )
			{
				//Don't delete category 65 for corresponding WCs
				//if(j==65)
				//	continue;
				
				wc.addCategory(new Category(j));
				numRows++;
			}
			
			listOfWinConditions.add(wc);
		}
		
		int numRowsDeleted = wcCategoryDao.deleteWinConditionsFromCategory(listOfWinConditions);
		
		assertTrue(numRows==numRowsDeleted);
	}
	
}///:~ End tests
