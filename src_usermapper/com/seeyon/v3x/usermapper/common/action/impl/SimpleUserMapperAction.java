package com.seeyon.v3x.usermapper.common.action.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;
import com.seeyon.v3x.usermapper.NoMethodException;
import com.seeyon.v3x.usermapper.common.action.UserMapperAction;
import com.seeyon.v3x.usermapper.common.constants.RefreshUserMapperPolice;
import com.seeyon.v3x.usermapper.report.domain.ReportDetail;

public abstract class SimpleUserMapperAction implements UserMapperAction {
	
	protected List<ReportDetail> rds=new ArrayList<ReportDetail>();
	
	public List<ReportDetail> getReportDetails(){
		List<ReportDetail> nrds=new ArrayList<ReportDetail>();
		nrds.addAll(rds);
		rds=null;
		return nrds;
	}
	
	protected ReportDetail newReportDetail(){
		ReportDetail rd=new ReportDetail();
		
		rd.setId(UUID.randomUUID().getLeastSignificantBits());
		
		return rd;
	}

	public void begin(RefreshUserMapperPolice police)throws Exception {
		// TODO Auto-generated method stub

	}

	public void execute(V3xOrgUserMapper um) throws NoMethodException,Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public void execute(List<V3xOrgUserMapper> ums) throws NoMethodException,Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public void execute4Login(Map<String, List<V3xOrgUserMapper>> ums)
			throws NoMethodException,Exception {
		// TODO Auto-generated method stub

	}

	public void execute4LoginExLogin(Map<String, List<String>> ums)
			throws NoMethodException,Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public void execute4LoginExLogin(String login, List<String> exLogins)
			throws NoMethodException,Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public void ok() {
		// TODO Auto-generated method stub
	}

}//end class
