package com.seeyon.v3x.doc.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;


public class DocLibDao extends BaseHibernateDao<DocLib> {	
	// 查询所有公共文档库
	public List<DocLib> getDocLibs() {
		String hsql = "from DocLib as lib where lib.type != ? and lib.id != lib.domainId order by lib.domainId,lib.orderNum";
		return super.find(hsql, Constants.PERSONAL_LIB_TYPE);
	}
	
	/**
	 * 获取文档库的详细信息列表
	 * @param ids docLib的ID列表。
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DocLib> getDocLibByIds(List<Long> ids){
		String hsql=" from DocLib lib where lib.id in (:ids)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		return  super.find(hsql, -1,-1,map);
	}
	
	// 获取有权查看的所有自定义文档库id
	@SuppressWarnings("unchecked")
	public List<Long> getAllMember(String orgIds){
		if(orgIds == null || "".equals(orgIds))
			return new ArrayList<Long>();

		String hql = "select docLibId from DocLibMember where userId in (:ids)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", Constants.parseStrings2Longs(orgIds, ","));
		List the_list = super.find(hql, -1, -1, map);	

		if(the_list != null)
			return (List<Long>)the_list;
		else
			return new ArrayList<Long>();
	}
	
	// 删除指定单位的所有公共文档库
	public void deleteDocLibsByDomainId(long domainId) {
		String hsql = "delete from DocLib as lib where lib.domainId=? and lib.type != ?  ";
		Object[] values = {domainId, Constants.PERSONAL_LIB_TYPE };
		super.bulkUpdate(hsql, null, values);
	}
	
	/**
	 * 恢复virtualLib
	 */
	public void restoreVirtualLib(long domainId){
		String hql = "update DocLib set isDefault = true where id=?";
		super.bulkUpdate(hql, null, domainId);
	}
	
	/**
	 * 更改文档库的状态
	 * @param ids	文档库ID集合
	 * @param status	状态：启用、停用
	 */
	public void updateStatus(List<Long> ids, byte status) {
		String hql = "update " + DocLib.class.getCanonicalName() + " set status=? where id in (:ids)";
		Map<String, Object> params = FormBizConfigUtils.newHashMap("ids", ids);
		this.bulkUpdate(hql, params, status);
	}

}
