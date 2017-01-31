package com.seeyon.v3x.system.debug;

public interface InfoOpenManager {

	public InfoOpen get();

	public void save(InfoOpen infoOpen);

	/**
	 * 判断当前是否可以使用：开启调试，并且没有到期
	 * @param remoteAddress
	 * @return
	 */
	public boolean isAccess(String remoteAddress, String password);
}