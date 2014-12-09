package com.winbook.layer.Resources;

import org.restlet.data.Status;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import com.winbook.DTO.ItemScoreDTO;
import com.winbook.layer.controller.WinbookConfig;
import com.winbook.layer.dataaccess.ItemScoreDAO;

public class PriorityResource extends BaseResource {

	private String itemType;
	private int itemId;
	private ItemScoreDAO dao;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		itemId = Integer.parseInt(getRequestAttributes().get("itemId").toString());
		itemType = getRequestAttributes().get("itemType").toString();
		dao = WinbookConfig.getSpringWebApplicationContext().getBean("ItemScoreDAO", ItemScoreDAO.class);
	}

	@Put
	public void updatePriorityScoreForItem(ItemScoreDTO dto)
	{
		if(dao.updateScore(dto, itemType))
			setStatus(Status.SUCCESS_NO_CONTENT);
		else
			setStatus(Status.CLIENT_ERROR_GONE,"The item may have been deleted");
	}
	
}
