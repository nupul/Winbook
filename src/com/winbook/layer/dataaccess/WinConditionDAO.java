package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.winbook.domainobjects.Agreement;
import com.winbook.domainobjects.Category;
import com.winbook.domainobjects.Issue;
import com.winbook.domainobjects.Option;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;
import com.winbook.exceptions.TooLongNameException;

public class WinConditionDAO extends BasicWinBookDAO implements IWinConditionDAO {

	private String projectName;
	private String wallName;
	private SimpleJdbcTemplate fetchWinCondition;
	private SimpleJdbcInsert insertWinCondition;
	
	private IGenericDAO<Agreement> agreementDAO;
	private IssueDAO issueDAO;
	private WinConditionCategoryAssociationDAO wcCategoryDAO;
	
	public WinConditionDAO(String projectName, String wallName)
	{
		super();
		this.projectName = projectName;
		this.wallName = wallName;
		fetchWinCondition = new SimpleJdbcTemplate(getDataSource());
		insertWinCondition = new SimpleJdbcInsert(getDataSource()).usingColumns("email","wincondition","wall_name", "project_title").withTableName("wincondition").usingGeneratedKeyColumns("wc_id");
		issueDAO = new IssueDAO(projectName,wallName);
		wcCategoryDAO = new WinConditionCategoryAssociationDAO();
		agreementDAO = new AgreementDAO(projectName, wallName);
	}
	
		
	
	@Override
	public int addWinCondition(WinCondition winCondition) {

		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("wincondition", winCondition.getWinCondition());
		params.put("email", winCondition.getAuthor().getEmailId());
		params.put("wall_name", wallName);
		params.put("project_title", projectName);
		
		Number winConditionID = insertWinCondition.executeAndReturnKey(params);
		
		return winConditionID.intValue();
	}
	
	@Override
	public WinCondition getWinCondition(int id) {

		String sql = "select wc_id, wincondition, timestamp, status, email, firstName, LastName, nickname, avatar " 
					+"from wincondition "
					+"join user using(email) where wc_id = ?";

		
		WinCondition wc;
		
		try {
			wc = fetchWinCondition.queryForObject(sql, new WinConditionMapper(), Integer.valueOf(id));
			wcCategoryDAO.getCategoriesForWinConditions(wc, projectName, wallName);
			
		} catch (EmptyResultDataAccessException e) {
			// Basically a NOOP since it's valid to return null
			wc = null;
			
		}
		return wc;
	}

	@Override
	public boolean updateWinCondition(WinCondition winCondition) {

		String sql = "UPDATE wincondition SET wincondition = ? WHERE wc_id = ?";
		
		int numRowsUpdated = fetchWinCondition.update(sql, winCondition.getWinCondition(), Integer.valueOf(winCondition.getId()) );
		
		if(numRowsUpdated == 0)
			return false;
		
		return true;
	}
	
	

	@Override
	public boolean updateStatus(WinCondition winCondition) {
		
		String sql = "update wincondition set status = ? where wc_id = ?";
		
		int numRowsUpdated = fetchWinCondition.update(sql, winCondition.getStatus(), Integer.valueOf(winCondition.getId()) );
		
		if(numRowsUpdated == 0)
			return false;
		
		return true;
		
	}



	@Override
	public boolean deleteWinCondition(int id) {

		String sql = "DELETE FROM wincondition WHERE wc_id = ?";
		
		int numRowsDeleted = fetchWinCondition.update(sql, Integer.valueOf(id));
		
		if(numRowsDeleted == 0)
			return false;
		
		return true;

		
		
	}

	@Override
	public List<WinCondition> getAllWinConditions() throws NoSuchProjectExistsException, NoWallForProjectExistsException {

		String sql = " select wc_id, wincondition, timestamp, status, email, firstName, lastName, nickname, avatar " 
					+" from wincondition "
					+" join user using(email) where wall_name = ? and project_title = ? order by timestamp desc";
		
		List<WinCondition> listOfWinConditions=null;


		listOfWinConditions=fetchWinCondition.query(sql, new WinConditionMapper(), wallName, projectName );


		if(listOfWinConditions.isEmpty())
		{

			int projectCount = fetchWinCondition.queryForInt("select count(*) from project where project_title=?",projectName);
			int wallCount = fetchWinCondition.queryForInt("select count(*) from wall where project_title = ? and wall_name = ?", projectName, wallName);

			if(projectCount==0) {
				throw new NoSuchProjectExistsException("Project: "+projectName+" does not exist");
			}

			else if(wallCount==0)
				throw new NoWallForProjectExistsException("Project: "+projectName+" does not have a wall by name: "+wallName);
			
			else
				return listOfWinConditions;
		}
		
		getIssuesAndAgreementsForWinConditions(listOfWinConditions);
		wcCategoryDAO.getCategoriesForWinConditions(listOfWinConditions, projectName, wallName);
		
		return listOfWinConditions;
	}
	
	private void getIssuesAndAgreementsForWinConditions(List<WinCondition> listOfWinConditions) {

		/*
		 * Convert the list of winconditions into a map for easy access when mapping issues to win conditions. The original order of the list
		 * of win conditions must be maintained since it's sorted by timestamp! Hence use LinkedHashMap
		 */
		Map<Integer, WinCondition> mapOfWinConditions = new LinkedHashMap<Integer, WinCondition>(listOfWinConditions.size());
		
		for(WinCondition wc : listOfWinConditions)
		{
			mapOfWinConditions.put(Integer.valueOf(wc.getId()), wc);
		}
		
		
		
		List<Issue> listOfIssues = issueDAO.getAllIssuesForProject();
		
		//Add each issue to the corresponding win condition(s)
		
		for(Issue issue : listOfIssues)
		{
			mapOfWinConditions.get(Integer.valueOf(issue.getWinCondition().getId())).addIssue(issue);
		}
		
		List<Agreement> lisOfAgreements = agreementDAO.getAllItemsForProject();
		
		//Add each agreement to corresponding win condition(s)
		
		for(Agreement agreement: lisOfAgreements)
		{
			mapOfWinConditions.get(Integer.valueOf(agreement.getWinConditionCovered().getId())).addAgreement(agreement);
		}
		
		/*
		 * You don't need to do anything else after this. Since Java is pass by reference, the objects pointed to by (each of the winConditions in) 
		 * listOfWinConditions gets updated when we add an issue to that win condition. You may make the argument of this method final, since 
		 * the reference is not being changed.
		 * 
		 * If you were to do this:
		 * listOfIssues = new ArrayList<WinCondition>(mapOfWinConditions.values())
		 * 
		 * then the reference (i.e., method argument) CANNOT be final		
		 */
		
	}

		
	//RowMapper - converting each row to a win condition object with it's (User) author.
	
	private class WinConditionMapper implements RowMapper<WinCondition> {

		@Override
		public WinCondition mapRow(ResultSet rs, int rowNumber) throws SQLException 
		{
			User author = new User(rs.getString("email"));
			author.setFirstName(rs.getString("firstName"));
			author.setLastName(rs.getString("lastName"));
			author.setNickname(rs.getString("nickname"));
			author.setAvatarURL(rs.getString("avatar"));
			
			WinCondition wc = new WinCondition(author, rs.getString("wincondition"));
			wc.setId(rs.getInt("wc_id"));
			wc.setStatus(rs.getString("status"));
			wc.setTimestamp(rs.getTimestamp("timestamp"));
			
			return wc;
			
		}
		
		
	}///:~ End WinConditionMapper
	

}///:~ End WinConditionDAO
