package com.seeyon.v3x.mobile.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputValueAll;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldInputType;

import com.seeyon.cap.meeting.domain.MtReplyCAP;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.bulletin.controller.BulDataController;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.LoginConstants;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Constants;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.parser.StrExtractor;
import com.seeyon.v3x.common.permission.domain.Permission;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageState;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.edoc.domain.EdocBody;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.index.share.datamodel.SearchResult;
import com.seeyon.v3x.mobile.MobileException;
import com.seeyon.v3x.mobile.dao.MobileMessageDao;
import com.seeyon.v3x.mobile.manager.MobileFormBean;
import com.seeyon.v3x.mobile.manager.OAManagerInterface;
import com.seeyon.v3x.mobile.menu.BaseMobileMenu;
import com.seeyon.v3x.mobile.menu.domain.MobileMenuSetting;
import com.seeyon.v3x.mobile.menu.manager.MobileMenuManager;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.mobile.utils.MobileConstants;
import com.seeyon.v3x.mobile.utils.MobileUtil;
import com.seeyon.v3x.mobile.utils.Pagination;
import com.seeyon.v3x.mobile.webmodel.AffairsListObject;
import com.seeyon.v3x.mobile.webmodel.Bulletion;
import com.seeyon.v3x.mobile.webmodel.Calendar;
import com.seeyon.v3x.mobile.webmodel.Collaboration;
import com.seeyon.v3x.mobile.webmodel.Edoc;
import com.seeyon.v3x.mobile.webmodel.EdocItem;
import com.seeyon.v3x.mobile.webmodel.MeetingDetial;
import com.seeyon.v3x.mobile.webmodel.MobileBookEntity;
import com.seeyon.v3x.mobile.webmodel.MobileHistoryMessage;
import com.seeyon.v3x.mobile.webmodel.MobileOrgEntity;
import com.seeyon.v3x.mobile.webmodel.News;
import com.seeyon.v3x.mobile.webmodel.Nodes;
import com.seeyon.v3x.mobile.webmodel.ProcessModeSelector;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.util.Cookies;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.NeedlessCheckLogin;
import com.seeyon.v3x.util.cache.DataCache;

public class MobileController extends BaseController {

	private OAManagerInterface oaManagerInterface;

	private MobileMessageDao mobileMessageDao;
	
	private AffairManager affairManager;
	
	private MobileMessageManager mobileMessageManager;

    private UserMessageManager userMessageManager;
    
    private FileManager fileManager;
    
	private ColManager colManager;
	
	private MetadataManager metadataManager;

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	private MobileMenuManager mobileMenuManager;
     
	private Map<Long, NewCollClass> newCollMap = new HashMap<Long, NewCollClass>();

	private Map<Long, String> actionMap = new HashMap<Long, String>();
	
	private Map<Long,List<Attachment>> attsMap = new HashMap<Long,List<Attachment>>();

	private Map<String, Map<String, TIP_InputValueAll>> formMobileMap = new HashMap<String, Map<String, TIP_InputValueAll>>();
	
	//key -为事项的ID 
	private Map<String,Map<String, TIP_InputValueAll>> formDetial = new HashMap<String,Map<String, TIP_InputValueAll>>();
	//key -为事项的ID 
	private Map<String,Map<String, TIP_InputValueAll>> formDeal = new HashMap<String,Map<String, TIP_InputValueAll>>();
	
	private Map<Long, DealCollClass> dealCollMap = new HashMap<Long, DealCollClass>();
	
	// key - 当前登录用户的id,value  - (key:事项的id,value:表单中选人的表单项)
	private Map<Long,Map<String,TIP_InputValueAll>> formPerson = new HashMap<Long,Map<String,TIP_InputValueAll>>();
	
	private static Map<Long,List<String>> userLock = new HashMap<Long,List<String>>();
	
	private OnLineManager onLineManager;
	
	private OrgManager orgManager;
	
	private SystemConfig systemConfig;
	
	public void setOnLineManager(OnLineManager onLineManager) {
		this.onLineManager = onLineManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public Map<Long, Map<String, TIP_InputValueAll>> getFormPerson() {
		return formPerson;
	}

	public void setFormPerson(Map<Long, Map<String, TIP_InputValueAll>> formPerson) {
		this.formPerson = formPerson;
	}

	public Map<String, Map<String, TIP_InputValueAll>> getFormDeal() {
		return formDeal;
	}

	public void setFormDeal(Map<String, Map<String, TIP_InputValueAll>> formDeal) {
		this.formDeal = formDeal;
	}

	public Map<String, Map<String, TIP_InputValueAll>> getFormDetial() {
		return formDetial;
	}

	public void setFormDetial(Map<String, Map<String, TIP_InputValueAll>> formDetial) {
		this.formDetial = formDetial;
	}

	public Map<String, Map<String, TIP_InputValueAll>> getFormMobileMap() {
		return formMobileMap;
	}

	public void setFormMobileMap(Map<String, Map<String, TIP_InputValueAll>> formMobileMap) {
		this.formMobileMap = formMobileMap;
	}

	public MobileMessageManager getMobileMessageManager() {
		return mobileMessageManager;
	}

	public void setMobileMessageManager(
			MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}

	public MobileMessageDao getMobileMessageDao() {
		return mobileMessageDao;
	}

	public void setMobileMessageDao(MobileMessageDao mobileMessageDao) {
		this.mobileMessageDao = mobileMessageDao;
	}

	public OAManagerInterface getOaManagerInterface() {
		return oaManagerInterface;
	}

	public void setOaManagerInterface(OAManagerInterface oaManagerInterface) {
		this.oaManagerInterface = oaManagerInterface;
	}

    public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }
	
	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	// 设置 新建事项的开关，用户跳出新建事项的正常流程并且 value为true时，清空 用户所有 操作数据：title , content,
	// flowchart
	private Map<Long, Boolean> newMap = new HashMap<Long, Boolean>();

	/**
	 * 为 newColl对象赋值
	 * 
	 * @param title
	 * @param content
	 * @param list
	 */
	private void setNewCollMember(String title, String content,
			List<String[]> list) {
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
		if (nc != null) {
			nc.setContent(Strings.isNotBlank(content)? content : "");
			nc.setTitle(Strings.isNotBlank(title)? title : "");
		} else {
			nc = new NewCollClass();
			nc.setContent(Strings.isNotBlank(content)? content : "");
			nc.setTitle(Strings.isNotBlank(title)? title : "");
			nc.setAllPersonList(list != null ? list: new ArrayList<String[]>());
			newCollMap.put(MobileConstants.getCurrentId(), nc);
		}
	}

	/**
	 * 给 协同模板 中的角色赋值
	 * 
	 * @param key
	 * @param members
	 */
	private void setPersonTemplate(String key, String[] members,String type) {
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());

