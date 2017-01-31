package com.seeyon.v3x.doc.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.doc.dao.DocMetadataDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.webmodel.DocSearchModel;
import com.seeyon.v3x.doc.webmodel.SimpleDocQueryModel;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;


/**
 * 文档简单属性、高级组合查询工具类，用于解析查询条件并获取列表结果
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-12-24
 */
public class DocSearchHqlUtils {
	private static final Log logger = LogFactory.getLog(DocSearchHqlUtils.class);
	
	/**
	 * Hql语句中对文档类型部分的约束条件，约定文档主表别名为"d"
	 */
	public static final String HQL_FR_TYPE = " and d.frType!=" + Constants.FOLDER_PLAN
											+ " and d.frType!=" + Constants.FOLDER_TEMPLET
											+ " and d.frType!=" + Constants.FOLDER_BORROW
											+ " and d.frType!=" + Constants.FOLDER_SHARE
											+ " and d.frType!=" + Constants.FOLDER_SHAREOUT
											+ " and d.frType!=" + Constants.FOLDER_BORROWOUT
											+ " and d.frType!=" + Constants.FOLDER_PLAN_WORK
											+ " and d.frType!=" + Constants.FOLDER_PLAN_DAY
											+ " and d.frType!=" + Constants.FOLDER_PLAN_MONTH
											+ " and d.frType!=" + Constants.FOLDER_PLAN_WEEK;
	
	/**
	 * Hql语句中对所得结果的排序条件，约定文档主表别名为"d"，按照最后修改时间降序、排序字段升序排列
	 */
	public static String Order_By_Query = " order by d.lastUpdate desc, d.frOrder ";

	@SuppressWarnings("unchecked")
	public static List<DocResource> searchByProperties(DocResource dr, DocSearchModel dsm, DocResourceDao docResourceDao, DocMetadataDao docMetadataDao) {
		if (logger.isDebugEnabled()) {
			logger.debug("[高级查询条件]:\n" + TaskConstants.xStream4Debug.toXML(dsm));
		}

		List<SimpleDocQueryModel> metaDataQs = dsm.getMetaDataQueries();
		List<SimpleDocQueryModel> simpleQs = dsm.getSimplePropertyQueries();
		boolean hasMetaDataQ = CollectionUtils.isNotEmpty(metaDataQs);
		boolean hasSimpleQ = CollectionUtils.isNotEmpty(simpleQs);

		StringBuffer hql2 = new StringBuffer("select d from DocResource as d ");
		Map<String, Object> params = CommonTools.newHashMap("logicalPath", dr.getLogicalPath() + ".%");
		if (hasMetaDataQ) {// 扩展属性 + 简单属性查询 或 扩展属性查询
			StringBuffer hql1 = new StringBuffer(" , DocMetadata as dm where d.id=dm.docResourceId ");
			parseProperties4Hql(hql1, metaDataQs, params, " dm.");

			hql2.append(hql1 + " and d.logicalPath like :logicalPath ");
			if (hasSimpleQ) {
				parseProperties4Hql(hql2, simpleQs, params, " d.");
			}
		} else {// 仅有简单属性查询
			hql2.append(" where d.logicalPath like :logicalPath ");
			parseProperties4Hql(hql2, simpleQs, params, " d.");
		}

		return docMetadataDao.find(hql2.toString() + HQL_FR_TYPE + Order_By_Query, -1, -1, params);
	}
	
	private static void parseProperties4Hql(StringBuffer hql, List<SimpleDocQueryModel> sdms, Map<String, Object> params, String as) {
		if(CollectionUtils.isNotEmpty(sdms)) {
			for(SimpleDocQueryModel sdm : sdms) {
				parseSingleProperty4Hql(hql, sdm, params, as);
			}
		}
	}

	/**
	 * 将单个属性查询的约束条件拼接到Hql语句中，并将命名变量键值对设定好
	 * @param hql	主hql语句
	 * @param sdm	查询条件
	 * @param params	命名变量键值Map
	 */
	private static void parseSingleProperty4Hql(StringBuffer hql, SimpleDocQueryModel sdm, Map<String, Object> params, String as) {
		if(sdm == null || Strings.isBlank(sdm.getPropertyName()))
			throw new IllegalArgumentException("这位大侠，您给出的文档查询条件无效，恕洒家无法给力...");
		boolean isOralce = "Oracle".equals(SystemEnvironment.getA8DatabaseType());
		String propertyName = sdm.getPropertyName();
		int type = sdm.getPropertyType();
		String and = " and ";
		switch(type) {
		case Constants.DATE :
		case Constants.DATETIME :
			if(Strings.isNotBlank(sdm.getValue1())) {
				hql.append(and + as + propertyName + " >= :" + propertyName + "startDate ");
				params.put(propertyName + "startDate", Datetimes.getTodayFirstTime(sdm.getValue1()));
			}
			
			if(Strings.isNotBlank(sdm.getValue2())) {
				hql.append(and + as + propertyName + " <= :" + propertyName + "endDate ");
				params.put(propertyName + "endDate", Datetimes.getTodayLastTime(sdm.getValue2()));
			}	
			break;
		case Constants.USER_ID :
		case Constants.DEPT_ID :
		case Constants.REFERENCE :
		case Constants.CONTENT_TYPE :
		case Constants.ENUM :
		case Constants.SIZE :
		case Constants.IMAGE_ID :
			Long value = NumberUtils.toLong(sdm.getValue1(), -1l);
			if(value != -1l) {
				hql.append(and + as + propertyName + " = :" + propertyName + "Value ");
				params.put(propertyName + "Value", value);
			}
			break;
		case Constants.TEXT_ONE_LINE :
		case Constants.TEXT :
			if(Strings.isNotBlank(sdm.getValue1())) {
				if (isOralce) {
					hql.append(and + "lower(" + as  + propertyName + ") like :" + propertyName + "Value ");
					params.put(propertyName + "Value", "%" + SQLWildcardUtil.escape(sdm.getValue1().trim()).toLowerCase() + "%");
				} else {
					hql.append(and + as  + propertyName + " like :" + propertyName + "Value ");
					params.put(propertyName + "Value", "%" + SQLWildcardUtil.escape(sdm.getValue1().trim()) + "%");
				}
			}
			break;
		case Constants.INTEGER :
			hql.append(and + as + propertyName + " = :" + propertyName + "Value ");
			params.put(propertyName + "Value", NumberUtils.toInt(sdm.getValue1()));
			break;
		case Constants.BOOLEAN :
			if(Strings.isNotBlank(sdm.getValue1())) {
				hql.append(and + as + propertyName + " = :" + propertyName + "Value ");
				params.put(propertyName + "Value", BooleanUtils.toBoolean(sdm.getValue1()));
			}
			break;
		case Constants.FLOAT :
			if(Strings.isNotBlank(sdm.getValue1())) {
				hql.append(and + as + propertyName + " = :" + propertyName + "Value ");
				params.put(propertyName + "Value", NumberUtils.toDouble(sdm.getValue1()));
			}
			break;
		}
	}
	
}
