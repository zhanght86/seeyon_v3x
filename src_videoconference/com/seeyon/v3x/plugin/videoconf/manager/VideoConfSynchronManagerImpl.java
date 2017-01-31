/**
 * 视频会议同步实现类
 * @author radishlee
 * @since 2012-2-17
 * @describe 视频会议同步实现类
 */
package com.seeyon.v3x.plugin.videoconf.manager;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.propertymapper.idmapper.GuidMapper;
import com.seeyon.v3x.common.security.MessageEncoder;
import com.seeyon.v3x.event.Event;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.event.AddMemberEvent;
import com.seeyon.v3x.organization.event.DeleteMemberEvent;
import com.seeyon.v3x.organization.event.UpdateMemberEvent;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.videoconf.util.AddMember;
import com.seeyon.v3x.plugin.videoconf.util.Constants;
import com.seeyon.v3x.plugin.videoconf.util.DeleteMember;
import com.seeyon.v3x.util.annotation.ListenEvent;
import com.seeyon.v3x.videoconference.util.ParseXML;



public class VideoConfSynchronManagerImpl implements VideoConfSynchronManager {
	private static final Log log = LogFactory.getLog(VideoConfSynchronManagerImpl.class);
	private GuidMapper guidMapper;
	private OrgManager orgManager;

	/**
	 * @describe 同步组织机构入口
	 * @author radishlee
	 * @since 2012-2-17
	 * @param List<V3xOrgAccount> accountList,boolean isOverOrgDate, GuidMapper guidMapper
	 * @return String类型返回值
	 */
	public String synchronStart(List<V3xOrgAccount> accountList,boolean isOverOrgDate, GuidMapper guidMapper) throws Exception {
		try {
			for (V3xOrgAccount ent : accountList) {
				if (Constants.getStopSyn()) {
					return "0";
				}
				
				List<V3xOrgMember> allInMembers = orgManager.getAllMembers(ent.getId());

				for(V3xOrgMember member : allInMembers){
					try{
						this.synchronEntity(member, isOverOrgDate, guidMapper);
					}catch(Exception e){
						continue;//同步单个人员出错。继续同步。不中断
					}
				}
				
				List<V3xOrgMember> allExtMembers = orgManager.getAllExtMembers(ent.getId());
				for(V3xOrgMember member : allExtMembers){
					try{
						this.synchronEntity(member, isOverOrgDate, guidMapper);
					}catch(Exception e){
						continue;//同步单个人员出错。继续同步。不中断
					}
				}
			}
			// 清理单位
			// this.delAccounts(guidMapper, accountList);
		} catch (Exception e) {
			log.error("同步出错", e);
			throw new BusinessException(e.getMessage());
		}
		return "0";
	}

     
	
	/**
	 * @describe 同步组织机构
	 * @author radishlee
	 * @since 2012-2-17
	 * @param V3xOrgEntity ent, boolean isCover,GuidMapper guidMapper
	 * @
	 */
	private void synchronEntity(V3xOrgMember member, boolean isCover,GuidMapper guidMapper) throws Exception{
	    String username = Constants.SYN_USER_NAME;
	    String password = Constants.SYN_PASSWORD;
	    MessageEncoder encoder = new MessageEncoder();
	    
		//批量导入
		String result = AddMember.addMember(Constants.SYN_URL, username, password, member,encoder);
		log.info("调用红杉树创建人员接口：  "+result);
     	if(!StringUtils.contains(result,"SUCCESS")){
     		throw new Exception("创建人员失败！错误码："+result+"人员ID"+member.getId()+"人员名称"+member.getName());
     	}
		String userId = (String)ParseXML.parseXML(result).get("userId");
		guidMapper.map(member.getId(), userId,VideoConfSynchronManager.MAP_MEMBER);
	}
	

