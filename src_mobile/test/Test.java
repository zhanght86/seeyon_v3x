//package test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import junit.framework.TestCase;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import com.seeyon.v3x.collaboration.domain.ColSummary;
//import com.seeyon.v3x.collaboration.exception.ColException;
//import com.seeyon.v3x.collaboration.manager.ColManager;
//import com.seeyon.v3x.common.authenticate.domain.User;
//import com.seeyon.v3x.common.exceptions.BusinessException;
//import com.seeyon.v3x.common.web.login.CurrentUser;
//import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
//import com.seeyon.v3x.mobile.manager.OAManagerInterface;
//import com.seeyon.v3x.mobile.message.domain.MessageReciever;
//import com.seeyon.v3x.mobile.message.domain.MobileMessageObject;
//import com.seeyon.v3x.mobile.message.manager.MessageManagerInterface;
//import com.seeyon.v3x.mobile.webmodel.AffairsListObject;
//import com.seeyon.v3x.mobile.webmodel.Bulletion;
//import com.seeyon.v3x.mobile.webmodel.Collaboration;
//import com.seeyon.v3x.mobile.webmodel.MeetingDetial;
//import com.seeyon.v3x.organization.domain.V3xOrgMember;
//import com.seeyon.v3x.organization.domain.V3xOrgTeam;
//import com.seeyon.v3x.organization.manager.OrgManager;
//
//public class Test extends TestCase {
//	private static final Log log = LogFactory.getLog(Test.class);
//	
//	String paths[] = { "test/cfg.xml", "hibernate.cfg.xml", "task-context.xml", "filemanager-context.xml",
//			"metadata-context.xml", "message-context.xml", "search-manager.xml", "meeting.xml", "peoplerelate.xml",
//			"config-manager.xml", "SeeyonOrganization.xml", "affair-manager.xml", 
//			"mobile.xml", "collaboration-manager.xml", "doc-manager.xml", "bulletin.xml", "news.xml", "calendar.xml" };
//	
//	ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
//	
//	OrgManager orgManager = (OrgManager) ctx.getBean("OrgManager");		
//	OAManagerInterface oaManagerInterface = (OAManagerInterface) ctx.getBean("oaManagerInterface");	
//	ColManager colManager = (ColManager) ctx.getBean("colManager");	
//	MessageManagerInterface messageManagerInterface = (MessageManagerInterface) ctx.getBean("messageManager");	
//	
//	
//	long startTime = 0;
//	
//	@Override
//	protected void setUp() throws Exception {
//		setCurrentUser();
//		ApplicationContextHolder.setApplicationContext(ctx);
//		
//		startTime = System.currentTimeMillis();
//		
//		super.setUp();
//	}
//	
//	@Override
//	protected void tearDown() throws Exception {
//		System.out.println("耗时：" + (System.currentTimeMillis() - startTime) + " MS");
//		super.tearDown();
//	}
//
//	/**
//	 * 协同4个列表
//	 *
//	 */
//	public void te2stCollaborationList() {		
//		int pagecounter = 4;
//		int pagenumber = 1;
//		
////		log.info("测试待发");
////		
////		List<AffairsListObject> currentlist1 = new ArrayList<AffairsListObject>();
////		int total1 = oaManagerInterface.getCollaborationWaitSendList(null, pagecounter, pagenumber, currentlist1, null);
////		
////		System.out.println("待发总数" + total1);
////		for (AffairsListObject object : currentlist1) {
////			System.out.println(object.getId() + "\t" + object.getTitle());
////		}
//		
//		log.info("测试已发");
//		
//		List<AffairsListObject> currentlist2 = new ArrayList<AffairsListObject>();
//		int total2 = oaManagerInterface.getCollaborationSentList(null, pagecounter, pagenumber, currentlist2, null);
//		
//		System.out.println("已发总数" + total2);
//		for (AffairsListObject object : currentlist2) {
//			System.out.println(object.getId() + "\t" + object.getTitle());
//		}
////		
////		log.info("测试待办");
////		
////		List<AffairsListObject> currentlist3 = new ArrayList<AffairsListObject>();
////		int total3 = oaManagerInterface.getCollaborationPendingList(null, pagecounter, pagenumber, currentlist3, null);
////		
////		System.out.println("待办总数" + total3);
////		for (AffairsListObject object : currentlist3) {
////			System.out.println(object.getId() + "\t" + object.getTitle());
////		}
////
////		log.info("测试已办");
////		
////		List<AffairsListObject> currentlist4 = new ArrayList<AffairsListObject>();
////		int total4 = oaManagerInterface.getCollaborationDoneList(null, pagecounter, pagenumber, currentlist4, null);
////		
////		System.out.println("已办总数" + total4);
////		for (AffairsListObject object : currentlist4) {
////			System.out.println(object.getId() + "\t" + object.getTitle());
////		}
//	}
//	
//	public void te2stCollaborationNode(){
//		try {
//			Map<String, Object> n = this.oaManagerInterface.getNodes(7372646419432555232L);
//			System.out.println(n);
//		}
//		catch (ColException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void te2stGetProcessModeSelectorList(){
//		try {
//			Map<String, Object> n = this.oaManagerInterface.getProcessModeSelectorList(-3115030914740370560L, -1L);
//			System.out.println(n);
//		}
//		catch (ColException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void te2stIsSeen(){
//		boolean a = this.oaManagerInterface.isSeen(-8201123131973023577L, null);
//		System.out.println(a);
//	}
//	
//	public void te2stSaveToPendingAffair(){
//		List<Long> memberlist = new ArrayList<Long>();
//		memberlist.add(-1060164074406394784L);
//		memberlist.add(-276534882846619412L);
//		
//		try {
//			this.oaManagerInterface.saveToPendingAffair(0L,"来自手机的待发协同(串发)", "来自手机的待发协同来自手机的待发协同", memberlist, 0, -1060164074406394784L);
//			this.oaManagerInterface.saveToPendingAffair(0L,"来自手机的待发协同(并发)", "来自手机的待发协同来自手机的待发协同", memberlist, 1, -1060164074406394784L);
//		}
//		catch (ColException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void te2stSendCollaborationNow(){
//		List<Long> memberlist = new ArrayList<Long>();
//		memberlist.add(-1060164074406394784L);
//		memberlist.add(-276534882846619412L);
//		
//		try {
////			this.oaManagerInterface.sendCollaborationNow("11来自手机的待发协同(串发)", "来自手机的待发协同来自手机的待发协同", memberlist, 1, -1060164074406394784L);
////			this.oaManagerInterface.sendCollaborationNow("11来自手机的待发协同(并发)", "来自手机的待发协同来自手机的待发协同", memberlist, 0, -1060164074406394784L, null, null);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void te2stProcessCollaboration(){
//		try {
////			this.oaManagerInterface.processCollaboration(2, -4383285131773260318L, "暂存待办意见：好家伙", 1);
////			this.oaManagerInterface.processCollaboration(1, -4383285131773260318L, "正常处理意见：好家伙", 2, null);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void te2stCollaborationDetail(){
//		Collaboration c = oaManagerInterface.CollaborationDetial(692982995340577758L, null);
//		System.out.println(c.getTitle());
////		System.out.println(c.getContent());
////		System.out.println(c.getCreaterOr());
////		System.out.println(c.getOpinions());
////		System.out.println(c.getComments());
//		System.out.println(c.getOriginalSendOpinionKey());
//		System.out.println(c.getOriginalSendOpinion());
//		System.out.println(c.getOriginalSignOpinion());
//	}
//	
//	public void te2stCollaborationNumWithType(){
//		Map<String, Integer> nums = this.oaManagerInterface.getCollaborationNumWithType(null);
//		System.out.println(nums);
//	}
//	
//	public void te2stMeetingDetail(){
//		MeetingDetial m = this.oaManagerInterface.getMeetingDetial(3750141888878955658L, null);
//		System.out.println(m.getTitle());
//		System.out.println(m.getContent());
//		System.out.println(m.getCreator());
//	}
//	
//	public void te2stProcessMeeting(){
//		try {
//			this.oaManagerInterface.processMeeting(4693952796171456114L, -1124336987124644239L, 1, "不去，好家伙");
//		}
//		catch (BusinessException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void te2stMeetingObjectList(){
//		List<AffairsListObject> currentlist = new ArrayList<AffairsListObject>();
//		int count = this.oaManagerInterface.getMeetingObjectList(null, 4, 1, currentlist, "1");
//		
//		System.out.println(count);
//		System.out.println(currentlist);
//	}
//	
//	public void te2stSimpleColSummary(){
//		ColSummary s = null;
//		try {
//			s = colManager.getSimpleColSummaryById(-8768631914409144433L);
//		}
//		catch (ColException e) {
//			e.printStackTrace();
//		}
//		System.out.println(s);
//	}
//	
//	public void te2stGetTeamList(){
//		List<V3xOrgTeam> t = this.oaManagerInterface.getTeamList(null);
//		System.out.println(t);
//		for (V3xOrgTeam team : t) {
//			System.out.println(team);
//		}
//	}
//	
//	public void te2stSendMessageToPC(){
//		MessageReciever r = new MessageReciever(-1060164074406394784L, "13488826950", -1L);
//		MobileMessageObject o = new MobileMessageObject();
//		o.addMessageReciever(r);
//		o.setContent("hello你好");
//		o.setSid(-1060164074406394784L);
//		messageManagerInterface.sendCommunictionMessageList(o);
//	}
//	
//	public void te2stSendMessageToMobile(){
//		List<MobileMessageObject> ms = this.messageManagerInterface.getMessageList();
//		for (MobileMessageObject msg : ms) {
//			System.out.println(msg.getSid());
//			System.out.println(msg.getContent());
//			System.out.println(msg.getRecievers());
//		}
//	}
//	
//	public void te2stUpdateSendMessageStateFromMobile(){
//		List<Long> mids = new ArrayList<Long>();
//		mids.add(-6038011274832034895L);
//		mids.add(9079337650348771342L);
//		
//		this.messageManagerInterface.updateMessageState(mids);
//	}
//	
//	public void te2stHomepage(){
//		Map<String, Integer> map = this.oaManagerInterface.getHomePageInfo(null);
//		System.out.println(map);
//	}
//	
//	public void te2stPendingTrack(){
//		int pagecounter = 4;
//		int pagenumber = 1;
//		List<AffairsListObject> pengingaffairlist = new ArrayList<AffairsListObject>();
//		this.oaManagerInterface.getPendingAffairObjectList(null, pagecounter, pagenumber, pengingaffairlist, null);
//		System.out.println(pengingaffairlist);
//		
//		List<AffairsListObject> trackaffairlist = new ArrayList<AffairsListObject>();
//		this.oaManagerInterface.getTrackAffairObjectList(null, pagecounter, pagenumber, trackaffairlist, null);
//		System.out.println(trackaffairlist);
//	}
//	
//	public void te2stGetRelativeMember(){
//		//Map<RelationType, List<V3xOrgMember>> m = this.oaManagerInterface.getRelativeMember(null);
//		
//	}
//		
//	public void setCurrentUser() {
//		User user = CurrentUser.get();
//        if (user == null) {
//            V3xOrgMember member = null;
//			try {
//				orgManager.setDefaultAccount(-5362937964371884064L);
//				member = orgManager.getMemberByLoginName("tanmf");
//				user = new User();
//				user.setId(member.getId());
//				user.setLevelId(member.getOrgLevelId());
//				user.setDepartmentId(member.getOrgDepartmentId());
//				user.setLoginAccount(member.getOrgAccountId());
//				user.setLoginName(member.getLoginName());
//				user.setName(member.getName());
//				user.setPassword(member.getPassword());
//				user.setPostId(member.getOrgPostId());
//				user.setAgentToId(member.getAgentToId());
//				
//				CurrentUser.set(user);
//			}
//			catch (BusinessException e) {
//				e.printStackTrace();
//			}
//        }
//	}
//	
//	public void te2stBulletinList(){
//		int pagecounter = 4;
//		int pagenumber = 1;
//		
//		List<AffairsListObject> currentlist = new ArrayList<AffairsListObject>();
//		//int count = this.oaManagerInterface.getBulletinList(null, pagecounter, pagenumber, currentlist, null);
//		//System.out.println(count);
//		for (AffairsListObject object : currentlist) {
//			System.out.println(object.getTitle());
//			System.out.println(object.getId());
//		}
//	}
//	
//	public void te2stBulletionDetial(){
//		Bulletion bulletion = this.oaManagerInterface.getBulletionDetial(-5028002684482600336L, null);
//		
//		System.out.println(bulletion.getTitle());
//		System.out.println(bulletion.getContent());
//		System.out.println(bulletion.getType());
//		System.out.println(bulletion.getSenderId());
//		System.out.println(bulletion.getSendTime());
//	}
//	
//	public void te2stNewsList(){
//		int pagecounter = 4;
//		int pagenumber = 1;
//		
//		List<AffairsListObject> currentlist = new ArrayList<AffairsListObject>();
//		//int count = this.oaManagerInterface.getNewsList(null, pagecounter, pagenumber, currentlist, null);
//		//System.out.println(count);
//		for (AffairsListObject object : currentlist) {
//			System.out.println(object.getTitle());
//		}
//	}
//	
//	public void te2stCalendarList(){
//		int pagecounter = 4;
//		int pagenumber = 1;
//		
//		List<AffairsListObject> currentlist = new ArrayList<AffairsListObject>();
//		//int count = this.oaManagerInterface.getCalendarList(null, pagecounter, pagenumber, currentlist, null);
//		//System.out.println(count);
//		for (AffairsListObject object : currentlist) {
//			System.out.println(object.getTitle());
//		}
//	}
//	
//	public void te2stSearchMember(){
//		int pagecounter = 4;
//		int pagenumber = 2;
//		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
//		int count = this.oaManagerInterface.searchMember("张", pagecounter, pagenumber, members);
//		System.out.println(count);
//		for (V3xOrgMember member : members) {
//			System.out.println(member.getName());
//		}
//	}
//	
////	public static void main(String[] args) {
////		d.setTimeZone(TimeZone.getTimeZone("GMT+9"));
////		String s = d.format(new Date());
////		System.out.println(s);
////		
////		String[] sid = TimeZone.getAvailableIDs();
////		for (String string : sid) {
////			System.out.println(string);
////		}
////		
////		System.out.println(TimeZone.getTimeZone("America/New_York"));
////		
////		String a = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "top.datetime.pattern", new Date(), "GMT+9");
////		System.out.println(a);
////		
////		MessageFormat mf = new MessageFormat("时间{0, date, yyyy年MM月dd日 HH:mm:ss}");
////		String a = mf.format(new Object[]{new Date()});
////		System.out.println(a);
////	}
//
//}