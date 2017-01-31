/**
 * 
 */
package com.seeyon.v3x.doc.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.seeyon.v3x.doc.controller.DocController;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;

import junit.framework.TestCase;

/**
 * @author lihf
 * 
 */
public class DocControllerTest extends TestCase {

	String[] paths = { "hibernate.cfg.xml", "filemanager-context.xml",
			"doc-controller.xml", "doc-manager.xml", "SeeyonOrganization.xml" };

	ApplicationContext context = new ClassPathXmlApplicationContext(paths);

	DocController ctrl = (DocController) context.getBean("docController");

	private static final Log log = LogFactory.getLog(DocControllerTest.class);

	/**
	 * @param arg0
	 */
	public DocControllerTest(String arg0) {
		super(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link com.seeyon.v3x.doc.controller.DocController#listFolders(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	// public void testListFolders() {
	// try {
	// log.debug(ctrl.listDocs(null, null));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	public void testListDocs() {
		try {
//			ctrl.listDocs(null, null);
//			ctrl.listLibs(null, null);
//			ctrl.rename(null, null);
//			ctrl.xmlJsp(null, null);
//			ctrl.index(null, null);
//			ctrl.addDocument(null, null);
//			String test = "22汉字";
//			log.debug("----- " + test.getBytes().length);
//			ctrl.addNewDocument(null, null);
//			ctrl.docOpen(null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
