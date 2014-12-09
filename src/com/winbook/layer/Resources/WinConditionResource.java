package com.winbook.layer.Resources;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.Role;

import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;
import com.winbook.layer.accessControl.WinbookEnroler;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.IWinConditionDAO;
import com.winbook.layer.dataaccess.WinConditionDAO;


public class WinConditionResource extends BaseResource {
	
	private IWinConditionDAO wcDAO;
	private Role userRole;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		wcDAO = new WinConditionDAO(getProjectName(), getWallName());
		userRole = WinbookEnroler.getRole(getAuthorEmail(), getProjectName());
	}



	@Get("html")
	public Representation getWinConditions()
	{
		
//		Form f = (Form) getRequestAttributes().get("org.restlet.http.headers");
//		
//		System.out.println("Is AJAX? ---------------------------------------------> "+f.getFirstValue("X-Requested-With", true));
//		
//		System.out.println("-------------------------------------------------------->Retrieving Win Conditions....");
		WallPage wallPage = new WallPage(getProjectName(), getWallName());
		
		Map<String, Object> wall = new HashMap<String, Object>();
		
		try {
			wall.put("baseRef", getRequest().getRootRef());
			wall.put("role", userRole.getName());
			wall.put("winconditions", wallPage.getWinConditions());
		} catch (NoSuchProjectExistsException e) {

			wall.put("title", "No Such Project Exists");
			setStatus(Status.CLIENT_ERROR_NOT_FOUND, "<b>No such Project exists: "+wallPage.getProjectName()+"</b>");
			return null;
			

		} catch (NoWallForProjectExistsException e) {
			
			wall.put("title", "No Such Wall Exists");
			setStatus(Status.CLIENT_ERROR_NOT_FOUND, "<b>No wall by the name: "+wallPage.getWallName()+" exists for Project: "+wallPage.getProjectName()+"</b>");
			return null;
			
		}
		
		return new TemplateRepresentation("WinConditions.ftl",WinbookConfig.getFreemarkerConfiguration(),wall,MediaType.TEXT_HTML);
		
	}
	
	@SuppressWarnings("deprecation")
	@Post
	public Representation createNewWinCondition(Representation entity)
	{
		
		Form winConditionForm = new Form(entity);
		String winConditionText = winConditionForm.getFirstValue("wincondition");
				
		WinCondition winCondition = new WinCondition(new User(getAuthorEmail()), winConditionText);
		
		int wcid = wcDAO.addWinCondition(winCondition);
		
		JSONObject json = new JSONObject();
		try {
			json.put("wcid", wcid);
			json.put("timestamp", new Timestamp(100000));
			setStatus(Status.SUCCESS_CREATED);
		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL, "There was an error in acknowleding creation of the \"Win Condition\". Refreshing the 'wall' page may solve the problem. Please send feedback about the same");
		}
		
		
		return new JsonRepresentation(json);
	}
	
	
	@Put
	public void updateWinCondition(Representation entity)
	{
		
		if(userRole.equals(WinbookEnroler.NON_MEMBER))
		{
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, "You do not have the required permissions for this operation");
			return;
		}
		
		int wcID = Integer.parseInt(getRequestAttributes().get("id").toString());
		WinCondition wc = new WinCondition(wcID);
		
		Form updateForm = new Form(entity);
		//check if the update is for an agreement or for content.
		if(updateForm.getValuesMap().containsKey("agreement"))
		{
			String updatedWCStatus = updateForm.getFirstValue("agreement");
			wc.setStatus(updatedWCStatus);
			
			if(wcDAO.updateStatus(wc))
				setStatus(Status.SUCCESS_ACCEPTED);
			
			else
				setStatus(Status.SERVER_ERROR_INTERNAL,"Failed to update status of win condition. Either malformed request or internal server error. Please inform administrator.");
			
		}
		
		else
		{
			String updatedWinCondition = updateForm.getFirstValue("update");
			
			wc.setWinCondition(updatedWinCondition);
			
			if(wcDAO.updateWinCondition(wc))
				setStatus(Status.SUCCESS_ACCEPTED);
			
			else
				setStatus(Status.SERVER_ERROR_INTERNAL, "Update Failed: Either malformed request or internal server error. Please inform administrator.");
		}
		
	}
	
	@Delete
	public void deleteWinCondition(Representation entity)
	{
		int wcidToDelete = Integer.parseInt(getRequestAttributes().get("id").toString());
			
		if(wcDAO.deleteWinCondition(wcidToDelete))
			setStatus(Status.SUCCESS_OK);
		
		else 
			setStatus(Status.SERVER_ERROR_INTERNAL, "Delete Failed: Either malformed request or internal server error. Please inform administrator.");
		
	}
	
	
}///:~
