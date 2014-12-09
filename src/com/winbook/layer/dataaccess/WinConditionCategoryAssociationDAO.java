package com.winbook.layer.dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.winbook.domainobjects.Category;
import com.winbook.domainobjects.WinCondition;
import com.winbook.domainobjects.WinbookConstants;
import com.winbook.exceptions.MissingOrIncorrectDataException;
import com.winbook.exceptions.TooLongNameException;

/*
 * DAO for updating the association between the win conditions and the corresponding categeories in the DB. This DAO is required since 
 * it's a many to many relation between win conditions and categories. This class acts as an "Association Table Mapper" 
 * as per Patterns of Enterprise Architecture, Fowler.
 */
public class WinConditionCategoryAssociationDAO extends BasicWinBookDAO{
	
	/*
	 * This method assumes that the list of win conditions is initialized with the category(s) the wincondition needs to be added to.
	 * So for each win condition multiple categories could be provided. And you need to insert multiple win conditions to update the relation/association
	 * Basically a nested list structure. The upper layers must provide data in this fashion else an exception will be thrown
	 */

	private JdbcTemplate jdbcTemplate;
	
	public WinConditionCategoryAssociationDAO()
	{
		jdbcTemplate = new JdbcTemplate(getDataSource());
	}
	
	public int addWinConditionsToCategory(List<WinCondition> listOfWinConditions) {
		
		//Ignore null/zero size lists and return 0: Implies 0 rows updated. No need to throw exceptions in this case.
		if(listOfWinConditions==null || listOfWinConditions.size()==0)
			return 0;
		
		String sqlInsertTemplate = " INSERT IGNORE INTO wc_category (wc_id, category_id) VALUES ";
		StringBuffer sqlInsert = new StringBuffer(sqlInsertTemplate);
		int numRowsInserted = 0;
		
		//Build insert string for multiple inserts
		for(WinCondition wc: listOfWinConditions)
		{
			if(wc.getCategories()!=null && wc.getCategories().size()>0)
				for(Category category: wc.getCategories())
				{
					sqlInsert.append(getInsertionSQLSubString(wc.getId(), category.getId()));
					numRowsInserted++;
				}
		}
		
		//Getting rid of the final comma as appended by getInsertRelationString
		sqlInsert.deleteCharAt(sqlInsert.length()-1);
		
		numRowsInserted = jdbcTemplate.update(sqlInsert.toString());
		
		return numRowsInserted;
	}
	
	/*
	 * Updating the categorization of a win condition is as good as deleting some categories and adding the rest. So only delete is provided
	 * for now since simultaneous deletion/addition is not yet enabled. You can only do it one at a time, so to speak.
	 */
	public int deleteWinConditionsFromCategory(List<WinCondition> listOfWinConditions)
	{
		//Ignore null and return 0 --> 0 rows updated. No need to throw exceptions in this case.
		if(listOfWinConditions==null || listOfWinConditions.size()==0)
			return 0;
		
		int numRowsDeleted = 0;
		String sqlDeleteTemplate = " DELETE FROM wc_category WHERE ";
		StringBuffer sqlDelete = new StringBuffer(sqlDeleteTemplate);
		
		for(WinCondition wc: listOfWinConditions)
		{
			if(wc.getCategories()!=null && wc.getCategories().size()>0)
				for(Category category: wc.getCategories())
				{
					sqlDelete.append(getDeletionSQLSubstring(wc.getId(), category.getId()));
					numRowsDeleted++;
				}
		}
		
		/*
		 * Deleting the last unnecessary "OR" that got appended as a result of looping (and by construction of string)
		 * Arbitrarily taking the last 5 (and not exacting it to 3) characters just to be safe when searching for the last occurrence of "OR"
		 */
		
		sqlDelete.delete(sqlDelete.lastIndexOf("OR"), sqlDelete.length());
		
		numRowsDeleted = jdbcTemplate.update(sqlDelete.toString());
		
		return numRowsDeleted;
	}
	

