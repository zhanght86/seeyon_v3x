package com.seeyon.v3x.usermapper.common.action.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.seeyon.v3x.usermapper.common.constants.RefreshUserMapperPolice;
import com.seeyon.v3x.usermapper.common.saver.Saver;
import com.seeyon.v3x.common.usermapper.dao.UserMapperDao;
import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;
import com.seeyon.v3x.usermapper.report.domain.ReportDetail;
import com.seeyon.v3x.usermapper.util.UserMapperUtil;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgMember;


public abstract class V3xAbstractUserMapperAction extends SimpleUserMapperAction {
	protected static Log log = LogFactory.getLog(V3xAbstractUserMapperAction.class);

	protected UserMapperDao dao=null;
	protected RefreshUserMapperPolice police=null;
	protected OrgManagerDirect om=null;
	
	protected Saver saver=null;

	public UserMapperDao getDao() {
		return dao;
	}
	public void setDao(UserMapperDao val) {
		this.dao = val;
	}

	public OrgManagerDirect getOm() {
		return om;
	}
	public void setOm(OrgManagerDirect val) {
		this.om = val;
	}
/*
	public RefreshUserMapperPolice getPolice() {
		return police;
	}
	public void setPolice(RefreshUserMapperPolice police) {
		this.police = police;
	}
*/
	public Saver getSaver() {
		return saver;
	}
	public void setSaver(Saver val) {
		this.saver = val;
	}
	
	protected void throwUserMapperException(String msg,ReportDetail rd)throws Exception{
		this.ok();
		if(rd!=null){
			rd.appendMemo(msg);
			rd.appendAction("Failed");
			this.rds.add(rd);
		}
		throw new Exception(msg);
	}
	
	public void begin(RefreshUserMapperPolice val)throws Exception {
		if(val==null){
			this.police=RefreshUserMapperPolice.append;
		}else
		this.police=val;
		
		if(!StringUtils.hasText(this.getType())){
			throwUserMapperException("no type field",this.newReportDetail());
			return;
		}
	}
	public void ok(){
		this.police=null;
		
	}
	
	protected void begin()throws Exception {
		if(this.police==null)
		     begin(RefreshUserMapperPolice.append);
	}
	
	protected V3xOrgMember catchMember(String login,ReportDetail rd) throws Exception {
		
		if(!StringUtils.hasText(login)){
			throwUserMapperException("no loginname",rd);
			return null;
		}
		
		V3xOrgMember m=null;
		try{
			m=om.getMemberByLoginName(login);
		}catch(Exception e){
			log.error("", e);
			throwUserMapperException(e.getMessage(),rd);
		}
		if(m==null)
			throwUserMapperException("no member:"+login,rd);
		
		return m;
	}
	protected void save(V3xOrgUserMapper vum,V3xOrgUserMapper um,V3xOrgMember m)
				throws Exception{
		um.setType(this.getType());
		this.getSaver().save(vum, um, m);
		this.rds.addAll(this.getSaver().getReportDetails());
	}
	
	public void execute(V3xOrgUserMapper um) throws Exception {
		// TODO Auto-generated method stub
		//if(this.police==null)
			begin();
			ReportDetail rd=this.newReportDetail();	
		if(um==null){
			throwUserMapperException("no V3xOrgUserMapper object",rd);
			return;
		}
		
		rd.appendData("A8:"+um.getLoginName());
		if(!StringUtils.hasText(um.getExLoginName())){
			
			throwUserMapperException("no exloginname",rd);
			return;
		}
		rd.appendData("\n Ex:"+um.getExLoginName());
		V3xOrgMember m=this.catchMember(um.getLoginName(),rd);
		
		V3xOrgUserMapper vum=null;
		long umpk=um.getId();
		if(-1L!=umpk)
			vum=this.getDao().getById(umpk);
		if(vum==null)
		   vum=this.getDao().getLoginName(um.getExLoginName(), this.getType());
		
		int pk=RefreshUserMapperPolice.delete.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.delete.getKey()==pk){
			if(vum!=null){
				rd.appendAction("delete");
				try{
					this.getDao().deleteUserMapper(vum);
					rd.appendAction("--OK");
					this.rds.add(rd);
				}catch(Exception e){
					this.throwUserMapperException(e.getMessage(), rd);
				}
			}
			this.ok();
			return;
		}
		
