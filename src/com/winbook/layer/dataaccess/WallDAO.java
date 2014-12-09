package com.winbook.layer.dataaccess;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WallDAO extends JdbcDaoSupport {

	public boolean wallExists(String projectName, String wallName)
	{
		String sql = "Select count(*) from wall where project_title = ? and wall_name = ?";
		int wallCount = getJdbcTemplate().queryForInt(sql, projectName, wallName);
		if(wallCount==0)
			return false;
		else
			return true;
	}
	
}
