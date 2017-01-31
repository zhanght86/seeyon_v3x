package com.seeyon.v3x.indexresume.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyong
 *
 */
public class IndexResumeInfo{
   public boolean isEnableState() {
		return enableState;
	}
	public void setEnableState(boolean enableState) {
		this.enableState = enableState;
	}
	public String getStarHour() {
		return starHour;
	}
	public void setStarHour(String starHour) {
		this.starHour = starHour;
	}
	public String getStarMin() {
		return starMin;
	}
	public void setStarMin(String starMin) {
		this.starMin = starMin;
	}
	public String getEndHour() {
		return endHour;
	}
	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}
	public String getEndMin() {
		return endMin;
	}
	public void setEndMin(String endMin) {
		this.endMin = endMin;
	}
	public String[] getAppType() {
		return appType;
	}
	public void setAppType(String[] appType) {
		this.appType = appType;
	}
	public String getResumeStarDate() {
		return resumeStarDate;
	}
	public void setResumeStarDate(String resumeStarDate) {
		this.resumeStarDate = resumeStarDate;
	}
	public String getResumeEndDate() {
		return resumeEndDate;
	}
	public void setResumeEndDate(String resumeEndDate) {
		this.resumeEndDate = resumeEndDate;
	}
	
   private boolean enableState=false;//默认不启用
   private String starHour;
   private String starMin;
   private String endHour;
   private String endMin;
   private String[] appType;
   private String resumeStarDate;
   private String resumeEndDate;
   public List<resumeInfo> getResumeList() {
	return resumeList;
}
   private List<resumeInfo> resumeList;
   public void add(String appType,String startDate,String endDate){
	   if(resumeList==null){
		   resumeList=new ArrayList<resumeInfo>();
	   }
	   resumeList.add(new resumeInfo(appType,startDate,endDate));
   }
   public class resumeInfo
   {
	   public int getAppType() {
		return appType;
	       }
	 void setAppType(int appType) {
		this.appType = appType;
	                        }
	   int appType;
	   String startDate4Resume;
	   String endDate4Resume;
	   resumeInfo(String appType,String startDate,String endDate){
		   this.appType=Integer.parseInt(appType);
		   this.startDate4Resume=startDate;
		   this.endDate4Resume=endDate;
	   }
	   public String getStartDate4Resume() {
		   return startDate4Resume;
	   }
	   void setStartDate4Resume(String startDate4Resume) {
		   this.startDate4Resume = startDate4Resume;
	   }
	   public String getEndDate4Resume() {
		   return endDate4Resume;
	   }
	   void setEndDate4Resume(String endDate4Resume) {
		   this.endDate4Resume = endDate4Resume;
	   }
   }
}
