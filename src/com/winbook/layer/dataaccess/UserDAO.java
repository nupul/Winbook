/**
 * 
 */
package com.winbook.layer.dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.restlet.security.Role;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.winbook.domainobjects.User;
import com.winbook.domainobjects.UserConfiguration;
import com.winbook.layer.accessControl.WinbookEnroler;

/**
 * @author Nupul
 *
 */
public class UserDAO {//extends BasicWinBookDAO{

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	
	
	public UserDAO(){}
	
	//This has been created only to accommodate the datasource creation from spring rather than rely on WinbookConfig for providing one.
	public void setDataSource(DataSource datasource) {
		jdbcTemplate = new SimpleJdbcTemplate(datasource);
		jdbcInsert = new SimpleJdbcInsert(datasource).withTableName("user").usingColumns("email","password","firstname","lastname","nickname","avatar","isshaper");
	}



	/**
	 * Return true/false based on whether the user is authenticated or not. Expects the SHA1 Hash of the password for authentication.
	 * @param username
	 * @return
	 */
	public boolean isAuthenticated(String username, String passwordSHA1Hash) {

		String sql = "select `password` from `user` where email=?"; 
		
		String passcode = null;
		
		try {
			passcode = jdbcTemplate.queryForObject(sql, String.class, username);
		} catch (EmptyResultDataAccessException e) {
			//implies no such user exists
			return false;
		}
		
		if(!passcode.equalsIgnoreCase(passwordSHA1Hash))
			return false;
		
		return true;
	}

	/**
	 * Return possible valid role of the user - Either member of project or shaper or viewer (i.e., non member of project but valid authenticated user)
	 * @param username
	 * @return
	 */
	public Role getRole(String username, String projectName) {
		
		String sql = " select u.email, firstName, lastName, nickname, avatar, isShaper from `user` u"
					+" join user_memberof_project using(email)"
					+" where email = ? and project_title=?";	
		User user = null;
		Role role = null;
		try {
			user = jdbcTemplate.queryForObject(sql, new UserMapper(), username, projectName);
			
			if(user.getConfiguration().isShaper())
				role = WinbookEnroler.SHAPER;
			else
				role = WinbookEnroler.MEMBER;
			
			
		} catch (EmptyResultDataAccessException e) {
			
			role = WinbookEnroler.NON_MEMBER;
			
		}
		
		return role;
	}
	
	public User getUser(String email)
	{
		String sql = " select email, firstName, lastName, nickname, avatar, isShaper from `user`"
					+" where email = ?";
		
		User user = jdbcTemplate.queryForObject(sql, new UserMapper(),email);
		
		return user;
		
	}
	
	public boolean createUser(User user) throws DuplicateKeyException{
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("email", user.getEmailId());
		queryParams.put("firstName", user.getFirstName());
		queryParams.put("lastName", user.getLastName());
		queryParams.put("password", user.getPassword());
		queryParams.put("nickName", user.getNickname());
		queryParams.put("avatar", user.getAvatarURL());
		queryParams.put("isShaper", user.getConfiguration().isShaper());
		
		if(jdbcInsert.execute(queryParams)==0)
			return false;
		
		return true;
			
	}
	
	private class UserMapper implements RowMapper<User>
	{
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			
			User user = new User(rs.getString("email"));
			user.setAvatarURL(rs.getString("avatar"));
			user.setFirstName(rs.getString("firstName"));
			user.setLastName(rs.getString("lastName"));
			user.setNickname(rs.getString("nickname"));
			user.getConfiguration().setShaper(rs.getBoolean("isShaper"));
			
			return user;
		}
		
	}

}
