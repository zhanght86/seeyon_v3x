package com.seeyon.v3x.common.isignature.dao;

import java.util.List;

import com.seeyon.v3x.common.isignature.domain.ISignatureHtml;

public interface ISignatureHtmlDao {
	public void save(ISignatureHtml iSignatureHtml);
	public ISignatureHtml get(Long id);
	public void update(ISignatureHtml iSignatureHtml) ;
	public List<ISignatureHtml> LoadISignatureByDocumentId(Long documentId);
	public int getISignCount(Long document);
	public void delete(Long id);
	public void deleteAllByDocumentId(Long documentId);
}
