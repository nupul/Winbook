package com.winbook.layer.Resources;

import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.Role;

import com.winbook.domainobjects.Issue;
import com.winbook.domainobjects.Option;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;
import com.winbook.layer.accessControl.WinbookEnroler;
import com.winbook.layer.dataaccess.OptionDAO;

public class OptionResource extends BaseResource {
	
	private OptionDAO optionDAO;
	private String authorEmail, projectName, wallName;
	private Role userRole;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		authorEmail = getAuthorEmail();
		projectName = getProjectName();
		wallName = getWallName();
		userRole = WinbookEnroler.getRole(authorEmail, projectName);
		optionDAO = new OptionDAO(projectName, wallName);
	}
	
	@Post
	public Representation addOptionForIssue(Representation optionData)
	{
		
		Form optionForm = new Form(optionData);
		String optionText = optionForm.getFirstValue("option",true);
		
		if(!getRequestAttributes().containsKey("wcid") || !getRequestAttributes().containsKey("issueid"))
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Need both WinCondition Id and Issue Id to complete the request");
			return new StringRepresentation("Can't add option without know issue and win condition id. One or both of them are missing.");
		}
		
		int winConditionId = Integer.parseInt(getRequestAttributes().get("wcid").toString());
		int issueId = Integer.parseInt(getRequestAttributes().get("issueid").toString());
		
		Option option = new Option(new User(authorEmail), issueId, optionText);
		option.getIssue().setWinCondition(new WinCondition(winConditionId));	
		
		int optionId = optionDAO.addOptionForIssue(option);
		
		JSONObject json = new JSONObject();
		
		try {
			json.put("optionid",optionId);
			json.put("timestamp", new Timestamp(1000000));
			setStatus(Status.SUCCESS_CREATED);
		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL, "There was an error in acknowleding creation of the \"Option\". Refreshing the 'wall' page may solve the problem. Please send feedback about the same");
			return new StringRepresentation("\"Option\" Creation acknowledgment failed. Refreshing page may reflect changes");
		}
		
		
		return new JsonRepresentation(json);
		
	}//end POST
	
	
	@Put
	public void updateOption(Representation entity)
	{
		if(userRole.equals(WinbookEnroler.NON_MEMBER))
		{
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, "You are not authorized to perform this operation");
			return;
		}
		
		
		if(!getRequestAttributes().containsKey("wcid") || !getRequestAttributes().containsKey("issueid") || !getRequestAttributes().containsKey("optionid"))
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Need WinCondition Id, Issue Id and Option Id to complete the request");
			return;
		}
		
		
		Form updateForm = new Form(entity);
		int issueId = Integer.parseInt(getRequestAttributes().get("issueid").toString());
		int optionId = Integer.parseInt(getRequestAttributes().get("optionid").toString());
		Option option = new Option(optionId);
		
		//check if the update is for an agreement or for content.
		if(updateForm.getValuesMap().containsKey("agreement"))
		{
			String updatedOptionStatus = updateForm.getFirstValue("agreement",true);
			option.setStatus(updatedOptionStatus);
			
			if(optionDAO.updateOptionStatus(option))
				setStatus(Status.SUCCESS_ACCEPTED);
			
			else
				setStatus(Status.SERVER_ERROR_INTERNAL,"Failed to update status of Option. Either malformed request or internal server error. Please inform administrator.");

		}
		else
		{
			String updatedOption = updateForm.getFirstValue("update",true);
			
			option.setAuthor(new User(authorEmail));
			option.setIssue(new Issue(issueId));
			option.setOption(updatedOption);
			
			option.getIssue().setWinCondition(new WinCondition(Integer.parseInt(getRequestAttributes().get("wcid").toString())));
			
			if(optionDAO.updateOptionText(option))
				setStatus(Status.SUCCESS_ACCEPTED);
			else
				setStatus(Status.SERVER_ERROR_INTERNAL,"Update Failed: Either malformed request or internal server error. Please inform administrator.");
			
		}
		
	}
	
	@Delete
	public void deleteOption(Representation entity)
	{
		if(!getRequestAttributes().containsKey("wcid") || !getRequestAttributes().containsKey("issueid") || !getRequestAttributes().containsKey("optionid"))
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Incorrect delete request. Need WinCondition Id, Issue Id and Option Id to complete the request");
			return;
		}
		
		int optionIdForDeletion = Integer.parseInt(getRequestAttributes().get("optionid").toString());
		
		if(optionDAO.deleteOption(optionIdForDeletion))
			setStatus(Status.SUCCESS_OK);
		else
			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to delete Option. Please leave feedback on the same");
		
	}
	

}
