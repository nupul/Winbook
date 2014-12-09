/**
 * 
 */
package com.winbook.representations;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import com.winbook.layer.controller.WinbookConfig;

/**
 * @author Nupul
 *
 */
public class RepresentationFactory {

	public static RepresentationFactory getInstance()
	{
		return WinbookConfig.getSpringWebApplicationContext().getBean(RepresentationFactory.class);
	}
	//Get a login page without any message being set.
	public TemplateRepresentation getLoginRepresentation(Request request)
	{
		
		return getLoginRepresentation(request, null);
	}
	
	//If you wish to set a default message to be displayed on the login page
	public TemplateRepresentation getLoginRepresentation(Request request, String message)
	{
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("baseRef", request.getRootRef());
		data.put("resourceRef", request.getResourceRef());
		
		if(message!=null)
			data.put("message",message);
		
		return new TemplateRepresentation("LoginPage.ftl", WinbookConfig.getFreemarkerConfiguration(),data,MediaType.TEXT_HTML);
		
	}
	
	public TemplateRepresentation getProjectListForUserRepresentation(String username)
	{
		ProjectListRepresentation repr = new ProjectListRepresentation();
		return repr.getListOfProjectsForUserRepresentation(username);
	}
	
}
