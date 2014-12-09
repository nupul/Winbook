package com.winbook.layer.dataaccess;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.winbook.layer.controller.WinbookConfig;

/*
 * An abstract base class that sets up the connection and datasource to be used for accessing the database. Used by all DAOs of WinBook.
 * A central class that should access a cached copy of the datasource to be used for accessing the DB. Ideally fetching from ServletContext in a J2EE environment.
 */
public abstract class BasicWinBookDAO {
	
	private DataSource dataSource;
	
	public BasicWinBookDAO()
	{
//		datasource = new MysqlDataSource();
//		
//		((MysqlDataSource)datasource).setUser("root");
//		((MysqlDataSource)datasource).setPassword("Loveme247");
//		((MysqlDataSource)datasource).setServerName("localhost");
//		((MysqlDataSource)datasource).setPortNumber(3306);
//		((MysqlDataSource)datasource).setDatabaseName("mydb");
		
		dataSource = WinbookConfig.getSpringWebApplicationContext().getBean("dataSource", DataSource.class);
		
	}
	
	public DataSource getDataSource()
	{
		return dataSource;
	}
	
	public void setDataSource(DataSource datasource)
	{
		this.dataSource = datasource;
	}

}
