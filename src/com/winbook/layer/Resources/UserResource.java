/**
 * 
 */
package com.winbook.layer.Resources;

import java.util.HashMap;
import java.util.Map;

import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.dao.DuplicateKeyException;

import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinbookConstants;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.ProjectDAO;
import com.winbook.layer.dataaccess.UserDAO;
import com.winbook.representations.RepresentationFactory;

/**
 * @author Nupul
 *
 */
public class UserResource extends ServerResource {

	private UserDAO userDAO;
	private ProjectDAO projectDAO;
	
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		userDAO = WinbookConfig.getSpringWebApplicationContext().getBean("UserDAO",UserDAO.class);
		projectDAO = WinbookConfig.getSpringWebApplicationContext().getBean("ProjectDAO",ProjectDAO.class);
	}



	@Post
	public Representation createUser(Representation entity)
	{
		Form registrationForm = new Form(entity);
		User newUser = new User(registrationForm.getFirstValue("email", true));
		newUser.setFirstName(registrationForm.getFirstValue("firstname",true));
		newUser.setLastName(registrationForm.getFirstValue("lastname",true));
		newUser.setPassword(registrationForm.getFirstValue("newpassword", true));
		newUser.setAvatarURL(registrationForm.getFirstValue("avatar", true, WinbookConstants.DEFAULT_AVATAR));
		newUser.setNickname(registrationForm.getFirstValue("nickname", true, null));
		
		String role = registrationForm.getFirstValue("role");
		
		if(role.equalsIgnoreCase("client") || role.equalsIgnoreCase("shaper"))
			newUser.getConfiguration().setShaper(true);
		else
			newUser.getConfiguration().setShaper(false);
		
		try {
			if(userDAO.createUser(newUser))
			{
				setStatus(Status.SUCCESS_CREATED);
				
				newUser = projectDAO.populateProjectMembershipDetailsForUser(newUser);
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("baseRef", getRequest().getRootRef());
				params.put("user", newUser);
				//getRequest().setMethod(Method.GET);
				//Return cookie with login credentials...
				CookieSetting cs = new CookieSetting(WinbookConstants.COOKIE_CREDENTIALS, newUser.getEmailId()+"="+newUser.getPassword());
				cs.setAccessRestricted(true);
				cs.setPath("/");
				cs.setComment("Winbook");
				
				getResponse().getCookieSettings().add(cs);
				
				return new TemplateRepresentation("ListOfProjects.ftl", WinbookConfig.getFreemarkerConfiguration(),params, MediaType.TEXT_HTML);
			}
			else
			{
				setStatus(Status.SERVER_ERROR_INTERNAL,"Unable to create user. Please try again. Inform administrator if problem persists");
				return new StringRepresentation("User Creation faillure...", MediaType.TEXT_HTML);
			}
		} catch (DuplicateKeyException e) {
			setStatus(Status.CLIENT_ERROR_CONFLICT,"Username already exists. Sign up FAILED. Please try again with a different username/password");
			RepresentationFactory factory = WinbookConfig.getSpringWebApplicationContext().getBean("RepresentationFactory",RepresentationFactory.class);
			return factory.getLoginRepresentation(getRequest(), "Username already exists. If you are resubmitting the form, please proceed with login/password else create an account with a different email id");
		}
	}
}
