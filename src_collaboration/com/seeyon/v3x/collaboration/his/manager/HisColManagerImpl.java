/**
 * 
 */
package com.seeyon.v3x.collaboration.his.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.his.domain.HisAffair;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.his.domain.HisColBody;
import com.seeyon.v3x.collaboration.his.domain.HisColComment;
import com.seeyon.v3x.collaboration.his.domain.HisColOpinion;
import com.seeyon.v3x.collaboration.his.domain.HisColSummary;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-1-7
 */
public class HisColManagerImpl extends BaseDao<HisColSummary> implements HisColManager {
	
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void save(ColSummary summary){
		HisColSummary hisColSummary = new HisColSummary();
		
		cloneToHis(hisColSummary, summary);
		
		super.save(hisColSummary);
	}
	
	private void cloneToHis(HisColSummary hisColSummary, ColSummary s) {
		BeanUtils.convert(hisColSummary, s);
		{
			java.util.Set<ColBody> bs = s.getBodies();
			java.util.Set<HisColBody> nbs = new HashSet<HisColBody>();
			if(bs != null){
				for (ColBody b : bs) {
					HisColBody hb = new HisColBody();
					hb.clone(b);
					nbs.add(hb);
				}
			}
			hisColSummary.setBodies(nbs);
		}
		{
			java.util.Set<ColOpinion> ns = s.getOpinions();
			java.util.Set<HisColOpinion> nns = new HashSet<HisColOpinion>();
			if(ns != null){
				for (ColOpinion n : ns) {
					HisColOpinion hn = new HisColOpinion();
					hn.clone(n);
					nns.add(hn);
				}
			}
			hisColSummary.setOpinions(nns);
		}
		{
			java.util.Set<ColComment> ns = s.getComments();
			java.util.Set<HisColComment> nns = new HashSet<HisColComment>();
			if(ns != null){
				for (ColComment n : ns) {
					HisColComment hn = new HisColComment();
					hn.clone(n);
					nns.add(hn);
				}
			}
			hisColSummary.setComments(nns);
		}
	}
	
    public ColSummary getColSummaryById(long summaryId, boolean needBody) throws ColException {
        HisColSummary hisSummary = super.get(summaryId);
        if(hisSummary == null)
        	return null;
        
        ColSummary summary = new ColSummary();
        BeanUtils.convert(summary, hisSummary);
        
		if (needBody) {
			java.util.Set<HisColBody> bs = hisSummary.getBodies();
			java.util.Set<ColBody> nbs = new HashSet<ColBody>();
			if(bs != null){
				for (HisColBody b : bs) {
					ColBody hb = b.toColBody();
					nbs.add(hb);
				}
			}
			
			summary.setBodies(nbs);
		}

        return summary;
    }
    
    public ColSummary getColAllById(long summaryId) throws ColException {
        HisColSummary hisSummary = super.get(summaryId);
		if(hisSummary == null) return null;

    	if(hisSummary.getOpinions()!=null)hisSummary.getOpinions().size();
    	if(hisSummary.getComments()!=null)hisSummary.getComments().size();
    	if(hisSummary.getBodies()!=null)hisSummary.getBodies().size();
            hisSummary.getFirstBody();
            
        ColSummary summary = new ColSummary();
        BeanUtils.convert(summary, hisSummary);
		{
			java.util.Set<HisColBody> bs = hisSummary.getBodies();
			java.util.Set<ColBody> nbs = new HashSet<ColBody>();
			if(bs != null){
				for (HisColBody b : bs) {
					nbs.add(b.toColBody());
				}
			}
			summary.setBodies(nbs);
		}
		{
			java.util.Set<HisColOpinion> ns = hisSummary.getOpinions();
			java.util.Set<ColOpinion> nns = new HashSet<ColOpinion>();
			if(ns != null){
				for (HisColOpinion n : ns) {
					nns.add(n.toColOpinion());
				}
			}
			summary.setOpinions(nns);
		}
		{
			java.util.Set<HisColComment> ns = hisSummary.getComments();
			java.util.Set<ColComment> nns = new HashSet<ColComment>();
			if(ns != null){
				for (HisColComment n : ns) {
					nns.add(n.toColComment());
				}
			}
			summary.setComments(nns);
		}
		
        return summary;
    }
    
