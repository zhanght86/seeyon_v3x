package com.seeyon.v3x.doc.manager;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.dao.DocSpaceDao;
import com.seeyon.v3x.doc.domain.DocStorageSpace;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.webmail.manager.LocalMailCfg;

public class DocSpaceManagerImpl implements DocSpaceManager {
	private static final Log log = LogFactory.getLog(DocSpaceManagerImpl.class);
	
	private DocSpaceDao docSpaceDao;
	
	private OrgManager orgManager;

	public DocSpaceDao getDocSpaceDao() {
		return docSpaceDao;
	}
	public void setDocSpaceDao(DocSpaceDao docSpaceDao) {
		this.docSpaceDao = docSpaceDao;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public DocStorageSpace addDocSpace(long userId, long totalSize, long mailSize) {
		DocStorageSpace space = docSpaceDao.findUniqueBy("userId", userId);
		if(space != null) {
			return space ;
		}
		DocStorageSpace docSpace=new DocStorageSpace();
		docSpace.setIdIfNew();
//		docSpace.setDocLibId(docLibId);
		docSpace.setUserId(userId);
		docSpace.setUsedSpaceSize(0);
		docSpace.setStatus(Constants.SPACE_FREE);//0表示空闲，1表示警示，2表示空间已满
		
//		docSpace.setMailSpace(1024*1024*10);
		docSpace.setMailUsedSpace(0);
		docSpace.setMailStatus(Constants.SPACE_FREE);		
		
		if(totalSize==0){
			docSpace.setTotalSpaceSize(new Integer(1024*1024*100).longValue()); 	//默认设置为100MB
		}else{
			docSpace.setTotalSpaceSize(totalSize);
		}
		
		if (mailSize == 0) {
			docSpace.setMailSpace(new Long(1024*1024*100).longValue());
		}
		else {
			docSpace.setMailSpace(mailSize);
		}
		
		docSpace.setBlogSpace(new Long(1024*1024*10).longValue());
		docSpace.setBlogUsedSpace(0);
		docSpace.setBlogStatus(Constants.SPACE_NOT_ASSIGNED);
		
		docSpaceDao.save(docSpace);
		
		return docSpace;
	}
	
	public void modifyDocSpace(long spaceId, long totalSize,long mailTotalSize,
			long blogTotalSize) throws DocException{
		DocStorageSpace docSpace=this.getDocSpaceById(spaceId);
		long docUsedSize=docSpace.getUsedSpaceSize();
		long mailUsedSize=docSpace.getMailUsedSpace();
		long blogUsedSize=docSpace.getBlogUsedSpace();
		long temp=new Integer(1024*1024).longValue();
//		if(docUsedSize > totalSize*temp || mailUsedSize>mailTotalSize*temp
//				|| blogUsedSize>blogTotalSize*temp){
//			throw new DocException("空间分配太小");
//		}
		
		long _totalSize = totalSize;
		long _mailTotalSize = mailTotalSize;
		long _blogTotalSize = blogTotalSize;
		
		// 当分配的新空间大小小于已经使用的时候，自动将使用大小作为空间大小
		if(docUsedSize > totalSize * temp){
			long divNum = docUsedSize / temp;
			long modNum = docUsedSize % temp;
			if(modNum == 0)
				_totalSize = divNum;
			else
				_totalSize = divNum + 1;
		}
		if(mailUsedSize > mailTotalSize * temp){
			long divNum = mailUsedSize / temp;
			long modNum = mailUsedSize % temp;
			if(modNum == 0)
				_mailTotalSize = divNum;
			else
				_mailTotalSize = divNum + 1;
		}
		if(blogTotalSize>=0){
			if(blogUsedSize > blogTotalSize * temp){
				long divNum = blogUsedSize / temp;
				long modNum = blogUsedSize % temp;
				if(modNum == 0)
					_blogTotalSize = divNum;
				else
					_blogTotalSize = divNum + 1;
			}
		}
		
		if(docUsedSize == _totalSize*temp){
			docSpace.setStatus(Constants.SPACE_FULL);
		}
		else if(docUsedSize * 100 < _totalSize*temp*90){
			docSpace.setStatus(Constants.SPACE_FREE);
		}
		else{
			docSpace.setStatus(Constants.SPACE_ALERT);
		}
		
		if(mailUsedSize == _mailTotalSize*temp){
			docSpace.setMailStatus(Constants.SPACE_FULL);
		}
		else if(mailUsedSize*100 < _mailTotalSize*temp*90){
			docSpace.setMailStatus(Constants.SPACE_FREE);
		}
		else{
			docSpace.setMailStatus(Constants.SPACE_ALERT);
		}
		if(blogTotalSize>=0){
			if (_blogTotalSize != 0) {
				if (blogUsedSize == _blogTotalSize*temp) {
					docSpace.setBlogStatus(Constants.SPACE_FULL);
				}
				else if (blogUsedSize * 100 < _blogTotalSize*temp*90) {
					docSpace.setBlogStatus(Constants.SPACE_FREE);
				}
				else {
					docSpace.setBlogStatus(Constants.SPACE_ALERT);
				}
			}
			else {
				docSpace.setBlogStatus(Constants.SPACE_NOT_ASSIGNED);
			}
		}
		docSpace.setTotalSpaceSize(_totalSize*temp);
		docSpace.setMailSpace(_mailTotalSize*temp);
		if(blogTotalSize>=0)
			docSpace.setBlogSpace(_blogTotalSize*temp);
		docSpaceDao.update(docSpace);		
	}
	
	public void setMailSpace(long userId,String mailTotalSize)throws DocException{
		DocStorageSpace docSpace=this.getDocSpaceByUserId(userId);
		String usedSize=this.formatSize(docSpace.getMailUsedSpace()/(new Long(1024*1024)).floatValue());
		if(Float.valueOf(usedSize) > Float.valueOf(mailTotalSize)){
			throw new DocException("空间分配太小");
		}
		if(usedSize.equals(mailTotalSize)){
			docSpace.setMailStatus(Constants.SPACE_FULL);
		}else if((Float.valueOf(usedSize)/Float.valueOf(mailTotalSize))*100 <90){
			docSpace.setMailStatus(Constants.SPACE_FREE);
		}else{
			docSpace.setMailStatus(Constants.SPACE_ALERT);
		}
	//	docSpace.setMailSpace(Float.valueOf(mailTotalSize)*(new Long(1024*1024).floatValue()));
		
		docSpaceDao.update(docSpace);
	}
	
	public DocStorageSpace getDocSpaceByUserId(long userId) {
		
		DocStorageSpace space = docSpaceDao.findUniqueBy("userId", userId);
		if(space == null){
			log.debug(" 这个人员的id是没有查找到的userId= "+userId);
			V3xOrgMember member;
			try {
				member = orgManager.getMemberById(userId);
				if(member != null)
					space = this.addDocSpace(userId, 0L, 0L);
			} catch (BusinessException e) {
				log.error("", e);
			}

		}else {
			long mailOcuppiedSize = LocalMailCfg.getMailSpaceSize(String.valueOf(userId));
			//long mailUsedSize= mailOcuppiedSize/temp;
			space.setMailUsedSpace(mailOcuppiedSize);						
		}
		
		return space;
	}
	
	/**
	 * 
	 */
	public List<DocStorageSpace> getSpacesByUserIds(List<V3xOrgMember> members){
		List<DocStorageSpace> list = new ArrayList<DocStorageSpace>();
		if(members == null || members.size() == 0)
			return list;
		
		String ids = "";
		for(V3xOrgMember m : members){
			ids += "," + m.getId();
		}
		String hql = "from DocStorageSpace where userId in(:ids)";
		Map<Long, DocStorageSpace> map = new HashMap<Long, DocStorageSpace>();
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("ids", Constants.parseStrings2Longs(ids.substring(1), ","));
		List<DocStorageSpace> alist = docSpaceDao.find(hql, -1, -1, nmap);
		if(alist != null){
			for(DocStorageSpace td : alist){
				map.put(td.getUserId(), td);
			}
		}
		for(V3xOrgMember m : members){
			DocStorageSpace val = map.get(m.getId());
			if(val == null){
				log.warn("DocSpace**Man.getSpacesByUserIds(): 人员["+m.getId()+"]没有查找到空间存储记录，追加一条。");
				val = this.addDocSpace(m.getId(), 0L, 0L);
			}
			//long temp=new Integer(1024*1024).longValue();
			long mailOcuppiedSize = LocalMailCfg.getMailSpaceSize(String.valueOf(m.getId()));
			//long mailUsedSize= mailOcuppiedSize/temp;
			val.setMailUsedSpace(mailOcuppiedSize);
			
			list.add(val);
		}
		
		
		return list;
	}
	
	public void addUsedSpaceSize(long userId, long size) throws DocException{
		DocStorageSpace docSpace=this.getDocSpaceByUserId(userId);
		if(docSpace == null)
			return;
		BigDecimal totalSize=new BigDecimal(docSpace.getTotalSpaceSize());	//总空间
		BigDecimal usedSize=new BigDecimal(docSpace.getUsedSpaceSize());	//已使用的空间
		
		BigDecimal addSize=new BigDecimal(size);		//要增加的空间
		usedSize=usedSize.add(addSize);
		if(totalSize.compareTo(usedSize) == -1){
			throw new DocException("空间容量不足");
		}
		if(totalSize.compareTo(usedSize) == 0){
			docSpace.setStatus(Constants.SPACE_FULL);
		}else if(usedSize.multiply(new BigDecimal(100)).compareTo(totalSize.abs().multiply(new BigDecimal(90))) == -1){
			docSpace.setStatus(Constants.SPACE_FREE);
		}else{
			docSpace.setStatus(Constants.SPACE_ALERT);
		}
		docSpace.setUsedSpaceSize(size+docSpace.getUsedSpaceSize());		//最后的结果
		docSpaceDao.update(docSpace);
	}
	
	public void subUsedSpaceSize(long userId, long size) {
		DocStorageSpace docSpace=this.getDocSpaceByUserId(userId);
		BigDecimal totalSize=new BigDecimal(docSpace.getTotalSpaceSize());	//总空间
		BigDecimal usedSize=new BigDecimal(docSpace.getUsedSpaceSize());	//已使用的空间
		
		BigDecimal deleteSize=new BigDecimal(size);		//要减少的空间
		usedSize=usedSize.subtract(deleteSize);
		if(usedSize.compareTo(new BigDecimal(0)) == -1){
			usedSize = new BigDecimal(0);
//			throw new DocException("使用的空间不能为负值");
		}
		
		if(usedSize.multiply(new BigDecimal(100)).compareTo(totalSize.multiply(new BigDecimal(90))) == -1){
			docSpace.setStatus(Constants.SPACE_FREE);
		}else{
			docSpace.setStatus(Constants.SPACE_ALERT);
		}

		docSpace.setUsedSpaceSize(usedSize.longValue());
		docSpaceDao.update(docSpace);
		
	}
	
	public List<DocStorageSpace> getDocStorageSpaces() {
		
		return docSpaceDao.getAll();
	}
	public List<DocStorageSpace>  getDocStorageSpacesByAccount(final long accountId){
//		
//        if (Pagination.isNeedCount()) {
//        	String hql2 = "from DocStorageSpace as d, V3xOrgMember as m where d.userId = m.id and m.orgAccountId = " + accountId;
//            int rowCount = docSpaceDao.getQueryCount(hql2, null, null);
//            Pagination.setRowCount(rowCount);
//        }
//        
////        Session session = docSpaceDao.getASession();
////        Query q = session.createQuery(hql).setFirstResult(Pagination.getFirstResult())
////        		.setMaxResults(Pagination.getMaxResults());
//        List<DocStorageSpace> ret = (List<DocStorageSpace>)docSpaceDao.getHibernateTemplate().execute(new HibernateCallback(){
//			public Object doInHibernate(Session session) throws HibernateException, SQLException {
//				String hql = "select d from DocStorageSpace as d, V3xOrgMember as m where d.userId = m.id and m.orgAccountId = " + accountId;
//
//				return (List<DocStorageSpace>)session.createQuery(hql).setFirstResult(Pagination.getFirstResult())
//        		.setMaxResults(Pagination.getMaxResults()).list();
//			}
//    	});
//        
//        
//		return ret;
		
		
		List<V3xOrgMember> members = null;
		Map<Long,List<V3xOrgMember>> map = null;
		try {
			members = orgManager.getAllMembers(accountId);
			
			List<V3xOrgMember> outters = orgManager.getAllExtMembers(accountId);
			if(outters != null && members != null)
				members.addAll(outters);
			
			map = orgManager.getConcurentPostByAccount(accountId);
		} catch (BusinessException e) {
			log.error("", e) ;
		}
		members = this.filterConcurrentPostOfAccount(members, map);
		return this.getSpacesByUserIds(pagenate(members));
	}
	// 过滤单位兼职
	private List<V3xOrgMember> filterConcurrentPostOfAccount(List<V3xOrgMember> list, Map<Long,List<V3xOrgMember>> map){		
		List<V3xOrgMember> ret = new ArrayList<V3xOrgMember>();
		if(list == null || list.size() == 0)
			return ret;
		
		if(map == null || map.size() == 0)
			return list;
		else{
			Set<V3xOrgMember> set = new HashSet<V3xOrgMember>();
			Set<Long> keyset = map.keySet();
			for(Long k : keyset){
				List<V3xOrgMember> value = map.get(k);
				if(value != null)
					set.addAll(value);
			}
			
			for(V3xOrgMember m : list){
				if(!set.contains(m))
					ret.add(m);
			}
		}
		
		return ret;
	}
	
	public List<DocStorageSpace> getDocSpaceByStatus(int status) {
		byte temp=0;
		byte temp1=1;
		byte temp2=2;
		if(status==0){
			return docSpaceDao.findBy("status", temp);
		}else if(status==1){
			return docSpaceDao.findBy("status", temp1);
		}else {
			return docSpaceDao.findBy("status", temp2);
		}
	}
	
	
	public DocStorageSpace getDocSpaceById(long docSpaceId) {
		DocStorageSpace space=docSpaceDao.get(docSpaceId);
		return space;
	}
	public void addMailSpaceSize(long userId, long size) throws DocException{
		DocStorageSpace docSpace=this.getDocSpaceByUserId(userId);
		BigDecimal mailTotal=new BigDecimal(docSpace.getMailSpace());
		BigDecimal mailUsed=new BigDecimal(docSpace.getMailUsedSpace());
		BigDecimal addSize=new BigDecimal(size);
		mailUsed=mailUsed.add(addSize);
		if(mailUsed.compareTo(mailTotal) == 1){
			throw new DocException("空间容量不足");
		}
		
		if(mailTotal.compareTo(mailUsed) == 0){
			docSpace.setMailStatus(Constants.SPACE_FULL);
		}else if(mailUsed.multiply(new BigDecimal(100)).compareTo(mailTotal.multiply(new BigDecimal(90))) == -1){
			docSpace.setMailStatus(Constants.SPACE_FREE);
		}else{
			docSpace.setMailStatus(Constants.SPACE_ALERT);
		}
		
		docSpace.setMailUsedSpace(docSpace.getMailUsedSpace()+size);
		docSpaceDao.update(docSpace);

	}
	public void deleteMailSpaceSize(long userId, long size) throws DocException{
		DocStorageSpace docSpace=this.getDocSpaceByUserId(userId);
		BigDecimal mailTotal=new BigDecimal(docSpace.getMailSpace());
		BigDecimal mailUsed=new BigDecimal(docSpace.getMailUsedSpace());
		BigDecimal deleteSize=new BigDecimal(size);
		mailUsed=mailUsed.subtract(deleteSize);
		if(mailUsed.compareTo(new BigDecimal(0)) == -1){
			throw new DocException("使用的空间不能为负值");
		}
		
		if(mailUsed.multiply(new BigDecimal(100)).compareTo(mailTotal.multiply(new BigDecimal(90))) == -1){
			docSpace.setMailStatus(Constants.SPACE_FREE);
		}else {
			docSpace.setMailStatus(Constants.SPACE_ALERT);
		}
		docSpace.setMailUsedSpace(docSpace.getMailUsedSpace()+size);
		docSpaceDao.update(docSpace);
	}
	
	public void addBlogSpaceSize(long userId, long size) throws DocException {
		DocStorageSpace docSpace=this.getDocSpaceByUserId(userId);
		BigDecimal blogTotal=new BigDecimal(docSpace.getBlogSpace());
		BigDecimal blogUsed=new BigDecimal(docSpace.getBlogUsedSpace());
		BigDecimal addSize=new BigDecimal(size);
		blogUsed=blogUsed.add(addSize);
		if(blogUsed.compareTo(blogTotal) == 1){
			throw new DocException("空间容量不足");
		}
		
		if(blogTotal.compareTo(blogUsed) == 0){
			docSpace.setBlogStatus(Constants.SPACE_FULL);
		}else if(blogUsed.multiply(new BigDecimal(100)).compareTo(blogTotal.multiply(new BigDecimal(90))) == -1){
			docSpace.setBlogStatus(Constants.SPACE_FREE);
		}else{
			docSpace.setBlogStatus(Constants.SPACE_ALERT);
		}
		
		docSpace.setBlogUsedSpace(docSpace.getBlogUsedSpace()+size);
		docSpaceDao.update(docSpace);
	}
	public String judgeBlogSpace(Long attSizeSum){

		DocStorageSpace docSpace=this.getDocSpaceByUserId(CurrentUser.get().getId());
		BigDecimal blogTotal=new BigDecimal(docSpace.getBlogSpace());
		BigDecimal blogUsed=new BigDecimal(docSpace.getBlogUsedSpace());
		BigDecimal addSize=new BigDecimal(attSizeSum);
		blogUsed=blogUsed.add(addSize);
		
		if(blogUsed.compareTo(blogTotal) == 1){
			return "false";
		}		
		
		return "true"; 
	}
	public void deleteBlogSpaceSize(long userId, long size) throws DocException {
		DocStorageSpace docSpace=this.getDocSpaceByUserId(userId);
		BigDecimal blogTotal=new BigDecimal(docSpace.getBlogSpace());
		BigDecimal blogUsed=new BigDecimal(docSpace.getBlogUsedSpace());
		BigDecimal deleteSize=new BigDecimal(size);
		blogUsed=blogUsed.subtract(deleteSize);
		if(blogUsed.compareTo(new BigDecimal(0)) == -1){
			throw new DocException("使用的空间不能为负值");
		}
		
		if(blogUsed.multiply(new BigDecimal(100)).compareTo(blogTotal.multiply(new BigDecimal(90))) == -1){
			docSpace.setBlogStatus(Constants.SPACE_FREE);
		}else {
			docSpace.setBlogStatus(Constants.SPACE_ALERT);
		}
		docSpace.setBlogUsedSpace(docSpace.getBlogUsedSpace()+size);
		docSpaceDao.update(docSpace);
	}
	
	private String formatSize(float size){
		Locale locale = Locale.getDefault();

		User user = CurrentUser.get();
		if (user != null) {
			locale = user.getLocale();
		}

		NumberFormat format = NumberFormat.getInstance(locale);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(0);
		
		return format.format(size);
	}
	
	public List<DocStorageSpace> getStorageSpacesByDeptId(long deptId) {
//		return docSpaceDao.findByDeptId(deptId);
		List<V3xOrgMember> members = null;
		User user = CurrentUser.get();
		try {
			//members = orgManager.getMembersByDepartment(deptId, true);
			members = orgManager.getMembersByDepartment(deptId, true,false,user.getAccountId(),true);
		} catch (BusinessException e) {
			log.error("",e) ;
		}
		members = this.filterConcurrentPost(members, deptId);
		return this.getSpacesByUserIds(pagenate(members));
	}
	//过滤兼职
	private List<V3xOrgMember> filterConcurrentPost(List<V3xOrgMember> list, Long deptId){
		List<V3xOrgMember> ret = new ArrayList<V3xOrgMember>();
		if(list == null || list.size() == 0 || deptId == null)
			return ret;
		
		for(V3xOrgMember m : list){
			if(m.getOrgDepartmentId().longValue() == deptId.longValue())
				ret.add(m);
		}
		
		return ret;
	}
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return null;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}	
	
	public void assignBlogSpace(long userId, long size) throws DocException {
		DocStorageSpace docSpace=this.getDocSpaceByUserId(userId);
		long blogUsedSize=docSpace.getBlogUsedSpace();
		long temp=new Integer(1024*1024).longValue();
		if(blogUsedSize>size*temp){
			throw new DocException("空间分配太小");
		}
		if (size != 0) {
			if (blogUsedSize == size*temp) {
				docSpace.setBlogStatus(Constants.SPACE_FULL);
			}
			else if (blogUsedSize * 100 < size*temp*90) {
				docSpace.setBlogStatus(Constants.SPACE_FREE);
			}
			else {
				docSpace.setBlogStatus(Constants.SPACE_ALERT);
			}
		}
		else {
			docSpace.setBlogStatus(Constants.SPACE_NOT_ASSIGNED);
		}
		docSpace.setBlogSpace(size*temp);
		docSpaceDao.update(docSpace);
	}
	
	
}
