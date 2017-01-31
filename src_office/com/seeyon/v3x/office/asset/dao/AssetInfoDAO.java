package com.seeyon.v3x.office.asset.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.office.asset.domain.MAssetInfo;

/**
 *
 * @author modified by<a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 *
 */

public interface AssetInfoDAO {
	/**
	 * 查询所有 设备
	 * @param userid
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public List findAssetRegList(Long userid,String fieldName,String fieldValue);
	
	public List findAssetAppList(String fieldName,String fieldValue,Long[] depart);
	
	public List findAssetPermList(String fieldName,String fieldValue,Long adminId);
	
	public List findAssetStorageList(String fieldName,String fieldValue,Long[] depart);
	
	public List findAllAssetInfo(Long assMge);
	
	public void save(MAssetInfo mAssetInfo);
	
	public void update(MAssetInfo mAssetInfo);
	
	public SQLQuery find(String sql,Map m);

	public int getCount(String sql,Map m);
	
	public MAssetInfo load(long id);
    /**
     * 管理员管理的办公设备移交功能
     *
     */
    public void updateAssetMangerBatch(long oldManager, long newManager,User user);
    
    public void updateAssetMangerBatch(long oldManager, long newManager,User user,boolean fromFlag);
    
    /**
     * 管理员管理的办公设备审批移交功能
     *
     */
    public void audiTransfer( long oldManager,  long newManager);
    
    /**
     * 按部门统计办公设备
     * @return
     */
    public List getAssetSummayByDep(boolean needPage);
    /**
     *  按人员名称统计办公设备
     * @return
     */
    public List getAssetSummayByMember(boolean needPage);
    
}