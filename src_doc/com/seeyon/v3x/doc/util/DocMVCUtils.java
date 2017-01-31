package com.seeyon.v3x.doc.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.manager.ContentTypeManager;
import com.seeyon.v3x.doc.manager.DefaultSearchCondition;
import com.seeyon.v3x.doc.manager.DocAclManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.doc.webmodel.DocAclVO;
import com.seeyon.v3x.doc.webmodel.DocPersonalShareVO;
import com.seeyon.v3x.doc.webmodel.DocTableVO;
import com.seeyon.v3x.doc.webmodel.DocTreeVO;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.util.ProjectConstants;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 用于分离DocController中的部分私有方法
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-10-25
 */
public class DocMVCUtils {
	private static final Log logger = LogFactory.getLog(DocMVCUtils.class);

	/**
	 * 获取文档树节点VO，此方法为重构后extract method，以便单点维护(2010-10-25)
	 * @param userId	用户ID
	 * @param dr	文档
	 * @param isPersonalLib		是否为个人文档库
	 * @return	文档树节点VO
	 */
	public static DocTreeVO getDocTreeVO(Long userId, DocResource dr, boolean isPersonalLib, 
			DocMimeTypeManager docMimeTypeManager, DocAclManager docAclManager) {
		DocTreeVO vo = new DocTreeVO(dr);
		setGottenAclsInVO(vo, userId, false, docAclManager);
		String srcIcon = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getIcon();
		if(srcIcon.indexOf('|') != -1) {
			vo.setOpenIcon(srcIcon.substring(srcIcon.indexOf('|') + 1, srcIcon.length()));
			vo.setCloseIcon(srcIcon.substring(0, srcIcon.indexOf('|')));
		}
		else {
			vo.setOpenIcon(srcIcon);
			vo.setCloseIcon(srcIcon);
		}
		vo.setIsPersonalLib(isPersonalLib);
		setNeedI18nInVo(vo);
		return vo;
	}
	
	/**
	 * 获取文档树节点VO，此方法为重构后extract method，以便单点维护(2010-10-25)
	 * @param userId	用户ID
	 * @param dr	文档
	 * @param docLibType	文档库类型
	 * @return	文档树节点VO
	 */
	public static DocTreeVO getDocTreeVO(Long userId, DocResource dr, byte docLibType, 
			DocMimeTypeManager docMimeTypeManager, DocAclManager docAclManager) {
		DocTreeVO vo = getDocTreeVO(userId, dr, docLibType == Constants.PERSONAL_LIB_TYPE, docMimeTypeManager, docAclManager);
		vo.setDocLibType(docLibType);
		return vo;
	}
	
