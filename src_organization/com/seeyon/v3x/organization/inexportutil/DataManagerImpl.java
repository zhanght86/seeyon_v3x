package com.seeyon.v3x.organization.inexportutil;

import java.util.List;

/**
 * 
 * @author kyt
 *
 */
public class DataManagerImpl implements DataManager{
	private DataDao dataDao;
	
	public DataDao getDataDao() {
		return dataDao;
	}

	public void setDataDao(DataDao dataDao) {
		this.dataDao = dataDao;
	}

	/**
	 * 从后台取出表的结构，并组成DataObject,组装list
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public List getDataStructure(String tableName) throws Exception{
		return getDataDao().getDataStructure(tableName);
	}
	public void execSQLList(List sqlstr) throws Exception{
		getDataDao().execSQLList(sqlstr);
	}
}
