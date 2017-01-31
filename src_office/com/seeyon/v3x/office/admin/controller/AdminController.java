package com.seeyon.v3x.office.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.meetingroom.manager.MeetingRoomManagerCAP;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meetingroom.manager.MeetingRoomManager;
import com.seeyon.v3x.office.admin.domain.MAdminInfo;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.domain.MAdminSettingId;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.office.admin.util.Constants;
import com.seeyon.v3x.office.asset.manager.AssetManager;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.book.manager.BookManager;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.stock.manager.StockManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2007-04-15
 */
@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class AdminController extends BaseManageController {
	
	private static final Log log = LogFactory.getLog(AdminController.class);
	
	private AdminManager officeAdminManager;

	private OrgManager orgManager;

	private AutoManager autoManager;

	private AssetManager assetManager;

	private BookManager bookManager;

	private StockManager stockManager;

	private MeetingRoomManagerCAP meetingRoomManagerCAP;
	
	private AppLogManager applogManager ;
	
	private MeetingRoomManager meetingRoomManager;

	public void setMeetingRoomManagerCAP(MeetingRoomManagerCAP meetingRoomManagerCAP) {
		this.meetingRoomManagerCAP = meetingRoomManagerCAP;
	}

	public AdminManager getOfficeAdminManager() {
		return officeAdminManager;
	}

	public void setOfficeAdminManager(AdminManager officeAdminManager) {
		this.officeAdminManager = officeAdminManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public ModelAndView jumpUrl(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String url = request.getParameter("url");
		ModelAndView mav = new ModelAndView(url);
		return mav;
	}

	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("office/admin/index");
		return mav;
	}

	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String condition = request.getParameter("condition");
		String keyWord = request.getParameter("textfield");
		/*
		 * if(!Pagination.isNeedCount()){ int size =
		 * this.officeAdminManager.getAdminSettingCount(countSql);
		 * Pagination.setRowCount(size); }
		 */
		List list = this.officeAdminManager.findAdminSetting(Long.valueOf(user.getLoginAccount()), condition, keyWord);
		//ArrayList mArr = new ArrayList();
		ArrayList arr = new ArrayList();
		if (list != null) {
			//数据录入时，已经合并同人同范围的记录了
//			out: for (int i = 0; i < list.size(); i++) {
//				MAdminSetting admin = (MAdminSetting) list.get(i);
//				for (int j = 0; j < mArr.size(); j++) {
//					String[] s_id = (String[]) mArr.get(j);
//					if (s_id[0].equals(String.valueOf(admin.getId().getAdmin())) && s_id[1].equals(admin.getAdminModel())) {
//						continue out;
//					}
//				}
//				mArr.add(new String[] {String.valueOf(admin.getId().getAdmin()), admin.getAdminModel() });
//			}
			for (int i = 0; i < list.size(); i++) {
				MAdminSetting admin = (MAdminSetting) list.get(i);
				MAdminInfo info = new MAdminInfo();
				info.setAdmin(admin.getId().getAdmin());
				info.setAdmin_model(admin.getAdminModel());
				info.setDomainId(user.getLoginAccount());
				char[] c_model = admin.getAdminModel().toCharArray();
				for (int j = 0; j < c_model.length; j++) {
					if (c_model[j] == '1') {
						if (info.getModelName() != null && info.getModelName().length() > 0) {
							info.setModelName(info.getModelName() + ",");
						} else {
							info.setModelName("");
						}
						if (j == 0) {
							info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.auto", new Object[0]));
						} else if (j == 1) {
							info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.asset", new Object[0]));
						} else if (j == 2) {
							info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.book", new Object[0]));
						} else if (j == 3) {
							info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.stock", new Object[0]));
						}
						// start add by liusg
						else if (j == 4) {
							info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.meetingroom", new Object[0]));
						}
						// end add by liusg
					}
				}
				V3xOrgMember member = this.orgManager.getMemberById(admin.getId().getAdmin());
				info.setAdminName(member==null?"":member.getName());
				//不合并同人同模块不同范围的记录
//				info.setDepArr(this.officeAdminManager.getAdminDepartments(info.getAdmin(), info.getAdmin_model()));
				ArrayList depArr = new ArrayList();
				//单位
				if(admin.getId().getMngdepId().equals(String.valueOf(admin.getDomainId()))){
					V3xOrgAccount account = this.orgManager.getAccountById(admin.getDomainId());
					depArr.add(account.getName());
				}else{
					V3xOrgDepartment department = this.orgManager.getDepartmentById(Long.parseLong(admin.getId().getMngdepId()));
					depArr.add(department.getName());
				}
				info.setDepIdArr(admin.getId().getMngdepId());
				info.setDepArr(depArr); 
				if (info.getDepArr() != null) {
					for (int j = 0; j < info.getDepArr().size(); j++) {
						if (info.getDepStr() == null || info.getDepStr().length() == 0) {
							info.setDepStr((String) info.getDepArr().get(j));
						} else {
							info.setDepStr(info.getDepStr() + "," + info.getDepArr().get(j));
						}
					}
				}
				arr.add(info);
			}
		}
//		int count = arr.size();
//		Pagination.setRowCount(count);
//		int endIndex = Pagination.getFirstResult() + Pagination.getMaxResults() > (count - 1) ? (count - 1) : Pagination.getFirstResult() + Pagination.getMaxResults();
//		ArrayList pageArr = new ArrayList();
//		for (int i = Pagination.getFirstResult(); i <= endIndex; i++) {
//			pageArr.add(arr.get(i));
//		}
		ModelAndView mav = new ModelAndView("office/admin/list");
		if (request.getAttribute("script") != null && ((String) request.getAttribute("script")).length() > 0) {
			mav.addObject("script", request.getAttribute("script"));
		}
		mav.addObject("list", arr);
		return mav;
	}

	public ModelAndView create_admin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("office/admin/create_admin");
		if (request.getParameter("showFlag") != null
				&& request.getParameter("showFlag").equals("1")) {
			mav.addObject("showFlag", 1);
		}

		mav.addObject("assetType", OfficeModelType.asset_type);
		mav.addObject("autoType", OfficeModelType.auto_type);
		mav.addObject("bookType", OfficeModelType.book_type);
		mav.addObject("stockType", OfficeModelType.stock_type);
		mav.addObject("meetingRoomType", OfficeModelType.meeting_type);
		return mav;
	}

	public ModelAndView doCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] accStrTemp = request.getParameter("accountId").split(",");
		String[] depStrTemp = request.getParameter("departmentId").split(",");
		String[] accStr, depStr;

		// -----------
		ArrayList accArr = new ArrayList();
		ArrayList depArr = new ArrayList();
		for (int i = 0; i < accStrTemp.length; i++) {
			accArr.add(accStrTemp[i]);
			depArr.add(depStrTemp[i]);
		}
		accStr = new String[accArr.size()];
		depStr = new String[depArr.size()];
		for (int i = 0; i < accArr.size(); i++) {
			accStr[i] = (String) accArr.get(i);
			depStr[i] = (String) depArr.get(i);
		}
		
		//pxb 修改，创建管理员时，一个模块只能创建一个管理员，否则返回提示信息
		User user = CurrentUser.get();
