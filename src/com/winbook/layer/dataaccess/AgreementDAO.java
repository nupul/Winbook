/**
 * 
 */
package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.winbook.domainobjects.Agreement;
import com.winbook.domainobjects.Option;
import com.winbook.domainobjects.User;
import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.MissingOrIncorrectDataException;

/**
 * @author Nupul
 *
 */
public class AgreementDAO extends BasicWinBookDAO implements IGenericDAO<Agreement> {

	private String projectName, wallName;
	private SimpleJdbcTemplate simpleTemplate;
	private SimpleJdbcInsert simpleInsert;
	
	
	public AgreementDAO(String projectName, String wallName)
	{
		this.projectName = projectName;
		this.wallName = wallName;
		simpleTemplate = new SimpleJdbcTemplate(getDataSource());
		simpleInsert = new SimpleJdbcInsert(getDataSource()).withTableName("agreement").usingColumns("email","wc_id","opt_id").usingGeneratedKeyColumns("ag_id");
	}
	
	@Override
	public int create(Agreement agreement) throws MissingOrIncorrectDataException {
		// TODO Auto-generated method stub
		if(agreement.getAuthor()==null || agreement.getWinConditionCovered()==null)
			throw new MissingOrIncorrectDataException("User id of stakeholder in agreement OR Win condition being covered by this agreement NOT SUPPLIED");
		
		Map<String, Object> params = new HashMap<String, Object>(3);
		
		params.put("email", agreement.getAuthor().getEmailId());
		params.put("wc_id", Integer.valueOf(agreement.getWinConditionCovered().getId()));
		
		if(agreement.getOptionAgreedTo()!=null)
			params.put("opt_id", Integer.valueOf(agreement.getOptionAgreedTo().getId()));
		
		Number id = simpleInsert.executeAndReturnKey(params);
		
		return id.intValue(); 
		
	}

	@Override
	public boolean delete(int agreementId) throws MissingOrIncorrectDataException {

		if(agreementId<=0)
			throw new MissingOrIncorrectDataException("Deleting Agreement: Agreement Id must be a valid > 0");
		
		String sql = "DELETE FROM agreement WHERE ag_id = ?";
		
		int numRowsDeleted = simpleTemplate.update(sql, Integer.valueOf(agreementId));
		
		if(numRowsDeleted==0)
			return false;
		
		return true;
	}
	
	//Gets only agreements for all win conditions. Need to add another method to get all agreements for options
	@Override
	public List<Agreement> getAllItemsForProject() {

		String sql = " select wc.wc_id, ag_id, ag.email, firstName, lastName, nickname,isShaper "
					+" from wincondition wc, agreement ag, `user` u"
					+" where wc.wc_id = ag.wc_id and wc.project_title= ? and wc.wall_name= ? and ag.opt_id IS NULL and ag.email = u.email";
		
		List<Agreement> listOfAgreementsForWinConditions = null;
		
		listOfAgreementsForWinConditions = simpleTemplate.query(sql, new WCAgreementMapper(), projectName, wallName);
		
		return listOfAgreementsForWinConditions;
	}
	
	//Not part of IGenericDAO interface.
	public List<Agreement> getAgreementsForOptions() {
		String sql = " select wc.wc_id, ag_id, ag.email, ag.opt_id ,firstName, lastName, nickname,isShaper "
					+" from wincondition wc, agreement ag, `user` u"
					+" where wc.wc_id = ag.wc_id and wc.project_title= ? and wc.wall_name= ? and ag.opt_id IS NOT NULL and ag.email = u.email";
		
		List<Agreement> listOfAgreementsForOptions = null;
		
		listOfAgreementsForOptions = simpleTemplate.query(sql, new OptionsAgreementMapper(), projectName, wallName);
		
		return listOfAgreementsForOptions;
	}

	@Override
	public Agreement getById(int agreementId)
	throws MissingOrIncorrectDataException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean update(Agreement entity)
			throws MissingOrIncorrectDataException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateList(List<Agreement> listOfEntities) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean deleteList(List<Agreement> listOfEntitites) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * Need two row mappers - one for win condition based data and one for options
	 */
	private class WCAgreementMapper implements RowMapper<Agreement>
	{
		@Override
		public Agreement mapRow(ResultSet rs, int rowNumber) throws SQLException {
			
			WinCondition wc = new WinCondition(rs.getInt("wc_id"));
			
			User stakeholderInAgreement = new User(rs.getString("email"));
			stakeholderInAgreement.setFirstName(rs.getString("firstName"));
			stakeholderInAgreement.setLastName(rs.getString("lastName"));
			stakeholderInAgreement.setNickname(rs.getString("nickname"));
			//also need to return if user is shaper. Part of query but currently ignored in row mapper
			Agreement agreement = new Agreement(stakeholderInAgreement, wc); 
			agreement.setId(rs.getInt("ag_id"));
			
			return agreement;
		}
		
	}//end WCAgreementMapper
	
	
	private class OptionsAgreementMapper extends WCAgreementMapper
	{
		@Override
		public Agreement mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Agreement agreement =  super.mapRow(rs, rowNumber);
			agreement.setOptionAgreedTo(new Option(rs.getInt("opt_id")));
			
			return agreement;
		}
		
	}
}
