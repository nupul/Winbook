package com.winbook.layer.dataaccess.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.winbook.DTO.GoalDTO;

public class GoalMapper extends SuccessSliderRowMapper implements RowMapper<GoalDTO> {

	@Override
	public GoalDTO mapRow(ResultSet rs, int line) throws SQLException {
		GoalDTO dto = new GoalDTO();
		load(rs, dto);
		dto.setForPrioritization(rs.getBoolean("isForPrioritization"));
		dto.setDetails(rs.getString("details"));
		
		return dto;
	
	}
	
	

}
