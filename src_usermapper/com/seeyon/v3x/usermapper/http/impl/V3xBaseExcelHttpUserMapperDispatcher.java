package com.seeyon.v3x.usermapper.http.impl;

import java.io.File;
import java.util.List;

import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.usermapper.util.UserMapperUtil;

public abstract class V3xBaseExcelHttpUserMapperDispatcher extends
		V3xFileHttpUserMapperDispatcher {
	@Override
	public String getFileTag() {
		// TODO Auto-generated method stub
		return "xls";
	}

	@Override
	protected List<List<String>> readFile(File f) throws Exception {
		// TODO Auto-generated method stub
		List<List<String>> ol=this.getFileToExcelManager().readExcel(f);
		if(ol==null)
			return null;
		if(ol.size()<2)
			return null;
		return ol.subList(2, ol.size());
	}
}//end class
