package com.winbook.layer.dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.winbook.DTO.GoalDTO;
import com.winbook.layer.dataaccess.mappers.GoalMapper;

public class GoalDAO extends JdbcDaoSupport{
	
	public List<GoalDTO> getGoalsForProject(String projectName, String wallName, String type)
	{
		String sql = "Select * from goal where project_title = ? and wall_name = ? and type = ?"
					+" ORDER BY timestamp desc";
		
		List<GoalDTO> goals;
		try {
			goals = getJdbcTemplate().query(sql, new GoalMapper(), projectName, wallName, type);
		} catch (EmptyResultDataAccessException e) {
			goals = new ArrayList<GoalDTO>(); //return empty collection
		}
		
		return goals;
	}
	
	public boolean updateGoal(GoalDTO goal)
	{
		String sql = "UPDATE goal SET title = ?, weight = ? , isForPrioritization = ?, details = ?, revision = revision + 1"
					+" WHERE revision = ? and goal_id = ?";
		
		int numRows = getJdbcTemplate().update(sql, goal.getTitle(), goal.getWeight(), goal.isForPrioritization(), goal.getDetails(), goal.getRevision(), goal.getId());
		
		if(numRows==0)
			return false;
		
		return true;
	}

	public GoalDTO findById(int goalId)
	{
		String sql = "Select * from goal where goal_id = ?";
		
		return getJdbcTemplate().queryForObject(sql, new GoalMapper(), goalId);
	}
	
	public GoalDTO addGoal(GoalDTO goal, String projectName, String wallName)
	{
		SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate());
		insert.withTableName("goal").usingColumns("project_title", "wall_name", "title", "weight", "isForPrioritization", "details").usingGeneratedKeyColumns("goal_id");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("project_title", projectName);
		params.put("wall_name", wallName);
		params.put("title", goal.getTitle());
		params.put("weight", Integer.valueOf(goal.getWeight()));
		params.put("isForPrioritization", Boolean.valueOf(goal.isForPrioritization()));
		params.put("details", goal.getDetails());
		
		Number goalId = insert.executeAndReturnKey(params);
		
		goal.setId(goalId.intValue());
		
		return goal;
		
	}
	
	public boolean deleteGoal(int goalId)
	{
		String sql = "DELETE FROM goal where goal_id = ?";
		int numRows = getJdbcTemplate().update(sql, Integer.valueOf(goalId));
		
		if(numRows == 0)
			return false;
		
		return true;
	}
	
}
