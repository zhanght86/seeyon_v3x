package com.seeyon.v3x.system.ipcontrol.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.IP;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.ipcontrol.IpcontrolUserManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.system.ipcontrol.domain.V3xIpcontrol;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class IpcontrolManagerImpl extends BaseDao<V3xIpcontrol> implements IpcontrolManager {
	
	private static final Log log = LogFactory.getLog(IpcontrolManagerImpl.class);
	private OrgManager orgManager;
	private AppLogManager appLogManager;
	/**
	 * 限制
	 */
	public static final int LIMIT = 0;
	public static final String LIMITTEXT = Constants.getString4CurrentUser("system.ipcontrol.limit");
	/**
	 * 不限制
	 */
	public static final int NOLIMIT = 1;
	public static final String NOLIMITTEXT = Constants.getString4CurrentUser("system.ipcontrol.nolimit");
	
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void init(){
		initNoLimitIp();
		initLimitIp();
		log.info("初始化访问控制信息完成！");
	}
	
	
	/**
	 *初始化不限制IP访问控制信息
	 */
	public void initNoLimitIp(){
		List<V3xIpcontrol> ipcontrols = super.findBy("type", Integer.valueOf(NOLIMIT));
		Set<String> loginNames = new HashSet<String>();
		if (ipcontrols != null) {
			for (V3xIpcontrol ipcontrol : ipcontrols) {
				String users = ipcontrol.getUsers();
				try {
					Set<V3xOrgMember> members = orgManager.getMembersByTypeAndIds(users);
					Iterator<V3xOrgMember> iterator = members.iterator();
					while (iterator.hasNext()) {
						V3xOrgMember member = iterator.next();
						loginNames.add(member.getLoginName());
					}
				} catch (BusinessException e) {
					log.error("添加不限制人员信息出错！", e);
				}
			}
			IpcontrolUserManager.getInstance().addNoLimitIp(loginNames);
		}
	}
	/**
	 * 初始化限制IP访问控制信息
	 */
	public void initLimitIp(){
		List<V3xIpcontrol> ipcontrols = super.findBy("type", Integer.valueOf(LIMIT));
		Map<String, List<IP>> limitIp = new HashMap<String, List<IP>>();
		if (ipcontrols != null) {
			for (V3xIpcontrol ipcontrol : ipcontrols) {
				List<IP> ipList = new ArrayList<IP>();
				String ipAddress = ipcontrol.getAddress();
				if (Strings.isNotBlank(ipAddress)) {
					String[] ips = ipAddress.split(";");
					for (String ip : ips) {
						ipList.add(new IP(ip));
					}
				}
				String users = ipcontrol.getUsers();
				try {
					Set<V3xOrgMember> members = orgManager
							.getMembersByTypeAndIds(users);
					Iterator<V3xOrgMember> iterator = members.iterator();
					while (iterator.hasNext()) {
						V3xOrgMember member = iterator.next();
						if(Strings.isBlank(member.getLoginName())){
							log.warn(member.getName() + "的登录名为空！");
							continue;
						}
						if(limitIp.containsKey(member.getLoginName())){
							List<IP> list = new ArrayList<IP>();
							list.addAll(limitIp.get(member.getLoginName()));
							list.addAll(ipList);
							limitIp.put(member.getLoginName(), list);
						} else {
							limitIp.put(member.getLoginName(), ipList);
						}
					}
				} catch (BusinessException e) {
					log.error("添加限制人员信息出错！",e);
				}
			}
			IpcontrolUserManager.getInstance().addLimitIp(limitIp);
		}
	}
	public void delete(List<Long> ids) {
		User user = CurrentUser.get();
		String type = null;
		if(ids != null){
			for(Long id : ids){
				V3xIpcontrol ipcontrol  = getIpcontrol(id);
				if(ipcontrol.getType() == LIMIT){
					type = LIMITTEXT;
				} else {
					type = NOLIMITTEXT;
				}
				appLogManager.insertLog(user, AppLogAction.Ipcontrol_delete, user.getName(), type, ipcontrol.getName());
			}
			Map<String, Object> nameParameters = new HashMap<String, Object>();
			String hql = "delete from "+ V3xIpcontrol.class.getName() + " as v where v.id in (:ids)";
			nameParameters.put("ids",ids);
			super.bulkUpdate(hql, nameParameters);
		}
		//更新内存
		this.initNoLimitIp();
		this.initLimitIp();
	}

	public List<V3xIpcontrol> findIpcontrolByAccount(Long accountId) {
		return super.findBy("accountId", accountId);
	}

	public List<V3xIpcontrol> findAllIpcontrol() {
		return super.getAll();
	}

	public V3xIpcontrol getIpcontrol(Long id) {
		return (V3xIpcontrol)super.get(id);
	}

	public void save(V3xIpcontrol ipcontrol) throws Exception {
		String type = null;
		super.save(ipcontrol);
		//更新内存
		if(ipcontrol.getType() == LIMIT){
			type = LIMITTEXT;
			this.initLimitIp();
		} else {
			type = NOLIMITTEXT;
			this.initNoLimitIp();
		}
		User user = CurrentUser.get();
		appLogManager.insertLog(user, AppLogAction.Ipcontrol_Create, user.getName(), type, ipcontrol.getName());
	}

	public void update(V3xIpcontrol ipcontrol) throws Exception {
		String type = null;
		super.update(ipcontrol);
		//更新内存
		this.initLimitIp();
		this.initNoLimitIp();
		if(ipcontrol.getType() == LIMIT){
			type = LIMITTEXT;
		} else {
			type = NOLIMITTEXT;
		}
		User user = CurrentUser.get();
		appLogManager.insertLog(user, AppLogAction.Ipcontrol_Update, user.getName(), type, ipcontrol.getName());
	}

	public List<V3xIpcontrol> findIpcontrolBy(String name, String type, String accountId, String accountId2){
			Map<String, Object> param = new HashMap<String, Object>();
			String hql = "select v from " + V3xIpcontrol.class.getName() + " as v where 1=1 ";
			if(Strings.isNotBlank(name)){
				hql += " and v.name like :name";
				param.put("name", "%" + SQLWildcardUtil.escape(name.trim()) + "%");
			}
			if(Strings.isNotBlank(type)){
				hql += " and v.type=:type";
				param.put("type", Integer.parseInt(type));
			}
			if(Strings.isNotBlank(accountId2)){
				hql += " and v.accountId=:accountId";
				param.put("accountId", Long.parseLong(accountId2));
			} else if(Strings.isNotBlank(accountId)){
				hql += " and v.accountId=:accountId";
				param.put("accountId", Long.parseLong(accountId));
			}
			return (List<V3xIpcontrol>)super.find(hql, param);
	}

}
