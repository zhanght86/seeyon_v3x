package com.seeyon.v3x.blog.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.addressbook.webmodel.WebWithPropV3xOrgMember;
import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogAttention;
import com.seeyon.v3x.blog.domain.BlogConstants;
import com.seeyon.v3x.blog.domain.BlogEmployee;
import com.seeyon.v3x.blog.domain.BlogFamily;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.blog.domain.BlogReply;
import com.seeyon.v3x.blog.domain.BlogShare;
import com.seeyon.v3x.blog.manager.BlogArticleManager;
import com.seeyon.v3x.blog.manager.BlogFamilyManager;
import com.seeyon.v3x.blog.manager.BlogManager;
import com.seeyon.v3x.blog.webmodel.ArticleModel;
import com.seeyon.v3x.blog.webmodel.ArticleReplyModel;
import com.seeyon.v3x.blog.webmodel.AttentionModel;
import com.seeyon.v3x.blog.webmodel.EmployeeModel;
import com.seeyon.v3x.blog.webmodel.FamilyModel;
import com.seeyon.v3x.blog.webmodel.ShareModel;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocBody;
import com.seeyon.v3x.doc.domain.DocStorageSpace;
import com.seeyon.v3x.doc.manager.DocSpaceManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.webmodel.DocSpaceVO;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * @author xiaoqiuhe blogController
 */
public class BlogController extends BaseController {
	private static final Log log = LogFactory.getLog(BlogController.class);

	private MetadataManager metadataManager;

	private BlogManager blogManager;

	private FileManager fileManager;

	private BlogFamilyManager blogFamilyManager;

	private BlogArticleManager blogArticleManager;

	private BlogEmployee blogEmployee;

	private OrgManager orgManager;

	private AttachmentManager attachmentManager;

	private UserMessageManager userMessageManager;

	private IndexManager indexManager;

	private UpdateIndexManager updateIndexManager;

	private DocSpaceManager docSpaceManager;

	private AppLogManager appLogManager;
	
	private StaffInfoManager staffInfoManager;

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setBlogArticleManager(BlogArticleManager blogArticleManager) {
		this.blogArticleManager = blogArticleManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setBlogManager(BlogManager blogManager) {
		this.blogManager = blogManager;
	}

	public void setBlogFamilyManager(BlogFamilyManager blogFamilyManager) {
		this.blogFamilyManager = blogFamilyManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setDocSpaceManager(DocSpaceManager docSpaceManager) {
		this.docSpaceManager = docSpaceManager;
	}

	// 博客信息收藏(xiaoqiuhe)
	public ModelAndView blogFavorites(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		// System.out.println("blogFavorites");
		String path = "blog/bloguse/chooseFavorites";
		ModelAndView mav = new ModelAndView(path);
		// 记录进入该帖的上一页面
		String resourceMethod = httpServletRequest
				.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);
		// 文章ID
		String articleId = httpServletRequest.getParameter("articleId");
		// Long articleId =
		// Long.parseLong(httpServletRequest.getParameter("articleId"));
		// BlogArticle article = blogArticleManager.getArticleById(articleId);

		// if (article == null){

		// }

		// if(article == null){
		// PrintWriter out = null;
		// try {
		// // out = httpServletResponse.getWriter();
		// } catch (IOException e) {
		// log.error("取得response的输出流", e);
		// }
		// out.println("<script>");
		// out.println("alert(v3x.getMessage('BlogLang.blog_alert_source_deleted_article'));");
		// out.println("window.close();");
		// out.println("</script>");
		// return null;//super.refreshWorkspace();
		// }

		mav.addObject("articleId", articleId);
		// 分类ID
		String id = httpServletRequest.getParameter("familyid");
		mav.addObject("familyid", id);
		// 收藏分类列表
		// List<BlogFamily> favoritesList = null;
		// favoritesList =
		// blogManager.listFamily2(BlogConstants.Blog_FAMILY_TYPE2);
		// mav.addObject("favoritesList", favoritesList.iterator());
		// 所有收藏分类信息显示列表
		List<FamilyModel> FamilyModelList = getFamilyModelList(BlogConstants.Blog_FAMILY_TYPE2);

		// FamilyModel defaultFm = null;
		// for(FamilyModel fm : FamilyModelList){
		// if(fm.getNameFamily().equals(BlogConstants.Blog_FAVORITES_DEFAULT)
		// && fm.getSeqDisplay().intValue() == -1){
		// defaultFm = fm;
		// break;
		// }
		// }
		mav.addObject("defaultFmId", BlogConstants.BLOG_DEFAULT_FAVORITE_ID);
		mav.addObject("FamilyModelList", FamilyModelList);

		return mav;
	}

	// 新建文章
	public ModelAndView blogNew(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/newArticle");
		// 得到博客是否启用
		User currentUser = CurrentUser.get();
		BlogEmployee blogEmployee = blogManager.getEmployeeById(currentUser
				.getId());
		mav.addObject("flagStart", blogEmployee.getFlagStart());
		// 所有分类信息显示列表
		List<FamilyModel> FamilyModelList = getFamilyModelList(BlogConstants.Blog_FAMILY_TYPE1);
		mav.addObject("FamilyModelList", FamilyModelList);
		// edit by bianteng for get blog content list start
		List<BlogArticle> BlogArticleList = null;
		BlogArticleList = blogArticleManager.queryByCondition(CurrentUser.get()
				.getId(), "", "", "");
		List<ArticleModel> allArticleModelList = new ArrayList<ArticleModel>();
		allArticleModelList = this.getArticleModelList(BlogArticleList);
		mav.addObject("articleModellist", allArticleModelList);
		// end
		return mav;
	}

	/**
	 * 点击Home和打开之后的页面进入的修改页面之前的数据处理
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView updateBlog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView updateView = new ModelAndView("blog/bloguse/updateArticle");

		Long articleId = Long.valueOf(request.getParameter("blogId"));
		BlogArticle article = this.blogArticleManager.getArticleById(articleId);
		updateView.addObject("article", article);
		
		DocBody body = new DocBody();
		body.setContent(article.getContent());
	
		updateView.addObject("body", body);
		// 得到博客是否启用
		User currentUser = CurrentUser.get();
		BlogEmployee blogEmployee = blogManager.getEmployeeById(currentUser
				.getId());
		updateView.addObject("flagStart", blogEmployee.getFlagStart());
		// 所有分类信息显示列表
		List<FamilyModel> FamilyModelList = getFamilyModelList(BlogConstants.Blog_FAMILY_TYPE1);
		updateView.addObject("FamilyModelList", FamilyModelList);
		// edit by bianteng for get blog content list start
		List<BlogArticle> BlogArticleList = null;
		BlogArticleList = blogArticleManager.queryByCondition(CurrentUser.get()
				.getId(), "", "", "");
		List<ArticleModel> allArticleModelList = new ArrayList<ArticleModel>();
		allArticleModelList = this.getArticleModelList(BlogArticleList);
		updateView.addObject("articleModellist", allArticleModelList);
		
		List<Attachment> attachments = null;
		attachments = attachmentManager.getByReference(articleId, articleId);
		
		updateView.addObject("attachments", attachments);

		return updateView;

	}

	// 构造所有分类信息显示列表
	public List<FamilyModel> getFamilyModelList(String type) throws Exception {
		List<BlogFamily> BlogFamilyList = null;
		List<FamilyModel> FamilyModelList = null;
		Iterator<BlogFamily> BlogFamilyIterator = null;
		List<String> FamilyNameList = null; // 主题名称列表

		FamilyNameList = new ArrayList<String>();
		FamilyModelList = new ArrayList<FamilyModel>();
		BlogFamilyList = blogManager.listFamily2(type);

		BlogFamilyIterator = BlogFamilyList.iterator();
		// 构造所有分类信息显示列表
		while (BlogFamilyIterator.hasNext()) {
			BlogFamily BlogFamily = null;
			FamilyModel FamilyModel = null;

			BlogFamily = getBlogFamily(BlogConstants.Blog_AUTH_TYPE_ADMIN,
					BlogFamilyIterator.next());

			// 构造主题名称列表
			FamilyNameList.add(BlogFamily.getNameFamily());

			FamilyModel = new FamilyModel();

			FamilyModel.setHasNewPostFlag((byte) 1);
			FamilyModel.setId(BlogFamily.getId());
			FamilyModel.setNameFamily(BlogFamily.getNameFamily());
			FamilyModel.setRemark(BlogFamily.getRemark());

			// 计算该分类的主题数
			FamilyModel.setArticleNumber(BlogFamily.getArticleNumber());

			FamilyModelList.add(FamilyModel);
		}
		return FamilyModelList;
	}

	/**
	 * 显示博客管理主页面(xiaoqiuhe)
	 */
	public ModelAndView listMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/blogmanager/blogmanageframe");
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		// System.out.println("listMain type==" + type);
		if (id != null)
			mav.addObject("id", id);
		if (type != null)
			mav.addObject("type", type);
		return mav;
	}

	/**
	 * 整理收藏夹(xiaoqiuhe)
	 */
	public ModelAndView indexFavoritesSetup(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/favoritesSetupFrame");
		return mav;
	}

