package com.winbook.layer.controller;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import freemarker.template.Configuration;

/*
 * This class initializes and caches the JDBC DataSource that needs to be used by the Data Access Objects (DAOs). 
 * Called by BasicWinConditionDAO which is the abstract base class for all DAOs.
 */
public class WinbookConfig implements ServletContextListener {

	private static DataSource datasource;
	private static Configuration configuration;
	private static String resourcePath;
	private static WebApplicationContext springWebApplicationContext;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		ServletContext sc = event.getServletContext();
		//Load freemarker configuration with location of templates as per "freemarkerTemplateLocation" attribute in web.xml
		String freemarkerTemplateLocation = sc.getInitParameter("freemarkerTemplateLocation");		
		configuration = new Configuration();
		configuration.setServletContextForTemplateLoading(sc, freemarkerTemplateLocation);
		
		//cache the WebApplicationContext for instantiating/using Spring's IoC container
		springWebApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sc);
		
//		String databaseName = sc.getInitParameter("databaseName");
//		try {
//			InitialContext ctx = new InitialContext();
//			datasource = (DataSource) ctx.lookup(databaseName);
//			
//		} catch (NamingException e) {
//			String error = "Datasource not found in ServletContext in web.xml Ex.:" +
//							"<context-param>\n" +
//									"<param-name>databaseName</param-name>\n" +
//									"<param-value>jdbc/mysqldb</param-value>\n" +
//							"</context-param>";
//			throw new RuntimeException(error);
//		}

	}
	
	/*
	 * Returns an instance of freemarker's configuration with location of the the templates folders. Preferably in /WEB-INF or it's sub-folders.
	 */
	public static Configuration getFreemarkerConfiguration()
	{
		return configuration;
	}
	
	/*
	 * Returns a cached instance of the datasource.
	 */
//	public static DataSource getJDBCDataSource()
//	{
//		return datasource;
//	}
	
	

	public static WebApplicationContext getSpringWebApplicationContext() {
		return springWebApplicationContext;
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// NOOP
	}
	

}
