/**
 * 
 */
package com.winbook.layer.accessControl;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinbookConstants;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.UserDAO;
import com.winbook.representations.RepresentationFactory;

/**
 * @author Nupul
 *
 */
public class LoginHandler extends ServerResource {

	private UserDAO userDAO;
	private RepresentationFactory factory;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		userDAO = WinbookConfig.getSpringWebApplicationContext().getBean("UserDAO", UserDAO.class);
		factory = WinbookConfig.getSpringWebApplicationContext().getBean("RepresentationFactory",RepresentationFactory.class);
	}

	@Get
	public Representation getLoginScreen()
	{
		return factory.getLoginRepresentation(getRequest());
	}
	
	@Post
	public void validateCredentials(Representation entity) 
	{
		Form f = new Form(entity);
		String username = f.getFirstValue("username");
		String password = f.getFirstValue("password");
		
		if(userDAO.isAuthenticated(username, password))
		{
			CookieSetting cs = new CookieSetting(WinbookConstants.COOKIE_CREDENTIALS, username+"="+password);
			cs.setAccessRestricted(true);
			cs.setPath("/");
			cs.setComment("Winbook");
			
			getResponse().getCookieSettings().add(cs);
			setStatus(Status.SUCCESS_OK);
			
		}
		
		else
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		
	}
}
