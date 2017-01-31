/**
 * 
 */
package com.seeyon.v3x.common.taglibs.functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.his.manager.HisColManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.product.ProductInfo;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-6-6
 */
public class CollaborationFunction {
	private static Log log = LogFactory.getLog(CollaborationFunction.class);

	/**
	 * 在流程文本框中，显示流程信息，如：毛凯(协同)、张田枫(协同)、Tan.Minfeng(协同)、宋牮(协同)
	 * 
	 * @param parties
	 * @param nodePermissionPolicy
	 * @param pageContext
	 * @return
	 */
	public static String getWorkflowInfo(List<Party> parties,
			Metadata nodePermissionPolicy, PageContext pageContext) {
		if (parties == null) {
			return null;
		}
		if (nodePermissionPolicy == null) {
			log.warn("协同节点权限原数据为null");
			return null;
		}

		StringBuffer sb = new StringBuffer();
		
		String sp = Functions._(pageContext, "common.separator.label");
		
		boolean isShowShortName = !"false".equals(String.valueOf(pageContext.findAttribute("isShowShortName")));
		int i = 0;
		for (Party party : parties) {
			if(i++ > 0){
				sb.append(sp);
			}
			
			if(isShowShortName){
				String shortname = party.getAccountShortName();
				if(Strings.isNotBlank(shortname) && !"null".equalsIgnoreCase(shortname) && !"undefined".equalsIgnoreCase(shortname)){
					sb.append("(").append(shortname).append(")");
				}
			}
			
			if("Role".equals(party.getType())){
				String key = "sys.role.rolename." + party.getId();
				String label = ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources", key);
				if(key.equals(label)){
					sb.append(party.getId());
				}
				else{
					sb.append(label);
				}
			}
			else{
                if(Strings.isNotBlank(party.getName())){
                    sb.append(party.getName());
                }else if("FormField".equals(party.getType()) && Strings.isNotBlank(party.getId())){
                	  sb.append(party.getId());
                }else{
                    //所选组织实体被删除
                    sb.append(ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "col.node.notExist"));
                }
			}

			String key = nodePermissionPolicy.getItemLabel(party.getPolicy());
			String label = "";
			if(key == null){
				label = party.getPolicy();
			}else{
				label = ResourceBundleUtil.getString(pageContext, key);
			}
			if(Strings.isNotBlank(label)){
				sb.append("(").append(label).append(")");
			}
		}

