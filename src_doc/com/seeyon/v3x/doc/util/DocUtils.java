package com.seeyon.v3x.doc.util;

import java.util.List;
import java.util.Set;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.manager.DocLibManager;

/**
 * 一个工具类，暂时主要用来解决两个类之间的依赖问题 
 * docLibManager从这里获得 
 */
public class DocUtils {
	private static DocLibManager docLibManager;
	
	/**
	 * 取得一个文档库
	 */
	public DocLib getDocLibById(long libId){
		return this.getDocLibManager().getDocLibById(libId);
	}
	
	/**
	 * 取得某个单位下某种类型的文档库
	 * 
	 */
	public List<DocLib> getLibsOfAccount(long domainId, byte libType){
		return this.getDocLibManager().getLibsOfAccount(domainId, libType);
	}
	
	/**
	 * 
	 */
	public synchronized DocLibManager getDocLibManager(){
		if(docLibManager == null){
			 docLibManager = (DocLibManager)ApplicationContextHolder.getBean("docLibManager");
		}
		
		return docLibManager;
	}
	
	/**
	 * 判断当前用户是否某个库的owner
	 */
	public boolean isOwnerOfLib(Long userId, Long libId) {
		return this.getDocLibManager().isOwnerOfLib(userId, libId);
	}
	
	/**
	 * 根据docLibId 得到 owners
	 * @param userId
	 * @return
	 */
	public List<Long> getOwnersByDocLibId(long docLibId) {
		return this.getDocLibManager().getOwnersByDocLibId(docLibId);
	}
}
