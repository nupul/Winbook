package com.winbook.layer.dataaccess;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.winbook.DTO.NegotiationProgressDTO;

public class NegotiationProgressDAO extends JdbcDaoSupport {

	public NegotiationProgressDTO getNegotiationProgress(String projectName, String wallName){
		NegotiationProgressDTO dto = new NegotiationProgressDTO();
		
		String sql = " select datediff(now(),`timestamp`) as `day`, `status`, count(distinct wc_id) as count"
					+" from wc_audit_log where project_title = ? and wall_name = ? group by 1,2 order by `day` desc, `status` asc";
		
		SqlRowSet srs = getJdbcTemplate().queryForRowSet(sql, projectName, wallName);
		
		while(srs.next())
		{
			int day = srs.getInt("day");
			String status = srs.getString("status");
			int count = srs.getInt("count");
			
			dto.setStatusCount(day, status, count);
			
		}
		//dto.computeCumulativeSum();
		return dto;
	}
	
	
	
}
