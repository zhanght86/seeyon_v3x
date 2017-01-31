package com.seeyon.v3x.mobile.adapter.jindi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.JdbcAccessDataException;
import com.seeyon.v3x.mobile.adapter.AdapterMobileMessageManger;
import com.seeyon.v3x.mobile.adapter.jindi.dao.AdapterMobileJinDiDao;
import com.seeyon.v3x.mobile.adapter.jindi.domain.SendTask;
import com.seeyon.v3x.mobile.message.domain.MobileReciver;
import com.seeyon.v3x.util.Strings;

public class AdaptMobileJindiModemPoolImpl extends BaseHibernateDao implements AdapterMobileMessageManger {
	private static Log log = LogFactory.getLog(AdaptMobileJindiModemPoolImpl.class);
	private String comNum;
	
	public String getComNum() {
		return comNum;
	}
	public void setComNum(String comNum) {
		this.comNum = comNum;
	}
	
	private long maxNum = 0;
	
	private boolean needSetId = false;
	
	private List<String> needSetIdSQL ;
	
	private AdapterMobileJinDiDao adapterMobileJinDiDao;
	
	private Queue<Long> gateWayIds = new LinkedList<Long>();
	

	public String getName() {
		return "金迪MODEM池";
	}

	/**
	 * 用户填写的端口号，为连续用逗号隔开的数字，例如：3,4,5,6 其他格式的一律不对
	 * 
	 */
	public boolean isAvailability() {
		if(comNum!=null && Strings.isNotBlank(comNum)){
			String[] s = comNum.split(",");
			for(String str : s){
				Pattern   pattern   =   Pattern.compile("[0-9]{1,}");//建立正则表达式
				Matcher   matcher   =   pattern.matcher(str);
				if(!matcher.matches()){
					return false;
				}else{
					continue;
				}
			}
			return true;
		}else{
			return false;
		}
	}

	public boolean isSupportQueueSend() {
		return true;
	}

	public boolean isSupportRecive() {
		String isSupportRecive = SystemProperties.getInstance().getProperty("mobile.jindi.isSupportRecive", "true");
		return BooleanUtils.toBoolean(isSupportRecive);
	}

	public List<MobileReciver> recive() {
		try {
			List<Long> messageId = new ArrayList<Long>();
			List<MobileReciver> result = new ArrayList<MobileReciver>();
			List<Object[]> messages = adapterMobileJinDiDao.findReceiveMes();
			for(Object[] obs : messages){
				Long id = Long.valueOf(obs[0].toString());
				messageId.add(id);
				if(obs[1] != null && obs[2] != null){
					String srcNum = obs[1].toString();
					String content = obs[2].toString();
					MobileReciver receiver = new MobileReciver();
					receiver.setSrcPhone(srcNum.substring(2));
					receiver.setContent(content);
					result.add(receiver);
				}
			}
			if(!messageId.isEmpty()){
				adapterMobileJinDiDao.setReceiveReaded(messageId);
			}
			return result;
		} catch (JdbcAccessDataException e) {
			log.warn("从金迪中取消息发生异常"+e);
		}
		return null;
	}
	
	public void init(){
	if(isAvailability()){
		log.info("金笛插件开始初始化...");
		if(comNum!=null && comNum.length()!=0){
			String[] ids = comNum.split(",");
			for(String id : ids){
				Long iId = Long.valueOf(id);
				if(!gateWayIds.contains(iId)){
					gateWayIds.add(iId);
				}
			}
		}
		for(Long id : gateWayIds){
			log.info(id);
		}
		getMaxNum();
		if(needSetIdSQL != null){
			String sqlDialect = SystemProperties.getInstance().getProperty("workflow.dialect");
			for(String sql :needSetIdSQL){
				if(sqlDialect.equals(sql)){
					needSetId = true;
				}
			}
		}
		//log.info("maxNum:"+maxNum);
		log.info("金笛插件初始化结束.");
		}
	}
	
	public boolean sendMessage(Long messageId, String srcPhone, String destPhone, String content){
		Long gwId = gateWayIds.poll();
		try{
			Long taskId = ++maxNum;
			log.info("发送内容到手机号："+destPhone+",通过COM:"+gwId+"发出.TaskId为：" + taskId);
			SendTask st = new SendTask();
			st.setTaskId(taskId);
			st.setDestNumber(destPhone);
			st.setContent(content);
			st.setMsgTyep(0l);
			st.setSendFlag(0l);
			st.setCommPort(gwId);
			adapterMobileJinDiDao.saveSendTask(needSetId, st);
		}catch(Exception e){
			log.error("", e);
		}finally{
			((LinkedList<Long>) gateWayIds).addLast(gwId);
		}
		
		
		return true;
	}
	
	public boolean sendMessage(Long messageId, String srcPhone, Collection<String> destPhone, String content){
		for (String phone : destPhone) {
			sendMessage(messageId, srcPhone, phone, content);
		}
		
		return true;
	}
	
	public void  getMaxNum(){
		try{
			List size = this.getHibernateTemplate().find("select max(taskId) from SendTask");//select max(TaskID) from t_sendtask
			if(size != null && !size.isEmpty()){
				maxNum = size.get(0) == null ? 0:(Long)size.get(0);
			}
		}
		catch(Exception e){
			//第一次启动报错，隐蔽掉。
			maxNum = 0;
			log.warn("查询金迪发送短信最大id", e);
		}
			//maxNum = Long.valueOf(Integer.toString(size));
	}
	public void setAdapterMobileJinDiDao(AdapterMobileJinDiDao adapterMobileJinDiDao) {
		this.adapterMobileJinDiDao = adapterMobileJinDiDao;
	}
	public void setNeedSetIdSQL(List<String> needSetIdSQL) {
		this.needSetIdSQL = needSetIdSQL;
	}
	
	public boolean isSupportSplit(){
    	return true;
    }
}
