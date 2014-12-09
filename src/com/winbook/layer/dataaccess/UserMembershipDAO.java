/**
 * 
 */
package com.winbook.layer.dataaccess;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * @author Nupul
 * Class responsible for updating the membership details for a particular user - basically add/delete. Modify doesn't make sense in such a scenario since there are no other 
 * attributes associated with the membership as yet.
 */
public class UserMembershipDAO {
	private SimpleJdbcInsert simpleInsert;
	
	public void setDataSource(DataSource dataSource)
	{
		simpleInsert = new SimpleJdbcInsert(dataSource).withTableName("user_memberof_project").usingColumns("email","project_title");
	}

	public boolean addMembership(String userEmail, String projectName) {

		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("email", userEmail);
		params.put("project_title", projectName);
		
		int numRowsUpdated = simpleInsert.execute(params);
		
		if(numRowsUpdated==0)
			return false;
		
		return true;
	}
}
