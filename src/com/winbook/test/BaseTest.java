package com.winbook.test;

import static org.junit.Assert.*;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.winbook.layer.dataaccess.CriterionScoreDAO;

public class BaseTest {

	private DataSource dataSource;

	public void setUp() throws Exception {
		dataSource = new MysqlDataSource();
		
		((MysqlDataSource)dataSource).setUser("nupul");
		((MysqlDataSource)dataSource).setPassword("vfE9YIFE");
		((MysqlDataSource)dataSource).setServerName("localhost");
		((MysqlDataSource)dataSource).setPortNumber(3306);
		((MysqlDataSource)dataSource).setDatabaseName("winbookdb");
		
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	
	
}
