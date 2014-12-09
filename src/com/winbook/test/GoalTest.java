package com.winbook.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.winbook.DTO.GoalDTO;
import com.winbook.layer.dataaccess.GoalDAO;

public class GoalTest extends BaseTest{

	private GoalDAO dao;
	private String projectName, wallName;
	
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		dao = new GoalDAO();
		dao.setDataSource(getDataSource());
		projectName = "myproject";
		wallName = "wall";
	}
	
	@Test
	public void addNewGoal() {

		GoalDTO dto = new GoalDTO();
		dto.setTitle("B - Quality");
		dto.setForPrioritization(true);
		dto.setDetails(null);
		dto.setWeight(10);
		
		dao.addGoal(dto, projectName, wallName);
		
		assertTrue(dto.getId()>0);
		assertTrue(dto.getRevision() ==1);
	
		WorkingGoalDTO.setGoalDTO(dto);
	}

	@Test 
	public void updateGoal() {
		GoalDTO dto = WorkingGoalDTO.getGoalDTO();
		dto.setTitle("Simba");
		dto.setWeight(90);
		dto.setForPrioritization(false);
		dto.setDetails("here are some details");
		
		assertTrue(dao.updateGoal(dto));
		
	}
	
	@Test 
	public void readGoal(){
		int id = WorkingGoalDTO.getGoalDTO().getId();
		
		GoalDTO dto = dao.findById(id);
		
		assertTrue(dto.getId()>0);
		assertTrue(dto.getWeight()>0);
		assertTrue(dto.getTitle().equalsIgnoreCase("simba"));
		assertTrue(dto.getDetails().length()>10);
		
	}
	
	@Test
	public void getAllGoalsForProjectWall(){
		List<GoalDTO> dtoList = dao.getGoalsForProject(projectName, wallName, "business");
		
		assertTrue(dtoList.size() >= 1);
		
		for(GoalDTO dto : dtoList)
		{
			assertTrue(dto.getId()>0);
			assertTrue(dto.getTitle().length()>=5);
			assertTrue(dto.getWeight()>0);
			assertTrue(dto.getDetails()==null || dto.getDetails().length()>10);
		}
		
	}
	
	
	@Test
	public void deleteGoal(){
		assertTrue(dao.deleteGoal(WorkingGoalDTO.getGoalDTO().getId()));
	}
	
	private static class WorkingGoalDTO
	{
		private static GoalDTO goalDTO;

		public static GoalDTO getGoalDTO() {
			return goalDTO;
		}

		public static void setGoalDTO(GoalDTO goalDTO) {
			WorkingGoalDTO.goalDTO = goalDTO;
		}
		
		
	}
	
	
}
