/**
 * 
 */
package com.winbook.layer.Resources;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.winbook.domainobjects.Agreement;
import com.winbook.domainobjects.Option;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.MissingOrIncorrectDataException;
import com.winbook.layer.dataaccess.AgreementDAO;
import com.winbook.layer.dataaccess.IGenericDAO;

/**
 * @author Nupul
 *
 */
public class AgreementResource extends BaseResource {
	
	IGenericDAO<Agreement> agreementDAO;

	@Post
	public Representation addAgreement(Representation entity)
	{
		if(!getRequestAttributes().containsKey("wcid"))
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Win Condition Id needs to be specified");
			return null;
		}
		
		String URL = getReference().getHierarchicalPart();
		int winConditionId = Integer.parseInt((getRequestAttributes().get("wcid").toString()));
		
		Agreement agreement = new Agreement(new User(getAuthorEmail()), new WinCondition(winConditionId));
		
		//If agreement is for an option add the corresponding option id
		if(URL.contains("Options"))
		{
			int optionId = Integer.parseInt((getRequestAttributes().get("optionid").toString()));
			agreement.setOptionAgreedTo(new Option(optionId));
		}
		
		agreementDAO = new AgreementDAO(getProjectName(), getWallName());
		int agreementId = 0;
		
		try {
			agreementId = agreementDAO.create(agreement);
		} catch (MissingOrIncorrectDataException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage());
		}
		
		return new StringRepresentation(Integer.toString(agreementId));
	}
	
	@Delete
	public void deleteAgreement(Representation entity)
	{
		if(!getRequestAttributes().containsKey("id"))
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Agreement Id needs to be specified for deletion");
		
		agreementDAO = new AgreementDAO(getProjectName(), getWallName());
		
		int agreementIdToDelete = Integer.parseInt(getRequestAttributes().get("id").toString());
		
		try {
			if(!agreementDAO.delete(agreementIdToDelete))
				setStatus(Status.SERVER_ERROR_INTERNAL,"Unable to delete agreement or agreement already deleted.");
			
			setStatus(Status.SUCCESS_OK);
		} catch (MissingOrIncorrectDataException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage());
		}
	}
}
