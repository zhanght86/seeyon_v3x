package com.seeyon.v3x.doc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocVersionInfo;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 文档历史版本信息Dao
 * @author <a href="mailto:yangmeng84@sina.com">菜鸟杨</a> 2010-11-2
 */
public class DocVersionInfoDao extends BaseHibernateDao<DocVersionInfo> {

	/**
	 * 获取某一文档的符合指定搜索条件的所有历史版本信息前端展现字段数组集合
	 * @param docResId	文档ID
	 * @param sm	搜索类型及值
	 * @param page	是否需要分页
	 * @return	符合条件的历史版本信息记录的前端展现字段数组集合
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getDisplayFields(Long docResId, SearchModel sm, boolean page) {
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		
		sb.append("select " + StringUtils.join(DocVersionInfo.DISPLAY_FIELDS, ','));
		sb.append(" from " + DocVersionInfo.class.getCanonicalName() + " where " + DocVersionInfo.PROP_DOC_RES_ID + "=? ");
		params.add(docResId);
		
		if(sm != null) {
			if(sm.searchByCreator()) {
				sb.append(" and " + DocVersionInfo.PROP_LAST_USER + "=? ");
				params.add(NumberUtils.toLong(sm.getSearchValue2()));
			} 
			else if(sm.searchByName()) {
				sb.append(" and " + DocVersionInfo.PROP_FRNAME + " like ? ");
				params.add("%" + SQLWildcardUtil.escape(sm.getSearchValue1().trim()) + "%");
			} 
			else if(sm.searchByDate()) {
				if(Strings.isNotBlank(sm.getSearchValue1())) {
					sb.append(" and " + DocVersionInfo.PROP_LAST_UPDATE + " >= ? ");
					params.add(Datetimes.getTodayFirstTime(sm.getSearchValue1()));
				}
				
				if(Strings.isNotBlank(sm.getSearchValue2())) {
					sb.append(" and " + DocVersionInfo.PROP_LAST_UPDATE + " <= ? ");
					params.add(Datetimes.getTodayLastTime(sm.getSearchValue2()));
				}		
			}
			else if(sm.searchByVersionNumber()) {
				sb.append(" and " + DocVersionInfo.PROP_VERSION + "=? ");
				params.add(this.parse2VersionNumber(sm.getSearchValue1()));
			}
		}
		
		if(sm == null || !sm.searchByVersionNumber())
			sb.append(" order by " + DocVersionInfo.PROP_VERSION + " desc");
		
		return page ? this.find(sb.toString(), null, params) : this.find(sb.toString(), -1, -1, null, params);
	}
	
	/**
	 * 支持用户如"V1.0"、"v1.0"、"1.0"等格式的输入，解析为整数版本号
	 * @param versionNumberStr	版本号输入值
	 */
	private int parse2VersionNumber(String versionNumberStr) {
		int result = 0;
		try {
			result = Integer.parseInt(versionNumberStr);
		}
		catch(NumberFormatException e) {
			if(Strings.isNotBlank(versionNumberStr)) {
				String version = versionNumberStr.replaceAll("[a-zA-Z]*", "");
				String[] elements = StringUtils.split(version, '.');
				if(elements != null && elements.length > 0)
					result = NumberUtils.toInt(elements[0]);
			}
		}
		return result;
	}
	
	/**
	 * 将字段数组集合转换为文档历史版本信息集合，同时根据文件类型设定显示图标
	 * @param objArrList	字段数组集合
	 */
	private List<DocVersionInfo> parseObjArrs(List<Object[]> objArrList) {
		List<DocVersionInfo> result = null;
		if(CollectionUtils.isNotEmpty(objArrList)) {
			result = new ArrayList<DocVersionInfo>(objArrList.size());
			for(Object[] arr : objArrList) {
				DocVersionInfo dvi = new DocVersionInfo();
				int index = 0;
				for(String fieldName : DocVersionInfo.DISPLAY_FIELDS) {
					try {
						PropertyUtils.setSimpleProperty(dvi, fieldName, arr[index++]);
					} 
					catch (Exception e) {
						logger.error("设置文档历史版本属性时出现异常：", e);
					}
				}
				result.add(dvi);
			}
		}
		return result;
	}
	
	/**
	 * 获取某一文档的符合指定搜索条件的所有历史版本信息
	 * @param docResId	文档ID
	 * @param sm	搜索类型及值
	 * @param page	是否需要分页
	 * @return	符合条件的历史版本信息记录
	 */
	public List<DocVersionInfo> getAllDocVersion(Long docResId, SearchModel sm, boolean page) {
		List<Object[]> objArrs = this.getDisplayFields(docResId, sm, page);
		return this.parseObjArrs(objArrs);
	}
	
	/**
	 * 根据给定的历史版本信息ID集合查找对应的数据
	 * @param dvIds		历史版本信息ID集合
	 * @param page		是否分页
	 * @return	历史版本信息展现字段数组集合
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getDisplayFields(List<Long> dvIds, boolean page) {
		String hql = "select " + StringUtils.join(DocVersionInfo.DISPLAY_FIELDS, ',') + " from " + DocVersionInfo.class.getCanonicalName() + " where id in (:ids)";
		Map<String, Object> params = FormBizConfigUtils.newHashMap("ids", dvIds);
		
		return page ? this.find(hql, params) : this.find(hql, -1, -1, params);
	}
	
	/**
	 * 根据给定的历史版本信息ID集合查找对应的数据
	 * @param dvIds		历史版本信息ID集合
	 * @param page		是否分页
	 * @return	历史版本信息对象集合
	 */
	public List<DocVersionInfo> getDocVersionInfos(List<Long> dvIds, boolean page) {
		List<Object[]> objArrs = this.getDisplayFields(dvIds, page);
		return this.parseObjArrs(objArrs);
	}
	
	/**
	 * 获取指定文档的历史版本信息总数目
	 * @param docResId	文档ID
	 */
	public int getDocVersionCount(Long docResId) {
		String hql = "select count(v.id) from " + DocVersionInfo.class.getCanonicalName() + " as v, " + 
		DocResource.class.getCanonicalName() + " as r where v.docResourceId=r.id and r.id=? and r.versionEnabled=true";
		Integer count = (Integer)super.findUnique(hql, null, docResId);
		return count == null ? 0 : count.intValue();
	}
	
	/**
	 * 获取指定文档的历史版本信息当前最高版本号
	 * @param docResId	文档ID
	 * @return	最高历史版本号
	 */
	public int getMaxVersion(Long docResId) {
		String hql = "select max(version) from " + DocVersionInfo.class.getCanonicalName() + " where docResourceId=?";
		Integer max = (Integer)super.findUnique(hql, null, docResId);
		return max == null ? 0 : max;
	}
	
}
