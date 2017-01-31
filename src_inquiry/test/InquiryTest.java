/**
 * 
 */
package test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.seeyon.v3x.inquiry.controller.InquiryController;
import com.seeyon.v3x.inquiry.domain.InquiryScope;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.manager.InquiryManagerImpl;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyTypeCompose;

import junit.framework.TestCase;

/**
 * @author lin tian
 * 
 * 2007-2-28
 */
public class InquiryTest extends TestCase {
	String paths[] = { "test/hibernate.cfg.xml", "inquiry.xml",
			"SeeyonOrganization.xml" };

	ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);

	InquiryManager manager = (InquiryManager) ctx.getBean("inquiryManager");

//	public void test3() throws Exception {
//
//		System.out.println("------===========------"
//				+ manager.getInquiryList().size());
//	}
//
//	public void test2() throws Exception {
//		InquirySurveytype surveytype = new InquirySurveytype();
//		surveytype.setIdIfNew();// 设置ID号
//		surveytype.setTypeName("aaaaa");
//		surveytype.setSurveyDesc("bbbbb");
//		
//		surveytype.setCensorDesc(0);
//		surveytype.setFlag(0);// 设置为正常状态 1为删除状态
//		surveytype.setAuthDesc(0);
//		
//		Set managerSet = new HashSet();
//		surveytype.setInquirySurveytypeextends(managerSet);
//		InquirySurveytypeextend isextendmanager = new InquirySurveytypeextend();
//		isextendmanager.setIdIfNew();
//		isextendmanager
//		.setManagerDesc(InquirySurveytypeextend.MANAGER_SYSTEM);//设置为管理员
//		isextendmanager.setInquirySurveytype(surveytype);
//		managerSet.add(isextendmanager);//级联加入管理员子对象
//		manager.saveInquiryType(surveytype);// 保存调查类型
//		System.out.println("------ok------");
//	}
//
//	public void test1() {
//		try {
//			System.out.println("------------"
//					+ manager.getInquirySurveytypeBYID(8).getSurveyDesc());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public void test4() throws Exception {
//		InquirySurveytype surveytype = new InquirySurveytype();
//        manager.deleteInquiryType(Long.parseLong("8"));
//		}
//	
//	public void test5() throws Exception {
//		InquirySurveytype surveytype = new InquirySurveytype();
//        manager.updateInquiryTypeAuth(Long.parseLong("8"));
//		}
//	public void test6() throws Exception{
//		manager.deleteInquiryBasic(Long.parseLong("1"));
//	}
	
//	public void test7() throws Exception{
//		SurveyTypeCompose scompose= manager.getSurveyTypeComposeBYID(Long.parseLong("8"));
//		//System.out.println("------------------"+scompose.getChecker().getMemberName());
//		System.out.println("------------------"+scompose.getInquirySurveytype().getTypeName());
//	}
//	
//	public void test8()throws Exception{
//	InquirySurveytype surveytype = new InquirySurveytype();
//		
//	surveytype =manager.getInquirySurveytypeBYID(Long.parseLong("8"));
//		Set managerSet = new HashSet();
//		InquirySurveytypeextend isextendmanager = new InquirySurveytypeextend();
//		isextendmanager.setId(Long.parseLong("21"));
//		isextendmanager
//		.setManagerDesc(InquirySurveytypeextend.MANAGER_SYSTEM);//设置为管理员
//		isextendmanager.setInquirySurveytype(surveytype);
//		managerSet.add(isextendmanager);//级联加入管理员子对象
//		
//		manager.updateInquiryType(surveytype, managerSet);// 保存调查类型
//		System.out.println("------ok------");		
//	}
	
//	public void test9() throws Exception{
//		List<SurveyBasicCompose> alits = manager.getInquiryListByUserID();
//		for (SurveyBasicCompose compose : alits) {
//		    System.out.println("------ok------"+compose.getInquirySurveybasic().getId());	
//			}
//	}
	
//	public void test26() throws Exception{
//		manager.closeSendBasicByCreator("-5475315087073524065");
//	}
	
	public void test26() throws Exception{
//	List<InquirySurveybasic> blist	=manager.getTemplateList();
//	 for (InquirySurveybasic surveybasic : blist) {
//		System.out.println("---------"+surveybasic.getSurveyName());
//	}
		
		SurveyBasicCompose b = manager.getTemplateListByID(Long.parseLong("7413476440867883342"),false);
		System.out.println("---------"+b.getInquirySurveybasic().getSurveyName());
		System.out.println("---------"+b.getInquirySurveybasic().getDepartmentId());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(InquiryTest.class);
	}

}