//		List<MAdminSetting> listAdmins = officeAdminManager.findAdminSetting(Long.valueOf(user.getLoginAccount()), null, null);
//		String[] adminModels = { "0", "0", "0", "0", "0" };
//		for (MAdminSetting mAdminSetting : listAdmins) {
//			String adminModel = mAdminSetting.getAdminModel();
//			String[] str = adminModel.split("");
//			for (int i = 1; i < str.length; i++) {
//				if (str[i].equals("1")) adminModels[i-1] = "1";
//			}
//		}
		
		MAdminSetting admin = new MAdminSetting();
		MAdminSettingId id = new MAdminSettingId();
		id.setAdmin(Long.parseLong(request.getParameter("admin")));
		String[] adminModel = request.getParameterValues("adminModel");
		String[] models = { "0", "0", "0", "0", "0" };// modify by liusg
		for (int i = 0; i < adminModel.length; i++) {
			if (adminModel[i].equals("1")) {
				models[0] = "1";
			} else if (adminModel[i].equals("2")) {
				models[1] = "1";
			} else if (adminModel[i].equals("3")) {
				models[2] = "1";
			} else if (adminModel[i].equals("4")) {
				models[3] = "1";
			}// start add by liusg
			else if (adminModel[i].equals("5")) {
				models[4] = "1";
			}// end add by liusg
		}
		//过滤，如果有重复的管理员，则给提示：XXXX已有重复的管理员;
//		boolean hasTwoManager = false;
//		int twoManagerModel = 0;
//		for (int i = 0; i < models.length; i++) {
//			if (models[i].equals("1") && adminModels[i].equals("1")) {
//				hasTwoManager = true;
//				twoManagerModel = i;
//				break;
//			}
//		}
		
//		if (hasTwoManager) {
//			super.rendJavaScript(response, "alert(parent.v3x.getMessage('officeLang.detail_repeat_admin"+twoManagerModel+"'));");
//			return null;
//		}else{
		//TODO youhb 如果是新创建就不移交之前管理员的操作
