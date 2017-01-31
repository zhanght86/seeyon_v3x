/**
 * 
 */
package com.seeyon.v3x.main.phrase;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-1-22
 */
public class CommonPhraseController extends BaseController {
	private static Log log = LogFactory.getLog(CommonPhraseController.class);
	private CommonPhraseManager phraseManager;

	public void setPhraseManager(CommonPhraseManager phraseManager) {
		this.phraseManager = phraseManager;
	}

	/**
	 * 普通用户在回复协同时对常用语进行编辑
	 */
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("v3xmain/phrase/edit");

		String id = request.getParameter("id");
		CommonPhrase phrase = null;
		if (id == null) { // new
			phrase = new CommonPhrase();
		} else {// edit
			phrase = this.phraseManager.get(new Long(id));
		}

		mv.addObject("phrase", phrase);

		return mv;
	}

	/**
	 * 列表显示
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mv = new ModelAndView("v3xmain/phrase/list");
		
		DetachedCriteria criteria = DetachedCriteria.forClass(CommonPhrase.class)
			.add(Expression.eq("accountId", user.getLoginAccount()))
			.add(Expression.or(
					Expression.eq("memberId", user.getId()),
					Expression.eq("type", CommonPhrase.PHRASE_TYPE.system.ordinal())
			))
			.addOrder(Order.desc("createDate"))
		;
		
		List<CommonPhrase> phrases = (List<CommonPhrase>)phraseManager.executeCriteria(criteria, -1, -1);

		mv.addObject("phrases", phrases);
		return mv;
	}

	@SuppressWarnings("unchecked")
	public ModelAndView list4Edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("v3xmain/phrase/list4Edit");
		User user = CurrentUser.get();

		String queryString = "from CommonPhrase as c where memberId=? order by createDate desc ";

		List<CommonPhrase> phrases = (List<CommonPhrase>) phraseManager.find(queryString, user.getId());

		mv.addObject("phrases", phrases);
		return mv;
	}

	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		CommonPhrase c = new CommonPhrase();

		bind(request, c);

		Date date = new Date();

		if (c.isNew()) {// insert
			c.setIdIfNew();
			c.setMemberId(user.getId());
			c.setAccountId(user.getLoginAccount());
			c.setCreateDate(date);
			c.setUpdateDate(date);

			this.phraseManager.save(c);
		} else {// update
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("content", c.getContent());
			map.put("updateDate", date);

			this.phraseManager.update(c.getId(), map);
		}

		return super.redirectModelAndView("/phrase.do?method=list4Edit");
	}

	/**
	 * 删除
	 */
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String id = request.getParameter("id");

		if (id != null) {
			this.phraseManager.delete(new Long(id));
		}

		return super.redirectModelAndView("/phrase.do?method=list4Edit");
	}

	/* 下面是单位管理员进行常用语设置的相关方法 */
	
	/**
	 * 系统常用语进入方法
	 */
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView systemFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("v3xmain/phrase/systemFrame");
	}

	/**
	 * 系统常用语的显示数据方法
	 */
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView systemList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView result = new ModelAndView("v3xmain/phrase/systemList");
        DetachedCriteria criteria = DetachedCriteria.forClass(CommonPhrase.class)
        .add(Expression.eq("accountId", user.getLoginAccount()))
        .add(Expression.or(Expression.eq("memberId", user.getId()),
                Expression.eq("type", CommonPhrase.PHRASE_TYPE.system.ordinal())
        ))
        .addOrder(Order.asc("createDate"));
    
        List<CommonPhrase> phraseList = (List<CommonPhrase>)phraseManager.executeCriteria(criteria, -1, -1);
		result.addObject("phraseList", phraseList);
		return result;
	}

	/**
	 * 进入系统常用语的添加方法
	 */
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView addPhrase(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("v3xmain/phrase/systemEdit");
		result.addObject("systemEumitosis",1);
		return result;
	}

	/**
	 * 进入系统常用语的修改方法
	 */
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView modifyPhrase(HttpServletRequest request,
			HttpServletResponse resposne) {
		ModelAndView result = new ModelAndView("v3xmain/phrase/systemEdit");

		Long phraseId = Long.valueOf(request.getParameter("id"));
		CommonPhrase commonPhrase = null;
		try {
			commonPhrase = phraseManager.get(phraseId);
		}
		catch (BusinessException e) {
			log.error("", e);
		}

		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("systemEumitosis",3);
		}else{
			result.addObject("systemEumitosis",2);
		}
		result.addObject("commonPhrase", commonPhrase);
		return result;
	}

	/**
	 * 系统常用语的 create and modify 方法
	 */
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView systemEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String phraseId = request.getParameter("id");
		CommonPhrase commonPhrase = new CommonPhrase();
		User user = CurrentUser.get();
		bind(request, commonPhrase);

		Date date = new Date();
		if (phraseId == null || phraseId.equals("")) {
			commonPhrase.setIdIfNew();
			commonPhrase.setMemberId(user.getId());
			commonPhrase.setAccountId(user.getLoginAccount());
			commonPhrase.setCreateDate(date);
			commonPhrase.setUpdateDate(date);
			this.phraseManager.save(commonPhrase);
			return super.refreshWorkspace();

		} else {
			commonPhrase.setIdIfNew();
			commonPhrase.setMemberId(user.getId());
			commonPhrase.setAccountId(user.getLoginAccount());
			commonPhrase.setCreateDate(date);
			commonPhrase.setUpdateDate(date);
			this.phraseManager.update(commonPhrase);
			return super.refreshWorkspace();
		}
	}

	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView systemDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			String[] ids = request.getParameterValues("id");

			Long l = null;
			PrintWriter out = response.getWriter();
			for (int i = 1; i < ids.length; i++) {
				l = Long.valueOf(ids[i].toString());
				this.phraseManager.delete(l);
			}
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('MainLang.system_lang_option_ok'));");
			out.println("</script>");

			return super.refreshWorkspace();
		} catch (Exception e) {
			log.error("", e);
			return null;
		}

	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("v3xmain/phrase/frame");
	}

}
