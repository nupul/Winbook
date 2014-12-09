/**
 * 
 */
package com.winbook.test.integration;

import org.junit.Before;
import org.junit.Test;

import com.winbook.domainobjects.Agreement;
import com.winbook.domainobjects.Option;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.MissingOrIncorrectDataException;
import com.winbook.layer.dataaccess.AgreementDAO;
import com.winbook.layer.dataaccess.IGenericDAO;
import static org.junit.Assert.*;


/**
 * @author Nupul
 *
 */
public class TestAgreementDAO {

	IGenericDAO<Agreement> agreementDAO;
	User user;
	WinCondition winCondition;
	Option option;
	Agreement agreement;
	
	@Before
	public void setUp()
	{
		agreementDAO = new AgreementDAO("ezbay","wall");
		user = new User("nkukreja@usc.edu");
		winCondition = new WinCondition(23);
		option = new Option(24);
		agreement = new Agreement(user, winCondition);
	}
	
	//@Test
	public void testAddAgreementForWinConditionAndOption() throws MissingOrIncorrectDataException
	{
		int agreementId = agreementDAO.create(agreement);
		
		assertTrue("Agreement id cannot be zero or less", agreementId>0);
		
		agreement.setOptionAgreedTo(option);
		int agreement2Id = agreementDAO.create(agreement);
		
		assertTrue("New agreement id should be generated from last time", ((agreement2Id != agreementId) && agreement2Id > 0));
	}
	
	@Test
	public void testDeleteAgreement() throws MissingOrIncorrectDataException
	{
		int agreementToDelete = 6;
		
		boolean isDeleted = agreementDAO.delete(agreementToDelete);
		
		assertTrue("agreement not deleted", isDeleted);
	}
	
	
	
}
