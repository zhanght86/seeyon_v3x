package com.seeyon.v3x.messageManager.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.messageManager.Constant;
import com.seeyon.v3x.messageManager.dao.MessageDelsetDao;
import com.seeyon.v3x.messageManager.domain.MessageDelset;

/**
 * 
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-11-26
 */
public class MessageDelsetManagerImpl implements MessageDelsetManager {
	
	private static final Log log = LogFactory.getLog(MessageDelsetManagerImpl.class);

	private MessageDelsetDao messageDelsetDao;
	
	public void init(){
		MessageDelset messageDelset = messageDelsetDao.get();
		if(messageDelset == null || messageDelset.getMessageCount() < 1 || messageDelset.getMessageDay() < 1){
			updateMessageDelset(50, 30);
		}
	}
	
	public MessageDelset getMessageDelset() {
		return messageDelsetDao.get();
	}

	public void setMessageDelsetDao(MessageDelsetDao messageDelsetDao) {
		this.messageDelsetDao = messageDelsetDao;
	}

	public void updateMessageDelset(int count, int day) {
		//更新设置
		MessageDelset messageDelset = messageDelsetDao.get();
		
		if(messageDelset == null){
			messageDelset = new MessageDelset();
			messageDelset.setId(1L);
		}
		
		messageDelset.setMessageCount(count);
		messageDelset.setMessageDay(day);
		messageDelset.setStatus(Constant.Message_DELSET.ALL.ordinal());
		
		messageDelsetDao.update(messageDelset);
	}
	
}
