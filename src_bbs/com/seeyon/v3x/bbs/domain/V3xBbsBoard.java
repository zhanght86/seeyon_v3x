package com.seeyon.v3x.bbs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.util.Strings;

/**
 * 
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-9-13
 */
public class V3xBbsBoard extends BaseModel implements Serializable,Comparator<V3xBbsBoard> {

	private static final long serialVersionUID = -366539503894691650L;

	private Byte anonymousFlag;
	
	//讨论版块增加一项设定：是否允许匿名回复 added by Meng Yang 2009-05-11
	private Byte anonymousReplyFlag;

	private int affiliateroomFlag;

	private Long accountId;

	private String description;

	private String name;

	private Integer topNumber;

	private java.sql.Timestamp boardTime;
	
//	排序字段
	private Integer  sort = 0;
	
//	回复排序类型（升序，降序）
	private Integer orderFlag=0;


	/**
	 * 管理员
	 */
	private List<Long> admins;

	/**
	 * 发帖人员
	 */
	private List<Long> generalMember;
	
	private List<V3xOrgEntity> issuerList = new ArrayList<V3xOrgEntity>();

	/**
	 * 禁止回复人员
	 */
	private List<Long> notReplyMember;
	
	private List<V3xOrgEntity> canNotReplyList = new ArrayList<V3xOrgEntity>();
	
	public List<V3xOrgEntity> getCanNotReplyList() {
		return canNotReplyList;
	}

	public void setCanNotReplyList(List<V3xOrgEntity> canNotReplyList) {
		this.canNotReplyList = canNotReplyList;
	}

	public List<V3xOrgEntity> getIssuerList() {
		return issuerList;
	}

	public void setIssuerList(List<V3xOrgEntity> issuerList) {
		this.issuerList = issuerList;
	}

	public V3xBbsBoard() {
	}

	public byte getAnonymousFlag() {
		return this.anonymousFlag;
	}

	public void setAnonymousFlag(byte anonymousFlag) {
		this.anonymousFlag = anonymousFlag;
	}

	public int getAffiliateroomFlag() {
		return affiliateroomFlag;
	}

	public void setAffiliateroomFlag(int affiliateroomFlag) {
		this.affiliateroomFlag = affiliateroomFlag;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTopNumber() {
		return this.topNumber;
	}

	public void setTopNumber(Integer topNumber) {
		this.topNumber = topNumber;
	}

	public void setAnonymousFlag(Byte anonymousFlag) {
		this.anonymousFlag = anonymousFlag;
	}

	public void setAffiliateroomFlag(Byte affiliateroomFlag) {
		this.affiliateroomFlag = affiliateroomFlag;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public java.sql.Timestamp getBoardTime() {
		return boardTime;
	}

	public void setBoardTime(java.sql.Timestamp boardTime) {
		this.boardTime = boardTime;
	}

	public List<Long> getGeneralMember() {
		if(this.generalMember == null){
			generalMember = new ArrayList<Long>();
		}
		return generalMember;
	}

	public void setGeneralMember(List<Long> generalMember) {
		this.generalMember = generalMember;
	}

	public List<Long> getNotReplyMember() {
		if(this.notReplyMember == null){
			notReplyMember = new ArrayList<Long>();
		}
		return notReplyMember;
	}

	public void setNotReplyMember(List<Long> notReplyMember) {
		this.notReplyMember = notReplyMember;
	}

	public List<Long> getAdmins() {
		if(this.admins == null){
			admins = new ArrayList<Long>();
		}
		
		return admins;
	}

	public void setAdmins(List<Long> admins) {
		this.admins = admins;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public Integer getOrderFlag() {
		return orderFlag;
	}

	public void setOrderFlag(Integer orderFlag) {
		this.orderFlag = orderFlag;
	}
	
	/**
	 * 排序：按照用户设定的排序号和板块创建日期进行排序
	 */
	public int compare(V3xBbsBoard o1, V3xBbsBoard o2) {
		V3xBbsBoard p1=(V3xBbsBoard)o1;
		V3xBbsBoard p2=(V3xBbsBoard)o2;
		if(p1.sort.intValue()>p2.sort.intValue()) {
			return 1;
		} else if(p1.sort.intValue()<p2.sort.intValue()) {
			return -1;
		} else {
			if(p1.getBoardTime().getTime() > p2.getBoardTime().getTime()) {
				return -1;
			} else if(p1.getBoardTime().getTime() < p2.getBoardTime().getTime()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public Byte getAnonymousReplyFlag() {
		return anonymousReplyFlag;
	}

	public void setAnonymousReplyFlag(Byte anonymousReplyFlag) {
		this.anonymousReplyFlag = anonymousReplyFlag;
	}
	
	/**
	 * 得到允许发帖或禁止回帖的对象Type|Ids
	 * @param authType  授权类型
	 */
	public String getAuthInfo(String authType) {
		List<V3xOrgEntity> auth = null;
		if(BbsConstants.AUTH_TO_POST.equals(authType)) {
			auth = this.getIssuerList();
		} else if(BbsConstants.FORBIDDEN_TO_REPLY.equals(authType)) {
			auth = this.getCanNotReplyList();
		}
		StringBuffer result = new StringBuffer("");
		if(auth!=null && auth.size()>0) {
			for(V3xOrgEntity ent : auth) {
				result.append(ent.getEntityType() + "|" + ent.getId() + ",");
			}
		}
		return Strings.isNotBlank(result.toString()) ? result.substring(0, result.length()-1) : result.toString();
	}

}