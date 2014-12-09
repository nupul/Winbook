package com.winbook.layer.Resources;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.POJONode;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.winbook.DTO.NegotiationProgressDTO;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.NegotiationProgressDAO;

public class AnalyticsResource extends BaseResource {

	@Get
	public NegotiationProgressDTO getNegotiationHistory(){
		
		NegotiationProgressDAO dao = WinbookConfig.getSpringWebApplicationContext().getBean("NegotiationHistory", NegotiationProgressDAO.class);
		NegotiationProgressDTO dto = dao.getNegotiationProgress(getProjectName(), getWallName());
		
		return dto; 
	}
	
}