	/** 设置DocAclVO对象中的权限标记  */
	public static void setGottenAclsInVO(DocAclVO vo, Long userId, boolean isBorrowOrShare, DocAclManager docAclManager) {
		if (CurrentUser.get().isAdministrator()) {
			vo.setAllAcl(true);
			vo.setAddAcl(true);
			vo.setEditAcl(true);
			vo.setReadOnlyAcl(true);
			vo.setBrowseAcl(true);
			vo.setListAcl(true);
			vo.setDeptBorrowAcl(true);
			return;
		}

		DocResource dr = vo.getDocResource();
		if (dr == null)
			return;
		
		// 计划
		if (dr.getFrType() == Constants.FOLDER_PLAN
				|| dr.getFrType() == Constants.FOLDER_PLAN_DAY
				|| dr.getFrType() == Constants.FOLDER_PLAN_MONTH
				|| dr.getFrType() == Constants.FOLDER_PLAN_WEEK
				|| dr.getFrType() == Constants.FOLDER_PLAN_WORK) {
			vo.setAllAcl(false);
			vo.setAddAcl(false);
			vo.setEditAcl(false);
			vo.setReadOnlyAcl(false);
			vo.setBrowseAcl(false);
			vo.setListAcl(true);
			vo.setDeptBorrowAcl(true);
		} 
		else if (dr.getFrType() == Constants.SYSTEM_PLAN) {
			vo.setAllAcl(false);
			vo.setAddAcl(false);
			vo.setEditAcl(false);
			vo.setReadOnlyAcl(false);
			vo.setBrowseAcl(false);
			vo.setListAcl(false);
			vo.setDeptBorrowAcl(true);
			vo.setIsBorrowOrShare(true);
		} 
		else if (isBorrowOrShare
				|| (vo.getIsPersonalLib() && !dr.getIsMyOwn())) {
			// 借阅、共享的权限设置
			vo.setAllAcl(false);
			vo.setAddAcl(false);
			vo.setEditAcl(false);
			vo.setListAcl(false);
			vo.setIsBorrowOrShare(true);
			vo.setDeptBorrowAcl(true);
			String acl = docAclManager.getBorrowPotent(dr.getId());
			vo.setReadOnlyAcl('1' == acl.charAt(0));
			vo.setBrowseAcl(true);
		} 
		else if (dr.getIsMyOwn()) {
			vo.setAllAcl(true);
			vo.setAddAcl(true);
			vo.setEditAcl(true);
			vo.setReadOnlyAcl(true);
			vo.setBrowseAcl(true);
			vo.setListAcl(true);
			vo.setDeptBorrowAcl(true);
		} 
		else {
			// 2007.07.19 lihf 权限从DocResource取得（Manager返回时已经添加）
			Set<Integer> aclset = vo.getDocResource().getAclSet();
			if (aclset == null || aclset.size() == 0) {
				if (!vo.getDocResource().getHasAcl()) {
					// 不做重复抽取
					String aclIds = Constants.getOrgIdsOfUser(userId);
					Set<Integer> acls = docAclManager.getDocResourceAclList(dr,
							aclIds);
					if (acls != null) {
						if (acls.contains(Constants.ALLPOTENT))
							vo.setAllAcl(true);
						
 						if (acls.contains(Constants.EDITPOTENT))
							vo.setEditAcl(true);
						
						if (acls.contains(Constants.ADDPOTENT))
							vo.setAddAcl(true);
						
						if (acls.contains(Constants.READONLYPOTENT))
							vo.setReadOnlyAcl(true);
						
						if (acls.contains(Constants.BROWSEPOTENT))
							vo.setBrowseAcl(true);
						
						if (acls.contains(Constants.DEPTBORROW))
							vo.setDeptBorrowAcl(true);
						
						if (acls.contains(Constants.LISTPOTENT))
							vo.setListAcl(true);
					}
				}
			} 
			else {
				for (int da : aclset) {
					switch (da) {
					case Constants.ALLPOTENT:
						vo.setAllAcl(true);
						break;
					case Constants.ADDPOTENT:
						vo.setAddAcl(true);
						break;
					case Constants.EDITPOTENT:
						vo.setEditAcl(true);
						break;
					case Constants.READONLYPOTENT:
						vo.setReadOnlyAcl(true);
						break;
					case Constants.BROWSEPOTENT:
						vo.setBrowseAcl(true);
						break;
					case Constants.DEPTBORROW:
						vo.setDeptBorrowAcl(true);
						break;
					case Constants.LISTPOTENT:
						vo.setListAcl(true);
						break;
					}
				}
			}
		}
	}
	
	private static Set<Long> NEED_I18N_TYPES = null;
	static {
		NEED_I18N_TYPES = new HashSet<Long>();
		
		NEED_I18N_TYPES.add(Constants.FOLDER_MINE);
		NEED_I18N_TYPES.add(Constants.FOLDER_CORP);
		NEED_I18N_TYPES.add(Constants.ROOT_ARC);
		NEED_I18N_TYPES.add(Constants.FOLDER_ARC_PRE);
		NEED_I18N_TYPES.add(Constants.FOLDER_PROJECT_ROOT);
		NEED_I18N_TYPES.add(Constants.FOLDER_PLAN);
		NEED_I18N_TYPES.add(Constants.FOLDER_TEMPLET);
		NEED_I18N_TYPES.add(Constants.FOLDER_SHARE);
		NEED_I18N_TYPES.add(Constants.FOLDER_SHAREOUT);
		NEED_I18N_TYPES.add(Constants.FOLDER_BORROWOUT);
		NEED_I18N_TYPES.add(Constants.FOLDER_BORROW);
		NEED_I18N_TYPES.add(Constants.FOLDER_PLAN_DAY);
		NEED_I18N_TYPES.add(Constants.FOLDER_PLAN_MONTH);
		NEED_I18N_TYPES.add(Constants.FOLDER_PLAN_WEEK);
		NEED_I18N_TYPES.add(Constants.FOLDER_PLAN_WORK);
		NEED_I18N_TYPES.add(Constants.DEPARTMENT_BORROW);
	}

