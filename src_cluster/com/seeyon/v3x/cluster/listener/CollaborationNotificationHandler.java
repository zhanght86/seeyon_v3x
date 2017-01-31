package com.seeyon.v3x.cluster.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.collaboration.domain.MessageData;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColLock;
import com.seeyon.v3x.collaboration.webmodel.ColLockModel;
import com.seeyon.v3x.common.processlog.domain.ProcessLog;
import com.seeyon.v3x.common.security.AccessControlBean;
import com.seeyon.v3x.util.annotation.HandleNotification;

/**
 * 协同 - 集群通知监听处理
 * @author Mazc
 * 2010-03-02
 */
public class CollaborationNotificationHandler {
	protected static final Log log = LogFactory.getLog(CollaborationNotificationHandler.class);
	
	@HandleNotification (type=NotificationType.Collaboration_CheckLock)
	public void checkLock(Object o){
		if(o instanceof ColLockModel){
			try{
				long summaryId = ((ColLockModel) o).getSummaryId(); 
				long memberId = ((ColLockModel) o).getMemberId(); 
				String memberName = ((ColLockModel) o).getMemberName();
				ColLock.COL_ACTION currentAction = ((ColLockModel) o).getAction();
				ColLock.getInstance().checkCanAction(summaryId, memberId, memberName, currentAction);
			}
			catch(Exception e){
				log.error("集群-更新协同锁异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-更新协同锁]");
		}
	}
	
	@HandleNotification (type=NotificationType.Collaboration_RemoveLock)
	public void removeLock(Object o){
		if(o instanceof Long){
			try{
				ColLock.getInstance().removeLock((Long)o);
			}
			catch(Exception e){
				log.error("集群-删除协同锁异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-删除协同锁]");
		}
	}
	
	@HandleNotification (type=NotificationType.Collaboration_UpdateMessageData_Put)
	public void updateMessageDataMap(Object o){
		if(o instanceof MessageData){
			try{
				Long summaryId = ((MessageData)o).getSummary().getId();
				List<MessageData> messageDataList = ColHelper.messageDataMap.get(summaryId);
		        if(messageDataList == null){
		        	 messageDataList = new ArrayList<MessageData>();
		        }
		        messageDataList.add((MessageData)o);
		        ColHelper.messageDataMap.put(summaryId, messageDataList);
			}
			catch(Exception e){
				log.error("集群-更新协同MessageData异常", e);
			}
		}
	}
	
	@HandleNotification (type=NotificationType.Collaboration_UpdateMessageData_Remove)
	public void removeMessageDataMap(Object o){
		if(o instanceof Long){
			try{
				ColHelper.messageDataMap.remove((Long)o);
			}
			catch(Exception e){
				log.error("集群-删除messageData值报错", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-删除messageData]");
		}
	}
	
	@HandleNotification (type=NotificationType.Collaboration_UpdateProcessLog_Put)
	public void updateProcessLogMap(Object o){
		if(o instanceof ArrayList){
			try{
				ProcessLog log = (ProcessLog)((ArrayList<ProcessLog>)o).get(0);
				if(log != null){
					Long processId = log.getProcessId();
					ColHelper.processLogMetaMap.put(processId, (ArrayList<ProcessLog>)o);
				}
			}
			catch(Exception e){
				log.error("集群-更新ProcessLogMap报错", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-更新processLogMap]");
		}
	}
	
	@HandleNotification (type=NotificationType.Collaboration_UpdateProcessLog_Remove)
	public void removeProcessLogMap(Object o){
		if(o instanceof Long){
			try{
				ColHelper.processLogMetaMap.remove((Long)o);
			}
			catch(Exception e){
				log.error("集群-删除processLogMap值报错", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-删除processLogMap]");
		}
	}
	
	@HandleNotification (type=NotificationType.Coll_AccessControl_updateAccessCache) 
	public void updateAccessCache(Object o){
		if(o instanceof String){
			try{
				AccessControlBean.getInstance().addAccessCacheValue(String.valueOf(o));
			}
			catch(Exception e){
				log.error("集群-删除关联文档缓存的前一Summary报错", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-删除关联文档缓存的前一Summary报错]");
		}
	}
}
