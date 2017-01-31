package com.seeyon.v3x.mobile.adapter.cmpp3;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.huawei.insa2.comm.cmpp30.message.CMPP30SubmitMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30SubmitRepMessage;
import com.huawei.insa2.util.Args;
import com.seeyon.v3x.mobile.adapter.AdapterMobileMessageManger;
import com.seeyon.v3x.mobile.adapter.AdapterMobileWapPushManager;
import com.seeyon.v3x.mobile.message.domain.MobileReciver;
import com.seeyon.v3x.util.Strings;

/****
 * 中国移动CMPP3短信适配器实现类
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class AdapterMobileMessageMangerCMPP3Impl implements
        AdapterMobileMessageManger,AdapterMobileWapPushManager
{
    private static final Log log = LogFactory.getLog(AdapterMobileMessageMangerCMPP3Impl.class);

    /**
     * Socket连接配置
     */
    private String host; // 主机地址 
    private String port; // 端口号
    private String sourceAddr; // SP…ID (最大为六位字符)
    private String sharedSecret; // SP Password 
    private String version; // 版本号(大于0，小于256)
    private Integer heartbeatInterval; // 心跳信息发送间隔时间(单位：秒)
    private Integer reconnectInterval; // 连接中断时重连间隔时间(单位：秒)
    private Integer heartbeatNoresponseout; // 需要重连时，连续发出心跳而没有接收到响应的个数
    private Integer transactionTimeout; // 链接超时时间 (单位：秒)
    private boolean debug = false; // 是否调试
    
    /**
     * 发送短信配置
     */
    private int pkTotal = 1;            //相同msg_Id消息总条数
    private int pkNumber = 1;           //相同msg_Id的消息序号
    private int registeredDelivery = 0; //是否要求返回状态报告
    private int msgLevel = 1;           //信息级别
    private String serviceId = "";      //业务类型
    private int feeUserType = 2;        //计费用户类型字段---
    private String feeTerminalId = "";  //被计费用户的号码---用户的手机号
    private int feeTerminalType = 1;    //被计费用户的号码类型
    private int tpPid = 0;              //GSM协议类型： 为 1 长消息
    private int tpUdhi = 0;             //GSM协议类型： 为 1 长消息
    private int msgFmt = 15;            //消息格式
    private String msgSrc = "";         //消息内容来源
    private String feeType = "02";      //资费类别
    private String feeCode = "0";       //资费代码(以分为单位)
    private Date validTime = null;      //存活有效期
    private Date atTime = null;         //定时发送时间
    private String srcTerminalId = "";  //源终端号码，为SP的服务代码或前缀
    private int destTerminalType = 0;   //接收短信的用户号码类型
    private String linkID = "";         //点播业务使用的linkID
    
    private int wapTpPid = 0; // Wappush GSM协议类型
    private int wapTpUdhi = 1; // Wappush GSM协议类型
    private int wapMsgFmt = 4; // Wappush 消息格式
    
    //参数配置;
    private static Args args = null; 
    
    //短信代理
    private SeeyonSMProxy30 mySMProxy = null;
    
    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(Integer heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public Integer getHeartbeatNoresponseout() {
        return heartbeatNoresponseout;
    }

    public void setHeartbeatNoresponseout(Integer heartbeatNoresponseout) {
        this.heartbeatNoresponseout = heartbeatNoresponseout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Integer getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(Integer reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public Integer getTransactionTimeout() {
        return transactionTimeout;
    }

    public void setTransactionTimeout(Integer transactionTimeout) {
        this.transactionTimeout = transactionTimeout;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getDestTerminalType() {
        return destTerminalType;
    }

    public void setDestTerminalType(int destTerminalType) {
        this.destTerminalType = destTerminalType;
    }

    public String getFeeCode() {
        return feeCode;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public String getFeeTerminalId() {
        return feeTerminalId;
    }

    public void setFeeTerminalId(String feeTerminalId) {
        this.feeTerminalId = feeTerminalId;
    }

    public int getFeeTerminalType() {
        return feeTerminalType;
    }

    public void setFeeTerminalType(int feeTerminalType) {
        this.feeTerminalType = feeTerminalType;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public int getFeeUserType() {
        return feeUserType;
    }

    public void setFeeUserType(int feeUserType) {
        this.feeUserType = feeUserType;
    }

    public int getMsgLevel() {
        return msgLevel;
    }

    public void setMsgLevel(int msgLevel) {
        this.msgLevel = msgLevel;
    }

    public String getMsgSrc() {
        return msgSrc;
    }

    public void setMsgSrc(String msgSrc) {
        this.msgSrc = msgSrc;
    }

    public int getPkNumber() {
        return pkNumber;
    }

    public void setPkNumber(int pkNumber) {
        this.pkNumber = pkNumber;
    }

    public int getPkTotal() {
        return pkTotal;
    }

    public void setPkTotal(int pkTotal) {
        this.pkTotal = pkTotal;
    }

    public int getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(int registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getTpPid() {
        return tpPid;
    }

    public void setTpPid(int tpPid) {
        this.tpPid = tpPid;
    }

    public int getTpUdhi() {
        return tpUdhi;
    }

    public void setTpUdhi(int tpUdhi) {
        this.tpUdhi = tpUdhi;
    }

    public String getSrcTerminalId() {
        return srcTerminalId;
    }

    public void setSrcTerminalId(String srcTerminalId) {
        this.srcTerminalId = srcTerminalId;
    }
    
    public String getName() {
        return "中国移动CMPP3";
    }

    private void initSMProxy(){
        try{
            //SMProxy30 连接正常getConnState()返回null, 否则抛出异常
            if(mySMProxy == null || mySMProxy.getConnState() != null){
                if(args == null){
                    //导入配置参数
                    args = new Args();
                    args.set("host", host);
                    args.set("port", port);
                    args.set("source-addr", sourceAddr);
                    args.set("shared-secret", sharedSecret);
                    args.set("heartbeat-interval", heartbeatInterval);
                    args.set("reconnect-interval", reconnectInterval);
                    args.set("heartbeat-noresponseout", heartbeatNoresponseout);
                    args.set("transaction-timeout", transactionTimeout);
                    args.set("version", version);
                    args.set("debug", debug);
                }
                mySMProxy = new SeeyonSMProxy30(args);
                log.info("CMPP3短信代理连接成功.");
            }
        }catch(Exception e){
            /*
            int i = e.toString().indexOf(':');
            if(i != -1){
                log.warn("CMPP3短信代理连接失败, " + e.toString().substring(i+1));                
            }
            else{
            }*/
            log.warn("CMPP3短信代理连接失败." + e.toString());
        }
    }
    
    /**
     * CMPP3是否可用
     * SMProxy30 连接正常getConnState()返回null, 否则抛出异常
     */
    public boolean isAvailability() {
        
        if(Strings.isBlank(host) || Strings.isBlank(port) || Strings.isBlank(sourceAddr) || Strings.isBlank(sharedSecret)){
            return false;
        }
        
        initSMProxy();
        boolean isAvailable = false;
        try{
            isAvailable = (mySMProxy != null && mySMProxy.getConnState() == null);
        }
        catch(Exception e){
            isAvailable = false;
            log.warn("CMPP3短信代理查询连接状态异常.", e);
        }
        return isAvailable;
    }

    //支持群发
    public boolean isSupportQueueSend() {
        return true;
    }
    //支持上行
    public boolean isSupportRecive() {
        return true;
    }

    /**
     * 发送短信（单发）
     */
    public boolean sendMessage(Long messageId, String srcPhone,
            String destPhone, String content) {
        initSMProxy();
        //封装 CMPP30SubmitMessage
        String[] destTerminalId = {destPhone};  //接收者号码
        byte[] msgContent = content.getBytes(); //消息内容
        CMPP30SubmitMessage submitMsg = new CMPP30SubmitMessage(
                pkTotal,
                pkNumber,
                registeredDelivery,
                msgLevel,
                serviceId,
                feeUserType,
                feeTerminalId,
                feeTerminalType,
                tpPid,
                tpUdhi,
                msgFmt,
                msgSrc,
                feeType,
                feeCode,
                validTime,
                atTime,
                srcTerminalId,
                destTerminalId,
                destTerminalType,
                msgContent,
                linkID
           );
        //发送短信
        CMPP30SubmitRepMessage repMsg = null;
        if(mySMProxy != null){
            repMsg = mySMProxy.send(submitMsg);        
        }
        
        if(repMsg != null && repMsg.getResult()==0){
            //log.info("短信发送成功. 手机号码:[" + destPhone + "], 内容:" + content);
            return true;
        }
        else{
            log.warn("短信发送失败, 发起者号码:["+ srcPhone + "], 接收者号码:[" + destPhone + "]");
            return false;
        }
    }

    /**
     * 发送短信 (群发)
     */
    public boolean sendMessage(Long messageId, String srcPhone,
            Collection<String> destPhone, String content) {
        
        initSMProxy();
        
        //封装 CMPP30SubmitMessage
        String[] destTerminalId = (String[]) destPhone.toArray(new String[destPhone.size()]);    //接收短信的MSISDN号码
        byte[] msgContent = content.getBytes();    //消息内容
        CMPP30SubmitMessage submitMsg = new CMPP30SubmitMessage(
                pkTotal,
                pkNumber,
                registeredDelivery,
                msgLevel,
                serviceId,
                feeUserType,
                feeTerminalId,
                feeTerminalType,
                tpPid,
                tpUdhi,
                msgFmt,
                msgSrc,
                feeType,
                feeCode,
                validTime,
                atTime,
                srcTerminalId,
                destTerminalId,
                destTerminalType,
                msgContent,
                linkID
           );
        
        //群发短信
        CMPP30SubmitRepMessage repMsg = null;
        if(mySMProxy != null){
            repMsg = mySMProxy.send(submitMsg);
        }
        
        if(repMsg != null && repMsg.getResult()==0){
            //log.info("短信发送成功. 手机号码:" + destPhone.toString() + ", 内容:" + content);
            return true;
        }
        else{
            log.warn("短信发送失败, 发起者号码:["+ srcPhone + "], 接收者号码:" + destPhone.toString());
            return false;
        }
    }
    

    /**
     * 接收消息
     */
    public List<MobileReciver> recive() {
        initSMProxy();
        List<MobileReciver> result = null;
        try{
            result = mySMProxy.getReciverMsg();
        }
        catch(Exception e){
            result = null;
            log.warn("CMPP3接收短信异常:", e);
        }
        
        /*
        if(result != null && !result.isEmpty()){
            for(MobileReciver o : result){
                log.info("成功接收到短信, " + "手机号码:[" + o.getSrcPhone() + "], 内容:" + o.getContent());
            }
        }*/
        
        return result;
    }

    /**
     * 发送wappush短信
     */
    public boolean sendMessage(int messageId, String srcPhone, String destPhone, String content, String wappushURL) {
        initSMProxy();
        //组装Wappush短信内容
        byte[] msgContent = assembleMsgContent(content, wappushURL);
        String[] destTerminalId = {destPhone};  //接收者号码
        CMPP30SubmitMessage submitMsg = new CMPP30SubmitMessage(
                pkTotal,
                pkNumber,
                registeredDelivery,
                msgLevel,
                serviceId,
                feeUserType,
                feeTerminalId,
                feeTerminalType,
                wapTpPid,
                wapTpUdhi,
                wapMsgFmt,
                msgSrc,
                feeType,
                feeCode,
                validTime,
                atTime,
                srcTerminalId,
                destTerminalId,
                destTerminalType,
                msgContent,
                linkID
           );
        //发送短信
        CMPP30SubmitRepMessage repMsg = null;
        if(mySMProxy != null){
            repMsg = mySMProxy.send(submitMsg);        
        }
        
        if(repMsg != null && repMsg.getResult()==0){
            //log.info("Wappush短信发送成功. 手机号码:[" + destPhone + "], 内容:" + content + ",地址:" + wappushURL);
            return true;
        }
        else{
            log.warn("WapPush短信发送失败, 发起者号码:["+ srcPhone + "], 接收号码:[" + destPhone + "],URL:" + wappushURL);
            return false;
        }
    }

    /**
     * 群发wappush短信
     */
    public boolean sendMessage(int messageId, String srcPhone, Collection<String> destPhone, String content, String wappushURL) {
        initSMProxy();
        //组装Wappush短信内容
        byte[] msgContent = assembleMsgContent(content, wappushURL);
        String[] destTerminalId = (String[]) destPhone.toArray(new String[destPhone.size()]);    //接收短信的MSISDN号码
        CMPP30SubmitMessage submitMsg = new CMPP30SubmitMessage(
                pkTotal,
                pkNumber,
                registeredDelivery,
                msgLevel,
                serviceId,
                feeUserType,
                feeTerminalId,
                feeTerminalType,
                wapTpPid,
                wapTpUdhi,
                wapMsgFmt,
                msgSrc,
                feeType,
                feeCode,
                validTime,
                atTime,
                srcTerminalId,
                destTerminalId,
                destTerminalType,
                msgContent,
                linkID
           );
        //发送短信
        CMPP30SubmitRepMessage repMsg = null;
        if(mySMProxy != null){
            repMsg = mySMProxy.send(submitMsg);        
        }
        
        if(repMsg != null && repMsg.getResult()==0){
            //log.info("Wappush短信发送成功. 手机号码:[" + destPhone + "], 内容:" + content + ",地址:" + wappushURL);
            return true;
        }
        else{
            log.warn("WapPush短信发送失败, 发起者号码:["+ srcPhone + "], 接收号码:" + destPhone.toString() + ",URL:" + wappushURL);
            return false;
        }
    }
    
    /**
     * 封装Wappush短信内容
     */
    private byte[] assembleMsgContent(String content, String wappushURL){
        
        if(wappushURL.toLowerCase().startsWith("http://")){
            wappushURL = wappushURL.substring(7);
        }
        if(wappushURL.toLowerCase().startsWith("https://")){
            wappushURL = wappushURL.substring(8);
        }
        //wappush短信内容请求头
        byte[] head = hexToBytes("0605040B8423F0DC0601AE02056A0045C60C03");
        byte[] ubody = wappushURL.getBytes();
        byte[] tbody = null;
        try {
            tbody = content.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            tbody = content.getBytes();
        }
        //消息内容
        byte[] msgContent = new byte[head.length + ubody.length + tbody.length + 6];
        System.arraycopy(head, 0, msgContent, 0, head.length);
        System.arraycopy(ubody, 0, msgContent, head.length, ubody.length);
        msgContent[head.length + ubody.length] = 0x00;
        msgContent[head.length + ubody.length + 1] = 0x01;
        msgContent[head.length + ubody.length + 2] = 0x03;
        System.arraycopy(tbody, 0, msgContent, head.length + ubody.length + 3, tbody.length);
        msgContent[head.length + ubody.length + tbody.length + 3] = 0x00;
        msgContent[head.length + ubody.length + tbody.length + 4] = 0x01;
        msgContent[head.length + ubody.length + tbody.length + 5] = 0x01;

        return msgContent;
    }

    /**
     * 十六进制转换为二进制
     */
    private static byte[] hexToBytes(String str) {
        if(str == null || str.length() < 2){
           return null;
        }
        else{
           int len = str.length()/2;
           byte[] buffer = new byte[len];
           for(int i=0; i<len; i++){
               buffer[i] = (byte) Integer.parseInt(str.substring(i*2, i*2+2),16);
           }
           return buffer;
        }
    }
    
    public boolean isSupportSplit(){
    	return false;
    }
}