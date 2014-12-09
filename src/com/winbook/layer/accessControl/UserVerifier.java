/**
 * 
 */
package com.winbook.layer.accessControl;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.SecretVerifier;
import org.restlet.security.Verifier;
import org.springframework.web.context.WebApplicationContext;

import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.UserDAO;

/**
 * @author Nupul
 *
 */
public class UserVerifier extends SecretVerifier {

	UserDAO userDAO;

	public UserVerifier() {
		super();
		userDAO = WinbookConfig.getSpringWebApplicationContext().getBean("UserDAO",UserDAO.class);
	}

	
	@Override
	public boolean verify(String username, char[] secret) throws IllegalArgumentException {
		
		String password = String.copyValueOf(secret);
		
		return userDAO.isAuthenticated(username, password);
	}

	
}///:~


/*
 * Showing the use of a custom verifier. It is MUST to do a null check since this code is called twice when used with a 
 * ChallengeAuthenticator - one with a valid challenge response and one without. It has been left here and commented only as
 * institutional memory so that the "flow" of how to create/use your own verifier is clear.
 */
//	private class MyVerifier implements Verifier
//	{
//
//		/* (non-Javadoc)
//		 * @see org.restlet.security.Verifier#verify(org.restlet.Request, org.restlet.Response)
//		 */
//		@Override
//		public int verify(Request req, Response resp) {
//		if(req.getChallengeResponse()==null)
//		{
//			System.out.println(">>>>>NULL CHALLENGE RESPONSE...");
//			return RESULT_MISSING;
//		}
//			System.out.println(">>>> NON-NULL CHALLENGE CRESPONSE...");
//			String identifier = req.getChallengeResponse().getIdentifier();
//			String password = String.copyValueOf(req.getChallengeResponse().getSecret());
//			
//			if(identifier.equals("nkukreja@usc.edu") && password.equals("nkukreja"))
//				return RESULT_VALID;
//			
//			return RESULT_INVALID;
//		}
//		
//	}