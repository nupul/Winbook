/**
 * 
 */
package com.winbook.layer.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.winbook.domainobjects.Category;
import com.winbook.domainobjects.WinCondition;
import com.winbook.layer.dataaccess.WinConditionCategoryAssociationDAO;

/**
 * @author Nupul
 *
 */
public class WinConditionCategorizerResource extends BaseResource {
	
	private WinConditionCategoryAssociationDAO wcCategorizerDAO;

	@Put("json")
	public void updateCategorization(Representation entity)
	{
		JsonRepresentation jsonRepresentation;
		try {
			 jsonRepresentation = new JsonRepresentation(entity);
			 JSONArray wcCategorization = jsonRepresentation.getJsonArray();
			 
			 //The lists to give to the dao for addition/deletion respectively
			 List<WinCondition> listOfWCsToAddToCategories = new ArrayList<WinCondition>();
			 List<WinCondition> listOfWCsToDeleteFromCategories = new ArrayList<WinCondition>();

			 //loop through all the postIds (i.e., winconditions)
			 for(int i=0;i < wcCategorization.length(); i++)
			 {
				 JSONObject wcData = wcCategorization.getJSONObject(i);
				 
				 /*
				  * Create 2 win condition objects - one to hold data for "adding" categories and the other for deleting
				  * This is to be done since there are two different lists for addition/deletion and the same object can't be in both!
				  */
				 WinCondition wcToAddCategories = new WinCondition(wcData.getInt("postId"));
				 WinCondition wcToDeleteCategories = new WinCondition(wcData.getInt("postId"));
				 
				 JSONArray listOfCategories = wcData.getJSONArray("labels");
				 
				 //for each post, loop through its list of category labels and decide what action to take.
				 for(int j=0; j<listOfCategories.length(); j++)
				 {
					 JSONObject category = listOfCategories.getJSONObject(j);
					 String action = category.getString("action");
					 
					 if(action.equalsIgnoreCase("add"))
						 wcToAddCategories.addCategory(new Category(category.getInt("id")));
					 
					 else if(action.equalsIgnoreCase("delete"))
						 wcToDeleteCategories.addCategory(new Category(category.getInt("id")));
				 }
				 
				 
				 /*
				  * It is possible that no categories are to be added/deleted i.e., action=="none". Only add wincondition to list if 
				  * some action needs to be taken and list of categories is NOT empty/null
				  */
				 if(wcToAddCategories.getCategories()!=null && !wcToAddCategories.getCategories().isEmpty())
					 listOfWCsToAddToCategories.add(wcToAddCategories);
				 
				 if(wcToDeleteCategories.getCategories()!=null && !wcToDeleteCategories.getCategories().isEmpty())
					 listOfWCsToDeleteFromCategories.add(wcToDeleteCategories);
			 }
			 
			 wcCategorizerDAO = new WinConditionCategoryAssociationDAO();
			 
			 //ensuring null lists aren't sent to the DAO
			 if(!listOfWCsToAddToCategories.isEmpty())
				 wcCategorizerDAO.addWinConditionsToCategory(listOfWCsToAddToCategories);
			 
			 if(!listOfWCsToDeleteFromCategories.isEmpty())
				 wcCategorizerDAO.deleteWinConditionsFromCategory(listOfWCsToDeleteFromCategories);
			 
			 setStatus(Status.SUCCESS_CREATED);
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Content type is perhaps not application/json...");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Unable to get JSON values for performing categorization. Either bad request or malformed json");
		}
	}

}
