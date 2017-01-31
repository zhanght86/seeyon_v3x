package com.seeyon.v3x.taskmanage.utils;

/**
 * 用户对某一任务所有的角色类型
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-19
 */
public class UserRoles {

	private boolean creator;
	private boolean manager;
	private boolean participator;
	
	/**
	 * <pre>
	 * 黄霑，人称霑叔以示敬意，英文名<b>James Wong</b>
	 * 纵横华语乐坛数十载，列香江四大才子之一
	 * 平生洒脱无羁，狄龙称之"亦侠亦狂亦不文"
	 * 此方法名用做表明菜鸟杨对这位率性鬼魅不文霑的喜爱
	 * 为他的歌、为他的狂、为他的诗、为他的笑
	 * 为他无论走到哪里，都好像漆黑夜中的萤火虫一般...
	 * 一个死去的怪才，自然与现世的任何事务无关
	 * 若你不幸是<b>James Wong</b>，那么这个任务自然也与你无关
	 * 如此，兄弟，你当：焚身以火，沧海一声笑，开心做出戏，两忘烟水里...
	 * 打打酱油，何尝不是美事，抽身事外，自可不亦乐乎...
	 * </pre>
	 */
	public boolean isJamesWong() {
		return !this.creator && !this.manager && !this.participator;
	}
	
	/**
	 * 是否具备修改任务权限
	 */
	public boolean canEdit() {
		return this.creator || this.manager || this.participator;
	}
	
	/*-------------------------setter/getter------------------------*/
	public boolean isCreator() {
		return creator;
	}
	public void setCreator(boolean creator) {
		this.creator = creator;
	}
	public boolean isManager() {
		return manager;
	}
	public void setManager(boolean manager) {
		this.manager = manager;
	}
	public boolean isParticipator() {
		return participator;
	}
	public void setParticipator(boolean participator) {
		this.participator = participator;
	}
}
