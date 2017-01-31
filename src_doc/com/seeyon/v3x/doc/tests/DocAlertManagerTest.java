package com.seeyon.v3x.doc.tests;

import java.sql.Timestamp;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.seeyon.v3x.doc.manager.DocAlertLatestManager;
import com.seeyon.v3x.doc.manager.DocAlertManager;
import com.seeyon.v3x.doc.util.Constants;

import junit.framework.TestCase;

public class DocAlertManagerTest extends TestCase {
	String[] paths = { "hibernate.cfg.xml",
			"doc-manager.xml", "SeeyonOrganization.xml", "config-manager.xml", "message-context.xml", 
			"task-context.xml"};

	ApplicationContext context = new ClassPathXmlApplicationContext(paths);

	DocAlertManager mngr = (DocAlertManager) context
			.getBean("docAlertManager");
	DocAlertLatestManager mngr2 = (DocAlertLatestManager) context
	.getBean("docAlertLatestManager");

	public DocAlertManagerTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAddAlert() {
		try {
//			mngr.addAlert(361L, false, Constants.ALERT_OPR_TYPE_ALL, Constants.ALERT_USER_TYPE_PERSONAL,
//					-4965142565841452946L, -4965142565841452946L, null);
//			mngr.updateAlertOprType(8109997205396341263L, Constants.ALERT_OPR_TYPE_DELETE);
//			System.out.println(mngr.findAlertsByUserId(Constants.ALERT_USER_TYPE_PERSONAL, -4965142565841452946L));
//			mngr.findAlertsByDocResourceId(361L);
//			mngr.findAlertsByUserId(Constants.ALERT_USER_TYPE_PERSONAL, -4965142565841452946L);
//			mngr.deleteAlertByDocResourceId(361L);
			
//			mngr2.addAlertLatest(361L, Constants.ALERT_OPR_TYPE_EDIT, -4965142565841452946L,
//					new Timestamp(12163465L));
//			System.out.println(mngr2.findAlertLatestsByUser(Constants.ALERT_USER_TYPE_PERSONAL, -4965142565841452946L));
			mngr2.tidyAlertLatests(2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public void testUpdateAlertOprType() {
//		fail("Not yet implemented");
//	}
//
//	public void testDeleteAlertById() {
//		fail("Not yet implemented");
//	}
//
//	public void testDeleteAlertByDocResourceId() {
//		fail("Not yet implemented");
//	}
//
//	public void testFindAlertById() {
//		fail("Not yet implemented");
//	}
//
//	public void testFindAlertsByDocResourceId() {
//		fail("Not yet implemented");
//	}
//
//	public void testFindAlertsByUserId() {
//		fail("Not yet implemented");
//	}

}
