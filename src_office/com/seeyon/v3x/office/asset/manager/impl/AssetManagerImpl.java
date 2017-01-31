package com.seeyon.v3x.office.asset.manager.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;

import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.asset.dao.AssetApplyInfoDAO;
import com.seeyon.v3x.office.asset.dao.AssetDepartInfoDAO;
import com.seeyon.v3x.office.asset.dao.AssetInfoDAO;
import com.seeyon.v3x.office.asset.domain.MAssetInfo;
import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.asset.domain.TAssetDepartinfo;
import com.seeyon.v3x.office.asset.manager.AssetManager;
import com.seeyon.v3x.office.asset.util.Constants;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.office.myapply.dao.ApplyListDAO;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.util.Datetimes;

/**
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-17
 * 
 */
public class AssetManagerImpl implements AssetManager {
	
	private final static Log log = LogFactory.getLog(AssetManagerImpl.class);

	private AssetInfoDAO assetInfoDAO;

	private ApplyListDAO applyListDAO;

	private AssetApplyInfoDAO assetApplyInfoDAO;

	private AssetDepartInfoDAO assetDepartInfoDao;
	
	private UserMessageManager userMessageManager;

	public void setAssetInfoDAO(AssetInfoDAO assetInfoDAO) {
		this.assetInfoDAO = assetInfoDAO;
	}

	public void setApplyListDAO(ApplyListDAO applyListDAO) {
		this.applyListDAO = applyListDAO;
	}

	public void setAssetApplyInfoDAO(AssetApplyInfoDAO assetApplyInfoDAO) {
		this.assetApplyInfoDAO = assetApplyInfoDAO;
	}

	public void setAssetDepartInfoDao(AssetDepartInfoDAO assetDepartInfoDao) {
		this.assetDepartInfoDao = assetDepartInfoDao;
	}

	public AssetInfoDAO getAssetInfoDAO() {
		return assetInfoDAO;
	}

	public ApplyListDAO getApplyListDAO() {
		return applyListDAO;
	}

	public AssetApplyInfoDAO getAssetApplyInfoDAO() {
		return assetApplyInfoDAO;
	}

