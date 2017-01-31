package com.seeyon.v3x.common.isignature.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.hibernate.type.Type;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.isignature.domain.ISignatureHtml;
import com.seeyon.v3x.system.signet.domain.V3xSignet;

public class ISignatureHtmlDaoImpl extends BaseHibernateDao<ISignatureHtml> implements ISignatureHtmlDao{

	@Override
	public void save(ISignatureHtml iSignatureHtml) {
		super.save(iSignatureHtml);
	}
	public void update(ISignatureHtml iSignatureHtml) {
		super.update(iSignatureHtml);
	}
	
	public ISignatureHtml get(Long id){
		return super.get(id);
	}
	public void delete(Long id){
		super.delete(id);
	}
	public void deleteAllByDocumentId(Long id){
		String hql = "delete from ISignatureHtml as signature  where signature.documentId = :documentId ";
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("documentId", id);
		super.bulkUpdate(hql,m);
	}
	public List<ISignatureHtml> LoadISignatureByDocumentId(Long documentId){
		String hql = "from ISignatureHtml as signature  where signature.documentId = ?";
		return super.find(hql, new Object[]{documentId});
	}
	
	public ISignatureHtml getISignatureHtmlByDocumentIdAndSignatureId(Long documentId,Long signatureId){
		String hql = "from ISignatureHtml as signature  where signature.documentId = ? and signature.signatureId = ? ";
		return super.findUniqueBy(hql, new Object[]{documentId,signatureId});
	}
	
	public int getISignCount(Long document){
		String hql ="from ISignatureHtml as isi where isi.documentId = ? ";
		return super.getQueryCount(hql, 
				new Object[]{document}, 
				new Type[]{org.hibernate.Hibernate.LONG});
	} 
}