package com.winbook.layer.dataaccess.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.winbook.DTO.SuccessSliderDTO;

/*
 * The Goal Table in the DB is the only one that is represented as a different resource
 * at different points in the application. It's either a criterion for prioritization
 * or a business/technical goal for the project.
 * 
 * The criteria require to have a direction of preference (isMaxBetter) where as goals
 * may/not be marked for prioritization (isForPrioritization) and may have additional details
 * which are not required by the criteria. Thus the resource is 'partially' accessible
 * to the clients depending on their visualization. Hence the common parts are isolated
 * into this base class which provides a mapper (protected) to be used by the children
 * reducing duplication.
 * 
 * This class doesn't need to implement RowMapper<T> but could be and type casted accordingly
 * to cater for the difference in types.
 */
public abstract class SuccessSliderRowMapper{
	
	protected void load(ResultSet rs, SuccessSliderDTO dto) throws SQLException
	{
		dto.setId(rs.getInt("goal_id"));
		dto.setTitle(rs.getString("title"));
		dto.setWeight(rs.getInt("weight"));
		dto.setRevision(rs.getInt("revision"));
	}
	
}
