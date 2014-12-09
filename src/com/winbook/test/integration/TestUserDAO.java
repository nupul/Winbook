package com.winbook.test.integration;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.restlet.security.Role;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.winbook.domainobjects.User;
import com.winbook.layer.accessControl.CryptoHash;
import com.winbook.layer.accessControl.WinbookEnroler;
import com.winbook.layer.dataaccess.UserDAO;

import static org.junit.Assert.*;

/**
 * @author Nupul
 *
 */
public class TestUserDAO {
	
	UserDAO userDAO;
	String username, password,projectName;
	
	@Before
	public void setUp()
	{
		DataSource datasource = new MysqlDataSource();
		
		((MysqlDataSource)datasource).setUser("nupul");
		((MysqlDataSource)datasource).setPassword("vfE9YIFE");
		((MysqlDataSource)datasource).setServerName("localhost");
		((MysqlDataSource)datasource).setPortNumber(3306);
		((MysqlDataSource)datasource).setDatabaseName("winbookdb");
		userDAO = new UserDAO();
		userDAO.setDataSource(datasource);
		username = "nkukreja@usc.edu";
		password = CryptoHash.SHA1("nkukreja");
		projectName = "ezbay";
	}
	
	@Test
	public void testAuthentication()
	{
		
		boolean isAuthenticated = userDAO.isAuthenticated(username,password);
		
		assertTrue("User authentication failed...should have succeeded. Username/password invalid or user doesn't exist",isAuthenticated);
		
		isAuthenticated = userDAO.isAuthenticated("xyz","pqr");
		
		assertFalse("User authentication should have failed...but succeeded",isAuthenticated);
	}

	@Test
	public void testFetchRoles()
	{
		Role role = userDAO.getRole(username, projectName);
		
		assertTrue(role.equals(WinbookEnroler.SHAPER));
		
		//role = userDAO.getRole("csci577@usc.edu",projectName);
		
		//assertTrue(role.equals(WinbookEnroler.MEMBER));
		
		//role = userDAO.getRole("rahul@gmail.com",projectName);
		
		//assertTrue(role.equals(WinbookEnroler.NON_MEMBER));
	}
	
	@Test
	public void addNewUserWithDisplayNameAndAvatarURL()
	{
		User user = new User("nupul@bj.com");
		user.setFirstName("nupul");
		user.setLastName("kukreja");
		user.setPassword(CryptoHash.SHA1("nupul"));
		//user.setNickname("nk");
		//user.setAvatarURL("http://www.google.com/avatar?=ai9878kdjf");
		user.getConfiguration().setShaper(true);
		
		assertTrue(userDAO.createUser(user));
	}
	
}
