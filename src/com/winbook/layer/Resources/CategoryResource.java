package com.winbook.layer.Resources;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.winbook.domainobjects.Category;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinbookConstants;
import com.winbook.exceptions.MissingOrIncorrectDataException;
import com.winbook.layer.dataaccess.CategoryDAO;
import com.winbook.layer.dataaccess.IGenericDAO;

public class CategoryResource extends BaseResource{
	
	private String authorEmail, projectName, wallName;
	private IGenericDAO<Category> categoryDAO;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		authorEmail = getAuthorEmail();
		wallName = getWallName();
		projectName = getProjectName();
		categoryDAO = new CategoryDAO(projectName, wallName);
	}

	@Post
	public Representation createNewCategory(Representation entity)
	{
		Form categoryForm = new Form(entity);
		String categoryName = categoryForm.getFirstValue("categoryName", true);
		String categoryColor = categoryForm.getFirstValue("color", true, WinbookConstants.DEFAULT_CATEGORY_COLOR);
		boolean isMMF = Boolean.parseBoolean(categoryForm.getFirstValue("isMMF",true,"false"));
		
		Category category = new Category(new User(authorEmail), categoryName, categoryColor);
		category.setMMF(isMMF);
		
		int categoryId = 0;
		try {
			categoryId = categoryDAO.create(category);
		} catch (MissingOrIncorrectDataException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
		
		return new StringRepresentation(Integer.toString(categoryId));
	}	
	
	@Delete
	public void deleteCategory(Representation entity)
	{
		if(!getRequestAttributes().containsKey("id"))
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Incorrect delete request. Category Id not specified");
			return;
		}
		
		int categoryIdForDeletion = Integer.parseInt(getRequestAttributes().get("id").toString());
		
		try {
			if(categoryDAO.delete(categoryIdForDeletion))
				setStatus(Status.SUCCESS_OK);
			else
				setStatus(Status.SERVER_ERROR_INTERNAL,"Unable to delete category (Perhaps already deleted). Please leave feedback for the same.");
			
		} catch (MissingOrIncorrectDataException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage());
		}
	}
	
	@Put
	public void updateCategory(Representation entity)
	{
		if(!getRequestAttributes().containsKey("id"))
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Incorrect delete request. Category Id not specified");
			return;
		}
		
		Form update = new Form(entity);
		int id = Integer.parseInt(getRequestAttributes().get("id").toString());
		Category category = new Category(id);
		
		if(update.getFirstValue("name", true)!=null)
			category.setCategoryName(update.getFirstValue("name", true));
		
		if(update.getFirstValue("color",true)!=null)
			category.setHexColorCode(update.getFirstValue("color",true));
		
		try {
			categoryDAO.update(category);
		} catch (MissingOrIncorrectDataException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage());
		}
		
		setStatus(Status.SUCCESS_ACCEPTED);
	}

}
