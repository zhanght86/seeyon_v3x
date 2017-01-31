package com.seeyon.v3x.office.asset.manager;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.office.asset.domain.MAssetInfo;
import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.asset.domain.TAssetDepartinfo;

public interface AssetManager{
	/**
	 * 取得办公设备管理 登记的设备
	 * @param userId 登陆人id
	 * @param condition 查询条件
	 * @param keyword  查询的值
	 * @return
	 */
	public List getAssetRegList(Long userId,String condition,String keyword);
	
	/**
	 * 查看可以 申请的设备
	 * @param condition
	 * @param keyword
	 * @return
	 */
	public List getAssetAppList(String condition,String keyword,Long[] depart);
	
	/**
	 * 得到审批的设备
	 * @param condition
	 * @param keyword
	 * @param adminId 管理员id
	 * @return
	 */
	public List getAssetPermList(String condition,String keyword,Long adminId);
	
	/**
	 * 借出、归还的设备
	 * @param condition
	 * @param keyword
	 * @param dapart
	 * @return
	 */
	public List getAssetStorageList(String condition,String keyword,Long[] dapart);
	
	/**
	 * 得到所有的 设备信息
	 * @param assMge
	 * @return
	 */
	public List getAllAssetInfo(Long assMge);
	
	public void save(MAssetInfo mAssetInfo);
	
	public void save(TAssetDepartinfo tAssetDepartInfo);
	
	public void update(MAssetInfo mAssetInfo);
	
	public void update(TAssetDepartinfo tAssetDepartInfo);
	
	public void update(TAssetApplyinfo assetApply);
	
	public SQLQuery find(String sql,Map m);

	public int getCount(String sql,Map m);
	
	public SQLQuery findApply(String sql);

	public int getApplyCount(String sql);
	
	public MAssetInfo getById(long id);
	
	public TAssetApplyinfo getApplyinfoById(long id);
	
	public TAssetDepartinfo getDepartinfoById(long id);
	
	public void createApply(long assetId, long userId, long depId, String apply_count, String asset_start, String asset_end, String asset_purpose);
	
	public void createApply(long assetId, long userId, long depId, long apply_user, long apply_usedep, String long_flag, String apply_count, String asset_start, String asset_end, String asset_purpose);
	
	//add by liusg 2007-10-11
	
	public int getWeekCount(long userId);
	
	public int getMonthCount(long userId);
	
	public int getTotalCount(long userId);
	
	public int getTotalNoBackCount(long userId);
	public void updateAssetMangerBatch(long oldManager, long newManager,User user);
	public void updateAssetMangerBatch(long oldManager, long newManager,User user,boolean fromFlag);
	/**
	 * 按部门统计办公设备
	 * @return
	 */
    public List getAssetSummayByDep(boolean needPage);
    /**
	 * 按部人员名称统计办公设备
	 * @return
	 */
    public List getAssetSummayByMember(boolean needPage);
    /**
     * 根据userid获得该用户还没有归还的办公设备信息列表
     * @param userid
     * @return
     */
	public List getAssetBackListByUserId(String userid);
	
}