	/**
	 * 显示分类的列表页面(xiaoqiuhe)
	 */
	public ModelAndView listFavoritesSetup(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 处理查询条件
		// System.out.println("listFamilyMain type=="+type);
		ModelAndView mav = new ModelAndView("blog/bloguse/favoritesSetup");
		try {

			List<BlogFamily> blogFamilyList = blogManager
					.listFamily(BlogConstants.Blog_FAMILY_TYPE2);

			List<BlogFamily> list = new ArrayList<BlogFamily>();
			for (BlogFamily BlogFamily : blogFamilyList) {
				// if(BlogFamily.getNameFamily().equals(BlogConstants.Blog_FAVORITES_DEFAULT)
				// && BlogFamily.getSeqDisplay().intValue() == -1)
				// continue;

				BlogFamily = getBlogFamily(BlogConstants.Blog_AUTH_TYPE_ADMIN,
						BlogFamily);
				list.add(BlogFamily);
			}

			this.setTotalOfFamily(list);

			mav.addObject("list", list);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		// 得到博客是否启用
		// User currentUser = CurrentUser.get();
		// BlogEmployee blogEmployee =
		// blogManager.getEmployeeById(currentUser.getId());
		// mav.addObject("flagStart", blogEmployee.getFlagStart());
		return mav;
	}

	//
	private void setTotalOfFamily(List<BlogFamily> list) {
		if (list == null || list.size() == 0)
			return;
		for (BlogFamily bf : list) {
			int total = blogArticleManager.getTotalOfFamily(bf.getId());
			bf.setTotal(total);
		}
	}

	// 新建收藏信息框架
	public ModelAndView listFavoritesAdd(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/favoritesSetupCreate");

		return mav;
	}

	/**
	 * 显示分类的列表页面(xiaoqiuhe)
	 */
	public ModelAndView listFamilyMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 处理查询条件
		String type = request.getParameter("type");
		// System.out.println("listFamilyMain type=="+type);
		ModelAndView mav = new ModelAndView("blog/blogmanager/blogmanage");
		try {

			List<BlogFamily> blogFamilyList = blogManager.listFamily(type);

			List<BlogFamily> list = new ArrayList<BlogFamily>();
			for (BlogFamily BlogFamily : blogFamilyList) {
				// if(BlogFamily.getNameFamily().equals(BlogConstants.Blog_FAVORITES_DEFAULT)
				// && BlogFamily.getSeqDisplay().intValue() == -1)
				// continue;

				BlogFamily = getBlogFamily(BlogConstants.Blog_AUTH_TYPE_ADMIN,
						BlogFamily);
				list.add(BlogFamily);
			}

			mav.addObject("list", list);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		// 得到博客是否启用
		User currentUser = CurrentUser.get();
		BlogEmployee blogEmployee = blogManager.getEmployeeById(currentUser
				.getId());
		mav.addObject("flagStart", blogEmployee.getFlagStart());
		return mav;
	}

	// 新建分类信息框架
	public ModelAndView listFamilyAdd(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/blogmanager/blogmanagecreate");

		return mav;
	}

	// 新增分类初始化信息
	public ModelAndView newFamily(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/blogmanager/createfamily");

		return mav;
	}

	// 新增收藏夹初始化信息
	public ModelAndView newFavorites(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/createFavorites");

		return mav;
	}

	/**
	 * 新增关注信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView creatShare(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] employeeIdArray = request.getParameter("id").split(",");
		User user = CurrentUser.get();
		String id = "";
		String type = "";
		int index = 0;
		//
		Long currUser = user.getId();
		BlogShare blogShare = null;
		if (employeeIdArray != null) {
			for (String employeeInfo : employeeIdArray) {
				// System.out.println("creatShare employeeInfo==" +
				// employeeInfo);
				index = employeeInfo.indexOf("|");
				// String[] temArray = employeeInfo.split("|");
				// type = temArray[0];
				type = employeeInfo.substring(0, index);
				// id = temArray[1];
				id = employeeInfo.substring(index + 1);
				// System.out.println("creatShare id==" + id);
				blogShare = new BlogShare();

				blogShare.setIdIfNew();
				blogShare.setType(type);
				blogShare.setEmployeeId(currUser);
				blogShare.setShareId(Long.parseLong(id));
				if (blogManager.checkEmployeeShared(Long.parseLong(id)) > 0) {
					// blogManager.modifyShare(blogShare);
				} else {
					blogManager.createShare(blogShare);
				}

			}
		}
		ModelAndView mav = listAllShare(request, response);
		return mav;

		// return
		// super.redirectModelAndView("/blog.do?listAllShare","parent.parent");
	}

	// /**
	// * 提供方法，在新增员工信息是，新增员工博客信息
	// *
	// * @param request
	// * @param response
	// * @return void
	// * @throws Exception
	// */
	// public void createEmployee(HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	// String id = request.getParameter("id");
	// //System.out.println("createEmployee id==" + id);
	// User user = CurrentUser.get();
	// blogEmployee = new BlogEmployee();
	//
	// blogEmployee.setId(Long.parseLong(id));
	// blogEmployee.setIntroduce("");
	// blogEmployee.setImage("");
	// blogEmployee.setFlagStart((byte) 0);
	// blogEmployee.setFlagShare((byte) 0);
	// blogEmployee.setArticleNumber(0);
	// blogEmployee.setIdCompany(user.getLoginAccount());
	// if (blogManager.checkEmployeeId(Long.parseLong(id)) > 0) {
	// // blogManager.modifyShare(blogShare);
	// } else {
	// blogManager.createEmployee(blogEmployee);
	// }
	//
	// }
	/**
	 * 新增博客共享
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView createAttention(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// String[] employeeIdArray = request.getParameter("id").split(",");
		String[] employeeIdArray = request.getParameterValues("confreres");
		// String resourceMethod = request.getParameter("resourceMethod");
		User user = CurrentUser.get();
		//
		Long currUser = user.getId();
		Long attentionId = null;
		// 删除所有关注
		List<BlogAttention> blogAttentionList = blogManager.listAttention2();
		Iterator<BlogAttention> BlogAttentionIterator = blogAttentionList
				.iterator();
		while (BlogAttentionIterator.hasNext()) {
			blogManager.deleteAttention(BlogAttentionIterator.next().getId());
		}
		if (employeeIdArray != null) {
			for (String employeeInfo : employeeIdArray) {
				// System.out.println("creatShare employeeInfo==" +
				// employeeInfo);
				attentionId = Long.parseLong(employeeInfo);
				BlogAttention blogAttention = new BlogAttention();

				blogAttention.setIdIfNew();
				blogAttention.setEmployeeId(currUser);
				blogAttention.setAttentionId(attentionId);
				if (blogManager.checkEmployeeAttention(attentionId) > 0) {
					// blogManager.modifyShare(blogShare);
				} else {
					blogManager.createAttention(blogAttention);
				}
			}
		}
		// ModelAndView mav = listAllShare(request, response);
		// return mav;
		return super
				.redirectModelAndView("/blog.do?method=listShareOtherIndex");
	}

	// 新增分类信息(xiaoqiuhe)
	// public ModelAndView createFamily(HttpServletRequest httpServletRequest,
	// HttpServletResponse httpServletResponse) throws Exception {
	// BlogFamily BlogFamily = new BlogFamily();
	//
	// BlogFamily.setIdIfNew();
	// String tem = "";
	// tem = httpServletRequest.getParameter("nameFamily");
	// BlogFamily.setNameFamily(tem);
	// tem = httpServletRequest.getParameter("remark");
	// BlogFamily.setRemark(tem);
	// tem = httpServletRequest.getParameter("seqDisplay");
	// BlogFamily.setSeqDisplay(new Integer(tem));
	// tem = httpServletRequest.getParameter("type");
	// // System.out.println("createFamily type=="+tem);
	// BlogFamily.setType(tem);
	// BlogFamily.setArticleNumber(new Integer(0));
	//
	// User issueUser = CurrentUser.get();
	//
	// BlogFamily.setEmployeeId(issueUser.getId());
	//
	// blogManager.createFamily(BlogFamily);// 新增加分类信息，调用底层添加。
	//
	// // String authInfo = httpServletRequest.getParameter("blogFamilyAdmin");
	// // this.setAuthInfo(BlogConstants.Blog_AUTH_TYPE_ADMIN,
	// // blogFamily.getId()
	// // .toString(), authInfo);
	//
	// // 跳向查询分类信息--》 listFamilyMain
	// return super.redirectModelAndView("/blog.do?method=listMain&type="
	// + tem, "parent.parent");
	// }
	// 新增分类信息(xiaoqiuhe)
	public ModelAndView createFavorites(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		BlogFamily BlogFamily = new BlogFamily();

		// List<BlogFamily> blogFamilyList =
		// blogManager.listFamily(BlogConstants.Blog_FAMILY_TYPE2);

		// int seq = blogFamilyList.size()+1;
		BlogFamily.setIdIfNew();
		String tem = "";
		tem = httpServletRequest.getParameter("nameFamily");
		BlogFamily.setNameFamily(tem);
		tem = httpServletRequest.getParameter("remark");
		BlogFamily.setRemark(tem);
		// tem = httpServletRequest.getParameter("seqDisplay");
		// BlogFamily.setSeqDisplay(seq);
		BlogFamily.setSeqDisplay(0);
		BlogFamily.setCreateDate(new Date()) ;
		// tem = httpServletRequest.getParameter("type");
		// System.out.println("createFamily type=="+tem);
		BlogFamily.setType(BlogConstants.Blog_FAMILY_TYPE2);
		BlogFamily.setArticleNumber(new Integer(0));

		User issueUser = CurrentUser.get();

		BlogFamily.setEmployeeId(issueUser.getId());

		blogManager.createFamily(BlogFamily);// 新增加分类信息，调用底层添加。

		// String authInfo = httpServletRequest.getParameter("blogFamilyAdmin");
		// this.setAuthInfo(BlogConstants.Blog_AUTH_TYPE_ADMIN,
		// blogFamily.getId()
		// .toString(), authInfo);

		// 跳向查询分类信息--》 listFamilyMain
		return super.redirectModelAndView(
				"/blog.do?method=indexFavoritesSetup", "parent.parent");
	}

	// 新增收藏文章(xiaoqiuhe)
	public ModelAndView createFavoritesArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		BlogFavorites BlogFavorites = new BlogFavorites();
		User issueUser = CurrentUser.get();

		BlogFavorites.setIdIfNew();
		String familyid = request.getParameter("familyid");
		// System.out.println("createFavorites familyId type=="+tem);
		BlogFavorites.setFamilyId(Long.parseLong(familyid));
		String articleId = request.getParameter("articleId");

		// if ( blogManager.checkFavorites(Long.valueOf(articleId),
		// CurrentUser.get().getId())>0){

		// }
		BlogArticle article = new BlogArticle();
		// String[] arr = articleId.split(",");2
		// System.out.println("createFavorites articleId type=="+tem);
		// BlogFavorites.setArticleId(Long.parseLong(articleId));
		if (articleId != null && familyid != null) {

			article.setId(Long.valueOf(articleId));
			BlogFavorites.setBlogArticle(article);
			BlogFavorites.setEmployeeId(issueUser.getId());
			if (!(blogManager.checkFavorites(Long.valueOf(articleId),
					CurrentUser.get().getId()) > 0)) {
				// try {blogManager.createFavorites(BlogFavorites);//
				// 新增加收藏信息，调用底层添加。
				// }catch(Exception e){

				// log.error("", e);
				blogManager.createFavorites(BlogFavorites);// 新增加收藏信息，调用底层添加。

			} else {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out
						.println("alert(parent.v3x.getMessage('BlogLang.blog_alert_article_favorited'));");
				out.println("</script>");
			}

		}

		PrintWriter out = response.getWriter();
		out.println("<script>");

		out.println("parent.window.returnValue = \"success\";");
		out.println("window.close();");

		out.println("</script>");
		return null;

		// return super
		// .redirectModelAndView("/blog.do?method=showPost&resourceMethod="
		// + resourceMethod
		// + "&FamilyId="
		// + familyid
		// + "&articleId=" + articleId);
	}

	// 初始化分类信息建立默认分类和私有分类，默认收藏分类(xiaoqiuhe)

	/**
	 * public ModelAndView updateFavoritesArticle(HttpServletRequest request,
	 * HttpServletResponse response) throws Exception {
	 * 
	 * 
	 * BlogFavorites BlogFavorites = new BlogFavorites();
	 * 
	 * String[] tem = request.getParameterValues("familyId");
	 * 
	 * String familyid = request.getParameter("familyid");
	 * 
	 * String articleId = request.getParameter("articleId"); String[] ids =
	 * request.getParameterValues("articleid"); //String[] arr =
	 * articleId.split(",");2 // System.out.println("createFavorites articleId
	 * type=="+tem); // BlogFavorites.setArticleId(Long.parseLong(articleId));
	 * BlogArticle article = new BlogArticle();
	 * article.setId(Long.valueOf(articleId));
	 * BlogFavorites.setBlogArticle(article);
	 * 
	 * User issueUser = CurrentUser.get(); String favids = articleId;
	 * 
	 * BlogFavorites.setEmployeeId(issueUser.getId());
	 *  // blogManager.updateFavorites(articleId, long
	 * newFamId);//修改收藏分类信息，调用底层update。 // PrintWriter out =
	 * response.getWriter(); // out.println("<script>"); //
	 * out.println("parent.window.close();"); // out.println("</script>");
	 * return super
	 * .redirectModelAndView("/blog.do?method=listFavoritesArticle&articleId=" +
	 * articleId + "&FamilyId=" + familyid); }
	 */
	// 初始化分类信息建立默认分类和私有分类，默认收藏分类(xiaoqiuhe)
	public void initFamily(Long employeeId) throws Exception {
		// 判断是否已经初始化
		Long tem = blogManager.getDefaultFamilyID(employeeId,
				BlogConstants.Blog_FAMILY_TYPE1);
		// System.out.println("initFamily
		// employeeId=="+employeeId+",tem=="+tem);
		if (tem == null || tem != 0) {
			return;
		}

		BlogFamily BlogFamily = new BlogFamily();
		BlogFamily.setIdIfNew();
		BlogFamily.setNameFamily(BlogConstants.Blog_FAMILY_DEFAULT);
		BlogFamily.setRemark("");
		BlogFamily.setType(BlogConstants.Blog_FAMILY_TYPE1);
		BlogFamily.setArticleNumber(new Integer(0));
		BlogFamily.setSeqDisplay(new Integer(0));

		BlogFamily.setEmployeeId(employeeId);
		// 新增加默认分类信息，调用底层添加。
		blogManager.createFamily(BlogFamily);

		BlogFamily = new BlogFamily();
		BlogFamily.setIdIfNew();
		BlogFamily.setNameFamily(BlogConstants.Blog_PRIVATE_DEFAULT);
		BlogFamily.setRemark("");
		BlogFamily.setType(BlogConstants.Blog_FAMILY_TYPE1);
		BlogFamily.setArticleNumber(new Integer(0));
		BlogFamily.setSeqDisplay(new Integer(1));

		BlogFamily.setEmployeeId(employeeId);
		// 新增加私有分类信息，调用底层添加。
		blogManager.createFamily(BlogFamily);

		// BlogFamily = new BlogFamily();
		// BlogFamily.setIdIfNew();
		// BlogFamily.setNameFamily(BlogConstants.Blog_FAVORITES_DEFAULT);
		// BlogFamily.setRemark("");
		// BlogFamily.setType(BlogConstants.Blog_FAMILY_TYPE2);
		// BlogFamily.setArticleNumber(new Integer(0));
		// BlogFamily.setSeqDisplay(-1);
		//
		// BlogFamily.setEmployeeId(employeeId);
		//
		// blogManager.createFamily(BlogFamily);// 新增加默认收藏分类信息，调用底层添加。

		return;
	}

	// 查询分类信息(修改)
	public ModelAndView listFamilyModify(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/blogmanager/blogmanagemodify");

		return mav;
	}

	// 查询分类信息(修改)
	public ModelAndView listFavoritesModify(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/favoritesSetupModify");

		return mav;
	}

	// 变更分类变更前信息
	public ModelAndView oldFamily(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/blogmanager/modifyfamily");

		String blogFamilyId = httpServletRequest.getParameter("id");
		BlogFamily blogFamily = blogManager.getSingleFamily(new Long(
				blogFamilyId));

		blogFamily = this.getBlogFamily(BlogConstants.Blog_AUTH_TYPE_ADMIN,
				blogFamily);

		mav.addObject("blogFamily", blogFamily);

		// 版主信息，用于选人界面回显
		String admin = blogFamilyManager.getFamilyAuth("0", new Long(
				blogFamilyId));
		mav.addObject("admin", admin);

		return mav;
	}

	// 变更收藏夹信息（xiaoqiuhe）
	public ModelAndView oldFavorites(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/modifyFavorites");

		String blogFamilyId = httpServletRequest.getParameter("id");
		BlogFamily blogFamily = blogManager.getSingleFamily(new Long(
				blogFamilyId));

		blogFamily = this.getBlogFamily(BlogConstants.Blog_AUTH_TYPE_ADMIN,
				blogFamily);

		mav.addObject("blogFamily", blogFamily);

		// 版主信息，用于选人界面回显
		String admin = blogFamilyManager.getFamilyAuth("0", new Long(
				blogFamilyId));
		mav.addObject("admin", admin);

		return mav;
	}

	// 变更分类信息
	public ModelAndView modifyFamily(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		BlogFamily blogFamily = setVO(httpServletRequest, httpServletResponse);
		String blogFamilyId = httpServletRequest.getParameter("id");
		String type = httpServletRequest.getParameter("type");
		// System.out.println("modifyFamily type=="+type);
		blogFamily.setId(new Long(blogFamilyId));

		blogManager.modifyFamily(blogFamily);

		return super.redirectModelAndView("/blog.do?method=listMain&type="
				+ type, "parent.parent");
	}

	// 变更收藏夹信息
	public ModelAndView modifyFavorites(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		BlogFamily blogFamily = setVO(httpServletRequest, httpServletResponse);
		String blogFamilyId = httpServletRequest.getParameter("id");
		String type = BlogConstants.Blog_FAMILY_TYPE2;
		// System.out.println("modifyFavorites type=="+type);
		blogFamily.setId(new Long(blogFamilyId));

		blogManager.modifyFamily(blogFamily);

		return super.redirectModelAndView(
				"/blog.do?method=indexFavoritesSetup", "parent.parent");
	}

	// 变更文章分类信息
	public ModelAndView modifyArticleFamily(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		// 记录进入该帖的上一页面
		String resourceMethod = httpServletRequest
				.getParameter("resourceMethod");
		Long articleId = Long.parseLong(httpServletRequest
				.getParameter("articleId"));
		Long familyid = Long.parseLong(httpServletRequest
				.getParameter("familyid"));
		BlogArticle article = blogArticleManager.getArticleById(articleId);

		article.setFamilyId(familyid);
		blogArticleManager.updateFamilyId(articleId, familyid);

		return super.redirectModelAndView("/blog.do?method=" + resourceMethod);
	}

	// 查询分类信息
	public ModelAndView listFamilyDel(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("blog/blogmanager/blogmanagedelete");

		return mav;
	}

	// 删除分类信息(xiaoqiuhe)
	public ModelAndView delFamily(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		String type = httpServletRequest.getParameter("type");
		String[] blogFamilyIdArray = httpServletRequest.getParameter("id")
				.split(",");
		String defaultFamilyId = null;
		String defaultPrivateId = null;
		BlogFamily blogFamily = null;
		List<BlogArticle> familyArticleList = null;
		if (blogFamilyIdArray != null) {
			for (String id : blogFamilyIdArray) {
				try {
					blogFamily = blogManager
							.getSingleFamily(Long.parseLong(id));
					// 判断不能删除默认分类
					defaultFamilyId = blogManager.getDefaultFamilyID(
							blogFamily.getEmployeeId(), blogFamily.getType())
							.toString();
					if (defaultFamilyId != null && defaultFamilyId.equals(id)) {
						continue;
					}
					// 判断不能删除私有分类
					defaultPrivateId = blogManager.getPrivateFamilyID(
							blogFamily.getEmployeeId(), blogFamily.getType())
							.toString();
					if (defaultPrivateId != null && defaultPrivateId.equals(id)) {
						continue;
					}
					blogManager.deleteFamily(new Long(id));

					// 将此分类下的文章，更改成未分类
					familyArticleList = blogArticleManager
							.listArticleByFamilyId(Long.valueOf(id));
					if (familyArticleList != null) {
						for (BlogArticle article : familyArticleList) {
							article
									.setFamilyId(Long
											.parseLong(defaultFamilyId));
							blogArticleManager.createArticle(article);
						}
					}
				} catch (org.springframework.dao.DataIntegrityViolationException me) {
					log.error(me.getMessage(), me);
				}
			}
		}

		return super.redirectModelAndView("/blog.do?method=listMain&type="
				+ type);
	}

	// 删除收藏夹(xiaoqiuhe)
	public ModelAndView delFavorites(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		String[] blogFamilyIdArray = httpServletRequest.getParameter("id")
				.split(",");
		String defaultFamilyId = null;
		BlogFamily blogFamily = null;
		List<BlogArticle> familyArticleList = null;
		if (blogFamilyIdArray != null) {
			for (String id : blogFamilyIdArray) {
				try {
					blogFamily = blogManager
							.getSingleFamily(Long.parseLong(id));
					// 判断不能删除默认分类
					defaultFamilyId = blogManager.getDefaultFamilyID(
							blogFamily.getEmployeeId(), blogFamily.getType())
							.toString();
					if (defaultFamilyId != null && defaultFamilyId.equals(id)) {
						continue;
					}
					blogManager.deleteFamily(new Long(id));

					// 将此分类下的文章，更改成未分类
					familyArticleList = blogArticleManager
							.listArticleByFamilyId(Long.valueOf(id));
					if (familyArticleList != null) {
						for (BlogArticle article : familyArticleList) {
							article
									.setFamilyId(Long
											.parseLong(defaultFamilyId));
							blogArticleManager.createArticle(article);
						}
					}
				} catch (org.springframework.dao.DataIntegrityViolationException me) {
					log.error(me.getMessage(), me);
				}
			}
		}

		return super
				.redirectModelAndView("/blog.do?method=indexFavoritesSetup");
	}

	// 删除收藏文章(xiaoqiuhe)
	public ModelAndView delFavoritesArticle(HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws Exception {

		String[] affairIds = request.getParameterValues("affairId");
		String pageType = request.getParameter("pageType");
		if (affairIds != null) {
			for (String affairId : affairIds) {
				Long articleId = new Long(affairId);
				blogManager.deleteFavorites(articleId);
			}
		}

		String method = request.getParameter("from");
		if (method == null) {
			method = "listLatestFiveFavoritesArticleAndAllFamily";
		}
		return this.redirectModelAndView("/blog.do?method=" + method);
	}

	// setVO
	private BlogFamily setVO(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		BlogFamily blogFamily = new BlogFamily();

		String tem = httpServletRequest.getParameter("nameFamily");
		blogFamily.setNameFamily(tem);
		tem = httpServletRequest.getParameter("remark");
		blogFamily.setRemark(tem);

		tem = httpServletRequest.getParameter("type");
		blogFamily.setType(tem);

		tem = httpServletRequest.getParameter("articleNumber");
		blogFamily.setArticleNumber(Integer.parseInt(tem));
		// tem = httpServletRequest.getParameter("seqDisplay");
		blogFamily.setSeqDisplay(0);
		tem = httpServletRequest.getParameter("employeeId");
		blogFamily.setEmployeeId(Long.parseLong(tem));

		return blogFamily;
	}

	// 删除主题,同时删除该主题下的所有回复帖(xiaoqiuhe)
	public ModelAndView deleteArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] affairIds = request.getParameterValues("affairId");
		String familyid = request.getParameter("familyid");
		// 得到当前用户
		// User currentUser = CurrentUser.get();
		// Long employeeId = currentUser.getId();
		Long articleId = null;
		long size = 0L;
		if (affairIds != null) {
			for (String affairId : affairIds) {
				articleId = Long.parseLong(affairId);
				this.attachmentManager.removeByReference(articleId);
				BlogArticle article = blogArticleManager
						.getArticleById(articleId);
				if (article != null) {
					size = article.getArticleSize();
					blogArticleManager.deleteReplyPostByArticleId(articleId);
					// 更新员工文章数,文章数减一
					blogManager
							.updateArticleNumber(article.getEmployeeId(), -1);
					// 更新分类文章数,文章数减一
					if (familyid != null && familyid.length() != 0) {
						blogManager.updateFamilyArticleNumber(Long
								.parseLong(familyid), 1);
					}
					// TODO 根据文章ID删除收藏
					// 删除此文章的收藏
					blogManager.deleteFavoritesByArticleId(articleId);
					blogArticleManager.deleteArticle(articleId);
					// 计算文章并更新所占空间
					
					User user = CurrentUser.get();
					docSpaceManager.deleteBlogSpaceSize(user.getId(), -size);
				}

			}
		}

		String method = request.getParameter("from");
		String resourceMethod = request.getParameter("resourceMethod");
		String familyStr = request.getParameter("familyId");
		if (resourceMethod != null) {
			Long familyId = null;
			if (familyStr != null) {
				familyId = Long.valueOf(familyStr);
			}

			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("parent.closePage();");
			out.print("</script>");
			return  null;
			
		} else if (familyStr != null) {
			Long familyId = Long.valueOf(familyStr);
			return this.redirectModelAndView("/blog.do?method=" + method
					+ "&&id=" + familyId);
		} else if (method == null) {
			method = "listAllArticle";
		}

		return this.redirectModelAndView("/blog.do?method=" + method);
	}

