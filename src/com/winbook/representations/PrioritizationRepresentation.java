package com.winbook.representations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;

import com.winbook.DTO.CriterionDTO;
import com.winbook.layer.controller.WinbookConfig;

public class PrioritizationRepresentation {

	
	public TemplateRepresentation getPrioritizationTemplate(List<CriterionDTO> listOfCriteria)
	{
		Map<String, Object> data = new HashMap<String, Object>(1);
		
		data.put("criteria", listOfCriteria);
		
		return new TemplateRepresentation("Prioritization.ftl", WinbookConfig.getFreemarkerConfiguration(), data, MediaType.TEXT_HTML);
		
	}
	
}
