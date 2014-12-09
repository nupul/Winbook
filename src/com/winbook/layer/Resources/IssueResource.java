package com.winbook.layer.Resources;

import java.sql.Timestamp;
import java.util.Map;

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
import com.winbook.domainobjects.User;
import com.winbook.layer.accessControl.WinbookEnroler;
import com.winbook.layer.dataaccess.IssueDAO;

public class IssueResource extends BaseResource {
	
	IssueDAO issueDAO;
	private String authorEmail, projectName, wallName;
	private Role userRole;
	
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		authorEmail = getAuthorEmail();
		wallName = getWallName();
		projectName = getProjectName();
		userRole = WinbookEnroler.getRole(authorEmail, projectName);
		issueDAO = new IssueDAO(projectName, wallName);
	}


	@Get("html")
	public String getIssue()
	{
		return "<H1>FETCHING ISSUE DETAILs TO BE IMPLEMENTED</H1>";
	}
	
	
	@Post
	public Representation addIssueToWinCondition(Representation issueData)
	{
		Form issueForm = new Form(issueData);
		String issueText = issueForm.getFirstValue("issue",true);
		
		if(getRequestAttributes().get("wcid")==null)
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Can't add Issue to a WinCondition without knowing it's id");
			return new StringRepresentation("Can't add Issue to a WinCondition without knowing it's id");
		}
		
		int winConditionId = Integer.parseInt(getRequestAttributes().get("wcid").toString()); //Integer.valueOf(issueForm.getFirstValue("wcid", true));
		
		Issue issue = new Issue(new User(authorEmail), winConditionId, issueText);
		
		int issueId = issueDAO.addIssueForWinCondition(issue);
		
		JSONObject json = new JSONObject();
		
		try {
			json.put("issueid", issueId);
			json.put("timestamp", new Timestamp(1000000));
			setStatus(Status.SUCCESS_CREATED);
		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL, "There was an error in acknowleding creation of the \"Issue\". Refreshing the 'wall' page may solve the problem. Please send feedback about the same");
			return new StringRepresentation("\"Issue\" Creation acknowledgment failed. Refreshing page may reflect changes");
		}
		
		
		return new JsonRepresentation(json);
	}
	
	
	@Put
	public void updateIssue(Representation entity)
	{
		if(userRole.equals(WinbookEnroler.NON_MEMBER))
		{
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, "You do not have the required permissions for this operation");
			return;
		}
		
		Issue issue = new Issue(Integer.parseInt(getRequestAttributes().get("id").toString()));
		Form updateForm = new Form(entity);
		
		if(updateForm.getValuesMap().containsKey("status"))
		{
			String status = updateForm.getFirstValue("status");
			issue.setStatus(status);
			
			if(issueDAO.updateIssueStatus(issue))
				setStatus(Status.SUCCESS_ACCEPTED);
			else
				setStatus(Status.SERVER_ERROR_INTERNAL,"Failed to update status of Issue. Either malformed request or internal server error. Please inform administrator.");
		}
		
		else
		{
			String updatedIssue = updateForm.getFirstValue("update",true);
			
			issue.setIssue(updatedIssue);
			
			if(issueDAO.updateIssueText(issue))
				setStatus(Status.SUCCESS_ACCEPTED);
			
			else
				setStatus(Status.SERVER_ERROR_INTERNAL,"Update Failed: Either malformed request or internal server error. Please inform administrator.");
			
		}
		
		
		
	}
	
	
	@Delete("html|text/plain")
	public void deleteIssue(Representation entity)
	{
		if(!getRequestAttributes().containsKey("id"))
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Incorrect delete request. Issue Id not specified");
			return;
		}
		
		int issueIdForDeletion = Integer.parseInt(getRequestAttributes().get("id").toString());
		
		if(issueDAO.deleteIssue(issueIdForDeletion))
			setStatus(Status.SUCCESS_OK);
		else
			setStatus(Status.SERVER_ERROR_INTERNAL, "Unable to delete Issue. Please leave feedback on the same");

	}
	
	
	
}///:~
	
	
	
