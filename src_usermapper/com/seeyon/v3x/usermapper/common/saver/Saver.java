package com.seeyon.v3x.usermapper.common.saver;

import java.util.List;

import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.usermapper.report.domain.ReportDetail;

public interface Saver {
 public void save(V3xOrgUserMapper vum,V3xOrgUserMapper um,V3xOrgMember m)
                    throws Exception;
 
 public List<ReportDetail> getReportDetails();
}//end class
