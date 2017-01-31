/**
 * 
 */
package com.seeyon.v3x.doc.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Date;
import java.util.Set;
import java.sql.Timestamp;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.util.*;
import com.seeyon.v3x.project.domain.ProjectMember;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.domain.ProjectSummary;

/**
 * @author lihf
 * 
 */
public class TestDocHierarchyManager extends TestCase {

	String[] paths = { "hibernate.cfg.xml",
			"doc-manager.xml", "SeeyonOrganization.xml", "config-manager.xml", "message-context.xml", 
			"task-context.xml"};

	ApplicationContext context = new ClassPathXmlApplicationContext(paths);

	DocHierarchyManager mngr = (DocHierarchyManager) context
			.getBean("docHierarchyManager");

	DocResourceDao dao = (DocResourceDao) context.getBean("docResourceDao");

	private static final Log log = LogFactory
			.getLog(TestDocHierarchyManager.class);

	private Long userId = -4965142565841452946L;

	public TestDocHierarchyManager() {
		super();
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
//	
//	public void testInitLib() {
//	try {
////		mngr.initPersonalLib(6L, "我的文档", 125L);
////		mngr.initCorpLib(7L, "单位文档", 126L);
////		mngr.initDeptLib(8L, "部门文档", 127L);
////		mngr.initCaseLib(9L, "项目文档", 128L);
////		mngr.initArcsLib(10L, "公文文档", 129L);
////		mngr.initCustomLib(11L, "自定义文档", 130L);
////		mngr.pigeonholeAsLinkWithoutAcl(-490440215842176730L, ApplicationCategoryEnum.inquiry, 6L, 2L, 14250L);
////		mngr.createLinkWithoutAcl(14L, 6L, 4L, 1L);
////		log.debug("=== " + mngr.findMyFolders(2L, 31L, 1L, ""));
//		
//		
//		ProjectSummary summary = new ProjectSummary();
//		summary.setId(123456789L);
//		summary.setProjectState(ProjectSummary.state_begin);
//		summary.setProjectName("A8 V1.0项目");
//		summary.setProjectType("协同项目");
//		summary.setBegintime(new java.util.Date());
//		summary.setClosetime(new java.util.Date());
//		Set<ProjectMember> members = new HashSet<ProjectMember>();
//		ProjectMember member1 = new ProjectMember();
//		member1.setMemberid(678392695774923338L);
//		member1.setMemberType(ProjectMember.memberType_charge);
//		members.add(member1);
////		ProjectMember member2 = new ProjectMember();
////		member2.setMemberid(2L);
////		member2.setMemberType(ProjectMember.memberType_charge);
////		members.add(member2);
//		ProjectMember member3 = new ProjectMember();
//		member3.setMemberid(127561002946809715L);
//		member3.setMemberType(ProjectMember.memberType_interfix);
//		members.add(member3);
////		ProjectMember member4 = new ProjectMember();
////		member4.setMemberid(4L);
////		member4.setMemberType(ProjectMember.memberType_interfix);
////		members.add(member4);
//		ProjectMember member5 = new ProjectMember();
//		member5.setMemberid(3197079513703990291L);
//		member5.setMemberType(ProjectMember.memberType_manager);
//		members.add(member5);
////		ProjectMember member6 = new ProjectMember();
////		member6.setMemberid(6L);
////		member6.setMemberType(ProjectMember.memberType_manager);
////		members.add(member6);
//		ProjectMember member7 = new ProjectMember();
//		member7.setMemberid(-4965142565841452946L);
//		member7.setMemberType(ProjectMember.memberType_member);
//		members.add(member7);
////		ProjectMember member8 = new ProjectMember();
////		member8.setMemberid(8L);
////		member8.setMemberType(ProjectMember.memberType_member);
////		members.add(member8);
//		summary.setProjectMembers(members);
//		Set<ProjectPhase> phases = new HashSet<ProjectPhase>();
//		ProjectPhase phase1 = new ProjectPhase();
//		phase1.setId(11111111111111L);
//		phase1.setPhaseName("第一阶段");
//		phase1.setPhaseBegintime(new java.util.Date());
//		phase1.setPhaseClosetime(new java.util.Date());
//		phase1.setPhasePercent(1.1F);
//		phases.add(phase1);
//		ProjectPhase phase2 = new ProjectPhase();
//		phase2.setId(22222222222222L);
//		phase2.setPhaseName("第二阶段");
//		phase2.setPhaseBegintime(new java.util.Date());
//		phase2.setPhaseClosetime(new java.util.Date());
//		phase2.setPhasePercent(1.12F);
//		phases.add(phase2);
//		summary.setProjectPhases(phases);
//		mngr.createNewProject(summary, userId);
//		
////		mngr.deleteProject(123456789L, userId);
//		
//		ProjectPhase phase3 = new ProjectPhase();
//		phase3.setId(33333333333333L);
//		phase3.setPhaseName("新增阶段");
//		phase3.setPhaseBegintime(new java.util.Date());
//		phase3.setPhaseClosetime(new java.util.Date());
//		phase3.setPhasePercent(0.12F);
//		Set<ProjectPhase> addPhases = new HashSet<ProjectPhase>();
//		addPhases.add(phase3);
//		Set<ProjectPhase> updatePhases = new HashSet<ProjectPhase>();
//		phase2.setId(22222222222222L);
//		phase2.setPhaseName("修改了的第二阶段");
//		phase2.setPhaseBegintime(new java.util.Date());
//		phase2.setPhaseClosetime(new java.util.Date());
//		phase2.setPhasePercent(0.1F);
//		updatePhases.add(phase2);
//		mngr.updateProject(summary, addPhases, updatePhases, new String[]{"11111111111111"}, userId);
////		mngr.updateProject(summary, null, updatePhases, new String[]{}, userId);
////		mngr.updateProject(summary, addPhases, null, new String[]{}, 127561002946809715L);
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//}
//	
	

//	/**
//	 * 1 初始化个人文档库 Test method for {@link
//	 * 
//	 * com.seeyon.v3x.doc.manager.DocHierarchyManager#initializeMyLib(java.lang.Long,
//	 * java.lang.String)}.
//	 */
//	public void testInitializeMyLib() {
//		try {
//			deptIds.add(1L);
//			workIds.add(1L);
//			groupIds.add(1L);
//			mngr
//					.initializeMyLib(6L, "我的文档", userId, deptIds, workIds,
//							groupIds);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 2 初始化普通文档库 Test method for {@link
//	 * 
//	 * com.seeyon.v3x.doc.manager.DocHierarchyManager#initializeCommonLib(java.lang.Long,
//	 * java.lang.String, java.lang.Long)}.
//	 */
//	public void testInitializeCommonLib() {
//		try {
//			mngr.initializeCommonLib(7L, "单位文档", userId, deptIds, workIds,
//					groupIds);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 3 初始化项目文档库 Test method for {@link
//	 * 
//	 * com.seeyon.v3x.doc.manager.DocHierarchyManager#initializeCaseLib(java.lang.Long,
//	 * java.lang.String, java.lang.Long)}.
//	 */
//	public void testInitializeCaseLib() {
//		try {
//			mngr.initializeCaseLib(8L, "V3X项目文档", userId, deptIds, workIds,
//					groupIds);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 4 创建普通文档夹 Test method for {@link
//	 * 
//	 * com.seeyon.v3x.doc.manager.DocHierarchyManager#createCommonFolder(java.lang.String,
//	 * java.lang.Long, java.lang.Long, java.lang.Long)}.
//	 */
//	public void testCreateCommonFolder() {
//		deptIds.add(1L);
//		workIds.add(1L);
//		groupIds.add(1L);
//		try {
//			mngr.createCommonFolder("一级文档夹1", Constants.FOLDER_COMMON, 6L, 1L,
//					userId, deptIds, workIds, groupIds);
//			mngr.createCommonFolder("二级文档夹1", Constants.FOLDER_COMMON, 6L, 8L,
//					userId, deptIds, workIds, groupIds);
//			mngr.createCommonFolder("二级文档夹2", Constants.FOLDER_COMMON, 6L, 8L,
//					userId, deptIds, workIds, groupIds);
//			mngr.createCommonFolder("三级文档夹1", Constants.FOLDER_COMMON, 6L, 10L,
//					userId, deptIds, workIds, groupIds);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	// /**
	// * 5 上传单个文档 Test method for
	// * {@link
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#uploadFile(java.lang.Long,
	// java.lang.Long, java.lang.Long, java.lang.Long)}.
	// */
	// public void testUploadFile() {
	// try {
	// mngr.uploadFile(1254L, 8L, 7L, 2L, deptIds, workIds, groupIds);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// /**
	// * 6 批量上传文档 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#batchUploadFiles(java.util.List,
	// * java.lang.Long, java.lang.Long, java.lang.Long)}.
	// */
	// public void testBatchUploadFiles() {
	// List<Long> fileIds = new ArrayList<Long>();
	// fileIds.add(365L);
	// fileIds.add(366L);
	// try {
	// mngr.batchUploadFiles(fileIds, 7L, 6L, 3L, deptIds, workIds, groupIds);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

//	 /**
//	 * 7 新建文档 Test method for {@link
//	 *
//	 com.seeyon.v3x.doc.manager.DocHierarchyManager#createDocument(java.lang.String,
//	 * java.lang.String, java.util.List, java.lang.Long, java.lang.Long,
//	 * java.lang.Long)}.
//	 */
//	 public void testCreateDocument() {
//	 List<Long> docFileIds = new ArrayList<Long>();
//	 docFileIds.add(214L);
//	 docFileIds.add(707L);
//	 try {
//	 mngr.createDocument("新建文档1", "这是文档的正文", docFileIds, 6L, 1L, userId,
//	 deptIds, workIds, groupIds);
//	 mngr.createDocument("新建文档2", "这是文档的正文", docFileIds, 6L, 8L, userId,
//			 deptIds, workIds, groupIds);
//	 mngr.createDocument("新建文档3", "这是文档的正文", docFileIds, 6L, 10L, userId,
//			 deptIds, workIds, groupIds);
//	 } catch (Exception e) {
//	 // TODO Auto-generated catch block
//	 e.printStackTrace();
//	 }
//	 }
	//
	// /**
	// * 8 以链接方式归档 Test method for {@link
	// *
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#pigeonholeAsLink(java.lang.String,
	// * java.lang.String, java.lang.Float, java.lang.Long, java.lang.Long,
	// * java.lang.Long, java.lang.Long)}.
	// */
	// public void testPigeonholeAsLink() {
	// try {
	// mngr.pigeonholeAsLink("协同链接", "测试链接", 1253F, 45L, 8L, 7L, 5L,
	// deptIds, workIds, groupIds);
	// mngr.pigeonholeAsLink("公文链接", "测试链接", 1234F, 236L, 6L, 8L, 5L,
	// deptIds, workIds, groupIds);
	// mngr.pigeonholeAsLink("公文链接", "测试链接", 2653F, 365L, 6L, 4L, 5L,
	// deptIds, workIds, groupIds);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 9 创建链接 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#createLink(com.seeyon.v3x.doc.domain.DocResource,
	// * java.lang.Long, java.lang.Long, java.lang.Long)}.
	// */
	// public void testCreateLink() {
	// try {
	// mngr.createLink(10L, 6L, 2L, 4L, deptIds, workIds, groupIds);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// /**
	// * 10 文档、文档夹的移动 Test method for {@link
	// * com.seeyon.v3x.doc.manager.DocHierarchyManager#moveDocs(java.util.List,
	// * java.lang.Long, java.lang.Long, java.lang.Long)}.
	// */
	// public void testMoveDocs() {
	// List<Long> ids = new ArrayList<Long>();
	// ids.add(8L);
	// ids.add(16L);
	// try {
	// mngr.moveDocs(ids, 7L, 9L, 6L, deptIds, workIds, groupIds);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 11 文档、文档夹的重命名 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#renameDoc(java.lang.Long,
	// * java.lang.String, java.lang.Long)}.
	// */
	// public void testRenameDoc() {
	// try {
	// mngr.renameDoc(15L, "新改的名字", 7L, deptIds, workIds, groupIds);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// /**
	// * 12 显示顺序的上移 Test method for {@link
	// * com.seeyon.v3x.doc.manager.DocHierarchyManager#moveUp(java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testMoveUp() {
	// try {
	// mngr.moveUp(12L, 5L);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//	
	// /**
	// * 13 显示顺序的下移 Test method for {@link
	// * com.seeyon.v3x.doc.manager.DocHierarchyManager#moveDown(java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testMoveDown() {
	// try {
	// mngr.moveDown(9L, 5L);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// /**
	// * 13 根据 libId 得到 rootId Test method for {@link
	// * com.seeyon.v3x.doc.manager.DocHierarchyManager#moveDown(java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testGetRootIdByLibId() {
	// try {
	// log.debug("root id --- " + mngr.getRootIdByLibId(7L));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// /**
	// * 13 根据docResourceId 查找某个文档的从根节点开始的整个文档夹对象链 Test method for {@link
	// * com.seeyon.v3x.doc.manager.DocHierarchyManager#moveDown(java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testGetFoldersChainById() {
	// try {
	// log.debug(mngr.getFoldersChainById(15L));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 13 查找相似标题内容 Test method for {@link
	// * com.seeyon.v3x.doc.manager.DocHierarchyManager#moveDown(java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testGetFoldersChainById() {
	// try {
	// // log.debug(mngr.findDocsBySimilarName(6L, "文档", 3L, deptIds,
	// // workIds, groupIds));
	// log.debug(mngr.findDocsByCreateUser(6L, 5L, 10L, deptIds, workIds,
	// groupIds));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

//	 /**
//	 * 14 查找我的文档下的所有文档夹 Test method for {@link
//	 *
//	 com.seeyon.v3x.doc.manager.DocHierarchyManager#findMyFolders(java.lang.Long)}.
//	 */
//	 public void testFindMyFolders() {
//	 try {
//		 log.debug("RRRRRRRRRRRRR " + mngr.findMyFolders(1L, Constants.FOLDER_COMMON, userId, deptIds, workIds, groupIds));
//	 } catch (Exception e) {
//	 // TODO Auto-generated catch block
//	 e.printStackTrace();
//	 }
//	 }
	
//	 /**
//	 * 14 查找我的文档下的所有文档夹 Test method for {@link
//	 *
//	 com.seeyon.v3x.doc.manager.DocHierarchyManager#findMyFolders(java.lang.Long)}.
//	 */
//	 public void testFindMyFolders() {
//	 try {
////		 mngr.accessOneTime(12L);
////		 mngr.removeFolderWithoutAcl(mngr.getDocResourceById(8L), 1L);
////		 mngr.moveDocWithoutAcl(mngr.getDocResourceById(10L), 6L, 6L, 2L, 1L);
//		 log.debug("---------- " + mngr.getTotalWithoutAcl(8L));
//	 } catch (Exception e) {
//	 // TODO Auto-generated catch block
//	 e.printStackTrace();
//	 }
//	 }
	//	
	// /**
	// * 15 分页查找我的文档库下的所有内容 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#findAllMyDocsByPage(java.lang.Long,
	// * java.lang.Integer, java.lang.Integer)}.
	// */
	// public void testFindAllMyDocsByPage() {
	// try {
	// log.debug(mngr.findAllMyDocsByPage(1L, 1, 2));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//	
	// /**
	// * 16 查找其他文档库下的所有文档夹 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#findFolders(java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testFindFolders() {
	// try {
	// log.debug("=========== " + mngr.findFolders(6L, 100L, deptIds, workIds,
	// groupIds));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 17 分页查找其他文档库下的所有内容 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#findAllDocsByPage(java.lang.Long,
	// * java.lang.Integer, java.lang.Integer, java.lang.Long)}.
	// */
	// public void testFindAllDocsByPage() {
	// try {
	// log.debug("========" + mngr.findAllDocsByPage(6L, 3, 2, 563L, null,
	// null));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 18 根据 docId 得到 docResourceId Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#getRootIdByLibId(java.lang.Long)}.
	// */
	// public void testGetRootIdByLibId() {
	// try {
	// System.out.println("RootId: " + mngr.getRootIdByLibId(10L));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 21 删除文档、文档夹 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#removeDocsByIds(java.util.List,
	// * java.lang.Long)}.
	// */
	// public void testRemoveDocsByIds() {
	// // List<Long> ids = new ArrayList<Long>();
	// // ids.add(12L);
	// // ids.add(22L);
	// // try {
	// // mngr.removeDocsByIds(ids, 5632L);
	// // } catch (Exception e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// }
	//
	// /**
	// * 22 清空一个库下的所有内容，仅保留根节点 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#emptyLibLeftRoot(java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testEmptyLibLeftRoot() {
	// // try {
	// // mngr.emptyLibLeftRoot(9L, 45L);
	// // } catch (Exception e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// }
	//
	// /**
	// * 23 清空一个库下的所有内容，不保留根节点 Test method for {@link
	// * com.seeyon.v3x.doc.manager.DocHierarchyManager#emptyLib(java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testEmptyLib() {
	// // try {
	// // mngr.emptyLib(10L, 465L);
	// // } catch (Exception e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// }

	// /**
	// * 24 界面右上角的查询功能 查询某个文档夹下的对应内容 标题 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#findDocsBySimilarName(java.lang.Long,
	// java.lang.String, java.lang.Long, java.lang.Long, java.lang.Long)}.
	// */
	// public void testFindDocsBySimilarName() {
	// log.debug(mngr.findDocsBySimilarName(6L, "项目", 125L, null, null));
	// }
	//	
	// /**
	// * 25 判断一个库下是否只存在一个根文档夹 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#isLibOnlyRoot(java.lang.Long)}.
	// */
	// public void testIsLibOnlyRoot() {
	// log.debug(mngr.isLibOnlyRoot(100L));
	// }

	// /**
	// * 26 界面右上角的查询功能 查询某个文档夹下的对应内容 内容 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#findDocsByType(java.lang.Long,
	// * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Long)}.
	// */
	// public void testFindDocsByType() {
	// log.debug(mngr.findDocsByType(1L, 1L, null, null, null));
	// }

	// /**
	// * 27 界面右上角的查询功能 查询某个文档夹下的对应内容 创建时间 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#findDocsByType(java.lang.Long,
	// * java.sql.Timestamp, java.sql.Timestamp, java.lang.Long, java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testFindDocsByCreateTime() {
	// Date now = new Date();
	// log.debug(mngr.findDocsByCreateTime(1L, new Timestamp(now.getTime() - 5
	// * 24 * 60 * 60 * 1000), new Timestamp(now.getTime() - 2 * 24
	// * 60 * 60 * 1000), null, null, null));
	// }

	// /**
	// * 27 界面右上角的查询功能 查询某个文档夹下的对应内容 创建用户 Test method for {@link
	// *
	// com.seeyon.v3x.doc.manager.DocHierarchyManager#findDocsByType(java.lang.Long,
	// * java.sql.Timestamp, java.sql.Timestamp, java.lang.Long, java.lang.Long,
	// * java.lang.Long)}.
	// */
	// public void testFindDocsByCreateUser() {
	// Date now = new Date();
	// log.debug(mngr.findDocsByCreateUser(6L, 555L, null, null, null));
	// }

	// // 测试 取得最大 frOrder
	// public void testGetMaxOrder() {
	// log.debug("------------ " + mngr.getMaxOrder(1L));
	// }

	// // 测试 取得下一个 frOrder
	// public void testGetNextOrder() {
	// //log.debug("------------ " + mngr.getNextOrderDoc(1L, 6));
	// log.debug("------------ " + mngr.getPriviousOrderDoc(1L, 4));
	// }

	//
	// /**
	// * 附加：屏幕输出 doc_resources 表
	// */
	// public void testOutput() {
	// // 显示 doc_resources 表的所有记录
	// List<DocResource> all = dao.getAll();
	// for (DocResource dr : all) {
	// System.out.println("id: " + dr.getId() + ", name: "
	// + dr.getFrName() + ", desc: " + dr.getFrDesc()
	// + ", docLibId: " + dr.getDocLibId() + ", parentId: "
	// + dr.getParentFrId() + ", isFolder: " + dr.getIsFolder()
	// + ", frType: " + dr.getFrType() + ", logicalPath: "
	// + dr.getLogicalPath() + ", Order: " + dr.getFrOrder());
	// }
	// }
}
