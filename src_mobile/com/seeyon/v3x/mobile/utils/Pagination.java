package com.seeyon.v3x.mobile.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.peoplerelate.RelationType;

//分页功能
public class Pagination {
	private List<V3xOrgMember> list;

	private List<V3xOrgDepartment> listDepartment;

	private List<OnlineUser> listOnlineUser;

	private List<Attachment> listAttachment;

	private Map<Long, List<ColComment>> mapColComment;

	private List<ColOpinion> opinions;

	private static String content;

	private List<Object[]> objectList;

	private List<Long> listlong;

	private Map<RelationType, List<V3xOrgMember>> map;
	
	private SortedSet<String> formSet;

	/**
	 * 关联人员中的领导
	 */
	private List<V3xOrgMember> leaders;

	/**
	 * 关联人员中的助手/秘书
	 */
	private List<V3xOrgMember> assistant;

	/**
	 * 关联人员中的下级
	 */
	private List<V3xOrgMember> junior;

	/**
	 * 关联人员中的同事
	 */
	private List<V3xOrgMember> confrere;

	private String content_opinion_1 = "";

	private String content_opinion_2 = "";

	public String getContent() {
		return content;
	}

	@SuppressWarnings("static-access")
	public void setContent(String content) {
		this.content = content;
	}

	public Map<Long, List<ColComment>> getMapColComment() {
		return mapColComment;
	}

	public void setMapColComment(Map<Long, List<ColComment>> mapColComment) {
		this.mapColComment = mapColComment;
	}

	public List<ColOpinion> getOpinions() {
		return opinions;
	}

	public void setOpinions(List<ColOpinion> opinions) {
		this.opinions = opinions;
	}

	public List<OnlineUser> getListOnlineUser() {
		return listOnlineUser;
	}

	public void setListOnlineUser(List<OnlineUser> listOnlineUser) {
		this.listOnlineUser = listOnlineUser;
	}

	public List<V3xOrgDepartment> getListDepartment() {
		return listDepartment;
	}

	public void setListDepartment(List<V3xOrgDepartment> listDepartment) {
		this.listDepartment = listDepartment;
	}

	public List<V3xOrgMember> getList() {
		return list;
	}

	public void setList(List<V3xOrgMember> list) {
		this.list = list;
	}

	public List<Attachment> getListAttachment() {
		return listAttachment;
	}

	public void setListAttachment(List<Attachment> listAttachment) {
		this.listAttachment = listAttachment;
	}

	public List<V3xOrgMember> getAssistant() {
		return assistant;
	}

	public void setAssistant(List<V3xOrgMember> assistant) {
		this.assistant = assistant;
	}

	public List<V3xOrgMember> getConfrere() {
		return confrere;
	}

	public void setConfrere(List<V3xOrgMember> confrere) {
		this.confrere = confrere;
	}

	public List<V3xOrgMember> getJunior() {
		return junior;
	}

	public void setJunior(List<V3xOrgMember> junior) {
		this.junior = junior;
	}

	public List<V3xOrgMember> getLeaders() {
		return leaders;
	}

	public void setLeaders(List<V3xOrgMember> leaders) {
		this.leaders = leaders;
	}

