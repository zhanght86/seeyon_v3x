package com.seeyon.v3x.main.phrase;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.manager.EdocManagerImpl;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

/**
 * 
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-1-17
 */
public class CommonPhraseManagerImpl extends BaseHibernateDao<CommonPhrase> implements CommonPhraseManager {
	
	private final static Log log = LogFactory.getLog(CommonPhraseManagerImpl.class);
	
	public void delete(Long id) throws BusinessException {
		super.delete(id.longValue());
	}

	public void save(CommonPhrase o) throws BusinessException {
		super.save(o);
	}

	public void update(CommonPhrase transientObject) throws BusinessException {
		super.update(transientObject);
	}
	
	/**
	 * 该方法只对集团版而言，每生成一个单位，复制一套常用语
	 *
	 */
	public void generateCommonPharse(long accountId){
		
		log.info("开始为新单位初始化常用语...");
		
		User user = CurrentUser.get();
		
		try{
		DetachedCriteria criteria = DetachedCriteria.forClass(CommonPhrase.class)
			.add(Expression.eq("accountId", V3xOrgEntity.VIRTUAL_ACCOUNT_ID))
			.add(Expression.eq("type", CommonPhrase.PHRASE_TYPE.system.ordinal()));
		
		List<CommonPhrase> phrasesList = (List<CommonPhrase>)this.executeCriteria(criteria, -1, -1);
		for(CommonPhrase phrase : phrasesList){
			CommonPhrase newPhrase = new CommonPhrase();
			newPhrase.setIdIfNew();
			newPhrase.setAccountId(accountId);
			newPhrase.setContent(phrase.getContent());
			newPhrase.setCreateDate(new Date());
			newPhrase.setMemberId(user.getId());
			newPhrase.setType(CommonPhrase.PHRASE_TYPE.system.ordinal());
			newPhrase.setUpdateDate(new Date());
			
			this.save(newPhrase);
		}
		}catch(Exception e){
			log.error("初始化常用语失败!",e);
		}
		
		log.info("成功为新单位初始化常用语!");
	}

}
