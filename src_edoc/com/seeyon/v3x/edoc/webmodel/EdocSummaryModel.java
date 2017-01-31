package com.seeyon.v3x.edoc.webmodel;

import java.sql.Date;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.util.Strings;

/**
 * @author Yang.Yinghai
 * @date 2011-10-26下午03:14:31
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class EdocSummaryModel {

    /**
     * 公文类型：待办、待发、已办、已发
     */
    public static enum EDOCTYPE{
        WaitSend, Sent, Pending, Done
    }

    /**  */
    private String edocType;

    /** 是否代理 */
    private boolean proxy;

    /** 代理人 */
    private String proxyName;

    /** 是否有附件 */
    private boolean hasAttachments;

    /** 正文类型 */
    private String bodyType;

    /** 流程是否结束 */
    private boolean finshed;

    /** 待办公文状态：未读、待办、暂存待办、已发待发公文状态：草稿、回退、撤销 */
    private int state;

    /** 是否跟踪该事项 */
    private boolean isTrack;

    /**  */
    private Affair affair;

    /**  */
    private EdocSummary summary;

    /** 催办次数 */
    private int hastenTimes;

    private Date startDate;

    private Long advanceRemindTime;

    private Long deadLine;

    private Date dealTime;

    private String nodePolicy;

    /** 归档逻辑路径 */
    private String logicalPath;

    /** 归档文件件名称 */
    private String archiveName;

    /** 是否代理人处理 */
    private boolean isAgentDeal;

    /** 政务公文类型 */
    private String administrative;

    /** 党务公文类型 */
    private String party;

    /**  */
    private String workitemId;

    /**  */
    private String caseId;

    /**  */
    private Long affairId;

    /**  */
    private boolean overtopTime;

    /** 送往单位 */
    private String sendToUnit;

    /** 办理剩余时间（=处理期限（自然日）-（现在的日-收到待办日-非工作日）） */
    private int[] surplusTime;
    
    private Date zcdbTime; //暂存待办提醒时间
    
    /**
     * cy add
     */
    private java.sql.Timestamp recieveDate;//来文日期(签收时间) 
    private String sender;		//分发人(发文)
    private String cydw;//成文单位
	public String getCydw() {
		return cydw;
	}
	public void setCydw(String cydw) {
		this.cydw = cydw;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public java.sql.Timestamp getRecieveDate() {
		return recieveDate;
	}
	public void setRecieveDate(java.sql.Timestamp recieveDate) {
		this.recieveDate = recieveDate;
	}
	
	
	//GOV-506 收文登记薄中自定义分类的备选项不全 -----------------------收文登记簿增加----------------
	//
	private String signer;	//增加 会签人  (收文登记簿需要)
	private String registerUserName;	//登记人
	private java.sql.Date registerDate;	//登记时间
	private String distributer;	//分发人
	
	//branches_a8_v350_r_gov  GOV-3338 常屹  打开收文后没有关联发文记录  start
	private int registerType; //登记类型  (电子 1，纸质 2)，主要用于收文关联发文时，如果是电子公文，那么在待登记 登记 待分发 分发流程中关联发文所对应的
							//关联表edoc_summary_relation中summary_id用的是公文表id，如果是纸质公文，那么就要用登记表id,那么在从各个列表点击转发文
							//按钮的时候，调用showForwardWDTwo  js方法中，就要判断当前公文是纸质的还是电子的
	private long edocId;	//公文id(解决待登记的问题)
	private long registerId; //登记id
	//branches_a8_v350_r_gov  GOV-3338 常屹  打开收文后没有关联发文记录  end
	
	public long getRegisterId() {
		return registerId;
	}
	public void setRegisterId(long registerId) {
		this.registerId = registerId;
	}
	public long getEdocId() {
		return edocId;
	}
	public void setEdocId(long edocId) {
		this.edocId = edocId;
	}
	public String getSigner() {
		return signer;
	}
	public int getRegisterType() {
		return registerType;
	}
	public void setRegisterType(int registerType) {
		this.registerType = registerType;
	}
	public void setSigner(String signer) {
		this.signer = signer;
	}
	
	public String getDistributer() {
		return distributer;
	}
	public void setDistributer(String distributer) {
		this.distributer = distributer;
	}
	public String getRegisterUserName() {
		return registerUserName;
	}
	public void setRegisterUserName(String registerUserName) {
		this.registerUserName = registerUserName;
	}
	public java.sql.Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(java.sql.Date registerDate) {
		this.registerDate = registerDate;
	}
	
	
	//GOV-506 收文登记薄中自定义分类的备选项不全 -----------------------收文登记簿增加----------------
	
	
	
	
    public void setSummary(EdocSummary edocSummary) {
        this.summary = edocSummary;
        // 校验 发文单位ID数据
        if(edocSummary != null && !Strings.isBlank(edocSummary.getSendUnitId())) {
            if(edocSummary.getSendUnitId().indexOf("|") < 0) {
                edocSummary.setSendUnitId("Account|" + edocSummary.getSendUnitId());
            }
        }
        if(edocSummary != null && !Strings.isBlank(edocSummary.getSendUnitId2())) {
            if(edocSummary.getSendUnitId2().indexOf("|") < 0) {
                edocSummary.setSendUnitId2("Account|" + edocSummary.getSendUnitId2());
            }
        }
    }

	/**
     * 获取edocType
     * @return edocType
     */
    public String getEdocType() {
        return edocType;
    }

    /**
     * 设置edocType
     * @param edocType edocType
     */
    public void setEdocType(String edocType) {
        this.edocType = edocType;
    }

    /**
     * 获取proxy
     * @return proxy
     */
    public boolean isProxy() {
        return proxy;
    }

    /**
     * 设置proxy
     * @param proxy proxy
     */
    public void setProxy(boolean proxy) {
        this.proxy = proxy;
    }

    /**
     * 获取proxyName
     * @return proxyName
     */
    public String getProxyName() {
        return proxyName;
    }

    /**
     * 设置proxyName
     * @param proxyName proxyName
     */
    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    /**
     * 获取hasAttachments
     * @return hasAttachments
     */
    public boolean isHasAttachments() {
        return hasAttachments;
    }

    /**
     * 设置hasAttachments
     * @param hasAttachments hasAttachments
     */
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    /**
     * 获取bodyType
     * @return bodyType
     */
    public String getBodyType() {
        return bodyType;
    }

    /**
     * 设置bodyType
     * @param bodyType bodyType
     */
    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    /**
     * 获取finshed
     * @return finshed
     */
    public boolean isFinshed() {
        return finshed;
    }

    /**
     * 设置finshed
     * @param finshed finshed
     */
    public void setFinshed(boolean finshed) {
        this.finshed = finshed;
    }

    /**
     * 获取state
     * @return state
     */
    public int getState() {
        return state;
    }

    /**
     * 设置state
     * @param state state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 获取isTrack
     * @return isTrack
     */
    public boolean getIsTrack() {
        return isTrack;
    }

    /**
     * 设置isTrack
     * @param isTrack isTrack
     */
    public void setIsTrack(boolean isTrack) {
        this.isTrack = isTrack;
    }

    /**
     * 获取affair
     * @return affair
     */
    public Affair getAffair() {
        return affair;
    }

    /**
     * 设置affair
     * @param affair affair
     */
    public void setAffair(Affair affair) {
        this.affair = affair;
    }

    /**
     * 获取hastenTimes
     * @return hastenTimes
     */
    public int getHastenTimes() {
        return hastenTimes;
    }

    /**
     * 设置hastenTimes
     * @param hastenTimes hastenTimes
     */
    public void setHastenTimes(int hastenTimes) {
        this.hastenTimes = hastenTimes;
    }

    /**
     * 获取startDate
     * @return startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 设置startDate
     * @param startDate startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 获取advanceRemindTime
     * @return advanceRemindTime
     */
    public Long getAdvanceRemindTime() {
        return advanceRemindTime;
    }

    /**
     * 设置advanceRemindTime
     * @param advanceRemindTime advanceRemindTime
     */
    public void setAdvanceRemindTime(Long advanceRemindTime) {
        this.advanceRemindTime = advanceRemindTime;
    }

    /**
     * 获取deadLine
     * @return deadLine
     */
    public Long getDeadLine() {
        return deadLine;
    }

    /**
     * 设置deadLine
     * @param deadLine deadLine
     */
    public void setDeadLine(Long deadLine) {
        this.deadLine = deadLine;
    }

    /**
     * 获取dealTime
     * @return dealTime
     */
    public Date getDealTime() {
        return dealTime;
    }

    /**
     * 设置dealTime
     * @param dealTime dealTime
     */
    public void setDealTime(Date dealTime) {
        this.dealTime = dealTime;
    }

    /**
     * 获取nodePolicy
     * @return nodePolicy
     */
    public String getNodePolicy() {
        return nodePolicy;
    }

    /**
     * 设置nodePolicy
     * @param nodePolicy nodePolicy
     */
    public void setNodePolicy(String nodePolicy) {
        this.nodePolicy = nodePolicy;
    }

    /**
     * 获取logicalPath
     * @return logicalPath
     */
    public String getLogicalPath() {
        return logicalPath;
    }

    /**
     * 设置logicalPath
     * @param logicalPath logicalPath
     */
    public void setLogicalPath(String logicalPath) {
        this.logicalPath = logicalPath;
    }

    /**
     * 获取archiveName
     * @return archiveName
     */
    public String getArchiveName() {
        return archiveName;
    }

    /**
     * 设置archiveName
     * @param archiveName archiveName
     */
    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    /**
     * 获取isAgentDeal
     * @return isAgentDeal
     */
    public boolean isAgentDeal() {
        return isAgentDeal;
    }

    /**
     * 设置isAgentDeal
     * @param isAgentDeal isAgentDeal
     */
    public void setAgentDeal(boolean isAgentDeal) {
        this.isAgentDeal = isAgentDeal;
    }

    /**
     * 获取administrative
     * @return administrative
     */
    public String getAdministrative() {
        return administrative;
    }

    /**
     * 设置administrative
     * @param administrative administrative
     */
    public void setAdministrative(String administrative) {
        this.administrative = administrative;
    }

    /**
     * 获取party
     * @return party
     */
    public String getParty() {
        return party;
    }

    /**
     * 设置party
     * @param party party
     */
    public void setParty(String party) {
        this.party = party;
    }

    /**
     * 获取workitemId
     * @return workitemId
     */
    public String getWorkitemId() {
        return workitemId;
    }

    /**
     * 设置workitemId
     * @param workitemId workitemId
     */
    public void setWorkitemId(String workitemId) {
        this.workitemId = workitemId;
    }

    /**
     * 获取caseId
     * @return caseId
     */
    public String getCaseId() {
        return caseId;
    }

    /**
     * 设置caseId
     * @param caseId caseId
     */
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    /**
     * 获取affairId
     * @return affairId
     */
    public Long getAffairId() {
        return affairId;
    }

    /**
     * 设置affairId
     * @param affairId affairId
     */
    public void setAffairId(Long affairId) {
        this.affairId = affairId;
    }

    /**
     * 获取overtopTime
     * @return overtopTime
     */
    public boolean isOvertopTime() {
        return overtopTime;
    }

    /**
     * 设置overtopTime
     * @param overtopTime overtopTime
     */
    public void setOvertopTime(boolean overtopTime) {
        this.overtopTime = overtopTime;
    }

    /**
     * 获取sendToUnit
     * @return sendToUnit
     */
    public String getSendToUnit() {
        return sendToUnit;
    }

    /**
     * 设置sendToUnit
     * @param sendToUnit sendToUnit
     */
    public void setSendToUnit(String sendToUnit) {
        this.sendToUnit = sendToUnit;
    }

    /**
     * 获取summary
     * @return summary
     */
    public EdocSummary getSummary() {
        return summary;
    }

    /**
     * 获取surplusTime
     * @return surplusTime
     */
    public int[] getSurplusTime() {
        return surplusTime;
    }

    /**
     * 设置surplusTime
     * @param surplusTime surplusTime
     */
    public void setSurplusTime(int[] surplusTime) {
        this.surplusTime = surplusTime;
    }
	public Date getZcdbTime() {
		return zcdbTime;
	}
	public void setZcdbTime(Date zcdbTime) {
		this.zcdbTime = zcdbTime;
	}
    
    
}