		return sb.toString();
	}

	/**
	 * 将原数据项的label做成JS常量
	 * 
	 * var jsVar = { MetadataItem.value : label(已经做完国际化转换) }
	 * 
	 * @param metadata
	 * @param jsVar
	 *            js变量名
	 * @param pageContext
	 * @return
	 */
	public static String listMetadataLable(Metadata metadata, String jsVar,
			PageContext pageContext) {
		if (metadata == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();

		List<MetadataItem> items = metadata.getItems();

		sb.append("var " + jsVar + " = {");
		if (items != null) {
			for (int i = 0; i < items.size(); i++) {
				MetadataItem item = items.get(i);
				if (i > 0) {
					sb.append(",\n");
				}

				String key = item.getLabel();
				String label = ResourceBundleUtil.getString(pageContext, key);

				sb.append(item.getValue() + " : \"" + StringEscapeUtils.escapeJavaScript(label) + "\"");
			}
		}

		sb.append("}");

		return sb.toString();
	}
	
	public static String showForwardInfo(PageContext pageContext, List<String> memberNames){
		if(memberNames != null){
			StringBuffer sb = new StringBuffer();
			
			for (String name : memberNames) {
				sb.append(ResourceBundleUtil.getString(pageContext, "col.forward.subject.suffix", name));
			}
			
			return sb.toString();
		}
		
		return null;
	}
	
	public static String getAttitudes(ColOpinion opinion, PageContext pageContext){
		int type = opinion.getOpinionType();
		String key = null;
		
		if(ColOpinion.OpinionType.backOpinion.ordinal() == type){ //回退
			key = "stepBack.label";
		}
		else if(ColOpinion.OpinionType.provisionalOpinoin.ordinal() == type){ //回退
			key = "zancundaiban.label";
		}
		else if(ColOpinion.OpinionType.stopOpinion.ordinal() == type){ //终止
			key = "stepStop.label";
		}
		else if(ColOpinion.OpinionType.cancelOpinion.ordinal() == type){ //撤销
			key = "repeal.label";
		}
		else if(ColOpinion.OpinionType.sysAutoSignOpinion.ordinal() == type){//自动跳过
			key = "sysautofinish.label";
		}
		else{
			return null;
		}
		
		return ResourceBundleUtil.getString(pageContext, key);
	}

	
	public static List<String[]> showDecreaseNode(FlowData flowData){
		if(flowData == null){
			return null;
		}
		
		List<Party> parties = flowData.getPeople();
		if(parties == null || parties.isEmpty()){
			return null;
		}
		
		Long accountId = CurrentUser.get().getAccountId();
		boolean isShowAccountName = false;
		for (Party party : parties) {
			Long entityAccountId = Long.parseLong(party.getAccountId());
//			if("true".equals(flowData.getIsShowShortName()) && entityAccountId != null && !entityAccountId.equals(accountId) && !Functions.isMyAccount(entityAccountId)){
			// !isMyAccount不应该作为判断条件，预减签【兼职单位部门节点】时，没显示单位名称。
			if("true".equals(flowData.getIsShowShortName()) && entityAccountId != null && !entityAccountId.equals(accountId)){				
				isShowAccountName = true;
				break;
			}
		}
		
		List<String[]> result = new ArrayList<String[]>();
		String sAccountId = String.valueOf(accountId);
		for (Party party : parties) {
            String str = "<input type=checkbox name=deletePeople value='" + party.getId() + "' pname='"+Strings.escapeJavascript(party.getName())+"' ptype='"+party.getType()+"'  paccountId='"+party.getAccountId()+"'  paccountShortName='"+Strings.escapeNULL(Strings.escapeJavascript(party.getAccountShortName()), "")+"' pActivityId='" + Strings.escapeNULL(party.getActivityId(), "") + "' />";
            String name = "";
            name += party.getName();
            String accountShortName = party.getAccountShortName();
            boolean isCurrentAccountMember = party.getAccountId().equals(sAccountId);
            if(isShowAccountName && Strings.isNotBlank(accountShortName) && !isCurrentAccountMember && !"null".equalsIgnoreCase(accountShortName)){
            	name +="(" + accountShortName + ")";
            }            
            
            result.add(new String[]{str, name});
		}
		
		return result;
	}
	
	private static OrgManager orgManager = null;
	private static OrgManager getOrgManager(){
		if(orgManager == null){
			orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
		}
		
		return orgManager;
	}
	private static ColManager colManager = null;
	private static ColManager getColManager(){
	    if(colManager == null){
            colManager = (ColManager) ApplicationContextHolder.getBean("colManager");
	    }
	    
	    return colManager;
	}
	private static HisColManager hisColManager = null;
	private static HisColManager getHisColManager(){
		if(hisColManager == null){
			hisColManager = (HisColManager) ApplicationContextHolder.getBean("hisColManager");
		}
		
		return hisColManager;
	}
	private static MetadataManager metadataManager;
	private static MetadataManager getMetaDataManager(){
		if(metadataManager == null){
			metadataManager =  (MetadataManager)ApplicationContextHolder.getBean("metadataManager");
		}
		return metadataManager;
	}
	
	public static String getOpinionAttitude(int attitude){
		String resource = "com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";
		Map<String, String> attitudes =getMetaDataManager().getMetadataItemLabelMap(MetadataNameEnum.collaboration_attitude);
		if(attitude > 0){
			String label = attitudes.get(String.valueOf(attitude));
			return ResourceBundleUtil.getString(resource, label);
		}
		return "";
	}
	/**
	 * 
	 * @param affair
	 * @param pageContext
	 * @return
	 */
	public static String showSubject(Affair affair, int length){
		if(affair == null){
			return null;
		}
		
		//String colProxyLabel = "";
		/*if(affair.getMemberId().longValue() != CurrentUser.get().getId()){ //代理
			colProxyLabel = "(" + Constant.getString4CurrentUser("col.proxy") + ")";
			length -= colProxyLabel.getBytes().length;
		}*/
        Integer resentTime = affair.getResentTime();
        String forwardMember = affair.getForwardMember();
        
        return ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), length, forwardMember, resentTime, getOrgManager(), null);// + colProxyLabel;
	}
	
	public static String showSubjectOfSummary(ColSummary summary, Boolean isProxy, int length, String proxyName){
		if(summary == null)
			return null;
		return showSubject(summary.getSubject(),summary.getForwardMember(),summary.getResentTime(),isProxy,length,proxyName,false);
	}
	
	/**
	 * 用于协同已办的显示
	 * @param summary
	 * @param isProxy
	 * @param length
	 * @param proxyName
	 * @param isAgentDeal
	 * @return
	 */
	public static String showSubjectOfSummary4Done(Affair affair, int length){
		if(affair == null)
			return null;
		V3xOrgMember member = null;
		String subject = "";
		String colProxyLabel = "";
		boolean isAgent = true;
		User user = CurrentUser.get();
		long userId = user.getId();
		if(affair.getTransactorId() != null){
			Long memberId = affair.getTransactorId();
			if(memberId.longValue()==userId){
	    		memberId = affair.getMemberId();
			}else{
				isAgent = false;
			}
			try {
				member = getOrgManager().getMemberById(memberId);
				if(member != null){
					if(isAgent)
						colProxyLabel = "(" + Constant.getString4CurrentUser("col.proxy") + member.getName() + ")";
					else
						colProxyLabel = "(" + member.getName() + Constant.getString4CurrentUser("col.proxy") + ")";
					length -= colProxyLabel.getBytes().length;
					subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), length, affair.getForwardMember(), affair.getResentTime(), getOrgManager(), null) + colProxyLabel;
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}else if(affair.getMemberId() != userId){
    		try{
    			member = orgManager.getMemberById(affair.getMemberId());
    			colProxyLabel = "(" + Constant.getString4CurrentUser("col.proxy.deal",member.getName()) + ")";
    			length -= colProxyLabel.getBytes().length;
				subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), length, affair.getForwardMember(), affair.getResentTime(), getOrgManager(), null) + colProxyLabel;
    		}catch(Exception e){
    			log.error("", e);
    		}
    	}else{
			subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), length, affair.getForwardMember(), affair.getResentTime(), getOrgManager(), null);
		}
		return subject;
	}
	
	public static String showSubjectOfEdocSummary(EdocSummary summary, Boolean isProxy, int length, String proxyName,Boolean isAgentDeal){
		if(summary == null)
			return null;
		return showSubject(summary.getSubject(),"",0,isProxy,length,proxyName,isAgentDeal);
	}
	
	public static String showSubjectOfInfoSummary(String subject, Boolean isProxy, int length, String proxyName,Boolean isAgentDeal){
		if(subject == null)
			return null;
		return showSubject(subject,"",0,isProxy,length,proxyName,isAgentDeal);
	}
	
	private static String showSubject(String subject, String forwardMember,Integer resendTiem, Boolean isProxy, int length, String proxyName,Boolean isAgentDeal){
		if(Strings.isEmpty(proxyName))
			return ColHelper.mergeSubjectWithForwardMembers(subject, length, forwardMember, resendTiem, getOrgManager(), null);
		String colProxyLabel = "";
		if(Boolean.TRUE.equals(isProxy)){
			List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(CurrentUser.get().getId());
	    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(CurrentUser.get().getId());
			boolean agentToFlag = false;
			if(_agentModelList != null && !_agentModelList.isEmpty()){
				agentToFlag = false;
			}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
				agentToFlag = true;
			}
			if(agentToFlag){
				colProxyLabel = "(" + proxyName + Constant.getString4CurrentUser("col.proxy") + ")";
			}else{
				if(isAgentDeal){      //被代理人自己处理
					colProxyLabel = "(" + Constant.getString4CurrentUser("col.proxy.deal",proxyName) + ")";
				}else{
					colProxyLabel = "(" + Constant.getString4CurrentUser("col.proxy") + proxyName + ")";
				}
			}
			length -= colProxyLabel.getBytes().length;
		}		
		return ColHelper.mergeSubjectWithForwardMembers(subject, length, forwardMember, resendTiem, getOrgManager(), null) + colProxyLabel;
	}
	
	/**
	 * 
	 * @param subject 原始标题
	 * @param subjectLength
	 * @param forwardMember
	 * @param resentTime
	 * @return
	 */
	public static String showSubject(String subject, int subjectLength, String forwardMember, Integer resentTime) {
		return ColHelper.mergeSubjectWithForwardMembers(subject, subjectLength, forwardMember, 0, getOrgManager(), null);
	}
	
    /***
     * 显示模板的创建者等详细信息
     */
    public static String showTempleteCreatorAlt(long memberId){
        User user = CurrentUser.get();
        if(user.getId() == memberId){
            return null;
        }
        
        V3xOrgMember member = Functions.getMember(memberId);
        if(member != null){
        	ResourceBundle rb = ResourceBundle.getBundle("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", user.getLocale());
        	String memberName = Functions.showMemberName(member);
            StringBuffer sb = new StringBuffer();
            sb.append(ResourceBundleUtil.getString(rb, "common.creater.label")).append(" : ");
            String s = Functions.showMemberAlt(member);
            if(Strings.isNotBlank(s)){
                sb.append(s);
            } else {
            	sb.append(memberName);
            }
            
            return sb.toString();
        } 
        else{
            return null;
        }
    }
      
    
    public static String getSubjectOfTemplate(String templateId){
    	if(Strings.isNotBlank(templateId)){
    		User user = CurrentUser.get();
    		return "(" + Functions.toHTML(user.getName()) + " " + Datetimes.formatDatetimeWithoutSecond(new Date()) + ")";
    	}
    	
    	return null;
    }

    public static ColSummary getSummaryById(Long summaryId){
        ColSummary summary = null;
        if(summaryId != null){
            try {
                summary = getColManager().getColSummaryById(summaryId, false);
            }
            catch (ColException e) {
                log.error("ColSummary不存在, id=" + summaryId);
            }
        }
        return summary;
    }
    public static ColSummary getSummaryByIdOrHis(Long summaryId){
    	if(summaryId != null){
    		ColSummary summary = getSummaryById(summaryId);
    		if(summary == null){
	    		try {
	    			summary = getHisColManager().getColSummaryById(summaryId, false);
	    		}
	    		catch (ColException e) {
	    			log.error("ColSummary不存在, id=" + summaryId);
	    		}
    		}
    		
    		return summary;
    	}
    	
    	return null;
    }
    
    public static String getSenderInfo(long memberId){
        V3xOrgMember member = Functions.getMember(memberId);
        if(member == null){
            return null;
        }
        String result = member.getName();
        V3xOrgDepartment dept = Functions.getDepartment(member.getOrgDepartmentId());
        V3xOrgPost post = Functions.getPost(member.getOrgPostId());
        if(dept != null){
            result += " (" + dept.getName() + (post != null ? " " + post.getName() : "") + ") ";
        }else{
        	if(post != null){
                result += " (" + post.getName() + ") ";
            }
        }
        return result;
    }
    
    /**
     * 是否可以发送全单位协同
     * 
     * @return
     */
    public static boolean isCanSendAccountColl(){
    	return ProductInfo.getMaxOnline() <= 1000;
    }
    
    public static String transformQuot(String before){
    	if(before.indexOf("\"") != -1){
    		return before.replace("\"", "\'").replace("&quot;", "\'");
    	}
    	return before;
    }
}