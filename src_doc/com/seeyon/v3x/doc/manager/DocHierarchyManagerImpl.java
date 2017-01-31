package com.seeyon.v3x.doc.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.seeyon.cap.info.domain.InfoStatCAP;
import com.seeyon.cap.info.domain.InfoSummaryCAP;
import com.seeyon.cap.info.manager.InfoStatManagerCAP;
import com.seeyon.cap.info.manager.InfoSummaryManagerCAP;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.meeting.manager.MtMeetingManagerCAP;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.his.manager.HisAffairManager;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulDataManagerImpl;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColManagerImpl;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.dao.support.page.Page;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.lock.domain.Lock;
import com.seeyon.v3x.common.lock.manager.LockManager;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.dao.DocBodyDao;
import com.seeyon.v3x.doc.dao.DocFromPotentDao;
import com.seeyon.v3x.doc.dao.DocMetadataDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.domain.DocBody;
import com.seeyon.v3x.doc.domain.DocForum;
import com.seeyon.v3x.doc.domain.DocFromPotent;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocMetadataObject;
import com.seeyon.v3x.doc.domain.DocMimeType;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocStorageSpace;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocTypeDetail;
import com.seeyon.v3x.doc.domain.DocVersionInfo;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.util.ActionType;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.util.Constants.LockStatus;
import com.seeyon.v3x.doc.util.DocMgrUtils;
import com.seeyon.v3x.doc.util.DocSearchHqlUtils;
import com.seeyon.v3x.doc.util.DocUtils;
import com.seeyon.v3x.doc.util.compress.CompressUtil;
import com.seeyon.v3x.doc.webmodel.DocEditVO;
import com.seeyon.v3x.doc.webmodel.DocSearchModel;
import com.seeyon.v3x.doc.webmodel.DocSortProperty;
import com.seeyon.v3x.doc.webmodel.DocTreeVO;
import com.seeyon.v3x.doc.webmodel.FolderItemDoc;
import com.seeyon.v3x.doc.webmodel.SimpleDocQueryModel;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.index.convert.Convertor;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexExtPropertiesConfig;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.Constant;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.manager.InquiryManagerImpl;
import com.seeyon.v3x.inquiry.webmdoel.InquiryBasicData;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsDataManagerImpl;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.manager.PlanManager;
import com.seeyon.v3x.project.domain.ProjectMember;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.webmodel.ProjectSummaryData;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.manager.MailBoxManager;
import com.seeyon.v3x.webmail.manager.WebMailManager;

public class DocHierarchyManagerImpl implements DocHierarchyManager {

	private static final Log log = LogFactory.getLog(DocHierarchyManagerImpl.class);

