/**
 * 
 */
package com.seeyon.v3x.bbs.test;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;

/**
 * 类描述：BbsArticleManager的测试类
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class TestBbsArticleManager extends TestCase{
	
	String [] paths = { "/com/seeyon/v3x/bbs/test/hibernate.cfg.xml", "bbs.xml","SeeyonOrganization.xml" };

	ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);

	BbsArticleManager bbsArticleManager = (BbsArticleManager)ctx
			.getBean("bbsArticleManager");
	/*
	public void testQueryArticleByCondition()throws Exception{
		List list = bbsArticleManager.queryByCondition(1L, "subject", "我们", "");
		
		if(list != null) {
			System.out.println(list.size());
		}else{
			System.out.println("Have no element!");
		}
	}
	*/
	
	//测试listAllArticle()方法
	public void testListAllArticle() throws Exception{
		List list = bbsArticleManager.listAllArticle(false, null, null, null , null);
		
		if (list != null) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				V3xBbsArticle article = (V3xBbsArticle) it.next();
				System.out.println("测试listAllArticle()方法");
				System.out.println(article.getArticleName() + "---"
						+ article.getIssueTime());
			}
			System.out.println(list.size());
		}else{
			System.out.println("Have no element!");
		}
	}
	
	/*
	//测试listAllArticle()方法
	public void testListAllElitePost() throws Exception{
		List list = bbsArticleManager.listAllElitePost();
		
		if (list != null) {
			Iterator it = list.iterator();
			int i = 0;
			while (it.hasNext()) {
				i++;
				V3xBbsArticle article = (V3xBbsArticle) it.next();
				System.out.println(article.getArticleName() + "---"
						+ article.getIssueTime());
			}
			System.out.println("count---MemberId------=" + i);
		}else{
			System.out.println("Have no element!");
		}
	}
	
	//	测试listAllElitePost()方法
	public void testListBoardElitePost() throws Exception{
		Long boardId = new Long(1);
		List list = null;
		list = bbsArticleManager.listBoardElitePost(boardId);
		
		if (list != null) {
			Iterator it = list.iterator();
			int i = 0;
			while (it.hasNext()) {
				i++;
				V3xBbsArticle article = (V3xBbsArticle) it.next();
				System.out.println(article.getArticleName() + "---"
						+ article.getIssueTime());
			}
			System.out.println("count---MemberId------=" + i);
		}else{
			System.out.println("Have no element!");
		}
	}
	
	//	测试listArticleByBoardId()方法
	public void testListArticleByBoardId() throws Exception{
		Long boardId = new Long(1);
		List list = null;
		list = bbsArticleManager.listArticleByBoardId(boardId);
		
		if (list != null) {
			Iterator it = list.iterator();
			int i = 0;
			while (it.hasNext()) {
				i++;
				V3xBbsArticle article = (V3xBbsArticle) it.next();
				System.out.println(article.getArticleName() + "---"
						+ article.getIssueTime());
			}
			System.out.println("count---MemberId------=" + i);
		}else{
			System.out.println("Have no element!");
		}
	}
	
	//	测试getBoardArticleNumber()方法
	public void testGetBoardArticleNumber() throws Exception{
		Long boardId = new Long(1);
		Integer boardArticleNumber = null;
		
		boardArticleNumber = bbsArticleManager.getBoardArticleNumber(boardId);
		
		System.out.println("There are " + boardArticleNumber.intValue() + " subject in the board!");
	}	
	
	//	测试getArticleReplyNumber()方法
	public void testGetArticleReplyNumber() throws Exception{
		Long articleId = new Long(1);
		Integer articleReplyNumber = null;
		
		articleReplyNumber = bbsArticleManager.getArticleReplyNumber(articleId);
		
		System.out.println("There are " + articleReplyNumber.intValue() + " article reply number in the article!");
	}	
	
	//	测试getBoardReplyNumber()方法
	public void testGetBoardReplyNumber() throws Exception{
		Long boardId = new Long(1);
		Integer boardReplyNumber = null;
		
		boardReplyNumber = bbsArticleManager.getBoardReplyNumber(boardId);
		
		System.out.println("There are " + boardReplyNumber.intValue() + " board reply number in the board!");
	}	
	
	//	测试getBoardElitePostNumber()方法
	public void testGetBoardElitePostNumber() throws Exception{
		Long boardId = new Long(1);
		Integer elitePostNumber = null;
		
		elitePostNumber = bbsArticleManager.getBoardElitePostNumber(boardId);
		
		System.out.println("There are " + elitePostNumber.intValue() + " elite post in the board!");
	}	
	*/
	protected void setUp() throws Exception {

		super.setUp();
	}

	protected void tearDown() throws Exception {

		super.tearDown();
	}
}
