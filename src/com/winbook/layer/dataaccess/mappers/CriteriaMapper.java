package com.winbook.layer.dataaccess.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.winbook.DTO.CriterionDTO;

public class CriteriaMapper extends SuccessSliderRowMapper implements RowMapper<CriterionDTO> 
{
	@Override
	public CriterionDTO mapRow(ResultSet rs, int line)
			throws SQLException {
		
		CriterionDTO dto = new CriterionDTO();
		load(rs, dto);
		dto.setMaxBetter(rs.getBoolean("isMaxBetter"));
		return dto;
	}
}