	/**
	 * @param listOfWinConditions
	 */
	public void getCategoriesForWinConditions(List<WinCondition> listOfWinConditions, String projectName, String wallName) {
		/*
		 * -fetch all categories for corresponding WCs in project/wall
		 * -get all data as SqlRowSet
		 * -iterate through SqlRowSet and add categories to list for each WC in map using getListOfCategoriesForEachWinCondition(...)
		 * -for each WC in map add list to listOfWinConditions
		 */
		
		//Convert current list of win conditions into a map for easy access of win condition by id
		Map<Integer, WinCondition> mapOfWinConditions = new HashMap<Integer, WinCondition>();
		for(WinCondition wc: listOfWinConditions)
			mapOfWinConditions.put(Integer.valueOf(wc.getId()), wc);
		
		String sql = " select wc_id, category_id, name, color from category"
					+" join wc_category using(category_id)"
					+" join wincondition w using (wc_id)"
					+" where w.project_title = ? and w.wall_name = ?"
					+" order by wc_id asc";	
		
		Map<Integer, List<Category>> mapOfCategoriesForEachWinCondition = null;
		
		try {
			mapOfCategoriesForEachWinCondition = getListOfCategoriesForEachWinCondition(jdbcTemplate.queryForRowSet(sql, projectName, wallName));
			
			//iterate over collection and add list of categories to corresponding win conditions
			for(Map.Entry<Integer, List<Category>> entry: mapOfCategoriesForEachWinCondition.entrySet())
			{	
				if(mapOfWinConditions.containsKey(entry.getKey()))
					mapOfWinConditions.get(entry.getKey()).setCategories(entry.getValue());
			}
			
		} catch (EmptyResultDataAccessException e) {
			//Implies no categorization exists for win conditions. Valid operation
			mapOfCategoriesForEachWinCondition = null;
		}
		
	}
	
	/*
	 * Overloaded method for convenience when having only a single win condition to fetch categories for
	 */
	public void getCategoriesForWinConditions(WinCondition winCondition, String projectName, String wallName) {
		List<WinCondition> wc = new ArrayList<WinCondition>(1);
		wc.add(winCondition);
		/*
		 * This is a wasteful way of doing. The query should be specific to that WC else the called methods fetches all categories and iterates
		 * through it. The above method is NOT optimized for a single win condition since it assumes a list of winconditions is ALREADY
		 * PRESENT When it is fetching their categories 
		 */
		
		getCategoriesForWinConditions(wc, projectName, wallName);
		
	}
	
	/*
	 * Parse SqlRowSet for getting categories for each win condition and add them to a list - one for each WC
	 */
	private Map<Integer, List<Category>> getListOfCategoriesForEachWinCondition(SqlRowSet srs)
	{
		Map<Integer, List<Category>> result = new HashMap<Integer, List<Category>>();
		while(srs.next())
		{
			Integer wcId = Integer.valueOf(srs.getInt("wc_id"));
			
			if(!result.containsKey(wcId))
				result.put(wcId, new ArrayList<Category>());
			
			Category c = new Category(srs.getInt("category_id"));
			c.setHexColorCode(srs.getString("color"));
			c.setCategoryName(srs.getString("name"));
			
			result.get(wcId).add(c);
		}
		
		return result;
	}
	
	/*
	 * Creates a string of the type '(wc_id, category_id),' to be appended to the sql insert statement for multiple inserts
	 * Note comma at the end of string - used by SQL to concatenate multiple insertions into a single query
	 */
	private String getInsertionSQLSubString(int winConditionId, int categoryId)
	{		
		String values = " ("+Integer.toString(winConditionId)+", "+Integer.toString(categoryId)+"),";
		return values;
	}
	
	/*
	 * Creates string of type '(wc_id = 2 AND category_id = 3) or' where 2, 3 are the corresponding ids.
	 * Note the 'OR' at the end of the string. Used to combine multiple deletes.
	 */
	private String getDeletionSQLSubstring(int winConditionId, int categoryId)
	{
		String whereClause = " (wc_id = "+Integer.toString(winConditionId)+" AND "+"category_id = "+Integer.toString(categoryId)+") OR";		
		return whereClause;
	}
	

}///:~ End class
