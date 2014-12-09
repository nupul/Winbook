package com.winbook.layer.Resources;

import org.restlet.resource.ResourceException;

public class WinConditionScoreResource extends CriterionScoreResource {

	private int wcId, criterionId;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		wcId = Integer.parseInt(getRequestAttributes().get("wcid").toString());
		criterionId = Integer.parseInt(getRequestAttributes().get("id").toString());
	}
	
	
	
}