	private static NodeList nodes;
	private static Map<String, Node> nodesMap;
	// 在类加载时读取归档配置文件
	static {
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		InputStream is = null;
		try {
			DocumentBuilder dombuilder;
			dombuilder = domfac.newDocumentBuilder();
			is = DocHierarchyManager.class.getClassLoader().getResourceAsStream("pigeonhole.xml");
			Document doc = dombuilder.parse(is);
			Element root = doc.getDocumentElement();
			nodes = root.getChildNodes();
			nodesMap = new HashMap<String, Node>();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node app = nodes.item(i);
				if (app.getNodeType() == Node.ELEMENT_NODE) {
					nodesMap.put(app.getNodeName(), app);
				}
			}
			is.close();
			log.info("DocHierarchyManagerImpl 加载归档设置文件 pigeonhole.xml 成功。");
		} 
		catch (ParserConfigurationException e) {
			log.error("DocHierarchyManagerImpl 加载归档设置文件 pigeonhole.xml: ", e);
		} 
		catch (SAXException e) {
			log.error("DocHierarchyManagerImpl 加载归档设置文件 pigeonhole.xml: ", e);
		} 
		catch (IOException e) {
			log.error("DocHierarchyManagerImpl 加载归档设置文件 pigeonhole.xml: ", e);
		} 
		finally {
			IOUtils.closeQuietly(is);
		}
	}

	private DocResourceDao docResourceDao;
	private DocFromPotentDao docFromPotentDao;
	private FileManager fileManager;
	private DocBodyDao docBodyDao;
	private DocAclManager docAclManager;
	private OrgManager orgManager;
	private DocMimeTypeManager docMimeTypeManager;
	private ColManager colManager;
	private DocMetadataManager docMetadataManager;
	private InquiryManager inquiryManager;
	private AttachmentManager attachmentManager;
	private DocSpaceManager docSpaceManager;
	private AffairManager affairManager;
	private ContentTypeManager contentTypeManager;
	private MtMeetingManagerCAP mtMeetingManagerCAP;
	private DocForumManager docForumManager;
	private PlanManager planManager;
	private DocFavoriteManager docFavoriteManager;
	private DocAlertManager docAlertManager;
	private DocAlertLatestManager docAlertLatestManager;
	private EdocSummaryManager edocSummaryManager;
	private IndexManager indexManager;
	private WebMailManager webMailManager;
	private DocLearningManager docLearningManager;
	private NewsDataManager newsDataManager;
	private BulDataManager bulDataManager;
	private OperationlogManager operationlogManager;
	/** 文档层级总数上限 */
	private int folderLevelLimit;
	private DocMetadataDao docMetadataDao;
	
	private HisAffairManager hisAffairManager;

	public void setDocMetadataDao(DocMetadataDao docMetadataDao) {
		this.docMetadataDao = docMetadataDao;
	}
	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}
	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}
	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}
	public void setWebMailManager(WebMailManager webMailManager) {
		this.webMailManager = webMailManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}
	public void setDocAlertLatestManager(DocAlertLatestManager docAlertLatestManager) {
		this.docAlertLatestManager = docAlertLatestManager;
	}
	public void setDocAlertManager(DocAlertManager docAlertManager) {
		this.docAlertManager = docAlertManager;
	}
	public void setDocFavoriteManager(DocFavoriteManager docFavoriteManager) {
		this.docFavoriteManager = docFavoriteManager;
	}
	public void setPlanManager(PlanManager planManager) {
		this.planManager = planManager;
	}
	public void setDocForumManager(DocForumManager docForumManager) {
		this.docForumManager = docForumManager;
	}
	public void setMtMeetingManagerCAP(MtMeetingManagerCAP mtMeetingManagerCAP) {
		this.mtMeetingManagerCAP = mtMeetingManagerCAP;
	}
	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
		this.contentTypeManager = contentTypeManager;
	}
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setDocSpaceManager(DocSpaceManager docSpaceManager) {
		this.docSpaceManager = docSpaceManager;
	}
	public void setDocMetadataManager(DocMetadataManager docMetadataManager) {
		this.docMetadataManager = docMetadataManager;
	}
	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
	public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
		this.docMimeTypeManager = docMimeTypeManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}
	public void setDocAclManager(DocAclManager docAclManager) {
		this.docAclManager = docAclManager;
	}
	public void setDocBodyDao(DocBodyDao docBodyDao) {
		this.docBodyDao = docBodyDao;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	public void setHisAffairManager(HisAffairManager hisAffairManager) {
		this.hisAffairManager = hisAffairManager;
	}
	public void setFolderLevelLimit(int folderLevelLimit) {
		if(folderLevelLimit > 20 || folderLevelLimit <= 0)
			this.folderLevelLimit = 20;
		else
			this.folderLevelLimit = folderLevelLimit;
	}

	// 连接字符串成为acl需要的格式
	private String getAclIdsByOrgIds(String orgIds, Long userId) {
		if (Strings.isBlank(orgIds)) {
			return String.valueOf(userId);
		} else {
			return orgIds + "," + userId;
		}
	}

	// 判断某个用户对某个DocResource对象是否拥有某种权限
	private boolean hasPermission(DocResource dr, Long userId, String orgIds, List<Integer> levels) {
		String aclIds = this.getAclIdsByOrgIds(orgIds, userId);
		Set<Integer> acls = docAclManager.getDocResourceAclList(dr, aclIds);
		// 判断是否没有任何权限返回
		if ((acls == null) || (levels == null))
			return false;
		// 判断是否返回了需要的权限
		for (Integer temp : levels)
			if (acls.contains(temp))
				return true;
		return false;
	}

	private static List<Integer> ALL_EDIT_POTENT = Arrays.asList(Constants.ALLPOTENT, Constants.EDITPOTENT);
	private static List<Integer> ALL_EDIT_ADD_POTENT = Arrays.asList(Constants.ALLPOTENT, Constants.EDITPOTENT, Constants.ADDPOTENT);
	
	private static List<Integer> ALL_DOWNLOAD_POTENT = Arrays.asList(Constants.ALLPOTENT, Constants.EDITPOTENT, Constants.ADDPOTENT, Constants.READONLYPOTENT);
	
	public boolean hasEditPermission(DocResource dr, Long userId, String orgIds) {
		return this.hasPermission(dr, userId, orgIds, ALL_EDIT_POTENT);
	}

	// 判断某个用户对某个文档夹是否拥有创建权限
	public boolean hasDownloadPermission(DocResource dr, Long userId, String orgIds) {
		return this.hasPermission(dr, userId, orgIds, ALL_DOWNLOAD_POTENT);
	}
	// 判断某个用户对某个文档夹是否拥有创建权限
	private boolean hasCreatePermission(DocResource dr, Long userId, String orgIds) {
		return this.hasPermission(dr, userId, orgIds, ALL_EDIT_ADD_POTENT);
	}

	/** 取得当前表中的最小 fr_order  */
	public int getMinOrder(Long parentId) {
		return this.getMaxOrMinOrder(parentId, false);
	}
	
	/**
	 * 获取当前表中的最大或最小fr_order
	 * @param parentId
	 * @param max	是否取最大（还是最小）
	 * @return	最大或最小排序号
	 */
	private int getMaxOrMinOrder(Long parentId, boolean max) {
		if (parentId != 0) {
			String hql = "select " + (max ? "max" : "min") + "(d.frOrder) from DocResource d where d.parentFrId = ?";
			Integer result = (Integer)this.docResourceDao.findUnique(hql, null, parentId);
			return result == null ? 0 : result;
		}
		return 0;
	}
	
	/** 取得当前表中的最大 fr_order  */
	public int getMaxOrder(Long parentId) {
		return this.getMaxOrMinOrder(parentId, true);
	}
	
	/**
	 * 按照指定的排序号获得其离它最近的一个文档
	 * @param parentId:文档夹id
	 * @param order：指定的排序号
	 * @param orderType：如果是">"，则是下一个显示的对象 ； 如果是"<"，则是上一个显示的对象
	 * @author Fanxc
	 */
	@SuppressWarnings("unchecked")
	public DocResource getDocByOrderType(Long parentId, int order,String orderType){
		DocResource ret = null;
		StringBuffer buffer = new StringBuffer("from DocResource dr where dr.parentFrId = ? and dr.frOrder = (select ");
		
		if(">".equals(orderType)){
			buffer.append(" min(d.frOrder) from DocResource d where d.parentFrId = ? and d.frOrder ");
		}
		else if("<".equals(orderType)){
			buffer.append(" max(d.frOrder) from DocResource d where d.parentFrId = ? and d.frOrder ");
		}
		buffer.append(orderType);
		String hql = buffer.toString() + " ? " + DocSearchHqlUtils.HQL_FR_TYPE + ")";
		List<DocResource> list = docResourceDao.find(hql, 0, 1, null, parentId, parentId, order);
		if (CollectionUtils.isNotEmpty(list))
			ret = list.get(0);
		return ret;
	}

	// 判断在同一父文档夹下是否存在同名、同格式 的节点
	public boolean hasSameNameAndSameTypeDr(Long parentId, String name, Long type) {
		String hql = "select count(dr.id) from DocResource dr where frName = ? and dr.parentFrId = ? and frType = ?";
		Integer count = (Integer)this.docResourceDao.findUnique(hql, null, name.trim(), parentId, type);
		return count != null && count.intValue() > 0;
	}

	// 判断在同一父文档夹下是否存在同名、同格式 的节点
	public boolean hasSameNameAndSameTypeDr(Long parentId, String name, String type) {
		return this.hasSameNameAndSameTypeDr(parentId, name, Constants.DOCUMENT);
	}

	/**
	 * 判断某个文档夹下是否有同一应用、同一sourceId 的归档记录
	 * @return 没有--null；有--docResourceId
	 */
	public Long hasSamePigeonhole(long parentId, int appEnumKey, long sourceId) {
		Long contentId = Constants.getContentTypeIdByAppEnumKey(appEnumKey);
		if (contentId == null)
			return null;

		String hql = "from DocResource dr where dr.parentFrId = ? and sourceId = ? and frType = ?";
		List<DocResource> drs = docResourceDao.find(hql, parentId, sourceId, contentId);
		return CollectionUtils.isNotEmpty(drs) ? drs.get(0).getId() : null;
	}

	/**
	 * 得到归档文件
	 */
	public List<DocResource> getPigeonholeFile(int appEnumKey, long sourceId) {
		Long contentId = Constants.getContentTypeIdByAppEnumKey(appEnumKey);
		if (contentId != null) {
			String hql = "from DocResource dr where dr.sourceId = ? and dr.frType = ?";
			return docResourceDao.find(hql, sourceId, contentId);
		}
		return null;
	}

	/**
	 * 判断一个文档是否归档类型
	 * 
	 * common: 直接打开类型 link,4654646: 源文件存在的链接类型，sourceId link: 链接类型，源文件不存在
	 * 2,46466565465465 归档类型，第一个是key，第二个是sourceId
	 */
	public String getTheOpenType(Long docResourceId) {
		DocResource dr = docResourceDao.get(docResourceId);
		long type = dr.getFrType();
		String ret = "common";
		// 链接类型
		if (type == Constants.LINK) {
			ret = "link";
			// 归档类型的链接判断
			DocResource srcDr = docResourceDao.get(dr.getSourceId());
			if (srcDr != null) {
				type = srcDr.getFrType();
				if (type == Constants.SYSTEM_ARCHIVES) {
					ret = ApplicationCategoryEnum.edoc.getKey() + ",";
				} else if (type == Constants.SYSTEM_BBS) {
					ret = ApplicationCategoryEnum.bbs.getKey() + ",";
				} else if (type == Constants.SYSTEM_BULLETIN) {
					ret = ApplicationCategoryEnum.bulletin.getKey() + ",";
				} else if (type == Constants.SYSTEM_COL) {
					ret = ApplicationCategoryEnum.collaboration.getKey() + ",";
				} else if (type == Constants.SYSTEM_FORM) {
					ret = ApplicationCategoryEnum.form.getKey() + ",";
				} else if (type == Constants.SYSTEM_INQUIRY) {
					ret = ApplicationCategoryEnum.inquiry.getKey() + ",";
				} else if (type == Constants.SYSTEM_MEETING) {
					ret = ApplicationCategoryEnum.meeting.getKey() + ",";
				} else if (type == Constants.SYSTEM_NEWS) {
					ret = ApplicationCategoryEnum.news.getKey() + ",";
				} else if (type == Constants.SYSTEM_PLAN) {
					ret = ApplicationCategoryEnum.plan.getKey() + ",";
				} else if (type == Constants.SYSTEM_MAIL) {
					ret = ApplicationCategoryEnum.mail.getKey() + ",";
				} else if(type == Constants.SYSTEM_INFO) {
					ret = ApplicationCategoryEnum.info.getKey() + ",";
				} else {
					return ret + "," + dr.getSourceId();
				}
				dr = srcDr;
			}
		} else {
			if (type == Constants.SYSTEM_ARCHIVES) {
				ret = ApplicationCategoryEnum.edoc.getKey() + ",";
			} else if (type == Constants.SYSTEM_BBS) {
				ret = ApplicationCategoryEnum.bbs.getKey() + ",";
			} else if (type == Constants.SYSTEM_BULLETIN) {
				ret = ApplicationCategoryEnum.bulletin.getKey() + ",";
			} else if (type == Constants.SYSTEM_COL) {
				ret = ApplicationCategoryEnum.collaboration.getKey() + ",";
			} else if (type == Constants.SYSTEM_FORM) {
				ret = ApplicationCategoryEnum.form.getKey() + ",";
			} else if (type == Constants.SYSTEM_INQUIRY) {
				ret = ApplicationCategoryEnum.inquiry.getKey() + ",";
			} else if (type == Constants.SYSTEM_MEETING) {
				ret = ApplicationCategoryEnum.meeting.getKey() + ",";
			} else if (type == Constants.SYSTEM_NEWS) {
				ret = ApplicationCategoryEnum.news.getKey() + ",";
			} else if (type == Constants.SYSTEM_PLAN) {
				ret = ApplicationCategoryEnum.plan.getKey() + ",";
			} else if (type == Constants.SYSTEM_MAIL) {
				ret = ApplicationCategoryEnum.mail.getKey() + ",";
			} else if(type == Constants.SYSTEM_INFO) {
				ret = ApplicationCategoryEnum.info.getKey() + ",";
			}
		}

		if (!ret.startsWith("common"))
			ret = ret + dr.getSourceId();

		return ret;
	}
	
	public DocResource createFolderByTypeWithoutAcl(String name, Long type,
			Long docLibId, Long destFolderId, Long userId) throws DocException {
		return this.createFolderByTypeWithoutAcl(name, type, docLibId, destFolderId, userId, false, false);
	}

	public DocResource createFolderByTypeWithoutAcl(String name, Long type,
			Long docLibId, Long destFolderId, Long userId, boolean parentVersionEnabled, boolean parentCommentEnabled) throws DocException {
		int minOrder = this.getMinOrder(destFolderId);

		DocResource dr = new DocResource();
		dr.setFrName(name);
		dr.setParentFrId(destFolderId);
		dr.setAccessCount(0);
		dr.setCommentCount(0);
		dr.setCommentEnabled(false);
		dr.setCreateTime(new Timestamp(new Date().getTime()));
		dr.setCreateUserId(userId);
		dr.setDocLibId(docLibId);
		dr.setIsFolder(true);
		dr.setSubfolderEnabled(true);
		dr.setFrOrder(minOrder - 1);
		dr.setFrSize(0);
		dr.setFrType(type);
		dr.setLastUpdate(new Timestamp(new Date().getTime()));
		dr.setLastUserId(userId);
		dr.setStatus(Byte.parseByte("2"));
		dr.setStatusDate(new Timestamp(new Date().getTime()));
		dr.setMimeTypeId(type);
		dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());
		dr.setIsCheckOut(false);
		dr.setVersionEnabled(parentVersionEnabled);
		dr.setCommentEnabled(parentCommentEnabled);

		docResourceDao.saveAndGetId(dr);
		return dr;
	}

	// 创建根文档夹
	private DocResource createRoot(Long docLibId, String docLibName, Long contentType, Long userId) throws DocException {
		// 判断当前库下是否已经存在根文档夹, 存在则直接返回
		DocResource existRoot = this.getRootByLibId(docLibId);
		if (existRoot != null)
			throw new DocException("doc_lib_init_again");
		return this.createFolderByTypeWithoutAcl(docLibName, contentType, docLibId, 0L, userId);
	}

	public void moveDocWithoutAcl(DocResource dr, Long srcLibId, Long destLibId, Long destFolderId, 
			Long userId, boolean destPersonal, boolean parentCommentEnabled, int destFolderLevelPath) throws DocException {
		//  系统类型移动时不进行同名校验            
		if (dr.getFrType() > Constants.FORMAT_TYPE_SYSTEM_MAIL && 
				this.hasSameNameAndSameTypeDr(destFolderId, dr.getFrName(), dr.getFrType()))     
			throw new DocException("doc_move_dupli_name_failure_alert");
	
		// 删除相关的权限记录
		docAclManager.deletePotent(dr);

		if (dr.getIsFolder()) {
			this.moveFolderWithoutAcl(dr, srcLibId, destLibId, destFolderId, userId, destPersonal, destFolderLevelPath);
		} else {
			this.moveLeafWithoutAcl(dr, srcLibId, destLibId, destFolderId, userId, destPersonal, parentCommentEnabled);
		}
	}
	
	private void moveFolderWithoutAcl(DocResource dr, Long srcLibId, Long docLibId, Long destFolderId, 
			Long userId, boolean destPersonal, int destFolderLevelPath) throws DocException {
		String oldParentPath = dr.getLogicalPath();
		List<DocResource> drs = docResourceDao.findByLike("logicalPath", oldParentPath + ".");
		int oldParentPathLength = oldParentPath.length();

		if (this.hasSameNameAndSameTypeDr(destFolderId, dr.getFrName(), dr.getFrType()))
			throw new DocException("doc_move_dupli_name_failure_alert");
		
		// 先进行文档层级控制判断，避免移动过程中抛出异常，数据出现紊乱
		for (DocResource tempDr : drs) {
			if(tempDr.getIsFolder()) {
				int totalDepth = tempDr.getRelativeLevelDepth(dr.getId()) + destFolderLevelPath + 1;
				if(totalDepth > this.folderLevelLimit)
					throw new DocException("doc_alert_level_too_deep");
			}
		}
		
		this.moveLeafWithoutAcl(dr, srcLibId, docLibId, destFolderId, userId, destPersonal, dr.getCommentEnabled());
		
		long sumSize = 0L;
		for (DocResource tempDr : drs) {
			if (tempDr.getFrType() == Constants.DOCUMENT)
				sumSize += tempDr.getFrSize();

			tempDr.setDocLibId(docLibId);
			tempDr.setLastUserId(userId);
			tempDr.setLastUpdate(new Timestamp(new Date().getTime()));

			if (destPersonal) {
				tempDr.setCommentEnabled(false);
				tempDr.setIsCheckOut(false);
			}

			tempDr.setLogicalPath(dr.getLogicalPath() + tempDr.getLogicalPath().substring(oldParentPathLength));
			docResourceDao.update(tempDr);
		}

		// 修改个人文档库的空间占有情况
		if (this.isPersonalLib(srcLibId))
			docSpaceManager.subUsedSpaceSize(userId, sumSize);
		
		if (this.isPersonalLib(docLibId)) {
			DocStorageSpace space = docSpaceManager.getDocSpaceByUserId(userId);
			if (space.getTotalSpaceSize() < (sumSize + space.getUsedSpaceSize()))
				throw new DocException("personal_storage_not_enough");
			docSpaceManager.addUsedSpaceSize(userId, sumSize);
		}
	}
	
	private void moveLeafWithoutAcl(DocResource dr, Long srcLibId,
			Long destLibId, Long destFolderId, Long userId,
			boolean destPersonal, boolean parentCommentEnabled)
			throws DocException {
		int minOrder = this.getMinOrder(destFolderId);
		dr = docResourceDao.get(dr.getId());
		// 修改 doc_resources 表中相关属性
		dr.setDocLibId(destLibId);
		dr.setParentFrId(destFolderId);
		dr.setLastUserId(userId);
		dr.setLastUpdate(new Timestamp(new Date().getTime()));
		dr.setFrOrder(minOrder - 1);
		dr.setCommentEnabled(parentCommentEnabled);

		if (destPersonal) {
			dr.setCommentEnabled(false);
			dr.setIsCheckOut(false);
		}

		String newParentPath = docResourceDao.get(destFolderId).getLogicalPath();
		dr.setLogicalPath(newParentPath + "." + dr.getId());
		docResourceDao.update(dr);

		// 修改个人文档库的空间占有情况
		if (dr.getFrType() == Constants.DOCUMENT) {
			if (this.isPersonalLib(srcLibId))
				docSpaceManager.subUsedSpaceSize(userId, dr.getFrSize());
			
			if (this.isPersonalLib(destLibId)) {
				DocStorageSpace space = docSpaceManager.getDocSpaceByUserId(userId);
				if (space.getTotalSpaceSize() < (dr.getFrSize() + space.getUsedSpaceSize()))
					throw new DocException("personal_storage_not_enough");
				
				docSpaceManager.addUsedSpaceSize(userId, dr.getFrSize());
			}
		}

	}

	public void removeDocWithAcl(DocResource dr, Long userId, String orgIds,
			boolean first) throws DocException {
		if (!this.hasPermission(dr, userId, orgIds, ALL_EDIT_POTENT))
			throw new DocException("doc_deal_no_acl");

		this.removeDocWithoutAcl(dr, userId, first);
	}

	// 根据人名列表封装DocResource对象，
	// 使用userId作为docResourceId, PERSON_SHARE等作为frType, userName作为frName
	@SuppressWarnings("unchecked")
	private List<DocResource> getDrListByUserIds(Set<Long> userIds,
			long contentType, Long currentUserId) {
		List<DocResource> ret = new ArrayList<DocResource>();
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(currentUserId);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		if (contentType == Constants.PERSON_BORROW || contentType == Constants.PERSON_SHARE) {
			String hql = "from DocResource as dr where dr.createUserId =:uid and dr.frType =:ftype and (dr.secretLevel <="+member.getSecretLevel()+" or dr.secretLevel is null)";//成发集团项目 程炯 使用文档密级进行列表筛选
			Map<String, Object> nmap = new HashMap<String, Object>();
			nmap.put("uid", currentUserId);
			if (contentType == Constants.PERSON_BORROW)
				nmap.put("ftype", Constants.FOLDER_BORROWOUT);
			else
				nmap.put("ftype", Constants.FOLDER_SHAREOUT);
			List<DocResource> docList = docResourceDao.find(hql, -1, -1, nmap);

			if (docList.size() == 1)
				ret.add(docList.get(0));
		}

		for (Long userId : userIds) {
			DocResource dr = new DocResource();
			dr.setId(userId);
			dr.setFrName(Constants.getOrgEntityName("Member", userId, false));
			dr.setFrType(contentType);

			dr.setCreateTime(new Timestamp(new Date().getTime()));
			dr.setCreateUserId(userId);
			dr.setDocLibId(0);
			dr.setIsFolder(true);
			dr.setSubfolderEnabled(false);
			dr.setFrSize(0);
			dr.setLastUpdate(new Timestamp(new Date().getTime()));
			dr.setLastUserId(userId);

			ret.add(dr);

		}
		// 单位借阅在我的借阅中的显示
		if (contentType == Constants.PERSON_BORROW) {
			long userId = CurrentUser.get().getId();
			String orgIds = Constants.getOrgIdsOfUser(userId);
			if (docAclManager.getDeptBorrowDocsCount(this.getAclIdsByOrgIds(orgIds, userId)) > 0) {
				DocResource dr = new DocResource();
				dr.setId(0L);
				dr.setFrName(Constants.DEPARTMENT_BORROW_KEY);
				dr.setFrType(Constants.DEPARTMENT_BORROW);
				dr.setCreateTime(new Timestamp(new Date().getTime()));
				dr.setCreateUserId(0L);
				dr.setDocLibId(0L);
				dr.setIsFolder(true);
				dr.setSubfolderEnabled(false);
				dr.setFrSize(0);
				dr.setLastUpdate(new Timestamp(new Date().getTime()));

				ret.add(dr);
			}
		}

		return ret;
	}
 
	public DocResource createCommonFolder(String name, Long destFolderId, Long userId, String orgIds) throws DocException {
		return this.createCommonFolderWithoutAcl(name, destFolderId, userId);
	}

	public DocResource createCommonFolderWithoutAcl(String name, Long destFolderId,
			Long userId) throws DocException {
		return this.createCommonFolderWithoutAcl(name, destFolderId, userId, false, false);
	}
	
	public DocResource createCommonFolderWithoutAcl(String name, Long destFolderId,
			Long userId, boolean parentVersionEnabled, boolean parentCommentEnabled) throws DocException {
		// 判断目的文档夹是否允许创建子文档夹
		DocResource destFolder = docResourceDao.get(destFolderId);
		if (!destFolder.getSubfolderEnabled())
			throw new DocException("doc_subfolder_disabled");

		// 判断是否存在同名同类型记录
		if (this.hasSameNameAndSameTypeDr(destFolderId, name,
				Constants.FOLDER_COMMON))
			throw new DocException("doc_upload_dupli_name_failure_alert");

		return this.createFolderByTypeWithoutAcl(name, Constants.FOLDER_COMMON,
				destFolder.getDocLibId(), destFolderId, userId, parentVersionEnabled, parentCommentEnabled);
	}

	public Long initPersonalLib(Long docLibId, String docLibName, Long userId)
			throws DocException {
		DocResource root = this.createRoot(docLibId, Constants.FOLDER_MINE_KEY,
				Constants.FOLDER_MINE, userId);
		Long rootId = root.getId();
		// 创建必要的文档夹
		DocResource plan = this.createFolderByTypeWithoutAcl(
				Constants.FOLDER_PLAN_KEY, Constants.FOLDER_PLAN, docLibId,
				rootId, userId);
		Long planId = plan.getId();
		DocResource borrow = this.createFolderByTypeWithoutAcl(
				Constants.FOLDER_BORROW_KEY, Constants.FOLDER_BORROW, docLibId,
				rootId, userId);
		this.createFolderByTypeWithoutAcl(Constants.FOLDER_BORROWOUT_KEY,
				Constants.FOLDER_BORROWOUT, docLibId, borrow.getId(), userId);
		DocResource share = this.createFolderByTypeWithoutAcl(
				Constants.FOLDER_SHARE_KEY, Constants.FOLDER_SHARE, docLibId,
				rootId, userId);
		this.createFolderByTypeWithoutAcl(Constants.FOLDER_SHAREOUT_KEY,
				Constants.FOLDER_SHAREOUT, docLibId, share.getId(), userId);

		this.createFolderByTypeWithoutAcl(Constants.FOLDER_PLAN_WORK_KEY,
				Constants.FOLDER_PLAN_WORK, docLibId, planId, userId);
		this.createFolderByTypeWithoutAcl(Constants.FOLDER_PLAN_DAY_KEY,
				Constants.FOLDER_PLAN_DAY, docLibId, planId, userId);
		this.createFolderByTypeWithoutAcl(Constants.FOLDER_PLAN_MONTH_KEY,
				Constants.FOLDER_PLAN_MONTH, docLibId, planId, userId);
		this.createFolderByTypeWithoutAcl(Constants.FOLDER_PLAN_WEEK_KEY,
				Constants.FOLDER_PLAN_WEEK, docLibId, planId, userId);
		return rootId;
	}

	public Long initCorpLib(Long docLibId, String docLibName, Long userId)
			throws DocException {
		Long rootId = this.createRoot(docLibId, Constants.FOLDER_CORP_KEY,
				Constants.FOLDER_CORP, userId).getId();
		return rootId;
	}

	public Long initCaseLib(Long docLibId, String docLibName, Long userId)
			throws DocException {
		Long rootId = this.createRoot(docLibId,
				Constants.FOLDER_PROJECT_ROOT_KEY,
				Constants.FOLDER_PROJECT_ROOT, userId).getId();
		return rootId;
	}

	public Long initArcsLib(Long docLibId, String docLibName, Long userId)
			throws DocException {
		Long rootId = this.createRoot(docLibId, Constants.ROOT_ARC_KEY,
				Constants.ROOT_ARC, userId).getId();

		// 07.08.31 增加一个公文预归档文档夹
		this.createFolderByTypeWithoutAcl(Constants.FOLDER_ARC_PRE_KEY,
				Constants.FOLDER_ARC_PRE, docLibId, rootId, userId);

		return rootId;
	}

	public Long initCustomLib(Long docLibId, String docLibName, Long userId)
			throws DocException {
		Long rootId = this.createRoot(docLibId, docLibName,
				Constants.FOLDER_COMMON, userId).getId();
		return rootId;
	}

	public Long uploadFile(V3XFile file, Long docLibId, byte docLibType,
			Long destFolderId, Long userId, String orgIds,
			boolean parentCommentEnabled, boolean parentVersionEnabled) throws DocException {
		if (!this.hasCreatePermission(docResourceDao.get(destFolderId), userId, orgIds))
			throw new DocException("doc_link_create_doclink_alert");

		return this.uploadFileWithoutAcl(file, docLibId, docLibType,
				destFolderId, userId, parentCommentEnabled, parentVersionEnabled).getId();
	}

	public DocResource uploadFileWithoutAcl(V3XFile file, Long docLibId,
			byte docLibType, Long destFolderId, Long userId,
			boolean parentCommentEnabled, boolean parentVersionEnabled) throws DocException {
		// 个人文档库增加使用空间
		if (docLibType == Constants.PERSONAL_LIB_TYPE.byteValue()) {
			DocStorageSpace space = docSpaceManager.getDocSpaceByUserId(userId);
			if (space.getTotalSpaceSize() < (file.getSize() + space.getUsedSpaceSize()))
				throw new DocException("personal_storage_not_enough");
			
			docSpaceManager.addUsedSpaceSize(userId, file.getSize());
		}

		// 判断是否存在同名文档
		if (this.hasSameNameAndSameTypeDr(destFolderId, file.getFilename(), Constants.DOCUMENT))
			throw new DocException("doc_upload_dupli_name_failure_alert");

		int minOrder = this.getMinOrder(destFolderId);
		Timestamp time = new Timestamp(new Date().getTime());
		String name = file.getFilename();
		
		DocResource dr = new DocResource();
		dr.setFrName(name);
		dr.setFrSize(file.getSize());
		dr.setParentFrId(destFolderId);
		dr.setAccessCount(0);
		dr.setCommentCount(0);
		dr.setCommentEnabled(parentCommentEnabled);
		dr.setVersionEnabled(parentVersionEnabled);
		dr.setCreateTime(time);
		dr.setCreateUserId(userId);
		dr.setDocLibId(docLibId);
		dr.setIsFolder(false);
		dr.setFrOrder(minOrder - 1);
		dr.setFrType(Constants.DOCUMENT);
		dr.setLastUpdate(time);
		dr.setLastUserId(userId);
		dr.setSourceId(file.getId());
		dr.setStatus(Byte.parseByte("2"));
		dr.setStatusDate(time);
		dr.setIsCheckOut(false);
		// 从文件管理组件得到文件类型后缀
		String postfix = name.substring(name.lastIndexOf(".") + 1, name.length());
		dr.setMimeTypeId(docMimeTypeManager.getDocMimeTypeByFilePostix(postfix));
		dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());
		docResourceDao.saveAndGetId(dr);
		return dr;
	}

	/**
	 * 创建复合文档
	 */
	public DocResource createDoc(String name, DocBody docBody, Long docLibId,
			Long destFolderId, Long userId, String orgIds,
			boolean parentCommentEnabled, boolean parentVersionEnabled, long contentTypeId,
			Map<String, Comparable> metadatas) throws DocException {
		if (!this.hasCreatePermission(docResourceDao.get(destFolderId), userId, orgIds))
			throw new DocException("doc_deal_no_acl");
		
		return this.createDocWithoutAcl(name, docBody, docLibId, destFolderId,
				userId, parentCommentEnabled, parentVersionEnabled, contentTypeId, metadatas);
	}

	public DocResource createDocWithoutAcl(String name, DocBody docBody,
			Long docLibId, Long destFolderId, Long userId,
			boolean parentCommentEnabled, boolean parentVersionEnabled, long contentTypeId,
			Map<String, Comparable> metadatas) throws DocException {
		return createDocWithoutAcl(name, "", "", docBody, docLibId,
				destFolderId, userId, parentCommentEnabled, parentVersionEnabled, contentTypeId,
				metadatas);
	}

	/**
	 * 创建复合文档，不考虑权限
	 */
	public DocResource createDocWithoutAcl(String name, String description,
			String keyword, DocBody docBody, Long docLibId, Long destFolderId,
			Long userId, boolean parentCommentEnabled, boolean parentVersionEnabled, long contentTypeId, Map<String, Comparable> metadatas) {
		long mimeId = Constants.FORMAT_TYPE_DOC_A6;
		String bodyType = docBody.getBodyType();
		if (bodyType.equals(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD))
			mimeId = Constants.FORMAT_TYPE_DOC_WORD;
		else if (bodyType.equals(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL))
			mimeId = Constants.FORMAT_TYPE_DOC_EXCEL;
		else if (bodyType.equals(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD))
			mimeId = Constants.FORMAT_TYPE_DOC_WORD_WPS;
		else if (bodyType.equals(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL))
			mimeId = Constants.FORMAT_TYPE_DOC_EXCEL_WPS;

		int minOrder = this.getMinOrder(destFolderId);
		Timestamp now = new Timestamp(System.currentTimeMillis());

		DocResource dr = new DocResource();
		dr.setFrName(name);
		dr.setFrDesc(description);
		dr.setKeyWords(keyword);
		dr.setParentFrId(destFolderId);
		dr.setAccessCount(0);
		dr.setCommentCount(0);
		dr.setCommentEnabled(parentCommentEnabled);
		dr.setVersionEnabled(parentVersionEnabled);
		dr.setCreateTime(now);
		dr.setCreateUserId(userId);
		dr.setDocLibId(docLibId);
		dr.setIsFolder(false);
		dr.setFrOrder(minOrder - 1);
		dr.setFrSize(0);
		dr.setFrType(contentTypeId);
		dr.setLastUpdate(now);
		dr.setLastUserId(userId);
		dr.setStatus(Byte.parseByte("2"));
		dr.setStatusDate(now);
		dr.setMimeTypeId(mimeId);
		dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());
		dr.setIsCheckOut(false);
		Long newId = docResourceDao.saveAndGetId(dr);
		this.saveBody(newId, docBody);
		
		if ((metadatas != null) && (metadatas.size() > 0))
			docMetadataManager.addMetadata(newId, metadatas);
		
		return dr;
	}

	/** *******************************归档开始************************************** */

	/**
	 * 链接归档
	 */
	public Long pigeonholeAsLink(Long sourceId, boolean hasAttachments, int appEnumKey, Long docLibId, 
			Long destFolderId, Long userId, String orgIds) throws DocException {
		boolean needCheckAcl = this.isNotPartOfMyLib(userId, docLibId);
		if (needCheckAcl) {
			DocResource destFolder = docResourceDao.get(destFolderId);
			boolean hasPermission = this.hasCreatePermission(destFolder, userId, orgIds);
			if (!hasPermission)
				throw new DocException("doc_deal_no_acl");
		}

		return this.pigeonholeAsLinkWithoutAcl(sourceId, hasAttachments, appEnumKey, docLibId, destFolderId, userId);
	}

	/**
	 * 链接方式归档
	 */
	public List<Long> pigeonholeAsLink(List<Long> sourceIds,
			List<Boolean> hasAttachments, int appEnumKey, Long docLibId,
			Long destFolderId, Long userId) throws DocException {
		List<Long> newIds = new ArrayList<Long>();
		int hassize = 0;
		if (hasAttachments != null)
			hassize = hasAttachments.size();
		for (int i = 0; i < sourceIds.size(); i++) {
			long srcId = sourceIds.get(i);
			boolean has = false;
			if (hassize > i)
				has = hasAttachments.get(i);
			Long newId = this.pigeonholeAsLinkWithoutAcl(srcId, has, appEnumKey, docLibId, destFolderId, userId);
			newIds.add(newId);
		}
		return newIds;
	}

	/**
	 * 预归档之后的正式归档
	 * 
	 * @return 正常情况是归档生成记录的id; 如果目标文档夹不存在，返回 null
	 */
	public Long pigeonholeAfterPre(int appEnumKey, Long sourceId,
			boolean hasAttachments, Long destFolderId, Long userId)
			throws DocException {
		DocResource dr = docResourceDao.get(destFolderId);
		if (dr == null)
			return null;

		Long docLibId = dr.getDocLibId();
		Long id = this.pigeonholeAsLinkWithoutAcl(sourceId, hasAttachments,
				appEnumKey, docLibId, destFolderId, userId);

		if (id != null) {
			DocResource dr2 = this.getDocResourceById(id);
			docAlertLatestManager.addAlertLatest(dr2,
					Constants.ALERT_OPR_TYPE_ADD, userId, new Timestamp(
							new Date().getTime()),
					Constants.DOC_MESSAGE_ALERT_ADD_DOC, null);

			// 全文检索
			try {
				indexManager.index(this.getIndexInfo(dr2,false));
			} catch (Exception e) {
				log.error("全文检索入库", e);
			}

			// 记录操作日志
			operationlogManager.insertOplog(id, destFolderId,
					ApplicationCategoryEnum.doc, ActionType.LOG_DOC_PIGEONHOLE,
					ActionType.LOG_DOC_PIGEONHOLE + ".desc", CurrentUser.get()
							.getName(), Constants
							.getAppEnumI18nValue(appEnumKey + ""), dr2
							.getFrName());
		}

		return id;
	}

	// 返回归档配置文件的 NodeList
	private static Map<String, Node> getNodesMap() {
		return nodesMap;
	}

	public void updatePigeHoleFile(Long sourceId, int appEnumKey, Long userId) throws DocException {
		if (sourceId == null) {
			return;
		}
		/**
		 * 判断该文件是不是已经归档
		 */
		List<DocResource> list = getPigeonholeFile(appEnumKey, sourceId);
		if (list == null) {
			return;
		}
		for (DocResource docResource : list) {
			this.updatePigeHoleFile(sourceId, userId, docResource);
		}
	}

	private void updatePigeHoleFile(Long sourceId, Long userId, DocResource docResource) throws DocException {

		EdocSummary summary = edocSummaryManager.findById(sourceId);
		if (summary == null) {
			return;
		}
		String name = summary.getSubject();
		Node app = DocHierarchyManagerImpl.getNodesMap().get("edoc");
		this.updatePigeHoleFile(summary, app, sourceId, name, userId, docResource);
		/**
		 * 联合发文
		 */
		if (summary.getIsunit()) {
			app = DocHierarchyManagerImpl.getNodesMap().get("edoc2");
			this.updatePigeHoleFile(summary, app, sourceId, name, userId, docResource);
		}
	}
	
	/** 遍历字段、属性的对应  */
	private void handleFields(Object appObject, String name, Node table, Map drMetadatas, Map metadatas) {
		for (Node prop = table.getFirstChild(); prop != null; prop = prop.getNextSibling()) {
			if (prop.getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap atts = prop.getAttributes();
				String methodName = atts.getNamedItem("method").getNodeValue();
				Method method;
				Object value = null;
				try {
					method = appObject.getClass().getMethod(methodName);
					value = method.invoke(appObject);
				} catch (SecurityException e) {
					log.error("归档 pigeonholeWithoutAcl: ", e);
				} catch (NoSuchMethodException e) {
					log.error("归档 pigeonholeWithoutAcl: ", e);
				} catch (IllegalArgumentException e) {
					log.error("归档 pigeonholeWithoutAcl: ", e);
				} catch (IllegalAccessException e) {
					log.error("归档 pigeonholeWithoutAcl: ", e);
				} catch (InvocationTargetException e) {
					log.error("归档 pigeonholeWithoutAcl: ", e);
				}
				String valueClassName = atts.getNamedItem("type").getNodeValue();
				Class valueClass = null;
				if (valueClassName.contains(".")) {
					// 处理对象类型
					try {
						valueClass = Class.forName(valueClassName);
					} catch (ClassNotFoundException e) {
						log.error("归档 pigeonholeWithoutAcl: ", e);
					}
					valueClass.cast(value);
				} else {
					// 处理基本类型
					if (valueClassName.equals("byte"))
						valueClass = byte.class;
					else if (valueClassName.equals("short"))
						valueClass = short.class;
					else if (valueClassName.equals("int"))
						valueClass = int.class;
					else if (valueClassName.equals("long"))
						valueClass = long.class;
					else if (valueClassName.equals("float"))
						valueClass = float.class;
					else if (valueClassName.equals("double"))
						valueClass = double.class;
					else if (valueClassName.equals("char"))
						valueClass = char.class;
					else if (valueClassName.equals("boolean"))
						valueClass = boolean.class;
				}

				if (table.getNodeName().equals("doc_resources")) {
					Method setMethod;
					try {
						setMethod = DocResource.class.getMethod(atts.getNamedItem("setMethod").getNodeValue(), valueClass);
						drMetadatas.put(setMethod, value);
					} catch (SecurityException e) {
						log.error("归档 pigeonholeWithoutAcl: ", e);
					} catch (DOMException e) {
						log.error("归档 pigeonholeWithoutAcl: ", e);
					} catch (NoSuchMethodException e) {
						log.error("归档 pigeonholeWithoutAcl: ", e);
					}
				} else if (table.getNodeName().equals("doc_metadata")) {
					String column = atts.getNamedItem("column").getNodeValue();
					metadatas.put(column, value);
				}
			}// end of if
		}// end of 字段遍历
	}

	private void updatePigeHoleFile(Object appObject, Node appNode,
			Long sourceId, String name, Long userId, DocResource docResource)
			throws DocException {

		// 遍历数据库表
		for (Node table = appNode.getFirstChild(); table != null; table = table.getNextSibling()) {
			String tableName = table.getNodeName();
			Map drMetadatas = new HashMap();
			Map metadatas = new HashMap();
			if (table.getNodeType() == Node.ELEMENT_NODE) {
				this.handleFields(appObject, tableName, table, drMetadatas, metadatas);

				// 向不同的表保存元数据
				if (tableName.equals("doc_resources")) {
					DocResource dr = docResource;
					dr.setFrName(name);
					dr.setLastUserId(userId);
					dr.setLastUpdate(new Timestamp(new Date().getTime()));
					dr.setParentFrId(docResource.getParentFrId());
					dr.setAccessCount(docResource.getAccessCount());
					dr.setCommentCount(docResource.getCommentCount());
					dr.setCommentEnabled(docResource.getCommentEnabled());
					dr.setCreateTime(docResource.getCreateTime());
					dr.setCreateUserId(docResource.getCreateUserId());
					dr.setDocLibId(docResource.getDocLibId());
					dr.setIsFolder(docResource.getIsFolder());
					dr.setFrOrder(docResource.getFrOrder());
					dr.setFrSize(docResource.getFrSize());
					dr.setFrType(docResource.getFrType());
					dr.setSourceId(docResource.getSourceId());
					dr.setStatus(docResource.getStatus());
					dr.setStatusDate(docResource.getStatusDate());
					dr.setMimeTypeId(docResource.getMimeTypeId());
					dr.setMimeOrder(docResource.getMimeOrder());
					dr.setIsCheckOut(docResource.getIsCheckOut());
					dr.setHasAttachments(docResource.getHasAttachments());

					// 处理定义归档的doc_resources数据
					Set<Method> keys = drMetadatas.keySet();
					for (Method m : keys) {
						try {
							m.invoke(dr, drMetadatas.get(m));
						} catch (IllegalArgumentException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						} catch (IllegalAccessException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						} catch (InvocationTargetException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						}
					}
					dr.setFrName(name);
					docResourceDao.update(dr);

				} else if (tableName.equals("doc_metadata")) {
					Set<String> keys = metadatas.keySet();
					docMetadataManager.updateMetadata(docResource.getId(), metadatas);
				}
			}
		}
	}

	public Long pigeonholeAsLinkWithoutAcl(Long sourceId,
			boolean hasAttachments, int appEnumKey, Long docLibId,
			Long destFolderId, Long userId) throws DocException {
		// 文档夹是否存在判断
		boolean exist = this.docResourceExist(destFolderId);
		if (!exist)
			return null;

		// 判断在同一个文档夹下是否归档过
		Long oldId = this.hasSamePigeonhole(destFolderId, appEnumKey, sourceId);
		if (oldId != null) {
			if (appEnumKey == ApplicationCategoryEnum.collaboration.getKey())
				return oldId;
			else{
				//GOV-3185 公文管理-发文管理-已办，归档，再次归档的时候的提示有点问题
				boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  
				if(isGovVersion){
					throw new DocException("doc_move_dupli_name_failure_gov_alert");
				}else{
					throw new DocException("doc_move_dupli_name_failure_alert");
				}
			}
		}

		if (appEnumKey == ApplicationCategoryEnum.collaboration.getKey()) {
			return this.pigeonholeColWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.inquiry.getKey()) {
			return this.pigeonholeInquiryWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.meeting.getKey()) {
			return this.pigeonholeMeetingWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.plan.getKey()) {
			return this.pigeonholePlanWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.mail.getKey()) {
			return this.pigeonholeMailWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.edoc.getKey()) {
			return this.pigeonholeEdocWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.news.getKey()) {
			return this.pigeonholeNewsWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.bulletin.getKey()) {
			return this.pigeonholeBulletinWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.info.getKey()) {
			return this.pigeonholeInfoWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else
			return null;
	}

	/**
	 * lijl重写,添加departPigeonhole参数,用来区分"部门归档"的来历,链接方式归档，不考虑权限
	 * 判断是不是从"发文管理"——"已办"中的"部门归档"中来的
	 */
	public Long pigeonholeAsLinkWithoutAcl(Long sourceId,
			boolean hasAttachments, int appEnumKey, Long docLibId,
			Long destFolderId, Long userId,String departPigeonhole) throws DocException {
		// 文档夹是否存在判断
		boolean exist = this.docResourceExist(destFolderId);
		if (!exist)
			return null;

		// 判断在同一个文档夹下是否归档过
		Long oldId = this.hasSamePigeonhole(destFolderId, appEnumKey, sourceId);
		if (oldId != null) {
			if (appEnumKey == ApplicationCategoryEnum.collaboration.getKey())
				return oldId;
			else{
				//GOV-3185 公文管理-发文管理-已办，归档，再次归档的时候的提示有点问题
				boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  
				if(isGovVersion){
					throw new DocException("doc_move_dupli_name_failure_gov_alert");
				}else{
					throw new DocException("doc_move_dupli_name_failure_alert");
				}
			}
		}

		if (appEnumKey == ApplicationCategoryEnum.collaboration.getKey()) {
			return this.pigeonholeColWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.inquiry.getKey()) {
			return this.pigeonholeInquiryWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.meeting.getKey()) {
			return this.pigeonholeMeetingWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.plan.getKey()) {
			return this.pigeonholePlanWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.mail.getKey()) {
			return this.pigeonholeMailWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.edoc.getKey()) {
			return this.pigeonholeEdocWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId,departPigeonhole);
		} else if (appEnumKey == ApplicationCategoryEnum.news.getKey()) {
			return this.pigeonholeNewsWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.bulletin.getKey()) {
			return this.pigeonholeBulletinWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else if (appEnumKey == ApplicationCategoryEnum.info.getKey()) {
			return this.pigeonholeInfoWithoutAcl(sourceId, hasAttachments,
					docLibId, destFolderId, userId);
		} else
			return null;
	}

	public Long pigeonholeEdoc(EdocSummary summary, boolean hasAttachments)throws DocException {
		log.info("即将归档公文《"+summary.getSubject()+"》archiveId:"+summary.getArchiveId());
		DocResource destFolder = getDocResourceById(summary.getArchiveId());
		if(destFolder == null){
			log.error("公文归档查找归档路径失败:"+summary.getId()+" 《"+summary.getSubject()+"》");
		}else{
			log.info("归档公文《"+summary.getSubject()+"》到目录《"+destFolder.getFrName()+"》成功！");
		}
		long sourceId = summary.getId();
		
		String name = summary.getSubject();
		Integer secretLevel = summary.getEdocSecretLevel();//成发集团项目 程炯 获取公文流程密级
		Long contentType = Constants.SYSTEM_ARCHIVES;
		Long oldId = this.hasSamePigeonhole(destFolder.getId(), ApplicationCategoryEnum.edoc.key(), sourceId);
		if (oldId != null)
			return oldId;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("edoc");
		if (app != null)
			newId = this.pigeonholeWithoutAclByType(summary, app, sourceId,
					hasAttachments, name, contentType,
					destFolder.getDocLibId(), destFolder.getId(), CurrentUser.get().getId(), 0,secretLevel);//成发集团项目 程炯 保存公文时传入流程密级

		// 记录操作日志
		DocResource dr = this.getDocResourceById(newId);
		
		docAlertLatestManager.addAlertLatest(dr, Constants.ALERT_OPR_TYPE_ADD,
				CurrentUser.get().getId(), new Timestamp(new Date().getTime()),
				Constants.DOC_MESSAGE_ALERT_ADD_DOC, null);
		
		operationlogManager.insertOplog(newId, destFolder.getId(), ApplicationCategoryEnum.doc, 
				ActionType.LOG_DOC_PIGEONHOLE, ActionType.LOG_DOC_PIGEONHOLE + ".desc", CurrentUser.get().getName(), 
				Constants.getAppEnumI18nValue(ApplicationCategoryEnum.edoc.key() + ""), dr.getFrName());
		
		// 联合发文进行第二次归档
		if (summary.getIsunit()) {
			app = DocHierarchyManagerImpl.getNodesMap().get("edoc2");
			if (app != null) {
				newId = this.pigeonholeWithoutAclByType(summary, app, sourceId,
						hasAttachments, name, contentType, destFolder.getDocLibId(), destFolder.getId(), 
						CurrentUser.get().getId(), 0,secretLevel);//成发集团项目 程炯 保存公文时传入流程密级

				dr = this.getDocResourceById(newId);
				operationlogManager.insertOplog(newId, destFolder.getId(), ApplicationCategoryEnum.doc, 
						ActionType.LOG_DOC_PIGEONHOLE, ActionType.LOG_DOC_PIGEONHOLE + ".desc",
						CurrentUser.get().getName(), Constants.getAppEnumI18nValue(ApplicationCategoryEnum.edoc.key() + ""), dr.getFrName());
			}
		}
		try {
			indexManager.index(getIndexInfo(newId));
		} catch (Exception e) {
			log.error("全文检索入库", e);
		}
		return newId;
	}
	/**
	 * lijl重写此方法,添加参数departPigeonhole,用来区分"部门归档"的来历,公文归档，不考虑权限
	 */
	public Long pigeonholeEdocWithoutAcl(Long sourceId, boolean hasAttachments,
			Long destLibId, Long destFolderId, Long userId,String departPigeonhole) throws DocException {
		// 根据归档源的不同，取得不同的对象
		EdocSummary summary = edocSummaryManager.findById(sourceId); 
		//branches_a8_v350_r_gov GOV-3996  发文管理-已办-已办结-未归档，归档某公文后，它仍在未归档列表中_v1_常屹_0620
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
        if(isGovVersion){
        	//lijl添加if判断,如果用户是从"发文管理"——"已办"中的"部门归档"得来的则设置hasArchive为true
        	if("true".equals(departPigeonhole)){
        		summary.setHasArchive(true);
        	}
        }
      //branches_a8_v350_r_gov GOV-3996  发文管理-已办-已办结-未归档，归档某公文后，它仍在未归档列表中_v1_常屹_0620
        // 判断是否存在同名文档
        String name = summary.getSubject();
        
        //三元分离项目  获取公文流程密级
    	Integer secretLevel = summary.getEdocSecretLevel();
        Long contentType = Constants.SYSTEM_ARCHIVES;

        // 2. 声明将来的新Id
        Long newId = null;
        Node app = DocHierarchyManagerImpl.getNodesMap().get("edoc");
        if (app != null)
            newId = this.pigeonholeWithoutAclByType(summary, app, sourceId,
                    hasAttachments, name, contentType, destLibId, destFolderId,
                    CurrentUser.get().getId(),new Integer(1),secretLevel);

		// 联合发文进行第二次归档
		if (summary.getIsunit()) {
			app = DocHierarchyManagerImpl.getNodesMap().get("edoc2");
			if (app != null) {
				newId = this.pigeonholeWithoutAclByType(summary, app, sourceId,
						hasAttachments, name, contentType, destLibId,
						destFolderId, CurrentUser.get().getId(),new Integer(1));
			}
		}

		return newId;
	}
	
	/**
	 * 公文归档，不考虑权限
	 */
	public Long pigeonholeEdocWithoutAcl(Long sourceId, boolean hasAttachments,
			Long destLibId, Long destFolderId, Long userId) throws DocException {
		// 根据归档源的不同，取得不同的对象
		EdocSummary summary = edocSummaryManager.findById(sourceId); 
		//branches_a8_v350_r_gov GOV-3996  发文管理-已办-已办结-未归档，归档某公文后，它仍在未归档列表中_v1_常屹_0620
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
        if(isGovVersion){
		  summary.setHasArchive(true);
        }
        Integer secretLevel = summary.getEdocSecretLevel();//成发集团项目 程炯  获取公文流程密级
      //branches_a8_v350_r_gov GOV-3996  发文管理-已办-已办结-未归档，归档某公文后，它仍在未归档列表中_v1_常屹_0620
		// 判断是否存在同名文档
		String name = summary.getSubject();
		Long contentType = Constants.SYSTEM_ARCHIVES;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("edoc");
		if (app != null)
			newId = this.pigeonholeWithoutAclByType(summary, app, sourceId,
					hasAttachments, name, contentType, destLibId, destFolderId,
					CurrentUser.get().getId(),new Integer(1),secretLevel);//成发集团项目 程炯 为公文归档传入公文密级

		// 联合发文进行第二次归档
		if (summary.getIsunit()) {
			app = DocHierarchyManagerImpl.getNodesMap().get("edoc2");
			if (app != null) {
				newId = this.pigeonholeWithoutAclByType(summary, app, sourceId,
						hasAttachments, name, contentType, destLibId,
						destFolderId, CurrentUser.get().getId(),new Integer(1),secretLevel);//成发集团项目 程炯 为公文归档传入公文密级
			}
		}

		return newId;
	}
	
	/**
	 * 信息归档，不考虑权限
	 */
	public Long pigeonholeInfoWithoutAcl(Long sourceId, boolean hasAttachments, Long destLibId, Long destFolderId, Long userId) throws DocException { 
		// 根据归档源的不同，取得不同的对象
		InfoSummaryManagerCAP infoSummaryManagerCAP = (InfoSummaryManagerCAP)ApplicationContextHolder.getBean("infoSummaryManagerCAP");
		InfoSummaryCAP summary = infoSummaryManagerCAP.getInfoSummaryById(sourceId, false);
		// 判断是否存在同名文档
		String name = summary.getSubject();
		Long contentType = Constants.SYSTEM_INFO;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("info");
		if (app != null)
			newId = this.pigeonholeWithoutAclByType(summary, app, sourceId,
					hasAttachments, name, contentType, destLibId, destFolderId,
					CurrentUser.get().getId(),new Integer(1));

		return newId;
	}	
	
	public Long pigeonholeWithoutAcl(Object appObject, Node appNode,
			Long sourceId, boolean hasAttachments, String name,
			Long contentType, Long destLibId, Long destFolderId, Long userId)
			throws DocException{
		
			return pigeonholeWithoutAclByType(appObject,appNode,sourceId,hasAttachments,name,
				contentType,destLibId,destFolderId,userId,null);
	}
	//成发集团项目 重写pigeonholeWithoutAcl
	public Long pigeonholeWithoutAcl(Object appObject, Node appNode,
			Long sourceId, boolean hasAttachments, String name,
			Long contentType, Long destLibId, Long destFolderId, Long userId, Integer secretLevel)
			throws DocException{
		
			return pigeonholeWithoutAclByType(appObject,appNode,sourceId,hasAttachments,name,
				contentType,destLibId,destFolderId,userId,null,secretLevel);
	}
	/**
	 * 重写pigeonholeWithoutAclByType
	 */
	private Long pigeonholeWithoutAclByType(Object appObject, Node appNode,
			Long sourceId, boolean hasAttachments, String name,
			Long contentType, Long destLibId, Long destFolderId, Long userId,Integer pigeonholeType,Integer secretLevel)
			throws DocException {
		// 2. 声明将来的新Id
		Long newId = null;

		// 遍历数据库表
		for (Node table = appNode.getFirstChild(); table != null; table = table.getNextSibling()) {
			String tableName = table.getNodeName();
			Map drMetadatas = new HashMap();
			Map metadatas = new HashMap();
			if (table.getNodeType() == Node.ELEMENT_NODE) {
				this.handleFields(appObject, tableName, table, drMetadatas, metadatas);

				// 向不同的表保存元数据
				if (tableName.equals("doc_resources")) {
					int minOrder = this.getMinOrder(destFolderId);

					// 只在 doc_resources 表中记录下来源文件的 Id
					DocResource dr = new DocResource();
					dr.setFrName(name);
					dr.setParentFrId(destFolderId);
					dr.setAccessCount(0);
					dr.setCommentCount(0);
					dr.setCommentEnabled(false);
					Timestamp now = new Timestamp(System.currentTimeMillis());
					dr.setCreateTime(now);
					dr.setCreateUserId(userId);
					dr.setDocLibId(destLibId);
					dr.setIsFolder(false);
					dr.setFrOrder(minOrder - 1);
					dr.setFrSize(0);
					dr.setFrType(contentType);
					dr.setLastUpdate(now);
					dr.setLastUserId(userId);
					dr.setSourceId(sourceId);
					dr.setStatus(Byte.parseByte("2"));
					dr.setStatusDate(now);
					dr.setMimeTypeId(contentType);
					dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());
					dr.setIsCheckOut(false);
					dr.setHasAttachments(hasAttachments);
					dr.setSecretLevel(secretLevel);//成发集团项目 程炯 归档直接保存流程密级
					
					if( pigeonholeType != null){
						dr.setPigeonholeType(pigeonholeType.intValue());
					}
					// 处理定义归档的doc_resources数据
					Set<Method> keys = drMetadatas.keySet();
					for (Method m : keys) {
						try {
							m.invoke(dr, drMetadatas.get(m));
						} catch (IllegalArgumentException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						} catch (IllegalAccessException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						} catch (InvocationTargetException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						}
					}
					dr.setFrName(name);

					// 2. 保存，并返回新生成的id
					newId = docResourceDao.saveAndGetId(dr);
				} else if (tableName.equals("doc_metadata")) {
					Set<String> keys = metadatas.keySet();
					docMetadataManager.addMetadata(newId, metadatas);
				}
			}// end of 有效表节点
		}

		return newId;
	}
	/**
	 * 归档，不考虑权限
	 */
	private Long pigeonholeWithoutAclByType(Object appObject, Node appNode,
			Long sourceId, boolean hasAttachments, String name,
			Long contentType, Long destLibId, Long destFolderId, Long userId,Integer pigeonholeType)
			throws DocException {
		// 2. 声明将来的新Id
		Long newId = null;

		// 遍历数据库表
		for (Node table = appNode.getFirstChild(); table != null; table = table.getNextSibling()) {
			String tableName = table.getNodeName();
			Map drMetadatas = new HashMap();
			Map metadatas = new HashMap();
			if (table.getNodeType() == Node.ELEMENT_NODE) {
				this.handleFields(appObject, tableName, table, drMetadatas, metadatas);

				// 向不同的表保存元数据
				if (tableName.equals("doc_resources")) {
					int minOrder = this.getMinOrder(destFolderId);

					// 只在 doc_resources 表中记录下来源文件的 Id
					DocResource dr = new DocResource();
					dr.setFrName(name);
					dr.setParentFrId(destFolderId);
					dr.setAccessCount(0);
					dr.setCommentCount(0);
					dr.setCommentEnabled(false);
					Timestamp now = new Timestamp(System.currentTimeMillis());
					dr.setCreateTime(now);
					dr.setCreateUserId(userId);
					dr.setDocLibId(destLibId);
					dr.setIsFolder(false);
					dr.setFrOrder(minOrder - 1);
					dr.setFrSize(0);
					dr.setFrType(contentType);
					dr.setLastUpdate(now);
					dr.setLastUserId(userId);
					dr.setSourceId(sourceId);
					dr.setStatus(Byte.parseByte("2"));
					dr.setStatusDate(now);
					dr.setMimeTypeId(contentType);
					dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());
					dr.setIsCheckOut(false);
					dr.setHasAttachments(hasAttachments);
					
					if( pigeonholeType != null){
						dr.setPigeonholeType(pigeonholeType.intValue());
					}
					// 处理定义归档的doc_resources数据
					Set<Method> keys = drMetadatas.keySet();
					for (Method m : keys) {
						try {
							m.invoke(dr, drMetadatas.get(m));
						} catch (IllegalArgumentException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						} catch (IllegalAccessException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						} catch (InvocationTargetException e) {
							log.error("归档 pigeonholeWithoutAcl: ", e);
						}
					}
					dr.setFrName(name);

					// 2. 保存，并返回新生成的id
					newId = docResourceDao.saveAndGetId(dr);
				} else if (tableName.equals("doc_metadata")) {
					Set<String> keys = metadatas.keySet();
					docMetadataManager.addMetadata(newId, metadatas);
				}
			}// end of 有效表节点
		}

		return newId;
	}

	/**
	 * 协同归档，不考虑权限
	 */
	public Long pigeonholeColWithoutAcl(Long sourceId, boolean hasAttachments,
			Long destLibId, Long destFolderId, Long userId) throws DocException {
		String name = "";
		// 根据归档源的不同，取得不同的对象
		// sourceId -- affairId
		// 根据Affair可以得到summaryId -- Affair.objectId
		Affair affair = affairManager.getById(sourceId);
		// 2. 声明将来的新Id
		Long newId = null;
		try {
			Integer secretLevel = colManager.getColSummaryById(affair.getObjectId(), true).getSecretLevel();//成发集团项目 程炯 归档查出归档协同流程密级
			String forwardMember = affair.getForwardMember();
			Integer resentTime = affair.getResentTime();
			name = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(),
					forwardMember, resentTime, orgManager, null);
			Long contentType = Constants.SYSTEM_COL;

			Node app = DocHierarchyManagerImpl.getNodesMap().get("collaboration");
			if (app != null)
				newId = this.pigeonholeWithoutAcl(affair, app, sourceId,
						hasAttachments, name, contentType, destLibId, destFolderId,
						userId,secretLevel);//成发集团项目 程炯 为归档传入流程密级
		} catch (ColException e) {
			log.error("协同归档取得流程密级错误", e);
		}
		return newId;
	}

	/**
	 * ajax调用，协同转发的辅助方法
	 */
	public long getSummaryIdByAffairId(long affairId) {
		Affair affair = affairManager.getById(affairId);
		if(affair == null){
			affair = hisAffairManager.getById(affairId);
		}
		
		return affair.getObjectId();
	}

	/**
	 * 调查归档，不考虑权限
	 */
	public Long pigeonholeInquiryWithoutAcl(Long sourceId,
			boolean hasAttachments, Long destLibId, Long destFolderId,
			Long userId) throws DocException {
		String name = "";
		Long contentType = Constants.SYSTEM_INQUIRY;
		// 根据归档源的不同，取得不同的对象
		InquirySurveybasic isb = null;
		InquiryBasicData ibd = null;
		try {
			isb = inquiryManager.getBasicByID(sourceId);
			name = isb.getSurveyName();
			ibd = new InquiryBasicData(isb, orgManager);
		} catch (Exception e) {
			log.error("调查归档，取调查数据：", e);
		}

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("inquiry");
		if (app != null)
			newId = this.pigeonholeWithoutAcl(ibd, app, sourceId,
					hasAttachments, name, contentType, destLibId, destFolderId,
					userId);
		return newId;
	}

	/**
	 * 会议归档，不考虑权限
	 */
	public Long pigeonholeMeetingWithoutAcl(Long sourceId,
			boolean hasAttachments, Long destLibId, Long destFolderId,
			Long userId) throws DocException {
		// 根据归档源的不同，取得不同的对象
		MtMeetingCAP meeting = mtMeetingManagerCAP.getById(sourceId);
		// 判断是否存在同名文档
		String name = meeting.getTitle();
		Long contentType = Constants.SYSTEM_MEETING;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("meeting");
		if (app != null)
			newId = this.pigeonholeWithoutAcl(meeting, app, sourceId,
					hasAttachments, name, contentType, destLibId, destFolderId,
					userId);

		return newId;
	}

	/**
	 * 计划归档，不考虑权限
	 */
	public Long pigeonholePlanWithoutAcl(Long sourceId, boolean hasAttachments,
			Long destLibId, Long destFolderId, Long userId) throws DocException {
		// 根据归档源的不同，取得不同的对象
		Plan plan = planManager.getPlanById(sourceId);
		// 判断是否存在同名文档
		String name = plan.getTitle();
		Long contentType = Constants.SYSTEM_PLAN;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("plan");
		if (app != null)
			newId = this.pigeonholeWithoutAcl(plan, app, sourceId,
					hasAttachments, name, contentType, destLibId, destFolderId,
					userId);
		return newId;
	}

	/**
	 * 邮件归档，不考虑权限
	 */
	public Long pigeonholeMailWithoutAcl(Long sourceId, boolean hasAttachments,
			Long destLibId, Long destFolderId, Long userId) throws DocException {
		String name = "";
		// 根据归档源的不同，取得不同的对象
		MailInfo mail = null;
		try {
			mail = webMailManager.getMailInfoById(userId, sourceId);
			name = mail.getSubject();
		} catch (Exception e) {
			log.error("邮件归档，取邮件数据：", e);
		}

		Long contentType = Constants.SYSTEM_MAIL;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("mail");
		if (app != null)
			newId = this.pigeonholeWithoutAcl(mail, app, sourceId,
					hasAttachments, name, contentType, destLibId, destFolderId,
					userId);
		return newId;
	}

	/**
	 * 新闻归档，不考虑权限
	 */
	public Long pigeonholeNewsWithoutAcl(Long sourceId, boolean hasAttachments,
			Long destLibId, Long destFolderId, Long userId) throws DocException {
		// 根据归档源的不同，取得不同的对象
		NewsData news = newsDataManager.getById(sourceId);
		// 判断是否存在同名文档
		String name = news.getTitle();
		Long contentType = Constants.SYSTEM_NEWS;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("news");
		if (app != null)
			newId = this.pigeonholeWithoutAcl(news, app, sourceId,
					hasAttachments, name, contentType, destLibId, destFolderId,
					userId);

		return newId;
	}

	/**
	 * 公告归档，不考虑权限
	 */
	public Long pigeonholeBulletinWithoutAcl(Long sourceId,
			boolean hasAttachments, Long destLibId, Long destFolderId,
			Long userId) throws DocException {
		// 根据归档源的不同，取得不同的对象
		BulData bul = bulDataManager.getById(sourceId);
		// 判断是否存在同名文档
		String name = bul.getTitle();
		Long contentType = Constants.SYSTEM_BULLETIN;
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("bulletin");
		if (app != null)
			newId = this.pigeonholeWithoutAcl(bul, app, sourceId,
					hasAttachments, name, contentType, destLibId, destFolderId,
					userId);
		return newId;
	}

	public boolean hasPigeonholeSource(Long docId, Integer appKey) {
		if (appKey == null || docId == null)
			return false;

		ApplicationCategoryEnum app = ApplicationCategoryEnum.valueOf(appKey);
		DocResource dr = this.getDocResourceById(docId);
		return this.hasPigeonholeSource(app, dr.getSourceId());
	}

	public boolean hasPigeonholeSource(ApplicationCategoryEnum app, Long sourceId) {
		if (app != null && sourceId != null) {
			try {
				Object obj = null;
				if (app.key() == ApplicationCategoryEnum.collaboration.key()) {
					obj = affairManager.getById(sourceId);
		            //看一下是否被转储了
		            if(obj == null){
		            	obj = this.hisAffairManager.getById(sourceId);
		            }
				} else if (app.key() == ApplicationCategoryEnum.edoc.key()) {
					obj = edocSummaryManager.findById(sourceId);
				} else if (app.key() == ApplicationCategoryEnum.meeting.key()) {
					obj = mtMeetingManagerCAP.getById(sourceId);
				} else if (app.key() == ApplicationCategoryEnum.plan.key()) {
					obj = planManager.getPlanById(sourceId);
				} else if (app.key() == ApplicationCategoryEnum.inquiry.key()) {
					obj = inquiryManager.getBasicByID(sourceId);
				} else if (app.key() == ApplicationCategoryEnum.bulletin.key()) {
					obj = bulDataManager.getById(sourceId);
				} else if (app.key() == ApplicationCategoryEnum.news.key()) {
					obj = newsDataManager.getById(sourceId);
				} else if (app.key() == ApplicationCategoryEnum.mail.key()) {
					obj = webMailManager.getMailInfoById(CurrentUser.get().getId(), sourceId);
				} else if (app.key() == ApplicationCategoryEnum.info.key()) {
					InfoSummaryManagerCAP infoManager = (InfoSummaryManagerCAP)ApplicationContextHolder.getBean("infoSummaryManagerCAP");
					obj = infoManager.getInfoSummaryById(sourceId, true);
				}else if (app.key() == ApplicationCategoryEnum.infoStat.key()) {
					InfoStatManagerCAP infoStatManager = (InfoStatManagerCAP)ApplicationContextHolder.getBean("infoStatManagerCAP");
					obj = infoStatManager.getInfoStatById(sourceId);
				}
				return obj != null;
			} 
			catch (Exception e) {
				log.error("检测归档源是否存在过程中出现异常[归档类型：" + app.name() + "]：", e);
			}
		}
		return false;
	}

	public boolean hasPigeonholeSource(Integer appKey, Long sourceId) {
		if (appKey == null || sourceId == null)
			return false;

		ApplicationCategoryEnum app = ApplicationCategoryEnum.valueOf(appKey);
		return this.hasPigeonholeSource(app, sourceId);
	}

	/** *******************************归档结束************************************** */

	/**
	 * 创建链接
	 */
	public DocResource createLink(Long sourceId, Long docLibId, Long destFolderId,
			Long userId, String orgIds) throws DocException {
		if (!this.hasCreatePermission(docResourceDao.get(destFolderId), userId, orgIds))
			throw new DocException("DocLang.doc_link_create_doclink_alert");
		return this.createLinkWithoutAcl(sourceId, docLibId, destFolderId, userId);
	}

	/**
	 * 批量创建链接,包括文档、文档夹
	 */
	public List<Long> createLinks(List<Long> sourceIds, Long docLibId,
			Long destFolderId, Long userId, String orgIds) throws DocException {
		if (!this.hasCreatePermission(docResourceDao.get(destFolderId), userId, orgIds))
			throw new DocException("DocLang.doc_link_create_doclink_alert");

		return this.createLinksWithoutAcl(sourceIds, docLibId, destFolderId, userId);
	}

	/**
	 * 批量创建链接,包括文档、文档夹，不考虑权限
	 */
	public List<Long> createLinksWithoutAcl(List<Long> sourceIds,
			Long docLibId, Long destFolderId, Long userId) throws DocException {
		// 判断目的文档夹是否允许创建子文档夹
		DocResource destFolder = docResourceDao.get(destFolderId);
		if (!destFolder.getSubfolderEnabled())
			throw new DocException("DocLang.doc_link_create_folder");

		List<Long> ret = new ArrayList<Long>();

		// 判断是否存在同名文档链接
		for (Long sourceId : sourceIds) {
			DocResource sourceDr = docResourceDao.get(sourceId);
			if (this.hasSameNameAndSameTypeDr(destFolderId, sourceDr.getFrName(), Constants.LINK))
				continue;
			DocResource dr = this.createLinkOnly(sourceDr, destFolderId, docLibId, userId);
			if(dr != null)
				ret.add(dr.getId());
		}

		return ret;
	}

	/**
	 * 创建链接，不考虑权限
	 */
	public DocResource createLinkWithoutAcl(Long sourceId, Long docLibId,
			Long destFolderId, Long userId) throws DocException {
		// 判断是否存在同名文档链接
		DocResource sourceDr = docResourceDao.get(sourceId);
		if (this.hasSameNameAndSameTypeDr(destFolderId, sourceDr.getFrName(),
				(sourceDr.getIsFolder() ? Constants.LINK_FOLDER : Constants.LINK)))
			throw new DocException("DocLang.doc_link_create_same_name");

		return this.createLinkOnly(sourceDr, destFolderId, docLibId, userId);
	}

	// 创建链接，没有任何判断
	private DocResource createLinkOnly(DocResource sourceDr, Long destFolderId, Long docLibId, Long userId) {
		int minOrder = this.getMinOrder(destFolderId);

		DocResource docResource = new DocResource();
		docResource.setAccessCount(sourceDr.getAccessCount());
		docResource.setCommentCount(sourceDr.getCommentCount());
		docResource.setCommentEnabled(sourceDr.getCommentEnabled());
		docResource.setFrName(sourceDr.getFrName());
		docResource.setFrOrder(minOrder - 1);
		docResource.setFrSize(sourceDr.getFrSize());
		docResource.setIsFolder(false);
		docResource.setStatus(sourceDr.getStatus());
		docResource.setStatusDate(sourceDr.getStatusDate());
		docResource.setSubfolderEnabled(false);
		docResource.setDocLibId(docLibId);
		docResource.setFrType(sourceDr.getIsFolder() ? Constants.LINK_FOLDER : Constants.LINK);
		docResource.setSourceId(sourceDr.getId());
		docResource.setParentFrId(destFolderId);
		docResource.setCreateUserId(userId);
		docResource.setCreateTime(new Timestamp(new Date().getTime()));
		docResource.setLastUserId(userId);
		docResource.setLastUpdate(new Timestamp(new Date().getTime()));
		docResource.setMimeTypeId(sourceDr.getIsFolder() ? Constants.LINK_FOLDER : Constants.LINK);
		docResource.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(docResource.getMimeTypeId()).getOrderNum());
		docResource.setIsCheckOut(false);
		
		docResourceDao.saveAndGetId(docResource);
		return docResource;
	}

	public void updateDocResource(Long docResourceId, Map<String, Object> properties) {
		if(properties != null && !properties.isEmpty())
			docResourceDao.update(docResourceId, properties);
	}
	
	public DocResource replaceDoc(DocResource dr, V3XFile file, Long userId, String orgIds, boolean remainOld) throws DocException {
		if (this.hasEditPermission(dr, userId, orgIds))
			return this.replaceDocWithoutAcl(dr, file, userId, remainOld);
		else
			throw new DocException("doc_deal_no_acl");
	}

	public DocResource replaceDocWithoutAcl(DocResource dr, V3XFile file, Long userId, boolean remainOld) throws DocException {
		Long docResourceId = dr.getId();
		boolean old_isImage = dr.isImage();
		boolean old_isPdf = dr.isPDF();
		boolean old_hasDocBody = old_isImage || old_isPdf;
		Timestamp now = new Timestamp(new Date().getTime());
		
		try {
			// 删除附件、关联文档
			attachmentManager.deleteByReference(docResourceId);
			
			if(!remainOld)
				fileManager.deleteFile(docResourceId, true);
		} catch (BusinessException e) {
			log.error("替换文件时候，删除原来的附件、关联文档或源文件时出现异常：", e);
		}
		String name = file.getFilename();
		if(!name.equals(dr.getFrName())) {
			docResourceDao.updateLinkName(docResourceId, name);
		}
		dr.setFrName(name);
		dr.setAccessCount(0);
		dr.setCommentCount(0);
		dr.setCommentEnabled(true);
		dr.setHasAttachments(false);
		dr.setIsFolder(false);
		dr.setLastUpdate(now);
		dr.setLastUserId(userId);
		dr.setSourceId(file.getId());
		dr.setIsCheckOut(false);
		dr.setCheckOutUserId(null);
		// 从文件管理组件得到文件类型后缀
		String postfix = name.substring(name.lastIndexOf(".") + 1, name.length());
		dr.setMimeTypeId(docMimeTypeManager.getDocMimeTypeByFilePostix(postfix));
		dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());

		docResourceDao.update(dr);
		this.docResourceDao.getHibernateTemplate().flush();
		
		boolean new_isImage = dr.isImage();
		boolean new_isPdf = dr.isPDF();
		boolean new_hasDocBody = new_isImage || new_isPdf;
		
		String formatType = new_isImage ? com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML : 
			com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF;
		
		if(old_hasDocBody && new_hasDocBody) {
			this.docBodyDao.update(docResourceId, FormBizConfigUtils.newHashMap(new String[]{"createDate", "content", "bodyType"}, 
					new Object[]{now, file.getId().toString(), formatType}));
		}
		else if(old_hasDocBody && !new_hasDocBody) {
			this.docBodyDao.delete(docResourceId);
		}
		else if(!old_hasDocBody && new_hasDocBody) {
			DocBody db = new DocBody();
			db.setCreateDate(now);
			db.setContent(file.getId().toString());
			db.setBodyType(formatType);
			this.saveBody(docResourceId, db);
		}

		this.updateFileSize(docResourceId);
		return dr;
	}
	
	public DocResource replaceDocWithoutAcl(Long docResourceId, V3XFile file, Long userId, boolean remainOld) throws DocException {
		DocResource dr = this.getDocResourceById(docResourceId);
		return this.replaceDocWithoutAcl(dr, file, userId, remainOld);
	}

	public void checkOutDocResource(Long docResourceId, Long userId) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		this.docResourceDao.update(docResourceId, FormBizConfigUtils.newHashMap(
				new String[]{"isCheckOut", "checkOutTime", "checkOutUserId", "lastUpdate", "lastUserId"}, 
				new Object[]{true, now, userId, now, userId}));
	}

	/**
	 * 签入文档
	 */
	public void checkInDocResource(Long docResourceId, Long userId, String orgIds) throws DocException {
		DocResource dr = docResourceDao.get(docResourceId);
		// 判断当前用户对目标文档夹是否具有签入权限
		List<Integer> levels = Arrays.asList(Constants.ALLPOTENT);
		if (!this.hasPermission(dr, userId, orgIds, levels))
			throw new DocException("doc_deal_no_acl");

		this.checkInDocResourceWithoutAcl(docResourceId, userId);
	}

	/**
	 * 签入文档,不考虑权限
	 */
	public void checkInDocResourceWithoutAcl(Long docResourceId, Long userId) {
		this.docResourceDao.update(docResourceId, FormBizConfigUtils.newHashMap(
				new String[]{"isCheckOut", "lastUpdate", "lastUserId"}, 
				new Object[]{false, new Timestamp(System.currentTimeMillis()), userId}));
	}

	public void checkInDocResourcesWithoutAcl(List<Long> drIds, Long userId) throws DocException {
		String hql = "update " + DocResource.class.getCanonicalName() + " set isCheckOut=false, lastUpdate=?, lastUserId=? where id in (:ids)";
		this.docResourceDao.bulkUpdate(hql, FormBizConfigUtils.newHashMap("ids", drIds), new Timestamp(System.currentTimeMillis()), userId);
	}

	/**
	 * 修改复合文档
	 */
	public void updateDoc(FolderItemDoc doc, Long userId, String orgIds) throws DocException {
		if (this.hasEditPermission(doc.getDocResource(), userId, orgIds))
			this.updateDocWithoutAcl(doc, userId);
		else
			throw new DocException("doc_deal_no_acl");
	}

	/**
	 * 修改复合文档, 不考虑权限
	 */
	public DocResource updateDocWithoutAcl(FolderItemDoc doc, Long userId) throws DocException {
		DocResource dr = doc.getDocResource();
		String body = doc.getBody();
		if(!doc.getName().equals(dr.getFrName())) {
			this.docResourceDao.updateLinkName(dr.getId(), doc.getName());
		}
		dr.setFrName(doc.getName());
		dr.setLastUpdate(new Timestamp(new java.util.Date().getTime()));
		dr.setLastUserId(userId);
		dr.setFrType(doc.getContentTypeId());
		dr.setFrDesc(doc.getDesc());
		dr.setKeyWords(doc.getKeywords());
		dr.setVersionComment(doc.getVersionComment());

		// 修改签出标记
		dr.setCheckOutTime(null);
		dr.setCheckOutUserId(null);
		dr.setIsCheckOut(false);
		dr.setHasAttachments(doc.getHasAtt());

		docResourceDao.update(dr);
		this.updateBody(doc.getDocResourceId(), body);
		List<Attachment> atts = attachmentManager.getByReference(dr.getId());
		
		DocBody docBody = new DocBody();
		docBody.setContent(body);
		this.updateDocSize(dr.getId(), docBody, atts);
		return dr;
	}

	/**
	 * 更改复合文档的大小
	 */
	public void updateDocSize(Long docResourceId, DocBody docBody,
			List<Attachment> atts) throws DocException {
		DocResource dr = docResourceDao.get(docResourceId);
		long oldSize = dr.getFrSize();
		long size = 0L;
		long formatType = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getFormatType();
		if (formatType == Constants.FORMAT_TYPE_DOC_A6) {
			size = docBody.getContent().getBytes().length;
		} 
		else {
			V3XFile file;
			try {
				file = fileManager.getV3XFile(Long.valueOf(docBody.getContent()));
				if (file != null)
					size = file.getSize();
			} catch (NumberFormatException e) {
				log.error("从fileManager取得在线编辑的V3xFile", e);
			} catch (BusinessException e) {
				log.error("从fileManager取得在线编辑的V3xFile", e);
			}
		}
		for (Attachment att : atts) {
			if (att.getFileUrl() != null) {
				size += att.getSize();
			}
		}
		dr.setFrSize(size);
		dr.setHasAttachments(CollectionUtils.isNotEmpty(atts));
		
		docResourceDao.update(dr);

		// 更改个人文档库的占用空间
		if (this.isPersonalLib(dr.getDocLibId())) {
			DocStorageSpace space = docSpaceManager.getDocSpaceByUserId(CurrentUser.get().getId());
			if (space.getTotalSpaceSize() < (dr.getFrSize() - oldSize + space.getUsedSpaceSize()))
				throw new DocException("personal_storage_not_enough");
			
			docSpaceManager.addUsedSpaceSize(CurrentUser.get().getId(), dr.getFrSize() - oldSize);
		}
	}

	public void updateDocAttFlag(Long docResourceId, boolean attaFlag) throws DocException {
		DocResource dr = docResourceDao.get(docResourceId);
		dr.setHasAttachments(attaFlag);
		docResourceDao.update(dr);
	}
	
	private void updateFileSize(DocResource dr) throws DocException {
		long oldSize = dr.getFrSize();
		V3XFile file;
		long size = 0L;
		try {
			file = fileManager.getV3XFile(dr.getSourceId());
			if (file != null)
				size = file.getSize();
		} catch (BusinessException e) {
			log.error("从fileManager取得在线编辑的V3xFile", e);
		}
		dr.setFrSize(size);

		docResourceDao.update(dr);

		// 更改个人文档库的占用空间
		if (this.isPersonalLib(dr.getDocLibId())) {
			DocStorageSpace space = docSpaceManager.getDocSpaceByUserId(CurrentUser.get().getId());
			if (space.getTotalSpaceSize() < (dr.getFrSize() - oldSize + space.getUsedSpaceSize()))
				throw new DocException("personal_storage_not_enough");
			
			docSpaceManager.addUsedSpaceSize(CurrentUser.get().getId(), dr.getFrSize() - oldSize);
		}
	}

	/**
	 * 更改上传文件的大小
	 */
	public void updateFileSize(Long docResourceId) throws DocException {
		DocResource dr = docResourceDao.get(docResourceId);
		if(dr != null)
			this.updateFileSize(dr);
	}

	/**
	 * 判断给定的libId对应的库是否个人文档库
	 */
	public boolean isPersonalLib(Long libId) {
		if (libId == null)
			return false;

		DocLib lib = this.docUtils.getDocLibById(libId);
		return lib.getType() == Constants.PERSONAL_LIB_TYPE.byteValue();
	}

	/**
	 * 判断用户是否库的管理员
	 */
	public boolean isOwnerOfLib(Long libId, Long userId) {
		return this.docUtils.isOwnerOfLib(userId, libId);
	}

	/**
	 * 保存复合文档的正文
	 */
	public void saveBody(Long docResourceId, DocBody docBody) {
		// 保存正文到到 doc_body 表
		docBody.setDocResourceId(docResourceId);
		docBodyDao.save(docBody);
	}

	public void updateBody(Long docResourceId, String content) {
		String hql = "update " + DocBody.class.getCanonicalName() + " set content=? where docResourceId=?";
		docBodyDao.bulkUpdate(hql, null, content, docResourceId);
	}

	/**
	 * 删除复合文档的正文
	 */
	public void removeBody(Long docResourceId) {
		docBodyDao.delete(docResourceId.longValue());
	}

	/**
	 * 查找复合文档的正文
	 */
	public DocBody getBody(Long docResourceId) {
		return docBodyDao.get(docResourceId);
	}
	
	public DocResource updateFileWithoutAcl(DocEditVO vo, byte docLibType, boolean remainOldFile) throws DocException {
		return updateFileWithoutAcl(vo, docLibType, remainOldFile, false);
	}
	public DocResource updateFileWithoutAcl(DocEditVO vo, byte docLibType, boolean remainOldFile, boolean replaceFlag) throws DocException {
		DocResource dr = vo.getDocResource();
		long userId = CurrentUser.get().getId();
		// 替换
		V3XFile file = vo.getFile();
		if (file != null) {
			// 如需保留历史版本，则保留源文件
			if(!remainOldFile) {
				try {
					fileManager.deleteFile(dr.getSourceId(), true);
				} catch (BusinessException e) {
					log.error("从fileManager删除在线编辑的V3xFile", e);
				}
			}
			
			dr.setSourceId(file.getId());
			// 从文件管理组件得到文件类型后缀
			String name = file.getFilename();
			String postfix = name.substring(name.lastIndexOf(".") + 1, name.length());
			dr.setMimeTypeId(docMimeTypeManager.getDocMimeTypeByFilePostix(postfix));
			dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());
		}
		
		if(!vo.getName().equals(dr.getFrName())) {
			this.docResourceDao.updateLinkName(dr.getId(), vo.getName());
		}
		dr.setFrName(vo.getName());
		dr.setFrType(vo.getContentTypeId());
		dr.setFrDesc(vo.getDesc());
		dr.setKeyWords(vo.getKeywords());
		dr.setVersionComment(vo.getVersionComment());

		dr.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		dr.setLastUserId(userId);

		// 修改签出标记
		dr.setCheckOutTime(null);
		dr.setCheckOutUserId(null);
		dr.setIsCheckOut(false);

		docResourceDao.update(dr);
		//pxb修改docBody
		if(replaceFlag){
			boolean new_isImage = dr.isImage();
			String formatType = new_isImage ? com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML : 
				com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF;
			Timestamp now = new Timestamp(new Date().getTime());
			docBodyDao.update(dr.getId(), FormBizConfigUtils.newHashMap(new String[]{"createDate", "content", "bodyType"}, 
					new Object[]{now, file.getId().toString(), formatType}));
		}
		this.updateFileSize(dr);
		return dr;
	}

	public DocResource updateFileWithoutAcl(DocEditVO vo, byte docLibType) throws DocException {
		return this.updateFileWithoutAcl(vo, docLibType, false);
	}

	public synchronized void accessOneTime(Long docResourceId, boolean learning, boolean personalLib) {
		if(!personalLib) {
			String hql = "update " + DocResource.class.getCanonicalName() + " set accessCount=accessCount+1 where id=?";
			this.docResourceDao.bulkUpdate(hql, null, docResourceId);
		}
		
		if(learning) {
			docLearningManager.learnTheDoc(docResourceId);
		}
	}

	@Deprecated
	public synchronized void accessOneTime(Long docResourceId) {
		DocResource dr = docResourceDao.get(docResourceId);
		if(dr != null){
			boolean personalLib = this.isPersonalLib(dr.getDocLibId());
			this.accessOneTime(docResourceId, dr.getIsLearningDoc(), personalLib);
		}
	}

	public synchronized void forumOneTime(Long docResourceId) {
		String hql = "update " + DocResource.class.getCanonicalName() + " set commentCount=commentCount+1 where id=?";
		this.docResourceDao.bulkUpdate(hql, null, docResourceId);
	}

	public synchronized void deleteForumOneTime(Long docResourceId) {
		DocResource dr = docResourceDao.get(docResourceId);
		int nc = dr.getCommentCount() - 1;
		dr.setCommentCount(nc > 0 ? nc : 0);
		docResourceDao.update(dr);
	}

	public void moveDocWithoutAcl4Project(DocResource dr) {
		if (dr.getIsFolder()) {
			this.moveFolderWithoutAcl4Project(dr);
		} else {
			this.moveLeafWithoutAcl4Project(dr);
		}
	}

	private void moveFolderWithoutAcl4Project(DocResource dr) {
		// 2. 根据将要被移动的文档夹的 logicalPath 找到它下面所有的子节点
		// 2.1 找到文档夹的所有子节点
		String oldParentPath = dr.getLogicalPath();
		// 此处比较的logicalPath后边加上了一个点 . 表示查找它的下级内容，
		// 防止在寻找 1.2 下面的内容时找到 1.20
		List<DocResource> drs = docResourceDao.findByLike("logicalPath", oldParentPath + ".");
		// 2.2 保存原路径长度，供后边(4)使用
		int oldParentPathLength = oldParentPath.length();

		// 3. 移动文档夹本身
		this.moveLeafWithoutAcl4Project(dr);
		// 4. 修改所有子节点的表记录。
		// 4.1 修改 doc_resources 表的记录，此处不用修改 parentId
		String newParentPath = Constants.DOC_LIB_ROOT_ID_PROJECT + "." + dr.getId();
		for (DocResource tempDr : drs) {
			tempDr.setDocLibId(Constants.DOC_LIB_ID_PROJECT);
			tempDr.setLastUserId(-1L);
			tempDr.setLastUpdate(new Timestamp(System.currentTimeMillis()));

			StringBuffer sb = new StringBuffer(tempDr.getLogicalPath());
			tempDr.setLogicalPath(newParentPath + sb.substring(oldParentPathLength));
			docResourceDao.update(tempDr);
		}

	}

	private void moveLeafWithoutAcl4Project(DocResource dr) {
		dr = docResourceDao.get(dr.getId());
		// 修改 doc_resources 表中相关属性
		dr.setDocLibId(Constants.DOC_LIB_ID_PROJECT);
		dr.setParentFrId(Constants.DOC_LIB_ROOT_ID_PROJECT);
		dr.setLastUserId(-1L);
		dr.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		dr.setFrOrder(1);
		dr.setCommentEnabled(false);

		String newParentPath = "" + Constants.DOC_LIB_ROOT_ID_PROJECT;
		dr.setLogicalPath(newParentPath + "." + dr.getId());

		docResourceDao.update(dr);
	}

	/**
	 * 重命名文档/文档夹
	 */
	public void renameDoc(Long docResourceId, String newName, Long userId,
			String orgIds) throws DocException {
		DocResource docResource = docResourceDao.get(docResourceId);
		if (!this.hasEditPermission(docResource, userId, orgIds))
			throw new DocException("对不起，您没有重命名该文档的权限。");

		this.renameDocWithoutAcl(docResourceId, newName, userId);
	}
	
	public void renameDocWithoutAcl(Long docResourceId, String newName, Long userId) {
		docResourceDao.update(docResourceId, 
				FormBizConfigUtils.newHashMap(new String[]{"lastUserId", "lastUpdate", "frName", "isCheckOut"}, 
				new Object[]{userId, new Timestamp(System.currentTimeMillis()), newName, false}));
		
		docResourceDao.updateLinkName(docResourceId, newName);
	}

	public void setFolderCommentEnabled(DocResource drs, boolean enabled, int includeDocs, Long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "update DocResource set lastUserId = ?, lastUpdate = ?, commentEnabled = ? where id = :id ";
		params.put("id", drs.getId());
		if (includeDocs == Constants.SCOPE_LEV1_CHILDS) {
			hql += " or (parentFrId = :id and isFolder = false) ";
		} 
		else if (includeDocs == Constants.SCOPE_ALL) {
			hql += " or logicalPath like :lp ";
			params.put("lp", drs.getLogicalPath() + ".%");
		}
		this.docResourceDao.bulkUpdate(hql, params, userId, new Timestamp(System.currentTimeMillis()), enabled);
	}
	
	public void setFolderVersionEnabled(DocResource drs, boolean fve, int includeDocs, Long userId) {
		String hql = "update DocResource set lastUserId = ?, lastUpdate = ?, versionEnabled = ? where id = :id ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", drs.getId());
		
		//huangfj 2011-12-30 开启历史版本，仅应用到本文档夹下：仅对本文档夹下的文档起作用，不对文档夹起作用
		if (includeDocs == Constants.SCOPE_LEV1_CHILDS) {
			hql += " or (parentFrId = :id and isFolder=false)";
		} 
		else if (includeDocs == Constants.SCOPE_ALL) {
			hql += " or logicalPath like :lp ";
			params.put("lp", drs.getLogicalPath() + ".%");
		}
		this.docResourceDao.bulkUpdate(hql, params, userId, new Timestamp(System.currentTimeMillis()), fve);
	}

	public void setDocLearning(long docResourceId) {
		Map<String, Object> columns = FormBizConfigUtils.newHashMap(new String[]{"isLearningDoc", "commentEnabled"}, new Object[]{true, true});
		docResourceDao.update(docResourceId, columns);
	}

	public void setDocLearning(List<Long> docResourceIds) {
		String hql = "update DocResource set isLearningDoc=?, commentEnabled=? where id in (:ids)";
		Map<String, Object> params = FormBizConfigUtils.newHashMap("ids", docResourceIds);
		this.docResourceDao.bulkUpdate(hql, params, true, true);
	}

	public void cancelDocLearning(long docResourceId) {
		docResourceDao.update(docResourceId, FormBizConfigUtils.newHashMap("isLearningDoc", false));
	}

	public void cancelDocLearning(List<Long> docResourceIds) {
		String hql = "update DocResource set isLearningDoc=? where id in (:ids)";
		Map<String, Object> params = FormBizConfigUtils.newHashMap("ids", docResourceIds);
		this.docResourceDao.bulkUpdate(hql, params, false);
	}

	// 不考虑权限(在进入此方法之前已经判断过了)
	public void removeDocWithoutAcl(DocResource dr, Long userId, boolean first) throws DocException {
		Long drId = dr.getId();
		if (first) {
			// 删除相关的权限记录
			docAclManager.deletePotent(dr);
			// 删除元数据
			if (contentTypeManager.hasExtendMetadata(dr.getFrType()))
				docMetadataManager.deleteMetadata(dr);
			// 删除评论
			docForumManager.deleteDocForumByDocId(dr);
			// 删除收藏
			docFavoriteManager.deleteFavoriteDocByDoc(dr);
			// 删除订阅
			docAlertManager.deleteAlertByDocResourceId(dr);
			// 删除最新订阅
			docAlertLatestManager.deleteAlertLatestsByDoc(dr);
		}

		if (dr.getIsFolder() && first) {
			this.removeFolderWithoutAcl(dr, userId);
		} else {
			if (!dr.getIsFolder()) {
				// 删除全文检索
				try {
					indexManager.deleteFromIndex(ApplicationCategoryEnum.doc, drId);
				} catch (Exception e) {
					log.error("从indexManager删除检索项。", e);
				}
				// 删除学习文档
				if (dr.getIsLearningDoc())
					docLearningManager.deleteLearnByDocId(drId);

				// 2. 根据文档类型判断有无内容，如果有，调用内容管理接口删除内容
				try {
					DocMgrUtils.deleteBodyAndSource(dr, docMimeTypeManager, fileManager, docBodyDao);
				} catch (BusinessException e) {
					log.error("从fileManager删除在线编辑的V3xFile出现异常：", e);
				}

				// 删除附件
				try {
					attachmentManager.removeByReference(drId);
				} catch (BusinessException e) {
					log.error("从attachManager删除附件。", e);
				}
				// 删除个人使用空间
				if (this.isPersonalLib(dr.getDocLibId()))
					if (dr.getFrSize() > 0)
						docSpaceManager.subUsedSpaceSize(userId, dr.getFrSize());
			}
			
			// 删除历史版本信息记录
			this.docVersionInfoManager.deleteByDocResourceId(drId);

			// 3. 删除 doc_resources 表中的记录
			docResourceDao.delete(drId.longValue());
		}
	}
	
	public void removeFolderWithoutAcl(DocResource dr, Long userId) throws DocException {
		// 1. 查找 docLogicalPath
		String parentLogicalPath = dr.getLogicalPath();
		// 2. 根据文档夹的 logicalPath 查找所有对应文档夹下的所有内容，包含文档、文档夹，使用 like path.%
		List<DocResource> children = docResourceDao.findByLike("logicalPath", parentLogicalPath + ".");

		if (children == null || children.size() == 0) {
			docResourceDao.delete(dr.getId().longValue());
		} else {
			// 添加文档夹本身进去需要删除列表
			children.add(dr);
			// 3. 逐个删除节点
			for (DocResource tempDr : children) {
				this.removeDocWithoutAcl(tempDr, userId, false);
			}
		}
	}

	/**
	 * 删除个人文档库下所有内容，不保留根节点 库类型和用户权限在文档库管理处判断，此处不再进行判断
	 * @throws DocException
	 */
	protected void emptyLib(Long libId, Long userId) throws DocException {
		this.removeDocWithoutAcl(this.getRootByLibId(libId), userId, true);
	}

	public DocResource getRootByLibId(Long libId) {
		String hql = "from " + DocResource.class.getName() + " where parentFrId=0 and docLibId=?";
		return (DocResource)this.docResourceDao.findUnique(hql, null, libId);
	}
	
	@SuppressWarnings("unchecked")
	public List<DocResource> getRootByLibIds(List<Long> libIds) {
		String hql = "from DocResource dr where parentFrId = 0 and docLibId in (:libIds)";
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("libIds", libIds);
		return docResourceDao.find(hql, -1, -1, nmap);
	}
	
	public Map<Long, DocResource> getRootMapByLibIds(List<Long> libIds) {
		List<DocResource> drs = this.getRootByLibIds(libIds);
		Map<Long, DocResource> result = new HashMap<Long, DocResource>();
		if(CollectionUtils.isNotEmpty(drs)) {
			for(DocResource dr : drs) {
				result.put(dr.getDocLibId(), dr);
			}
		}
		return result;
	}

	public List<DocResource> getRootsByLibIds(List<Long> ids, String orgIds) {
		List<DocResource> drs = this.getRootByLibIds(ids);
		if (CollectionUtils.isNotEmpty(drs)) {
			String docIds = FormBizConfigUtils.getIdStrs(drs);
			Map<Long, Set<DocAcl>> map = this.docAclManager.getAclSet(docIds, orgIds);
			for (DocResource dr : drs) {
				Set<Integer> set = new HashSet<Integer>();
				for (DocAcl da : map.get(dr.getId())) {
					if (da.getSharetype().byteValue() == Constants.SHARETYPE_DEPTSHARE
							&& da.getPotenttype() != Constants.NOPOTENT) {
						set.add(da.getPotenttype());
					}
				}
				dr.setAclSet(set);
			}
		}

		return drs;
	}
	
	public List<DocResource> getRootsByLibIds(String idStrs, String orgIds) {
		return this.getRootsByLibIds(FormBizConfigUtils.parseStr2Ids(idStrs), orgIds);
	}

	/**
	 * 得到某个用户的个人文档库的根
	 */
	public DocResource getPersonalFolderOfUser(long userId) {
		String hql = "select dr from DocResource as dr where dr.createUserId = ? and dr.frType = ?";

		List<DocResource> list = docResourceDao.find(hql, userId,
				Constants.FORMAT_TYPE_FOLDER_MINE);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;

	}

	/**
	 * 得到某个用户的个人文档库的根
	 */
	public DocResource getPersonalLibRootOfUser(long userId) {
		String hql = "select dr from DocResource as dr, DocLib as dl, DocLibOwner as dlo where "
				+ " dr.docLibId = dl.id and dl.id = dlo.docLibId and dlo.ownerId = ? and dl.type = ?";

		List<DocResource> list = docResourceDao.find(hql, userId, Constants.PERSONAL_LIB_TYPE.byteValue());
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;

	}

	/**
	 * 查找我的文档库下的文档夹 在查询借阅，共享时考虑权限
	 */
	public List<DocResource> findMyFolders(Long parentId, Long contentType, Long userId, String orgIds) {
		// 根据传过来的内容类型，调用相应的方法
		if (contentType == Constants.FOLDER_SHARE) {
			return this.getDrListByUserIds(this.getShareUserIds(userId, orgIds), Constants.PERSON_SHARE, userId);
		} else if (contentType == Constants.FOLDER_BORROW) {
			return this.getDrListByUserIds(this.getBorrowUserIds(userId, orgIds), Constants.PERSON_BORROW, userId);
		} else if (contentType == Constants.PERSON_SHARE) {
			return this.getShareRootDocs(parentId, userId, orgIds);
		} else if (contentType == Constants.PERSON_BORROW || contentType == Constants.DEPARTMENT_BORROW) {
			return new ArrayList<DocResource>();
		} else {
			String hql = "from DocResource as d where d.parentFrId=? and d.isFolder=true " + Order_By;
			return docResourceDao.find(hql, parentId);
		}
	}
	
	/** 排序：按照文件编号、创建日期降序排列 */
	private static String Order_By = " order by d.frOrder ";
	
	public List<DocResource> findAllMyDocsByPageByDate(Long parentId, Long contentType, Integer pageNo, Integer pageSize, Long userId) {
		return this.findAllMyDocsByPage(parentId, contentType, pageNo, pageSize, userId, Order_By);
	}

	public List<DocResource> findAllMyDocsByPage(Long parentId, Long contentType, Integer pageNo, Integer pageSize, Long userId) {
		return this.findAllMyDocsByPage(parentId, contentType, pageNo, pageSize, userId, Order_By);
	}

	/**
	 * 分页查找我的文档库下的所有内容
	 * 
	 * 不考虑权限
	 */
	private List<DocResource> findAllMyDocsByPage(Long parentId,
			Long contentType, Integer pageNo, Integer pageSize, Long userId,
			String orderStr) {
		List<DocResource> ret = null;
		V3xOrgMember member = null;
		try {
			 member = orgManager.getMemberById(CurrentUser.get().getId());
		} catch (BusinessException e) {
			log.error("文档流程密级筛选时获取当前用户错误", e);
		}
		// 根据传过来的内容类型，调用相应的方法
		String orgIds = Constants.getOrgIdsOfUser(userId);
		if (contentType == Constants.FOLDER_SHARE)
			ret = this.getDrListByUserIds(this.getShareUserIdsByPage(userId,
					orgIds, pageNo, pageSize), Constants.PERSON_SHARE, userId);
		else if (contentType == Constants.FOLDER_BORROW)
			ret = this.getDrListByUserIds(this.getBorrowUserIdsByPage(userId,
					orgIds, pageNo, pageSize), Constants.PERSON_BORROW, userId);
		else if (contentType == Constants.FOLDER_SHAREOUT
				|| contentType == Constants.FOLDER_BORROWOUT) {
			List<DocResource> drs = new ArrayList<DocResource>();
			List<DocResource> fdrs = new ArrayList<DocResource>();
			DocResource dr = this.getDocResourceById(parentId);
			long pId = dr.getParentFrId();
			String lp = this.getDocResourceById(pId).getLogicalPath();

			String hql = "from DocResource as d where logicalPath like :lp " + DocSearchHqlUtils.HQL_FR_TYPE + " and (d.secretLevel <=" +member.getSecretLevel()+ " or d.secretLevel is null)";//成发集团项目 程炯 根据密级进行筛选
			Map<String, Object> nmap = new HashMap<String, Object>();
			nmap.put("lp", lp + ".%");

			ret = docResourceDao.find(hql, -1, -1, nmap);
			if (ret != null && ret.size() > 0) {
				for (DocResource drt : ret) {
					List<DocAcl> ilist = new ArrayList<DocAcl>();
					List<DocAcl> filist = new ArrayList<DocAcl>();

					if (drt.getIsFolder()) {
						ilist = docAclManager.getPersonalShareList(drt.getId());
						filist = docAclManager.getPersonalShareInHeritList(drt.getId());
						FormBizConfigUtils.addAllIgnoreEmpty(ilist, filist);
					} else {
						ilist = docAclManager.getPersonalBorrowList(drt.getId());
					}

					if (drt.getIsFolder() && ilist != null && ilist.size() != 0)
						fdrs.add(drt);
					else if ((!drt.getIsFolder()) && ilist != null
							&& ilist.size() != 0)
						drs.add(drt);

				}
			}
			
			if (contentType == Constants.FOLDER_SHAREOUT) {
				Pagination.setRowCount(fdrs.size());
				ret = this.getPagedDrs(fdrs, pageNo, pageSize);
				return ret;
			} 
			else {
				Pagination.setRowCount(drs.size());
				ret = this.getPagedDrs(drs, pageNo, pageSize);
				return ret;
			}

		}

		else if (contentType == Constants.PERSON_SHARE)
			ret = this.getShareRootDocsByPage(parentId, pageNo, pageSize,
					userId, orgIds);
		else if (contentType == Constants.PERSON_BORROW)
			ret = this.getBorrowDocsByPage(parentId, pageNo, pageSize, userId,
					orgIds);
		else if (contentType == Constants.DEPARTMENT_BORROW) {
			ret = docAclManager.getDeptBorrowDocsPage(this.getAclIdsByOrgIds(
					orgIds, userId), pageNo, pageSize);
		} else if (contentType == Constants.FOLDER_PLAN_DAY
				|| contentType == Constants.FOLDER_PLAN_MONTH
				|| contentType == Constants.FOLDER_PLAN_WEEK
				|| contentType == Constants.FOLDER_PLAN_WORK) {
			int rowcount = planManager.countDraftsmanPlan(userId, Constants
					.getPlanTypeByFrType(contentType));
			Pagination.setRowCount(rowcount);
			List<Plan> plans = planManager.getDraftsmanPlan(userId, Constants.getPlanTypeByFrType(contentType));

			ret = this.getDrsByPlans(plans);
		} else {
			// 我的文档库下显示不依赖 frOrder
			// 根据 类型-时间
			String hql = "from DocResource as d where d.parentFrId=? and "
					+ "d.frType!=" + Constants.FOLDER_PLAN + " and d.frType!="
					+ Constants.FOLDER_TEMPLET + " and d.frType!="
					+ Constants.FOLDER_SHAREOUT + " and d.frType!="
					+ Constants.FOLDER_BORROW + " and d.frType!="
					+ Constants.FOLDER_SHARE + " and (d.secretLevel <=" +member.getSecretLevel()+ " or d.secretLevel is null)";//成发集团项目 程炯 根据文档密级对可见文档进行筛选
			Page page = docResourceDao.pagedQuery(hql + orderStr, pageNo, pageSize, parentId);

			Pagination.setRowCount(page.getTotalCount());
			ret = (List<DocResource>) page.getResult();
			if (ret != null && ret.size() > 0) {
				if (this.docUtils.isOwnerOfLib(userId, ret.get(0).getDocLibId())) {
					for (DocResource tdr : ret) {
						tdr.setIsMyOwn(true);
						if(tdr.getFrType() == Constants.SYSTEM_COL){
							Affair affair = affairManager.getById(tdr.getSourceId());
				            //看一下是否被转储了
				            if(affair == null){
				            	affair = this.hisAffairManager.getById(tdr.getSourceId());
				            }
				            
				            //关联的协同不存在 
				            if(affair == null){
								tdr.setIsRelationAuthority(Boolean.FALSE);
				            }else{
								tdr.setIsRelationAuthority(affair.getIsRelationAuthority());
				            }
						}
					}
				}
			}
		}
		return ret;
	}

	// 根据 Plan 封装 DocResource 对象
	private List<DocResource> getDrsByPlans(List<Plan> plans) {
		User user = CurrentUser.get();
		List<DocResource> ret = new ArrayList<DocResource>();
		long i = 0;
		for (Plan p : plans) {
			DocResource dr = new DocResource();
			dr.setId(i++);
			dr.setSourceId(p.getId());
			dr.setFrName(p.getTitle());
			dr.setFrType(Constants.SYSTEM_PLAN);
			dr.setCreateTime(new Timestamp(p.getStartTime().getTime()));
			dr.setCreateUserId(user.getId());
			dr.setDocLibId(0L);
			dr.setIsFolder(false);
			dr.setSubfolderEnabled(false);
			dr.setFrSize(0);
			dr.setLastUpdate(new Timestamp(p.getStartTime().getTime()));
			dr.setLastUserId(user.getId());
			dr.setMimeTypeId(Constants.SYSTEM_PLAN);
			dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(
					dr.getMimeTypeId()).getOrderNum());
			dr.setHasAttachments(p.isHasAttachments());
			ret.add(dr);

		}
		return ret;
	}

	public List<DocResource> findMyDocs4Rel(Long parentId) throws DocException {
		List<DocResource> ret = null;
		String hql = "from DocResource as d where d.parentFrId=? and "
				+ "d.frType!=" + Constants.FOLDER_PLAN + " and d.frType!="
				+ Constants.FOLDER_TEMPLET + " and d.frType!="
				+ Constants.FOLDER_BORROW + " and d.frType!="
				+ Constants.FOLDER_SHARE
				+ " order by d.frOrder ";//,  d.frType desc, d.lastUpdate desc,  d.createTime desc";
		ret = docResourceDao.find(hql, parentId);

		return ret;
	}

	/**
	 * 通过文档类型和parentId查找文档
	 */
	public List<DocResource> findDocByType(Long parentId, Long type) throws DocException {
		String hql = "from DocResource as d where d.parentFrId =? and d.frType=? order by d.frOrder ";
		return docResourceDao.find(hql, parentId, type);
	}

	/**
	 * 通用文档夹查找
	 */
	public List<DocResource> findFolders(Long parentId, Long contentType,
			Long userId, String orgIds, boolean isPersonalLib) {
		List<DocResource> ret = null;
		// 虚拟的DocResource对象判断
		String aclIds = this.getAclIdsByOrgIds(orgIds, userId);
		if ((contentType == Constants.PERSON_SHARE)
				|| (contentType == Constants.PERSON_BORROW)
				|| (contentType == Constants.DEPARTMENT_BORROW)
				|| (contentType == Constants.FOLDER_SHARE)
				|| (contentType == Constants.FOLDER_BORROW))
			ret = this.findMyFolders(parentId, contentType, userId, orgIds);
		else {
			DocResource dr = docResourceDao.get(parentId);
			if (!isPersonalLib) {
				ret = docAclManager.findNextNodeOfTree(dr, aclIds);
			} else
				ret = this.findMyFolders(parentId, contentType, userId, orgIds);
		}
		return ret;
	}

	public List<DocResource> findFoldersWithOutAcl(Long parentId) {
		List<DocResource> ret = null;
		DocResource dr = docResourceDao.get(parentId);
		ret = docAclManager.findNextNodeOfTreeWithOutAcl(dr);
		return ret;
	}

	/**
	 * 首页查找文档内容
	 */
	public List<DocResource> findAllDocsByPageBySection(Long parentId,
			Long contentType, Integer pageNo, Integer pageSize, Long userId) {
		List<DocResource> ret = null;
		// 虚拟的DocResource对象判断
		// String aclIds = this.getAclIdsByOrgIds(orgIds, userId);
		if ((contentType == Constants.PERSON_SHARE)
				|| (contentType == Constants.PERSON_BORROW)
				|| (contentType == Constants.DEPARTMENT_BORROW)) {
			ret = this.findAllMyDocsByPage(parentId, contentType, pageNo,
					pageSize, userId);
		} else {
			DocResource dr = docResourceDao.get(parentId);
			if (this.isNotPartOfMyLib(userId, dr.getDocLibId())) {
				String aclIds = Constants.getOrgIdsOfUser(userId);
				ret = docAclManager.findNextNodeOfTablePageByDate(dr, aclIds,
						pageNo, pageSize);
			} else
				ret = this.findAllMyDocsByPageByDate(parentId, contentType,
						pageNo, pageSize, userId);
		}

		return ret;
	}

	public List<DocResource> findAllDocsByPage(Long parentId, Long contentType, Long userId,String... type) {
		Integer pageNo = 0;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		pageNo = first / pageSize + 1;
		
		return this.findAllDocsByPage(parentId, contentType, pageNo, pageSize, userId,type);
	}
	
	/**
	 * 通用分页查找所有内容
	 */
	public List<DocResource> findAllDocsByPage(Long parentId, Long contentType,
			Integer pageNo, Integer pageSize, Long userId,String... type) {
		List<DocResource> ret = null;
		// 虚拟的DocResource对象判断
		if ((contentType == Constants.PERSON_SHARE)
				|| (contentType == Constants.PERSON_BORROW)
				|| (contentType == Constants.DEPARTMENT_BORROW)) {
			ret = this.findAllMyDocsByPage(parentId, contentType, pageNo, pageSize, userId);
		} else { 
			DocResource dr = docResourceDao.get(parentId);
			if (this.isNotPartOfMyLib(userId, dr.getDocLibId())) {
				String aclIds = Constants.getOrgIdsOfUser(userId);
				ret = docAclManager.findNextNodeOfTablePage(dr, aclIds, pageNo, pageSize,type);
			} else {
				ret = this.findAllMyDocsByPage(parentId, contentType, pageNo, pageSize, userId);
			}
		}

		return ret;
	}

	public DocResource getDocResourceById(Long docResourceId) {
		return docResourceDao.get(docResourceId);
	}

	public boolean docResourceExist(Long docResourceId) {
		return isDocExsit(docResourceId);
	}

	public boolean deeperThanLimit(DocResource drs) {
		return drs.deeperThanLimit(this.folderLevelLimit);
	}
	
	public boolean docResourceEdit(Long docResourceId) {
		DocResource ret = docResourceDao.get(docResourceId);
		return ret == null ? true : ret.getIsCheckOut();
	}

	public String docResourceNoChange(Long docResourceId, String logicalPath) {
		DocResource ret = docResourceDao.get(docResourceId);
		if (ret == null)
			return "delete";
		else if (!ret.getLogicalPath().equals(logicalPath))
			return "move";
		else
			return "true";
	}

	/**
	 * 根据docResourceId 查找某个文档的从根节点开始的整个文档夹对象链
	 */
	@SuppressWarnings("unchecked")
	public List<DocResource> getFoldersChainById(Long docResourceId) {
		DocResource doc = docResourceDao.get(docResourceId);
		if(doc != null) {
			String logicalPath = doc.getLogicalPath();
			List<Long> docIds = FormBizConfigUtils.parseStr2Ids(logicalPath, ".");
			docIds.remove(docResourceId);
			if(CollectionUtils.isNotEmpty(docIds)) {
				String hql = "from " + DocResource.class.getName() + " as d where d.id in (:ids)";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ids", docIds);
				return this.docResourceDao.find(hql, -1, -1, params);
			}
		}
		return null;
	}

	/** 判断将要被条件搜索的文档夹是否个人文档库所属，即是否需要判断权限  */
	public boolean isNotPartOfMyLib(Long userId, Long libId) {
		if(libId != null && userId != null) {
			DocLib lib = this.docUtils.getDocLibById(libId);
			return lib == null || !lib.isPersonalLib();
		}
		return true;
	}

	/** 右上角查询，当不需要权限过滤时候，取得分页数据 */
	private List<DocResource> getPagedDrs(List<DocResource> src,
			Integer pageNo, Integer pageSize) {
		List<DocResource> ret = new ArrayList<DocResource>();
		int first = Pagination.getFirstResult();
		int end1 = first + pageSize;
		int end2 = src.size();
		int end = 0;
		if (end1 > end2)
			end = end2;
		else
			end = end1;
		for (int i = first; i < end; i++) {
			src.get(i).setIsMyOwn(true);
			ret.add(src.get(i));
		}
		return ret;
	}


	public String getPhysicalPath(String logicalPath, String separator) {
		return getPhysicalPathDetail(logicalPath,separator,true,0); 
	}
	public  String getPhysicalPathDetail(String logicalPath, String separator,boolean needSub1,int beginIndex) {
		if (Strings.isBlank(logicalPath))
			return "";

		StringBuffer sb = new StringBuffer("");

		String ids = logicalPath.replace('.', ',');
		String[] arr = logicalPath.split("\\.");

		List<DocResource> list = this.getDocsByIds(ids.substring(beginIndex));
		if (list == null || list.size() == 0)
			return "";
		
		Map<String, DocResource> map = new HashMap<String, DocResource>();
		for (DocResource td : list) {
			map.put(td.getId().toString(), td);
		}
		int loop=0;
		if(needSub1){
			loop = (arr.length == 1 ? 1 : (arr.length - 1));
		}else{
			loop = arr.length ;
		}
		for (int i = 0; i < loop; i++) {
			DocResource td = map.get(arr[i]);
			if (td == null)
				continue;
			if (i > 0)
				sb.append(separator);
			String key = td.getFrName();
			if (Constants.needI18n(td.getFrType()))
				key = Constants.getDocI18nValue(key);
			sb.append(key);
		}

		return sb.toString();
	}
	
	/**
	 * 判断一个库下是否只存在一个根文档夹
	 */
	public boolean isLibOnlyRoot(Long libId) {
		// 通过判断该库下是否只有一个节点
		List<DocResource> drs = docResourceDao.findBy("docLibId", libId);
		return CollectionUtils.isEmpty(drs) || drs.size() == 1;
	}

	/**
	 * 查询共享所有人列表
	 */
	public Set<Long> getShareUserIds(Long userId, String orgIds) {
		String aclIds = Constants.getOrgIdsOfUser(userId);
		return docAclManager.getShareUserIds(aclIds);
	}

	/**
	 * 查询共享所有人列表（分页）
	 */
	public Set<Long> getShareUserIdsByPage(Long userId, String orgIds,
			Integer pageNo, Integer pageSize) {
		String aclIds = this.getAclIdsByOrgIds(orgIds, userId);
		return docAclManager.getShareUserIdsPage(aclIds, pageNo, pageSize);
	}

	/**
	 * 根据所有人查询共享第一级文档夹
	 */
	public List<DocResource> getShareRootDocs(Long ownerId, Long userId,
			String orgIds) {
		String aclIds = this.getAclIdsByOrgIds(orgIds, userId);
		return docAclManager.getShareRootDocs(aclIds, ownerId);
	}

	/**
	 * 根据所有人查询共享第一级文档夹（分页）
	 */
	public List<DocResource> getShareRootDocsByPage(Long ownerId,
			Integer pageNo, Integer pageSize, Long userId, String orgIds) {
		String aclIds = this.getAclIdsByOrgIds(orgIds, userId);
		return docAclManager.getShareRootDocsPage(aclIds, ownerId, pageNo,
				pageSize);
	}

	/**
	 * 查询借阅所有人id列表
	 */
	public Set<Long> getBorrowUserIds(Long userId, String orgIds) {
		String aclIds = Constants.getOrgIdsOfUser(userId);
		return docAclManager.getBorrowUserIds(aclIds);
	}

	/**
	 * 查询借阅所有人id列表(分页)
	 */
	public Set<Long> getBorrowUserIdsByPage(Long userId, String orgIds,
			Integer pageNo, Integer pageSize) {
		String aclIds = this.getAclIdsByOrgIds(orgIds, userId);
		return docAclManager.getBorrowUserIdsPage(aclIds, pageNo, pageSize);
	}

	/**
	 * 根据所有人查询借阅文档(分页)
	 */
	public List<DocResource> getBorrowDocsByPage(Long ownerId, Integer pageNo,
			Integer pageSize, Long userId, String orgIds) {
		String aclIds = this.getAclIdsByOrgIds(orgIds, userId);
		return docAclManager.getBorrowDocsPage(aclIds, ownerId, pageNo,
				pageSize);
	}

	public List<DocResource> findAllCheckedOutDocsByDays(int days) throws DocException {
		String hql = "from DocResource dr where dr.isCheckOut = 'true' and (dr.checkOutTime <= ?)";
		Timestamp flagtime = new Timestamp(new Date().getTime() - days * 24 * 60 * 60 * 1000);
		return docResourceDao.find(hql, flagtime);
	}

	public List<DocResource> findAllCheckoutDocsByDocLibIdByPage(final long docLibId) {
		String hql = "from DocResource dr where dr.docLibId= ? and dr.isFolder = false and dr.isCheckOut=true ";
		return docResourceDao.find(hql, docLibId);
	}

	public List<DocResource> findDocResourceByHql(String hql, Object... args) {
		return docResourceDao.find(hql, args);
	}

	public List<DocResource> findFirstDocResourceById(long docResId) {
		List<DocResource> list = docResourceDao.findBy("parentFrId", docResId);
		return list;
	}

	/**
	 * 根据id获取名字
	 * 
	 * @return 正常返回 name; 如果该文档不存在，返回 null
	 */
	public String getNameById(Long docResourceId) {
		DocResource dr = docResourceDao.get(docResourceId);
		if (dr == null)
			return null;
		String key = dr.getFrName();
		String name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, key);
		return name;
	}

	/**
	 * 找出某个文档夹下的所有符合类型的数据记录
	 * 
	 * @param types
	 *            类型连接字符串,逗号分割 如 163,165,56
	 */
	public List<DocResource> getDocsInFolderByType(long folderId, String types) {
		String hql = "from DocResource where parentFrId = :fid and frType in (:ids)";
		Map<String, Object> amap = new HashMap<String, Object>();
		amap.put("fid", folderId);
		amap.put("ids", Constants.parseStrings2Longs(types, ","));
		return docResourceDao.find(hql, -1, -1, amap);
	}

	/**
	 * 查询某人共享给当前用户的所有文档 关联人员使用
	 */
	public List<DocTreeVO> getShareDocsByOwnerId(Long ownerId)
			throws DocException {
		List<DocTreeVO> ret = new ArrayList<DocTreeVO>();

		long userId = CurrentUser.get().getId();
		String aclIds = Constants.getOrgIdsOfUser(userId);
		List<DocResource> list = docAclManager
				.getShareRootDocs(aclIds, ownerId);
		if (list != null) {
			for (DocResource dr : list) {
				DocTreeVO vo = new DocTreeVO(dr);
				String srcIcon = docMimeTypeManager.getDocMimeTypeById(
						dr.getMimeTypeId()).getIcon();
				vo.setCloseIcon(srcIcon.substring(0, srcIcon.indexOf('|')));
				vo.setShowName(ResourceBundleUtil.getString(
						Constants.RESOURCE_BASENAME, dr.getFrName()));
				ret.add(vo);
			}
		}

		return ret;
	}

	/** **************项目管理开始******************* */

	/**
	 * 新建项目 1. 生成项目一级文档夹 2. 生成该项目的二级阶段文档夹
	 * 
	 * @return 项目一级文档夹的id 不判断权限，在项目管理处做判断
	 */
	public Long createNewProject(ProjectSummary summary, Long userId) throws DocException {
		// 取得项目文档库的根
		DocLib lib = this.docUtils.getDocLibManager().getProjectDocLib();
		DocResource root = this.getRootByLibId(lib.getId());
		// 转换数据
		ProjectSummaryData appObject = new ProjectSummaryData(summary, orgManager);
		// 1. 新建项目文档夹
		Long newProjectId = this.createCaseFolder(summary.getProjectName(),
				Constants.FOLDER_CASE, summary.getId(), lib.getId(), root.getId(), userId);

		Node appNode = DocHierarchyManagerImpl.getNodesMap().get("project_data");
		if (appNode != null)
			this.saveProjectMetadata(newProjectId, appObject, appNode, true);

		// 3. 新建阶段文档夹
		Set<ProjectPhase> phases = summary.getProjectPhases();
		if (phases != null) {
			for (ProjectPhase p : phases) {
				Long pid = this.createCaseFolder(p.getPhaseName(),
						Constants.FOLDER_CASE_PHASE, p.getId(), lib.getId(),
						newProjectId, userId);
				Node appNode2 = DocHierarchyManagerImpl.getNodesMap().get("project_phase_data");
				if (appNode2 != null)
					this.saveProjectMetadata(pid, p, appNode2, true);
			}
		}
		// 4. 保存权限
		Set<ProjectMember> members = summary.getProjectMembers();
		this.saveProjectAcl(members, userId, newProjectId, summary.getId());

		return newProjectId;
	}

	// 项目文档夹，项目阶段文档夹类型的元数据保存
	// newId: 对应文档夹的id
	private void saveProjectMetadata(Long newId, Object appObject,
			Node appNode, boolean newFlag) throws DocException {
		// 遍历数据库表
		for (Node table = appNode.getFirstChild(); table != null; table = table
				.getNextSibling()) {
			String tableName = table.getNodeName();
			Map drMetadatas = new HashMap();
			Map metadatas = new HashMap();
			if (table.getNodeType() == Node.ELEMENT_NODE) {
				// 遍历字段、属性的对应
				for (Node prop = table.getFirstChild(); prop != null; prop = prop
						.getNextSibling()) {
					if (prop.getNodeType() == Node.ELEMENT_NODE) {
						NamedNodeMap atts = prop.getAttributes();
						String methodName = atts.getNamedItem("method")
								.getNodeValue();
						Method method = null;
						try {
							method = appObject.getClass().getMethod(methodName);
						} catch (SecurityException e) {
							log.error("保存关联项目的元数据", e);
						} catch (NoSuchMethodException e) {
							log.error("保存关联项目的元数据", e);
						}
						Object value = null;
						try {
							value = method.invoke(appObject);
						} catch (IllegalArgumentException e) {
							log.error("保存关联项目的元数据", e);
						} catch (IllegalAccessException e) {
							log.error("保存关联项目的元数据", e);
						} catch (InvocationTargetException e) {
							log.error("保存关联项目的元数据", e);
						}
						String valueClassName = atts.getNamedItem("type")
								.getNodeValue();
						Class valueClass = null;
						if (valueClassName.contains(".")) {
							// 处理对象类型
							try {
								valueClass = Class.forName(valueClassName);
							} catch (ClassNotFoundException e) {
								log.error("", e);
							}
							valueClass.cast(value);
						} else {
							// 处理基本类型
							if (valueClassName.equals("byte"))
								valueClass = byte.class;
							else if (valueClassName.equals("short"))
								valueClass = short.class;
							else if (valueClassName.equals("int"))
								valueClass = int.class;
							else if (valueClassName.equals("long"))
								valueClass = long.class;
							else if (valueClassName.equals("float"))
								valueClass = float.class;
							else if (valueClassName.equals("double"))
								valueClass = double.class;
							else if (valueClassName.equals("char"))
								valueClass = char.class;
							else if (valueClassName.equals("boolean"))
								valueClass = boolean.class;
						}

						if (table.getNodeName().equals("doc_resources")) {
						} else if (table.getNodeName().equals("doc_metadata")) {
							String column = atts.getNamedItem("column")
									.getNodeValue();

							metadatas.put(column, value);
						}
					}
				}
				// 向不同的表保存元数据
				if (tableName.equals("doc_resources")) {
				} else if (tableName.equals("doc_metadata")) {
					Set<String> keys = metadatas.keySet();
					if (newFlag)
						docMetadataManager.addMetadata(newId, metadatas);
					else
						docMetadataManager.updateMetadata(newId, metadatas);
				}
			}// end of 有效表节点
		}
	}

	/** 新建项目文档夹和项目阶段文档夹  */
	private Long createCaseFolder(String name, Long type, Long sourceId, Long docLibId, Long destFolderId, Long userId) {
		int minOrder = this.getMinOrder(destFolderId);

		DocResource dr = new DocResource();
		dr.setFrName(name);
		dr.setParentFrId(destFolderId);
		dr.setAccessCount(0);
		dr.setCommentCount(0);
		dr.setCommentEnabled(false);
		dr.setCreateTime(new Timestamp(new Date().getTime()));
		dr.setCreateUserId(userId);
		dr.setDocLibId(docLibId);
		dr.setIsFolder(true);
		dr.setSourceId(sourceId);
		dr.setSubfolderEnabled(true);
		dr.setFrOrder(minOrder - 1);
		dr.setFrSize(0);
		dr.setFrType(type);
		dr.setLastUpdate(new Timestamp(new Date().getTime()));
		dr.setLastUserId(userId);
		dr.setStatus(Byte.parseByte("2"));
		dr.setStatusDate(new Timestamp(new Date().getTime()));
		dr.setMimeTypeId(type);
		dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());
		dr.setIsCheckOut(false);
		// 系统预定义类型的 mimeTypeId == docTypeId
		dr.setMimeTypeId(type);
		dr.setMimeOrder(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getOrderNum());
		
		return docResourceDao.saveAndGetId(dr);
	}

	/**
	 * 修改项目信息
	 * 
	 * @param summary
	 *            要修改的项目summary
	 * @param addPhases
	 *            要新增的阶段
	 * @param updatePhases
	 *            要修改的阶段
	 * @param delPhaseIds
	 *            要删除的阶段id串，如 1,2,3
	 */
	public void updateProject(ProjectSummary summary,
			Set<ProjectPhase> addPhases, Set<ProjectPhase> updatePhases,
			String[] delPhaseIds, Long userId) throws DocException {
		DocResource projectFolder = this.getProjectFolderByProjectId(summary.getId());
		if (projectFolder == null) {
			return;
		}
		Long projectFolderId = projectFolder.getId();
		// 修改DocResource中保存的项目名称
		this.renameDocWithoutAcl(projectFolderId, summary.getProjectName(), userId);
		Node appNode = DocHierarchyManagerImpl.getNodesMap().get("project_data");
		if (appNode != null)
			this.saveProjectMetadata(projectFolderId, new ProjectSummaryData(summary, orgManager), appNode, false);
		
		//删除权限、订阅只针对修改前的项目人员
		List<Long> oldProjectMemberIds = summary.getOldMemberIds();
		Set<ProjectMember> members = summary.getProjectMembers();

		//原有项目人员不更改权限
		//获取未更改人员 
		List<Long> sameMembers = new ArrayList<Long>();
		for(ProjectMember member : members){
			for(Long id : oldProjectMemberIds){
				if(id.equals(member.getMemberid())){
					sameMembers.add(id);
					break; 
				}  
			} 
		}  
		oldProjectMemberIds.removeAll(sameMembers);
		if(!oldProjectMemberIds.isEmpty()){
			docAclManager.deleteProjectFolderShare(projectFolderId, oldProjectMemberIds);
			docAlertManager.deleteProjectFolderAlert(projectFolder, oldProjectMemberIds);

		}
		Iterator<ProjectMember> it = members.iterator();
		while (it.hasNext()) {
			ProjectMember m = it.next();
			if(sameMembers.contains(m.getMemberid())){
				it.remove();
			}
		}
		// 1.3.2 新增新的权限记录
		if(!members.isEmpty()){
			this.saveProjectAcl(members, userId, projectFolderId, summary.getId());
		}
		// 2. 增加项目阶段
		this.newProjectPhases(addPhases, projectFolder.getDocLibId(), projectFolder.getId(), userId);
		// 3. 修改项目阶段
		this.updateProjectPhases(updatePhases, userId);
		// 4. 删除项目阶段
		this.deleteProjectPhase(delPhaseIds);
	}

	// 修改项目阶段
	private void updateProjectPhases(Set<ProjectPhase> updatePhases,
			Long userId) throws DocException {
		if (updatePhases == null)
			return;
		for (ProjectPhase p : updatePhases) {
			DocResource projectPhase = this.getProjectFolderByProjectId(p.getId(), true);
			if(projectPhase == null) {
				return;
			}

			this.renameDocWithoutAcl(projectPhase.getId(), p.getPhaseName(), userId);
			Node appNode = DocHierarchyManagerImpl.getNodesMap().get("project_phase_data");
			if (appNode != null)
				this.saveProjectMetadata(projectPhase.getId(), p, appNode, false);
		}
	}

	// 删除项目阶段
	@SuppressWarnings("unchecked")
	private void deleteProjectPhase(String[] delPhaseIds) throws DocException {
		if (delPhaseIds == null)
			return;
		for (int i = 0; i < delPhaseIds.length; i++) {
			// 1. 找到该项目阶段的文档夹
			DocResource project = this.getProjectFolderByProjectId(Long.valueOf(delPhaseIds[i]), true);
			if (project == null) {
				return;
			}
			DocResource detail = docMetadataManager.getDocResourceDetail(project.getId());
			// 2. 更改删除标记
			List<DocMetadataObject> metadatas = detail.getMetadataList();
			Map params = new HashMap();
			for (DocMetadataObject m : metadatas) {
				if (m.getPhysicalName().equals(Constants.FOLDER_CASE_PHASE_PHYSICAL_NAME_DELETE))
					m.setMetadataValue(true);
				try {
					params.put(m.getPhysicalName(), Constants.getTrueTypeValue(
							m.getPhysicalName(), String.valueOf(m.getMetadataValue())));
				} catch (ParseException e) {
					log.error("删除项目阶段", e);
				}
			}
			docMetadataManager.updateMetadata(project.getId(), params);
			// 直接删除项目阶段对应的文档夹，不管文档夹中是否有文档；
			removeProjectFolderWithoutAcl(project.getSourceId());
		}
	}

	// 增加项目阶段
	private void newProjectPhases(Set<ProjectPhase> newPhases, Long docLibId, Long destFolderId, Long userId) throws DocException {
		if (newPhases == null)
			return;
		for (ProjectPhase p : newPhases) {
			Long pid = this.createCaseFolder(p.getPhaseName(),
					Constants.FOLDER_CASE_PHASE, p.getId(), docLibId,
					destFolderId, userId);
			Node appNode = DocHierarchyManagerImpl.getNodesMap().get(
					"project_phase_data");
			if (appNode != null)
				this.saveProjectMetadata(pid, p, appNode, true);
		}
	}

	// 保存项目权限
	// 负责人、助理：全部权限
	// 成员、领导：增、看
	// 相关人员：看
	private void saveProjectAcl(Set<ProjectMember> members, Long userId,
			Long projectFolderId, Long projectId) throws DocException {

		int minOrder = docAclManager.getMaxOrder();
		for (ProjectMember m : members) {
			// 默认给项目成员订阅
			Long alertId = docAlertManager.addAlert(projectFolderId, true,
					Constants.ALERT_OPR_TYPE_ADD,
					V3xOrgEntity.ORGENT_TYPE_MEMBER, m.getMemberid(), userId,
					true, false, true);

			// 设置不继承标记
			byte type = m.getMemberType();
			// 项目领导、项目成员
			if (type == ProjectMember.memberType_charge || type == ProjectMember.memberType_member) { 
				docAclManager.setDeptSharePotent(m.getMemberid(),
						V3xOrgEntity.ORGENT_TYPE_MEMBER, projectFolderId,
						Constants.ADDPOTENT, true, alertId,minOrder++);
				docAclManager.setDeptSharePotent(m.getMemberid(),
						V3xOrgEntity.ORGENT_TYPE_MEMBER, projectFolderId,
						Constants.READONLYPOTENT, true, alertId,minOrder++);
			} 
			// 相关人员
			else if (type == ProjectMember.memberType_interfix) { 
				docAclManager.setDeptSharePotent(m.getMemberid(),
						V3xOrgEntity.ORGENT_TYPE_MEMBER, projectFolderId,
						Constants.READONLYPOTENT, true, alertId,minOrder++);
			} 
			// 项目负责人、项目助理
			else if (type == ProjectMember.memberType_manager || type == ProjectMember.memberType_assistant) { 
				docAclManager.setDeptSharePotent(m.getMemberid(),
						V3xOrgEntity.ORGENT_TYPE_MEMBER, projectFolderId,
						Constants.ALLPOTENT, true, alertId,minOrder++);
			}
		}
	}

	/**
	 * 删除项目 关联项目模块做了项目删除标记，文档也要做删除标记
	 */
	@SuppressWarnings("unchecked")
	public void deleteProject(Long summaryId, Long userId) throws DocException {
		// 1. 找到该项目的文档夹
		DocResource project = this.getProjectFolderByProjectId(summaryId);
		if (project == null) {
			return;
		} 
		
		DocResource detail = docMetadataManager.getDocResourceDetail(project.getId());
		// 2. 更改状态为删除标记
		List<DocMetadataObject> metadatas = detail.getMetadataList();
		Map params = new HashMap();
		for (DocMetadataObject m : metadatas) {
			if (m.getPhysicalName().equals(Constants.FOLDER_CASE_PHYSICAL_NAME_STATUS)) {
				m.setMetadataValue(ProjectSummary.state_delete);
			}
			try {
				params.put(m.getPhysicalName(), Constants.getTrueTypeValue(m.getPhysicalName(), String.valueOf(m.getMetadataValue())));
			} catch (ParseException e) {
				log.error("删除关联项目", e);
			}
		}
		docMetadataManager.updateMetadata(project.getId(), params);
	}

	public List<FolderItemDoc> getLatestDocsOfProject(Long projectId, Long phaseId,String orgids, boolean hasAcl) throws DocException {
		boolean isPhase = phaseId != TaskConstants.PROJECT_PHASE_ALL;
		DocResource projectFolder = this.getProjectFolderByProjectId(isPhase ? phaseId : projectId, isPhase);
		if(projectFolder != null) {
			List<DocResource> drs = this.docResourceDao.getDocsOfProjectPhase(projectFolder.getLogicalPath(),orgids,hasAcl,null);
			
			if(CollectionUtils.isNotEmpty(drs)) {
				List<FolderItemDoc> ret = new ArrayList<FolderItemDoc>(drs.size());
				for (DocResource dr : drs) {
					FolderItemDoc doc = new FolderItemDoc(dr);
					DocMimeType mime = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId());
					if (mime != null)
						doc.setIcon("/apps_res/doc/images/docIcon/" + mime.getIcon());
					DocType type = contentTypeManager.getContentTypeById(dr.getFrType());
					
					String stype = "";
					if (type != null)
						stype = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, type.getName());
					doc.setType(stype);
					ret.add(doc);
				}
				return ret;
			}
		}
		 
		return null;
	}

	public List<FolderItemDoc> getLatestDocsOfProjectByCondition(String condition,Long projectId, Long phaseId,Map<String,String> paramMap,String orgids, boolean hasAcl) throws DocException {
		boolean isPhase = phaseId != TaskConstants.PROJECT_PHASE_ALL;
		DocResource projectFolder = this.getProjectFolderByProjectId(isPhase ? phaseId : projectId, isPhase);
		if(projectFolder != null) {
			paramMap.put("condition", condition);
			List<DocResource> drs = this.docResourceDao.getDocsOfProjectPhase(projectFolder.getLogicalPath(),orgids, hasAcl,paramMap);
			
			if(CollectionUtils.isNotEmpty(drs)) {
				List<FolderItemDoc> ret = new ArrayList<FolderItemDoc>(drs.size());
				for (DocResource dr : drs) {
					FolderItemDoc doc = new FolderItemDoc(dr);
					DocMimeType mime = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId());
					if (mime != null)
						doc.setIcon("/apps_res/doc/images/docIcon/" + mime.getIcon());
					DocType type = contentTypeManager.getContentTypeById(dr.getFrType());
					String stype = "";
					if (type != null)
						stype = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, type.getName());
					doc.setType(stype);
					
					ret.add(doc);
				}
				return ret;
			}
		}
		
		return null;
	}
	
	/**
	 * 根据项目id得到项目文档夹
	 */
	@SuppressWarnings("unchecked")
	public DocResource getProjectFolderByProjectId(long projectId, boolean isPhase) {
		String hql1 = "from DocResource where sourceId = ? and frType = ? and isFolder=true";
		List<DocResource> list1 = this.docResourceDao.find(hql1, projectId, isPhase ? Constants.FOLDER_CASE_PHASE : Constants.FOLDER_CASE);
		if (list1 == null || list1.size() == 0)
			return null;
		
		return list1.get(0);
	}
	
	public DocResource getProjectFolderByProjectId(long projectid) {
		return this.getProjectFolderByProjectId(projectid, false);
	}

	/**
	 * 判断一个项目或项目阶段文档夹下是否有文档（不算文档夹，不论层级）
	 */
	public boolean hasDocsInProject(long sourceId) {
		boolean ret = false;

		DocResource dr = this.getDocResBySourceId(sourceId);
		if (dr == null)
			return ret;

		final List<Type> parameterTypes = new ArrayList<Type>();
		final List<Object> parameterValues = new ArrayList<Object>();

		parameterTypes.add(Hibernate.STRING);
		parameterValues.add(dr.getLogicalPath() + ".%");

		String hql = "from DocResource where isFolder = false and logicalPath like ?";
		int total = this.docResourceDao.getQueryCount(hql, parameterValues
				.toArray(new Object[parameterValues.size()]), parameterTypes
				.toArray(new Type[parameterTypes.size()]));
		if (total > 0)
			return true;

		return ret;
	}

	/**
	 * 判断项目或项目阶段文档夹下是否有文档（不算文档夹，不论层级）,sourceIds ","分割项目或者项目阶段sourceID
	 */
	public boolean hasDocsInProjects(String sourceIds) {
		boolean hasDoc = false;
		String[] sids = sourceIds.split(",");
		for (String sid : sids) {
			if (hasDocsInProject(Long.parseLong(sid))) {
				hasDoc = true;
				break;
			}
		}
		return hasDoc;
	}

	/**
	 * 删除项目文档夹项目阶段文档夹
	 */
	public void removeProjectFolderWithoutAcl(long sourceId) throws DocException {
		DocResource dr = this.getDocResBySourceId(sourceId);
		if (dr == null)
			return;
		this.removeDocWithoutAcl(dr, CurrentUser.get().getId(), true);
	}

	/** **************项目管理结束******************* */

	/**
	 * 全文检索
	 */
	public IndexInfo getIndexInfo(long id) throws DocException {
		return this.getIndexInfo(id, false);
	}
	
	public IndexInfo getIndexInfo(DocResource dr) throws DocException {
		return this.getIndexInfo(dr, false);
	}
	/**
	 * 全文检索文档夹权限更新,只更新权限true,默认为false
	 */
	public IndexInfo getIndexInfo(long id,boolean isUpdateAuthOnly) throws DocException {
		DocResource dr = docResourceDao.get(id);
		return this.getIndexInfo(dr, isUpdateAuthOnly);
	}
	@SuppressWarnings("unchecked")
	private IndexInfo getIndexInfo(DocResource dr,boolean isUpateAuthOnly) throws DocException {
		if (dr == null)
			return null;
		Long id = dr.getId();
		IndexInfo info = new IndexInfo();
		info.setEntityID(id);
		//按照现在的情况，已经没有单独的权限库了，故去掉 --方剑
	    /*if(isUpateAuthOnly){
        	info.setUpdateAuthOnly(isUpateAuthOnly);
        	getIndexAuth(dr, info);
        	return info;
        }*/
		Long createUserId = dr.getCreateUserId();
		
		// some properties added to index info by Rookie Young at 2011-02-17, the fifth day of Spring Festival...
		info.setStartMemberId(createUserId);
		info.setHasAttachment(dr.getHasAttachments());
		
		
		long type = dr.getFrType();
		boolean isPig = Constants.isPigeonhole(type);
		StringBuilder keyword=new StringBuilder();
		IndexExtPropertiesConfig idexExt = (IndexExtPropertiesConfig)ApplicationContextHolder.getBean("extPropertiesConfig");
		String[] ie = idexExt.getField("doc");
		String value = null;
		DocBody body = null;
		int fieldIndexType = IndexInfo.FieldIndex_Type.IndexNo.ordinal();
		for (String fieldName : ie) {
			value=null;
			if("docLibId".equals(fieldName)) {
				value = String.valueOf(dr.getDocLibId());
				fieldIndexType = IndexInfo.FieldIndex_Type.IndexNo.ordinal();
			}
			else if("folderId".equals(fieldName)) {
				value = String.valueOf(dr.getIsFolder() ? dr.getId() : dr.getParentFrId());
				fieldIndexType = IndexInfo.FieldIndex_Type.IndexNo.ordinal();
			}
			else if("changeUserName".equals(fieldName)) {
				try {
					V3xOrgMember updateUser = this.orgManager.getMemberById(dr.getLastUserId());
					if(updateUser != null) {
						value = updateUser.getName();
						keyword.append(value+" ");
					}
					fieldIndexType = IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal();
				} catch (BusinessException e) {
					log.error("获取[id=" + dr.getLastUserId() + "]人员时出现异常：", e);
				}
			}
			else if("changeTime".equals(fieldName)) {
				value = dr.getLastUpdate().toString();
				fieldIndexType = IndexInfo.FieldIndex_Type.IndexNo.ordinal();
			}
			else if("fileSize".equals(fieldName)) {
				if(isPig){continue;}
				value=Strings.formatFileSize(dr.getFrSize(), true);
				fieldIndexType = IndexInfo.FieldIndex_Type.IndexNo.ordinal();
			}
			else if("docPath".equals(fieldName)) {
				StringBuilder pathName = new StringBuilder();
				String[] drIds = StringUtils.split(dr.getLogicalPath(), '.');
				int y=0;
				for(String drId : drIds) {
					if(y==drIds.length-1)
					{
						continue;
					}
					Long folderId = NumberUtils.toLong(drId);
					DocResource folder = getDocResourceById(folderId);
					if(folder==null){continue;}
					String folderName = Constants.getDocI18nValue(folder.getFrName());
					pathName.append(folderName+">");
					y++;
				}
				value = StringUtils.removeEnd(pathName.toString(), ">");
				fieldIndexType = IndexInfo.FieldIndex_Type.IndexNo.ordinal();
			}
			else if("docType".equals(fieldName)) {
				value = String.valueOf(dr.getMimeTypeId());
				fieldIndexType = IndexInfo.FieldIndex_Type.IndexNo.ordinal();
			}
			else if("desc".equals(fieldName)) {
				value = dr.getFrDesc();
				fieldIndexType = IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal();
				keyword.append(StringUtils.isBlank(value)?"":value+" ");
			}
			else if("docCustomInfo".equals(fieldName)) {
				Map metadatas = this.docMetadataManager.getDocMetadataMap(dr.getId());
				Set<String> keys = metadatas.keySet();
				StringBuilder sb = new StringBuilder();
				for(String key : keys) {
					if(key.startsWith("avarchar") || key.startsWith("text")) {
						sb.append(metadatas.get(key));
					}
				}
				value = sb.toString();
				fieldIndexType = IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal();
				keyword.append(StringUtils.isBlank(value)?"":value+" ");
			}
			else if("fileId".equals(fieldName)) {
				if(isPig){continue;}
					Long sourceId=dr.getSourceId();
					if(sourceId!=null)
					{
						value=sourceId.longValue()+"";
					}else{
						body = docBodyDao.get(id);
						if(body!=null){
							value="zip"+body.getDocResourceId()+"";
						}
					}
					fieldIndexType = IndexInfo.FieldIndex_Type.IndexNo.ordinal();
			}
			info.addExtendProperties(fieldName, value, fieldIndexType);
			
			if(log.isDebugEnabled()) {
				log.debug("入库信息[属性名称:" + fieldName + ", 属性值:" + value + "]");
			}
		}
		
		V3xOrgMember member;
		try {
			member = orgManager.getMemberById(createUserId);
			if (member != null) {
				info.setAuthor(member.getName());
			}
		} catch (BusinessException e) {
			log.error("取得全文检索IndexInfo时候从orgManager取得member", e);
		}

		String name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, dr.getFrName());
		info.setTitle(name);
		info.setCreateDate(dr.getCreateTime());

		// 2008.01.07 全文检索加入归档数据
		if (isPig) {
			try {
				IndexInfo pigInfo = null;
				if (type == Constants.SYSTEM_ARCHIVES) {
					EdocManager edocManager = (EdocManager)ApplicationContextHolder.getBean("edocManager");
					pigInfo = ((IndexEnable)edocManager).getIndexInfo(dr.getSourceId());
				} else if (type == Constants.SYSTEM_COL) {
					Affair affair = affairManager.getById(dr.getSourceId());
					if (affair != null) {
						long summaryId = affair.getObjectId();
						ColManagerImpl cmi = (ColManagerImpl) colManager;
						pigInfo = cmi.getIndexInfo(summaryId);
					}
				} else if (type == Constants.SYSTEM_INQUIRY) {
					InquiryManagerImpl imi = (InquiryManagerImpl) inquiryManager;
					pigInfo = imi.getIndexInfo(dr.getSourceId());
				} else if (type == Constants.SYSTEM_MEETING) {
					pigInfo = mtMeetingManagerCAP.getIndexInfo(dr.getSourceId());
				} else if (type == Constants.SYSTEM_NEWS) {
					NewsDataManagerImpl ndmi = (NewsDataManagerImpl) newsDataManager;
					pigInfo = ndmi.getIndexInfo(dr.getSourceId());
				} else if (type == Constants.SYSTEM_PLAN) {

				} else if (type == Constants.SYSTEM_MAIL) {

				} else if (type == Constants.SYSTEM_BULLETIN) {
					BulDataManagerImpl bdmi = (BulDataManagerImpl) bulDataManager;
					pigInfo = bdmi.getIndexInfo(dr.getSourceId());
				}

				if (pigInfo != null) {
					if (pigInfo.getOpinion() != null)
						info.setOpinion(pigInfo.getOpinion());
					if (pigInfo.getComment() != null)
						info.setComment(pigInfo.getComment());
					if (pigInfo.getContentType() != null)
						info.setContentType(pigInfo.getContentType());
					info.setContentID(pigInfo.getContentID());
					if (pigInfo.getContentCreateDate() != null)
						info.setContentCreateDate(pigInfo.getContentCreateDate());
					if (pigInfo.getContent() != null)
						info.setContent(pigInfo.getContent());
					if (pigInfo.getAccessories() != null)
						info.setAccessories(pigInfo.getAccessories());
				}
			} catch (Exception e) {
				log.error("全文检索调用归档方的接口：", e);
			}
		} else {
			List<DocForum> opinions = docForumManager.findFirstForumsByDocId(id);
			List<DocForum> comments = docForumManager.findReplyByDocId(id);
			StringBuffer commentStr = null;
			StringBuffer opinionStr = null;
			if (opinions != null && opinions.size() > 0) {
				opinionStr = new StringBuffer();
				for (DocForum f : opinions) {
					opinionStr.append(f.getBody());
				}
				info.setOpinion(opinionStr.toString());
			}
			if (comments != null && comments.size() > 0) {
				commentStr = new StringBuffer();
				for (DocForum f : comments) {
					commentStr.append(f.getBody());
				}
				info.setComment(commentStr.toString());
			}
			long newOrUploadformatType = docMimeTypeManager.getDocMimeTypeById(
					dr.getMimeTypeId()).getFormatType();
			long formatType = dr.getMimeTypeId();
			File file1 = null;
			if (dr.getSourceId() != null) {
				info.setContentID(dr.getSourceId());
				try {
					file1 = fileManager.getFile(dr.getSourceId());
				} catch (BusinessException e1) {
					log.error(e1);
				}
			}
			info.setContentCreateDate(dr.getCreateTime());
			info.setContentType(0);
			if (newOrUploadformatType == Constants.FORMAT_TYPE_DOC_FILE) {
				if(dr.getSourceId() != null){
					if (formatType == Constants.FORMAT_TYPE_ID_UPLOAD_DOC) {
						info.setContent(Convertor.contentParse(IndexInfo.CONTENTTYPE_WORD, file1));
					} else if (formatType == Constants.FORMAT_TYPE_ID_UPLOAD_XLS||formatType == Constants.FORMAT_TYPE_ID_HTML||formatType ==Constants.FORMAT_TYPE_ID_HTM) {
						try {
							V3XFile file=fileManager.getV3XFile(dr.getSourceId());
							String content =Convertor.contentParse(
									Convertor.getContentType(file.getMimeType(),
											file.getFilename().substring(file.getFilename().lastIndexOf(".")+1)), file1);
								info.setContent(content);
							
						} catch (Exception e) {
							log.error("",e);
						}
					}else {
						try {
							V3XFile file = fileManager.getV3XFile(dr.getSourceId());
							String contentPath = this.fileManager.getFolder(file.getCreateDate(), false);
							info.setContentPath(contentPath.substring(contentPath.length() - 11) + System.getProperty("file.separator"));
							info.setContentType(Convertor.getContentType(file.getMimeType(), file.getFilename().split("\\.")[1]));
							info.setContentID(dr.getSourceId());
							info.setContentCreateDate(file.getCreateDate());
							Partition partition = partitionManager.getPartition(file.getCreateDate(), true);
							info.setContentAreaId(partition.getId().toString());
						} catch (Exception e) {
							log.error("取得全文检索IndexInfo(sourceId:"+dr.getSourceId()+")时候从fileManager取得V3xFile"+ e);
						}
					}
				}
			} else {
				if(body==null){body=docBodyDao.get(id);}
				if(body!=null)
				{
					Date date = body.getCreateDate();
					if (formatType == Constants.FORMAT_TYPE_DOC_A6) {
						info.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
						info.setContent(body.getContent());
					} else if (formatType == Constants.FORMAT_TYPE_DOC_WORD) {
						info.setContentType(IndexInfo.CONTENTTYPE_WORD);
						Long fileId = Long.parseLong(body.getContent());
						Partition partition = partitionManager.getPartition(body.getCreateDate(), true);
						info.setContentAreaId(partition.getId().toString());
						String contentPath = "";
						try {
							contentPath = this.fileManager.getFolder(body.getCreateDate(), false);
						} catch (BusinessException e) {
							log.error(e);
						}
						info.setContentPath(contentPath.substring(contentPath.length() - 11) + System.getProperty("file.separator"));
						info.setContentID(fileId);
						info.setContentCreateDate(date);
					} else if (formatType == Constants.FORMAT_TYPE_DOC_EXCEL) {
						info.setContentType(IndexInfo.CONTENTTYPE_XLS);
						Long fileId = Long.parseLong(body.getContent());
						Partition partition = partitionManager.getPartition(body.getCreateDate(), true);
						info.setContentAreaId(partition.getId().toString());
						String contentPath = "";
						try {
							contentPath = this.fileManager.getFolder(body.getCreateDate(), false);
						} catch (BusinessException e) {
							log.error(e);
						}
						info.setContentPath(contentPath.substring(contentPath.length() - 11) + System.getProperty("file.separator"));
						info.setContentID(fileId);
						info.setContentCreateDate(date);
					} else {
						try {
							Partition partition = partitionManager.getPartition(body.getCreateDate(), true);
							info.setContentAreaId(partition.getId().toString());
							String contentPath = this.fileManager.getFolder(body.getCreateDate(), false);
							info.setContentPath(contentPath.substring(contentPath.length() - 11) + System.getProperty("file.separator"));
							if (formatType == Constants.FORMAT_TYPE_DOC_WORD_WPS) {
								info.setContentType(IndexInfo.CONTENTTYPE_WPS_Word);
							} else {
								info.setContentType(IndexInfo.CONTENTTYPE_WPS_EXCEL);
							}
							Long fileId = Long.parseLong(body.getContent());
							info.setContentID(fileId);
							info.setContentCreateDate(date);
						} catch (Exception e) {
							log.error("取得全文检索IndexInfo时候从fileManager取得V3xFile"+e);
						}
					}
				}
			}
			IndexUtil.convertToAccessory(info);
		}
		info.setKeyword(keyword.append(StringUtils.isBlank(dr.getKeyWords())?"":" "+dr.getKeyWords()).toString());
		info.setAppType(ApplicationCategoryEnum.doc);
		getIndexAuth(dr, info);

		return info;
	}
	
	private void getIndexAuth(DocResource dr, IndexInfo info) {
		// 权限
		AuthorizationInfo ai = new AuthorizationInfo();
		Map<Long, String> acls = docAclManager.getSpecialAclsByDocResourceId(dr, Constants.aclLevels4Index);
		// 如果acls中有库管理员，则将其从acls中删除，将库管理员放入到role角色中去
		List<Long> dlos = this.docUtils.getOwnersByDocLibId(dr.getDocLibId());
		if (dlos != null) {
			for (Long m : dlos) {
				if (acls.get(m) != null && V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(acls.get(m))) {
					acls.remove(m);
				}
			}
		}
		List<Long> owners = new ArrayList<Long>();
		List<Long> depts = new ArrayList<Long>();
		List<Long> posts = new ArrayList<Long>();
		List<Long> accounts = new ArrayList<Long>();
		List<String> roles = new ArrayList<String>();
		Set<Long> keySet = acls.keySet();
		for (Long k : keySet) {
			if (V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(acls.get(k)))
				owners.add(k);
			else if (V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(acls.get(k)))
				depts.add(k);
			else if (V3xOrgEntity.ORGENT_TYPE_POST.equals(acls.get(k)))
				posts.add(k);
			else if (V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(acls.get(k)))
				accounts.add(k);
		}
		roles.add(Constant.DOC_LIB + "|" + dr.getDocLibId());

		ai.setOwner(owners);
		ai.setDepartment(depts);
		ai.setPost(posts);
		ai.setAccount(accounts);
		ai.setRole(roles);
		info.setAuthorizationInfo(ai);
	}

	public DocLearningManager getDocLearningManager() {
		return docLearningManager;
	}

	public void setDocLearningManager(DocLearningManager docLearningManager) {
		this.docLearningManager = docLearningManager;
	}

	/**
	 * 获取带有单位简称的实体名
	 */
	public String getEntityNameWithAccountShort(String orgType, Long orgId) {
		return Constants.getOrgEntityName(orgType, orgId, true);
	}

	public DocResource getDocResBySourceId(long sourceId) {
		String hql = "from DocResource where sourceId = ?";
		return (DocResource)this.docResourceDao.findUnique(hql, null, sourceId);
	}

	/**
	 * 判断内容类型
	 */
	public boolean contentTypeExist(long typeId) {
		return this.contentTypeManager.getContentTypeById(typeId) != null;
	}

	/**
	 * Map<tempFolder, temp> Map<docResId, zipfile>
	 */
	public static Map<String, File> downloadMap = new HashMap<String, File>();

	public static final String DOWNLOAD_TEMP_FOLDER_KEY = "tempFolder";

	public boolean docHistoryDownloadCompress(long docVersionId) {
		return this.downloadCompress(docVersionId, true);
	}
	
	/**
	 * 将复合文档打包供用户下载
	 * @param resId		资源ID：文档ID 或 历史版本信息ID
	 * @param history	是否为历史版本文件
	 */
	private boolean downloadCompress(long resId, boolean history) {
		DocResource dr = null;
		DocBody docBody = null;
		if(!history) {
			dr = this.getDocResourceById(resId);
			docBody = this.getBody(resId);
		}
		else {
			DocVersionInfo dvi = this.docVersionInfoManager.getDocVersion(resId);
			dr = dvi.getDocResourceFromXml();
			docBody = dvi.getDocBodyFromXml();
		}
		
		String bodyType = "HTML";
		if (docBody != null)
			bodyType = docBody.getBodyType();

		String sSrcName = dr.getFrName();
		sSrcName = Constants.dealUnChar(sSrcName);
		String srcName = sSrcName;

		String filename = srcName + ".html";

		String inner = this.getInnerOfDocResource(dr, docBody);

		List<File> files = new ArrayList<File>();

		String sysTemp = SystemEnvironment.getSystemTempFolder();
		String docTemp = sysTemp + "/doctemp/";

		File temp = new File(docTemp);
		temp.mkdir();
		if (temp == null)
			log.info("文档下载时创建临时文档夹失败。");

		downloadMap.put(DOWNLOAD_TEMP_FOLDER_KEY, temp);

		// 保存正文中的图片的名字 Map<fileUrl, fileName>
		Map<String, String> imgName = new HashMap<String, String>();

		/** ********************** 附件下载开始 ********************************* */

		List<Attachment> atts = attachmentManager.getByReference(resId);
		for (Attachment att : atts) {
			if (att.getFileUrl() == null)
				continue;

			imgName.put(att.getFileUrl() + "", att.getFilename());

			OutputStream tout = null;
			InputStream tin = null;
			try {
				tout = new FileOutputStream(docTemp + att.getFilename());
				tin = fileManager.getFileInputStream(att.getFileUrl());
				if (tin == null) {
					if (tout != null)
						tout.close();
					continue;
				}
				CoderFactory.getInstance().download(tin, tout);
			} catch (FileNotFoundException e1) {
				log.error("复合文档下载", e1);
			} catch (BusinessException e1) {
				log.error("复合文档下载", e1);
			} catch (IOException e1) {
				log.error("复合文档下载", e1);
			} catch (Exception e1) {
				log.error("复合文档下载", e1);
			}
			try {
				if (tin != null)
					tin.close();
			} catch (Exception e) {
				log.error("复合文档下载", e);
			} finally {
				try {
					if (tout != null)
						tout.close();
				} catch (Exception e) {
					log.error("复合文档下载", e);
				}
			}
			files.add(new File(docTemp + att.getFilename()));
		}
		/** ********************** 附件下载结束 ********************************* */

		try {
			int first = inner.indexOf("<img border=");
			while (first != -1) {
				int onload = inner.indexOf("onload", first);
				if (onload != -1) {
					inner = inner.replace(inner.substring(onload, onload + 27),
							"");
				}
				int src = inner.indexOf("src", first);
				int srcend = inner.indexOf("\"", src + 6);
				int fileId = inner.indexOf("fileId", src);
				int fileIdEnd = inner.indexOf("&amp", fileId);
				String strSrc = inner.substring(src + 5, srcend);
				String theName = imgName.get(inner.substring(fileId + 7,
						fileIdEnd));
				inner = inner.replace(strSrc, theName);
				int end = inner.indexOf(">", src);
				first = inner.indexOf("<img border=", end);
			}
		} catch (Exception e) {
			log.error("复合文档下载", e);
		}

		/**
		 * *********************** word, excel格式文件的下载开始
		 * ***************************
		 */
		if (!"HTML".equals(bodyType)) {
			Long fileId = Long.valueOf(docBody.getContent());
			File bodyFile = null;

			String bodyFileName = srcName;
			try {
				bodyFile = fileManager.getStandardOffice(fileId, dr
						.getCreateTime());
			} catch (BusinessException e1) {
				log.error("复合文档下载", e1);
			}
			if (bodyFile != null) {
				if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD
						.equals(bodyType)) {
					// FileManager的标准转换
					bodyFileName += ".doc";
				} else if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL
						.equals(bodyType)) {
					bodyFileName += ".xls";
				} else if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD
						.equals(bodyType)) {
					bodyFileName += ".wps";
				} else if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL
						.equals(bodyType)) {
					bodyFileName += ".et";
				}
				OutputStream bodyout = null;
				InputStream bodyin = null;
				try {
					bodyout = new FileOutputStream(docTemp + bodyFileName);
					bodyin = new FileInputStream(bodyFile);
					IOUtils.copy(bodyin, bodyout);
					bodyout.flush();
				} catch (FileNotFoundException e1) {
					log.error("复合文档下载", e1);
				} catch (IOException e1) {
					log.error("复合文档下载", e1);
				}
				try {
					if (bodyin != null)
						bodyin.close();
				} catch (Exception e) {
					log.error("复合文档下载", e);
				} finally {
					try {
						if (bodyout != null)
							bodyout.close();
					} catch (Exception e) {
						log.error("复合文档下载", e);
					}
				}
				files.add(new File(docTemp + bodyFileName));
			}

		}

		/**
		 * *********************** word, excel格式文件的下载结束
		 * ***************************
		 */

		/** ********************** 正文下载开始 ********************************* */
		PrintWriter out1 = null;
		try {
			out1 = new PrintWriter(docTemp + filename);
			IOUtils.write(inner, out1);
		} catch (FileNotFoundException e1) {
			log.error("复合文档下载", e1);
		} catch (IOException e1) {
			log.error("复合文档下载", e1);
		}
		out1.flush();
		try {
			if (out1 != null)
				out1.close();
		} catch (Exception e) {
			log.error("复合文档下载", e);
		}
		files.add(new File(docTemp + filename));
		/** ********************** 正文下载结束 ********************************* */
		File zipFile = null;
		try {
			zipFile = CompressUtil.zip(docTemp + srcName, files);
			downloadMap.put(resId + "", zipFile);
		} catch (Exception e) {
			log.error("复合文档下载压缩", e);
		}

		/** **************** 删除临时文件开始 *********************** */
		for (File f : files) {
			try {
				f.delete();
			} catch (Exception e) {
			}
		}

		/** **************** 删除临时文件结束 *********************** */

		return (zipFile != null);
	}
	
	public boolean docDownloadCompress(long docResourceId) {
		return this.downloadCompress(docResourceId, false);
	}

	private String getInnerOfDocResource(DocResource dr, DocBody body) {
		String inner = "<html><head><title>";
		inner += dr.getFrName();
		inner += "</title></head><body><table width='100%' cellpadding='0' cellspacing='0'><tr><td style='padding: 10px 20px;'><table width='100%' cellpadding='0' cellspacing='0' style='border: solid 1px #999999; '><tr><td height='30'><div id=\"propDiv\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" height=\"5%\" align=\"center\">";
		inner += "<tr><td height=\"10\" style=\"repeat-x;background-color: f6f6f6;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"center\">";

		inner += "<tr><td width=\"90\" height=\"28\" nowrap style=\"text-align: right;	padding-right: 5px;	font-size: 12px;height: 24px; padding-top: 10px;\">";
		inner += ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME,
				"doc.jsp.open.body.name")
				+ ": ";
		inner += "</td>	<td style=\"padding-top: 10px; font-size: 12px;\">";
		inner += dr.getFrName();
		inner += "</td></tr>";

		inner += "<tr><td width=\"90\" height=\"28\" nowrap style=\"text-align: right;	padding-right: 5px;	font-size: 12px;height: 24px; padding-top: 10px;\">";
		inner += ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME,
				"doc.metadata.def.creater")
				+ ": ";
		inner += "</td>	<td style=\"padding-top: 10px; font-size: 12px;\">";
		inner += Constants.getOrgEntityName("Member", dr.getCreateUserId(),
				false)
				+ " ("
				+ Datetimes.formateToLocaleDatetime(dr.getCreateTime())
				+ ")";
		inner += "</td></tr>";

		inner += "</table></td></tr><tr><td height=\"5\" style=\" repeat-x;	background-color: #f6f6f6;\"></td></tr></table></div></td> </tr> <tr> <td valign='top'> ";
		inner += "<div id=\"bodyDiv\"><table width=\"100%\" border=\"0\" height=\"10%\" cellspacing=\"0\" cellpadding=\"0\" style=\"\"><tr>";
		inner += "<td style=\" repeat-y;	width: 20px;\"><img src=\"\" height=\"1\" width=\"20px\"></td> <td align=\"center\" style='padding: 20px 0px;'><div style='text-align: left;' id=\"\">";

		String trueBody = "";
		if ("HTML".equals(body.getBodyType())
				&& Strings.isNotBlank(body.getContent()))
			trueBody = body.getContent();
		inner += trueBody;
		inner += "</div></td> <td style=\" repeat-y;width: 20px;\"><img src=\"\" height=\"1\" width=\"20px\"></td></tr></table></div></td> </tr> </table>";
		inner += "</td></tr></table></body></html>";

		return inner;
	}

	public List<DocResource> iSearch(ConditionModel cModel, DocType docType) {
		Long docLibId = cModel.getDocLibId();
		long userId = cModel.getUser().getId();
		
		List<DocResource> listDocs = docResourceDao.iSearch(cModel, docType);
		DocLib lib = this.docUtils.getDocLibById(docLibId);
		if (lib.isPersonalLib()) {
			return FormBizConfigUtils.pagenate(listDocs);
		} 
		else {
			DocResource parent = this.getRootByLibId(docLibId);
			if(parent == null)
				return null;
			
			String aclIds = Constants.getOrgIdsOfUser(userId);
			Integer pageNo = 0;
			Integer first = Pagination.getFirstResult();
			Integer pageSize = Pagination.getMaxResults();
			pageNo = first / pageSize + 1;
			List<DocResource> list = docAclManager.getResourcesByConditionAndPotentPage(listDocs, parent, aclIds, pageNo, pageSize);
			return list;
		}
	}

	/**
	 * 综合查询(归档类)
	 */
	public List<DocResource> iSearchPiged(ConditionModel cModel, DocType docType) {
		Long docLibId = cModel.getDocLibId();
		long userId = CurrentUser.get().getId();
		DocLib lib = this.docUtils.getDocLibById(docLibId);
		List<DocResource> list = new ArrayList<DocResource>();

		Map<Long, String> idPathMap = new HashMap<Long, String>();

		String sql = this.getQueryString4ISearchPiged(cModel, docType);
		Session session = docResourceDao.getDocSession();
		try {
			Connection conn = session.connection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				idPathMap.put(rs.getLong(1), rs.getString(2));
			}
			rs.close();
		} catch (SQLException e) {
			log.error("综合查询使用jdbc：", e);
		} finally {
			if (session != null) {
				docResourceDao.releaseDocSession(session);
			}
		}

		if (idPathMap.size() == 0)
			return list;

		Integer pageNo = 0;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		pageNo = first / pageSize + 1;

		if (lib.getType() == Constants.PERSONAL_LIB_TYPE.byteValue()) {
			List<Long> idList = new ArrayList<Long>(idPathMap.keySet());
			idList = FormBizConfigUtils.pagenate(idList);
			list = docResourceDao.getDocsByIds(idList);
		} else {
			String aclIds = Constants.getOrgIdsOfUser(userId);
			list = docAclManager.getResourcesByConditionAndPotentPageNoDr(idPathMap, aclIds, pageNo, pageSize);
		}

		return list;
	}

	private String getQueryString4ISearchPiged(ConditionModel cModel,
			DocType docType) {
		String dbtype = Constants.getDBType();
		if ("mysql".equals(dbtype) || "sqlserver".equals(dbtype) || "oracle".equals(dbtype)) {
			boolean isOracle = "oracle".equals(dbtype);
			StringBuffer sb = new StringBuffer("select d.id, d.logical_path from doc_resources d, doc_metadata m ");
			boolean isColPiged = String.valueOf(ApplicationCategoryEnum.collaboration.key()).equals(cModel.getAppKey());
			// 协同需关联Affair表
			if (isColPiged) { 
				sb.append(", v3x_affair a");
			}
			sb.append(" where d.id = m.doc_resource_id and d.doc_lib_id = ").append(cModel.getDocLibId());
			sb.append(" and d.fr_type = ").append(docType.getId());
			if (Strings.isNotBlank(cModel.getTitle())) {
				sb.append(" and d.fr_name like '%").append(cModel.getTitle()).append("%'");
			}
			if (Strings.isNotBlank(cModel.getKeywords())) {
				sb.append(" and d.key_words like '%").append(cModel.getKeywords()).append("%'");
			}
			if (isColPiged) {
				sb.append(" and d.source_id = a.id and a.app=1 and a.state in(");
				if (cModel.getFromUserId() != null && cModel.getToUserId() == null) {
					sb.append(StateEnum.col_sent.key());
				} else {
					sb.append(StateEnum.col_pending.key()).append(",").append(StateEnum.col_done.key());
				}
				sb.append(") and a.is_delete!=1 and a.member_id=").append(CurrentUser.get().getId());
				if (cModel.getFromUserId() != null && cModel.getToUserId() != null) {
					sb.append(" and m.reference1 = ").append(cModel.getFromUserId());
				}
			} else {
				sb.append(" and m.reference1 = ").append(CurrentUser.get().getId());
			}
			if (cModel.getBeginDate() != null) {
				if(isOracle)
					sb.append(" and m.date1 >= to_date('").append(Datetimes.formatDate(cModel.getEndDate())).append("', 'yyyy-mm-dd')");
				else	
					sb.append(" and m.date1 >= '").append(Datetimes.formatDate(cModel.getBeginDate())).append("'");
			}
			if (cModel.getEndDate() != null) {
				if(isOracle)
					sb.append(" and m.date1 <= to_date('").append(Datetimes.formatDate(cModel.getBeginDate())).append("', 'yyyy-mm-dd')");
				else
					sb.append(" and m.date1 <= '").append(Datetimes.formatDate(cModel.getEndDate())).append("'");
			}

			return sb.toString();
		} else {
			return null;
		}
	}

	/**
	 * 根据id串得到多个docResource
	 */
	@SuppressWarnings("unchecked")
	public List<DocResource> getDocsByIds(String ids) {
		if (Strings.isBlank(ids))
			return new ArrayList<DocResource>();

		String hql = "from DocResource where id in(:ids)";
		Map<String, Object> amap = new HashMap<String, Object>();
		amap.put("ids", Constants.parseStrings2Longs(ids, ","));
		return this.docResourceDao.find(hql, -1, -1, amap);
	}

	/**
	 * 判断一个人是否正在查看别人（个人）借阅给自己的文档
	 */
	public boolean isViewPerlBorrowDoc(long memberId, long docResId) {
		List<DocAcl> list = docAclManager.getAclList(docResId,
				Constants.SHARETYPE_PERSBORROW);
		if (list == null || list.size() == 0)
			return false;
		else {
			for (DocAcl da : list) {
				if (da.getUserId() == memberId)
					return true;
			}
		}
		return false;
	}

	private DocUtils docUtils;

	public DocUtils getDocUtils() {
		return docUtils;
	}

	public void setDocUtils(DocUtils docUtils) {
		this.docUtils = docUtils;
	}

	/**
	 * 找到某个父文档夹的所有一级子节点
	 */
	public List<DocResource> getAllFirstChildren(long parentId) {
		String hql = "from DocResource where parentFrId = ?";
		return this.docResourceDao.find(hql, parentId);
	}

	/**
	 * 初始化
	 * 
	 */
	public void init() {
		// 检查格式类型的排序字段
		int invlidCount = this.docResourceDao.getQueryCount(
				"from DocResource where mimeOrder = 0", null, null);
		if (invlidCount > 0) {
			String hql = "update DocResource set mimeOrder = ? where mimeTypeId = ?";
			log.info("更新文档的排序字段开始。。。");
			for (DocMimeType dmt : com.seeyon.v3x.doc.manager.DocMimeTypeManagerImpl.docMimeTypeTable
					.values()) {
				this.docResourceDao.bulkUpdate(hql, null, dmt.getOrderNum(),
						dmt.getId());
			}
			log.info("更新文档的排序字段结束。");
		}
	}

	/**
	 * 记录转发协同、邮件的日志
	 */
	public void logForward(String isMail, Long docResourceId) {
		if (Strings.isBlank(isMail) || docResourceId == null)
			return;
		DocResource dr = this.getDocResourceById(docResourceId);
		if (dr == null)
			return;
		if (this.isPersonalLib(dr.getDocLibId()))
			return;

		String typeKey = "doc.contenttype.xietong";
		long uId = CurrentUser.get().getId();
		boolean hasMail = false;
		try {
			MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(String
					.valueOf(uId));
			if (mbc != null)
				hasMail = true;
		} catch (Exception e1) {
			log.error("调用邮件接口判断当前用户是否有邮箱设置：", e1);
		}

		if ("true".equals(isMail)) {
			typeKey = "doc.contenttype.mail";
			String typeValue = ResourceBundleUtil.getString(
					Constants.RESOURCE_BASENAME, typeKey);

			// 记录操作日志
			if (hasMail == true)
				operationlogManager.insertOplog(docResourceId, dr
						.getParentFrId(), ApplicationCategoryEnum.doc,
						ActionType.LOG_DOC_FORWARD, ActionType.LOG_DOC_FORWARD
								+ ".desc", CurrentUser.get().getName(),
						typeValue, dr.getFrName());
		} else {
			String typeValue = ResourceBundleUtil.getString(
					Constants.RESOURCE_BASENAME, typeKey);
			operationlogManager.insertOplog(docResourceId, dr.getParentFrId(),
					ApplicationCategoryEnum.doc, ActionType.LOG_DOC_FORWARD,
					ActionType.LOG_DOC_FORWARD + ".desc", CurrentUser.get()
							.getName(), typeValue, dr.getFrName());

		}

	}

	@Deprecated
	public boolean lockState(long docid, boolean locked) {
		DocResource dr = this.docResourceDao.get(docid);
		if (dr == null)
			return false;
		
		if (dr.getIsCheckOut())
			return locked;
		else
			return !locked;
	}

	public Long getParentFrIdByResourceId(Long docResourceId) {
		DocResource res = this.getDocResourceById(docResourceId);
		if (res != null) {
			return res.getParentFrId();
		}
		return null;
	}

	public Long pigeonFormpotent(Long sourceId, Long docresid, String formids)
			throws DocException {
		if (!"".equals(formids) && !"null".equals(formids) && formids != null) {
			if (formids.indexOf("|") > -1) {
				for (int a = 0; a < formids.split("\\|").length; a++) {
					String formid = "";
					String operationid = "";
					DocFromPotent docform = new DocFromPotent();
					String showDetailsArray[] = formids.split("\\|");
					String showDetails[] = showDetailsArray[a].split("\\.");
					formid = showDetails[0];
					operationid = showDetails[1];
					docform.setFormid(Long.parseLong(formid));
					docform.setOperationid(Long.parseLong(operationid));
					docform.setDocresid(docresid);
					docform.setAffairid(sourceId);
					this.docFromPotentDao.insert(docform);
				}
			} else {
				String showDetails[] = formids.split("\\.");
				String formid = "";
				String operationid = "";
				DocFromPotent docform = new DocFromPotent();
				formid = showDetails[0];
				operationid = showDetails[1];
				docform.setFormid(Long.parseLong(formid));
				docform.setOperationid(Long.parseLong(operationid));
				docform.setDocresid(docresid);
				docform.setAffairid(sourceId);
				this.docFromPotentDao.insert(docform);
			}

		}
		return null;
	}

	public void setDocFromPotentDao(DocFromPotentDao docFromPotentDao) {
		this.docFromPotentDao = docFromPotentDao;
	}

	@SuppressWarnings("unchecked")
	public List queryFormpotent(BaseModel bm) throws DocException {
		return this.docFromPotentDao.query(bm);
	}

	public void deleteDocByResources(List<Long> resourceIds, User user) throws DocException {
		List<DocResource> docs = this.docResourceDao.getDocsBySourceId(resourceIds);
		for (DocResource doc : docs) {
			Long did = doc.getId();
			docAlertLatestManager.addAlertLatest(doc,
					Constants.ALERT_OPR_TYPE_DELETE, user.getId(),
					new Timestamp(new Date().getTime()),
					Constants.DOC_MESSAGE_ALERT_DELETE_DOC, null);
			removeDocWithoutAcl(doc, user.getId(), true);
			operationlogManager.insertOplog(did, doc.getParentFrId(),
					ApplicationCategoryEnum.doc,
					ActionType.LOG_DOC_REMOVE_DOCUMENT,
					ActionType.LOG_DOC_REMOVE_DOCUMENT + ".desc", CurrentUser
							.get().getName(), doc.getFrName());
		}
	}

	public List<DocResource> findSubFolderDocs(Long id) {
		return this.docResourceDao.getSubDocResources(id);
	}

	public DocResource getDocByType(long libId, long type) {
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		String hql = "from DocResource as dr where dr.docLibId= :lid and dr.frType = :tp";
		nameParameters.put("lid", libId);
		nameParameters.put("tp", type);

		return (DocResource)docResourceDao.findUnique(hql, nameParameters);
	}
	
	private DocVersionInfoManager docVersionInfoManager;
	public void setDocVersionInfoManager(DocVersionInfoManager docVersionInfoManager) {
		this.docVersionInfoManager = docVersionInfoManager;
	}
	
	public void recoidopertionLog(String fileid, String logType, boolean history) {
		if (Strings.isBlank(fileid) || Strings.isBlank(logType)) {
			return;
		}
		try {
			DocResource dr = null;
			String versionInfo = "";
			if(history) {
				DocVersionInfo dvi = this.docVersionInfoManager.getDocVersion(NumberUtils.toLong(fileid));
				if(dvi != null) {
					dr = dvi.getDocResourceFromXml();
					versionInfo = "[" + Constants.getDocI18nValue("doc.menu.history.label") + " - V" + dvi.getVersion() + ".0]";
				}
			} else {
				dr = this.getDocResourceById(Long.valueOf(fileid));
			}
			
			if (dr == null) {
				return;
			}
			DocLib docLib = this.docUtils.getDocLibManager().getDocLibById(dr.getDocLibId());
			if (docLib == null) {
				return;
			}
			if (docLib.getType() == Constants.PERSONAL_LIB_TYPE.byteValue()) {
				return;
			}
			
			if (docLib.getDownloadLog() != null && docLib.getDownloadLog()
					&& logType.equals("downLoadFile")) {
				this.operationlogManager.insertOplog(dr.getId(), dr
						.getParentFrId(), ApplicationCategoryEnum.doc,
						ActionType.LOG_DOC_DOWNLOAD,
						ActionType.LOG_DOC_DOWNLOAD + ".desc", CurrentUser
								.get().getName(), dr.getFrName() + versionInfo);
			}
			if (docLib.getPrintLog() != null && docLib.getPrintLog()
					&& logType.equals("docPrint")) {
				this.operationlogManager.insertOplog(dr.getId(), dr
						.getParentFrId(), ApplicationCategoryEnum.doc,
						ActionType.LOG_DOC_PRINT, ActionType.LOG_DOC_PRINT
								+ ".desc", CurrentUser.get().getName(), dr
								.getFrName() + versionInfo);
			}

		} catch (Exception e) {
			log.error("记录文档操作日志时候出现问题", e);
		}
	}

	public void recoidopertionLog(String fileid, String logType) {
		this.recoidopertionLog(fileid, logType, false);
	}

	public boolean checkDocResourceIsSystem(String typeId, String docResId)
			throws DocException {
		if (Strings.isNotBlank(typeId)) {
			DocType contentType = contentTypeManager.getContentTypeById(Long
					.valueOf(typeId));
			if (contentType != null) {
				Set<DocTypeDetail> typeDetailes = contentType
						.getDocTypeDetail();
				if (typeDetailes != null) {
					for (DocTypeDetail docTypeDetail : typeDetailes) {
						if (!docTypeDetail.getNullable()) {
							return true;
						}
					}
				}
				return false;
			}
		}
		return false;
	}

	private PartitionManager partitionManager;
	public void setPartitionManager(PartitionManager partitionManager) {
		this.partitionManager = partitionManager;
	}

	/**
	 * 通过文档的id获得sourceId
	 */
	public Long getDocResSourceId(Long docResId) {
		Long sourceId = this.docResourceDao.get(docResId).getSourceId();
		if (sourceId == null) {
			return -1L;
		}
		return sourceId;
	}

	public void saveOrder(List<DocResource> docList, List<Integer> frOrderList) {

		if (docList != null && docList.size() > 0 && frOrderList != null
				&& frOrderList.size() > 0
				&& docList.size() == frOrderList.size()) {
			for (int i = 0; i < docList.size(); i++) {
				DocResource doc = docList.get(i);
				doc.setFrOrder(frOrderList.get(i));

				this.docResourceDao.update(doc);
			}
		}

	}

	public boolean judgeSamePigeonhole(Long docResId, Integer appEnumKey, String colIds) {
		Long contentId = Constants.getContentTypeIdByAppEnumKey(appEnumKey);
		if (contentId != null &&  contentId == ApplicationCategoryEnum.collaboration.key()) {
			List<Long> srIds = FormBizConfigUtils.parseStr2Ids(colIds);
			if(CollectionUtils.isNotEmpty(srIds)) {
				return this.docResourceDao.judgeSamePigeonhole(docResId, contentId, srIds);
			}
		}
		return false;
	}
	
	/**
	 * 获取文件的正文内容，以便用户在文档中心点击文件时，可以直接查看其内容<br>
	 * @param fileId
	 * @see com.seeyon.v3x.doc.controller.DocController#docOpenBody
	 */
	public String getTextContent(Long fileId) {
		String result = null;
		try {
			File f = this.fileManager.getFile(fileId);
			result = CoderFactory.getInstance().getFileToString(f.getAbsolutePath());
		} catch (BusinessException e) {
			log.warn("文件Id=" + fileId + "不存在", e);
		} catch (Exception e) {
			log.error("解密读取文件内容时出现异常，文件Id=" + fileId, e);
		}
		return result;
	}

	public List<DocSortProperty> getDocSortTable(List<DocResource> docs) {
		List<DocSortProperty> sortProperty = new ArrayList<DocSortProperty>();
		
		for(DocResource doc : docs){
			DocSortProperty property = new DocSortProperty();
			property.setId(doc.getId());//复选框的值
			//设置图标
			DocMimeType mime = this.docMimeTypeManager.getDocMimeTypeById(doc.getMimeTypeId());
			String icon = mime.getIcon();
			if(icon.contains("|")){
				icon = icon.split("\\|")[0];
			}
			property.setDocImageType(icon);
			
			//设置名称
			property.setDocName(ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, doc.getFrName()));
			
			//设置内容类型
			DocType type = this.contentTypeManager.getContentTypeById(doc.getFrType());
			property.setDocContentType(type.getDescription());
			
			//设置创建人
			try {
				V3xOrgMember member = this.orgManager.getMemberById(doc.getCreateUserId());
				property.setDocCreater(member == null ? "" : member.getName());
			} catch (BusinessException e) {
				log.error("获取文档排序列表的创建人数据时出错！");
			}
			
			//设置最后修改时间
			property.setDocLastUpdateDate(doc.getLastUpdate());
			
			sortProperty.add(property);//封装
		}
		
		return sortProperty;
	}

	/**
	 * 对文档的4种排序统一成一个方法，Modify By Fanxc
	 * @param docResId : 需要排序的文档的主键id
	 * @param sortType:操作类型，即 upwards（上移） 、downwards（下移）、top（置顶）、end（末页）
	 */
	public boolean sort(Long docResId,String sortType){	
		boolean result = true;
		
		DocResource doc = this.getDocResourceById(docResId);
		int order = doc.getFrOrder();//讲需要排序的文档的序号记录下来，后面用到
		
		String sortLogicChar = "";
		
		if("upwards".equals(sortType) || "top".equals(sortType)){
			sortLogicChar = "<";
		}
		else if("downwards".equals(sortType) || "end".equals(sortType)){
			sortLogicChar = ">";
		}
		
		if("upwards".equals(sortType) || "downwards".equals(sortType)){
		
			result = this.singleSort(doc, sortLogicChar, order);
		}
		else if("top".equals(sortType) || "end".equals(sortType)){
			result = this.listSort(doc, sortLogicChar, order);
		}
		return result;
	}
	
	/**
	 * 文档的上移或者下移，此时只针对两条数据更新数据库
	 * @param doc：需要上移或者下移的文档
	 * @param sortLogicChar：逻辑符号，即"<"（上移）  ">"（下移）
	 * @param order：需要上移或者下移的文档的排序号，应该是update之前的排序号
	 */
	private boolean singleSort(DocResource doc,String sortLogicChar,int order){
		DocResource changedObj = this.getDocByOrderType(doc.getParentFrId(), order,sortLogicChar);
		if(changedObj == null){
			return false;
		}
		
		int changedOrder = changedObj.getFrOrder();
		
		doc.setFrOrder(changedOrder);
		this.docResourceDao.update(doc);
		
		changedObj.setFrOrder(order);
		this.docResourceDao.update(changedObj);
		
		return true;
	}
	
	/**
	 * 文档的置顶或者末页
	 * @param doc：需要置顶或者末页的文档
	 * @param sortLogicChar：：逻辑符号，即"<"（置顶）  ">"（末页）
	 * @param order：：被操作文档的排序号，应该是update之前的排序号
	 * @author Fanxc
	 */
	private boolean listSort(DocResource doc,String sortLogicChar,int order){
		List<DocResource> docList = this.getAllPreOrderByParentId(doc.getParentFrId(), order,sortLogicChar);
		
		if(docList == null || docList.size() == 0){
			return false;
		}
		
		doc.setFrOrder(docList.get(0).getFrOrder());
		
		this.docResourceDao.update(doc);

		for(int i = 0; i < docList.size(); i++){
			DocResource tempPre = docList.get(i);//拿到当前的文档
			if(i == docList.size() - 1){//当是最后一个文档时，就只需要把它的order号设置成我们选择的文档order即可
				tempPre.setFrOrder(order);//此处需要注意，应该用之前保存的序号，而不能直接用doc.getFrOrder()，因为这时已经改变
			}else{
				DocResource tempNext = docList.get(i + 1);//取得它后面的那个文档
				tempPre.setFrOrder(tempNext.getFrOrder());//排序号依次后移
			}
			this.docResourceDao.update(tempPre);
		}
		return true;
	}
	
	/**
	 * 得到所有在此文档排序号之前的文档
	 * @param parentId:文档夹id
	 * @param order:文档的排序号,以此为根据查找
	 * @param orderType:如果是">"，则是在其后面显示的对象 ； 如果是"<"，则是在其前面显示的对象
	 * @author Fanxc
	 */
	@SuppressWarnings("unchecked")
	private List<DocResource> getAllPreOrderByParentId(Long parentId,int order,String orderType){
		List<DocResource> orderList = new ArrayList<DocResource>();

		/**
		 * 此处中sql的条件主要是排除系统自定义的一些文档，限制到我们新建或者上传的
		 * 并没有对权限进行限制，因为只有文档夹的管理员才可以进行【排序】的操作，管理员拥有所有权限
		 */
		StringBuffer buffer = new StringBuffer("from DocResource as d where d.parentFrId=? and d.frOrder ");
		buffer.append(orderType);
		String hql = buffer.toString() + " ? " + DocSearchHqlUtils.HQL_FR_TYPE + "order by d.frOrder";
		if(">".equals(orderType)){
			hql = hql + " desc";
		}
		Object[] indexParam = {parentId, order};

		orderList = docResourceDao.find(hql, -1, -1, null, indexParam);
		return orderList;
	}
	
	public boolean isNeedSort(Long docResId,String sortType){
		DocResource doc = this.getDocResourceById(docResId);
		String sortLogicChar = "";
		
		if("upwards".equals(sortType) || "top".equals(sortType)){
			sortLogicChar = "<";
		}
		else if("downwards".equals(sortType) || "end".equals(sortType)){
			sortLogicChar = ">";
		}
		
		DocResource changedObj = this.getDocByOrderType(doc.getParentFrId(), doc.getFrOrder(), sortLogicChar);
		
		if(changedObj == null){
			return false;
		}
		return true;
	}
	
	public List<DocResource> findAllDocsByPage(Long parentId, Long contentType, Long userId,int flag) {
		Integer pageNo = 0;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		pageNo = first / pageSize + 1;
		
		return this.findAllDocsByPage(parentId, contentType, pageNo, pageSize, userId, flag);
	}
	
	public List<DocResource> findAllDocsByPage(Long parentId, Long contentType, Integer pageNo, Integer pageSize, Long userId, int flag) {
		List<DocResource> ret = new ArrayList<DocResource>();
		List<DocResource> all = this.findAllDocsByPage(parentId, contentType, -1, -1, userId);
		if (all == null)
			return null;
		
		for (DocResource docResource : all) {
			if (flag == 1 && docResource.isThird_hasPingHole()) {
				ret.add(docResource);
				continue;
			}
			
			if (flag == 0 && !docResource.isThird_hasPingHole()) {
				ret.add(docResource);
			}
		}
		
		Pagination.setRowCount(ret.size());
		ret = this.getPagedDrs(ret, pageNo, pageSize);
		return ret;
	}
	
	public void updateDocResourceAfterPingHole(List<Long> ids){
		if(ids == null || ids.size() == 0) {
			return ;
		}
		String hql = "update DocResource dr set dr.third_hasPingHole = '1' where dr.id in (:ids) " ;
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("ids", ids);
		this.docResourceDao.bulkUpdate(hql, namedParameterMap) ;
	}
	
	public Boolean hasPingHole(Long id){
		Boolean flag = false ;
		DocResource docResource = docResourceDao.get(id);
		if(docResource == null) {
			log.error("文件已经被删除！！" ) ;
			return null ;
		}
		if(!docResource.isThird_hasPingHole()){
			return (Boolean)true ;
		}
		return (Boolean)flag ;
	}	
	
	@SuppressWarnings("unchecked")
	public void checkOrder(Long parentId){
	    String hql = "select distinct frOrder from DocResource where parentFrId = ? ";
	    List<Long> ids =  this.docResourceDao.find(hql , null, parentId);
	    String hql1 = "from DocResource as d where d.parentFrId=? ";
		List<DocResource> orderList = docResourceDao.find(hql1, -1, -1, null, parentId);
	    if(ids.size()!=orderList.size()){
	    	int  i = orderList.size()+1;
	    	 for(DocResource dr :orderList ){
	    		 dr.setFrOrder(i);
	    		 i--;
	    	 }
	    }
	}

	private  boolean isDocExsit(Long archiveId){
	   if(archiveId == null ) 
		   return false ;
	   
	   return docResourceDao.isDocResourceExsit(archiveId);
	}
	
	@Deprecated
	public boolean getCheckStatus(Long docId) {
		return !Constants.LOCK_MSG_NONE.equals(this.getLockMsg(docId));
	}
	
	public ContentTypeManager getContentTypeManager() {
		return this.contentTypeManager;
	}
	public DocResourceDao getDocResourceDao() {
		return this.docResourceDao;
	}
	public int getFolderLevelLimit() {
		return folderLevelLimit;
	}
	
	public List<DocResource> getSimpleQueryResult(SimpleDocQueryModel sdqm, Long parentFrId, Byte docLibType,String... type) {
		if(sdqm == null || !sdqm.isValid()) {
			throw new IllegalArgumentException("按照单个属性简单查询文档时，查询条件无效!");
		}
		
		DocSearchModel dsm = new DocSearchModel(sdqm);
		return this.getQueryResult(parentFrId, dsm, docLibType,type);
	}
	
	public List<DocResource> getAdvancedQueryResult(DocSearchModel dsm, Long parentFrId, Byte docLibType,String... type) {
		if(dsm == null || !dsm.isValid())
			throw new IllegalArgumentException("按照多个属性组合查询文档时，查询条件无效!");
		
		return this.getQueryResult(parentFrId, dsm, docLibType,type);
	}
	
	private List<DocResource> getQueryResult(Long docResourceId, DocSearchModel dsm, Byte docLibType,String... type) {
		DocResource dr = docResourceDao.get(docResourceId);
		if (dr == null) {
			log.warn("查询[id=" + docResourceId + "]文档夹下文档时，文档夹已被他人删除!");
			return null;
		}
		List<DocResource> ret = DocSearchHqlUtils.searchByProperties(dr, dsm, docResourceDao, docMetadataDao);
			
		Long userId = CurrentUser.get().getId();
		boolean owner = this.docUtils.isOwnerOfLib(userId, dr.getDocLibId());
		if (docLibType == Constants.LIB_TYPE_NO.byteValue()) {
			DocLib lib = this.docUtils.getDocLibById(dr.getDocLibId());
			docLibType = lib.getType();
		}
		
		return this.handleAclAndPaginate(userId, dr, ret, docLibType, owner, type);
	}
	
	public List<DocResource> getDocsByTypes(Long folderId, Long userId, long... docTypes) {
		DocResource folder = docResourceDao.get(folderId);
		if (folder == null)
			return null;
		
		List<DocResource> ret = this.findDocsByTypes(folder, docTypes);

		DocLib lib = this.docUtils.getDocLibById(folder.getDocLibId());
		Byte docLibType = lib.getType();
		boolean owner = this.docUtils.isOwnerOfLib(userId, folder.getDocLibId());
		
		return this.handleAclAndPaginate(userId, folder, ret, docLibType, owner);
	}
	
	private List<DocResource> handleAclAndPaginate(Long userId, DocResource folder, List<DocResource> ret, 
			Byte docLibType, boolean owner,String... type) {
		Integer pageNo = 0;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		pageNo = first / pageSize + 1;
		Pagination.setFirstResult(first);
		Pagination.setMaxResults(pageSize);
		
		if(CollectionUtils.isNotEmpty(ret)) {
			if (docLibType != Constants.PERSONAL_LIB_TYPE && !owner) {
				String aclIds = Constants.getOrgIdsOfUser(userId);
				ret = docAclManager.getResourcesByConditionAndPotentPage(ret, folder, aclIds, pageNo, pageSize,type);
			} 
			else {		
				Pagination.setRowCount(ret.size());
				ret = this.getPagedDrs(ret, pageNo, pageSize);
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private List<DocResource> findDocsByTypes(DocResource folder, long... docTypes) {
		String hql = "from DocResource as d where d.parentFrId =:parentFrId and d.mimeTypeId in (:mimeTypes) " + 
					 DocSearchHqlUtils.HQL_FR_TYPE + Order_By ;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentFrId", folder.getId());
		List<Long> mimeTypes = new ArrayList<Long>(docTypes.length);
		for(long docType : docTypes) {
			mimeTypes.add(docType);
		}
		params.put("mimeTypes", mimeTypes);
		return docResourceDao.find(hql, -1, -1, params);
	}

	@SuppressWarnings("unchecked")
	public DocResource getDocByFileId(String bodyContent){
		String hql = "select dr from DocResource dr, DocBody db where dr.id=db.docResourceId and db.bodyType != :htmlBody and db.content like :content";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("htmlBody", com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		params.put("content", bodyContent);
		List<DocResource> list = docResourceDao.find(hql, -1, -1, params);
		
		if (CollectionUtils.isNotEmpty(list))
			return list.get(0);
		
		return null;
	}
	
	private LockManager docManageLock;
	public void setDocManageLock(LockManager docManageLock) {
		this.docManageLock = docManageLock;
	}
	
	public void lockWhenAct(Long docResourceId, Long userId) {
		this.docManageLock.lock(userId, docResourceId);
	}
	
	public void lockWhenAct(Long docResourceId) {
		this.lockWhenAct(docResourceId, CurrentUser.get().getId());
	}
	
	public void unLockAfterAct(Long docResourceId) {
		this.docManageLock.unlock(docResourceId);
	}
	
	public String getLockMsg(Long docResId) {
		return this.getLockMsg(docResId, CurrentUser.get().getId());
	}
	
	public String[] getLockMsgAndStatus(Long docResId) {
		return this.getLockMsgAndStatus(docResId, CurrentUser.get().getId());
	}
	
	public String[] getLockMsgAndStatus(Long docResId, Long userId) {
		DocResource dr = docResourceDao.get(docResId);
		return getLockMsgAndStatus(dr, userId);
	}
	
	public String[] getLockMsgAndStatus(DocResource dr, Long userId) {
		// 文档已被删除
		if(dr == null) {
			return new String[]{Constants.getDocI18nValue("doc.lockstatus.msg.docinvalid"),
								String.valueOf(LockStatus.DocInvalid.key())};
		}
		
		// 应用锁(只有文档可能存在)
		String docName = Strings.escapeJavascript(Constants.getDocI18nValue(dr.getFrName()));
		if(!dr.getIsFolder() && dr.getIsCheckOut() && dr.getCheckOutUserId().longValue() != userId) {
			String lockerName = Functions.showMemberName(dr.getCheckOutUserId());
			return new String[]{Constants.getDocI18nValue("doc.lockstatus.msg.applock", docName, lockerName),
								String.valueOf(LockStatus.AppLock.key())};
		}
		
		// 并发操作锁(文档/文档夹均可能存在)
		List<Lock> locks = this.docManageLock.getLocks(dr.getId());
		if(CollectionUtils.isNotEmpty(locks)) {
			Lock lock = locks.get(0);
			// 根据锁定时用户登录时间和目前实际锁持有者的登录时间来确定锁的占有是否仍旧有效
			Long lockerId = lock.getOwner();
			if(lockerId != userId.longValue()) {
				long lockLoginTime = lock.getLoginTime();
				V3xOrgMember member = null;
				try {
					member = this.orgManager.getMemberById(lockerId);
					
					if(member != null && member.isValid()) {
						OnlineUser onlineUser = OnlineRecorder.getOnlineUser(member.getLoginName());
						boolean isLogin = onlineUser != null;
						if(isLogin) {
							long loginTime = onlineUser.getLoginTime().getTime();
							if(loginTime == lockLoginTime) {
								String editorName = Functions.showMemberName(lockerId);
								int resource_choice = dr.getIsFolder() ? 1 : 0;
								return new String[]{Constants.getDocI18nValue("doc.lockstatus.msg.actionlock", docName, editorName, resource_choice),
									  				String.valueOf(LockStatus.ActionLock.key())};
							}
						}
					}
					
				} 
				catch (BusinessException e) {
					log.warn("根据[id=" + lockerId + "]无法查找到对应人员!", e);
				}
			}
		}
		
		return new String[]{Constants.LOCK_MSG_NONE, String.valueOf(Constants.LockStatus.None.key())};
	}
	
	public String getLockMsg(Long docResId, Long userId) {
		String[] msg_status = this.getLockMsgAndStatus(docResId, userId);
		return msg_status[0];
	}
	
	public boolean isDocAppUnlocked(Long docResId, Long userId) {
		DocResource dr = this.getDocResourceById(docResId);
		return dr == null || !dr.getIsCheckOut();
	}
	
	public void updateProjectManagerAuth4ProjectFolder(Long projectId, List<Long> oldManagers, List<Long> newManagers) {
		// 暂只处理新增的项目文档夹管理员，为其赋予全部权限、默认订阅
		List<Long> addedManagers = FormBizConfigUtils.getAddedCollection(oldManagers, newManagers);
		if(CollectionUtils.isNotEmpty(addedManagers)) {
			Long userId = CurrentUser.get().getId();
			DocResource dr = this.getProjectFolderByProjectId(projectId);
			if(dr != null) {
				Long projectFolderId = dr.getId();
				this.docAclManager.deleteProjectFolderShare(projectFolderId, addedManagers);
				int minOrder = docAclManager.getMaxOrder();
				for (Long managerId : addedManagers) {
					Long alertId = docAlertManager.addAlert(projectFolderId, true, Constants.ALERT_OPR_TYPE_ADD, 
							V3xOrgEntity.ORGENT_TYPE_MEMBER, managerId, userId, true, false, true);
					
					docAclManager.setDeptSharePotent(managerId, V3xOrgEntity.ORGENT_TYPE_MEMBER, 
							projectFolderId, Constants.ALLPOTENT, true, alertId,minOrder++);
				}
			}
			else {
				log.warn("项目[id=" + projectId + "]对应项目文件夹已被删除!");
			}
		}
	}
	
	public Long pigeonholeInfo(InfoSummaryCAP summary, boolean hasAttachments)throws DocException {
		log.info("即将归档信息《"+summary.getSubject()+"》archiveId:"+summary.getArchiveId());
		DocResource destFolder = getDocResourceById(summary.getArchiveId());
		if(destFolder == null){
			log.error("信息报送归档查找归档路径失败:"+summary.getId()+" 《"+summary.getSubject()+"》");
		}else{
			log.info("归档信息《"+summary.getSubject()+"》到目录《"+destFolder.getFrName()+"》成功！");
		}
		long sourceId = summary.getId();
		
		String name = summary.getSubject();
		Long contentType = Constants.SYSTEM_INFO;
		Long oldId = this.hasSamePigeonhole(destFolder.getId(), ApplicationCategoryEnum.info.key(), sourceId);
		if (oldId != null)
			return oldId;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("info");
		if (app != null)
			newId = this.pigeonholeWithoutAclByType(summary, app, sourceId,
					hasAttachments, name, contentType,
					destFolder.getDocLibId(), destFolder.getId(), CurrentUser.get().getId(), 0);

		return newId;
	}

	public Long pigeonholeInfoStat(InfoStatCAP infoStat, Long archiveId,boolean hasAttachments) throws DocException {
		log.info("即将归档信息《"+infoStat.getName()+"》archiveId:"+archiveId);
		DocResource destFolder = getDocResourceById(archiveId);
		if(destFolder == null){
			log.error("信息报送统计归档查找归档路径失败:"+infoStat.getId()+" 《"+infoStat.getName()+"》");
		}else{
			log.info("归档信息《"+infoStat.getName()+"》到目录《"+destFolder.getFrName()+"》成功！");
		}
		long sourceId = infoStat.getId();
		
		String name = infoStat.getName();
		Long contentType = Constants.SYSTEM_INFOSTAT;
		Long oldId = this.hasSamePigeonhole(destFolder.getId(), ApplicationCategoryEnum.infoStat.key(), sourceId);
		if (oldId != null)
			return oldId;

		// 2. 声明将来的新Id
		Long newId = null;
		Node app = DocHierarchyManagerImpl.getNodesMap().get("info_stat");
		if (app != null)
			newId = this.pigeonholeWithoutAclByType(infoStat, app, sourceId,
					hasAttachments, name, contentType,
					destFolder.getDocLibId(), destFolder.getId(), CurrentUser.get().getId(), 0);
		return newId;
	}
	/**
	 *成发集团项目 更新文档的密级 
	 */
	public void updateDocSecretLevel(Long docResourceId,Integer secretLevel) throws DocException {
		docResourceDao.updateDocSecretLevel(docResourceId,secretLevel);
	}
		
}