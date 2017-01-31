/**
 * 
 */
package com.seeyon.v3x.notepager.manager;

import com.seeyon.v3x.notepager.domain.Notepage;
   
/** 
 * @author LL   
 * 
 */  
public interface NotepagerManager
 {
	public Object autoSave(String notepagerId, Long memberId, String FormContent) throws Exception;
	
	public Notepage get(Long memberId) throws Exception;
 }
 