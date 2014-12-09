/**
 * 
 */
package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.winbook.DTO.ProjectVisionDTO;
import com.winbook.domainobjects.Project;
import com.winbook.domainobjects.User;

/**
 * @author Nupul
 *
 */
public class ProjectDAO extends JdbcDaoSupport{


	public User populateProjectMembershipDetailsForUser(User user) {
		String sql = " select distinct u.email, project_title, project_subtitle from `user` u"
					+" right join user_memberof_project ump on u.email = ump.email and u.email=?"
					+" right join project using(project_title)"
					+" where isOpen = true"
					+" order by u.email desc, project_title asc";
		
		SqlRowSet listOfProjectsForUser = getJdbcTemplate().queryForRowSet(sql, user.getEmailId());
		
		//scan through entire list and categorize the projects based on membership of user...
		addProjectMembershipDetailsToUser(listOfProjectsForUser, user);
		
		return user;
	}

	/*
	 * Iterate through the retrieved rowset and check if a particular project is associated to the user or not. Categorize membership accordingly.
	 * The resultSet is in the following format:
	 * 
	 * (email, project_title, project_subtitle)
	 * 
	 * The above query results in such a result set. If email == null (or empty "") it implies that user is NOT member of that project, else is :)
	 */
	private void addProjectMembershipDetailsToUser(SqlRowSet srs, User user) {
		
		List<Project> listOfProjectsUserMemberOf = new ArrayList<Project>();
		List<Project> listOfProjectsUserNotMemberOf = new ArrayList<Project>();
		
		Map<String, Project> membershipMap = new HashMap<String, Project>();
		
		while(srs.next())
		{
			String email = srs.getString("email");
			Project project = new Project(srs.getString("project_title"));
			project.setSubtitle(srs.getString("project_subtitle"));
			
			if(email==null || email=="")
			{
				if(!membershipMap.containsKey(project.getName()))	
					listOfProjectsUserNotMemberOf.add(project);
			}
			else
			{
				listOfProjectsUserMemberOf.add(project);
				membershipMap.put(project.getName(), project);
			}
		}
		
		user.setProjectsMemberOf(listOfProjectsUserMemberOf);
		user.setProjectsNotMemberOf(listOfProjectsUserNotMemberOf);
		
	}
	
	public boolean projectExists(String projectName)
	{
		String sql = " select count(*) from project where project_title = ?";
		int projectCount = getJdbcTemplate().queryForInt(sql, projectName);
		
		if(projectCount==0)
			return false;
		else
			return true;
	}

	/*
	 * Currently adding method to update vision. Ideally should be for whole project.
	 */
	public boolean updateVision(String projectName, ProjectVisionDTO dto) {
		String sql = "UPDATE project SET vision = ? WHERE project_title = ?";
		int numRows = getJdbcTemplate().update(sql, dto.getVision(), projectName);
		
		if(numRows==0)
			return false;

		return true;
	}

	public ProjectVisionDTO getVision(String projectName) {
		String sql = "select * from project where project_title = ?";
		return getJdbcTemplate().queryForObject(sql, new VisionMapper(), projectName);
	}
	
	private class VisionMapper implements RowMapper<ProjectVisionDTO>
	{
		@Override
		public ProjectVisionDTO mapRow(ResultSet rs, int line) throws SQLException {
			ProjectVisionDTO dto = new ProjectVisionDTO();
			dto.setVision(rs.getString("vision"));
			return dto;
		}
		
	}
	
	
}
