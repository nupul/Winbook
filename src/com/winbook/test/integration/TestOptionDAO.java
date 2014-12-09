package com.winbook.test.integration;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.winbook.domainobjects.Issue;
import com.winbook.domainobjects.Option;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinbookConstants;
import com.winbook.layer.dataaccess.OptionDAO;
import static org.junit.Assert.*;


public class TestOptionDAO {
	
	OptionDAO optionDAO;
	
	@Before
	public void setUp()
	{
		optionDAO = new OptionDAO("ezbay","wall");
	}
	
	@Test
	public void testAddOptionForIssue()
	{
		int issueId = 73;
		
		User author = new User("176.neha@gmail.com");
		String optionText = "I have an option for Issue 64 Yabadabadoo!!";
		
		Option option = new Option(author, issueId, optionText);
		
		int optionId = optionDAO.addOptionForIssue(option);
		
		assertTrue("Autogenerated optionId must be > 0", optionId >0);
		
		WorkingOption.setWorkingOptionId(optionId);
	}

	
	@Test
	public void testUpdateForOption()
	{
		int optionId = WorkingOption.getWorkingOptionId();
		
		String newOptionText = "Updated option for issue 64 opt id 1";
		
		Option option = new Option(optionId);
		option.setOption(newOptionText);
		
		boolean isUpdated = optionDAO.updateOptionText(option);
		
		assertTrue("Option update failed", isUpdated);
		
		
	}
	
	@Test
	public void testUpdateStatusOfOption()
	{
		int optionId = WorkingOption.getWorkingOptionId();
		
		Option option = new Option(optionId);
		option.setStatus(WinbookConstants.STATUS_MAY_AGREE);
		
		assertTrue(optionDAO.updateOptionStatus(option));
		
	}
	
	@Test
	public void testDeleteOption()
	{
		int optionId = WorkingOption.getWorkingOptionId();
		
		boolean isDeleted = optionDAO.deleteOption(optionId);
		
		assertTrue("Option deletion failed",isDeleted);
	}
	
	@Test
	public void fetchAllOptionsForProjectIssuesNullSetReturned()
	{
		optionDAO = new OptionDAO("abc", "wall");
		
		List<Option> listOfOptions = optionDAO.getAllOptionsForProject();
		
		assertTrue("Non-null set of options returned. May need to change project name or or fix DAO code", (listOfOptions==null || listOfOptions.size()==0));
	}
	
	@Test
	public void fetchAllOptionsForProjectIssuesNonNullSetReturnedAndSortedByTimeStamp()
	{
		String project = "vbrp";
		
		List<Option> listOfOptions = optionDAO.getAllOptionsForProject();
		
		assertNotNull("Null set of options returned when non-null expected. May need to change project name or fix DAO code", listOfOptions);
		assertTrue("Empty set of options returned.", listOfOptions.size()>0);
		
		
		Timestamp currentTimestamp = listOfOptions.get(0).getTimestamp();
		
		for (Option anOption : listOfOptions)
		{
			assertTrue("An Option in the list doesn't have an id or id is <=0", anOption.getId()>0);
			assertNotNull("Option: "+anOption.getId()+" is not associated to any Issue", anOption.getIssue());
			assertTrue("No Issue id found for Option: "+anOption.getId(), anOption.getIssue().getId()>0);
			assertNotNull("'Null' author found for Option: "+anOption.getId(), anOption.getAuthor());
			assertNotNull("Timestamp of Option: "+anOption.getId()+" missing ", anOption.getTimestamp());
			assertTrue("User display name for Option: "+anOption.getId()+" is missing", anOption.getAuthor().getDisplayName().length()>0);
			
			//check for timestamp sorting
			assertTrue("Options not ordered by timestamp", currentTimestamp.compareTo(anOption.getTimestamp())>=0);
			
			
		}
		
		
	}
	
	
	//Holds the current Option id on which the test case is being run on. Deletes the added issue after tests are complete leaving DB clean of test data
	
	private static class WorkingOption
	{
		static int workingOptionId;

		public static int getWorkingOptionId() {
			return workingOptionId;
		}

		public static void setWorkingOptionId(int workingOptionId) {
			WorkingOption.workingOptionId = workingOptionId;
		}
		
		
	}///:~ WorkingOption
	
	
}///:~