	public AssetDepartInfoDAO getAssetDepartInfoDao() {
		return assetDepartInfoDao;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

    public void save(MAssetInfo mAssetInfo)
    {
        this.assetInfoDAO.save(mAssetInfo);
    }

    public void save(TAssetDepartinfo tAssetDepartInfo)
    {
        this.assetDepartInfoDao.save(tAssetDepartInfo);
    }

    public void update(MAssetInfo mAssetInfo)
    {
        this.assetInfoDAO.update(mAssetInfo);
    }

    public void update(TAssetDepartinfo tAssetDepartInfo)
    {
        this.assetDepartInfoDao.update(tAssetDepartInfo);
    }

    public void update(TAssetApplyinfo assetApply)
    {
        this.assetApplyInfoDAO.update(assetApply);
    }

    public SQLQuery find(String sql,Map map)
    {
        SQLQuery query = this.assetInfoDAO.find(sql,map);
        return query;
    }

    public int getCount(String sql,Map m)
    {
        return this.assetInfoDAO.getCount(sql,m);
    }

    public SQLQuery findApply(String sql)
    {
        SQLQuery query = this.assetApplyInfoDAO.find(sql);
        return query;
    }

    public int getApplyCount(String sql)
    {
        return this.assetApplyInfoDAO.getCount(sql);
    }

    public MAssetInfo getById(long id)
    {
        return this.assetInfoDAO.load(id);
    }

    public TAssetApplyinfo getApplyinfoById(long id)
    {
        return this.assetApplyInfoDAO.load(id);
    }

    public TAssetDepartinfo getDepartinfoById(long id)
    {
        return this.assetDepartInfoDao.load(id);
    }

    public void createApply(long assetId, long userId, long depId,
            String apply_count, String asset_start, String asset_end,
            String asset_purpose)
    {
        createApply(assetId, userId, depId, 0, 0, "0", apply_count,
                asset_start, asset_end, asset_purpose);
    }

    public void createApply(long assetId, long userId, long depId,
            long apply_user, long apply_usedep, String long_flag,
            String apply_count, String asset_start, String asset_end,
            String asset_purpose)
    {
        Long l_Apply_count = null;
        if (apply_count != null && apply_count.length() > 0)
        {
            l_Apply_count = new Long(Long.parseLong(apply_count));
        }
        if (asset_start == null || asset_start.length() == 0)
        {
            asset_start = null;
        }
        if (asset_end == null || asset_end.length() == 0)
        {
            asset_end = null;
        }
        if (asset_purpose == null || asset_purpose.length() == 0)
        {
            asset_purpose = null;
        }
        int count = 0;
        try
        {
            count = this.applyListDAO.getCount("Select max(apply_id) as "
                    + Constants.Total_Count_Field + " From t_applylist",null);
            if (count == 0)
            {
                count = 30000000;
            }
            else
            {
                count = count + 1;
            }
        }
        catch (Exception ex)
        {
            count = 30000000;
        }
        TApplylist applyList = new TApplylist();
        // applyList.setApplyId(UUIDLong.longUUID());
        applyList.setApplyId(new Long(count));
        applyList.setApplyUsername(new Long(userId));
        applyList.setApplyDepId(new Long(depId));
        if (apply_user != 0)
        {
            applyList.setApplyUser(new Long(apply_user));
        }
        if (apply_usedep != 0)
        {
            applyList.setApplyUsedep(new Long(apply_usedep));
        }
        //edit date by bianteng start
        applyList.setApplyDate(new Date());
        //edit date by bianteng end
        applyList
                .setApplyState(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);
        applyList.setApplyType(new Integer(
                com.seeyon.v3x.office.myapply.util.Constants.ApplyType_Asset));
        MAssetInfo assetInfo = this.getById(assetId);
        if (assetInfo.getAssetMge() != null)
        {
            applyList.setApplyMge(assetInfo.getAssetMge());
        }
        applyList.setDelFlag(new Integer(
                com.seeyon.v3x.office.asset.util.Constants.Del_Flag_Normal));
        TAssetApplyinfo assetApplyInfo = new TAssetApplyinfo();
        assetApplyInfo.setApplyId(applyList.getApplyId());
        assetApplyInfo.setAssetId(assetId);
        assetApplyInfo.setApplyCount(l_Apply_count);
        assetApplyInfo.setAssetPurpose(asset_purpose);
        if (long_flag == null || long_flag.equals("0"))
        {
            if (asset_start != null && asset_start.length() > 0)
            {
                assetApplyInfo
                        .setAssetStart(com.seeyon.v3x.office.asset.util.str
                                .strToDate(asset_start));
            }
            if (asset_end != null && asset_end.length() > 0)
            {
                assetApplyInfo.setAssetEnd(com.seeyon.v3x.office.asset.util.str
                        .strToDate(asset_end));
            }
        }
        else if (long_flag.equals("1"))
        {
            assetApplyInfo.setLongFlag(new Integer(1));
        }
        assetApplyInfo.setDelFlag(new Integer(
                com.seeyon.v3x.office.asset.util.Constants.Del_Flag_Normal));
        this.applyListDAO.save(applyList);
        this.assetApplyInfoDAO.save(assetApplyInfo);

		OfficeHelper.addPendingAffair(assetInfo.getAssetName(), applyList, ApplicationSubCategoryEnum.office_asset);
        
		// 给管理员发送消息
		try {
			userMessageManager.sendSystemMessage(MessageContent.get("office.asset.apply", assetInfo.getAssetName(), CurrentUser.get().getName()), 
					ApplicationCategoryEnum.office, userId, 
					MessageReceiver.get(applyList.getApplyId(), assetInfo.getAssetMge(), "message.link.office.asset", String.valueOf(applyList.getApplyId())));
		} catch (MessageException e) {
			log.error("办公设备申请失败：", e);
		}
    }

    public int getMonthCount(long userId)
    {
        java.util.Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1 * (c.get(Calendar.DAY_OF_MONTH)));
        //by yongzhang
//        String sql = "Select * From t_applylist where del_flag = "
//                + Constants.Del_Flag_Normal + " and apply_username=" + userId;
        String sql = "Select * From t_applylist where "
             + " apply_username= :userId" ;
        sql += " and apply_type=2 and apply_state > 1";
        SQLQuery query = this.findApply(sql);
        query.addEntity(TApplylist.class);
        query.setLong("userId", Long.valueOf(userId)) ;
        List list = query.list();
        int count = 0;
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                TApplylist apply = (TApplylist) list.get(i);
                if (apply.getApplyDate() != null
                        && apply.getApplyDate().after(c.getTime()))
                {
                    count++;
                }
            }
        }
        return count;
    }

    public int getTotalCount(long userId)
    {
        //by Yongzhang
        String sql = "Select count(*) as " + Constants.Total_Count_Field
                + " from t_applylist where  "
                 + "  apply_username= :userId" ;
        sql += " and apply_type=2 and apply_state > 1";
        Map<String ,Object> map = new HashMap<String,Object>() ;
        map.put("userId", Long.valueOf(userId)) ;
        return this.getCount(sql,map);
    }

    public int getTotalNoBackCount(long userId)
    {
        StringBuffer sql = new StringBuffer();

        sql.append("Select t.*  from t_applylist  a,t_asset_departinfo  t  where a.apply_id=t.apply_id  and  t.del_flag = "
                        + Constants.Del_Flag_Normal
                        + " and a.apply_user= :userId"
                        );
        sql.append(" and apply_type=2 and apply_state > 1 and apply_state < 5");
        SQLQuery query = this.findApply(sql.toString());// sql语句把as去掉和oracle兼容
        query.addEntity(TAssetDepartinfo.class);
        query.setLong("userId", Long.valueOf(userId)) ;
        List list = query.list();
        int backcount = 0;
        int m = 0;
        int n = 0;
        if (list != null)
        {

            for (int i = 0; i < list.size(); i++)
            {
                int y = 0;
                int z = 0;
                TAssetDepartinfo td = (TAssetDepartinfo) list.get(i);
                if (td.getApplyCount() != null )
                {
                    y = Integer.parseInt(td.getApplyCount() + "");
                }
                if (td.getAssetBackcount() != null)
                {
                    z = Integer.parseInt(td.getAssetBackcount() + "");
                }
                m += y;
                n += z;
            }
            backcount = m - n;
        }

        return backcount;
    }

    public int getWeekCount(long userId)
    {
        java.util.Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1 * (c.get(Calendar.DAY_OF_WEEK) - 1 ));
        // String sql = "Select count(*) as "+Constants.Total_Count_Field + "
        // from t_applylist where del_flag = " + Constants.Del_Flag_Normal + "
        // and apply_username="+userId;
        // sql += " and apply_type=2 and apply_state > 1 and apply_date > '" +
        // df.format(c.getTime()) + "'";
        //by Yongzhang  2008-05-06