    public List<ResultModel> iSearch(ConditionModel cModel){
    	User user = CurrentUser.get();
    	Map<String, Object> parameterMap = new HashMap<String, Object>();
    	StringBuffer sb = new StringBuffer();
    	
    	String title0 = cModel.getTitle();
		final java.util.Date beginDate = cModel.getBeginDate();
		final java.util.Date endDate = cModel.getEndDate();
		Long fromUserId = cModel.getFromUserId();
		Long archiveId = cModel.getDocLibId(); ////归档ID
		List<Integer> stateList = new ArrayList<Integer>();
        sb.append("select affair from "+ HisAffair.class.getName() +" as affair");
        if(cModel.getPigeonholedFlag() && archiveId != null){
            //sb.append("," + ColSummary.class.getName() + " as col");
            sb.append("," + DocResource.class.getName() + " as doc");
        }
        parameterMap.put("APP", ApplicationCategoryEnum.collaboration.key());
		
        sb.append(" where ");
        boolean hasSenderId = false;
        if(fromUserId != null && !fromUserId.equals(user.getId())){
        	//指定认发给我的
        	sb.append(" affair.memberId=:userId2 ");
			parameterMap.put("userId2", user.getId());
            stateList.add(StateEnum.col_pending.key());
            stateList.add(StateEnum.col_done.key());
        	hasSenderId = true;
        }else if(fromUserId != null){
        	//我发送到
        	sb.append(" affair.memberId=:userId1 ");
        	parameterMap.put("userId1", fromUserId);
        	stateList.add(StateEnum.col_sent.key());
        }else{
        	//别人发给我的
        	sb.append(" affair.memberId=:userId3");
			parameterMap.put("userId3", user.getId());			
            stateList.add(StateEnum.col_pending.key());
            stateList.add(StateEnum.col_done.key());
        }
        sb.append(" and affair.state in(:stateList) and affair.app=:APP and affair.isDelete=false ");
		if(cModel.getPigeonholedFlag() && archiveId != null){
		    sb.append(" and doc.docLibId =:archiveId and doc.id=affair.archiveId");
		    parameterMap.put("archiveId", archiveId);
		}
		else{
		    sb.append(" and affair.archiveId is null");                    
		}
		if(hasSenderId){
            sb.append(" and affair.senderId=:userId1 ");
			parameterMap.put("userId1", fromUserId);
		}
		
        parameterMap.put("stateList", stateList);
		
		if(Strings.isNotBlank(title0)){
			sb.append(" and affair.subject like :subject ");
			parameterMap.put("subject", "%" + title0 + "%");
		}
		if(beginDate != null){
			sb.append(" and affair.createDate >= :begin");
			parameterMap.put("begin", beginDate);
		}
		if(endDate != null){
			sb.append(" and affair.createDate <= :end");
			parameterMap.put("end", endDate);
		}
		sb.append(" order by affair.createDate desc");
		final String hsql = sb.toString();
		
		List<HisAffair> list = super.find(hsql, parameterMap);
		List<ResultModel> ret = new ArrayList<ResultModel>();
		String locationPre = Constant.getString4CurrentUser("collaboration.information.label");
		String locationSuf = Constant.getString4CurrentUser("collaboration.information.store.label");
		if(list != null){
			for(HisAffair affair : list){
                Integer resentTime = affair.getResentTime();
                String forwardMember = affair.getForwardMember();
                String title = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), 80, forwardMember, resentTime, orgManager, null);
                
				V3xOrgMember member = null;
				try {
					member = orgManager.getMemberById(affair.getSenderId());
				} catch (BusinessException e) {
					
				}
				String fromUserName = member.getName();
				String locationSuffix = null;
				if(affair.getState() == StateEnum.col_pending.key()){
					locationSuffix = Constant.getString4CurrentUser("col.coltype.Pending.label");
				}else if(affair.getState() == StateEnum.col_done.key()){
					locationSuffix = Constant.getString4CurrentUser("col.coltype.Done.label");
				}else{
					locationSuffix = Constant.getString4CurrentUser("col.coltype.Sent.label");
				}
				
				String location = locationPre + "-" + locationSuffix + "-" + locationSuf;
				
				String link = "/collaboration.do?method=detail&from=Done&affairId=" + affair.getId();
				String bodyType = affair.getBodyType();
				boolean hasAttachments = affair.isHasAttachments();
				ResultModel rm = new ResultModel(title, fromUserName, affair.getCreateDate(), location, link,bodyType,hasAttachments);
				ret.add(rm);
			}
		}
		
		return ret;
    }
    
    public List<ColSummary> getSummaryIdByFormIdAndRecordId(Long formAppId, Long formId, Long formRecordId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(HisColSummary.class)
									.add(Expression.eq("formAppId", formAppId))
									//.add(Expression.eq("formId", formId))
									.add(Expression.eq("formRecordId", formRecordId));
		
		List<HisColSummary> l = super.executeCriteria(criteria, -1, -1);
		if(l.size() == 0)
			return null;
		else{
			List<ColSummary> r = new ArrayList<ColSummary>(l.size());
			for (HisColSummary c : l) {
		        ColSummary summary = new ColSummary();
		        BeanUtils.convert(summary, c);
		        r.add(summary);
			}
			
			return r;
		}
	}
}
