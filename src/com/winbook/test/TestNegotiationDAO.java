package com.winbook.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.winbook.DTO.NegotiationProgressDTO;
import com.winbook.layer.dataaccess.NegotiationProgressDAO;

public class TestNegotiationDAO extends BaseTest {

	private NegotiationProgressDAO dao;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
		dao = new NegotiationProgressDAO();
		dao.setDataSource(getDataSource());
	}
	
	@Test
	public void testDAO() {
		
		NegotiationProgressDTO dto = dao.getNegotiationProgress("2012-Project03","wall");
		assertTrue("No negotiation history for project. Possibly project doesn't exist", dto.getProgress().size()>0);
		
	}

}
