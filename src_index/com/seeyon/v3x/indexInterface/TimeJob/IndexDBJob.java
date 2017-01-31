package com.seeyon.v3x.indexInterface.TimeJob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.indexInterface.ProxyManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;


/**
 * @author zhangyong
 *
 */
public class IndexDBJob implements Runnable {
  private int intervalTime;
  private ProxyManager  proxyMgr;
  private UpdateIndexManager updateIndexManager;
  private final static Log log = LogFactory.getLog(IndexDBJob.class);
  public IndexDBJob(UpdateIndexManager updateIndexManager,int intervalTime)
  {
	  this.proxyMgr=updateIndexManager.getIndexManager();
	  this.intervalTime=intervalTime;
	  this.updateIndexManager=updateIndexManager;
  }
	public void run() {
		try{
		updateIndexManager.resumeDBIndexInfo();
		log.info(this.getClass().getName()+" 全文检索DB操作线程启动");
			while (true) {
				proxyMgr.getAllNeedDeleteList();
				try {
					Thread.sleep(intervalTime * 10000);
				} catch (Throwable e) {
					log.error("", e);
				}

			}
		}catch(Throwable e){
			log.error("parse error!",e);
		}
	}

}