	public List<Object[]> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<Object[]> objectList) {
		this.objectList = objectList;
	}

	public List<Long> getListlong() {
		return listlong;
	}

	public void setListlong(List<Long> listlong) {
		this.listlong = listlong;
	}

	public SortedSet<String> getFormSet() {
		return formSet;
	}

	public void setFormSet(SortedSet<String> formSet) {
		this.formSet = formSet;
	}
	
	public List<V3xOrgMember> paginationMember(int pagecurrent, int num,
			List<V3xOrgMember> currentList) {
		// pagecount总共的页数
		// pagecurrent当前是第几页
		// num为该页除子部门外，填补部门人员的个数
		if(num<=0) return currentList;
		//得到子部门的个数
		 int departSize = listDepartment == null ? 0 : listDepartment.size() % MobileConstants.PAGE_COUNTER;
	        if(list != null)
	        {
	            int number = list.size();
	            if(num >= number && departSize == 0)
	                currentList = list.subList(0, number);
	            else
	            if(number > 0 && num < number)
	            {
	                int pagecount = getPageCount(departSize + number);
	                if(pagecurrent == 1)
	                    currentList = list.subList(0, num);
	                else
	                if(pagecurrent == pagecount)
	                    currentList = list.subList((num - departSize) + (pagecurrent - 2) * MobileConstants.PAGE_COUNTER, number);
	                else
	                    currentList = list.subList((num - departSize) + (pagecurrent - 2) * MobileConstants.PAGE_COUNTER, (num - departSize) + (pagecurrent - 1) * MobileConstants.PAGE_COUNTER);
	            } else
	            {
	                int pageDepart = getPageCount(departSize);
	                if(pagecurrent >= pageDepart)
	                {
	                    int index = num;
	                    int toIndex = index + MobileConstants.PAGE_COUNTER;
	                    return list.subList(index >= list.size() ? 0 : index, toIndex >= list.size() ? list.size() : toIndex);
	                }
	                if(pagecurrent < pageDepart)
	                    return null;
	            }
	        }
	        return currentList;
	}

	public List<V3xOrgDepartment> paginationDepartment(int pagecurrent,List<V3xOrgDepartment> currentList) {
		
		if (listDepartment != null) {
			
			int pagecount = getPageCount(listDepartment.size());
			
			if (pagecount == 1) {
				if(pagecurrent ==1){
					currentList = listDepartment.subList(0, listDepartment.size());
				}else{
					return null;
				}
			} else {
				if (pagecount > 1) {
					if (pagecurrent == pagecount) {
						currentList = listDepartment.subList(MobileConstants.PAGE_COUNTER * (pagecurrent - 1),listDepartment.size());
					} else {
						currentList = listDepartment.subList(MobileConstants.PAGE_COUNTER * (pagecurrent - 1),MobileConstants.PAGE_COUNTER * pagecurrent);
					}
				}
			}
		}
		return currentList;
	}

	public List<OnlineUser> paginationOnLineUser(int pagecurrent, int num,
			List<OnlineUser> currentList) {
		// pagecount总共的页数
		// pagecurrent当前是第几页
		// num为该页除子部门外，填补部门人员的个数
		int pagecount = getPageCount(listOnlineUser.size() - num);

		if (num == listOnlineUser.size() || num > listOnlineUser.size()) {
			currentList = listOnlineUser.subList(0, listOnlineUser.size());
		}
		if (listOnlineUser.size() != 0 && num < listOnlineUser.size()) {
			if (pagecurrent == 1) {
				currentList = listOnlineUser.subList(0, num);
			} else {
				if (pagecurrent == pagecount + 1) {
					currentList = listOnlineUser.subList(num + (pagecurrent - 2) * MobileConstants.PAGE_COUNTER,listOnlineUser.size());
				} else {
					currentList = listOnlineUser.subList(num + (pagecurrent - 2) * MobileConstants.PAGE_COUNTER,num + (pagecurrent - 1) * MobileConstants.PAGE_COUNTER);
				}
			}
		}
		return currentList;
	}

	public List<Attachment> paginationAttachments(int pagecurrent, int num,
			List<Attachment> currentList) {
		int pagecount = getPageCount(listAttachment.size() - num);

		if (num == listAttachment.size() || num > listAttachment.size()) {
			currentList = listAttachment.subList(0, listAttachment.size());
		}
		if (listAttachment.size() != 0 && num < listAttachment.size()) {
			if (pagecurrent == 1) {
				currentList = listAttachment.subList(0, num);
			} else {
				if (pagecurrent == pagecount + 1) {
					currentList = listAttachment.subList(num + (pagecurrent - 2) * MobileConstants.PAGE_COUNTER,listAttachment.size());
				} else {
					currentList = listAttachment.subList(num + (pagecurrent - 2) * MobileConstants.PAGE_COUNTER,num + (pagecurrent - 1) * MobileConstants.PAGE_COUNTER);
				}
			}
		}

		return currentList;
	}

	public String paginationString(String str, int pagenum, int strint) {
		if (strint == 1) {
			return str;
		} else {
			if (pagenum == strint) {
				return str.substring((pagenum - 1) * MobileConstants.DISPLAY_PAGE_NUMBER,str.length());
			} else {
				return str.substring((pagenum - 1) * (MobileConstants.DISPLAY_PAGE_NUMBER),pagenum * MobileConstants.DISPLAY_PAGE_NUMBER);
			}
		}
	}

	public Map<Long, List<ColComment>> paginationOPinion(int pagenum,
			int pagecount, Map<Long, List<ColComment>> map) {
		int i=0;
		
		for (ColOpinion col : opinions) {
			List<ColComment> list = mapColComment.get(col.getId());
			
				if( i>=(pagenum-1) * pagecount && i< pagenum * pagecount){
					map.put(col.getId(),list );
				}
			i++;
		}
		return map;
	}

	public String appendString(String str, Long Id) {
		content_opinion_1 = content_opinion_1 + str;
		List<ColComment> list = mapColComment.get(Id);
		if (list != null) {
			for (ColComment col : list) {
				content_opinion_1 = content_opinion_1 + col.getContent();
			}
		}
		return content_opinion_1;
	}

	public Map<Long, List<ColComment>> getEndOpinion(int pagenum,
			int pagecount, Map<Long, List<ColComment>> map) {
		String contents = "";
		if (pagenum > 1) {
			pagenum = pagenum - 1;
		} else {
			pagenum = 1;
		}
		for (ColOpinion col : opinions) {
			List<ColComment> list = mapColComment.get(col.getId());
			map.put(col.getId(), list);
			contents = col.getContent();
			if (content_opinion_2.length() < pagenum * pagecount) {
				contents = appendStrings(contents, col.getId());
				if (contents.length() < pagenum * pagecount) {
					contents = contents + "";
				} else {
					return map;
				}
			} else {
				return map;
			}
		}
		return map;
	}

	public String appendStrings(String str, Long Id) {
		content_opinion_2 = content_opinion_2 + str;
		List<ColComment> list = mapColComment.get(Id);
		if (list != null) {
			for (ColComment col : list) {
				content_opinion_2 = content_opinion_2 + col.getContent();
			}
		}
		return content_opinion_2;
	}

	public Map<Long, List<ColComment>> getOverOpinion(
			Map<Long, List<ColComment>> map1, Map<Long, List<ColComment>> map2) {
		if(map1 != null && map2 != null){
			Set<Long> set = map2.keySet();
			for (Long l : set) {
				if (map1 != null) {
					map1.remove(l);
				}
			}
		}
		return map1;
	}

	public Map<RelationType, List<V3xOrgMember>> getMap() {
		return map;
	}

	public void setMap(Map<RelationType, List<V3xOrgMember>> map) {
		this.map = map;
	}

	public Map<RelationType, List<V3xOrgMember>> getRelativeMembers() {
		@SuppressWarnings("unused")
		Map<RelationType, List<V3xOrgMember>> relationMap = new HashMap<RelationType, List<V3xOrgMember>>();

		return null;

	}

	public List<Long> paginationLong(int pagecurrent, int num,List<Long> currentList) {
		// pagecount总共的页数
		// pagecurrent当前是第几页
		// num为该页除子部门外，填补部门人员的个数
		if (listlong != null) {
			int number = listlong.size();
			if (num == number || num > number) {
				currentList = listlong.subList(0, listlong.size());
			}
			if (listlong.size() != 0 && num < listlong.size() && num > 0) {
				
				int pagecount = getPageCount(number - num);
				
				if (pagecurrent == 1) {
					currentList = listlong.subList(0, num);
				} else {
					if (pagecurrent == pagecount + 1) {
						currentList = listlong.subList(num + (pagecurrent - 2) * MobileConstants.PAGE_COUNTER,listlong.size());
					} else {
						currentList = listlong.subList(num + (pagecurrent - 2) * MobileConstants.PAGE_COUNTER,num + (pagecurrent - 1) * MobileConstants.PAGE_COUNTER);
					}
				}
			}
		}
		return currentList;
	}
	
	/**
	 * 得到 总的页码数
	 * @param size
	 * @return
	 */
	private int getPageCount(int size){
		int pagecount=0;
		float pagecountfloat = (float) size / MobileConstants.PAGE_COUNTER;
		int pagecountint = (int) size / MobileConstants.PAGE_COUNTER;
		if (pagecountfloat - pagecountint != 0) {
			pagecount = pagecountint + 1;
		} else {
			pagecount = pagecountint;
		}
		return pagecount;
	}
	
	/**
	 * 
	 * 将 List 分页
	 * @param list
	 * @param currentPageNum
	 * @return
	 */
	public static List paginationObjectList(List list,int currentPageNum){
		MobileUtil mobileUtil = new MobileUtil();
		int allSize = list!=null?list.size():0;
		if(allSize <= MobileConstants.PAGE_COUNTER){
			return list;
		}else{
			if(allSize % MobileConstants.PAGE_COUNTER == 0){
				return list.subList((currentPageNum-1 )* MobileConstants.PAGE_COUNTER, currentPageNum * MobileConstants.PAGE_COUNTER);
			}else{
				int pagecounter = mobileUtil.getPageCount(allSize, MobileConstants.PAGE_COUNTER);
				if (currentPageNum != pagecounter) {
					return list.subList((currentPageNum - 1)* MobileConstants.PAGE_COUNTER,(currentPageNum * MobileConstants.PAGE_COUNTER));
				}else{
					return list.subList((currentPageNum - 1)* MobileConstants.PAGE_COUNTER,allSize);
				}
			}
		}
	}
}
