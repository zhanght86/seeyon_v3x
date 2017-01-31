package test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.seeyon.v3x.system.signet.dao.SignetDao;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SignetDaoImplTest extends TestCase {

	String paths[] = { "test/cfgSignet.xml", "system-mgr.xml" };

	ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);

	SignetDao om = (SignetDao) ctx.getBean("signetDao");

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDeleteSignet() {
	}

	public void testFindAll() {
		System.out.println("sdsdsad");
		try {
			Assert.assertNotNull(om.findAll());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testSaveV3xSignet() {
	}

	public void testUpdateV3xSignet() {
	}

}
