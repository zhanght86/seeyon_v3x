package com.seeyon.v3x.office.myapply.domain;

import java.util.Date;


/**
 * TApplylist generated by MyEclipse - Hibernate Tools
 */

public class TApplylist  implements java.io.Serializable {


    // Fields    

     private Long applyId;
     private Long applyUsername;
     private Long applyDepId;
     private Long applyUser;
     private Long applyUsedep;
     private Date applyDate;
     private Integer applyState;
     private Integer applyType;//1车辆 2 设备 3 办公用品
     private String applyMemo;
     private Long applyMge;
     private Long applyExam;
     private Date auditTime;
     private Integer delFlag;
     
     /**
      * other
      */
     private String person_Name; //applyUsername;
     private String dep_Name;    //applyDepId;
     private long office_Id;
     private String office_Name;
     private Date start_date;
     private Date end_date;
     private String purpose;
     private long applyCount;
     private int storageStatus=0;
     private String className;
     private long departCount=0;

    // Constructors

    /** default constructor */
    public TApplylist() {
    }

	/** minimal constructor */
    public TApplylist(Long applyId, Integer delFlag) {
        this.applyId = applyId;
        this.delFlag = delFlag;
    }
    
    /** full constructor */
    public TApplylist(Long applyId, Long applyUsername, Long applyDepId, Long applyUser, Long applyUsedep, Date applyDate, Integer applyState, Integer applyType, String applyMemo, Long applyMge, Long applyExam, Date auditTime, Integer delFlag) {
        this.applyId = applyId;
        this.applyUsername = applyUsername;
        this.applyDepId = applyDepId;
        this.applyUser = applyUser;
        this.applyUsedep = applyUsedep;
        this.applyDate = applyDate;
        this.applyState = applyState;
        this.applyType = applyType;
        this.applyMemo = applyMemo;
        this.applyMge = applyMge;
        this.applyExam = applyExam;
        this.auditTime = auditTime;
        this.delFlag = delFlag;
    }

   
    // Property accessors

    public Long getApplyId() {
        return this.applyId;
    }
    
    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public Long getApplyUsername() {
        return this.applyUsername;
    }
    
    public void setApplyUsername(Long applyUsername) {
        this.applyUsername = applyUsername;
    }

    public Long getApplyDepId() {
        return this.applyDepId;
    }
    
    public void setApplyDepId(Long applyDepId) {
        this.applyDepId = applyDepId;
    }

    public Long getApplyUser() {
        return this.applyUser;
    }
    
    public void setApplyUser(Long applyUser) {
        this.applyUser = applyUser;
    }

    public Long getApplyUsedep() {
        return this.applyUsedep;
    }
    
    public void setApplyUsedep(Long applyUsedep) {
        this.applyUsedep = applyUsedep;
    }

    public Date getApplyDate() {
        return this.applyDate;
    }
    
    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public Integer getApplyState() {
        return this.applyState;
    }
    
    public void setApplyState(Integer applyState) {
        this.applyState = applyState;
    }

    public Integer getApplyType() {
        return this.applyType;
    }
    
    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }

    public String getApplyMemo() {
        return this.applyMemo;
    }
    
    public void setApplyMemo(String applyMemo) {
        this.applyMemo = applyMemo;
    }

    public Long getApplyMge() {
        return this.applyMge;
    }
    
    public void setApplyMge(Long applyMge) {
        this.applyMge = applyMge;
    }

    public Long getApplyExam() {
        return this.applyExam;
    }
    
    public void setApplyExam(Long applyExam) {
        this.applyExam = applyExam;
    }

    public Date getAuditTime() {
        return this.auditTime;
    }
    
    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Integer getDelFlag() {
        return this.delFlag;
    }
    
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
   

    /**
     * other
     */

	public String getDep_Name() {
		return dep_Name;
	}

	public void setDep_Name(String dep_Name) {
		this.dep_Name = dep_Name;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public long getOffice_Id() {
		return office_Id;
	}

	public void setOffice_Id(long office_Id) {
		this.office_Id = office_Id;
	}

	public String getOffice_Name() {
		return office_Name;
	}

	public void setOffice_Name(String office_Name) {
		this.office_Name = office_Name;
	}

	public String getPerson_Name() {
		return person_Name;
	}

	public void setPerson_Name(String person_Name) {
		this.person_Name = person_Name;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public long getApplyCount() {
		return applyCount;
	}

	public void setApplyCount(long applyCount) {
		this.applyCount = applyCount;
	}

	public int getStorageStatus() {
		return storageStatus;
	}

	public void setStorageStatus(int storageStatus) {
		this.storageStatus = storageStatus;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getDepartCount() {
		return departCount;
	}

	public void setDepartCount(long departCount) {
		this.departCount = departCount;
	}






}