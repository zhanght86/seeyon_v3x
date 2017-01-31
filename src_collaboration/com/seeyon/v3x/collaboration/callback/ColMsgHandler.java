package com.seeyon.v3x.collaboration.callback;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.callback.CallbackHandler;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;

/**
 * @deprecated
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-9-18
 */
public class ColMsgHandler extends CallbackHandler {
    public static final Logger logger = Logger.getLogger(ColMsgHandler.class);

    ColMsgHandler() {
		registerMyself();
	}

	@Override
	/*
	 * 参数1--caseiId, 2--senderId, 3--一个包含所有receiverId的字符串，这个每个参数用‘；’分割；
	 */
	public void invoke(String... args) {

		UserMessageManager userManager = (UserMessageManager) ApplicationContextHolder
				.getBean("UserMessageManager");
        AffairManager affairManager = (AffairManager) ApplicationContextHolder.getBean("affairManager");

        ColSummary col = null;
        try {
            ColManager colManager = (ColManager) ApplicationContextHolder
                        .getBean("colManager");
            col = colManager.getSummaryByCaseId(Integer.valueOf(
					args[0]).intValue());
        } catch (ColException e) {
        	logger.error(e.getMessage(),e);
        }

        if(col == null){
            logger.error("col == null!");
            return;
        }

        String senderName = "";
        String[] receivers = args[2].split(";");
        List<MessageReceiver> receiver_list = new ArrayList<MessageReceiver>();
        for(int i=0;i<receivers.length;i++){
            Long receiverId = new Long(receivers[i]);
            Affair affair = affairManager.getLatestAffairBySummaryAndMember(col.getId(),receiverId);
            MessageReceiver receiver = null;
            if(affair == null){
            	receiver = new MessageReceiver(null, receiverId);
            }else{
                receiver = new MessageReceiver(affair.getId(), receiverId,"message.link.col.pending",affair.getId().toString());
            }
            receiver_list.add(receiver);
        }
        try {

			if( col != null)
//				userManager.sendUserMessage(col.getSubject(),
//					Constants.MESSAGE_TYPE_COLLABORATION, args[1], receivers);
            userManager.sendSystemMessage(new MessageContent("col.send",col.getSubject(),senderName, 0), ApplicationCategoryEnum.collaboration,
					new Long(args[1]), receiver_list);
			else //TODO!!! 删除这个操作。
// 				userManager.sendUserMessage("待实现（更改生成流程的顺序）。流程ID: " + args[0],
//					Constants.MESSAGE_TYPE_COLLABORATION, args[1], receivers);
            userManager.sendSystemMessage(new MessageContent("待实现（更改生成流程的顺序）。流程ID: " + args[0]),
					ApplicationCategoryEnum.collaboration, new Long(args[1]), receiver_list);
			} catch (NumberFormatException e) {
				logger.error(e.getMessage(),e);
		} catch (MessageException e) {
			logger.error(e.getMessage(),e);
		}

	}

	@Override
	protected void registerMyself() {
		registerCallbackHandler(CallbackHandler.CALLBACK_COLLABORATION_SENDMSG,
				this);
	}

}
