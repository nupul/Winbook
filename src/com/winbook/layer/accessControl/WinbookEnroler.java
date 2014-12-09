/**
 * 
 */
package com.winbook.layer.accessControl;

import org.restlet.data.ClientInfo;
import org.restlet.security.Enroler;
import org.restlet.security.Role;

import com.winbook.domainobjects.User;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.UserDAO;

/**
 * @author Nupul
 *
 */
public class WinbookEnroler {

	public static final Role SHAPER = new Role("shaper","shaper - project admin");
	public static final Role NON_MEMBER = new Role("non-member","valid authenticated user of system but not part of project");
	public static final Role MEMBER = new Role("member","valid member but not shaper of project");
	
	//When running outside the web/spring container this class may throw ExceptionInInitialize since it can't initialize the userDAO... 
	private static UserDAO userDAO = WinbookConfig.getSpringWebApplicationContext().getBean("UserDAO",UserDAO.class);
	
	public static Role getRole(String username, String projectName)
	{
		return userDAO.getRole(username, projectName);
		
	}
	
	public static User getUserDetails(String email)
	{
		return userDAO.getUser(email);
	}
	
//	@Override
//	public void enrole(ClientInfo client) {
//		
//		Role shaper = new Role("shaper", "shaper");
//		client.getRoles().add(shaper);
//		
//		
//	}

}
