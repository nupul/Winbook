/**
 * 
 */
package com.winbook.layer.dataaccess;

import java.util.List;

import com.winbook.exceptions.MissingOrIncorrectDataException;

/**
 * @author Nupul
 * Generic DAO interface for all the CRUD related operations for all DAOs in the system
 */
public interface IGenericDAO<T> {
	
	public int create(T entity)throws MissingOrIncorrectDataException;
	public T getById(int entityId) throws MissingOrIncorrectDataException;
	public List<T> getAllItemsForProject();
	public boolean update(T entity) throws MissingOrIncorrectDataException;
	public boolean updateList(List<T> listOfEntities);
	public boolean delete(int entityId) throws MissingOrIncorrectDataException;
	public boolean deleteList(List<T> listOfEntitites);

}
