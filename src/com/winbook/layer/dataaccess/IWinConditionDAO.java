package com.winbook.layer.dataaccess;

import java.util.List;

import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;

public interface IWinConditionDAO {

	public int addWinCondition(WinCondition winCondition);

	public WinCondition getWinCondition(int id);

	public boolean updateWinCondition(WinCondition winCondition);

	public boolean deleteWinCondition(int id);

	public List<WinCondition> getAllWinConditions() throws NoSuchProjectExistsException, NoWallForProjectExistsException;

	public boolean updateStatus(WinCondition winCondition);

}