	/** 设置DocAcLVO中的needI18n标记  */
	public static void setNeedI18nInVo(DocAclVO vo) {
		Long type = vo.getDocResource().getFrType();
		vo.setNeedI18n(NEED_I18N_TYPES.contains(type));
	}
	
	/**
	 * 计算每一列宽度，标题列宽度3倍展现，公文号2倍展现，其余按照所得结果展现
	 */
	public static List<Integer> getColumnWidthNew(List<DocMetadataDefinition> dmds) {
		List<Integer> widths = new ArrayList<Integer>();
		for (DocMetadataDefinition dmd : dmds) {
			if(dmd.getPhysicalName().equals("frName"))
				widths.add(Constants.getWidthByType(dmd.getType()) * 3);
			else if(dmd.getPhysicalName().equals("avarchar17"))
				widths.add(Constants.getWidthByType(dmd.getType()) * 2);
			else
				widths.add(Constants.getWidthByType(dmd.getType()));
		}
		logger.debug("result widths -- " + widths);
		return widths;
	}
	
	/** 得到当前用户可以推送首页的部门集合  */
	public static Set<Long> getDepSetAdmin(SpaceManager spaceManager) {
		// 部门管理员、部门主管都可以发送到部门首页
		long userId = CurrentUser.get().getId();
		Set<Long> deptSet = new HashSet<Long>();
		List<Long> list = spaceManager.getManagerDepartments(userId, CurrentUser.get().getLoginAccount());
		if(CollectionUtils.isNotEmpty(list))
			deptSet.addAll(list);
		return deptSet;
	}
	
	/**
	 * 在点击文档中心左侧树节点时，动态获取xml信息以便加载节点下的子节点
	 * @param docLibId	文档库ID
	 * @param folders	文档树节点(子文件夹)VOs
	 * @return	xloadtree加载子节点时所需的xml信息
	 */
	public static String getXmlStr4LoadNodeOfCommonTree(Long docLibId, List<DocTreeVO> folders) {
		StringBuilder result = new StringBuilder();
		for (DocTreeVO vo : folders) {
			String name = Strings.toXmlStr(vo.getDocResource().getFrName());
			if (vo.getNeedI18n()) {
				name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name);
			}

			result.append("<tree businessId=\"" + vo.getDocResource().getId()
					+ "\" icon=\"" + vo.getCloseIcon() + "\" openIcon =\""
					+ vo.getOpenIcon() + "\"" + " text=\"" + name
					+ "\" src=\"/seeyon/doc.do?method=xmlJsp" + "&amp;resId="
					+ vo.getDocResource().getId() + "&amp;frType="
					+ vo.getDocResource().getFrType()
					+ "&amp;isShareAndBorrowRoot=" + vo.getIsBorrowOrShare()
					+ "\" action=\"javascript:showSrcAndAction('"
					+ vo.getDocResource().getId() + "','"
					+ vo.getDocResource().getFrType() + "','" + docLibId
					+ "','" + vo.getDocLibType() + "','"
					+ vo.getIsBorrowOrShare() + "','" + vo.isAllAcl() + "','"
					+ vo.isEditAcl() + "','" + vo.isAddAcl() + "','"
					+ vo.isReadOnlyAcl() + "','" + vo.isBrowseAcl() + "','"
					+ vo.isListAcl() + "','" + "')\"/>");
		}
		