	// 删除收藏主题(xiaoqiuhe)
	public ModelAndView deleteFavoritesArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] affairIds = request.getParameterValues("id");
		if (affairIds != null) {
			for (String affairId : affairIds) {
				Long id = new Long(affairId);
				// 删除此文章的收藏
				blogManager.deleteFavorites(id);
			}
		}
		return this
				.redirectModelAndView("/blog.do?method=listAllFavoritesArticle");
	}

	// 修改收藏分类
	public ModelAndView modifyFavoritesArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] favIds = request.getParameterValues("id");
		String tem = request.getParameter("familyId");
		Long famiyId = Long.valueOf(tem);
		if (favIds != null) {
			for (String favId : favIds) {
				String id = new String(favId);
				blogManager.updateFavorites(id, famiyId);
			}
		}
		// String tem = request.getParameter("familyId");
		// Long familyId = new Long(Id);
		// Long famiyId = Long.valueOf(tem);

		// blogManager.updateFavorites(Ids,famiyId);

		return this
				.redirectModelAndView("/blog.do?method=listAllFavoritesArticle");
	}

	// 解析管理员名称
	private BlogFamily getBlogFamily(String authType, BlogFamily BlogFamily)
			throws Exception {
		String blogFamilyAdmin = blogFamilyManager.getFamilyAuth(authType,
				new Long(BlogFamily.getId()));
		String blogFamilyAdminName = "";
		if (blogFamilyAdmin != null) {
			if (blogFamilyAdmin.length() > 0) {
				blogFamilyAdminName = this.getModelName(blogFamilyAdmin);
			}
		}
		// BlogFamily.setBlogFamilyAdminName(blogFamilyAdminName);

		return BlogFamily;
	}

	// 解析组件名称
	private String getModelName(String str) {
		String blogFamilyPrincipalId = ""; // 该字符串保存的是管理员的ID，中间用","分隔
		StringBuffer blogFamilyPrincipal = new StringBuffer(); // 该字符串保存的是管理员的名称，中间用","分隔
		try {
			if (StringUtils.isNotEmpty(str)) {
				blogFamilyPrincipalId = str;
				String[] principalId = null; // 保存管理员ID的数组
				principalId = blogFamilyPrincipalId
						.split(BlogConstants.Blog_MODULE_DELI3);
				int i = 1;

				for (String id : principalId) {
					if (principalId.length == 1) {
						// if
						// (id.equals(BlogConstants.Blog_Family_ADMIN_IS_ALL)) {
						// return BlogConstants.Blog_Family_ADMIN_IS_ALL_VALUE;
						// }
					}
					String name = null;
					name = this.orgManager.getEntity(id).getName();
					blogFamilyPrincipal.append(name);
					if (i++ < principalId.length)
						blogFamilyPrincipal
								.append(BlogConstants.Blog_MODULE_DELI2);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return blogFamilyPrincipal.toString();
	}

	// default
	public ModelAndView index(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		return null;
	}

	// 将List<BlogArticle>转换为List<ArticleModel>(xiaoqiuhe)
	public List<ArticleModel> getArticleModelList(List<BlogArticle> articleList)
			throws Exception {
		List<ArticleModel> articleModelList = new ArrayList<ArticleModel>();
		Long userId = null;
		V3xOrgMember v3xOrgMember = null;

		String userName = "";
		if (articleList != null) {
			// 构造显示列表
			for (BlogArticle BlogArticle : articleList) {
				ArticleModel articleModel = new ArticleModel();
				articleModel.setId(BlogArticle.getId());
				articleModel.setSubject(Strings.toHTML(BlogArticle.getSubject()));
				articleModel.setFamilyId(BlogArticle.getFamilyId());

				articleModel.setClickNumber((Integer) BlogArticle
						.getClickNumber());

				// 计算该主题的回复数
				articleModel.setReplyNumber((Integer) BlogArticle
						.getReplyNumber());

				articleModel.setIssueTime(new java.sql.Date(BlogArticle
						.getIssueTime().getTime()));

				articleModel.setShareState(BlogArticle.getState());

				List<Attachment> attachment = attachmentManager
						.getByReference(BlogArticle.getId());
				// 是否有附件
				if (attachment != null && attachment.size() > 0) {
					articleModel.setAttachmentFlag((byte) 1);
				} else {
					articleModel.setAttachmentFlag((byte) 0);
				}
				// articleModel.setFavoritesId(BlogArticle.getFavoritesId());

				// 发布人信息
				userId = BlogArticle.getEmployeeId();
				v3xOrgMember = orgManager.getMemberById(userId);
				if (v3xOrgMember != null) {
					userName = v3xOrgMember.getName();
				}
				articleModel.setEmployeeId(userId);
				articleModel.setUserName(userName);
				// articleModel.setShareState(BlogArticle.getState());
				articleModelList.add(articleModel);

			}
		}

		return articleModelList;
	}

	public List<ArticleModel> getArticleModelListByFavorites(
			List<BlogFavorites> bfs, List<FamilyModel> FamilyModelList)
			throws Exception {
		List<ArticleModel> articleModelList = new ArrayList<ArticleModel>();
		Long userId = null;
		V3xOrgMember v3xOrgMember = null;
		String userName = "";
		if (bfs != null) {
			// 构造显示列表
			// for (BlogArticle BlogArticle : articleList) {
			Map<Long, String> map = new HashMap<Long, String>();
			for (FamilyModel fm : FamilyModelList) {
				map.put(fm.getId(), fm.getNameFamily());
			}

			for (BlogFavorites bf : bfs) {
				BlogArticle BlogArticle = bf.getBlogArticle();
				ArticleModel articleModel = new ArticleModel();
				articleModel.setId(BlogArticle.getId());
				articleModel.setSubject(BlogArticle.getSubject());
				articleModel.setFamilyId(bf.getFamilyId());

				articleModel.setFamilyName(map.get(articleModel.getFamilyId()));
				articleModel.setClickNumber((Integer) BlogArticle
						.getClickNumber());

				// 计算该主题的回复数
				articleModel.setReplyNumber((Integer) BlogArticle
						.getReplyNumber());

				articleModel.setIssueTime(new java.sql.Date(BlogArticle
						.getIssueTime().getTime()));

				List<Attachment> attachment = attachmentManager
						.getByReference(BlogArticle.getId());
				// 是否有附件
				if (attachment != null && attachment.size() > 0) {
					articleModel.setAttachmentFlag((byte) 1);
				} else {
					articleModel.setAttachmentFlag((byte) 0);
				}
				articleModel.setFavoritesId(bf.getId());

				// 发布人信息
				userId = BlogArticle.getEmployeeId();
				v3xOrgMember = orgManager.getMemberById(userId);
				if (v3xOrgMember != null) {
					userName = v3xOrgMember.getName();
				}
				articleModel.setEmployeeId(userId);
				articleModel.setUserName(userName);
				articleModelList.add(articleModel);
			}
		}

		return articleModelList;
	}

	// 得到个人信息(xiaoqiuhe)
	public ModelAndView setEmployeeInfo(ModelAndView mav, Long userId)
			throws Exception {
		// 得到个人信息
		BlogEmployee BlogEmployee = null;

		String userName = "";
		BlogEmployee = blogManager.getEmployeeById(userId);
		V3xOrgMember v3xOrgMember = orgManager.getMemberById(userId);
		if (v3xOrgMember != null) {
			userName = v3xOrgMember.getName();
		}
		mav.addObject("userId", userId);
		mav.addObject("userName", userName);
		mav.addObject("introduce", BlogEmployee.getIntroduce());
		return mav;
	}

	// 管理员功能：显示博客所有的主题信息，按照时间降序排列(xiaoqiuhe)
	public ModelAndView listAllArticleAdmin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/admin/listArticle";

		ModelAndView mav = new ModelAndView(path);
		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);

		List<BlogArticle> blogArticleList = null;
		List<ArticleModel> allArticleModelList = new ArrayList<ArticleModel>();

		blogArticleList = blogArticleManager.listAllUsersArticlePaging();

		// flagAdmin==1为文章管理员
		mav.addObject("flagAdmin", (byte) 1);
		allArticleModelList = this.getArticleModelList(blogArticleList);

		mav.addObject("articleModellist", allArticleModelList);
		return mav;
	}

	// 得到年月选择框,用于按年月查询(xiaoqiuhe)
	public ModelAndView getCalendar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/bloguse/getCalendar";
		ModelAndView mav = new ModelAndView(path);
		String userId = request.getParameter("userId");
		String type = request.getParameter("type");
		String date = Datetimes.formatDate(new Date());
		String tem = date.substring(0, 4);
		mav.addObject("calSelectedYear", tem);
		tem = date.substring(5, 7);
		mav.addObject("calSelectedMonth", tem);
		tem = date.substring(8, 10);

		mav.addObject("calSelectedDate", tem);
		mav.addObject("type", type);
		mav.addObject("userId", userId);

		return mav;
	}

	// 显示博客所有的主题信息，按照时间降序排列(xiaoqiuhe)
	public ModelAndView listAllArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/bloguse/listArticle";

		ModelAndView mav = new ModelAndView(path);
		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);

		String userId = request.getParameter("userId");
		List<BlogArticle> blogArticleList = null;
		List<ArticleModel> allArticleModelList = new ArrayList<ArticleModel>();
		User currentUser = CurrentUser.get();
		long currentUserId = currentUser.getId();
		long dataUserId = currentUserId;
		if (userId == null || userId.trim().equals("")) {
			// 得到个人信息
			mav = setEmployeeInfo(mav, currentUserId);
			mav.addObject("flagAdmin", 1);
			// 如果是本人，则列出私有和共享的文章
			blogArticleList = blogArticleManager.listAllArticlePaging(
					currentUserId, (byte) 2);
		} else {
			mav = setEmployeeInfo(mav, Long.parseLong(userId));
			StaffInfo staff = staffInfoManager.getStaffInfoById(Long.parseLong(userId));
			mav.addObject("staff", staff);
			if (null != staff) {
				if (Strings.isNotBlank(staff.getSelf_image_name())) {
					if(staff.getSelf_image_name().startsWith("fileId")){
						mav.addObject("image", "image1");
					}else{
						mav.addObject("image", "image2");
					}
				} 
	 		}
			mav.addObject("flagAdmin", 0);
			// 如果不是本人，则只列出共享文章
			blogArticleList = blogArticleManager.listAllArticlePaging(Long
					.parseLong(userId), (byte) 0);

			dataUserId = Long.parseLong(userId);
		}
		// flagAdmin==1为文章管理员
		Byte flagAdmin = 0;
		if (userId == null || userId.trim().equals("")) {
			flagAdmin = 1;
		} else if (Long.parseLong(userId) == currentUserId) {
			flagAdmin = 1;
		}
		// 验证是否已经开通博客
		if (flagAdmin == 1) {
			BlogEmployee blogEmployee = blogManager.getEmployeeById(currentUser
					.getId());
			if (blogEmployee.getFlagStart() == null
					|| blogEmployee.getFlagStart() != 1) {
				flagAdmin = 2;
			}
		}

		String searchFlag = request.getParameter("searchFlag");

		boolean boolSearch = (searchFlag != null && "true".equals(searchFlag));

		if (!boolSearch) {
			mav.addObject("condition", "");
		}
		mav.addObject("flagAdmin", flagAdmin);
		allArticleModelList = this.getArticleModelList(blogArticleList);
		mav.addObject("dataUserId", dataUserId);
		mav.addObject("articleModellist", allArticleModelList);
		return mav;
	}

	public ModelAndView queryOneListFvoritesAct(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"blog/bloguse/listOneFavoritesArticle");
		String id = request.getParameter("id");
		List<BlogArticle> list = null;
		BlogFamily blogFamily = null;
		if (Strings.isNotBlank(id)) {
			list = blogArticleManager.listArticleByFamilyFavoritesId(Long
					.valueOf(id));
			blogFamily = blogManager.getSingleFamily(Long.valueOf(id));
		}
		String name = "";
		if (blogFamily != null) {
			name = blogFamily.getNameFamily();
		}
		mav.addObject("name", name);
		mav.addObject("articleModelList", getArticleModelList(list));
		return mav;
	}

	// 显示博客所有的收藏文章，按照时间降序排列(xiaoqiuhe)
	public ModelAndView listAllFavoritesArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/bloguse/listFavoritesArticle";

		ModelAndView mav = new ModelAndView(path);
		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);

		List<BlogArticle> BlogArticleList = null;
		List<ArticleModel> allArticleModelList = new ArrayList<ArticleModel>();

		// BlogArticleList = blogArticleManager.listAllFavoritesArticle();
		List<BlogFavorites> bfs = blogArticleManager
				.getFavoriteBlogsOfUserByPage(CurrentUser.get().getId());
		List<FamilyModel> FamilyModelList = getFamilyModelList(BlogConstants.Blog_FAMILY_TYPE2);
		allArticleModelList = this.getArticleModelListByFavorites(bfs,
				FamilyModelList);// this.getArticleModelList(BlogArticleList);

		mav.addObject("articleModellist", allArticleModelList);

		boolean onlyDefault = true;
		if (allArticleModelList != null)
			for (ArticleModel t : allArticleModelList) {
				if (t.getFamilyId().longValue() != BlogConstants.BLOG_DEFAULT_FAVORITE_ID
						.longValue()) {
					onlyDefault = false;
					break;
				}
			}

		mav.addObject("onlyDefault", onlyDefault);
		mav.addObject("FamilyModelList", FamilyModelList);

		return mav;
	}

	// 显示博客所有的收藏文章，按照收藏分类分组排列(xiaoqiuhe)
	public ModelAndView listFavoritesArticleGroupByFamily(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String path = "blog/bloguse/listFavoritesArticleGroupByFamily";
		ModelAndView mav = new ModelAndView(path);

		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);

		List<BlogArticle> FamilyArticleList = null;
		List<ArticleModel> FamilyArticleModelList = null;
		// List<ArticleModel> list2 = null;
		Long familyId = null;

		// Map<Long, List<ArticleModel>> map = null;
		// 收藏分类
		List<BlogFamily> BlogFamilyList = null;
		List<FamilyModel> FamilyModelList = null;
		Iterator<BlogFamily> BlogFamilyIterator = null;
		List<String> FamilyNameList = null; // 主题名称列表

		FamilyNameList = new ArrayList<String>();
		FamilyModelList = new ArrayList<FamilyModel>();
		BlogFamilyList = blogManager
				.listFamily2(BlogConstants.Blog_FAMILY_TYPE2);

		BlogFamilyIterator = BlogFamilyList.iterator();
		// 构造所有分类信息显示列表
		int i = 0;
		while (BlogFamilyIterator.hasNext()) {
			BlogFamily BlogFamily = null;
			FamilyModel FamilyModel = null;

			BlogFamily = getBlogFamily(BlogConstants.Blog_AUTH_TYPE_ADMIN,
					BlogFamilyIterator.next());

			// 构造主题名称列表
			familyId = BlogFamily.getId();
			FamilyNameList.add(BlogFamily.getNameFamily());

			FamilyModel = new FamilyModel();

			FamilyModel.setHasNewPostFlag((byte) 1);
			FamilyModel.setId(familyId);
			FamilyModel.setNameFamily(BlogFamily.getNameFamily());
			FamilyModel.setRemark((BlogFamily.getRemark()));

			// 计算该分类的主题数
			FamilyModel.setArticleNumber(BlogFamily.getArticleNumber());

			FamilyModelList.add(FamilyModel);
			// 列出该分类的所有文章
			FamilyArticleModelList = new ArrayList<ArticleModel>();

			FamilyArticleList = blogArticleManager
					.listAllArticleByFamilyFavoritesId(familyId);

			FamilyArticleModelList = this
					.getArticleModelList(FamilyArticleList);
			i++;
			mav.addObject("familyArticle" + i, FamilyArticleModelList);

		}

		// for(NewsType bt : FamilyModelList){
		// list2.add(map.get(bt.getId()));

		mav.addObject("favoritesFamily", FamilyModelList);
		// mav.addObject("list2",list2);

		return mav;

	}

	// 显示某一收藏分类的所有主题信息(xiaoqiuhe)
	@SuppressWarnings("unchecked")
	public ModelAndView listFamilyFavoritesArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/bloguse/listFamilyArticle";

		ModelAndView mav = new ModelAndView(path);
		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);

		Long FamilyId = null;
		String tem = request.getParameter("id");
		if (tem != null && !tem.equals("")) {
			FamilyId = Long.valueOf(tem);
		} else {
			FamilyId = new Long(0);
		}

		List<BlogArticle> FamilyArticleList = null;
		List<ArticleModel> FamilyArticleModelList = new ArrayList<ArticleModel>();

		if (FamilyId != null) {
			FamilyArticleList = blogArticleManager
					.listArticleByFamilyFavoritesId(Long.valueOf(FamilyId));
		}

		FamilyArticleModelList = this.getArticleModelList(FamilyArticleList);

		mav.addObject("familyArticle", FamilyArticleModelList);

		// 分类信息
		BlogFamily BlogFamily = blogFamilyManager.getFamilyById(FamilyId);

		FamilyModel FamilyModel = new FamilyModel();

		BlogFamily = getBlogFamily(BlogConstants.Blog_AUTH_TYPE_ADMIN,
				BlogFamily);

		// 判断当前用户是否是管理员
		User currentUser = CurrentUser.get();
		Byte isAdminFlag = (blogFamilyManager.validUserIsAdmin(currentUser
				.getId()) ? (byte) 1 : 0);
		FamilyModel.setIsAdminFlag(isAdminFlag);

		mav.addObject("FamilyModel", FamilyModel);

		// 判断当前用户是否有在当前分类发帖的权限
		Byte issueAuthFlag = (blogFamilyManager.validIssueAuth(FamilyId,
				currentUser.getId()) ? (byte) 1 : 0);
		mav.addObject("issueAuthFlag", issueAuthFlag);

		// 论坛分类列表
		List<BlogFamily> FamilyList = null;
		FamilyList = blogManager.listFamily2(BlogConstants.Blog_FAMILY_TYPE1);
		mav.addObject("FamilyList", FamilyList.iterator());

		return mav;
	}

	// 显示某一分类的所有主题信息(xiaoqiuhe)
	@SuppressWarnings("unchecked")
	public ModelAndView listFamilyArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/bloguse/listFamilyArticle";

		ModelAndView mav = new ModelAndView(path);
		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);

		Long FamilyId = null;
		String tem = request.getParameter("id");
		if (tem != null && !tem.equals("")) {
			FamilyId = Long.valueOf(tem);
		} else {
			FamilyId = new Long(0);
		}

		List<BlogArticle> FamilyArticleList = null;
		List<ArticleModel> FamilyArticleModelList = new ArrayList<ArticleModel>();

		if (FamilyId != null && FamilyId != 0) {
			FamilyArticleList = blogArticleManager.listArticleByFamilyId(Long
					.valueOf(FamilyId));
		}

		FamilyArticleModelList = this.getArticleModelList(FamilyArticleList);

		mav.addObject("familyArticle", FamilyArticleModelList);

		// 分类信息
		BlogFamily BlogFamily = blogFamilyManager.getFamilyById(FamilyId);

		FamilyModel FamilyModel = new FamilyModel();

		BlogFamily = getBlogFamily(BlogConstants.Blog_AUTH_TYPE_ADMIN,
				BlogFamily);

		FamilyModel.setNameFamily(BlogFamily.getNameFamily());
		FamilyModel.setId(BlogFamily.getId());

		// 判断当前用户是否是管理员
		User currentUser = CurrentUser.get();
		Byte isAdminFlag = (blogFamilyManager.validUserIsAdmin(currentUser
				.getId()) ? (byte) 1 : 0);
		FamilyModel.setIsAdminFlag(isAdminFlag);

		mav.addObject("FamilyModel", FamilyModel);

		// 判断当前用户是否有在当前分类发帖的权限
		Byte issueAuthFlag = (blogFamilyManager.validIssueAuth(FamilyId,
				currentUser.getId()) ? (byte) 1 : 0);
		mav.addObject("issueAuthFlag", issueAuthFlag);

		// 论坛分类列表
		List<BlogFamily> FamilyList = null;
		FamilyList = blogManager.listFamily2(BlogConstants.Blog_FAMILY_TYPE1);
		mav.addObject("FamilyList", FamilyList.iterator());

		return mav;
	}

	// 显示某一分类的所有主题信息(xiaoqiuhe)
	@SuppressWarnings("unchecked")
	public ModelAndView searchArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/bloguse/listArticle";
		String condition = request.getParameter("condition");
		if (condition.equals("byDate")) {
			path = "blog/admin/listArticle";
		}
		ModelAndView mav = new ModelAndView(path);
		// 记录进入该帖的上一页面

		String userId = request.getParameter("userId");
		if (userId == null || userId.equals("")) {
			userId = "0";
		}
		String field = "";
		if (condition == null) {
			condition = "";
		}
		if (condition.equals("subject") || condition.equals("yearMonth")) {
			field = request.getParameter("textfield");
		} else {
			field = request.getParameter("textfield1");
		}
		if (condition.equals("byDate")) {
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			String day = request.getParameter("day");
			field = year + "-" + month + "-" + day;
		}
		if (field == null)
			field = "";

		String field1 = request.getParameter("textfield2");
		if (field1 == null) {
			field1 = "";
		}

		mav.addObject("field", field);
		mav.addObject("field1", field1);
		List<BlogArticle> BlogArticleList = null;
		List<ArticleModel> allArticleModelList = new ArrayList<ArticleModel>();
		if (condition != null && !condition.equals("")) {
			BlogArticleList = blogArticleManager.queryByCondition(Long
					.parseLong(userId), condition, field, field1);
		}
		allArticleModelList = this.getArticleModelList(BlogArticleList);

		mav.addObject("articleModellist", allArticleModelList);
		User currentUser = CurrentUser.get();
		Long currentUserId = currentUser.getId();
		if (userId == null || userId.trim().equals("")
				|| userId.trim().equals("0")) {
			// 得到个人信息
			mav = setEmployeeInfo(mav, currentUserId);
		} else {
			mav = setEmployeeInfo(mav, Long.parseLong(userId));
			StaffInfo staff = staffInfoManager.getStaffInfoById(Long.parseLong(userId));
			mav.addObject("staff", staff);
			if (null != staff) {
				if (Strings.isNotBlank(staff.getSelf_image_name())) {
					if(staff.getSelf_image_name().startsWith("fileId")){
						mav.addObject("image", "image1");
					}else{
						mav.addObject("image", "image2");
					}
				} 
	 		}
		}
		Byte flagAdmin = 0;
		if (userId == null || userId.trim().equals("")) {
			flagAdmin = 1;
		} else if (Long.parseLong(userId) == currentUserId) {
			flagAdmin = 1;
		}
		// 验证是否已经开通博客
		if (flagAdmin == 1) {
			BlogEmployee blogEmployee = blogManager.getEmployeeById(currentUser
					.getId());
			if (blogEmployee.getFlagStart() == null
					|| blogEmployee.getFlagStart() != 1) {
				flagAdmin = 0;
			}
		}
		mav.addObject("flagAdmin", flagAdmin);
		String searchFlag = request.getParameter("searchFlag");

		boolean boolSearch = (searchFlag != null && "true".equals(searchFlag));

		if (!boolSearch) {
			mav.addObject("condition", "");
		} else {
			mav.addObject("condition", condition);
		}

		return mav;
	}

	/**
	 * 得到Article的数据进行更新保存
	 */
	public ModelAndView saveArticleAfterUpdate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long articleId = Long.valueOf(request.getParameter("articleId"));
		BlogArticle article = this.blogArticleManager.getArticleById(articleId);
