package com.seeyon.v3x.log.manager;

import java.util.List;

import com.seeyon.v3x.common.operationlog.domain.OperationLog;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;

public class LogManagerImpl implements LogManager{

	private OperationlogManager operationlogManager; 
	
	public List<OperationLog> findTotalLog(){
		
		List<OperationLog> list = operationlogManager.getAllOperationLog(true);
		return list;
	}
	
	public List<OperationLog> findLogsByCat(){
		
		
		return null;
	}
}
