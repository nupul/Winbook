package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.winbook.DTO.CriterionScoreDTO;
import com.winbook.layer.controller.WinbookConfig;

public class CriterionScoreDAO extends JdbcDaoSupport{
	
	private String tableName, itemIdColName;
	
	public void setItemTypeForScoring(String itemType)
	{
		if(itemType.equalsIgnoreCase("wincondition"))
		{
			tableName = "goal_wincondition_score";
			itemIdColName = "wc_id";
		}
		else if(itemType.equalsIgnoreCase("MMF"))
		{
			tableName = "goal_category_score";
			itemIdColName = "category_id";
		}
			
	}
	
	public CriterionScoreDTO find(int itemId, int criterionId){
		String sql = "Select * from "+tableName+" where goal_id = ? and "+itemIdColName+" = ?";
		
		CriterionScoreDTO dto = getJdbcTemplate().queryForObject(sql, new CriterionScoreRowMapper(itemIdColName), Integer.valueOf(criterionId), Integer.valueOf(itemId));
		
		return dto; 
		
	}

	public boolean updateScore(CriterionScoreDTO criterionScore) {
		
		String sql = "Update "+tableName+" set revision = revision +1, score = ? where goal_id = ? and revision = ? and "+itemIdColName+" = ? ";
		int numRows = getJdbcTemplate().update(sql, Integer.valueOf(criterionScore.getScore()), criterionScore.getCriterionId(), criterionScore.getRevision(), criterionScore.getItemId());
		
		if(numRows==0)
			return false;
		
		return true;
		
	}

	public void addScore(CriterionScoreDTO criterionScore) {
		
		String sql = "insert into "+tableName+"(goal_id, "+itemIdColName+",score) values (?,?,?)";
		getJdbcTemplate().update(sql, criterionScore.getCriterionId(), criterionScore.getItemId(), criterionScore.getScore());
	}
	
	private class CriterionScoreRowMapper implements RowMapper<CriterionScoreDTO>
	{

		private String idColName;
		public CriterionScoreRowMapper(String idColName)
		{
			this.idColName = idColName;
		}
		
		@Override
		public CriterionScoreDTO mapRow(ResultSet rs, int line) throws SQLException {
			CriterionScoreDTO dto = new CriterionScoreDTO();
			dto.setItemId(rs.getInt(this.idColName));
			dto.setCriterionId(rs.getInt("goal_id"));
			dto.setScore(rs.getInt("score"));
			dto.setRevision(rs.getInt("revision"));
			return dto;
		}
		
	}
	
}///:~
