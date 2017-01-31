/**
 * 
 */
package test;

import java.util.List;

import com.seeyon.v3x.inquiry.domain.InquiryScope;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.sun.jmx.snmp.Timestamp;

/**
 * @author lin tian
 *
 * 2007-2-28
 */
public class TestAA {

	/**
	 * 
	 */
	public TestAA() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static String  testlist()throws Exception{
		Timestamp nowtime = new Timestamp(System.currentTimeMillis());
		long memberid = 123456;//member.getId();// 获取当前用户ID
		long departmentid =123456; //member.getDepartmentId();// 当前用户的部门ID
		long levelid =123456; //member.getLevelId();// 用户职务ID
		long postid = 123456;//member.getPostId();// 岗位ID
		String hql = "Select distinct(iqs.id) From "
			+ InquiryScope.class.getName()
			+ " As iqs Where ((iqs.scopeId = ?  and iqs.scopeDesc=? )"
			+ " or (iqs.scopeId =? AND iqs.scopeDesc=? ) or (iqs.scopeId =? AND iqs.scopeDesc=? )"
			+ " or (iqs.scopeId =? AND iqs.scopeDesc=?))"
			+ " or iqs.scopeDesc=?) AND iqs.inquirySurveybasic.censor ="
			+ InquirySurveybasic.CENSOR_PASS.intValue()
			+ " And iqs.inquirySurveybasic in (From " + InquirySurveybasic.class.getName()
			+ " As isb Where isb.flag="
			+ InquirySurveybasic.FLAG_NORMAL.intValue()
			+ " AND isb.censor="
			+ InquirySurveybasic.CENSOR_PASS.intValue()
		
			+ " AND isb.inquirySurveytype.flag="
			+ InquirySurveytype.FLAG_NORMAL.intValue()
			+ " AND isb.sendDate < "+nowtime+ " AND isb.closeDate > "+nowtime+
 ")"
			+ " order by qs.inquirySurveybasic.sendDate desc";
	return  hql;
	}


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(TestAA.testlist());
	
	}

}
