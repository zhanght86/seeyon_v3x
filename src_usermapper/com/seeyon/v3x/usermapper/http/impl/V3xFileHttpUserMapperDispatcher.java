package com.seeyon.v3x.usermapper.http.impl;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.usermapper.NoMethodException;
import com.seeyon.v3x.usermapper.util.UserMapperUtil;

public abstract class V3xFileHttpUserMapperDispatcher extends
		V3xHttpUserMapperDispatcher {
	
	protected FileManager fileManager;

	public FileManager getFileManager() {
		return fileManager;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	public ModelAndView fileUserMapperList(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException,Exception {
		//this.type(request, response);
		checkAction();
		
		UserMapperUtil umu=UserMapperUtil.newInstance();
		File f=umu.provideFile(request, fileManager, getFileTag());
		
		List<List<String>> data=this.readFile(f);
		this.proceedList(data, request,response);
		umu=null;
		
		afterAction(request,response);
		
		return null;
	}

	abstract public String getFileTag();
	abstract protected List<List<String>> readFile(File f)throws Exception;
	abstract protected void proceedList(List<List<String>> data,HttpServletRequest request,
			HttpServletResponse response)throws Exception;
}//end class
