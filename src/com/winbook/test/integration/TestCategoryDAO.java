package com.winbook.test.integration;

import java.util.List;
import java.util.zip.DataFormatException;

import org.junit.Before;
import org.junit.Test;

import com.winbook.domainobjects.Category;
import com.winbook.domainobjects.User;
import com.winbook.exceptions.MissingOrIncorrectDataException;
import com.winbook.exceptions.TooLongNameException;
import com.winbook.layer.dataaccess.CategoryDAO;
import com.winbook.layer.dataaccess.IGenericDAO;

import static org.junit.Assert.*;


public class TestCategoryDAO {
	
	IGenericDAO<Category> categoryDAO;
	String projectName, wallName, author;
	
	@Before
	public void setUp()
	{
		projectName = "ezbay";
		wallName = "Wall";
		author = "nkukreja@usc.edu";
		categoryDAO = new CategoryDAO(projectName, wallName);
	}
	@Test
	public void testAddNewCategory() throws MissingOrIncorrectDataException
	{
		Category category = new Category(new User(author), "Interface Requirements", "#8b008b");
		
		int categoryId = categoryDAO.create(category);
		
		assertTrue("Category Id should be greater than zero!", categoryId>0);
		
		WorkingCategory.setWorkingCategoryId(categoryId);
	}

	
	
	@Test
	public void testUpdateAllCategoryDetails() throws TooLongNameException, MissingOrIncorrectDataException, DataFormatException 
	{
		int categoryId = WorkingCategory.getWorkingCategoryId();
		Category category = categoryDAO.getById(categoryId);
		String oldName = category.getCategoryName();
		String oldColor = category.getHexColorCode();
		
		String categoryName = "Interfacial Requirements :)";
		String hexColorCode = "#bbb777";
		category.setCategoryName(categoryName);
		category.setHexColorCode(hexColorCode);
		
		boolean isUpdated = categoryDAO.update(category);
		
		assertTrue("Category update failed", isUpdated);
		assertFalse("Category not updated!", oldName.equalsIgnoreCase(category.getCategoryName()));
		assertFalse("Category color not updated!", oldColor.equalsIgnoreCase(category.getHexColorCode()));
		
	}
	
	@Test
	public void testUpdateOnlyCategoryName() throws TooLongNameException, MissingOrIncorrectDataException
	{
		int categoryId = WorkingCategory.getWorkingCategoryId();
		Category category = categoryDAO.getById(categoryId);
		String color = category.getHexColorCode();
		String oldName = category.getCategoryName();
				
		String newCategoryName = "Interfacial25678 Requirements :)";		
				
		category.setCategoryName(newCategoryName);
		boolean isUpdated = categoryDAO.update(category);
		
		assertTrue("Category update failed", isUpdated);
		assertFalse("Category name same before and after update!", oldName.equalsIgnoreCase(categoryDAO.getById(categoryId).getCategoryName()));
		assertEquals("Color before and after update should have been the same!", color, categoryDAO.getById(categoryId).getHexColorCode());
	}
	
	
	@Test
	public void testUpdateOnlyCategoryColor() throws MissingOrIncorrectDataException, DataFormatException
	{
		int categoryId = WorkingCategory.getWorkingCategoryId();
		Category category = categoryDAO.getById(categoryId);
		String oldColor = category.getHexColorCode();
		String oldName = category.getCategoryName();
		
		category.setHexColorCode("#8b00b8");
		boolean isUpdated = categoryDAO.update(category);
		
		assertTrue("Category update failed", isUpdated);
		
		category = categoryDAO.getById(categoryId);
		assertNotSame("color should have been updated!", category.getHexColorCode(), oldColor);
		assertEquals("Category name should be the same", category.getCategoryName(), oldName);
	}
	
	@Test(expected=MissingOrIncorrectDataException.class)
	public void testTooLongAndEmptyCategoryNameAndColor() throws Exception
	{
		categoryDAO.create(new Category(new User(author), "", ""));
		Category c = categoryDAO.getById(WorkingCategory.getWorkingCategoryId());
		c.setCategoryName("");
		c.setHexColorCode("");
		
		categoryDAO.update(c);
	}
	
	@Test
	public void testGetSingleCategory() throws MissingOrIncorrectDataException
	{
		int categoryId = WorkingCategory.getWorkingCategoryId();
		Category category = categoryDAO.getById(categoryId);
		checkFields(category);
	}
	
	@Test
	public void testGetSingleNullCategory() throws MissingOrIncorrectDataException
	{
		int categoryId = 99999;
		Category c = categoryDAO.getById(categoryId);
		
		assertNull(c);
	}
	
	@Test
	public void testGetAllCategoriesForProjectWallSortedByCategoryName() throws MissingOrIncorrectDataException
	{
		List<Category> listOfCategories = categoryDAO.getAllItemsForProject();
		
		String previousName = listOfCategories.get(0).getCategoryName();
		for(int i=0;i<listOfCategories.size();i++)
		{
			Category c = listOfCategories.get(i);
			
			checkFields(c);
			
			if(i>0)
			{
				assertTrue("Data not sorted by category name",(c.getCategoryName().compareToIgnoreCase(previousName)>=0));
				previousName = listOfCategories.get(i).getCategoryName();
			}
			
			
		}
		
		//Yet to check sorting
	}
	
	@Test
	public void testGetAllCategoriesNullSetReturned() throws MissingOrIncorrectDataException
	{
		categoryDAO = new CategoryDAO("abc", "wallname");
		List<Category> listOfCategories = categoryDAO.getAllItemsForProject();
		
		assertTrue("list of categories should be Null. Change projectName.", listOfCategories==null||listOfCategories.size()==0);
	}
	
	@Test
	public void testDeleteCategory() throws MissingOrIncorrectDataException
	{
		int categoryId = WorkingCategory.getWorkingCategoryId();
		boolean isDeleted = categoryDAO.delete(categoryId);
		
		assertTrue(isDeleted);
		
		Category c = categoryDAO.getById(categoryId);
		
		assertNull(c);
	}
	
	@Test(expected=MissingOrIncorrectDataException.class)
	public void testIncorrectDeletionUpdateAndGetForCategory() throws Exception
	{
		categoryDAO.getById(0);
		categoryDAO.getById(-2);
		categoryDAO.delete(0);
		categoryDAO.delete(-2);
		categoryDAO.update(new Category(0));
		categoryDAO.update(new Category(-1));
	}
	

	
	private void checkFields(Category category) {
		assertNotNull("Category object is null!", category);
		assertNotNull("Category Name is null", category.getCategoryName());
		assertNotNull("Category color is null", category.getHexColorCode());
		assertTrue("Hex color code is more than 7 characters including #",category.getHexColorCode().length()<8);
		
	}
	
	private static class WorkingCategory
	{
		private static int workingCategoryId;

		public static int getWorkingCategoryId() {
			return workingCategoryId;
		}

		public static void setWorkingCategoryId(int workingCategoryId) {
			WorkingCategory.workingCategoryId = workingCategoryId;
		}
		
		
	}
	
}///:~
