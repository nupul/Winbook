/**
 * 
 */
package com.winbook.layer.accessControl;

import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.winbook.domainobjects.WinbookConstants;
import com.winbook.representations.RepresentationFactory;

/**
 * @author Nupul
 * Handles the Logout action for the user
 */
public class LogoutHandler extends ServerResource {

	@Get
	public Representation logout()
	{
		Cookie cookie = getRequest().getCookies().getFirst(WinbookConstants.COOKIE_CREDENTIALS);
		if(cookie!=null)
		{
			CookieSetting cs = new CookieSetting(WinbookConstants.COOKIE_CREDENTIALS, "logout");
			cs.setMaxAge(0);
			cs.setAccessRestricted(true);
			cs.setPath("/");
			cs.setComment("Winbook");
			
			getResponse().getCookieSettings().add(cs);
		}
		return RepresentationFactory.getInstance().getLoginRepresentation(getRequest());
	}
}
