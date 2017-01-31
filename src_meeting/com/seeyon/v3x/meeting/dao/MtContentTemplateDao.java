package com.seeyon.v3x.meeting.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.meeting.domain.MtContentTemplate;

public class MtContentTemplateDao extends BaseHibernateDao<MtContentTemplate> {

	/**
	 * 根据名字获得整个模板对象
	 */
	public MtContentTemplate findMttembyTemplateName(Object... pro)
	{
		MtContentTemplate mt=null;
		String hqlStr="from MtContentTemplate as mt where mt.templateName=? and (mt.ext1=? or mt.ext2=?)";
		List<MtContentTemplate> list=this.getHibernateTemplate().find(hqlStr, pro);
		if(list!=null&&list.size()>0)
		{
			mt=list.get(0);
		}
		return mt;
	}
}
