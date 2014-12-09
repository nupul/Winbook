package com.winbook.test;


import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.winbook.layer.dataaccess.UserMembershipDAO;
import static org.junit.Assert.*;

public class TestUserMembershipDAO {

	private UserMembershipDAO userMembershipDAO;
	private String userEmail, projectName;
	
	@Before
	public void setUp() throws Exception {
		DataSource dataSource = new MysqlDataSource();
		
		((MysqlDataSource)dataSource).setUser("nupul");
		((MysqlDataSource)dataSource).setPassword("vfE9YIFE");
		((MysqlDataSource)dataSource).setServerName("localhost");
		((MysqlDataSource)dataSource).setPortNumber(3306);
		((MysqlDataSource)dataSource).setDatabaseName("winbookdb");
		
		userMembershipDAO = new UserMembershipDAO();
		userMembershipDAO.setDataSource(dataSource);
		
		userEmail = "csci577@usc.edu";
		projectName = "myproject";
	}

	@Test
	public void testAddMembership()
	{
		assertTrue(userMembershipDAO.addMembership(userEmail, projectName));
	}
}
