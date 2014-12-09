package com.winbook.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.winbook.DTO.CriterionDTO;
import com.winbook.layer.dataaccess.CriteriaDAO;

public class CriterionTest extends BaseTest{

	private CriteriaDAO dao;

	@Before
	public void setUp() throws Exception{
		super.setUp();
		dao = new CriteriaDAO();
		dao.setDataSource(getDataSource());
	}
	

	
}
