package com.seeyon.v3x.mobile.adapter.cmpp3;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30DeliverMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30SubmitMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30SubmitRepMessage;
import com.huawei.insa2.util.Args;
import com.huawei.smproxy.SMProxy30;
import com.seeyon.v3x.mobile.message.domain.MobileReciver;

/**
 * 继承SMProxy30类，实现短信收发
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class SeeyonSMProxy30 extends SMProxy30 
{
    private static final Log log = LogFactory.getLog(SeeyonSMProxy30.class);
    
    private List<MobileReciver> reciverMsgList = new ArrayList<MobileReciver>();

    /**
     * 初始化方法
     * @param arg0
     */
    public SeeyonSMProxy30(Args arg0) {
        super(arg0);
    }
    
    /**
     * 断开连接前的处理
     */
    public void onTerminate(){
        log.info("CMPP3连接已关闭.");
    }
    
    /**
     * 发送短信
     * @param messageId
     * @param srcPhone
     * @param destPhone
     * @param content
     * @return
     */
    public CMPP30SubmitRepMessage send(CMPP30SubmitMessage submitMsg){
        
        CMPP30SubmitRepMessage submitRepMsg = null;
        try {
            submitRepMsg = (CMPP30SubmitRepMessage) super.send(submitMsg);
        }
        catch (Exception e) {
            log.error("CMPP3发送短信异常:", e);
        }
        return submitRepMsg;
    }
    
    /**
     * 重载父类SMProxy30的消息接收<br>
     * 将处理到的消息转换为MobileReciver对象，添加到List中
     * @param CMPP30DeliverMessage
     * @return 
     */
    public CMPPMessage onDeliver(CMPP30DeliverMessage msg){
        //处理接收到的消息
        if(msg!= null && msg.getRegisteredDeliver() != 1){
            try{
                MobileReciver result = new MobileReciver();
                String srcPhone = new String(msg.getSrcterminalId());
                String content = new String(msg.getMsgContent());
                if(msg.getMsgFmt() == 8){
                    content = new String(msg.getMsgContent(),"UnicodeBigUnmarked");
                }
                //TODO 取到的手机号码格式是 "13XXXXXXXXX 0 00 00 0c 00 00 00" 为什么?
                //处理手机号码
                if(srcPhone.indexOf("86") == 0){
                    srcPhone = srcPhone.substring(2);
                }
                else if(srcPhone.indexOf("+86") == 0){
                    srcPhone = srcPhone.substring(3);
                }
                if(srcPhone.length() > 11){
                    srcPhone = srcPhone.substring(0, 11);
                }
                result.setSrcPhone(srcPhone);
                result.setContent(content);
                reciverMsgList.add(result);
            }
            catch(Exception e){
                log.error("CMPP3处理接收到的短信异常:", e);
                return null;
            }
        }
        
        try {
            return super.onDeliver(msg);
        }
        catch (Exception e) {
            log.error("CMPP3接收短信异常:", e);
            return null;
        }
    }
    
    /**
     * 将接收到的数据转交给A8的短信接收线程<br>
     * 同时重置List重新接收
     * @return
     */
    public List<MobileReciver> getReciverMsg(){
        List<MobileReciver> result = new ArrayList<MobileReciver>();
        result.addAll(reciverMsgList);
        reciverMsgList.clear();
        return result;
    }

}
