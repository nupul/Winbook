/**
 * 
 */
package com.winbook.layer.accessControl;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.routing.Redirector;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Verifier;

import com.winbook.domainobjects.WinbookConstants;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.representations.RepresentationFactory;

/**
 * @author Nupul
 *
 */
public class CookieAuthenticator extends ChallengeAuthenticator {

	//calling super with HTTP_COOKIE as challenge scheme
	public CookieAuthenticator(Context context, boolean optional, String realm) {
		super(context, optional, ChallengeScheme.HTTP_COOKIE, realm);
	}

	public CookieAuthenticator(Context context, boolean optional, String realm, Verifier verifier) {
		super(context, optional, ChallengeScheme.HTTP_COOKIE, realm, verifier);
	}

	public CookieAuthenticator(Context context, String realm) {
		super(context, ChallengeScheme.HTTP_COOKIE, realm);
	}

	//Check if cookie exists (before handling incoming request):
	@Override
	protected int beforeHandle(Request request, Response response) {
		
		Cookie cookie = request.getCookies().getFirst(WinbookConstants.COOKIE_CREDENTIALS);
		
		//process cookie if it exists else continue with the call (implying authentication failure)
		if(cookie!=null)
		{
			String[] credentials = cookie.getValue().split("=");
			
			if(credentials.length == 2)
			{
				String identifier = credentials[0];
				String secret = credentials[1];
				request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_COOKIE, identifier, secret));
			}
		}
		
		return super.beforeHandle(request, response);
	}

	//Redirect user to login page if cookie doesn't exist.
	@Override
	public void challenge(Response response, boolean stale) {
		Request request = Request.getCurrent();
		response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		Redirector redirect = new Redirector(getContext(), request.getRootRef().toString(), Redirector.MODE_CLIENT_SEE_OTHER);
		redirect.handle(request, response);
	}
	
	
}///:~