/*		List<Attachment> attachments = new ArrayList<Attachment>();//原有附件的集合
		List<V3XFile> files = new ArrayList<V3XFile>();//对应的文件的集合
*/		PrintWriter out = null;
		try {
			out = response.getWriter();

			String tem = "";
			tem = request.getParameter("state");
			if (tem == null) {
				// 标记为私有
				article.setState((byte) 1);
			} else {
				// 标记为共享
				article.setState((byte) 0);
			}

			tem = request.getParameter("subject");
			article.setSubject(tem);

			tem = request.getParameter("content");
			article.setContent(tem);
			//article.setArticleSize(Long.valueOf(String.valueOf(article.getContent().length())));
			tem = request.getParameter("familyId");
			User issueUser = CurrentUser.get();
			Long userid = issueUser.getId();
			long famiyid = 0;
			if (tem == null || tem.equals("")) {
				try {
					famiyid = blogManager.getDefaultFamilyID(userid, BlogConstants.Blog_FAMILY_TYPE1);
				} catch (Exception e) {
					log.error("博客类别：", e);
				}
			} else {
				famiyid = Long.parseLong(tem);
			}
			article.setFamilyId(famiyid);

			article.setModifyTime(new java.sql.Timestamp(new java.util.Date()
					.getTime()));
			String date = Datetimes.formatDate(new Date());
			tem = date.substring(0, 4);
			article.setY(Integer.parseInt(tem));
			tem = date.substring(5, 7);
			article.setM(Integer.parseInt(tem));

			this.appLogManager.insertLog(issueUser, AppLogAction.Blog_Update,
					issueUser.getName(), article.getSubject());// 插入应用日志
/*			*//**		说明（1）、
			 * 在保存新的附件之前，原有附件的大小将暂时不计算在内
			 * 是为了给新的附件腾出空间
			 * 如果新的附件保存成功，那么将物理删除原有附件
			 * 但是如果保存新的附件出现异常，就会回滚此步，即恢复原有的博客大小和附件状态
			 * 总之，只有保存的每一步都无异常的，最后才实际会操作数据库
			 *//*
			
			//判断博客是否有附件，如果有附件的话，将执行说明（1）
			
			if(article.getAttachmentFlag() == 1){
				attachments = attachmentManager.getByReference(article.getId());//拿到原有附件		
				for(Attachment a : attachments){
					files.add(this.fileManager.getV3XFile(a.getFileUrl()));
				}			
			}*/
			//提前更新空间容量，控制异常
			
			this.docSpaceManager.deleteBlogSpaceSize(article.getEmployeeId(), 0 - article.getArticleSize());
			// 保存附件
			String attaFlag = attachmentManager.update(ApplicationCategoryEnum.blog, article.getId(),article.getId(), request);
			if (com.seeyon.v3x.common.filemanager.Constants
					.isUploadLocaleFile(attaFlag)) {
				article.setAttachmentFlag((byte) 1);
			} else {
				article.setAttachmentFlag((byte) 0);
			}
			// 计算文章并更新所占空间			
			
			// 附件的长度
			long size = article.getContent().getBytes().length;
			List<Attachment> atts = attachmentManager.getByReference(article
					.getId());
			for (Attachment att : atts) {
				size += att.getSize();//按照新的附件大小重新计算博客的大小
			}
			
			docSpaceManager.addBlogSpaceSize(issueUser.getId(), size);//更新博客大小
			
			article.setArticleSize(size);
			
			blogArticleManager.updateArticle(article);//最终才会对数据库进行操作

		} catch (Exception e) {
/*			// 物理删除新的附件
			if(attachments.size() > 0){
				List<Attachment> atts = attachmentManager.getByReference(article.getId());
				
				for (Attachment att : atts) {
					for(Attachment a : attachments){
						*//**
						 * 当是新的附件，但不是原附件时，删除这个新的附件，
						 * 即删除对于原博客来说是新添加的附件
						 *//*
						if(att.getId() != a.getId()){
							this.attachmentManager.deleteById(att.getId());
							this.fileManager.deleteFile(att.getFileUrl(), true);
						}
					}
					
				}
				
				this.attachmentManager.save(attachments);//重新加载原附件
				for(V3XFile file : files){
					this.fileManager.save(file);
				}
				try{
//					重新计算容量
					this.docSpaceManager.addBlogSpaceSize(CurrentUser.get().getId(), article.getArticleSize());
				}catch(Exception ec){
					
					log.error("增加博客文章时，更新空间大小: ", ec);

					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('BlogLang.blog_create_space_no_enough'));");
					out.println("</script>");
					
					return null;
				}

			}*/
			

			log.error("增加博客文章时，更新空间大小: ", e);

			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('BlogLang.blog_create_space_no_enough'));");
			out.println("</script>");
			
			return null;
		}

		String familyId = request.getParameter("familyId");
		String url = "blog.do?method=showPostIframe&flag=open&resourceMethod=blogHome&articleId="
						+ articleId +"&familyId=" + familyId + "&where=other";
		

		return super.redirectModelAndView(url);
	}

	// 发布文章(xiaoqiuhe)
	public ModelAndView createArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BlogArticle BlogArticle = new BlogArticle();
		PrintWriter out = null;
		try {
			out = response.getWriter();
			BlogArticle.setIdIfNew();
			String tem = "";
			tem = request.getParameter("state");
			if (tem == null) {
				// 标记为私有
				BlogArticle.setState((byte) 1);
			} else {
				// 标记为共享
				BlogArticle.setState((byte) 0);
			}

			tem = request.getParameter("subject");
			BlogArticle.setSubject(tem);
			tem = request.getParameter("content");
			BlogArticle.setContent(tem);
			tem = request.getParameter("familyId");
			User issueUser = CurrentUser.get();
			Long userid = issueUser.getId();
			long famiyid = 0;
			if (tem == null || tem.equals("")) {
				famiyid = blogManager.getDefaultFamilyID(userid,
						BlogConstants.Blog_FAMILY_TYPE1);
			} else {
				famiyid = Long.parseLong(tem);
			}
			BlogArticle.setFamilyId(famiyid);
			BlogArticle.setClickNumber(new Integer(0));
			BlogArticle.setReplyNumber(new Integer(0));

			BlogArticle.setEmployeeId(userid);
			BlogArticle.setIdCompany(issueUser.getAccountId());

			BlogArticle.setIssueTime(new java.sql.Timestamp(
					new java.util.Date().getTime()));
			BlogArticle.setModifyTime(new java.sql.Timestamp(
					new java.util.Date().getTime()));
			String date = Datetimes.formatDate(new Date());
			tem = date.substring(0, 4);
			BlogArticle.setY(Integer.parseInt(tem));
			tem = date.substring(5, 7);
			BlogArticle.setM(Integer.parseInt(tem));

			blogArticleManager.createArticle(BlogArticle);
			this.appLogManager.insertLog(issueUser, AppLogAction.Blog_New,
					issueUser.getName(), BlogArticle.getSubject());// 插入应用日志

			// 保存附件
			String attaFlag = attachmentManager.create(
					ApplicationCategoryEnum.blog, BlogArticle.getId(),
					BlogArticle.getId(), request);
			if (com.seeyon.v3x.common.filemanager.Constants
					.isUploadLocaleFile(attaFlag)) {
				BlogArticle.setAttachmentFlag((byte) 1);
			} else {
				BlogArticle.setAttachmentFlag((byte) 0);
			}

			// 更新员工文章数,文章数加一
			blogManager.updateArticleNumber(issueUser.getId(), 1);
			// 更新分类文章数,文章数加一
			blogManager.updateFamilyArticleNumber(famiyid, 1);
			// 计算文章并更新所占空间
			long size = 0L;
			// 文章内容的长度
			DocBody docBody = new DocBody();
			Date bodyCreateDate = Datetimes.parseDatetime(new java.util.Date()
					.toString());
			if (bodyCreateDate != null) {
				docBody.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
			}
			bind(request, docBody);
			size = docBody.getContent().getBytes().length;
			// 附件的长度
			List<Attachment> atts = attachmentManager
					.getByReference(BlogArticle.getId());
			for (Attachment att : atts) {
				size += att.getSize();
			}
			BlogArticle.setArticleSize(size);

			docSpaceManager.addBlogSpaceSize(issueUser.getId(), size);

		} catch (Exception e) {
			// 删除附件
			List<Attachment> atts = attachmentManager
					.getByReference(BlogArticle.getId());
			for (Attachment att : atts) {
				attachmentManager.deleteById(att.getId());
				fileManager.deleteFile(att.getFileUrl(), true);
			}

			blogArticleManager.deleteArticle(BlogArticle.getId());

			log.error("增加博客文章时，更新空间大小: ", e);

			out.println("<script>");
			out
					.println("alert(parent.v3x.getMessage('BlogLang.blog_create_space_no_enough'));");

			out.println("</script>");
			
			return null;
		}
		out.println("<script>");
		out
				.println("parent.location.href = parent.genericURL + '?method=blogHome';");
		out.println("</script>");

		return null;
	}

	/**
	 * 
	 */
	public ModelAndView showPostIframe(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView("blog/bloguse/showPostIframe");

		Long articleId = Long.parseLong(request.getParameter("articleId"));

		BlogArticle article = blogArticleManager.getArticleById(articleId);

		if (article == null) {
			ret.addObject("dataExist", false);

			return ret;
		} else
			ret.addObject("dataExist", true);

		blogArticleManager.updateClickNumber(articleId);
		String flag = request.getParameter("flag");
		ret.addObject("flag", flag);
		return ret;
	}

	//

	public ModelAndView showPostPro(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView ret = new ModelAndView("blog/bloguse/showPostPro");
		String from = request.getParameter("from");
		ret.addObject("from", from);

		Long articleId = Long.parseLong(request.getParameter("articleId"));

		BlogArticle article = blogArticleManager.getArticleById(articleId);

		if (article == null) {
			ret.addObject("dataExist", false);

			return ret;
		} else
			ret.addObject("dataExist", true);

		blogArticleManager.updateClickNumber(articleId);

		return ret;

	}

	// 查看帖(xiaoqiuhe)
	public ModelAndView showPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Boolean isFromBlog = false;
		String _isFromBlog = request.getParameter("isFromBlog");
		String from = request.getParameter("from");
		String fromFlag = request.getParameter("fromFlag");
		String where = request.getParameter("where");

		if ("true".equals(_isFromBlog)) {
			isFromBlog = true;
		}

		String path = "";
		if (from != null) {
			path = "blog/bloguse/showPostFrom";
		} else {
			path = "blog/bloguse/showPost";
		}
		ModelAndView mav = new ModelAndView(path);
		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);
		String familyId = request.getParameter("familyId");
		if (familyId == null || familyId.equals("")) {
			familyId = request.getParameter("theFamilyId");
		}
		mav.addObject("familyId", familyId);
		if (from != null) {
			mav.addObject("from", from);
		}
		if (fromFlag != null) {
			mav.addObject("fromFlag", fromFlag);
		}

		// 文章信息------------
		Long articleId = Long.parseLong(request.getParameter("articleId"));
		// 更新该主题的点击数：点击数加一

		BlogArticle article = blogArticleManager.getArticleById(articleId);

		if (article == null) {
			mav.addObject("dataExist", false);
			return mav;

		} else
			mav.addObject("dataExist", true);

		String subject = article.getSubject();

		mav.addObject("article", article);
		mav.addObject("name", subject);
		User currentUser = CurrentUser.get();
		Long userId = currentUser.getId();
		// 判断当前用户是否有回复权限
		boolean canReply = blogFamilyManager.validReplyAuth(userId);

		// 0为禁止回复
		Byte canReplyFlag = 0;
		if (canReply) {
			// 1为可以回复
			canReplyFlag = 1;
		}
		mav.addObject("canReplyFlag", canReplyFlag);

		// --------------------判断删除权限------------------
		// 判断当前用户是否是管理员(“删除”总是可见）
		boolean isAdmin = blogFamilyManager.validUserIsAdmin(userId);
		mav.addObject("isAdmin", isAdmin);
		// 自己的博客可以删除任何人的回复/评论
		if (article.getEmployeeId().longValue() == userId.longValue()) {
			isAdmin = true;
		}
		// 判断用户是否可以进行"删除该主题“操作
		Byte canDeleteArticleFlag = 0;
		
		// 判断用户是否可以进行"编辑该主题“操作
		Byte canEditeArticleFlag = 0;
		if(currentUser.getId() == article.getEmployeeId()) {
			canEditeArticleFlag = (byte) 1;
			canDeleteArticleFlag = (byte) 1;
		}
		// 当前用户是管理员(“删除”总是可见）
		if (isAdmin && "admin".equals(where)) {
			canDeleteArticleFlag = (byte) 1;
		}// 当前用户是普通用户（不可以删除其他人的主题帖）
		else {
			if (!((userId != article.getEmployeeId()))) {
				canDeleteArticleFlag = (byte) 0;
			}
		}

		// --------------------------------------
		mav.addObject("canDeleteArticleFlag", canDeleteArticleFlag);
		mav.addObject("canEditeArticleFlag", canEditeArticleFlag);

		// 判断是否可以对当前博客进行共享操作

		Byte canShareFlag = 0;
		if (article.getEmployeeId().longValue() == userId.longValue()) {
			canShareFlag = (byte) 1;

		}

		mav.addObject("canShareFlag", canShareFlag);
		// ----------------------------------------------
		// 检查收藏按钮是否可见
		Byte canFavoritesFlag = 1;
		Byte adminFlag = 0;
		// if (isAdmin || blogManager.checkFavorites(articleId, userId)>0){
		if (blogManager.checkFavorites(articleId, userId) > 0) {
			canFavoritesFlag = 0;
		}
		if (isAdmin == true && userId == article.getEmployeeId())
			adminFlag = 1;
		mav.addObject("adminFlag", adminFlag);

		mav.addObject("canFavoritesFlag", canFavoritesFlag);
		// 附件
		mav.addObject("attachments", attachmentManager
				.getByReference(articleId));

		// 设置发布者名称
		mav.addObject("replyUserId", currentUser.getId());
		V3xOrgMember v3xOrgMember = orgManager.getMemberById(article
				.getEmployeeId());
		if (v3xOrgMember != null) {
			mav.addObject("issueUserName", v3xOrgMember.getName());
		} else {

			mav.addObject("issueUserName", "");
		}

		// 文章信息-----结束-------
		// 回复帖信息
		Long replyId = null;
		List<BlogReply> replyList = blogArticleManager
				.listReplyByArticleId(articleId);
		List<ArticleReplyModel> replyModelList = new ArrayList<ArticleReplyModel>();
		List<BlogReply> refPostreplyList = null;
		List<ArticleReplyModel> refPostReplyModelList = null;
		if (replyList != null) {
			for (BlogReply articleReply : replyList) {
				ArticleReplyModel replyModel = new ArticleReplyModel();

				replyId = articleReply.getId();
				replyModel.setId(replyId);
				replyModel.setContent(articleReply.getContent());
				replyModel.setSuject(articleReply.getSubject());
				replyModel.setIssueTime(articleReply.getIssueTime());
				replyModel.setBlogArticle(articleReply.getBlogArticle());
				replyModel.setUseReplyFlag((byte) 0);
				v3xOrgMember = orgManager.getMemberById(articleReply
						.getEmployeeId());
				if (v3xOrgMember != null) {
					replyModel.setReplyUserName(v3xOrgMember.getName());
				} else {
					replyModel.setReplyUserName("");
				}
				// 检查是否有对此评论的回复
				refPostreplyList = blogArticleManager
						.listReplyByParentId(replyId);
				if (refPostreplyList != null) {
					replyModel.setUseReplyFlag((byte) 3);
				}
				// 设置附件
				replyModel.setAttachment(attachmentManager
						.getByReference(articleReply.getId()));

				// 判断用户是否可以进行"删除该回复帖“操作
				Byte canDeleteReplyPostFlag = 0;
				// 当前用户是管理员(“删除”总是可见）
				if (isAdmin) {
					canDeleteReplyPostFlag = (byte) 1;
				}
				// 当前用户是普通用户（不可以删除其他人的回复帖）
				else {
					canDeleteReplyPostFlag = (byte) 0;
					// 也不可以删除自己的回复帖
					// if (!((currentUser.getId() !=
					// articleReply.getEmployeeId()))) {
					// canDeleteReplyPostFlag = (byte) 1;
					// }
				}
				replyModel.setCanBeDeleteFlag(canDeleteReplyPostFlag);

				replyModelList.add(replyModel);
			}
			// 得到所有评论的回复
			refPostreplyList = blogArticleManager
					.listReplyHaveParentId(articleId);
			refPostReplyModelList = new ArrayList<ArticleReplyModel>();
			if (refPostreplyList != null) {
				for (BlogReply refPostArticleReply : refPostreplyList) {
					ArticleReplyModel refPostreplyModel = new ArticleReplyModel();
					// System.out.println("showPost
					// refPostreplyId=="+refPostArticleReply.getId());
					refPostreplyModel.setId(refPostArticleReply.getId());
					refPostreplyModel.setParentId(refPostArticleReply
							.getParentId());
					refPostreplyModel.setContent(refPostArticleReply
							.getContent());
					refPostreplyModel.setSuject(refPostArticleReply
							.getSubject());
					refPostreplyModel.setIssueTime(refPostArticleReply
							.getIssueTime());
					refPostreplyModel.setBlogArticle(refPostArticleReply
							.getBlogArticle());
					refPostreplyModel.setUseReplyFlag((byte) 0);
					v3xOrgMember = orgManager.getMemberById(refPostArticleReply
							.getEmployeeId());
					if (v3xOrgMember != null) {
						refPostreplyModel.setReplyUserName(v3xOrgMember
								.getName());
					} else {
						refPostreplyModel.setReplyUserName("");
					}
					// 设置附件
					refPostreplyModel.setAttachment(attachmentManager
							.getByReference(refPostArticleReply.getId()));
					refPostReplyModelList.add(refPostreplyModel);
				}
				// 循环标记不同的回复
				mav.addObject("refPostReplyModelList", refPostReplyModelList);
			}
		}

		mav.addObject("replyModelList", replyModelList);

		return mav;
	}

	// 管理员查看帖(xiaoqiuhe)
	public ModelAndView showPostAdmin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/admin/showPost";
		ModelAndView mav = new ModelAndView(path);

		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);
		String familyId = request.getParameter("familyId");
		if (familyId == null || familyId.equals("")) {
			familyId = request.getParameter("theFamilyId");
		}
		mav.addObject("familyId", familyId);
		// 文章信息------------
		Long articleId = Long.parseLong(request.getParameter("articleId"));

		BlogArticle article = blogArticleManager.getArticleById(articleId);
		mav.addObject("article", article);

		User currentUser = CurrentUser.get();

		mav.addObject("canReplyFlag", (byte) 1);
		mav.addObject("canDeleteArticleFlag", (byte) 1);

		// 附件
		mav.addObject("attachments", attachmentManager
				.getByReference(articleId));

		// 设置发布者名称
		mav.addObject("replyUserId", currentUser.getId());
		V3xOrgMember v3xOrgMember = orgManager.getMemberById(article
				.getEmployeeId());
		if (v3xOrgMember != null) {
			mav.addObject("issueUserName", v3xOrgMember.getName());
		} else {
			mav.addObject("issueUserName", "");
		}
		// 文章信息-----结束-------
		// 回复帖信息
		Long replyId = null;
		List<BlogReply> replyList = blogArticleManager
				.listReplyByArticleId(articleId);
		List<ArticleReplyModel> replyModelList = new ArrayList<ArticleReplyModel>();
		List<BlogReply> refPostreplyList = null;
		List<ArticleReplyModel> refPostReplyModelList = null;
		if (replyList != null) {
			for (BlogReply articleReply : replyList) {
				ArticleReplyModel replyModel = new ArticleReplyModel();

				replyId = articleReply.getId();
				replyModel.setId(replyId);
				replyModel.setContent(articleReply.getContent());
				replyModel.setSuject(articleReply.getSubject());
				replyModel.setIssueTime(articleReply.getIssueTime());
				replyModel.setBlogArticle(articleReply.getBlogArticle());
				replyModel.setUseReplyFlag((byte) 0);
				v3xOrgMember = orgManager.getMemberById(articleReply
						.getEmployeeId());
				if (v3xOrgMember != null) {
					replyModel.setReplyUserName(v3xOrgMember.getName());
				} else {
					replyModel.setReplyUserName("");
				}
				// 检查是否有对此评论的回复
				refPostreplyList = blogArticleManager
						.listReplyByParentId(replyId);
				if (refPostreplyList != null) {
					replyModel.setUseReplyFlag((byte) 3);
				}
				// 设置附件
				replyModel.setAttachment(attachmentManager
						.getByReference(articleReply.getId()));
				replyModel.setCanBeDeleteFlag((byte) 1);

				replyModelList.add(replyModel);
			}
			// 得到所有评论的回复
			refPostreplyList = blogArticleManager
					.listReplyHaveParentId(articleId);
			refPostReplyModelList = new ArrayList<ArticleReplyModel>();
			if (refPostreplyList != null) {
				for (BlogReply refPostArticleReply : refPostreplyList) {
					ArticleReplyModel refPostreplyModel = new ArticleReplyModel();
					refPostreplyModel.setId(refPostArticleReply.getId());
					refPostreplyModel.setParentId(refPostArticleReply
							.getParentId());
					refPostreplyModel.setContent(refPostArticleReply
							.getContent());
					refPostreplyModel.setSuject(refPostArticleReply
							.getSubject());
					refPostreplyModel.setIssueTime(refPostArticleReply
							.getIssueTime());
					refPostreplyModel.setBlogArticle(refPostArticleReply
							.getBlogArticle());
					refPostreplyModel.setUseReplyFlag((byte) 0);
					v3xOrgMember = orgManager.getMemberById(refPostArticleReply
							.getEmployeeId());
					if (v3xOrgMember != null) {
						refPostreplyModel.setReplyUserName(v3xOrgMember
								.getName());
					} else {
						refPostreplyModel.setReplyUserName("");
					}
					// 设置附件
					refPostreplyModel.setAttachment(attachmentManager
							.getByReference(refPostArticleReply.getId()));
					refPostReplyModelList.add(refPostreplyModel);
				}
				// 循环标记不同的回复
				mav.addObject("refPostReplyModelList", refPostReplyModelList);
			}
		}

		mav.addObject("replyModelList", replyModelList);

		return mav;
	}

	// 初始化回复帖(xiaoqiuhe)
	public ModelAndView replyArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/replyArticle");

		// 记录进入该帖的上一业面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);

		Long articleId = Long.parseLong(request.getParameter("articleId"));
		BlogArticle article = blogArticleManager.getArticleById(articleId);

		mav.addObject("article", article);

		// 当前用户信息
		User currentUser = CurrentUser.get();
		mav.addObject("replyUserId", currentUser.getId());
		mav.addObject("currentUserName", currentUser.getName());

		int useReplyFlag = Integer.parseInt(request
				.getParameter("useReplyFlag"));
		if (request.getParameter("postId") != null) {
			Long postId = Long.valueOf(request.getParameter("postId"));
			if (useReplyFlag == 3) {
				mav.addObject("useReplyId", postId);
			}
		}

		mav.addObject("useReplyFlag", useReplyFlag);

		return mav;
	}

	// 创建回复帖(xiaoqiuhe)
	public ModelAndView createReplyArticle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long articleId = Long.parseLong(request.getParameter("articleId"));

		BlogReply BlogReply = new BlogReply();

		BlogReply.setIdIfNew();
		BlogReply.setSubject(request.getParameter("subject"));
		BlogReply.setIssueTime(new java.sql.Timestamp(new java.util.Date()
				.getTime()));
		// 当前用户信息
		User currentUser = CurrentUser.get();
		BlogReply.setEmployeeId(currentUser.getId());

		BlogArticle article = blogArticleManager.getArticleById(articleId);

		// if (article == null){

		// }

		if (article == null) {
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				log.error("取得response的输出流", e);
			}
			out.println("<script>");
			out
					.println("alert(parent.v3x.getMessage('BlogLang.blog_alert_source_deleted_article'));");
			out.println("window.close();");
			out.println("</script>");
			return null;// super.refreshWorkspace();
		}

		// 回复操作类型
		int useReplyFlag = Integer.parseInt(request
				.getParameter("useReplyFlag"));
		// 快速回复,即评论
		if (useReplyFlag == 0) {
			BlogReply.setContent(Strings
					.toHTML(request.getParameter("content")));
		}
		// 不是引用回复
		if (useReplyFlag == 1) {
			BlogReply.setContent(request.getParameter("content"));
		}
		// 引用主题回复
		else if (useReplyFlag == 2) {
			BlogReply.setContent(request.getParameter("content"));
		}
		// 引用其他回复帖回复
		else if (useReplyFlag == 3) {
			BlogReply.setContent(Strings
					.toHTML(request.getParameter("content")));
			Long postId = Long.valueOf(request.getParameter("useReplyId"));
			boolean exist = false;
			exist = blogArticleManager.checkReply(postId);
			if (exist) {
				BlogReply.setParentId(postId);
			} else {
				PrintWriter out = null;
				try {
					out = response.getWriter();
				} catch (IOException e) {
					log.error("取得response的输出流", e);
				}
				out.println("<script>");
				out
						.println("alert(parent.v3x.getMessage('BlogLang.blog_data_delete_alert'));");
				// out.println("window.close();");
				out.println("</script>");
				return null;// super.refreshWorkspace();

			}
		}
		if (BlogReply.getSubject() == null)
			BlogReply.setSubject("RE:" + article.getSubject());
		BlogReply.setBlogArticle(article);

		blogArticleManager.replyArticle(BlogReply);

		// 创建附件
		this.attachmentManager.create(ApplicationCategoryEnum.blog, BlogReply
				.getId(), BlogReply.getId(), request);

		// 在此更新全文检索
		updateIndexManager.update(articleId, ApplicationCategoryEnum.blog
				.getKey());
		// 更新回复数,回复数加一
		blogArticleManager.updateReplyNumber(articleId, 1);

		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			log.error("取得response的输出流", e);
		}
		out.println("<script>");
		out.println("parent.reloadPage();");
		out.println("</script>");
		return null;// super.refreshWorkspace();
	}

	// 删除评论(xiaoqiuhe)
	public ModelAndView deleteReplyPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String postIdStr = request.getParameter("postId");
		String articleIdStr = request.getParameter("articleId");
		if (postIdStr == null || articleIdStr == null) {
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				log.error("取得response的输出流", e);
			}
			out.println("<script>");
			out
					.println("alert(parent.v3x.getMessage('BlogLang.blog_data_delete_alert'));");
			out.println("window.close();");
			out.println("</script>");
			return null;// super.refreshWorkspace();

		} else {
			Long postId = Long.valueOf(postIdStr);
			Long articleId = Long.valueOf(articleIdStr);

			// 删除评论
			blogArticleManager.deleteReplyPost(postId);
			// 更新回复数,回复数减一
			blogArticleManager.updateReplyNumber(articleId, -1);

			// 得到他人对此评论的回复
			List<BlogReply> refPostreplyList = blogArticleManager
					.listReplyByParentId(postId);
			if (refPostreplyList != null) {
				for (BlogReply refPostArticleReply : refPostreplyList) {
					ArticleReplyModel refPostreplyModel = new ArticleReplyModel();
					// 删除评论
					blogArticleManager.deleteReplyPost(refPostArticleReply
							.getId());
					// 更新回复数,回复数减一
					blogArticleManager.updateReplyNumber(articleId, -1);
				}
			}

			StringBuffer urlStr = new StringBuffer();
			String resourceMethod = request.getParameter("resourceMethod");
			String from = request.getParameter("from");
			String familyId = request.getParameter("familyId");
			String theFamilyId = request.getParameter("theFamilyId");
			if (from != null) {
				urlStr.append("/blog.do?method=showPostPro&from=" + from
						+ "&articleId=");
			} else {
				urlStr.append("/blog.do?method=showPost&articleId=");
			}
			urlStr.append(articleId);
			urlStr.append("&&resourceMethod=");
			urlStr.append(resourceMethod);
			if (familyId != null) {
				urlStr.append("&&familyId=");
				urlStr.append(familyId);
			} else {
				urlStr.append("&&theFamilyId=");
				urlStr.append(theFamilyId);
			}

			return this.redirectModelAndView(urlStr.toString());
		}

		// PrintWriter out = null;
		// out.print("<script>");
		// out.print("window.location.href = window.location.href;");
		// out.print("</script>");
		// return this.showPost(request, response);
	}

	// 列出所有共享信息
	public ModelAndView listAllShare(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/blogmanager/listShare";
		ModelAndView mav = new ModelAndView(path);

		List<BlogShare> BlogShareList = null;
		List<ShareModel> ShareModelList = null;
		ShareModelList = new ArrayList<ShareModel>();
		Iterator<BlogShare> BlogShareIterator = null;

		BlogShareList = blogManager.listShare();
		BlogShareIterator = BlogShareList.iterator();
		Long id = new Long(0);
		// 构造显示列表
		while (BlogShareIterator.hasNext()) {
			BlogShare BlogShare = null;
			ShareModel ShareModel = null;

			BlogShare = blogManager.getSingleShare(BlogShareIterator.next()
					.getId());

			id = BlogShare.getShareId();
			String type = BlogShare.getType();
			ShareModel = new ShareModel();
			ShareModel.setId(BlogShare.getId());
			ShareModel.setType(type);
			ShareModel.setShareId(id);
			if (type != null && type.equals("Member")) {
				V3xOrgMember v3xOrgMember = orgManager.getMemberById(id);
				if (v3xOrgMember != null) {
					ShareModel.setUserName(v3xOrgMember.getName());
				} else {
					ShareModel.setUserName("");
				}
			} else {
				V3xOrgDepartment dept = orgManager.getDepartmentById(id);
				if (dept != null) {
					ShareModel.setUserName(dept.getName());
				} else {
					ShareModel.setUserName("");
				}
			}
			ShareModelList.add(ShareModel);
		}

		mav.addObject("ShareModelList", ShareModelList);

		return mav;
	}

	// 列出他人全部共享信息
	public ModelAndView listAllShareOther(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/chooseAttention");
		// 列出关注信息
		String from = request.getParameter("from");
		List<AttentionModel> AttentionModelList = null;
		AttentionModelList = listAllAttention(request, response);
		mav.addObject("confrerelist", AttentionModelList);
		mav.addObject("from", from);

		return mav;
	}

	// 列出我关注的共享信息(xiaoqiuhe,暂不提供博客共享功能)
	public ModelAndView listShareOtherIndex(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/bloguse/listShareOther";
		String from = request.getParameter("from");
		ModelAndView mav = new ModelAndView(path);

		// 列出关注信息
		List<AttentionModel> AttentionModelList = null;
		AttentionModelList = listAllAttention(request, response);

		// 处理介绍
		// 没有自我介绍的时候，显示最新文章的标题
		for (AttentionModel tam : AttentionModelList) {
			StaffInfo staff = staffInfoManager.getStaffInfoById(tam.getAttentionId());
			if(null != staff){
				if (Strings.isNotBlank(staff.getSelf_image_name())) {
					if(staff.getSelf_image_name().startsWith("fileId")){
						tam.setImageType("image1");
					}else{
						tam.setImageType("image2");
					}
				} 
				tam.setSelf_image_name(staff.getSelf_image_name());
			}
			// int shareTotal =
			// blogArticleManager.getTotalOfUserShare(tam.getEmployeeId());
			// tam.setArticleNumber(shareTotal);
			if (Strings.isBlank(tam.getIntroduce())) {
				BlogArticle ba = blogArticleManager.getLatestSharedArticle(tam
						.getAttentionId());
				if (ba != null) {
					tam.setIntroduce(ba.getSubject());
				}
			}
		}

		mav.addObject("AttentionModelList", AttentionModelList);
		mav.addObject("from", from);

		return mav;
	}

	// 列出关注信息(xiaoqiuhe)
	public List<AttentionModel> listAllAttention(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<BlogAttention> blogAttentionList = null;
		blogAttentionList = blogManager.listAttention2();
		return blogManager.getAttentionModelList(blogAttentionList);
	}

	// 列出所有员工信息
	public ModelAndView listAllEmployee(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/admin/listMember";
		ModelAndView mav = new ModelAndView(path);

		List<BlogEmployee> BlogEmployeeList = null;
		List<EmployeeModel> EmployeeModelList = null;
		EmployeeModelList = new ArrayList<EmployeeModel>();
		Iterator<BlogEmployee> BlogEmployeeIterator = null;

		BlogEmployeeList = blogManager.listEmployee();
		BlogEmployeeIterator = BlogEmployeeList.iterator();
		Long id = new Long(0);
		// 构造显示列表
		while (BlogEmployeeIterator.hasNext()) {
			BlogEmployee BlogEmployee = null;
			EmployeeModel EmployeeModel = null;

			BlogEmployee = blogManager.getEmployeeById(BlogEmployeeIterator
					.next().getId());
			id = BlogEmployee.getId();

			EmployeeModel = new EmployeeModel();
			EmployeeModel.setId(id);
			EmployeeModel.setIntroduce(BlogEmployee.getIntroduce());
			EmployeeModel.setArticleNumber(BlogEmployee.getArticleNumber());
			EmployeeModel.setImage(BlogEmployee.getImage());
			EmployeeModel.setIdCompany(BlogEmployee.getIdCompany());
			EmployeeModel.setFlagStart(BlogEmployee.getFlagStart());
			EmployeeModel.setFlagShare(BlogEmployee.getFlagShare());

			V3xOrgMember v3xOrgMember = orgManager.getMemberById(id);
			if (v3xOrgMember != null) {
				EmployeeModel.setUserName(v3xOrgMember.getName());
			}
			EmployeeModelList.add(EmployeeModel);
		}

		mav.addObject("EmployeeModelList", EmployeeModelList);

		return mav;
	}

	// 删除分类信息(xiaoqiuhe)
	public ModelAndView delShare(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] blogShareIdArray = request.getParameter("id").split(",");
		if (blogShareIdArray != null) {
			for (String id : blogShareIdArray) {
				try {
					blogManager.deleteShare(new Long(id));
				} catch (org.springframework.dao.DataIntegrityViolationException me) {
					log.error(me.getMessage(), me);
				}
			}
		}
		ModelAndView mav = listAllShare(request, response);
		return mav;
	}

	/**
	 * 提供方法，在删除员工信息是，删除博客员工信息
	 * 
	 * @param request
	 * @param response
	 * @return void
	 * @throws Exception
	 */
	public void deleteEmployee(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String id = request.getParameter("id");
		blogManager.deleteEmployee(new Long(id));
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	/**
	 * 进入组织模型的上下结构部分
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView organizationFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("blog/admin/adminFrame");
		return result;
	}

	// 进入个人信息修改页面
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView getEmployeeModifyAdmin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView = new ModelAndView("blog/admin/modifyMember");
		String id = request.getParameter("id");
		String deptId = request.getParameter("deptId");
		String dbClick = request.getParameter("dbClick");

		BlogEmployee BlogEmployee = blogManager.getEmployeeById(Long
				.parseLong(id));
		modelView.addObject("blogEmployee", BlogEmployee);
		V3xOrgMember v3xOrgMember = orgManager
				.getMemberById(Long.parseLong(id));
		if (v3xOrgMember != null) {
			modelView.addObject("userName", v3xOrgMember.getName());

			List<V3xOrgMember> lista = new ArrayList<V3xOrgMember>();
			lista.add(v3xOrgMember);
			List<EmployeeModel> models = getEmployeeModelList(lista, true);

			if (models != null && models.size() > 0) {
				EmployeeModel model = models.get(0);
				modelView.addObject("model", model);

			}
		}

		modelView.addObject("blogStatus", v3xOrgMember.getName());
		modelView.addObject("deptId", deptId);
		DocStorageSpace docSpace = null;

		// 得到博客空间
		docSpace = docSpaceManager.getDocSpaceByUserId(Long.parseLong(id));
		// long temp=new Integer(1024*1024).longValue();
		// long blogSize = docSpace.getBlogSpace()/temp;
		// long blogUsedSize = docSpace.getBlogUsedSpace();

		long totalLong = docSpace.getBlogSpace();
		long usedLong = docSpace.getBlogUsedSpace();

		long totalLongByM = totalLong / (1024 * 1024);

		String total = Strings.formatFileSize(totalLong, true);
		String used = Strings.formatFileSize(usedLong, true);
		if (usedLong == 0L)
			used = "0 KB";

		modelView.addObject("blogTotal", total);
		modelView.addObject("blogUsedSpace", used);
		modelView.addObject("totalLong", totalLong);
		modelView.addObject("usedLong", usedLong);
		modelView.addObject("totalLongByM", totalLongByM);
		modelView.addObject("dbClick", dbClick);

		// EmployeeModel employeeModel=null;

		return modelView;
	}

	// 进入个人信息修改页面
	public ModelAndView getEmployeeModify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView = new ModelAndView("blog/bloguse/modifyMember");
		User user = CurrentUser.get();
		Long id = user.getId();

		BlogEmployee BlogEmployee = blogManager.getEmployeeById(id);
		modelView.addObject("blogEmployee", BlogEmployee);
		V3xOrgMember v3xOrgMember = orgManager.getMemberById(id);
		if (v3xOrgMember != null) {
			modelView.addObject("userName", v3xOrgMember.getName());
		} else {
			modelView.addObject("userName", "");
		}
		return modelView;
	}

	// 得到默认的博客空间
	public long getDefaultBlogSpaceSize() {
		return BlogConstants.Blog_SPACE_SIZE_DEFAULT;
	}

	public long getBlogSpaceSize(DocStorageSpace docspace) {
		long size = docspace.getBlogSpace();
		return size / 1024 / 1024;
	}

	// 批量开通和关闭个人博客

	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView modifyEmployeeBatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView = new ModelAndView("blog/admin/adminFrame");

		String flag = request.getParameter("flag");
		String flagStart = "0";
		if (flag.equals("start")) {
			flagStart = "1";
		}
		String[] employeeIdArray = request.getParameter("id").split(",");
		String choseDep = request.getParameter("choseDep");// 单位的ID
		String deptIds = request.getParameter("deptId");
		if (null == choseDep) {
			choseDep = "false";
		}

		Long employeeId = null;
		Long deptId = null;
		BlogEmployee blogEmployee = null;

		// 批量开通博客，取得博客空间状态
		for (int i = 0; i < employeeIdArray.length; i++) {
			String Idr = employeeIdArray[i];
			DocStorageSpace docspace = null;
			docspace = docSpaceManager.getDocSpaceByUserId(Long.parseLong(Idr));
			if (docspace.getBlogStatus() == Constants.SPACE_FREE) {

				docSpaceManager.assignBlogSpace(Long.parseLong(Idr),
						getBlogSpaceSize(docspace));
			}
		}

		long size = 0L;
		// 批量开通博客，取得默认空间大小
		size = this.getDefaultBlogSpaceSize();
		DocStorageSpace docStorageSpace = null;
		
		StringBuffer sb = new StringBuffer();
		if (employeeIdArray != null) {
			for (String employeeInfo : employeeIdArray) {
				employeeId = Long.parseLong(employeeInfo);
				blogEmployee = blogManager.getEmployeeById(employeeId);
				blogEmployee.setFlagStart(Byte.parseByte(flagStart));
				// blogEmployee.se
				blogManager.modifyEmployee(blogEmployee);
				// 开通博客

				if (flagStart != null && flagStart.equals("1")) {
					// 初始化分类
					this.initFamily(employeeId);
					// 检查博客是否开通
					docStorageSpace = docSpaceManager
							.getDocSpaceByUserId(employeeId);
					// 如果没有开通
					if (docStorageSpace.getBlogStatus() == Constants.SPACE_NOT_ASSIGNED) {
						// 初始化博客空间
						// DocSpaceVO vo = new DocSpaceVO(docStorageSpace);
						// docSpaceManager.assignBlogSpace(employeeId,
						// Long.parseLong(vo.getTotal()));
						docSpaceManager.assignBlogSpace(employeeId, size);
					}
				}
				if(sb.length() != 0){
					sb.append(",");
				}
				V3xOrgMember member = orgManager.getMemberById(employeeId);
				sb.append(member.getName());
			}
		}
		User user = CurrentUser.get();
		V3xOrgAccount account = orgManager.getAccountById(user.getAccountId());
		if("1".equals(flagStart)){
			appLogManager.insertLog(user, AppLogAction.BlogStateModify_Start,account.getName(),sb.toString());
		}else{
			appLogManager.insertLog(user, AppLogAction.BlogStateModify_Stop,account.getName(),sb.toString());
		}
		
		if( null != choseDep && "true".equals(choseDep)){
			if (employeeId!=null){
				V3xOrgMember v3xOrgMember = orgManager.getMemberById(employeeId);
				deptId = v3xOrgMember.getOrgDepartmentId();
			}
			modelView.addObject("deptId",deptId);	
			super.rendJavaScript(response, "parent.parent.detailFrame.location.href = '/seeyon/blog.do?method=initListAdmin&deptId="+deptId+"'");
			return null ;
		}else{
			modelView.addObject("deptId",deptIds);			 
			super.rendJavaScript(response, "parent.parent.detailFrame.location.href = '/seeyon/blog.do?method=listAccountMembersAdmin'");
			return null ;
		}

	}

	// 个人信息修改(admin)
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView modifyEmployeeAdmin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView = new ModelAndView("blog/admin/adminFrame");

		String id = request.getParameter("id");
		String deptId = request.getParameter("deptId");
		BlogEmployee BlogEmployee = blogManager.getEmployeeById(Long
				.parseLong(id));
		BlogEmployee.setId(Long.parseLong(id));
		String flagStart = request.getParameter("flagStart");
		BlogEmployee.setFlagStart(Byte.parseByte(flagStart));
		// PrintWriter out=response.getWriter();
		Long docSize = RequestUtils.getLongParameter(request, "docSize", 0);

		// if("1".equals(flagStart)){
		try {
			// if(docSize < 5 ){
			// out.print("<script>");
			// out.print("alert('空间分配不能少于5MB');");
			// out.print("</script>");
			// return null;
			// }
			// docSpaceManager.assignBlogSpace(Long.parseLong(id), docSize);
			// out.print("<script>");
			// out.print("parent.parent.main.location.href=parent.parent.main.location.href;");
			// out.print("parent.parent.bottom.location.href=\"/seeyon/common/detail.html\";");
			// out.print("</script>");
		} catch (Exception e) {
			log.error("修改个人博客设置时分配博客空间： ", e);
			// out.print("<script>");
			// out.print("alert('空间分配过小');");
			// out.print("</script>");
		}
		// }
		blogManager.modifyEmployee(BlogEmployee);

		// 初始化分类
		if (flagStart != null && flagStart.equals("1")) {
			this.initFamily(Long.parseLong(id));
		}
		modelView.addObject("deptId", deptId);
		return modelView;
	}

	// 个人信息修改
	public ModelAndView modifyEmployee(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		BlogEmployee BlogEmployee = blogManager.getEmployeeById(Long
				.parseLong(id));
		BlogEmployee.setId(Long.parseLong(id));
		String tem = request.getParameter("introduce");
		BlogEmployee.setIntroduce(tem);
		tem = request.getParameter("image");
		BlogEmployee.setImage(tem);

		blogManager.modifyEmployee(BlogEmployee);
		return super.redirectModelAndView("/blog.do?method=blogHome");
	}

	public ModelAndView showmenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("blog/bloguse/menu");
		return result;
	}

	// 显示左边树(部门/系统组/个人组)
	public ModelAndView treeDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		// 单位id
		V3xOrgAccount account = orgManager.getAccountById(user
				.getLoginAccount());
		// orgManager.setDefaultAccount(id);
		ModelAndView result = new ModelAndView("blog/bloguse/treeDept");
		List<V3xOrgDepartment> deptlist = orgManager.getAllDepartments();
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (com.seeyon.v3x.organization.domain.V3xOrgDepartment) deptlist
					.get(i);
			dept.getCode();
			Long longid = dept.getId();
			V3xOrgDepartment parent = orgManager.getParentDepartment(longid);
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
			webdept.setV3xOrgDepartment(dept);
			if (null != parent) {
				webdept.setParentId(parent.getId());
				webdept.setParentName(parent.getName());
			}
			resultlist.add(webdept);
		}
		result.addObject("account", account);
		result.addObject("deptlist", resultlist);
		return result;
	}

	// 显示上边的工具条
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView getToolbar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = "blog/admin/tooBar";
		ModelAndView mav = new ModelAndView(path);
		String tem = request.getParameter("deptId");
		// load member list
		long deptId = 0;
		if (tem == null || tem.trim().equals("")) {
			User user = CurrentUser.get();
			deptId = user.getDepartmentId();
		} else {
			deptId = Long.parseLong(tem);
		}
		mav.addObject("deptId", deptId);

		// 单位管理员是否显示RSS页签
		boolean showRssTagOnAccountAdmin = Constants.showRssTagOnAccountAdmin();
		mav.addObject("showRssTagOnAccountAdmin", showRssTagOnAccountAdmin);
		boolean rssEnabled = Constants.rssModuleEnabled();
		mav.addObject("rssEnabled", rssEnabled);

		//TODO code seems useless to be cleared
		// 当前是否集团管理员登录
//		boolean isGroupAdmin = Constants.isGroupAdmin();
//		mav.addObject("isGroupAdmin", isGroupAdmin);

		return mav;
	}

	// 显示左边树(部门/系统组/个人组)
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView treeDeptAdmin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		// 单位id
		V3xOrgAccount account = orgManager.getAccountById(user
				.getLoginAccount());
		ModelAndView result = new ModelAndView("blog/admin/treeDept");
		//显示有效的内外部门
		List<V3xOrgDepartment> deptlist = orgManager.getAllDepartments(user.getLoginAccount(), false, false);
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (com.seeyon.v3x.organization.domain.V3xOrgDepartment) deptlist
					.get(i);
			//if (dept.getIsInternal()) {去掉是为了显示外部单位
				dept.getCode();
				Long longid = dept.getId();
				V3xOrgDepartment parent = orgManager
						.getParentDepartment(longid);
				WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
				webdept.setV3xOrgDepartment(dept);
				if (null != parent) {
					webdept.setParentId(parent.getId());
					webdept.setParentName(parent.getName());
				}
				resultlist.add(webdept);
		//	}
		}
		result.addObject("account", account);
		result.addObject("deptlist", resultlist);
		return result;
	}

	// 初始化组织机构右侧列表
	public ModelAndView initList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/listMembers");
		User user = CurrentUser.get();
		// load member list
		// 员工
		List<V3xOrgMember> members = orgManager.getMembersByDepartment(user
				.getDepartmentId(), true);

		List<EmployeeModel> EmployeeModelList = getEmployeeModelList(
				pagenate(members), false);
		mav.addObject("members", EmployeeModelList);
		return mav;
	}

	// 初始化组织机构右侧列表
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView initListAdmin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/admin/listMembers");
		User user = CurrentUser.get();
		String tem = request.getParameter("deptId");
		// load member list
		long deptId = 0;
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		if (tem == null || tem.trim().equals("")) {
			// User user = CurrentUser.get();
			// deptId = user.getDepartmentId();
			 members = orgManager.getAllMembers(false);
			/**
			List<V3xOrgEntity> v3xOrgEntityList = orgManager.getEntityList(
					V3xOrgMember.class.getSimpleName(), "orgAccountId", user
							.getAccountId(), user.getAccountId(), true);
			for (V3xOrgEntity v3xOrgEntity : v3xOrgEntityList) {
				V3xOrgMember v3xOrgMember = (V3xOrgMember) v3xOrgEntity;
				if (v3xOrgMember.getIsInternal()) {
					members.add(v3xOrgMember);
				}
			}**/
		} else {
			deptId = Long.parseLong(tem);
			mav.addObject("deptId", deptId);
			members = orgManager.getMembersByDepartment(deptId, true);
			members = this.filterConcurrentPost(members, deptId);
		}

		List<EmployeeModel> EmployeeModelList = getEmployeeModelList(
				pagenate(members), true);
		mav.addObject("members", EmployeeModelList);
		return mav;
	}

	// 显示某部门的员工{用于显示他人博客}
	public ModelAndView listDeptMembers(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/listMembers");
		Long deptId = RequestUtils.getLongParameter(request, "pId");
		// load member list
		List<V3xOrgMember> members = orgManager.getMembersByDepartment(deptId,
				false, true);
		List<EmployeeModel> EmployeeModelList = getEmployeeModelList(
				pagenate(members), false);
		mav.addObject("members", EmployeeModelList);
		mav.addObject("deptId", deptId);

		return mav;
	}

	// 显示某部门的员工(用于开通博客)
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView listDeptMembersAdmin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/admin/listMembers");
		Long deptId = RequestUtils.getLongParameter(request, "pId");
		// load member list
		List<V3xOrgMember> members = null;
		members = orgManager.getMembersByDepartment(deptId, true);
		members = this.filterConcurrentPost(members, deptId);
		List<EmployeeModel> EmployeeModelList = getEmployeeModelList(
				pagenate(members), true);
		mav.addObject("members", EmployeeModelList);
		mav.addObject("deptId", deptId);
		return mav;
	}

	// 显示某单位的员工(用于开通博客)
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView listAccountMembersAdmin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/admin/listMembers");
		Long accountId = RequestUtils.getLongParameter(request, "pId");
		// load member list
		List<V3xOrgMember> members = null;
		members = orgManager.getAllMembers(false);
		// members = this.filterConcurrentPost(members, deptId);
		List<EmployeeModel> EmployeeModelList = getEmployeeModelList(
				pagenate(members), true);
		mav.addObject("members", EmployeeModelList);
		mav.addObject("accountId", accountId);
		return mav;
	}

	// 过滤兼职
	private List<V3xOrgMember> filterConcurrentPost(List<V3xOrgMember> list,
			Long deptId) {
		List<V3xOrgMember> ret = new ArrayList<V3xOrgMember>();
		if (list == null || list.size() == 0 || deptId == null)
			return ret;

		for (V3xOrgMember m : list) {
			if (m.getOrgDepartmentId().longValue() == deptId.longValue())
				ret.add(m);
		}

		return ret;
	}

	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return null;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}

	// 得到员工博客初始化信息
	private List<EmployeeModel> getEmployeeModelList(
			List<V3xOrgMember> members, boolean flagAll)
			throws BusinessException {
		List<EmployeeModel> EmployeeModelList = new ArrayList<EmployeeModel>();
		BlogEmployee BlogEmployee = null;
		EmployeeModel EmployeeModel = null;
		Long id = null;
		User user = CurrentUser.get();
		Long departmentid = user.getDepartmentId();

		DocStorageSpace docStorageSpace = null;

		if (null != members && members.size() > 0) {
			for (V3xOrgMember member : members) {
				// 得到员工博客信息
				try {
					BlogEmployee = blogManager.getEmployeeById(member.getId());
					if (null == BlogEmployee)
						continue;
					id = BlogEmployee.getId();
					// 如果不是显示全部员工
					// 则检查是否已经共享给我，如果没有开通共享，则不显示
					if (!flagAll) {
						if (blogManager.checkSharedEmployee(id) <= 0) {
							// 如果此员工没有共享给我，检查是否共享给了我所在的部门
							if (blogManager.checkSharedDeparment(id,
									departmentid) <= 0) {
								// 如果都没有共享，则不显示此员工
								continue;
							}
						}
					}

					EmployeeModel = new EmployeeModel();
					EmployeeModel.setId(id);
					EmployeeModel.setIntroduce(BlogEmployee.getIntroduce());
					EmployeeModel.setArticleNumber(BlogEmployee
							.getArticleNumber());
					EmployeeModel.setImage(BlogEmployee.getImage());
					EmployeeModel.setIdCompany(BlogEmployee.getIdCompany());
					EmployeeModel.setFlagStart(BlogEmployee.getFlagStart());
					EmployeeModel.setFlagShare(BlogEmployee.getFlagShare());
					EmployeeModel.setUserName(member.getName());

					docStorageSpace = docSpaceManager.getDocSpaceByUserId(id);
					// Byte flag = BlogEmployee.getFlagStart();
					// 如果博客状态是开启的但是又显示未分配的，则重新分配博客空间
					// if((flag==1)&&(docStorageSpace.getBlogStatus()==Constants.SPACE_NOT_ASSIGNED)){
					// docSpaceManager.assignBlogSpace(id , 10);

					// }
					DocSpaceVO vo = new DocSpaceVO(docStorageSpace);
					EmployeeModel.setBlogSpace(vo.getBlogTotal());
					EmployeeModel.setBlogUsedSpace(vo.getBlogUsed());
					EmployeeModel.setBlogStatus(vo.getBlogStatus());

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				EmployeeModelList.add(EmployeeModel);
			}
		}
		return EmployeeModelList;
	}

	private List<WebWithPropV3xOrgMember> translateList(
			List<V3xOrgMember> members) throws BusinessException {
		List<WebWithPropV3xOrgMember> results = new ArrayList<WebWithPropV3xOrgMember>();
		if (null != members && members.size() > 0) {
			for (V3xOrgMember member : members) {
				results.add(translate(member));
			}
		}
		return results;
	}

	/**
	 * 把V3xOrgMember对象转化为带属性且能被页面直接调用的WebWithPropV3xOrgMember
	 * 
	 * @param member
	 *            V3xOrgMemberO
	 * @param model
	 *            WebWithPropV3xOrgMember
	 * @throws BusinessException
	 */
	private WebWithPropV3xOrgMember translate(V3xOrgMember member)
			throws BusinessException {
		// TODO!!! orgManager.loadEntityProperty(member);
		WebWithPropV3xOrgMember webMember = new WebWithPropV3xOrgMember();
		translateV3xOrgMemberToWebV3xOrgMember(member, webMember);
		webMember.bind(member.getProperties());
		return webMember;
	}

	private void translateV3xOrgMemberToWebV3xOrgMember(V3xOrgMember member,
			WebV3xOrgMember webMember) throws BusinessException {
		webMember.setDepartmentName("");
		webMember.setLevelName("");
		webMember.setPostName("");

		long deptId = member.getOrgDepartmentId();
		long levelId = member.getOrgLevelId();
		long postId = member.getOrgPostId();

		webMember.setV3xOrgMember(member);
		V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
		if (dept != null) {
			webMember.setDepartmentName(dept.getName());
		}

		V3xOrgLevel level = orgManager.getLevelById(levelId);
		if (null != level) {
			webMember.setLevelName(level.getName());
		}

		V3xOrgPost post = orgManager.getPostById(postId);
		if (null != post) {
			webMember.setPostName(post.getName());
		}
	}

	/**
	 * 2008.02.19 博客新的首页
	 */
	public ModelAndView blogHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("blog/bloguse/blogHome");

		// 记录进入该帖的上一页面
		String resourceMethod = request.getParameter("resourceMethod");
		mav.addObject("resourceMethod", resourceMethod);
		// String flagY =
		// ResourceBundleUtil.getString("com.seeyon.v3x.blog.resources.i18n.BLOGResources",
		// blog.state.label);
		// mav.addObject("flagY", flagY);
		String userId = request.getParameter("userId");
		List<BlogArticle> blogArticleList = null;
		BlogArticle blogArticle = null;
		// blogArticle = this.get

		String searchFlag = request.getParameter("searchFlag");
		// String daySearchFlag = request.getParameter("daySearch");
		boolean boolSearch = (searchFlag != null && "true".equals(searchFlag));
		// boolean boolDaySearch = (daySearchFlag != null &&
		// "true".equals(daySearchFlag));
		if (!boolSearch) {
			mav.addObject("condition", "");
		}

		List<ArticleModel> allArticleModelList = new ArrayList<ArticleModel>();
		User currentUser = CurrentUser.get();
		long currentUserId = currentUser.getId();
		StaffInfo staff = staffInfoManager.getStaffInfoById(currentUserId); 
		mav.addObject("staff", staff);
		if (null != staff) {
			if (Strings.isNotBlank(staff.getSelf_image_name())) {
				if(staff.getSelf_image_name().startsWith("fileId")){
					mav.addObject("image", "image1");
				}else{
					mav.addObject("image", "image2");
				}
			} 
 		}
		long dataUserId = currentUserId;
		if (userId == null || userId.trim().equals("")) {
			// 得到个人信息
			mav = setEmployeeInfo(mav, currentUserId);
			mav.addObject("flagAdmin", 1);
			// 如果是本人，则列出私有和共享的文章
			if (boolSearch) {
				blogArticleList = this.searchArticleList(request, mav);
				// }else if(boolDaySearch){
				// blogArticleList = this.daySearchArticleList(request);
			} else {
				blogArticleList = blogArticleManager.listAllArticlePaging(
						currentUserId, (byte) 2);
			}

		} else {
			mav = setEmployeeInfo(mav, currentUserId);
			mav.addObject("flagAdmin", 0);
			// 如果不是本人，则只列出共享文章
			if (boolSearch) {
				blogArticleList = this.searchArticleList(request, mav);
				// }else if(boolDaySearch){
				// blogArticleList = this.daySearchArticleList(request);
			} else {
				blogArticleList = blogArticleManager.listAllArticlePaging(
						currentUserId, (byte) 0);
			}

			dataUserId = currentUserId;
		}
		// flagAdmin==1为文章管理员
		Byte flagAdmin = 0;
		if (userId == null || userId.trim().equals("")) {
			flagAdmin = 1;
		} else if (Long.parseLong(userId) == currentUserId) {
			flagAdmin = 1;
		}
		// 验证是否已经开通博客
		if (flagAdmin == 1) {
			BlogEmployee blogEmployee = blogManager.getEmployeeById(currentUser
					.getId());
			if (blogEmployee.getFlagStart() == null
					|| blogEmployee.getFlagStart() != 1) {
				flagAdmin = 2;
			}
		}
		mav.addObject("flagAdmin", flagAdmin);
		allArticleModelList = this.getArticleModelList(blogArticleList);
		mav.addObject("dataUserId", dataUserId);
		mav.addObject("articleModellist", allArticleModelList);

		// 列出关注信息
		List<AttentionModel> AttentionModelList = null;
		AttentionModelList = listAllAttention(request, response);
		if (AttentionModelList != null
				&& AttentionModelList.size() > BlogConstants.BLOG_HOME_ATTENTION_SIZE)
			AttentionModelList = AttentionModelList.subList(0,
					BlogConstants.BLOG_HOME_ATTENTION_SIZE);
		mav.addObject("attentionList", AttentionModelList);

		mav
				.addObject(
						"attentionListEmpty",
						(AttentionModelList == null ? BlogConstants.BLOG_HOME_ATTENTION_SIZE
								: (BlogConstants.BLOG_HOME_ATTENTION_SIZE - AttentionModelList
										.size())));
			
		List<BlogFavorites> bfs = blogArticleManager.getFavoriteBlogsOfUser(CurrentUser.get().getId());
	
        if(bfs != null){
        	mav.addObject("atrnum", bfs.size());
        }else{
        	mav.addObject("atrnum", 0) ;
        }
		return mav;
	}

	private List<BlogArticle> searchArticleList(HttpServletRequest request,
			ModelAndView mav) {
		String condition = request.getParameter("condition");
		mav.addObject("condition", condition);

		// 记录进入该帖的上一页面

		// String userId = request.getParameter("userId");
		// if (userId == null||userId.equals("")){
		// userId="0";
		// }
		String field = "";
		if (condition == null) {
			condition = "";
		}
		if (condition.equals("subject") || condition.equals("yearMonth")) {
			field = request.getParameter("textfield");
		} else {
			field = request.getParameter("textfield1");
		}
		if (condition.equals("byDate")) {
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			String day = request.getParameter("day");
			field = year + "-" + month + "-" + day;

			mav.addObject("byDateFlag", true);
		} else
			mav.addObject("byDateFlag", false);
		if (field == null)
			field = "";

		String field1 = request.getParameter("textfield2");
		if (field1 == null) {
			field1 = "";
		}

		mav.addObject("field", field);
		mav.addObject("field1", field1);

		List<BlogArticle> BlogArticleList = null;
		if (condition != null && !condition.equals("")) {
			try {
				BlogArticleList = blogArticleManager.queryByCondition(
						CurrentUser.get().getId(), condition, field, field1);
			} catch (Exception e) {
			}
		}

		return (BlogArticleList == null ? new ArrayList<BlogArticle>()
				: BlogArticleList);
	}

	// private List<BlogArticle> daySearchArticleList(HttpServletRequest
	// request){
	// String condition = request.getParameter("condition");
	//
	// // 记录进入该帖的上一页面
	//
	// String userId = request.getParameter("userId");
	// if (userId == null||userId.equals("")){
	// userId="0";
	// }
	// String field ="";
	// if (condition == null){
	// condition = "";
	// }
	// if(condition.equals("subject")||condition.equals("yearMonth")){
	// field = request.getParameter("textfield");
	// }
	// else{
	// field = request.getParameter("textfield1");
	// }
	// if(condition.equals("byDate")){
	// String year = request.getParameter("year");
	// String month = request.getParameter("month");
	// String day = request.getParameter("day");
	// field = year+"-"+month+"-"+day;
	// }
	// if (field == null)field = "";
	//		
	// String field1 = request.getParameter("textfield2");
	// if (field1 == null) {
	// field1 = "";
	// }
	// List<BlogArticle> BlogArticleList = null;
	// if (condition != null && !condition.equals("")) {
	// try {
	// BlogArticleList =
	// blogArticleManager.queryByCondition(Long.parseLong(userId), condition,
	// field, field1);
	// } catch (Exception e) {
	// }
	// }
	//		
	// return (BlogArticleList == null ? new ArrayList<BlogArticle>() :
	// BlogArticleList);
	// }

	public ModelAndView deleteArticles(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ids = request.getParameter("selectedIds");
		String familyIds = request.getParameter("selectedFamilyIds");

		if (Strings.isBlank(ids) || Strings.isBlank(familyIds))
			return this.blogHome(request, response);

		String[] idsArr = ids.split(",");
		String[] idsFArr = familyIds.split(",");
		Long employeeId = CurrentUser.get().getId();
		for (int i = 0; i < idsArr.length; i++) {
			long articleId = Long.valueOf(idsArr[i]);
			long familyId = Long.valueOf(idsFArr[i]);

			BlogArticle blogArticle = blogArticleManager
					.getArticleById(articleId);// 得到文章
			long size = 0;
			if (blogArticle != null) {
				this.appLogManager.insertLog(CurrentUser.get(),
						AppLogAction.Blog_Del, CurrentUser.get().getName(),
						blogArticle.getSubject());// 插入到应用日志
				size = blogArticleManager.getArticleById(articleId)
						.getArticleSize();

				blogArticleManager.deleteReplyPostByArticleId(articleId);
				// TODO 根据文章ID删除收藏
				// 删除此文章的收藏
				blogManager.deleteFavoritesByArticleId(articleId);
				blogArticleManager.deleteArticle(articleId);
				// 更新员工文章数,文章数减一
				blogManager.updateArticleNumber(employeeId, -1);
				// 更新分类文章数,文章数减一
				blogManager.updateFamilyArticleNumber(familyId, 1);
				// 计算文章并更新所占空间

				docSpaceManager.deleteBlogSpaceSize(employeeId, -size);

			}

		}

		return this.blogHome(request, response);
	}

	public ModelAndView delAttention(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		if (Strings.isNotBlank(id)) {
			blogManager.deleteAttention(Long.valueOf(id));
		}

		return this.listShareOtherIndex(request, response);
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

	/*
	 * public ModelAndView changeFav(HttpServletRequest request,
	 * HttpServletResponse response) throws Exception { String[] drIds =
	 * request.getParameterValues("id"); String id =
	 * request.getParameter("id2"); for(){ a = new
	 * BlogArticle(Long.valueOf(sid)); list.add(a); } this.blogArticleManager.ge }
	 */
}