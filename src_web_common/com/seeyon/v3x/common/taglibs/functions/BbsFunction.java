/**
 * 
 */
package com.seeyon.v3x.common.taglibs.functions;

import static org.apache.taglibs.standard.tag.common.fmt.BundleSupport.getLocalizationContext;

import java.util.ResourceBundle;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.webmodel.ArticleModel;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.util.Strings;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-9-17
 */
public class BbsFunction {
	/**
	 * [置顶][原/转]{subject}[精]
	 * 
	 * @param model
	 * @param length
	 * @param pageContext
	 * @return
	 */
	public static String showSubject(ArticleModel model, int length, PageContext pageContext){
		LocalizationContext locCtxt = getLocalizationContext(pageContext);
		ResourceBundle bundle = locCtxt.getResourceBundle();
		
		return showSubject(model, length, bundle);
	}
	
	/**
	 * [置顶][原/转]{subject}[精]
	 * 
	 * @param model
	 * @param length
	 * @param bundle
	 * @return
	 */
	public static String showSubject(ArticleModel model, int length, ResourceBundle bundle){
		if(model == null){
			return null;
		}
		
		String str = "";
		if(model.getTopSequence() > 0){
			String label = ResourceBundleUtil.getString(bundle, "bbs.top.label");
			str += "<font color='red'>[" + label + "]</font>";
			length -= label.getBytes().length + 2;
		}
		
//		if(model.getResourceFlag() == 1){
//			String label = ResourceBundleUtil.getString(bundle, "bbs.yuan.label");
//			str += "<font color='green'>[" + label + "]</font>";
//			length -= label.getBytes().length + 2;
//		}
//		else if(model.getResourceFlag() == 2){
//			String label = ResourceBundleUtil.getString(bundle, "bbs.zhuan.label");
//			str += "<font color='green'>[" + label + "]</font>";
//			length -= label.getBytes().length + 2;
//		}
		
		String eliteLabel = null;
		if(model.getEliteFlag()){
			eliteLabel = ResourceBundleUtil.getString(bundle, "bbs.elite.label");
			length -= eliteLabel.getBytes().length + 2;
		}
		
		str += Strings.getLimitLengthString(Functions.toHTML(model.getArticleName()), length, "...");
		
		if(model.getEliteFlag()){
			str += "<font color='red'>[" + eliteLabel + "]</font>";
		}
		
		return str;
	}
	
	/**
	 * 显示发帖者的名称
	 * 
	 * @param model
	 * @param pageContext
	 * @return
	 */
	public static String showName(ArticleModel model, PageContext pageContext){
		LocalizationContext locCtxt = getLocalizationContext(pageContext);
		ResourceBundle bundle = locCtxt.getResourceBundle();
		
		return showName(model, bundle);
	}
	
	/**
	 * 显示发帖者的名称
	 * 
	 * @param model
	 * @param bundle
	 * @return
	 */
	public static String showName(ArticleModel model, ResourceBundle bundle){
		if(model == null){
			return null;
		}
		
		long userId = CurrentUser.get().getId();
		long memberId = model.getIssueUser();
		//匿名
		if(Boolean.TRUE.equals(model.isAnonymousFlag())
				&& userId != memberId  //当前用户不是发帖者
				&& !(model.getBoard() != null && model.getBoard().getAdmins().contains(userId)) //当前用户是管理员
			){
			return ResourceBundleUtil.getString(bundle, "anonymous.label");
		}
		
		return Functions.showMemberName(memberId);
	}
	
	public static String showBoardName(ArticleModel model){
		if(model == null){
			return null;
		}
		
		V3xBbsBoard board = model.getBoard();
		if(board == null){
			return null;
		}
		
		if(board.getAccountId() == null 
				|| board.getAffiliateroomFlag() == com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType.group.ordinal()){
			return board.getName();
		}
		
		long loginAccount = CurrentUser.get().getLoginAccount();
		if (board.getAffiliateroomFlag() != 5 && board.getAffiliateroomFlag() != 6) {
			if(loginAccount != board.getAccountId().longValue()){ //外单位的
				String accountShortname = ((V3xOrgAccount)Functions.getEntity("Account", board.getAccountId())).getShortname();
				
				return "(" + accountShortname + ")" + board.getName();
			}
		}
		return board.getName();
	}
}
