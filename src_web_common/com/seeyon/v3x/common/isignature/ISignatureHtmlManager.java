package com.seeyon.v3x.common.isignature;

import com.seeyon.v3x.common.isignature.domain.ISignatureHtml;

public interface ISignatureHtmlManager {
  
  //保存
  public void  save(ISignatureHtml iSignatureHtml);
  public void delete(Long id);
  public ISignatureHtml get(Long id);
  public void update(ISignatureHtml iSignatureHtml) ;
  public String LoadISignatureByDocumentId(Long documentId) ;
  public int getISignCount(Long document);
  public void deleteAllByDocumentId(Long documentId);
  
  /**
   * 拷贝印章数据
   * @param srcDocumentId
   * @param newDocumentId
   */
  public void copyISignatureHtml2NewDocument(Long srcDocumentId,Long newDocumentId);
}
