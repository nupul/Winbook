package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.winbook.DTO.CriterionDTO;
import com.winbook.layer.dataaccess.mappers.CriteriaMapper;

public class CriteriaDAO extends JdbcDaoSupport {
	
	
	public List<CriterionDTO> getPrioritizationCriteria(String projectName, String wallName, String type)
	{
		String sql = "SELECT goal_id, title, weight, revision, isMaxBetter "
					+" FROM goal WHERE project_title = ? and wall_name = ? and type = ? and isForPrioritization = true"
					+" ORDER BY goal_id ASC";
		
		List<CriterionDTO> criteria= getJdbcTemplate().query(sql, new CriteriaMapper(), projectName, wallName, type);
		
		return criteria;
	}
	
	
	public boolean updatePrioritizationCriterion(CriterionDTO criterion)
	{
		String sql = "UPDATE goal SET weight = ?, isMaxBetter = ?, title = ?, revision = revision + 1 "
					+" WHERE revision = ? and goal_id = ?";
		
		int numRows = getJdbcTemplate().update(sql, criterion.getWeight(), criterion.isMaxBetter(), criterion.getTitle(), criterion.getRevision(), criterion.getId());
		
		if(numRows==0)
			return false;
		
		return true;
	}
	

	public CriterionDTO findById(int criterionId) {
		
		String sql = "SELECT goal_id, title, weight, revision, isMaxBetter "
					+" FROM goal WHERE goal_id = ?";
		
		return getJdbcTemplate().queryForObject(sql, new CriteriaMapper(), Integer.valueOf(criterionId));
		
	}
	
//	private class CriteriaMapper implements RowMapper<CriterionDTO>
//	{
//
//		@Override
//		public CriterionDTO mapRow(ResultSet rs, int line)
//				throws SQLException {
//			
//			CriterionDTO dto = new CriterionDTO();
//			dto.setId(rs.getInt("goal_id"));
//			dto.setTitle(rs.getString("title"));
//			dto.setWeight(rs.getInt("weight"));
//			dto.setRevision(rs.getInt("revision"));
//			dto.setMaxBetter(rs.getBoolean("isMaxBetter"));
//			
//			return dto;
//		}
//		
//	}
	//public CriterionDTO find



}
