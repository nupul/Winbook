package com.winbook.test.integration;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.naming.NamingException;

import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

import sun.jdbc.odbc.ee.DataSource;
import static org.junit.Assert.*;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.winbook.domainobjects.Project;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.Wall;
import com.winbook.domainobjects.WinCondition;
import com.winbook.domainobjects.WinbookConstants;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;
import com.winbook.layer.dataaccess.IWinConditionDAO;
import com.winbook.layer.dataaccess.WinConditionDAO;


public class TestWinConditionDAO {
	
	IWinConditionDAO wcDAO;
	Project project;
	Wall winWall;

	

	@Before
	public void setUp() throws NamingException, SQLException
	{	
		project = new Project(new User("nkukreja@usc.edu"), "ezbay");
		winWall = new Wall(project, "Wall");
		project.addWall(winWall);
		
		wcDAO = new WinConditionDAO(project.getName(), winWall.getName());
	
	}
	
	@Test
	public void addWinCondition()
	{
	
		
		WinCondition wc1 = new WinCondition(new User("nkukreja@usc.edu"), "There must be a way to add win conditions via a fb-like wall post - again");
				
		int idOfAddedWC = wcDAO.addWinCondition(wc1);	
		assertTrue("Id is zero!", idOfAddedWC>0 );
		
		WorkingWinCondition.setWorkingID(idOfAddedWC);
	}
	
	
	
	@Test
	public void fetchOneWinCondition() throws NamingException, SQLException
	{
		int wcID = WorkingWinCondition.getWorkingID();
		WinCondition wc = wcDAO.getWinCondition(wcID);
			
		assertNotNull("No Win Condition was found",wc);
		assertEquals(wcID, wc.getId());
		assertNotNull("A NULL wincondition was returned. Shouldn't be possible.", wc.getWinCondition());		
		assertThat("There is an empty win condition!", wc.getWinCondition().length(), is(not(0)));
		
		assertThat("The display name of the author is empty!", wc.getAuthor().getDisplayName().length(), is(not(0)));
		
	}
	
	
	@Test 
	public void updateWinCondition()
	{
		int wcID = WorkingWinCondition.getWorkingID();
		
		WinCondition wc = wcDAO.getWinCondition(wcID);
		String beforeUpdate = wc.getWinCondition();
		
		wc.setWinCondition("There must be a way to represent doubles eg: "+Math.random());
		assertTrue("WC update failed",wcDAO.updateWinCondition(wc));
		assertFalse("Win Condition not changed", wc.getWinCondition().equals(beforeUpdate));
		
		
	}
	
	@Test
	public void updateWinConditionStatus()
	{
		int wcID = WorkingWinCondition.getWorkingID();
		
		WinCondition wc = wcDAO.getWinCondition(wcID);
		String statusBeforeUpdate = wc.getStatus();
		
		wc.setStatus(WinbookConstants.STATUS_AGREE);
		
		assertTrue(wcDAO.updateStatus(wc));
		assertFalse(wc.getStatus().equals(statusBeforeUpdate));
	}
	
	@Test
	public void deleteWinCondition()
	{
		//add a win condition before deleting it.
		//WinCondition wc1 = new WinCondition(new User("mynameiskhan@usc.edu"), "There must be a way to add win conditions via a fb-like wall post - again");
		int wcID = WorkingWinCondition.getWorkingID();//wcDAO.addWinCondition(wc1);
	
		assertTrue("Win Condition Not deleted!", wcDAO.deleteWinCondition(wcID));
		assertNull("Win condition still exists after deletion!", wcDAO.getWinCondition(wcID));
	}
	
	@Test
	public void fetchAllWinConditionsAndCheckIfSortedByTime() throws NoSuchProjectExistsException, NoWallForProjectExistsException
	{
		List<WinCondition> allWinConditions = wcDAO.getAllWinConditions();
		assertNotNull("Null list of win conditions returned!", allWinConditions);
		
		Timestamp currentTimestamp = allWinConditions.get(0).getTimestamp();
		
		for(WinCondition wc : allWinConditions)
		{
			assertTrue("A WC has Id 0 or undefined", wc.getId()>0);
			
			assertTrue("WinConditions NOT sorted by timestamp", currentTimestamp.compareTo(wc.getTimestamp()) >=0 );  //if the currently accessed timestamp is greater than or equal to the next ==> Sorted.
				
		}
		
	
		
	}
	
	@Test
	public void testIfProjectDoesNotExist() 
	{
		wcDAO = new WinConditionDAO("nonexistingproject", "nowall");
		List<WinCondition> lWCs = null;
		
		try {
			wcDAO.getAllWinConditions();
			fail("No such project exists and an exception must be thrown!");
		} catch (NoSuchProjectExistsException e) {


		} catch (Exception e) {
			
			fail("The exception must be caught earlier!");

		}
		
		//assertNull("NON Null list was returned!!",lWCs);
	}
	
	@Test(expected=NoWallForProjectExistsException.class)
	public void testForNoWallForProjectExists() throws Exception
	{
		wcDAO = new WinConditionDAO("ezbay", "pink");
		
		List<WinCondition> lWCs = wcDAO.getAllWinConditions();
		
		assertNull("NON Null list was returned!", lWCs);
	}
	
	
	
	//Passing state between tests of the wincondition currently being worked on.
	private static class WorkingWinCondition
	{
		static int workingWC;
		public static void setWorkingID(int id){
			workingWC = id;
		}
		
		public static int getWorkingID()
		{
			return workingWC;
		}
		
	}
	
}