	/**
	 * @describe 增加人员
	 * @author radishlee
	 * @since 2012-2-17
	 * @param String url,String username,String password,V3xOrgEntity ent
	 * 
	 */
	@ListenEvent(event= AddMemberEvent.class,async = true)
	public void addMember(AddMemberEvent event) throws Exception{
		if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("videoconf")){
			try{
				this.addMember(event,"add");
	     	}catch(Exception e){
	     		log.error("修改失败人员memberid="+event.getMember().getId()+"||"+"修改失败人员名称="+event.getMember().getName(), e);
	     		return;
	     	}
		}
	}
	
	
	/**
	 * @describe 更新人员
	 * @author radishlee
	 * @since 2012-2-17
	 * @param pdateMemberEvent event
	 * 
	 */
	@ListenEvent(event= UpdateMemberEvent.class,async = true)
	public void updateMember(UpdateMemberEvent event) throws Exception{
		if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("videoconf")){
			String username = Constants.SYN_USER_NAME;
			String password = Constants.SYN_PASSWORD;
			
			V3xOrgMember updatedMember = event.getMember();
			
			String result = DeleteMember.deleteMember(Constants.SYN_URL, username, password, updatedMember);
			log.info("调用红杉树删除人员接口：  "+result);
	     	if(!StringUtils.contains(result,"SUCCESS")){
	     		//radishlee add 2012-4-13 如果红杉树系统不存在当前人。A8存在。就创建当前人员
	     		if(StringUtils.contains(result,Constants.NO_MEMBER_ERROR)){
	     			try{
	    	     		this.addMember(event,"update");
	    	     	}catch(Exception e){
	    	     		log.error("修改失败人员memberid="+updatedMember.getId()+"||"+"修改失败人员名称="+updatedMember.getName(), e);
	    	     		return;
	    	     	}
	     		}
	     		log.error("修改失败人员memberid="+updatedMember.getId()+"||"+"修改失败人员名称="+updatedMember.getName());
	     		return;
	     	}
	     	try{
	     		this.addMember(event,"update");
	     	}catch(Exception e){
	     		log.error("修改失败人员memberid="+updatedMember.getId()+"||"+"修改失败人员名称="+updatedMember.getName(), e);
	     		return;
	     	}
		}
	}
	
	/**
	 * @describe 删除人员
	 * @author radishlee
	 * @since 2012-2-17
	 * @param pdateMemberEvent event
	 * 
	 */
	@ListenEvent(event= DeleteMemberEvent.class,async = true)
	public void deleteMember(DeleteMemberEvent event) throws Exception{
		if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("videoConf")){
			try{
				this.deleteMember(event,"add");
	     	}catch(Exception e){
	     		log.error("删除失败人员memberid="+event.getMember().getId()+"||"+"删除失败人员名称="+event.getMember().getName(), e);
	     		return;
	     	}
		}
	}
	
	
	private void deleteMember(DeleteMemberEvent event, String string) {
		String username = Constants.SYN_USER_NAME;
		String password = Constants.SYN_PASSWORD;
		
		String result = DeleteMember.deleteMember(Constants.SYN_URL, username, password, event.getMember());
		log.info("调用红杉树删除人员接口：  "+result);
     	if(!StringUtils.contains(result,"SUCCESS")){
     		log.error("删除失败人员memberid="+event.getMember().getId()+"||"+"删除失败人员名称="+event.getMember().getName());
     		return;
     	}
	}


	/**
	 * @describe 增加人员
	 * @author radishlee
	 * @since 2012-2-17
	 * @param String url,String username,String password,V3xOrgEntity ent
	 * @throws Exception 
	 * 
	 */
	private void addMember(Event event,String type) throws Exception{
		String username = Constants.SYN_USER_NAME;
		String password = Constants.SYN_PASSWORD;
		MessageEncoder encoder = new MessageEncoder();
		
		V3xOrgMember member = new V3xOrgMember();
		if(type.equals("update")){
			UpdateMemberEvent uevent = (UpdateMemberEvent)event;
			member = uevent.getMember();
		}else if(type.equals("add")){
			AddMemberEvent aevent = (AddMemberEvent)event;
			member = aevent.getMember();
		}
		
		String result = AddMember.addMember(Constants.SYN_URL, username, password, member,encoder);
		log.info("调用红杉树创建人员接口：  "+result);
     	if(!StringUtils.contains(result,"SUCCESS")){
     		throw new Exception("创建人员失败！错误码："+result);
     	}
		String userId = (String)ParseXML.parseXML(result).get("userId");
		guidMapper.map(member.getId(), userId,VideoConfSynchronManager.MAP_MEMBER);
	}
	
	
	
	
	

	public void setGuidMapper(GuidMapper guidMapper) {
		this.guidMapper = guidMapper;
	}


	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
}