/**
 * 
 */
package com.seeyon.v3x.doc.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocMimeType;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;

import junit.framework.TestCase;

/**
 * @author lihf
 *
 */
public class TestDocMimeTypeManager extends TestCase {

	String[] paths = { "hibernate.cfg.xml", "doc-manager.xml" };

	ApplicationContext context = new ClassPathXmlApplicationContext(paths);

	DocMimeTypeManager mngr = (DocMimeTypeManager) context
			.getBean("docMimeTypeManager");

	private static final Log log = LogFactory
			.getLog(TestDocMimeTypeManager.class);

	/**
	 * @param arg0
	 */
	public TestDocMimeTypeManager(String arg0) {
		super(arg0);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link com.seeyon.v3x.doc.manager.DocMimeTypeManager#getDocMimeTypeByFilePostix(java.lang.String)}.
	 */
//	public void testGetDocMimeTypeByFilePostix() {
//		DocMimeType dmt = mngr.getDocMimeTypeByFilePostix("doc");
//		log.debug("=======doc.icon======= " + dmt.getIcon());
//	}

//	/**
//	 * Test method for {@link com.seeyon.v3x.doc.manager.DocMimeTypeManager#getDocMimeTypeByDocTypeId(java.lang.Long)}.
//	 */
//	public void testGetDocMimeTypeByDocTypeId() {
//		DocMimeType dmt = mngr.getDocMimeTypeByDocTypeId(51L);
//		log.debug("=======link.icon======= " + dmt.getIcon());
//	}

}
