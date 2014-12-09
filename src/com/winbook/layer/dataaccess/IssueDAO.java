package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.winbook.domainobjects.Issue;
import com.winbook.domainobjects.Option;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;

public class IssueDAO extends BasicWinBookDAO {

	
	SimpleJdbcTemplate simpleJdbcTemplateForIssue;
	SimpleJdbcInsert simpleJdbcInsertForIssue;
	private String projectName, wallName;
	
	public IssueDAO(String projectName, String wallName)
	{
		this.projectName = projectName;
		this.wallName = wallName;
		simpleJdbcTemplateForIssue = new SimpleJdbcTemplate(getDataSource());
		simpleJdbcInsertForIssue = new SimpleJdbcInsert(getDataSource()).withTableName("issue").usingColumns("issue","email","wc_id").usingGeneratedKeyColumns("iss_id");
	}
	
	public int addIssueForWinCondition(Issue issue) 
	{
		Map<String, Object> queryParams = new HashMap<String, Object>();
		
		queryParams.put("issue", issue.getIssue());
		queryParams.put("email", issue.getAuthor().getEmailId());
		queryParams.put("wc_id", Integer.valueOf(issue.getWinCondition().getId()));
		
		Number issueId = simpleJdbcInsertForIssue.executeAndReturnKey(queryParams);
		
		return issueId.intValue();
	}

	public boolean deleteIssue(int issueId) {

		String sql = "DELETE FROM issue WHERE iss_id = ?";
		
		int numRowsDeleted = simpleJdbcTemplateForIssue.update(sql, Integer.valueOf(issueId));
		
		if(numRowsDeleted==0)
			return false;
		
		return true;
	}

	public boolean updateIssueText(Issue issue) 
	{
		
		String sql = "UPDATE issue SET issue = ? WHERE iss_id = ?";
		int numRowsUpdated = simpleJdbcTemplateForIssue.update(sql, issue.getIssue(), Integer.valueOf(issue.getId()));
		
		if(numRowsUpdated == 0)
			return false;
		
		return true;
	}
	
	public boolean updateIssueStatus(Issue issue) {

		String sql = "UPDATE issue SET status = ? WHERE iss_id = ?";
		
		int numRowsUpdated = simpleJdbcTemplateForIssue.update(sql, issue.getStatus(),Integer.valueOf(issue.getId()));
		
		if(numRowsUpdated == 0)
			return false;
		
		return true;
	}

	public List<Issue> getAllIssuesForProject() {
		
		String sql = "select wc_id, iss_id, issue, issue.timestamp, issue.status, issue.email, firstname, lastName, nickName, avatar from issue"
					+" join user using (email)"
					+" join wincondition w using (wc_id)"
					+" where w.project_title = ? and wall_name = ?"
					+" order by timestamp desc";
		
		List<Issue> listOfIssues = null;
		
		listOfIssues = simpleJdbcTemplateForIssue.query(sql, new IssueMapper(), projectName, wallName);		
		
		getAllOptionsForIssues(listOfIssues, projectName);
		
		return listOfIssues;
	}
	
	//Populate issues with corresponding options
	private void getAllOptionsForIssues(List<Issue> listOfIssues,String projectName)
	{
		/*
		 * Convert the list of issues into a map for easy access when mapping options to issues. The original order of the list
		 * of issues must be maintained since it's sorted by timestamp! Hence use LinkedHashMap
		 */
		Map<Integer, Issue> mapOfIssues = new LinkedHashMap<Integer, Issue>(listOfIssues.size());
		
		for(Issue issue : listOfIssues)
		{
			mapOfIssues.put(Integer.valueOf(issue.getId()),issue);
		}
		
		OptionDAO optionDAO = new OptionDAO(this.projectName,this.wallName);
		
		//Add options to the corresponding issues
		List<Option> listOfOptions = optionDAO.getAllOptionsForProject();
		
		for(Option option : listOfOptions)
		{
			mapOfIssues.get(Integer.valueOf(option.getIssue().getId())).addOption(option);
		}
		
		/*
		 * You don't need to do anything else after this. Since Java is pass by reference, the objects pointed to by (each of the issues in) 
		 * listOfIssues gets updated when we add an option to that issue. You may make the argument of this method final, since 
		 * the reference is not being changed.
		 * 
		 * See WinConditionDAO's getAllIssuesForProject() Method for further details.		
		 */
		
	}
	
	
	public List<Issue> getIssuesForWinCondition(int winConditionId)
	{
		throw new NotImplementedException();
	}
	
	public boolean updateIssueStatus(String status)
	{
		throw new NotImplementedException();
	}
	
	
	
	
	//Maps DB columns to the corresponding object attributes of Issue
	private class IssueMapper implements RowMapper<Issue>
	{

		@Override
		public Issue mapRow(ResultSet rs, int rowNumber) throws SQLException {
			
			WinCondition wc = new WinCondition(rs.getInt("wc_id"));
			
			User author = new User(rs.getString("email"));
			author.setFirstName(rs.getString("firstname"));
			author.setLastName(rs.getString("lastName"));
			author.setNickname(rs.getString("nickname"));
			author.setAvatarURL(rs.getString("avatar"));
			
			Issue issue = new Issue(author, wc, rs.getString("issue"));
			issue.setId(rs.getInt("iss_id"));
			issue.setTimestamp(rs.getTimestamp("timestamp"));
			issue.setStatus(rs.getString("status"));
			
			
			// TODO Auto-generated method stub
			return issue;
		}
		
	}



}///:~
