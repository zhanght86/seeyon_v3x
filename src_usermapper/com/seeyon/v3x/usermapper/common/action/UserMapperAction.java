package com.seeyon.v3x.usermapper.common.action;

import java.util.List;
import java.util.Map;
 
import com.seeyon.v3x.usermapper.NoMethodException;
import com.seeyon.v3x.usermapper.common.constants.RefreshUserMapperPolice;
import com.seeyon.v3x.usermapper.report.domain.ReportDetail;
import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;

public interface UserMapperAction {
	public void begin(RefreshUserMapperPolice police)throws Exception;
	public void ok();
	
	public void execute(V3xOrgUserMapper um)throws NoMethodException,Exception;
	public void execute(List<V3xOrgUserMapper> ums)throws NoMethodException,Exception;	
	public void execute4Login(Map<String,List<V3xOrgUserMapper>> ums)throws NoMethodException,Exception;
	public void execute4LoginExLogin(Map<String,List<String>> ums)throws NoMethodException,Exception;
	public void execute4LoginExLogin(String login,List<String> exLogins)throws NoMethodException,Exception;
	
	public String getType();
	//public void setType(String val);
	public List<ReportDetail> getReportDetails();
}//end class