//        String sql = "Select * from t_applylist where del_flag = "
//                + Constants.Del_Flag_Normal + " and apply_username=" + userId;
        String sql = "Select * from t_applylist where "
             + " apply_username= :userId" ;
        sql += " and apply_type=2 and apply_state > 1";
        int count = 0;
        SQLQuery query = this.findApply(sql);
        query.addEntity(TApplylist.class);
        query.setLong("userId", Long.valueOf(userId)) ;
        List list = query.list();
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                TApplylist apply = (TApplylist) list.get(i);
                if (apply.getApplyDate() != null
                        && apply.getApplyDate().after(c.getTime()))
                {
                    count++;
                }
            }
        }
        return count;
    }
    /**
     * 管理员管理的办公设备移交功能
     *
     */
    public void updateAssetMangerBatch(long oldManager, long newManager,User user)
    {
    	this.updateAssetMangerBatch(oldManager,newManager,user,true);
    }
    
    public void updateAssetMangerBatch(long oldManager, long newManager,User user,boolean fromFlag){
    	if (fromFlag) {
    		this.assetInfoDAO.updateAssetMangerBatch(oldManager, newManager,user);
	        assetInfoDAO.audiTransfer(oldManager, newManager);
		}else {
			this.assetInfoDAO.updateAssetMangerBatch(oldManager, newManager,user,fromFlag);
		}
        
        
    }

	public List getAssetRegList(Long userId, String condition, String keyword) {
		return assetInfoDAO.findAssetRegList(userId, condition, keyword);
	}
	
	public List getAssetAppList(String condition, String keyword, Long[] depart) {
		return assetInfoDAO.findAssetAppList(condition, keyword, depart);
	}
	
	public List getAssetPermList(String condition, String keyword, Long adminid) {
		
		return assetInfoDAO.findAssetPermList(condition, keyword, adminid);
	}
	
	public List getAssetStorageList(String condition, String keyword, Long[] dapart) {

		return assetInfoDAO.findAssetStorageList(condition, keyword, dapart);
	}
	public List getAllAssetInfo(Long assMge) {
		
		return assetInfoDAO.findAllAssetInfo(assMge);
	}

	public List getAssetSummayByDep(boolean needPage) {
		return assetInfoDAO.getAssetSummayByDep(needPage);
	}
	public List getAssetSummayByMember(boolean needPage) {
		return assetInfoDAO.getAssetSummayByMember(needPage);
	}

	@Override
	public List getAssetBackListByUserId(String userid) {
		List list= assetDepartInfoDao.getAssetBackListByUserId(userid);
		return list;
	}
}
