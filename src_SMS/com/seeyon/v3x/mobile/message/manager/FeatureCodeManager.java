package com.seeyon.v3x.mobile.message.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.util.Strings;


/**
 * 特征号生成器
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-3
 */
public final class FeatureCodeManager {
	private static final Log log = LogFactory.getLog(FeatureCodeManager.class);
	private char[] x = {'2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z',};
	
	/**
	 * 分别是特征的每一个数字在数组中的位置，从个位开始
	 */
	private int[] currentIndexs = null;
	
	
	
	private int length = 4;
	
	public void setLength(int length) {
		this.length = Math.max(length, this.length);
	}

	public void init(String initFeatureCode, int step){
		if(Strings.isNotBlank(initFeatureCode) && initFeatureCode.length() > length){ //特征码的长度缩小了，不允许
			length = initFeatureCode.length();
			log.warn("特征码的长度不允许缩小, 恢复到" + length + "位");
		}
		
		currentIndexs = new int[this.length];
		for (int i = 0; i < this.length; i++) {
			currentIndexs[i] = 0;
		}
		
		if(Strings.isNotBlank(initFeatureCode)){
			for (int i = initFeatureCode.length() - 1; i >= 0; i--) {
				currentIndexs[i] = indexOf(initFeatureCode.charAt(i));
			}
			
			increase(3, step);
		}
	}
	
	private int indexOf(char c){
		for (int i = 0; i < x.length; i++) {
			if(x[i] == c){
				return i;
			}
		}
		
		return 0;
	}

	private void increase(int index, int step){
		if(index > this.length || step < 1){
			return;
		}
		
		int up = (currentIndexs[index] + step) / x.length;
		currentIndexs[index] = (currentIndexs[index] + step) % x.length;
		
		if(up > 0){
			increase(index -1, up);
		}
	}
	
	/**
	 * 增加指定的长度
	 * 
	 * @param step
	 * @return
	 */
	public synchronized String next(int step){
		char[] r = new char[this.length];
		for (int i = 0; i < this.length; i++) {
			if(i==this.length-1){
				if(currentIndexs[i]==31){
					r[i] = x[0];
				}else{
					r[i] = x[currentIndexs[i]+1];
				}
			}else{
				r[i] = x[currentIndexs[i]];
			}
			
		}
		
		String re = new String(r);
		
		increase(3, step);
		
		return re;
	}
	
	/**
	 * 增1
	 * 
	 * @return
	 */
	public String next(){
		return next(1);
	}
	public int[] getCurrentIndexs() {
		return currentIndexs;
	}
}
