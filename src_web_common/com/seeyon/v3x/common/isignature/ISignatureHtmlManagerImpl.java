package com.seeyon.v3x.common.isignature;

import java.util.List;

import com.seeyon.v3x.common.isignature.dao.ISignatureHtmlDao;
import com.seeyon.v3x.common.isignature.domain.ISignatureHtml;
import com.seeyon.v3x.util.Strings;

public class ISignatureHtmlManagerImpl implements  ISignatureHtmlManager{
    private ISignatureHtmlDao iSignatureHtmlDao;
	 
	public ISignatureHtmlDao getiSignatureHtmlDao() {
		return iSignatureHtmlDao;
	}

	public void setiSignatureHtmlDao(ISignatureHtmlDao iSignatureHtmlDao) {
		this.iSignatureHtmlDao = iSignatureHtmlDao;
	}

	public void save(ISignatureHtml iSignatureHtml) {
		iSignatureHtmlDao.save(iSignatureHtml);
	}

	public String LoadISignatureByDocumentId(Long documentId) {
		List<ISignatureHtml> iSignatureHtmls = iSignatureHtmlDao.LoadISignatureByDocumentId(documentId);
		StringBuilder  SignatureIDs= new StringBuilder();
		for(ISignatureHtml iSignatureHtml : iSignatureHtmls){
			SignatureIDs.append(iSignatureHtml.getId());
			SignatureIDs.append(";");
		}
		return SignatureIDs.toString();
	}

	@Override
	public ISignatureHtml get(Long id) {
		ISignatureHtml iSignatureHtml = iSignatureHtmlDao.get(id);
		return iSignatureHtml;
	}
	public void delete(Long id) {
		iSignatureHtmlDao.delete(id);
	}
	public void deleteAllByDocumentId(Long documentId) {
		iSignatureHtmlDao.deleteAllByDocumentId(documentId);
	}
	@Override
	public void update(ISignatureHtml iSignatureHtml) {
		iSignatureHtmlDao.update(iSignatureHtml);
	}
	
	public int getISignCount(Long document){
		return iSignatureHtmlDao.getISignCount(document);
	}

	@Override
	public void copyISignatureHtml2NewDocument(Long srcDocumentId,
			Long newDocumentId) {
		
		List<ISignatureHtml> iSignatureHtmls = iSignatureHtmlDao.LoadISignatureByDocumentId(srcDocumentId);
		
		if(Strings.isNotEmpty(iSignatureHtmls)){
			for(ISignatureHtml is : iSignatureHtmls){
				ISignatureHtml newISignatureHTML = new ISignatureHtml();
				newISignatureHTML.setIdIfNew();
				newISignatureHTML.setDocumentId(newDocumentId);
				newISignatureHTML.setSignature(is.getSignature());
				newISignatureHTML.setSignDate(is.getSignDate());
				newISignatureHTML.setUserId(is.getUserId());
				newISignatureHTML.setHostName(is.getHostName());
				save(newISignatureHTML);
			}
		}
	}
}
