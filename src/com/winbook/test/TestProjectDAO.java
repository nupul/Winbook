package com.winbook.test;


import javax.sql.DataSource;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.winbook.DTO.ProjectVisionDTO;
import com.winbook.domainobjects.Project;
import com.winbook.domainobjects.User;
import com.winbook.layer.dataaccess.ProjectDAO;
import com.winbook.layer.dataaccess.UserDAO;

public class TestProjectDAO {

	private ProjectDAO projectDAO;
	private String username;
	
	@Before
	public void setUp() throws Exception {
		DataSource dataSource = new MysqlDataSource();
		
		((MysqlDataSource)dataSource).setUser("nupul");
		((MysqlDataSource)dataSource).setPassword("vfE9YIFE");
		((MysqlDataSource)dataSource).setServerName("localhost");
		((MysqlDataSource)dataSource).setPortNumber(3306);
		((MysqlDataSource)dataSource).setDatabaseName("winbookdb");
		
		projectDAO = new ProjectDAO();
		projectDAO.setDataSource(dataSource);
		username = "nkukreja@usc.edu";
		
	}
	
	/*
	 * This gets all projects in the system and checks for which projects the user is a member and for which he/she isn't. 
	 * This is a rather naive test case - it was not worth it to write an automated comparison at "this time: Sep 18 4:30pm" and hence an inspection based test case is written.
	 * Time is of the essence and hence the decision...
	 */
	@Test
	public void testFetchAllProjectsForMemberToCheckOverallMembership()
	{
		User user = new User(username); 
		user.setFirstName("Nupul");
		user.setLastName("Kukreja");
		
		System.out.println("Getting Membership Details for: "+user.getDisplayName());
		
		user = projectDAO.populateProjectMembershipDetailsForUser(user);
		for(Project project : user.getProjectsMemberOf())
			System.out.println("Member of: "+project.getName());
		
		for(Project project : user.getProjectsNotMemberOf())
			System.out.println("NOT Member of:"+project.getName());
		
		
	}
	
	@Test
	public void testGetVision()
	{
		String vision = projectDAO.getVision("myproject").getVision();
		assertEquals("hello", vision);
	}
	
	@Test
	public void testUpdateVision()
	{
		String newVision = "Here is a new vision";
		ProjectVisionDTO dto = new ProjectVisionDTO();
		dto.setVision(newVision);
		assertTrue(projectDAO.updateVision("myproject", dto));
		
		String updatedVision = projectDAO.getVision("myproject").getVision();
		assertEquals(newVision, updatedVision);
	}
}
