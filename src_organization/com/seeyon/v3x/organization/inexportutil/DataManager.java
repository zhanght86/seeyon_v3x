package com.seeyon.v3x.organization.inexportutil;

import java.util.List;

/**
 * 
 * @author kyt
 *
 */
public interface DataManager{
	public List getDataStructure(String tableName) throws Exception;
	public void execSQLList(List sqlstr) throws Exception;
}
