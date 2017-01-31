package com.seeyon.v3x.system.signet.dao;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.system.signet.domain.V3xSignet;
import com.seeyon.v3x.util.TextEncoder;

public class SignetDaoImpl extends BaseHibernateDao<V3xSignet> implements SignetDao {
	
	private static final Log log = LogFactory.getLog(SignetDaoImpl.class);

	public void delete(long id) {
		super.delete(id);
	}
	
	public void deleteByAccountId(Long accountId){
		String hql = "delete from V3xSignet as signet where signet.orgAccountId= ? ";
		super.bulkUpdate(hql, null, new Object[]{accountId});
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xSignet> findAll() {
		String hql = "from V3xSignet as signet order by markDate";
		List<V3xSignet> signets = super.find(hql);
		for (V3xSignet signet : signets) {
			this.setMarkBodyByte(signet);
		}
		return signets;
	}

	public void create(V3xSignet signet) {
		super.save(signet);
	}

	public void update(V3xSignet signet) {
		super.update(signet);
	}

	@Override
	public V3xSignet getSignet(long id) {
		V3xSignet signet = super.get(id);
		this.setMarkBodyByte(signet);
		return signet;
	}
	
	private void setMarkBodyByte(V3xSignet signet) {
		InputStream in = null;
		try {
			if (signet.getMarkBody() != null) {
				in = signet.getMarkBody().getBinaryStream();
				byte[] b = IOUtils.toByteArray(in);
				b = TextEncoder.decodeBytes(b);
				signet.setMarkBodyByte(b);
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
	}

}