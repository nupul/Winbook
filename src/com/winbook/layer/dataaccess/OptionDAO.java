package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.winbook.domainobjects.Agreement;
import com.winbook.domainobjects.Issue;
import com.winbook.domainobjects.Option;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;

public class OptionDAO extends BasicWinBookDAO{

	private SimpleJdbcTemplate simpleJdbcTemplateForOption;
	private SimpleJdbcInsert simpleJdbcInsertForOption;
	private String projectName, wallName;
	
	public OptionDAO(String projectName, String wallName)
	{
		this.projectName = projectName;
		this.wallName = wallName;
		simpleJdbcTemplateForOption = new SimpleJdbcTemplate(getDataSource());
		simpleJdbcInsertForOption = new SimpleJdbcInsert(getDataSource()).withTableName("issoption").usingColumns("optionDetail", "email", "iss_id").usingGeneratedKeyColumns("opt_id");
	}
	
	public int addOptionForIssue(Option option) {
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		
		queryParams.put("optionDetail", option.getOption());
		queryParams.put("email", option.getAuthor().getEmailId());
		queryParams.put("iss_id", Integer.valueOf(option.getIssue().getId()));
		
		Number optionId = simpleJdbcInsertForOption.executeAndReturnKey(queryParams);
		
		return optionId.intValue();
	}

	public boolean updateOptionText(Option option) {
		
		String sql = "UPDATE issoption SET optionDetail = ? WHERE opt_id = ?";
		
		int numRowsUpdated = simpleJdbcTemplateForOption.update(sql, option.getOption(), Integer.valueOf(option.getId()));
		
		if(numRowsUpdated==0)
			return false;		
		
		return true;
	}
	
	public boolean updateOptionStatus(Option option) {
		
		String sql = "UPDATE issoption SET status= ? WHERE opt_id = ?";
		
		int numRowsUpdated = simpleJdbcTemplateForOption.update(sql, option.getStatus(),Integer.valueOf(option.getId()));
		
		if(numRowsUpdated==0)
			return false;
		
		return true;
	}


	public boolean deleteOption(int optionId) {
		String sql = "DELETE FROM issoption WHERE opt_id = ?";
		
		int numRowsDeleted = simpleJdbcTemplateForOption.update(sql, Integer.valueOf(optionId));
		
		if(numRowsDeleted == 0)
			return false;
		
		return true;
	}

	public List<Option> getAllOptionsForProject() {
		
		String sql = " select opt_id, iss_id, wc_id, optionDetail, opt.email, opt.timestamp, opt.status, firstName, lastName, nickName, avatar"
					+" from issoption opt"
					+" join user using(email)"
					+" join issue using(iss_id)"
					+" join wincondition using (wc_id)"
					+" where project_title = ? and wall_name = ?"
					+" order by opt.timestamp desc";
		
		List<Option> listOfOptions = null;
		listOfOptions = simpleJdbcTemplateForOption.query(sql, new OptionMapper(), projectName, wallName);
		
		getAgreementsForOptions(listOfOptions);
		
		return listOfOptions;
	}

	/*
	 * See WinConditionDAO.getIssuesAndAgreementsForWinConditions(...) method for detailed comments.
	 */
	private void getAgreementsForOptions(List<Option> listOfOptions) {
		
		Map<Integer, Option> mapOfOptions = new LinkedHashMap<Integer, Option>();
		
		for(Option opt: listOfOptions)
			mapOfOptions.put(Integer.valueOf(opt.getId()), opt);

		//Since only using for one method, directly using concrete class rather than IGenericDAO, since need to downcast anyways...
		AgreementDAO agreementDAO = new AgreementDAO(this.projectName, this.wallName);
		
		List<Agreement> listOfAgreements = agreementDAO.getAgreementsForOptions();
		
		for(Agreement agreement: listOfAgreements)
			mapOfOptions.get(Integer.valueOf(agreement.getOptionAgreedTo().getId())).addAgreement(agreement);
		
	}

	private class OptionMapper implements RowMapper<Option>
	{
		@Override
		public Option mapRow(ResultSet rs, int rowNumber) throws SQLException {

			Issue issue = new Issue(rs.getInt("iss_id"));
			issue.setWinCondition(new WinCondition(rs.getInt("wc_id")));
			
			User author = new User(rs.getString("email"));
			author.setFirstName(rs.getString("firstname"));
			author.setLastName(rs.getString("lastName"));
			author.setNickname(rs.getString("nickname"));
			author.setAvatarURL(rs.getString("avatar"));
			
			Option option = new Option(author,issue,rs.getString("optionDetail"));
			option.setId(rs.getInt("opt_id"));
			option.setTimestamp(rs.getTimestamp("timestamp"));
			option.setStatus(rs.getString("status"));
			
			return option;
		}
		
	}

	
	

}///:~
