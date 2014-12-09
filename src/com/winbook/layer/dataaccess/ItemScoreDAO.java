package com.winbook.layer.dataaccess;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.winbook.DTO.ItemScoreDTO;

public class ItemScoreDAO extends JdbcDaoSupport {

	
	public boolean updateScore(ItemScoreDTO dto, String itemType)
	{
		String tableName = null, 
				itemPriorityColName = null, 
				itemIdColName = null;
		
		if(itemType.equalsIgnoreCase("wincondition"))
		{
			tableName = "wincondition";
			itemPriorityColName = "priority";
			itemIdColName = "wc_id";
		}
		else if(itemType.equalsIgnoreCase("MMF"))
		{
			tableName = "category";
			itemPriorityColName = "MMF_priority";
			itemIdColName = "category_id";
		}
		
		String sql = "UPDATE "+tableName+" SET "+itemPriorityColName+" = ? "
					+" WHERE "+itemIdColName+" = ?";
		
		int numRows = getJdbcTemplate().update(sql, dto.getFinalScore(), dto.getId());
		
		if(numRows == 0)
			return false;
		else
			return true; 
	}
	
}