		return result.toString();
	}
	
	/**
	 * 在点击弹出树节点时，动态获取xml信息以便加载节点下的子节点
	 * @param lib	文档库
	 * @param folders	文档树节点(子文件夹)VOs
	 * @param otherAccountShortName		外单位名称简称
	 * @return	xloadtree加载子节点时所需的xml信息
	 */
	public static String getXmlStr4LoadNodeOfMoveTree(DocLib lib, List<DocTreeVO> folders, String otherAccountShortName) {
		StringBuilder xmlstr = new StringBuilder("");
		for (DocTreeVO vo : folders) {
			String name = Strings.toXmlStr(vo.getDocResource().getFrName() + otherAccountShortName);
			if (vo.getNeedI18n()) {
				name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name) + otherAccountShortName;
			}
			xmlstr.append("<tree businessId=\"" + vo.getDocResource().getId()
					+ "\" icon=\"" + vo.getCloseIcon() + "\" openIcon =\""
					+ vo.getOpenIcon() + "\"" + " text=\"" + name
					+ "\" src=\"/seeyon/doc.do?method=xmlJspMove"
					+ "&amp;resId=" + vo.getDocResource().getId()
					+ "&amp;frType=" + vo.getDocResource().getFrType()
					+ "&amp;docLibId=" + vo.getDocResource().getDocLibId()
					+ "&amp;docLibType=" + lib.getType()
					+ "&amp;isShareAndBorrowRoot=" + vo.getIsBorrowOrShare()
					+ "&amp;logicalPath="
					+ vo.getDocResource().getLogicalPath() + "&amp;all="
					+ vo.isAllAcl() + "&amp;edit=" + vo.isEditAcl()
					+ "&amp;add=" + vo.isAddAcl() + "&amp;commentEnabled="
					+ vo.getDocResource().getCommentEnabled()
					+ "\" target=\"moveIframe\"/>");
		}
		return xmlstr.toString();
	}
	
	/**
	 * 在插入关联文档时，点击关联文档中心左侧树节点时，动态获取xml信息以便加载节点下的子节点
	 * @param docLibId	文档库ID
	 * @param folders	文档树节点(子文件夹)VOs
	 * @return	xloadtree加载子节点时所需的xml信息
	 */
	public static String getXmlStr4LoadNodeOfQuoteTree(Long docLibId, List<DocTreeVO> folders) {
		StringBuilder sb = new StringBuilder();
		if(CollectionUtils.isNotEmpty(folders)) {
			for (DocTreeVO vo : folders) {
				String name = Strings.toXmlStr(vo.getDocResource().getFrName());
				if (vo.getNeedI18n()) {
					name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name);
				}
				
				sb.append("<tree businessId=\"" + vo.getDocResource().getId()
						+ "\" icon=\"" + vo.getCloseIcon() + "\" openIcon =\""
						+ vo.getOpenIcon() + "\"" + " text=\"" + name
						+ "\" src=\"/seeyon/doc.do?method=xmlJspQuote"
						+ "&amp;resId=" + vo.getDocResource().getId()
						+ "&amp;frType=" + vo.getDocResource().getFrType()
						+ "\" action=\"javascript:showSrcAndAction4Quote('"
						+ vo.getDocResource().getId() + "','"
						+ vo.getDocResource().getFrType() + "','"
						+ docLibId + "','" + vo.getDocLibType() + "','"
						+ vo.getIsBorrowOrShare() + "','" + vo.isAllAcl() + "','"
						+ vo.isEditAcl() + "','" + vo.isAddAcl() + "','"
						+ vo.isReadOnlyAcl() + "','" + vo.isBrowseAcl() + "','"
						+ vo.isListAcl() + "','" + "')\"/>");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 在项目文档夹下点击树状节点时，动态获取xml信息以便加载节点下的子节点
	 * @param folders	文档树节点(子文件夹)VOs
	 * @return	xloadtree加载子节点时所需的xml信息
	 */
	public static String getXmlStr4LoadNodeOfProjectTree(List<DocTreeVO> folders) {
		StringBuilder sb = new StringBuilder();
		if(CollectionUtils.isNotEmpty(folders)) {
			for (DocTreeVO vo : folders) {
				String name = vo.getDocResource().getFrName();
				if (vo.getNeedI18n()) {
					name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name);
				}
				sb.append("<tree businessId=\"" + vo.getDocResource().getId()
						+ "\" icon=\"" + vo.getCloseIcon() + "\" openIcon =\""
						+ vo.getOpenIcon() + "\"" + " text=\"" + name
						+ "\" src=\"/seeyon/doc.do?method=xmlJspProject"
						+ "&amp;resId=" + vo.getDocResource().getId()
						+ "&amp;frType=" + vo.getDocResource().getFrType()
						+ "&amp;docLibId=" + vo.getDocResource().getDocLibId()
						+ "&amp;docLibType=" + Constants.PROJECT_LIB_TYPE
						+ "\" target=\"moveIframe\"/>");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 如果当前点击的单位/用户自定义文档库是从外单位共享而来的<br>
	 * 点击打开之后其下的文档夹名称需要加上外单位简称
	 * @param lib	文档库(本单位文档库或外单位共享来的文档库)
	 */
	public static String getOtherAccountShortName(DocLib lib, OrgManager orgManager) {
		String result = "";
		boolean isFromOtherAccount = lib.getDomainId() != CurrentUser.get().getLoginAccount() && 
									 (lib.getType() == Constants.ACCOUNT_LIB_TYPE.byteValue() || 
									  lib.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue());
		if (isFromOtherAccount) {
			try {
				result = "(" + orgManager.getAccountById(lib.getDomainId()).getShortname() + ")";
			} 
			catch (BusinessException e) {
				logger.error("获取当前文档库所在单位出现异常", e);
			}
		}
		return result;
	}
	
	/** 设置是否归档类型标记  */
	public static void setPigFlag(DocTableVO vo) {
		long type = vo.getDocResource().getFrType();
		if (type == Constants.SYSTEM_ARCHIVES) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.edoc.getKey());
		} else if (type == Constants.SYSTEM_BBS) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.bbs.getKey());
		} else if (type == Constants.SYSTEM_BULLETIN) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.bulletin.getKey());
		} else if (type == Constants.SYSTEM_COL) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.collaboration.getKey());
		} else if (type == Constants.SYSTEM_FORM) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.form.getKey());
		} else if (type == Constants.SYSTEM_INQUIRY) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.inquiry.getKey());
		} else if (type == Constants.SYSTEM_MEETING) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.meeting.getKey());
		} else if (type == Constants.SYSTEM_NEWS) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.news.getKey());
		} else if (type == Constants.SYSTEM_PLAN) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.plan.getKey());
		} else if (type == Constants.SYSTEM_MAIL) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.mail.getKey());
		} else if (type == Constants.SYSTEM_INFO) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.info.getKey());
		}else if (type == Constants.SYSTEM_INFOSTAT) {
			vo.setIsPig(true);
			vo.setAppEnumKey(ApplicationCategoryEnum.infoStat.getKey());
		} else {
			vo.setIsPig(false);
			vo.setAppEnumKey(ApplicationCategoryEnum.doc.getKey());
		}
	}
	
	/**
	 * 判断是否归档类型
	 * @param dr
	 */
	public static boolean isPig(DocResource dr) {
		long type = dr.getFrType();
		return type == Constants.SYSTEM_ARCHIVES
				|| type == Constants.SYSTEM_BBS
				|| type == Constants.SYSTEM_BULLETIN
				|| type == Constants.SYSTEM_COL
				|| type == Constants.SYSTEM_FORM
				|| type == Constants.SYSTEM_INQUIRY
				|| type == Constants.SYSTEM_MEETING
				|| type == Constants.SYSTEM_NEWS
				|| type == Constants.SYSTEM_PLAN
				|| type == Constants.SYSTEM_MAIL;
	}
	
	/** 转换file的创建日期为 yyyy-mm-dd */
	public static String getCreateDateOfFile(DocResource dr, FileManager fileManager) {
		if (dr == null)
			return new Date().toString().substring(0, 10);

		// 对于上传文件，因为可能出现替换情况，导致新建时间不一致，所以应该从系统取。解决下载的定位问题
		try {
			V3XFile file = fileManager.getV3XFile(dr.getSourceId());

			if (file != null)
				return file.getCreateDate().toString().substring(0, 10);
		} 
		catch (BusinessException e) {
			logger.error("从fileManager取得V3xFile, ", e);
		}

		return new Date().toString().substring(0, 10);
	}
	
	/** 取得我的文档授权数据  */
	public static List<DocPersonalShareVO> getMyGrantVO(Long docResId, DocAclManager docAclManager) {
		List<DocPersonalShareVO> pvos = getDocPersonalShareVOs(docResId, docAclManager, false);
		if(CollectionUtils.isNotEmpty(pvos)) {
			return pvos;
		}
		else {
			return getDocPersonalShareVOs(docResId, docAclManager, true);
		}
	}

	private static List<DocPersonalShareVO> getDocPersonalShareVOs(Long docResId, DocAclManager docAclManager, boolean inherit) {
		List<DocPersonalShareVO> result = null;
		
		List<DocAcl> ilist = null;
		if(inherit)
			ilist = docAclManager.getPersonalShareInHeritList(docResId);
		else
			ilist = docAclManager.getPersonalShareList(docResId);
		
		if (ilist != null) {
			result = new ArrayList<DocPersonalShareVO>();
			for (DocAcl acl : ilist) {
				DocPersonalShareVO pvo = new DocPersonalShareVO();
				pvo.setInherit(inherit);
				pvo.setUserId(acl.getUserId());
				String userName = Constants.getOrgEntityName(acl.getUserType(), acl.getUserId(), false);

				pvo.setUserName(userName);
				pvo.setUserType(acl.getUserType());
				pvo.setAclId(acl.getId());

				pvo.setAlert(acl.getIsAlert());
				if(inherit) {
					pvo.setAlertId(0l);
				} else if(acl.getDocAlertId() != null) {
					pvo.setAlertId(acl.getDocAlertId());
				}
				result.add(pvo);
			}
		}
		return result;
	}
	
	public static String[] setDocMetadataDefinitionNames(List<DocMetadataDefinition> definitions, Byte docLibType) {
		if(CollectionUtils.isNotEmpty(definitions)) {
			List<String> names = new ArrayList<String>(definitions.size());
			for(DocMetadataDefinition def : definitions) {
				if(def == null || def.getStatus() == Constants.DOC_METADATA_DEF_STATUS_DELETED)
					continue;
				
				String name = def.getName();	
				// 公文建文日期 与 协同发起日期同属一个字段，但显示名称不同
				if(def.getId() == DefaultSearchCondition.Search_EDOC_SEND_DATE && docLibType == Constants.EDOC_LIB_TYPE.byteValue()) {
					name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME_EDOC, "edoc.edoctitle.createDate.label");
				}
				else {
					name = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, name);
					if(name.equals(def.getName())){
						String resName = Constants.getResourceNameOfMetadata(name, "");
						if(Strings.isNotBlank(resName))
							name = ResourceBundleUtil.getString(resName, name);		
					}
				}
				
				def.setShowName(name);
				names.add(name);
			}
			return names.toArray(new String[0]);
		}
		return new String[0];
	}
	
	/**
	 * 写入需要渲染显示的数据，通常会在不同显示场合重复出现
	 */
	public static void returnVaule(ModelAndView ret, Byte docLibType, DocLib docLib, HttpServletRequest request, 
			ContentTypeManager contentTypeManager, DocLibManager docLibManager) {
		List<DocType> types = contentTypeManager.getAllSearchContentType();
		ret.addObject("types", types);
		
		Long docLibId = docLib.getId();
		renderSearchConditions(ret, docLibManager, docLibId);
		
		ret.addObject("isGroupLib", (docLibType.byteValue() == Constants.GROUP_LIB_TYPE.byteValue()));
		ret.addObject("isPrivateLib", docLibType.equals(Constants.PERSONAL_LIB_TYPE));
		ret.addObject("isEdocLib", (docLibType.byteValue() == Constants.EDOC_LIB_TYPE.byteValue()));
		ret.addObject("folderEnabled", docLib.getFolderEnabled());
		ret.addObject("a6Enabled", docLib.getA6Enabled());
		ret.addObject("officeEnabled", docLib.getOfficeEnabled());
		ret.addObject("uploadEnabled", docLib.getUploadEnabled());
		ret.addAllObjects(Constants.EDITOR_TYPES);
		ret.addObject("docLibId", docLibId);
		ret.addObject("docLibType", docLibType);
		ret.addObject("isPersonalLib",(docLibType.byteValue() == Constants.PERSONAL_LIB_TYPE.byteValue()));
		ret.addObject("isGroupLib", (docLibType.byteValue() == Constants.GROUP_LIB_TYPE.byteValue()));
		ret.addObject("isEdocLib", (docLibType.byteValue() == Constants.EDOC_LIB_TYPE.byteValue()));		
		ret.addObject("noShare", (docLibType.byteValue() == Constants.EDOC_LIB_TYPE.byteValue() || docLibType.byteValue() == Constants.PROJECT_LIB_TYPE.byteValue()));
		ret.addObject("theLib", docLib);
		ret.addObject("isShareAndBorrowRoot", BooleanUtils.toBoolean(request.getParameter("isShareAndBorrowRoot")));
	}

	public static void renderSearchConditions(ModelAndView ret, DocLibManager docLibManager, Long docLibId) {
		DocLib lib = docLibManager.getDocLibById(docLibId);
		List<DocMetadataDefinition> searchConditions = docLibManager.getSearchConditions4DocLib(docLibId, lib.getType());
		setDocMetadataDefinitionNames(searchConditions, lib.getType());
		ret.addObject("searchConditions", searchConditions);
		
		List<DocMetadataDefinition> miscConditions = docLibManager.getMiscSearchConditions4DocLib(searchConditions);
		setDocMetadataDefinitionNames(miscConditions, lib.getType());
		ret.addObject("miscConditions", miscConditions);
	}

	/**
	 * 根据文档类型获取其是否归档及如果归档，对应的打开链接地址
	 */
	public static PigUrlInfo getPigUrlInfo(HttpServletRequest request, DocResource dr, boolean isPersonalLibOwner, DocAclManager docAclManager) {
		boolean pig = false;
		String url = "";
		long id = dr.getId();
		if (dr.getFrType() == Constants.SYSTEM_COL || dr.getFrType() == Constants.SYSTEM_ARCHIVES) {
			pig = true;
			String openFrom = request.getParameter("openFrom");
			String lenPotent="";
			if("lenPotent".equals(openFrom) || "BorrowMsg".equals(request.getParameter("fromFlag"))){
				openFrom = "lenPotent";
				lenPotent = docAclManager.getEdocBorrowPotent(id);
			} else {
				lenPotent = docAclManager.getEdocSharePotent(id);
			}
			
			if(dr.getFrType() == Constants.SYSTEM_COL) {
				if (isPersonalLibOwner) {
					lenPotent = "111";
				}
				url = "/collaboration.do?method=detail&from=Done&affairId=" + dr.getSourceId() + "&type=doc&lenPotent=" + lenPotent;
			}
			else {
				url = "/edocController.do?method=edocDetailInDoc&openFrom=" + openFrom + "&summaryId=" + dr.getSourceId()+"&lenPotent=" +lenPotent;
			}
		} else if (dr.getFrType() == Constants.SYSTEM_MEETING) {
			pig = true;
			url = "/mtMeeting.do?method=detail&id=" + dr.getSourceId();
		} else if (dr.getFrType() == Constants.SYSTEM_PLAN) {
			pig = true;
			url = "/plan.do?method=initDetailHome&editType=doc&id=" + dr.getSourceId();
		} else if (dr.getFrType() == Constants.SYSTEM_MAIL) {
			pig = true;
			url = "/webmail.do?method=showMail&id=" + dr.getSourceId();
		} else if (dr.getFrType() == Constants.SYSTEM_INQUIRY) {
			pig = true;
			url = "/inquirybasic.do?method=pigeonhole_detail&id=" + dr.getSourceId();
		}
		// 已归档的公告、新闻添加标识参数，避免作为关联文档被查看时出现错误防护提示信息
		else if (dr.getFrType() == Constants.SYSTEM_NEWS) {
			pig = true;
			url = "/newsData.do?method=userView&id=" + dr.getSourceId() + "&fromPigeonhole=true";
		} else if (dr.getFrType() == Constants.SYSTEM_BULLETIN) {
			pig = true;
			url = "/bulData.do?method=userView&id=" + dr.getSourceId() + "&fromPigeonhole=true";
		}else if (dr.getFrType() == Constants.SYSTEM_INFOSTAT){
			pig = true;
			url = "/infoStatController.do?method=showCheckResultDetail&t=1354543782062&id=" + dr.getSourceId();
		}else if(dr.getFrType() == Constants.SYSTEM_INFO){//xiangfan添加，对应的信息报送的url			
			pig = true;
			url = "/infoDetailController.do?method=detail&summaryId="+dr.getSourceId()+"&affairId=&from=Done&openFrom=doc";
		}
		
		//关联文档的打开需要传入前一对象ID
		//用于权限校验传递，请勿改动
		if(Strings.isNotBlank(request.getParameter("baseObjectId"))){
			url += "&openFrom=glwd&baseObjectId=" + request.getParameter("baseObjectId") + "&baseApp=" + request.getParameter("baseApp");
			if(Strings.isNotBlank(request.getParameter("openerSummaryId"))){
				url += "&openerSummaryId=" + request.getParameter("openerSummaryId");
			}
		}
		//解决归档的协同等发送到其他文件夹，权限丢失的问题
		url += "&docResId=" + id + "&docId=" + id; 
		
		return new PigUrlInfo(pig, url);
	}
	
	private static List<Long> FILTER_TYPES = new ArrayList<Long>();
	static {
		FILTER_TYPES.add(Constants.FOLDER_TEMPLET);
		FILTER_TYPES.add(Constants.FOLDER_SHAREOUT);
		FILTER_TYPES.add(Constants.FOLDER_BORROWOUT);
		FILTER_TYPES.add(Constants.FOLDER_BORROW);
		FILTER_TYPES.add(Constants.FOLDER_PLAN_WEEK);
		FILTER_TYPES.add(Constants.FOLDER_PLAN_MONTH);
		FILTER_TYPES.add(Constants.FOLDER_PLAN_DAY);
		FILTER_TYPES.add(Constants.FOLDER_PLAN_WORK);
		FILTER_TYPES.add(Constants.FOLDER_SHARE);
	}
	
	/**
	 * 对数据进行过滤
	 * @param needNoFilter	是否不需过滤，如果不需要过滤，则直接返回全部记录，否则进行处理
	 */
	public static List<DocResource> getListDocResource(List<DocResource> drs, boolean needNoFilter) {
		if (drs == null || needNoFilter) {
			return drs;
		}
		
		List<DocResource> list = new ArrayList<DocResource>(drs.size());
		for (DocResource docResource : drs) {
			if (!FILTER_TYPES.contains(docResource.getFrType())) {
				list.add(docResource);
			}
		}
		return list;
	}
	
	/**
	 * 获取图片文件的显示内容
	 * @param sourceId	图片源文件ID
	 * @param drCreateTime	文件创建日期
	 * @return	图片文件的显示内容
	 */
	public static String getPicBody(Long sourceId, Date drCreateTime) {
		String result =  "<img border='0' alt='' width='720' src='/seeyon/fileUpload.do?method=showRTE&amp;fileId=" + sourceId + 
						   "&amp;createDate=" + Datetimes.formatDate(drCreateTime) + "&amp;type=image' />";
		return result;
	}
	
	/**
	 * 根据文档获取所在文档库的管理员，如果是在项目文档库，则取项目的负责人和项目助理
	 */
	public static List<Long> getLibOwners(DocResource dr) {
		if(Constants.DOC_LIB_ID_PROJECT.longValue() == dr.getDocLibId()) {
			return DocMVCUtils.getProjectFolderOwners(dr.getLogicalPath());
		}
		else {
			DocLibManager docLibManager = (DocLibManager)ApplicationContextHolder.getBean("docLibManager");
			return docLibManager.getOwnersByDocLibId(dr.getDocLibId());
		}
	}
	
	/**
	 * 项目文档库情况下，将项目负责人和项目助理作为文档库的管理员，辅助文档排序的权限判断
	 * @param logicalPath	项目文档夹的逻辑路径
	 * @return	项目负责人和助理的ID集合
	 */
	public static List<Long> getProjectFolderOwners(String logicalPath) {
		try {
		     String[] docIdsArray = StringUtils.split(logicalPath, '.');
		     if(docIdsArray.length == 1)
		    	 return null;
		     
			 String pdocId = docIdsArray[1];
			 DocHierarchyManager docHierarchyManager = (DocHierarchyManager)ApplicationContextHolder.getBean("docHierarchyManager");
			 
			 DocResource pdoc = docHierarchyManager.getDocResourceById(NumberUtils.toLong(pdocId));
			 if(pdoc != null && pdoc.getSourceId() != null) {
				 ProjectManager projectManager = (ProjectManager)ApplicationContextHolder.getBean("projectManager");
				 ProjectCompose projectCompose = projectManager.getProjectComposeByID(pdoc.getSourceId(), true);
				 
				 List<V3xOrgMember> principalAndAssistants = projectCompose.getAllProjectMembers(ProjectConstants.MEMBERTYPE_PRINCIPAL, 
						 ProjectConstants.MEMBERTYPE_ASSISTANT);
				 
				 return FormBizConfigUtils.getEntityIds(principalAndAssistants);
			 }
	    } 
		catch (Exception e) {
		      logger.error("项目文档授权时，通过项目文档夹[逻辑路径=" + logicalPath + "]的sourceId获得ProjectSummary异常！", e);
	    }
	    
	    return null;
	}
	
	/**
	 * 获取元数据属性信息在文档列表显示的列名称
	 * @param dmdName	元数据属性名称
	 */
	public static String getDisplayName4MetadataDefinition(String dmdName) {
		String aname = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, dmdName);
		if (aname.equals(dmdName)) {
			String resName = Constants.getResourceNameOfMetadata(aname, "");
			if (Strings.isNotBlank(resName)) {
				aname = ResourceBundleUtil.getString(resName, aname);
			}
		}
		return aname;
	}
	
	/**
	 * 根据文档库设置的属性显示信息确定是否需要在显示列表时抓取元数据信息，避免无谓sql发出
	 * @param dmds	文档库所对应的栏目显示信息
	 */
	public static boolean needFetchMetadata(List<DocMetadataDefinition> dmds) {
		for(DocMetadataDefinition dmd : dmds) {
			if(!dmd.getIsDefault())
				return true;
		}
		return false;
	}
	 
}
