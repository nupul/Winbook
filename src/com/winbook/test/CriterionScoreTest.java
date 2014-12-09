package com.winbook.test;

import static org.junit.Assert.*;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.winbook.DTO.CriterionScoreDTO;
import com.winbook.layer.dataaccess.CriterionScoreDAO;

import static org.junit.Assert.*;

public class CriterionScoreTest {

	private CriterionScoreDAO dao;
	private int itemId = 28, criterionId = 1;
	
	@Before
	public void setUp() throws Exception {
		DataSource dataSource = new MysqlDataSource();
		
		((MysqlDataSource)dataSource).setUser("nupul");
		((MysqlDataSource)dataSource).setPassword("vfE9YIFE");
		((MysqlDataSource)dataSource).setServerName("localhost");
		((MysqlDataSource)dataSource).setPortNumber(3306);
		((MysqlDataSource)dataSource).setDatabaseName("winbookdb");
		
		dao = new CriterionScoreDAO();
		dao.setDataSource(dataSource);
		dao.setItemTypeForScoring("mmf");
	}
	
	
	@Test
	public void testGetScore(){
		
		CriterionScoreDTO dto = dao.find(itemId, criterionId);
		assertNotNull(dto);
		WorkingScoreDTO.setDto(dto);
		
	}
	
	@Test
	public void testupdateScore() {
	
		int score = 1000;
		CriterionScoreDTO dto = WorkingScoreDTO.getDto();
		dto.setScore(score);
		assertTrue(dao.updateScore(dto));
	}

	//@Test
	public void addNewScore() {
		
		int score = 101;
		int id = 36;
		CriterionScoreDTO dto = new CriterionScoreDTO();
		dto.setCriterionId(criterionId);
		dto.setScore(score);
		dto.setItemId(id);
		dto.setRevision(1);
		
		dao.addScore(dto);
		
		assertNotNull(dao.find(id, criterionId));
		assertTrue(dao.find(id, criterionId).getScore() == score);
		
	}

	
	private static class WorkingScoreDTO
	{
		private static CriterionScoreDTO dto;

		public static CriterionScoreDTO getDto() {
			return dto;
		}

		public static void setDto(CriterionScoreDTO dto) {
			WorkingScoreDTO.dto = dto;
		}
		
	}
}
