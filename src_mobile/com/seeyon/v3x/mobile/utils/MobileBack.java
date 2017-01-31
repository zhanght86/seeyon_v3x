package com.seeyon.v3x.mobile.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 处理协同 附件返回的问题<br>
 * 将返回问题看做是一个分页的过程，其中总数为访问的所有协同，每页有1条数据
 * 切换到首页后，清空session，不支持使用手机端返回来进行 关联附件的返回，这里只进行防护
 * @author dongyj
 *
 */
public class MobileBack {
	//当前深度
	private static final String DEEP = "deep";
	//增加深度
	private static final int FORWARD = 1;
	//减小深度
	private static final int BACK = 0;
	//不增加深度 也不减小深度
	private static final int NORMAL = 2;
	//数据
	private static final String AFFAIR_ID = "parentId";
	//处理方式，是增加深度还是减小深度
	private static final String DEALTYPE = "deal_type";
	
	//前进---当查看附件列表时候，就相当于分页中的前进
	public static void service(HttpServletRequest request) {
		String deeply = request.getParameter(DEEP);// 当前页
		String dealType = request.getParameter(DEALTYPE);
		String parentId = request.getParameter(AFFAIR_ID);
		try {
			int deep = 0;
			if (!Strings.isBlank(deeply)) {
				deep = Integer.parseInt(deeply);
			}
			List<String> data = getTotal();
			int size = data.size();
			if (Strings.isNotBlank(dealType)) {
				switch (Integer.parseInt(dealType)) {
				case FORWARD:
					if(deep <= size && size != 0){//刷新或者使用手机后退后的请求
						parentId = data.get(deep);
					}else{
						data.add(parentId);
					}
					deep += 1;
					break;
				case BACK:
					if(deep <= size){
						parentId = data.get(deep-1);
					}
					deep -=1;
					break;
				}
			}
			request.setAttribute(AFFAIR_ID, parentId);
			request.setAttribute(DEEP, deep);
			WebUtil.saveObject(data);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 得到现在session缓存的affairid，注意这里得到后，就从session中清理了，如果使用，注意保存
	 */
	private static List<String> getTotal(){
		Object o = WebUtil.getObject();
		List<String> list = null;
		if(o != null && o instanceof List){
			list = (List<String>)o;
		}else{
			list = new ArrayList<String>();
		}
		return list;
	}
	
	//清理session的缓存，（不清理其他的）
	public static void clearSession(){
		Object o = WebUtil.getObject();
		if(o != null && !(o instanceof List)){
			WebUtil.saveObject(o);
		}
	}
	
}