		Map<String, String[]> roleMember = nc != null ? nc.getRoleMember()
				: new HashMap<String, String[]>();
		List<String[]> list = new ArrayList<String[]>();
		if(key!=null&&key.length()!=0 && null!=members && members.length>0){
			if (roleMember != null) {
				if(roleMember.get(key)!=null && null!=type && !"single".equals(type)){
					String[] oldMembers= roleMember.get(key);
					Map<String,String> tempMap= new HashMap<String,String>();
					for (int i = 0; i < oldMembers.length; i++) {
						String[] aray = oldMembers[i].split("&");
						tempMap.put(aray[0], oldMembers[i]);
					}
					for (int i = 0; i < members.length; i++) {
						String[] aray = members[i].split("&");
						tempMap.put(aray[0], members[i]);
					}
					String[] newMembers= new String[tempMap.size()];
					Iterator<String> iter= tempMap.keySet().iterator();
					int j=0;
					while (iter.hasNext()) {
						String keyId = iter.next();
						String value= tempMap.get(keyId);
						newMembers[j]= value;
						j++;
					}
					roleMember.put(key, newMembers);
				}else{
					roleMember.put(key, members);
				}
			} else {
				roleMember = new HashMap<String, String[]>();
				roleMember.put(key, members);
			}
		}
		if(members!=null){
			for (String s : members) {
				String[] aray = s.split("&");
				list.add(aray);
			}
		}
		if (nc != null) {
			if(key!=null&&key.length()!=0){
				nc.setRoleMember(roleMember);
			}
			for(String[] str : list){
				if(nc.getAllPersonList()!=null){
					if(!isContainSameMemberId(str[0],nc.getAllPersonList()))
						nc.getAllPersonList().add(str);
				}else{
					nc.setAllPersonList(new ArrayList<String[]>());
					nc.getAllPersonList().add(str);
				}
			}
		} else {
			nc = new NewCollClass();
			if(key!=null&&key.length()!=0){
				nc.setRoleMember(roleMember);
			}else{
				nc.setAllPersonList(list);
			}
		}
		newCollMap.put(MobileConstants.getCurrentId(), nc);
	}
	
	/**
	 * 是否已经包含该 id
	 * @param str --id
	 * @param list --含有id的集合
	 * @return
	 */
	private boolean isContainSameMemberId(String str,List<String[]> list){
		if(list!=null){
			for(String[] array : list){
				if(str.equals(array[0]))
					return true;
				else
					continue;
			}
		}
		return false;
	}

	/**
	 * 得到 人员列表
	 * 
	 * @return
	 */
	private  List<String[]>  getPersonList() {
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
		if (nc != null) {
			return nc.getAllPersonList();
		} else {
			return new ArrayList<String[]>();
		}
	}
	
	/**
	 * 得到 角色所匹配的人
	 * @param nid
	 * @return
	 */
	private List<String[]> getRolePersonList(String nid){
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
		if(nc!=null&&nc.getRoleMember()!=null){
			List<String[]> list = new ArrayList<String[]>();
			String[] str = nc.getRoleMember().get(nid);
			if(str!=null){
				for(String s : str){
					if(s!=null){
						String[] strArray = s.split("&");
						list.add(strArray);
					}
				}
			}
			return list;
		}else{
			return null;
		}
	}
	
	/**
	 * 得到 选人的个数
	 * @return
	 */
	private int getPersonNum(String nid){
		List<String> listId = new ArrayList<String>();
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
		if(nc!=null){
			List<String[]> l = new ArrayList<String[]>();
			if(nid==null||nid.length()==0){
				l = nc.getAllPersonList();
			}else{
				String[] s = nc.getRoleMember().get(nid);
				if(null!=s){
					for(String str :s){
						if(str!=null){
							String[] strArray = str.split("&");
							l.add(strArray);
						}
					}
				}
			}
			if(l!=null){
				for(String[] s : l){
					if(!listId.contains(s[0])){
						listId.add(s[0]);
					}
				}
			}
		}
		return listId.size();
	}
	/**
	 * 删除 人员根据 index 
	 * @param index
	 */
	private void removePersonByIndex(int index){
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
		if(nc!=null){
			List<String[]> list = nc.getAllPersonList();
			if(list!=null){
				list.remove(index);
			}
		}
	}

	/**
	 * 得到 标题
	 * 
	 * @return
	 */
	private String getTitle() {
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
		if (nc != null) {
			return nc.getTitle();
		} else {
			return "";
		}
	}

	private String getContent() {
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
		if (nc != null) {
			return nc.getContent();
		} else {
			return "";
		}
	}

	private void setDealCollValue(String id, String opinion, String attitude,
			String button,String type,String pass,boolean track) {
		DealCollClass dealObj = dealCollMap.get(MobileConstants.getCurrentId());
		if (dealObj != null) {
			dealObj.setAttitude(attitude);
			dealObj.setButton(button);
			dealObj.setOpinion(opinion);
			dealObj.setId(id);
			dealObj.setTrack(track);
		} else {
			dealObj = new DealCollClass(opinion, attitude, button, id,type,pass,track);
		}
		dealCollMap.put(MobileConstants.getCurrentId(), dealObj);
	}

	private DealCollClass getDealCollObj(){
		return dealCollMap.get(MobileConstants.getCurrentId());	
	}
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		return null;
	}

	/**
	 * 登录中转调度,将登陆成功后的客户端跳转交给到服务器端处理<br>
	 * 使手机客户端通过校验后直接跳转到首页。
	 */
	public ModelAndView loginTransfer(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		User currentUser = CurrentUser.get();

		String prompt = null;
		if (currentUser == null) {
			prompt = "isNull";
		} else if (currentUser.isAdministrator() || currentUser.isGroupAdmin()
				|| currentUser.isSystemAdmin()) {
			prompt = "isAdmin";
		}
		else {
			List<Long> list = mobileMessageManager.getAccountOfCanUseWap();
			if (!list.isEmpty() && list.contains(currentUser.getAccountId())) {
				//跳转，以免发生刷新重复登录。
				return super.redirectModelAndView("/mob.do?method=showAffairs");
				//return showAffairs(request, response);
			}
			else {
				prompt = "isForbid";
			}
		}
		ModelAndView mav = new ModelAndView("mobile/unsuccess");
		mav.addObject("prompt", prompt);

		return mav;
	}

	class NewCollClass {
		private String title;// 新建的 title

		private String content;// 新建的 content

		private List<String[]> allPersonList;// 新建事项所选的人员

		private Map<String, String[]> roleMember;

		public List<String[]> getAllPersonList() {
			return allPersonList;
		}

		public void setAllPersonList(List<String[]> allPersonList) {
			this.allPersonList = allPersonList;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Map<String, String[]> getRoleMember() {
			return roleMember;
		}

		public void setRoleMember(Map<String, String[]> roleMember) {
			this.roleMember = roleMember;
		}

	}

	class DealCollClass {
		private String opinion;

		private String attitude;

		private String button;

		private String id;
		
		private String type;//form/其他
		
		private String pass;//如果 该协同不是表单，则不用关心
		
		private boolean track;//是否跟踪

		public DealCollClass(String opinion, String attitude, String button,
				String id,String type,String pass,boolean track) {
			this.attitude = attitude;
			this.opinion = opinion;
			this.button = button;
			this.id = id;
			this.type = type;
			this.pass = pass;
			this.track = track;
		}

		public String getAttitude() {
			return attitude;
		}

		public void setAttitude(String attitude) {
			this.attitude = attitude;
		}

		public String getButton() {
			return button;
		}

		public void setButton(String button) {
			this.button = button;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPass() {
			return pass;
		}

		public void setPass(String pass) {
			this.pass = pass;
		}

		public boolean isTrack() {
			return track;
		}

		public void setTrack(boolean track) {
			this.track = track;
		}
		
	}

	/**
	 * 显示登录首页
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showAffairs(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/index");
		User user = CurrentUser.get();
		Long memberId = user.getId();
		deleteAllPerson();//删除数据
		//int count = MessageState.getInstance().getState(userInternalID);得到 系统消息的总个数
		//做什么用？？ add by dongyj
		/*HttpSession session = request.getSession();
		String messageId = (String) session.getAttribute("messageid");
		session.removeAttribute("messageid");
		if (messageId != null) {
			Integer messid = Integer.parseInt(messageId);
			MobileMessage mobileMessage = mobileMessageDao.getMessageById(messid);
			if (mobileMessage.getUid().equals(memberId)) {
				request.setAttribute("id", mobileMessage.getObjectId());
				if (mobileMessage.getType() == ApplicationCategoryEnum.collaboration.getKey()) {
					mav = showPendingCollaborationDetial(request, response);
				} else if (mobileMessage.getType() == ApplicationCategoryEnum.meeting.getKey()) {
					mav = showMeetDetial(request, response);
				}
				return mav;
			}
		}*/
		List<BaseMobileMenu> menus = mobileMenuManager.listMenuByUser(memberId, user.getLoginAccount());
		mav.addObject("menus", menus);
		//待办事项
		getPendingSearchList(request, response, mav);
		List<String> countMenu = new ArrayList<String>();
		for(BaseMobileMenu menu : menus){
			if("6".equals(menu.getId()) || "9".equals(menu.getId())){
				countMenu.add(menu.getId());
			}
		}
		Map<String, Integer> map = oaManagerInterface.getHomePageInfo(memberId,countMenu);
		mav.addObject("map", map);
		return mav;
	}

	/**
	 * 待办事项 入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView pendingAffairListEntrance(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/pendingList");

		getPendingSearchList(request, response, mav);

		return mav;
	}

	/**
	 * 跟踪事项 入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView followAffairListEntrance(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/trackList");

		getTrackSearchList(request, response, mav);

		return mav;
	}

	/**
	 * 协同办公 入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView collAffairNumEntrance(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/index");

		Map<String, Integer> map = oaManagerInterface.getCollaborationNumWithType(MobileConstants.getCurrentId());
		mav.addObject("map", map);
		
		deleteAllPerson();
		attsMap.remove(MobileConstants.getCurrentId());//清除新建协同中的附件
		return mav;

	}

	/**
	 * 给 某个ModelAndView添加Object
	 * 
	 * @param total
	 *            事项的总个数
	 * @param currentPage
	 *            当前的页码
	 * @param mav
	 *            ModelAndView对象
	 * @param getTotal
	 *            总的页码数
	 * @param getAffiarTotal
	 *            总的事项个数
	 */
	private void addObjectMethod(int total, int currentPage, ModelAndView mav,
			int getTotal, int getAffiarTotal, List affairList) {
		MobileUtil mobileUtil = new MobileUtil();
		int pageCount = 1;
		if (total != 0) {
			pageCount = mobileUtil.getPageCount(total,MobileConstants.PAGE_COUNTER);
		} else {
			pageCount = getTotal == 0 ? 1 : getTotal;
			total = getAffiarTotal;
		}
		mav.addObject("pagecount", pageCount); // 总共的页码
		mav.addObject("affairTotal", total);// 总共事项的个数
		mav.addObject("pagenumber", affairList.size()!=0?currentPage:pageCount);// 当前的页码
		mav.addObject("affairlist", affairList);// 事项的List
	}

	/**
	 * 确定 事项
	 * 
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView confirmAffair(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {

		String affairType = request.getParameter("affairtype");
		String affairId = request.getParameter("id");

		if (affairType != null && affairType.length() != 0 && affairId != null
				&& affairId.length() != 0) {
			return switchAffairByTypeAndId(affairType, affairId, request,response);
		} else {
			return null;
		}
	}

	/**
	 * 跳转到指定的 Affair的方法
	 * 
	 * @param type
	 *            事项的Type
	 * @param id
	 *            事项的id
	 * @return
	 * @throws MobileException
	 */
	private ModelAndView switchAffairByTypeAndId(String type, String id,
			HttpServletRequest request, HttpServletResponse response)
			throws MobileException {
		Long currntMemberId = MobileConstants.getCurrentId();
		ModelAndView mav = new ModelAndView();
		int aType = Integer.parseInt(type);
		switch (ApplicationCategoryEnum.valueOf(aType)) {
		case collaboration:
			try {
				Collaboration coll  = oaManagerInterface.CollaborationDetial(Long
						.parseLong(id), currntMemberId);
				if(coll == null){
					Map<String,String> parameter = new HashMap<String,String>();
					parameter.put("search", request.getParameter("search"));
					return mobilePrompt(MobileConstants.getValueFromMobileRes("col.delete.message"), "/mob.do?method=searchEntrance", parameter);
				}
				mav = switchCollaborationByObj(coll, request,response);
			} catch (NumberFormatException e) {
				logger.info("switchAffairByTypeAndId方法中的id转换异常:", e);
				throw new MobileException(e);
			} catch (ColException e) {
				//logger.info("得到协同的详细信息异常：", e);
				Map<String,String> parameter = new HashMap<String,String>();
				parameter.put("search", request.getParameter("search"));
				return mobilePrompt(e.getMessage(), "/mob.do?method=searchEntrance", parameter);
				//throw new MobileException(e);
			}catch(Exception e){
				Map<String,String> parameter = new HashMap<String,String>();
				parameter.put("search", request.getParameter("search"));
				String message = e.getMessage();
				if(Strings.isBlank(message) && e.getCause() != null){
					message = e.getCause().getMessage();
				}
				return mobilePrompt(e.getMessage(), "/mob.do?method=searchEntrance", parameter);
			}
			break;
		case meeting:
			mav = showMeetDetial(request, response);
			break;
		case news:
			mav = showNewsDetial(request, response);
			break;
		case bulletin:
			mav = showBulletionDetial(request, response);
			break;
		case calendar:
			mav = showCalendarDetial(request, response);
			break;
		}
		return mav;
	}

	/**
	 * 跳转到相应的协同页面根据 协同对象
	 * 
	 * @param coll
	 * @return
	 */
	private ModelAndView switchCollaborationByObj(Collaboration coll,
			HttpServletRequest request, HttpServletResponse response)
			throws MobileException {
		ModelAndView mav = new ModelAndView();
		request.setAttribute("id", coll.getId());
		switch (coll.getState()) {
		case MobileConstants.WAIT_SEND_STATE:

			mav = showWaitSendCollaborationDetial(request, response);
			break;
		case MobileConstants.SEND_STATE:

			mav = showSendCollaborationDetial(request, response);
			break;
		case MobileConstants.PENDING_STATE:

			mav = showPendingCol(request, response);
			break;
		case MobileConstants.DONE_STATE:

			mav = showDoneCollaborationDetial(request, response);
			break;
		}
		return mav;
	}

	/**
	 * 得到 待发事项的 列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getWaitSendCollaborationList(
			HttpServletRequest request, HttpServletResponse response)
			throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/waitSendCollList");

		deleteAllPerson();//清除数据

		getWaitSendCollSearchList(request, response, mav);

		return mav;
	}

	/**
	 * 得到已发事项的 列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getSendCollaborationList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/sendCollList");

		getSendCollSearchList(request, response, mav);

		return mav;
	}

	/**
	 * 得到待办事项的 列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getPendingCollaborationList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/pendingCollList");

		getPendingCollSearchList(request, response, mav);

		return mav;
	}

	/**
	 * 得到已办事项的 列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getDoneCollaborationList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/doneCollList");

		getDoneCollSearchList(request, response, mav);

		return mav;
	}

	/**
	 * 显示 待发协同 (待发事项的细节和新建事项的细节 长 的差不多)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showWaitSendCollaborationDetial(
			HttpServletRequest request, HttpServletResponse response)
			throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/waitSendDetial");
		String id = request.getParameter("id");
		String search = request.getParameter("search");
		String cid = request.getParameter("cid");
		
		Collaboration collObject = null;
		try{
			collObject = getCollObjectById(id);
		}catch(Exception e){//此处不可以抛出异常
			collObject = getCollObjectById(cid);
		}
		
		
		if (collObject.getContentType().equalsIgnoreCase("FORM")) {// 如果待发事项为
			// 表单类型则给出提示
			mav = new ModelAndView("mobile/prompt");
			mav.addObject("prompt", 3);
		}
		if (collObject != null) {
			String title = getTitle();
			String content = getContent();
			if(title!=null&&title.length()!=0){
				collObject.setTitle(title);
			}if(content!=null&&content.length()!=0){
				collObject.setContent(content);
			}
			Object[] atts = oaManagerInterface.getColAttachment(collObject.getSummaryId());
			List<Attachment> list = attsMap.get(MobileConstants.getCurrentId());
			List<Attachment> listNew = new ArrayList<Attachment>();
			if(list!=null){
				listNew.addAll(list);
			}
			listNew.addAll((List<Attachment>)atts[0]);
			mav.addObject("atts", listNew);
			mav.addObject("collaboration", collObject);
		}
		mav.addObject("search", (search!=null&&search.length()!=0)?search:null);
		return mav;
	}

	/**
	 * 重新选择人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView reSelecteMemberAction(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("");

		String title = request.getParameter("title");
		String content = request.getParameter("content");

		String orAction = request.getParameter("or");
		String andAction = request.getParameter("and");
		String sendAction = request.getParameter("send");
		String saveAction = request.getParameter("save");

		String id = request.getParameter("id");
		
		String upfile = request.getParameter("upfile");
		
		if(upfile!=null){
			return new ModelAndView("mobile/coll/upfile");
		}

		if (orAction != null || andAction != null) {
			List<String[]> list = getPersonList();
			if (list != null) {
				list.clear();
			}
			getCollObjectById(id).setProcessId(null);// 如果 手机端用户 点击
			// 并发/串发，则会清空该待发事项的以前的流程人员
			mav = createNewCollaboration(request, response);

			mav.addObject("id", id);
			return mav;
		} else {
			if (StringUtils.isBlank(title)) {// 标题为空,
				
				return rendJavaScriptMobileMsg(response, "","com.seeyon.v3x.mobile.btbnwkqtxbt");
			}
			if (sendAction != null) {
				Collaboration collObj = getCollObjectById(id);
				NewCollClass newCollObj = newCollMap.get(MobileConstants.getCurrentId());

				if (collObj.getProcessId() == null
						&& (newCollObj == null || (newCollObj != null && newCollObj
								.getAllPersonList().size() == 0))) {// 流程为空则提示
					//request.setAttribute("id", id);
					return rendJavaScriptMobileMsg(response, "","com.seeyon.v3x.mobile.lcbnwk");
				} else {
					mav = sendCollaborationToA8(title, content, collObj,request, response);
				}
			} else {
				if (saveAction != null) {

					mav = saveCollabortionToA8(id, title, content, request, response);
				}
			}
		}
		if(attsMap!=null && !attsMap.isEmpty()){
			attsMap.remove(MobileConstants.getCurrentId());
		}
		return mav;
	}

	/**
	 * 显示 已发协同
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showSendCollaborationDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/sendDetial");
		String id = request.getParameter("id");
		String search = request.getParameter("search");
		Collaboration collObject = null;
		MobileFormBean formBean = null;
		User user = CurrentUser.get();
		try{
			collObject = getCollObjectById(id);
			if(collObject.getContentType().equalsIgnoreCase("FORM")){
				formBean = oaManagerInterface.getFormAll(collObject.getId(),collObject.getSummaryId(),user);
			}
		}catch(MobileException e){
			//已办中的表单异常了。返回
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("search", search);
			return mobilePrompt(e.getMessage(), "/mob.do?method=getSendCollaborationList", parameters);
		}
		if (collObject != null) {
			mav.addObject("collaboration", collObject);
			if(collObject.getContentType().equalsIgnoreCase("FORM")){
				Map<String,TIP_InputValueAll> map = formBean.getFromApp();
				mav.addObject("formMap", map);
				List<String> formList = new ArrayList<String>(map.keySet());
				mav.addObject("keySet", formList);
				mav.addObject("isContainSub", formBean.getHasChildForm());
			}
			Object[] attachments =  oaManagerInterface.getColAttachment(collObject.getSummaryId());
			List<Attachment> att1 = (List<Attachment>) attachments[1];
			List<Attachment> att2 = (List<Attachment>) attachments[2];
			mav.addObject("att1", att1 != null?att1.size():0);//附件
			mav.addObject("att2", att2 != null?att2.size():0);//关联协同
			mav.addObject("opinLength", getColOpinionLength(collObject));
		}
		mav.addObject("search", (search!=null&&search.length()!=0)?search:null);
		return mav;
	}
	public ModelAndView doneColDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView modelAndView = showDoneCollaborationDetial(request,response);
		modelAndView.setViewName("mobile/coll/showDoneCol");
		
		return modelAndView;
	}
	
	
	/**
	 * 显示 已办协同--关联协同
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showDoneCollaborationDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/doneDetial");
		String id = request.getParameter("id");
		String search = request.getParameter("search");
		MobileFormBean formBean = null;
		Collaboration collObject = null;
		User user = CurrentUser.get();
		try{
			collObject = getCollObjectById(id);
			if(collObject.getContentType().equalsIgnoreCase("FORM")){
				formBean = oaManagerInterface.getFormAll(collObject.getId(), collObject.getSummaryId(), user);
			}
		}catch(MobileException e){
			//已办中的表单异常了。返回
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("search", search);
			return mobilePrompt(e.getMessage(), "/mob.do?method=getDoneCollaborationList", parameters);
		}
		if (collObject != null) {
			mav.addObject("collaboration", collObject);
			if(collObject.getContentType().equalsIgnoreCase("FORM")){
				Map<String,TIP_InputValueAll> map = formBean.getFromApp();
				mav.addObject("formMap", map);
				List<String> formList = new ArrayList<String>(map.keySet());
				mav.addObject("keySet", formList);
				mav.addObject("isContainSub", formBean.getHasChildForm());
			}
			Object[] attachments =  oaManagerInterface.getColAttachment(collObject.getSummaryId());
			List<Attachment> att1 = (List<Attachment>) attachments[1];
			List<Attachment> att2 = (List<Attachment>) attachments[2];
			mav.addObject("att1", att1 != null?att1.size():0);//附件
			mav.addObject("att2", att2 != null?att2.size():0);//关联协同
			mav.addObject("opinLength", getColOpinionLength(collObject));
		}
		mav.addObject("search",(search != null && search.length() != 0) ? search : null);
		return mav;
	}
	
	/**
	 * 查看子表单
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showChidForm(
			HttpServletRequest request, HttpServletResponse response)
			throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/childForm");
		String id = request.getParameter("id");
		Long affairId = Long.parseLong(id);
		Affair affair = affairManager.getById(affairId);
		String message = ColHelper.getErrorMsgByAffair(affair);
		if(affair == null || (affair.getState() != StateEnum.col_sent.getKey() && affair.getState() != StateEnum.col_pending.getKey()&& affair.getState() != StateEnum.col_done.getKey())){
			Map<String,String> parameter = new HashMap<String,String>();
			return mobilePrompt(message, "/mob.do?method=showAffairs", parameter);
		}
		User user = CurrentUser.get();
		try {
			MobileFormBean formBean = oaManagerInterface.getFormAll(affair.getId(), affair.getObjectId(), user);
			Map<String, List<List<TIP_InputValueAll>>> childForm = formBean.getChildFormApp();
			Map<String, List<String>> childFormName = formBean.getChildFormNames();
			mav.addObject("childForm", childForm);
			mav.addObject("childFormName", childFormName);
			mav.addObject("formKey", childForm.keySet());
			mav.addObject("affair", affair);
		} catch (MobileException e) {
			Map<String,String> parameter = new HashMap<String,String>();
			return mobilePrompt(e.getCause().getMessage(), "/mob.do?method=showAffairs", parameter);
		}
		return mav;
	}
	
	/**
	 * 显示 待办协同
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showPendingCol(
			HttpServletRequest request, HttpServletResponse response)
			throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/pendingDetial");
		String id = request.getParameter("id");
		String source = request.getParameter("source");
		String search = request.getParameter("search");
		Long cid = Long.parseLong(id);
		
		ModelAndView modelAndView = validateAffair(request,response,cid);
		if(modelAndView!=null){
			return modelAndView;
		}
		Collaboration collObject = null;
		try{
			collObject = getCollObjectById(id);
		}catch(Exception e){
			Map<String,String> parameter = new HashMap<String,String>();
			parameter.put("search", search);
			return mobilePrompt(e.getCause().getMessage(), "/mob.do?method=showAffairs", parameter);
			//return rendJavaScriptMobileMsg(response, e.getCause().getMessage(),"");
		}
		User user = CurrentUser.get();
		Long summaryId = collObject.getSummaryId();
		if (collObject!=null && collObject.getContentType().equalsIgnoreCase("FORM")) {
			Long modifyerId = oaManagerInterface.getModifyMember(summaryId.toString(),id);
			if(modifyerId!=null&&modifyerId.longValue()!=user.getId()){
				//TODO 正在被人编辑 
				mav.addObject("modifyer", modifyerId);//提示。然后不显示处理框
			}else{
				if(modifyerId.longValue()==user.getId()){
					List<String> lockSummary = userLock.get(user.getId());
					if(lockSummary == null){
						lockSummary = new ArrayList<String>();
					}
					lockSummary.add(summaryId.toString());
					userLock.put(user.getId(), lockSummary);
				}
			}
			MobileFormBean formBean = null;
			Map<String,TIP_InputValueAll> map = null;
			try{
				formBean = oaManagerInterface.getFormAll(collObject.getId(), summaryId, user);
				map = formBean.getFromApp();
				/*if(this.getFormDeal()==null||this.getFormDetial()==null||!this.getFormDeal().containsKey(summaryId)||!this.getFormDetial().containsKey(summaryId)){
					setFormItem(id,summaryId);//设置表单中的内容.
				}*/
			}catch(Exception e){
				Map<String,String> parameter = new HashMap<String,String>();
				parameter.put("search", search);
				return mobilePrompt(e.getCause().getMessage(), "/mob.do?method=showAffairs", parameter);
			}
			boolean isContainCalculate = formBean.getIsContainCalculate(),
					isContainExtend=formBean.getIsContainExtend();
			
			//Map<String,Boolean> subMapForm =oaManagerInterface.getContainSubForm();
			//Map<String,Boolean> containMapMark = oaManagerInterface.getContainMark();
			mav.addObject("isContainSub", formBean.getHasChildForm());//是否含有子表单
			mav.addObject("isContainMark", formBean.getContainMapMark());//如果为true,则提示含有签章，到PC端查看；否则不给予提示
			mav.addObject("isContainCalculate", isContainCalculate);//如果为true,则提示含有计算字段，到PC端查看；否则不给予提示
			mav.addObject("isContainExtend", isContainExtend);//getFormExtendField(id)//如果为true,则提示含有扩展控件，到PC端查看；否则不给予提示
			mav.addObject("isContainEditChild", formBean.getChildFormEdit());//是否含有可操作的子表单数据，如果存在。提示去pc端操作
			
			mav.addObject("formMap", map);
			List<String> formList = new ArrayList<String>(map.keySet());
			mav.addObject("keySet", formList);
			mav.addObject("form", "form");
			//--
		}
		if (collObject != null) {
			mav.addObject("collaboration", collObject);
			mav.addObject("isAllowAttitude", collObject.isAllowAttitude());// 是否允许选择态度
			mav.addObject("isAllowOpinion", collObject.isAllowOpinion());// 是否允许填写态度
			
			Object[] attachments =  oaManagerInterface.getColAttachment(collObject.getSummaryId());
			List<Attachment> att1 = (List<Attachment>) attachments[1];
			List<Attachment> att2 = (List<Attachment>) attachments[2];
			mav.addObject("att1", att1 != null?att1.size():0);//附件
			mav.addObject("att2", att2 != null?att2.size():0);//关联协同
			mav.addObject("opinLength", getColOpinionLength(collObject));
			
			Map<String, Object> res = oaManagerInterface.getProcessModeSelectorList(cid,MobileConstants.getCurrentId());
			String invalidateActivityStr= "";
			//对不需要弹出页面，但存在不用人员时，在这里进行处理，以便在alert时提示用户
    		Object invalidateActivityObj= res.get("invalidateActivityMap");
    		//判断是否存在不可用的节点，如果存在则进行如下处理
        	if(invalidateActivityObj != null){
        		Map invalidateActivityMap= (Map)invalidateActivityObj;
        		if(!invalidateActivityMap.isEmpty()){
        			Iterator iter= invalidateActivityMap.keySet().iterator();
            		for (; iter.hasNext();) {
    					String key = (String) iter.next();
    					String value= (String)invalidateActivityMap.get(key);
    					invalidateActivityStr += value+",";
    				}
            		if(invalidateActivityStr.endsWith(",")){
            			invalidateActivityStr= invalidateActivityStr.substring(0, invalidateActivityStr.length()-1);
            		}
        		}
        	}
        	mav.addObject("invalidateActivityStr", invalidateActivityStr);
			mav.addObject("hasInformNode", res.get("hasInformNode"));
			mav.addObject("nodeTypes", res.get("nodeTypes"));
			Object conditionsObj= res.get("conditions");
			if(null!=conditionsObj){//有分支条件
				if(((List)conditionsObj).size()>0){
					mav.addObject("hasConditions", "true");
				}else{
					mav.addObject("hasConditions", "false");
				}
			}else{
				mav.addObject("hasConditions", "false");
			}
		}
		Map<String, Object> map = null;
		try {
			map = oaManagerInterface.getCollPolicyName(cid, summaryId);
		} catch (Exception e) {
			logger.error("", e);
		}
		mav.addObject("policyMap", map);
		mav.addObject("policy",collObject.getNodePermissionPolicy());
		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
		mav.addObject("colMetadata", colMetadata);
		Permission permission = collObject.getPermission();
		mav.addObject("attitudes", permission==null ? "" : permission.getNodePolicy()!=null ? permission.getNodePolicy().getAttitude() : "");
		mav.addObject("source", source);
		mav.addObject("search", (search!=null&&search.length()!=0)?search:null);
		mav.addObject("id", cid);
		
		return mav;
	}
	private Integer getColOpinionLength(Collaboration collObj){
		int i = 0;
		// 协同的处理意见
		if(collObj.getOpinions()!= null){
			i += collObj.getOpinions().size();
		}
		// 原协同的发起人附言
		if(collObj.getOriginalSendOpinion() != null){
			i += collObj.getOriginalSendOpinion().values().size();
		}
		// 原协同的处理人意�见
		if(collObj.getOriginalSignOpinion() != null){
			i += collObj.getOriginalSignOpinion().values().size();
		}
		// 协同发起人附言
		if(collObj.getSenderOpinion() != null){
			i += collObj.getSenderOpinion().size();
		}
		return i;
	}
	private Map<String,Map<String, TIP_InputValueAll>> getFormDetial(
			Map<String, TIP_InputValueAll> map, boolean isDetial,String cid) {
		Map<String, TIP_InputValueAll> formMap = new HashMap<String, TIP_InputValueAll>();
		Map<String,Map<String, TIP_InputValueAll>> finalMap = new HashMap<String,Map<String, TIP_InputValueAll>>();
		if(map!=null){
			Set<String> keySet = map.keySet();
			for (String str : keySet) {
				TIP_InputValueAll object = (TIP_InputValueAll) map.get(str);
				if (isDetial) {
					if (!object.getAccess().equalsIgnoreCase("edit")) {
						formMap.put(str, object);
					} else {
						continue;
					}
				} else {
					if (object.getAccess().equalsIgnoreCase("edit")) {
						formMap.put(str, object);
					} else {
						continue;
					}
				}
			}
			finalMap.put(cid, formMap);
		}
		return finalMap;
	}

	/**
	 * 显示 新建协同页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showNewCollaborationDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/newColl");

		Boolean newOff = newMap.get(MobileConstants.getCurrentId());

		if (newOff != null) {
			newOff = true;
		} else {
			newMap.put(MobileConstants.getCurrentId(), true);
		}
		mav.addObject("title", getTitle());
		mav.addObject("content", getContent());
		mav.addObject("atts", attsMap.get(MobileConstants.getCurrentId()));

		return mav;
	}

	/**
	 * 处理协同的页面----废弃
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	/*public ModelAndView dealCollaboration(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/process");
		String cid = request.getParameter("id");
		String strCid = Strings.isNotBlank(cid)? cid : "0";
		Long  affarId = Long.parseLong(strCid);
		ModelAndView modelAndView = validateAffair(request,response,affarId);
		if(modelAndView!=null){
			return modelAndView;
		}
		Collaboration coll = null;
		try {
			coll = affarId!=0?oaManagerInterface.CollaborationDetial(affarId, MobileConstants.getCurrentId()):null;
			if(coll!=null&&coll.getContentType().equalsIgnoreCase("FORM")){
				Long modifyerId = oaManagerInterface.getModifyMember(oaManagerInterface.getSummeryIdByAffairId(strCid),strCid);
				if(modifyerId!=null&&modifyerId.longValue()!=CurrentUser.get().getId()){
					return promptPage(request, response, 8);
				}
			}
		} catch (NumberFormatException e) {
			logger.error("处理协同错误.",e);
		} catch (ColException e) {
			logger.error("处理协同错误.",e);
			request.setAttribute("id", strCid);
			return rendJavaScriptMobileMsg( response,e.getCause().toString(),"");
		}

		DealCollClass dealObj = this.getDealCollObj();
		//-begin 这里是得到人员可以处理的表单-项，以前的做法只能发生重复和冲突，比如两个不同视图的人员，只会显示一种视图。
		//因为map中的键值是summaryid，而同一条协同，summaryid是相同的。modify by dongyj
		if(coll.getContentType().equalsIgnoreCase("FORM")){
			//List<String> list = getFormKey(trunType, currentPage, false,strCid);
			List<String> list = new ArrayList<String>();
			//Map<String, TIP_InputValueAll> formMap = this.getFormDeal().get(getSummaryId(strCid));
			Map<String,Object> formMapList = oaManagerInterface.getFormList(affarId,false);
			if(formMapList!=null){
				Set<String> keySet = formMapList.keySet();
				for (String str : keySet) {
					TIP_InputValueAll object = (TIP_InputValueAll) formMapList.get(str);
					if (object.getAccess().equalsIgnoreCase("edit")) {
						list.add(str);
					}
				}
			}
			String formName = oaManagerInterface.getFormName(affarId);
			mav.addObject("formMap", formMapList);
			mav.addObject("formName", formName);
			mav.addObject("keySet", list);
		}
		
		//String formName = coll.getContentType().equals("FORM") ? oaManagerInterface.getFormName(affarId):null;
		//--end
		
		Long summaryId = oaManagerInterface.getCollSummaryIdByAffairId(affarId);
		Map<String, Object> map = null;
		try {
			map = oaManagerInterface.getCollPolicyName(affarId, summaryId);
		} catch (Exception e) {
			logger.error("", e);
		}
		mav.addObject("policyMap", map);
		mav.addObject("policy", coll != null ? coll.getNodePermissionPolicy(): "");
		
		mav.addObject("content", dealObj==null?"":(dealObj.getId().equals(strCid)&&dealObj.getButton().equals("save"))?dealObj.getOpinion():"");
		mav.addObject("attitude", dealObj==null?null:dealObj.getId().equals(strCid)?dealObj.getAttitude():null);
		mav.addObject("templeteId", coll!=null?coll.getTempleteId():null);
		return mav;
	}*/

	/**
	 * 处理 协同 Action
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView dealCollaborationAction(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		String cid = request.getParameter("id");// 事项的id
		String opinion = request.getParameter("opinion");// 意见
		String attitude = request.getParameter("attitude");// 态度
		String affairId = request.getParameter("cid");

		String submitButton = request.getParameter("submit");// 提交 按钮
		String saveButton = request.getParameter("tempSave");// 暂存待办按钮

		String source = request.getParameter("source");
		String search = request.getParameter("search");
		String templeteId = request.getParameter("templeteId");
		String isForm = request.getParameter("form");
		
		String track = request.getParameter("track");
		String from = request.getParameter("from");
		
		String selectperson = null;
		String[] hiddeperson = request.getParameterValues("hiddeperson");
		String selectdepartment = null;
		String[] hiddedepartment = request.getParameterValues("hiddedepartment");
		
		if(hiddeperson!=null){
			for(String str : hiddeperson){
				String select = request.getParameter(str);
				if(select!=null){
					selectperson = str;
					break;
				}else{
					continue;
				}
			}
		}if(hiddedepartment!=null){
			for(String str : hiddedepartment){
				String select = request.getParameter(str);
				if(select!=null){
					selectdepartment = str;
					break;
				}else{
					continue;
				}
			}
		}
		if (affairId != null) {
			cid = affairId;
		}
		ModelAndView modelAndView = validateAffair(request,response,Long.parseLong(cid));
		if(modelAndView!=null){
			return modelAndView;
		}
		//验证表单
		Map<String,TIP_InputValueAll> formMapList = null;
		Map<String,String[]> fieldValueMap= new HashMap<String, String[]>();
		if (Strings.isNotBlank(isForm)) {
			Map<String, TIP_InputValueAll> dealMap = new HashMap<String,TIP_InputValueAll>();
			formMapList = oaManagerInterface.getFormList(Long.parseLong(cid),false);
			if(formMapList!=null){
				Set<String> keySet = formMapList.keySet();
				for (String str : keySet) {
					TIP_InputValueAll object = (TIP_InputValueAll) formMapList.get(str);
					if (object.getAccess().equalsIgnoreCase("edit") || object.getAccess().equalsIgnoreCase("add")) {
						dealMap.put(str, object);
					}
					fieldValueMap.put(str, new String[]{object.getDisplayValue(),object.getValue()});
				}
			}
			Set<String> strList = dealMap.keySet();
			request.setAttribute("dealMap", dealMap);
			for (String str : strList) {
				TIP_InputValueAll valueAll = dealMap.get(str);
				String value = request.getParameter(valueAll != null ? valueAll.getId() : "");
				// 得到前一次追加的值
				String value1 = request.getParameter(valueAll != null ? valueAll.getId()+"old" : "");
				if ("add".equals(valueAll.getAccess())) {
					// 如果以前有追加的值
					if ( value1 != null && !"".equals(value1.trim())) {
						// 如果添加的新追加值不为空
						if (value != null && !"".equals(value.trim())) {
							value = value1 + "\n" +value + " "+"["+CurrentUser.get().getName()+ " "+Datetimes.formatDatetimeWithoutSecond(new Date()) + " " + Constantform.getString4CurrentUser("form.oper.superaddition.label")+"]";
							value = value.replaceAll("<br/>", "\n");
						} else {
							value = value1 ;
						}
					} else {
						if (value != null && !"".equals(value.trim())) {
							value = value + " "+"["+CurrentUser.get().getName()+ " "+Datetimes.formatDatetimeWithoutSecond(new Date()) + " " + Constantform.getString4CurrentUser("form.oper.superaddition.label")+"]";
							value = value.replaceAll("<br/>", "\n");
						}
					}
				} 
				
				List<V3xOrgEntity> listEntity  = null;
				if(valueAll.getType().equals(TFieldInputType.fitCheckBox)){
					if(value!=null&&value.equals("on")){
						value = "1";
					}else{
						value = "0";
					}
				}else{
					if(valueAll.getType().equals(TFieldInputType.fitExtend)
							&&!valueAll.getStageRSXml().equals("选择人员")
							&&!valueAll.getStageRSXml().equals("日期时间选取器")
							&&!valueAll.getStageRSXml().equals("日期选取器")){
						if(valueAll.getStageRSXml().equals("选择部门")){
							listEntity = oaManagerInterface.getOrgEntityByName(V3xOrgDepartment.class.getSimpleName(), "name", value,CurrentUser.get().getAccountId());
						}else{
							if(valueAll.getStageRSXml().equals("选择岗位")){
								listEntity = oaManagerInterface.getOrgEntityByName(V3xOrgPost.class.getSimpleName(), "name", value,CurrentUser.get().getAccountId());
							}else{
								if(valueAll.getStageRSXml().equals("选择职务级别")){
									listEntity = oaManagerInterface.getOrgEntityByName(V3xOrgLevel.class.getSimpleName(), "name", value,CurrentUser.get().getAccountId());
								}else{
									if(valueAll.getStageRSXml().equals("选择单位")){
										listEntity = oaManagerInterface.getOrgEntityByName(V3xOrgLevel.class.getSimpleName(), "name", value,CurrentUser.get().getAccountId());
									}
								}
							}
						}
					}
					if(listEntity!=null&&!listEntity.isEmpty()){
						value = listEntity.get(0).getId().toString();
					}else{
						if(submitButton!=null&&valueAll.getStageRSXml()!=null&&(valueAll.getStageRSXml().equals("选择岗位")||valueAll.getStageRSXml().equals("选择职务级别")||valueAll.getStageRSXml().equals("选择单位"))){
							request.setAttribute("promptMessage", value);
							request.setAttribute("id", cid);
							request.setAttribute("msg", valueAll.getStageRSXml().equals("选择岗位")?0:valueAll.getStageRSXml().equals("选择职务级别")?1:valueAll.getStageRSXml().equals("选择职务级别")?2:3);
							return rendJavaScriptMobileMsg(response, "","form.lable.prompt.post");
						}
					}
				}
				
				String isSet = setDealFormValue(str, value,cid,dealMap,formMapList);
				if (isSet != null) {
					//return rendJavaScriptMobileMsg(response, isSet,"");
					Map<String,String> parameter = new HashMap<String,String>();
					parameter.put("id", cid);
					parameter.put("search", request.getParameter("search"));
					return mobilePrompt(isSet, "/mob.do?method=showPendingCol", parameter);
				} 
			}
		}
		
		setDealCollValue(cid, opinion, attitude,submitButton != null ? "submit" : saveButton != null ? "save": "",isForm,request.getParameter("pass"),"true".equals(track));// 将意见，态度，操作类型，事项的ID保存起来
		
		//如果有选人的表单相，则跳转到选人界面
		if(selectperson!=null||selectdepartment!=null){
			StringBuffer strBuffer = new StringBuffer("/mob.do?method=noMatchingPeople&");
			if(selectperson!=null){
				strBuffer.append("type=person&");
				strBuffer.append("cid="+cid);
				strBuffer.append("&fid="+selectperson);
			}else{
				if(selectdepartment!=null){
					strBuffer.append("type=department&");
					strBuffer.append("cid="+cid);
					strBuffer.append("&fid="+selectdepartment);
				}
			}
			return super.redirectModelAndView(strBuffer.toString());
		}
		
		Long collId = Long.parseLong(((cid != null) && (cid.length() != 0)) ? cid : "0");
		
		Map<String, Object> res = oaManagerInterface.getProcessModeSelectorList(collId,MobileConstants.getCurrentId(),fieldValueMap);
		List<ProcessModeSelector> list = (List<ProcessModeSelector>) res.get("processModeSelector");//
		
		Map<String,Integer> conditionType = (Map<String,Integer>)res.get("conditionTypes");

		Map<String, Object> mapBratch = collId!=0?oaManagerInterface.getBranchLong(formMapList,collId,isForm):null;
		
		List<String> formCondition = (List<String>) (mapBratch != null ? mapBratch.get("conditions"): null);
		String isExecuteFinished= (String)res.get("isExecuteFinished");
		if ("true".equals(isExecuteFinished) && (isNeedSelectPeople(list)|| (formCondition != null && formCondition.size() != 0))&& submitButton != null&&((templeteId!=null&&templeteId.length()!=0)?true:false)) {// 判断 需要跳转到选人界面
			if (isNeedSelectPeople(list) && formCondition == null) {
				ModelAndView mav = getProcessSelectedPage(list);
				mav.addObject("conditionTypes", conditionType);
				mav.addObject("from", from);
				mav.addObject("source", source);
				mav.addObject("search", (search!=null&&search.length()!=0)?search:null);
				return mav;
			} else {
				if ((formCondition != null && formCondition.size() != 0)) {
					ModelAndView mav = getProcessSelectedPageForm(mapBratch, list, cid);
					mav.addObject("from", from);
					mav.addObject("conditionTypes", conditionType);
					return mav;
				} else {
					return null;
				}
			}
		} else {
			ModelAndView mav = processCollPage(request, response);
			if(mav==null){
				return null;
			}
			mav.addObject("source", source);
			mav.addObject("search", (search!=null&&search.length()!=0)?search:null);
			return mav;
		}
	}

	/**
	 * 处理 模板协同
	 */
	public ModelAndView processCollTemplet(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {

		DealCollClass dealObj = dealCollMap.get(MobileConstants.getCurrentId());
		NewCollClass newObj = newCollMap.get(MobileConstants.getCurrentId());

		String[] bratchName = request.getParameterValues("bratchMember");
		Long l = Long.parseLong(dealObj!=null?dealObj.getId():"0");
		
		ModelAndView mav = validateAffair(request,response,l);
		if(mav!=null){
			return mav;
		}
		ListNode ln = new ListNode();
		
		if (dealObj != null) {
			Map<String, String[]> nodeMap = newObj != null ? newObj.getRoleMember() : new HashMap<String, String[]>();
			Map<String, String> conditionMap = new HashMap<String, String>();
			Map<String, String[]> newArrayMap = new HashMap<String, String[]>();
			Map<String, String[]> mapArray = null;
			List<ProcessModeSelector> list = (List<ProcessModeSelector>) oaManagerInterface.getProcessModeSelectorList(Long.parseLong(dealObj.getId()),MobileConstants.getCurrentId()).get("processModeSelector");

			Map<String, Object> mapBratch = oaManagerInterface.getBranchLong(this.getFormMobileMap().get(dealObj.getId()), Long.parseLong(dealObj.getId()),"");
			
			List<String> listKey = mapBratch!=null?(List<String>) mapBratch.get("keys"):null;
			Integer number = mapBratch!=null?Integer.parseInt((String)mapBratch.get("nodeCount")):null;
			if(mapBratch!=null&&bratchName==null&&(listKey!=null?listKey.size():0)==number){
				return rendJavaScriptMobileMsg(response,"","coll.flow.4");// 提示 必须选择节点,否则该流程终止
			}
			if (list != null) {
				for (ProcessModeSelector se : list) {
					if (nodeMap.get(se.getId()) == null) {
						String[] selectValue = request.getParameterValues(se.getId());
						if("multiple".equals(se.getType()) && selectValue == null){
							//多选不允许为空
							Map<String,String> parameter = new HashMap<String,String>();
							parameter.put("id", dealObj.getId());
							return mobilePrompt(MobileConstants.getValueFromMobileRes("coll.mutiple.selectpeople", se.getName()), "/mob.do?method=showPendingCol", parameter);
						}
						nodeMap.put(se.getId(),selectValue);
					}
				}
			}
			nodeMap = cancelKeyNode(bratchName,nodeMap,mapBratch);
			mapArray = getStringMember(nodeMap);
			
			if (mapArray == null) {
				Map<String,String> map = oaManagerInterface.flowChartParam(Long.parseLong(dealObj.getId()));
				
				Long summaryId = Long.valueOf(map.get("summaryId"));
				Long caseId = Long.valueOf(map.get("caseId"));
				String processId = map.get("processId");
				
				String message = ln.getNodeName((Nodes) oaManagerInterface.getNodes(summaryId,caseId,processId,false).get("nodes"),getNullString(nodeMap));
				return rendJavaScriptMobileMsg(response, message,"coll.flow.single.prompt.0");// 提示某节点不能为空
			}
			List<String> keyList = mapBratch!=null?(List<String>) mapBratch.get("keys"):null;
			List<String> conditions = mapBratch!=null?(List<String>) mapBratch.get("conditions"):null;

			conditions = restBratch(conditions);// 将所有的条件都设为true
			if(bratchName != null && bratchName.length != 0){
				for (String key : bratchName) {// 将 用户选择的 分支条件 置为false
					if (keyList!=null&&keyList.contains(key)) {
						conditions.set(keyList.indexOf(key), "false");
					}
					if (mapArray.containsKey(key)) {
						newArrayMap.put(key, mapArray.get(key));
					}
				}
				mapArray = newArrayMap;// 在分支页面上没有选择的分支，需要去掉
			}
			if(keyList!=null&&conditions!=null){
				for (int i = 0; i < keyList.size(); i++) {// 得到 条件 Map
					// key-node id;
					// value- true/false
					conditionMap.put(keyList.get(i), conditions.get(i));
				}
			}
			oaManagerInterface.processCollaboration(getActionType(dealObj
					.getButton().equals("submit") ? "" : null, dealObj
					.getButton().equals("save") ? "" : null), Long
					.parseLong(dealObj.getId()), dealObj.getOpinion(),
					getAttitude(dealObj.getAttitude()), mapArray, conditionMap,dealObj.isTrack());
			
			if(dealObj.getType()!=null && dealObj.getType().equals("form")){//如果处理的协同是表单，则需要将用户填写的表单数据保存
				Integer pass = null;
				if (dealObj.getButton().equals("submit") || dealObj.getButton().equals("save")) {
					pass = new Integer(1);
				} else {
					pass = dealObj.getPass() != null ? dealObj.getPass().equals("yes") ? 2
							: dealObj.getPass().equals("no") ? 3 : 0 : 0;
				}
				try{
				oaManagerInterface.processForm(this.getFormDeal().get(getSummaryId(dealObj.getId())), Long.parseLong(dealObj.getId()),pass,"0");
				}
            	catch(DataDefineException e1){
            	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){      		
    	              try {
						super.rendJavaScript(response, "alert('" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "');history.back();");
					} catch (IOException e) {
						logger.warn("手机端保存表单数据时发生错误", e1);
					}
            	  }       		  
          		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
          			  throw new MobileException(e1.getMessage());
          		  else{
          			logger.error("手机端保存表单数据时发生错误", e1);
          			  throw new MobileException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
          		  }
          	  
            	  }catch(Exception e){
            		  logger.error("手机端保存表单数据时发生错误", e);
          		  throw new MobileException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
          	      }
				oaManagerInterface.removeModifyMember(oaManagerInterface.getSummeryIdByAffairId(dealObj.getId()));
			}
		}
		if (dealCollMap != null)
			dealCollMap.clear();
		if (newCollMap != null)
			newCollMap.clear();

		return getDoneCollaborationList(request, response);
	}
	
	public ModelAndView processEdocTemplement(HttpServletRequest request,HttpServletResponse response) throws Exception{
		DealCollClass dealObj = dealCollMap.get(MobileConstants.getCurrentId());
		String[] bratchName = request.getParameterValues("bratchMember");
		ListNode ln = new ListNode();
		
		Map<String, String> conditionMap = new HashMap<String, String>();
		Map<String, String[]> newArrayMap = new HashMap<String, String[]>();
		Map<String, String[]> mapArray = null;
		Long affairId = Long.parseLong(dealObj.getId());
			
		List<ProcessModeSelector> list = (List<ProcessModeSelector>) oaManagerInterface.getProcessModeSelectorList(affairId,MobileConstants.getCurrentId()).get("processModeSelector");
		Map<String, String[]> nodeMap = new HashMap<String, String[]>();
		if (list != null) {
			for (ProcessModeSelector se : list) {
				if (nodeMap.get(se.getId()) == null) {
					String[] selectValue = request.getParameterValues(se.getId());
					if("multiple".equals(se.getType()) && selectValue == null){
						//多选不允许为空
						Map<String,String> parameter = new HashMap<String,String>();
						parameter.put("id", dealObj.getId());
						return mobilePrompt(MobileConstants.getValueFromMobileRes("coll.mutiple.selectpeople", se.getName()), "/mob.do?method=showEdocObj", parameter);
					}
					nodeMap.put(se.getId(),selectValue);
				}
			}
		}
		
		Map<String,String> map = oaManagerInterface.flowChartParam(Long.parseLong(dealObj.getId()));
		Long summaryId = Long.valueOf(map.get("summaryId"));
		Long caseId = Long.valueOf(map.get("caseId"));
		String processId = map.get("processId");
		
		Map<String, Object> edocMapBratch = oaManagerInterface.getEdocBratch(summaryId, Long.parseLong(dealObj.getId()));
		
		List<String> listKey = edocMapBratch!=null?(List<String>) edocMapBratch.get("keys"):null;
		Integer number = edocMapBratch!=null?Integer.parseInt((String)edocMapBratch.get("nodeCount")):null;
		List<String> force = (List<String>) (edocMapBratch!=null?edocMapBratch.get("forces"):null);
		boolean isForce = false;
		if(force!=null){
			for(String str : force){
				if(Boolean.parseBoolean(str)){
					isForce = true;
				}
			}
		}
		if(edocMapBratch!=null&&bratchName==null&& !isForce &&(listKey!=null?listKey.size():0)==number){
			return rendJavaScriptMobileMsg(response,"","coll.flow.4");// 提示 必须选择节点,否则该流程终止
		}
		
		nodeMap  = cancelKeyNode(bratchName,nodeMap,edocMapBratch);
		mapArray = getStringMember(nodeMap);
		
		if(mapArray==null && bratchName==null && !isForce){
			String message = ln.getNodeName((Nodes) oaManagerInterface.getNodes(summaryId,caseId,processId,false).get("nodes"),getNullString(nodeMap));
			return rendJavaScriptMobileMsg(response, message,"coll.flow.single.prompt.0");// 提示某节点不能为空
		}
		
		List<String> keyList = edocMapBratch!=null?(List<String>) edocMapBratch.get("keys"):null;
		List<String> conditions = edocMapBratch!=null?(List<String>) edocMapBratch.get("conditions"):null;

		conditions = restBratch(conditions);// 将所有的条件都设为true
		if(bratchName != null && bratchName.length != 0){
			for (String key : bratchName) {// 将 用户选择的 分支条件 置为false
				if (keyList!=null&&keyList.contains(key)) {
					conditions.set(keyList.indexOf(key), "false");
				}
				if (mapArray!=null && mapArray.containsKey(key)) {
					newArrayMap.put(key, mapArray.get(key));
				}
			}
			mapArray = newArrayMap;// 在分支页面上没有选择的分支，需要去掉
		}else{
			if(force!=null){
				for(int i=0;i<force.size();i++){
					String str = force.get(i);
					if(Boolean.parseBoolean(str)){
						conditions.set(i, "false");
					}
				}
			}
		}
		if(keyList!=null&&conditions!=null){
			for (int i = 0; i < keyList.size(); i++) {// 得到 条件 Map
				// key-node id;
				// value- true/false
				conditionMap.put(keyList.get(i), conditions.get(i));
			}
		}
		int actionType = getActionType(dealObj.getButton().equals("submmit") ? "" : null, dealObj.getButton().equals("save") ? "" : null);
		String opinion = dealObj.getOpinion();
		String attitude = dealObj.getAttitude();
		
		Map<String, Object> flowChart1 = oaManagerInterface.getNodes(summaryId,caseId,processId,true);
		
		Nodes node = (Nodes) flowChart1.get("nodes");
		Object isLock =  flowChart1.get("isLock");
		
		if(isLock==null){
			String[] str = new String[1];
			str[0] = node.getNodename();
			nodeMap.put(node.getNid(), str);
			int att = Strings.isNotBlank(attitude)?getAttitude(attitude):-1;
			oaManagerInterface.processEdoc(actionType, affairId, opinion,att ,nodeMap, conditionMap,dealObj.isTrack());
		}
		
		if (dealCollMap != null)
			dealCollMap.clear();
		return super.redirectModelAndView("/mob.do?method=showAffairs");
		
	}
	
	/**
	 * 删除 没有选中的key
	 * @param selectKey
	 * @param map
	 * 
	 */
	private Map<String,String[]> cancelKeyNode(String[] selectKey,Map<String,String[]> map,Map<String, Object> mapBratch){
		Map<String,String[]> keyMap= new HashMap<String,String[]>();
		List<String> keys = new ArrayList<String>();
		if( null!=mapBratch && null!= mapBratch.get("keys") ){
			keys = (List<String>)mapBratch.get("keys");
		}
		if(selectKey!=null){
			for(String key : selectKey){
				keyMap.put(key, map.get(key));
			}
			for(String key : keys){
				map.remove(key);
			}
			keyMap.putAll(map);
			return keyMap;
		}else{
			List<String> force = (List<String>) (mapBratch!=null?mapBratch.get("forces"):null);
			boolean isForce = false;
			if(force!=null){
				for(String str : force){
					if(Boolean.parseBoolean(str)){
						isForce = true;
					}
				}
			}
			if(mapBratch!=null && !isForce){
				List<String> list = (List<String>) mapBratch.get("conditions");
				List<Integer> listIndex = new ArrayList<Integer>();
				for(int i=0;i<list.size();i++){
					String str = list.get(i);
					if(str.equals("true")){
						listIndex.add(i);
					}
				}
				for(Integer index : listIndex){
					String  key = keys.get(index);
					if(key!=null){
						map.remove(key);
					}
				}
			}
			return map;
		}
		
	}

	private String getNullString(Map<String, String[]> nodeMap) {
		Set<String> keys = nodeMap.keySet();
		for (String key : keys) {
			if (nodeMap.get(key) == null)
				return key;
			else if (nodeMap.get(key)[0] == null)
				return key;
			else
				continue;
		}
		return null;
	}

	private List<String> restBratch(List<String> list) {
		if(list!=null){
			for (int i = 0; i < list.size(); i++) {
				list.set(i, "true");
			}
		}
		return list;
	}

	private Map<String, String[]> getStringMember(Map<String, String[]> nodeMap) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		Set<String> set = nodeMap.keySet();
		for (String s : set) {
			List<String> list = new ArrayList<String>();
			String[] strArray = nodeMap.get(s);
			if(strArray!=null){
				for (String str : nodeMap.get(s)) {
					if (str != null) {
						list.add(str.split("&")[0]);
					} else {
						return null;
					}
				}
				map.put(s, list.toArray(new String[] {}));
			}
		}
		return map;
	}

	/**
	 * 处理协同返回 方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView processCollPageBak(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/process");
		String id = request.getParameter("id");
		String isCanAtt = request.getParameter("isCanAtt");
		String isCanOpin = request.getParameter("isCanOpin");
		String source = request.getParameter("source");

		mav.addObject("cid", id);
		mav.addObject("isCanAtt", isCanAtt);
		mav.addObject("isCanOpin", isCanOpin);
		mav.addObject("source", source);

		return mav;
	}

	/**
	 * 处理 协同
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView processCollPage(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		String opinion = request.getParameter("opinion");
		String attitude = request.getParameter("attitude");
		String cid = request.getParameter("id");
		String affairId = request.getParameter("cid");
		String track = request.getParameter("track");
		
		if (affairId != null && affairId.length() != 0)
			cid = affairId;
		Long collId = Long.parseLong(((cid != null) && (cid.length() != 0)) ? cid : "0");
		
		ModelAndView mav = validateAffair(request,response,collId);
		if(mav!=null){
			return mav;
		}
		
		String submitButton = request.getParameter("submit");// 提交 按钮
		String saveButton = request.getParameter("tempSave");// 暂存待办按钮
		String passType = request.getParameter("pass");
		String vouchPass = request.getParameter("vouchPass");

		Map<String,String> map = oaManagerInterface.flowChartParam(collId);
		
		Long summaryId = Long.valueOf(map.get("summaryId"));
		Long caseId = Long.valueOf(map.get("caseId"));
		String processId = map.get("processId");
		
		Map<String, Object> flowChart = oaManagerInterface.getNodes(summaryId,caseId,processId,true);
		Nodes node = (Nodes) flowChart.get("nodes");
		Object isLock =  flowChart.get("isLock");
		
		
		try {
			Collaboration coll = oaManagerInterface.CollaborationDetial(collId,MobileConstants.getCurrentId());
			
			if (coll != null) {
				if (coll.getContentType().equalsIgnoreCase("FORM")) {
					Integer pass = null;
					if (submitButton != null || saveButton != null) {
						pass = new Integer(1);
					} else {
						pass = passType != null ? passType.equals("yes") ? 2
								: passType.equals("no") ? 3 : 0 : 0;
					}
					try{
						Map<String, TIP_InputValueAll> dealMap = (Map<String, TIP_InputValueAll>) request.getAttribute("dealMap");
						if(dealMap == null){
							dealMap = this.getFormDeal().get(getSummaryId(cid));
						}
						oaManagerInterface.processForm(dealMap, collId,pass,vouchPass);
					}catch(DataDefineException e1){
                	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){      		
        	              try {
							super.rendJavaScript(response, "alert('" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "');history.back();");
							return null;
        	              } catch (IOException e) {
							logger.warn("手机端保存表单数据时发生错误", e1);
						}
                	  }       		  
              		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
              			  throw new MobileException(e1.getMessage());
              		  else{
              			logger.error("手机端保存表单数据时发生错误", e1);
              			  //throw new RuntimeException("不能保存");
              			  throw new MobileException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              		  }
              	  
                	  }catch(Exception e){
                		  logger.error("手机端保存表单数据时发生错误", e);
              		  //throw new RuntimeException("不能保存");
              		  throw new MobileException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              	      }
					oaManagerInterface.removeModifyMember(oaManagerInterface.getSummeryIdByAffairId(cid));
				}
			}
		} catch (ColException e) {
			logger.info("处理表单协同错误.");
			throw new MobileException(e);
		}
		
		if(isLock==null){
			Map<String, String[]> nodeMap = new HashMap<String, String[]>();

			String[] str = new String[1];
			str[0] = node.getNodename();
			nodeMap.put(node.getNid(), str);

			try {
				oaManagerInterface.processCollaboration(getActionType(submitButton,saveButton), collId, opinion, attitude == null?com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL:Integer.valueOf(attitude),nodeMap, null,"true".equals(track));
			
			} catch (MobileException e) {
				logger.info("手机端处理协同错误!");
				
				//request.setAttribute("id", cid);
				//request.setAttribute("promptMessage", e.getCause().getMessage());
				return rendJavaScriptMobileMsg(response,"", e.getCause().getMessage());
			}
		}else{
			request.setAttribute("promptMessage", flowChart.get("locker"));
			Map<String,String> paramter = new HashMap<String,String>();
			paramter.put("id", request.getParameter("id"));
			paramter.put("isCanAtt", request.getParameter("isCanAtt"));
			paramter.put("isCanOpin", request.getParameter("isCanOpin"));
			paramter.put("type", request.getParameter("type"));
			paramter.put("source", request.getParameter("source"));
			paramter.put("search", request.getParameter("search"));
			return mobilePrompt(MobileConstants.getValueFromMobileRes("coll.isLock.label",flowChart.get("locker").toString()), "/mob.do?method=showPendingCol", paramter);
			//return rendJavaScriptMobilesMsg( response,"", "coll.isLock.label");
			
		}
		return super.redirectModelAndView("/mob.do?method=showAffairs");
		
	}

	/**
	 * 处理 协同模板
	 * 
	 * @return
	 */
	public ModelAndView processCollTemplate(Long collId) throws MobileException {
		ModelAndView mav = null;
		List<ProcessModeSelector> list = (List<ProcessModeSelector>) oaManagerInterface
				.getProcessModeSelectorList(collId,
						MobileConstants.getCurrentId()).get(
						"processModeSelector");
		Map<String,TIP_InputValueAll> formMapList = oaManagerInterface.getFormList(collId.longValue(),false);
		//modified by wangchw:2011-11-6
		Map<String, Object> mapBratch = oaManagerInterface.getBranchLong(formMapList, collId,"");
		//Map<String, Object> mapBratch = oaManagerInterface.getBranchLong(this.getFormMobileMap().get(collId.toString()), collId);

		if (mapBratch != null) {
			List<String> listBratch = (List<String>) mapBratch.get("conditions");
			if (listBratch.size() != 0) {
				mav = getProcessSelectedPageForm(mapBratch, list, collId.toString());
				mav.addObject("map", getMapSelector(list));
				return mav;
			}
		}
		mav = getProcessSelectedPage(list);
		mav.addObject("id", collId);
		mav.addObject("isCanOpin", true);
		mav.addObject("isCanAtt", true);
		mav.addObject("map", getMapSelector(list));
		return mav;
	}

	/**
	 * 得到 处理协同时的 选人界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	private ModelAndView getProcessSelectedPage(List<ProcessModeSelector> list)
			throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectNodeTransactor");

		mav.addObject("selectorList", list);
		mav.addObject("map", getMapSelector(list));
		return mav;
	}

	private Map<String, Integer> getMapSelector(List<ProcessModeSelector> list) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		NewCollClass ncoll = newCollMap.get(MobileConstants.getCurrentId());
		if (list != null && list.size() != 0) {
			if (ncoll != null) {
				Map<String, String[]> roleMember = ncoll.getRoleMember();
				for (ProcessModeSelector ss : list) {
					if (roleMember != null
							&& !isNull(roleMember.get(ss.getId()))) {
						map.put(ss.getId(), roleMember.get(ss.getId()).length);
					}
				}
			} else {
				for (ProcessModeSelector ss : list) {
					map.put(ss.getId(), 0);
				}
			}
		}

		return map;
	}

	private boolean isNull(String[] strArray){
		if(strArray!=null){
			for(String str : strArray){
				if(str==null)
					return true;
				else
					continue;
			}
		}else{
			return true;
		}
		return false;
	}
	/**
	 * 得到 处理协同时的 选人界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	private ModelAndView getProcessSelectedPageForm(
			Map<String, Object> mapBratch, List<ProcessModeSelector> list,
			String id) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectNodeTransactor");
		if(Strings.isNotBlank(id)){
			Affair a = affairManager.getById(Long.parseLong(id));
			int app = a!=null?a.getApp():-1;
			if(app!=-1){
				if(app<10){
					mav.addObject("from", "col");
				}else if(app>10){
					mav.addObject("from", "edoc");
				}
			}
		}
		
		mav.addObject("id", id);
		mav.addObject("mapBratch", mapBratch);
		mav.addObject("selectorList", list);
		//added by wangchw 2011-11-6
		mav.addObject("map", getMapSelector(list));
		return mav;
	}

	/**
	 * 处理 协同模板选人
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView processSelectedTemplate(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		String id = request.getParameter("id");
		String[] nodeId = request.getParameterValues("node");
		String opinion = request.getParameter("opinion");
		String attitude = request.getParameter("attitude");
		String rid = request.getParameter("rid");
		String track = request.getParameter("track");
		
		Long collId = Long.parseLong(Strings.isNotBlank(id) ? id : "0");

		Map<String, String[]> nodeMap = new HashMap<String, String[]>();
		if (nodeId != null && nodeId.length != 0) {
			for (String str : nodeId) {
				String memberId = request.getParameter(str);
				String[] members = { memberId };
				nodeMap.put(str, members);
			}
		}
		if (rid != null) {
			nodeMap.put(rid, newCollMap.get(MobileConstants.getCurrentId()).getRoleMember().get(rid));
		}
		if (!isCanProcess(nodeId, nodeMap)) {
			//TODO
			return rendJavaScriptMobileMsg(response, "","");
		}
		oaManagerInterface.processCollaboration(getActionType("", null),collId, opinion, getAttitude(attitude), nodeMap, null,"true".equals(track));

		return getDoneCollaborationList(request, response);
	}

	/**
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	private boolean isCanProcess(String[] key, Map<String, String[]> map) {
		boolean isCan = true;
		for (String str : key) {
			int length = map.get(str).length;
			if (length == 0) {
				isCan = false;
				break;
			} else {
				continue;
			}
		}
		return isCan;
	}

	/**
	 * 查看事项的流程
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView lookFlowChart(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/showAffairFlow");
		String nid = request.getParameter("nid");// 当前节点的id
		String cid = request.getParameter("id");// 当前事项的id
		
		String pageNum = request.getParameter("pageNum");//当前页码数
		String type = request.getParameter("type");
		MobileUtil util = new MobileUtil();

		Long collId = Long.parseLong(Strings.isNotBlank(cid) ? cid : "0");
		
		Map<String,String> map = oaManagerInterface.flowChartParam(collId);
		String summaryIds = map.get("summaryId");
		String caseIds = map.get("caseId");
		Long summaryId = Long.valueOf(Strings.isNotBlank(summaryIds)?summaryIds:"0");
		Long caseId = Long.valueOf((Strings.isNotBlank(caseIds)&&!"null".equals(caseIds))?caseIds:"0");
		String processId = map.get("processId");
		
		Map<String, Object> objMap = oaManagerInterface.getNodes(summaryId,caseId,processId,false);
		
		MobileUtil mu = new MobileUtil();
		int currentPageNum = mu.getCurrentPage(type, pageNum);
		
		if(objMap!=null){
			
			Nodes flowChart = (Nodes) objMap.get("nodes");
			Nodes node = getNodeById(nid, flowChart);
			ListNode LN = new ListNode();
			List<Nodes> listNode = LN.getListNodesByNode(node);

			Map<String, List<Object[]>> affaircaseWorkItemLog = (Map<String, List<Object[]>>) objMap.get("caseWorkItemLog");

			mav.addObject("node", node);
			mav.addObject("list", Pagination.paginationObjectList(listNode,currentPageNum));
			mav.addObject("affaircaseWorkItemLog", affaircaseWorkItemLog);
			mav.addObject("pageNum", util.getCurrentPage(type, pageNum));
			mav.addObject("totalNum", listNode!=null?listNode.size():0);
			mav.addObject("totalPageNum", util.getPageCount(listNode!=null?listNode.size():0,MobileConstants.PAGE_COUNTER));
			
		}else{
			Collaboration coll = null;
			try {
				coll = oaManagerInterface.CollaborationDetial(collId, CurrentUser.get().getId());
			} catch (ColException e) {
				logger.error("查看附件时,得到附件异常");
			}
			if(coll==null){
				return rendJavaScriptMobileMsg(response,"","coll.withdraw.lable");//提示 协同不存在
			}else{
				
				return rendJavaScriptMobileMsg(response,"","coll.flow.delete");//提示该节点的已没有权限
			}
			
		}

		return mav;

	}

	/**
	 * 流程返回
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView flowChartBak(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/showAffairFlow");

		String nid = request.getParameter("nid");// 当前节点的id
		String cid = request.getParameter("id");// 当前事项的id
		String affairType = request.getParameter("affairType");
		String source = request.getParameter("source");

		Long coId = Long.parseLong(Strings.isNotBlank(cid)? cid : "0");
		Map<String, Object> objMap = null;
		
		Map<String,String> map = oaManagerInterface.flowChartParam(coId);
		
		Long summaryId = Long.valueOf(map.get("summaryId"));
		Long caseId = Long.valueOf(map.get("caseId"));
		String processId = map.get("processId");
		
		try{
			objMap = oaManagerInterface.getNodes(summaryId,caseId,processId,false);
			
		}catch(MobileException e){
			//request.setAttribute("promptMessage", e.getMessage());
			return rendJavaScriptMobileMsg(response,"",e.getMessage());
		}

		Nodes flowChart = (Nodes) objMap.get("nodes");
		Nodes node = getNodeById(nid, flowChart);
		ListNode LN = new ListNode();
		List<Nodes> listNode = LN.getListNodesByNode(node.getParent());

		
		
		Map<String, List<Object[]>> affaircaseWorkItemLog = (Map<String, List<Object[]>>) objMap.get("caseWorkItemLog");

		String pageNum = request.getParameter("pageNum");//当前页码数
		String type = request.getParameter("type");
		MobileUtil util = new MobileUtil();
		int currentPageNum = util.getCurrentPage(type, pageNum);
		mav.addObject("pageNum", util.getCurrentPage(type, pageNum));
		mav.addObject("totalNum", listNode!=null?listNode.size():0);
		mav.addObject("list", Pagination.paginationObjectList(listNode,currentPageNum));
		mav.addObject("totalPageNum", util.getPageCount(listNode!=null?listNode.size():0,MobileConstants.PAGE_COUNTER));
		
		mav.addObject("id", cid);
		mav.addObject("node", node.getParent());
		mav.addObject("affaircaseWorkItemLog", affaircaseWorkItemLog);
		mav.addObject("affairType", affairType);
		mav.addObject("source", source);

		return mav;
	}

	/**
	 * 查看附件列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView lookCollAttachment(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/collAttachment");
		String id = request.getParameter("id");
		String turnType = request.getParameter("prev");
		String page = request.getParameter("pagecurrent");
		String affairType = request.getParameter("affairType");
		String source = request.getParameter("source");
		String search = request.getParameter("search");
		String attachType = request.getParameter("attType");
		int attType = 1;
		if(Strings.isNotBlank(attachType)){
			attType = Integer.parseInt(attachType);
		}
		Long cid = Long.parseLong(Strings.isNotBlank(id) ? id : "0");
		Affair affair = affairManager.getById(cid);
		if(affair == null){
			return null;
		}
		List<Attachment> attachments = (List<Attachment>) oaManagerInterface.getColAttachment(affair.getObjectId())[attType];
		
		//attachments.addAll((List<Attachment>) oaManagerInterface.getColAttachment(cid)[1]);
		int numbers = attachments.size();
		MobileUtil mobileUtil = new MobileUtil();
		int pageNum = mobileUtil.getCurrentPage(turnType, page);

		Pagination p = new Pagination();
		p.setListAttachment(attachments);
		attachments = p.paginationAttachments(pageNum,
				MobileConstants.PAGE_COUNTER, attachments);

		Map<String, Boolean> isLink = new HashMap<String, Boolean>();
		for (Attachment att : attachments) {
			if (att != null) {
				isLink.put(att.getId().toString(), MobileConstants.validateSuffix(att));
			}
		}
		mav.addObject("pagecurrent", pageNum);
		mav.addObject("attachments", attachments);
		mav.addObject("isLink", isLink);
		mav.addObject("num", numbers);
		mav.addObject("pagecount", mobileUtil.getPageCount(numbers,MobileConstants.PAGE_COUNTER));
		mav.addObject("affairType", affairType);
		mav.addObject("source", source);
		mav.addObject("id", id);
		mav.addObject("search", (search!=null&&search.length()!=0)?search:null);
		
		return mav;

	}

	/**
	 * 显示 某一个附件的具体内容
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView lookAttachmentContent(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/attachment");
		String fileurl = request.getParameter("fileurl");
		String date = request.getParameter("creatdate");
		//String turnType = request.getParameter("pre");
		//String pageNum = request.getParameter("pagenum");
		String id = request.getParameter("id");
		String affairType = request.getParameter("affairType");
		String source = request.getParameter("source");

		Long file = Long.parseLong((StringUtils.isNotBlank(fileurl)) ? fileurl : "0");
		Date creatDate = Datetimes.parseDatetime((date != null) ? date.substring(0, 19) : "");

		StringBuffer content = oaManagerInterface.getAttachmentContent(file,creatDate);
		//String attContent = ((content != null) ? content : new StringBuffer("")).toString();
		
		/*Pagination p = new Pagination();
		p.setContent(attContent);

		String attachmentContent = p.getContent();
		MobileUtil mobileUtil = new MobileUtil();
		int strNum = mobileUtil.getPageCount(attachmentContent.length(),MobileConstants.DISPLAY_PAGE_NUMBER);
		int pagenum = mobileUtil.getCurrentPage(turnType, pageNum);

		String finalCont = p.paginationString(attachmentContent, pagenum,strNum);*/

		mav.addObject("content", content.toString());
		//mav.addObject("pagenum", pagenum);
		//mav.addObject("pagecount", strNum);
		mav.addObject("fileurl", fileurl);
		mav.addObject("creatdate", date);
		mav.addObject("id", id);
		mav.addObject("affairType", affairType);
		mav.addObject("source", source);
		return mav;
	}

	/**
	 * 查看意见
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobielException
	 */
	public ModelAndView lookOpinionContent(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/collOpinon");
		String id = request.getParameter("id");
		String turnType = request.getParameter("pre");
		String pageNum = request.getParameter("pagenum");
		String affairType = request.getParameter("affairType");
		String source = request.getParameter("source");
		String search = request.getParameter("search");
		Long currentUserId = MobileConstants.getCurrentId();
		try {
			Collaboration collObj = oaManagerInterface.CollaborationDetial(Long.parseLong(Strings.isNotBlank(id) ? id : "0"), currentUserId);
			Map<Long, List<ColComment>> comments = collObj.getComments();// 协同的
			// 回复意见
			List<ColOpinion> opinions = collObj.getOpinions();// 协同的处理意见

			java.util.Map<Integer, List<ColOpinion>> originalSignOpinion = collObj.getOriginalSendOpinion();// 原协同的发起人附言
			java.util.Map<Integer, List<ColOpinion>> originalSendOpinion = collObj.getOriginalSignOpinion();// 原协同的处理人意�见

			List<Integer> originalSendOpinionKey = collObj.getOriginalSendOpinionKey();// 原协同处理意见的key
			List<ColOpinion> senderOpinion = collObj.getSenderOpinion();// 协同发起人附言
	            
			Pagination p = new Pagination();
			p.setMapColComment(comments);
			p.setOpinions(opinions);

			MobileUtil mobileUtil = new MobileUtil();
			int page = mobileUtil.getCurrentPage(turnType, pageNum);

			Map<Long, List<ColComment>> newOpinionCommentList = new HashMap<Long, List<ColComment>>();
			//Map<Long, List<ColComment>> newEndOpinionCommentList = new HashMap<Long, List<ColComment>>();

			Map<Long, List<ColComment>> commentList = p.paginationOPinion(page,MobileConstants.OPINION_NUMBER, newOpinionCommentList);
			//Map<Long, List<ColComment>> commentEndList = p.getEndOpinion(page,MobileConstants.OPINION_NUMBER, newEndOpinionCommentList);
			Map<Long, List<ColComment>> commentFinallyList = commentList;
			//commentList是已经被分页的回复，下面就不要再做重复的动作了。
			/*if (page > 1) {
				commentFinallyList = p.getOverOpinion(commentList,commentEndList);
			} else {
				commentFinallyList = p.paginationOPinion(page,MobileConstants.OPINION_NUMBER, commentList);
			}*/

			int size = (opinions != null) ? opinions.size() : 0;
			int pageCount = mobileUtil.getPageCount(size, MobileConstants.OPINION_NUMBER);
			
			List<ColComment> colComments = new ArrayList<ColComment>();

            List<ColOpinion> opinionList = new ArrayList<ColOpinion>();
            Map<Long, ColOpinion> colOpinionMap = new HashMap<Long,ColOpinion>();
            Set<Long> keys = commentFinallyList.keySet();
			for (Long l : keys) {
				ColOpinion colOpinion = getOpinion(l,opinions);
				colOpinionMap.put(l, colOpinion);
				opinionList.add(colOpinion);
				List<ColComment> colCommentList = commentFinallyList.get(l);
				if (colCommentList != null) {
					for (ColComment col : colCommentList) {
						if ((!colComments.contains(col))) {
							colComments.add(col);
						}
					}
				}
			}
			
			Map<ColOpinion, List<Attachment>> map = oaManagerInterface
					.getOpinionAndAttachments(Long.parseLong(Strings.isNotBlank(id)? id : "0"), currentUserId);
			Map<String, Boolean> isLink = new HashMap<String, Boolean>();
			if (senderOpinion != null && !senderOpinion.isEmpty()) {
				for (ColOpinion col : senderOpinion) {
					List<Attachment> attList = map.get(col);
					if (attList != null && !attList.isEmpty()) {
						isLink = getLinkMethod(attList);
					}
				}
			}
            
            int commentsSize = 0;
            if(colComments != null && !colComments.isEmpty()){
                commentsSize = colComments.size();
            }
           
			mav.addObject("num", size);
			mav.addObject("pagecount", pageCount);
			mav.addObject("pagecurrent", page);

			mav.addObject("comments", colComments);
			mav.addObject("commentsSize", commentsSize);
			mav.addObject("opinions", getSortedOpinion(opinionList));

			mav.addObject("originalSignOpinion", originalSignOpinion);
			mav.addObject("originalSendOpinion", originalSendOpinion);
			mav.addObject("originalSendOpinionKey", originalSendOpinionKey);
			mav.addObject("senderOpinion", senderOpinion);

			mav.addObject("isLinkOpinion", isLink);

			mav.addObject("attachmentMap", map);
			mav.addObject("currentUserId", currentUserId);
			mav.addObject("currentUserIsSender", collObj.getCreaterOr().equals(currentUserId));

		} catch (NumberFormatException e) {
			logger.debug("查看 协同意见异常：", e);
			throw new MobileException(e);
		} catch (ColException e) {
			logger.debug("查看 协同意见异常：", e);
			throw new MobileException(e);
		}

		mav.addObject("source", source);
		mav.addObject("affairType", affairType);
		mav.addObject("id", id);
		mav.addObject("search", (search!=null&&search.length()!=0)?search:null);
		return mav;
	}

	/**
	 * 新建事项方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView createNewCollaboration(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");

		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String did = request.getParameter("did");
		String newColl = request.getParameter("newColl");

		setNewCollMember(title, content, null);

		String orAction = request.getParameter("or");// 并发
		String andAction = request.getParameter("and");// 串发
		String sendAction = request.getParameter("send");// 发送
		String saveAction = request.getParameter("save");// 保存
		String upfile = request.getParameter("upfile");
		if(upfile!=null){
			mav = new ModelAndView("mobile/coll/upfile");
			mav.addObject("newCol", "1");
			return mav;
		}

		if (orAction != null || andAction != null) {
			newCollMap.get(MobileConstants.getCurrentId()).getAllPersonList().clear();	
			if (orAction != null) {
				actionMap.put(MobileConstants.getCurrentId(), "or");
			} else {
				if (andAction != null) {
					actionMap.put(MobileConstants.getCurrentId(), "and");
				}
			}

				Long departId = Strings.isNotBlank(did)?Long.valueOf(did):CurrentUser.get().getDepartmentId();
				V3xOrgDepartment department = oaManagerInterface.getDepartment(departId);
				List<MobileOrgEntity> list = oaManagerInterface.getMobileOrgEntity(departId);
				MobileUtil util = new MobileUtil();
				int size = list!=null?list.size():0;
				int currentPage = 1;
				int totlaPageNum = util.getPageCount(size, MobileConstants.PAGE_COUNTER);
				mav.addObject("entitys", Pagination.paginationObjectList(list,currentPage));
				mav.addObject("page", currentPage);
				mav.addObject("pagecount", totlaPageNum);
				mav.addObject("department", department);
				mav.addObject("parentpath", department.getParentPath());
				
			mav.addObject("newColl", newColl);
		} else {
			if (StringUtils.isNotBlank(title)) {
				if (sendAction != null) {// 发送协同
					NewCollClass obj = newCollMap.get(MobileConstants.getCurrentId());
					List<String[]> list = (obj != null) ? obj.getAllPersonList() : null;

					if ((list != null) ? (list.size() != 0) ? false : true : true) {
						return rendJavaScriptMobileMsg(response,"", "com.seeyon.v3x.mobile.lcbnwk");
					} else {
						mav = sendCollaborationToA8(title, content, null,request, response);
					}
				}
				if (saveAction != null) {// 保存协同
					mav = saveCollabortionToA8(null, title, content, request,response);
				}
			} else {
				// 给出 标题不能为空的提示,判断 流程，如果没人，则提示
				return rendJavaScriptMobileMsg(response,"", "com.seeyon.v3x.mobile.btbnwkqtxbt");
			}
		}
		if(attsMap!=null &&( orAction == null && andAction == null)){
			attsMap.remove(MobileConstants.getCurrentId());
		}
		return mav;
	}

	/**
	 * 给 选人界面添加数据的方法
	 * 
	 * @param mav
	 * @param did
	 * @throws MobileException
	 */
	private void addSelectPeoplePageParames(ModelAndView mav, String did)
			throws MobileException {

		Long id = Strings.isNotBlank(did)?Long.valueOf(did):V3xOrgEntity.VIRTUAL_ACCOUNT_ID;

		List<MobileOrgEntity> memberList = oaManagerInterface.getMobileOrgEntity(id);// 得到 部门人员列表

		int size = memberList != null ? memberList.size() : 0;
		
		MobileUtil mobileUtil = new MobileUtil();

		//Map<Long,Integer> departmentMember = getDempartmentMap(departmentList);//key: 部门的id,value: 部门的人数
		
		V3xOrgDepartment department = getDepartmentById(did);
		V3xOrgAccount account = null;
		if(department == null){
			account = (V3xOrgAccount)oaManagerInterface.getOrgEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, Long.parseLong(did));
		}
		if(account == null){
			account = oaManagerInterface.getAccount(MobileConstants.getCurrentId());
		}
		mav.addObject("entitys", Pagination.paginationObjectList(memberList, 1));
		mav.addObject("department", department);
		mav.addObject("parentpath", department!=null?department.getParentPath():"");
		if(department==null){
			mav.addObject("account", account);
		}
		mav.addObject("isGroup", SysFlag.sys_isGroupVer.getFlag());
		mav.addObject("page", 1);
		mav.addObject("pagecount", mobileUtil.getPageCount(size,MobileConstants.PAGE_COUNTER));
		//mav.addObject("departmentMember", departmentMember);
	}

	/**
	 * 提示页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView promptPage(HttpServletRequest request,
			HttpServletResponse response, int prompt) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/prompt");

		mav.addObject("message", request.getAttribute("promptMessage"));
		mav.addObject("id", request.getAttribute("id"));
		mav.addObject("prompt", prompt);
		mav.addObject("msg", request.getAttribute("msg"));
		return mav;
	}

	/**
	 * 移动消息提示，用于系统给用户提示信息，并且给出返回地址。
	 * @param message 消息
	 * @param action  提示后跳转的页面
	 * @param parameters 其他的参数
	 * @return
	 */
	public ModelAndView mobilePrompt(String message,String action,Map<String,String> parameters){
		ModelAndView mav = new ModelAndView("mobile/mobileErrorMessage");
		mav.addObject("message", message);
		if(parameters == null){
			parameters = new HashMap<String,String>();
		}
		Set<String> keys = parameters.keySet();
		StringBuffer sb = new StringBuffer(action);
		boolean firstUrl = false;
		if(action.indexOf("?") <0){
			sb.append("?");
			firstUrl = true;
		}
		for(String key : keys){
			if(parameters.get(key) != null){
				if(firstUrl){
					firstUrl = false;
				}else{
					sb.append("&");
				}
				sb.append(key).append("=").append(Functions.urlEncoder(parameters.get(key)));
			}
		}
		mav.addObject("action", sb.toString());
		return mav;
	}
	/**
	 * 得到 一个父部门下的 成员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getParentDepartemtnMember(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");

		String parentPath = request.getParameter("parentpath");
		String newColl = request.getParameter("newColl");
		String noMatch = request.getParameter("noMatch");
		String selectType = request.getParameter("selectType");
		String nid = request.getParameter("nid");
		String extendType = request.getParameter("extendType");
		String cid = request.getParameter("cid");
		String fid = request.getParameter("fid");//得到表单项的Id
		String account = request.getParameter("account");
		if(Strings.isNotBlank(account)){
			setDepartmentMember(mav,request.getParameter("did"),parentPath);
		}else{
			setDepartmentMember(mav,null,parentPath);
		}
		
		mav.addObject("noMatch",(noMatch != null && noMatch.length() != 0) ? noMatch : null);
		mav.addObject("newColl", (newColl!=null&&newColl.length()!=0)?newColl:null);
		mav.addObject("selectType", (selectType!=null&&selectType.length()!=0)?selectType:null);
		mav.addObject("nid", nid);
		mav.addObject("extendType", (extendType!=null&&extendType.length()!=0)?extendType:null);
		mav.addObject("listSize", getPersonNum(nid));
		mav.addObject("id", (cid!=null&&cid.length()!=0)?cid:null);
		mav.addObject("fid", fid);
		return mav;
	}

	/**
	 * 得到 某一部门下的 成员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getDepartmentMember(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");
		String id = request.getParameter("did");
		String newColl = request.getParameter("newColl");
		String noMatch = request.getParameter("noMatch");
		String selectType = request.getParameter("selectType");
		String nid = request.getParameter("nid");
		String extendType = request.getParameter("extendType");
		String cid = request.getParameter("cid");

		setDepartmentMember(mav, id,null);
		mav.addObject("newColl", (newColl!=null&&newColl.length()!=0)?newColl:null);
		mav.addObject("noMatch", noMatch);
		mav.addObject("selectType", selectType);
		mav.addObject("nid", nid);
		mav.addObject("extendType", (extendType!=null&&extendType.length()!=0)?extendType:null);
		mav.addObject("listSize", getPersonNum(nid));
		mav.addObject("id", cid);
		return mav;
	}

	/**
	 * 设置部门成员
	 * 
	 * @param mav
	 * @param id
	 * @throws MobileException
	 */
	private void setDepartmentMember(ModelAndView mav, String id,String parentPath)
			throws MobileException {

		V3xOrgDepartment deparetment = Strings.isNotBlank(id)?getDepartmentById(id):null;
		
		List<MobileOrgEntity> list= null;
		MobileUtil mobileUtil = new MobileUtil();
		if (deparetment != null) {
			list = oaManagerInterface.getMobileOrgEntity(deparetment.getId());
			
			V3xOrgDepartment orgDepartment = oaManagerInterface.getDepartmentByPath(parentPath,null);
			mav.addObject("department", deparetment);
			mav.addObject("parentpath", StringUtils.isNotBlank(parentPath)?orgDepartment!=null?orgDepartment.getParentPath():parentPath:deparetment.getParentPath());
		
		}else{
			V3xOrgAccount account = null;
			if(Strings.isNotBlank(id)){
				account = (V3xOrgAccount)oaManagerInterface.getOrgEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, Long.parseLong(id));
			}
			if("-1".equals(parentPath)){//求单位
				list = oaManagerInterface.getMobileOrgEntity(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
				mav.addObject("allAccount", true);
			}else{
				if (parentPath!=null&&parentPath.lastIndexOf(".") == -1) {
					if(account == null){
						account = oaManagerInterface.getAccount(MobileConstants.getCurrentId());
					}
					list = oaManagerInterface.getMobileOrgEntity(account.getId());
					mav.addObject("account", account);
					mav.addObject("isGroup", SysFlag.sys_isGroupVer.getFlag());
				}
				else {
					Long accountId = null;
					if(account != null){
						accountId = account.getId();
					}
					deparetment = oaManagerInterface.getDepartmentByPath(parentPath,accountId);
					list = oaManagerInterface.getMobileOrgEntity(deparetment.getId());
					mav.addObject("department", deparetment);
					mav.addObject("parentpath", deparetment.getParentPath());
				}
			}
		}
		int size = list!=null?list.size():0;
		mav.addObject("entitys", Pagination.paginationObjectList(list,1));
		mav.addObject("page", 1);
		mav.addObject("pagecount", mobileUtil.getPageCount(size,MobileConstants.PAGE_COUNTER));
	}

	/**
	 * 显示 关联人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showRelationPersonList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/relativePersonList");
		String trunType = request.getParameter("pre");
		String pageNum = request.getParameter("pagecurrent");
		String newColl = request.getParameter("newColl");
		String selectType = request.getParameter("selectType");
		String nid = request.getParameter("nid");
		String noMatch = request.getParameter("noMatch");
		String cid = request.getParameter("cid");
		String sizeNum = request.getParameter("size");
		String extendType = request.getParameter("extendType");
		String fid = request.getParameter("fid");
		String from = request.getParameter("from");
		
		List<Long> listRelat = getRelativePersonList();

		MobileUtil mobileUtil = new MobileUtil();
		Pagination p = new Pagination();
		p.setListlong(listRelat);

		int size = listRelat.size();
		int currentpage = mobileUtil.getCurrentPage(trunType, pageNum);
		int pageint = mobileUtil.getPageCount(size,
				MobileConstants.PAGE_COUNTER);

		List<Long> currentList = p.paginationLong(currentpage,
				MobileConstants.PAGE_COUNTER, listRelat);

		mav.addObject("reltiveMembers", currentList);
		mav.addObject("pagecount", pageint);
		mav.addObject("pagenumber", currentpage);
		mav.addObject("num", size);
		mav.addObject("newColl", newColl);
		mav.addObject("selectType", selectType);
		mav.addObject("nid", nid);
		mav.addObject("noMatch", noMatch);
		mav.addObject("id", cid);
		mav.addObject("sizeNum", (sizeNum!=null&&sizeNum.length()!=0)?sizeNum:0);
		mav.addObject("extendType", (extendType!=null&&extendType.length()!=0)?extendType:null);
		mav.addObject("fid", (fid!=null&&fid.length()!=0)?fid:null);
		mav.addObject("from", from);
		mav.addObject("listSize", (extendType!=null&&extendType.length()!=0)?getSelectedPersonForm(cid,fid):getPersonNum(nid));
		return mav;
	}

	/**
	 * 得到 当前页面的关联人员
	 * 
	 * @param pageNum
	 * @param list
	 * @return
	 */
	private List<Long> getRelativePersonList() {
		Map<RelationType, List<Long>> map = oaManagerInterface.getRelativeMember(MobileConstants.getCurrentId());
		Collection<List<Long>> members = map.values();
		List<Long> listRelat = new ArrayList<Long>();
		for (List<Long> l : members) {
			listRelat.addAll(l);
		}
		return listRelat;
	}

	/**
	 * 新建事项时,选择关联人员后的返回页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showRelationPersonListBack(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");
		String newColl = request.getParameter("newColl");
		String selectType = request.getParameter("selectType");
		String nid = request.getParameter("nid");
		String noMatch = request.getParameter("noMatch");
		String cid = request.getParameter("cid");
		String extendType = request.getParameter("extendType");
		String fid = request.getParameter("fid");
		String did = request.getParameter("did");
		
		addSelectPeoplePageParames(mav, did);
		if(Strings.isBlank(did)){
			mav.addObject("department", null);
		}
		mav.addObject("newColl", newColl);
		mav.addObject("listSize", (extendType!=null&&extendType.length()!=0)?getSelectedPersonForm(cid,fid):getPersonNum(nid));
		mav.addObject("selectType", selectType);
		mav.addObject("nid", nid);
		mav.addObject("noMatch", noMatch);
		mav.addObject("id", cid);
		mav.addObject("extendType", (extendType!=null&&extendType.length()!=0)?extendType:null);
		mav.addObject("fid", (fid!=null&&fid.length()!=0)?fid:null);
		return mav;
	}

	/**
	 * 选择 关联人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView selectRelativePerson(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/relativePersonList");
		String[] personIds = request.getParameterValues("username");
		String pageNum = request.getParameter("pagecurrent");
		String pageCount = request.getParameter("pageCount");
		String size = request.getParameter("size");
		String newColl = request.getParameter("newColl");
		String selectType = request.getParameter("selectType");
		String completeOver = request.getParameter("completeOver");
		String sizeNum = request.getParameter("sizeNum");
		String extendType = request.getParameter("extendType");
		String fid = request.getParameter("fid");
		String cid = request.getParameter("cid");
		String nid = request.getParameter("nid");
		
		if(completeOver!=null){
			return showRelationPersonListBack(request,response);
		}

		List<Long> list = getRelativePersonList();
		Pagination p = new Pagination();
		p.setListlong(list);

		List<Long> currentList = p.paginationLong(Integer.parseInt(pageNum != null ? pageNum : "1"),
				MobileConstants.PAGE_COUNTER, list);

		if(extendType!=null&&extendType.length()!=0){
			setSelectPersonForm(cid,fid,personIds);
			mav.addObject("extendType", extendType);
		}else{
			setPersonTemplate(nid,personIds,selectType);// 将所选人 保存起来
		}
		
		mav.addObject("pagecount", pageCount);
		mav.addObject("pagenumber", pageNum);
		mav.addObject("num", size);
		mav.addObject("newColl", newColl);
		mav.addObject("reltiveMembers", currentList);
		mav.addObject("selectType", selectType);
		mav.addObject("selectSize", (extendType!=null&&extendType.length()!=0)?getSelectedPersonForm(cid,fid):getPersonNum(nid));
		mav.addObject("sizeNum", sizeNum);//在选人界面选的人数
		mav.addObject("extendType", (extendType!=null&&extendType.length()!=0)?extendType:null);
		mav.addObject("fid", (fid!=null&&fid.length()!=0)?fid:null);
		mav.addObject("id", (cid!=null&&cid.length()!=0)?cid:null);
		mav.addObject("nid", nid);
		return mav;
	}

	/**
	 * 显示 已选人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showSelectedPersonList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/showSelectedPeople");
		String pageNum = request.getParameter("pagecurrent");
		String trunType = request.getParameter("pre");
		String newColl = request.getParameter("newColl");
		String selectType = request.getParameter("selectType");
		String nid = request.getParameter("nid");
		String noMatch = request.getParameter("noMatch");
		String cid = request.getParameter("cid");
		String extendType = request.getParameter("extendType");
		String fid = request.getParameter("fid");

		setSelectedPerson(mav, trunType, pageNum,selectType,nid,extendType,cid);
		mav.addObject("newColl", (newColl!=null&&newColl.length()!=0)?newColl:null);
		mav.addObject("selectType", selectType);
		mav.addObject("nid", nid);
		mav.addObject("noMatch", noMatch);
		mav.addObject("id",cid);
		mav.addObject("extendType", (extendType!=null&&extendType.length()!=0)?extendType:null);
		mav.addObject("fid", (fid!=null&&fid.length()!=0)?fid:null);
		return mav;
	}

	/**
	 * 已选人员返回
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showSelectedPersonListBack(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");
		String newColl = request.getParameter("newColl");
		String selectType = request.getParameter("selectType");//角色的多人,单人,竞争执行
		String noMatch = request.getParameter("noMatch");
		String cid = request.getParameter("cid");
		String nid = request.getParameter("nid");
		String extendType = request.getParameter("extendType");
		String fid = request.getParameter("fid");
		String did = request.getParameter("did");
		
		addSelectPeoplePageParames(mav, did);
		if(Strings.isBlank(did)){
			mav.addObject("department", null);
		}
		mav.addObject("newColl", (newColl!=null&&newColl.length()!=0)?newColl:null);
		mav.addObject("listSize", (extendType!=null&&extendType.length()!=0)?getSelectedPersonForm(cid,fid):getPersonNum(nid));
		mav.addObject("selectType", selectType);
		mav.addObject("allAccount", request.getParameter("isAccount"));
		mav.addObject("noMatch", noMatch);
		mav.addObject("id", Strings.isBlank(cid)?did:cid);
		mav.addObject("nid", nid);
		mav.addObject("extendType", (extendType!=null&&extendType.length()!=0)?extendType:null);
		mav.addObject("fid", (fid!=null&&fid.length()!=0)?fid:null);
		return mav;
	}

	/**
	 * 对 新建事项时的 选择人员进行分页
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView paginationSelectedPerson(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");

		String pageNum = request.getParameter("page");// 当前页码
		String trunType = request.getParameter("type");// 0:上一页，1:下一页
		String nid = request.getParameter("nid");
		String id = request.getParameter("id");
		String searchText = request.getParameter("search");

		Long did = Long.parseLong(Strings.isNotBlank(id) ? id : "0");
		String isAccount = request.getParameter("isAccount");
		List<MobileOrgEntity> listMember = new ArrayList<MobileOrgEntity>();
		MobileUtil mobileUtil = new MobileUtil();
		int currentPage = mobileUtil.getCurrentPage(trunType, pageNum);
		if(!"true".equals(isAccount)){
			V3xOrgDepartment department = oaManagerInterface.getDepartment(did);
			int size=0;
			if(searchText!=null&&searchText.length()!=0){
				size = oaManagerInterface.searchMember(did,searchText, MobileConstants.PAGE_COUNTER, currentPage,listMember);
				mav.addObject("total", size);
			}else{
				listMember = oaManagerInterface.getMobileOrgEntity(did);
			}
			mav.addObject("isGroup", SysFlag.sys_isGroupVer.getFlag());
			if(department!=null){
				mav.addObject("department", department);
			}else{
				V3xOrgAccount account = null;
				if(Strings.isNotBlank(id)){
					account = (V3xOrgAccount)oaManagerInterface.getOrgEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, Long.parseLong(id));
				}
				if(account == null){
					account = oaManagerInterface.getAccount(MobileConstants.getCurrentId());
				}
				mav.addObject("account", account);
			}
		}else{
			listMember = oaManagerInterface.getMobileOrgEntity(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			mav.addObject("allAccount", true);
		}
		mav.addObject("entitys", Pagination.paginationObjectList(listMember,currentPage));
		mav.addObject("page", currentPage);
		mav.addObject("daId", did);
		mav.addObject("newColl", request.getParameter("newColl"));
		mav.addObject("listSize", getPersonNum(nid));
		return mav;
	}

	/**
	 * 查看 已选人员 和 编辑已选人员
	 * 
	 * @param mav
	 * @param trunType
	 * @param pageNum
	 */
	private void setSelectedPerson(ModelAndView mav, String trunType,
			String pageNum,String type,String nid,String extendType,String cid) {
		List<String[]> listMap = null;
		if(type!=null&&type.length()!=0){
			listMap = getRolePersonList(nid);
		}else{
			listMap = getPersonList();
		}
		if(extendType!=null&&extendType.length()!=0){
			listMap = new ArrayList<String[]>();
			if(cid!=null&&cid.length()!=0){
				Map<Long,Map<String,TIP_InputValueAll>> map = this.getFormPerson();
				Map<String,TIP_InputValueAll> mapObj = (map!=null&&!map.isEmpty())?map.get(CurrentUser.get().getId()):null;
				TIP_InputValueAll obj = (mapObj!=null&&!mapObj.isEmpty())?mapObj.get(cid):null;
				String[] str = {obj!=null?obj.getValue():null,"Member"};
				listMap.add(str);
			}
		}
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		List<Long> listMember = getSelectedMember(listMap);
		List<V3xOrgDepartment> listDepartment = new ArrayList<V3xOrgDepartment>();
		List<V3xOrgAccount> listAccount = new ArrayList<V3xOrgAccount>();
		if (listMember != null) {
			for (Long l : listMember) {
				V3xOrgMember m = oaManagerInterface.getMemberById(l);
				if (m!=null&&!memberList.contains(m)) {
					memberList.add(m);
				}else{
					if(m==null){
						V3xOrgDepartment d = oaManagerInterface.getDepartment(l);
						if(d!=null&&!listDepartment.contains(d)){
							listDepartment.add(d);
						}else{
							V3xOrgAccount account = (V3xOrgAccount)oaManagerInterface.getOrgEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, l);
							if(account != null && !listAccount.contains(account)){
								listAccount.add(account);
							}
						}
					}
				}
			}
		}
		
		mav.addObject("selectedMembers", memberList);
		mav.addObject("selectDepartment", listDepartment);
		mav.addObject("selectAccount", listAccount);
	}

	private List<Long> getSelectedMember(List<String[]> listMap) {
		List<Long> list = new ArrayList<Long>();
		if (listMap != null&&listMap.size()!=0) {
			for (String[] strArray : listMap) {
				if (strArray[1].equalsIgnoreCase("Member")) {
					list.add(Long.parseLong((strArray[0] != null && strArray[0].length() != 0) ? strArray[0] : "0"));
				}else if(strArray[1].equalsIgnoreCase("Department")){
					list.add(Long.parseLong((strArray[0] != null && strArray[0].length() != 0) ? strArray[0] : "0"));
				}else if(strArray[1].equalsIgnoreCase("Account")){
					list.add(Long.parseLong((strArray[0] != null && strArray[0].length() != 0) ? strArray[0] : "0"));
				}
			}
			return list;
		} else {
			return null;
		}

	}

	private void removeObj(Long id,String nid) {
		List<String[]> list = (nid!=null&&nid.length()!=0)?getRolePersonList(nid):getPersonList();
		List<Long> listMember = getSelectedMember(list);
		if(nid!=null&&nid.length()!=0){
			NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
			Map<String,String[]> map = new HashMap<String,String[]>();
			List<String> listA = new ArrayList<String>();
			if(id!=null&&nid!=null&&nid.length()!=0){
				String[] rolSting = nc.getRoleMember().get(nid);
				for(String s : rolSting){
					String[] str = s.split("&");
					if(Long.parseLong(str[0])!=id){
						listA.add(s);
					}else{
						continue;
					}
				}
				map.put(nid, listA.toArray(new String[]{}));
				nc.getRoleMember().remove(nid);
				nc.setRoleMember(map);
			}
		}else{
			if (listMember.contains(id)){
				List<Integer> intee = getIndex(id);
				if(intee!=null){
					int j=0;
					for(Integer i :intee){
						if(j!=0){
							removePersonByIndex(i-j>=0?i-j:0);
						}else{
							removePersonByIndex(i);
						}
						j++;
					}
				}
			}
		}
	}
	private List<Integer> getIndex(Long id) {
		List<String[]> list = getPersonList();
		List<Integer> listInteger = new ArrayList<Integer>();
		for (String[] str : list) {
			if (str[0].equals(id.toString())){
				Integer index = list.indexOf(str);
				listInteger.add(index);
			}else{
				continue;
			}
		}
		return listInteger;
	}

	/**
	 * 编辑 已选人员列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView editSelectedPerson(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/editSelectedPeople");
		String pageNum = request.getParameter("pagecurrent");
		String trunType = request.getParameter("pre");
		String newColl = request.getParameter("newColl");
		String type = request.getParameter("selectType");
		String nid = request.getParameter("nid");
		String extendType = request.getParameter("extendType");
		String fid = request.getParameter("fid");
		String cid = request.getParameter("cid");
		String noMatch = request.getParameter("noMatch");
		
		setSelectedPerson(mav, trunType, pageNum,type,nid,extendType,cid);

		mav.addObject("newColl", Strings.isNotBlank(newColl)?newColl:null);
		mav.addObject("selectType", type);
		mav.addObject("nid", nid);
		mav.addObject("extendType", Strings.isNotBlank(extendType)?extendType:null);
		mav.addObject("fid", Strings.isNotBlank(fid)?fid:null);
		mav.addObject("cid", Strings.isNotBlank(cid)?cid:null);
		mav.addObject("noMatch", Strings.isNotBlank(noMatch));
		return mav;
	}

	/**
	 * 删除 已选人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView deealSelectedPerson(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/showSelectedPeople");
		String[] needReomveUser = request.getParameterValues("username");
		String nid = request.getParameter("nid");
		String page = request.getParameter("pagenum");
		String newColl = request.getParameter("newColl");
		String noMatch = request.getParameter("noMatch");
		String type = request.getParameter("selectType");
		String extendType = request.getParameter("extendType");
		String fid = request.getParameter("fid");
		String cid = request.getParameter("cid");
		
		if (needReomveUser != null&&(extendType==null||extendType.length()==0)) {

			for (String s : needReomveUser) {
				Long id = Long.parseLong(s);
				removeObj(id,nid);// 删除选中的 人员
			}
		}else{
			if(needReomveUser!=null&&extendType!=null&&extendType.length()!=0){//将 表单选人的扩展控件所选的人删除掉
				Map<Long,Map<String,TIP_InputValueAll>> map = this.getFormPerson();
				Map<String,TIP_InputValueAll> mapObj = (map!=null&&!map.isEmpty())?map.get(cid):null;
				if(mapObj!=null&&!mapObj.isEmpty()){
					mapObj.clear();
				}
			}
		}

		setSelectedPerson(mav, null, page,type,nid,null,cid);
		mav.addObject("newColl", Strings.isNotBlank(newColl)?newColl:null);
		mav.addObject("selectType", type);
		mav.addObject("nid", nid);
		mav.addObject("extendType",Strings.isNotBlank(extendType)?extendType:null);
		mav.addObject("fid", Strings.isNotBlank(fid)?fid:null);
		mav.addObject("id", Strings.isNotBlank(cid)?cid:null);
		mav.addObject("noMatch", noMatch);
		return mav;
	}

	/**
	 * 选择 所选人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobielException
	 */
	public ModelAndView selectePersonAction(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");
		String selectAction = request.getParameter("selectAction");
		String finishAction = request.getParameter("finishAction");
		String type = request.getParameter("selectType");
		String newColl = request.getParameter("newColl");
		String noMatch = request.getParameter("noMatch");

		String did = request.getParameter("did");// 部门的id
		String cid = request.getParameter("cid");

		String selectType = request.getParameter("selectType");
		String extendType = request.getParameter("extendType");
		String fid = request.getParameter("fid");

		String[] selectedPerson = request.getParameterValues("username");
		String searchText = request.getParameter("search");
		String pageNum = request.getParameter("page");
		String nid = request.getParameter("nid");
		//String parentPath = request.getParameter("parentpath");
		String prevType = request.getParameter("type");
		String action = actionMap.get(MobileConstants.getCurrentId());
		if(selectedPerson!=null && action != null && "or".equals(action)){
			for(String str : selectedPerson){
				List<String> name = isContain(str);//得到部门名称
				if(name!=null&&!name.isEmpty()){
					StringBuffer message = new StringBuffer();
					for(String s : name){
						if(message.length() > 0){
							message.append("<br/>");
						}
						message.append(s);
					}
					Map<String,String> parameter = MobileUtil.selectPerBackPara(request);
					return mobilePrompt(message.toString(), "/mob.do?method="+request.getParameter("parentMethod"), parameter);
					//request.setAttribute("id", cid);
					//return rendJavaScriptMobileMsg(response,message.toString(),"");
				}else{
					continue;
				}
			}
		}
		MobileUtil mobileUtil = new MobileUtil();
		
		List<MobileOrgEntity> listMember = new ArrayList<MobileOrgEntity>();
		Long departmentId = Strings.isNotBlank(did)?Long.valueOf(did):0L;
		int currentPage = mobileUtil.getCurrentPage(prevType, pageNum);
		
		String isAccount = request.getParameter("isAccount");
		int size=0;
		if(Strings.isNotBlank(searchText)){
			size = oaManagerInterface.searchMember(MobileConstants.getCurrentAccountId(),searchText, MobileConstants.PAGE_COUNTER, currentPage,listMember);
		}else{
			if(!"true".equals(isAccount)){
				V3xOrgDepartment department = oaManagerInterface.getDepartment(departmentId);
				if(searchText!=null&&searchText.length()!=0){
					size = oaManagerInterface.searchMember(MobileConstants.getCurrentAccountId(),searchText, MobileConstants.PAGE_COUNTER, currentPage,listMember);
				}else{
					listMember = oaManagerInterface.getMobileOrgEntity(departmentId);
					size = listMember.size();
				}
				mav.addObject("isGroup", SysFlag.sys_isGroupVer.getFlag());
				if(department!=null){
					mav.addObject("department", department);
				}else{
					V3xOrgAccount account = null;
					if(Strings.isNotBlank(did)){
						account = (V3xOrgAccount)oaManagerInterface.getOrgEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, Long.parseLong(did));
					}
					if(account == null){
						account = oaManagerInterface.getAccount(MobileConstants.getCurrentId());
					}
					mav.addObject("account", account);
				}
			}else{
				listMember = oaManagerInterface.getMobileOrgEntity(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
				size = listMember.size();
				mav.addObject("allAccount", true);
			}
		}
		//if (Strings.isNotBlank(searchText)) {
		/*int size = oaManagerInterface
					.searchMember(departmentId,
							searchText, MobileConstants.PAGE_COUNTER, currentPage,list);*/
		int pageCount = mobileUtil.getPageCount(size, MobileConstants.PAGE_COUNTER);
		mav.addObject("entitys", Pagination.paginationObjectList(listMember, currentPage));
		mav.addObject("page", currentPage);
		mav.addObject("pagecount", pageCount);
		mav.addObject("total", size);
		mav.addObject("search", searchText);
		mav.addObject("listSize",getPersonNum(nid));
		mav.addObject("parentpath", request.getParameter("parentpath"));
		//}
		
		String arrairId = "";
		if (finishAction != null) {
			if (selectType != null && selectType.length() != 0) {
				arrairId = dealCollMap.get(MobileConstants.getCurrentId()).getId().toString();
				mav = processCollTemplate(Long.parseLong((arrairId != null && arrairId.length() != 0) ? arrairId : "0"));
				mav.addObject("cid", cid);
				return mav;
			} else {
				if(newColl.equalsIgnoreCase("0")){
					return showWaitSendCollaborationDetial(request, response);
				}else{
					if(newColl.equalsIgnoreCase("1")){
						return showNewCollaborationDetial(request, response);
					}else{
						if(extendType!=null&&extendType.length()!=0){
							return super.redirectModelAndView("/mob.do?method=showAffairs");
						}
					}
				}
			}
		} else {
			if (selectAction != null) {
				if (type != null && type.length() != 0 & nid != null
						&& nid.length() != 0) {
						if(selectedPerson!=null){
							setPersonTemplate(nid, selectedPerson,type);// 将选择的人员保存
						}
						ModelAndView modelView = showSelectedPersonListBack(request, response);
						
						modelView.addObject("listSize", getPersonNum(nid));// 就是为了在页面上显示出 选人完毕
						modelView.addObject("selectType", selectType);
						modelView.addObject("nid", nid);
						return modelView;
				} else {
						if(selectedPerson!=null){
							if(extendType!=null&&extendType.length()!=0){
								setSelectPersonForm(cid,fid,selectedPerson);
								mav.addObject("extendType", extendType);
							}else{
								setPersonTemplate(nid, selectedPerson,type);// 将选择的人员保存
								//setDepartmentMember(mav, did,parentPath);
								mav.addObject("listSize",getPersonNum(nid));
							}
						}
				}
			}
		}
		mav.addObject("newColl", (newColl!=null&&newColl.length()!=0)?newColl:null);
		mav.addObject("selectType", selectType);
		mav.addObject("id", Strings.isNotBlank(arrairId)?arrairId:Strings.isNotBlank(cid)?cid:"0");
		mav.addObject("nid", nid);
		/*V3xOrgDepartment department = oaManagerInterface.getDepartment(Long.parseLong((did!=null&&did.length()!=0)?did:"0"));
		if(department!=null){//当选人界面在单位下 department为null
			
			mav.addObject("department", department);
			if(Strings.isNotBlank(searchText)){
				Pagination  p = new Pagination();
				List<V3xOrgDepartment> listDepartment = getSubDepartmentList(department.getId().toString());
				List<Long> listMember = getMemberList(department.getId().toString());
				int sizeNum = listDepartment!=null?listDepartment.size():0;
				sizeNum = sizeNum + (listMember!=null?listMember.size():0);
				p.setListDepartment(listDepartment);
				p.setListlong(listMember);
				List<V3xOrgDepartment> newListDepartment = p.paginationDepartment(1, new ArrayList<V3xOrgDepartment>());
				mav.addObject("departlist",newListDepartment);
				mav.addObject("pagecount", mobileUtil.getPageCount(sizeNum, MobileConstants.PAGE_COUNTER));
				mav.addObject("memberlist", p.paginationLong(1, MobileConstants.PAGE_COUNTER-(listDepartment.size()%MobileConstants.PAGE_COUNTER), new ArrayList<Long>()));
				mav.addObject("departmentMember", getDempartmentMap(newListDepartment));
			}
			
		}else{//选择的是部门。--单位
			//选择单位
			if("true".equals(request.getParameter("account"))){
				list = oaManagerInterface.getMobileOrgEntity(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
				mav.addObject("allAccount", true);
			}
			V3xOrgAccount account = oaManagerInterface.getAccount(MobileConstants.getCurrentId());
			if(Strings.isNotBlank(searchText)){
				Pagination  p = new Pagination();
				List<V3xOrgDepartment> listDepartment = getSubDepartmentList(account.getId().toString());
				int sizeNum = listDepartment!=null?listDepartment.size():0;
				p.setListDepartment(listDepartment);
				List<V3xOrgDepartment> newListDepartment = p.paginationDepartment(1, new ArrayList<V3xOrgDepartment>());
				mav.addObject("departlist",newListDepartment);
				mav.addObject("pagecount", mobileUtil.getPageCount(sizeNum, MobileConstants.PAGE_COUNTER));
				mav.addObject("departmentMember", getDempartmentMap(newListDepartment));
			}
			mav.addObject("account", account);
		}
		mav.addObject("page", currentPage);
		mav.addObject("listSize",(extendType!=null&&extendType.length()!=0)?getSelectedPersonFormById(cid,fid):getPersonNum(nid));//
		mav.addObject("extendType", (extendType!=null&&extendType.length()!=0)?extendType:null);*/
		mav.addObject("noMatch", Strings.isNotBlank(noMatch)?noMatch:null);
		mav.addObject("fid", fid);
		mav.addObject("atts", attsMap.get(MobileConstants.getCurrentId()));		
		
		return mav;
	}

	/**
	 * 为 协同模板 在没有匹配到人的情况下，进行选人
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView selectMemberByTemplate(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		String opinion = request.getParameter("opinion");
		String attitude = request.getParameter("attitude");
		String rid = request.getParameter("rid");

		setDepartmentMember(mav, MobileConstants.getCurrentDepartmentId().toString(),null);
		mav.addObject("type", type);
		mav.addObject("id", id);
		mav.addObject("opinion", opinion);
		mav.addObject("attitude", attitude);
		mav.addObject("rid", rid);
		return mav;
	}

	/**
	 * 根据id 得到一个部门对象
	 * 
	 * @param did
	 * @return
	 */
	private V3xOrgDepartment getDepartmentById(String did) {
		

		return oaManagerInterface.getDepartment(Long
				.parseLong(Strings.isNotBlank(did)? did
						: MobileConstants.getCurrentDepartmentId().toString()));
	}

	/**
	 * 得到 部门人员列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	private List<Long> getMemberList(String did) throws MobileException {

		List<Long> list = new ArrayList<Long>();

		List<V3xOrgMember> members = oaManagerInterface
				.getMemberByDepartment(Long.parseLong(Strings.isNotBlank(did) ? did : MobileConstants.getCurrentDepartmentId().toString()));
		if (members != null) {
			for (V3xOrgMember member : members) {
				// 判断 某一个人是否可以被 当前用户看到
				if (!CurrentUser.get().isInternal()) {
					if (!member.getIsInternal())
						list.add(member.getId());
				} else {
					if (oaManagerInterface.isSeen(member.getId(),
							MobileConstants.getCurrentId())) {
						list.add(member.getId());
					}
				}
			}
		}

		return list;
	}

	/**
	 * 得到 一个单位,或者 部门 下的子部门列表
	 * 
	 * @param id
	 * @return
	 * @throws MobileException
	 */
	private List<V3xOrgDepartment> getSubDepartmentList(String id)
			throws MobileException {
		Long did = Long.parseLong(Strings.isNotBlank(id)? id : "0");

		List<V3xOrgDepartment> listDepartment = oaManagerInterface.getDepartmentSubordinate(did);
		List<V3xOrgDepartment> newList = new ArrayList<V3xOrgDepartment>();
		for (V3xOrgDepartment depart : listDepartment) {
			//if (depart.getIsInternal().equals(CurrentUser.get().isInternal())) {
				newList.add(depart);
			//}
		}

		if (id == null) {
			return null;
		} else {
			return newList;
		}

	}

	/**
	 * 发送协同到A8
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	private ModelAndView sendCollaborationToA8(String title, String content,
			Collaboration collObj, HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		String actionType = actionMap.get(MobileConstants.getCurrentId());
		oaManagerInterface.sendCollaborationNow(title, Functions.toHTML(content),
				getPersonList(), (actionType == null ? 10 : actionType
						.equals("or") ? 1 : actionType.equals("and") ? 0 : 11),
				MobileConstants.getCurrentId(), (collObj != null ? collObj
						.getId() : null), ((collObj != null && collObj
						.getProcessId() != null) ? collObj.getProcessId()
						: null), null, null, null,request);
		newCollMap.put(MobileConstants.getCurrentId(), null);// 发送成功后，将 Value
		// 置为null
		newMap.put(MobileConstants.getCurrentId(), false);// 将开关置为false
		
		//协同发起事件通知
		
		return redirectModelAndView("/mob.do?method=getSendCollaborationList");
	}

	/**
	 * 保存协同到A8
	 * 
	 * @param title
	 * @param content
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView saveCollabortionToA8(String id, String title,
			String content, HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		String actionType = actionMap.get(MobileConstants.getCurrentId());
		int type = 2;
		if (actionType != null) {
			type = actionType.equals("or") ? 1 : 0;
		}
		List<String[]> list = getPersonList();
		list = (list == null) ? new ArrayList<String[]>() : list;
		Long aId = Long.parseLong(Strings.isNotBlank(id) ? id : "0");
		oaManagerInterface.saveToPendingAffair(aId == 0 ? null : aId, title,
				Functions.toHTML(content), list, type, MobileConstants.getCurrentId(),request);

		newCollMap.put(MobileConstants.getCurrentId(), null);// 发送成功后，将 Value
		// 置为null
		newMap.put(MobileConstants.getCurrentId(), false);// 将开关置为false

		return redirectModelAndView("/mob.do?method=getDoneCollaborationList");
	}

	/**
	 * 根据 Button得到ActionType
	 * 
	 * @param submit
	 * @param save
	 * @return
	 */
	private int getActionType(String submit, String save) {
		int actionType = 0;
		if (submit != null) {
			actionType = 1;
		} else {
			if (save != null) {
				actionType = 2;
			}
		}
		return actionType;
	}

	/**
	 * 根据 str 确定 态度
	 * 
	 * @param str
	 * @return
	 */
	private int getAttitude(String str) {
		if(str!=null){
			if (str.equalsIgnoreCase("readed")) {
				return 1;
			} else {
				if (str.equalsIgnoreCase("agree")) {
					return 2;
				} else {
					if (str.equalsIgnoreCase("disagree")) {
						return 3;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 根据节点id得到节点对象
	 * 
	 * @return
	 */
	private Nodes getNodeById(String nid, Nodes node) {
		class FindNode {
			private Nodes nodes;

			public Nodes getNode(String n, Nodes no) {
				if (n == null) {
					return no;
				} else {
					if (no.getNid().equals(n)) {
						nodes = no;
						return no;
					} else {
						List<Nodes> list = no.getChildren();
						for (Nodes nod : list) {
							getNode(n, nod);
						}
					}
				}
				return nodes;
			}
		}
		FindNode fn = new FindNode();
		return fn.getNode(nid, node);
	}
	
	/**
	 * 根据当前登录用户的id得到节点对象
	 * 
	 * @return
	 */
	private Nodes getNodeById(Nodes node) {
		class FindNode {
			private Nodes n;

			public Nodes getNode(Nodes no) {
				if(no!=null ){
					if (no.getUid()!=null && (no.getUid().equals(CurrentUser.get().getId())||no.getUid().equals(CurrentUser.get().getDepartmentId())||no.getUid().equals(CurrentUser.get().getAccountId())) && no.getPermission()!=null){
						n = no;
						return no;
					} else {
						List<Nodes> list = no.getChildren();
						for (Nodes nod : list) {
							getNode(nod);
						}
					}
					return n;
				}else{
					return null;
				}
			}
		}
		FindNode fn = new FindNode();
		return fn.getNode(node);
	}
	

	/**
	 * 显示跟踪事项的细节
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showTrackAffairDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/trackDetial");
		String id = request.getParameter("id");

		Collaboration collObject = null;
		Map<String,TIP_InputValueAll> map = null;
		MobileFormBean formBean = null;
		User user = CurrentUser.get();
		try{
			collObject = getCollObjectById(id);
			if(collObject.getContentType().equalsIgnoreCase("FORM")){
				formBean = oaManagerInterface.getFormAll(collObject.getId(), collObject.getSummaryId(), user);
			}
		}catch(MobileException e){
			//已办中的表单异常了。返回
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("search", request.getParameter("search"));
			return mobilePrompt(e.getMessage(), "/mob.do?method=getSendCollaborationList", parameters);
		}
		if (collObject != null) {
			mav.addObject("collaboration", collObject);
			if(collObject.getContentType().equalsIgnoreCase("FORM")){
				map = formBean.getFromApp();
				mav.addObject("formMap", map);
				List<String> formList = new ArrayList<String>(map.keySet());
				mav.addObject("keySet", formList);
				mav.addObject("isContainSub", formBean.getHasChildForm());
			}
			Object[] attachments =  oaManagerInterface.getColAttachment(collObject.getSummaryId());
			List<Attachment> att1 = (List<Attachment>) attachments[1];
			List<Attachment> att2 = (List<Attachment>) attachments[2];
			mav.addObject("att1", att1 != null?att1.size():0);//附件
			mav.addObject("att2", att2 != null?att2.size():0);//关联协同
			mav.addObject("opinLength", getColOpinionLength(collObject));
		}
		return mav;
	}

	/**
	 * 根据 ID 得到 协同对象
	 * 
	 * @param id
	 * @return
	 * @throws MobileException
	 */
	private Collaboration getCollObjectById(String id) throws MobileException {
		Long Id = Long.parseLong(Strings.isNotBlank(id) ? id : "0");
		try {
			Collaboration collObject = oaManagerInterface.CollaborationDetial(Id, MobileConstants.getCurrentId());

			return collObject;

		} catch (ColException e) {
			logger.debug("得到协同的详细信息异常：", e);
			throw new MobileException(e);
		}
	}

	/**
	 * 得到 待办事项的 列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getPendingSearchList(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mav)
			throws MobileException {
		String keyWorld = request.getParameter("search");
		List<Affair> affairList= oaManagerInterface.getPendingAffairObjectList(MobileConstants.getCurrentId(),Strings.isNotBlank(keyWorld)? keyWorld.trim() : null);
		mav.addObject("affairList", affairList);
		mav.addObject("search", Strings.isNotBlank(keyWorld)?keyWorld:null);
		int totalCount = com.seeyon.v3x.common.dao.paginate.Pagination.getRowCount();
		mav.addObject("affairCount", totalCount);
		com.seeyon.v3x.common.dao.paginate.Pagination.setRowCount(totalCount);
	}

	/**
	 * 得到 跟踪事项 的列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getTrackSearchList(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mav)
			throws MobileException {
		String turnType = request.getParameter("type1");
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");
		String affairTotalNum = request.getParameter("affairTotal");
		String keyWorld = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<Affair> followingAffairList = new ArrayList<Affair>();
		int followTotal = oaManagerInterface.getTrackAffairObjectList(
				MobileConstants.getCurrentId(), MobileConstants.PAGE_COUNTER,
				currentPage, followingAffairList,
				Strings.isNotBlank(keyWorld) ? keyWorld.trim() : null);

		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);
		addObjectMethod(followTotal, currentPage, mav, getTotal,
				getAffiarTotal, followingAffairList);
		mav.addObject("search", Strings.isNotBlank(keyWorld)?keyWorld:null);
	}

	/**
	 * 得到 待发事项的 列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getWaitSendCollSearchList(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mav)
			throws MobileException {
		String turnType = request.getParameter("type");// 翻页类型 0--prev,1--next
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");// 页面的总个数
		String affairTotalNum = request.getParameter("count");// 事项的总个数
		String keyWorld = request.getParameter("search");
		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<AffairsListObject> wiatSendList = new ArrayList<AffairsListObject>();
		int waitSendNum = oaManagerInterface.getCollaborationWaitSendList(
				MobileConstants.getCurrentId(), MobileConstants.PAGE_COUNTER,
				currentPage, wiatSendList,Strings.isNotBlank(keyWorld)? keyWorld.trim() : null);

		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);

		addObjectMethod(waitSendNum, currentPage, mav, getTotal,
				getAffiarTotal, wiatSendList);
		mav.addObject("search", Strings.isNotBlank(keyWorld)?keyWorld:null);
	}

	/**
	 * 得到 已发事项的列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getSendCollSearchList(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mav)
			throws MobileException {
		String turnType = request.getParameter("type1");// 翻页类型 0--prev,1--next
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");// 页面的总个数
		String affairTotalNum = request.getParameter("count");// 事项的总个数
		String keyWorld = request.getParameter("search");
		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<Affair> sendList = new ArrayList<Affair>();
		int sendNum = oaManagerInterface.getCollaborationSentList(
				MobileConstants.getCurrentId(), MobileConstants.PAGE_COUNTER,
				currentPage, sendList, Strings.isNotBlank(keyWorld)? keyWorld.trim() : null);

		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);

		addObjectMethod(sendNum, currentPage, mav, getTotal, getAffiarTotal,sendList);
		mav.addObject("search",Strings.isNotBlank(keyWorld)?keyWorld:null);
	}

	/**
	 * 得到 待办协同的列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getPendingCollSearchList(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mav)
			throws MobileException {
		String turnType = request.getParameter("type1");// 翻页类型 0--prev,1--next
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");// 页面的总个数
		String affairTotalNum = request.getParameter("count");// 事项的总个数
		String keyWorld = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<AffairsListObject> pendingList = new ArrayList<AffairsListObject>();
		int pendingNum = oaManagerInterface.getCollaborationPendingList(
				MobileConstants.getCurrentId(), MobileConstants.PAGE_COUNTER,
				currentPage, pendingList, Strings.isNotBlank(keyWorld) ? keyWorld.trim() : null);

		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);

		addObjectMethod(pendingNum, currentPage, mav, getTotal, getAffiarTotal,pendingList);
		mav.addObject("search", Strings.isNotBlank(keyWorld)?keyWorld:null);
	}

	/**
	 * 得到 已办协同 列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getDoneCollSearchList(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mav)
			throws MobileException {
		String turnType = request.getParameter("type1");// 翻页类型 0--prev,1--next
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");// 页面的总个数
		String affairTotalNum = request.getParameter("count");// 事项的总个数
		String keyWorld = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<Affair> doneList = new ArrayList<Affair>();
		int doneNum = oaManagerInterface.getCollaborationDoneList(
				MobileConstants.getCurrentId(), MobileConstants.PAGE_COUNTER,
				currentPage, doneList, Strings.isNotBlank(keyWorld) ? keyWorld.trim() : null);

		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);
		//Collections.sort(doneList);
		addObjectMethod(doneNum, currentPage, mav, getTotal, getAffiarTotal,doneList);
		mav.addObject("search", Strings.isNotBlank(keyWorld)?keyWorld:null);
	}

	class ListNode {
		private List<Nodes> list = new ArrayList<Nodes>();

		private String nodeName;

		/**
		 * 根据 Node得到 需要显示的List<Nodes>
		 * 
		 * 将串发流程中的 所有的 节点都在一个页面上显示出来
		 * 
		 * @return
		 */
		public List<Nodes> getListNodesByNode(Nodes node) {

			if (node.getChildren() == null || isChildNodeAble(node) || node.getChildren().size() == 0) {

				return (node.getChildren() == null) ? list : node.getChildren();
			} else {
				for (Nodes n : node.getChildren()) {
					if (!list.contains(n) && n.getIsDelete().equals("false")) {
						list.add(n);
					}
					getListNodesByNode(n);
				}
			}
			return list;
		}

		public boolean isChildNodeAble(Nodes node){
			List<Nodes> children = node.getChildren();
			int i=0;
			if(children!=null && children.size()>1){
				for(Nodes n : children){
					if(n!=null && n.getIsDelete().equals("false"))
						i = i+1;
				}
			}
			if(i>=2){
				return true;
			}else{
				return false;
			}
		}
		/**
		 * 根据 开始节点 和 节点id 取得 该节点的Name
		 * 
		 * @param node
		 * @param nid
		 * @return
		 */
		public String getNodeName(Nodes node, String nid) {
			if (node != null) {
				if (node.getNid().equals(nid)) {
					return node.getNodename();
				} else {
					for (Nodes n : node.getChildren()) {
						if (n.getNid().equals(nid)) {
							nodeName = n.getNodename();
							break;
						} else {
							getNodeName(n, nid);
						}
					}
					return nodeName;
				}
			}
			return nodeName;
		}
		
	}

	/**
	 * 全文检索 入口
	 * 
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView searchEntrance(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/search/index");

		getContextSearch(request, response, mav);

		return mav;
	}

	/**
	 * 得到 全文检索 的列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getContextSearch(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mav)
			throws MobileException {

		String pageNum = request.getParameter("page");
		String turnType = request.getParameter("prev");
		String keyWorld = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<SearchResult> searchResult = new ArrayList<SearchResult>();
		if(keyWorld!=null&&keyWorld.length()!=0){
			int searchNum = oaManagerInterface.searchResult(MobileConstants.getCurrentId(),
					keyWorld, MobileConstants.PAGE_COUNTER, currentPage,searchResult);

			int getTotal = mobileUtil.getPageCount(searchNum,MobileConstants.PAGE_COUNTER);

			addObjectMethod(searchNum, currentPage, mav, getTotal, searchNum,searchResult);
		}
		mav.addObject("search", keyWorld);
	}

	/**
	 * 在线沟通 入口
	 * 
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView onlineCommunicationEntrance(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/online/communicate");
		mav.addObject("size", oaManagerInterface.getOnLineNum());
		Integer[] num = {0,0};
		try {
			num = userMessageManager.countNewMessage(CurrentUser.get().getId());
		} catch (MessageException e1) {
			logger.error("", e1);
		}
		mav.addObject("sysMessageNum", num[0]==null?0:num[0]);
		mav.addObject("personMessageNum",num[1]==null?0:num[1]);
		return mav;
	}

	/**
	 * 会议 入口
	 * 
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView meetListEntrance(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/meeting/list");

		getMeetSearchList(request, mav);

		return mav;
	}

	/**
	 * 公告 列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getBullList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/bulletin/list");

		getBullSearchList(request, mav);

		return mav;
	}

	/**
	 * 新闻列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getNewsList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/news/list");

		getNewsSearchList(request, mav);

		return mav;
	}

	/**
	 * 日程列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getCalendarList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/calendar/list");

		getCalendarSearchList(request, mav);

		return mav;
	}

	/**
	 * 在线人员列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getOnLineMemberList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/online/onLineMember");

		getOnLineMemberSearchList(request, mav);

		return mav;
	}

	/**
	 * 关联人员列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getRelateMemberList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/online/relativeMember");

		getRelativeMemberSearchList(request, mav);

		return mav;
	}

	/**
	 * 部门人员列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 * 
	 */
	public ModelAndView getMyDepartmentMemberList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/online/employys");

		getMyDepartmentMemberSearchList(request, mav);

		return mav;
	}
	
	/**
	 * 员工 通讯录
	 * @param request
	 * @param response
	 * @return employys
	 */
	public ModelAndView getAddressBook(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/online/employys");
		String id = request.getParameter("id");//部门ID
		String path = request.getParameter("path");
		String pageNum  = request.getParameter("pageNum");
		String type = request.getParameter("type");
		Long departmentId = CurrentUser.get().getDepartmentId();
		
		if(Strings.isNotBlank(path)){
			V3xOrgDepartment orgDepart = oaManagerInterface.getDepartmentByPath(path,null);
			if(orgDepart!=null){
				departmentId = orgDepart.getId();
			}else{//得到 单位
				departmentId = CurrentUser.get().getAccountId();
				V3xOrgAccount account = oaManagerInterface.getAccount(CurrentUser.get().getId());
				mav.addObject("account", account);
				mav.addObject("parentPath", 0);
			}
		}else{
			if(Strings.isNotBlank(id)){
				departmentId = Long.valueOf(id);
			}
		}
		List<MobileOrgEntity> list = oaManagerInterface.getMobileOrgEntity(departmentId);
		V3xOrgDepartment department = oaManagerInterface.getDepartment(departmentId);
		String parentPath = department!=null?department.getParentPath():null;
		MobileUtil util = new MobileUtil();
		int size = list!=null?list.size():0;
		int currentPage = util.getCurrentPage(type, pageNum);
		int totlaPageNum = util.getPageCount(size, MobileConstants.PAGE_COUNTER);
		mav.addObject("entitys", Pagination.paginationObjectList(list,currentPage));
		if(parentPath!=null){
			mav.addObject("parentPath", parentPath);
		}
		mav.addObject("department", department);
		mav.addObject("pageNum", currentPage);
		mav.addObject("totlaPageNum",totlaPageNum);
		mav.addObject("size", size);
		return mav;
	}
	
	/**
	 * 私人 通讯录
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getAddressBookPrivate(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/online/privateMembers");
		String pageNum = request.getParameter("pageNum");
		String type = request.getParameter("type");
		
		List<MobileBookEntity> list = oaManagerInterface.getAllOutTeam(CurrentUser.get().getId());
		
		MobileUtil util = new MobileUtil();
		int size = list!=null?list.size():0;
		int currentPage = util.getCurrentPage(type, pageNum);
		int totlaPageNum = util.getPageCount(size, MobileConstants.PAGE_COUNTER);
		
		mav.addObject("members", Pagination.paginationObjectList(list, currentPage));
		mav.addObject("pageNum", currentPage);
		mav.addObject("totlaPageNum",totlaPageNum);
		mav.addObject("size", size);
		return mav;
	}

	/**
	 * 展现 组内的人员
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showTeamMembers(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/online/privateMembers");
		String pageNum = request.getParameter("pageNum");
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		Long teamId = Strings.isNotBlank(id)?Long.valueOf(id):null;
		
		List<MobileBookEntity> list = oaManagerInterface.showTeamMembers(teamId);
		MobileBookEntity teamObj = oaManagerInterface.showTeamName(teamId);
		
		MobileUtil util = new MobileUtil();
		int size = list!=null?list.size():0;
		int currentPage = util.getCurrentPage(type, pageNum);
		int totlaPageNum = util.getPageCount(size, MobileConstants.PAGE_COUNTER);
		
		mav.addObject("members", Pagination.paginationObjectList(list, currentPage));
		mav.addObject("pageNum", currentPage);
		mav.addObject("totlaPageNum",totlaPageNum);
		mav.addObject("size", size);
		mav.addObject("teamObj", teamObj);
		
		return mav;
	}
	/**
	 * 得到会议的细节
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showMeetDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/meeting/detail");
		String id = request.getParameter("id");
		String search = request.getParameter("search");
		String source = request.getParameter("source");

		Long mid = Long.parseLong(Strings.isNotBlank(id)? id : "0");
		MeetingDetial meetObject = mid!=0?oaManagerInterface.getMeetingDetial(mid,MobileConstants.getCurrentId()):null;
		List<Attachment> attachments = oaManagerInterface.getAttachment(mid);
		Object[] files = splitFile(attachments);
		if(files != null){
			mav.addObject("fileAtt", files[0]);
			mav.addObject("docAtt", files[1]);
		}
		if (meetObject != null) {
			if (meetObject.getBeginDate().before(new Date())
					|| (MobileConstants.getCurrentId().equals(
							meetObject.getMasterId()) && meetObject
							.getCreator().equals(meetObject.getMasterId()))
					|| (MobileConstants.getCurrentId().equals(
							meetObject.getCreator()) && (meetObject
							.getCreator().equals(meetObject.getRecordId())))) {
				mav.addObject("off", true);
			}
			mav.addObject("meeting", meetObject);
			mav.addObject("receipt", getMeettingAttitudeOfCurrentUser(meetObject.getAttenders()));
			List<MtReplyCAP> mtReply= this.oaManagerInterface.findByMeetingIdAndUserId(mid, MobileConstants.getCurrentId());
			if(CollectionUtils.isNotEmpty(mtReply)){
				mav.addObject("reply", mtReply.get(0));
			}
		}else{
			return rendJavaScriptMobileMsg(response,"","meeting.delete.label");
		}
		mav.addObject("search",Strings.isNotBlank(search)? search : null);
		mav.addObject("source", Strings.isNotBlank(source)?source:null);
		return mav;
	}

	/**
	 * 得到个人的细节
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showPersonDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/online/personDetial");

		String nid = request.getParameter("nid");
        boolean isCanSendSMS = false;
        if(Strings.isNotBlank(nid)){
		    Long uid = Long.parseLong(nid);
		    V3xOrgMember member = oaManagerInterface.getMemberById(uid);
		    if(member != null){
		        mav.addObject("userName", member.getName());
                if(Strings.isNotBlank(member.getTelNumber())){
                    mav.addObject("phoneNum", member.getTelNumber());
                    //判断是否具有发送手机短信的权限
                    isCanSendSMS = mobileMessageManager.isCanSend(uid, CurrentUser.get().getLoginAccount());
                }
                
            }
        }
		mav.addObject("isCanSendSMS", isCanSendSMS);

		return mav;
	}

	/**
	 * 显示 公告的细节
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showBulletionDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/bulletin/detail");
		String id = request.getParameter("id");
		String search = request.getParameter("search");
		User user = CurrentUser.get();
		Long bid = Long.parseLong(Strings.isNotBlank(id)?id : "0");

		BulDataController controller = (BulDataController)ApplicationContextHolder.getBean("bulDataController");
		DataCache<BulData> dataCache = controller.getBulDataCache().getDataCache();
		BulData bul = dataCache.get(bid);
		boolean hasCache = false;
		Bulletion bullDetial = null;
		if(bul == null){
			bul = oaManagerInterface.getBulletionDetial(bid,MobileConstants.getCurrentId());
		}else{
			hasCache = true;
		}
		bullDetial = oaManagerInterface.getBulletionDetial(bul);
		
		if(bullDetial!=null){
			if(bullDetial.isDeleteFlag()||bullDetial.getBulState()==0){
				if(bullDetial.isDeleteFlag()){
					return rendJavaScriptMobileMsg(response,"","bul.delete.label");
				}
				if(bullDetial.getBulState()==0){
					return rendJavaScriptMobileMsg(response,"","bul.cancel.label");
				}
				
			}else{
				mav.addObject("bulletion", bullDetial);
				mav.addObject("name", oaManagerInterface.getMemberById(bullDetial.getSenderId()).getName());
				List<Attachment> attList = bullDetial.getAttachmentList();
				Object[] attJoin = splitFile(attList);
				if(attJoin != null){
					mav.addObject("fileAtt", attJoin[0]);
					mav.addObject("docAtt", attJoin[1]);
				}
				//记录阅读信息
				controller.recordBulRead(bid,bul,hasCache,user);
			}
		}else{
			return rendJavaScriptMobileMsg(response,"","bul.delete.label");
		}
		mav.addObject("search", Strings.isNotBlank(search) ? search : null);
		return mav;
	}
	
	/**
	 * 弹出 alert 提示框
	 * @param response
	 * @param key
	 * @return
	 */
	private ModelAndView rendJavaScriptMobileMsg(HttpServletResponse response,String message ,String key){
		try {
			super.rendJavaScript(response, "alert('" + message + MobileConstants.getValueFromMobileRes(key)+ "');history.back();");
		} catch (IOException e) {
			logger.info("移动应用提示异常");
		}
		return null;
	}
	/** 
	 * 弹出 alert 提示框
	 * @param response 
	 * @param strMessage 数组: 0 前面的Msg, 1 国际化的Key
	 * @return
	 */
	private ModelAndView rendJavaScriptMobileKey(HttpServletResponse response,String key ,String message){
		
		try {
			super.rendJavaScript(response, "alert('" +MobileConstants.getValueFromMobileRes(key) +  message + "');history.back();");
		} catch (IOException e) {
			logger.info("移动应用提示异常");
		}
		return null;
	}
	
	/**
	 * 显示 新闻的细节
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showNewsDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/news/detail");
		String id = request.getParameter("id");
		String search = request.getParameter("search");
		
		Long nid = Long.parseLong(Strings.isNotBlank(id) ? id : "0");
		News newsDetial = oaManagerInterface.getNewsDetial(nid, MobileConstants
				.getCurrentId());
		if(newsDetial!=null){
			if(newsDetial.isDeletedFlag()||newsDetial.getState()==0){
				if(newsDetial.isDeletedFlag()){
					return rendJavaScriptMobileMsg(response,"","news.prompt.1");
				}
				else{
					return rendJavaScriptMobileMsg(response,"","news.prompt.2");
				}
			}
			mav.addObject("news", newsDetial);
			mav.addObject("sender", oaManagerInterface.getMemberById(newsDetial.getSenderId()).getName());
			List attList = newsDetial.getAttachments();
			mav.addObject("have",attList != null && attList.size() != 0 ? "" : null);
			mav.addObject("attSize", attList.size());
		}
		mav.addObject("search", Strings.isNotBlank(search) ? search : null);
		
		return mav;
	}

	/**
	 * 显示 发送消息的页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView sendMessageToPerson(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/online/sendSMS");
		String id = request.getParameter("id");

		V3xOrgMember person = oaManagerInterface.getMemberById(Long.parseLong((Strings.isNotBlank(id) ? id : "0")));

		mav.addObject("userName", person!=null?person.getName():"");

		return mav;
	}

	/**
	 * 将消息发送的 A8 系统内
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 * @throws MessageException 
	 */
	public ModelAndView sendMessageToA8(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		String id = request.getParameter("id");
		String content = request.getParameter("sms");
        String messageType = request.getParameter("type");
        String from  = request.getParameter("f");
        Map<String,String> parameter = new HashMap<String,String>();
        parameter.put("id", id);
        parameter.put("cid", request.getParameter("cid"));
        parameter.put("affairType", request.getParameter("affairType"));
        parameter.put("onLineType", request.getParameter("onLineType"));
        parameter.put("source", request.getParameter("source"));
        parameter.put("search", request.getParameter("search"));
        parameter.put("type", request.getParameter("type"));
        parameter.put("f", from);
        if(Strings.isBlank(content)){
        	return mobilePrompt(MobileConstants.getValueFromMobileRes("online.message.notnull"),"/mob.do?method=sendMessageToPerson",parameter);
        }else if(content.length() > 200){
        	return mobilePrompt(MobileConstants.getValueFromMobileRes("online.message.overflow"),"/mob.do?method=sendMessageToPerson",parameter);
        }
        if(Strings.isNotBlank(id)){
            V3xOrgMember person = oaManagerInterface.getMemberById(Long.parseLong(id));
            if (Strings.isNotBlank(content)) {
                if("shortmess".equals(messageType)){
                    Long[] receiverIdsArray = {person.getId()};
                    mobileMessageManager.sendMobilePersonMessage(content, MobileConstants.getCurrentId(), new Date(), receiverIdsArray);
                }
                else{                    
                    if(Strings.isNotBlank(content)){
                        try {
                            userMessageManager.sendPersonMessage(content, MobileConstants.getCurrentId(), person.getId());
                        }
                        catch (MessageException e) {
                            logger.error("发送在线消息异常");
                            throw new MobileException(e);
                        }
                    }
                }
            }
        }
        if(Strings.isBlank(from)){
        	return new ModelAndView("mobile/online/personDetial");
        }else{
        	return showPerson(request,response);
        }
	}

	/**
	 * 显示 日程的细节
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showCalendarDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/calendar/detail");
		String id = request.getParameter("id");
		String search = request.getParameter("search");
		
		Long cid = Long.parseLong(Strings.isNotBlank(id)? id : "0");

		Calendar calDetial = oaManagerInterface.getCalendarDetial(cid,MobileConstants.getCurrentId());
		if(calDetial==null){
			return rendJavaScriptMobileMsg(response,"","calendar.prompt.1");
		}
		if(calDetial==null){
			
		}
		List<Attachment> list = calDetial.getAttachments();
		
		mav.addObject("calendar", calDetial);
		mav.addObject("have", (list!=null&&list.size()!=0)?list:null);
		mav.addObject("attSize", list.size());
		mav.addObject("search",Strings.isNotBlank(search)? search : null);
		return mav;
	}

	/**
	 * 得到 某一个流程中节点 所选的 所有人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getMemberList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/showMember");
		String id = request.getParameter("id");// 节点id
		String cid = request.getParameter("cid");

		Long coId = Long.parseLong(Strings.isNotBlank(cid) ? cid : "0");
		Long summaryId = this.affairManager.getObjectIdByAffairId(coId);
		
		ColSummary summary=null;
		try {
			summary = colManager.getColSummaryById(summaryId, false);
		} catch (ColException e) {
			logger.error("",e);
		}
		Long caseId = summary!=null?summary.getCaseId():null;
		String processId = summary!=null?summary.getProcessId():null;
		
		Map<String, Object> map = oaManagerInterface.getNodes(summaryId,caseId,processId,false);

		Map<String, List<Object[]>> caseWorkItemLog = (Map<String, List<Object[]>>) (map != null ? map
				.get("caseWorkItemLog") : new HashMap<String, List<Object[]>>());

		List<Object[]> listObj = caseWorkItemLog != null ? caseWorkItemLog
				.get(id) : new ArrayList<Object[]>();

		int size = listObj != null ? listObj.size() : 0;
		MobileUtil mobileUtil = new MobileUtil();

		List<Object[]> currentlist = Pagination.paginationObjectList(listObj,1);

		mav.addObject("members", currentlist);
		mav.addObject("total", size);
		mav.addObject("page", 1);
		mav.addObject("size", mobileUtil.getPageCount(size,MobileConstants.PAGE_COUNTER));
		return mav;
	}

	/**
	 * 得到表单的细节
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getFormDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/form/detial");
		String cid = request.getParameter("id");
		String currentPage = request.getParameter("page");
		String trunType = request.getParameter("pre");

		Long id = Long.parseLong(Strings.isNotBlank(cid)? cid : "0");

		String formName = oaManagerInterface.getFormName(id);

		//List<String> formList = getAllFormKey(trunType, currentPage,cid);
		List<String> formList = new ArrayList<String>();
		Map<String,TIP_InputValueAll> allFormKey = oaManagerInterface.getFormList(id, false);
		if(allFormKey!= null){
			formList = new ArrayList<String>(allFormKey.keySet());
		}
		MobileUtil mobileUtil = new MobileUtil();
		int page = mobileUtil.getCurrentPage(trunType, currentPage);
		List<String> list = Pagination.paginationObjectList(formList,page);

		int pageCount = mobileUtil.getPageCount(formList != null ? formList.size() : 0, MobileConstants.PAGE_COUNTER);
		
		Map<String,TIP_InputValueAll> mapDetial = this.getFormDetial().get(getSummaryId(cid));
		Map<String,TIP_InputValueAll> mapDeal = this.getFormDeal().get(getSummaryId(cid));
		Map<String,TIP_InputValueAll> newMapDeal = new HashMap<String,TIP_InputValueAll>();
		
		for(String s : mapDeal.keySet()){
			newMapDeal.put(s, mapDeal.get(s));
		}for(String s : mapDetial.keySet()){
			newMapDeal.put(s, mapDetial.get(s));
		}
			
		mav.addObject("keySet", list);
		mav.addObject("formMap", newMapDeal);
		mav.addObject("formName", formName);
		mav.addObject("pagenumber", page);
		mav.addObject("size", formList != null ? formList.size() : 0);
		mav.addObject("pagecount", pageCount);
		return mav;
	}

	private List<String> getFormKey(String trunType, String currentPage,
			boolean isDetial,String cid) throws MobileException {
		Long affairId = Long.parseLong(Strings.isNotBlank(cid)?cid:"0");
		Affair affair  = affairManager.getById(affairId);
		Long summaryId = affair!=null?affair.getObjectId():null;
		String strSummaryId = summaryId!=null?summaryId.toString():null;
		Set<String> keySet = null;
		if (isDetial) {
			keySet = this.getFormDetial() == null ?null: this.getFormDetial().get(strSummaryId)==null?null:this.getFormDetial().get(strSummaryId).keySet();
		} else {
			keySet = this.getFormDeal() == null ? null:this.getFormDeal().get(strSummaryId)==null?null:this.getFormDeal().get(strSummaryId).keySet();
		}
		List<String> formList = new ArrayList<String>();
		if(keySet!=null){
			for (String str : keySet) {
				formList.add(str);
			}
		}
		return formList;
	}
	
	private List<String> getAllFormKey(String trunType,String currentPage,String cid)throws MobileException{
		
		
		String strSummaryId = getSummaryId(cid);
		
		Set<String> keyDetailSet = this.getFormDetial() != null ? this.getFormDetial().get(strSummaryId).keySet() : null;
		Set<String> keyDealSet = this.getFormDeal() != null ? this.getFormDeal().get(strSummaryId).keySet() : null;
		
		List<String> formList = new ArrayList<String>();
		for (String str : keyDetailSet) {
			formList.add(str);
		}
		for (String str : keyDealSet) {
			formList.add(str);
		}
		return formList;
	}

	/**
	 * 得到 表单处理的所有的 key
	 * 
	 * @return
	 */
	private List<String> getAllFormDealKey(String cid) {
		Long affairId = Long.parseLong(Strings.isNotBlank(cid)?cid:"0");
		Affair affair  = affairManager.getById(affairId);
		Long summaryId = affair!=null?affair.getObjectId():null;
		String strSummaryId = summaryId!=null?summaryId.toString():null;
		
		List<String> list = new ArrayList<String>();
		Set<String> keySet = this.getFormDeal() == null ?null: this.getFormDeal().get(strSummaryId)==null?null:this.getFormDeal().get(strSummaryId).keySet();
		if (keySet != null) {
			for (String str : keySet) {
				list.add(str);
			}
			return list;
		} else {
			return null;
		}
	}

	/**
	 * 将表单输入项 写值
	 * 
	 * @param key
	 * @param value
	 */
	private String setDealFormValue(String key, String value,String cid,Map<String, TIP_InputValueAll> map,Map<String,TIP_InputValueAll> mobileFormMap) {
		String summaryId = getSummaryId(cid);
		Map<String, TIP_InputValueAll> dealMap = this.getFormDeal().get(summaryId);
		if(dealMap == null){
			dealMap = new HashMap<String, TIP_InputValueAll>();
		}
		Map<String, TIP_InputValueAll> mapExtend = this.getFormPerson().get(CurrentUser.get().getId());
		if (map != null) {
			TIP_InputValueAll obj = map.get(key);
			TIP_InputValueAll formObj = mapExtend!=null?mapExtend.get(cid):mobileFormMap.get(key);
			TIP_InputValueAll mobileFormObject = (TIP_InputValueAll)mobileFormMap.get(key);
			//扩展控件的值等于他们现在的值，客户端处理得不到
			if (obj != null && !obj.getType().equals(TFieldInputType.fitExtend) && !obj.getType().equals(TFieldInputType.fitComboedit)
					&& Strings.isBlank(obj.getStageCalculateXml())
					&& !obj.getType().equals(TFieldInputType.fitHandwrite)){
				String valiValue = valiForm(obj, value);
				if (valiValue == null){
					obj.setValue(value);
					obj.setDisplayValue(value);
					
					mobileFormObject.setValue(value);
					mobileFormObject.setDisplayValue(value);
					
					dealMap.put(obj.getId(), obj);
				}
				else
					return valiValue;
			}else{
				if(formObj!=null && obj.getName().equals(formObj.getName())){
					obj.setValue(formObj.getValue());
					obj.setDisplayValue(formObj.getDisplayValue());
					dealMap.put(obj.getId(), obj);
				}
			}
			this.getFormDeal().put(summaryId, dealMap);
		}
		return null;
	}

	/**
	 * 验证 表单提交的数据格式是否符合标准 如果格式没有错误则返回Null，如错误则返回相应的提示
	 * 
	 * @param formObj
	 * @param value
	 * @return
	 */
	private String valiForm(TIP_InputValueAll formObj, String value) {
		String string = null;
		if (formObj.getFieldtype().equals("VARCHAR")) {

			string = isVarchar(formObj.getFieldlength(), formObj.isIs_null(),
					value) != null ? formObj.getName() : null;
		}
		if (formObj.getFieldtype().equals("DECIMAL")) {
			string = isDecimal(formObj.getFieldlength(), formObj.isIs_null(),
					formObj.getDigit(), value) != null ? formObj.getName()
					: null;
		}
		if (formObj.getFieldtype().equals("TIMESTAMP")) {
			string = isDate(formObj.isIs_null(), value) != null ? formObj
					.getName() : null;
		}
		if (formObj.getFieldtype().equals("DATETIME")) {
			string = isDateTime(formObj.isIs_null(), value) != null ? formObj
					.getName() : null;
		}
		//不允许为空
		if(!formObj.isIs_null() && Strings.isBlank(value)){
			string = MobileConstants.getValueFromMobileRes("mob.validate.not_null", formObj.getName());
		}
		return string;
	}

	/**
	 * 判断是不是符合 VARCHAR
	 * 
	 * @param fieldlength
	 * @param isNull
	 * @param value
	 * @return
	 */
	private String isVarchar(String fieldlength, boolean isNull, String value) {
		if(isNull){//可以为空
			if(value!=null){
				if(value.length()<Integer.parseInt(fieldlength))
					return null;
				else
					return "字数过多";
			}else
				return null;
		}else{//不能为空
			if(value!=null){
				if(value.length()<Integer.parseInt(fieldlength)&&value.length()>0)
					return null;
				else
					return "字数过多";
			}else
				return "内容不能为空";
		}
		
	}

	/**
	 * 判读是不是符合 DECIMAL
	 * 
	 * @param fieldlength
	 * @param isNull
	 * @param digit
	 * @param value
	 * @return
	 */
	private String isDecimal(String fieldlength, boolean isNull, String digit,
			String value) {
		Pattern p = Pattern.compile("^[-0-9.]{1,}$");// 匹配是否为数字
		Matcher m = p.matcher(value != null ? value : "");
		String result = null;
		try {
			String[] values = value.split("[.]");
			String s = values.length > 2 ? values[1] : "";
			result = ((value == null ||value.length()==0 )&& isNull == true) ? null
					: (value != null && value.length() > Integer
							.parseInt(fieldlength)) ? "数字的长度超过规定的字数国际化" : (s
							.length() > Integer.parseInt(digit != null ? digit
							: "0") ? "小数点的位数国际化" : m.matches() ? null
							: "不是数字国际化");

		} catch (Exception e) {
			result = "";
		}
		return result;
	}

	/**
	 * 验证是不是时间格式 正确格式：2008-08-08/2008-8-8/
	 * 
	 * @param isNull
	 * @param value
	 * @return
	 */
	private String isDate(boolean isNull, String value) {
		String string = "非法时间";

		Pattern pYear = Pattern.compile("^[1-9][0-9]{3}$");// 匹配年
		Pattern pMonth = Pattern.compile("^0[1-9]|^[1-9]|^1[0-2]");// 匹配月
		Pattern pDay = Pattern
				.compile("^[1-9]|^0[1-9]|^1[0-9]|^2[0-9]|^3[0-1]");// 匹配天

		String[] str = value != null ? value.split("-") : null;
		if (str != null&&value!=null&&value.length()!=0) {
			if (str.length < 3)
				return string;
			for (int i = 0; i < str.length; i++) {
				switch (i) {
				case 0:
					Matcher my = pYear.matcher(str[i]);
					if (!my.matches()) {
						return string;
					}
					break;
				case 1:
					Matcher mm = pMonth.matcher(str[i]);
					if (!mm.matches()) {
						return string;
					}
					break;
				case 2:
					Matcher md = pDay.matcher(str[i]);
					if (!md.matches()) {
						return string;
					}
					break;
				default:
				}
			}
		}else{
			if(isNull){
				return null;
			}else{
				return string;
			}
		}

		return null;
	}

	/**
	 * 验证是不是 时间日期格式的数据 2008-08-08 23:59
	 * 
	 * @param isNull
	 * @param value
	 * @return
	 */
	private String isDateTime(boolean isNull, String value) {
		String[] string = value.split(" ");// 根据 空格 来分开 日期 和 时间
		String str = null;
		if (string != null) {
			for (int i = 0; i < string.length; i++) {
				switch (i) {
				case 0:
					str = isDate(isNull, string[i]);
					break;
				case 1:
					str = isTime(isNull, string[i]);
					break;
				default:
				}
			}
		}
		return null;
	}

	private String isTime(boolean isNull, String value) {
		if (isNull && (value == null || value.length() == 0)) {
			return null;
		} else {
			if (value == null) {
				return "时间不能为空国际化";
			} else {
				Pattern pHour = Pattern.compile("^[0-1][0-9]|^2[0-3]|[0-9]");// 匹配小时
				Pattern pMinute = Pattern.compile("[0-9]|^[0-5][0-9]");// 匹配分钟
				String[] strArray = value.split(":");

				Matcher hour = pHour.matcher(strArray[0]);
				Matcher minute = pMinute.matcher(strArray[1]);

				if (!hour.matches() && !minute.matches()) {
					return "时间不匹配";
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * 得到 会议 的列表
	 * 
	 * @param request
	 * @param response
	 * @param mavo
	 * @throws MobileException
	 */
	private void getMeetSearchList(HttpServletRequest request, ModelAndView mav)
			throws MobileException {
		String turnType = request.getParameter("type");
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");// 总的 页码数
		String affairTotalNum = request.getParameter("count");// 总事项个数
		String keyWorld = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<AffairsListObject> meetList = new ArrayList<AffairsListObject>();
		int meetingTotal = oaManagerInterface.getMeetingObjectList(
				MobileConstants.getCurrentId(), MobileConstants.PAGE_COUNTER,
				currentPage, meetList, Strings.isNotBlank(keyWorld)? keyWorld.trim() : null);

		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);
		List<AffairsListObject> newList = Pagination.paginationObjectList(meetList, currentPage);

		addObjectMethod(meetingTotal, currentPage, mav, getTotal,getAffiarTotal, newList);
		mav.addObject("search", Strings.isNotBlank(keyWorld)?keyWorld:null);
	}

	/**
	 * 得到 公告 的列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getBullSearchList(HttpServletRequest request, ModelAndView mav)
			throws MobileException {
		String turnType = request.getParameter("type");
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");
		String affairTotalNum = request.getParameter("count");
		String keyWorld = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<AffairsListObject> bullList = new ArrayList<AffairsListObject>();
		int bullTotal = oaManagerInterface.BulletinList(MobileConstants
				.getCurrentId(), MobileConstants.PAGE_COUNTER, currentPage,
				bullList,
				Strings.isNotBlank(keyWorld)? keyWorld.trim(): null);

		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);

		addObjectMethod(bullTotal, currentPage, mav, getTotal, getAffiarTotal,bullList);
		mav.addObject("search",Strings.isNotBlank(keyWorld)?keyWorld:null);

	}

	/**
	 * 得到 新闻的列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getNewsSearchList(HttpServletRequest request, ModelAndView mav)
			throws MobileException {
		String turnType = request.getParameter("type");
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");
		String affairTotalNum = request.getParameter("count");
		String keyWorld = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<AffairsListObject> newsList = new ArrayList<AffairsListObject>();
		int newsTotal = oaManagerInterface.NewsList(MobileConstants
				.getCurrentId(), MobileConstants.PAGE_COUNTER, currentPage,
				newsList,
				Strings.isNotBlank(keyWorld) ? keyWorld.trim(): null);

		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);

		addObjectMethod(newsTotal, currentPage, mav, getTotal, getAffiarTotal,newsList);
		mav.addObject("search", Strings.isNotBlank(keyWorld)?keyWorld:null);
	}

	/**
	 * 得到 日程列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getCalendarSearchList(HttpServletRequest request,
			ModelAndView mav) throws MobileException {
		String turnType = request.getParameter("type");
		String pageNum = request.getParameter("page");
		String pageTotalNum = request.getParameter("total");
		String affairTotalNum = request.getParameter("count");
		String keyWorld = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		int currentPage = mobileUtil.getCurrentPage(turnType, pageNum);
		List<AffairsListObject> calList = new ArrayList<AffairsListObject>();
		int calTotal = oaManagerInterface.CalendarList(MobileConstants.getCurrentId(), 
															MobileConstants.PAGE_COUNTER, 
															currentPage,calList,Strings.isNotBlank(keyWorld) ? keyWorld.trim() : null);
		int getTotal = mobileUtil.initVariable(pageTotalNum);
		int getAffiarTotal = mobileUtil.initVariable(affairTotalNum);

		addObjectMethod(calList.size(), currentPage, mav, getTotal, getAffiarTotal,pageAffairsList(currentPage,getTotal,calList));
		mav.addObject("search", Strings.isNotBlank(keyWorld)?keyWorld:null);
	}
	
	private List<AffairsListObject> pageAffairsList(int currentPage,int pageTotal,List<AffairsListObject> calList){
		int totalAffair = calList!=null?calList.size():0;
		if(totalAffair==0){
			return new ArrayList<AffairsListObject>();
		}else{
			if(totalAffair<=MobileConstants.PAGE_COUNTER){
				if(currentPage==1){
					return calList;
				}
			}else{
				if(currentPage==pageTotal){
					int num = totalAffair%MobileConstants.PAGE_COUNTER;
					if(num==0){
						return calList.subList(totalAffair-MobileConstants.PAGE_COUNTER, totalAffair);
					}else{
						return calList.subList(totalAffair-num, totalAffair);
					}
				}else{
					return calList.subList((currentPage-1)*MobileConstants.PAGE_COUNTER, (currentPage)*MobileConstants.PAGE_COUNTER);
				}
			}
		}
		return new ArrayList<AffairsListObject>();
	}

	/**
	 * 得到 在线人员列表
	 * 
	 * @param request
	 * @param response
	 * @param mav
	 * @throws MobileException
	 */
	private void getOnLineMemberSearchList(HttpServletRequest request,
			ModelAndView mav) throws MobileException {
		String pageNum = request.getParameter("page");
		String trunType = request.getParameter("type");
		String search = request.getParameter("search");
		String showAccount = request.getParameter("accountId");
		User user = CurrentUser.get();
		Long accountId = null;
		if(Strings.isNotBlank(showAccount)){
			accountId = Long.parseLong(showAccount);
		}else{
			accountId = user.getLoginAccount();
		}
		V3xOrgAccount curAccount = null ;
		try {
			curAccount = orgManager.getAccountById(accountId);
		} catch (BusinessException e) {
			logger.error("",e);
		}
		MobileUtil mobileUtil = new MobileUtil();

		int page = mobileUtil.getCurrentPage(trunType, pageNum);

		List<OnlineUser> onLineMemberlist = onLineManager.getOnlineList(accountId);
		List<OnlineUser> currentList = ((onLineMemberlist != null) && (onLineMemberlist
				.size() != 0)) ? onLineMemberlist : new ArrayList<OnlineUser>();
		int numbers = currentList.size();
		Pagination p = new Pagination();
		p.setListOnlineUser(currentList);
		currentList = p.paginationOnLineUser(page,
				MobileConstants.PAGE_COUNTER, currentList);
		boolean isMulti = (Boolean)(SysFlag.frontPage_online_showAccountSwitch.getFlag());
		mav.addObject("isMulti", isMulti);
		
		mav.addObject("search", Strings.isNotBlank(search)?search:null);
		mav.addObject("pagenumber", page);// 当前的页码
		mav.addObject("pagecount", mobileUtil.getPageCount(numbers,MobileConstants.PAGE_COUNTER));// 总共的页数
		mav.addObject("num", numbers);// 总共的 在线人数
		mav.addObject("list", currentList);// 在线人员列表
		mav.addObject("currentId", MobileConstants.getCurrentId());// 登录用户的id
		mav.addObject("curAccount", curAccount);
		mav.addObject("totalMember", onLineManager.getOnlineNumber());

	}

	/**
	 * 得到我的关联人员类别
	 * 
	 * @param request
	 * @param mav
	 * @throws MobileException
	 */
	private void getRelativeMemberSearchList(HttpServletRequest request,
			ModelAndView mav) throws MobileException {
		String trunType = request.getParameter("type");
		String pageNum = request.getParameter("page");
		String search = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();

		Map<RelationType, List<Long>> map = oaManagerInterface
				.getRelativeMember(MobileConstants.getCurrentId());
		Collection<List<Long>> members = map.values();
		List<Long> relateList = new ArrayList<Long>();
		for (List<Long> l : members) {
			relateList.addAll(l);
		}
		Pagination p = new Pagination();
		p.setListlong(relateList);
		int size = relateList.size();
		int currentpage = mobileUtil.getCurrentPage(trunType, pageNum);

		List<Long> currentList = p.paginationLong(currentpage,MobileConstants.PAGE_COUNTER, relateList);

		mav.addObject("search", Strings.isNotBlank(search)?search:null);
		mav.addObject("members", currentList);// 当前页的人员列表
		mav.addObject("size", size);// 总共的人员个数
		mav.addObject("page", currentpage);// 当前的页码数
		mav.addObject("pageCount", mobileUtil.getPageCount(size,MobileConstants.PAGE_COUNTER));// 总共的页码数
	}

	/**
	 * 得到 我的部门人员列表
	 * 
	 * @param request
	 * @param mav
	 * @throws MobileException
	 */
	private void getMyDepartmentMemberSearchList(HttpServletRequest request,
			ModelAndView mav) throws MobileException {
		String trunType = request.getParameter("type");
		String pageNum = request.getParameter("page");
		String search = request.getParameter("search");

		MobileUtil mobileUtil = new MobileUtil();
		List<V3xOrgMember> memberList = oaManagerInterface
				.getMemberByDepartment(MobileConstants.getCurrentDepartmentId());

		memberList = (memberList != null) ? memberList: new ArrayList<V3xOrgMember>();

		Pagination p = new Pagination();
		p.setList(memberList);

		int size = memberList.size();
		int currentpage = mobileUtil.getCurrentPage(trunType, pageNum);

		List<V3xOrgMember> currentList = p.paginationMember(currentpage,MobileConstants.PAGE_COUNTER, memberList);
		List<Long> listLong = new ArrayList<Long>();
		for (V3xOrgMember member : currentList) {
			listLong.add(member.getId());
		}

		mav.addObject("search", Strings.isNotBlank(search)?search:null);
		mav.addObject("size", size);// 总共人员个数
		mav.addObject("members", listLong);// 当前页面的人员列表
		mav.addObject("page", currentpage);// 当前页面的页码
		mav.addObject("pageCount", mobileUtil.getPageCount(size,
				MobileConstants.PAGE_COUNTER));// 总共的页码数

	}

	/**
	 * 没有匹配到人
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView noMatchingPeople(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/selectPeople");
		String nid = request.getParameter("nid");
		String extendType = request.getParameter("type");//表单的扩展字段中选人和选部门的
		String cid = request.getParameter("cid");
		String fid = request.getParameter("fid");
		String from = request.getParameter("from");
		
		addSelectPeoplePageParames(mav, MobileConstants.getCurrentDepartmentId().toString());

		mav.addObject("noMatch", 1);
		mav.addObject("extendType", Strings.isNotBlank(extendType)?extendType:null);
		mav.addObject("isCanAtt", true);
		mav.addObject("isCanOpin", true);
		mav.addObject("from", from);
		mav.addObject("listSize",Strings.isNotBlank(extendType)?getSelectedPersonForm(cid,fid):getPersonNum(nid));//
		return mav;
	}

	/**
	 * 显示查询人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showSearchMember(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/search/showSearchMember");
		String keyword = request.getParameter("search");
		String page = request.getParameter("pageNum");
		String trunTyp = request.getParameter("prev");
		String totalNum = request.getParameter("num");// 总共的人数
		String from = request.getParameter("from");
		
		int num = 0;

		CharSequence c = new StringBuffer(keyword);
		
		MobileUtil mobileUtil = new MobileUtil();
		List<MobileOrgEntity> list = new ArrayList<MobileOrgEntity>();

		int pagenum = mobileUtil.getCurrentPage(trunTyp, page);
		
		if(Strings.isNotBlank(from)){
			List<MobileBookEntity> listPrivate = oaManagerInterface.getAllOutTeam(CurrentUser.get().getId());
			if(listPrivate!=null){
				for(MobileBookEntity  m : listPrivate){
					if(m!=null && m.getName().contains(c)){
						MobileOrgEntity org = new MobileOrgEntity();
						org.setId(m.getId());
						org.setName(m.getName());
						org.setTelNum(m.getMobileNum());
						org.setType(m.getType());
						list.add(org);
					}
				}
				num = list!=null?list.size():0;
			}
		}else{
			try {
				String str = java.net.URLEncoder.encode(keyword.trim(), "utf-8");

				mav.addObject("search", str.trim());

				if (CurrentUser.get().getLevelId() != -1) {
					num = oaManagerInterface.searchMember(MobileConstants
							.getCurrentAccountId(), keyword.trim(),
							MobileConstants.PAGE_COUNTER, pagenum, list);
				} else {
					num = oaManagerInterface.searchMember(MobileConstants
							.getCurrentDepartmentId(), keyword.trim(),
							MobileConstants.PAGE_COUNTER, pagenum, list);
				}
			} catch (UnsupportedEncodingException e) {
				logger.info("显示查找人员错误");
				throw new MobileException(e);
			}
		}
		mav.addObject("from", from);
		mav.addObject("entitys", Pagination.paginationObjectList(list, pagenum));
		mav.addObject("currentpage", pagenum);
		mav.addObject("size", mobileUtil.getPageCount(num,MobileConstants.PAGE_COUNTER));
		mav.addObject("number", totalNum != null ? totalNum : num);
		return mav;
	}

	/**
	 * 显示 角色中的memberList
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView showMemberList(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/coll/showMember");
		String nid = request.getParameter("id");
		String cid = request.getParameter("cid");
		String pageNum = request.getParameter("page");
		String trunType = request.getParameter("prev");
		String source = request.getParameter("source");
		
		MobileUtil mobileUtil = new MobileUtil();
		int currentPage = mobileUtil.getCurrentPage(trunType, pageNum);

		Long longCid = Long.parseLong(Strings.isNotBlank(cid) ? cid: "0");
		
		if(source!=null&&source.length()!=0&&Integer.parseInt(source)==0){
			ModelAndView modelAndView = validateAffair(request,response,longCid);
			if(modelAndView!=null){
				return modelAndView;
			}
		}
		Map<String, Object> map = null;
		
		Map<String,String> mapParam = oaManagerInterface.flowChartParam(longCid);
		
		Long summaryId = Long.valueOf(mapParam.get("summaryId"));
		Long caseId = Long.valueOf(mapParam.get("caseId"));
		String processId = mapParam.get("processId");
		
		try{
			map = oaManagerInterface.getNodes(summaryId,caseId,processId,false);
			
		}catch(MobileException e){
			return rendJavaScriptMobileMsg(response,"",e.getMessage());
		}
		List<Object[]> currentList = null;
		int size = 0;
		if (map != null) {
			Map<String, List<Object[]>> caseWorkItemLog = (Map<String, List<Object[]>>) map.get("caseWorkItemLog");
			if (caseWorkItemLog != null) {
				List<Object[]> list = caseWorkItemLog.get(nid != null ? nid : "");
				size = list != null ? list.size() : 0;
				currentList = Pagination.paginationObjectList(list,currentPage);
			}
		}

		mav.addObject("page", currentPage);
		mav.addObject("members", currentList);
		mav.addObject("size", mobileUtil.getPageCount(size,MobileConstants.PAGE_COUNTER));
		mav.addObject("total", size);
		return mav;
	}

	/**
	 * 处理会议
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView processMett(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {

		String id = request.getParameter("id");
		String sign = request.getParameter("sign");
		String content = request.getParameter("content");
		String from = request.getParameter("from");
		String proxyId = request.getParameter("proxyId");
		
		Long processId = Strings.isNotBlank(proxyId)?Long.parseLong(proxyId):MobileConstants.getCurrentId();
		Long mid = Long.parseLong(Strings.isNotBlank(id)? id : "0");
		MeetingDetial meetObject = mid!=0?oaManagerInterface.getMeetingDetial(mid,MobileConstants.getCurrentId()):null;
		if(meetObject!=null){
			int attitude = sign.equals("attend") ? 1 : sign.equals("unattend") ? 0 : sign.equals("pending") ? -1 : 10;
			oaManagerInterface.processMeeting(mid,processId,attitude, content,Strings.isNotBlank(proxyId));
		}else{
			return rendJavaScriptMobileMsg(response,"","meeting.delete.label");
		}
		if(Strings.isNotBlank(from)){
			if(from.equals("mt")){
				
				return meetListEntrance(request, response);
			}else if(from.equals("pc")){
				
				return pendingAffairListEntrance(request, response);
			}
		}
		return meetListEntrance(request, response);
	}

	/**
	 * 得到会议 与会人的处理细节
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView getMeetAttenderDetial(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/meeting/condition");
		String id = request.getParameter("id");
		String currentPageNum = request.getParameter("page");
		String type = request.getParameter("type");
		
		MobileUtil mobileUtil = new MobileUtil();
		
		int currentPageNumInt = mobileUtil.getCurrentPage(type, currentPageNum);
		Long mid = Long.parseLong(Strings.isNotBlank(id) ? id : "0");
		MeetingDetial md = oaManagerInterface.getMeetingDetial(mid,MobileConstants.getCurrentId());
		List<Object[]> attends = md.getAttenders();
		int allPageNum = mobileUtil.getPageCount(md.getAttenders()!=null?md.getAttenders().size():0, MobileConstants.PAGE_COUNTER);
		mav.addObject("attends", md != null ? Pagination.paginationObjectList(attends,currentPageNumInt) : null);
		mav.addObject("conditions", md != null ? md.getCondition() : null);
		mav.addObject("masterId", md != null ? md.getMasterId() : null);
		mav.addObject("recordId", md != null ? md.getRecordId() : null);
		mav.addObject("currentPage", currentPageNumInt);
		mav.addObject("allPageNum", allPageNum);
		mav.addObject("affairTotal", attends!=null?attends.size():0);
		return mav;
	}

	/**
	 * 查看 新闻 附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView lookNewsAttachment(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/news/attachment");
		String id = request.getParameter("id");

		Long nId = Long.parseLong(Strings.isNotBlank(id)? id : "0");

		News newsDetial = oaManagerInterface.getNewsDetial(nId, MobileConstants.getCurrentId());
		List<Attachment> listAtt = newsDetial.getAttachments();

		int size = listAtt != null ? listAtt.size() : 0;
		Map<String, Boolean> isLink = getLinkMethod(listAtt);

		addSomeToMav(mav, request, size,listAtt);

		mav.addObject("isLink", isLink);
		mav.addObject("id", id);

		return mav;
	}

	/**
	 * 查看 公告附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView lookBulletionAttachment(HttpServletRequest request,
			HttpServletResponse response) throws MobileException {
		ModelAndView mav = new ModelAndView("mobile/bulletin/attachment");
		String id = request.getParameter("id");

		Long nId = Long.parseLong(Strings.isNotBlank(id) ? id : "0");
		BulData bul = oaManagerInterface.getBulletionDetial(nId,MobileConstants.getCurrentId());
		Bulletion bullDetial = oaManagerInterface.getBulletionDetial(bul);
		List<Attachment> listAtt = bullDetial.getAttachmentList();
		
		Map<String, Boolean> isLink = getLinkMethod(listAtt);
		
		Object[] attJoin = splitFile(listAtt);
		List<Attachment> attList = null;
		String type = request.getParameter("type");
		if(attJoin != null){
			if("0".equals(type)){
				attList = (ArrayList<Attachment>)attJoin[0];
			}else{
				attList = (ArrayList<Attachment>)attJoin[1];
			}
		}
		//addSomeToMav(mav, request, size,listAtt);
		mav.addObject("listAtt", attList);
		mav.addObject("isLink", isLink);
		mav.addObject("id", id);
		return mav;
	}
	
	/**
	 * 查看 日程附件
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException
	 */
	public ModelAndView lookCalendaAttachment(HttpServletRequest request,
			HttpServletResponse response)throws MobileException{
		ModelAndView mav = new ModelAndView("mobile/calendar/attachment");
		String id = request.getParameter("id");
		Long cid = Long.parseLong(Strings.isNotBlank(id)?id:"0");
		Calendar calendar = oaManagerInterface.getCalendarDetial(cid, MobileConstants.getCurrentId());
		List<Attachment> list = calendar.getAttachments();
		
		Map<String, Boolean> isLink = getLinkMethod(list);
		
		int size = list!=null?list.size():0;
		
		addSomeToMav(mav,request,size,list);
		
		if(calendar!=null){
			
			mav.addObject("isLink", isLink);
			mav.addObject("id", id);
		}
		return mav;
	}

	private void addSomeToMav(ModelAndView mav, HttpServletRequest request,int size,List<Attachment> list) {
		Pagination p = new Pagination();
		p.setListAttachment(list);
		
		String trunType = request.getParameter("prev");
		String pageNum = request.getParameter("pageNum");
		MobileUtil mobileUtil = new MobileUtil();
		int currentPage = mobileUtil.getCurrentPage(trunType, pageNum);

		mav.addObject("num", size);
		mav.addObject("pagecurrent", currentPage);
		mav.addObject("pagecount", mobileUtil.getPageCount(size,MobileConstants.PAGE_COUNTER));
		mav.addObject("listAtt", (p.paginationAttachments(currentPage, MobileConstants.PAGE_COUNTER, new ArrayList<Attachment>())));
	}

	/**
	 * 
	 * @param attachments
	 * @return
	 */
	private Map<String, Boolean> getLinkMethod(List<Attachment> attachments) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		for (Attachment att : attachments) {
			if (att != null) {
				map.put(att.getId().toString(), MobileConstants.validateSuffix(att));
			}
		}
		return map;
	}
	
	/**
	 * 设置 Form 中的项内容
	 *@param cid 为是相当 id
	 */
	private void setFormItem(String cid,Long summaryId)throws MobileException{
		this.setFormDetial(getFormDetial(oaManagerInterface.getFormList(Long.parseLong(cid), false), true,summaryId.toString()));
		this.setFormDeal(getFormDetial(oaManagerInterface.getFormList(Long.parseLong(cid),false), false,summaryId.toString()));
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	/**
	 * 验证 该事项是不是被进行过 特殊处理
	 * @param request
	 * @param response
	 * @param cid
	 * @return
	 * @throws MobileException
	 */
	private ModelAndView validateAffair(HttpServletRequest request,
			HttpServletResponse response,Long cid)throws MobileException{

		Affair affair = affairManager.getById(cid);
		
		if(affair == null || affair.getState()!=StateEnum.col_pending.key()){
			String message = ColHelper.getErrorMsgByAffair(affair);
			return this.mobilePrompt(message, "/mob.do?method=showAffairs", new HashMap<String,String>());
		}else{
			return null;
		}
	}

	/**
	 * 设置 表单中的选人的扩展控件
	 * @param cid  事项的id
	 * @param id   表单项的id
	 * @param selectedId 所选的人和部门
	 */
	private void setSelectPersonForm(String cid,String id,String[] selectedId){
		String seId = null;
		if(selectedId!=null&&selectedId.length==1){
			String str = selectedId[0];
			seId = str.split("&")[0];
			
			V3xOrgMember member = oaManagerInterface.getMemberById(seId!=null?Long.parseLong(seId):null);
			V3xOrgDepartment department = oaManagerInterface.getDepartment(seId!=null?Long.parseLong(seId):null);
			String memberName = member!=null?member.getName():department!=null?department.getName():"";
			if(cid!=null){
				Long currnentUserId = CurrentUser.get().getId();
				Map<Long,Map<String,TIP_InputValueAll>> map = this.getFormPerson();
				if(map!=null&&!map.isEmpty()){
					Map<String,TIP_InputValueAll> mapObj =map.get(currnentUserId);
					if(mapObj!=null&&!mapObj.isEmpty()){
						TIP_InputValueAll obj = mapObj.get(id);
						if(obj!=null){
							obj.setValue((seId!=null&&seId.length()!=0)?seId:"");
							obj.setDisplayValue(memberName);
							//mapObj.put(id, obj);
						}else{
							obj = getFormItemObj(cid,id);
							obj.setValue((seId!=null&&seId.length()!=0)?seId:"");
							obj.setDisplayValue(memberName);
							//mapObj.put(obj.getName(), obj);
						}
						
					}else{
						mapObj = new HashMap<String,TIP_InputValueAll>();
						Map<String, TIP_InputValueAll> formMap = this.getFormDeal().get(getSummaryId(cid));
						TIP_InputValueAll obj = formMap.get(id)!=null?formMap.get(id):getFormItemObj(cid,id);
						if(obj!=null){
							obj.setValue(seId);
							obj.setDisplayValue(memberName);
							//mapObj.put(obj.getName(),obj);
						}
					}
				}else{
					Map<Long,Map<String,TIP_InputValueAll>> mapNew = new HashMap<Long,Map<String,TIP_InputValueAll>>();
					Map<String,TIP_InputValueAll> mapObj = new HashMap<String,TIP_InputValueAll>();
					Map<String, TIP_InputValueAll> formMap = this.getFormDeal().get(getSummaryId(cid));
					TIP_InputValueAll obj = formMap.get(id)!=null?formMap.get(id):getFormItemObj(cid,id);
					if(obj!=null){
						obj.setValue(seId);
						obj.setDisplayValue(memberName);
						mapObj.put(obj.getName(), obj);
					}
					mapNew.put(currnentUserId, mapObj);
					Map<Long,Map<String,TIP_InputValueAll>> formPerson = this.getFormPerson();
					if(formPerson!=null){
						formPerson.putAll(mapNew);
					}else{
						this.setFormPerson(mapNew);
					}
				}
			}
		}
		
	}
	
	/**
	 * 得到表单选项中 选人扩展控件的所选人数
	 * @param cid
	 * @param id
	 * @return
	 */
	private Integer getSelectedPersonForm(String cid,String key){
		
		Map<String, TIP_InputValueAll> formMap = cid!=null?this.getFormDeal().get(getSummaryId(cid)):null;
		TIP_InputValueAll obj = key!=null?formMap.get(key):null;
		String value = obj!=null?obj.getValue():null;
		return (value!=null&&value.length()!=0)?1:0;
	}
	
	/**
	 * 根据 事项的id,TIP_InputValueAll对象的id来判断该对象是否被赋值Value
	 * @param cid
	 * @param id
	 * @return
	 */
	private Integer getSelectedPersonFormById(String cid,String id){
		TIP_InputValueAll obj = getFormItemObj(cid,id);
		String value = obj!=null?obj.getValue():null;
		return Strings.isNotBlank(value)?1:0;
	}
	
	/**
	 * 得到 表单项对象
	 * @param cid
	 * @param id
	 * @return
	 */
	private TIP_InputValueAll getFormItemObj(String cid,String id){
		Map<String, TIP_InputValueAll> formMap = cid!=null?this.getFormDeal().get(getSummaryId(cid)):null;
		Set<Entry<String,TIP_InputValueAll>> formLoop = formMap!=null?formMap.entrySet():null;
		if(formLoop!=null){
			for(Entry<String,TIP_InputValueAll> entry : formLoop){
				TIP_InputValueAll obj = entry.getValue();
				if(obj.getId().equals(id)){
					return obj;
				}else{
					continue;
				}
			}
			return null;
		}
		return null;
	}
	/**
	 * 得到部门中人员的个数
	 * @param list
	 * @return
	 * @throws MobileException
	 */
	private Map<Long,Integer> getDempartmentMap(List<V3xOrgDepartment> list) throws MobileException{
		Map<Long,Integer> map = new HashMap<Long,Integer>();
		if(list!=null){
			for(V3xOrgDepartment department : list){
				map.put(department.getId(), getMemberList(department.getId().toString()).size());
			}
			return map;
		}else{
			return null;
		}
	}
	/**
	 * 删除 数据
	 *
	 */
	private void deleteAllPerson(){
		Long userId = CurrentUser.get().getId();
		if (newCollMap != null)
			newCollMap.remove(userId);
		if (dealCollMap != null)
			dealCollMap.remove(userId);
		if(formPerson!=null){
			Map<String,TIP_InputValueAll> map = formPerson.get(userId);
			Set<String> keys = map!=null?map.keySet():null;
			if(keys!=null){
				for(String str : keys){
					TIP_InputValueAll obj = map.get(str);
					if(obj!=null)
					obj.setValue(null);
				}
			}
			formPerson.remove(userId);
		}
		if(attsMap != null){
			attsMap.remove(userId);
		}
		List<String> lockSummary = userLock.get(userId);
		if(lockSummary != null && !lockSummary.isEmpty()){
			for(String summaryId : lockSummary){
				try {
					oaManagerInterface.removeModifyMember(summaryId);
				} catch (MobileException e) {
					logger.error(e);
				}
			}
			userLock.remove(userId);
		}
		Object o = WebUtil.getObject();
		if(o != null && !(o instanceof List)){
			WebUtil.saveObject(o);
		}
	}
	/**
	 * 判断 某一个 表单是否具有编辑字段
	 * @param cid
	 * @return
	 */
   private Boolean getFormCalculateXml(String cid){
		
		Map<String,Map<String,TIP_InputValueAll>> formDeal = this.getFormDeal();
		Map<String,TIP_InputValueAll> deal = formDeal!=null?formDeal.get(getSummaryId(cid)):null;
		if(deal!=null&&!deal.isEmpty()){
			Collection<TIP_InputValueAll> co = deal.values();
			for(TIP_InputValueAll tip : co){
				if(tip!=null&&tip.getStageCalculateXml()!=null&&tip.getAccess().equals("edit")){
					return true;
				}else{
					continue;
				}
			}
		}else{
			return false;
		}
		return false;
	}
	
	/**
	 * 判断是否需要选人
	 * @param list
	 * @return
	 */
	private boolean isNeedSelectPeople(List<ProcessModeSelector> list){
		if(list!=null&&list.size()!=0){
			for(ProcessModeSelector selector : list){
				if(selector.getType().equals("competition")){//竞争执行
					if(selector.getmemberNumuber()>=1){//有人，则不需要选择人
						continue;
					}else{//无人，则需要选择人
						return true;
					}
				}else if(selector.getType().equals("single")){//单人执行
					if(selector.getmemberNumuber()==1){//有1个人，则不需要选人
						continue;
					}else{//有多余1个人或无人，则需要选择人
						return true;
					}
				}else if(selector.getType().equals("multiple")){//多人执行
					if(selector.getmemberNumuber()==1){//有1个人，则不需要选人
						continue;
					}else{//有多余1个人或无人，则需要选择人
						return true;
					}
				}else if(selector.getType().equals("all")){//全体执行
					if(selector.getmemberNumuber()>=1){//有人，则不需要选择人
						continue;
					}else{//无人，则需要选择人
						return true;
					}
				}else{//其他情况，则继续
					continue;
				}
			}
		}
		return false;
	}
	private ColOpinion getOpinion(Long key,List<ColOpinion> list){
		if(key!=null){
			for(ColOpinion opinion : list){
				if(opinion.getId().equals(key))
					return opinion;
				else
					continue;
			}
		}
		return null;
	}
	/**
	 * 对协同意见进行 排序
	 * @param list
	 * @return
	 * 
	 */
	private List<ColOpinion> getSortedOpinion(List<ColOpinion> list){
		if(list!=null){
			class SortObj{
				private List<ColOpinion> listOpinion = new ArrayList<ColOpinion>();
				
				public List<ColOpinion> getListOpinion() {
					return listOpinion;
				}

				public void  sortColOpinion(List<ColOpinion> list){
					if(list.size()!=0){
						List<ColOpinion> newList = new ArrayList<ColOpinion>();
						newList.addAll(list);
						ColOpinion op = newList.get(0);
						for(ColOpinion opinion : newList){
							if(op.getCreateDate().getTime()>opinion.getCreateDate().getTime()){
								op = opinion;
							}else{
								continue;
							}
						}
						listOpinion.add(op);
						list.remove(op);
						sortColOpinion(list);
					}
				}
			}
			SortObj obj = new SortObj();
			obj.sortColOpinion(list);
			return obj.getListOpinion();
		}
		return null;
	}
	private List<String> isContain(String id){
		NewCollClass nc = newCollMap.get(MobileConstants.getCurrentId());
		List<String[]> list =nc!=null?nc.getAllPersonList():null;
		String[] idArray = Strings.isNotBlank(id)?id.split("&"):null;
		if(list!=null&&idArray!=null){
			return isConatain(list,idArray);
		}else{
			return null;
		}
	}
	private List<String> isConatain(List<String[]> list,String[] id){
		List<String> listName = new ArrayList<String>();
		String rolName="";
		String s = "";
		for(String[] str : list){
			if(id[1].equals("Member")){
				rolName =  oaManagerInterface.getMemberById(Long.parseLong(id[0])).getName();
				if("Department".equals(str[1])){
					List<V3xOrgMember> memberList = oaManagerInterface.getMemberByDepartment(Long.parseLong(str[0]));
					if(memberList!=null&&!memberList.isEmpty()){
						for(V3xOrgMember member : memberList){
							if(member.getId().toString().equals(id[0])){
								s = oaManagerInterface.getDepartment(Long.parseLong(str[0])).getName();
								listName.add(MobileConstants.getValueFromMobileRes("coll.prompt", s,rolName));
							}
						}
					}
				}else if("Account".equals(str[1])){
					Set<Long> memberIdsSet = Functions.getAllMembersId("Account",Long.parseLong(str[0]));
					for (Long long1 : memberIdsSet) {
						if(long1.toString().equals(id[0])){
							s = Functions.getAccount(Long.parseLong(str[0])).getName();
							listName.add(MobileConstants.getValueFromMobileRes("coll.prompt", s,rolName));
						}
					}
				}
			}else if(id[1].equals("Department")){
				rolName = oaManagerInterface.getDepartment(Long.parseLong(id[0])).getName();
				List<V3xOrgMember> memberList = oaManagerInterface.getMemberByDepartment(Long.parseLong(id[0]));
				if(str[1].equals("Member")){
					for(V3xOrgMember member : memberList){
						if(member.getId().toString().equals(str[0])){
							s = member.getName();
							listName.add(MobileConstants.getValueFromMobileRes("coll.prompt", rolName,s));
						}
					}
				}else if(str[1].equals("Department")){
					V3xOrgDepartment dep = oaManagerInterface.getDepartment(Long.parseLong(str[0]));
					if(dep != null){
						s = dep.getName();
						List<V3xOrgDepartment> departments = oaManagerInterface.getDepartmentByAccount(Long.parseLong(id[0]), false);
						for (V3xOrgDepartment department : departments) {
							if(department.getId().toString().equals(str[0])){
								listName.add(MobileConstants.getValueFromMobileRes("coll.prompt",s, rolName));
							}
						}
					}
				}
			}else if(id[1].equals("Account")){
				rolName = Functions.getAccount(Long.parseLong(id[0])).getName();
				Set<Long> memberIdsSet = Functions.getAllMembersId("Account",Long.parseLong(id[0]));
				if(str[1].equals("Member")){
					for (Long long1 : memberIdsSet) {
						if(long1.toString().equals(str[0])){
							V3xOrgMember member = Functions.getMember(long1);
							if(member != null){
								s = member.getName();
								listName.add(MobileConstants.getValueFromMobileRes("coll.prompt",rolName, s));
							}
						}
					}
				}else if("Department".equals(str[1])){
					List<V3xOrgDepartment> departments = oaManagerInterface.getDepartmentByAccount(Long.parseLong(id[0]), false);
					for (V3xOrgDepartment department : departments) {
						if(department.getId().toString().equals(str[0])){
							s = department.getName();
							listName.add(MobileConstants.getValueFromMobileRes("coll.prompt",rolName, s));
						}
					}
				}
				
			}
			
		}
		return listName;
	}
	
	private String getSummaryId(String cid){
		
		Long affairObjId = Long.parseLong(Strings.isNotBlank(cid)?cid:"0");
		Affair affair  = affairManager.getById(affairObjId);
		Long summaryId = affair!=null?affair.getObjectId():null;
		return summaryId!=null?summaryId.toString():null;
	}
	private Integer getMeettingAttitudeOfCurrentUser(List<Object[]> list){
		if(list!=null){
			for(Object[] obj : list){
				if(obj[0].equals(CurrentUser.get().getId())){
					return (Integer)obj[1];
				}else{
					continue;
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * 上传附件
	 * @param request
	 * @param response
	 */
	public ModelAndView processUpLoad(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView("mobile/coll/newColl");
		String id = request.getParameter("id");
		List<Attachment> ordinarl = new ArrayList<Attachment>();
		if(Strings.isNotBlank(id)){
			Collaboration coll = null;
			try {
				coll = oaManagerInterface.CollaborationDetial(Long.parseLong(id), CurrentUser.get().getId());
				
				modelAndView =  new ModelAndView("mobile/coll/waitSendDetial");
				if(coll!=null){
					Object[] atts = oaManagerInterface.getColAttachment(coll.getSummaryId());
					ordinarl.addAll((List<Attachment>)atts[0]);
					//modelAndView.addObject("atts", (atts!=null&&atts.length!=0)?:null);
					modelAndView.addObject("collaboration", coll);
				}
			} catch (Exception e) {
				logger.error("", e);
			} 
		}
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Iterator fileNames = multipartRequest.getFileNames();
		if(fileNames!=null){
			while(fileNames.hasNext()){
				Object name = fileNames.next();
				MultipartFile fileItem = multipartRequest.getFile(String.valueOf(name));
				if(fileItem==null||(fileItem!=null && fileItem.getSize()==0)){
					return new ModelAndView("mobile/coll/upfile");
				}
			}
		}
		
		com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE type = com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE;
		
		Map<String, V3XFile> v3xFiles = new HashMap<String, V3XFile>();
		try {
			 v3xFiles = fileManager.uploadFiles(request, null, Long.valueOf(2*1024*1024));
		} catch (BusinessException e) {
			rendJavaScriptMobileKey(response,"coll.uplaod.maxsize",e.getErrorArgs()[0].toString());
		}
		
		List<Attachment> list = attsMap.get(MobileConstants.getCurrentId());
		if(list==null){
			attsMap.put(MobileConstants.getCurrentId(), new ArrayList<Attachment>());
		}
		
		String key="";
		if(v3xFiles != null) { 
			Iterator<String> keys = v3xFiles.keySet().iterator();
			while(keys.hasNext()) {
				key = keys.next();
				
				attsMap.get(MobileConstants.getCurrentId()).add(new Attachment(v3xFiles.get(key),ApplicationCategoryEnum.collaboration, type));
			}
		}
		
		NewCollClass newcolObj = !newCollMap.isEmpty()?newCollMap.get(MobileConstants.getCurrentId()):null;
		List<Attachment> lA = attsMap.get(MobileConstants.getCurrentId());
		List<Attachment> ll = new ArrayList<Attachment>();
		ll.addAll(lA);
		ll.addAll(ordinarl);
		modelAndView.addObject("atts", ll);
		
		modelAndView.addObject("title", newcolObj!=null?newcolObj.getTitle():"");
		modelAndView.addObject("content", newcolObj!=null?newcolObj.getContent():"");
		return modelAndView;
	}
	
	/**
	 * 跳转到公文的待处理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showPendingEdocList(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/edoc/pendingEdocList");
		String currentPage = request.getParameter("currentPage");//当前页码
		String pgeType = request.getParameter("pageType");//翻页类型
		String keyWord = request.getParameter("search");//页面上查询提交的关键字
		String totlalPageNum = request.getParameter("totalPageNum");
		String totalAffairNum = request.getParameter("totalAffairNum");
		
		MobileUtil util = new MobileUtil();
		
		int pageNum = util.getCurrentPage(pgeType, currentPage);
		Long uid = CurrentUser.get().getId();
		List<AffairsListObject> eodcAffairs = new ArrayList<AffairsListObject>();
		
		int totalNum = oaManagerInterface.getPendingEdocList(uid, MobileConstants.PAGE_COUNTER, pageNum, eodcAffairs, Strings.isNotBlank(keyWord)?keyWord:"");
		int doneEdocNum = oaManagerInterface.getDoneOfEdocs(uid, ApplicationCategoryEnum.edoc, StateEnum.col_done.key());
		
		mav.addObject("totalPageNum", (Strings.isNotBlank(totlalPageNum))?Integer.parseInt(totlalPageNum):util.getPageCount(totalNum, MobileConstants.PAGE_COUNTER));
		mav.addObject("currentPage", pageNum);
		mav.addObject("affairs", Pagination.paginationObjectList(eodcAffairs, pageNum));
		mav.addObject("totalAffairNum", totalNum!=-1?totalNum:totalAffairNum);
		mav.addObject("doneEdocs", doneEdocNum);
		mav.addObject("search", Strings.isNotBlank(keyWord)?keyWord:null);
		return mav;
	}
	public ModelAndView showAttEdoc(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav  = showEdocObj(request,response);
		mav.setViewName("mobile/edoc/edocAtt");
		return mav	;
	}
	/**
	 * 跳转到公文的细节页面
	 * @param request
	 * @param response
	 * @return
	 * @throws MobileException 
	 * @throws NumberFormatException 
	 */
	public ModelAndView showEdocObj(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("mobile/edoc/pendingEdocDetial");
		String affairId = request.getParameter("id");//得到事项的id
		String formType = request.getParameter("fromType");// 0-待办 1-已办
		String isAtt = request.getParameter("parentId");
		MobileUtil util = new MobileUtil();
		User user = CurrentUser.get();
		
		if(Strings.isNotBlank(affairId)){
			ModelAndView modelAndView = validateAffair(request,response,Long.parseLong(affairId));
			if(modelAndView!=null && "0".equals(formType) && Strings.isBlank(isAtt)){
				return modelAndView;
			}
			Affair affair = affairManager.getById(Long.parseLong(affairId));
			
			Edoc e = oaManagerInterface.getPendingEdocObj(affair);
			
			if(e!=null && e.getState()==5){
				return mobilePrompt(MobileConstants.getValueFromMobileRes("edoc.cancel"), "/mob.do?method=showAffairs", null);
			}else{
				if(Strings.isNotBlank(formType) && Integer.valueOf(formType)==1 && e.isHasArchive()){
					return mobilePrompt(MobileConstants.getValueFromMobileRes("edoc.pigeonhole"), "/mob.do?method=showAffairs", null);
				}
			}
			mav.addObject("edoc", e);
			if(util.isNumber(e.getSecretType())){
				mav.addObject("secretType", true);
			}
			if(util.isNumber(e.getEmergentType())){
				mav.addObject("emergentType", true);
			}
			if(e.getAppType()==ApplicationCategoryEnum.edocRegister.key() || e.getAppType()==ApplicationCategoryEnum.exSend.key()||e.getAppType()==ApplicationCategoryEnum.exSign.key()){//如果当前用户既是单位/部门收发员，该公文又处于待发送状态
				mav.addObject("isCanProcess",false);
			}else{
				if(affair.getState().equals(StateEnum.col_pending.getKey())){
					mav.addObject("isCanProcess",true);
					Map<String,Object> map = oaManagerInterface.getEdocPolicyName(Long.valueOf(affairId),affair.getObjectId());
					String content = oaManagerInterface.getZCDBOpinion(affair.getObjectId());
					mav.addObject("policyMap", map);
					mav.addObject("zcdbContent", content);
					
					//判断是否是封发节点，这里不支持封发
					String nodePermissionPolicy = (String)map.get("nodePermissionPolicy");
					if("fengfa".equals(nodePermissionPolicy)){
						mav.addObject("isCanProcess",false);
						mav.addObject("fengfa",true);
					}
				}
			}
			EdocBody edocBody = e.getEdocBody();
			mav.addObject("edocBody", edocBody);
			if(e.getEdocBody() != null){
				try {
					String content = StrExtractor.getText(edocBody.getContentType(), edocBody.getContent(), edocBody.getCreateTime());
					mav.addObject("content", content);
				} catch (Exception exception) {
					logger.error("解析正文", exception);
				}
			}
			
			List<EdocItem> listEdoc = oaManagerInterface.getEdocOpinion(e.getEdocId(),affair.getId());
			mav.addObject("itemLength", listEdoc != null?listEdoc.size():0);
			
			List<Attachment> atts = oaManagerInterface.getEdocAtts(e.getEdocId());
			Object[] attJoin = splitFile(atts);
			if(attJoin != null){
				mav.addObject("fileAtt", attJoin[0]);
				mav.addObject("docAtt", attJoin[1]);
			}
		}
		return mav;
	}
	
	/**
	 * 跳转到公文的正文页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showPendingEdocObjContent(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/edoc/edocContent");
		String edocId = request.getParameter("edocid");
		String edocContent = oaManagerInterface.getPendingEdocObjContent(CurrentUser.get().getId(), Strings.isNotBlank(edocId)?Long.parseLong(edocId):0L);
		mav.addObject("content", edocContent);
		
		return mav;
	}
	
	/**
	 * 跳转到公文的文单列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showEdocItemList(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/edoc/items");
		String edocId = request.getParameter("edocid");
		String affairId = request.getParameter("id");
		//String pageNum = request.getParameter("pageNum");
		//String type = request.getParameter("type");
		Map<String,Object> mapEdoc = oaManagerInterface.getPendingEodcItemList(CurrentUser.get().getId(), Strings.isNotBlank(edocId)?Long.parseLong(edocId):0L);
		
		List<EdocElement> listEdoc = (List<EdocElement>) mapEdoc.get("list");
		//Map<Long,Map> mapMetadata = (Map<Long, m>) mapEdoc.get("map");
		
		Map<Long,Map<String,String>> systemmap = (Map<Long, Map<String, String>>) ((Map<String,Object>) mapEdoc.get("map")).get("system");
		Map<Long,Map<String,String>> usermap = (Map<Long, Map<String, String>>) ((Map<String,Object>) mapEdoc.get("map")).get("user");
		
		EdocSummary summary = oaManagerInterface.getEodcSummaryById(Strings.isNotBlank(edocId)?Long.parseLong(edocId):0L);
		
		MobileUtil util = new MobileUtil();
		if(Strings.isNotBlank(affairId)){
			Affair affair = affairManager.getById(Long.parseLong(affairId));
			Edoc e = oaManagerInterface.getPendingEdocObj(affair);
			
			mav.addObject("edoc", e);
			if(util.isNumber(e.getSecretType())){
				mav.addObject("secretType", true);
			}
			if(util.isNumber(e.getEmergentType())){
				mav.addObject("emergentType", true);
			}
		}
		
		//int page = util.getCurrentPage(type, pageNum);
		//int totalPageNum = util.getPageCount(listEdoc!=null?listEdoc.size():0,MobileConstants.PAGE_COUNTER);
		mav.addObject("Items", listEdoc);
		mav.addObject("totalAffairNum", listEdoc!=null?listEdoc.size():0);
		//mav.addObject("currentPage",page);
		//mav.addObject("totalPageNum",totalPageNum);
		mav.addObject("edocId", edocId);
		mav.addObject("summary", summary);
		mav.addObject("systemmap", systemmap);
		mav.addObject("usermap", usermap);
		return mav;
	}
	
	/**
	 * 展现 公文 附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showEdocAtts(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/edoc/attachments");
		String edocId = request.getParameter("edocId");
		String pageNum = request.getParameter("pageNum");
		String type = request.getParameter("prev");
		String fileType = request.getParameter("attType");
		
		MobileUtil mutil = new MobileUtil();
		
		Long lId = Long.valueOf(Strings.isNotBlank(edocId)?edocId:"0");
		List<Attachment> display = new ArrayList<Attachment>();
		List<Attachment> atts = oaManagerInterface.getEdocAtts(lId);
		Object[] attJoin = splitFile(atts);
		if(attJoin != null){
			if("0".equals(fileType)){
				Map<String, Boolean> isLink = new HashMap<String, Boolean>();
				display = (List<Attachment>) attJoin[0];
				for (Attachment att : display) {
					isLink.put(att.getId().toString(), MobileConstants.validateSuffix(att));
				}
				mav.addObject("isLink", isLink);
			}else{
				display = (List<Attachment>) attJoin[1];
			}
		}
		mav.addObject("atts", display);
		mav.addObject("pagecurrent", mutil.getCurrentPage(type, pageNum));
		mav.addObject("pagecount", mutil.getPageCount(display!=null?display.size():0, MobileConstants.PAGE_COUNTER));
		mav.addObject("num", display!=null?display.size():0);
		return mav;
	}
	private Object[] splitFile(List<Attachment> atts){
		if(atts == null) return null;
		List<Attachment> fileAtt = new ArrayList<Attachment>();
		List<Attachment> docAtt = new ArrayList<Attachment>();
		for (Attachment attachment : atts) {
			if(attachment.getType() != Constants.ATTACHMENT_TYPE.DOCUMENT.ordinal()){
				fileAtt.add(attachment);
			}else if(attachment.getMimeType().equals(ApplicationCategoryEnum.collaboration.name()) || attachment.getMimeType().equals(ApplicationCategoryEnum.edoc.name())){
				docAtt.add(attachment);
			}
		}
		Object[] attJoin = new Object[]{fileAtt,docAtt};
		return attJoin;
	}
	
	/**
	 * 处理 移动公文
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView processEdoc(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/edoc/process");
		String affairId = request.getParameter("id");//affairID
		String summaryId = request.getParameter("sid");
		if(Strings.isNotBlank(affairId) && Strings.isNotBlank(summaryId)){
			Map<String,Object> map = oaManagerInterface.getEdocPolicyName(Long.valueOf(affairId),Long.valueOf(summaryId));
			String content = oaManagerInterface.getZCDBOpinion(Long.valueOf(summaryId));
			mav.addObject("policyMap", map);
			mav.addObject("content", content);
		}
		mav.addObject("isNextNodeUnsure", true);
		return mav;
	}
	/**
	 * 处理 移动公文 Action 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView processEdocAction(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		String affairId = request.getParameter("id");
		String submmit = request.getParameter("submit");
		String save = request.getParameter("tempSave");
		String attitude = request.getParameter("attitude");
		String opinion = request.getParameter("opinion");
		String mustWrite = request.getParameter("mustWrite");
		String track = request.getParameter("track");
		
		if(Strings.isNotBlank(mustWrite)){
			Integer m = Integer.valueOf(mustWrite);
			if(m==1 && Strings.isBlank(opinion)){
				return rendJavaScriptMobileKey(response,"edoc.opinioncontent","");
			}
		}
		ModelAndView modelAndView = validateAffair(request,response,Long.parseLong(affairId));
		if(modelAndView!=null){
			return modelAndView;
		}
		Long afId = Strings.isNotBlank(affairId)?Long.parseLong(affairId):0L;
		
		Integer att = getAttitude(attitude);
		
		Map<String,String> map = oaManagerInterface.flowChartParam(afId);

		Long summaryId = Long.valueOf(map.get("summaryId"));
		Long caseId = Long.valueOf(map.get("caseId"));
		String processId = map.get("processId");
		String button = submmit!=null?"submmit":save!=null?"save":"";
	
		DealCollClass dealObj = new DealCollClass(opinion,attitude,button,affairId,null,null,"true".equals(track));
		dealCollMap.put(CurrentUser.get().getId(), dealObj);
		
		Map<String,Object> edocMap = oaManagerInterface.getEdocBratch(summaryId, afId);
		Map<String,Object> res = null;
		try {
			res = oaManagerInterface.getProcessModeSelectorList(afId,MobileConstants.getCurrentId());
		} catch (Exception e) {
			Map<String,String> parameter = new HashMap<String,String>();
			return mobilePrompt(e.getCause().getMessage(), "/mob.do?method=showAffairs", parameter);
		}
		List<ProcessModeSelector> list = (List<ProcessModeSelector>) res.get("processModeSelector");//
		Map<String,Integer> conditionType = (Map<String,Integer>)res.get("conditionTypes");

		List<String> edocCondition = (List<String>) (edocMap != null ? edocMap.get("conditions"): null);
		List<String> edocKeys  = (List<String>) (edocMap != null ? edocMap.get("keys"): null);
		String isExecuteFinished= (String)res.get("isExecuteFinished");
		if ("true".equals(isExecuteFinished) &&(isNeedSelectPeople(list)|| (edocCondition != null && edocCondition.size() != 0))&& submmit != null) {// 判断 需要跳转到选人界面
			if (isNeedSelectPeople(list) && edocCondition == null) {
				ModelAndView mav = getProcessSelectedPage(list);
				mav.addObject("conditionTypes", conditionType);
				return mav;
			} else {
				if ((edocCondition != null && edocCondition.size() != 0)) {
					List<String> canKeys = new ArrayList<String>();
					for(int i=0;i<edocCondition.size();i++){
						if(Boolean.parseBoolean(edocCondition.get(i))){
							canKeys.add(edocKeys.get(i));
						}
					}
					Set<ProcessModeSelector> newList  = new HashSet<ProcessModeSelector>();
					for(String key : canKeys){
						for(ProcessModeSelector p : list){
							if(p.getId().equals(key)){
								newList.add(p);
								break;
							}
						}
					}
					if(conditionType != null ){
						for(ProcessModeSelector p : list){
							if( conditionType.get(p.getId())!= null && conditionType.get(p.getId()) != null &&  conditionType.get(p.getId()) ==2){
								newList.add(p);
							}
						}
					}
					List lists = new ArrayList(newList);
					ModelAndView mav = getProcessSelectedPageForm(edocMap, list, affairId);
					mav.addObject("conditionTypes", conditionType);
					return mav;
				} else {
					return null;
				}
			}
		}
		//try {
			Map<String, Object> flowChart1 = oaManagerInterface.getNodes(summaryId,caseId,processId,true);
			
			Nodes node = (Nodes) flowChart1.get("nodes");
			Object isLock =  flowChart1.get("isLock");
			
			if(isLock==null){
				Map<String, String[]> nodeMap = new HashMap<String, String[]>();

				String[] str = new String[1];
				str[0] = node.getNodename();
				nodeMap.put(node.getNid(), str);
				oaManagerInterface.processEdoc(submmit!=null?1:save!=null?2:0, afId, opinion, att,  nodeMap,  new HashMap<String, String>(),"true".equals(track));
			}
			
//		} catch (MobileException e) {
//			logger.error("",e);
//		}
		
		return super.redirectModelAndView("/mob.do?method=showAffairs");
	}
	
	/**
	 * 得到 已办公文列表
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showDoneEdocList(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/edoc/doneEdocList");
		String pageNum = request.getParameter("pageNumber");
		String type = request.getParameter("type");
		String keyWord = request.getParameter("search");
		MobileUtil mutil = new MobileUtil();
		int pageNumber = mutil.getCurrentPage(type, pageNum);
		
		List<AffairsListObject> list = new ArrayList<AffairsListObject>();
		int doneEdocNum = oaManagerInterface.getDoneEdocList(CurrentUser.get().getId(), MobileConstants.PAGE_COUNTER, pageNumber,list, keyWord);
		int totalPageNum = mutil.getPageCount(doneEdocNum, MobileConstants.PAGE_COUNTER);
		Collections.sort(list);
		mav.addObject("totalAffairNum",doneEdocNum);
		mav.addObject("doneList", Pagination.paginationObjectList(list, pageNumber));
		mav.addObject("currentPage",pageNumber);
		mav.addObject("totalPageNum",totalPageNum);
		mav.addObject("search",Strings.isNotBlank(keyWord)?keyWord:null);
		return mav;
	}
	/**
	 * 展现移动公文的意见
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showEdocOpinion(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/edoc/items");
		String edocId = request.getParameter("edocid");
		String pageNum = request.getParameter("pageNum");
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		Long edocid = Strings.isNotBlank(edocId)?Long.parseLong(edocId):0L;
		Long affarid = Strings.isNotBlank(id)?Long.parseLong(id):0L;
		List<EdocItem> listEdoc = oaManagerInterface.getEdocOpinion(edocid,affarid);
		Collections.sort(listEdoc);
		MobileUtil util = new MobileUtil();
		int page = util.getCurrentPage(type, pageNum);
		int totalPageNum = util.getPageCount(listEdoc!=null?listEdoc.size():0,MobileConstants.PAGE_COUNTER);
		mav.addObject("Items", Pagination.paginationObjectList(listEdoc,page));
		mav.addObject("totalAffairNum", listEdoc!=null?listEdoc.size():0);
		mav.addObject("currentPage",page);
		mav.addObject("totalPageNum",totalPageNum);
		mav.addObject("edocId", edocId);
		mav.addObject("opinion", true);
		return mav;
	}
	
	/**
	 * 展现 系统提醒消息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showMessage(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/online/onlineMessage");
		String showType = request.getParameter("showType");//系统 or 个人
		String type = request.getParameter("type");//翻页类型 上 or 下
		String pageNum = request.getParameter("pageNum");//当前的页码
		String totalMessageNum = request.getParameter("totalMsgs");
		String search = request.getParameter("search");
		MobileUtil util = new MobileUtil();
		User member = CurrentUser.get();
		Long internalId = member.getId();
		int currentPage = util.getCurrentPage(type, pageNum);
		List<MobileHistoryMessage> mobileMessageList = new ArrayList<MobileHistoryMessage>();
		int size = oaManagerInterface.getMobileMessageList(mobileMessageList,internalId, search, showType,currentPage,MobileConstants.PAGE_COUNTER);
		int totalPage = util.getPageCount(size, MobileConstants.PAGE_COUNTER);
		oaManagerInterface.setMessageReadedState(internalId, Integer.parseInt(showType));
		MessageState.getInstance().setNoMessageState(internalId);
		
		mav.addObject("messageList", mobileMessageList);
		mav.addObject("pageNum",currentPage);
		mav.addObject("totalPage",totalPage);
		mav.addObject("totalMessages", size!=-1?size:totalMessageNum);
		mav.addObject("title", showType);
		return mav;
	}
	
	//查看历史消息（只取在线消息和系统消息的前8条，不将消息设置为已读）
	public ModelAndView showHistoryMessage(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/online/historyMessage");
		User user = CurrentUser.get();
		List<MobileHistoryMessage> onlineMessage = new ArrayList<MobileHistoryMessage>();
		List<MobileHistoryMessage> systemMessage = new ArrayList<MobileHistoryMessage>();
		int onlineSize = oaManagerInterface.getMobileMessageList(onlineMessage,user.getId(), "", "1",1,MobileConstants.PAGE_COUNTER);
		int sysSize = oaManagerInterface.getMobileMessageList(systemMessage,user.getId(), "", "0",1,MobileConstants.PAGE_COUNTER);
		List[] result = calcMessage(onlineMessage,systemMessage);
		mav.addObject("onlineMessage", result[0]);
		mav.addObject("onlineSize", onlineSize);
		mav.addObject("sysSize", sysSize);
		mav.addObject("systemMessage", result[1]);
		return mav;
	}
	
	//平均分配在线消息和系统消息
	private List[] calcMessage(List<MobileHistoryMessage> onlineMessage,List<MobileHistoryMessage> systemMessage){
		int perCount = MobileConstants.PAGE_COUNTER/2;
		int onlineSize = onlineMessage.size();
		int sysSize = systemMessage.size();
		if(onlineSize >=perCount && sysSize >=perCount){
			onlineSize = perCount;
			sysSize = perCount;
		}else if(onlineSize >=perCount){
			onlineSize = MobileConstants.PAGE_COUNTER-sysSize;
		}else if(sysSize >=perCount){
			sysSize = MobileConstants.PAGE_COUNTER-onlineSize;
		}
		if(onlineSize >onlineMessage.size()){
			onlineSize = onlineMessage.size();
		}
		if(sysSize > systemMessage.size()){
			sysSize = systemMessage.size();
		}
		return new List[]{onlineMessage.subList(0, onlineSize),systemMessage.subList(0,sysSize)};
	}
	
	//用户在线消息
	public ModelAndView showNewMessage(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/online/newMessage");
		User user = CurrentUser.get();
		List<List<MobileHistoryMessage>> allMessage = oaManagerInterface.findUnReadMessage(user.getId());
		mav.addObject("onlineMessage", allMessage.get(0));
		mav.addObject("systemMessage", allMessage.get(1));
		return mav;
	}

	/**
	 * 展现个人的信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showPerson(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/online/person");
		String id = request.getParameter("id");
		String from = request.getParameter("from");
		if(Strings.isNotBlank(id)){
			if(Strings.isBlank(from)){
				Long lId = Long.valueOf(id);
				V3xOrgMember member = oaManagerInterface.getMemberById(lId);
				V3xOrgDepartment department = oaManagerInterface.getDepartment(member!=null?member.getOrgDepartmentId():0L);
				
				mav.addObject("departmentName", department!=null?department.getName():null);
				mav.addObject("postName", oaManagerInterface.getPostNameByMember(member));
				mav.addObject("emailAddress", member.getEmailAddress());
				try {
					mav.addObject("officeTel", member.getProperty("officeNum"));
				} catch (BusinessException e) {
					logger.error(e);
				}
				mav.addObject("mobileTel", member.getTelNumber());
				mav.addObject("member", member);
			}else{
				if("private".endsWith(from)){
					Long mId = Long.valueOf(id);
					MobileBookEntity mm = oaManagerInterface.getMobileBookMember(mId);
					
					mav.addObject("accountName", mm!=null?mm.getAccountName():"");
					mav.addObject("departmentName", mm!=null?mm.getDepartmentName():"");
					mav.addObject("postName", mm!=null?mm.getPost():null);
					mav.addObject("emailAddress", mm!=null?mm.getEmail():null);
					mav.addObject("officeTel",mm!=null?mm.getOfficeNum():null);
					mav.addObject("mobileTel", mm!=null?mm.getMobileNum():null);
					mav.addObject("familyTel", mm!=null?mm.getFamilyNum():null);
					mav.addObject("faxNum", mm!=null?mm.getFaxNumber():null);
					mav.addObject("familyAddress", mm!=null?mm.getFamilyAddress():null);
					mav.addObject("postCode", mm!=null?mm.getPostCode():null);
					mav.addObject("name", mm!=null?mm.getName():null);
					
				}
			}
			
		}
		return mav;
	}
	
	/**
	 * 删除待发事项中的附件
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView removeAttachment(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String attId = request.getParameter("attId");
		if(Strings.isNotBlank(attId)){
			oaManagerInterface.removeAttachmentById(Strings.isDigits(attId)?Long.parseLong(attId):0L);
		}
		
		return showWaitSendCollaborationDetial(request,response);
	}
	
	/**
	 * 删除新建协同上的附件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeAttachmentNew(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String attId = request.getParameter("attId");
		if(Strings.isNotBlank(attId)){
			int id = Integer.parseInt(attId);
			List<Attachment> list = attsMap.get(MobileConstants.getCurrentId());
			if(list!=null && !list.isEmpty()){
				list.remove(id);
			}
			attsMap.remove(MobileConstants.getCurrentId());
			attsMap.put(MobileConstants.getCurrentId(), list);
		}
		return showNewCollaborationDetial(request,response);
	}
	
	/**
	 * 展现 会议附件
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showMeetAttachments(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/meeting/attachments");
		String id = request.getParameter("id");
		String pageNum = request.getParameter("pageNum");
		String type = request.getParameter("prev");
		String fileType = request.getParameter("attType");
		if(Strings.isNotBlank(id)){
			Long mid = Long.parseLong(id);
			List<Attachment> allAtts = oaManagerInterface.getAttachment(mid);
			List<Attachment> atts = new ArrayList<Attachment>();
			Object[] files = splitFile(allAtts);
			if(files != null){
				if("0".equals(fileType)){
					atts = (List<Attachment>) files[0];
					Map<String,Boolean> isLink = new HashMap<String,Boolean>(); 
					for (Attachment att : atts) {
						if (att != null) {
							isLink.put(att.getId().toString(), MobileConstants.validateSuffix(att));
						}
					}
					mav.addObject("isLink", isLink);
				}else{
					atts = (List<Attachment>) files[1];
				}
			}
			MobileUtil mutil = new MobileUtil();
			mav.addObject("atts", atts);
			mav.addObject("pagecurrent", mutil.getCurrentPage(type, pageNum));
			mav.addObject("pagecount", mutil.getPageCount(atts!=null?atts.size():0, MobileConstants.PAGE_COUNTER));
			mav.addObject("num", atts!=null?atts.size():0);
		}
		return mav;
	}
	
	public ModelAndView menuSetting(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/menu/menuSetting");
		User user = CurrentUser.get();
		List<BaseMobileMenu> listMenu = mobileMenuManager.listMenuByUser(user.getId(),user.getLoginAccount());
		List<BaseMobileMenu> allMobileMenu = mobileMenuManager.listAllMenu();
		mav.addObject("menuList", listMenu);
		mav.addObject("allMenu", allMobileMenu);
		return mav;
	}

	public ModelAndView saveMenuSetting(HttpServletRequest request,HttpServletResponse response){
		User user = CurrentUser.get();
		String[] menuSetting = request.getParameterValues("menuSetting");
		int i = 1;
		List<MobileMenuSetting> userSetting = new ArrayList<MobileMenuSetting>();
		for(String menuId : menuSetting){
			MobileMenuSetting setting = new MobileMenuSetting();
			setting.setIdIfNew();
			setting.setMenuId(menuId);
			setting.setCreateDate(new Date());
			setting.setUserId(user.getId());
			setting.setSort(i);
			userSetting.add(setting);
			i++;
		}
		mobileMenuManager.saveOrUpdateMenuSetting(userSetting, user.getId());
		return super.redirectModelAndView("/mob.do?method=showAffairs");
	}
	
	public ModelAndView toDefaultMenu(HttpServletRequest request,HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		mobileMenuManager.deleteMenuSetting(user.getId());
		return super.redirectModelAndView("/mob.do?method=menuSetting");
	}
	
	public void setMobileMenuManager(MobileMenuManager mobileMenuManager) {
		this.mobileMenuManager = mobileMenuManager;
	}
	
	@NeedlessCheckLogin
	public ModelAndView login(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView("mobile/login");
		Locale currentLocale = LocaleContext.make4Frontpage(request);
		
		modelAndView.addObject("currentLocale", currentLocale);
		
		modelAndView.addObject("verifyCode", "enable".equals(this.systemConfig.get("verify_code")));
		
		return modelAndView;
	}
	
    @NeedlessCheckLogin
	public ModelAndView changeLocale(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Locale locale = LocaleContext.parseLocale(request.getParameter(LoginConstants.LOCALE));
		LocaleContext.setLocale(request, locale);
		Cookies.add(response, LoginConstants.LOCALE, locale.toString(), Cookies.COOKIE_EXPIRES_FOREVER);
		
		return this.login(request, response);
	}
    
    public ModelAndView onlineAccount(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("mobile/online/onlineAccount");
		boolean isMulti = (Boolean)(SysFlag.frontPage_online_showAccountSwitch.getFlag());
		User user = CurrentUser.get();
		if(isMulti){
			List<V3xOrgAccount> accountsList;
			try {
				accountsList = orgManager.accessableAccounts(user.getId());
				Map<String, Integer> onlineNumMap = new HashMap<String, Integer>();
				for(V3xOrgAccount account : accountsList){
					Long accountId = account.getId();
					int num = onLineManager.getOnlineNumber(accountId);
					onlineNumMap.put(accountId.toString(), num);
				}
				mav.addObject("accountsList", accountsList);
				mav.addObject("onlineNumMap", onlineNumMap);
				mav.addObject("totalMember", onLineManager.getOnlineNumber());
			} catch (BusinessException e) {
				logger.error("查询单位列表(在线人员)",e);
			}
		}
		
		return mav;
	}
}