/**
 * 
 */
package com.seeyon.v3x.system.debug;

import java.io.Serializable;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-2-16
 */
public class InfoOpen implements Serializable {

	private static final long serialVersionUID = 1517510733267225367L;

	private boolean enabled = false;

	private String endTime = null;
	
	private String password = null;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