//			if (models[0].equals("1")) {
//				autoManager.updateAutoMangerBatch(0l, Long.parseLong(request.getParameter("admin")), user,user.getAccountId());
//			}
//			if (models[1].equals("1")) {
//				assetManager.updateAssetMangerBatch(0l, Long.parseLong(request.getParameter("admin")), user,false);
//			}
//			if (models[2].equals("1")) {
//				bookManager.updateBookMangerBatch(0l, Long.parseLong(request.getParameter("admin")), user,false);
//			}
//			if (models[3].equals("1")) {
//				stockManager.updateStockMangerBatch(0l, Long.parseLong(request.getParameter("admin")), user,false);
//			}
//			if (models[4].equals("1")) {
//				meetingRoomManager.updateMeetingRoomMangerBatch(0l,Long.parseLong(request.getParameter("admin")), user,false);
//			}
//		}
		
		admin.setId(id);
		admin.setAdminModel(models[0] + models[1] + models[2] + models[3] + models[4]);// modify by liusg
		if (request.getParameter("adminMemo") != null && request.getParameter("adminMemo").length() > 0) {
			admin.setAdminMemo(request.getParameter("adminMemo"));
		}
		admin.setCreateDate(new java.util.Date());
		admin.setDelFlag(0);
		ModelAndView mav = new ModelAndView("office/admin/create_admin");
		try {
			for (int i = 0; i < accStr.length; i++) {
				try {
					for (int j = 0; j < models.length; j++) {
						if (models[j].equals("1")) {
							if (this.checkExists(id.getAdmin(), Long.parseLong(accStr[i]), Long.parseLong(depStr[i]), (j + 1))) {
								throw new Exception("\u7BA1\u7406\u5458\u7684\u7BA1\u7406\u8303\u56F4\u5DF2\u5B58\u5728");
							}
						}
					}
				} catch (org.hibernate.ObjectNotFoundException ex) {					
					log.error("",ex) ;
				} catch (Exception ex) {
					log.error("",ex) ;
					throw ex;
				}
			}
			for (int i = 0; i < accStr.length; i++) {
				MAdminSetting tempAdmin = new MAdminSetting();
				tempAdmin.setAccountName(admin.getAccountName());
				tempAdmin.setAdminMemo(admin.getAdminMemo());
				tempAdmin.setAdminModel(admin.getAdminModel());
				tempAdmin.setAdminName(admin.getAdminName());
				tempAdmin.setCreateDate(admin.getCreateDate());
				tempAdmin.setDelFlag(admin.getDelFlag());
				tempAdmin.setDepartmentName(admin.getDepartmentName());
				tempAdmin.setModelStr(admin.getModelStr());
				tempAdmin.setModifyDate(admin.getModifyDate());
				MAdminSettingId tempId = new MAdminSettingId();
				tempId.setAdmin(new Long(id.getAdmin().longValue()));
				// tempId.setMngdepId(accStr[i]+","+depStr[i]);
				tempId.setMngdepId(depStr[i]);
				tempAdmin.setId(tempId);
				Long accId = Long.parseLong(accStr[i]);
				//办公设置的所属单位
				tempAdmin.setAccountId(CurrentUser.get().getLoginAccount());
				tempAdmin.setDomainId(accId);
				try {
					// 搜索出 管理员所管理的相同单位下的所有模块
					List list = this.officeAdminManager.getAdminSettingById(accId, tempId.getAdmin(), null, null, false);
					int listSize = list.size();
					if (listSize > 0) {
						for (int k = 0; k < listSize; k++) {
							try {
								MAdminSetting tempUpdate = (MAdminSetting) list.get(k);
								if (tempUpdate.getId().getMngdepId().equals(tempId.getMngdepId())) {
									// 判定0: 如果管理的范围是一样(是同一单位或者部门)的,修改管理的模块(取合集)
									String updateModel = tempUpdate.getAdminModel();
									String tempUpdateModel = "";
									for (int j = 0; j < updateModel.length(); j++) {
										if (tempAdmin.getAdminModel().charAt(j) == '1') {
											tempUpdateModel += String.valueOf(tempAdmin.getAdminModel().charAt(j));
										} else {
											tempUpdateModel += String.valueOf(tempUpdate.getAdminModel().charAt(j));
										}
									}
									tempUpdate.setAdminModel(tempUpdateModel);
									this.officeAdminManager.updateAdminSetting(tempUpdate);
								} else {
									//判定1：新旧数据的管理范围都不是单位
									if (!new Long(tempUpdate.getDomainId()).toString().equals(tempUpdate.getId().getMngdepId())
											&& !new Long(tempAdmin.getDomainId()).toString().equals(tempAdmin.getId().getMngdepId())) {
										V3xOrgDepartment depU = this.orgManager.getDepartmentById(new Long(tempUpdate.getId().getMngdepId()));
										V3xOrgDepartment depT = this.orgManager.getDepartmentById(new Long(tempAdmin.getId().getMngdepId()));
										//新旧数据的单位一致
										if(tempUpdate.getDomainId().equals(tempAdmin.getDomainId())){
											//新旧数据的管理部门时候有包含关系
											//判定1.1：新数据的管理范围包含历史数据的管理范围（删除(修正)历史数据，新增记录）
											if((depU.getPath()+".").startsWith(depT.getPath()+".")){
												//管理模块一致
												if (tempUpdate.getAdminModel().equals(tempAdmin.getAdminModel())) {
													this.officeAdminManager.deleteAdminSettingForUpdate(tempUpdate);
													this.officeAdminManager.saveAdminSetting(tempAdmin);
												} else {
													String module = compareModule(tempAdmin.getAdminModel(), tempUpdate.getAdminModel());
													//历史数据管理模块少
													if (module.indexOf('1') < 0) {
														this.officeAdminManager.deleteAdminSettingForUpdate(tempUpdate);
													} else {
														//历史数据管理模保存新数据没有的模块
														tempUpdate.setAdminModel(module);
														this.officeAdminManager.updateAdminSetting(tempUpdate);
													}
													this.officeAdminManager.saveAdminSetting(tempAdmin);
												}
											}
											//判定1.2：历史数据的管理范围包含新数据的管理范围（只增加管理模块）
											else if((depT.getPath()+".").startsWith(depU.getPath()+".")){
												String module = compareModule(tempUpdate.getAdminModel(), tempAdmin.getAdminModel());
												if (module.indexOf('1') >= 0) {
													tempAdmin.setAdminModel(module);
													this.officeAdminManager.saveAdminSetting(tempAdmin);
												}
											}else{
												//判定1.3：相同单位下的无直属关系的部门直接保存
												this.officeAdminManager.saveAdminSetting(tempAdmin);
											}
										}else{
											//判定1.4：不同单位下的部门直接保存
											this.officeAdminManager.saveAdminSetting(tempAdmin);
										}
										//判定2：历史数据的管理范围不是单位 而新数据的管理范围是单位
									} else if (!new Long(tempUpdate.getDomainId()).toString().equals(tempUpdate.getId().getMngdepId())
											&& new Long(tempAdmin.getDomainId()).toString().equals(tempAdmin.getId().getMngdepId())) {
										//判定2.1 历史数据的管理范围（部门）直属于新数据的管理范围(删历史数据)
										if(tempUpdate.getDomainId().equals(tempAdmin.getDomainId())){
											String module = compareModule(tempAdmin.getAdminModel(), tempUpdate.getAdminModel());
											if (module.indexOf('1') >= 0) {
												tempUpdate.setAdminModel(module);
												this.officeAdminManager.updateAdminSetting(tempUpdate);
											} else {
												this.officeAdminManager.deleteAdminSettingForUpdate(tempUpdate);
											}
										}
										//判定2.2 历史数据的管理范围（部门）不直属于新数据的管理范围
										this.officeAdminManager.saveAdminSetting(tempAdmin);
										//判定3：历史数据的管理范围是单位 而新数据的管理范围不是单位
									} else if (new Long(tempUpdate.getDomainId()).toString().equals(tempUpdate.getId().getMngdepId())
											&& !new Long(tempAdmin.getDomainId()).toString().equals(tempAdmin.getId().getMngdepId())) {
										//判定3.1 新数据的管理范围（部门）直属于历史数据的管理范围(比较新数据)
										if(tempAdmin.getDomainId().toString().equals((tempUpdate.getDomainId()))){
											String module = compareModule(tempUpdate.getAdminModel(), tempAdmin.getAdminModel());
											if (module.indexOf('1') >= 0) {
												tempAdmin.setAdminModel(module);
												this.officeAdminManager.saveAdminSetting(tempAdmin);
											}
											//判定3.2 新数据的管理范围（部门）不直属于历史数据的管理范围
										}else{
											this.officeAdminManager.saveAdminSetting(tempAdmin);
										}
									} else {
										try {

											//判定4：历史数据和新数据的管理范围都是单位（相同数据的已经在上面判定了，这里肯定是不同单位的，直接保存）
											this.officeAdminManager.saveAdminSetting(tempAdmin);
										} catch (Exception ex) {
											log.error("不存在的分支出现问题",ex) ;
										}
									}
								}
							} catch (Exception ex) {
								log.error("存在的分支出现问题",ex) ;
							}
						}
					} else {
						try {
							this.officeAdminManager.saveAdminSetting(tempAdmin);
						} catch (Exception ex) {
							log.error("不存在的分支出现问题",ex) ;
						}
					}
					this.applogManager.insertLog(CurrentUser.get(), AppLogAction.Office_ChangeAuth, CurrentUser.get().getName());
				} catch (Exception ex) {
					log.error("",ex) ;
				}
				// 移交删除的权限人的 东西 start add by dongyajie
				//权限的合并只能是自己的管理模块
//				for (int j = 1; j <= models.length; j++) {
//					if (models[j - 1].equals("1")) {
//						this.officeAdminManager.handOverOffice(j, id.getAdmin(), accId);
//					}
//				}
			}
			mav.addObject("script", "parent.listFrame.listIframe.location.href=parent.listFrame.listIframe.tempUrl;");
		} catch (Exception ex) {
			log.error("",ex) ;
			mav.addObject("script", "alert(\"" + ex.getMessage() + "\");");
		}
		return mav;
	}

	/**
	 * 比较管理的模块 依照reg 将s中与reg中相同的模块去掉
	 * 
	 * @param reg
	 *            不变的
	 * @param s
	 *            要改变的,将其中的设置为0的
	 * @return
	 */
	private String compareModule(String reg, String s) {
		StringBuilder sb = new StringBuilder("");
		char cr, cs;
		for (int i = 0; i < reg.length(); i++) {
			cr = reg.charAt(i);
			cs = s.charAt(i);
			if (cr == '1' && cs == '1') {
				sb.append("0");
			} else if (cs == '1') {
				sb.append("1");
			} else {
				sb.append("0");
			}
		}
		return sb.toString();
	}

	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = (String) request.getAttribute("id");
		if (Strings.isBlank(id)) {
			id = request.getParameter("id");
		}
		String fs = request.getParameter("fs");
		String[] ids = id.split(",");
		MAdminInfo info = new MAdminInfo();
		V3xOrgMember adminMember = this.orgManager.getMemberById(Long.parseLong(ids[0]));
		info.setAdmin(Long.parseLong(ids[0]));
		info.setAdminName(adminMember==null ? "" : adminMember.getName());
		List adminList = new ArrayList();
		if (Strings.isNotBlank(ids[2])) {
			adminList = this.officeAdminManager.getAdminSettingById(null, Long.parseLong(ids[0]), Long.parseLong(ids[2]), ids[1], true);
		} else {
			adminList = this.officeAdminManager.getAdminSettingById(null, Long.parseLong(ids[0]), null, ids[1], true);
		}
		ArrayList depArr = new ArrayList();
		if (adminList != null) {
			for (int i = 0; i < adminList.size(); i++) {
				MAdminSetting admin = (MAdminSetting) adminList.get(i);
				String[] mngdep_Id = admin.getId().getMngdepId().split(",");
				// 增加综合办公单位管理权限
				// ============start edit==========================
				String doMainId = String.valueOf(admin.getDomainId());
				String TempDep = null;
				if (info.getDepIdArr() == null || info.getDepIdArr().length() == 0) {
					info.setDepIdArr(mngdep_Id[0]);// 修改mngdep_Id[1]为mngdep_Id[0]
				} else {
					info.setDepIdArr(info.getDepIdArr() + "," + mngdep_Id[0]);// 修改mngdep_Id[1]为mngdep_Id[0]
				}
				if ((doMainId != null && mngdep_Id[0] != null) && doMainId.equals(mngdep_Id[0])) {
					V3xOrgAccount account = this.orgManager.getAccountById(Long.parseLong(mngdep_Id[0]));
					TempDep = account.getName();
				} else {
					V3xOrgDepartment department = this.orgManager.getDepartmentById(Long.parseLong(mngdep_Id[0]));
					TempDep = department.getName();
				}
				// 修改mngdep_Id[1]为mngdep_Id[0]
				depArr.add(TempDep);
				// ===============end edit==============================
				info.setCreateDate(admin.getCreateDate());
				info.setModifyDate(admin.getModifyDate());
			}
		}
		info.setDepArr(depArr);
		if (info.getDepArr() != null) {
			for (int j = 0; j < info.getDepArr().size(); j++) {
				if (info.getDepStr() == null || info.getDepStr().length() == 0) {
					info.setDepStr((String) info.getDepArr().get(j));
				} else {
					info.setDepStr(info.getDepStr() + "," + info.getDepArr().get(j));
				}
			}
		}
		char[] c_models = ids[1].toCharArray();
		for (int i = 0; i < c_models.length; i++) {
			if (c_models[i] == '1') {
				if (info.getModelName() != null && info.getModelName().length() != 0) {
					info.setModelName(info.getModelName() + ",");
				} else {
					info.setModelName("");
				}
				if (i == 0) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.auto", new Object[0]));
				} else if (i == 1) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.asset", new Object[0]));
				} else if (i == 2) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.book", new Object[0]));
				} else if (i == 3) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.stock", new Object[0]));
				}// start add by liusg
				else if (i == 4) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.meetingroom", new Object[0]));
				}// end add by liusg
			}
		}
		ModelAndView mav = new ModelAndView("office/admin/detail");
		mav.addObject("bean", info);
		if (fs != null && fs.length() > 0) {
			mav.addObject("fs", new Integer(1));
		}
		return mav;
	}

	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id = (String) request.getAttribute("id");
		if (id == null || id.length() == 0) {
			id = request.getParameter("id");
		}
		String[] ids = id.split(",");
		String adminId = ids[0];
		String adminModel = ids[1];
		String depid = ids[2];
		User user = CurrentUser.get();
		MAdminInfo info = new MAdminInfo();
		V3xOrgMember adminMember = this.orgManager.getMemberById(Long.parseLong(adminId));
		info.setAdmin(Long.parseLong(adminId));
		info.setAdmin_model(adminModel);
		info.setAdminName(adminMember==null ? "" : adminMember.getName());
		List adminList = new ArrayList();
		if (Strings.isNotBlank(depid)) {
			adminList = this.officeAdminManager.getAdminSettingById(null, Long.parseLong(adminId), Long.parseLong(depid), adminModel, true);
		} else {
			adminList = this.officeAdminManager.getAdminSettingById(null, Long.parseLong(adminId), null, adminModel, true);
		}
		ArrayList depArr = new ArrayList();
		if (adminList != null) {
			for (int i = 0; i < adminList.size(); i++) {
				MAdminSetting admin = (MAdminSetting) adminList.get(i);
				String mngdep_Id = admin.getId().getMngdepId();
				// 增加综合办公单位管理范围权限
				// =========start edit ==========
				String doMain_Id = String.valueOf(admin.getDomainId());
				String TempDep = null;
				if (info.getDepIdArr() == null || info.getDepIdArr().length() == 0) {
					info.setDepIdArr(mngdep_Id);
					info.setAccountIds(doMain_Id);
				} else {
					info.setDepIdArr(info.getDepIdArr() + "," + mngdep_Id);
					info.setAccountIds(info.getAccountIds() + "," + doMain_Id);
				}
				if ((doMain_Id != null && mngdep_Id != null) && mngdep_Id.equals(doMain_Id)) {
					V3xOrgAccount account = this.orgManager.getAccountById(Long.parseLong(mngdep_Id));
					TempDep = account.getName();
				} else {
					V3xOrgDepartment department = this.orgManager.getDepartmentById(Long.parseLong(mngdep_Id));
					TempDep = department.getName();
				}
				depArr.add(TempDep);
				// ===========start end =================
				info.setCreateDate(admin.getCreateDate());
				info.setModifyDate(admin.getModifyDate());
			}
		}
		info.setDepArr(depArr);
		if (info.getDepArr() != null) {
			for (int j = 0; j < info.getDepArr().size(); j++) {
				if (info.getDepStr() == null || info.getDepStr().length() == 0) {
					info.setDepStr((String) info.getDepArr().get(j));
				} else {
					info.setDepStr(info.getDepStr() + "," + info.getDepArr().get(j));
				}
			}
		}
		char[] c_models = ids[1].toCharArray();
		for (int i = 0; i < c_models.length; i++) {
			if (c_models[i] == '1') {
				if (info.getModelName() != null && info.getModelName().length() != 0) {
					info.setModelName(info.getModelName() + ",");
				} else {
					info.setModelName("");
				}
				if (i == 0) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.auto", new Object[0]));
				} else if (i == 1) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.asset", new Object[0]));
				} else if (i == 2) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.book", new Object[0]));
				} else if (i == 3) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.stock", new Object[0]));
				}// start add by liusg
				else if (i == 4) {
					info.setModelName(info.getModelName() + ResourceBundleUtil.getString(Constants.ADMIN_RESOURCE_NAME, "admin.label.meetingroom", new Object[0]));
				}// end add by liusg
			}
		}
		String check1 = "", check2 = "", check3 = "", check4 = "", check5 = "";// modify by liusg
		for (int i = 0; i < c_models.length; i++) {
			if (c_models[i] == '1') {
				if (i == 0) {
					check1 = "checked";
				} else if (i == 1) {
					check2 = "checked";
				} else if (i == 2) {
					check3 = "checked";
				} else if (i == 3) {
					check4 = "checked";
				}// start add by liusg
				else if (i == 4) {
					check5 = "checked";
				}// end add by liusg
			}
		}
		ModelAndView mav = new ModelAndView("office/admin/edit");
		mav.addObject("bean", info);
		mav.addObject("check1", check1);
		mav.addObject("check2", check2);
		mav.addObject("check3", check3);
		mav.addObject("check4", check4);
		mav.addObject("check5", check5);// modify by liusg
		return mav;
	}

	public ModelAndView doModify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] ids = request.getParameter("id").split(",");
		String adminId = ids[0];
		String admin_Model = ids[1];
		String dep  = ids[2];
		String admin_new = request.getParameter("admin");
		User user = CurrentUser.get();
		String[] accStrTemp = request.getParameter("accountId").split(",");
		String[] depStrTemp = request.getParameter("departmentId").split(",");
		String[] adminModel = request.getParameterValues("adminModel");
		String modelStr = "";
		Map<String, Object> parameter = new HashMap<String, Object>();
		for (int i = 0; i < adminModel.length; i++) {
			if (adminModel[i].equals("1")) { // start modify by caofei 2008 -
												// 9 -17
				modelStr += " admin_model like :admin_model" + i + " ";// modify
																		// by
																		// dongyajie
																		// '1___'
																		// to
																		// '1____'
				parameter.put("admin_model" + i, "1____");
			} else if (adminModel[i].equals("2")) {
				if (modelStr.length() > 0) {
					modelStr += " or ";
				}
				modelStr += " admin_model like :admin_model" + i + " ";
				parameter.put("admin_model" + i, "_1___");
			} else if (adminModel[i].equals("3")) {
				if (modelStr.length() > 0) {
					modelStr += " or ";
				}
				modelStr += " admin_model like :admin_model" + i + " ";
				parameter.put("admin_model" + i, "__1__");
			} else if (adminModel[i].equals("4")) {
				if (modelStr.length() > 0) {
					modelStr += " or ";
				}
				modelStr += " admin_model like :admin_model" + i + " ";
				parameter.put("admin_model" + i, "___1_");
			}
			// end modify by caofei 2008 - 9 - 17
			// start add by caofei 2008 - 9 - 17
			else if (adminModel[i].equals("5")) {
				if (modelStr.length() > 0) {
					modelStr += " or ";
				}
				modelStr += " admin_model like :admin_model" + i + " ";
				parameter.put("admin_model" + i, "____1");
			}
			// end add by caofei 2008 - 9 - 17

		}
		if (modelStr.length() > 0) {
			modelStr = " and (" + modelStr + ")";
		}
		String depStr = "";
		for (int i = 0; i < accStrTemp.length; i++) {
			if (depStr.length() > 0) {
				depStr += ",";
			}
			depStr += "'" + depStrTemp[i] + "'";
		}
		if (depStr.length() > 0) {
			parameter.put(":depStr", depStr);
			depStr = " and mngdep_id in (:depStr)";
		}
		try {
			String sql = "select count(*) as " + Constants.Total_Count_Field
					+ " from m_admin_setting where admin =:adminId " + depStr
					+ modelStr;
			parameter.put("adminId", Long.parseLong(adminId));
			sql += " and admin_model != ? ";
			sql += " and domain_id=? ";
			parameter.put(":adminModel", admin_Model);
			parameter.put("domainId", user.getLoginAccount());
			int count = this.officeAdminManager.getAdminSettingCount(sql,
					parameter);
			if (count != 0) {
				throw new Exception(
						"\u7BA1\u7406\u5458\u7684\u7BA1\u7406\u8303\u56F4\u5DF2\u5B58\u5728");
			}
		} catch (Exception ex) {
			ModelAndView mav = new ModelAndView("office/admin/create_admin");
			mav.addObject("script", "alert(\"" + ex.getMessage()
					+ "\");history.go(-1);");
			return mav;
		}
		try {
			/*String sql = "select * from m_admin_setting where del_flag = "
					+ Constants.Del_Flag_Normal;
			sql += " and admin = " + adminId + " and admin_model = '"
					+ admin_Model + "' and domain_id = "
					+ user.getLoginAccount();*/
			List list = new ArrayList();
			if (Strings.isNotBlank(dep)) {
				list = this.officeAdminManager.getAdminSettingById(null, Long.parseLong(adminId), Long.parseLong(dep),
						admin_Model, true);
			} else {
				list = this.officeAdminManager.getAdminSettingById(null, Long.parseLong(adminId), null,
						admin_Model, true);
			}
			if (list != null) {
				// 添加移交功能 by Yongzhang 2008-6-20
				if (!admin_new.equals(adminId)) {
					long adminIdLong = Long.parseLong(adminId);
					long admin_newLong = Long.parseLong(admin_new);
					for (int i = 0; i < adminModel.length; i++) {

						int key = Character.getNumericValue(adminModel[i]
								.charAt(0));
						switch (key) {
						case OfficeModelType.auto_type:
							autoManager.updateAutoMangerBatch(adminIdLong,
									admin_newLong, user);
							break;
						case OfficeModelType.asset_type:
							assetManager.updateAssetMangerBatch(adminIdLong,
									admin_newLong, user);
							break;
						case OfficeModelType.book_type:
							bookManager.updateBookMangerBatch(adminIdLong,
									admin_newLong, user);
							break;
						case OfficeModelType.stock_type:
							stockManager.updateStockMangerBatch(adminIdLong,
									admin_newLong, user);
							break;
						// start modifyed by dongyajie
						case OfficeModelType.meeting_type:
							meetingRoomManagerCAP.updateMeetingRoomMangerBatch(
									adminIdLong, admin_newLong, user);
							break;
						// end modifyed by dongyajie
						default:
							break;
						}
					}

				}
				for (int i = 0; i < list.size(); i++) {
					MAdminSetting admin = (MAdminSetting) list.get(i);
					this.officeAdminManager.deleteAdminSettingForUpdate(admin);

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			return this.doCreate(request, response);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception();
		}
	}

	public ModelAndView del(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] ids = request.getParameterValues("id");

		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				String[] mId = ids[i].split(",");
				/*String sql = "select * from m_admin_setting where admin = "
						+ mId[0] + " and admin_model = '" + mId[1]
						+ "' and domain_id="
						+ CurrentUser.get().getLoginAccount();*/
				Long depId = null;
				if(mId.length>2){
					depId = Long.parseLong(mId[2]);
				}
				List list = this.officeAdminManager.getAdminSettingById(null, Long
								.parseLong(mId[0]), depId, mId[1], true);

				String ints = "";
				for (Object object : list) {
					MAdminSetting admin = (MAdminSetting) object;
					ints = admin.getId().getMngdepId() + ",";
				}
				if (StringUtils.isNotBlank(ints)) {
					ints = ints.substring(0, ints.length() - 1);

				}

				String sql1 = "select count(*) as "
						+ Constants.Total_Count_Field
						+ " from t_applylist where apply_state =:applyState and apply_depid in (:applyDepId) and apply_mge=:applyMge ";
				Map<String,Object> map = new HashMap<String, Object>();
				map.put(":applyState", 1);
				map.put("applyDepId", ints);
				map.put("applyMge", Long.parseLong(mId[0]));
				int count = this.officeAdminManager.getAdminSettingCount(sql1,map);
				if (count == 0) {
					/*String sql2 = "select * from m_admin_setting where admin = "
							+ mId[0]
							+ " and admin_model = '"
							+ mId[1]
							+ "' and domain_id="
							+ CurrentUser.get().getLoginAccount();
					SQLQuery query1 = this.officeAdminManager
							.findAdminSetting(sql2);*/
					List list1 = this.officeAdminManager.getAdminSettingById(CurrentUser.get().getLoginAccount(), Long.parseLong(mId[0]),
							depId, mId[1], true);
					if (list1 != null) {
						for (int j = 0; j < list1.size(); j++) {
							MAdminSetting admin = (MAdminSetting) list1.get(j);
							this.officeAdminManager.deleteAdminSetting(admin);
						}
					}
				} else {
					request.setAttribute("script", "alert(\""
							+ ResourceBundleUtil
									.getString(Constants.ADMIN_RESOURCE_NAME,
											"admin.alert.delete.confirm",
											new Object[0]) + "\");");
				}
			}
		}
		return this.list(request, response);
	}

	private String createSearchSql(int condition, String keyWord) {
		String searchSql = "";
		switch (condition) {
		case Constants.Search_Condition_Model: {// start modify by liusg
			if (keyWord.equals("1")) {
				searchSql = "admin_model like '1____'";
			} else if (keyWord.equals("2")) {
				searchSql = "admin_model like '_1___'";
			} else if (keyWord.equals("3")) {
				searchSql = "admin_model like '__1__'";
			} else if (keyWord.equals("4")) {
				searchSql = "admin_model like '___1_'";
			} else if (keyWord.equals("5")) {
				searchSql = "admin_model like '____1'";
			}
			break;
		}// end modify by liusg
		case Constants.Search_Condition_Admin: {
			searchSql = "admin in(select mem.id from  v3x_org_member mem where mem.name like '%"
					+ keyWord + "%')";
			break;
		}
		case Constants.Search_Condition_Depart: {
			searchSql = "mngdep_id = '" + keyWord + "'";
			break;
		}
		}
		return searchSql;
	}

	private boolean checkExists(long adminId, long accountId, long depId,
			int model) {
		Map<String,Object> map = new HashMap<String, Object>();
		String sql = "select count(*) as " + Constants.Total_Count_Field
				+ " from m_admin_setting where admin = :admin " 
				+ " and mngdep_id = ':accountId,:depId'";
		map.put("admin", adminId);
		map.put("accountId", accountId);
		map.put("depId", depId);
		switch (model) {
		// start modify by caofei 2008 - 9 - 17
		case 1: {
			sql += " and admin_model like :adminModel ";
			map.put("adminModel", "1____");
			break;
		}
		case 2: {
			sql += " and admin_model like :adminModel ";
			map.put("adminModel", "_1___");
			break;
		}
		case 3: {
			sql += " and admin_model like :adminModel ";
			map.put("adminModel", "__1__");
			break;
		}
		case 4: {
			sql += " and admin_model like :adminModel ";
			map.put("adminModel", "___1_");
			break;
		}
			// end modify by caofei 2008 - 9 - 17
			// start add by caofei 2008 - 9 - 17
		case 5: {
			sql += " and admin_model like :adminModel ";
			map.put("adminModel", "____1");
			break;
		}
			// end add by caofei 2008 - 9 - 17
		}
		int count = this.officeAdminManager.getAdminSettingCount(sql,map);
		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void setAutoManager(AutoManager autoManager) {
		this.autoManager = autoManager;
	}

	public AutoManager getAutoManager() {
		return autoManager;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public BookManager getBookManager() {
		return bookManager;
	}

	public void setBookManager(BookManager bookManager) {
		this.bookManager = bookManager;
	}

	public StockManager getStockManager() {
		return stockManager;
	}

	public void setStockManager(StockManager stockManager) {
		this.stockManager = stockManager;
	}

	public void setApplogManager(AppLogManager applogManager) {
		this.applogManager = applogManager;
	}

	public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
		this.meetingRoomManager = meetingRoomManager;
	}
	

}
