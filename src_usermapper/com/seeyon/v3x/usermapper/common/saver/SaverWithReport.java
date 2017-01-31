package com.seeyon.v3x.usermapper.common.saver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.usermapper.report.domain.ReportDetail;

public abstract class SaverWithReport implements Saver {

	protected List<ReportDetail> rds=new ArrayList<ReportDetail>();
	
	public List<ReportDetail> getReportDetails(){
		List<ReportDetail> nrds=new ArrayList<ReportDetail>();
		nrds.addAll(rds);
		rds=null;
		return nrds;
	}
	
	public List<ReportDetail> getRds(){
		if(this.rds==null)
			this.rds=new ArrayList<ReportDetail>();
		
		return this.rds;
	}
	
	public void appendRd(ReportDetail rd){		
		if(rd!=null)
			this.getRds().add(rd);
	}
	
	protected ReportDetail newReportDetail(){
		ReportDetail rd=new ReportDetail();
		
		rd.setId(UUID.randomUUID().getLeastSignificantBits());
		
		return rd;
	}
}//end class
