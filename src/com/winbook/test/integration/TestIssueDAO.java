package com.winbook.test.integration;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import static org.junit.Assert.*;

import com.winbook.domainobjects.Issue;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;
import com.winbook.domainobjects.WinbookConstants;
import com.winbook.layer.dataaccess.BasicWinBookDAO;
import com.winbook.layer.dataaccess.IssueDAO;


public class TestIssueDAO {
	
	IssueDAO issueDAO;
	
	@Before
	public void setUp()
	{
		issueDAO = new IssueDAO("ezbay","wall");
	}

	@Test
	public void addIssueForWinCondition()
	{
		int winConditionId = 4;
		String issue = "None of the students have any experience with it whatsoever...anymore again!";
		
		Issue issueForWC = new Issue(new User("nkukreja@usc.edu"), winConditionId, issue);
		
		int issueId = issueDAO.addIssueForWinCondition(issueForWC);
		
		assertTrue("Issue Id must be greater than 0!", issueId>0);
		
		WorkingIssue.setWorkingIssueId(issueId);
	}
	
	@Test
	public void updateIssueText()
	{		
		Issue issue = new Issue(WorkingIssue.getWorkingIssueId());
		issue.setIssue("Here is the updated issue!");
		
		boolean isUpdated = issueDAO.updateIssueText(issue);
		
		assertTrue("Issue not updated...", isUpdated);
		
	}
	
	@Test
	public void updateIssueStatus()
	{
		Issue issue = new Issue(WorkingIssue.getWorkingIssueId());
		issue.setStatus(WinbookConstants.STATUS_CLOSED);
		
		assertTrue(issueDAO.updateIssueStatus(issue));
	}
	
		
	@Test
	public void fetchAllIssuesForProjectNullSetReturned()
	{
		issueDAO = new IssueDAO("abc", "wall");
		List<Issue> listOfIssues = issueDAO.getAllIssuesForProject();
		assertTrue("Issues existing when null should be returned", (listOfIssues==null || listOfIssues.size()==0));
	}
	
	
	@Test
	public void fetchAllIssuesForProjectNonNullSetReturnedAndSortedByTimestamp()
	{
		String project = "ezbay";
		List<Issue> listOfIssues = issueDAO.getAllIssuesForProject();
		
		assertTrue("Null/empty issue set returned when populated set was expect. Possible project name is incorrect for querying", listOfIssues.size()>0);
		
		
		
		Timestamp currentTimestamp = listOfIssues.get(0).getTimestamp();
		
		for (Issue anIssue : listOfIssues)
		{
			assertTrue("An Issue in the list doesn't have an id", anIssue.getId()>0);
			assertNotNull("Issue:"+anIssue.getId()+" is not associated to any wincondition", anIssue.getWinCondition());
			assertTrue("No win condition id found for Issue: "+anIssue.getId(), anIssue.getWinCondition().getId()>0);
			assertNotNull("'Null' author found for Issue: "+anIssue.getId(), anIssue.getAuthor());
			assertNotNull("Timestamp of Issue: "+anIssue.getId()+" missing ", anIssue.getTimestamp());
			assertTrue("User display name for Issue:"+anIssue.getId()+" is missing", anIssue.getAuthor().getDisplayName().length()>0);
			
			//check for timestamp sorting
			assertTrue("Issues not ordered by timestamp", currentTimestamp.compareTo(anIssue.getTimestamp())>=0);
			
			
		}
		
	}
	
	
	@Test
	public void deleteIssue()
	{
		int issueId = WorkingIssue.getWorkingIssueId();
		boolean isDeleted = issueDAO.deleteIssue(issueId);
		
		assertTrue("Issue not deleted OR no such issue exists for deletion", isDeleted);
	}
	
	
	
	//Holds the current Issue id on which the test case is being run on. Deletes the added issue after tests are complete leaving DB clean of test data
	private static class WorkingIssue
	{
		static int workingIssueId;

		public static int getWorkingIssueId() {
			return workingIssueId;
		}

		public static void setWorkingIssueId(int id) {
			workingIssueId = id;
		}
		
		
	}///:~ End WorkingIssue class
	
	
}///:~ End IssueDAO
