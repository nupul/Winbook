package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.winbook.domainobjects.Category;
import com.winbook.domainobjects.WinbookConstants;
import com.winbook.exceptions.MissingOrIncorrectDataException;
import com.winbook.exceptions.TooLongNameException;

public class CategoryDAO extends BasicWinBookDAO implements IGenericDAO<Category> {

	private String projectName, wallName;
	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	
	public CategoryDAO(String projectName, String wallName) {
		this.projectName = projectName;
		this.wallName = wallName;
		
		jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
		jdbcInsert = new SimpleJdbcInsert(getDataSource()).withTableName("category").usingColumns("name", "color", "isMMF", "wall_name","project_title").usingGeneratedKeyColumns("category_id");
	}


	@Override
	public int create(Category category) throws MissingOrIncorrectDataException {

		if(category.getCategoryName()==null || category.getHexColorCode()==null || category.getCategoryName().length()==0 || category.getHexColorCode().length()==0)
			throw new MissingOrIncorrectDataException("Category Creation: Both color code and category name are required for creation. Either of them is missing. Check data sent to DAO");
		
		//trim the length if more than max
		if(category.getCategoryName().length()>WinbookConstants.MAX_CATEGORY_NAME_LENGTH)
			category.setCategoryName(category.getCategoryName().substring(0,WinbookConstants.MAX_CATEGORY_NAME_LENGTH));
		
		//If color value incorrectly specified, use default
		if(!Pattern.matches("#[a-fA-F0-9]{6}", category.getHexColorCode()))
			category.setHexColorCode(WinbookConstants.DEFAULT_CATEGORY_COLOR);
		
		Map<String, Object> queryParams = new HashMap<String, Object>(4); //# of fields in table to be inserted
		queryParams.put("name", category.getCategoryName());
		queryParams.put("color", category.getHexColorCode());
		queryParams.put("wall_name", wallName);
		queryParams.put("project_title", projectName);
		queryParams.put("isMMF", category.isMMF());
		
		Number categoryId = jdbcInsert.executeAndReturnKey(queryParams);
		
		return categoryId.intValue();
	
	}


	@Override
	public Category getById(int entityId) throws MissingOrIncorrectDataException {
		
		if(entityId<=0)
			throw new MissingOrIncorrectDataException("Get Category: A valid id > 0 MUST be specified");
		
		String sql = "Select category_id, name, color from category where category_id = ?";
		
		Category c = null;
		
		try {
			c = jdbcTemplate.queryForObject(sql, new CategoryRowMapper(), Integer.valueOf(entityId));
		} catch (EmptyResultDataAccessException e) {
			//NO-OP - since it's valid to have an empty result set be returned
		}
		
		return c;
	}


	@Override
	public List<Category> getAllItemsForProject() {
		
		String sql = "Select category_id, name, color, isMMF from category "
					+" where project_title = ? and wall_name = ? order by isMMF desc, name asc";
		
		List<Category> listOfCategories = jdbcTemplate.query(sql, new CategoryRowMapper(), projectName, wallName);
		
		return listOfCategories;
	}

	/*
	 * A single method to update the color as well as the name. This method detects which value is present/absent and frames the update accordingly
	 * Throws an exception if both values are missing.
	 */
	@Override
	public boolean update(Category c) throws MissingOrIncorrectDataException {

		StringBuffer sqlUpdate = new StringBuffer(" Update category set ");
		String whereClause = " where category_id = :id";
		
		SqlParameterSource queryParams = new BeanPropertySqlParameterSource(c);
		
		if(c.getId()<=0)
			throw new MissingOrIncorrectDataException("Updating Category: category id MUST be valid > 0");
		
		else if(c.getCategoryName() == null && c.getHexColorCode()==null)
			throw new MissingOrIncorrectDataException("Updating Category: Either color code or category name must be present. Both are absent. Check data sent to DAO");
		
		else if( (c.getCategoryName()==null || c.getCategoryName().length()==0) && c.getHexColorCode().length() > 0 )
			sqlUpdate.append(" color = :hexColorCode");
		
		else if( (c.getHexColorCode()==null || c.getHexColorCode().length()==0) && c.getCategoryName().length() > 0 )
			sqlUpdate.append(" name = :categoryName");
		
		else if(c.getCategoryName().length()>0 && c.getHexColorCode().length()>0)
		{
			//trim the length if more than max
			if(c.getCategoryName().length()>WinbookConstants.MAX_CATEGORY_NAME_LENGTH)
				c.setCategoryName(c.getCategoryName().substring(0,WinbookConstants.MAX_CATEGORY_NAME_LENGTH));
			
			//If color value incorrectly specified, use default
			if(!Pattern.matches("#[a-fA-F0-9]{6}", c.getHexColorCode()))
				c.setHexColorCode(WinbookConstants.DEFAULT_CATEGORY_COLOR);
			
			sqlUpdate.append(" name = :categoryName, color = :hexColorCode");
		}
		
		else
			throw new MissingOrIncorrectDataException("Updating category: Possibly both category name and color are empty. Check data sent to DAO");
		
		sqlUpdate.append(whereClause);
		
		int numRowsUpdated = jdbcTemplate.update(sqlUpdate.toString(), queryParams);
		
		if(numRowsUpdated==0)		
			return false;
		
		return true;
	}

	@Override
	public boolean delete(int entityId) throws MissingOrIncorrectDataException {
		
		if(entityId<=0)
			throw new MissingOrIncorrectDataException("Deleting Category: category id for deletion MUST be valid > 0");
		
		String sql = "delete from category where category_id = ?";
		
		int numRowsDeleted = 0;
		
		try {
			numRowsDeleted = jdbcTemplate.update(sql, Integer.valueOf(entityId));
		} catch (EmptyResultDataAccessException e) {
			//NO-OP
		}
		
		if(numRowsDeleted==0)
			return false;
		
		return true;
	}

	@Override
	public boolean updateList(List<Category> listOfEntities) {
		throw new NotImplementedException();
	}

	@Override
	public boolean deleteList(List<Category> listOfEntitites) {
		throw new NotImplementedException();
	}
	
	private class CategoryRowMapper implements RowMapper<Category>
	{

		@Override
		public Category mapRow(ResultSet rs, int rowNumber) throws SQLException {
			
			Category category = new Category(rs.getInt("category_id"));
			String catName = rs.getString("name");
			String catCode = rs.getString("color");
			boolean isMMF = rs.getBoolean("isMMF");
						
			//Only there as sanity check for data in DB. May be removed if deemed appropriate
			if(!Pattern.matches("#[a-fA-F0-9]{6}", catCode))
				catCode = WinbookConstants.DEFAULT_CATEGORY_COLOR;
			
			category.setCategoryName(catName);
			category.setHexColorCode(catCode);
			category.setMMF(isMMF);
			
			return category;
		}
		
	}///:~ End RowMapper

}///:~ End DAO