		pk=RefreshUserMapperPolice.ignoreWhenExist.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.ignoreWhenExist.getKey()==pk
				&& vum!=null){
			rd.appendAction("ignoreWhenExist --OK");
			this.rds.add(rd);
			this.ok();
			return;
		}

		this.save(vum, um, m);
		this.ok();
	}
	public void execute(List<V3xOrgUserMapper> ums) throws Exception {
		// TODO Auto-generated method stub
		this.begin();
		ReportDetail rd=this.newReportDetail();	
		if(ums==null){
			throwUserMapperException("no List<V3xOrgUserMapper> objects",rd);
			return;
		}
		
		int pk=RefreshUserMapperPolice.delete.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.delete.getKey()==pk){			
			this.execute4delete(null);
			return;
		}
		
		pk=RefreshUserMapperPolice.refreshAll.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.refreshAll.getKey()==pk){
			RefreshUserMapperPolice orgp=this.police;
			
			begin(RefreshUserMapperPolice.delete);
			this.execute(ums);
			
			begin(RefreshUserMapperPolice.append);
			this.execute(ums);
			return;
		}
		
		//.....
		Map<String, List<V3xOrgUserMapper>> umsm=UserMapperUtil.listToMap4Login(ums);
		
		this.execute4Login(umsm);
	}
	public void execute4Login(Map<String, List<V3xOrgUserMapper>> ums)
			throws Exception {
		// TODO Auto-generated method stub
		this.begin();
		
		if(ums==null){
			throwUserMapperException("no Map<String, List<V3xOrgUserMapper>> objects",this.newReportDetail());
			return;
		}
		
		int pk=RefreshUserMapperPolice.delete.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.delete.getKey()==pk){			
			this.execute4delete(null);
			return;
		}
		
		pk=RefreshUserMapperPolice.refreshAll.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.refreshAll.getKey()==pk){
			RefreshUserMapperPolice orgp=this.police;
			
			begin(RefreshUserMapperPolice.delete);
			this.execute(new ArrayList<V3xOrgUserMapper>());
			
			begin(RefreshUserMapperPolice.append);
			this.execute4Login(ums);
			return;
		}
		
		for(String key:ums.keySet()){
			if(!StringUtils.hasText(key))
				continue;
			
			List<V3xOrgUserMapper> l=ums.get(key);
			if(l==null)
				continue;
			
			ReportDetail rd=newReportDetail();	
			rd.appendData("A8:"+key);
			rd.appendAction("execute4Login");
			try{
				
				execute4Login(key,l);
			}catch(Exception e){
				rd.appendAction("--Failed");
				rd.appendMemo(e.getMessage());
				this.rds.add(rd);
				log.error("", e);
			}
		}
	}
	public void execute4Login(String login, List<V3xOrgUserMapper> ums)throws Exception {
		this.begin();
		ReportDetail rd=newReportDetail();	
		
		if(!StringUtils.hasText(login)){
			throwUserMapperException("no login",this.newReportDetail());
			return;
		}
		rd.appendData("A8:"+login);
		int pk=RefreshUserMapperPolice.delete.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.delete.getKey()==pk){			
			this.execute4delete(login);
			return;
		}
		
		pk=RefreshUserMapperPolice.refreshAll.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.refreshAll.getKey()==pk){
			RefreshUserMapperPolice orgp=this.police;
			
			begin(RefreshUserMapperPolice.delete);
			this.execute(new ArrayList<V3xOrgUserMapper>());
			
			begin(RefreshUserMapperPolice.append);
			this.execute4Login(login,ums);
			return;
		}
		
		pk=RefreshUserMapperPolice.refreshSameLogin.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.refreshSameLogin.getKey()==pk){
			RefreshUserMapperPolice orgp=this.police;
			
			begin(RefreshUserMapperPolice.delete);
			this.execute4Login(login,ums);
			
			begin(RefreshUserMapperPolice.append);
			this.execute4Login(login,ums);
			return;
		}
		
		if(ums==null){
			rd.appendAction("ignored");
			rd.appendMemo("no List<V3xOrgUserMapper> ums");
			this.rds.add(rd);
			return;
		}
		
		for(V3xOrgUserMapper um:ums){
			if(um==null)
				continue;
			if(!login.equals(um.getLoginName()))
				continue;
			
			ReportDetail rd1=newReportDetail();	
			rd1.appendAction("execute4Login");
			try{
				execute(um);
			}catch(Exception e){
				rd1.appendAction("--Failed");
				rd1.appendMemo(e.getMessage());
				this.rds.add(rd1);
				log.error("", e);
			}
		}
	}
	public void execute4LoginExLogin(Map<String, List<String>> ums)
			throws Exception {
		// TODO Auto-generated method stub
		this.begin();
		ReportDetail rd=newReportDetail();	
		if(ums==null){
			throwUserMapperException("no Map<String, List<String>> objects",rd);
			return;
		}
		
		int pk=RefreshUserMapperPolice.delete.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.delete.getKey()==pk){			
			this.execute4delete(null);
			return;
		}
		
		pk=RefreshUserMapperPolice.refreshAll.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.refreshAll.getKey()==pk){
			RefreshUserMapperPolice orgp=this.police;
			
			begin(RefreshUserMapperPolice.delete);
			this.execute(new ArrayList<V3xOrgUserMapper>());
			
			begin(RefreshUserMapperPolice.append);
			this.execute4LoginExLogin(ums);
			return;
		}
		
		for(String key:ums.keySet()){
			if(!StringUtils.hasText(key))
				continue;
			
			List<String> l=ums.get(key);
			if(l==null)
				continue;
			
			ReportDetail rd1=newReportDetail();	
			rd1.appendData("A8:"+key);
			rd1.appendAction("execute4Login");
			try{
				execute4LoginExLogin(key,l);
			}catch(Exception e){
				rd1.appendAction("--Failed");
				rd1.appendMemo(e.getMessage());
				this.rds.add(rd1);
				log.error("", e);
			}
		}
	}
	public void execute4LoginExLogin(String login,List<String> exLogins)
	        throws Exception{
		this.begin();
		ReportDetail rd=newReportDetail();	
		if(!StringUtils.hasText(login)){
			throwUserMapperException("no login",rd);
			return;
		}
		
		int pk=RefreshUserMapperPolice.delete.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.delete.getKey()==pk){			
			this.execute4delete(login);
			return;
		}
		
		pk=RefreshUserMapperPolice.refreshAll.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.refreshAll.getKey()==pk){
			RefreshUserMapperPolice orgp=this.police;
			
			begin(RefreshUserMapperPolice.delete);
			this.execute(new ArrayList<V3xOrgUserMapper>());
			
			begin(RefreshUserMapperPolice.append);
			this.execute4LoginExLogin(login,exLogins);
			return;
		}
		
		pk=RefreshUserMapperPolice.refreshSameLogin.getKey() & this.police.getKey();
		if(RefreshUserMapperPolice.refreshSameLogin.getKey()==pk){
			RefreshUserMapperPolice orgp=this.police;
			
			begin(RefreshUserMapperPolice.delete);
			this.execute4LoginExLogin(login,exLogins);
			
			begin(RefreshUserMapperPolice.append);
			this.execute4LoginExLogin(login,exLogins);
			return;
		}
		
		if(exLogins==null)
			return;
		
		for(String exlogin:exLogins){
			if(!StringUtils.hasText(exlogin))
				continue;
			ReportDetail rd1=newReportDetail();	
			rd1.appendData("A8:"+login);
			rd1.appendData("\nEx:"+exlogin);
			rd1.appendAction("execute4LoginExLogin");
			try{
				V3xOrgUserMapper um=new V3xOrgUserMapper();
				
				um.setLoginName(login);
				um.setExLoginName(exlogin);
				
				execute(um);
			}catch(Exception e){
				rd1.appendAction("--Failed");
				rd1.appendMemo(e.getMessage());
				this.rds.add(rd1);
				log.error("", e);
			}
		}
	}
	private void execute4delete(String login)throws Exception{
		ReportDetail rd1=newReportDetail();	
		rd1.appendData("A8:"+login);
		rd1.appendData("\ntype:"+this.getType());
		rd1.appendAction("清除 ");
		try{
			this.getDao().clearTypeLogin(this.getType(), login,om);
			rd1.appendAction("--成功");
			rd1.appendMemo("全清 ");
			if(StringUtils.hasText(login)){
				rd1.appendMemo(" 针对:"+login);
			}
		}catch(Exception e){
			rd1.appendAction("--失败");
			rd1.appendMemo(e.getMessage());
			this.rds.add(rd1);
			log.error("", e);
		}
		this.rds.add(rd1);
		this.ok();
	}
}//end class
